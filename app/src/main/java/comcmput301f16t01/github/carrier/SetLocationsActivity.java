package comcmput301f16t01.github.carrier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.ViewLocationsActivity;
import comcmput301f16t01.github.carrier.Searching.SearchResultsActivity;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * <p>SetLocationsActivity allow the user to put a marker on the map to specify the start and end location
 * for their ride or search.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#setlocationsactivity">SetLocationsActivity</a></p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android">How to pass data from 2nd activity to 1st activity when pressed back? - android</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/1202025/%CF%81%D1%8F%CF%83%D1%95%CF%81%D1%94%D1%8F-k">ρяσѕρєя K</a></p>
 * <p>Posted on: January 12th, 2013</p>
 * <p>Retrieved on: November 7th, 2016</p>
 * </br>
 * <p>Based on: <a href="https://developer.android.com/training/location/retrieve-current.html">Getting the last known location</a></p>
 * <p>Based on: <a href="https://developer.android.com/training/permissions/requesting.html">Requesting Permissions at Run Time</a></p>
 * <p>Author: Android Dev Docs</p>
 * <p>Retrieved on: November 9th, 2016</p>
 * </br>
 * <p>Based on: <a href="https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0">Tutorial_0</a></p>
 * <p>Author: MKergall</p>
 * <p>Retrieved on: November 10th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/37986082/android-googlemaps-mylocation-permission">Maps Permissions</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/4558709/antonio">antonio</a></p>
 * <p>Posted on: June 23rd, 2016</p>
 * <p>Retrieved on: November 9th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/26217983/osmdroid-bonus-pack-reverse-geolocation">osmdroid bonus pack reverse geolocation</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/4095382/cristina">cristina</a></p>
 * <p>Posted on: October 6th, 2014</p>
 * <p>Retrieved on: November 11th, 2016</p>
 */
public class SetLocationsActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, MapEventsReceiver {
    // result code for when we return to an instance of this activity
    private static final int PASS_ACTIVITY_BACK = 1;
    private static final int PASS_ACTIVITY_FORWARD = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient googleApiClient = null;
    public final Activity activity = SetLocationsActivity.this;
    Location lastLocation = null;
    CarrierLocation locationPoint = null;
    Marker marker = null;
    double latitude = 0;
    double longitude = 0;
    String point = "";
    String type = "";
    Bundle lastBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(intent.hasExtra("point")){
            point = bundle.getString("point");
        }
        if(intent.hasExtra("type")) {
            type = bundle.getString("type");
        }
        setTitle("Choose " + point + " point");

        Button button = (Button) findViewById(R.id.button_confirmLocation);
        button.setText("Confirm " + point + " point");

