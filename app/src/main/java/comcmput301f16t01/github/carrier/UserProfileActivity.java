package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private String oldPhoneNumber;
    private String oldEmailAddress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserController uc = new UserController();
        User currentUser = UserController.getLoggedInUser();

        String username;

        // Developer code, may not be needed for release...
        if (currentUser == null) {
            AlertDialog.Builder adb = new AlertDialog.Builder( this );
            adb.setTitle( "debug mode" );
            adb.setMessage( "you managed to log in without a user. Test data will be shown. This is an error if you are not a developer." );
            adb.setPositiveButton( "OK", null );
            adb.show();
            oldEmailAddress = "testEmail@youDaBomb.com";
            oldPhoneNumber = "7357129";
            username = "testUsername";
        } else {
            oldEmailAddress = currentUser.getEmail();
            oldPhoneNumber = currentUser.getPhone();
            username = currentUser.getUsername();
        }

        // Get the TextViews for the information that is going to be shown.
        EditText usernameEditText = (EditText) findViewById(R.id.NameEditText);
        EditText emailAddressEditText = (EditText) findViewById(R.id.EmailEditText);
        EditText phoneNumberEditText = (EditText) findViewById(R.id.PhoneEditText);

        //Save old values in case the user changes their mind about editing.
        usernameEditText.setText(username);
        emailAddressEditText.setText(oldEmailAddress);
        phoneNumberEditText.setText(oldPhoneNumber);

        // Removes the key listener, so that it can't hear keys.
        // Also stores it as their tag, so we can grab it later...
        phoneNumberEditText.setTag( phoneNumberEditText.getKeyListener() );
        phoneNumberEditText.setKeyListener( null );
        emailAddressEditText.setTag( emailAddressEditText.getKeyListener() );
        emailAddressEditText.setKeyListener( null );
        usernameEditText.setKeyListener( null );
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
        phoneNumber.setClickable(true);
        // Set it so the user can edit the EditText
        phoneNumber.setFocusableInTouchMode(true);
        phoneNumber.setKeyListener( (KeyListener) phoneNumber.getTag() );
        phoneNumber.requestFocus();
        phoneNumber.moveCursorToVisibleOffset();
        phoneNumber.setText( "" );
        phoneNumber.append( oldPhoneNumber );
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
        phoneNumberText.setKeyListener( null );
        hideKeyboard( phoneNumberText );
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
        phoneNumberText.setClickable(false);
        // Revert to the old phone number before editing started
        phoneNumberText.setText(this.oldPhoneNumber);
        phoneNumberText.setKeyListener( null );

        hideKeyboard( phoneNumberText );
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
        emailView.setFocusableInTouchMode(true);
        emailView.setClickable(true);
        emailView.setKeyListener( (KeyListener) emailView.getTag() );
        emailView.requestFocus();
        emailView.setText( "" );
        emailView.append( oldEmailAddress );
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
        emailView.setClickable(false);
        String email = emailView.getText().toString();
        // Since editing was confirmed, overwrite old value of email
        this.oldEmailAddress = email;
        // TODO Check if valid email and update elasticsearch

        hideKeyboard( emailView );

        emailView.setKeyListener( null );
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
        emailView.setClickable(false);
        // Revert to the old email address before editing started
        emailView.setText(this.oldEmailAddress);
        hideKeyboard( emailView );

        emailView.setKeyListener( null );
    }

    // TODO src: http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public void hideKeyboard( View v ) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}