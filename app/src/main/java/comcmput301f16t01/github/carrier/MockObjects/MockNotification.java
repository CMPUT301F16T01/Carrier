package comcmput301f16t01.github.carrier.MockObjects;

import android.support.annotation.NonNull;

import java.util.Date;

import comcmput301f16t01.github.carrier.Notifications.Notification;
import comcmput301f16t01.github.carrier.Requests.Request;
import comcmput301f16t01.github.carrier.User.User;

/**
 * Mocks a notification. Allows to set the date of a notification instead of it being solely set by
 * the time it was created.
 */
public class MockNotification extends Notification {
    public MockNotification(@NonNull User userToBeNotified, @NonNull Request relatedRequest) {
        super(userToBeNotified, relatedRequest);
    }

    public void setDate( Date date ) {
        this.date = date;
    }

}