        lastBundle.putString("type",type);
        if(point.equals("end")) {
            lastBundle.putString("point", "start");
            if(intent.hasExtra("startLocation")) {
                lastBundle.putString("startLocation", bundle.getString("startLocation"));
            }
            if(intent.hasExtra("endLocation")) {
                locationPoint = new Gson().fromJson(bundle.getString("endLocation"), CarrierLocation.class);
            }
        } else if(point.equals("start")) {
            if(intent.hasExtra("startLocation")) {
                locationPoint = new Gson().fromJson(bundle.getString("startLocation"), CarrierLocation.class);
            }
            if(intent.hasExtra("endLocation")) {
                lastBundle.putString("endLocation",bundle.getString("endLocation"));
            }
        }

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new Builder(activity)
                    .addConnectionCallbacks((ConnectionCallbacks) activity)
                    .addOnConnectionFailedListener((OnConnectionFailedListener) activity)
                    .addApi(LocationServices.API)
                    .build();
        }

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(18);

        if(locationPoint != null) {
            GeoPoint geoPoint = new GeoPoint(locationPoint.getLatitude(), locationPoint.getLongitude());
            map = (MapView) findViewById(R.id.map);
            if(marker == null) {
                marker = new Marker(map);
            }
            setLocationMarker(map, geoPoint);
            mapController.setCenter(geoPoint);
        }
    }

    // see code attribution
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    // see code attribution
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Set the location marker on the map for the user-selected location
     *
     * @param map the map to set the location on
     * @param geoPoint the geographical point on the map the marker will be placed
     */
    private void setLocationMarker(MapView map, GeoPoint geoPoint) {
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_location_on, null));

        marker.setDraggable(true);
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                locationPoint.setLatitude(marker.getPosition().getLatitude());
                locationPoint.setLongitude(marker.getPosition().getLongitude());
                locationPoint.setAddress(RequestController.getAddress(activity, locationPoint.getLatitude(), locationPoint.getLongitude()));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        map.getOverlays().add(marker);
        map.invalidate();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    /** Handle the user's response to accepting/denying permissions. */
    // see code attribution
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO potentially offline behaviour?
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO potentially offline behaviour?
    }

    /**
     * Implemented to catch clicks/taps on the screen to place a location marker. This function is
     * called by an overlay.
     *
     * @param geoPoint the geopoint generated by clicking
     * @return Boolean, false should this event need be handled by anyone else
     */
    // see code attribution
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        MapView map = (MapView) findViewById(R.id.map);
        if(marker == null) {
            marker = new Marker(map);
        }
        if(locationPoint == null) {
            locationPoint = new CarrierLocation();
        }
        locationPoint.setLatitude(geoPoint.getLatitude());
        locationPoint.setLongitude(geoPoint.getLongitude());
        locationPoint.setAddress(RequestController.getAddress(activity, locationPoint.getLatitude(), locationPoint.getLongitude()));
        setLocationMarker(map, geoPoint);
        return true;
    }

    /**
     * Implemented to catch long clicks/taps on the screen to place a location marker. This function is
     * called by an overlay.
     *
     * @param geoPoint the geopoint generated by long pressing
     * @return Boolean, false should this event need be handled by anyone else.
     */
    // see code attribution
    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        MapView map = (MapView) findViewById(R.id.map);
        if(marker == null) {
            marker = new Marker(map);
        }
        if(locationPoint == null) {
            locationPoint = new CarrierLocation();
        }
        locationPoint.setLatitude(geoPoint.getLatitude());
        locationPoint.setLongitude(geoPoint.getLongitude());
        locationPoint.setAddress(RequestController.getAddress(activity, locationPoint.getLatitude(), locationPoint.getLongitude()));
        setLocationMarker(map, geoPoint);
        return true;
    }

    /**
     * This is called when we startActivityForResult from here and get a result back when that activity finishes.
     * This allows us to do any "clean up actions" when we get back here
     */
    // see code attribution
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PASS_ACTIVITY_BACK) {
            if(resultCode == RESULT_OK) {
                // when done choosing end, go back to request screen and pass bundle of locations
                Intent backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                lastBundle.putString("startLocation", intent.getStringExtra("startLocation"));
                lastBundle.putString("endLocation", intent.getStringExtra("endLocation"));
                backIntent.putExtras(lastBundle);
                activity.finish();
            }
        } else if(requestCode == PASS_ACTIVITY_FORWARD) {
            if(resultCode == RESULT_OK) {
                Intent forwardIntent = new Intent(activity, ViewLocationsActivity.class);
                setResult(RESULT_OK,forwardIntent);
                lastBundle.putString("startLocation", intent.getStringExtra("startLocation"));
                lastBundle.putString("endLocation", intent.getStringExtra("endLocation"));
                forwardIntent.putExtras(lastBundle);
                activity.finish();
                Toast.makeText(activity, "Route set", Toast.LENGTH_LONG).show();
                startActivity(forwardIntent);
            }
        }
    }

    /**
     * If the user has selected a location, set the location point and continue to the next screen
     * in the workflow.
     *
     * @param view The calling view, the confirmLocation button
     */
    public void confirmLocation(View view) {
        if(locationPoint != null) {
            // if choosing start point, continue to end point screen
            if (point.equals("start")) {
                lastBundle.putString("point", "end");
                lastBundle.putString("startLocation", new Gson().toJson(locationPoint));
                Intent intent = new Intent(activity, SetLocationsActivity.class);
                intent.putExtras(lastBundle);
                if(type.equals("new")) {
                    startActivityForResult(intent, PASS_ACTIVITY_FORWARD);
                } else {
                    startActivityForResult(intent, PASS_ACTIVITY_BACK);
                }
            } else if (point.equals("end")) {
                // if choosing end point, go back to last activity, passing bundle of locations
                Intent backIntent = new Intent();
                lastBundle.putString("endLocation", new Gson().toJson(locationPoint));
                backIntent.putExtras(lastBundle);
                setResult(RESULT_OK, backIntent);
                activity.finish();
            } else if (point.equals("search")) {
                // if choosing search point, go to search results activity, passing bundle with search location
                RequestController.searchByLocation(locationPoint);
                Intent intent = new Intent(activity, SearchResultsActivity.class);
                // Move the filter from this intent to the SearchResultActivity intent. 
                intent.putExtra( "filterBundle", getIntent().getBundleExtra("filterBundle") );
                activity.finish();
                startActivity(intent);
            }
        } else {
            Toast.makeText(activity, "You must first choose a location", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up the map to be pinpointed on the user's location.
     */
    // see code attribution
    public void getCurrentLocation() {
        if(ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (lastLocation == null) {
                lastLocation = new Location("");
                lastLocation.setLatitude(0);
                lastLocation.setLongitude(0);
            }
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            MapView map = (MapView) findViewById(R.id.map);
            GeoPoint startPoint = new GeoPoint(latitude,longitude);
            IMapController mapController = map.getController();
            mapController.setZoom(18);
            if(locationPoint == null) {
                mapController.setCenter(startPoint);
            }

            MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(activity, (MapEventsReceiver) activity);
            map.getOverlays().add(0, mapEventsOverlay);
        }
    }
}
