package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTests {
    // U of A
    static final double latitude1 = 113;
    static final double longitude1 = 53.5;

    // somewhere in Columbus, OH
    static final double latitude2 = 40.1;
    static final double longitude2 = -82.9;

    @After
    public void clean() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     * As a rider, I want to specify a start and end geo locations on a map for a request.
     * Related: US 10.01.01
     */
    @Test
    public void riderChooseRequestLocation() {
        Rider rider = new Rider("Mandy");
        // TODO better way to get current location (using location manager perhaps?)
        Location currentLocation = new Location();

        // gets current location
        Location startLocation = new Location();

        assertEquals("Current location should match startLocation", currentLocation, startLocation);

        // user moves pin for start location
        startLocation.setLocation(latitude1, longitude1);
        Location endLocation = new Location(latitude2, longitude2);

        Request request = new Request(rider, startLocation, endLocation);

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
        Rider rider = new Rider("Mike");
        Location startLocation = new Location();
        Location endLocation = new Location();
        Request request = new Request(rider, startLocation, endLocation);

        Location start = request.getStart();
        Location end = request.getEnd();

        // assert that the start locations match
        assertEquals("The start locations should match", startLocation, start);
        // assert that the end locations match
        assertEquals("The end locations should match", endLocation, end);

        // TODO do we need to make an application test for this (to double check on mapview?)
    }
}
