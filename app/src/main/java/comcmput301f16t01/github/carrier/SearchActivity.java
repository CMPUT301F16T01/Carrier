package comcmput301f16t01.github.carrier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void searchByKeyword(View view) {
        // this will create a keyword search dialogue
        Toast.makeText(this, "Search by keyword", Toast.LENGTH_LONG).show();
    }

    public void searchByLocation(View view) {
        // this will create the searchMapsActivity for the user to choose a location
        // marker defaults to current location
        // currently this just creates the blank map activity
        Toast.makeText(this, "Search by Location", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SearchActivity.this, SearchMapsActivity.class);
        startActivity(intent);
    }
}
