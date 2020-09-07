package com.example.jolly.visitormanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jolly.visitormanagement.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
        ,GoogleApiClient.OnConnectionFailedListener
        ,GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCTAION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136)
    );

    //widgets
    private AutoCompleteTextView msearch;
    private ImageView mgps,minfo,mplacepicker;

    //var
    private boolean mpermission = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfused;

    private PlaceAutocompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mplace;
    private Marker marker;
    private FloatingActionButton fabSattelite;
    private ImageView btnRequestDirection;
    public LatLng origin,destination;
    private String or,des;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        msearch = (AutoCompleteTextView) findViewById(R.id.search);
        mgps = (ImageView) findViewById(R.id.ic_gps);
        minfo = (ImageView) findViewById(R.id.place_info);
        mplacepicker = (ImageView) findViewById(R.id.picker);
        btnRequestDirection = (ImageView) findViewById(R.id.btnrequest);
        btnRequestDirection.setVisibility(View.GONE);
        fabSattelite = findViewById(R.id.fabSattelite);
        getLocationPermission();
        init();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void init(){
        Log.d(TAG,"init: initializing.");
        try {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        } catch (Exception e){
            e.printStackTrace();
        }
        msearch.setOnItemClickListener(onItemClickListener);
        adapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        msearch.setAdapter(adapter);
        msearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }

                return false;
            }
        });

        mgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: clicked GPS Icon");
                getDeviceLocation();
            }
        });

        minfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: clicked place info ");
                try {
                    if(marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    } else {
                        Log.d(TAG,"onClick: Place Info "+ mplace.toString());
                        marker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG,"onClick: NullPointerException "+ e.getMessage());
                }
               // Toast.makeText(MapActivity.this, or, Toast.LENGTH_SHORT).show();
            }
        });

        mplacepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG,"onClick: GooglePlayServicesRepairableException "+ e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG,"onClick: GooglePlayServicesNotAvailableException "+ e.getMessage());
                }
            }
        });
        hideSoftKeyword();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                placeResult.setResultCallback(mUpdateResultCallBack);
            }
        }
    }

    private void geoLocate() {
        Log.d(TAG,"geoLocate: geolocating");
        String searchstr = msearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchstr,1);
        } catch (IOException e){
            Log.e(TAG,"geoLocate: IOException: "+ e.getMessage());
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG,"geoLocate: found a location: "+ address.toString());
            destination = new LatLng(address.getLatitude(),address.getLongitude());
            des = getCompleteAddressString(address.getLatitude(),address.getLongitude());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,mplace);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting Current Devices Location");
        mfused = LocationServices.getFusedLocationProviderClient(this);
        final PlaceInfo info = new PlaceInfo();
        try {
            if (mpermission) {
                final Task location = mfused.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found Location");
                            Location current = (Location) task.getResult();
                            info.setName("My Location");
                            origin = new LatLng(current.getLatitude(),current.getLongitude());
                            or = getCompleteAddressString(current.getLatitude(),current.getLongitude());
                            moveCamera(new LatLng(current.getLatitude(), current.getLongitude()), DEFAULT_ZOOM,info);
                        } else {
                            Log.d(TAG, "onComplete: Current Location is null");
                            Toast.makeText(MapActivity.this, "Unable to get Current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());

        }
    }

    private void moveCamera(LatLng latLng, Float zoom,PlaceInfo mplace) {
        Log.d(TAG, "moveCamera: moving the camera to : lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();
        mMap.setInfoWindowAdapter(new customInfoAdapter(MapActivity.this));

        if(mplace != null){
            try {
                if(!mplace.getName().equals("My Location")){
                    String snippet = "Address: " + mplace.getAddress() + "\n" +
                            "Phone No: " + mplace.getPhoneno() + "\n" +
                            "Website: " + mplace.getWebsiteuri() + "\n" +
                            "Rating: " + mplace.getRating();

                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title(mplace.getName())
                            .snippet(snippet);
                    marker = mMap.addMarker(options);
                }
            }catch (NullPointerException e){
                Log.e(TAG,"moveCamera: NullPointerException "+ e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));

        }
        hideSoftKeyword();
    }

    private void initMap() {
        Log.d(TAG, "initMap: Map is initializing");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;
        if (mpermission) {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
            getDeviceLocation();
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveStartedListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraMoveCanceledListener(this);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            init();
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            fabSattelite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            });
            /*mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);*/
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
    }

    @Override
    public void onCameraMove() {
    }

    @Override
    public void onCameraMoveCanceled() {
    }

    @Override
    public void onCameraIdle() {
    }

    private void getLocationPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mpermission = true;
                initMap();
            } else {
                android.support.v4.app.ActivityCompat.requestPermissions(this,permission
                        ,LOCTAION_PERMISSION_REQUEST_CODE);
            }
        } else {
            android.support.v4.app.ActivityCompat.requestPermissions(this,permission
                    ,LOCTAION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mpermission = false;

        switch (requestCode){
            case LOCTAION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for (int i = 0;i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mpermission = false;
                            return;
                        }
                    }
                    mpermission = true;
                    initMap();
                }
        }
    }

    private void hideSoftKeyword(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    ------------google places API autocomplete suggestions-----------
     */

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyword();

            final AutocompletePrediction item = adapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdateResultCallBack);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdateResultCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG,"onResult: place query did not complete successfully: "+ places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mplace = new PlaceInfo();
                mplace.setName(place.getName().toString());
                mplace.setAddress(place.getAddress().toString());
                mplace.setId(place.getId());
                mplace.setLatLng(place.getLatLng());
                mplace.setPhoneno(place.getPhoneNumber().toString());
                mplace.setWebsiteuri(place.getWebsiteUri());
                mplace.setRating(place.getRating());
                des = place.getAddress().toString();
                Log.d(TAG,"onResult: Place details: "+ mplace.toString());
            } catch (NullPointerException e){
                Log.e(TAG,"onResult: NullpointerException "+ e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mplace);

            places.release();
        }
    };


    @SuppressLint("LongLogTag")
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}