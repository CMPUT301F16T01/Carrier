package comcmput301f16t01.github.carrier;

import org.junit.After;
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
     * As a rider, I want to request rides between two locations.
     * Related: US 01.01.01
     */
    @Test
    public void riderRequest() {
        Rider rider = new Rider("Kieter");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();

        // assert there is no requests for this rider
        assertEquals( "There should be no requests in the requestController",
                0, rc.getRequests(rider).size() );

        rc.addRequest(request);

        // assert there is one request
        assertEquals( "There should be no requests in the requestController",
                1, rc.getRequests(rider).size() );

        assertTrue("Request made and request in controller aren't the same",
                request.equals(rc.getRequests(rider).get(0)));
    }

    /**
     * As a rider, I want to see current requests I have open.
     * Related: US 01.02.01
     */
    @Test
    public void seeOpenRequests() {
        Rider riderOne = new Rider("username");
        Request request = new Request(riderOne, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        assertEquals("There should only be one request returned.",
                1, rc.getRequests(riderOne).size());

        // Add a request to ensure we get back specific requests of a user.
        rc.addRequest(new Request(new Rider("otherRider"), new Location(), new Location()));

        // Ensures that we still only get one request for our user, with a second user in the system
        assertEquals("There should only be one request returned.",
                1, rc.getRequests(riderOne).size());

        // Checks if the request put in is the same that returns
        assertEquals("getRequests should return requests for a specified user",
                request, rc.getRequests(riderOne).get(0));

        // TODO include "get open requests? or just check if .isOpen() (?)

    }

    /**
     * As a rider, I want to be notified if my request is accepted.
     * Related: US 01.03.01
     */
    @Test
    public void acceptedRequestNotification() {
        Rider rider = new Rider("Bennett");
        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();

        rc.addRequest(request);
        rc.addDriver( request, new Driver("Kieter (not really)"));

        assertEquals( "The request should be accepted",
                Request.OFFERED, request.getStatus() );

        // Ensures that we've done something with the fact that a driver has accepted the
        // rider's request.
        assertTrue( "The rider should be notified that they have gotten a driver for their request",
                rider.hasNotification());
    }

    /**
     * As a rider, I want to cancel requests.
     * Related: US 01.04.01
     */
    @Test
    public void riderCancelRequests() {
        Rider riderOne = new Rider("username");
        Request request1 = new Request(riderOne, new Location(), new Location());
        Request request2 = new Request(riderOne, new Location(), new Location());

        assertNotEquals("The requests cannot be considered equal for this test",
                request1, request2);

        RequestController rc = new RequestController();
        rc.addRequest(request1);
        rc.addRequest(request2);

        rc.cancelRequest(riderOne, request2);

        assertEquals("The request should be init to open",
                Request.OPEN, request1.getStatus());
        assertEquals("This request should be closed",
                Request.CANCELLED, request2.getStatus());
    }

    /**
     * As a rider, I want to be able to phone or email the driver who accepted a request.
     * US 01.05.01
     */
    @Test
    public void riderContactDriver() {
        Rider rider = new Rider("Sarah");
        Driver driver = new Driver("Mandy");
        String email = "mandy@mandy.com";
        driver.setEmail(email);
        String phone = "1234567890";
        driver.setPhone(phone);

        Request request = new Request(rider, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        // Adds a driver to the request, meaning that the request was accepted by a driver/drivers
        // In this case, just a single driver.
        rc.addDriver(request, driver);

        // The rider is able to access the driver's phone/email we input
        assertEquals("The driver email doesn't match what was input",
                request.getOffers().get(0).getEmail());
        assertEquals("The driver phone number doesn't match what was input",
                phone, request.getOffers().get(0).getPhone());

        // TODO consider the case with more than one driver
    }

    /**
     * As a rider, I want an estimate of a fair fare to offer to drivers.
     * Related: US 01.06.01
     */
    @Test
    public void getFareEstimate() {
        Rider riderOne = new Rider("username");
        Location start = new Location();
        Location end = new Location();
        Request request = new Request(riderOne, start, end);
        FareCalculator fareCalc = new FareCalculator(start, end);
        assertEquals("A request should have a fare estimate",
                request.getFareEstimate(), fareCalc.getEstimate());

        assertNotEquals("The fare estimate should not be 0.",
                0, request.getFareEstimate());
    }

    /**
     * As a rider, I want to confirm the completion of a request and enable payment
     */
    @Test
    public void confirmCompletionAndPay() {
        Rider rider = new Rider("Michael");
        Driver driver = new Driver("Protein Powder");
        Request request = new Request(rider, new Location(), new Location());

        RequestController rc = new RequestController();
        rc.addDriver(request, driver);

        rc.confirmDriver( request, driver );

        rc.completeRequest( request );

        assertEquals( "The request should be set to complete",
                Request.COMPLETE, request.getStatus());

        rc.payForRequest( request );

        assertEquals( "The request should be paid for now.",
                Request.PAID, request.getStatus());
    }

    /**
     * As a rider, I want to confirm a driver's acceptance.
     * Related: US 01.08.01
     */
    @Test
    public void riderAcceptsDriver() {
        Rider riderOne = new Rider("username");
        Driver driverOne = new Driver("username2");
        Driver driverTwo = new Driver("username3");
        RequestController rc = new RequestController();
        Request request = new Request(riderOne, new Location(), new Location());
        rc.addRequest(request);
        rc.addDriver(request, driverOne);
        rc.addDriver(request, driverTwo);

        assertEquals("There should be two offers on this request",
                2, request.getOffers().size());

        rc.confirmDriver(request, driverTwo);

        assertEquals("The status of the request should be CONFIRMED",
                Request.CONFIRMED, request.getStatus());

        try {
            Driver testDriver = new Driver("Mr. FailYourTests");
            rc.addDriver(request, testDriver);
            fail("You should not be able to add a driver to a request if it is CONFIRMED.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

}
