package com.example.jolly.visitormanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "Help";
    private TelephonyManager mTelephonyManager;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private ImageButton call;
    private MyPhoneCallListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ArrayList<String> number = new ArrayList<>();
        number.add("08953784248");
        number.add("08894451510");
        number.add("08789273218");
        number.add("08604616966");
        number.add("07607022461");
        number.add("06394698125");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, number);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner num = findViewById(R.id.type);
        call = findViewById(R.id.call);
        num.setAdapter(adapter);

        final String no = num.getSelectedItem().toString();
        Toast.makeText(this, no, Toast.LENGTH_SHORT).show();

        mTelephonyManager = (TelephonyManager)
                getSystemService(TELEPHONY_SERVICE);
        if (isTelephonyEnabled()) {
            checkForPhonePermission();
            mListener = new MyPhoneCallListener();
            mTelephonyManager.listen(mListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber(num.getSelectedItem().toString());
                }
            });
            Log.d(TAG, "Telephony is enabled");
        } else {
            Toast.makeText(this,
                    "TELEPHONY NOT ENABLED! ",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "TELEPHONY NOT ENABLED! ");
            // Disable the call button
            call.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTelephonyEnabled()) {
            mTelephonyManager.listen(mListener,
                    PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0].equalsIgnoreCase
                        (Manifest.permission.CALL_PHONE)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                } else {
                    // Permission denied.
                    Log.d(TAG, "Failure to obtain permission!");
                    Toast.makeText(this,
                            "Failure to obtain permission!",
                            Toast.LENGTH_LONG).show();
                    // Disable the call button
                    call.setVisibility(View.GONE);
                }
            }
        }
    }

    private void checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION NOT GRANTED!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            // Permission already granted. Enable the call button.
            call.setVisibility(View.VISIBLE);
        }
    }


    private boolean isTelephonyEnabled() {
        if (mTelephonyManager != null) {
            if (mTelephonyManager.getSimState() ==
                    TelephonyManager.SIM_STATE_READY) {
                return true;
            }
        }
        return false;
    }


    public void callNumber(String no) {
        // Use format with "tel:" and phone number to create phoneNumber.
        String phoneNumber = String.format("tel: %s",
                no);
        // Log the concatenated phone number for dialing.
        Log.d(TAG, no + phoneNumber);
        Toast.makeText(this,
                no + phoneNumber,
                Toast.LENGTH_LONG).show();
        // Create the intent.
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        // Set the data for the intent as the phone number.
        callIntent.setData(Uri.parse(phoneNumber));
        // If package resolves to an app, send intent.
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }

    private class MyPhoneCallListener extends PhoneStateListener {
        private boolean returningFromOffHook = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Define a string for the message to use in a toast.
            String message = getString(R.string.phone_status);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // Incoming call is ringing
                    message = message +
                            getString(R.string.ringing) + incomingNumber;
                    Toast.makeText(HelpActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Phone call is active -- off the hook
                    message = message + getString(R.string.offhook);
                    Toast.makeText(HelpActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    returningFromOffHook = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // Phone is idle before and after phone call.
                    // If running on version older than 19 (KitKat),
                    // restart activity when phone call ends.
                    message = message + getString(R.string.idle);
                    Toast.makeText(HelpActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    if (returningFromOffHook) {
                        // No need to do anything if >= version K
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            Log.i(TAG, getString(R.string.restarting_app));
                            // Restart the app.
                            Intent intent = getPackageManager()
                                    .getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                    break;
                default:
                    message = message + "Phone off";
                    Toast.makeText(HelpActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
            }
        }
    }
}


