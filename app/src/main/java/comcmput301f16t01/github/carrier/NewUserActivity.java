package comcmput301f16t01.github.carrier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

        // Send new user to UserController
        UserController uc = new UserController();
        String result = uc.createNewUser(usernameEditText.getText().toString(),
                emailEditText.getText().toString(),
                phoneEditText.getText().toString());

        // Ensure that a new user was created
        if (result == null) {
            String welcome = "Welcome to CARrier, " + usernameEditText.getText().toString() + "!";
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
