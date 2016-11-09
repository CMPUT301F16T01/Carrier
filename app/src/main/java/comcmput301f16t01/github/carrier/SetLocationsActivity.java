package comcmput301f16t01.github.carrier;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.File;

public class SetLocationsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MapEventsReceiver {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int DEFAULT_REQ_CODE = 1;
    private GoogleApiClient googleApiClient = null;
    public final Activity activity = SetLocationsActivity.this;
    Location lastLocation = null;
    Location locationPoint = null;
    double latitude = 0;
    double longitude = 0;
    String point = "";
    Bundle lastBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_locations);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        point = bundle.getString("point");
        setTitle("Choose " + point + " point");

        Button button = (Button) findViewById(R.id.button_confirmLocation);
        button.setText("Confirm " + point + " point");

        if(point.equals("end")) {
            lastBundle.putString("point", "start");
            if(intent.hasExtra("startLocation")) {
                lastBundle.putString("startLocation", bundle.getString("startLocation"));
            }
            if(intent.hasExtra("endLocation")) {
                locationPoint = new Gson().fromJson(bundle.getString("endLocation"), Location.class);
            }
        } else if(point.equals("start")) {
            if(intent.hasExtra("startLocation")) {
                locationPoint = new Gson().fromJson(bundle.getString("startLocation"), Location.class);
            }
            if(intent.hasExtra("endLocation")) {
                lastBundle.putString("endLocation",bundle.getString("endLocation"));
            }
        }

        checkPermissions();

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) activity)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) activity)
                    .addApi(LocationServices.API)
                    .build();
        }

        OpenStreetMapTileProviderConstants.setCachePath(new File(Environment.getExternalStorageDirectory().getPath() + "osmdroid2").getAbsolutePath());

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        if(locationPoint != null) {
            GeoPoint geoPoint = new GeoPoint(locationPoint.getLatitude(), locationPoint.getLongitude());
            map = (MapView) findViewById(R.id.map);
            setLocationMarker(map, geoPoint);
            IMapController mapController = map.getController();
            mapController.setZoom(18);
            mapController.setCenter(geoPoint);
        }
    }

    // the following two functions come from: https://developer.android.com/training/location/retrieve-current.html
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

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
        final Marker startMarker = new Marker(map);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        startMarker.setDraggable(true);
        startMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                locationPoint.setLatitude(marker.getPosition().getLatitude());
                locationPoint.setLongitude(marker.getPosition().getLongitude());
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        map.getOverlays().add(startMarker);
        map.invalidate();
    }

    // this function comes from: https://developer.android.com/training/permissions/requesting.html

    /**
     * Result of the user granting or denying permissions. If they grant the permissions
     * we don't need to do anything. If they do not grant the permissions, we should tell
     * them that they are required for the map to be displayed and the app to function.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle("Permissions Denied");
                    adb.setMessage("You cannot view the map to select locations without " +
                            "allowing the app to access your device's storage. You can change " +
                            "this permission from the app info.");
                    adb.setCancelable(true);
                    adb.setPositiveButton("OK", null);
                    adb.show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }

    /**
     * Asks user to grant required permissions for the maps to work.
     */
    private void checkPermissions() {
        // if statement from https://developer.android.com/training/permissions/requesting.html
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        latitude = lastLocation.getLatitude();
        longitude = lastLocation.getLongitude();

        // This code is from: https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        if(locationPoint == null){
            MapView map = (MapView) findViewById(R.id.map);
            locationPoint = new Location("");
            locationPoint.setLatitude(geoPoint.getLatitude());
            locationPoint.setLongitude(geoPoint.getLongitude());
            setLocationMarker(map, geoPoint);
        }
        return false;
    }

    // from: http://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
    // author: ρяσѕρєя K
    // retrieved on: November 7th, 2016
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == DEFAULT_REQ_CODE) {
            if(resultCode == RESULT_OK){
                // when done choosing end, go back to request screen and pass bundle of locations
                Intent backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                lastBundle.putString("startLocation", intent.getStringExtra("startLocation"));
                lastBundle.putString("endLocation", intent.getStringExtra("endLocation"));
                backIntent.putExtras(lastBundle);
                activity.finish();
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
                startActivityForResult(intent, DEFAULT_REQ_CODE);
            } else if (point.equals("end")) {
                // if choosing end point, go back to last activity, passing bundle of locations
                Intent backIntent = new Intent();
                lastBundle.putString("endLocation", new Gson().toJson(locationPoint));
                backIntent.putExtras(lastBundle);
                setResult(RESULT_OK, backIntent);
                activity.finish();
            }
        } else {
            Toast.makeText(activity, "You must first choose a location by long-pressing on the map", Toast.LENGTH_SHORT).show();
        }
    }
}
