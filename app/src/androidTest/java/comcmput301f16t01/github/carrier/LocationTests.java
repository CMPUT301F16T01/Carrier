package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Notifications.ElasticNotificationController;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

public class LocationTests extends ApplicationTest {
    // University of Alberta, Edmonton
    private final double latitude1 = 53.5232;
    private final double longitude1 = 113.5263;

    // somewhere in London, Ontario
    private final double latitude2 = 42.9870;
    private final double longitude2 = 81.2432;

    private final User loggedInUser = new User( "notifTestUser", "notify@email.com", "888-999-1234" );
    private final User driverOne = new User( "notifTestDriver", "notifyYou@email.com", "0118-99-112" );

    // Set up a test user to receive notifications
    private void setUpUser() {
        String result = UserController.createNewUser( loggedInUser.getUsername(),
                loggedInUser.getEmail(),
                loggedInUser.getPhone() );

        if (result == null) {
            System.out.print( "null line" );
        }

        Assert.assertTrue( "Failed to log in for test.", UserController.logInUser( loggedInUser.getUsername() ) );
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

        UserController.logOutUser();

        super.tearDown();
    }

    /**
     * TEST1
     *
     * Tests that the latitude and longitude values of start and end locations added to a
     * request are added correctly.
     */
    public void testLatLongCorrect() {
        setUpUser();

        // gets current location
        CarrierLocation startLocation = new CarrierLocation();
        CarrierLocation endLocation = new CarrierLocation();

        // user moves pin for start location
        startLocation.setLatitude(latitude1);
        startLocation.setLongitude(longitude1);
        endLocation.setLatitude(latitude2);
        endLocation.setLongitude(longitude2);

        Request request = new Request(loggedInUser, startLocation, endLocation, "");

        Assert.assertEquals("Start location latitude should match", startLocation.getLatitude(), request.getStart().getLatitude());
        Assert.assertEquals("Start location longitude should match", startLocation.getLongitude(), request.getStart().getLongitude());

        Assert.assertEquals("End location latitude should match", endLocation.getLatitude(), request.getEnd().getLatitude());
        Assert.assertEquals("End location longitude should match", endLocation.getLongitude(), request.getEnd().getLongitude());
    }

    /**
     * TEST2
     *
     * Tests that the start and end locations added to a request are added correctly.
    */
    public void testLocationCorrect() {
        setUpUser();

        CarrierLocation startLocation = new CarrierLocation();
        CarrierLocation endLocation = new CarrierLocation();
        startLocation.setLatitude(latitude1);
        startLocation.setLongitude(longitude1);
        endLocation.setLatitude(latitude2);
        endLocation.setLongitude(longitude2);
        Request request = new Request(loggedInUser, startLocation, endLocation, "");

        CarrierLocation start = request.getStart();
        CarrierLocation end = request.getEnd();

        // assert that the start locations match
        Assert.assertEquals("The start locations should match", startLocation, start);
        // assert that the end locations match
        Assert.assertEquals("The end locations should match", endLocation, end);
    }

    /**
     * TEST3
     *
     * Tests that the start and end addresses (including short addresses) added to a request
     * are added correctly.
     */
    public void testAddresses() {
        setUpUser();

        String startAddress = "11390 87 Avenue Northwest\nEdmonton, AB T6G 2T9\nCanada";
        String startShortAddress = "11390 87 Avenue Northwest";
        String endAddress = "8770 170 Street Northwest\nEdmonton, AB T5T 4V4\nCanada";
        String endShortAddress = "8770 170 Street Northwest";

        CarrierLocation startLocation = new CarrierLocation();
        CarrierLocation endLocation = new CarrierLocation();
        startLocation.setLatitude(latitude1);
        startLocation.setLongitude(longitude1);
        startLocation.setAddress(startAddress);
        startLocation.setShortAddress(startShortAddress);
        endLocation.setLatitude(latitude2);
        endLocation.setLongitude(longitude2);
        endLocation.setAddress(endAddress);
        endLocation.setShortAddress(endShortAddress);

        Request request = new Request(loggedInUser, startLocation, endLocation, "");

        // assert that the start addresses match
        Assert.assertEquals("The start addresses should match", startAddress, request.getStart().getAddress());
        // assert that the start short addresses match
        Assert.assertEquals("The start short addresses should match", startShortAddress, request.getStart().getShortAddress());
        // assert that the end locations match
        Assert.assertEquals("The end addresses should match", endAddress, request.getEnd().getAddress());
        // assert that the end locations match
        Assert.assertEquals("The end short addresses should match", endShortAddress, request.getEnd().getShortAddress());
    }
}
