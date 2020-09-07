package com.example.jolly.visitormanagement;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jolly.visitormanagement.models.PlaceInfo;
import com.example.jolly.visitormanagement.models.visitor_details;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment{
    private View mview;
    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView lati;

    String addr;

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCTAION_PERMISSION_REQUEST_CODE = 1234;
    String date;

    public FindFragment() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date now = new Date();
        date = sdf.format(now);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_find, container, false);

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context
                .NOTIFICATION_SERVICE);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        //setupPermission();
        getLocationPermission();
        lati = (TextView) mview.findViewById(R.id.lng);
        if (isServicesOK()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int status = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (status == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 89);
            }
        }
        return mview;
    }



    private void init() {
        Button btnMap = (Button) mview.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager lm = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
                if (lm != null) {
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(getActivity(), "Please enable location services", Toast.LENGTH_SHORT).show();
                    } else {
                        Notification builder = new NotificationCompat.Builder(getActivity())
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText("You just reach, "+""+addr)
                                .setOngoing(true)
                                .setSmallIcon(R.drawable.ic_tracker)
                                .setStyle(new NotificationCompat.BigTextStyle())
                                .build();
                        builder.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotificationManager.notify(NOTIFICATION_ID,builder);
                        //startForeground(1, builder.build());

                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void setupPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int status = Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (status == PackageManager.PERMISSION_GRANTED) {
                useLocationService();
            } else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 89);
        }
    }

    private void getLocationPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                useLocationService();
            } else {
                android.support.v4.app.ActivityCompat.requestPermissions(getActivity(),permission
                        ,LOCTAION_PERMISSION_REQUEST_CODE);
            }
        } else {
            android.support.v4.app.ActivityCompat.requestPermissions(getActivity(),permission
                    ,LOCTAION_PERMISSION_REQUEST_CODE);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 89) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                useLocationService();
            else {
                Toast.makeText(getActivity(), "Please allow this  service", Toast.LENGTH_SHORT).show();
                setupPermission();
            }
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case LOCTAION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity(), "Please allow this  service", Toast.LENGTH_SHORT).show();
                            getLocationPermission();
                            return;
                        }
                    }
                    useLocationService();
                }
        }
    }

    private void useLocationService() {
        @SuppressLint("RestrictedApi") FusedLocationProviderClient client = new FusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.getException()==null)
                {
                    PlaceInfo info = new PlaceInfo();
                    try{
                        Location location= (Location) task.getResult();
                        double lat= location.getLatitude();
                        double lng=location.getLongitude();
                        String adr = getCompleteAddressString(lat,lng);
                        addr = adr;
                        lati.setText(adr);
                        //lati.setText("You are here:"+lat +","+lng);
                        //lati.setText((CharSequence) info.getName());
                        saveToCloud(lat,lng);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getActivity(), "Sorry bro!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToCloud(double latitude, double longitude) {
        String id = "";
        try {
            id = mAuth.getCurrentUser().getUid();
        } catch (NullPointerException e){
            //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        FirebaseDatabase fbi = FirebaseDatabase.getInstance();
        DatabaseReference fbDetails = fbi.getReference().child("Visitor_Details");//child node created named visitor_Details
        //Map<String,visitor_details> visitors = new HashMap<>();
        //visitors.put(id,new visitor_details(vname,voname,vno,id,email,vpurpose));
        if (!id.isEmpty()) {
            fbDetails.child(id).child("latitude").setValue(latitude, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("longitude").setValue(longitude, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("TimeStamp").setValue(date, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @SuppressLint("LongLogTag")
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
    public void onStop() {
        super.onStop();
    }
}