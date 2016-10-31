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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

/*
This will be a long thought...

So I am running into some problems with the Request class so I've made executive
decisions for now that we may want to change. There is a getEstimate() method in
the Request class to estimate the method. It doesn't make sense to use that here
because I need the location to create the request but I don't want to make the
request partway through the user's activity here because they could cancel it. I
want to make the Request object when they hit the submit button. But I need to
show the user the fare estimate on the screen so I need the estimate before I
instantiate the Request object.

It makes sense for the Request object to require the location parameters because
those are necessary attributes for a Request to exist. I am assuming that if the
user doesn't make any changes to the location, the fare estimate will be the same
every time the method is called so maybe this is a non-issue but it just feels messy
to have a method that does it, but here I have to call the method directly from its
own class and then explicitly set the Request fare.

I don't know if this is even a problem but whoever's reviewing my code give me your
thoughts or ask me if this doesn't make sense...
 */

public class MakeRequestActivity extends AppCompatActivity {

    final Activity activity = MakeRequestActivity.this;
    private static Location start = null;
    private static Location end = null;
    private static double fareEstimated = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        setTitle("New Request");

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button locationButton = (Button) findViewById(R.id.button_chooseLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                chooseLocations();
            }
        });

        Button estimateButton = (Button) findViewById(R.id.button_estimateFare);
        estimateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                estimateFare();
            }
        });

        Button submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                submitRequest();
            }
        });
    }

    private void chooseLocations() {
        Toast.makeText(activity, "Choose locations on map", Toast.LENGTH_SHORT).show();

        if(start == null) start = new Location();
        if(end == null) end = new Location();

        // fake data, University of Alberta
        start.setLocation(53.5232, -113.5263);
        // fake data, Edmonton City Centre
        end.setLocation(53.5438, -113.4923);

        // TODO create the activity for selecting locations of request (later use case)
        // SetLocationsActivity does not exist, placeholder name
        //   this will be where the user sets the start and end locations of their request
        // Intent intent = new Intent(MakeRequestActivity.this, SetLocationsActivity.class);
        // startActivity(intent);
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

    public void estimateFare() {
        if(start == null || end == null) {
            Toast.makeText(activity, "You must first select a start and end location", Toast.LENGTH_SHORT).show();
        } else {
            FareCalculator fc = new FareCalculator(start, end);
            double fareEstimate = fc.getEstimate();

            // fake data
            fareEstimate = 65.24;

            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(formatFare(fareEstimate));

            fareEstimated = fareEstimate;
        }
    }

    //
    public String formatFare(double fare) {
        String ret = "";
        // format the fare as a string with 2 decimal points
        ret += String.format(Locale.getDefault(),"%.0f",fare/1) + "." +
                String.format(Locale.getDefault(),"%.0f",(fare%1)*100);
        // if there is no decimal, add an extra 0 to the string
        if(fare%1 == 0) ret += "0";
        return ret;
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

        if(start == null || end == null) {
            Toast.makeText(activity, "You must first select a start and end location", Toast.LENGTH_SHORT).show();
        } else if (fareEstimated != -1) {
            Toast.makeText(activity, "You must first estimate the ride fare", Toast.LENGTH_SHORT).show();
        } else {
            User user = uc.getLoggedInUser();

            EditText descEditText = (EditText) findViewById(R.id.editText_description);
            String description = descEditText.getText().toString();

            Request request;
            if(description.equals("")) {
                request = new Request(user, start, end);
            } else {
                request = new Request(user, start, end, description);
            }

            request.setFare(fareEstimated);

            rc.addRequest(request);

            // TODO save the data (elasticsearch...will this be automatically done from within rc?)

            Toast.makeText(MakeRequestActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
            activity.finish();
            Intent intent = new Intent(MakeRequestActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
