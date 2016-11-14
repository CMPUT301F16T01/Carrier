package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Call to the static function ConnectionChecker.isConnected( context ) to check if you have
 * an avaliable network connection. Note that this does not test if they actually have an internet
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

}
