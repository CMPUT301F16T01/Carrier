package comcmput301f16t01.github.carrier.Searching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import comcmput301f16t01.github.carrier.R;
import comcmput301f16t01.github.carrier.Request;
import comcmput301f16t01.github.carrier.RequestController;
import comcmput301f16t01.github.carrier.RequestList;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        RequestController rc = new RequestController();
        RequestList requestList = rc.getResult();
    }
}
