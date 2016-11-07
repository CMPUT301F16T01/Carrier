package comcmput301f16t01.github.carrier.Searching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.RequestController;
import comcmput301f16t01.github.carrier.RequestList;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ListView requestListView = (ListView) findViewById( R.id.listView_searchResults );

        RequestController rc = new RequestController();

        // It shouldn't matter what query we used, the singleton will be up to date with the query when we get here
        RequestList requestList = rc.getResult();
        ArrayAdapter<Request> requestArrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, requestList );
        requestListView.setAdapter( requestArrayAdapter );
    }
}
