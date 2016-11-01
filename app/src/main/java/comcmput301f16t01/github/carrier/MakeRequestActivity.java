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
the Request class to estimate the method (that calls the getEstimate() method in
the FareCalculator class. It doesn't make sense to use the Request method here
because I need the location to create the request (with the constructor) but I
don't want to make the request partway through the user's activity here because
they could cancel it. I want to make the Request object when they hit the submit
button. I don't want to modify the constructor because the start and end locations
are necessary information for the existence of a Request. Nevertheless, I need to
show the user the fare estimate on the screen so I need the estimate before I
instantiate the Request object.

It makes sense for the Request object to require the location parameters because
those are necessary attributes for a Request to exist. I am assuming that if the
user doesn't make any changes to the location, the fare estimate will be the same
every time the method is called so maybe this is a non-issue but it just feels messy
to have a method that does it, but here I have to call the method directly from its
own class and then explicitly set the Request fare.

I don't know if this is even a problem or if I am just overthinking things but
whoever's reviewing my code give me your thoughts or ask me if my use of these
classes and their methods doesn't make sense...
 */

public class MakeRequestActivity extends AppCompatActivity {

    final Activity activity = MakeRequestActivity.this;
    private static Location start = null;
    private static Location end = null;
    private static int fareEstimated = -1;

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
        Toast.makeText(activity, "Choose locations", Toast.LENGTH_SHORT).show();

        if(start == null) start = new Location();
        if(end == null) end = new Location();

        // TODO remove fake data
        // fake data, University of Alberta
        start.setLocation(53.5232, -113.5263);
        // fake data, Edmonton City Centre
        end.setLocation(53.5438, -113.4923);

        // prompts for fake data verification
        Toast.makeText(activity, "Location 1: (53.5232, -113.5263)", Toast.LENGTH_SHORT).show();
        Toast.makeText(activity, "Location 2: (53.5438, -113.4923)", Toast.LENGTH_SHORT).show();

        // TODO create the activity for selecting locations of request (later use case)
        //    the new activity will handle everything and will return back to use the
        //    start and end location coordinates
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
        // TODO semantics re: cancel, button text (is it confusing to have "cancel" and "cancel request"?)
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
     * Use the FareCalculator to estimate the fare between the user-selected start and end locations.
     * The start and end locations must have both been selected before the fare can be estimated.
     */
    public void estimateFare() {
        if(start == null || end == null) {
            Toast.makeText(activity, "You must first select a start and end location", Toast.LENGTH_SHORT).show();
        } else {
            // TODO use FareCalculator once available
            // the MockFareCalculator generates random numbers so we can see different values on the display
            MockFareCalculator fc = new MockFareCalculator(start, end);
            int fareEstimate = fc.getEstimate();

            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(formatFare(fareEstimate));

            fareEstimated = fareEstimate;
        }
    }

    /**
     * Format the fare we get as a double as a string to be printed on the screen.
     * This string will be preceded by a dollar sign (are we concerned about locale?)
     * and will be to 2 decimal places.
     * @param intFare
     * @return String
     */
    // TODO deprecate once toString is available in FareCalculator
    public String formatFare(int intFare) {
        double fare = ((double) intFare)/100;
        String str = String.format(Locale.getDefault(),"%.0f",fare/1) + ".";
        String dec = String.format(Locale.getDefault(),"0%.0f",(fare%1)*100);
        // format the fare as a string with 2 decimal points
        str +=  dec.substring(dec.length()-2, dec.length());
        return str;
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
