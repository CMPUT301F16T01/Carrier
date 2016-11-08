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

    // TODO https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    public static boolean isConnected( Context context ) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
