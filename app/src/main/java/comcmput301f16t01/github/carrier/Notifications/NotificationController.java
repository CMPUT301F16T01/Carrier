package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.os.health.SystemHealthManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.User;

/**
 * Elastic Search in between...?
 */
public class NotificationController {
    // private NotificationList notificationList;

    public NotificationList fetchNotifications( String username ) {
        ElasticNotificationController.FindNotificationTask fnt = new ElasticNotificationController.FindNotificationTask();
        fnt.execute( username );
        System.out.print( username );
        NotificationList notificationList = new NotificationList();
        try {
            notificationList.addAll( fnt.get() );
        } catch (Exception e) {
            Log.i("NotificationController", "bad error");
        }
        return notificationList;
    }

    public void clearAllNotifications( User user ) {
        ElasticNotificationController.ClearAllTask cat = new ElasticNotificationController.ClearAllTask();
        cat.execute( user.getUsername() );
    }

    public void deleteNotification( String notificationID ) {
    }

    public void addNotification(@NonNull User userToAlert, @NonNull Request relatedRequest) {
        ElasticNotificationController.AddNotificationTask ant = new ElasticNotificationController.AddNotificationTask();
        Notification newNotification = new Notification( userToAlert, relatedRequest );
        ant.execute( newNotification );
    }
}
