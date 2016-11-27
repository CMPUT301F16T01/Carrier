package comcmput301f16t01.github.carrier.Requests;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import comcmput301f16t01.github.carrier.FareCalculator;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;
import comcmput301f16t01.github.carrier.Users.UsernameTextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static comcmput301f16t01.github.carrier.Requests.Request.Status.COMPLETE;
import static comcmput301f16t01.github.carrier.Requests.Request.Status.PAID;

/**
 * <p>This will help us show the request from the perspective of a driver. Will have
 * the position in the request controller bundled to determine what request to display.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#driverviewrequestactivity">DriverViewRequestActivity</a></p>
 * </br>
 * <p>Based on: <a href="https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0">Tutorial_0</a></p>
 * <p>Author: MKergall</p>
 * <p>Retrieved on: November 10th, 2016</p>
 * </br>
 * <p>Updated with: <a href="http://stackoverflow.com/questions/38539637/osmbonuspack-roadmanager-networkonmainthreadexception">OSMBonuspack RoadManager NetworkOnMainThreadException</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/4670837/yubaraj-poudel">yubaraj poudel</a></p>
 * <p>Posted on: August 6th, 2016</p>
 * <p>Retrieved on: November 10th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/20608590/osmdroid-zooming-to-show-the-whole-pathoverlay">OSMDroid: zooming to show the whole PathOverlay</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/6769091/thebugger">theBugger</a></p>
 * <p>Posted on: September 30th, 2016</p>
 * <p>Retrieved on: November 24th, 2016</p>
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
        setTitle("Request");

        //getting the request controller to get a list of requests
        loggedInUser = UserController.getLoggedInUser();

        // unpacking the bundle to get the position of request
        Bundle bundle = getIntent().getExtras();

        int pos = bundle.getInt( "position" );
        if (pos == -1) {
            // signal that the request was searched and has not been related to the user yet
            request = new Gson().fromJson( bundle.getString( "request" ), Request.class );
        } else {
            // else we can grab it from the request controller.
            request = RequestController.getOffersInstance().get(pos);

            // They have already made an offer, so we can turn off "make offer" button
            Button makeOfferButton = (Button) findViewById( R.id.button_makeOffer );
            makeOfferButton.setEnabled( false ); // Make the button un-clickable
            makeOfferButton.setAlpha((float) 0.5); // Set transparency to 50%
        }

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        startPoint = new GeoPoint(request.getStart());
        endPoint = new GeoPoint(request.getEnd());

        mapController = map.getController();
        mapController.setCenter(getCenter());
        zoomToBounds(getBoundingBox(startPoint, endPoint));

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
     * Called when the activity is resumed.
     */
    public void onResume() {
        super.onResume();
        setViews();
    }

    /**
     * This function finds a BoundingBox that fits both the start and end location points.
     *
     * @param start GeoPoint for start location
     * @param end GeoPoint for end location
     * @return BoundingBox that holds both location points
     */
    public BoundingBox getBoundingBox(GeoPoint start, GeoPoint end) {
        double north;
        double south;
        double east;
        double west;
        if(start.getLatitude() > end.getLatitude()) {
            north = start.getLatitude();
            south = end.getLatitude();
        } else {
            north = end.getLatitude();
            south = start.getLatitude();
        }
        if(start.getLongitude() > end.getLongitude()) {
            east = start.getLongitude();
            west = end.getLongitude();
        } else {
            east = end.getLongitude();
            west = start.getLongitude();
        }
        return new BoundingBox(north, east, south, west);
    }

    /**
     * This function allows the MapView to zoom to show the whole path between
     * the start and end points.
     *
     * @param box BoundingBox for start and end points
     */
    // see code attribution
    public void zoomToBounds(final BoundingBox box) {
        if (map.getHeight() > 0) {
            map.zoomToBoundingBox(box, false);
            map.zoomToBoundingBox(box, false);
        } else {
            ViewTreeObserver vto = map.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    map.zoomToBoundingBox(box, false);
                    map.zoomToBoundingBox(box, false);
                    ViewTreeObserver vto2 = map.getViewTreeObserver();
                    vto2.removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    /**
     * Given the request the user is viewing, set the map according to the start and end locations of the trip.
     */
    private void setMarkers() {
        Marker startMarker = new Marker(map);
        startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_start_marker, null));
        Marker endMarker = new Marker(map);
        endMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_end_marker, null));

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


    /**
     * Asynchronous task to get the route between the two points
     */
    // see code attribution
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
     * This AsyncTask updates the road on the map and maps a route between two points. This is
     * so that it does not lock up the UI thread or try to make a network connection on it.
     */
    // see code attribution
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {

        @Override
        protected Road[] doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
            RoadManager roadManager = new OSRMRoadManager(activity);
            return roadManager.getRoads(waypoints);
        }

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

    /**
     * Center the map on the Start point of the trip.
     *
     * @param view the TextView that shows the Start address
     */
    public void centerStart(View view) {
        mapController.setCenter(startPoint);
    }

    /**
     * Center the map on the End point of the trip.
     *
     * @param view the TextView that shows the End address
     */
    public void centerEnd(View view) {
        mapController.setCenter(endPoint);
    }

    /**
     * Get the center point of the route to center the screen on.
     *
     * @return GeoPoint that specifies center between start and end points
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
            retLoc.setLongitude(startLong + ((endLong - startLong)/2));
        }

        return new GeoPoint(retLoc);
    }

    /**
     * Given the request the user is viewing, set the views in the layout.
     */
    public void setViews() {
        // Set up the fare
        Currency localCurrency = Currency.getInstance( Locale.getDefault() );
        String price = localCurrency.getSymbol() + FareCalculator.toString(request.getFare());
        TextView fareTextView = (TextView) findViewById(R.id.textView_$fareAmount);
        fareTextView.setText(price);

        // Set up the UsernameTextView of the rider
        UsernameTextView riderUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_rider);
        riderUsernameTextView.setText(request.getRider().getUsername());
        riderUsernameTextView.setUser(request.getRider());

        // Set up the UsernameTextView of the driver
        UsernameTextView driverUsernameTextView = (UsernameTextView) findViewById(R.id.UsernameTextView_driver);
        if (request.getConfirmedDriver() != null) {
            driverUsernameTextView.setText(request.getConfirmedDriver().getUsername());
            driverUsernameTextView.setUser(request.getConfirmedDriver());
        }

        TextView startAddressTextView = (TextView) findViewById(R.id.textView_start);
        startAddressTextView.setText(request.getStart().toString());

        TextView endAddressTextView = (TextView) findViewById(R.id.textView_end);
        endAddressTextView.setText(request.getEnd().toString());

        TextView descriptionTextView = (TextView) findViewById(R.id.textView_description);
        descriptionTextView.setText(request.getDescription());

        /**
         * This switch statement changes the status image
         */
        ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
        if (statusImageView != null) {
            switch (request.getStatus()) {
                case OPEN:
                    statusImageView.setImageResource(R.drawable.open);
                    break;
                case OFFERED:
                    statusImageView.setImageResource(R.drawable.offered);
                    break;
                case CONFIRMED:
                    statusImageView.setImageResource(R.drawable.confirmed);
                    break;
                case COMPLETE:
                    statusImageView.setImageResource(R.drawable.complete);
                    break;
                case PAID:
                    statusImageView.setImageResource(R.drawable.paid);
                    break;
                case CANCELLED:
                    statusImageView.setImageResource(R.drawable.cancel);
                    break;

            }
        }
        // If status is complete we change the make an offer button to display that they have received payment
        if (request.getStatus() == COMPLETE && request.getConfirmedDriver().getUsername().equals(loggedInUser.getUsername())) {
            Button payment_button = (Button) findViewById(R.id.button_makeOffer);
            payment_button.setText(R.string.payment_received);
            payment_button.setEnabled( true ); // Make the button clickable
            payment_button.setAlpha((float) 1);
            payment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    receivedPayment();
                }
            });
        }
        if (request.getStatus() == PAID ) {
            Button payment_button = (Button) findViewById(R.id.button_makeOffer);
            payment_button.setText(R.string.payment_received);
            payment_button.setEnabled( false ); // Make the button clickable
            payment_button.setAlpha((float) 0.5);
        }
    }

    /**
     * Will update the current request in elastic search to be paid for.
     */
    private void receivedPayment() {
        Toast.makeText(this, "Request is now complete.", Toast.LENGTH_SHORT).show();
        RequestController.payForRequest(request);
        ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
        statusImageView.setImageResource(R.drawable.paid);
        Button payment_button = (Button) findViewById(R.id.button_makeOffer);
        payment_button.setText(R.string.payment_received);
        payment_button.setEnabled( false ); // Make the button clickable
        payment_button.setAlpha((float) 0.5);
        RequestController.getOffersInstance().notifyListeners();
        RequestController.getRiderInstance().notifyListeners();
    }

    /**
     * Attempts to make an offer for a request as a driver
     * @param view The make offer view that was clicked
     */
    public void makeOffer(View view) {
        // Prepare an alert dialogue in case of an error.
        AlertDialog.Builder adb = new AlertDialog.Builder(DriverViewRequestActivity.this);
        adb.setTitle("Error: ");
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // We use the controller to attempt to add the driver
        try {
            Toast.makeText(this, "Made an offer.", Toast.LENGTH_SHORT).show();
            RequestController.addDriver( request, loggedInUser );
            Button button = (Button) findViewById( R.id.button_makeOffer);
            button.setEnabled(false); // Make the button un-clickable after offering.
            button.setAlpha((float) 0.5); // The button becomes 50% transparent
            ImageView statusImageView = (ImageView) findViewById(R.id.imageView_requestStatus);
            statusImageView.setImageResource(R.drawable.offered);
            RequestController.getOffersInstance().notifyListeners();
        } catch (Exception e) {
            // If there is an issue, set the message to the exception message and show it
            adb.setMessage(e.getMessage());
            adb.show();
        }
    }
}