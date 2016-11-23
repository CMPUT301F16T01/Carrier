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


/**
 * See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#searchactivity">SearchActivity</a>
 * Author: Android Dev Docs
 * Retrieved on: October 28th, 2016
 */
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
     * @param view Search by keyword Button
     */
    public void searchByKeyword(View view) {
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
     * @param view Search by location Button
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
     * @param view Search by address Button
     */
    public void searchByAddress(View view) {
        Intent intent = new Intent(activity, SearchAddressChoiceActivity.class);
        startActivity(intent);
    }
}
