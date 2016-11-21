package comcmput301f16t01.github.carrier.Searching;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.DriverViewRequestActivity;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;

public class SearchAddressChoiceActivity extends AppCompatActivity {
    final Activity activity = SearchAddressChoiceActivity.this;
    // TODO http://stackoverflow.com/questions/32444863/google-gson-linkedtreemap-class-cast-to-myclass
    final Type listType = new TypeToken<List<CarrierLocation>>() {}.getType();

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address_choice);
        setTitle("Choose search address");

        ListView locationListView = (ListView) findViewById( R.id.listView_addressChoiceList );

        Intent intent = getIntent();
        final List<CarrierLocation> locations = new Gson().fromJson(intent.getStringExtra("addressList"), listType);

        ArrayAdapter<CarrierLocation> locationArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, locations);
        locationListView.setAdapter(locationArrayAdapter);
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, SearchResultsActivity.class);
                Bundle bundle = new Bundle();
                RequestController rc = new RequestController();
                rc.searchByLocation(locations.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
