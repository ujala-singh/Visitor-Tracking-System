package com.example.jolly.visitormanagement;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {


    FirebaseAuth mAuth;
    private String UserID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Feedback";
    private ProgressDialog dialog;
    View v;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_feedback, container, false);
        mAuth = FirebaseAuth.getInstance();
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
        TextView idView = (TextView) v.findViewById(R.id.idView);
        idView.setText(mAuth.getCurrentUser().getEmail());
        final EditText etcomment=(EditText) v.findViewById(R.id.etcomment);
        final RatingBar ratingBar=(RatingBar) v.findViewById(R.id.ratingBar);
        final Button save_btn=(Button) v.findViewById(R.id.save_btn);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UserID = user.getUid();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds1 : ds.getChildren()){
                        if(ds.hasChild(UserID)){
                            save_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showProgress();
                                    String comment=etcomment.getText().toString();
                                    float rating=ratingBar.getRating();
                                    saveToCloud(comment,rating);
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return v;
    }

    private void saveToCloud(String comment, float rating) {
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
            fbDetails.child(id).child("comment").setValue(comment, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            fbDetails.child(id).child("rating").setValue(rating, new DatabaseReference.CompletionListener() {
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
        hideProgress();
        update();
    }

    private void update() {
        Intent i = new Intent(getActivity(),Home.class);
        startActivity(i);
    }

    public  void showProgress()
    {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading Your feedback");
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

}
