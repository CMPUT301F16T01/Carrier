package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.net.wifi.WifiManager;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Users.ElasticUserController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Tests a driver caching requests from elastic search and then being able to search these
 * requests while offline. If the driver wants to make an offer on a request, they can also
 * do that while offline, it will be queued, and then made (if possible) when they go back online.
 */
public class OfflineMakeOfferTest extends ApplicationTest {
    private String keyword = "sufhepsjaucgelcmayfi";
    private User offlineTestUser = new User("offlineTestUser", "offline@offline.com", "00000000", "offline car");
    private User offlineTestUser2 = new User("offlineTestUser2", "offline2@offline.com", "00000001", "offline car2");
    private Request offlineTestRequest = new Request(offlineTestUser, new CarrierLocation(), new CarrierLocation(), keyword);
    private Request offlineTestRequest2 = new Request(offlineTestUser, new CarrierLocation(), new CarrierLocation(), keyword);

    /**
     * Clears requests created by searchTestUser and clears request offers made by searchTestDriver
     */
    protected void tearDown() throws Exception {
        // Ensure we are connected to Wifi
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        int pass = 0;
        while(!wifiManager.isWifiEnabled()) {
            Thread.sleep(1000);
            pass++;
            if(pass > 5) fail("Wifi could not connect in time");
        }

        ElasticRequestController.ClearRiderRequestsTask crt = new ElasticRequestController.ClearRiderRequestsTask();
        crt.execute( offlineTestUser.getUsername());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute(offlineTestUser.getUsername(), offlineTestUser2.getUsername());

        ElasticUserController.DeleteUserTask dut = new ElasticUserController.DeleteUserTask();
        dut.execute( offlineTestUser.getUsername(), offlineTestUser2.getUsername() );

        UserController.logOutUser();

        super.tearDown();
    }

    public void testCacheRequests() throws InterruptedException {
        // Ensure we are connected to Wifi
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        Thread.sleep(1000);

        // make the requests on behalf of offlineTestUser
        RequestController.addRequest(offlineTestRequest);
        RequestController.addRequest(offlineTestRequest2);
        Thread.sleep(1000);

        // log in driver, online log int wasn't working
        UserController.offlineLogInUser(offlineTestUser2.getUsername(), offlineTestUser2);
        Thread.sleep(1000);

        RequestController.searchByKeyword(keyword);
        Thread.sleep(1000);
        RequestList onlineSearchResults = RequestController.getResult();

        assertTrue("Online search failed: " + onlineSearchResults.toString(), onlineSearchResults.size() == 2);

        // disconnect from wifi
        wifiManager.setWifiEnabled(false);
        Thread.sleep(1000);

        RequestController.searchByKeyword(keyword);
        Thread.sleep(1000);
        RequestList offlineSearchResults = RequestController.getResult();

        assertTrue("Offline search failed", offlineSearchResults.size() == 2);
    }

}
