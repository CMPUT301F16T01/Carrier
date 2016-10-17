package comcmput301f16t01.github.carrier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class RiderMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_rider_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_viewProfile) {
            Toast.makeText( RiderMainActivity.this, "Wanna view your profile? Nope!",
                    Toast.LENGTH_SHORT ).show();
            // TODO link to user profile.
        }

        if (id == R.id.action_logOut ) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When back is pressed or the "Log Out" menu option is selected:
     *      Pop up a AlertDialog to confirm and open a new LoginActivity, while closing the current
     *      RiderMainActivity.
     */
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        adb.setMessage("Log out and return to the login screen?");
        adb.setCancelable(true);
        final Activity activity = RiderMainActivity.this;
        adb.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                Intent intent = new Intent(RiderMainActivity.this, LoginActivity.class);
                startActivity(intent);
                // TODO log out current user?
            }
        });
        adb.setNegativeButton("Cancel", null );
        adb.show();
    }
}
