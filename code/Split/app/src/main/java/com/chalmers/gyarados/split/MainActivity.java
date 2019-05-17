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
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.widget.AdapterView;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.Arrays;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * The main activity of the app
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Constants
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = "MainActivity";


    /**
     * Used to receive the address of a position
     */
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
    /**
     * Textview to show the current position
     */
    private TextView currentPositionTextView; //Textview to show the current location
    /**
     * The "Find a trip" button
     */
    private Button findButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        //UI
        findButton = findViewById(R.id.findbutton);
        disableFindButton();
        findButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,GroupActivity.class);
            startActivity(intent);
        });

        currentPositionTextView = findViewById(R.id.currentPositionTextView);
        Spinner companionSpinner = findViewById(R.id.companionSpinner);
        companionSpinner.setOnItemSelectedListener(new SpinnerListener());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        resultReceiver=new AddressResultReceiver(new Handler());

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        initMapFragment();
        initAutocompleteSupportFragment();

    }




    private void disableFindButton() {
        findButton.setEnabled(false);
        findButton.setBackgroundResource(R.drawable.disabled_button);
    }
    private void enableFindButton() {
        findButton.setEnabled(true);
        findButton.setBackgroundResource(R.drawable.confirm_button);
    }



    //-----------------------------MAP--------------------------------------------------------------
    private void initMapFragment(){
        //Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }else{
            errorWhenInitializingMapFragment();
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
     * @param requestCode The code for the permission asked
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
     * @param googleMap A map ready to use
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
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                        CurrentSession.setCurrentLatitude(mLastKnownLocation.getLatitude());
                        CurrentSession.setCurrentLongitude(mLastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));


                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    }
                    StartAddressIntentService();
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Updates the map
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


    //------------------------------------FETCHING THE ADDRESS--------------------------------------

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
        currentPositionTextView.setText(address);
    }

    /**
     * Receives the results of the address intent service
     */
    private class AddressResultReceiver extends ResultReceiver {

        private String addressOutput;

        AddressResultReceiver(Handler handler) {
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

    //--------------------------------PLACE SELECTION-----------------------------------------------
    private void initAutocompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            autocompleteFragment.setCountry("SE");
            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener());

            AppCompatImageButton clear_autocomplete = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
            clear_autocomplete.setOnClickListener(view -> {
                disableFindButton();
                autocompleteFragment.setText("");
            });
        }else{
            errorWhenInitializingAutocompleteFragment();
        }

    }



    private class PlaceSelectionListener implements com.google.android.libraries.places.widget.listener.PlaceSelectionListener {

        @Override
        public void onPlaceSelected(@NonNull Place place) {
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            CurrentSession.setDesinationLatitude(place.getLatLng().latitude);
            CurrentSession.setDestinationLongitude(place.getLatLng().longitude);
            enableFindButton();
        }

        @Override
        public void onError(@NonNull Status status) {
            Log.i(TAG, "An error occurred: " + status);
        }
    }
    //--------------------------------SPINNER-------------------------------------------------------

    /**
     * A class that listens to a spinner
     */
    private class SpinnerListener implements AdapterView.OnItemSelectedListener{

        private int[] items = {1,2,3};
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            CurrentSession.setNrOfTravelers(items[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    //--------------------------------ERROR---------------------------------------------------------

    private void errorWhenInitializingMapFragment() {

    }

    private void errorWhenInitializingAutocompleteFragment() {

    }







}