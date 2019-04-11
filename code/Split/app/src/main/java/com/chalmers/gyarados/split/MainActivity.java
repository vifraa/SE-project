package com.chalmers.gyarados.split;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient; //Used to get last known position
    private Geocoder geocoder; //Used to get location from longitude and latitude


    private boolean mLocationPermissionGranted; //true if user has given location permission, false otherwise

    private TextView positionTextView; //Textview to show the current location



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        geocoder = new Geocoder(this, Locale.getDefault());
        positionTextView = findViewById(R.id.latitudeTextView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();
        updateLocation();
    }

    /**
     * Checks if the user have given permission, otherwise we ask the user for permission
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Called when user answered the request permission prompt
     * @param requestCode
     * @param permissions the permissions you have asked for
     * @param grantResults the results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

    }

    /**
     * Updates the position
     */
    @SuppressLint("MissingPermission")
    private void updateLocation() {
        if(mLocationPermissionGranted){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                //todo This is not the best way to get location from longitude and latitude. Should be done in seperate thread.
                                //todo How it should be done can be found on https://developer.android.com/training/location/display-address
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    positionTextView.setText(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    String error = "Couldn't find your address";
                                    positionTextView.setText(error);
                                }
                            }
                        }
                    });
        }else{
            String noPermission = "Can't show position since we don't have permission...";
            positionTextView.setText(noPermission);
        }

    }
}
