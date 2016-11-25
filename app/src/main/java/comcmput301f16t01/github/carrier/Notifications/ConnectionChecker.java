package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>Call to the static function ConnectionChecker.isConnected( context ) to check if you have
 * an available network connection. Note that this does not test if they actually have an internet
 * connection, just that they have access to some network (which may not have internet connection).</p>
 *
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#connectionchecker">ConnectionChecker</a></p>
 * </br>
 * <p>Author: Android Dev Docs</p>
 * <p>Retrieved on: November 13, 2016</p>
 */
public class ConnectionChecker {
    public static boolean isConnected( Context context ) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
