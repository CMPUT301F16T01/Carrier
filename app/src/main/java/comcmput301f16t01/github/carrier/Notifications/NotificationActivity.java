package comcmput301f16t01.github.carrier.Notifications;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Listener;
import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * NotificationActivity displays notifications about changes in a driver's offered requests or
 * in a requester's requests. Here the user can choose to mark them as read or delete them.
 */
public class NotificationActivity extends AppCompatActivity {
    private ArrayAdapter<Notification> notificationArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Set up the notification list view
        final ListView notificationListView = (ListView) findViewById( R.id.ListView_notifications );
        final ArrayList<Notification> notificationList = NotificationController.getNotificationListInstance();
        notificationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList ); // TODO create a unique array adapter for this?
        notificationListView.setAdapter( notificationArrayAdapter );

        final SwipeRefreshLayout srl = (SwipeRefreshLayout) findViewById( R.id.swipeRefresh);
        // Set up a scroll listener to turn off swipe to refresh if the view is not at the top.
        notificationListView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = notificationListView.getScrollY();
                if(scrollY == 0) notificationListView.setEnabled(true);
                else srl.setEnabled(false);
            }
        });

        // Swipe to refresh calls the async update and shows the user visually that the list view
        // may be updating
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!ConnectionChecker.isConnected(getBaseContext())) {
                    Toast.makeText(getBaseContext(), "You have no network connection!", Toast.LENGTH_LONG ).show();
                    srl.setRefreshing( false );
                    return;
                }

                // Listens for unread notifications/the list to be updated
                NotificationController nc = new NotificationController();
                nc.asyncUnreadNotification( UserController.getLoggedInUser(), new Listener() {
                    @Override
                    public void update() {
                        notificationArrayAdapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                        Toast.makeText( getBaseContext(), "You have a new notification!", Toast.LENGTH_SHORT ).show();
                    }
                });
                srl.setRefreshing( false );
            }
        });
    }

    @Override
    protected void onResume() {
        // Create a listener on the async task to update the list view when new unread notifications come in when resuming
        NotificationController nc = new NotificationController();
        nc.asyncUnreadNotification(UserController.getLoggedInUser(), new Listener() {
            @Override
            public void update() {
                notificationArrayAdapter.notifyDataSetChanged();
            }
        });
        super.onResume();
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
