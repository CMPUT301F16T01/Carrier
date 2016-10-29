package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RiderViewRequestActivity extends Activity {
    private TextView fareAmountTextView;
    private TextView startLocationTextView;
    private TextView endLocationTextView;
    private TextView descriptionTextView;
    private TextView riderTextView;
    private TextView driverTextView;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        getViewIds();
        setViewValues(request);
        // RequestController rc = new RequestController();


    }

    /**
     * Will initialize the view values to the values from the request
     * @param request The request that the values come from
     */
    private void setViewValues(Request request) {
        fareAmountTextView.setText(request.getFare());
        startLocationTextView.setText(request.getStart().toString());
        endLocationTextView.setText(request.getEnd().toString());
        descriptionTextView.setText(request.getDescription());
        riderTextView.setText(request.getRider().getUsername());
        driverTextView.setText(request.getConfirmedDriver().getUsername());
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

    public void cancelRequest(View v){
        RequestController rc = new RequestController();
        // TODO Add a dialog prompt.
        rc.cancelRequest(request.getRider(), request);

    }
}
