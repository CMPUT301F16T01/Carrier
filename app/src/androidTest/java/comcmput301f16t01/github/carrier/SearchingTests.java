package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;


public class SearchingTests extends ApplicationTest {
    // somewhere in Tokyo, Japan
    static final double latitude1 = 35.6895;
    static final double longitude1 = 139.6917;

    // somewhere in Seoul, South Korea
    static final double latitude2 = 37.5665;
    static final double longitude2 = 126.9780;

    // somewhere in Kawasaki, Japan
    static final double latitude3 = 35.5308;
    static final double longitude3 = 139.7030;

    // Imperial Palace, Tokyo, Japan
    static final double latitude4 = 35.6852;
    static final double longitude4 = 139.7528;

    private User loggedInUser = new User( "searchTestUser", "notify@email.com", "888-999-1234" );
    private User driverOne = new User( "searchTestDriver", "notifyYou@email.com", "0118-99-112" );

    /**
     * Clears requests created by searchTestUser and clears request offers made by searchTestDriver
     */
    protected void tearDown() throws Exception {
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

    /**
     * Tests the SearchByLocation functionality in the RequestController. Tests that the correct
     * number of requests are returned and that they are returned in the correct order (i.e.
     * starting with those closest to the driver searching).
     *
     * Addresses Use Cases Searching #1 and #5.
     */
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

        RequestController rc = new RequestController();
        rc.addRequest(request3);
        rc.addRequest(request2);
        rc.addRequest(request1);

        RequestList requests = rc.fetchAllRequestsWhereRider(loggedInUser);

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while (requests.size() < 3) {
            requests = rc.fetchAllRequestsWhereRider(loggedInUser);
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
        rc.searchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 2 requests: " + rc.getResult().size(), rc.getResult().size() == 2);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request lat incorrect", request1.getStart().getLatitude(), rc.getResult().get(0).getStart().getLatitude());
        Assert.assertEquals("Closest request long incorrect", request1.getStart().getLongitude(), rc.getResult().get(0).getStart().getLongitude());
        Assert.assertEquals("2nd closest request lat incorrect", request3.getStart().getLatitude(), rc.getResult().get(1).getStart().getLatitude());
        Assert.assertEquals("2nd closest request long incorrect", request3.getStart().getLongitude(), rc.getResult().get(1).getStart().getLongitude());
        Assert.assertFalse("Search returned location out of range", rc.getResult().contains(request2));
    }

    public void testDriverSearchByLocationWithConfirmed() {
        // TODO write test once confirming driver stuff is done
        assertTrue(false);
    }

    /**
     * Tests that requests with specific keywords in the description can be queried.
     *
     * Addressing Use Case Searching #2.
     */
    public void testDriverSearchByKeyword() {
        String keyword1 = "dkfjlasb";
        String keyword2 = "ksjdahfk";
        String keyword3 = "sjdjakfk";
        String keyword4 = "dhsbskak";

        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword1);
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2);
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2 + ", " + keyword3);

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

        rc.searchByKeyword(keyword2);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests: " + rc.getResult().size(), rc.getResult().size() == 2);
        rc.searchByKeyword(keyword1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", rc.getResult().size() == 1);
        rc.searchByKeyword(keyword4);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);
    }

    // TODO confirmDriver method not complete, this test will not pass
    public void testDriverSearchByKeywordWithConfirmed() {
        String keyword1 = "dkfjlasb";
        String keyword2 = "ksjdahfk";
        String keyword3 = "sjdjakfk";
        String keyword4 = "dhsbskak";

        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword1);
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2);
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2 + ", " + keyword3);

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
        rc.searchByKeyword(keyword2);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests: " + rc.getResult().size(), rc.getResult().size() == 2);
        rc.searchByKeyword(keyword1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request", rc.getResult().size() == 1);
        rc.searchByKeyword(keyword4);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);

        rc.addDriver(requestThree, driverOne);
        rc.confirmDriver(requestThree, driverOne);
    }
}
