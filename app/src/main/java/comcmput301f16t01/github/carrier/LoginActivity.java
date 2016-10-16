package comcmput301f16t01.github.carrier;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

/**
 * LoginActivity is where the user enters a username and password so they can access their account.
 *
 * @author Kieter
 * @since Friday October 14th, 2016
 *
 */
/*
TODO javadoc this
TODO comment this
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserController uc = new UserController();

        // The editTexts that the user puts login credentials into
        EditText usernameEditText = (EditText) findViewById(R.id.UsernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.PasswordEditText);
        // The strings that the user typed
        String usernameString = usernameEditText.getText().toString();
        //TODO jarble this?
        String passwordString = passwordEditText.getText().toString();

        // Attempt to authenticate credentials and "log in"
        try {
            uc.authenticate();
        } catch (AuthenticationException authException){
            // Show the LoginErrorTextView
            TextView loginErrorTextView = (TextView) findViewById(R.id.LoginErrorTextView);
            loginErrorTextView.setVisibility(View.VISIBLE);
            // Shake it!
            loginErrorTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }

    }




}
