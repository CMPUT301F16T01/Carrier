package comcmput301f16t01.github.carrier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

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
        Request request = requestList.get(position);

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
}
