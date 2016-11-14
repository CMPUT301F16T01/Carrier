package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import java.util.ArrayList;


import comcmput301f16t01.github.carrier.Notifications.ElasticNotificationController;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;


public class SearchingTests extends ApplicationTest {
    // University of Alberta, Edmonton
    static final double latitude1 = 53.5232;
    static final double longitude1 = -113.5263;

    // somewhere in London, Ontario
    static final double latitude2 = 42.9870;
    static final double longitude2 = -81.2432;

    // somewhere in St. Albert, Alberta
    static final double latitude3 = 53.6305;
    static final double longitude3 = -113.6256;

    // somewhere in Edmonton, Alberta
    static final double latitude4 = 53.5444;
    static final double longitude4 = -113.4909;

    private User loggedInUser = new User( "notifTestUser", "notify@email.com", "888-999-1234" );
    private User driverOne = new User( "notifTestDriver", "notifyYou@email.com", "0118-99-112" );

    // Set up a test user to receive notifications
    private void setUpUser() {
        UserController uc = new UserController();
        String result = uc.createNewUser( loggedInUser.getUsername(),
                loggedInUser.getEmail(),
                loggedInUser.getPhone() );

        if (result == null) {
            System.out.print( "null line" );
        }

        assertTrue( "Failed to log in for test.", uc.logInUser( loggedInUser.getUsername() ) );
    }

    // This tear down method may not be working entirely as expected...test further
    protected void tearDown() throws Exception {
        ElasticNotificationController.ClearAllTask cat = new ElasticNotificationController.ClearAllTask();
        cat.execute( loggedInUser.getUsername(), driverOne.getUsername());

        ElasticRequestController.ClearRiderRequestsTask crt = new ElasticRequestController.ClearRiderRequestsTask();
        crt.execute( loggedInUser.getUsername(), driverOne.getUsername());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute(loggedInUser.getUsername(), driverOne.getUsername());

        super.tearDown();
    }

    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    // TODO convert to a full out AsyncWait method to generalize waiting for .size() == RequestAdapter tasks?
    private void chillabit( long time ) {
        try {
            Thread.sleep( time );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO implement searchByLocation, this test will not pass
    public void testDriverSearchByLocation() {
        ArrayList<Request> requests;

        User rider1 = new User("Mandy");
        CarrierLocation startLocation1 = new CarrierLocation();
        CarrierLocation endLocation1 = new CarrierLocation();
        endLocation1.setLatitude(latitude1);
        endLocation1.setLongitude(longitude1);
        Request request1 = new Request(rider1, startLocation1, endLocation1, "");

        User rider2 = new User("Abigail");
        CarrierLocation startLocation2 = new CarrierLocation();
        CarrierLocation endLocation2 = new CarrierLocation();
        endLocation2.setLatitude(latitude2);
        endLocation2.setLongitude(longitude2);
        Request request2 = new Request(rider2, startLocation2, endLocation2, "");

        User rider3 = new User("Alison");
        CarrierLocation startLocation3 = new CarrierLocation();
        CarrierLocation endLocation3 = new CarrierLocation();
        endLocation3.setLatitude(latitude3);
        endLocation3.setLongitude(longitude3);
        Request request3 = new Request(rider3, startLocation3, endLocation3, "");

        RequestController rc = new RequestController();
        rc.addRequest(request1);
        rc.addRequest(request2);
        rc.addRequest(request3);

        CarrierLocation driverLocation = new CarrierLocation();
        driverLocation.setLatitude(latitude4);
        driverLocation.setLongitude(longitude4);
        // this method should return a list of requests, sorted based on proximity of start location
        // for now I'm assuming there are limits on how far away a request can be to be included in this list
        // TODO would it be better to use ArrayList<Request> or requestList
        requests = rc.getSearchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 2 requests", requests.size() == 2);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request incorrect", request1, requests.get(0));
        Assert.assertEquals("2nd closest request incorrect", request3, requests.get(1));
        Assert.assertFalse("Search returned location out of range", requests.contains(request2));

        // TODO clarify our terminology...should this return open or accepted requests
        User driver = new User("Amber");
        rc.addDriver(request1, driver);
        rc.confirmDriver(request1, driver);

        // request1 should no longer be included in the search results
        requests = rc.getSearchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 1 request", requests.size() == 1);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request incorrect", request3, requests.get(0));
        Assert.assertFalse("Search returned non-open request", requests.contains(request1));
        Assert.assertFalse("Search returned location out of range", requests.contains(request2));
    }

    /**
     * TEST1
     *
     * Tests that requests with specific keywords in the description can be queried.
     *
     * As a driver, I want to browse and search for open requests by keyword.
     * Related: US 04.02.01
     */
    public void testDriverSearchByKeyword() {

        setUpUser();

        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: downtown");
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home");
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home, work");

        RequestController rc = new RequestController();
        rc.addRequest(requestOne);
        rc.addRequest(requestTwo);
        rc.addRequest(requestThree);

        RequestList requests = rc.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while( requests.size() < 3 ) {
            requests = rc.fetchAllRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 10) { break; }
        }

        // this method should return a list of requests based on keywords in the request description
        String query1 = "home";
        String query2 = "downtown"; // should not be case-dependent
        String query3 = "walk";

        rc.searchByKeyword(query1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests: " + rc.getResult().size(), rc.getResult().size() == 2);
        rc.searchByKeyword(query2);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", rc.getResult().size() == 1);
        rc.searchByKeyword(query3);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);
    }

    // TODO confirmDriver method not complete, this test will not pass
    public void testDriverSearchByKeywordWithConfirmed() {
        setUpUser();

        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: downtown");
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home");
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home, work");

        RequestController rc = new RequestController();
        rc.addRequest(requestOne);
        rc.addRequest(requestTwo);
        rc.addRequest(requestThree);

        RequestList requests = rc.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while( requests.size() < 3 ) {
            requests = rc.fetchAllRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 10) { break; }
        }

        // this method should return a list of requests based on keywords in the request description
        String query1 = "home";
        String query2 = "downtown"; // should not be case-dependent
        String query3 = "walk";

        rc.addDriver(requestOne, driverOne);
        rc.confirmDriver(requestOne, driverOne);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        pass = 0;
        while( requestOne.getStatus() != 3 ) {
            chillabit( 1000 );
            pass++;
            if (pass > 10) { break; }
        }

        // requestOne should no longer be included in search results
        rc.searchByKeyword(query1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests", rc.getResult().size() == 2);
        rc.searchByKeyword(query2);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests: " + rc.getResult().size(), rc.getResult().size() == 0);
        rc.searchByKeyword(query3);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", rc.getResult().size() == 1);

        rc.addDriver(requestThree, driverOne);
        rc.confirmDriver(requestThree, driverOne);
    }
}
