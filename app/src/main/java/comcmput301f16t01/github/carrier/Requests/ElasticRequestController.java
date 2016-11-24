package comcmput301f16t01.github.carrier.Requests;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Listener;
import comcmput301f16t01.github.carrier.Users.UserController;
import io.searchbox.client.JestResult;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Handles elastic search tasks with requests.
 */
public class ElasticRequestController {
    private static JestDroidClient client;

    /**
     * This listener listens to the two major fetch request tasks (offered requests, and requested
     * requests). When either of the tasks are finished in async mode, it will notify this listener.
     * @see FetchRiderRequestsTask
     * @see GetOfferedRequestsTask
     */
    private static Listener listener = null;
    public static void setListener( Listener newListener ) {
        listener = newListener;
    }
    public static void notifyListener() {
        if (listener != null) {
            listener.update();
        }
    }

    /**
     * Adds a request to Elastic Search.
     * @see RequestController#addRequest(Request)
     */
    public static class AddRequestTask extends AsyncTask<Request, Void, Void> {

        @Override
        protected Void doInBackground(Request... requests) {
            verifySettings();

            for (Request request : requests) {
                Index index = new Index.Builder(request).index("cmput301f16t01").type("request").build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        request.setId(result.getId());
                    } else {
                        Log.i("Add Request Failure", "Failed to add request to elastic search");
                    }
                } catch (IOException e) {
                    Log.i("Add Request Failure", "Something went wrong adding a request to elastic search.");
                    e.printStackTrace();
                }
            }
            return null;
        }
    } // AddRequestTask

    /**
     * Searches by a keyword/string based phrase.
     * @see RequestController#searchByKeyword(String)
     */
    public static class SearchByKeywordTask extends AsyncTask<String, Void, RequestList> {

        @Override
        protected RequestList doInBackground(String... search_parameters) {
            verifySettings();

            // TODO check if this is returning based on most recent results...
            String query =
                    "{ \"from\" : 0, \"size\" : 500,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": { \"match\": { \"description\": \"" + search_parameters[0] + "\" }},\n" +
                    "      \"should\": [\n" +
                    "              { \"match\": { \"status\": 1 }},\n" +
                    "              { \"match\": { \"status\": 2 }}\n" +
                    "      ],\n" +
                    "      \"minimum_should_match\": \"1\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("request")
                    .build();

            RequestList foundRequests = new RequestList();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Request> notificationList = result.getSourceAsObjectList(Request.class);
                    foundRequests.addAll( notificationList );
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "Something went wrong when we tried to talk to elastic search");
            }

            // Load all the offers from these requests
            getOffers( foundRequests );

            // Filter the requests so that we can't see
            filterOutLoggedInUser( foundRequests );

            return foundRequests;
        }
    } // SearchByKeywordTask

    /**
     * Searches requests by a geo-location.
     */
    public static class SearchByLocationTask extends AsyncTask<Location, Void, RequestList>{

        /**
         * Distance represents how far our search query will reach.
         */
        private static final int DISTANCE = 50;

        @Override
        protected RequestList doInBackground(Location... search_parameters) {
            verifySettings();

            String query =
                "{ \"from\" : 0, \"size\" : 500,\n" +
                "  \"query\" : {\n" +
                "    \"filtered\" : {\n" +
                "      \"query\" : {\n" +
                "        \"bool\": { " +
                "          \"should\": [\n" +
                "            { \"match\": { \"status\": 1 }},\n" +
                "            { \"match\": { \"status\": 2 }}\n" +
                "          ],\n" +
                "          \"minimum_should_match\": \"1\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\" : {\n" +
                "        \"geo_distance\" : {\n" +
                "          \"distance\" : \"" + DISTANCE + "km\",\n" +
                "          \"location\" : [" + search_parameters[0].getLongitude() + ", "
                                             + search_parameters[0].getLatitude() + "]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [ {\n" +
                "    \"_geo_distance\": {\n" +
                "      \"location\" : [" + search_parameters[0].getLongitude() + ", "
                                         + search_parameters[0].getLatitude() + "],\n" +
                "      \"order\": \"asc\",\n" +
                "      \"unit\": \"km\",\n" +
                "      \"distance_type\": \"plane\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("request")
                    .build();

            RequestList foundRequests = new RequestList();

            try {
                SearchResult result = client.execute(search);
                Log.i("Result", result.toString());
                if (result.isSucceeded()) {
                    List<Request> notificationList = result.getSourceAsObjectList(Request.class);
                    foundRequests.addAll( notificationList );
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "Something went wrong when we tried to talk to elastic search");
            }
            return foundRequests;
        }
    }

    // TODO http://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java

    /**
     * Searches for a list of possible geo-location from an address string.
     */
    public static class SearchByAddressTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... addresses) {
            List<Address> addressList = null;

            GeocoderNominatim geoNom = new GeocoderNominatim("");
            try {
                addressList = geoNom.getFromLocationName(addresses[0], 50);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addressList;
        }
    }

    /**
     * Get all of a rider's requests filtered by status
     * @see RequestController#fetchRequestsWhereRider(User, Integer...)
     */
    public static class FetchRiderRequestsTask extends AsyncTask<String, Void, RequestList> {

        public boolean withAsync = false;

        @Override
        protected RequestList doInBackground(String... params) {
            verifySettings();

            // Set up must match this username bool query
            String query = "{ \"from\" : 0, \"size\" : 500,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": { \"match\": { \"rider.username\": \"" + params[0] + "\" }}";


            if (params.length > 1) {
                // add should clause(s)
                query += ",\n\"should\": [\n";
                int last = 0;
                for (int i = 1; i < params.length-1; i++) {
                    // complete n-1 filters with a comma
                    query += "{ \"match\": { \"status\": " + params[i] + " }},";
                    last = i;
                }
                // Do the final parameter without a comma and close it.
                query += "{ \"match\": { \"status\": " + params[params.length-1]  + " }}\n],\n";
                query += "\"minimum_should_match\": \"1\"\n";
            }

            // add final closing brackets
            query += "\n    }\n" +
                    "  }\n" +
                    "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("request")
                    .build();

            RequestList foundRequests = new RequestList();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Request> requestList = result.getSourceAsObjectList(Request.class);
                    foundRequests.addAll( requestList );
                } else {
                    throw new IllegalArgumentException( query );
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "Something went wrong when we tried to talk to elastic search");
            }

            // fill the requests with their respective offering drivers.
            getOffers( foundRequests );

            return foundRequests;
        }

        /**
         * After the doInBackground task is complete, this is called on the UI thread so that we can
         * access UI elements if listeners are in place to update any view that is updated when
         * the affected list is updated.
         */
        @Override
        protected void onPostExecute(RequestList requests) {
            // Perform our update on the UI thread
            if (withAsync) {
                RequestController rc = new RequestController();
                rc.getRiderInstance().replaceList( requests );
                notifyListener();
            }
            super.onPostExecute(requests);
        }
    } // FetchRiderRequestsTask

    /**
     * Clears requests for a specified rider.
     */
    public static class ClearRiderRequestsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... search_parameters) {
            verifySettings();

            for ( String searchParam : search_parameters ) {
                String search_string = "{\"query\": {\"match\": {\"rider.username\": \"" + searchParam + "\"}}}";

                DeleteByQuery delete = new DeleteByQuery.Builder(search_string)
                        .addIndex("cmput301f16t01")
                        .addType("request")
                        .build();

                try {
                    client.execute(delete);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException();
                }
            }

            return null;
        }
    }

    /**
     * Attempt to get the latest version of a request
     * TODO update this so that it uses the offer type to grab offers and append them to the request
     * TODO Maybe we don't need this task anymore?
     */
    public static class GetRequestTask extends AsyncTask<String, Void, Request> {

        @Override
        protected Request doInBackground(String... params) {
            verifySettings();

            Get get = new Get.Builder("cmput301f16t01", params[0])
                    .type( "request" ).build();

            try {
                JestResult result = client.execute(get);
                if (result.isSucceeded()) {
                    return result.getSourceAsObject(Request.class);
                } else {
                    throw new IllegalArgumentException( result.getErrorMessage() );
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Adds offers to elastic search
     */
    public static class AddOfferTask extends AsyncTask<Offer, Void, Void> {

        @Override
        protected Void doInBackground(Offer... params) {
            verifySettings();

            for ( Offer offer : params ) {
                Index index = new Index.Builder(offer)
                        .index("cmput301f16t01")
                        .type("offer")
                        .build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        //offer.setId(result.getId());
                    } else {
                        Log.i("Add Request Failure", "Failed to add request to elastic search");
                    }
                } catch (IOException e) {
                    Log.i("Add Request Failure", "Something went wrong adding a request to elastic search.");
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    /**
     * Remove offers from a specified request in elastic search.
     * This is for when you choose a driver to fulfill a request (no longer need the offers in
     * elastic search)
     */
    public static class RemoveOffersTask extends AsyncTask<String, Void, Void> {

        /**
         * Set the mode for the type of "delete by" query you would like to do.
         * Default mode is to delete by request ID.
         */
        public int MODE_REQUEST_ID = 0;
        public int MODE_USERNAME = 1;
        private int mode = 1;
        public void setMode(int mode) {
            if( mode != MODE_REQUEST_ID && mode != MODE_USERNAME ) {
                throw new IllegalArgumentException( "Invalid mode usage." );
            }
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(String... params) {
            verifySettings();

            for( String searchParam : params ) {
                String query = "";

                // Depending on the mode we execute a different query.
                if (mode == MODE_REQUEST_ID) {
                    query = "{\n" +
                            "    \"query\": { \"match\": { \"requestID\" : \"" + searchParam + "\" } }\n" +
                            "}";
                } else {
                    query = "{\n" +
                            "    \"query\": { \"match\": { \"offeringUser\" : \"" + searchParam + "\" } }\n" +
                            "}";
                }

                DeleteByQuery delete = new DeleteByQuery.Builder(query)
                        .addIndex("cmput301f16t01")
                        .addType("offer")
                        .build();

                try {
                    client.execute( delete );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    } // RemoveOffersTask


    /**
     * Get requests where the driver has offered to complete them.
     * @see RequestController#getOfferedRequests(User)
     */
    public static class GetOfferedRequestsTask extends AsyncTask<String, Void, RequestList> {

        public boolean withAsync = false;

        @Override
        protected RequestList doInBackground(String... params) {
            verifySettings();

            RequestList foundRequests;


            String query =
                    "{ \"from\": 0, \"size\": 500,\n" +
                    "    \"query\": { \"match\": { \"offeringUser\": \"" + params[0] + "\" } }\n" +
                    "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("offer")
                    .build();

            SearchResult result;
            try {
                result = client.execute(search);
                if (!result.isSucceeded()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            List<Offer> offers = result.getSourceAsObjectList(Offer.class);

            foundRequests = getRequests( offers );

            return foundRequests;
        }

        /**
         * Get requests for the given offers
         * TODO move this class to be a general sub-task of the ElasticRequestController class
         */
        private RequestList getRequests(List<Offer> offers) {
            RequestList requestList = new RequestList();
            for (Offer offer : offers ) {
                // TODO prune ones that no longer relate to a driver? (i.e. cancelled)
                String query =
                        "{ \"from\": 0, \"size\": 1,\n" +
                        "    \"query\": { \"match\": { \"_id\": \"" + offer.getRequestID() + "\" } }\n" +
                        "}";

                Search search = new Search.Builder(query)
                        .addIndex("cmput301f16t01")
                        .addType("request")
                        .build();

                try {
                    SearchResult result = client.execute(search);
                    if (result.isSucceeded()) {
                        requestList.add( result.getSourceAsObject(Request.class) );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return requestList;
        }

        @Override
        protected void onPostExecute(RequestList requests) {
            // Perform result update on UI thread
            if (withAsync) {
                RequestController rc = new RequestController();
                rc.getOffersInstance().replaceList( requests );
                notifyListener();
            }
            super.onPostExecute(requests);
        }
    } // GetOfferedRequestsTask


    public static class UpdateRequestTask extends AsyncTask<Request, Void, Void> {

        @Override
        protected Void doInBackground(Request... params) {
            verifySettings();

            for (Request request : params ) {
                // Start with basic query to change the status
                String query =
                        "{\n" +
                        "    \"doc\": {\n" +
                        "        \"status\": " + request.getStatus();

                // If there is a chosenDriver, update that as well.
                // TODO if the status is changing to complete or paid or cancelled we might not need this.
                if ( request.getChosenDriver() != null ) {
                    User chosen = request.getChosenDriver();
                    query += ",\n" +
                        "        \"chosenDriver\": {\n" +
                        "            \"email\": \"" + chosen.getEmail() + "\",\n" +
                        "            \"phoneNumber\": \"" + chosen.getPhone() + "\",\n" +
                        "            \"username\": \"" + chosen.getUsername() + "\"\n" +
                        "        }\n";
                }

                // finishing brackets for the query.
                query += "    }\n" +
                        "}";

                Update update = new Update.Builder(query)
                        .index("cmput301f16t01")
                        .type("request")
                        .id(request.getId())
                        .build();

                try {
                    client.execute( update );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    /**
     * Sub-task: grab the offers for a request and then populate a request with them.
     * @see #populate(Request, List)
     */
    private static void getOffers(RequestList foundRequests) {
        for( Request request : foundRequests ) {
            // TODO filter requests that will not have offering drivers?

            String query =
                    "{ \"from\":0, \"size\":1000,\n" +
                            "    \"query\": { \"match\": { \"requestID\" : \"" + request.getId() + "\" } }\n" +
                            "}";
            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("offer")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Offer> offers = result.getSourceAsObjectList(Offer.class);
                    populate( request, offers );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } //getOffers sub-task

    /**
     * Sub-task: For each offer object, grab the user information associated with it and add it as
     * an offer to the request.
     */
    private static void populate(Request request, List<Offer> offers) {
        request.getOfferedDrivers().clear();
        for( Offer offer : offers ) {
            String query =
                    "{ \"from\":0, \"size\":1,\n" +
                            "    \"query\": { \"match\": { \"username\" : \"" + offer.getOfferingUser() + "\" } }\n" +
                            "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301f16t01")
                    .addType("user")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    User offeringUser = result.getSourceAsObject(User.class);
                    try {
                        request.addOfferingDriver(offeringUser);
                    } catch (Exception e) { /* possibly do nothing */ }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // populate sub-task

    /**
     * If the logged in user is found as the offered driver or the requester, then
     * the request is removed from the passed request array.
     */
    private static void filterOutLoggedInUser( RequestList foundRequests ) {
        RequestList filteredRequests = new RequestList();
        for( Request request : foundRequests ) {
            String loggedInUsername = UserController.getLoggedInUser().getUsername();
            String requesterUsername = request.getRider().getUsername();
            if( loggedInUsername.equals(requesterUsername) ){
                continue; // Skip this request and do not add it to the request list
            }
            if( request.hasOfferingDriver(UserController.getLoggedInUser()) ) {
                continue; // Skip this request and do not add it to the request list
            }
            filteredRequests.add( request );
        }
        foundRequests.replaceList( filteredRequests );
    }

    /**
     * Opens a connection to the elastic search server
     */
    private static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder =
                    new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
