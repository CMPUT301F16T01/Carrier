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

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        // Get the TextViews for the information that is going to be shown.
        EditText userName = (EditText) findViewById(R.id.NameEditText);
        EditText emailAddress = (EditText) findViewById(R.id.EmailEditText);
        EditText phoneNumber = (EditText) findViewById(R.id.PhoneEditText);
        // TODO Set the TextView to the proper values.
}

    public void editPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        TextView phoneNumber = (TextView) findViewById(R.id.PhoneEditText);
        // Set it so the user can edit the TextView
        phoneNumber.setFocusable(true);
    }

    public void saveEditedPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.PhoneEditText);
        // Set it so the user can edit the TextView
        phoneNumberText.setFocusable(false);
        String phoneNumber = phoneNumberText.getText().toString();
        // TODO Check if it is a valid phoneNumber
        // TODO Update The information in elasticsearch
    }
    public void cancelEditPhoneNumber(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditPhoneButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.PhoneSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.PhoneEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        EditText phoneNumberText = (EditText) findViewById(R.id.PhoneEditText);
        // Set it so the user can edit the TextView
        phoneNumberText.setFocusable(false);
        // TODO Set the EditText to the value it originally was.
    }

    public void editEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        TextView emailView = (TextView) findViewById(R.id.EmailEditText);
        // Set it so the user can edit the TextView
        emailView.setFocusable(true);
    }

    public void saveEditedEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        TextView emailView = (TextView) findViewById(R.id.EmailEditText);
        // Set it so the user can edit the TextView
        emailView.setFocusable(false);
        String email = emailView.getText().toString();
        // TODO Check if valid email and update elasticsearch
    }
    public void cancelEditEmailAddress(View v){
        ImageButton cancelButton = (ImageButton) findViewById(R.id.CancelEditEmailButton);
        ImageButton saveButton = (ImageButton) findViewById(R.id.EmailSaveEditButton);
        ImageButton editButton = (ImageButton) findViewById(R.id.EmailEditIconImageButton);
        // Set visibility of the buttons.
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        TextView emailView = (TextView) findViewById(R.id.EmailEditText);
        // Set it so the user can edit the TextView
        emailView.setFocusable(false);
        // TODO set email address to what it was before being edited.
    }


}