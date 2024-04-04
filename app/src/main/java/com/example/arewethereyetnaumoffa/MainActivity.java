package com.example.arewethereyetnaumoffa;


import static com.example.arewethereyetnaumoffa.R.id;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


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

    private ActiveListener activeListener = new ActiveListener();

    private TextView textTo;
    private TextView viewLatitude;
    private TextView viewLongitude;
    private TextView viewDistance;

    private TextView viewLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settings = getSharedPreferences("arewethereyetnaumoffa.app.packagename_preferences", Context.MODE_PRIVATE);
        to = settings.getString(TO, "1225 Engineering");
        toLatitude = Double.parseDouble(settings.getString(TOLAT, "42.724303"));
        toLongitude = Double.parseDouble(settings.getString(TOLONG, "-84.480507"));
        latitude = 42.731138;
        longitude = -84.487508;
        valid = true;
        EditText editLocation = (EditText) findViewById(id.editLocation);


        textTo = findViewById(id.textNewLocLabel); // Replace 'yourTextViewIdForTextTo' with the actual ID from your layout
        viewLatitude = findViewById(id.textLatitude); // Replace 'yourTextViewIdForLatitude' with the actual ID from your layout
        viewLongitude = findViewById(id.textLongitude); // Replace 'yourTextViewIdForLongitude' with the actual ID from your layout
        viewDistance = findViewById(id.textDistance);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        Button showRouteButton = findViewById(R.id.buttonShowRoute);
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoute();
            }
        });

        Button newLocationButton = findViewById(R.id.buttonNewLoc);
        newLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNew( v);
            }
        });

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
        TextView viewProvider = (TextView) findViewById(id.textProvider);
        viewProvider.setText("");
        setUI();
        registerListeners();
    }

    /**
     * Called when this application is no longer the foreground application. */
    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();
    }

    private class ActiveListener implements LocationListener {


        @Override
        public void onStatusChanged(String s, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
        }


    }

    ;

    private void registerListeners() {
        unregisterListeners();
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        String bestAvailable = locationManager.getBestProvider(criteria, true);
        if(bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1,
                    activeListener);
            TextView viewProvider = (TextView)findViewById(id.textProvider);
            viewProvider.setText(bestAvailable);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }

    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }
    private void onLocation(Location location) {
        if(location == null) {
            return;
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;
        setUI();
    }

    /**
     * Handle setting a new "to" location.
     * @param address Address to display
     * @param lat latitude
     * @param lon longitude
     */
    private void newTo(String address, double lat, double lon) {
        to = address;
        toLatitude = lat;
        toLongitude = lon;
        setUI();

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.putFloat("latitude", (float) lat);
        editor.putFloat("longitude", (float) lon);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemSparty) {
            newTo("Sparty", 42.731138, -84.487508);
            return true;
        } else if (id == R.id.itemHome) {
            newTo("Home", 42.735636, -84.4882755);
            return true;

        } else if (id == R.id.item2250) {
            newTo("2250 Engineering", 42.724303, -84.480507);
            return true;

        }
        else if (id == R.id.itemConrads) {
            newTo("Conrad's", 42.730800, -84.466310);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNew(View view) {
        EditText location = (EditText)findViewById(R.id.editLocation);
        final String address = location.getText().toString().trim();
        newAddress(address);
    }

    private void newAddress(final String address) {
        if(address.equals("")) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                lookupAddress(address);
            }
        }).start();
    }

    /**
     * Look up the provided address. This works in a thread!
     * @param address Address we are looking up
     */
    private void lookupAddress(String address) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
        boolean exception = false;
        List<Address> locations;
        try {
            locations = geocoder.getFromLocationName(address, 1);
        } catch(IOException ex) {
            locations = null;
            exception = true;
        }
        final boolean exception_ = exception;
        final List<Address> locations_ = locations;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newLocation(address, exception_, locations_);
            }
        });
    }

    private void newLocation(String address, boolean exception, List<Address>
            locations) {
        if(exception) {
            Toast.makeText(MainActivity.this, R.string.exception,
                    Toast.LENGTH_SHORT).show();
        } else {
            if(locations == null || locations.size() == 0) {
                Toast.makeText(this, R.string.couldnotfind,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            EditText location = (EditText)findViewById(R.id.editLocation);
            location.setText("");
            Address a = locations.get(0);
            newTo(address, a.getLatitude(), a.getLongitude());
        }
    }

    private void showRoute() {
        RadioGroup radioGroup = findViewById(R.id.radioGroupTransportMode);
        int selectedModeId = radioGroup.getCheckedRadioButtonId();
        String mode = "driving";
        if (selectedModeId == R.id.radioButtonDriving) {
            mode = "driving";
        } else if (selectedModeId == R.id.radioButtonWalking) {
            mode = "walking";
        } else if (selectedModeId == R.id.radioButtonBicycling) {
            mode = "bicycling";
        }
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1"
                + "&origin=" + latitude + "," + longitude
                + "&destination=" + toLatitude + "," + toLongitude
                + "&travelmode=" + mode);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
        }
    }





}
