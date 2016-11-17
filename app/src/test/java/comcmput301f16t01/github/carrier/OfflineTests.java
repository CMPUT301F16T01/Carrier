package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.User.User;

import static org.junit.Assert.*;

/**
 *
 */
public class OfflineTests {

    @After
    public void reset() {
        RequestController rc = new RequestController();
        rc.clear();
    }

    /**
     * US 08.01.01 As an driver, I want to see requests that I already accepted while offline.
     */
    @Test
    public void OfflineSeeDriverOffers() {
        // Setting up
        User rider = new User("Kieter");
        User driver = new User("Baenett");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());

        // Adding a request while online
        RequestController rc = new RequestController();
        rc.addRequest(request);
        // A driver offers a ride for that request
        rc.addDriver(request, driver);

        // Going offline
        ElasticController sc = new ElasticController();
        sc.setOnline(false);


        // Tests that the offered requests offline are the same as the ones made online
        assertEquals( "Driver could not get the requests he offered to fulfill while offline",
                request, rc.getOfferedRequests(driver).get(0));

    }

    /**
     * US 08.02.01 As a rider, I want to see requests that I have made while offline.
     */
    @Test
    public void OfflineSeeRiderRequests() {
        // Setting up
        User rider = new User("Kieter");
        User driver = new User("Baenett");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        // Going offline
        ElasticController sc = new ElasticController();
        sc.setOnline(false);

        // Tests that the requests the rider made online persist offline (they are the same)
        assertEquals("Rider could not get the requests they made", request, rc.getRequests(rider).get(0));
    }

    /**
     * US 08.03.01 As a rider, I want to make requests that will be sent once I get connectivity again.
     */
    @Test
    public void MakeRequestsOnceConnected() {
        // Going offline
        ElasticController sc = new ElasticController();
        sc.setOnline(false);

        // Setting up
        User rider = new User("Mandy");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();

        // adding a request offline will send it to the queue in the SyncController
        rc.addRequest(request);
        // Tests that the request is in the SyncController queue waiting to be posted
        //   this assumes that this is the only request in the queue
        assertEquals("Request not in queue", request, sc.getRequestQueue().peek());
        // Tests that the request has not been posted yet
        assertFalse("Rider request was posted", rc.getRequests(rider).contains(request));

        // Going back online, the request should be made as soon as we get connectivity
        sc.setOnline(true);

        // Tests that the request was posted because we got connectivity
        assertEquals("Rider request was not posted", request, rc.getRequests(rider).get(0));
    }

    /**
     * US 08.04.01 As a driver, I want to accept requests that will be sent once I get connectivity again.
     */
    @Test
    public void AcceptRequestsOnceConnected() {
        // Setting up
        User rider = new User("Mandy");
        User driver = new User("Abigail");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        // Going offline
        ElasticController sc = new ElasticController();
        sc.setOnline(false);

        // driver accepts request, request sent to queue in SyncController
        rc.addDriver(request, driver);
        // Tests that the request is in the SyncController queue waiting to be posted
        //   this assumes that this is the only request in the queue
        assertEquals("Request not in queue", request, sc.getRequestQueue().peek());
        // Tests that the request has not been posted yet
        assertFalse("Driver request offer was posted", rc.getRequests(rider).contains(request));

        // Going back online, the request should be accepted as soon as we get connectivity
        sc.setOnline(true);

        // Tests that the request offer was posted because we got connectivity
        assertEquals("Driver request offer was not posted", request, rc.getOfferedRequests(driver).get(0));
    }

}
