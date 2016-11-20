package comcmput301f16t01.github.carrier.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;

/**
 * Created by meind on 2016-11-17.
 */

public class NewVehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
    }

    public void register(View v) {
        EditText makeEditText = (EditText) findViewById(R.id.editText_vehicleMake);
        EditText modelEditText = (EditText) findViewById(R.id.editText_vehicleModel);
        EditText yearEditText = (EditText) findViewById(R.id.editText_vehicleYear);
        EditText colorEditText = (EditText) findViewById(R.id.editText_vehicleColor);
        EditText otherEditText = (EditText) findViewById(R.id.editText_vehicleOther);

        String vehicleDescription = concatenateVehicleDescription(makeEditText.getText().toString(),
                modelEditText.getText().toString(), yearEditText.getText().toString(),
                colorEditText.getText().toString(), otherEditText.getText().toString());

        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("username");
        String phone = bundle.getString("phone");
        String email = bundle.getString("email");

        UserController uc = new UserController();
        uc.createNewUser(username, email, phone, vehicleDescription);

        submitNewUser(username);
    }

    /**
     * @param username The user we are trying to create
     */
    public void submitNewUser( String username) {
        // Save username to file
        LoginMemory lm = new LoginMemory( this );
        lm.saveUsername( username );

        // Log in to CARier
        String welcome = "Welcome to CARrier, " + username + "!";
        Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        this.finish();
    }


    /**
     * This function will take these multiple strings and concatentate them into a nice string that is easy to store and display
     *
     * @param make      The make of the user's vehicle
     * @param model     The model of the user's vehicle
     * @param year      The year of the user's vehicle
     * @param color     The color of the user's vehicle
     * @param other     The notes the user added to their vehicle
     * @return A string with all the info that is easy to display and read
     */
    private String concatenateVehicleDescription(String make, String model, String year,
                                                 String color, String other) {
        String vehicleDescription = "";

        // If all squares are empty then just leave the string empty
        if ( make.equals("") && model.equals("") && year.equals("")
        && color.equals("") && other.equals("") ) {
            vehicleDescription = "";
        }
        //else we need to piece the string together
        else {
            if (!make.equals("")) {
                vehicleDescription += make;
                vehicleDescription += ", ";
            }
            if (!model.equals("")) {
                vehicleDescription += model;
                vehicleDescription += ", ";
            }
            if (!year.equals("")) {
                vehicleDescription += year;
                vehicleDescription += ", ";
            }
            if (!color.equals("")) {
                vehicleDescription += color;
                vehicleDescription += ", ";
            }
            if (!other.equals("")) {
                vehicleDescription += other;
            }
        }
        return vehicleDescription;
    }
}
