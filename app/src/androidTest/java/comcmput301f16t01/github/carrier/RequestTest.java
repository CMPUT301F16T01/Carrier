package comcmput301f16t01.github.carrier;

/**
 * Test suite for Elastic Requests.
 * Test List:
 *      1) Test clearing all requests for a user. (Confirms that we can add a request for a user).
 *      2) Test getting more than one type of request for a user.
 *      3) Test adding an offer to a request (from a driver offering to accept)
 */
public class RequestTest extends ApplicationTest {
    private User basicRider = new User( "reqTestUser", "giveMeRide@carrier.com", "41534153" );
    private User anotherUser = new User( "reqTestUser2", "loveSia@hotmail.com", "514514514" );

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

    
}
