package comcmput301f16t01.github.carrier.Searching;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.DriverViewRequestActivity;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.LoginActivity;
import comcmput301f16t01.github.carrier.Users.LoginMemory;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * SearchResultsActivity handles displaying and linking to new requests for a driver to choose from.
 */
public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        setTitle( "Search Results" );

        ListView requestListView = (ListView) findViewById( R.id.listView_searchResults );

        unpackBundle( this.getIntent().getBundleExtra("filterBundle"));

        // If the user is offline, show dialog to tell them they are seeing a cache
        if (!ConnectionChecker.isThereInternet()) {
            showOfflineDialog();
        }

        // It shouldn't matter what query we used, the singleton will be up to date with the query when we get here
        final RequestList requestList = RequestController.getResult();

        ArrayAdapter<Request> requestArrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, requestList );
        requestListView.setAdapter( requestArrayAdapter );

        // Create an onClickListener for the items to take them to a "make offer" page.
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * We don't have the requests stored locally in our singleton, so we need to let
                 * the viewRequest activity know with a special code (position = -1) that it will use gson to
                 * deserialize a request.
                 */
                Intent intent = new Intent(SearchResultsActivity.this, DriverViewRequestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt( "position", -1 );
                bundle.putString("request", new Gson().toJson(requestList.get(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void showOfflineDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("No network connection");
        adb.setMessage("You are seeing a cache of your previously searched requests. " +
                "You can still make offers on these requests, which will be sent once " +
                "you regain a network connection.");
        adb.setPositiveButton("Okay", null);
        adb.show();
    }

    /**
     * Unpacks the bundle that contains the filters for the request, then asks the request controller
     * to prune by those values if it has been requested.
     * @param filterBundle the bundle containing all the values to filter by.
     */
    private void unpackBundle(Bundle filterBundle) {
        // both will be false by default
        boolean filterByPrice = false;
        boolean filterByPricePerKM = false;
        if(getIntent().hasExtra("filterByPrice")) {
            filterByPrice = filterBundle.getBoolean("filterByPrice");
        }
        if(getIntent().hasExtra("filterByPricePerKM")) {
            filterByPricePerKM = filterBundle.getBoolean("filterByPricePerKM");
        }

        // Check if we are filtering by price
        if (filterByPrice) {
            Double minPrice = filterBundle.getDouble("minPrice");
            Double maxPrice = filterBundle.getDouble("maxPrice");
            if (maxPrice == -1) { maxPrice = null; }
            RequestController.pruneByPrice( minPrice, maxPrice );
        }
        // Check if we are filtering by price per KM
        if (filterByPricePerKM) {
            Double minPricePerKM = filterBundle.getDouble("minPricePerKM");
            Double maxPricePerKM = filterBundle.getDouble("maxPricePerKM");
            if (maxPricePerKM == -1) { maxPricePerKM = null; }
            RequestController.pruneByPricePerKM( minPricePerKM, maxPricePerKM );
        }
    }
}
