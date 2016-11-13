package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Retrieve the user this intent was started with.
        Bundle bundle = getIntent().getExtras();
        User user = bundle.getParcelable("user");

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
        EditText usernameEditText = (EditText) findViewById(R.id.EditText_name);
        EditText emailAddressEditText = (EditText) findViewById(R.id.EditText_email);
        EditText phoneNumberEditText = (EditText) findViewById(R.id.EditText_phone);

        //Save old values in case the user changes their mind about editing.
        usernameEditText.setText(username);
        emailAddressEditText.setText(oldEmailAddress);
        phoneNumberEditText.setText(oldPhoneNumber);

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
            ElasticUserController.EditUserTask eut = new ElasticUserController.EditUserTask();
            eut.execute(currentUser.getId(), currentUser.getEmail(), phoneNumber);
        }
        // Since editing was confirmed, overwrite old value of phone number of the current user
        this.oldPhoneNumber = phoneNumber;
        currentUser.setPhone(phoneNumber);
        // The edit button is weirdly dissapearing? This fixes it.
        editButton.setVisibility(View.VISIBLE);
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
    }

    /**
     * Tapping the edit icon next to the email allows the user to edit their email
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void editEmailAddress(View v) {
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
            ElasticUserController.EditUserTask eut = new ElasticUserController.EditUserTask();
            eut.execute(currentUser.getId(), email, currentUser.getPhone());
        }
        // Since editing was confirmed, overwrite old value of email int he current user.
        this.oldPhoneNumber = email;
        currentUser.setEmail(email);
    }

    /**
     * Tapping the cancel icon during an edit removes changes to the email field
     *
     * @param v v is a View, allows usage of on click in xml
     */
    public void cancelEditEmailAddress(View v) {
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
}