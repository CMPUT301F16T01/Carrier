package comcmput301f16t01.github.carrier.Requests;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.google.gson.Gson;

import java.util.Currency;
import java.util.Locale;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.FareCalculator;
import comcmput301f16t01.github.carrier.MainActivity;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.SetLocationsActivity;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Activity for making a request. User can set the start and end locations,
 * the fare and an optional description before submitting it.
 *
 * See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#makerequestactivity">MakeRequestActivity</a>
 *
 * Incrementing/decrementing arrows code based on: <a href="http://stackoverflow.com/questions/7938516/continuously-increase-integer-value-as-the-button-is-pressed">Continuously increase integer value as the button is pressed</a>
 * Author: <a href="http://stackoverflow.com/users/525319/yar">Yar</a>
 * Posted on: October 29th, 2011
 * Retrieved on: November 5th, 2016
 *
 * Based on: <a href="http://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android">How to pass data from 2nd activity to 1st activity when pressed back? - android</a>
 * Author: <a href="http://stackoverflow.com/users/1202025/%CF%81%D1%8F%CF%83%D1%95%CF%81%D1%94%D1%8F-k">ρяσѕρєя K</a>
 * Posted on: January 12th, 2013
 * Retrieved on: November 7th, 2016
 */
public class MakeRequestActivity extends AppCompatActivity {

    // result code for when we return to an instance of this activity
    private static final int PASS_ACTIVITY_BACK = 1;
    final Activity activity = MakeRequestActivity.this;
    /**
     * Determines how fast the arrows increment/decrement the estimated fare.
     */
    final int REPEATED_DELAY = 25;

    private CarrierLocation start = null;
    private CarrierLocation end = null;
    private double distance = 0;
    private double duration = 0;
    private int fareEstimated = -1;

    private Handler repeatUpdateHandler = new Handler();
    private boolean autoIncrement = false;
    private boolean autoDecrement = false;

