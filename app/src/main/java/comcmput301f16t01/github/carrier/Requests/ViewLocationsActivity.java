package comcmput301f16t01.github.carrier.Requests;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.util.List;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.R;

/**
 * <p>The ViewLocationsActivity allows the user to view on a map two start and end locations and
 * the route between them. Giving the user a better idea of what route they will be taking.</p>
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
public class ViewLocationsActivity extends AppCompatActivity {
    final Activity activity = ViewLocationsActivity.this;
    private CarrierLocation start = null;
    private CarrierLocation end = null;
    String type = null;
    GeoPoint startPoint = null;
    GeoPoint endPoint = null;
    Road[] roadList = null;
    MapView map;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        setTitle("View Route");

        Intent intent = getIntent();
        if(intent.hasExtra("startLocation")) {
            start = new Gson().fromJson(intent.getStringExtra("startLocation"), CarrierLocation.class);
        }
        if(intent.hasExtra("endLocation")) {
            end = new Gson().fromJson(intent.getStringExtra("endLocation"), CarrierLocation.class);
        }
        if(intent.hasExtra("type")) {
            type = intent.getStringExtra("type");
        }

        // set the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        try {
            startPoint = new GeoPoint(start);
            endPoint = new GeoPoint(end);
        } catch (NullPointerException e) {
            startPoint = new GeoPoint(new CarrierLocation());
            endPoint = new GeoPoint(new CarrierLocation());
        }

        IMapController mapController = map.getController();
        mapController.setCenter(getCenter());
        zoomToBounds(getBoundingBox(startPoint, endPoint));

        ArrayList<OverlayItem> overlayItems = new ArrayList<>();
        overlayItems.add(new OverlayItem("Starting Point", "This is the starting point", startPoint));
        overlayItems.add(new OverlayItem("Destination", "This is the destination point", endPoint));

        setMarkers();
        getRoadAsync();
        map.invalidate();
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
     * On back press, if this was a new request, create the make request activity
     * Otherwise, the user navigated to this screen through the make request activity so
     * we can just finish the current activity.
     */
    public void onBackPressed() {
        if(type.equals("new")) {
            Intent intent = new Intent(activity, MakeRequestActivity.class);
            bundle.putString("startLocation", new Gson().toJson(start));
            bundle.putString("endLocation", new Gson().toJson(end));
            intent.putExtras(bundle);
            activity.finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            bundle.putString("startLocation", new Gson().toJson(start));
            bundle.putString("endLocation", new Gson().toJson(end));
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            activity.finish();
        }
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
            retLoc.setLongitude(startLong + ((endLong - startLong)/2));
        }

        return new GeoPoint(retLoc);
    }

    /**
     * Set the start and end markers based on the exact positions the user gave for them
     */
    private void setMarkers() {
        // set the map
        Marker startMarker = new Marker(map);
        startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_start_marker, null));
        Marker endMarker = new Marker(map);
        endMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_end_marker, null));

        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("START:\n" + start.getAddress());
        startMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setTitle("END:\n" + end.getAddress());
        endMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));

        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);
        map.invalidate();
    }

    public void continueToRequest(View view) {
        if(type.equals("new")) {
            Intent intent = new Intent(activity, MakeRequestActivity.class);
            bundle.putString("startLocation", new Gson().toJson(start));
            bundle.putString("endLocation", new Gson().toJson(end));
            intent.putExtras(bundle);
            activity.finish();
            startActivity(intent);
        } else {
            // TODO do we still need this??
            // we did to fix the updating start/end location problem but it may not be necessary
            Intent intent = new Intent();
            bundle.putString("startLocation", new Gson().toJson(start));
            bundle.putString("endLocation", new Gson().toJson(end));
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            activity.finish();
        }
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
     * Class to update the road on the map
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
            bundle.putDouble("distance", bestRoad.mLength);
            bundle.putDouble("duration", bestRoad.mDuration);

            Polyline roadPolyline = RoadManager.buildRoadOverlay(bestRoad);
            roadPolyline.setTitle(getString(R.string.app_name) + " - " + routeDesc);
            roadPolyline.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
            mapOverlays.add(0, roadPolyline);
            map.invalidate();
        }
    }
}
