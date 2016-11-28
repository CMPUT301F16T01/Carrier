package comcmput301f16t01.github.carrier.Requests;

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

import comcmput301f16t01.github.carrier.FareCalculator;
import comcmput301f16t01.github.carrier.R;

/**
 * RequestAdapter adapts a request to show the user information easily, such as the status as an icon
 * the start and end addresses and the fare.
 *
 * @see Request
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
            TextView endLocTextView = (TextView) v.findViewById(R.id.textView_endLocation);
            TextView startLocTextView = (TextView) v.findViewById(R.id.textView_startLocation);
            TextView priceTextView = (TextView) v.findViewById(R.id.textView_price);
            ImageView statusImageView = (ImageView) v.findViewById(R.id.imageView_requestStatus);

            // Set the start location in the item's view
            if (startLocTextView != null) {
                String startLoc;
                if (request.getStart().getShortAddress() != null) {
                    if(!request.getStart().getShortAddress().equals("")) {
                        startLoc = "Start: " + request.getStart().getShortAddress();
                    } else {
                        startLoc = "Start: " + request.getStart().getLatLong();
                    }
                } else {
                    startLoc = "Start: " + request.getStart().getLatLong();
                }
                startLocTextView.setText(startLoc);
            }

            // Set the end location in the item's view
            if (endLocTextView != null) {
                String endLoc;
                if (request.getEnd().getShortAddress() != null) {
                    if(!request.getEnd().getShortAddress().equals("")) {
                        endLoc = "End: " + request.getEnd().getShortAddress();
                    } else {
                        endLoc = "End: " + request.getEnd().getLatLong();
                    }
                } else {
                    endLoc = "End: " + request.getEnd().getLatLong();
                }
                endLocTextView.setText(endLoc);
            }

            // Set the price in the item's view
            if (priceTextView != null) {
                Currency localCurrency = Currency.getInstance( Locale.getDefault() );
                String price = localCurrency.getSymbol() + FareCalculator.toString(request.getFare());
                priceTextView.setText(price);
            }

            // Set up the status icon depending on the status of the request
            if (statusImageView != null) {
                switch( request.getStatus() ) {
                    case OPEN:
                        statusImageView.setImageResource(R.drawable.open);
                        break;
                    case OFFERED:
                        statusImageView.setImageResource(R.drawable.offered);
                        break;
                    case CONFIRMED:
                        statusImageView.setImageResource(R.drawable.confirmed);
                        break;
                    case COMPLETE:
                        statusImageView.setImageResource(R.drawable.complete);
                        break;
                    case PAID:
                        statusImageView.setImageResource(R.drawable.paid);
                        break;
                    case CANCELLED:
                        statusImageView.setImageResource(R.drawable.cancel);
                        break;
                }
            }
        }
        return v;
    }
}
