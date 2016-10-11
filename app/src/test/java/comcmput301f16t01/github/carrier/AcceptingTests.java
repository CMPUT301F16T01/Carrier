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
        assertTrue("Get open request is not return any open requests", rc.getOpenRequests().size() == 1);
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
        assertTrue("Get open request is not return any open requests", rc.getOpenRequests().size() == 1);
        rc.addDriver(request, driver);
        assertTrue("The request is being updated when it shouldn't", rc.getOpenRequests().size() == 1);
        assertTrue("The request has not been marked as accepted by the driver.", rc.getDrivers(request).size() == 1);
        assertTrue("Driver is not able to view their pending requests .", rc.getPendingRequests(driver).size() == 1);
        assertTrue("Request in pending requests is not the same.", rc.getPendingRequests(driver).get(0).equals(request));


    }
    /**
     * (US 05.03.01) As a driver, I want to see if my acceptance was accepted.
     */
    /**
     * (US 05.04.01) As a driver, I want to be notified if my ride offer was accepted.
     */
}
