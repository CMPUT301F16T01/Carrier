package comcmput301f16t01.github.carrier;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.List;

import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

// TODO still need to test that request was added correctly

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
     *
     */
    public static class FetchRiderRequestsTask extends AsyncTask<Object, Void, Request> {

        @Override
        protected Request doInBackground(Object... params) {
            String query;
            // must match clause
            if (params.length == 1) {
                // do not include shoulds.
            } else {
                // should match clause
            }

            return null;
        }
    }

    /**
     * Clears requests for a specified rider.
     */
    public static class ClearRiderRequestsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... search_parameters) {
            verifySettings();
            String search_string = "{\"query\": {\"match\": {\"username\": \"" + search_parameters[0] + "\"}}}";

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
