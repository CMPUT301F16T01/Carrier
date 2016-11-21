package comcmput301f16t01.github.carrier.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;

/**
 * LoginActivity is where the user enters a username and password so they can access their account.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tryQuickLogin();

        // TODO grab their username based on their ID?
        // TODO alert them if they cannot log in because they are offline
    }

    /**
     * Attempts to log in a user from memory.
     * @see LoginMemory
     */
    private void tryQuickLogin() {
        LoginMemory lm = new LoginMemory( this );
        String username = lm.loadUsername();

        if (username == null) {
            return;
        }

        if (username.trim().equals("")) {
            return;
        }

        UserController uc = new UserController();

        if ( !uc.logInUser( username ) ) {
            EditText usernameEditText = (EditText) findViewById( R.id.EditText_username );
            usernameEditText.setText( username );
            AlertDialog.Builder adb = new AlertDialog.Builder( this );
            String message = "Your account '" + username + "' was not found!";
            adb.setTitle( "Warning!" );
            adb.setMessage( message );
            adb.setPositiveButton( "OK", null );
        } else {
            enterApp( username );
        }
    }

    /**
     * After pressing login, the system attempts to log them in with their given username here
     */
    public void attemptLogin(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.EditText_username);
        String username = usernameEditText.getText().toString().trim();

        UserController uc = new UserController();
        if (!uc.logInUser(username)) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
        } else {
            // Save username to file
            LoginMemory lm = new LoginMemory( this );
            lm.saveUsername( username );

            enterApp( username );
        }
    }

    /**
     * Standard welcome when entering the application.
     */
    private void enterApp( String username ) {
        String welcome = "Welcome back, " + username + "!";
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Opens the activity where the user can create a new profile, etc
     */
    public void startNewUserActivity(View v) {
        Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
        startActivity(intent);
        this.finish();
    }
}

