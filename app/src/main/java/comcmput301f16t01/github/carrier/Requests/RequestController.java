package comcmput301f16t01.github.carrier.Requests;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.channels.FileLockInterruptionException;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * <p>Uses a singleton pattern to store information about three types of requests. (Request requested,
 * requests offered to complete, and requests searched for.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#requestcontroller">RequestController</a></p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/26217983/osmdroid-bonus-pack-reverse-geolocation">osmdroid bonus pack reverse geolocation</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/4095382/cristina">cristina</a></p>
 * <p>Posted on: October 6th, 2014</p>
 * <p>Retrieved on: November 11th, 2016</p>
 */
public class RequestController {
    /** Holds requests where the rider has requested a ride. */
    private static final RequestList requestsWhereRider = new RequestList();

    /** Holds requests where the rider has offered to ride. */
    private static final RequestList requestsWhereOffered = new RequestList();

    /** Requests that are made while offline that are put on elastic search when there
     * there is connection. */
    private static final RequestList offlineRiderRequests = new RequestList();

    /** Holds requests that have been searched for by the user. */
    private static final RequestList searchResult = new RequestList();

    /** The file name of the locally saved made rider requests .*/
    private static final String RIDER_FILENAME = "RiderRequests.sav";

    /** The file name of the locally saved offered driver requests. */
    private static final String DRIVER_FILENAME = "DriverRequests.sav";

    /** The file name of the locally saved offline rider requests. */
    private static final String OFFLINE_REQUEST_FILENAME = "OfflineRequests.sav";

    /** The context with which to save */
    private static Context saveContext;

    /**
     * Prevents errors when a RequestController is initialized and methods that require requestList
     * to not be null (i.e. getResult() )
     */
    private RequestController() { /* prevent instantiation */ }

    /**
     * In order to save to file, we require a context. This function sets the context.
     * @param contextToSet The context in which to save.
     */
    public static void setContext(Context contextToSet) {
        saveContext = contextToSet;
    }

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
     *  @see #pruneByPrice(Double, Double)
     *  @see #pruneByPricePerKM(Double, Double)
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
            boolean internetConnection = ConnectionChecker.isThereInternet();
            // If there is internet we update ElasticSearch with the new request.
            if (internetConnection) {
                ElasticRequestController.AddRequestTask art = new ElasticRequestController.AddRequestTask();
                art.execute(request);
            } else {
                // If there is no internet, add to the offline request queue and save it
                offlineRiderRequests.add(request);
                saveOfflineRiderRequests();
            }
            // Regardless of whether or not there is internet, we add the request to the local requestWhereRider RequestList
            requestsWhereRider.add( request ); // Add new request to requestList (will notify riderList views)
            // Save requestsWhereRider to file
            saveRiderRequests();
        }
        return null;
    }

    /**
     * Cancels a request using elastic search.
     */
    public static void cancelRequest( Request request ) {
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.setStatus(Request.Status.CANCELLED);
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
            return; // If the driver is already offered we shouldn't do this action.
        }

        // remove the request from the search results because we are making an offer to it
        for ( Request searchResultRequest : searchResult ) {
            if ( searchResultRequest.getId().equals(request.getId())) {
                searchResult.remove( searchResultRequest );
            }
        }

        // If there is internet update the request on elastic search with the new accepting driver.
        if (ConnectionChecker.isThereInternet()) {
            // create an offer object [[ potentially throws IllegalArgumentException if called wrong ]]
            Offer newOffer = new Offer(request, driver);

            // Add offer to elastic search
            ElasticRequestController.AddOfferTask aot = new ElasticRequestController.AddOfferTask();
            aot.execute( newOffer );
            ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
            urt.execute(request);
        }
        
        // TODO add addOffer task to queue if offline

        // Regardless of whether or not there is internet, create a notification and add the offer to the local requestsWhereOffered RequestList
        // Add a notification
        NotificationController nc = new NotificationController();
        nc.addNotification( request.getRider(), request );
        // TODO add addNotification to queue if offline
        requestsWhereOffered.add( request ); // Notifies offerList views
        appendRiderRequests(request);
    }

    /**
     * Is used accept a given driver (who has offered) for the request.
     *
     * @param request The request that is being modified
     * @param driver  The driver that is being accepted
     */
    public static void confirmDriver(Request request, User driver) {
        // Modify and update the request, then execute the update task
        // You should only be able to confirm the driver of a request if the user that is confirming
        // is the rider.
        if (!requestsWhereRider.contains(request)){
            throw new RuntimeException();
        }
        ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
        request.confirmDriver( driver );
        request.setStatus( Request.Status.CONFIRMED );
        requestsWhereOffered.notifyListeners();

        // If there is internet, update the request on ElasticSearch with confirmed driver.
        if (ConnectionChecker.isThereInternet()) {
            urt.execute( request );
            ElasticRequestController.RemoveOffersTask rot = new ElasticRequestController.RemoveOffersTask();
            // Remove all offers
            rot.setMode(rot.MODE_REQUEST_ID);
            rot.execute(request.getId());
        }

        // Regardless of whether or not there is internet, create a notification and save the modified requestsWhereOffered
        // Send out a notification
        NotificationController nc = new NotificationController();
        nc.addNotification( driver, request );
        saveDriverOfferedRequests();
    }

    /**
     * Marks a request as complete in elastic search.
     */
    public static void completeRequest(Request request) {
        // If there is internet update elastic search with the completed request
        if (ConnectionChecker.isThereInternet()) {
            ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
            request.setStatus( Request.Status.COMPLETE );
            urt.execute( request );
        }
        // Regardles so of whether or not there is internet update the UI statuses and save the request lists.
        requestsWhereOffered.notifyListeners();
        requestsWhereRider.notifyListeners();
        saveDriverOfferedRequests();
        saveRiderRequests();
    }

    /**
     * Sets a request as paid for
     */
    public static void payForRequest(Request request) {
        // If there is internet update elastic search with the paid request
        if (ConnectionChecker.isThereInternet()) {
            ElasticRequestController.UpdateRequestTask urt = new ElasticRequestController.UpdateRequestTask();
            request.setStatus( Request.Status.PAID );
            urt.execute( request );
        }
        // Regardless of whether or not there is internet update the UI with the new status and save the request lists
        requestsWhereOffered.notifyListeners();
        requestsWhereRider.notifyListeners();
        saveDriverOfferedRequests();
        saveRiderRequests();
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
    public static RequestList getOfferedRequests(User driver) {
        // If there is no internet connection, load the cached driver requests into the requestsWhereOffered
        if (!ConnectionChecker.isThereInternet()) {
            loadDriverOfferedRequests();
            return requestsWhereOffered;
        }
        // If there is connection, fetch requests from elastic search to load into requestsWhereOffered
        ElasticRequestController.GetOfferedRequestsTask gort = new ElasticRequestController.GetOfferedRequestsTask();
        gort.execute( driver.getUsername() );
        try {
            requestsWhereOffered.replaceList( gort.get() );  // TODO maybe make this a background task. Now that it listens, it can just fill it when it's ready
        } catch (Exception e) {
            throw new IllegalArgumentException( "There was an error executing the AsyncTask." );
        }
        // Save the driver offered requests once they're loaded
        saveDriverOfferedRequests();
        return requestsWhereOffered;
    }

    /**
     * Used for testing. Clears out all the requested requests for a user
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
     * @see Request.Status
     *
     * @param rider the rider you want to match requests against
     * @param statuses the statues you would like to see (filters non listed ones) (null means grab all)
     * @return A list of requests from the given criteria
     */
    public static RequestList fetchRequestsWhereRider(User rider, Request.Status... statuses ) {
        // If the user is offline, load from rider requests from file rather than from elastic search
        if (!ConnectionChecker.isThereInternet()) {
            loadRiderRequests();
            return requestsWhereRider;
        }
        // Open a fetch task for the user
        loadOfflineRiderRequests();
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();

        // Convert the parameters of this method to a string array for the execution of the task
        String[] vars = new String[1 + statuses.length];
        vars[0] = rider.getUsername();
        for (int i = 1; i <= statuses.length; i++ ) {
            vars[i] = statuses[i-1].toString();
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
        // Save loaded riderRequests.
        saveRiderRequests();
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
        // If we're offline, load the cached rider requests rather than form elastic search
        if (!ConnectionChecker.isThereInternet()) {
            loadRiderRequests();
            return requestsWhereRider;
        }
        loadOfflineRiderRequests();
        ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();
        frrt.execute( rider.getUsername() );
        RequestList foundRequests = new RequestList();
        try {
            foundRequests = frrt.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestsWhereRider.replaceList( foundRequests );
        // Save rider requests after load
        saveRiderRequests();
        return foundRequests;
    }

    public static RequestList getOfflineRiderRequests() {
        return offlineRiderRequests;
    }

    /**
     * Updates the requestsWhereRider and requestsWhereOffered lists in the background (do not need
     * to wait on the main UI thread at all). If a rider had made requests while offline and they
     * regain connectivity, also adds their offline requests to elastic search.
     *
     * @see ElasticRequestController.FetchRiderRequestsTask
     * @see ElasticRequestController.GetOfferedRequestsTask
     */
    public static void performAsyncUpdate() {
        // If there is connectivity, perform updates
        if (ConnectionChecker.isThereInternet()) {
            /* If the size of the list of requests made offline is greater than 0, add them to
             elastic search
              */
            if (RequestController.getOfflineRiderRequests().size() > 0) {
                ElasticRequestController.AddRequestTask art = new ElasticRequestController.AddRequestTask();
                // Convert to an array since async tasks don't take in array lists as arguments.
                Request[] requestsToPass = new Request[RequestController.getOfflineRiderRequests().size()];
                for (int i = 0; i < RequestController.getOfflineRiderRequests().size(); i++) {
                    Request request = RequestController.getOfflineRiderRequests().get(i);
                    request.getStart().setAddress(getAddress(saveContext, request.getStart().getLatitude(), request.getEnd().getLongitude()));
                    request.getEnd().setAddress(getAddress(saveContext, request.getEnd().getLatitude(), request.getEnd().getLongitude()));
                    requestsToPass[i] = request;
                }
                art.execute(requestsToPass);
                // Empty the offline RequestList and save an empty file
//                RequestController.getOfflineRiderRequests().clear();
//                saveOfflineRiderRequests();
            }


            ElasticRequestController.FetchRiderRequestsTask frrt = new ElasticRequestController.FetchRiderRequestsTask();
            frrt.withAsync = true;
            frrt.execute(UserController.getLoggedInUser().getUsername());

            ElasticRequestController.GetOfferedRequestsTask gort = new ElasticRequestController.GetOfferedRequestsTask();
            gort.withAsync = true;
            gort.execute( UserController.getLoggedInUser().getUsername());
        }
    }


    /**
     * Get address string from a geo point
     *
     * @return the address as a string.
     */
    // see code attribution
    public static String getAddress(Context context, double latitude, double longitude) {
        String pointAddress;
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for(int i = 0; i <= n; i++) {
                    if(i != 0) {
                        sb.append("\n");
                    }
                    sb.append(address.getAddressLine(i));
                }
                pointAddress = new String(sb);
            } else {
                pointAddress = "";
            }
        } catch (Exception e) {
            pointAddress = "";
        }
        return pointAddress;
    }

    /**
     * Caches the requests that the rider has made.
     */
    public static void saveRiderRequests() {
        try {
            FileOutputStream fos = saveContext.openFileOutput(RIDER_FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(requestsWhereRider, out);
            out.flush();

            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For offline functionality. Loads the cached rider requests.
     */
    public static void loadRiderRequests() {
        FileInputStream fis = null;
        try {
            fis = saveContext.openFileInput(RIDER_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<RequestList>() {}.getType();

            // Load the rider requests into the controller
            requestsWhereRider.replaceList((RequestList) gson.fromJson(in, listType));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Append to the cached rider requests.
     */
    public static void appendRiderRequests(Request request) {
        Gson gson = new Gson();
        try {
            FileOutputStream fos = saveContext.openFileOutput(OFFLINE_REQUEST_FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            offlineRiderRequests.add(request);
            gson.toJson(offlineRiderRequests, out);
            out.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Caches the requests that the driver offered to fulfill.
     */
    public static void saveDriverOfferedRequests() {
        try {
            FileOutputStream fos = saveContext.openFileOutput(DRIVER_FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(requestsWhereOffered, out);
            out.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For offline functionality. Loads the cached driver offered requests.
     */
    public static void loadDriverOfferedRequests() {
        FileInputStream fis = null;
        try {
            fis = saveContext.openFileInput(DRIVER_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<RequestList>() {}.getType();
            // Load the driver requests into the controller
            requestsWhereOffered.replaceList((RequestList) gson.fromJson(in, listType));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For offline functionality. Saves the offline rider requests from the controller to file.
     */
    public static void saveOfflineRiderRequests() {
        try {
            FileOutputStream fos = saveContext.openFileOutput(OFFLINE_REQUEST_FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(offlineRiderRequests, out);
            out.flush();
            fos.close();

            Log.i("Saving offline: ", "Size of offline save " + Integer.toString(offlineRiderRequests.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For offline functionality. Loads the offlineRiderRequests from file into the controller.
     */
    public static void loadOfflineRiderRequests() {
        FileInputStream fis = null;
        try {
            fis = saveContext.openFileInput(OFFLINE_REQUEST_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<RequestList>() {}.getType();
            // Load the offline requests into the controller
            offlineRiderRequests.replaceList((RequestList) gson.fromJson(in, listType));

            Log.i("Loading offline: ", "Size of offline load " + Integer.toString(offlineRiderRequests.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures that the request controller on has results for requests within the price range of
     * minPrice to maxPrice. (This affects getResult).
     * @param minPrice The minimum value you wish to prune by.
     * @param maxPrice The maximum value you wish to prune by. (Passing null is equivalent to passing positive infinity)
     */
    public static void pruneByPrice(@NonNull Double minPrice, @Nullable Double maxPrice) {
        RequestList filteredRequests = new RequestList();
        for ( Request request : searchResult ) {

            Log.i( "price:", "" + request.getFare() );
            Log.i( "minPrice:", "" + minPrice );
            Log.i( "maxPrice:", "" + maxPrice );

            // If the fare is less than the minimum price specified, skip it
            if ( request.getFare() < minPrice * 100 ) { continue; }
            // If the maxPrice is not null, and the fare is greater than the max price, skip it.
            if ( maxPrice != null && maxPrice * 100 < request.getFare() ) { continue; }

            Log.i( " *** added fare", "" + request.getFare() );
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
    public static void pruneByPricePerKM( @NonNull Double minPricePerKM, @Nullable Double maxPricePerKM ) {
        RequestList filteredRequests = new RequestList();
        for ( Request request : searchResult ) {
            double pricePerKM = (request.getFare() / request.getDistance()) / 100;

            Log.i( "pricePerKM:", "" + pricePerKM );
            Log.i( "minPricePerKM:", "" + minPricePerKM );
            Log.i( "maxPricePerKM:", "" + maxPricePerKM );

            // ensure the price per kilometer is greater than the specified minimum
            if ( pricePerKM < minPricePerKM ) { continue; }
            // ensure the price per kilometer is less than the specified maximum.
            if ( maxPricePerKM != null && maxPricePerKM < pricePerKM ) { continue; }
            filteredRequests.add( request ); // add the request if it is in range

            Log.i( " *** added per KM", "" + pricePerKM );
        }
        searchResult.replaceList( filteredRequests );
    }
}