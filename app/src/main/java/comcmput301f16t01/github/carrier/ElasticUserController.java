package comcmput301f16t01.github.carrier;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Ben on 2016-10-27.
 */

public class ElasticUserController {
    private static JestDroidClient client;

    /**
     * Called to add a user to elastic search
     */
    public static class AddUserTask extends AsyncTask<User, Void, Void> {

        /**
         * Async task to add user to elastic search.
         *
         * @param users
         * @return
         */
        @Override
        protected Void doInBackground(User... users) {
            verifySettings();

            for (User user : users) {
                Index index = new Index.Builder(user).index("cmput301f16t01").type("user").build();

                try {
                    DocumentResult result = client.execute(index);

                    if (result.isSucceeded()) {
                        user.setId(result.getId());
                    } else {
                        Log.i("Add User Unsuccessful", "Failed to add user to elastic search?");
                    }
                } catch (IOException e) {
                    Log.i("Add User Failure", "Something went wrong adding a user to elastic search.");
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public static class FindUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... search_parameters) {
            verifySettings();
            String search_string = "{\"from\": 0, \"size\": 1, \"query\": {\"match\": {\"username\": \"" + search_parameters[0] + "\"}}}";

            Search search = new Search.Builder(search_string)
                    .addIndex("cmput301f16t01")
                    .addType("user")
                    .build();

            User foundUser = null;

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    foundUser = result.getSourceAsObject(User.class);

                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "Something went wrong when we tried to talk to elastic search");
            }
            return foundUser;
        }
    }

    public static class EditUserTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... update_params) {
             verifySettings();
            //update_params[0] is the id, update_params[1] is email, update params[2] is phone
            String script = "{\n" +
                    "    \"script\" : \"ctx._source.phoneNumber = \\\"9090909\\\"\",\n" +
                    "}";

            Update update = new Update.Builder(script)
                    .index("cmput301f16t01")
                    .type("user")
                    .id(update_params[0])
                    .build();

            try {
                client.execute(update);
            } catch (IOException e) {
                e.printStackTrace();
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