    /**
     * Class that runs in a thread to handle the repeated increments/decrements
     * to the estimated fare.
     */
    class RepeatUpdater implements Runnable {
        public void run() {
            if(autoIncrement) {
                incrementFare(null);
                repeatUpdateHandler.postDelayed( new RepeatUpdater(), REPEATED_DELAY);
            } else if(autoDecrement) {
                decrementFare(null);
                repeatUpdateHandler.postDelayed( new RepeatUpdater(), REPEATED_DELAY);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        setTitle("New Request");
        setButtons(); // setting increment and decrement fare buttons
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // if this came from the location selector we have data to save here
        Intent intent = getIntent();
        if(intent.hasExtra("startLocation")) {
            start = new Gson().fromJson(intent.getStringExtra("startLocation"), CarrierLocation.class);
        }
        if(intent.hasExtra("endLocation")) {
            end = new Gson().fromJson(intent.getStringExtra("endLocation"), CarrierLocation.class);
        }
        if(intent.hasExtra("distance")) {
            distance = intent.getDoubleExtra("distance", 0);
        }
        if(intent.hasExtra("duration")) {
            duration = intent.getDoubleExtra("duration", 0);
        }
    }

    /**
     * This is called when we startActivityForResult from here and get a result back when that activity finishes.
     * This allows us to do any "clean up actions" when we get back here. In this case, our "clean up" actions are
     * getting the start and end points, and the distance and duration from the intent.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PASS_ACTIVITY_BACK) {
            if(resultCode == RESULT_OK) {
                start = new Gson().fromJson(intent.getStringExtra("startLocation"), CarrierLocation.class);
                end = new Gson().fromJson(intent.getStringExtra("endLocation"), CarrierLocation.class);
                distance = intent.getDoubleExtra("distance", 0);
                duration = intent.getDoubleExtra("duration", 0);
            }
        }
    }

    /**
     * Set the buttons for incrementing and decrementing the fare estimate by the user holding it down
     */
    private void setButtons() {
        ImageButton fareUpButton = (ImageButton) findViewById(R.id.imageButton_fareUp);
        fareUpButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fareEstimated != -1) {
                    autoIncrement = true;
                    repeatUpdateHandler.post(new RepeatUpdater());
                }
                return false;
            }
        });
        fareUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // stop auto-incrementing when the user stops pressing down the button
                if((motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) && autoIncrement) {
                    autoIncrement = false;
                }
                return false;
            }
        });

        ImageButton fareDownButton = (ImageButton) findViewById(R.id.imageButton_fareDown);
        fareDownButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                autoDecrement = true;
                repeatUpdateHandler.post(new RepeatUpdater());
                return false;
            }
        });
        fareDownButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // stop auto-decrementing when the user stops pressing down the button
                if((motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) && autoDecrement) {
                    autoDecrement = false;
                }
                return false;
            }
        });
    }

    /**
     * Choose the start and end locations for the trip on a map
     * @param view The calling view of this function
     */
    public void chooseLocations(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("point","start");
        bundle.putString("type","existing");
        // if start or end has already been assigned, we will have this marker set on the map
        if(start == null) {
            start = new CarrierLocation();
        } else {
            bundle.putString("startLocation", new Gson().toJson(start));
        }
        if(end == null) {
            end = new CarrierLocation();
        } else {
            bundle.putString("endLocation", new Gson().toJson(end));
        }

        Intent intent = new Intent(MakeRequestActivity.this, SetLocationsActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, PASS_ACTIVITY_BACK);
    }

    /**
     * Allows the user to view a map
     * @param view The calling view of this function
     */
    public void viewMap(View view) {
        Intent intent = new Intent(activity, ViewLocationsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "existing");
        bundle.putString("startLocation", new Gson().toJson(start));
        bundle.putString("endLocation", new Gson().toJson(end));
        intent.putExtras(bundle);
        startActivityForResult(intent, PASS_ACTIVITY_BACK);
    }

    /**
     * When back is pressed
     *      Pop up an AlertDialog to confirm that the user will cancel their request.
     *      Return to MainActivity.
     */
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure you want to cancel request?");
        adb.setMessage("You will lose this request.");
        adb.setCancelable(true);
        final Activity activity = MakeRequestActivity.this;
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                Intent intent = new Intent(MakeRequestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("No", null );
        adb.show();
    }

    /**
     * Use the FareCalculator to estimate the fare between the user-selected start and end locations.
     * The start and end locations must have both been selected before the fare can be estimated.
     * @param view Estimate Fare button
     */
    public void estimateFare(View view) {
        if(start == null || end == null) {
            Toast.makeText(activity, "You must first select a start and end location", Toast.LENGTH_SHORT).show();
        } else {
            FareCalculator fc = new FareCalculator();
            int fareEstimate = fc.getEstimate(distance, duration);
            Currency localCurrency = Currency.getInstance( Locale.getDefault() );
            TextView currencyTextView = (TextView) findViewById(R.id.textView_currencySign);
            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            currencyTextView.setText(localCurrency.getSymbol());
            fareTextView.setText(fc.toString(fareEstimate));

            fareEstimated = fareEstimate;
        }
    }

    /**
     * Increase fare by 1 when up arrow is pressed.
     * @param view Up arrow button
     */
    public void incrementFare(View view) {
        FareCalculator fc = new FareCalculator();
        if(fareEstimated != -1) {
            fareEstimated++;
            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(fc.toString(fareEstimated));
        }
    }

    /**
     * Decrease fare by 1 when down arrow is pressed.
     * @param view Down arrow button
     */
    public void decrementFare(View view) {
        FareCalculator fc = new FareCalculator();
        if(fareEstimated > 0) {
            fareEstimated--;
            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(fc.toString(fareEstimated));
        }
    }

    /**
     * When the submit button is pressed
     *      Create a Request and add it to the request controller
     *      Return to MainActivity
     * @param view Submit button
     */
    public void submitRequest(View view) {
        RequestController rc = new RequestController();
        UserController uc = new UserController();

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

        String result = rc.addRequest(request);

        // Check that a new request was created
        if (result == null) {
            Toast.makeText(activity, "Request submitted", Toast.LENGTH_SHORT).show();
            activity.finish();
        } else { // if not, display returned result message as a Toast
            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        }
    }
}
