package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Created by kiete on 11/23/2016.
 */

/**
 * Tests offline functionality
 */
public class OfflineTests extends ApplicationTest {
    private User offlineTestUser = new User("offlineTestUser", "offline@offline.com", "00000000", "offline car");
    private User offlineTestUser2 = new User("offlineTestUser2", "offline2@offline.com", "00000001", "offline car2");
    private Request offlineTestRequest = new Request(offlineTestUser, new CarrierLocation(), new CarrierLocation());

    @Override
    protected void setUp() throws InterruptedException {
        // With the WiFi on, create the new user and add a request. These get put up on elastic search.
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        Thread.sleep(1000);
        //TODO: ask why this doesn't work, the tests can pass without them however
//        if (uc.findUser(offlineTestUser.getUsername()) == null) {

//            uc.createNewUser(offlineTestUser.getUsername(), offlineTestUser.getEmail(), offlineTestUser.getPhone(), offlineTestUser.getVehicleDescription());
//        Thread.sleep(1000);
////        }
////        if (uc.findUser(offlineTestUser2.getUsername()) == null) {
//            uc.createNewUser(offlineTestUser2.getUsername(), offlineTestUser2.getEmail(), offlineTestUser2.getPhone(), offlineTestUser2.getVehicleDescription());
//        Thread.sleep(1000);
//        }
        UserController.offlineLogInUser(offlineTestUser.getUsername(), offlineTestUser);
        Thread.sleep(1000);
        RequestController.addRequest(offlineTestRequest);
        Thread.sleep(1000);
        RequestController.addDriver(offlineTestRequest, offlineTestUser2);
        Thread.sleep(1000);

    }

    /**
     * Tests to see if the offline checker works
     * @throws InterruptedException
     */
    public void testOnlineChecker() throws InterruptedException {
        Thread.sleep(500);
        // I really don't know why but for some reason having wifiManager as an attribute throws a RuntimeException so it's at the top of every function instead :P.
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        // Go offline
        wifiManager.setWifiEnabled(false);
        Thread.sleep(500);
        boolean online = ConnectionChecker.isThereInternet();
        assertFalse("Still online", online);
    }

    /**
     * Tests to see if saving/loading driver requests while offline works and is consistent with online
     * @throws InterruptedException
     */
    public void testSavingLoadingOfflineDriverRequests() throws InterruptedException {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        // Go offline and load the rider requests from file
        wifiManager.setWifiEnabled(false);
        Thread.sleep(500);
        // While offline fetchAllRequestsWhereRider just loads file and store a copy
        RequestList offlineDriverOfferedList = RequestController.getOfferedRequests(offlineTestUser2);
        Thread.sleep(500);
        // Store the offline version of the request
        Request offlineVersion = offlineDriverOfferedList.get(0);

        // Repeat the same as above but go online instead.
        wifiManager.setWifiEnabled(true);
        Thread.sleep(500);
        // Store a copy of the online requestList
        RequestList onlineDriverOfferedList = RequestController.getOfferedRequests(offlineTestUser2);
        Thread.sleep(500);
        Request onlineVersion = onlineDriverOfferedList.get(0);

        // Test to see if the online and offline copies of offlineTestRequest are the same (elastic search and file are consistent)
        assertEquals("The online and offline requests are different", onlineVersion.getId(), offlineVersion.getId());
    }

    /**
     * Tests to see if saving/loading driver requests while offline works and is consistent with online
     * @throws InterruptedException
     */
    public void testSavingLoadingOfflineRiderRequests() throws InterruptedException {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        // With the WiFi on, create the new user and add a request. These get put up on elastic search.
        wifiManager.setWifiEnabled(true);

        // Go offline and load the rider requests from file
        wifiManager.setWifiEnabled(false);
        Thread.sleep(500);
        // While offline fetchAllRequestsWhereRider just loads file and store a copy
        RequestList offlineRequestList = RequestController.fetchAllRequestsWhereRider(UserController.getLoggedInUser());
        Thread.sleep(500);
        // Store the offline version of the request
        Request offlineVersion = offlineRequestList.get(0);

        // Repeat the same as above but go online instead.
        wifiManager.setWifiEnabled(true);
        Thread.sleep(500);
        // Store a copy of the online requestList
        RequestList onlineRequestList = RequestController.fetchAllRequestsWhereRider(UserController.getLoggedInUser());
        Thread.sleep(500);
        Request onlineVersion = onlineRequestList.get(0);

        // Test to see if the online and offline copies of offlineTestRequest are the same (elastic search and file are consistent)
        assertEquals("The online and offline requests are different", onlineVersion.getId(), offlineVersion.getId());
    }


}
