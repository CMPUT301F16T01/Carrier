package comcmput301f16t01.github.carrier.Notifications;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.Users.User;
import io.searchbox.annotations.JestId;

/**
 * Notification class for storing and retrieving notifications.
 */
public class Notification implements Comparable<Notification> {
    /** The username of the user to be notified */
    private String username;

    /** The JestId of the request this notification references */
    private String requestID;

    /** True if the notification is marked as read */
    private boolean read;

    /** True if the person to be notified is the rider of the request */
    private boolean isRider;

    /** The time the notification was created (for sorting purposes) */
    protected Date date;

    @JestId
    private String elasticID;

    public Notification(@NonNull User userToBeNotified, @NonNull Request relatedRequest) {
        this.requestID = relatedRequest.getId();
        this.username = userToBeNotified.getUsername();
        this.date = new Date();
        this.read = false;
        // If the notified is the rider, we set that value to true in the notification
        isRider = relatedRequest.getRider().getUsername().equals(userToBeNotified.getUsername());
    }

    public boolean isRead() {
        return read;
    }

    public void setRead( Boolean isRead ) {
        this.read = isRead;
    }

    public Date getDate() {
        return this.date;
    }

    public String getRequestID() {
        return requestID;
    }

    public String getUsername() {
        return username;
    }

    public void setID(String id) {
        this.elasticID = id;
    }

    public String getID() {
        return elasticID;
    }

    /** @return The string representation of a notification. */
    @Override
    public String toString() {
        String notifyString = "";
        if (!read) {
            notifyString += "New!\n";
        }
        if (isRider) {
            notifyString += "A driver has offered to accept your request!";
        } else {
            notifyString += "A rider has accepted your offer to drive!";
        }
        return notifyString;
    }

    /**
     * Returns an integer based on the comparison between two notifications.
     * We prioritize by notifications that are not read, then by their date (if their read status is
     * equivalent).
     *
     * @see java.util.Collections#sort(List)
     */
    @Override
    public int compareTo(@NonNull Notification o) {
        // 0 means 'this = o' || 1 means 'this > o' || -1 means 'this < o'
        if (this.isRead() == o.isRead()) {
            // If they are equal in isRead, we order them based on date.
            return -this.getDate().compareTo(o.getDate());
        } else if (this.isRead() && !(o.isRead())) {
            // If they are not equal, 'this < o' if this is not read yet.
            return 1;
        } else {
            // If they are not equal, 'this > o' if this is read.
            return -1;
        }
    }
}
