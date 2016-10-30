package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MakeRequestActivity extends AppCompatActivity {

    final Activity activity = MakeRequestActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        setTitle("New Request");

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // TODO set up the location buttons (open map view)

        // TODO set up the estimate fare button

        Button submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                submitRequest();
            }
        });
    }

    /**
     * When back is pressed
     *      Pop up an AlertDialog to confirm that the user will cancel their request.
     *      Return to MainActivity.
     */
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        adb.setMessage("Cancel request?");
        adb.setCancelable(true);
        final Activity activity = MakeRequestActivity.this;
        // TODO semantics re: cancel
        adb.setPositiveButton("Cancel request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                Intent intent = new Intent(MakeRequestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("Cancel", null );
        adb.show();
    }

    /**
     * When the submit button is pressed
     *      Create a Request and add it to the request controller
     *      Save the request (elasticsearch...through request controller?)
     *      Return to MainActivity
     */
    public void submitRequest() {
        RequestController rc = new RequestController();
        UserController uc = new UserController();

        // TODO create a request using all data currently on the screen
        User user = uc.getLoggedInUser();

        // TODO get the start and end locations from the map view
        // TODO enforce that the user inputs a start and end location
        // we need to set this with the lat/lon pair returned from the location selection on the map
        // should be done with returned values when it comes back from the map activity
        Location start = new Location();
        Location end = new Location();

        EditText descEditText = (EditText) findViewById(R.id.editText_description);
        String description = descEditText.getText().toString();

        Request request;
        if(description.equals("")) {
            request = new Request(user, start, end);
        } else {
            request = new Request(user, start, end, description);
        }

        rc.addRequest(request);

        // TODO save the data (elasticsearch...will this be automatically done from within rc?)

        Toast.makeText(MakeRequestActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
        activity.finish();
        Intent intent = new Intent(MakeRequestActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
