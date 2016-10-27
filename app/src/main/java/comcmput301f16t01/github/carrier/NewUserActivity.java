package comcmput301f16t01.github.carrier;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    /**
     *
     * @param v The button (view) the user presses that calls this method.
     */
    public void submitNewUser(View v) {
        EditText usernameEditText = (EditText) findViewById( R.id.editText_newUsername );
        EditText phoneEditText = (EditText) findViewById( R.id.editText_newPhoneNum);
        EditText emailEditText = (EditText) findViewById( R.id.editText_newEmail );

        UserController uc = new UserController();
        String result = uc.createNewUser( usernameEditText.getText().toString(),
                phoneEditText.getText().toString(),
                emailEditText.getText().toString() );

        // Ensure that a new user was created
        if ( result == null ) {
            // TODO return and phone will allow login (?)
        } else { // if not, display returned result message as an AlertDialog
            AlertDialog.Builder adb = new AlertDialog.Builder( this );
            adb.setTitle( "ERROR" );
            adb.setMessage( result );
            adb.setPositiveButton( "OK", null );
            adb.show();
        }
    }
}
