package comcmput301f16t01.github.carrier.Notifications;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * NotificationActivity displays notifications about changes in a driver's offered requests or
 * in a requester's requests. Here the user can choose to mark them as read or delete them.
 */
public class NotificationActivity extends AppCompatActivity {
    ArrayAdapter<Notification> notificationArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView notificationListView = (ListView) findViewById( R.id.ListView_notifications );
        NotificationController nc = new NotificationController();
        ArrayList<Notification> notificationList = nc.fetchNotifications( UserController.getLoggedInUser() );
        // TODO create a unique array adapter for this?
        notificationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList );
        notificationListView.setAdapter( notificationArrayAdapter );
    }

    /**
     * Ties the clear all notifications functionality to the UI.
     * @param view the clear all button
     */
    public void clearAll(View view) {
        NotificationController nc = new NotificationController();
        nc.clearAllNotifications( UserController.getLoggedInUser() );
        notificationArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Ties the mark all notifications as read functionality to the UI.
     * @param view the mark all as read button
     */
    public void markAllRead(View view) {
        NotificationController nc = new NotificationController();
        nc.markAllAsRead( UserController.getLoggedInUser() );
        notificationArrayAdapter.notifyDataSetChanged();
    }
}
