package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by michael on 17/11/16.
 */

public class OfferingDriversArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> offeringDriversList;

    public OfferingDriversArrayAdapter(Context context, int textViewResourceId, ArrayList<User> userArrayList) {
        super(context, textViewResourceId, userArrayList);
        this.offeringDriversList = userArrayList;
    }

    /**
     * Sets up individual items in the ListView, by position in the ArrayList
     *
     * @See ArrayAdapter
     */
    @NonNull
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.offeringdriverslist_item, null);
        }
        // Get the offering driver from the ArrayList
        final User offeringDriver = offeringDriversList.get(position);

        if (offeringDriver != null) {
            // Get the username to display it
            UsernameTextView offeringDriverUsernameTextView = (UsernameTextView) v.findViewById(R.id.UsernameTextView_offeringDriver);
            offeringDriverUsernameTextView.setText(offeringDriver.getUsername());
            offeringDriverUsernameTextView.setUser(offeringDriver);

        }
        return v;
    }
}

