package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Call to the static function ConnectionChecker.isConnected( context ) to check if you have
 * an available network connection. Note that this does not test if they actually have an internet
 * connection, just that they have access to some network (which may not have internet connection).
 */

public class ConnectionChecker {

    // Based on: https://goo.gl/oximGj
    // Author: Android Dev Docs
    // Retrieved on: November 13, 2016
    public static boolean isConnected( Context context ) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    //http://stackoverflow.com/questions/15496278/httpurlconnection-is-throwing-exception
    //https://developer.android.com/reference/java/net/HttpURLConnection.html
    //https://developer.android.com/reference/android/os/AsyncTask.html

    public static class isThereConnectionTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            URL team1URL = null;
            try {
                team1URL = new URL("http://cmput301.softwareprocess.es:8080/cmput301f16t01");
                HttpURLConnection urlConnection = (HttpURLConnection) team1URL.openConnection();
                return urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
