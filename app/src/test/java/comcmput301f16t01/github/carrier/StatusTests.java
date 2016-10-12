package comcmput301f16t01.github.carrier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by meind on 2016-10-11.
 */

public class StatusTests {

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusOpenTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        assertEquals("The status of the request should be OPEN",
                Request.OPEN, request.getStatus());
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusAcceptedTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        rc.addDriver(request, driver);
        assertEquals("The status of the request should be OFFERED",
                Request.OFFERED, request.getStatus());
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusConfirmedTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        assertEquals("The status of the request should be CONFIRMED",
                Request.CONFIRMED, request.getStatus());
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusCompleteTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        rc.completeRequest(request);
        assertEquals("The status of the request should be COMPLETE",
                Request.COMPLETE, request.getStatus());
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusPaidTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        rc.completeRequest(request);
        rc.payForRequest(request);
        assertEquals("The status of the request should be PAID",
                Request.PAID, request.getStatus());
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    @Test
    public void statusCancelledTest() {
        Rider rider = new Rider("Mandy");
        Driver driver = new Driver("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new Location(), new Location());

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        //can not be paid for or completed to be cancelled
        //rc.completeRequest(request);
        //rc.payForRequest(request);
        rc.cancelRequest(rider, request);
        assertEquals("The status of the request should be CANCELLED",
                Request.CANCELLED, request.getStatus());
    }

    
}
