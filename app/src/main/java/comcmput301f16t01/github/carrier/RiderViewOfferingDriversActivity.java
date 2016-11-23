package comcmput301f16t01.github.carrier;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Users.User;

public class RiderViewOfferingDriversActivity extends AppCompatActivity {
    private Request request;
    private RequestController rc = new RequestController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_view_offering_drivers);
        Bundle bundle = getIntent().getExtras();

        int position = bundle.getInt("position");
        request = rc.getRiderInstance().get(position);
        final ArrayList<User> offeringDrivers = request.getOfferedDrivers();
        OfferingDriversArrayAdapter offeringDriversArrayAdapter = new OfferingDriversArrayAdapter(this, R.layout.offeringdriverslist_item, offeringDrivers);
        offeringDriversArrayAdapter.setRequest(request);
        ListView offeringDriversListView = (ListView) findViewById(R.id.offeringDriversListView);
        offeringDriversListView.setAdapter(offeringDriversArrayAdapter);


    }
}
