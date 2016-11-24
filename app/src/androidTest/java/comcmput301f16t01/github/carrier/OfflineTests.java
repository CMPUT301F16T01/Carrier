package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Created by kiete on 11/23/2016.
 */

public class OfflineTests extends ApplicationTest {
    private WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    private RequestController rc = new RequestController();
    private UserController uc = new UserController();
    private User OfflineTestUser = new User("OfflineTestUser", "offline@offline.com", "00000000", "offline car");
    private Request OfflineRequest = new Request(OfflineTestUser, new CarrierLocation(), new CarrierLocation());

    @Override
    protected void setUp() throws InterruptedException {
        this.wifiManager.setWifiEnabled(false);
    }

    @Override
    protected void tearDown() {
        this.wifiManager.setWifiEnabled(true);
    }

    public void testOnlineChecker() throws InterruptedException {
        boolean online = ConnectionChecker.isThereInternet();
        Log.i("wifi", ConnectionChecker.isThereInternet().toString());
//        Thread.sleep(1000);
        assertFalse("Still online", online);
    }

    public void testSavingOfflineRequests() {
        // With the WiFi on 
        this.wifiManager.setWifiEnabled(true);
        UserController.createNewUser(OfflineTestUser.getUsername(), OfflineTestUser.getEmail(), OfflineTestUser.getPhone(), OfflineTestUser.getVehicleDescription());
        rc.addRequest(OfflineRequest);
        rc.getRiderInstance().clear();
        rc.fetchAllRequestsWhereRider(OfflineTestUser);

    }



}
