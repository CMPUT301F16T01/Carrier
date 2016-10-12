package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by michael on 11/10/16.
 */

public class AcceptingTests {

    /**
     * (US 05.01.01) As a driver, I want to accept a request I agree with and accept that offered payment upon completion.
     */
    @Test
    public void driverAccept(){
        Rider rider = new Rider("Mike");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        Driver driver = new Driver("Cole");
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        assertTrue("The request is being updated when it shouldn't", rc.getOpenRequests().size() == 1);
        assertTrue("The request has not been marked as accepted by the driver.", rc.getPendingRequests(driver).size() == 1);
    }

    /**
     * (US 05.02.01) As a driver, I want to view a list of things I have accepted that are pending, each request with its description, and locations.
     */
    @Test
    public void driverPendingAccepts(){
        Rider rider = new Rider("Mike");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        Driver driver = new Driver("Cole");
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        // Add another driver to compare
        Driver driver1 = new Driver("WASD");
        Request request1 = new Request(rider, new Location(), new Location());
        rc.addRequest(request1);
        rc.addDriver(request1,driver1);
        assertFalse("Two drivers share pending requests when they shouldn't", rc.getPendingRequests(driver).equals(rc.getPendingRequests(driver1)));
        assertTrue("The request has not been marked as accepted by the driver.", request.getAcceptedDrivers().contains(driver));
        assertTrue("Driver is not able to view their pending requests .", rc.getPendingRequests(driver).size() == 1);
        assertTrue("Request in pending requests is not the same.", rc.getPendingRequests(driver).get(0).equals(request));
        assertEquals("The request is not being updated to pending", Request.ACCEPTED, request.getStatus());
    }
    /**
     * (US 05.03.01) As a driver, I want to see if my acceptance was accepted.
     */
    @Test
    public void driverAcceptedRequests(){
        Rider rider = new Rider("Josh");
        Driver driver = new Driver("Kevin");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.acceptDriver(request, driver);
        assertTrue("Driver has been added to the request as the confirmed driver", request.getConfirmedDriver().equals(driver));
        assertFalse("The request is still available to be accepted by other drivers", rc.getAvailableRequests().contains(request));
        assertEquals("The request is not being updated to confirmed", Request.CONFIRMED, request.getStatus());
    }
    /**
     * (US 05.04.01) As a driver, I want to be notified if my ride offer was accepted.
     */
    @Test
    public void notifyDriver(){
        Rider rider = new Rider("Josh");
        Driver driver = new Driver("Kevin");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        rc.addDriver(request,driver);
        rc.acceptDriver(request,driver);
        assertTrue("Driver is not being notified about accepted ride offers.", driver.hasNotifications());
    }
}
