package comcmput301f16t01.github.carrier;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;

/**
 * Test suite for Elastic Requests.
 * Test List:
 *      1) Test clearing all requests for a user. (Confirms that we can add a request for a user).
 *      2) Test getting more than one type of request for a user.
 *      3) Test adding driver to a request (visible on a rider's getRequest)
 *      4) Test getting requests where the driver has offered
 *      5) Test getting a request by its ID.
 *
 *      TODO various tests:
 *      X) Test to ensure separation from "offering drivers" and "rider" (when searching)
 *      X) Test that we remove offers when we set a chosen driver (or just that the functionality works)
 *      X) Test that we have the most recent version of a user's information (while online) [[ i.e. a offeringDriver changes their info ]]
 */
public class RequestTest extends ApplicationTest {
    private User basicRider = new User( "reqTestUser", "giveMeRide@carrier.com", "41534153" );
    private User anotherUser = new User( "reqTestUser2", "loveSia@hotmail.com", "514514514" );
    private User basicDriver = new User( "offeringDriver", "wannaDriveYou@gmail.com", "1323123" );

    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    private void chillabit( long time ) {
        try {
            Thread.sleep( time );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** TEST1 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can clear all of a user's requests (only for debugging purposes).
     */
    public void testClearingRequests() {
        RequestController rc = new RequestController();
        RequestList requestList;
        int pass;

        // add and make a request for elastic search
        Request request = new Request( basicRider, new Location(), new Location(),
                "testClearingRequests" );
        rc.addRequest( request );

        // fetch and make sure we have made at least one request for elastic search
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() == 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be at least one fetched request for this test.",
                requestList.size() != 0);

        // clear all the requests and make sure we have done so.
        rc.clearAllRiderRequests( basicRider );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no more requests fetched because we cleared them.",
                requestList.size() == 0);
    } // testClearingRequests

    /** TEST2 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can grab requests of a certain status for a rider. 
     */
    public void testFilteringRequests() {
        RequestController rc = new RequestController();
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        rc.clearAllRiderRequests( anotherUser );
        rc.clearAllRiderRequests( basicRider );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Two requests for basicRider with differing statuses
        String rOneString = "testFilteringRequests1";
        Request requestOne = new Request( basicRider, new Location(), new Location(),
                rOneString );
        requestOne.setStatus( Request.CANCELLED );
        rc.addRequest( requestOne );

        String rTwoString = "testFilteringRequests2";
        Request requestTwo = new Request( basicRider, new Location(), new Location(),
                rTwoString );
        requestTwo.setStatus( Request.COMPLETE );
        rc.addRequest( requestTwo );

        // A request with another user, but the same status as requestOne
        String rThreeString = "testFilteringRequests3";
        Request requestThree = new Request( anotherUser, new Location(), new Location(),
                rThreeString );
        requestThree.setStatus( Request.CANCELLED );
        rc.addRequest( requestThree );

        // Test that we can fetch one request, even if there is another user with the same status
        requestList = rc.fetchRequestsWhereRider( basicRider, Request.CANCELLED );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit( 1000 );
            requestList = rc.fetchRequestsWhereRider( basicRider, Request.CANCELLED );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "[1] There should be exactly one request of this type for this user",
                requestList.size() == 1);
        assertTrue( "[1] We should get only what we requested (same rider)",
                requestList.get(0).getRider().getUsername().equals(basicRider.getUsername()));
        assertTrue( "[1] The request should have the same status.",
                requestList.get(0).getStatus() == Request.CANCELLED );
        assertTrue( "[1] The request we got should have the same description.",
                requestList.get(0).getDescription().equals(requestOne.getDescription()));

        // Add another request to basic rider to allow checking for 2+ status types
        String rFourString = "testFilteringRequests3";
        Request requestFour = new Request( basicRider, new Location(), new Location(),
                rFourString );
        requestFour.setStatus( Request.OPEN );
        rc.addRequest( requestFour );

