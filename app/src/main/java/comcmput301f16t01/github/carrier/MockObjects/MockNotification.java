package comcmput301f16t01.github.carrier.MockObjects;

import android.support.annotation.NonNull;

import java.util.Date;

import comcmput301f16t01.github.carrier.Notifications.Notification;
import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.User;

/**
 * Created by Ben on 2016-11-04.
 */

public class MockNotification extends Notification {
    public MockNotification(@NonNull User userToBeNotified, @NonNull Request relatedRequest) {
        super(userToBeNotified, relatedRequest);
    }

    public void setDate( Date date ) {
        this.date = date;
    }

}
