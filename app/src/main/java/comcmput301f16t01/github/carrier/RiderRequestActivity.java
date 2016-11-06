package comcmput301f16t01.github.carrier;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This will help us show the request from the perspective of a rider
 */
public class RiderRequestActivity extends AppCompatActivity {

    //this is just used to make it work for now
    // TODO remove this
    private Integer position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_view_request);

        //getting the request controller to get a list of requests
        RequestController rc = new RequestController();
        User loggedInUser = UserController.getLoggedInUser();
        ArrayList<Request> requestList = rc.getRequests(loggedInUser);

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        final Request request = requestList.get(position);

        TextView descriptionTextView = (TextView) findViewById(R.id.TextView_description);
        descriptionTextView.setText(request.getDescription());

        // The set up the UsernameTextView of the rider
        UsernameTextView riderUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_rider);
        riderUsernameTextView.setText("Rider: " + request.getRider().getUsername());
        riderUsernameTextView.setUser(request.getRider());

        // The set up the UsernameTextView of the driver
        UsernameTextView driverUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_driver);
        driverUsernameTextView.setText("Driver: " + request.getChosenDriver().getUsername());
        driverUsernameTextView.setUser(request.getChosenDriver());



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
//
//    public void onClickUsername(Request request) {
//        Toast.makeText(this, request.getRider().getUsername(), Toast.LENGTH_SHORT).show();
//
//    }



}