        // Test that we can fetch two requests.
        requestList = rc.fetchRequestsWhereRider( basicRider, Request.CANCELLED, Request.COMPLETE );
        pass = 0;
        while( requestList.size() != 2 ) {
            chillabit( 1000 );
            requestList = rc.fetchRequestsWhereRider( basicRider, Request.CANCELLED, Request.COMPLETE );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "[2] We should have only two requests, since we only created two.",
                requestList.size() == 2 );
        assertTrue( "[2] Their statuses should be completely different, since there are no duplicates",
                requestList.get(0).getStatus() != requestList.get(1).getStatus());
        assertTrue( "[2] One of the requests should equal the one of the statuses we requested (complete)",
                requestList.get(0).getStatus() == Request.COMPLETE || requestList.get(1).getStatus() == Request.COMPLETE );
        assertTrue( "[2] One of the requests should equal the one of the statuses we requested (cancelled)",
                requestList.get(0).getStatus() == Request.CANCELLED || requestList.get(1).getStatus() == Request.CANCELLED );
    }


    /** TEST3 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can add a driver to a request (as an offer)
     *
     */
    public void testAddingDriverToRequest() {
        RequestController rc = new RequestController();
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        rc.clearAllRiderRequests( basicRider );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Add a request and check that it is there
        Request request = new Request( basicRider, new Location(), new Location(),
                "testAddingDriverToRequest" );
        rc.addRequest( request );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "We should fetch a request here",
                requestList.size() == 1);
        Request test = requestList.get(0);
        assertTrue( "The request should have an ID.",
                test.getId() != null);
        assertTrue( "There should be no offered drivers yet",
                test.getOfferedDrivers() == null || test.getOfferedDrivers().size() == 0);

        rc.addDriver( test, basicDriver );
        pass = 0;
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        while( requestList.get(0).getOfferedDrivers().size() == 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "We should have added a driver to this request.",
                requestList.get(0).getOfferedDrivers().size() == 1 );
    }


    /** TEST4 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can grab requests for a driver who has offered to drive for some requests.
     *
     * Tests that it returns only one request even if there are others without him.
     * Tests that it does not return his own requests.
     */
    public void testGetRequestsWhereOffered() {
        RequestController rc = new RequestController();
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        rc.clearAllRiderRequests( basicDriver );
        rc.clearAllRiderRequests( basicRider );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Create and add three requests
        Request requestOne = new Request( basicRider, new Location(), new Location(),
                "testGetRequestsWhereOffered (no offers)" );
        Request requestTwo = new Request( basicRider, new Location(), new Location(),
                "testGetRequestsWhereOffered (offers)" );
        Request requestThree = new Request( basicDriver, new Location(), new Location(),
                "testGetRequestsWhereOffered (driver's request)");

        rc.addRequest( requestOne );
        rc.addRequest( requestTwo );
        rc.addRequest( requestThree );

        // Ensure that at least the basicDriver's request is present
        requestList = rc.fetchRequestsWhereRider( basicDriver );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit( 1000 );
            requestList = rc.fetchRequestsWhereRider( basicDriver );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "The driver posted a request, but it was not found.",
                requestList.size() == 1);

        fail( "This test need to be finished..." );
    }

    /** TEST5 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can get a request by its ID.
     */
    public void testGettingRequestByID() {
        RequestController rc = new RequestController();
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        rc.clearAllRiderRequests( basicRider );
        requestList = rc.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit( 1000 );
            requestList = rc.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);
        
        Request request = new Request( basicRider, new Location(), new Location(),
                "testGetMeByID" );
        rc.addRequest( request );

        pass = 0;
        while( request.getId() == null ) {
            chillabit( 1000 );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "The request should receive an ID value.",
                request.getId() != null );
        
        // Try getting the request
        ElasticRequestController.GetRequestTask grt = new ElasticRequestController.GetRequestTask();
        grt.execute( request.getId() );
        Request getRequest = null;
        try {
            getRequest = grt.get();
        } catch (Exception e) {
            fail( "There should be no exceptional case here" );
        }
        
        assertTrue( "The descriptions should match.",
                request.getDescription().equals(getRequest.getDescription()));
    }
}
