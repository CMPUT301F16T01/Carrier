package comcmput301f16t01.github.carrier.Notifications;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Users.UserController;



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
        NotificationList notificationList = nc.fetchNotifications( UserController.getLoggedInUser() );
        // TODO create a unique array adapter for this?
        notificationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList );
        notificationListView.setAdapter( notificationArrayAdapter );
    }

    public void clearAll(View view) {
        NotificationController nc = new NotificationController();
        nc.clearAllNotifications( UserController.getLoggedInUser() );
        notificationArrayAdapter.notifyDataSetChanged();
    }

    public void markAllRead(View view) {
        NotificationController nc = new NotificationController();
        nc.markAllAsRead( UserController.getLoggedInUser() );
        notificationArrayAdapter.notifyDataSetChanged();
    }
}
