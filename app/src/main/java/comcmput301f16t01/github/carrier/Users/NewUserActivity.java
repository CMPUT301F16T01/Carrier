package comcmput301f16t01.github.carrier.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import comcmput301f16t01.github.carrier.R;

import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;

/**
 * User can enter their information and a username
 * They will then go to the next screen to enter vehicle info
 */
public class NewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }


    /**
     * This is the function that will take us to the next screen.
     * We will bundle all info entered, check validity and then send it to next activity
     * @param v the view needed for the function
     */

    public void nextVehicle(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.editText_newUsername);
        EditText phoneEditText = (EditText) findViewById(R.id.editText_newPhoneNum);
        EditText emailEditText = (EditText) findViewById(R.id.editText_newEmail);

        String username = usernameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Send new user to UserController
        UserController uc = new UserController();
        String result = uc.checkValidInputs(username, email, phone);

        // Ensure that result is okay
        if (result == null) {
            // We will go to a new activity to add vehicle stuff
            Intent intent = new Intent(NewUserActivity.this, NewVehicleActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("phone", phone);
            bundle.putString("email", email);
            intent.putExtras(bundle);
            startActivity(intent);
        } else { // if not, display returned result message as an AlertDialog
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("ERROR");
            adb.setMessage(result);
            adb.setPositiveButton("OK", null);
            adb.show();
        }
    }
}
