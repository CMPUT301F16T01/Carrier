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
        Request request = requestList.get(position);

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

        final TextView driverUsernameTextView = (TextView) findViewById(R.id.text_view_driver);
        /**
         * This listener causes the TextView that displays driver username to have a dark
         * background colour when touched. Like on facebook.
         */
        driverUsernameTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String username = driverUsernameTextView.getText().toString()
                        .replace("Driver: ", "");
                if( event.getAction() == MotionEvent.ACTION_DOWN) {
                    driverUsernameTextView.setBackgroundColor(ContextCompat
                            .getColor(RiderRequestActivity.this, R.color.usernameClick));
                    // When the name is released, go to user profile
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    driverUsernameTextView.setBackgroundColor(Color.TRANSPARENT);

                }
                return true;
            }
        });

        final TextView riderUsernameTextView = (TextView) findViewById(R.id.text_view_rider);
        /**
         * This listener causes the TextView that displays rider username to have a dark
         * background colour when touched. Like on facebook.
         */
        riderUsernameTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String username = riderUsernameTextView.getText().toString()
                        .replace("Rider: ", "");
                if( event.getAction() == MotionEvent.ACTION_DOWN) {
                    riderUsernameTextView.setBackgroundColor(ContextCompat
                            .getColor(RiderRequestActivity.this, R.color.usernameClick));
                    // When the name is released, go to user profile
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    riderUsernameTextView.setBackgroundColor(Color.TRANSPARENT);
                    onClickUsername(username);
                }
                return true;
            }
        });
    }


    public void onClickUsername(String username) {
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
    }



}
