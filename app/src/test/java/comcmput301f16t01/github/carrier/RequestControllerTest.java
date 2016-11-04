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
}
