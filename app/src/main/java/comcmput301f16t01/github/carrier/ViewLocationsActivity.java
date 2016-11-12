package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

// Based on: https://goo.gl/4TKn2y
// Retrieved on: November 10th, 2016
//
// Updated with: https://goo.gl/h2CKyn
// Author: yubaraj poudel
// Posted: August 6th, 2016
// Retrieved on: November 10th, 2016

public class ViewLocationsActivity extends AppCompatActivity {
    final Activity activity = ViewLocationsActivity.this;
    private Location start = null;
    private Location end = null;
    GeoPoint startPoint = null;
    GeoPoint endPoint = null;
    Road[] roadList = null;
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        setTitle("View Route");

        Intent intent = getIntent();
        if(intent.hasExtra("startLocation")) {
            start = new Gson().fromJson(intent.getStringExtra("startLocation"), Location.class);
        }
        if(intent.hasExtra("endLocation")) {
            end = new Gson().fromJson(intent.getStringExtra("endLocation"), Location.class);
        }

        // set the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        startPoint = new GeoPoint(start);
        endPoint = new GeoPoint(end);

        IMapController mapController = map.getController();
        // TODO figure out a way to zoom dynamically to include both points?
        mapController.setZoom(16);
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> overlayItems = new ArrayList<>();
        overlayItems.add(new OverlayItem("Starting Point", "This is the starting point", startPoint));
        overlayItems.add(new OverlayItem("Destination", "This is the destination point", endPoint));

        setMarkers();
        getRoadAsync();
    }

    // TODO try to get the addresses to put in the info windows
    private void setMarkers() {
        // set the map
        Marker startMarker = new Marker(map);
        Marker endMarker = new Marker(map);

        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start");
        startMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setTitle("End");
        endMarker.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));

        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);
        map.invalidate();
    }

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

    public void centerStart(View view) {
        IMapController mapController = map.getController();
        mapController.setCenter(startPoint);
    }

    public void centerEnd(View view) {
        IMapController mapController = map.getController();
        mapController.setCenter(endPoint);
    }

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
                Toast.makeText(activity, road.getLengthDurationText(activity, -1), Toast.LENGTH_SHORT).show();
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
}
