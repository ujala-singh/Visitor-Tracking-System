package com.example.jolly.visitormanagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;


/**
 * A simple {@link Fragment} subclass.
 */
public class QrFragment extends Fragment {

    View mview;
    ImageView sampleImage;
    FirebaseAuth mAuth;
    private static final String TAG = "QrFragment";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String UserID;

    public QrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_qr, container, false);
        sampleImage = (ImageView) mview.findViewById(R.id.qrCode);
        final TextView permi = (TextView) mview.findViewById(R.id.permi);
        final TextView qr = (TextView) mview.findViewById(R.id.qr);
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
                    try{
                        if(ds.hasChild(UserID)){
                            if(ds.child(UserID).getChildrenCount() > 3) {
                                getUID();
                            } else {
                                permi.setVisibility(View.VISIBLE);
                                qr.setVisibility(View.GONE);
                                sampleImage.setVisibility(View.GONE);
                            }
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
        return mview;
    }

    private void getUID() {
        String id = "";
        FirebaseUser user = mAuth.getCurrentUser();
        try{
            id = user.getUid();
        } catch (NullPointerException e){
            Log.e(TAG,"NullPointer :"+ e.getMessage());
            Toast.makeText(getActivity(), "Welcome New User", Toast.LENGTH_SHORT).show();
        }
        generateQR(id);
    }

    private void generateQR(String str) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            //writer.encode ka pehla parameter hi hai data string
            /*BitMatrix bitMatrix = writer.encode("anoefpo4h98rhofjwpojf9284f2fnpojwbfuiyfuy8t87fu", BarcodeFormat.QR_CODE, 768, 768);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            sampleImage.setImageBitmap(bmp);*/

            //this method is faster
            MultiFormatWriter mfw = new MultiFormatWriter();
            BitMatrix bitMatrix1 = mfw.encode(str, BarcodeFormat.QR_CODE, 768, 768);
            BarcodeEncoder bce = new BarcodeEncoder();
            Bitmap bitmap = bce.createBitmap(bitMatrix1);
            sampleImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "WriterException", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "dsfdfs", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(getActivity(), "Your QR code is ready", Toast.LENGTH_SHORT).show();
    }


}

