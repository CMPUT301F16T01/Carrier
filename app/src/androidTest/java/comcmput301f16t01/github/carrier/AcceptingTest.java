package comcmput301f16t01.github.carrier;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Notifications.ElasticNotificationController;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Notifications.NotificationList;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
<<<<<<< HEAD
=======
import comcmput301f16t01.github.carrier.Users.User;
>>>>>>> master


public class AcceptingTest extends ApplicationTest {
    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    // TODO convert to a full out AsyncWait method to generalize waiting for .size() == RequestAdapter tasks?
    // From NotificationTest
    private User basicRider = new User( "reqTestUser", "giveMeRide@carrier.com", "41534153" );
    private User anotherUser = new User( "reqTestUser2", "loveSia@hotmail.com", "514514514" );
    private User basicDriver = new User( "offeringDriver", "wannaDriveYou@gmail.com", "1323123" );
    private User loggedInUser = new User( "notifTestUser", "notify@email.com", "888-999-1234" );


    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    private void chillabit( long time ) {
        try {
            Thread.sleep( time );
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
        rot.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername(), loggedInUser.getUsername() );
        cat.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername(), loggedInUser.getUsername());
        crt.execute( basicRider.getUsername(), anotherUser.getUsername(), basicDriver.getUsername(), loggedInUser.getUsername());
        super.tearDown();
    }


    /** TEST1 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * (US 05.01.01) As a driver, I want to accept a request I agree with and accept that offered payment upon completion.
     * From RequestTest test with the same name.
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
        Request request = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
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

        // Add the driver then assert that we could fetch it from elastic search
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
    /** TEST2 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  (US 05.02.01) As a driver, I want to view a list of things I have accepted that are pending, each request with its description, and locations.
     *  From RequestTest function with the same name.
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
        Request requestOne = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testGetRequestsWhereOffered (no offers)" );
        Request requestTwo = new Request( basicRider, new CarrierLocation(), new CarrierLocation(),
                "testGetRequestsWhereOffered (offers)" );
        Request requestThree = new Request( basicDriver, new CarrierLocation(), new CarrierLocation(),
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

        // add the driver to the previous two requests
        rc.addDriver( requestOne, basicDriver );
        rc.addDriver( requestTwo, basicDriver );

        requestList = rc.getOfferedRequests( basicDriver );
        pass = 0;
        while( requestList.size() != 2 ) {
            chillabit( 1000 );
            requestList = rc.getOfferedRequests( basicDriver );
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


    /** TEST4 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Test that a user receives a notification when a rider accepts a driver's offer on a request
     *
     * This test also confirms that notifications to two different users go to the specific user and
     * no one else
     * (US 05.04.01) As a driver, I want to be notified if my ride offer was accepted.
     * From NotificationTest with the same name.
     */
    public void testDriverGetNotified() {
        assertTrue( "You must at least have network connection to run this test",
                ConnectionChecker.isConnected( getContext() ) );

        setUpUser();

        NotificationController nc = new NotificationController();
        RequestController rc = new RequestController();

        nc.clearAllNotifications( basicDriver );

        Request newRequest = new Request( UserController.getLoggedInUser(),
                new CarrierLocation(), new CarrierLocation(), "testDriverGetNotified" );

        // Unnecessary clutter for request elastic search, and irrelevant to this test (?)
        rc.addRequest( newRequest );

        NotificationList notificationList = nc.fetchNotifications( basicDriver );

        assertTrue( "Driver should have no notifications yet", notificationList.size() == 0 );

        // driverOne offers to complete the request
        rc.addDriver( newRequest, basicDriver ); // creates a notification for loggedInUser

        // driverOne is accepted as the driver
        rc.confirmDriver( newRequest, basicDriver ); // creates a notification for driverOne

        notificationList = nc.fetchNotifications( basicDriver );

        // wait for async tasks to finish loop.
        int pass = 0;
        while( notificationList.size() == 0 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( basicDriver );
            pass++;
            if (pass > 5) { break; }
        }

        //nc.clearAllNotifications( driverOne );

        assertTrue( "The driver should have one and only one notification.",
                notificationList.size() == 1);
    }

    /**
     * Test5 to test the confirmation of a driver
     * (US 05.03.01) As a driver, I want to see if my acceptance was accepted.
     *  Will be added later.
     */
    public void testConfirmation() {

    }
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
}
