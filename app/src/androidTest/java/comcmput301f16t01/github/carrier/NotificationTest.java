package comcmput301f16t01.github.carrier;

import java.util.Collections;
import java.util.Date;

import comcmput301f16t01.github.carrier.MockObjects.MockNotification;
import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Notifications.Notification;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Notifications.NotificationList;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;

// TODO could probably use a mock RequestController, since it always posts test requests to elastic search

/**
 * Test Suite for Notifications.
 * Test List:
 *      1) Sorting of notifications
 *      2) Clearing notifications from Elastic Search
 *      3) Getting a notification when a driver offers to accept a rider's request (Use case test)
 *      4) Getting a notification when a driver's offer has been accepted (Use case test)
 *      5) Marking a notification as read
 *      6) Deleting more than 10 notifications (testing the 10 return limit of queries)
 *
 * @see NotificationController
 * @see Notification
 */
public class NotificationTest extends ApplicationTest {
    private User loggedInUser = new User( "notifTestUser", "notify@email.com", "888-999-1234" );
    private User driverOne = new User( "notifTestDriver", "notifyYou@email.com", "0118-99-112" );
    private User anotherUser = new User( "notifThirdUser", "notifyMe@gmail.com", "887112233" );

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

    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    // TODO convert to a full out AsyncWait method to generalize waiting for .size() == x tasks?
    private void chillabit( long time ) {
        try {
            Thread.sleep( time );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /** TEST1 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that Collection.sort( notificationList ) correctly sorts notifications in a expected
     * way.
     * That is, for notifications that are equal, they should be sorted on their date.
     * Otherwise notifications that are unread come before read/seen notifications
     *
     * Uses a MockNotification to set date
     * @see MockNotification
     */
    public void testNotificationSorting() {
        Request requestOne = new Request( loggedInUser, new Location(), new Location(),
                "testNotificationSorting Desc : requestOne" );
        Request requestTwo = new Request( loggedInUser, new Location(), new Location(),
                "testNotificationSorting Desc: requestTwo" );

        MockNotification a = new MockNotification( loggedInUser, requestOne );
        MockNotification b = new MockNotification( loggedInUser, requestTwo );
        b.setDate( a.getDate() );
        b.setRead( true );

        NotificationList notificationList = new NotificationList();
        notificationList.add(b);
        notificationList.add(a);

        Collections.sort( notificationList );

        // test that read is in order (unread first)
        // a > b
        assertEquals( "Could not sort by read priority", a, notificationList.get(0) );
        assertEquals( "Unexpected error...", b, notificationList.get(1) );

        MockNotification c = new MockNotification( loggedInUser, requestOne );
        MockNotification d = new MockNotification( loggedInUser, requestOne );

        a.setDate( new Date( 500 ) );
        b.setDate( new Date( 1000 ) ); // we want to test an read one with the newest date
        c.setDate( new Date( 400 ) );
        d.setDate( new Date( 600 ) );

        // d is newer than c => d > c
        notificationList.clear();
        notificationList.add(c);
        notificationList.add(d);
        Collections.sort( notificationList );

        assertEquals( "Could not sort by date", d, notificationList.get(0) );
        assertEquals( "Unexpected error...", c, notificationList.get(1) );

        d.setRead( true );
        // We want to check that the order is always newDate > oldDate for similar .isRead()
        // and that the newestDate being in a read Notification has no effect on the unread > read order
        // in this case: a > c > b > d
        notificationList.clear();
        notificationList.add( c );
        notificationList.add( d );
        notificationList.add( a );
        notificationList.add( b );
        Collections.sort( notificationList );

        assertEquals( "Could not sort on both rules", a, notificationList.get(0) );
        assertEquals( "Could not sort on both rules", c, notificationList.get(1) );
        assertEquals( "Could not sort on both rules", b, notificationList.get(2) );
        assertEquals( "Could not sort on both rules", d, notificationList.get(3) );
    }


    /** TEST2 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Tests that clearing notifications actually works inside the Notification Controller
     */
    public void testClearingNotifications() {
        Request requestOne = new Request( loggedInUser, new Location(), new Location(),
                "testClearingNotifications1" );
        requestOne.setId("testClearing1");
        Request requestTwo = new Request( loggedInUser, new Location(), new Location(),
                "testClearingNotifications2" );
        requestTwo.setId("testClearing2");

        // Add a few notifications to loggedInUser
        NotificationController nc = new NotificationController();
        nc.addNotification( loggedInUser, requestOne );
        nc.addNotification( loggedInUser, requestTwo );
        nc.addNotification( loggedInUser, requestOne );

        NotificationList notificationList = nc.fetchNotifications( loggedInUser );

        // Make sure this test is useful by ensuring there are notfications now
        assertTrue( "There should be at least one notification so far", notificationList.size() != 0 );

        // Try to clear them
        nc.clearAllNotifications( loggedInUser );

        /*
         * Dealing with Async tasks means we need to wait for them to finish.
         */
        int pass = 0;
        while( notificationList.size() != 0 ) {
            notificationList = nc.fetchNotifications( loggedInUser );
            chillabit( 1000 );
            pass++;
            if (pass > 5) { break; }
        }

        // Test that clearing the notifications was successful!
        assertTrue( "We should have cleared all the notifications.", notificationList.size() == 0 );
    }


    /** TEST3 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Test that a user receives a notification when a driver makes an offer on their request
     */
    public void testRiderGetNotified() {
        assertTrue( "You must at least have network connection to run this test",
                ConnectionChecker.isConnected( getContext() ) );

        setUpUser();

        NotificationController nc = new NotificationController();
        RequestController rc = new RequestController();

        nc.clearAllNotifications( loggedInUser );

        Request newRequest = new Request( UserController.getLoggedInUser(),
                new Location(), new Location(), "testRiderGetNotified" );

        // Unnecessary clutter for request elastic search, and irrelevant to this test (?)
        rc.addRequest( newRequest );

        NotificationList notificationList = nc.fetchNotifications( loggedInUser );
        assertTrue( "There should be no notifications for the user yet",
                0 == notificationList.size() );

        rc.addDriver( newRequest, driverOne ); // adding a driver should initiate a notification

        /*
         * Because this task is Async, we need to wait for the other tasks to complete
         * before we can be sure that this works... not sure if using a volatile variable would help
         * here.
         */
        int pass = 0;
        while (notificationList.size() == 0) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( loggedInUser );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be a notification for the user now",
                notificationList.size() != 0 );
    }


    /** TEST4 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Test that a user receives a notification when a rider accepts a driver's offer on a request
     *
     * This test also confirms that notifications to two different users go to the specific user and
     * no one else
     */
    public void testDriverGetNotified() {
        assertTrue( "You must at least have network connection to run this test",
                ConnectionChecker.isConnected( getContext() ) );

        setUpUser();

        NotificationController nc = new NotificationController();
        RequestController rc = new RequestController();

        nc.clearAllNotifications( driverOne );

        Request newRequest = new Request( UserController.getLoggedInUser(),
                new Location(), new Location(), "testDriverGetNotified" );

        // Unnecessary clutter for request elastic search, and irrelevant to this test (?)
        rc.addRequest( newRequest );

        NotificationList notificationList = nc.fetchNotifications( driverOne );

        assertTrue( "Driver should have no notifications yet", notificationList.size() == 0 );

        // driverOne offers to complete the request
        rc.addDriver( newRequest, driverOne ); // creates a notification for loggedInUser

        // driverOne is accepted as the driver
        rc.confirmDriver( newRequest, driverOne ); // creates a notification for driverOne

        notificationList = nc.fetchNotifications( driverOne );

        // wait for async tasks to finish loop.
        int pass = 0;
        while( notificationList.size() == 0 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( driverOne );
            pass++;
            if (pass > 5) { break; }
        }

        //nc.clearAllNotifications( driverOne );

        assertTrue( "The driver should have one and only one notification.",
                notificationList.size() == 1);
    }


    /** TEST5 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Test that we can mark a notification as read.
     * Also tests that it does not set all other notifications to read.
     */
    public void testMarkingNotificationAsRead() {
        Request requestOne = new Request( loggedInUser, new Location(), new Location(),
                "testMarkingNotificationAsRead1" );
        requestOne.setId("testMarkingNotificationAsRead1");
        Request requestTwo = new Request( loggedInUser, new Location(), new Location(),
                "testMarkingNotificationAsRead2" );
        requestTwo.setId("testMarkingNotificationAsRead2");

        NotificationController nc = new NotificationController();

        nc.clearAllNotifications( loggedInUser );

        NotificationList notificationList = nc.fetchNotifications( loggedInUser );

        // wait for async tasks to finish (no requests should be present now)
        int pass = 0;
        while( notificationList.size() != 0 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( loggedInUser );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "Notifications not clearing properly", notificationList.size() == 0 );

        nc.addNotification( loggedInUser, requestOne );
        nc.addNotification( loggedInUser, requestTwo );

        notificationList = nc.fetchNotifications( loggedInUser );

        // wait for async tasks to finish (two requests should be present)
        pass = 0;
        while( notificationList.size() != 2 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( loggedInUser );
            pass++;
            if (pass > 10) { break; }
        }

        assertTrue( "There should be two notifications", notificationList.size() == 2);
        assertFalse( "Both notifications should be unread", notificationList.get(0).isRead() );
        assertFalse( "Both notifications should be unread", notificationList.get(1).isRead() );

        String rememberReadID = notificationList.get(0).getID();
        nc.markNotificationAsRead( notificationList.get(0) );

        notificationList = nc.fetchNotifications( loggedInUser );

        // wait for async tasks to finish (two requests with different isRead status
        pass = 0;
        while( true ) {
            if ( notificationList.get(0).isRead() != notificationList.get(1).isRead() ) {
                break;
            }
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( loggedInUser );
            pass++;
            if (pass > 5) { break; }
        }

        // Assertions based on which one is marked as "read"
        if (notificationList.get(0).isRead()) {
            assertFalse( "One of the notifications should be false", notificationList.get(1).isRead() );
            //assertEquals( "The ID that was set to true is not the same",
            //        rememberReadID, notificationList.get(1).getID() );
        } else {
            assertTrue( "One of the notifications should be true", notificationList.get(1).isRead() );
            //assertEquals( "The ID that was set to true is not the same",
            //        rememberReadID, notificationList.get(0).getID() );
        }
    }

    /** TESTt * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Test that we can delete or get more than 10 notifications in one call
     */
    public void testDeletingManyNotification() {
        Request requestOne = new Request( anotherUser, new Location(), new Location(),
                "testDeletingManyNotifications1");

        NotificationController nc = new NotificationController();

        nc.clearAllNotifications( anotherUser );

        NotificationList notificationList = nc.fetchNotifications( anotherUser );

        // wait for async tasks to finish (All requests should be cleared)
        int pass = 0;
        while( notificationList.size() != 0 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( anotherUser );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be no notification", notificationList.size() == 0 );

        for( int i = 0; i < 15; i++ ) {
            nc.addNotification( anotherUser, requestOne );  //
        }

        notificationList = nc.fetchNotifications( anotherUser );

        // wait for async tasks to finish (need 15 or more unique requests)
        pass = 0;
        while( notificationList.size() != 15 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( anotherUser );
            pass++;
            if (pass > 5) { break; }
        }

        int numFound = notificationList.size();
        assertTrue( "There should be 15 notifications, found: " + Integer.toString( numFound ),
                numFound == 15 );

        nc.clearAllNotifications( anotherUser );

        notificationList = nc.fetchNotifications( anotherUser );

        // wait for async tasks to finish (All requests should be cleared)
        pass = 0;
        while( notificationList.size() != 0 ) {
            chillabit( 1000 );
            notificationList = nc.fetchNotifications( anotherUser );
            pass++;
            if (pass > 5) { break; }
        }

        assertTrue( "There should be no notification", notificationList.size() == 0 );

    }
}
