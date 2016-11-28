package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.ElasticUserController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

public class SearchingTests extends ApplicationTest {

    // somewhere in Kawasaki, Japan
    private final double latitude1 = 35.5308;
    private final double longitude1 = 139.7030;

    // somewhere in Seoul, South Korea
    private final double latitude2 = 37.5665;
    private final double longitude2 = 126.9780;

    // Yokohama, Japan
    static final double latitude3 = 35.4437;
    static final double longitude3 = 139.6380;

    // Imperial Palace, Tokyo, Japan
    private final double latitude4 = 35.6852;
    private final double longitude4 = 139.7528;

    private User loggedInUser = new User( "searchTestUser", "notify@email.com", "888-999-1234", "Kia, Rio" );
    private User driverOne = new User( "searchTestDriver", "notifyYou@email.com", "0118-99-112", "Kia, Rio"  );

    /**
     * Clears requests created by searchTestUser and clears request offers made by searchTestDriver
     */
    protected void tearDown() throws Exception {
        ElasticRequestController.ClearRiderRequestsTask crt = new ElasticRequestController.ClearRiderRequestsTask();
        crt.execute( loggedInUser.getUsername(), driverOne.getUsername());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute(loggedInUser.getUsername(), driverOne.getUsername());

        UserController.deleteUser( loggedInUser.getUsername() );
        UserController.deleteUser( driverOne.getUsername() );

        UserController.logOutUser();

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
        UserController.createNewUser( driverOne.getUsername(),
                driverOne.getEmail(),
                driverOne.getPhone(),
                driverOne.getVehicleDescription() );

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

        // Lets confirm that all the requests we've added are in elastic search.
        RequestList requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);
        int pass = 0;
        while (requests.size() != 3) {
            requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);
            chillabit(1000);
            pass++;
            if (pass > 5) { fail("It took too long to check if the requests were properly added"); }
        }

        CarrierLocation driverLocation = new CarrierLocation();
        driverLocation.setLatitude(latitude4);
        driverLocation.setLongitude(longitude4);

        // this method should return a list of requests, sorted based on proximity of start location
        // for now I'm assuming there are limits on how far away a request can be to be included in this list
        RequestController.searchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 2 requests: " + RequestController.getResult().size(),
                RequestController.getResult().size() == 2);

        // check that the requests are ordered properly (Yokohama is further than kawasaki).
        Assert.assertEquals("Closest request lat incorrect", request1.getStart().getLatitude(), RequestController.getResult().get(0).getStart().getLatitude());
        Assert.assertEquals("Closest request long incorrect", request1.getStart().getLongitude(), RequestController.getResult().get(0).getStart().getLongitude());
        Assert.assertEquals("2nd closest request lat incorrect", request3.getStart().getLatitude(), RequestController.getResult().get(1).getStart().getLatitude());
        Assert.assertEquals("2nd closest request long incorrect", request3.getStart().getLongitude(), RequestController.getResult().get(1).getStart().getLongitude());
        Assert.assertFalse("Search returned location out of range", RequestController.getResult().contains(request2));
    }

    /**
     * Tests that requests with specific keywords in the description can be queried.
     *
     * Addressing Use Case Searching #2.
     */
    public void testDriverSearchByKeyword() {
        UserController.deleteUser( driverOne.getUsername() );
        RequestController.clearAllRiderRequests( driverOne );
        // We use gibberish so that "live" requests do not interfere with tests
        String keyword1 = "dkfjlasb@#442323";
        String keyword2 = "ksjdahfk#@#@@@@!";
        String keyword3 = "sjdjakfk999@@@@!";
        String keyword4 = "dhsbskak!ewewwe4";

        UserController.createNewUser( driverOne.getUsername(),
                driverOne.getEmail(),
                driverOne.getPhone(),
                driverOne.getVehicleDescription() );

        // Add requests with the gibberish keywords
        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword1);
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2);
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "Test keywords: " + keyword2 + ", " + keyword3);
        RequestController.addRequest( requestOne );
        RequestController.addRequest( requestTwo );
        RequestController.addRequest( requestThree );

        // Ensure that they've been added to elastic search
        RequestList requests = RequestController.fetchAllRequestsWhereRider(loggedInUser);
        int pass = 0;
        while( requests.size() != 3 ) {
            requests = RequestController.fetchAllRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 5) { fail( "took too long to find three requests. Had " + requests.size()); }
        }

        RequestController.searchByKeyword(keyword2);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 2 requests: " + RequestController.getResult().size(),
                RequestController.getResult().size() == 2);
        RequestController.searchByKeyword(keyword1);
        chillabit( 1000 );
        Assert.assertTrue("Search did not return 1 request",
                RequestController.getResult().size() == 1);
        RequestController.searchByKeyword(keyword4);
        chillabit( 1000 );
        Assert.assertTrue("Search returned requests where it should not have",
                RequestController.getResult().size() == 0);
    }

    /**
     * Test that we can filter by price.
     */
    public void testPriceFiltering() {
        int pass;

        RequestController.clearAllRiderRequests( loggedInUser );
        UserController.createNewUser(driverOne.getUsername(),
                driverOne.getEmail(),
                driverOne.getPhone(),
                "");

        // We put some requests in the elastic searches
        Request requestOne = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "@45jkLmNO02032aassssssssssssssssssssssssss");
        requestOne.setFare( 1000 );
        requestOne.setDistance( 2 );
        Request requestTwo = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "@45jkLmNO02032aassssssssssssssssssssssssss");
        requestTwo.setFare( 998 );
        requestTwo.setDistance( 2 );
        Request requestThree = new Request(loggedInUser, new CarrierLocation(), new CarrierLocation(),
                "@45jkLmNO02032aassssssssssssssssssssssssss");
        requestThree.setFare( 1002 );
        requestThree.setDistance( 2 );
        RequestController.addRequest( requestOne );
        RequestController.addRequest( requestTwo );
        RequestController.addRequest( requestThree );

        // Check that they've been added
        RequestList requestList = RequestController.fetchAllRequestsWhereRider( loggedInUser );
        pass = 0;
        while ( requestList.size() != 3 ) {
            requestList = RequestController.fetchRequestsWhereRider( loggedInUser );
            chillabit( 1000 );
            pass++;
            if ( pass > 3 ) { break; }
        }
        assertTrue( "We should see the three requests we made got " + requestList.size(), requestList.size() == 3 );

        // Ensure the search return all the requests we've added.
        requestList.clear();
        requestList = RequestController.getResult();
        pass = 0;
        while( requestList.size() != 3 && pass < 5 ) {
            RequestController.searchByKeyword( "@45jkLmNO02032aassssssssssssssssssssssssss" );
            requestList = RequestController.getResult();
            chillabit( 1000 );
            pass++;
        }
        assertTrue( "The search should return three requests got " + requestList.size(), requestList.size() == 3 );

        // Ensure we can prune by price
        RequestController.pruneByPrice( 9.99, 10.01 );
        assertTrue( "There should be only one request after the price filter... got " + requestList.size(),
                RequestController.getResult().size() == 1 );

        // Ensure the search return all the requests we've added.
        requestList.clear();
        RequestController.searchByKeyword( "@45jkLmNO02032aassssssssssssssssssssssssss" );
        requestList = RequestController.getResult();
        pass = 0;
        while( requestList.size() != 3 && pass < 5 ) {
            requestList = RequestController.getResult();
            chillabit( 1000 );
            pass++;
        }
        assertTrue( "The search should return three requests", requestList.size() == 3 );

        // Ensure we can prune by price per "KM"
        RequestController.pruneByPricePerKM( 9.99/2 , 10.01/2 );
        assertTrue( "There should be only one request after the perKM filter... got " + requestList.size(),
                RequestController.getResult().size() == 1 );

        // Ensure the search return all the requests we've added.
        requestList.clear();
        RequestController.searchByKeyword( "@45jkLmNO02032aassssssssssssssssssssssssss" );
        requestList = RequestController.getResult();
        pass = 0;
        while( requestList.size() != 3 && pass < 5 ) {
            requestList = RequestController.getResult();
            chillabit( 1000 );
            pass++;
        }
        assertTrue( "The search should return three requests", requestList.size() == 3 );

        // Ensure we can prune by price per "KM" with the nullable attribute
        RequestController.pruneByPricePerKM( 9.99/2 , null );
        assertTrue( "There should be only two request after the perKM filter... got " + requestList.size(),
                RequestController.getResult().size() == 2 );

        // Ensure the search return all the requests we've added.
        requestList.clear();
        RequestController.searchByKeyword( "@45jkLmNO02032aassssssssssssssssssssssssss" );
        requestList = RequestController.getResult();
        pass = 0;
        while( requestList.size() != 3 && pass < 5 ) {
            requestList = RequestController.getResult();
            chillabit( 1000 );
            pass++;
        }
        assertTrue( "The search should return three requests", requestList.size() == 3 );

        // Ensure we can prune by price with the nullable
        RequestController.pruneByPrice( 9.99, null );
        assertTrue( "There should be only two request after the price filter... got " + requestList.size(),
                RequestController.getResult().size() == 2 );
    }
}
