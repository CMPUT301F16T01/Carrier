package comcmput301f16t01.github.carrier.Searching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import comcmput301f16t01.github.carrier.DriverViewRequestActivity;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ListView requestListView = (ListView) findViewById( R.id.listView_searchResults );

        RequestController rc = new RequestController();

        // It shouldn't matter what query we used, the singleton will be up to date with the query when we get here
        final RequestList requestList = rc.getResult();
        ArrayAdapter<Request> requestArrayAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, requestList );
        requestListView.setAdapter( requestArrayAdapter );
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultsActivity.this, DriverViewRequestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("request", new Gson().toJson(requestList.get(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
