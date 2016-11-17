package comcmput301f16t01.github.carrier.User;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import comcmput301f16t01.github.carrier.R;

/**
 * Created by meind on 2016-11-17.
 */

public class NewVehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
    }

    public void register(View v) {
        EditText makeEditText = (EditText) findViewById(R.id.editText_vehicleMake);
        EditText modelEditText = (EditText) findViewById(R.id.editText_vehicleModel);
        EditText yearEditText = (EditText) findViewById(R.id.editText_vehicleYear);
        EditText colorEditText = (EditText) findViewById(R.id.editText_vehicleColor);
        EditText otherEditText = (EditText) findViewById(R.id.editText_vehicleOther);

        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("username");
        String phone = bundle.getString("phone");
        String email = bundle.getString("email");


    }

}
