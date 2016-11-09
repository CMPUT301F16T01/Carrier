package comcmput301f16t01.github.carrier;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * This will help us show the request from the perspective of a driver
 * Will have the position in the requestcontroller bundled to determine what request to display.
 */
public class DriverViewRequestActivity extends AppCompatActivity {
    private Integer position = 0;
    private Request request;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_request);
        //getting the request controller to get a list of requests
        RequestController rc = new RequestController();
        loggedInUser = UserController.getLoggedInUser();
        ArrayList<Request> requestList = RequestController.getInstance();

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        if (position != -1) {
            request = requestList.get(position);
        }
        if ((request != null) && (loggedInUser != null)) {
            getViewIds();
            setViewIds();
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

    private TextView fareAmountTextView;
    private TextView startLocationTextView;
    private TextView descriptionTextView;
    private TextView riderTextView;
    private TextView driverTextView;

    /**
     * Will set the various views to display the correct information for the
     * request
     */
    private void setViewIds() {
        fareAmountTextView.setText(new MakeRequestActivity().formatFare(request.getFare()));
        startLocationTextView.setText(request.getStart().toString());
        descriptionTextView.setText((request.getDescription()));
        riderTextView.setText(request.getRider().getUsername());
        if (request.getConfirmedDriver() != null) {
            driverTextView.setText(request.getConfirmedDriver().getUsername());
        }
        else {
            driverTextView.setText("");
        }
    }

    /**
     * Will get the ViewIds from the R.java file.
     */
    private void getViewIds() {
        fareAmountTextView = (TextView) findViewById(R.id.textView_$fareAmount);
        startLocationTextView = (TextView) findViewById(R.id.textView_start);
        descriptionTextView = (TextView) findViewById(R.id.textView_description);
        riderTextView = (TextView) findViewById(R.id.textView_rider);
        driverTextView = (TextView) findViewById(R.id.textView_driver);
    }

    public void MakeOffer(View v) {
        RequestController rc = new RequestController();
        // Can not make an offer on a request that has a confirmed driver.
        // Can not make an offer on a request that you hae already made an offer on.
        // Can not make an offer on a cancelled request.
        if (request.getConfirmedDriver() != null || request.getOfferedDrivers().contains(loggedInUser) ||
                request.getStatus() == Request.CANCELLED) {
            AlertDialog.Builder adb = new AlertDialog.Builder(DriverViewRequestActivity.this);
            adb.setTitle("Error: ");
            adb.setMessage("Unable to make an offer on the request.");
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            adb.show();
        }
        else {
            rc.addDriver(request, loggedInUser);
            Toast.makeText(this, "Made an offer.", Toast.LENGTH_SHORT).show();
        }
    }
}