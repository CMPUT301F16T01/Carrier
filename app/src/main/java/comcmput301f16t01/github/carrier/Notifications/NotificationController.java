package comcmput301f16t01.github.carrier.Notifications;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Users.User;


/**
 * Controls a user's notifications for elastic search
 */
public class NotificationController {
    private static NotificationList notificationList;

    /**
     * @return A sorted NotificationList
     */
    public NotificationList fetchNotifications( User user ) {
        ElasticNotificationController.FindNotificationTask fnt = new ElasticNotificationController.FindNotificationTask();
        fnt.execute( user.getUsername() );
        try {
            notificationList = fnt.get();
            Collections.sort(notificationList);
        } catch (Exception e) {
            Log.i("NotificationController", "bad error");
        }
        return notificationList;
    }

    /**
     * @return true, if an unread notification exists. false otherwise
     */
    public boolean unreadNotification( User user ) {
        fetchNotifications( user );
        for ( Notification notification : notificationList ) {
            if (!notification.isRead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all notifications for a user.
     * @param user A user is anyone who uses our app. This is who we clear notifications for
     */
    public void clearAllNotifications( User user ) {
        ElasticNotificationController.ClearAllTask cat = new ElasticNotificationController.ClearAllTask();
        cat.execute( user.getUsername() );
        try {
            cat.get();
        } catch (Exception e) {
            // Make the Async insync
            e.printStackTrace();
        }
        if (notificationList != null) {
            notificationList.clear();
        }
    }

    /**
     * @return the notification generated by this method
     */
    public Notification addNotification(@NonNull User userToAlert, @NonNull Request relatedRequest) {
        ElasticNotificationController.AddNotificationTask ant = new ElasticNotificationController.AddNotificationTask();
        Notification newNotification = new Notification( userToAlert, relatedRequest );
        ant.execute( newNotification );
        try {
            ant.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newNotification; // TODO find out why I did this, because it makes no sense.
    }

    /**
     * Marks the given notification as read
     * @param notification This is a message that is sent to a user
     */
    public void markNotificationAsRead( Notification notification ) {
        ElasticNotificationController.MarkAsReadTask mart = new ElasticNotificationController.MarkAsReadTask();
        mart.execute( notification.getID() );
        try {
            mart.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.setRead( true );
    }

    /**
     * Marks all request for user as read, if they are currently unread.
     * @param user A user is anyone who uses our app. This is who we will clear notifications for.
     */
    public void markAllAsRead( User user ) {
        NotificationList notificationList = this.fetchNotifications( user );
        for (Notification notification : notificationList ) {
            if( !notification.isRead() ) {
                this.markNotificationAsRead(notification);
            }
        }
    }
}
