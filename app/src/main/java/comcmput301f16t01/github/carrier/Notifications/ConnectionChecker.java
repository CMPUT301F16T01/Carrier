package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ben on 2016-11-04.
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
