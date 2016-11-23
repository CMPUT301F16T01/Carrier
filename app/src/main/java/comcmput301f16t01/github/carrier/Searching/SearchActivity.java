package comcmput301f16t01.github.carrier.Searching;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.annotation.NotThreadSafe;

import java.util.IllegalFormatException;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.SetLocationsActivity;

/**
 * The SearchActivity is the driver's hub for finding requests to accept. Here the driver can find
 * their way to multiple requests they can offer to that match their needs (be it price range, location
 * a description, or otherwise).
 */
public class SearchActivity extends AppCompatActivity {
    final Activity activity = SearchActivity.this;

    /** Helps determine what extra filtering is needed if the user has specified */
    private Boolean filterByPrice = false;
    private Boolean filterByPricePerKM = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Hide the price view until the user enables price filtering.
        LinearLayout priceLayout = (LinearLayout) findViewById( R.id.linearLayout_MinMaxPrice );
        priceLayout.setVisibility( View.GONE );

        LinearLayout pricePerKMLayout = (LinearLayout) findViewById( R.id.linearLayout_PricePerKM );
        pricePerKMLayout.setVisibility( View.GONE );
    }

    /**
     * This will create the search by keyword dialog.
     * The keyword query entered by the user will be used in a
     * search on available requests.
     * @param view the searchByKeyword button
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
                RequestController rc = new RequestController();
                rc.searchByKeyword(query);
                Intent intent = new Intent(activity, SearchResultsActivity.class);
                try {
                    bundleFilters(intent);  // attempt to bundle the price filters
                } catch (Exception e) {
                    // If there is a failure we can toast the error message to the user.
                    Toast.makeText( getBaseContext(), e.getMessage(), Toast.LENGTH_LONG ).show();
                    return; // escape the routine and do not start the new activity.
                }
                startActivity(intent);
            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.show();
    }

    /**
     * This will open a maps activity to allow the user to
     * select a location to create a query for available requests.
     * The initial marker will default to the current location.
     * @param view the search by location button
     */
    public void searchByLocation(View view) {
        Toast.makeText(this, "Search by Location", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity, SetLocationsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("point", "search");
        bundle.putString("type", "search");
        try {
            bundleFilters(intent);  // attempt to bundle the price filters
        } catch (Exception e) {
            // If there is a failure we can toast the error message to the user.
            Toast.makeText( getBaseContext(), e.getMessage(), Toast.LENGTH_LONG ).show();
            return; // escape the routine and do not start the new activity.
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * We create a bundle to put in the intent for SearchResultActivity so the activity can make proper
     * calls and get the requests filtered by the user's need.
     * @param intent the intent for the SearchResultActivity
     * @see SearchResultsActivity
     */
    private void bundleFilters(Intent intent) {
        Bundle bundle = new Bundle();

        // Place information about how we are filtering by price
        bundle.putBoolean( "filterByPrice", filterByPrice );
        if ( filterByPrice ) {
            EditText minEditText = (EditText) findViewById( R.id.editText_minPrice);
            EditText maxEditText = (EditText) findViewById( R.id.editText_maxPrice);
            Double minPrice;
            Double maxPrice;

            try {
                // Try to convert the input to a float, if we fail return an error message
                minPrice = Double.valueOf(minEditText.getText().toString());
                bundle.putDouble( "minPrice", minPrice );
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("You must put a valid value for the minimum price you are searching for." );
            }

            // Check if the value is blank (user chose to leave this value out)
            if (maxEditText.getText().toString().equals("")) {
                bundle.putFloat( "maxPrice", -1 ); // We use negative one to say "we will not be checking by maxPrice"
            } else {
                // Else we check if we can parse the value and put it in our bundle.
                try {
                    maxPrice = Double.valueOf(maxEditText.getText().toString());
                    bundle.putDouble( "maxPrice", maxPrice );
                    if ( minPrice > maxPrice ) {
                        throw new IllegalArgumentException( "You may not set the minimum price higher than the maximum price.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException( "You must leave the maximum price per kilometer field blank or put a valid value." );
                }
            }
        } // if( filterByPrice )

        // We check if they have specified to filter requests by price per KM, and add it to the bundle.
        bundle.putBoolean( "filterByPricePerKM", filterByPricePerKM );
        if ( filterByPricePerKM ) {
            EditText minEditText = (EditText) findViewById( R.id.editText_minPricePerKM);
            EditText maxEditText = (EditText) findViewById( R.id.editText_maxPricePerKM);
            Double minPrice;
            Double maxPrice;

            // Try to convert the input to a float, if we fail return an error message
            try {
                minPrice = Double.valueOf(minEditText.getText().toString());
                bundle.putDouble( "minPricePerKM", minPrice );
            } catch (NumberFormatException e) {
                String message = "You must put a valid value for the minimum price per " +
                        "kilometer you are searching for.";
                throw new IllegalArgumentException( message );
            }

            // Check if the value is blank (user chose to leave this value out)
            if (maxEditText.getText().toString().equals("")) {
                bundle.putFloat( "maxPricePerKM", -1 ); // We use negative one to say "we will not be checking by maxPrice"
            } else {
                // Else we check if we can parse the value and put it in our bundle.
                try {
                    maxPrice = Double.valueOf(maxEditText.getText().toString());
                    bundle.putDouble( "maxPricePerKM", maxPrice );
                    if ( minPrice > maxPrice ) {
                        throw new IllegalArgumentException( "You may not set the minimum price per kilometer higher than the maximum price.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException( "You must leave the maximum price per kilometer field blank or put a valid value." );
                }
            }
        } // if( filterByPricePerKM )

        intent.putExtra( "filterBundle", bundle );
    } // bundleFilters

    /**
     * If the checkbox is checked, we display to the user the option of using the min max
     * text views for price filtering.
     * @param view the calling view (the checked checkbox)
     */
    public void setPriceFilterVisibility(View view) {
        LinearLayout priceLayout = (LinearLayout) findViewById( R.id.linearLayout_MinMaxPrice );
        if ( ((CheckBox) view).isChecked() ) {
            priceLayout.setVisibility( View.VISIBLE ); // We make it visible if the checkbox is checked
            filterByPrice = true;
        } else {
            priceLayout.setVisibility( View.GONE ); // Else we make it invisible
            filterByPrice = false;
        }
    }

    /**
     * If the checkbox is checked, we display to the user the option of using the min max
     * edit texts to provide a range of price per KM they want to choose.
     * @param view the calling view (the checked checkbox)
     */
    public void setPricePerKMFilterVisibility(View view) {
        LinearLayout pricePerKMLayout = (LinearLayout) findViewById( R.id.linearLayout_PricePerKM );
        if ( ((CheckBox) view).isChecked() ) {
            pricePerKMLayout.setVisibility( View.VISIBLE ); // We make it visible if the checkbox is checked
            filterByPricePerKM = true;
        } else {
            pricePerKMLayout.setVisibility( View.GONE ); // Else we make it invisible
            filterByPricePerKM = false;
        }
    }
}
