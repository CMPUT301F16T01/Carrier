package comcmput301f16t01.github.carrier.Notifications;

import android.support.annotation.NonNull;

import java.util.Date;

import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.User;

/**
 * Notification class for storing and retrieving notifications.
 */
public class Notification implements Comparable<Notification> {
    protected String username;
    protected String requestID;
    protected boolean read;
    protected Date date;
    private String elasticID;

    public Notification(@NonNull User userToBeNotified, @NonNull Request relatedRequest) {
        this.requestID = relatedRequest.getId();
        this.username = userToBeNotified.getUsername();
        this.date = new Date();
        this.read = false;
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
