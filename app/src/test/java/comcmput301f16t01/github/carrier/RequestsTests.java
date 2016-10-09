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
     * TODO fix name of test
     */
    @Test
    public void test_01_03_01() {
        assertTrue(true);
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
     * TODO fix name of test
     */
    @Test
    public void test_01_05_01() {
        assertTrue(true);
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
     * TODO fix name of test
     */
    @Test
    public void test_01_07_01() {
        assertTrue(true);
    }

    /**
     * TODO fix name of test
     */
    @Test
    public void test_01_08_01() {
        assertTrue(true);
    }

}
