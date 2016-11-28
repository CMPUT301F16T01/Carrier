package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>Call to the static function ConnectionChecker.isConnected( context ) to check if you have
 * an available network connection. Note that this does not test if they actually have an internet
 * connection, just that they have access to some network (which may not have internet connection).</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#connectionchecker">ConnectionChecker</a></p>
 * </br>
 * <p>Based on: <a href="https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html">Determining and Monitoring the Connectivity Status</a></p>
 * <p>Based on: <a href="https://developer.android.com/reference/java/net/HttpURLConnection.html">HttpURLConnection</a></p>
 * <p>Based on: <a href="https://developer.android.com/reference/android/os/AsyncTask.html">AsyncTask</a></p>
 * <p>Author: Android Dev Docs</p>
 * <p>Retrieved on: November 24th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/15496278/httpurlconnection-is-throwing-exception">HttpURLConnection is throwing exception</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/653856/raghunandan">Raghunandan</a></p>
 * <p>Posted on: March 19th, 2013</p>
 * <p>Retrieved on: November 24th, 2016</p>
 */
public class ConnectionChecker {

    public static boolean isConnected( Context context ) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Async task that checks to see if the user can connect to elastic search.Returns true if you
     * can, otherwise false.
     */
    public static class isThereConnectionTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        /**
         * Returns true if a connection to elastic search can be established, otherwise returns
         * false.
         */
        protected Boolean doInBackground(Void... params) {
            // Try making an http request to our index on elastic search
            try {
                URL team1URL = new URL("http://cmput301.softwareprocess.es:8080/cmput301f16t01");
                HttpURLConnection urlConnection = (HttpURLConnection) team1URL.openConnection();
                // Websites return status code 200 (HTTP_OK in this API) when you've successfully connected to them
                return urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Facade pattern for isThereConnectionTask doInBackground(...) because it's ugly.
     * @return True if there is internet, False otherwise
     */
    public static Boolean isThereInternet() {
        ConnectionChecker.isThereConnectionTask isThereConnectionTask = new ConnectionChecker.isThereConnectionTask();
        isThereConnectionTask.execute();
        Boolean isInternet = false;
        try {
            isInternet = isThereConnectionTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isInternet;
    }


}
