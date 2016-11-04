package comcmput301f16t01.github.carrier;

import android.support.annotation.NonNull;

import java.util.Date;

import comcmput301f16t01.github.carrier.Notifications.Notification;

/**
 * Created by Ben on 2016-11-04.
 */

class MockNotification extends Notification {
    public MockNotification(@NonNull User userToBeNotified, @NonNull Request relatedRequest) {
        super(userToBeNotified, relatedRequest);
    }

    public void setDate( Date date ) {
        this.date = date;
    }

}
