package comcmput301f16t01.github.carrier;

import android.content.Context;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Notifications.Notification;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;

/**
 * Uses a singleton pattern to query and get results of requests.
 *
 * Typical use:
 *      rc.searchType( params );            // Change the singleton's information
 *      requestList = rc.getResults();      // Get the results of that search (global to program)
 *
 * Or use one of the getX() functions to get immediate info.
 */
public class RequestController {
    private static ArrayList<Request> requestList;
    private Context saveContext = null;
    
    /**
     * Prevents errors when a RequestController is initialized and methods that require requestList
     * to not be null (i.e. getResult() )
     */
    public RequestController() {
        if (requestList == null) {
            requestList = new ArrayList<Request>();
        }
    }

    /** Adds a request to elastic search. */
    public String addRequest(Request request) {
        if(request.getStart() == null || request.getEnd() == null) {
            return "You must first select a start and end location";
        } else if (request.getFare() == -1) {
            return "You must first estimate the fare";
        } else {
            ElasticRequestController.AddRequestTask art = new ElasticRequestController.AddRequestTask();
            art.execute(request);
            return null;
        }
    }

    /** Clears information in the singleton, not exactly necessary */
    // TODO check the necessity of this function.
    public void clear() {
        requestList = new RequestList();
    }

    // TODO Why does this need a rider? You can cancel a request just knowing the request.
    public void cancelRequest(User rider, Request request) {
    }

    /**
     * Is used to add a driver to a request.
     *
     * @param request The request we are modifying
     * @param driver  the driver that is being added as a driver for the request.
     */
    public void addDriver(Request request, User driver) {

        // TODO Elastic Requests...
        // only on success should we send out a notification!
        NotificationController nc = new NotificationController();
        nc.addNotification( request.getRider(), request );
        // TODO check for notification success?
    }

    /**
     * Is used to show that the user has accepted the provided driver. The accepted driver should
     * have been added with addDriver() before being accepted.
     *
     * @param request The request that is being modified
     * @param driver  The driver that is being accepted
     */
    public void confirmDriver(Request request, User driver) {

        // TODO Elastic Requests...
        // only on success should we send out a notification!
        NotificationController nc = new NotificationController();
        nc.addNotification( driver, request );
        // TODO check for notification success?
    }

    public void completeRequest(Request request) {
    }

    public void payForRequest(Request request) {
    }

    /**
     * Search requests by the keyword, will set it so the singleton contains the information for
     * this query. Use getResults() to get the information.
     * @param keyword
     */
    public void searchByKeyword(String keyword) {
        //ElasticRequestController.SearchByKeywordTask sbkt = new ElasticRequestController.SearchByKeywordTask();
        //sbkt.execute(keyword)
        //requestList = sbkt.get();
    }

    /**
     * Search requests by a location. This sets it so the singleton contains the information for
     * this query. Use getResults() to get the information.
     */
    public void searchByLocation( /* location parameters? */ ) {

    }

    /**
     * Is used to provide a driver with a list of the requests that the driver has offered to give a ride.
     *
     * @param driver The driver who is requesting the list of the requests they have offered
     *               but the rider has no confirmed their choice in driver.
     * @return An ArrayList of requests that the driver has offered to give a ride on.
     */
    public ArrayList<Request> getOfferedRequests(User driver) {
        return new ArrayList<Request>();
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * *    DEPRECATED FUNCTIONS   * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Deprecated: use the void getSearchByLocation
     */
    @Deprecated
    public ArrayList<Request> getSearchByLocation(Location location) {
        return new ArrayList<>();
    }

    /**
     * Deprecated: This is literally built into a Request. (see the ArrayList of offered drivers)
     */
    @Deprecated
    public ArrayList<User> getDrivers(Request request) {
        return new ArrayList<User>();
    }

    /**
     * Deprecated: There are several other functions that do this. Also, try to only use
     * getSearchByKeyword or getSearchByLocation
     */
    @Deprecated
    public ArrayList<Request> getAvailableRequests() {
        return new ArrayList<Request>();
    }

    /** Get the results of a searchByKeyword or a getSearchByLocation query. */
    public RequestList getResult() {
        return (RequestList) requestList;
    }

    /**
     * Deprecated: Use getResult instead.
     */
    @Deprecated
    public static ArrayList<Request> getInstance() {
        if (requestList == null) {
            requestList = new ArrayList<Request>();
        }
        return requestList;
    }

    /**
     * Deprecated: There is no user story that says we need to modify a request description after it has been
     * created
     */
    @Deprecated
    public void setRequestDescription(Request request, String description) {
    }

    /**
     * Deprecated: Only use getSearchByKeyword or getSearchByLocation?
     */
    @Deprecated
    public ArrayList<Request> getOpenRequests() {
        return new ArrayList<Request>();
    }


    /**
     * Deprecated: use the void function instead (singleton changer) so that this can be used with
     * the getResults() method
     */
    @Deprecated
    public ArrayList<Request> getSearchByKeyword(String query) {
        return new ArrayList<>();
    }

    /**
     * Deprecated: should use new function that uses elastic search or FileIO (depending on
     * connectivity), not singleton?
     */
    @Deprecated
    public ArrayList<Request> getRequests(User rider) {
        return requestList;
    }
}