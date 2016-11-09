package comcmput301f16t01.github.carrier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by michael on 08/11/16.
 */

public class MockSearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ListView resultsListView = (ListView) findViewById(R.id.listView_searchResults);
        fillSearchResuls(resultsListView);


    }
    private void fillSearchResuls(ListView listView) {
        User rider = new User("Rider12");
        // Create different types of requests
        Request openRequest = new Request(rider, new Location(), new Location());
        Request cancelledRequest = new Request(rider, new Location(), new Location());
        Request confirmedRequest = new Request(rider, new Location(), new Location());
        Request offeredRequest = new Request(rider, new Location(), new Location());
        cancelledRequest.setStatus(Request.CANCELLED);
        User confirmedDriver = new User("Confirmed Driver");
        confirmedRequest.addOfferingDriver(confirmedDriver);
        confirmedRequest.confirmDriver(confirmedDriver);

        offeredRequest.addOfferingDriver(confirmedDriver);
        // Populate arraylist with the requests.
        final ArrayList<Request> requests = new ArrayList<>();
        final ArrayList<Request> rl = RequestController.getInstance();
        rl.add(openRequest);
        rl.add(cancelledRequest);
        rl.add(confirmedRequest);
        rl.add(offeredRequest);
        requests.add(openRequest);
        requests.add(cancelledRequest);
        requests.add(confirmedRequest);
        requests.add(offeredRequest);

        RequestAdapter requestArrayAdapter = new RequestAdapter(this,
                R.layout.requestlist_item, requests);
        listView.setAdapter( requestArrayAdapter );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MockSearchResultsActivity.this, DriverViewRequestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", rl.indexOf(requests.get(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
