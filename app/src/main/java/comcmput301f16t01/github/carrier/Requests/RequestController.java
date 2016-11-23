package comcmput301f16t01.github.carrier.Requests;

import android.location.Location;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

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
    /** Holds requests where the rider has requested a ride. */
    private static RequestList requestsWhereRider;

    /** Holds requests where the rider has offered to ride. */
    private static RequestList requestsWhereOffered;

    /** Holds requests that have been searched for by the user. */
    private static RequestList searchResult;

    /**
     * Prevents errors when a RequestController is initialized and methods that require requestList
     * to not be null (i.e. getResult() )
     */
    public RequestController() {
        // Note that requestList is static, so it will not be null if you create a second instance of RequestController
        if (requestsWhereRider == null) {
            requestsWhereRider = new RequestList();
        }
        if (requestsWhereOffered == null){
            requestsWhereOffered = new RequestList();
        }
        if (searchResult == null) {
            searchResult = new RequestList();
        }
    }

    /** Returns an instance of all requests where the user has offered to drive */
    public RequestList getOffersInstance() {
        return requestsWhereOffered;
    }

    /** Returns an instance of all requests where the user has requested a ride */
    public RequestList getRiderInstance() {
        return requestsWhereRider;
    }

    /** Get the results of a searchByKeyword or a getSearchByLocation query. */
    public RequestList getResult() { return searchResult; }

    /** Adds a request to elastic search. */
    public String addRequest(Request request) {
        if (request.getStart() == null || request.getEnd() == null) {
            return "You must first select a start and end location";
        } else if (request.getFare() == -1) {
            return "You must first estimate the fare";
        } else {
            ElasticRequestController.AddRequestTask art = new ElasticRequestController.AddRequestTask();
            art.execute(request);
            requestsWhereRider.add( request ); // Add new request to requestList (will notify riderList views)
        }
        return null;
    }

    /** Clears information in the singleton, not exactly necessary */
    @Deprecated
    public void clear() {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Deprecated: see fetch requests where rider?
     */
    @Deprecated
    public RequestList getRequests(User rider) {
        throw new IllegalArgumentException( "This method is deprecated." );
    }


    // TODO Why does this need a rider? You can cancel a request just knowing the request.
    @Deprecated
    public void cancelRequest(User rider, Request request) {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Cancels a request using elastic search
     */
    public void cancelRequest( Request request ) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus(Request.CANCELLED);
        urt.execute( request );
    }

    /**
     * Is used to add a driver to a request.
     *
     * @param request The request we are modifying
     * @param driver  the driver that is being added as a driver for the request.
     *
     * @see Offer
     */
    public void addDriver(Request request, User driver) {
        try {
            request.addOfferingDriver( driver );
        } catch ( Exception e ) {
            return; // If the driver is already offered we shouldn't do this action.
        }

        // create an offer object [[ potentially throws IllegalArgumentException if called wrong ]]
        Offer newOffer = new Offer(request, driver);

        // Add offer to elastic search
        ElasticRequestController.AddOfferTask aot = new ElasticRequestController.AddOfferTask();
        aot.execute( newOffer );
        // TODO add addOffer task to queue if offline

        // Add a notification
        NotificationController nc = new NotificationController();
        nc.addNotification( request.getRider(), request );
        // TODO add addNotification to queue if offline

        requestsWhereOffered.add( request ); // Notifies offerList views
    }

    /**
     * Is used to show that the user has accepted the provided driver.
     *
     * @param request The request that is being modified
     * @param driver  The driver that is being accepted
     */
    public void confirmDriver(Request request, User driver) {
        // Modify and update the request, then execute the update task
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setChosenDriver( driver );
        request.setStatus( Request.CONFIRMED );
        requestsWhereOffered.notifyListeners();
        urt.execute( request );

        // Send out a notification
        NotificationController nc = new NotificationController();
        nc.addNotification( driver, request );
    }

    /**
     * Completes a request
     */
    public void completeRequest(Request request) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus( Request.COMPLETE );
        urt.execute( request );
        requestsWhereOffered.notifyListeners();
        requestsWhereRider.notifyListeners();
    }

    /**
     * Sets a request as paid for
     */
    public void payForRequest(Request request) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus( Request.PAID );
        urt.execute( request );
        requestsWhereOffered.notifyListeners();
        requestsWhereRider.notifyListeners();
    }

    /**
     * Search requests by the keyword, will set it so the singleton contains the information for
     * this query. Use getResults() to get the information.
     * @param keyword This is the keyword that the user wants to look for requests with. We use to Query.
     */
    public void searchByKeyword(String keyword) {
        ElasticRequestController.SearchByKeywordTask sbkt = new ElasticRequestController.SearchByKeywordTask();
        sbkt.execute(keyword);
        try {
            searchResult.replaceList( sbkt.get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Search requests by a location. This sets it so the singleton contains the information for
     * this query. Use getResults() to get the information.
     */
    public void searchByLocation(Location location) {
        ElasticRequestController.SearchByLocationTask sblt = new ElasticRequestController.SearchByLocationTask();
        sblt.execute(location);
        try {
            searchResult.replaceList( sblt.get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Is used to provide a driver with a list of the requests that the driver has offered to give a ride.
     *
     * @param driver The driver who is requesting the list of the requests they have offered
     *               but the rider has no confirmed their choice in driver.
     * @return An ArrayList of requests that the driver has offered to give a ride on.
     */
    // TODO rename this method? i.e. getRequestsWhereDriverOffered, or something
    public RequestList getOfferedRequests(User driver) {
        ElasticRequestController.GetOfferedRequestsTask gort = new ElasticRequestController.GetOfferedRequestsTask();
        gort.execute( driver.getUsername() );
        try {
            requestsWhereOffered.replaceList( gort.get() );  // TODO maybe make this a background task. Now that it listens, it can just fill it when it's ready
        } catch (Exception e) {
            throw new IllegalArgumentException( "There was an error executing the AsyncTask." );
        }
        return requestsWhereOffered;
    }

    /**
     * Clears out all the requested requests for a user
     */
    public void clearAllRiderRequests(User rider) {
        ElasticRequestController.ClearRiderRequestsTask crrt = new ElasticRequestController.ClearRiderRequestsTask();
        crrt.execute( rider.getUsername() );
        requestsWhereRider.replaceList( new RequestList() );
    }

    /**
     *
     * @param rider the rider you want to match requests against
     * @param statuses the statues you would like to see (filters non listed ones) (null means grab all)
     * @return A list of requests from the given criteria
     */
    public RequestList fetchRequestsWhereRider(User rider, Integer... statuses ) {
        // Open a fetch task for the user
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();

        // Convert the parameters of this method to a string array for the execution of the task
        String[] vars = new String[1 + statuses.length];
        vars[0] = rider.getUsername();
        for (int i = 1; i <= statuses.length; i++ ) {
            vars[i] = Integer.toString( statuses[i-1] );
        }
        frrt.execute( vars );

        // Get the found requests from the task
        RequestList foundRequests = new RequestList();
        try {
            foundRequests = frrt.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestsWhereRider.replaceList( foundRequests );
        return foundRequests;
    }

    public RequestList fetchAllRequestsWhereRider( User rider ) {
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();
        frrt.execute( rider.getUsername() );
        RequestList foundRequests = new RequestList();
        try {
            foundRequests = frrt.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestsWhereRider.replaceList( foundRequests );
        return foundRequests;
    }

    public void performAsyncUpdate() {
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();
        frrt.withAsync = true;
        frrt.execute(UserController.getLoggedInUser().getUsername());

        ElasticRequestController.GetOfferedRequestsTask gort = new ElasticRequestController.GetOfferedRequestsTask();
        gort.withAsync = true;
        gort.execute( UserController.getLoggedInUser().getUsername());
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * *    DEPRECATED FUNCTIONS   * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Deprecated: use the void getSearchByLocation
     */
    @Deprecated
    public ArrayList<Request> getSearchByLocation(Location location) {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Deprecated: There are several other functions that do this. Also, try to only use
     * getSearchByKeyword or getSearchByLocation
     */
    @Deprecated
    public ArrayList<Request> getAvailableRequests() {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Deprecated: Use getResult instead.
     */
    @Deprecated
    public static RequestList getInstance() {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Deprecated: There is no user story that says we need to modify a request description after it has been
     * created
     */
    @Deprecated
    public void setRequestDescription(Request request, String description) {
        throw new IllegalArgumentException( "This method is deprecated." );
    }

    /**
     * Deprecated: Only use getSearchByKeyword or getSearchByLocation?
     */
    @Deprecated
    public ArrayList<Request> getOpenRequests() {
        throw new IllegalArgumentException( "This method is deprecated." );
    }
}