package comcmput301f16t01.github.carrier;

import android.location.Location;
import android.provider.Settings;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;

/**
 * Created by meind on 2016-10-11.
 */

public class StatusTests extends ApplicationTest {

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusOpen() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        rc.addRequest(request);
        Assert.assertEquals("The status of the request should be OPEN",
                Request.OPEN, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusAccepted() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        CarrierLocation car = new CarrierLocation(445.67,44.6);
        System.out.print(car.getLatLong());

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(32.2,43.2), new CarrierLocation(117.6,32.5), "");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        Assert.assertEquals("The status of the request should be OFFERED",
                Request.OFFERED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusConfirmed() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        Assert.assertEquals("The status of the request should be CONFIRMED",
                Request.CONFIRMED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusComplete() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        rc.completeRequest(request);
        Assert.assertEquals("The status of the request should be COMPLETE",
                Request.COMPLETE, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusPaid() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        rc.completeRequest(request);
        rc.payForRequest(request);
        Assert.assertEquals("The status of the request should be PAID",
                Request.PAID, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void teststatusCancelled() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        RequestController rc = new RequestController();
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.confirmDriver(request, driver);
        //can not be paid for or completed to be cancelled
        //rc.completeRequest(request);
        //rc.payForRequest(request);
        rc.cancelRequest(rider, request);
        Assert.assertEquals("The status of the request should be CANCELLED",
                Request.CANCELLED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    
}
