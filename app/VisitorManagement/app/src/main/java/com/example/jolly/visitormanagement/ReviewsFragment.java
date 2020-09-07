package com.example.jolly.visitormanagement;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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
public class ReviewsFragment extends Fragment {

    private ArrayList<visitor_details> movieRatings=new ArrayList<>();
    private RecyclerView rvReviews;
    View v;
    private final String TAG = "Reviews";
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_reviews, container, false);
        rvReviews = (RecyclerView) v.findViewById(R.id.rv);
        rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        DatabaseReference movieDb = FirebaseDatabase.getInstance().getReference().child("Visitor_Details");
        movieDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChildren())
                {
                    movieRatings.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        movieRatings.add(snapshot.getValue(visitor_details.class));
                    }
                    updateList(movieRatings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void updateList(ArrayList<visitor_details> movieRatings)
    {
        Adapter adapter=new Adapter(movieRatings,R.layout.review_card);
        rvReviews.setAdapter(adapter);

    }

    class  Adapter extends RecyclerView.Adapter<Adapter.Holder>
    {
        ArrayList<visitor_details> movieRatings;
        int layoutRes;

        Adapter(ArrayList<visitor_details> movieRatings, int layoutRes) {
            this.movieRatings = movieRatings;
            this.layoutRes = layoutRes;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(getActivity().getBaseContext()).inflate(layoutRes,parent,false);//use Reviews.this instead of getBaseContext

            return new Holder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(Holder holder, int position)
        {
            visitor_details model = movieRatings.get(position);
            holder.tvEmail.setText(model.visitor_email);
            holder.tvComment.setText(model.comment);
            if(holder.tvComment.getText().toString().isEmpty()){
                holder.tvComment.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                holder.tvComment.setText("This Visitor didn't provide any feedback \nyet about the campus.");
            }
            holder.tvMovie.setText(model.name);
            holder.ratingBar.setRating(model.rating);
            holder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieRatings.size();
        }

        class  Holder extends RecyclerView.ViewHolder
        {
            TextView tvMovie,tvEmail,tvComment;
            RatingBar ratingBar;
            Holder(View itemView) {
                super(itemView);
                tvMovie=itemView.findViewById(R.id.tvMovie);
                tvEmail=itemView.findViewById(R.id.tvEmail);
                tvComment=itemView.findViewById(R.id.tvComment);
                ratingBar=itemView.findViewById(R.id.rb);
            }
        }
    }

}
