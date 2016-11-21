package comcmput301f16t01.github.carrier;

import comcmput301f16t01.github.carrier.Notifications.ElasticNotificationController;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.User;

/**
 * Test suite for Elastic Requests.
 * Test List:
 *      1) Test clearing all requests for a user. (Confirms that we can add a request for a user).
 *      2) Test getting more than one type of request for a user.
 *      3) Test adding driver to a request (visible on a rider's getRequest)
 *      4) Test getting requests where the driver has offered
 *      5) Test getting a request by its ID.
 *      6) Tests that the request statuses are properly up to date at each step of the request life-cycle
 *      7) Tests that erroneous status states will never be created.
 *
 *      TODO various tests:
 *      X) Test to ensure separation from "offering drivers" and "rider" (when searching)
 *      X) Test that we have the most recent version of a user's information (while online) [[ i.e. a offeringDriver changes their info ]]
 *      X) Subtle issues, like we can't add drivers when we have a confirmed one!
 */
public class RequestTest extends ApplicationTest {
    private final User basicRider = new User( "reqTestUser", "giveMeRide@carrier.com", "41534153" );
    private final User anotherUser = new User( "reqTestUser2", "loveSia@hotmail.com", "514514514" );
    private final User basicDriver = new User( "offeringDriver", "wannaDriveYou@gmail.com", "1323123" );

    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    private void chillabit() {
        try {
            Thread.sleep((long) 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets all the elastic search for the test users, after each test.
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        ElasticRequestController.ClearRiderRequestsTask crt = new ElasticRequestController.ClearRiderRequestsTask();
        ElasticNotificationController.ClearAllTask cat = new ElasticNotificationController.ClearAllTask();
        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername() );
        cat.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername() );
        crt.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername() );
        super.tearDown();
    }

    /** TEST1 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can clear all of a user's requests (only for debugging purposes).
     */
    public void testClearingRequests() {
        RequestList requestList;
        int pass;

        // add and make a request for elastic search
        Request request = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testClearingRequests" );
        RequestController.addRequest( request );

        // fetch and make sure we have made at least one request for elastic search
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() == 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be at least one fetched request for this test.",
                requestList.size() != 0);

        // clear all the requests and make sure we have done so.
        RequestController.clearAllRiderRequests( basicRider );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
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
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        RequestController.clearAllRiderRequests( anotherUser );
        RequestController.clearAllRiderRequests( basicRider );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Two requests for basicRider with differing statuses
        String rOneString = "testFilteringRequests1";
        Request requestOne = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                rOneString );
        requestOne.setStatus( Request.CANCELLED );
        RequestController.addRequest( requestOne );

        String rTwoString = "testFilteringRequests2";
        Request requestTwo = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                rTwoString );
        requestTwo.setStatus( Request.COMPLETE );
        RequestController.addRequest( requestTwo );

        // A request with another user, but the same status as requestOne
        String rThreeString = "testFilteringRequests3";
        Request requestThree = new Request( anotherUser, new CarrierLocation(), new CarrierLocation(),
                rThreeString );
        requestThree.setStatus( Request.CANCELLED );
        RequestController.addRequest( requestThree );

        // Test that we can fetch one request, even if there is another user with the same status
        requestList = RequestController.fetchRequestsWhereRider( basicRider, Request.CANCELLED );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit();
            requestList = RequestController.fetchRequestsWhereRider( basicRider, Request.CANCELLED );
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
        Request requestFour = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                rFourString );
        requestFour.setStatus( Request.OPEN );
        RequestController.addRequest( requestFour );

        // Test that we can fetch two requests.
        requestList = RequestController.fetchRequestsWhereRider( basicRider, Request.CANCELLED, Request.COMPLETE );
        pass = 0;
        while( requestList.size() != 2 ) {
            chillabit();
            requestList = RequestController.fetchRequestsWhereRider( basicRider, Request.CANCELLED, Request.COMPLETE );
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
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        RequestController.clearAllRiderRequests( basicRider );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Add a request and check that it is there
        Request request = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testAddingDriverToRequest" );
        RequestController.addRequest( request );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
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

        // Add the driver then assert that we could fetch it from elastic search
        RequestController.addDriver( test, basicDriver );
        pass = 0;
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        while( requestList.get(0).getOfferedDrivers().size() == 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
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
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        RequestController.clearAllRiderRequests( basicDriver );
        RequestController.clearAllRiderRequests( basicRider );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);

        // Create and add three requests
        Request requestOne = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testGetRequestsWhereOffered (no offers)" );
        Request requestTwo = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testGetRequestsWhereOffered (offers)" );
        Request requestThree = new Request( basicDriver, new CarrierLocation(), new CarrierLocation(),
                "testGetRequestsWhereOffered (driver's request)");

        RequestController.addRequest( requestOne );
        RequestController.addRequest( requestTwo );
        RequestController.addRequest( requestThree );

        // Ensure that at least the basicDriver's request is present
        requestList = RequestController.fetchRequestsWhereRider( basicDriver );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit();
            requestList = RequestController.fetchRequestsWhereRider( basicDriver );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "The driver posted a request, but it was not found.",
                requestList.size() == 1);

        // add the driver to the previous two requests
        RequestController.addDriver( requestOne, basicDriver );
        RequestController.addDriver( requestTwo, basicDriver );

        requestList = RequestController.getOfferedRequests( basicDriver );
        pass = 0;
        while( requestList.size() != 2 ) {
            chillabit();
            requestList = RequestController.getOfferedRequests( basicDriver );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be two requests where this driver has offered.",
                requestList.size() == 2);
        assertFalse( "[1] We are not looking for requests the driver has issued.",
                requestList.get(0).getRider().getUsername().equals(basicDriver.getUsername()));
        assertFalse( "[2] We are not looking for requests the driver has issued.",
                requestList.get(1).getRider().getUsername().equals(basicDriver.getUsername()));
    }

    /** TEST5 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that we can get a request by its ID.
     */
    public void testGettingRequestByID() {
        RequestList requestList;
        int pass;

        // clear all the requests and make sure we have done so.
        RequestController.clearAllRiderRequests( basicRider );
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 0 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        assertTrue( "There should be no requests fetched because we cleared them.",
                requestList.size() == 0);
        
        Request request = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testGetMeByID" );
        RequestController.addRequest( request );

        pass = 0;
        while( request.getId() == null ) {
            chillabit();
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


    /** TEST6 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that the status changes as expected through each step of the request lifecycle.
     */
    public void testRequestStatus() {
        int pass;

        // Create and add a request
        Request request = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testRequestStatus" );
        RequestController.addRequest( request );

        // Get the request from the controller
        RequestList requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.size() != 1 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        request = requestList.get(0);
        assertTrue( "The request should be OPEN, initially.",
                request.getStatus() == Request.OPEN);

        request = requestList.get(0);
        // Add a driver to the request
        RequestController.addDriver( request, basicDriver );
        // Get the request from the controller (wait until there is an offered driver)
        requestList.clear();
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.get(0).getOfferedDrivers().size() != 1 ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        request = requestList.get(0);
        assertTrue( "There should be an offered driver.",
                request.getOfferedDrivers().size() == 1);
        assertTrue( "The status should be OFFERED",
                request.getStatus() == Request.OFFERED );

        // Confirm the driver for a request.
        RequestController.confirmDriver( request, basicDriver );
        requestList.clear();
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.get(0).getConfirmedDriver() == null ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        request = requestList.get(0);
        assertTrue( "There should be a confirmed driver and it should be the same user we passed in.",
                request.getConfirmedDriver() != null && request.getConfirmedDriver().getUsername().equals(basicDriver.getUsername()));
        assertTrue( "The status should be ",
                request.getStatus() == Request.CONFIRMED );

        // Complete the request
        RequestController.completeRequest( request );
        requestList.clear();
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.get(0).getStatus() != Request.COMPLETE ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        request = requestList.get(0);
        assertTrue( "The request should be complete now.",
                request.getStatus() == Request.COMPLETE );

        // Pay for the request
        RequestController.payForRequest( request );
        requestList.clear();
        requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
        pass = 0;
        while( requestList.get(0).getStatus() != Request.PAID ) {
            chillabit();
            requestList = RequestController.fetchAllRequestsWhereRider( basicRider );
            pass++;
            if (pass > 5) { break; }
        }
        request = requestList.get(0);
        assertTrue( "The request should be paid for now.",
                request.getStatus() == Request.PAID );
    }


    /** TEST7 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     */
    public void testStatusStateErrors() {

    }
}
