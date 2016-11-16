package comcmput301f16t01.github.carrier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.List;

import static com.google.android.gms.common.api.GoogleApiClient.*;

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
        setContentView(R.layout.activity_set_locations);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        point = bundle.getString("point");
        type = bundle.getString("type");
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
        // Based on: https://goo.gl/Kpueci
        // Author: Android Dev Docs
        // Retrieved on: November 9th, 2016
        if (googleApiClient == null) {
            googleApiClient = new Builder(activity)
                    .addConnectionCallbacks((ConnectionCallbacks) activity)
                    .addOnConnectionFailedListener((OnConnectionFailedListener) activity)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Based on: https://goo.gl/4TKn2y
        // Author: MKergall
        // Retrieved on: November 9th, 2016
        // Center our map on our current location
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

    // Based on: https://goo.gl/Kpueci
    // Author: Android Dev Docs
    // Retrieved on: November 9th, 2016
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    // Based on: https://goo.gl/Kpueci
    // Author: Android Dev Docs
    // Retrieved on: November 9th, 2016
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Set the location marker on the map for the user-selected location
     *
     * @param map
     * @param geoPoint
     */
    private void setLocationMarker(MapView map, GeoPoint geoPoint) {
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setDraggable(true);
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                locationPoint.setLatitude(marker.getPosition().getLatitude());
                locationPoint.setLongitude(marker.getPosition().getLongitude());
                locationPoint.setAddress(getAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
                locationPoint.setShortAddress(getShortAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        // Based on: https://goo.gl/4TKn2y
        // Author: MKergall
        // Retrieved on: November 9th, 2016
        MapView map = (MapView) findViewById(R.id.map);
        if(marker == null) {
            marker = new Marker(map);
        }
        if(locationPoint == null) {
            locationPoint = new CarrierLocation();
        }
        locationPoint.setLatitude(geoPoint.getLatitude());
        locationPoint.setLongitude(geoPoint.getLongitude());
        locationPoint.setAddress(getAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
        locationPoint.setShortAddress(getShortAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
        setLocationMarker(map, geoPoint);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        // Based on: https://goo.gl/4TKn2y
        // Author: MKergall
        // Retrieved on: November 9th, 2016
        MapView map = (MapView) findViewById(R.id.map);
        if(marker == null) {
            marker = new Marker(map);
        }
        if(locationPoint == null) {
            locationPoint = new CarrierLocation();
        }
        locationPoint.setLatitude(geoPoint.getLatitude());
        locationPoint.setLongitude(geoPoint.getLongitude());
        locationPoint.setAddress(getAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
        locationPoint.setShortAddress(getShortAddress(locationPoint.getLatitude(), locationPoint.getLongitude()));
        setLocationMarker(map, geoPoint);
        return false;
    }

    // from: https://goo.gl/IxFxpG
    // author: ρяσѕρєя K
    // retrieved on: November 7th, 2016
    // This is called when we startActivityForResult from here and get a result back when that activity finishes.
    // This allows us to do any "clean up actions" when we get back here
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
     * @param view
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
            }
        } else {
            Toast.makeText(activity, "You must first choose a location", Toast.LENGTH_SHORT).show();
        }
    }

    // Based on: https://goo.gl/iMJdJX
    // Author: cristina
    // Retrieved on: November 11th, 2016
    /**
     * Get address string from a geo point
     * @param latitude
     * @param longitude
     * @return String
     */
    private String getAddress(double latitude, double longitude) {
        String pointAddress;
        try {
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for(int i = 0; i <= n; i++) {
                    if(i != 0) {
                        sb.append("\n");
                    }
                    sb.append(address.getAddressLine(i));
                }
                pointAddress = new String(sb);
            } else {
                pointAddress = null;
            }
        } catch (Exception e) {
            pointAddress = null;
        }
        return pointAddress;
    }

    /**
     * Get short address string from a geo point
     * @param latitude
     * @param longitude
     * @return String
     */
    private String getShortAddress(double latitude, double longitude) {
        String pointAddress;
        try {
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                sb.append(address.getAddressLine(0));
            }
            pointAddress = new String(sb);
        } catch (Exception e) {
            pointAddress = null;
        }
        return pointAddress;
    }

    // Inspired by: https://goo.gl/qh3Dzf
    // Author: antonio
    // Retrieved on: November 9th, 2016
    public void getCurrentLocation() {
        if(ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            // Based on: https://goo.gl/4TKn2y
            // Author: MKergall
            // Retrieved on: November 9th, 2016
            // Center our map on our current location
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
