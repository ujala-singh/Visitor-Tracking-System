package com.example.jolly.visitormanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jolly.visitormanagement.models.visitor_details;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateInfo extends AppCompatActivity {

    FirebaseAuth mAuth;
    private TextView idView;
    private String UserID;
    private ProgressDialog dialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isReached = false;
    private static final String TAG = "UpdateInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
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
        idView = findViewById(R.id.idView);
        final EditText name = findViewById(R.id.name);
        final EditText oname = findViewById(R.id.oname);
        final EditText no = findViewById(R.id.no);
        final EditText purpose = findViewById(R.id.purpose);
        Button submit = findViewById(R.id.submit);
        idView.setText(mAuth.getCurrentUser().getEmail());
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            UserID = user.getUid();
        } catch (NullPointerException e){
            //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    visitor_details vInfo = new visitor_details();
                    if(ds.hasChild(UserID)){
                        if(ds.child(UserID).getChildrenCount() <= 3){
                            findViewById(R.id.submit).setVisibility(View.GONE);
                            Toast.makeText(UpdateInfo.this,"You have not uploaded any details yet.",Toast.LENGTH_LONG).show();
                        } else {
                            vInfo.setName(ds.child(UserID).getValue(visitor_details.class).getName());
                            vInfo.setOname(ds.child(UserID).getValue(visitor_details.class).getOname());
                            vInfo.setNo(ds.child(UserID).getValue(visitor_details.class).getNo());
                            vInfo.setPurpose(ds.child(UserID).getValue(visitor_details.class).getPurpose());
                            name.setText(vInfo.getName());
                            oname.setText(vInfo.getOname());
                            if(oname.getText().toString().contentEquals("Non-Professional")){
                                oname.setText("");
                            }
                            no.setText(vInfo.getNo());
                            purpose.setText(vInfo.getPurpose());
                            purpose.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            findViewById(R.id.submit).setVisibility(View.VISIBLE);
                        }
                    } else {
                        findViewById(R.id.submit).setVisibility(View.GONE);
                        Toast.makeText(UpdateInfo.this,"You have not uploaded any details yet.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    no.setError("Number is not correct.");
                    hideProgress();
                    return;
                }
                String vpurpose = purpose.getText().toString();
                if(vpurpose.isEmpty()){
                    purpose.setError("Purpose can not be empty.");
                    hideProgress();
                    return;
                }
                saveToCloud(vname,voname,vno,vpurpose);
                update();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void update() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }

    private void saveToCloud(String vname, String voname, String vno,String vpurpose) {
        String email = mAuth.getCurrentUser().getEmail();
        String id = mAuth.getCurrentUser().getUid();
        FirebaseDatabase fbi = FirebaseDatabase.getInstance();
        DatabaseReference fbDetails = fbi.getReference().child("Visitor_Details");//child node created named visitor_Details
        //Map<String,visitor_details> visitors = new HashMap<>();
        //visitors.put(id,new visitor_details(vname,voname,vno,id,email,vpurpose));
        if (!id.isEmpty()) {
            fbDetails.child(id).child("name").setValue(vname, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("oname").setValue(voname, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("no").setValue(vno, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            fbDetails.child(id).child("purpose").setValue(vpurpose, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateInfo.this, "success", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
