package comcmput301f16t01.github.carrier.Requests;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.FareCalculator;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.User;
import comcmput301f16t01.github.carrier.UserController;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.UsernameTextView;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * This will help us show the request from the perspective of a driver
<<<<<<< HEAD:app/src/main/java/comcmput301f16t01/github/carrier/Requests/DriverViewRequestActivity.java
 * Will have the position in the request controller bundled to determine what request to display.
=======
 * Will have the position in the requestController bundled to determine what request to display.
>>>>>>> refs/remotes/origin/master:app/src/main/java/comcmput301f16t01/github/carrier/DriverViewRequestActivity.java
 */
public class DriverViewRequestActivity extends AppCompatActivity {
    Activity activity = DriverViewRequestActivity.this;
    GeoPoint startPoint = null;
    GeoPoint endPoint = null;
    Road[] roadList = null;
    MapView map;
    IMapController mapController;
    private Request request;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_request);
        //getting the request controller to get a list of requests
        loggedInUser = UserController.getLoggedInUser();

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();
        request = new Gson().fromJson(bundle.getString("request"), Request.class);

        setTitle("Request");

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        startPoint = new GeoPoint(request.getStart());
        endPoint = new GeoPoint(request.getEnd());

        mapController = map.getController();
        // TODO figure out a way to zoom dynamically to include both points?
        mapController.setZoom(12);
        GeoPoint centerPoint = getCenter();
        mapController.setCenter(centerPoint);

        ArrayList<OverlayItem> overlayItems = new ArrayList<>();
        overlayItems.add(new OverlayItem("Starting Point", "This is the starting point", startPoint));
        overlayItems.add(new OverlayItem("Destination", "This is the destination point", endPoint));

        if ((request != null) && (loggedInUser != null)) {
            setViews();
            setMarkers();
            getRoadAsync();
        }
    }

    /**
     * Given the request passed in by the user, set the map according to the start and end locations
     */
    private void setMarkers() {
        Marker startMarker = new Marker(map);
        Marker endMarker = new Marker(map);

        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("START:\n" + request.getStart().getAddress());
        startMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setTitle("END:\n" + request.getEnd().getAddress());
        endMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));

        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);
        map.invalidate();
    }

    // Based on: https://goo.gl/4TKn2y
    // Retrieved on: November 10th, 2016

    // Updated with: https://goo.gl/h2CKyn
    // Author: yubaraj poudel
    // Posted: August 6th, 2016
    // Retrieved on: November 10th, 2016

    /**
     * Asynchronous task to get the route between the two points
     */
    public void getRoadAsync() {
        roadList = null;

        GeoPoint roadStartPoint = startPoint;
        GeoPoint roadEndPoint = endPoint;
        if (startPoint != null) {
            roadStartPoint = startPoint;
        }
        if (endPoint != null) {
            roadEndPoint = endPoint;
        }
        ArrayList<GeoPoint> waypoints = new ArrayList<>(2);
        waypoints.add(roadStartPoint);
        waypoints.add(roadEndPoint);
        new UpdateRoadTask().execute(waypoints);
    }


    /**
     * Class to update the road on the map
     */
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        @Override
        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new OSRMRoadManager(activity);
            return roadManager.getRoads(waypoints);
        }

        // TODO try to deal with the path too large to render problem
        @Override
        protected void onPostExecute(Road[] roads) {
            double minLength = 0;
            Road bestRoad = null;
            roadList = roads;
            if (roads == null)
                return;
            if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE) {
                Toast.makeText(activity, "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
            } else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) { //functional issues
                Toast.makeText(activity, "No possible route here", Toast.LENGTH_SHORT).show();
            }
            List<Overlay> mapOverlays = map.getOverlays();
            for (Road road : roads) {
                if(road.mLength < minLength || minLength == 0) {
                    minLength = road.mLength;
                    bestRoad = road;
                }
            }

            String routeDesc = bestRoad.getLengthDurationText(activity, -1);
            Polyline roadPolyline = RoadManager.buildRoadOverlay(bestRoad);
            roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            mapOverlays.add(0, roadPolyline);
            map.invalidate();
        }
    }

    public void centerStart(View view) {
        mapController.setCenter(startPoint);
    }

    public void centerEnd(View view) {
        mapController.setCenter(endPoint);
    }

    /**
     * Get the center point of the route to center the screen on
     * @return GeoPoint
     */
    public GeoPoint getCenter() {
        double startLat = startPoint.getLatitude();
        double startLong = startPoint.getLongitude();
        double endLat = endPoint.getLatitude();
        double endLong = endPoint.getLongitude();

        Location retLoc = new Location("");

        if(startLat > endLat) {
            retLoc.setLatitude(endLat + ((startLat - endLat)/2));
        } else {
            retLoc.setLatitude(startLat + ((endLat - startLat)/2));
        }

        if(startLong > endLong) {
            retLoc.setLongitude(endLong + ((startLong - endLong)/2));
        } else {
            retLoc.setLatitude(startLong + ((endLong - startLong)/2));
        }

        return new GeoPoint(retLoc);
    }

    /**
     * Given the request passed in by the user, set the views in the layout.
     */
    public void setViews() {
        UserController uc = new UserController();

        // Set up the fare
        FareCalculator fc = new FareCalculator();
        TextView fareTextView = (TextView) findViewById(R.id.textView_$fareAmount);
        fareTextView.setText("$" + fc.toString(request.getFare()));

        // Set up the UsernameTextView of the rider
        UsernameTextView riderUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_rider);
        riderUsernameTextView.setText(request.getRider().getUsername());
        riderUsernameTextView.setUser(request.getRider());

        // Set up the UsernameTextView of the driver
        UsernameTextView driverUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_driver);
        if (request.getChosenDriver() != null) {
            driverUsernameTextView.setText(request.getChosenDriver().getUsername());
            driverUsernameTextView.setUser(request.getChosenDriver());
        }

        TextView startAddressTextView = (TextView) findViewById(R.id.textView_start);
        String startAddress = request.getStart().getAddress();
        if (startAddress != null) {
            startAddressTextView.setText(startAddress);
        } else {
            String startPoint = "(" + String.valueOf(request.getStart().getLatitude()) + ", " +
                    String.valueOf(request.getStart().getLongitude()) + ")";
            startAddressTextView.setText(startPoint);
        }

        TextView endAddressTextView = (TextView) findViewById(R.id.textView_end);
        String endAddress = request.getEnd().getAddress();
        if (endAddress != null) {
            endAddressTextView.setText(request.getEnd().getAddress());
        } else {
            String endPoint = "(" + String.valueOf(request.getEnd().getLatitude()) + ", " +
                    String.valueOf(request.getEnd().getLongitude()) + ")";
            endAddressTextView.setText(endPoint);
        }

        TextView descriptionTextView = (TextView) findViewById(R.id.textView_description);
        descriptionTextView.setText(request.getDescription());

        /**
         * This switch statement changes the status image
         */
        ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
        if (statusImageView != null) {
            switch (request.getStatus()) {
                case (Request.OPEN):
                    statusImageView.setImageResource(R.drawable.open);
                    break;
                case (Request.OFFERED):
                    statusImageView.setImageResource(R.drawable.offered);
                    break;
                case (Request.CONFIRMED):
                    statusImageView.setImageResource(R.drawable.confirmed);
                    break;
                case (Request.COMPLETE):
                    statusImageView.setImageResource(R.drawable.complete);
                    break;
                case (Request.PAID):
                    statusImageView.setImageResource(R.drawable.paid);
                    break;
                case (Request.CANCELLED):
                    statusImageView.setImageResource(R.drawable.cancel);
                    break;

            }
        }
    }

    public void makeOffer(View view) {
        RequestController rc = new RequestController();
        // Can not make an offer on a request that has a confirmed driver.
        // Can not make an offer on a request that you hae already made an offer on.
        // Can not make an offer on a cancelled request.
        AlertDialog.Builder adb = new AlertDialog.Builder(DriverViewRequestActivity.this);
        adb.setTitle("Error: ");
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        if (request.getConfirmedDriver() != null) {
            adb.setMessage("Unable to make an offer on the request. There is already a confirmed driver.");
            adb.show();
        } else if (request.getOfferedDrivers().contains(loggedInUser)) {
            adb.setMessage("Unable to make an offer on the request. You have already made an offer.");
            adb.show();
        } else if (request.getStatus() == Request.CANCELLED) {
            adb.setMessage("Unable to make an offer on the request. The request has been cancelled.");
            adb.show();
        } else {
            rc.addDriver(request, loggedInUser);
            Toast.makeText(this, "Made an offer.", Toast.LENGTH_SHORT).show();
        }
    }
}