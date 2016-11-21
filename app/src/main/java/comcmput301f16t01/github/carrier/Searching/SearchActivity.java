package comcmput301f16t01.github.carrier.Searching;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.SetLocationsActivity;

public class SearchActivity extends AppCompatActivity {
    final Activity activity = SearchActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");
    }

    /**
     * This will create the search by keyword dialog.
     * The keyword query entered by the user will be used in a
     * search on available requests.
     * @param view
     */
    public void searchByKeyword(View view) {
        // Based on: https://goo.gl/6AAnXP
        // Author: Android Dev Docs
        // Retrieved on: October 28, 2016
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_keyword_search, null);
        adb.setTitle("Search by Keyword");
        adb.setView(dialogView);
        adb.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText searchEditText = (EditText) dialogView.findViewById(R.id.editText_keywordSearch);
                String query = searchEditText.getText().toString();
                // TODO consider any input handling on the keyword?
                RequestController rc = new RequestController();
                rc.searchByKeyword(query);
                Intent intent = new Intent(activity, SearchResultsActivity.class);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.show();
    }

    /**
     * This will open a maps activity to allow the user to
     * select a location to create a query for available requests.
     * The initial screen will center around to the current location.
     * @param view
     */
    public void searchByLocation(View view) {
        Intent intent = new Intent(activity, SetLocationsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("point", "search");
        bundle.putString("type", "search");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * This will open a dialog to search for an address. The selected address
     * will be shown on a map to confirm the location. This will create a
     * query for available requests.
     * @param view
     */
    // TODO decide how to allow the user to enter the address
    // they could just enter it into a text box but then parsing is difficult
    // could have multiple text boxes for different parts of the address
    // do we want to use a dialog (like keyword?)
    // do we want to show them on a map to confirm?
    public void searchByAddress(View view) {
        // Based on: https://goo.gl/6AAnXP
        // Author: Android Dev Docs
        // Retrieved on: October 28, 2016
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_address_search, null);
        adb.setTitle("Search by Address");
        adb.setView(dialogView);
        adb.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText searchEditText = (EditText) dialogView.findViewById(R.id.editText_addressSearch);
                List<CarrierLocation> query = getLocation(searchEditText.getText().toString());
                if(query != null) {
                    Intent intent = new Intent(activity, SearchAddressChoiceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("addressList", new Gson().toJson(query));
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(activity, "An error occurred when retrieving addresses", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.show();
    }

    /**
     * Gets possible geo-locations from an address string submitted by the user.
     * @param addressQuery address string inputted by the user
     * @return List<CarrierLocation> list of possible geo-locations that match address
     */
    private List<CarrierLocation> getLocation(final String addressQuery) {
        List<Address> addressResult = new ArrayList<>();
        List<CarrierLocation> locations = new ArrayList<>();

        ElasticRequestController.SearchByAddressTask sbat = new ElasticRequestController.SearchByAddressTask();
        sbat.execute(addressQuery);
        try {
            addressResult = sbat.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Address address : addressResult) {
            CarrierLocation location = new CarrierLocation();
            location.setLatitude(address.getLatitude());
            location.setLongitude(address.getLongitude());
            // Based on: https://goo.gl/iMJdJX
            // Author: cristina
            // Retrieved on: November 11th, 2016
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                if (i != 0) {
                    sb.append("\n");
                } else {
                    location.setShortAddress(address.getAddressLine(i));
                }
                sb.append(address.getAddressLine(i));
            }
            location.setAddress(sb.toString());
            locations.add(location);
        }
        return locations;
    }
}
