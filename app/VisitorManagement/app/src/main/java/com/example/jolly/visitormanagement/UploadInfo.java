package com.example.jolly.visitormanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jolly.visitormanagement.models.visitor_details;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadInfo extends AppCompatActivity {

    FirebaseAuth mAuth;
    private TextView idView;
    private ProgressDialog dialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "UploadInfo";
    private String UserID;
    double lati;
    double longi;
    private Spinner type,state;
    String date;
    public UploadInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date now = new Date();
        date = sdf.format(now);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        ArrayList<String> vtype = new ArrayList<>();
        vtype.add("Student");
        vtype.add("Faculty");
        vtype.add("Visitor");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vtype);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayList<String> vstate = new ArrayList<>();
        vstate.add("Uttar Pradesh");
        vstate.add("Andhra Pradesh");
        vstate.add("Madhya Pradesh");
        vstate.add("Himachal Pradesh");
        vstate.add("West Bengal");
        vstate.add("Bihar");
        vstate.add("Rajasthan");
        vstate.add("Maharashtra");

        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vstate);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        idView = findViewById(R.id.idView);
        final EditText name = findViewById(R.id.name);
        final EditText oname = findViewById(R.id.oname);
        final EditText no = findViewById(R.id.no);
        final EditText purpose = findViewById(R.id.purpose);
        purpose.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        final Button submit = findViewById(R.id.submit);
        final EditText age = findViewById(R.id.age);
        type = findViewById(R.id.type);
        type.setAdapter(adapter);
        state = findViewById(R.id.state);
        state.setAdapter(adapter1);
        setupPermission();
        RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radio);
        final String[] gender = new String[1];
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                gender[0] = radioButton.getText().toString();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                String vname = name.getText().toString();
                if(vname.isEmpty()){
                    name.setError("Name can not be empty.");
                    hideProgress();
                    return;
                }
                String voname = oname.getText().toString();
                if(voname.isEmpty()){
                    voname = "Non-Professional";
                }
                String vno = no.getText().toString();
                if(vno.isEmpty()){
                    no.setError("Number can not be empty.");
                    hideProgress();
                    return;
                }
                if(vno.length() > 10){
                    no.setError("Number can not be empty.");
                    hideProgress();
                    return;
                }
                String vpurpose = purpose.getText().toString();
                if(vpurpose.isEmpty()){
                    purpose.setError("Purpose can not be empty.");
                    hideProgress();
                    return;
                }
                int ag = Integer.parseInt(age.getText().toString().trim());
                saveToCloud(vname,voname,vno,vpurpose,lati,longi, gender[0],ag);
                }
        });
    }

    public void setupPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int status = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (status == PackageManager.PERMISSION_GRANTED) {
                useLocationService();
            } else
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 89);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 89) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                useLocationService();
            else {
                Toast.makeText(UploadInfo.this, "Please allow this  service", Toast.LENGTH_SHORT).show();
                setupPermission();
            }
        }
    }

    private void useLocationService() {
        @SuppressLint("RestrictedApi") FusedLocationProviderClient client = new FusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(UploadInfo.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.getException()==null)
                {
                    try{
                        Location location=task.getResult();
                        double lat= location.getLatitude();
                        double lng=location.getLongitude();
                        lati = lat;
                        longi = lng;
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(UploadInfo.this, "Sorry bro!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToCloud(String vname, String voname, String vno, String vpurpose, double lati, double longi, String gender, int age) {
        String email = mAuth.getCurrentUser().getEmail();
        String id = mAuth.getCurrentUser().getUid();
        String uid = id.substring(id.length()-5);
        FirebaseDatabase fbi = FirebaseDatabase.getInstance();
        DatabaseReference fbDetails = fbi.getReference().child("Visitor_Details");//child node created named visitor_Details
        //Map<String,visitor_details> visitors = new HashMap<>();
        //visitors.put(id,new visitor_details(vname,voname,vno,id,email,vpurpose));
        fbDetails.child(id).setValue(new visitor_details(vname,voname,vno,type.getSelectedItem().toString(),uid,email,vpurpose,lati,longi,"",0,gender,
                age,state.getSelectedItem().toString(),"","",date,Home.uri.toString()), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null)
                    Toast.makeText(UploadInfo.this, "success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(UploadInfo.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
        update();
    }

    private void update() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }

    public  void showProgress()
    {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Your details.");
        dialog.setTitle("Working");
        dialog.show();
    }
    public void hideProgress()
    {
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        updateUi(user);

    }
    public void updateUi(FirebaseUser user)
    {
        if(user!=null)
        {
            idView.setText(user.getEmail());
        }
        else
        {
            Toast.makeText(this, "Your Login was not successful.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,Home.class);
        startActivity(i);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
