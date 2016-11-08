package comcmput301f16t01.github.carrier;

/**
 * Test suite for Elastic Requests.
 * Test List:
 *      1) Test clearing all requests for a user. (Confirms that we can add a request for a user).
 */
public class RequestTest extends ApplicationTest {
    private User basicRider = new User( "reqTestUser", "giveMeRide@carrier.com", "41534153" );

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
        rc.clearAllRequests( basicRider );
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


    }
}
