package comcmput301f16t01.github.carrier;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestControllerTest {
    @Test
    public void addRequestTest() {
        // TODO write tests to test adding a request via elastic search
    }

    @Test
    public void cancelRequestTest(){
        RequestController rc = new RequestController();
        User user = new User("Name");
        // Create different types of requests
        Request cancelledRequest = new Request(user, new Location(), new Location());
        cancelledRequest.setStatus(Request.CANCELLED);
        Request openRequest = new Request(user, new Location(), new Location());
        Request paidRequest = new Request(user, new Location(), new Location());
        paidRequest.setStatus(Request.PAID);
        Request completedRequest = new Request(user, new Location(), new Location());
        completedRequest.setStatus(Request.COMPLETE);
        // Add the different requests.
        rc.addRequest(openRequest);
        rc.addRequest(cancelledRequest);
        rc.addRequest(paidRequest);
        rc.addRequest(completedRequest);
        // Cancel the different reqeusts
        rc.cancelRequest(user, openRequest);
        rc.cancelRequest(user, paidRequest);
        rc.cancelRequest(user,cancelledRequest);
        rc.cancelRequest(user, completedRequest);
        // Check that the expected behavior happened.
        assertTrue("openRequest not cancelled", openRequest.getStatus() == Request.CANCELLED);
        assertTrue("paidRequest changed when it shouldn't have.", paidRequest.getStatus() == Request.PAID);
        assertTrue("cancelledRequest changed when it shouldn't have", cancelledRequest.getStatus() == Request.CANCELLED);
        assertTrue("completedRequest changed when it shouldn't have.", completedRequest.getStatus() == Request.COMPLETE);
    }
}
