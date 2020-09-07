package com.example.jolly.visitormanagement;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth mAuth;
    private String UserID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final Context mContext = this;
    private TextView idView;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView, mUserTextView, mEmailTextView;
    private CircleImageView mprofile;
    private ProgressDialog mProgressDialog;
    public static NavigationView navigationView;
    public DrawerLayout drawer;
    FragmentManager FM;
    FragmentTransaction FT;
    public ViewPager view;
    public TabLayout tabs;
    private SignInButton sign;
    private int mCurrentPage;
    PermissionFragment permissionFragment;
    QrFragment qrFragment;
    FindFragment findFragment;
    GalleryFragment galleryFragment;
    AboutFragment aboutFragment;

    MyAdapter adapter;
    Toolbar toolbar;
    private boolean flag;

    public static Uri uri;
    DatabaseReference ref;

    //service
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //service
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceive.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,
                    pendingIntent);
        }

        mNotificationManager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up();
            }
        });
        mStatusTextView = (TextView) findViewById(R.id.status);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth=FirebaseAuth.getInstance();

        // Views
        mUserTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        mEmailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.mail);
        mprofile = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.img);

        // Button listeners

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        sign = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        sign.setSize(SignInButton.SIZE_WIDE);
        sign.setScopes(gso.getScopeArray());
        // [END customize_button]

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

        /*TrackerActivity track = new TrackerActivity();
        track.startTrackerService();*/
    }


    private void up() {
        Intent i = new Intent(this,HelpActivity.class);
        startActivity(i);
    }

    private void init(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        view = (ViewPager) findViewById(R.id.view);
        view.setOffscreenPageLimit(5);
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupViewPager(view);
        tabs.setupWithViewPager(view);
        view.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(view));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view.setCurrentItem(tab.getPosition(),false);
                /*Fragment frag = null;
                switch (tab.getPosition()) {
                    case 0:
                        frag = new PermissionFragment();
                        break;
                    case 1:
                        frag = new QrFragment();
                        break;
                    case 2:
                        frag = new FindFragment();
                        break;
                    case 3:
                        frag = new GalleryFragment();
                        break;
                    case 4:
                        frag = new AboutFragment();
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                performNoBackStackTransaction(fm,frag);*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                view.setCurrentItem(position,false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager view) {
        adapter = new MyAdapter(getSupportFragmentManager());
        permissionFragment = new PermissionFragment();
        qrFragment = new QrFragment();
        findFragment = new FindFragment();
        galleryFragment = new GalleryFragment();
        aboutFragment = new AboutFragment();
        adapter.addFragment(permissionFragment,"Permission");
        adapter.addFragment(qrFragment,"QR Code");
        adapter.addFragment(findFragment,"Find Me");
        adapter.addFragment(galleryFragment,"Campus Gallery");
        adapter.addFragment(aboutFragment,"Faculty Profiles");
        view.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            GoogleSignInAccount account = result.getSignInAccount();
            try {
                firebaseAuthWithGoogle(account);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    String idTokenString = "";
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG,"Google User ID:" + account.getId());

        Log.d(TAG,"Google JWT" + account.getIdToken());

        idTokenString = account.getIdToken();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"SignWithCredentials:Oncomplete:" + task.isSuccessful());

                if(task.isSuccessful()){
                    Log.d(TAG,"FirebaseUser Access Token:" + task.getResult());
                }
                else {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(Home.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            // Views inside NavigationView's header
            mUserTextView.setText(acct.getDisplayName());
            mEmailTextView.setText(acct.getEmail());
            uri = acct.getPhotoUrl();
            Picasso.with(mContext)
                    .load(uri)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .error(android.R.drawable.sym_def_app_icon)
                    .into(mprofile);
            updateUI(true);
            flag = true;
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
            flag = false;

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(flag){
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                ref = db.getReference();
                FirebaseUser user = mAuth.getCurrentUser();
                try {
                    UserID = user.getUid();
                } catch (NullPointerException e){
                    //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                if(ds.hasChild(UserID)){
                                    if(ds.child(UserID).getChildrenCount() <= 3){
                                        Toast.makeText(Home.this, "You have to upload your details first.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        update1();
                                    }
                                }
                            }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {
                Toast.makeText(this, "You are signed out.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void update1() {
        Intent i = new Intent(this,ProfileActivity.class);
        startActivity(i);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id == R.id.nav_upload){
            if(flag){
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference();
                FirebaseUser user = mAuth.getCurrentUser();
                try {
                    UserID = user.getUid();
                } catch (NullPointerException e){
                    //Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*for(DataSnapshot ds1 : ds.getChildren()){
                                if(ds.hasChild(UserID)){
                                    if(ds1.getChildrenCount() <= 2){
                                        update();
                                    } else{
                                        Toast.makeText(Home.this, "Your data has been uploaded.", Toast.LENGTH_SHORT).show();
                                        hideItem();
                                    }
                                } else {
                                    update();
                                }
                            }*/
                            try{
                                if(ds.hasChild(UserID)){
                                    if(ds.child(UserID).getChildrenCount() <= 3){
                                        update();
                                    }  else{
                                        Toast.makeText(Home.this, "Your data has been uploaded.", Toast.LENGTH_SHORT).show();
                                        hideItem();
                                    }
                                } else {
                                    update();
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {
                Toast.makeText(this, "You are signed out.", Toast.LENGTH_SHORT).show();
            }
        }else if(id == R.id.nav_update){
            if(flag){
                Intent i = new Intent(this,UpdateInfo.class);
                startActivity(i);
                //finish();
            } else {
                Toast.makeText(this, "You are signed out.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_gallery) {
            if(flag){
                view.setCurrentItem(3);
            } else {
                Toast.makeText(this, "You need to sign in first.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_permission) {
            if(flag){
                view.setCurrentItem(0);
            } else {
                Toast.makeText(this, "You need to sign in first.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_help) {
            Intent i = new Intent(this,HelpActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            if(flag) {
                view.setCurrentItem(4);
            } else {
                Toast.makeText(this, "You need to sign in first.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void update() {
        Intent i = new Intent(this,UploadInfo.class);
        startActivity(i);
        finish();
    }

    public static void hideItem()
    {
        //NavigationView navigationView = (NavigationView) findviewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_upload).setVisible(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = null;
                if (cm != null) {
                    info = cm.getActiveNetworkInfo();
                }
                String networkType = null;
                if (info != null) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        networkType = "WIFI";
                    }
                    else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                        networkType = "mobile";
                    }
                }
                if(networkType != null){
                    signIn();
                } else {
                    Toast.makeText(mContext, "Enable your Mobile Data or WiFi.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        flag = false;
                        // [END_EXCLUDE]
                    }
                });
        mUserTextView.setText("");
        mEmailTextView.setText("");
        String location = "@drawable/account";
        int res = getResources().getIdentifier(location,null,getPackageName());
        mprofile.setImageResource(res);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_upload).setVisible(true);
    }
    // [END signOut]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Welcome");
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            //service
            if (alarmManager == null) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceive.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,
                        pendingIntent);
            }

            String name = mUserTextView.getText().toString();
            Notification builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Welcome To IIITA, "+""+name)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.account)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .build();
            builder.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFICATION_ID,builder);

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = null;
            if (cm != null) {
                info = cm.getActiveNetworkInfo();
            }
            String networkType = null;
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    networkType = "WIFI";
                }
                else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                    networkType = "mobile";
                }
            }
            if(networkType != null){
                hideProgressDialog();
                init();
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.status).setVisibility(View.GONE);
                findViewById(R.id.containerView).setVisibility(View.VISIBLE);
                findViewById(R.id.tabs).setVisibility(View.VISIBLE);
                findViewById(R.id.view).setVisibility(View.VISIBLE);
            } else {
                hideProgressDialog();
                findViewById(R.id.tabs).setVisibility(View.GONE);
                Toast.makeText(mContext, "Enable your Mobile Data or WiFi.", Toast.LENGTH_SHORT).show();
            }


            /*int x = view.getCurrentItem();
            Fragment frag = null;
            switch (x) {
                case 0:
                    frag = new PermissionFragment();
                    break;
                case 1:
                    frag = new QrFragment();
                    break;
                case 2:
                    frag = new FindFragment();
                    break;
                case 3:
                    frag = new GalleryFragment();
                    break;
                case 4:
                    frag = new AboutFragment();
                    break;
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.containerView, frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();*/
        } else {
            hideProgressDialog();
            mStatusTextView.setText(R.string.signed_out);
            //findViewById(R.id.upload).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.status).setVisibility(View.VISIBLE);
            findViewById(R.id.containerView).setVisibility(View.GONE);
            findViewById(R.id.tabs).setVisibility(View.GONE);
            findViewById(R.id.view).setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}