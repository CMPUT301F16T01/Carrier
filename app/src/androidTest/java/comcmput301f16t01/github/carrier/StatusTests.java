package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Users.User;

public class StatusTests extends ApplicationTest {

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusOpen() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        RequestController.addRequest(request);
        Assert.assertEquals("The status of the request should be OPEN",
                Request.Status.OPEN, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusAccepted() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        CarrierLocation car = new CarrierLocation(445.67,44.6);
        System.out.print(car.getLatLong());

        Request request = new Request(rider, new CarrierLocation(32.2,43.2), new CarrierLocation(117.6,32.5), "");

        RequestController.addRequest(request);
        RequestController.addDriver(request, driver);
        Assert.assertEquals("The status of the request should be OFFERED",
                Request.Status.OFFERED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusConfirmed() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        RequestController.addRequest(request);
        RequestController.addDriver(request, driver);
        RequestController.confirmDriver(request, driver);
        Assert.assertEquals("The status of the request should be CONFIRMED",
                Request.Status.CONFIRMED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusComplete() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        RequestController.addRequest(request);
        RequestController.addDriver(request, driver);
        RequestController.confirmDriver(request, driver);
        RequestController.completeRequest(request);
        Assert.assertEquals("The status of the request should be COMPLETE",
                Request.Status.COMPLETE, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusPaid() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        RequestController.addRequest(request);
        RequestController.addDriver(request, driver);
        RequestController.confirmDriver(request, driver);
        RequestController.completeRequest(request);
        RequestController.payForRequest(request);
        Assert.assertEquals("The status of the request should be PAID",
                Request.Status.PAID, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    /**
     * As a rider or driver, I want to see the status of a request that I am involved in
     * Related: US 02.01.01
     */
    public void testStatusCancelled() {
        User rider = new User("Mandy");
        User driver = new User("username2");

        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), "");

        RequestController.addRequest(request);
        RequestController.addDriver(request, driver);
        RequestController.confirmDriver(request, driver);
        //can not be paid for or completed to be cancelled
        //rc.completeRequest(request);
        //rc.payForRequest(request);
        RequestController.cancelRequest(request);
        Assert.assertEquals("The status of the request should be CANCELLED",
                Request.Status.CANCELLED, request.getStatus());

        ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
        rot.setMode( rot.MODE_USERNAME );
        rot.execute( rider.getUsername(), driver.getUsername() );
    }

    
}
