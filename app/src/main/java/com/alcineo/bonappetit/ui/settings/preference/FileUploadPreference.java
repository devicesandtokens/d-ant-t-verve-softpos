package com.alcineo.bonappetit.ui.settings.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;

import com.alcineo.administrative.FileId;
import com.alcineo.bonappetit.R;
import com.alcineo.softpos.payment.api.KernelsAdministrationAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileUploadPreference extends Preference implements Preference.OnPreferenceClickListener {

    private static final String TAG = FileUploadPreference.class.getSimpleName();

    private final List<FileId> authorizedList = Arrays.asList(FileId.MCL,
            FileId.PAYWAVE,
            FileId.EXPRESS_PAY,
            FileId.JCB,
            FileId.DISCOVER,
            FileId.UNIONPAY,
            FileId.PURE,
            FileId.EFTPOS,
            FileId.CPACE,
            FileId.CA_KEY,
            FileId.LANGUAGE,
            FileId.REV,
            FileId.EXCEPT);

    private View.OnClickListener onFileIdPathClick;
    private AppCompatEditText filePath;
    private AppCompatSpinner spinner;
    private Uri fileUri;

    public FileUploadPreference(Context context) {
        super(context);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Upload settings file")
                .setMessage("Select a file type, and a file to upload to terminal.")
                .create();

        final View alertDialogContent = View.inflate(getContext(), R.layout.alertdialog_fileupload_content, null);
        alertDialog.setView(alertDialogContent);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Upload file.", this::uploadFile);
        alertDialog.show();

        spinner = alertDialog.findViewById(R.id.alertdialog_fileupload_filetype);
        final ArrayAdapter<FileId> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, authorizedList);
        spinner.setAdapter(adapter);

        filePath = alertDialog.findViewById(R.id.alertdialog_fileupload_filepath);
        filePath.setOnClickListener(onFileIdPathClick);

        return true;
    }

    private void uploadFile(DialogInterface dialogInterface, int whichButton) {
        if (whichButton == DialogInterface.BUTTON_POSITIVE) {
            final FileId fileId = (FileId) spinner.getSelectedItem();

            try (InputStream is = getContext().getContentResolver().openInputStream(fileUri)) {

                KernelsAdministrationAPI.uploadTerminalSettingsFile(fileId, is);
                Toast.makeText(getContext(), "File uploaded.", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e(TAG, "uploadFile: ", e);
                Toast.makeText(getContext(), "Could not upload selected file!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void registerFileIdPathOnClickListener(View.OnClickListener onClickListener) {
        this.onFileIdPathClick = onClickListener;
    }

    public void setFilePath(Uri fileUri) {
        this.fileUri = fileUri;
        this.filePath.setText(fileUri.getPath());
    }

}

