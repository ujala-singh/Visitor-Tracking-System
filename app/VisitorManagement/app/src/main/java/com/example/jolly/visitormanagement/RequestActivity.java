package com.example.jolly.visitormanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private String TAG = "Request";
    Switch enable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        final TextView loc = (TextView) findViewById(R.id.loc);
        loc.setText(PermissionFragment.loc);
        enable = (Switch) findViewById(R.id.enable);
        final EditText reason = (EditText) findViewById(R.id.reason);
        ImageButton submit = (ImageButton) findViewById(R.id.request);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(enable.isChecked()){
                    enable.setText("Enabled");
                    switchColor(isChecked);
                } else {
                    enable.setText("Disabled");
                    switchColor(isChecked);
                }
            }
        });

        mAuth=FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enable.isChecked()) {
                    String id = "";
                    try {
                        id = mAuth.getCurrentUser().getUid();
                    } catch (NullPointerException e) {
                        //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    FirebaseDatabase fbi = FirebaseDatabase.getInstance();
                    DatabaseReference fbDetails = fbi.getReference().child("Visitor_Details");//child node created named visitor_Details
                    if (!id.isEmpty()) {
                        if(reason.getText().toString().isEmpty()){
                            reason.setError("Reason can not be empty.");
                        } else {
                            fbDetails.child(id).child("location").setValue(loc.getText().toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(RequestActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RequestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            fbDetails.child(id).child("reason").setValue(reason.getText().toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(RequestActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RequestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            update();
                        }
                    }
                } else {
                    Toast.makeText(RequestActivity.this, "You didn't enabled the switch button.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void switchColor(boolean checked) {
            enable.getThumbDrawable().setColorFilter(checked ? Color.GREEN : Color.WHITE, PorterDuff.Mode.MULTIPLY);
            enable.getTrackDrawable().setColorFilter(!checked ? Color.RED : Color.RED, PorterDuff.Mode.MULTIPLY);
    }

    private void update(){
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }
}
