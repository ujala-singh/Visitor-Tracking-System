package com.example.jolly.visitormanagement;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {


    View v;
    FirebaseAuth mAuth;
    private String UserID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Profile";
    private CircleImageView mprofile;
    private final Context mContext = getActivity();
    private ProgressDialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

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

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll);
        final TextView email = (TextView) v.findViewById(R.id.mail);
        final TextView name = (TextView) v.findViewById(R.id.name);
        final TextView org = (TextView) v.findViewById(R.id.org);
        final TextView no = (TextView) v.findViewById(R.id.no);
        final TextView type = (TextView) v.findViewById(R.id.type);
        final TextView pur = (TextView) v.findViewById(R.id.pur);
        final TextView gender = (TextView) v.findViewById(R.id.gender);
        mprofile = (CircleImageView) v.findViewById(R.id.img);
        showProgress();
        Uri uri = Home.uri;
        Picasso.with(mContext)
                .load(uri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(mprofile);
        email.setText("Email-Id : "+ mAuth.getCurrentUser().getEmail());
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UserID = user.getUid();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    visitor_details vInfo = new visitor_details();
                    if(ds.hasChild(UserID)){
                        vInfo.setName(ds.child(UserID).getValue(visitor_details.class).getName());
                        vInfo.setOname(ds.child(UserID).getValue(visitor_details.class).getOname());
                        vInfo.setNo(ds.child(UserID).getValue(visitor_details.class).getNo());
                        vInfo.setPurpose(ds.child(UserID).getValue(visitor_details.class).getPurpose());
                        vInfo.setType(ds.child(UserID).getValue(visitor_details.class).getType());
                        vInfo.setGender(ds.child(UserID).getValue(visitor_details.class).getGender());
                        name.setText("Name : "+vInfo.getName());
                        org.setText("Organization : "+vInfo.getOname());
                        if(org.getText().toString().contentEquals("Non-Professional")){
                            org.setText("");
                        }
                        no.setText("Mobile Number : "+vInfo.getNo());
                        pur.setText("Purpose : "+vInfo.getPurpose());
                        type.setText("Type : "+vInfo.getType());
                        gender.setText("Gender : "+vInfo.getGender());
                    } else {
                        Toast.makeText(getActivity(),"You have not uploaded any details yet.",Toast.LENGTH_SHORT).show();
                    }
                }
                hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ll.setOrientation(LinearLayout.VERTICAL);
        //ll.setOrientation(LinearLayout.HORIZONTAL);
        return v;
    }

    public  void showProgress()
    {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait....");
        dialog.setTitle("Loading");
        dialog.setCancelable(false);
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
