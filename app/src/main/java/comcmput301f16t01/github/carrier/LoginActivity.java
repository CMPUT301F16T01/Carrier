package comcmput301f16t01.github.carrier;

import android.app.Dialog;
import android.content.Intent;
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

    }

    public void startRiderActivity(View v) {
        Intent intent = new Intent(LoginActivity.this, RiderMainActivity.class );
        startActivity(intent);
        this.finish();
    }

    public void startDriverActivity(View v) {

    }
}

