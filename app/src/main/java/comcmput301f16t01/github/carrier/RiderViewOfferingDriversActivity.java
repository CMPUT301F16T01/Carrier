package comcmput301f16t01.github.carrier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Requests.Request;

public class RiderViewOfferingDriversActivity extends AppCompatActivity {
    private Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_view_offering_drivers);
        Bundle bundle = getIntent().getExtras();
        //int position = bundle.getInt("position");
        //request = rc.getResult().get(position);

        request = new Gson().fromJson( bundle.getString("request"), Request.class );
        ArrayList<User> offeringDrivers = request.getOfferedDrivers();
        OfferingDriversArrayAdapter offeringDriversArrayAdapter = new OfferingDriversArrayAdapter(this, R.layout.offeringdriverslist_item, offeringDrivers);
        ListView offeringDriversListView = (ListView) findViewById(R.id.offeringDriversListView);
        offeringDriversListView.setAdapter(offeringDriversArrayAdapter);
        //offeringDriversArrayAdapter;
    }
}
