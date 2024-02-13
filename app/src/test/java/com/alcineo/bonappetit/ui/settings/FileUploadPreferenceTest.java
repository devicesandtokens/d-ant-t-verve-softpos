package com.alcineo.bonappetit.ui.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.test.core.app.ApplicationProvider;

import com.alcineo.administrative.FileId;
import com.alcineo.bonappetit.R;
import com.alcineo.bonappetit.ui.settings.preference.FileUploadPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAbsSpinner;
import org.robolectric.shadows.ShadowAlertDialog;

@RunWith(RobolectricTestRunner.class)
public class FileUploadPreferenceTest {

    private static final int                  MCL_POSITION = 0;
    private              ShadowAlertDialog    shadowAlertDialog;
    private              FileUploadPreference fileUploadPreference;
    private              AlertDialog          alertDialog;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        fileUploadPreference = new FileUploadPreference(context);
        fileUploadPreference.onPreferenceClick(fileUploadPreference);
        alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        shadowAlertDialog = shadowOf(alertDialog);
    }

    @Test
    public void testDialogIsDisplayedWhenPreferenceIsSelected() {
        assertEquals("Upload settings file", shadowAlertDialog.getTitle());
        assertEquals("Select a file type, and a file to upload to terminal.", shadowAlertDialog.getMessage());
    }

    @Test
    public void testAlertDisplayShouldHaveOkButton() {
        assertEquals("Upload file.", alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).getText());
    }

    @Test
    public void testContentIsCustom() {
        assertNotNull(shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filetype_title));
        assertNotNull(shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filetype));
        assertNotNull(shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filepath_title));
        assertNotNull(shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filepath));
    }

    @Test
    public void testFileTypeShouldContainsListOfAuthorizedFileTypes() {
        final AppCompatSpinner spinner = shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filetype);

        assertEquals(FileId.MCL,spinner.getItemAtPosition(0));
        assertEquals(FileId.PAYWAVE,spinner.getItemAtPosition(1));
    }

    @Test
    public void testWhenSelectedFileIdTypeSelectedShouldBeDisplayed() {
        final AppCompatSpinner spinner = shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filetype);
        final ShadowAbsSpinner shadow = shadowOf(spinner);
        shadow.performItemClick(MCL_POSITION);

        assertEquals(spinner.getSelectedItem().toString(),"MCL");
    }

    @Test
    public void testWhenTouchFileIdPathShouldNotBeEditable() {
        final EditText filePathEditText = shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filepath);

        assertEquals(InputType.TYPE_NULL, filePathEditText.getInputType());
    }

    @Test
    public void testWhenSelectFilePathShouldDisplayFilePathSelected() {
        final EditText filePathEditText = shadowAlertDialog.getView().findViewById(R.id.alertdialog_fileupload_filepath);
        fileUploadPreference.setFilePath(Uri.parse("/path/to/my/file"));

        assertEquals("/path/to/my/file", filePathEditText.getText().toString());
    }
}
