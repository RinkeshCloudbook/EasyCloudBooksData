package com.easycloudbooks.easycloudbooks.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import  	androidx.vectordrawable.graphics.drawable.VectorDrawableCompat ;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.model.DummyModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.easycloudbooks.easycloudbooks.common.DataGlobal.getRightActivityAnimation;

public class OfflineActivity extends AppCompatActivity {
    private static final String TAG = OfflineActivity.class.getSimpleName();
    SharedPreferences pref;
    private Context mContext;
    private LayoutInflater mInflater;
    private int CurrentPageTag;
    private String mDescription;
    private int mImage = 0;
    private boolean mIsRTL = false;
    private Typeface mCustomFont;
    protected List<DummyModel> mDrawerItems;
    private Handler mHandler;
    private boolean InitialBind;
    private FirebaseDatabase database;
    private DatabaseReference updateobj;
    private ValueEventListener updateobjListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitialBind = true;
        setContentView(R.layout.activity_offline);
        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);
        this.mInflater = LayoutInflater.from(mContext);
        database = FirebaseDatabase.getInstance();
        updateobj = database.getReference("GlobalData/IsOffline");
        updateobjListner = updateobj.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("false")) {
                    Config.IsOffline = false;
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent,getRightActivityAnimation(getApplicationContext()));
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updateobjListner == null) {
            updateobjListner = updateobj.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString().equals("false")) {
                        Config.IsOffline = false;
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    @Override
    protected void onPause() {
        updateobj.removeEventListener(updateobjListner);
        updateobjListner = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //donothing
    }

}
