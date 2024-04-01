package com.example.arewethereyetnaumoffa;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager = null;
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;
    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";

    private SharedPreferences settings = null;

    private final static String TO = "to";
    private final static String TOLAT = "tolat";
    private final static String TOLONG = "tolong";

    private TextView textTo;
    private TextView viewLatitude;
    private TextView viewLongitude;
    private TextView viewDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settings = getSharedPreferences( "my.app.packagename_preferences", Context.MODE_PRIVATE);
        to = settings.getString(TO, "1225 Engineering");
        toLatitude = Double.parseDouble(settings.getString(TOLAT, "42.724303")); toLongitude = Double.parseDouble(settings.getString(TOLONG, "-84.480507"));
        latitude = 42.731138;
        longitude = -84.487508;
        valid = true;
        EditText editLocation = (EditText) findViewById(R.id.editLocation);
        editLocation.setText("1234 Main St, Anytown, USA");

        setUI();

    }

    /**
     * Set all user interface components to the current state */
    private void setUI() {
        // Assuming you have your UI components and variables declared elsewhere
        textTo.setText(to); // Set 'textTo' to the value of 'to' variable

        if (!valid) {
            // Set to empty strings if not valid
            viewLatitude.setText("");
            viewLongitude.setText("");
            viewDistance.setText("");
        } else {
            // Set latitude and longitude if valid
            viewLatitude.setText(String.valueOf(latitude));
            viewLongitude.setText(String.valueOf(longitude));

            // Calculate the distance
            float[] results = new float[1];
            Location.distanceBetween(latitude, longitude, toLatitude, toLongitude, results);
            float distance = results[0]; // Distance in meters

            // Set the formatted distance
            viewDistance.setText(String.format("%1$6.1fm", distance));
        }
    }

    /**
     * Called when this application becomes foreground again. */
    @Override
    protected void onResume() {
        super.onResume();
        TextView viewProvider = (TextView)findViewById(R.id.textProvider); viewProvider.setText("");
        setUI();
    }

    /**
     * Called when this application is no longer the foreground application. */
    @Override
    protected void onPause() {
        super.onPause();
    }

}