package comcmput301f16t01.github.carrier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

// TODO A lot can be done to reduce code duplication. Notice how there are 4 functions that close,
// and 2 that open. I feel like a lot of code generalization can be done here to reduce bugs
// from editing one thing and forgetting to edit the other four. This is a low priority issue ^Ben

/**
 * UserProfileActivity allows the user to view their profile information and edit their
 * contact information.
 */
public class UserProfileActivity extends AppCompatActivity {
    // Saves the values of the old fields just in case the user cancels their edit.
    private String oldPhoneNumber;
    private String oldEmailAddress;
    private User currentUser = UserController.getLoggedInUser();
    private User user;

    private Boolean editingPhone = false;
    private Boolean editingEmail = false;

    //used for calling permissions
    private static final int MY_PERMISSIONS_CALL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Retrieve the user this intent was started with.
        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("user");

        // Get an instance of the UserController
        UserController uc = new UserController();
        //User currentUser = UserController.getLoggedInUser();

        String username;

        // Developer code, may not be needed for release...
        if (user == null) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("debug mode");
            adb.setMessage("you managed to log in without a user. Test data will be shown. This is an error if you are not a developer.");
            adb.setPositiveButton("OK", null);
            adb.show();
            oldEmailAddress = "testEmail@youDaBomb.com";
            oldPhoneNumber = "7357129";
            username = "testUsername";
        } else {
            oldEmailAddress = user.getEmail();
            oldPhoneNumber = user.getPhone();
            username = user.getUsername();
        }

        // Get the TextViews for the information that is going to be shown.
        TextView usernameEditText = (TextView) findViewById(R.id.TextView_name);
        EditText emailAddressEditText = (EditText) findViewById(R.id.EditText_email);
        EditText phoneNumberEditText = (EditText) findViewById(R.id.EditText_phone);

        //Save old values in case the user changes their mind about editing.
        usernameEditText.setText(username);
        emailAddressEditText.setText(oldEmailAddress);
        phoneNumberEditText.setText(oldPhoneNumber);

        //check permissions
        checkPermissionsCall();

        // Removes the key listener, so that it can't hear keys.
        // Also stores it as their tag, so we can grab it later...
        phoneNumberEditText.setTag(phoneNumberEditText.getKeyListener());
        phoneNumberEditText.setKeyListener(null);
        emailAddressEditText.setTag(emailAddressEditText.getKeyListener());
        emailAddressEditText.setKeyListener(null);
        usernameEditText.setKeyListener(null);

        /*If profile being viewed is not the logged in user's, the edit buttons are hidden and are
        unclickable.
         */
        if (!user.getUsername().equals(UserController.getLoggedInUser().getUsername())) {
            ImageButton phoneEditButton = (ImageButton) findViewById(R.id.ImageButton_phoneEditIcon);
            ImageButton emailEditButton = (ImageButton) findViewById(R.id.ImageButton_emailEditIcon);
            phoneEditButton.setClickable(false);
            phoneEditButton.setVisibility(View.INVISIBLE);
            emailEditButton.setClickable(false);
            emailEditButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Tapping the edit icon next to the phone number allows the user to edit their phone
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void editPhoneNumber(View v) {
        editingPhone = true;
        ImageButton cancelButton = (ImageButton) findViewById(R.id.Button_cancelPhoneEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_savePhoneEdit);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_phoneEditIcon);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        TextView phoneNumber = (TextView) findViewById(R.id.EditText_phone);
        phoneNumber.setClickable(true);
        // Set it so the user can edit the EditText
        phoneNumber.setFocusableInTouchMode(true);
        phoneNumber.setFocusable(true);
        phoneNumber.setKeyListener((KeyListener) phoneNumber.getTag());
        phoneNumber.requestFocus();
        phoneNumber.moveCursorToVisibleOffset();
        phoneNumber.setText("");
        phoneNumber.append(currentUser.getPhone());
    }

    /**
     * Tapping the save icon after an edit commits changes to the phone number field
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void saveEditedPhoneNumber(View v) {
        ImageButton cancelButton = (ImageButton) findViewById(R.id.Button_cancelPhoneEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_savePhoneEdit);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_phoneEditIcon);
        // Set visibility of the buttons.

        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.EditText_phone);
        // Set it so the user can't edit the EditText
        phoneNumberText.setFocusable(false);
        String phoneNumber = phoneNumberText.getText().toString();
        // TODO Check if it is a valid phoneNumber
        phoneNumberText.setKeyListener(null);
        hideKeyboard(phoneNumberText);
        // If the user actually made changes to the field, update in elastic search
        if (!this.oldPhoneNumber.equals(phoneNumber)) {
            UserController.editUser(currentUser.getEmail(), phoneNumber);
        }
        // Since editing was confirmed, overwrite old value of phone number of the current user
        this.oldPhoneNumber = phoneNumber;
        // The edit button is weirdly dissapearing? This fixes it.
        editButton.setVisibility(View.VISIBLE);
        editingPhone = false;
    }

    /**
     * Tapping the cancel icon during an edit removes changes to the phone number field
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void cancelEditPhoneNumber(View v) {
        ImageButton cancelButton = (ImageButton) findViewById(R.id.Button_cancelPhoneEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_savePhoneEdit);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_phoneEditIcon);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.EditText_phone);
        // Set it so the user can't edit the EditText
        phoneNumberText.setFocusable(false);
        phoneNumberText.setClickable(false);
        // Revert to the old phone number before editing started
        phoneNumberText.setText(this.oldPhoneNumber);
        phoneNumberText.setKeyListener(null);

        hideKeyboard(phoneNumberText);
        editingPhone = false;
    }

    /**
     * Tapping the edit icon next to the email allows the user to edit their email
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void editEmailAddress(View v) {
        editingEmail = true;
        ImageButton cancelButton = (ImageButton) findViewById(R.id.ImageButton_cancelEmailEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_saveEmail);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_emailEditIcon);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EditText_email);
        // Set it so the user can edit the EditText
        emailView.setFocusableInTouchMode(true);
        emailView.setClickable(true);
        emailView.setKeyListener((KeyListener) emailView.getTag());
        emailView.requestFocus();
        emailView.setText("");
        emailView.append(currentUser.getEmail());
    }

    /**
     * Tapping the save icon after an edit commits changes to the email address
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void saveEditedEmailAddress(View v) {
        editingEmail = false;
        ImageButton cancelButton = (ImageButton) findViewById(R.id.ImageButton_cancelEmailEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_saveEmail);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_emailEditIcon);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EditText_email);
        // Set it so the user can edit the EditText
        emailView.setFocusable(false);
        emailView.setClickable(false);
        String email = emailView.getText().toString();
        // Since editing was confirmed, overwrite old value of email
        hideKeyboard(emailView);
        emailView.setKeyListener(null);
        // If the user actually made changes to the field, update in elastic search
        if (!this.oldEmailAddress.equals(email)) {
            UserController.editUser(email, currentUser.getPhone());
        }
        // Since editing was confirmed, overwrite old value of email int he current user.
        this.oldEmailAddress = email;
    }

    /**
     * Tapping the cancel icon during an edit removes changes to the email field
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void cancelEditEmailAddress(View v) {
        editingEmail = false;
        ImageButton cancelButton = (ImageButton) findViewById(R.id.ImageButton_cancelEmailEdit);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EditButton_saveEmail);
        ImageButton editButton = (ImageButton) findViewById(R.id.ImageButton_emailEditIcon);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText emailView = (EditText) findViewById(R.id.EditText_email);
        // Set it so the user can't edit the EditText
        emailView.setFocusable(false);
        emailView.setClickable(false);
        // Revert to the old email address before editing started
        emailView.setText(this.oldEmailAddress);
        hideKeyboard(emailView);

        emailView.setKeyListener(null);
    }

    // TODO src: http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * This is the function that calls the number presented
     *
     * @param v view used for this function
     */
    public void callPhone(View v) {
        /* Source: http://stackoverflow.com/questions/5403308/make-a-phone-call-click-on-a-button
        * Author: Shaista Naaz
        * Retrieved on: November 21st 2016 */
        if (!editingPhone) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String phoneClicked = "tel:" + user.getPhone();
            callIntent.setData(Uri.parse(phoneClicked));
            Log.i("activity", "made to function");

            //this if statement checks to make sure we have the correct permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "You do not have the correct permissions to make a phone call.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * This is the function that will email the email pressed
     * @param v the view the function is in
     */
    public void emailUser(View v) {
        /**
         * Source: http://stackoverflow.com/questions/21720640/sending-email-from-android-app-when-click-on-button
         * Author: localhost
         * Retrieved on: November 21st 2016
         */
        if (!editingEmail) {
            Intent email = new Intent(android.content.Intent.ACTION_SEND);
            email.setType("plain/text");
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
            email.putExtra(Intent.EXTRA_SUBJECT, "");
            email.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break; // permission was granted, yay!
                } else {
                    // permission denied, boo!
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle("Permissions Denied");
                    adb.setMessage("You will not be allowed to call in this app.");
                    adb.setCancelable(true);
                    adb.setPositiveButton("OK", null);
                    adb.show();
                }
                break;
            }
        }
    }

    /**
     * Asks user to grant required permissions for the maps to work.
     */
    private void checkPermissionsCall() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_CALL);
        }
    }
}