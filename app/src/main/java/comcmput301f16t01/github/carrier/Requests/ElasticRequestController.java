package comcmput301f16t01.github.carrier.Requests;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.List;

import comcmput301f16t01.github.carrier.BuildConfig;
import comcmput301f16t01.github.carrier.User;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Handles elastic search tasks with requests
 */
public class ElasticRequestController {
    private static JestDroidClient client;

    /**
     * Adds a request to Elastic Search.
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

// TODO May need this to edit it later...
//    { "from" : 0, "size" : 500,
//      "query": {
//        "bool": {
//            "must": { "match": { "description": "ocean" }},
//            "should": [
//            { "match": { "status": 1 }},
//            { "match": { "status": 2 }}
//            ]
//        }
//    }
//}

    /**
     * Searches by one keyword (this keyword could be a phrase too, though...
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
                    "      ]\n" +
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
            return foundRequests;
        }
    } // SearchByKeywordTask

    /**
     * Get all of a rider's requests including a filter by status
     */
    public static class FetchRiderRequestsTask extends AsyncTask<String, Void, RequestList> {

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

            // We need to get all the offers for each of the requests
            getOffers( foundRequests );

            return foundRequests;
        }

        /**
         * Sub-task: grab the offers for a request
         */
        private void getOffers(RequestList foundRequests) {
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
         * Sub-task: grab the user info for each offer, add it to the offer array.
         */
        private void populate(Request request, List<Offer> offers) {
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
                        request.addOfferingDriver( offeringUser );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } // populate sub-task
    } // FetchRiderRequestsTask

    /**
     * Clears requests for a specified rider.
     */
    public static class ClearRiderRequestsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... search_parameters) {
            verifySettings();
            String search_string = "{\"query\": {\"match\": {\"rider.username\": \"" + search_parameters[0] + "\"}}}";

            DeleteByQuery delete = new DeleteByQuery.Builder(search_string)
                    .addIndex("cmput301f16t01")
                    .addType("request")
                    .build();

            try {
                client.execute( delete );
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException();
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

        @Override
        protected Void doInBackground(String... params) {
            verifySettings();

            for( String requestID : params ) {
                String query =
                        "{\n" +
                        "    \"query\": { \"match\": { \"requestID\" : \"" + requestID + "\" } }\n" +
                        "}";

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
    }

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
