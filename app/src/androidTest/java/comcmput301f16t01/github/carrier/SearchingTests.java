package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

public class SearchingTests extends ApplicationTest {
    // somewhere in Tokyo, Japan
    private final double latitude1 = 35.6895;
    private final double longitude1 = 139.6917;

    // somewhere in Seoul, South Korea
    private final double latitude2 = 37.5665;
    private final double longitude2 = 126.9780;

    // somewhere in Kawasaki, Japan
    private final double latitude3 = 35.5308;
    private final double longitude3 = 139.7030;

    // Imperial Palace, Tokyo, Japan
    private final double latitude4 = 35.6852;
    private final double longitude4 = 139.7528;

    private User loggedInUser = new User( "notifTestUser", "notify@email.com", "888-999-1234" );
    private User driverOne = new User( "notifTestDriver", "notifyYou@email.com", "0118-99-112" );

    // This tear down method may not be working entirely as expected...test further
    protected void tearDown() throws Exception {
        ElasticRequestController.ClearRiderRequestsTask crt = new ElasticRequestController.ClearRiderRequestsTask();
        crt.execute( loggedInUser.getUsername(), driverOne.getUsername());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute(loggedInUser.getUsername(), driverOne.getUsername());

        UserController.deleteUser( driverOne.getUsername() );

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

    public void testDriverSearchByLocation() {
        CarrierLocation startLocation1 = new CarrierLocation();
        CarrierLocation endLocation1 = new CarrierLocation();
        startLocation1.setLatitude(latitude1);
        startLocation1.setLongitude(longitude1);
        Request request1 = new Request(loggedInUser, startLocation1, endLocation1, "");

        CarrierLocation startLocation2 = new CarrierLocation();
        CarrierLocation endLocation2 = new CarrierLocation();
        startLocation2.setLatitude(latitude2);
        startLocation2.setLongitude(longitude2);
        Request request2 = new Request(loggedInUser, startLocation2, endLocation2, "");

        CarrierLocation startLocation3 = new CarrierLocation();
        CarrierLocation endLocation3 = new CarrierLocation();
        startLocation3.setLatitude(latitude3);
        startLocation3.setLongitude(longitude3);
        Request request3 = new Request(loggedInUser, startLocation3, endLocation3, "");
        
        RequestController.addRequest(request3);
        RequestController.addRequest(request2);
        RequestController.addRequest(request1);

        RequestList requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while (requests.size() < 3) {
            requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);
            chillabit(1000);
            pass++;
            if (pass > 10) {
                break;
            }
        }

        CarrierLocation driverLocation = new CarrierLocation();
        driverLocation.setLatitude(latitude4);
        driverLocation.setLongitude(longitude4);
        // this method should return a list of requests, sorted based on proximity of start location
        // for now I'm assuming there are limits on how far away a request can be to be included in this list
        RequestController.searchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 2 requests: " + RequestController.getResult().size(), RequestController.getResult().size() == 2);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request lat incorrect", request1.getStart().getLatitude(), RequestController.getResult().get(0).getStart().getLatitude());
        Assert.assertEquals("Closest request long incorrect", request1.getStart().getLongitude(), RequestController.getResult().get(0).getStart().getLongitude());
        Assert.assertEquals("2nd closest request lat incorrect", request3.getStart().getLatitude(), RequestController.getResult().get(1).getStart().getLatitude());
        Assert.assertEquals("2nd closest request long incorrect", request3.getStart().getLongitude(), RequestController.getResult().get(1).getStart().getLongitude());
        Assert.assertFalse("Search returned location out of range", RequestController.getResult().contains(request2));
    }

    public void testDriverSearchByLocationWithConfirmed() {
        // TODO write test once confirming driver stuff is done
        assertTrue(false);
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
        // The logged in user must be someone other than who created the requests (otherwise they will
        // not see offers)
        UserController.createNewUser( driverOne.getUsername(),
                driverOne.getEmail(),
                driverOne.getPhone());

        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: downtown");
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home");
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home, work");
        
        RequestController.addRequest(requestOne);
        RequestController.addRequest(requestTwo);
        RequestController.addRequest(requestThree);

        RequestList requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while( requests.size() < 3 ) {
            requests = RequestController.fetchAllRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 10) { break; }
        }

        // this method should return a list of requests based on keywords in the request description
        String query1 = "home";
        String query2 = "downtown"; // should not be case-dependent
        String query3 = "walk";

        RequestController.searchByKeyword(query1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests: " + RequestController.getResult().size(), RequestController.getResult().size() == 2);
        RequestController.searchByKeyword(query2);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", RequestController.getResult().size() == 1);
        RequestController.searchByKeyword(query3);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests", RequestController.getResult().size() == 0);
    }

    // TODO confirmDriver method not complete, this test will not pass
    public void testDriverSearchByKeywordWithConfirmed() {
        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: downtown");
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home");
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: home, work");
        
        RequestController.addRequest(requestOne);
        RequestController.addRequest(requestTwo);
        RequestController.addRequest(requestThree);

        RequestList requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while( requests.size() < 3 ) {
            requests = RequestController.fetchAllRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 10) { break; }
        }

        // this method should return a list of requests based on keywords in the request description
        String query1 = "home";
        String query2 = "downtown"; // should not be case-dependent
        String query3 = "walk";

        RequestController.addDriver(requestOne, driverOne);
        RequestController.confirmDriver(requestOne, driverOne);

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
        RequestController.searchByKeyword(query1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests", RequestController.getResult().size() == 2);
        RequestController.searchByKeyword(query2);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests: " + RequestController.getResult().size(), RequestController.getResult().size() == 0);
        RequestController.searchByKeyword(query3);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", RequestController.getResult().size() == 1);

        RequestController.addDriver(requestThree, driverOne);
        RequestController.confirmDriver(requestThree, driverOne);
    }
}
