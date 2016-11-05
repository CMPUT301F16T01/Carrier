package comcmput301f16t01.github.carrier.Notifications;

import android.content.Context;
import android.support.annotation.NonNull;

import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.User;

/**
 * Holds an instance of fetched notifications from ElasticSearch
 */

public class NotificationController {
    private NotificationList notificationList;

    public NotificationController() {
        fetchNotifications();
    }

    public NotificationList fetchNotifications() {
        return new NotificationList();
    }

    public void clearAllNotifications( User user ) {
    }

    public void deleteNotification( String notificationID ) {
    }

    public void addNotification(@NonNull String username, @NonNull Request relatedRequest) {

    }
}
