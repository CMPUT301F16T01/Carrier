package comcmput301f16t01.github.carrier;

import android.location.Location;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTests {
    // University of Alberta, Edmonton
    static final double latitude1 = 53.5232;
    static final double longitude1 = 113.5263;

    // somewhere in London, Ontario
    static final double latitude2 = 42.9870;
    static final double longitude2 = 81.2432;

    @After
    public void clean() {
        RequestController rc = new RequestController();
        rc.clear();
    }

    /**
     * As a rider, I want to specify a start and end geo locations on a map for a request.
     * Related: US 10.01.01
     */
    @Test
    public void riderChooseRequestLocation() {
        User rider = new User("Mandy");
        // TODO better way to get current location (using location manager perhaps?)

        // gets current location
        Location startLocation = new Location("");
        Location endLocation = new Location("");

        // user moves pin for start location
        startLocation.setLatitude(latitude1);
        startLocation.setLongitude(longitude1);
        endLocation.setLatitude(latitude2);
        endLocation.setLongitude(longitude2);

        Request request = new Request(rider, startLocation, endLocation, "");

        assertEquals("Start location latitude should match", startLocation.getLatitude(), request.getStart().getLatitude());
        assertEquals("Start location longitude should match", startLocation.getLongitude(), request.getStart().getLongitude());

        assertEquals("End location latitude should match", endLocation.getLatitude(), request.getEnd().getLatitude());
        assertEquals("End location longitude should match", endLocation.getLongitude(), request.getEnd().getLongitude());
    }

    /**
    * As a driver, I want to view start and end geo locations on a map for a request.
    * Related: US 10.02.01 (added 2016-02-29)
    */
    @Test
    public void driverViewRequestLocation() {
        User rider = new User("Mike");
        Location startLocation = new Location("");
        Location endLocation = new Location("");
        Request request = new Request(rider, startLocation, endLocation, "");

        Location start = request.getStart();
        Location end = request.getEnd();

        // assert that the start locations match
        assertEquals("The start locations should match", startLocation, start);
        // assert that the end locations match
        assertEquals("The end locations should match", endLocation, end);

        // TODO do we need to make an application test for this (to double check on mapview?)
    }
}
