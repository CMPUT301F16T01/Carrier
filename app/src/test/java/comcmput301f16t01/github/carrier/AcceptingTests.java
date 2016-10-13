package comcmput301f16t01.github.carrier;

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
        // Check to see if there is an open request.
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        // Check to see if the request is still labelled as open when it should be labelled as offered.
        // So it should no longer be in open.
        assertTrue("The request is not being updated when it should.", rc.getOpenRequests().size() == 0);
        // Check to make sure that the request has been added to the drivers offered requests.
        assertTrue("The request has not been marked as offered by the driver.", rc.getOfferedRequests(driver).size() == 1);
    }

    /**
     * (US 05.02.01) As a driver, I want to view a list of things I have accepted that are pending, each request with its description, and locations.
     */
    @Test
    public void driverPendingAccepts(){
        Rider rider = new Rider("Mike");
        Location start = new Location();
        Location end = new Location();
        Request request = new Request(rider, start, end);

        RequestController rc = new RequestController();
        rc.addRequest(request);
        String description = "This description.";
        rc.setRequestDescription(request, description);
        Driver driver = new Driver("Cole");
        // The request should be open right now and we test to make sure that it is.
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        // Add another driver to compare
        Driver driver1 = new Driver("WASD");
        Request request1 = new Request(rider, new Location(), new Location());
        rc.addRequest(request1);
        rc.addDriver(request1,driver1);
        // Make sure that the request is only added to the offering driver.
        assertFalse("Two drivers share pending requests when they shouldn't", rc.getOfferedRequests(driver).equals(rc.getOfferedRequests(driver1)));
        // We want to make sure that the driver has been added to the list of offered drivers in the request.
        assertTrue("The request has not been marked as offered by the driver.", request.getAcceptedDrivers().contains(driver));
        // Want to make sure that the request has been added to the drivers list of offered requests.
        assertTrue("Driver is not able to view their pending requests .", rc.getOfferedRequests(driver).size() == 1);
        // Make sure the request added to the driver's offered requests is the same as the current request.
        // This should cover viewing the locations and description since the are the same object but I will test it in the following lists.
        assertTrue("Request in pending requests is not the same.", rc.getOfferedRequests(driver).get(0).equals(request));
        assertTrue("Start location is not the same.", rc.getOfferedRequests(driver).get(0).getStart().equals(start));
        assertTrue("End location is not the same.", rc.getOfferedRequests(driver).get(0).getEnd().equals(start));
        assertEquals("Descriptions are not the same", description, rc.getOfferedRequests(driver).get(0).getDescription());
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
