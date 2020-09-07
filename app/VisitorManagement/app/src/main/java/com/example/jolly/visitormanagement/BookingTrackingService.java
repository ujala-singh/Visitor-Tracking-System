package com.example.jolly.visitormanagement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BookingTrackingService extends Service implements LocationListener {

    private static final String TAG = "BookingTrackingService";
    private Context context;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 1000;
    FirebaseAuth mAuth;

    public double track_lat = 0.0;
    public double track_lng = 0.0;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 0, notify_interval);
        intent = new Intent(str_receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.context = this;


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy <<");
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void trackLocation() {
        Log.e(TAG, "trackLocation");
        String TAG_TRACK_LOCATION = "trackLocation";
        Map<String, String> params = new HashMap<>();
        params.put("latitude", "" + track_lat);
        params.put("longitude", "" + track_lng);

        Log.e(TAG, "param_track_location >> " + params.toString());

        stopSelf();
        mTimer.cancel();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /******************************/

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.e(TAG, "CAN'T GET LOCATION");
            stopSelf();
        } else {
            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {

                        Log.e(TAG, "isNetworkEnable latitude" + location.getLatitude() + "\nlongitude" + location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        track_lat = latitude;
                        track_lng = longitude;
                        saveToCloud(track_lat,track_lng);
//                        fn_update(location);
                    }
                }
            }

            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e(TAG, "isGPSEnable latitude" + location.getLatitude() + "\nlongitude" + location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        track_lat = latitude;
                        track_lng = longitude;
                        saveToCloud(track_lat,track_lng);
//                        fn_update(location);
                    }
                }
            }

            Log.e(TAG, "START SERVICE");
            trackLocation();

        }
    }


    private void saveToCloud(double latitude, double longitude) {
        String id = "";
        try {
            id = mAuth.getCurrentUser().getUid();
        } catch (NullPointerException e){
            //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date now = new Date();
        String date = sdf.format(now);
        FirebaseDatabase fbi = FirebaseDatabase.getInstance();
        DatabaseReference fbDetails = fbi.getReference().child("Visitor_Details");//child node created named visitor_Details
        //Map<String,visitor_details> visitors = new HashMap<>();
        //visitors.put(id,new visitor_details(vname,voname,vno,id,email,vpurpose));
        if (!id.isEmpty()) {
            fbDetails.child(id).child("latitude").setValue(latitude, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(BookingTrackingService.this, "success Service", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookingTrackingService.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("longitude").setValue(longitude, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(BookingTrackingService.this, "success Service", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookingTrackingService.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("TimeStamp").setValue(date, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(BookingTrackingService.this, "success Service", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookingTrackingService.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }
}