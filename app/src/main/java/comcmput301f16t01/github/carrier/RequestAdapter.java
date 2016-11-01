package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

/**
 * From hreherch's implementation of LonelyTwitter
 * Takes an array of Requests and molds it into a ListView of requests
 */
public class RequestAdapter extends ArrayAdapter<Request> {
    private ArrayList<Request> requestList;

    public RequestAdapter(Context context, int textViewResourceId, ArrayList<Request> requestArrayList) {
        super(context, textViewResourceId, requestArrayList);
        this.requestList = requestArrayList;
    }

    /**
     * Sets up each individual item in the ListView, by position in the ArrayList
     * @see ArrayAdapter
     */
    @NonNull
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.requestlist_item, null);
        }

        final Request request = requestList.get(position);

        if (request != null) {
            // Get all sub views of requestlist_item
            TextView startLocTextView = (TextView) v.findViewById(R.id.textView_toLocation);
            TextView endLocTextView = (TextView) v.findViewById(R.id.textView_fromLocation);
            TextView priceTextView = (TextView) v.findViewById(R.id.textView_price);
            ImageView statusImageView = (ImageView) v.findViewById(R.id.imageView_requestStatus);

            // Set the start location in the item's view
            if (startLocTextView != null) {
                String startLoc = "To: " + request.getStart().toString();
                startLocTextView.setText(startLoc);
            }

            // Set the end location in the item's view
            if (endLocTextView != null) {
                String endLoc = "From: " + request.getEnd().toString();
                endLocTextView.setText(endLoc);
            }

            // Set the price in the item's view
            if (priceTextView != null) {
                Currency localCurrency = Currency.getInstance( Locale.getDefault() );
                String price = localCurrency.getSymbol()
                        + Float.toString(request.getFare() / 100);
                priceTextView.setText(price);
            }

            // TODO showing the status properly...
            // Set up the status icon depending on the status of the request
            if (statusImageView != null) {
                switch( request.getStatus() ) {
                    case( Request.OPEN ):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.openStatus);
                        break;
                    case( Request.OFFERED ):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.offeredStatus);
                        break;
                    case( Request.CONFIRMED ):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.confirmedStatus);
                        break;
                    case( Request.COMPLETE):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.completeStatus);
                        break;
                    case( Request.PAID):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.paidStatus);
                        break;
                    case( Request.CANCELLED):
                        //statusImageView.setImageResource( *** enter image for this status? *** );
                        statusImageView.setBackgroundResource(R.color.cancelledStatus);
                        break;
                }
            }
        }
        return v;
    }
}
