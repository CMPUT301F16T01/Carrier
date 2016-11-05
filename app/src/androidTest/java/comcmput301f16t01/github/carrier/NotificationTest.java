package comcmput301f16t01.github.carrier;

import java.util.Collections;
import java.util.Date;

import comcmput301f16t01.github.carrier.MockObjects.MockNotification;
import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Notifications.NotificationList;

public class NotificationTest extends ApplicationTest {
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

    /**
     * Tests that Collection.sort( notificationList ) correctly sorts notifications in a expected
     * way.
     * That is, for notifications that are equal, they should be sorted on their date.
     * Otherwise notifications that are unread come before read/seen notifications
     *
     * Uses a MockNotification to setDates
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

    /**
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

        rc.addRequest( newRequest );

        NotificationList notificationList = nc.fetchNotifications( loggedInUser.getUsername() );
        assertTrue( "There should be no notifications for the user yet",
                0 == notificationList.size() );

        rc.addDriver( newRequest, driverOne ); // adding a driver should initiate a notification

        notificationList = nc.fetchNotifications( loggedInUser.getUsername() );

        System.out.print( notificationList.size() );

        assertTrue( "There should be a notification for the user now",
                notificationList.size() != 0 );
    }
}
