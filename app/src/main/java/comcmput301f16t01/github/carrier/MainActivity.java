package comcmput301f16t01.github.carrier;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.Notifications.ConnectionChecker;
import comcmput301f16t01.github.carrier.Notifications.NotificationController;
import comcmput301f16t01.github.carrier.Notifications.NotificationActivity;
import comcmput301f16t01.github.carrier.Requests.DriverViewRequestActivity;
import comcmput301f16t01.github.carrier.Requests.ElasticRequestController;
import comcmput301f16t01.github.carrier.Requests.RequestAdapter;
import comcmput301f16t01.github.carrier.Requests.RequestController;
import comcmput301f16t01.github.carrier.Requests.RequestList;
import comcmput301f16t01.github.carrier.Requests.RiderRequestActivity;
import comcmput301f16t01.github.carrier.Searching.SearchActivity;
import comcmput301f16t01.github.carrier.Searching.SearchResultsActivity;
import comcmput301f16t01.github.carrier.Users.LoginActivity;
import comcmput301f16t01.github.carrier.Users.LoginMemory;
import comcmput301f16t01.github.carrier.Users.UserController;
import comcmput301f16t01.github.carrier.Users.UserProfileActivity;

/**
 * <p>Central activity for a user. After logging in, this is the activity the user will be taken to
 * whenever they open the app henceforth.</p>
 * </br>
 * <p>See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#mainactivity">MainActivity</a></p>
 * </br>
 * <p>Based on: <a href="https://developer.android.com/training/permissions/requesting.html">Requesting Permissions at Run Time</a></p>
 * <p>Author: Android Dev Docs</p>
 * <p>Retrieved on: November 9th, 2016</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/26295481/android-swiperefreshlayout-how-to-implement-canchildscrollup-if-child-is-not-a-l">Android SwipeRefreshLayout how to implement canChildScrollUp if child is not a ListView or ScrollView</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/2819876/twibit">Twibit</a>, <a href="http://stackoverflow.com/users/1032307/iamlukeyb">iamlukeyb</a></p>
 * <p>Posted on: October 10th, 2014</p>
 * <p>Retrieved on: November 16th, 2016</p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;

    private final Activity activity = MainActivity.this;

    /** The {@link ViewPager} that will host the section contents. */
    private ViewPager mViewPager;

    /** Identifying which permission we are checking for. */
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Carrier");

        checkPermissions();
        // Request controller requires a context to save in
        RequestController.setContext(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // TabLayout class is injected with viewpager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // The two listeners below listen to tabs being moved and perform the "change the fab to
        // look different" operation. (Fab is Floating Action Button)
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                changeFab( tab.getPosition() );     // Changes the fab between driver and rider fab
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        };
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeFab(position); // Changes the fab between driver and rider fab
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        // We start on the rider tab, so we hide the driver fab before launching.
        FloatingActionButton driver_fab = (FloatingActionButton) findViewById(R.id.fab_driver);
        driver_fab.hide();

        // Perform an update using RequestController if there is internet
        if (ConnectionChecker.isThereInternet()) {
            RequestController.performAsyncUpdate();
        }
        // Otherwise inform the user that they are offline
        else {
            Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show();
            // Load the cached rider and driver requests for the logged in user
            RequestController.fetchAllRequestsWhereRider(UserController.getLoggedInUser());
            RequestController.getOfferedRequests(UserController.getLoggedInUser());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Will toast if there are unread notifications.
        final Context context = this;
        NotificationController nc = new NotificationController();
        nc.asyncUnreadNotification(UserController.getLoggedInUser(), new Listener() {
            @Override
            public void update() {
                Toast.makeText( context, "You have unread notifications!", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    /**
     * Shows the correct FAB depending on what tab position we are at.
     *
     * @param position the current screen tab we are in (i.e. 0=Rider, 1=Driver)
     */
    private void changeFab(int position) {
        FloatingActionButton rider_fab = (FloatingActionButton) findViewById(R.id.fab_rider);
        FloatingActionButton driver_fab = (FloatingActionButton) findViewById(R.id.fab_driver);
        switch (position) {
            case 0:
                driver_fab.hide();
                rider_fab.show();
                break;

            case 1:
                rider_fab.hide();
                driver_fab.show();
                break;
        }
    }

    /**
     * Result of the user granting or denying permissions. If they grant the permissions
     * we don't need to do anything. If they do not grant the permissions, we should tell
     * them that they are required for the map to be displayed and the app to function.
     *
     * @param requestCode This is the variable we use to request permission
     * @param permissions The permission string for external storage
     * @param grantResults The result of the request
     */
    // see code attribution
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break; // permission was granted, yay!
                } else {
                    // permission denied, boo!
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle("Permissions Denied");
                    adb.setMessage("You will not be able to view the map to select locations to make a " +
                            "request without allowing the app to access your device's storage. You can " +
                            "change this permission from the app info.");
                    adb.setCancelable(true);
                    adb.setPositiveButton("OK", null);
                    adb.show();
                }
                break;
            }
        }
    }

    /**
     * Asks user to grant required permissions for the maps to work.
     */
    // see code attribution
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // User selected to view profile
        if (id == R.id.action_viewProfile) {
            Intent intent = new Intent(activity, UserProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", UserController.getLoggedInUser());
            intent.putExtras(bundle);
            startActivity(intent);
        }

        // User selected "help" (goto help activity)
        if (id == R.id.action_help) {
            Intent intent = new Intent(activity, HelpActivity.class);
            startActivity(intent);
        }

        // User selected view notifications (goto view notification activity)
        if (id == R.id.action_viewNotifications ) {
            Intent intent = new Intent(activity, NotificationActivity.class );
            startActivity(intent);
        }

        // User has selected to logout, see: logout() function (creates a logout prompt)
        if (id == R.id.action_logOut) {
            logout();
        }

        return super.onOptionsItemSelected(item); // user has selected none of the above, have superclass handle it.
    }

    /**
     * When back is pressed or the "Log Out" menu option is selected:
     * Pop up a AlertDialog to confirm
     * If they do indeed what to log out: open a new LoginActivity, while closing the current MainActivity
     * otherwise remain in MainActivity.
     */
    private void logout() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        adb.setMessage("Log out and return to the login screen?");
        adb.setCancelable(true);
        adb.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginMemory lm = new LoginMemory( activity );
                lm.saveUsername( "" ); // remove the username from memory
                activity.finish();
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                UserController.logOutUser();
            }
        });
        adb.setNegativeButton("Cancel", null);  // have cancel only close the dialog and nothing else
        adb.show();
    }

    /**
     * When the user clicks on the add button this will start the make request activity so they
     * can add a new request to our system
     *
     * @param view The calling view (Rider Floating Action Button)
     */
    public void makeRequest(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("point","start");
        bundle.putString("type","new");
        Intent intent = new Intent(activity, SetLocationsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * This will start the Search activity for a driver when they want to search requests
     * after they press the driver FAB. If they have internet, they will be navigated to the
     * regular search screen to make their search. If they do not have internet, they will be
     * immediately directed to a cache of their previous search results.
     * @param view The calling view (Driver Floating Action Button)
     */
    public void startSearchActivity(View view) {
        if (ConnectionChecker.isThereInternet()) {
            Intent intent = new Intent(activity, SearchActivity.class);
            startActivity(intent);
        } else {
            // if there is no internet, we will skip straight to the SearchResultsActivity
            Intent intent = new Intent(activity, SearchResultsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /** The fragment argument representing the section number for this fragment */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Set up the fragments to contain their respective ListView
            final ListView requestListView = (ListView) rootView.findViewById(R.id.listView_requestListView);
            if( getArguments().getInt(ARG_SECTION_NUMBER) == 1 ) {
                fillRiderRequests( requestListView );
            } else {
                fillDriverRequests( requestListView );
            }

            final SwipeRefreshLayout srl = (SwipeRefreshLayout) rootView.findViewById( R.id.swipeRefresh);

            // Set up a scroll listener to turn off swipe to refresh if the view is not at the top.
            requestListView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    int scrollY = requestListView.getScrollY();
                    if(scrollY == 0) requestListView.setEnabled(true);
                    else srl.setEnabled(false);
                }
            });

            // Set up SwipeRefresh and what should happen on a swipe action
            srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.i( "onCreateView", "onRefresh called from SwipeRefreshLayout");

                    // We first check if there is connection, if not, we stop refreshing and
                    // inform them that we cannot perform a live update.

                    if(!ConnectionChecker.isConnected(getContext())) {
                        Toast.makeText(getContext(), "You have no network connection!", Toast.LENGTH_SHORT ).show();
                        srl.setRefreshing( false );
                        return;
                    }

                    // Will toast if there are unread notifications.
                    NotificationController nc = new NotificationController();
                    nc.asyncUnreadNotification(UserController.getLoggedInUser(), new Listener() {
                        @Override
                        public void update() {
                            Toast.makeText( getContext(), "You have unread notifications!", Toast.LENGTH_SHORT ).show();
                        }
                    });


                    // Checks for when the AsyncTask is finished. It waits for two calls from the
                    // onPostExecute methods of the task, meaning that the two tasks (get driver requests
                    // and get rider requests) have finished.
                    ElasticRequestController.setListener(new Listener() {
                        private int finish = 0;
                        @Override
                        public void update() {
                            finish += 1;
                            if (finish >= 2) {
                                finish = 0;
                                srl.setRefreshing( false );
                            }
                        }
                    });

                    // Perform the async update with the listener in place to stop the refresh
                    // symbol when the async tasks have finished.
                    RequestController.performAsyncUpdate();
                }
            });

            return rootView;
        }

        /**
         * Sets up the ListView for the driver.
         * @param requestListView The view that will contain the request items to be presented
         */
        private void fillDriverRequests(final ListView requestListView) {
            // Get the reference to the list of offered requests and adapt it to the listView
            final RequestList requestList = RequestController.getOffersInstance();
            final RequestAdapter requestArrayAdapter = new RequestAdapter(this.getContext(),
                    R.layout.requestlist_item,
                    requestList);
            requestListView.setAdapter(requestArrayAdapter);

            // Add a listener to listen and update the view when the arrayList changes
            requestList.addListener(new Listener() {
                @Override
                public void update() {
                    requestArrayAdapter.notifyDataSetChanged();
                }
            });

            /*
             * When we click a request we want to be able to see it in another activity
             * Use bundles to send the position of the request in a list
             */
            requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DriverViewRequestActivity.class);
                    Bundle bundle = new Bundle();
                    // We give the position it is in the arrayList so that it can pull it up later
                    bundle.putInt( "position", position );
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }

        /**
         * Sets up the ListView for the rider.
         * @param requestListView The view that will contain the request items to be presented
         */
        private void fillRiderRequests(final ListView requestListView) {
            // Get the reference to the list of requested requests and adapt it to the listView
            final RequestList requestList = RequestController.getRiderInstance();
            final RequestAdapter requestArrayAdapter = new RequestAdapter(this.getContext(),
                    R.layout.requestlist_item,
                    requestList);
            requestListView.setAdapter(requestArrayAdapter);

            // Add a listener to listen and update the view when the arrayList changes
            requestList.addListener(new Listener() {
                @Override
                public void update() {
                    requestArrayAdapter.notifyDataSetChanged();
                }
            });

            /*
             * When we click a request we want to be able to see it in another activity
             * Use bundles to send the position of the request in a list
             */
            requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), RiderRequestActivity.class);
                    Bundle bundle = new Bundle();
                    // We give the position it is in the arrayList so that it can pull it up later
                    bundle.putInt( "position", position );
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "RIDER REQUESTS";
                case 1:
                    return "DRIVER REQUESTS";
            }
            return null;
        }
    }
}
