package comcmput301f16t01.github.carrier.Users;

import android.app.Activity;
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
 * This is the class a user will use to input information about their vehicle
 * Once they click register they will then be added as a new user
 */

public class NewVehicleActivity extends AppCompatActivity {
    final Activity activity = NewVehicleActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
    }

    /**
     * We will get all the information for the edit text feilds.
     * We will call a function to concatenate the vehicle description
     * We will create a new user with all this information
     * @param v This is the view for the function
     */
    public void register(View v) {
        Log.i("activity","made it first");
        EditText makeEditText = (EditText) findViewById(R.id.editText_vehicleMake);
        EditText modelEditText = (EditText) findViewById(R.id.editText_vehicleModel);
        EditText yearEditText = (EditText) findViewById(R.id.editText_vehicleYear);
        EditText colorEditText = (EditText) findViewById(R.id.editText_vehicleColor);
        EditText otherNotesEditText = (EditText) findViewById(R.id.editText_vehicleOther);

        //This is the funtion that will make a pretty string out of all the info given
        String vehicleDescription = concatenateVehicleDescription(makeEditText.getText().toString(),
                modelEditText.getText().toString(), yearEditText.getText().toString(),
                colorEditText.getText().toString(), otherNotesEditText.getText().toString());

        //we will unpack for the bundle that was sent to us
        //from the new user activity
        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("username");
        String phone = bundle.getString("phone");
        String email = bundle.getString("email");

        //create a new user
        //this is what adds to elastic search
        UserController uc = new UserController();
        uc.createNewUser(username, email, phone, vehicleDescription);

        //go tot the main activity with this function
        submitNewUser(username);

        activity.finish();
    }

    /**
     * This function is what brings the user to the requests page
     * It toasts that they have logged in successfully
     * @param username The user we are trying to create
     */
    public void submitNewUser( String username) {
        // Save username to file
        LoginMemory lm = new LoginMemory( activity );
        lm.saveUsername( username );

        // Log in to CARier
        String welcome = "Welcome to CARrier, " + username + "!";
        Intent intent = new Intent(activity, MainActivity.class);
        Toast.makeText(activity, welcome, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        activity.finish();
    }

    /**
     * This function will take these multiple strings and concatentate them into a nice string that is easy to store and display
     *
     * @param make      The make of the user's vehicle
     * @param model     The model of the user's vehicle
     * @param year      The year of the user's vehicle
     * @param color     The color of the user's vehicle
     * @param otherNotes     The notes the user added to their vehicle
     * @return A string with all the info that is easy to display and read
     */
    private String concatenateVehicleDescription(String make, String model, String year,
                                                 String color, String otherNotes) {
        String vehicleDescription = "";

        // If all squares are empty then just leave the string empty
        if ( make.equals("") && model.equals("") && year.equals("")
        && color.equals("") && otherNotes.equals("") ) {
            vehicleDescription = "No description entered";
        }
        //else we need to piece the string together
        else {
            if (!year.equals("")) {
                vehicleDescription += year;
                vehicleDescription += " ";
            }
            if (!make.equals("")) {
                vehicleDescription += make;
                vehicleDescription += " ";
            }
            if (!model.equals("")) {
                vehicleDescription += model;
                vehicleDescription += "\n";
            }
            if (!color.equals("")) {
                vehicleDescription += color;
                vehicleDescription += "\n";
            }
            if (!otherNotes.equals("")) {
                vehicleDescription += otherNotes;
            }
        }
        return vehicleDescription;
    }
}
