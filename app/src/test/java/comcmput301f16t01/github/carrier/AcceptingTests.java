package comcmput301f16t01.github.carrier;

import org.junit.Test;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Users.User;

import static org.junit.Assert.*;
/**
 * Created by michael on 11/10/16.
 */
@Deprecated
public class AcceptingTests {

    /**
     * (US 05.01.01) As a driver, I want to accept a request I agree with and accept that offered payment upon completion.
     */
    @Test
    public void driverAccept(){
        User rider = new User("Mike");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        User driver = new User("Cole");
        // Check to see if there is an open request.
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        // Check to see if the request is still labelled as open when it should be labelled as offered.
        // So it should no longer be in open.
        assertTrue("The request is not being updated when it should.", rc.getOpenRequests().size() == 0);
        // Check to make sure that the request has been added to the drivers offered requests.
        assertTrue("The request has not been marked as offered by the driver.", rc.getOfferedRequests(driver).size() == 1);
        rc.confirmDriver(request, driver);
        rc.completeRequest(request);
        rc.payForRequest(request);
        // Make sure that the status of the request is paid.
        assertEquals("The request has not been paid for", Request.PAID, request.getStatus());
    }

    /**
     * (US 05.02.01) As a driver, I want to view a list of things I have accepted that are pending, each request with its description, and locations.
     */
    @Test
    public void driverPendingAccepts(){
        User rider = new User("Mike");
        CarrierLocation start = new CarrierLocation();
        CarrierLocation end = new CarrierLocation();
        Request request = new Request(rider, start, end);
        RequestController rc = new RequestController();
        rc.addRequest(request);
        String description = "This description.";
        rc.setRequestDescription(request, description);
        User driver = new User("Cole");
        // The request should be open right now and we test to make sure that it is.
        assertTrue("Get open request is not returning any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        // Add another driver to compare
        User driver1 = new User("Mandy");
        Request request1 = new Request(rider, new CarrierLocation(), new CarrierLocation());
        rc.addRequest(request1);
        rc.addDriver(request1,driver1);
        // Make sure that the request is only added to the offering driver.
        assertFalse("Two drivers share pending requests when they shouldn't", rc.getOfferedRequests(driver).equals(rc.getOfferedRequests(driver1)));
        // We want to make sure that the driver has been added to the list of offered drivers in the request.
        assertTrue("The request has not been marked as offered by the driver.", request.getOfferedDrivers().contains(driver));
        // Want to make sure that the request has been added to the drivers list of offered requests.
        assertTrue("Driver is not able to view their pending requests .", rc.getOfferedRequests(driver).size() == 1);
        // Make sure the request added to the driver's offered requests is the same as the current request.
        // This should cover viewing the locations and description since the are the same object but I will test it in the following lists.
        assertTrue("Request in pending requests is not the same.", rc.getOfferedRequests(driver).get(0).equals(request));
        assertTrue("Start location is not the same.", rc.getOfferedRequests(driver).get(0).getStart().equals(start));
        assertTrue("End location is not the same.", rc.getOfferedRequests(driver).get(0).getEnd().equals(start));
        assertEquals("Descriptions are not the same", description, rc.getOfferedRequests(driver).get(0).getDescription());
        // Check to make sure that the request is labelled as offered.
        assertEquals("The request is not being updated to offered.", Request.OFFERED, request.getStatus());
    }

    /**
     * (US 05.03.01) As a driver, I want to see if my acceptance was accepted.
     */
    @Test
    public void driverAcceptedRequests(){
        User rider = new User("Josh");
        User driver = new User("Kevin");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        rc.addDriver(request, driver);
        // Makes sure that the request is still available to be offered to by other drivers.
        assertTrue("The request is no longer available but it should be.", rc.getAvailableRequests().contains(request));
        rc.confirmDriver(request, driver);
        // Make sure that the confirmed driver is correct.
        assertTrue("Driver has been added to the request as the confirmed driver", request.getConfirmedDriver().equals(driver));
        // Makes sure that drivers can not make an offer on the request.
        assertFalse("The request is still available to be accepted by other drivers", rc.getAvailableRequests().contains(request));
        // Makes sure that the request is being labelled as confirmed.
        assertEquals("The request is not being updated to confirmed", Request.CONFIRMED, request.getStatus());
    }

    /**
     * (US 05.04.01) As a driver, I want to be notified if my ride offer was accepted.
     */
    @Test
    public void notifyDriver(){
        User rider = new User("Josh");
        User driver = new User("Kevin");
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation());
        RequestController rc = new RequestController();
        rc.addRequest(request);
        rc.addDriver(request,driver);
        rc.confirmDriver(request,driver);
        assertTrue("Driver is not being notified about accepted ride offers.", driver.hasNotifications());
    }
}
