package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RiderViewRequestActivity extends Activity {
    private TextView fareAmountTextView;
    private TextView startLocationTextView;
    private TextView endLocationTextView;
    private TextView descriptionTextView;
    private TextView riderTextView;
    private TextView driverTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        getViewIds();

    }

    /**
     * Will initialize the view ids for all the views in the activity.
     */
    private void getViewIds() {
        fareAmountTextView = (TextView) findViewById(R.id.textView_$fareAmount);
        startLocationTextView = (TextView) findViewById(R.id.textView_start);
        endLocationTextView = (TextView) findViewById(R.id.textView_end);
        descriptionTextView = (TextView) findViewById(R.id.textView_description);
        riderTextView = (TextView) findViewById(R.id.textView_rider);
        driverTextView = (TextView) findViewById(R.id.textView_driver);
    }
}
