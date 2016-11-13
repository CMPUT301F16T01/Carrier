package comcmput301f16t01.github.carrier;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * This will help us show the request from the perspective of a rider
 */
public class RiderRequestActivity extends AppCompatActivity {

    //this is just used to make it work for now
    // TODO remove this
    private Integer position = 0;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_request);
        // Initialize the view ids

        //getting the request controller to get a list of requests
        RequestController rc = new RequestController();
        User loggedInUser = UserController.getLoggedInUser();
        ArrayList<Request> requestList = rc.getRequests(loggedInUser);

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");

        //changing the status image
        request = requestList.get(position);
        TextView descriptionTextView = (TextView) findViewById(R.id.TextView_description);
        descriptionTextView.setText(request.getDescription());

        // Set up the UsernameTextView of the rider
        UsernameTextView riderUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_rider);
        riderUsernameTextView.setText("Rider: " + request.getRider().getUsername());
        riderUsernameTextView.setUser(request.getRider());

        // Set up the UsernameTextView of the driver
        UsernameTextView driverUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_driver);
        if (request.getChosenDriver() != null) {
            driverUsernameTextView.setText("Driver: " + request.getChosenDriver().getUsername());
            driverUsernameTextView.setUser(request.getChosenDriver());
        }

        /**
         * This switch statement changes the status image
         * There is currently coloring code in here which we may use
         * in the future for a notification system?
         */
        ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
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
     * Will initialize the view ids for all the views in the activity.
     */

    public void cancelRequest(View v){
        AlertDialog.Builder adb = new AlertDialog.Builder(RiderRequestActivity.this);
        if ((request.getStatus() != Request.CANCELLED) && (request.getStatus() != Request.COMPLETE)
                && (request.getStatus() != Request.PAID)) {
            adb.setMessage("Cancel request?");
            adb.setCancelable(true);
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RequestController rc = new RequestController();
                    rc.cancelRequest(request.getRider(), request);
                    finish();
                }
            });

            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        else {
            adb.setTitle("Error: ");
            adb.setMessage("Request cannot be cancelled.");
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        adb.show();
    }
}
