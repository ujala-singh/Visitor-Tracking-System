package com.example.jolly.visitormanagement;
;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jolly.visitormanagement.models.PermissionModel;
import com.example.jolly.visitormanagement.models.visitor_details;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionFragment extends Fragment {


    private String UserID;
    View mview;
//    FloatingActionButton fabUnlock;
    private ArrayList<String> movieRatings=new ArrayList<>();
    private ArrayList<PermissionModel> itemList = new ArrayList<>();
    RecyclerView containerRV;
    public static String loc;
    private ProgressDialog dialog;
    FirebaseAuth mAuth;

    public PermissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_permission, container, false);
        containerRV = (RecyclerView) mview.findViewById(R.id.containerRV);
        final TextView permi = (TextView) mview.findViewById(R.id.permi);
//        fabUnlock = (FloatingActionButton) mview.findViewById(R.id.fabUnlock);
//        fabUnlock.setVisibility(View.GONE);
        showProgress();
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            UserID = user.getUid();
        } catch (NullPointerException e){
            //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if(UserID == null){
            hideProgress();
            permi.setVisibility(View.VISIBLE);
            CountDownTimer timer = new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    permi.setVisibility(View.INVISIBLE); //(or GONE)
                    showAlertDialog(getActivity(),"Upload Information","Please fill your details first.",true);
                }
            }.start();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    visitor_details vInfo = new visitor_details();
                    try{
                        if(ds.hasChild(UserID)){
                            vInfo.setType(ds.child(UserID).getValue(visitor_details.class).getType());
                            vInfo.setGender(ds.child(UserID).getValue(visitor_details.class).getGender());
                            movieRatings.add(0,vInfo.getType());
                            movieRatings.add(1,vInfo.getGender());
                            if(movieRatings.get(0) == null && movieRatings.get(1) == null){
                                hideProgress();
                                permi.setVisibility(View.VISIBLE);
                                CountDownTimer timer = new CountDownTimer(5000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        permi.setVisibility(View.INVISIBLE); //(or GONE)
                                        showAlertDialog(getActivity(),"Upload Information","Please fill your details first.",true);
                                    }
                                }.start();
                            } else {
                                permi.setVisibility(View.GONE);
                                Log.w("My List",movieRatings.get(0)+" "+movieRatings.get(1));
                                updateList(movieRatings);
                            }
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //this function tells the system that it will be a linear list of cards in vertical orientation.
        containerRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Log.w("My List",movieRatings.get(0)+" "+movieRatings.get(1));
        /*MyAdapter adapter = new MyAdapter();
        containerRV.setAdapter(adapter);*/
        return mview;

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


    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        alertDialog.setCancelable(false);

        if(status != null)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_success : R.drawable.ic_fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, UploadInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }

        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void updateList(ArrayList<String> movieRatings) {
        generateData(movieRatings.get(0),movieRatings.get(1));
        MyAdapter adapter = new MyAdapter(itemList,R.layout.permissions_card_items);
        containerRV.setAdapter(adapter);
        hideProgress();
    }

    public void generateData(String visitorType, String visitorGender) {
        //itemList = new ArrayList();
        if (visitorType.compareToIgnoreCase("Student") == 0) {
            itemList.add(new PermissionModel("Computer Center 3".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Lecture Theatre".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Computer Center 2".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Computer Center 1".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Faculty Apartment".toUpperCase(), "Denied".toUpperCase()));
            itemList.add(new PermissionModel("Visitor Hostel 1".toUpperCase(), "Denied".toUpperCase()));
            if (visitorGender.compareToIgnoreCase("male") == 0) {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Denied".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "Denied".toUpperCase()));
            } else {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Granted".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "granted".toUpperCase()));
            }
        } else if (visitorType.compareToIgnoreCase("Visitor") == 0) {
            itemList.add(new PermissionModel("Computer Center 3".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Lecture Theatre".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Computer Center 2".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Computer Center 1".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Faculty Apartment".toUpperCase(), "Denied".toUpperCase()));
            itemList.add(new PermissionModel("Visitor Hostel 1".toUpperCase(), "Granted".toUpperCase()));
            if (visitorGender.compareToIgnoreCase("male") == 0) {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Denied".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "Denied".toUpperCase()));
            } else {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Granted".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "granted".toUpperCase()));
            }
//            itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Denied".toUpperCase()));
//            itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "Denied".toUpperCase()));
        } else if (visitorType.compareToIgnoreCase("Faculty") == 0) {
            itemList.add(new PermissionModel("Computer Center 3".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Lecture Theatre".toUpperCase(), "Granted".toUpperCase()));
           itemList.add(new PermissionModel("Computer Center 2".toUpperCase(), "Granted".toUpperCase()));
             itemList.add(new PermissionModel("Computer Center 1".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Faculty Apartment".toUpperCase(), "Granted".toUpperCase()));
            itemList.add(new PermissionModel("Visitor Hostel 1".toUpperCase(), "Denied".toUpperCase()));
//            itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Granted".toUpperCase()));
//            itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "Granted".toUpperCase()));
            if (visitorGender.compareToIgnoreCase("male") == 0) {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Denied".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "Denied".toUpperCase()));
            } else {
                itemList.add(new PermissionModel("Girls' Hostel 1".toUpperCase(), "Granted".toUpperCase()));
                itemList.add(new PermissionModel("Girls' Hostel 2".toUpperCase(), "granted".toUpperCase()));
            }

        }
    }

    public int getSize() {
        return itemList.size();
    }

    class MyAdapter extends RecyclerView.Adapter<PermissionHolder> {

        ArrayList<PermissionModel> movieRatings;
        int permissions_card_items;

        public MyAdapter(ArrayList<PermissionModel> movieRatings, int permissions_card_items) {
            this.movieRatings = movieRatings;
            this.permissions_card_items = permissions_card_items;
        }

        @NonNull
        @Override
        public PermissionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return null;
            // an object of layout file is created with ref v
            View itemView = LayoutInflater.from(getActivity()).inflate(permissions_card_items, parent, false);

            return new PermissionHolder(itemView);
        }

        public void onBindViewHolder(@NonNull final PermissionHolder holder, int position) {

            FirebaseDatabase db1 = FirebaseDatabase.getInstance();
            final DatabaseReference ref1 = db1.getReference();
            FirebaseUser user = mAuth.getCurrentUser();
            try {
                UserID = user.getUid();
            } catch (NullPointerException e){
                //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            final PermissionModel item = movieRatings.get(position);
            holder.tvPerm.setText(item.getTitle());
            String str = item.getContent();
            holder.tvContent.setText(str);
            if (str.compareToIgnoreCase("Granted") == 0) {
                holder.tvContent.setTextColor(Color.GREEN);
            } else {
                holder.tvContent.setTextColor(Color.RED);
                holder.fabUnlock.setVisibility(View.VISIBLE);
                ItemClickSupport.addTo(containerRV).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                        v.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                if(position != RecyclerView.NO_POSITION){
                                    PermissionModel clickedDataItem = movieRatings.get(position);
                                    loc = clickedDataItem.getTitle();
                                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                visitor_details vInfo = new visitor_details();
                                                try{
                                                    if(ds.hasChild(UserID)){
                                                        vInfo.setLocation(ds.child(UserID).getValue(visitor_details.class).getLocation());
                                                        if(vInfo.getLocation().isEmpty()){
                                                            update();
                                                        } else {
                                                            Toast.makeText(getActivity(), "You have requested once you have to wait until your previous" +
                                                                    " request will be granted.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                    //update();
                                }
                            }
                        });
                    }
                });

            }
        }
        @Override
        public int getItemCount() {
            return movieRatings.size();
        }
    }

    private void update() {
        Intent i = new Intent(getActivity(),RequestActivity.class);
        startActivity(i);
    }


}

