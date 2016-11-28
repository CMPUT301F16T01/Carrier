package comcmput301f16t01.github.carrier.Searching;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.RequestController;

/**
 * <p>This activity allows the user to choose between possible addresses based on their address search.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#searchaddresschoiceactivity">SearchAddressChoiceActivity</a></p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/4451374/use-enter-key-on-softkeyboard-instead-of-clicking-button">Use “ENTER” key on softkeyboard instead of clicking button</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/310001/nailuj">Nailuj</a></p>
 * <p>Posted on: December 15th, 2010</p>
 * <p>Retrieved on: November 24th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard">Close/hide the Android Soft Keyboard</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/822/reto-meier">Reto Meier</a></p>
 * <p>Posted on: September 11th, 2015</p>
 * <p>Retrieved on: November 24th, 2016</p>
 */
public class SearchAddressChoiceActivity extends AppCompatActivity {
    final Activity activity = SearchAddressChoiceActivity.this;
    ArrayAdapter<CarrierLocation> locationArrayAdapter;
    ListView locationListView;
    EditText searchEditText;

    @Override
    @SuppressWarnings("unchecked")
    // see code attribution
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address_choice);
        setTitle("Choose search address");

        locationListView = (ListView) findViewById( R.id.listView_addressChoiceList );

        searchEditText = (EditText) findViewById(R.id.editText_addressSearch);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchAddress(view);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void searchAddress(View view) {
        final List<CarrierLocation> locations = getLocation(searchEditText.getText().toString());
        if(locations != null) {
            locationArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, locations);
            locationListView.setAdapter(locationArrayAdapter);
            locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, SearchResultsActivity.class);
                    RequestController.searchByLocation(locations.get(position));
                    intent.putExtra( "filterBundle", getIntent().getBundleExtra("filterBundle"));
                    startActivity(intent);
                }
            });
            hideKeyboard(searchEditText);
        } else {
            Toast.makeText(activity, "An error occurred when retrieving addresses", Toast.LENGTH_SHORT).show();
        }
    }

    // see code attribution
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Gets possible geo-locations from an address string submitted by the user.
     * @param addressQuery address string inputted by the user
     * @return List<CarrierLocation> list of possible geo-locations that match address
     */
    private List<CarrierLocation> getLocation(final String addressQuery) {
        List<Address> addressResult = new ArrayList<>();
        List<CarrierLocation> locations = new ArrayList<>();

        ElasticRequestController.SearchByAddressTask sbat = new ElasticRequestController.SearchByAddressTask();
        sbat.execute(addressQuery);
        try {
            addressResult = sbat.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Address address : addressResult) {
            CarrierLocation location = new CarrierLocation();
            location.setLatitude(address.getLatitude());
            location.setLongitude(address.getLongitude());
            location.setAddress(RequestController.getAddress(activity, address.getLatitude(), address.getLongitude()));
            locations.add(location);
        }
        return locations;
    }
}
