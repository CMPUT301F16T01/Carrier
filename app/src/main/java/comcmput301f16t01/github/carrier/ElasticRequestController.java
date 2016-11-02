package comcmput301f16t01.github.carrier;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Created by michael on 28/10/16.
 */

public class ElasticRequestController {
    private static JestDroidClient client;

    /**
     * Called to cancel a request in elastic search
     */
    public static class CancelRequest extends AsyncTask<Request, Void, Void> {

        /**
         * Async task to update a request in elastic search to cancelled.
         * @param requests
         * @return
         */
        @Override
        protected Void doInBackground(Request... requests) {
            verifySettings();

            for (Request request : requests) {
                // ctx._source.status tells elastic search the the tag that is being modified is the status.
                String script = "{\n" +
                        "    \"script\" : \"ctx._source.status = " + Request.CANCELLED + "\" }";
                // Updates are used to do partial updates of the document. The scripts is a string
                // That specifies what the update will do. The index is for the specific document, type
                // Is the type we are editing. The id is the unique id for the request that is being
                // edited.
                //
                Update update = new Update.Builder(script).index("cmput301f16t01").type("request").id(request.getId()).build();
                try {
                    DocumentResult result = client.execute(update);

                    if (result.isSucceeded()) {
                        request.setStatus(Integer.valueOf(result.getValue("status").toString()));
                    } else {
                        Log.i("Canceling Unsuccessful", "Failed update request in elastic search?");
                    }
                } catch (IOException e) {
                    Log.i("Canceling Failure", "Something went wrong updating value in elastic search.");
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
