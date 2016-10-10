package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AHASJDFHASKJFHD:KAJSHFIEUASOBFOUIBEFGOAUBGVOIAWHFOUIHAWFOUHAWFOUH
 */
public class RequestsTests {

    @After
    public void clean() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     *  As a rider, I want to request rides between two locations.
     *  Related: US 01.01.01
     */
    @Test
    public void riderRequest() {
        Rider rider = new Rider("Kieter");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        assertTrue("Request made and request in controller aren't the same", request.equals(rc.getRequests(rider).get(0)));
    }

    /**
     * As a rider, I want to see current requests I have open.
     * Related: US 01.02.01
     */
    @Test
    public void seeOpenRequests() {
        Rider riderOne = new Rider( "username" );
        Request request = new Request( riderOne, new Location(), new Location() );
        RequestController rc = new RequestController();
        rc.addRequest( request );

        assertEquals( "There should only be one request returned.",
                1, rc.getRequests( riderOne ).size() );

        // Add a request to ensure we get back specific requests of a user.
        rc.addRequest( new Request( new Rider( "otherRider" ), new Location(), new Location() ));

        // Ensures that we still only get one request for our user, with a second user in the system
        assertEquals( "There should only be one request returned.",
                1, rc.getRequests( riderOne ).size() );

        // Checks if the request put in is the same that returns
        assertEquals( "getRequests should return requests for a specified user",
                request, rc.getRequests( riderOne ).get( 0 ) );

        // TODO include "get open requests? or just check if .isOpen() (?)

    }

    /**
     * As a rider, I want to be notified if my request is accepted.
     * Related: US 01.03.01
     */
    @Test
    public void acceptedRequestNotification() {
        Rider rider = new Rider("Bennett");
        Request request = new Request( rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        // Ensures that we've done something with the fact that a driver has accepted the
        // rider's request.
        assertTrue(rider.getNotify());
    }

    /**
     * As a rider, I want to cancel requests.
     * Related: US 01.04.01
     */
    @Test
    public void riderCancelRequests() {
        Rider riderOne = new Rider( "username" );
        Request request1 = new Request( riderOne, new Location(), new Location() );
        Request request2 = new Request( riderOne, new Location(), new Location() );

        assertNotEquals( "The requests cannot be considered equal for this test",
                request1, request2 );

        RequestController rc = new RequestController();
        rc.addRequest( request1 );
        rc.addRequest( request2 );

        rc.cancelRequest( riderOne, request2 );

        assertEquals( "The request should be init to open",
                Request.OPEN, request1.getStatus() );
        assertEquals( "This request should be closed",
                Request.CANCELLED, request2.getStatus() );
    }

    /**
     * As a rider, I want to be able to phone or email the driver who accepted a request.
     * US 01.05.01
     */
    @Test
    public void riderContactDriver() {
        Rider rider = new Rider("Sarah");
        Driver driver = new Driver("Mandy");
        driver.setEmail("mandy@mandy.com");
        driver.setPhone = ("1234567890");

        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        // Adds a driver to the request, meaning that the request was accepted by a driver/drivers
        // In this case, just a single driver.
        request.addDriver(driver);

        // The rider is able to access the driver's phone/email as long as they are non-empty.
        assertTrue("There's no email.", request.getRiders().get(0).getEmail != "");
        assertTrue("There's no phone #.", request.getRiders().get(0).getPhone != "");
    }

    /**
     * 	As a rider, I want an estimate of a fair fare to offer to drivers.
     * 	Related: US 01.06.01
     */
    @Test
    public void getFareEstimate() {
        Rider riderOne = new Rider( "username" );
        Location start = new Location();
        Location end = new Location();
        Request request = new Request( riderOne, start, end );
        FareCalculator fareCalc = new FareCalculator( start, end );
        assertEquals( "A request should get a fare estimate",
                request.getFareEstimate(), fareCalc.getEstimate()  );
    }

    /**
     * As a rider, I want to confirm the completion of a request and enable payment
     */
    @Test
    public void confirmCompletionAndPay() {
        Rider rider = new Rider("Michael");
        Driver driver = new Driver("Protein Powder")
        Location start = new Location();
        Location end = new Location();
        Request request = new Request(rider, new Location(), new Location());
        assertTrue(true);

        RequestController rc = new RequestController();
        rc.addDriver(request, driver);
        assertEquals(4, request.getStatus());
    }

    /**
     * TODO fix name of test
     */
    @Test
    public void test_01_08_01() {
        assertTrue(true);
    }

}
