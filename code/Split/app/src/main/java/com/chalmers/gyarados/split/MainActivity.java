package com.chalmers.gyarados.split;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.view.View;
import android.widget.Button;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * The main activity of the app
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Constants
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;


    private AddressResultReceiver resultReceiver;

    /**
     * Used to get last known position
     */
    private FusedLocationProviderClient fusedLocationClient;
    /**
     * Our map
     */
    private GoogleMap mMap;

    /**
     * The last known location
     */
    private Location mLastKnownLocation;

    /**
     * The default location, right now Sidney
     */
    private LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    /**
     * True if user has given location permission, false otherwise
     */
    private boolean mLocationPermissionGranted;



    //UI
    private TextView destinationTextView; //Textview to show the current location
    private Spinner companionSpinner;
    private Button findButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        findButton = findViewById(R.id.findbutton);
        EditText ip_address = findViewById(R.id.ip_address);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GroupActivity.class);
                intent.putExtra("IP",ip_address.getText().toString());
                intent.putExtra("companions", companionSpinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        resultReceiver=new AddressResultReceiver(new Handler());

        //UI
        destinationTextView = findViewById(R.id.destinationTextView);
        companionSpinner = findViewById(R.id.companionSpinner);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Exception needs to be changed after the method has been updated
                Log.i("Exception", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                //TODO: implement onError method when we understaand TAG
            }
        });
    }

    private void lauchAutocomplete() {
        //int AUTOCOMPLETE_REQUEST_CODE = 1;
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Dont understand TAG so replaced it with "ëxception"
                Log.i("Exception" ,"Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Dont understand TAG so replaced it with "exception"
                Log.i("Exception", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
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

        updateLocationUI();
        getDeviceLocation();

    }


    /**
     * Called when the map is ready.
     * Tries to get the permission that is needed and updates the map and the current position text field.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        getLocationPermission();

        //updateLocationUI();

        //getDeviceLocation();


    }

    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));


                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                        StartAddressIntentService();
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     *
     */
    private void updateLocationUI() {
        if(mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                //getLocationPermission();

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Starts the intent service that tries to get the address based on our current location.
     */
    private void StartAddressIntentService() {
        if(mLastKnownLocation!=null){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastKnownLocation);
        startService(intent);
        }else{
            setAddress(getString(R.string.no_address_found));
        }
    }

    private void setAddress(String address) {
        destinationTextView.setText(address);
    }

    /**
     * Receives the results of the address intent service
     */
    private class AddressResultReceiver extends ResultReceiver {

        private String addressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }
            displayAddressOutput();


        }

        private void displayAddressOutput() {
            setAddress(addressOutput);
        }


    }







}