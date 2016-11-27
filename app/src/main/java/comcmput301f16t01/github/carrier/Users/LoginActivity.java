package comcmput301f16t01.github.carrier.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;

import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;


/**
 * LoginActivity is where the user enters a username and password so they can access their account.
 *
 * @see LoginMemory
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO grab their username based on their ID?
        // TODO alert them if they cannot log in because they are offline
    }

    /**
     * Called when the activity is resumed. Attempts to log users in by checking if a user is already
     * logged in. If no one is currently logged in it will execute tryQuickLogin which will look in
     * the internal memory to see if we can log a user in.
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            // If someone is already loggedIn we will enter the app immediatly. If not
            // an IllegalAccessError will be thrown meaning no one is logged in allowing us
            // to tryQuickLogin.
            enterApp(UserController.getLoggedInUser().getUsername());
            // Will check if someone is logged in.
            // If someone is not logged in we can do a tryQuickLogin()
        } catch (IllegalAccessError e) {
            tryQuickLogin();
        }
    }
    /**
     * If there is internet connection, attempts to login a user through
     * elastic search, otherwise attempts to login a cached user from
     * file. This login enters the app right away if the user has
     * logged in once before already.
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
        /* If there is internet connection, attempt to login the user from
        elastic search
         */
        if (ConnectionChecker.isThereInternet()) {
            if ( !UserController.logInUser( username ) ) {
                EditText usernameEditText = (EditText) findViewById( R.id.EditText_username );
                usernameEditText.setText( username );
                AlertDialog.Builder adb = new AlertDialog.Builder( this );
                String message = "Your account '" + username + "' was not found!";
                adb.setTitle( "Warning!" );
                adb.setMessage( message );
                adb.setPositiveButton( "OK", null );
            } else {
                enterApp( username );
                Toast.makeText(this, "Logged in online", Toast.LENGTH_LONG).show();
            }
            /*
            If the user is offline, login the user from file.
             */
        } else {
            User cachedUser = lm.loadUser();
            UserController.offlineLogInUser(cachedUser.getUsername(), cachedUser);
            enterApp(cachedUser.getUsername());
            Toast.makeText(this, "Logged in offline", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * After pressing login, the system attempts to log them in with their given username here
     */
    public void attemptLogin(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.EditText_username);
        String username = usernameEditText.getText().toString().trim();
        // Save username to file
        LoginMemory lm = new LoginMemory(this);
        // If there is internet connection, attempt to log in with elastic search
        if (ConnectionChecker.isThereInternet()) {
            if (!UserController.logInUser(username)) {
                Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
            } else {

                // Save username to file
                lm.saveUsername(username);
                lm.saveUser(UserController.getLoggedInUser());

                enterApp(username);
            }
        }
    /* Otherwise attempt login by loading the cached user and
    comparing usernames
     */
        else {
            User cachedUser = lm.loadUser();
            if (!UserController.offlineLogInUser(username, cachedUser)) {
                Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
            } else {
                enterApp(cachedUser.getUsername());
            }
        }
    }

    /**
     * Standard welcome when entering the application.
     */
    private void enterApp( String username ) {
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

