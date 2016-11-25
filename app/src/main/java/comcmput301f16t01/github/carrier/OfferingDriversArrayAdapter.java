package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UsernameTextView;

/**
 * <p>Takes an array of offering drivers and puts them into an array list.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#offeringdriversarrayadapter">OfferingDriversArrayAdapter</a></p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/9891360/getting-activity-from-context-in-android">Getting activity from context in android</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/1108032/boris-strandjev">Boris Strandjev</a></p>
 * <p>Posted on: March 27th, 2012</p>
 * <p>Retrieved on: November 24th, 2016</p>
 */

public class OfferingDriversArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> offeringDriversList;
    private Activity activity = null;
    /**
     * Request is needed to provide a way to confirm the driver.
     */
    private Request request = null;
    // see code attribution
    public OfferingDriversArrayAdapter(Context context, int textViewResourceId, ArrayList<User> userArrayList) {
        super(context, textViewResourceId, userArrayList);
        this.offeringDriversList = userArrayList;
        this.activity = (Activity) context;
    }

    /**
     * Sets up individual items in the ListView given a list of offering drivers for the request, by their position in the ArrayList.
     * Handles setting up the various views inside of the list view including the usernameTextViews
     * and adds the confirm driver button.
     * @see ArrayAdapter
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
            UsernameTextView offeringDriverUsernameTextView = (UsernameTextView) v.findViewById(R.id.UsernameTextView_offeringDriver);
            offeringDriverUsernameTextView.setText(offeringDriver.getUsername());
            offeringDriverUsernameTextView.setUser(offeringDriver);
        }
        if (request != null) {
            Button confirmDriverButton = (Button) v.findViewById(R.id.confirmDriverButton);
            confirmDriverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(view.getContext());
                    adb.setMessage("Confirm " + offeringDriver.getUsername() +" as the driver?");
                    adb.setCancelable(true);
                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestController.confirmDriver(request, offeringDriver);
                            Toast.makeText(view.getContext(), "Confirmed Driver", Toast.LENGTH_SHORT).show();
                            RequestController.getRiderInstance().notifyListeners();
                            RequestController.getOffersInstance().notifyListeners();
                            activity.finish(); // Finish the activity.
                        }
                    });
                    adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    adb.show();
                }
            });
        }
        return v;
    }

    /**
     * Is used to set the request that this array adapter is for.
     *
     * @param request The request that will be used to confirm the driver.
     */
    public void setRequest(Request request) {
        this.request = request;
    }
}

