package comcmput301f16t01.github.carrier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * LoginActivity is where the user enters a username and password so they can access their account.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO grab their username based on their ID?
        // TODO alert them if they cannot log in because they are offline
    }

    public void startMainActivity(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.EditText_username);

        UserController uc = new UserController();
        if (!uc.logInUser(usernameEditText.getText().toString())) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
        } else {
            String welcome = "Welcome back, " + usernameEditText.getText().toString() + "!";
            Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void startNewUserActivity(View v) {
        Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
        startActivity(intent);
        this.finish();
    }
}

