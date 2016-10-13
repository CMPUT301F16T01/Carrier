package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 *
 */
public class OfflineTests {

    @After
    public void reset() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     * US 08.01.01 As an driver, I want to see requests that I already accepted while offline.
     */
    @Test
    public void OfflineSeeRequestsTest() {
        Rider rider = new Rider("Kieter");
        Driver driver = new Driver("Baenett");
        Request request = new Request(rider, new Location(), new Location());

        // Adding a request while online
        RequestController rc = new RequestController();
        rc.addRequest(request);
        // A driver offers a ride for that request
        rc.addDriver(request, driver);

        // Going offline
        SyncController sc = new SyncController();
        sc.setOnline(false);


        // The offered requests offline are the same as the ones made online
        assertEquals( "Driver could not get the requests he offered to fulfill while offline",
                request, rc.getOfferedRequests(driver).get(0));

    }

    /**
     * US 08.02.01 As a rider, I want to see requests that I have made while offline.

     */
    @Test
    public void test_08_02_01() {

    }

    /**
     * US 08.03.01 As a rider, I want to make requests that will be sent once I get connectivity again.
     */
    @Test
    public void test_08_03_01() {

    }

    /**
     * US 08.04.01 As a driver, I want to accept requests that will be sent once I get connectivity again.
     */
    @Test
    public void test_08_04_01() {

    }

}
