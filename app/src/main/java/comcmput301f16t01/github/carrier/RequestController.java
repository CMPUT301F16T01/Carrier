package comcmput301f16t01.github.carrier;

import android.content.Context;

import java.util.ArrayList;

/**
 * Singleton Pattern
 * We will be using RequestController as a middleman between the activity and elasticController.
 * * modifies/returns a RequestList model
 * * @see Request
 * * @see RequestList
 *
 */
public class RequestController {
    private static ArrayList<Request> requestList = null;
    private Context saveContext = null;

    /**
     * Since we are using elastic search to get the request list.
     */
    public RequestController() {
        if (requestList == null) {
            requestList = new ArrayList<Request>();
        }
    }

    /**
     * @return the RequestList held by this controller.
     */
    public static ArrayList<Request> getInstance() {
        if (requestList == null) {
            requestList = new ArrayList<Request>();
        }
        return requestList;
    }

    /**
     * @param request
     */
    public void addRequest(Request request) {
    }

    /**
     * @param rider
     * @return
     */
    public ArrayList<Request> getRequests(User rider) {
        return requestList;
    }

    /**
     *
     */
    public void reset() {
    }

    public void cancelRequest(User rider, Request request) {
        ElasticRequestController.CancelRequest cancelRequestTask = new ElasticRequestController.CancelRequest();
        cancelRequestTask.execute(request);

    }

    /**
     * Is used to add a driver to a request.
     * @param request The request we are modifying
     * @param driver the driver that is being added as a driver for the request.
     */
    public void addDriver(Request request, User driver) {
    }

    /**
     * Is used to show that the user has accepted the provided driver. The accepted driver should
     * have been added with addDriver() before being accepted.
     * @param request The request that is being modified
     * @param driver The driver that is being accepted
     */
    public void confirmDriver(Request request, User driver) {
    }

    public void completeRequest(Request request) {
    }

    public void payForRequest(Request request) {
    }


    /**
     * Is used to search open requests by a location submitted by a driver. The search results
     * should return an ordered list with priority given to requests that begin in closest
     * proximity to the driver-submitted location. Search results are limited to a range of
     * locations that are within a reasonable distance to the driver-submitted location.
     * @param location The location submitted by the user to query by
     * @return Returns the search results
     */
    public ArrayList<Request> searchByLocation(Location location) {
        return new ArrayList<>();
    }

    /**
     * Is used to search open requests by a keyword query submitted by a driver. The query string
     * should not be case-dependent. The search matches the query string to the request description
     * to determine whether a request should be included in the search results or not.
     * @param query
     * @return Returns the search results
     */
    // TODO consider having these search results sorted by location and limited to a location range
    //   this will require us to compare against the driver's current location...how do we get that?
    public ArrayList<Request> searchByKeyword(String query) {
        return new ArrayList<>();
    }

    /**
     * Is used to provide a driver with a list of all open requests.
     * @return An array list of open requests.
     */
    public ArrayList<Request> getOpenRequests(){
        return new ArrayList<Request>();
    }

    /**
     * Is used to provide a driver with a list of the requests that the driver has offered to give a ride.
     * @param driver The driver who is requesting the list of the requests they have offered
     *               but the rider has no confirmed their choice in driver.
     * @return An ArrayList of requests that the driver has offered to give a ride on.
     */
    public ArrayList<Request> getOfferedRequests(User driver) {
        return  new ArrayList<Request>();
    }

    /** Will be used to get a list of drivers that the user can choose to confirm as there driver
     *
     * @param request The request that is being queried for a list of drivers.
     * @return An ArrayList of drivers that have offered to give a ride on the request.
     */
    public ArrayList<User> getDrivers(Request request) {
        return new ArrayList<User>();
    }

    /**
     * Provides a list of requests that are available for a driver to accept.
     * @return an arraylist of requests that a driver is able to accept.
     * @See getOpenRequests
     */
    public ArrayList<Request> getAvailableRequests(){
        return new ArrayList<Request>();
    }


    /**
     * This is used to set the Request description.
     * @param request The request that we are modifying
     * @param description The description that we are setting it to.
     */
    public void setRequestDescription(Request request, String description) {
    }
}