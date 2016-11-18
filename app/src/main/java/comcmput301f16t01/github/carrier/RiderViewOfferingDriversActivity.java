package comcmput301f16t01.github.carrier;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;

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
        final ArrayList<User> offeringDrivers = request.getOfferedDrivers();
        OfferingDriversArrayAdapter offeringDriversArrayAdapter = new OfferingDriversArrayAdapter(this, R.layout.offeringdriverslist_item, offeringDrivers);
        ListView offeringDriversListView = (ListView) findViewById(R.id.offeringDriversListView);
        offeringDriversListView.setAdapter(offeringDriversArrayAdapter);
        offeringDriversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(RiderViewOfferingDriversActivity.this);
                adb.setMessage("Confirm Driver?");
                adb.setCancelable(true);
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestController rc = new RequestController();
                        rc.confirmDriver(request, offeringDrivers.get(position));
                        Toast.makeText(RiderViewOfferingDriversActivity.this, "Confirmed Driver", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();

            }
        });
    }
}
