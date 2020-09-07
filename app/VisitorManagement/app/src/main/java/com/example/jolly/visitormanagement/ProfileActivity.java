package com.example.jolly.visitormanagement;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction=fm.beginTransaction();//Changes the Fragment through Transaction
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.content,new HomeFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.content,new FeedbackFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.content,new ReviewsFragment());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    private android.support.v4.app.FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm = getSupportFragmentManager(); //searches for fragment
        fm.beginTransaction().replace(R.id.content,new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }
}
