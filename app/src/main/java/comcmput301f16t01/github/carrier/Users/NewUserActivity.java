package comcmput301f16t01.github.carrier.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.LoginActivity;
import comcmput301f16t01.github.carrier.LoginMemory;
import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;

/**
 * User can enter their information and a username to create a new account.
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
     * @param v The button (view) the user presses that calls this method.
     */
    public void submitNewUser(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.editText_newUsername);
        EditText phoneEditText = (EditText) findViewById(R.id.editText_newPhoneNum);
        EditText emailEditText = (EditText) findViewById(R.id.editText_newEmail);

        String username = usernameEditText.getText().toString();

        // Send new user to UserController
        UserController uc = new UserController();
        String result = uc.createNewUser(username,
                emailEditText.getText().toString(),
                phoneEditText.getText().toString());

        // Ensure that a new user was created
        if (result == null) {
            // Save username to file
            LoginMemory lm = new LoginMemory( this );
            lm.saveUsername( username );

            // Log in to CARier
            String welcome = "Welcome to CARrier, " + username + "!";
            Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
            Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
            startActivity(intent);
            this.finish();
        } else { // if not, display returned result message as an AlertDialog
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("ERROR");
            adb.setMessage(result);
            adb.setPositiveButton("OK", null);
            adb.show();
        }
    }
}
