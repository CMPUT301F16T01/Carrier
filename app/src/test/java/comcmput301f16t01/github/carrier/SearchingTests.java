package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SearchingTests {
    // University of Alberta, Edmonton
    static final double latitude1 = 53.5232;
    static final double longitude1 = 113.5263;

    // somewhere in London, Ontario
    static final double latitude2 = 42.9870;
    static final double longitude2 = 81.2432;

    // somewhere in Calgary, Alberta
    static final double latitude3 = 51.0486;
    static final double longitude3 = 114.0708;

    // somewhere in Edmonton, Alberta
    static final double latitude4 = 53.5444;
    static final double longitude4 = 113.4909;

    @After
    public void clean() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     * As a driver, I want to browse and search for open requests by geo-location.
     * Related: US 04.01.01
     */
    @Test
    public void testDriverSearchByLocation() {
        Rider rider1 = new Rider("Mandy");
        Location startLocation1 = new Location();
        Location endLocation1 = new Location(latitude1, longitude1);
        Request request1 = new Request(rider1, startLocation1, endLocation1, "");

        Rider rider2 = new Rider("Abigail");
        Location startLocation2 = new Location();
        Location endLocation2 = new Location(latitude2, longitude2);
        Request request2 = new Request(rider2, startLocation2, endLocation2, "");

        Rider rider3 = new Rider("Alison");
        Location startLocation3 = new Location();
        Location endLocation3 = new Location(latitude3, longitude3);
        Request request3 = new Request(rider3, startLocation3, endLocation3, "");

        RequestController rc = new RequestController();
        rc.addRequest(request1);
        rc.addRequest(request2);
        rc.addRequest(request3);

        Location driverLocation = new Location(latitude4, longitude4);
        // this method should return a list of requests, sorted based on proximity of start location
        // for now I'm assuming there are limits on how far away a request can be to be included in this list
        // TODO would it be better to use ArrayList<Request> or requestList
        ArrayList<Request> requests = rc.searchByLocation(driverLocation);
        assertTrue("Search did not return 2 requests", requests.size() == 2);
        // check that the requests are ordered properly
        assertEquals("Closest request incorrect", request1, requests.get(0));
        assertEquals("2nd closest request incorrect", request3, requests.get(1));
        assertFalse("Search returned location out of range", requests.contains(request2));
    }

    /**
     * As a driver, I want to browse and search for open requests by keyword.
     * Related: US 04.02.01
     */
    @Test
    public void testDriverSearchByKeyword() {
        Rider rider1 = new Rider("Mandy");
        Location startLocation1 = new Location();
        Location endLocation1 = new Location(latitude1, longitude1);
        String description1 = "Need to get to Whyte Ave for work";
        Request request1 = new Request(rider1, startLocation1, endLocation1, description1);

        Rider rider2 = new Rider("Abigail");
        Location startLocation2 = new Location();
        Location endLocation2 = new Location(latitude2, longitude2);
        String description2 = "Going home from the bar";
        Request request2 = new Request(rider2, startLocation2, endLocation2, description2);

        Rider rider3 = new Rider("Alison");
        Location startLocation3 = new Location();
        Location endLocation3 = new Location(latitude3, longitude3);
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
        ArrayList<Request> requests1 = rc.searchByKeyword(query1);
        assertTrue("Search did not return 2 requests", requests1.size() == 2);

        ArrayList<Request> requests2 = rc.searchByKeyword(query2);
        assertTrue("Search did not return 1 request", requests2.size() == 1);

        ArrayList<Request> requests3 = rc.searchByKeyword(query3);
        assertTrue("Search returned requests", requests3.size() == 0);
    }
}
