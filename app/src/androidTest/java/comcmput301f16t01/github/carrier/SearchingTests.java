package comcmput301f16t01.github.carrier;

import junit.framework.Assert;

import java.util.ArrayList;


import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Requests.RequestController;


public class SearchingTests extends ApplicationTest {
    // University of Alberta, Edmonton
    static final double latitude1 = 53.5232;
    static final double longitude1 = -113.5263;

    // somewhere in London, Ontario
    static final double latitude2 = 42.9870;
    static final double longitude2 = -81.2432;

    // somewhere in St. Albert, Alberta
    static final double latitude3 = 53.6305;
    static final double longitude3 = -113.6256;

    // somewhere in Edmonton, Alberta
    static final double latitude4 = 53.5444;
    static final double longitude4 = -113.4909;

    // TODO implement search by location
    /**
     * As a driver, I want to browse and search for open requests by geo-location.
     * Related: US 04.01.01
     */
    public void testDriverSearchByLocation() {
        ArrayList<Request> requests;

        User rider1 = new User("Mandy");
        CarrierLocation startLocation1 = new CarrierLocation();
        CarrierLocation endLocation1 = new CarrierLocation();
        endLocation1.setLatitude(latitude1);
        endLocation1.setLongitude(longitude1);
        Request request1 = new Request(rider1, startLocation1, endLocation1, "");

        User rider2 = new User("Abigail");
        CarrierLocation startLocation2 = new CarrierLocation();
        CarrierLocation endLocation2 = new CarrierLocation();
        endLocation2.setLatitude(latitude2);
        endLocation2.setLongitude(longitude2);
        Request request2 = new Request(rider2, startLocation2, endLocation2, "");

        User rider3 = new User("Alison");
        CarrierLocation startLocation3 = new CarrierLocation();
        CarrierLocation endLocation3 = new CarrierLocation();
        endLocation3.setLatitude(latitude3);
        endLocation3.setLongitude(longitude3);
        Request request3 = new Request(rider3, startLocation3, endLocation3, "");

        RequestController rc = new RequestController();
        rc.addRequest(request1);
        rc.addRequest(request2);
        rc.addRequest(request3);

        CarrierLocation driverLocation = new CarrierLocation();
        driverLocation.setLatitude(latitude4);
        driverLocation.setLongitude(longitude4);
        // this method should return a list of requests, sorted based on proximity of start location
        // for now I'm assuming there are limits on how far away a request can be to be included in this list
        // TODO would it be better to use ArrayList<Request> or requestList
        requests = rc.getSearchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 2 requests", requests.size() == 2);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request incorrect", request1, requests.get(0));
        Assert.assertEquals("2nd closest request incorrect", request3, requests.get(1));
        Assert.assertFalse("Search returned location out of range", requests.contains(request2));

        // TODO clarify our terminology...should this return open or accepted requests
        User driver = new User("Amber");
        rc.addDriver(request1, driver);
        rc.confirmDriver(request1, driver);

        // request1 should no longer be included in the search results
        requests = rc.getSearchByLocation(driverLocation);
        Assert.assertTrue("Search did not return 1 request", requests.size() == 1);
        // check that the requests are ordered properly
        Assert.assertEquals("Closest request incorrect", request3, requests.get(0));
        Assert.assertFalse("Search returned non-open request", requests.contains(request1));
        Assert.assertFalse("Search returned location out of range", requests.contains(request2));
    }

    /**
     * As a driver, I want to browse and search for open requests by keyword.
     * Related: US 04.02.01
     */
    public void testDriverSearchByKeyword() {
        User rider1 = new User("Mandy");
        CarrierLocation startLocation1 = new CarrierLocation();
        CarrierLocation endLocation1 = new CarrierLocation();
        endLocation1.setLatitude(latitude1);
        endLocation1.setLongitude(longitude1);
        String description1 = "Need to get to Whyte Ave for work";
        Request request1 = new Request(rider1, startLocation1, endLocation1, description1);

        User rider2 = new User("Abigail");
        CarrierLocation startLocation2 = new CarrierLocation();
        CarrierLocation endLocation2 = new CarrierLocation();
        endLocation2.setLatitude(latitude2);
        endLocation2.setLongitude(longitude2);
        String description2 = "Going home from the bar";
        Request request2 = new Request(rider2, startLocation2, endLocation2, description2);

        User rider3 = new User("Alison");
        CarrierLocation startLocation3 = new CarrierLocation();
        CarrierLocation endLocation3 = new CarrierLocation();
        endLocation3.setLatitude(latitude3);
        endLocation3.setLongitude(longitude3);
        String description3 = "Going home from school";
        Request request3 = new Request(rider3, startLocation3, endLocation3, description3);

        RequestController rc = new RequestController();
        rc.addRequest(request1);
        rc.addRequest(request2);
        rc.addRequest(request3);

        // this method should return a list of requests based on keywords in the request description
        String query1 = "home";
        String query2 = "whyte"; // should not be case-dependent
        String query3 = "downtown";

        // TODO should we allow the capability to search more than one keyword?
        rc.searchByKeyword(query1);
        Assert.assertTrue(String.valueOf(rc.getResult().size()), rc.getResult().size() == 2);
        rc.searchByKeyword(query2);
        Assert.assertTrue("Search did not return 1 request", rc.getResult().size() == 1);
        rc.searchByKeyword(query3);
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);

        User driver = new User("Amber");
        rc.addDriver(request1, driver);
        rc.confirmDriver(request1, driver);

        // request1 should no longer be included in search results
        rc.searchByKeyword(query1);
        Assert.assertTrue("Search did not return 2 requests", rc.getResult().size() == 2);
        rc.searchByKeyword(query2);
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);
        rc.searchByKeyword(query3);
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);

        rc.addDriver(request2, driver);
        rc.confirmDriver(request2, driver);

        // request1 and request2 should no longer be included in search results
        rc.searchByKeyword(query1);
        Assert.assertTrue("Search did not return 1 requests", rc.getResult().size() == 1);
        rc.searchByKeyword(query2);
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);
        rc.searchByKeyword(query3);
        Assert.assertTrue("Search returned requests", rc.getResult().size() == 0);
    }
}
