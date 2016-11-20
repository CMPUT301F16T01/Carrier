package comcmput301f16t01.github.carrier.Requests;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Uses a singleton pattern to store information about three types of requests. (Request requested,
 * requests offered to complete, and requests searched for.
 */
public class RequestController {
    /** Holds requests where the rider has requested a ride. */
    private static final RequestList requestsWhereRider = new RequestList();

    /** Holds requests where the rider has offered to ride. */
    private static final RequestList requestsWhereOffered = new RequestList();

    /** Holds requests that have been searched for by the user. */
    private static final RequestList searchResult = new RequestList();

    /**
     * Prevents errors when a RequestController is initialized and methods that require requestList
     * to not be null (i.e. getResult() )
     */
    private RequestController() { /* prevent instantiation */ }

    /** Returns an instance of all requests where the user has offered to drive */
    public static RequestList getOffersInstance() {
        return requestsWhereOffered;
    }

    /** Returns an instance of all requests where the user has requested a ride */
    public static RequestList getRiderInstance() {
        return requestsWhereRider;
    }

    /**
     *  Get the results of a searchByKeyword or a getSearchByLocation query.
     *  @see #pruneByPrice(Float, Float)
     *  @see #pruneByPricePerKM(Float, Float)
     */
    public static RequestList getResult() {
        return searchResult;
    }

    /** Adds a request to elastic search. */
    public static String addRequest(Request request) {
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

    /**
     * Cancels a request using elastic search.
     */
    public static void cancelRequest( Request request ) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus(Request.CANCELLED);
        urt.execute( request );
    }

    /**
     * Is used to add a driver to a specified request (the request should be in the request controller).
     *
     * @param request The request we are modifying
     * @param driver  the driver that is being added as a driver for the request.
     *
     * @see Offer
     */
    public static void addDriver(Request request, User driver) {
        try {
            request.addOfferingDriver( driver );
        } catch ( Exception e ) {
            return; // If the driver is already offered we shouldn't do this action. TODO return message?
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
     * Is used accept a given driver (who has offered) for the request.
     *
     * @param request The request that is being modified
     * @param driver  The driver that is being accepted
     */
    public static void confirmDriver(Request request, User driver) {
        // Modify and update the request, then execute the update task
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setChosenDriver( driver ); // TODO did they really offer?
        request.setStatus( Request.CONFIRMED );
        requestsWhereOffered.notifyListeners();
        urt.execute( request );

        // Send out a notification
        NotificationController nc = new NotificationController();
        nc.addNotification( driver, request );
    }

    /**
     * Marks a request as complete in elastic search.
     */
    public static void completeRequest(Request request) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus( Request.COMPLETE );
        urt.execute( request );
        requestsWhereOffered.notifyListeners();
        requestsWhereRider.notifyListeners();
    }

    /**
     * Sets a request as paid for
     */
    public static void payForRequest(Request request) {
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
    public static void searchByKeyword(String keyword) {
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
    public static void searchByLocation(Location location) {
        // TODO check how these are sorted, we want to sort them by those closest to those furthest away
        ElasticRequestController.SearchByLocationTask sblt = new ElasticRequestController.SearchByLocationTask();
        sblt.execute(location);
        try {
            searchResult.replaceList(sblt.get());
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
    public static RequestList getOfferedRequests(User driver) {
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
     * Clears out all the requested requests for a user in elastic search
     */
    public static void clearAllRiderRequests(User rider) {
        ElasticRequestController.ClearRiderRequestsTask crrt = new ElasticRequestController.ClearRiderRequestsTask();
        crrt.execute( rider.getUsername() );
        requestsWhereRider.replaceList( new RequestList() );
    }

    /**
     * Has the same function as {@link #fetchAllRequestsWhereRider}, but allows the ability to
     * filter by status integers (provided by the Request class).
     *
     * @see #fetchAllRequestsWhereRider(User)
     * @see Request
     * @see Request (TODO .Status when we use ENUMS)
     *
     * @param rider the rider you want to match requests against
     * @param statuses the statues you would like to see (filters non listed ones) (null means grab all)
     * @return A list of requests from the given criteria
     */
    public static RequestList fetchRequestsWhereRider(User rider, Integer... statuses ) {
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

    /**
     * Use this to grab requests for the given rider, however it may be better to use the async update
     * method as it will not lock up the UI thread. This method also updates the singleton instance
     * of the rider's requests.
     *
     * @see #performAsyncUpdate()
     *
     * @param rider the rider you wish to find requests for.
     * @return The requests found by this search (locks UI thread).
     */
    public static RequestList fetchAllRequestsWhereRider( User rider ) {
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

    /**
     * Updates the requestsWhereRider and requestsWhereOffered lists in the background (do not need
     * to wait on the main UI thread at all).
     *
     * @see ElasticRequestController.FetchRiderRequestsTask
     * @see ElasticRequestController.GetOfferedRequestsTask
     */
    public static void performAsyncUpdate() {
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();
        frrt.withAsync = true;
        frrt.execute(UserController.getLoggedInUser().getUsername());

        ElasticRequestController.GetOfferedRequestsTask gort = new ElasticRequestController.GetOfferedRequestsTask();
        gort.withAsync = true;
        gort.execute( UserController.getLoggedInUser().getUsername());
    }

    /**
     * Ensures that the request controller on has results for requests within the price range of
     * minPrice to maxPrice. (This affects getResult).
     * @param minPrice The minimum value you wish to prune by.
     * @param maxPrice The maximum value you wish to prune by. (Passing null is equivalent to passing positive infinity)
     */
    public static void pruneByPrice(@NonNull Float minPrice, @Nullable Float maxPrice) {
        RequestList filteredRequests = new RequestList();
        for ( Request request : searchResult ) {
            if ( request.getFare() < minPrice * 100 ) { continue; }
            if ( maxPrice != null && maxPrice * 100 < request.getFare() ) { continue; }
            filteredRequests.add( request ); // add the request if it is in range
        }
        searchResult.replaceList( filteredRequests );
    }

    /**
     * Ensures that the request controller only has results for requests within the price per kilometer range of
     * minPricePerKM to maxPricePerKM. (This affects getResult).
     * @param minPricePerKM The minimum value you wish to prune by.
     * @param maxPricePerKM The maximum value you wish to prune by. (Passing null is equivalent to passing positive infinity)
     */
    public static void pruneByPricePerKM( @NonNull Float minPricePerKM, @Nullable Float maxPricePerKM ) {
        RequestList filteredRequests = new RequestList();
        for ( Request request : searchResult ) {
            double pricePerKM = request.getFare() / request.getDistance();
            // ensure the price per kilometer is greater than the specified minimum
            if ( pricePerKM < minPricePerKM * 100 ) { continue; }
            // ensure the price per kilometer is less than the specified maximum.
            if ( maxPricePerKM != null && maxPricePerKM * 100 < pricePerKM ) { continue; }
            filteredRequests.add( request ); // add the request if it is in range
        }
        searchResult.replaceList( filteredRequests );
    }
}