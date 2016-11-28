package comcmput301f16t01.github.carrier.Notifications;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comcmput301f16t01.github.carrier.Listener;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

/**
 * Maintains a user's notifications in elastic search
 */
public class ElasticNotificationController {
    private static JestDroidClient client;

    /** Adds a notification to Elastic Search */
    public static class AddNotificationTask extends AsyncTask<Notification, Void, Void> {

        /** @return true or false depending on the success of the task */
        @Override
        protected Void doInBackground(Notification... notifications) {
            verifySettings();

            for (Notification aNotification : notifications) {
                // translates a notification into a index that you can execute
                Index index = new Index.Builder(aNotification)
                        .index("cmput301f16t01")
                        .type("notification")
                        .build();

                // Attempts to push new notification to Elastic Search
                try {
                    DocumentResult result = client.execute(index);

                    if (result.isSucceeded()) {
                        aNotification.setID(result.getId());
                    } else {
                        Log.i("Add Notification bad", "Failed to add user to elastic search?");
                    }
                } catch (IOException e) {
                    Log.i("Add Notification bad", "Something went wrong adding a user to elastic search.");
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /** Returns notifications based on a username */
    public static class FindNotificationTask extends AsyncTask<String, Void, ArrayList<Notification>> {
        /** called upon if an unread notification is detected */
        Listener listener = null;

        /** Set to true if we detect a unread notification during the doInBackground */
        Boolean unreadExists = false;

        /**
         * Adds a listener to listen for unread notifications
         * @param listener the listener you wish to call if there is an unread one.
         */
        public void addListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        protected ArrayList<Notification> doInBackground(String... search_parameters) {
            verifySettings();
            String search_string = "{\"from\" : 0, \"size\" : 500, \"query\": {\"match\": {\"username\": \"" + search_parameters[0] + "\"}}}";

            Search search = new Search.Builder(search_string)
                    .addIndex("cmput301f16t01")
                    .addType("notification")
                    .build();

            ArrayList<Notification> foundNotifications = new ArrayList<>();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Notification> notificationList = result.getSourceAsObjectList(Notification.class);
                    foundNotifications.addAll( notificationList );
                } else {
                    return foundNotifications;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "Something went wrong when we tried to talk to elastic search");
            }

            // If we have a listener, it wants to know if there are unread notifications
            // We will tell the listener in onPostExecute (in case it needs to update a view on UI thread).
            Collections.sort( foundNotifications );
            if (listener != null) {
                for (Notification notification : foundNotifications) {
                    if (!notification.isRead()) {
                        unreadExists = true;
                        break;
                    }
                }
            }
            return foundNotifications;
        }

        @Override
        protected void onPostExecute(ArrayList<Notification> notifications) {
            // If the listener is not null, we can call update if we have detected an unread notification
            if (listener != null && unreadExists) {
                listener.update();
            }
            // We will update the notifications from here.
            NotificationController.getNotificationListInstance().clear();
            NotificationController.getNotificationListInstance().addAll( notifications );
            super.onPostExecute(notifications);
        }
    }

    /** Clears all requests related to a username */
    public static class ClearAllTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... search_parameters) {
            verifySettings();

            for (String searchParam : search_parameters ) {
                String search_string = "{\"query\": {\"match\": {\"username\": \"" + searchParam + "\"}}}";

                DeleteByQuery delete = new DeleteByQuery.Builder(search_string)
                        .addIndex("cmput301f16t01")
                        .addType("notification")
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

    public static class MarkAsReadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... id_parameter) {
            String script =
                    "{\n" +
                    "    \"doc\": {\n" +
                    "        \"read\": true \n" +
                    "    }\n" +
                    "}";

            Update update = new Update.Builder(script)
                    .index("cmput301f16t01")
                    .type("notification")
                    .id(id_parameter[0])
                    .build();

            try {
                client.execute( update );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /** Sets up the client to be used for Elastic Search */
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
