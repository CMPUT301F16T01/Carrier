package comcmput301f16t01.github.carrier.Searching;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.RequestController;

public class SearchActivity extends AppCompatActivity {
    final Activity activity = SearchActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    /**
     * This will create the search by keyword dialog.
     * The keyword query entered by the user will be used in a
     * search on available requests.
     * @param view
     */
    public void searchByKeyword(View view) {
        /* Code based on: https://developer.android.com/guide/topics/ui/dialogs.html
         * Retrieved on October 28, 2016
         */
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_keyword_search, null);
        adb.setTitle("Search by Keyword");
        adb.setView(dialogView);
        adb.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText searchEditText = (EditText) dialogView.findViewById(R.id.editText_keywordSearch);
                String query = searchEditText.getText().toString();
                Toast.makeText(activity, query, Toast.LENGTH_SHORT).show();
                // TODO consider any input handling on the keyword?
                RequestController rc = new RequestController();
                rc.searchByKeyword( query );
                Intent intent = new Intent( SearchActivity.this, SearchResultsActivity.class );
                startActivity( intent );
            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.show();
    }

    /**
     * This will open a maps activity to allow the user to
     * select a location to create a query for available requests.
     * The initial marker will default to the current location.
     * @param view
     */
    public void searchByLocation(View view) {
        Toast.makeText(this, "Search by Location", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(SearchActivity.this, SearchMapsActivity.class);
        //startActivity(intent);
        // opening the SearchMapsActivity doesn't work right now because the API key has not been shared
        // we are using a different method anyways so we will do an overhaul of SearchMapsActivity
        // TODO do the search by opening a map activity to query by location
    }
}
