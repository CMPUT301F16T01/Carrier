package comcmput301f16t01.github.carrier;

import org.junit.Test;
import static org.junit.Assert.*;

public class RequestControllerTest {
    @Test
    public void addRequestTest() {
        // TODO write tests to test adding a request via elastic search
    }

    @Test
    public void addOfferingDriverTest() {
        RequestController rc = new RequestController();
        User rider = new User("Rider");
        User offeringDriver = new User("Offering Driver");
        RequestList rl = RequestController.getInstance();
        Request request = new Request(rider, new Location(), new Location());
        rl.add(request);
        rc.addDriver(request, offeringDriver);
        assertTrue("Driver is not added as offering to the request.", request.getOfferedDrivers().contains(offeringDriver));
    }
    @Test
    public void getOfferedRequestsTest(){
        RequestController rc = new RequestController();
        User rider = new User("Rider");
        User offeringDriver = new User("Offering Driver");
        RequestList rl = RequestController.getInstance();
        Request request = new Request(rider, new Location(), new Location());
        rl.add(request);
        // Test that the empty requests work
        assertTrue("Driver has offered request when they shouldn't", rc.getOfferedRequests(offeringDriver).size() == 0);
        rc.addDriver(request, offeringDriver);
        // Make sure the driver is added the request
        assertTrue("Get offered requests is not returning the correct request", rc.getOfferedRequests(offeringDriver).contains(request));
        // Make sure that the driver is only added to one request.
        assertTrue("The driver is added to more than one reqeust", rc.getOfferedRequests(offeringDriver).size() == 1);
    }

}
