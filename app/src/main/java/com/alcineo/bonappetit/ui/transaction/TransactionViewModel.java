package com.alcineo.bonappetit.ui.transaction;

import static com.alcineo.bonappetit.model.TransactionLed.MODE1_ON;
import static com.alcineo.bonappetit.model.TransactionLed.MODE2_ON;
import static com.alcineo.softpos.payment.api.KernelsAdministrationAPI.getKernelInfoList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alcineo.administrative.Kernel;
import com.alcineo.administrative.commands.GetKernelInfos;
import com.alcineo.bonappetit.android.LocationHelper;
import com.alcineo.bonappetit.model.TransactionFullDataDto;
import com.alcineo.bonappetit.model.TransactionLed;
import com.alcineo.bonappetit.domain.utils.SoundGenerator;
import com.alcineo.softpos.payment.api.KernelsAdministrationAPI;
import com.alcineo.softpos.payment.api.TransactionAPI;
import com.alcineo.softpos.payment.api.interfaces.NFCListener;
import com.alcineo.softpos.payment.api.interfaces.TransactionEventListener;
import com.alcineo.softpos.payment.error.ExecutorException;
import com.alcineo.softpos.payment.error.TransactionException;
import com.alcineo.softpos.payment.model.beep.BeepStatus;
import com.alcineo.softpos.payment.model.transaction.TransactionParameters;
import com.alcineo.softpos.payment.model.transaction.TransactionResult;
import com.alcineo.utils.common.StringUtils;
import com.alcineo.utils.tlv.TlvException;
import com.alcineo.utils.tlv.TlvItem;
import com.alcineo.utils.tlv.TlvLength;
import com.alcineo.utils.tlv.TlvParser;
import com.alcineo.utils.tlv.TlvTag;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionViewModel extends ViewModel implements TransactionEventListener {

    public static final String TAG = TransactionViewModel.class.getSimpleName();

    public final MutableLiveData<TransactionFullDataDto> onTransactionFinishedEvent = new MutableLiveData<>();
    public final MutableLiveData<BigDecimal>             onPinRequiredEvent         = new MutableLiveData<>();
    public final MutableLiveData<String>                 transactionMessage         = new MutableLiveData<>();
    public final MutableLiveData<Boolean>                contactlessLogo            = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean>                transactionStartedEvent    = new MutableLiveData<>(false);
    public final MutableLiveData<Kernel>                 transactionKernel          = new MutableLiveData<>(Kernel.K_UNKNOWN);
    public final MutableLiveData<Drawable>               paymentSchemeLogo1         = new MutableLiveData<>();
    public final MutableLiveData<Drawable>               paymentSchemeLogo2         = new MutableLiveData<>();
    public final MutableLiveData<Drawable>               paymentSchemeLogo3         = new MutableLiveData<>();

    private final List<TransactionLed> transactionLeds = Arrays.asList(new TransactionLed(),
            new TransactionLed(),
            new TransactionLed(),
            new TransactionLed());

    public final MutableLiveData<List<TransactionLed>> transactionLedList = new MutableLiveData<>(transactionLeds);

    private final Context context;
    private final NFCListener           nfcListener;
    private final TransactionParameters transactionParameters;

    public TransactionViewModel(Context context, NFCListener nfcListener, TransactionParameters transactionParameters) {
        this.context = context;
        this.nfcListener = nfcListener;
        this.transactionParameters = transactionParameters;
    }

    /**
     * Send a start transaction to the softpos library.
     * <p> transactionParameters object is filled with value required by softpos library for a transaction.
     * Transaction object is created with required values and transactionParameters is started with static method from Softpos
     *
     * @param viewLifecycleOwner the activity/fragment launching transaction
     * @see TransactionFragment TransactionFragment#setupTransaction()
     */
    public void startTransaction(LifecycleOwner viewLifecycleOwner) {

        try {

            TransactionAPI.startTransaction(transactionParameters, nfcListener, this, viewLifecycleOwner);

        } catch (TransactionException e) {
            Log.e(TAG, "startTransaction: ", e);
        }

        kernelListHandler();

    }

    /**
     * Cancel current transactionParameters
     */
    public void cancelTransaction() {
        // stopTransactionAndConfirm return a boolean that can be used for confirm cancellation

        Log.i(TAG, "cancelTransaction");
        TransactionAPI.cancelTransaction();
    }

    /**
     * When text data is received from Softpos we handle it here
     *
     * @param s the string to display
     */
    @Override
    public void onDisplayText(String s) {

        Log.i(TAG, "onDisplayText");
        if (s.equals("** CLEAR SCREEN **")) { // Workaround for a clear screen MCL
            s = "";
        }
        transactionMessage.postValue(s);
    }

    /**
     * When TransactionLed need to be updated we handle it here
     *
     * @param ledsStatus       true if active, false if dark
     * @param mode             the led mode
     * @param turnOn           true if we need to activate whole led process, true when transactionParameters is active
     * @param blinkOnDuration  the duration of led blinking
     * @param blinkOffDuration the duration of dark led
     */
    @Override
    public void onDisplayLed(boolean[] ledsStatus, int mode, boolean turnOn, int blinkOnDuration, int blinkOffDuration) {
        if (blinkOffDuration != 0) {
            return;
        }

        transactionStartedEvent.postValue(true); // workaround, when we receive led event, transaction is started and ready to perform

        for (int ledId = 0; ledId < transactionLeds.size(); ledId++) {
            // If status is FALSE the LED is not concerned by the command
            if (!ledsStatus[ledId]) {
                continue;
            }

            if (turnOn) {
                transactionLeds.get(ledId).setBackgroundColor((mode == 1) ? MODE1_ON[ledId] : MODE2_ON[ledId]);
            } else {
                transactionLeds.get(ledId).setBackgroundColor(Color.BLACK);
            }

            transactionLedList.postValue(transactionLeds);
        }
    }

    /**
     * When pin is asked by the Softpos
     */
    @Override
    public void onDisplayPin() {

        Log.i(TAG, "onDisplayPin");

        try {
            TransactionAPI.sendPinCancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO nothings
    }

    /**
     * Method used when contactlesslogo need to be displayed
     *
     * @param isLogoVisible true or false
     */
    @Override
    public void onDisplayLogo(boolean isLogoVisible) {
        contactlessLogo.postValue(isLogoVisible);
    }

    /**
     * When a beep/sound need to be fired
     *
     * @param beepStatus the status of the beep
     * @param frequency  the frequency of the beep
     * @param duration   the duration of the beep
     * @param interval   the interval of the beep
     * @param count      numbers of beep
     */
    @Override
    public void onBeep(BeepStatus beepStatus, int frequency, int duration, int interval, int count) {

        Log.i(TAG, "onBeep");
        SoundGenerator.playSound(frequency, duration, interval, count);
    }

    /**
     * When transactionParameters is finished
     *
     * @param transactionResult data from transactionParameters
     */
    @Override
    public void onTransactionFinish(TransactionResult transactionResult) {

        Log.i(TAG, "onTransactionFinish");
        onTransactionFinishedEvent.postValue(new TransactionFullDataDto(transactionParameters, transactionResult));
    }

    @Override
    public byte[] onOnlineRequest(byte[] bytes) {
        Log.i(TAG, "onOnlineRequest");
        Log.i(TAG, StringUtils.convertBytesToHex(bytes));
        final byte[] approvedOnlineBuffer = {(byte) 0x8A, (byte) 0x02, (byte) 0x30, (byte) 0x30};
        return approvedOnlineBuffer;
    }

    @Override
    public void onDekRequest(byte[] bytes) {
        Log.i(TAG, "onDekRequest");

    }

    @Override
    public void onNotifyKernelId(Kernel kernel) {
        Log.i("kernel selected", kernel.toString());
        transactionKernel.postValue(kernel);
    }

   /**
     * After the kernel is selected we try to display the according logo
     *
     * @param kernelName Name of the kernel
     */
    public void displayPaymentSchemeLogo(String kernelName, int loops) {
        if (!kernelName.isEmpty()) {
            try {
//                System.out.println("Original kernelName " + kernelName);
//                System.out.println("Formatted kernelName " + kernelName.toLowerCase().trim());

                //Here we get the list of active kernels and create a list of the logo spaces
                List<GetKernelInfos.KernelInfo> myKernels = getKernelInfoList();
                List<MutableLiveData> logoSets = new ArrayList();
                    logoSets.add(paymentSchemeLogo1);
                    logoSets.add(paymentSchemeLogo2);
                    logoSets.add(paymentSchemeLogo3);
                Log.i("KernelInfo", String.valueOf(myKernels));

                //the kernel name is formatted so that we can get the drawable later
                kernelName = kernelName.replaceAll("\\s+", "");

//                kernelName = "ExpressPay";


                final int resourceId = context.getResources().getIdentifier(kernelName.toLowerCase(), "drawable", context.getPackageName());

                Log.i(TAG,"Numbers of loops : " + loops);
                //Handler for if the required resource hasn't been found
                if(resourceIdNotFoundHandler(resourceId) == true){
                    Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), resourceId, null);
                    Log.i(TAG, "Drawable for kernel name was found");
                    //here we get the index of the list of logo spaces using the numebr of loops and we post the set drawable
                    logoSets.get(loops).postValue(drawable);

                }
                else {
                    Log.w(TAG, "No drawable for kernel name was found");
                }
            } catch (Resources.NotFoundException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    //Method for the Handler
    public boolean resourceIdNotFoundHandler(int ResourceId) {

        return ResourceId != 0x00000000;
    }

    //Method for getting and handling the list of kernels
    public void kernelListHandler() {
        int kernelListIndex = 0;
        List<GetKernelInfos.KernelInfo> myKernels = getKernelInfoList();
        //here we try and catch if the list is empty
        try {
            while (kernelListIndex <= myKernels.size() - 1){
                System.out.println("Index of the kernel list : " + kernelListIndex);
                displayPaymentSchemeLogo(myKernels.get(kernelListIndex).kernel.getName(), kernelListIndex);
                kernelListIndex++;
            }
        } catch (NullPointerException e) {
            e.getCause();
            e.printStackTrace();
                }
    }
    @Override
    public List<TlvItem> onUpdateTagsBeforeKernelActivation(byte[] AID, Kernel kernelSelected, List<TlvItem> tags) {
        Log.i("kernel selected", kernelSelected.toString());
        Log.i("AID selected", StringUtils.convertBytesToHex(AID));
        List<TlvItem> updateTags = new ArrayList<>();
        // Code is comment. It is an example of usage.
        // If you don't neeed this, please return updateTags as emptyList.
//        try {
//            if(kernelSelected == Kernel.K_MCL) {
//                for(TlvItem tlv : tags) {
//                    // For DCPOS replace 9C value by DF38 value
//                    if (Arrays.equals(new byte[]{(byte)0xDF, 0x38}, tlv.getTag().getBytes())) {
//                        updateTags.add(new TlvItem(TlvTag.fromBytes(new byte[]{(byte)0x9C}, 0), tlv.getLength(), tlv.getValue()));
//                    }
//                }
//            }
//            else if (kernelSelected == Kernel.K_PAYWAVE) {
//                // For example, we overwrite different limit of transaction for paywave
//                byte[] df00 = new byte[]{0x00, 0x00, 0x00, 0x20, 0x00, 0x00};
//                byte[] df01 = new byte[]{0x00, 0x00, 0x00, 0x60, 0x00, 0x00};
//                byte[] df02 = new byte[]{0x00, 0x00, 0x02, 0x00, 0x00, 0x00};
//                TlvItem tag1 = new TlvItem(new TlvTag(new byte[]{(byte) 0xDF, 0x00}), TlvLength.fromValue(df00.length), df00);
//                TlvItem tag2 = new TlvItem(new TlvTag(new byte[]{(byte) 0xDF, 0x01}), TlvLength.fromValue(df01.length), df01);
//                TlvItem tag3 = new TlvItem(new TlvTag(new byte[]{(byte) 0xDF, 0x02}), TlvLength.fromValue(df02.length), df02);
//                updateTags.add(tag1);
//                updateTags.add(tag2);
//                updateTags.add(tag3);
//            }
//
//        } catch (TlvException e) {
//            e.printStackTrace();
//        }

        return updateTags;
    }


}
