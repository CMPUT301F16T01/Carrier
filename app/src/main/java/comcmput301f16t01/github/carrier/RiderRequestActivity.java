package comcmput301f16t01.github.carrier;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class RiderRequestActivity extends AppCompatActivity {

    //this is just used to make it work for now
    // TODO remove this
    private Integer position = 0;
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
        setContentView(R.layout.activity_rider_view_request);
        // Initialize the view ids
        getViewIds();

        //getting the request controller to get a list of requests
        RequestController rc = new RequestController();
        User loggedInUser = UserController.getLoggedInUser();
        ArrayList<Request> requestList = rc.getRequests(loggedInUser);

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        request = requestList.get(position);
        // Populate values for the different text views.
        setViewValues(request);
        //changing the status image
        ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
        // TODO showing the status properly...
        // Set up the status icon depending on the status of the request
        if (statusImageView != null) {
            switch (request.getStatus()) {
                case (Request.OPEN):
                    statusImageView.setImageResource(R.drawable.open);
                    //statusImageView.setBackgroundResource(R.color.openStatus);
                    break;
                case (Request.OFFERED):
                    statusImageView.setImageResource(R.drawable.offered);
                    //statusImageView.setBackgroundResource(R.color.offeredStatus);
                    break;
                case (Request.CONFIRMED):
                    statusImageView.setImageResource(R.drawable.confirmed);
                    //statusImageView.setBackgroundResource(R.color.confirmedStatus);
                    break;
                case (Request.COMPLETE):
                    statusImageView.setImageResource(R.drawable.complete);
                    //statusImageView.setBackgroundResource(R.color.completeStatus);
                    break;
                case (Request.PAID):
                    statusImageView.setImageResource(R.drawable.paid);
                    //statusImageView.setBackgroundResource(R.color.paidStatus);
                    break;
                case (Request.CANCELLED):
                    statusImageView.setImageResource(R.drawable.cancel);
                    //statusImageView.setBackgroundResource(R.color.cancelledStatus);
                    break;

            }
        }
    }

    /**
     * Will initialize the view values to the values from the request
     * @param request The request that the values come from
     */
    private void setViewValues(Request request) {
        fareAmountTextView.setText(Integer.toString(request.getFare())); // TODO Add locale support
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
        AlertDialog.Builder adb = new AlertDialog.Builder(RiderRequestActivity.this);
        adb.setMessage("Cancel request?");
        adb.setCancelable(true);
        adb.setPositiveButton("Cancel Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestController rc = new RequestController();
                rc.cancelRequest(request.getRider(), request);
            }
        });

        adb.setNegativeButton("Do not cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adb.show();
    }
}
