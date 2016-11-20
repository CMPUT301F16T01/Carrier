package comcmput301f16t01.github.carrier.Searching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.DriverViewRequestActivity;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;

/**
 * SearchResultsActivity handles displaying and linking to new requests for a driver to choose from.
 */
public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ListView requestListView = (ListView) findViewById( R.id.listView_searchResults );

        unpackBundle( this.getIntent().getBundleExtra("filterBundle"));

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

    /**
     * Unpacks the bundle that contains the filters for the request, then asks the request controller
     * to prune by those values if it has been requested.
     * @param filterBundle the bundle containing all the values to filter by.
     */
    private void unpackBundle(Bundle filterBundle) {
        Boolean filterByPrice = filterBundle.getBoolean("filterByPrice");
        Boolean filterByPricePerKM = filterBundle.getBoolean("filterByPricePerKM");

        // Check if we are filtering by price
        if (filterByPrice) {
            Float minPrice = filterBundle.getFloat("minPrice");
            Float maxPrice = filterBundle.getFloat("maxPrice");
            if (maxPrice == -1) { maxPrice = null; }
            RequestController.pruneByPrice( minPrice, maxPrice );
        }
        // Check if we are filtering by price per KM
        if (filterByPricePerKM) {
            Float minPricePerKM = filterBundle.getFloat("minPricePerKM");
            Float maxPricePerKM = filterBundle.getFloat("maxPricePerKM");
            if (maxPricePerKM == -1) { maxPricePerKM = null; }
            RequestController.pruneByPricePerKM( minPricePerKM, maxPricePerKM );
        }
    }
}
