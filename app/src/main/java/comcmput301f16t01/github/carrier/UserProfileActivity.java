package comcmput301f16t01.github.carrier;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by kiete on 10/18/2016.
 */

/**
 * UserProfileActivity allows the user to view their profile information and edit their
 * contact information.
 */
public class UserProfileActivity extends AppCompatActivity {
    // Saves the values of the old fields just in case the user cancels their edit.
    public String oldPhoneNumber;
    public String oldEmailAddress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        // Get the TextViews for the information that is going to be shown.
        EditText userNameEditText = (EditText) findViewById(R.id.NameEditText);
        EditText emailAddressEditText = (EditText) findViewById(R.id.EmailEditText);
        EditText phoneNumberEditText = (EditText) findViewById(R.id.PhoneEditText);
        // Save old values in case the user changes their mind about editing.
        this.oldPhoneNumber = phoneNumberEditText.getText().toString();
        this.oldEmailAddress = emailAddressEditText.getText().toString();
        // TODO Set the TextView to the proper values.
}

    /**
     * Tapping the edit icon next to the phone number allows the user to edit their phone
     * @param v v is a View, allows usage of on click in xml
     */
    public void editPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        TextView phoneNumber = (TextView) findViewById(R.id.PhoneEditText);
        // Set it so the user can edit the EditText
        phoneNumber.setFocusable(true);
    }

    /**
     * Tapping the save icon after an edit commits changes to the phone number field
     * @param v v is a View, allows usage of on click in xml
     */
    public void saveEditedPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.PhoneEditText);
        // Set it so the user can't edit the EditText
        phoneNumberText.setFocusable(false);
        String phoneNumber = phoneNumberText.getText().toString();
        // Since editing was confirmed, overwrite old value of phone number
        this.oldPhoneNumber = phoneNumber;
        // TODO Check if it is a valid phoneNumber
        // TODO Update The information in elasticsearch
    }

    /**
     * Tapping the cancel icon during an edit removes changes to the phone number field
     * @param v v is a View, allows usage of on click in xml
     */
    public void cancelEditPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.PhoneEditText);
        // Set it so the user can't edit the EditText
        phoneNumberText.setFocusable(false);
        // Revert to the old phone number before editing started
        phoneNumberText.setText(this.oldPhoneNumber);

    }

    /**
     * Tapping the edit icon next to the email allows the user to edit their email
     * @param v v is a View, allows usage of on click in xml
     */
    public void editEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EmailEditText);
        // Set it so the user can edit the EditText
        emailView.setFocusable(true);
    }


    /**
     * Tapping the save icon after an edit commits changes to the email address
     * @param v v is a View, allows usage of on click in xml
     */
    public void saveEditedEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EmailEditText);
        // Set it so the user can edit the EditText
        emailView.setFocusable(false);
        String email = emailView.getText().toString();
        // Since editing was confirmed, overwrite old value of email
        this.oldEmailAddress = email;
        // TODO Check if valid email and update elasticsearch
    }

    /**
     * Tapping the cancel icon during an edit removes changes to the email field
     * @param v v is a View, allows usage of on click in xml
     */
    public void cancelEditEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EmailEditText);
        // Set it so the user can't edit the EditText
        emailView.setFocusable(false);
        // Revert to the old email address before editing started
        emailView.setText(this.oldEmailAddress);
    }


}