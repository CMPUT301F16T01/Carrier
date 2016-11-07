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
        Bundle bundle = getIntent().getExtras();
        point = bundle.getString("point");
        setTitle("Choose " + point + " point");

        Button button = (Button) findViewById(R.id.button_confirmLocation);
        button.setText("Confirm " + point + " point");

        if(point.equals("end")) {
            String startLocation = bundle.getString("startLocation");
            lastBundle.putString("startLocation",startLocation);
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
        mapController.setCenter(startPoint);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(activity, (MapEventsReceiver) activity);
        map.getOverlays().add(0, mapEventsOverlay);

        // Set a start marker at our current location
        /*Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        map.invalidate();
        */
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
            final Marker startMarker = new Marker(map);
            startMarker.setPosition(geoPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            locationPoint = new Location("");
            locationPoint.setLatitude(geoPoint.getLatitude());
            locationPoint.setLongitude(geoPoint.getLongitude());

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

        return false;
    }


    public void confirmLocation(View view) {
        if(locationPoint != null) {
            if (point.equals("start")) {
                Intent intent = new Intent(activity, SetLocationsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("point", "end");
                bundle.putString("startLocation", new Gson().toJson(locationPoint));
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (point.equals("end")) {
                Intent intent = new Intent(activity, MakeRequestActivity.class);
                lastBundle.putString("endLocation", new Gson().toJson(locationPoint));
                intent.putExtras(lastBundle);
                startActivity(intent);
            }
        } else {
            Toast.makeText(activity, "You must first choose a location by long-pressing on the map", Toast.LENGTH_SHORT).show();
        }
    }
}
