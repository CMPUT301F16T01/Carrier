package comcmput301f16t01.github.carrier;

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
import java.util.Locale;
import android.os.Handler;

import com.google.gson.Gson;
import android.location.Location;

/*
 The code for incrementing/decrementing the fare while holding down
 the up and down arrows is based on: https://goo.gl/zKpYnX
 Author: Yar
 Retrieved on: November 5th, 2016
  */

public class MakeRequestActivity extends AppCompatActivity {

    private static final int DEFAULT_REQ_CODE = 1;
    final Activity activity = MakeRequestActivity.this;
    /**
     * Determines how fast the arrows increment/decrement the estimated fare
     */
    final int REP_DEL = 25;

    private Location start = null;
    private Location end = null;
    private int fareEstimated = -1;

    private Handler repeatUpdateHandler = new Handler();
    private boolean autoIncrement = false;
    private boolean autoDecrement = false;

    class RepeatUpdater implements Runnable {
        public void run() {
            if(autoIncrement) {
                incrementFare(null);
                repeatUpdateHandler.postDelayed( new RepeatUpdater(), REP_DEL);
            } else if(autoDecrement) {
                decrementFare(null);
                repeatUpdateHandler.postDelayed( new RepeatUpdater(), REP_DEL);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        setTitle("New Request");
        setButtons();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    // from: http://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
    // author: ρяσѕρєя K
    // retrieved on: November 7th, 2016
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == DEFAULT_REQ_CODE) {
            if(resultCode == RESULT_OK){
                // from LonelyTwitter
                start = new Gson().fromJson(intent.getStringExtra("startLocation"), Location.class);
                end = new Gson().fromJson(intent.getStringExtra("endLocation"), Location.class);
                TextView tv = (TextView) findViewById(R.id.textView_start);
                tv.setText(start.toString());
                tv = (TextView) findViewById(R.id.textView_end);
                tv.setText(end.toString());
            }
        }
    }

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
                if((motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) && autoDecrement) {
                    autoDecrement = false;
                }
                return false;
            }
        });
    }

    public void chooseLocations(View view) {
        if(start == null) start = new Location("");
        if(end == null) end = new Location("");

        Intent intent = new Intent(MakeRequestActivity.this, SetLocationsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("point","start");
        intent.putExtras(bundle);
        startActivityForResult(intent, DEFAULT_REQ_CODE);
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
     */
    public void estimateFare(View view) {
        // TODO possibly do check within FareCalculator...need to wait for this to be completed
        if(start == null || end == null) {
            Toast.makeText(activity, "You must first select a start and end location", Toast.LENGTH_SHORT).show();
        } else {
            // TODO use FareCalculator once available
            // the MockFareCalculator generates random numbers so we can see different values on the display
            MockFareCalculator fc = new MockFareCalculator();
            int fareEstimate = fc.getEstimate();

            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(formatFare(fareEstimate));

            fareEstimated = fareEstimate;
        }
    }

    /**
     * Increase fare by 1 when up arrow is pressed.
     */
    public void incrementFare(View view) {
        if(fareEstimated != -1) {
            fareEstimated++;
            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(formatFare(fareEstimated));
        }
    }

    /**
     * Decrease fare by 1 when down arrow is pressed.
     */
    public void decrementFare(View view) {
        if(fareEstimated > 0) {
            fareEstimated--;
            TextView fareTextView = (TextView) findViewById(R.id.textView_fareEstimate);
            fareTextView.setText(formatFare(fareEstimated));
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
        String str = String.format(Locale.getDefault(),"%d",(long)fare) + ".";
        String dec = String.format(Locale.getDefault(),"0%.0f",(fare%1)*100);
        // format the fare as a string with 2 decimal points
        str +=  dec.substring(dec.length()-2, dec.length());
        return str;
    }

    /**
     * When the submit button is pressed
     *      Create a Request and add it to the request controller
     *      Save the request (elastic search...through request controller?)
     *      Return to MainActivity
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
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
        } else { // if not, display returned result message as a Toast
            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        }
    }
}
