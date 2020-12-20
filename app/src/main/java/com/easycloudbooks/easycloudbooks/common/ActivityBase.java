package com.easycloudbooks.easycloudbooks.common;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.OfflineActivity;
import com.easycloudbooks.easycloudbooks.activity.UpdateActivity;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.app.ECBGetCurrentUser;
import com.easycloudbooks.easycloudbooks.constants.Constants;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

public class ActivityBase extends AppCompatActivity implements Constants {
    public static final String TAG = "ActivityBase";

    private ProgressDialog pDialog;
    ActivityBase _this;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        _this = this;
        initpDialog();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                   // FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.UPDATE_UI)) {
                   // Log.w(TAG, "onReceive: UPDATE_UI" );

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AddUpdateRef();
        }

    }

    private boolean IsPaused = false;
    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.UPDATE_UI));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
       // NotificationUtils.clearNotifications(getApplicationContext());
        IsPaused = false;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AddUpdateRef();
           // Log.i(TAG, FirebaseAuth.getInstance().getCurrentUser() != null ? "true" : "false");
           /* FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        new GetCurrentUser(getApplicationContext(), mUser.getIdToken(false).getResult().getToken(), MMenuReuired).execute();
                    }
                }
            });*/

            /*mHandler = new Handler();
            if (!HaveCustomMenu) {
                if (Config.IsOffline == true) {
                    selectItem(0, DummyModel.DRAWER_ITEM_TAG_Offline);
                }
                else if(App.getInstance().getCurrentACId() > 0){
                    selectItem(CurrentPageTag, mDrawerItems.get(CurrentPageTag - 1).getTag());
                }else {
                    selectItem(CurrentPageTag, mDrawerItems.get(CurrentPageTag - 1).getTag());
                }
            }*/
        } else {
            /*Log.w(TAG, "onCreate: SignOut9" );
            mAuth.signOut();
            updateUI(null);*/
        }

        updateManager  = AppUpdateManagerFactory.create(this);
         appupdatelistner = new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {

                        updateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                _this,
                                APP_UPDATE_REQUEST);
                    }
                    catch (IntentSender.SendIntentException ex)
                    {
                        Crashlytics.logException(ex);
                    }
                }
            }
        };
        updateManager.getAppUpdateInfo().addOnSuccessListener(appupdatelistner);
    }
    private  AppUpdateManager updateManager;
    private   OnSuccessListener appupdatelistner;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_UPDATE_REQUEST) {
            if (requestCode != RESULT_OK) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        if(GetCurrentUserRequest != null) {
            GetCurrentUserRequest.destroy();
        }
        GetCurrentUserRequest = null;
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (updateobj != null) {
            if(updateobjListner != null)
                updateobj.removeEventListener(updateobjListner);
            updateobjListner = null;
            if(updateobjNewUpdateListner != null)
                updateobjNewUpdate.removeEventListener(updateobjNewUpdateListner);
            updateobjNewUpdateListner = null;
            if(updateobjIsOfflineListner != null)
                updateobjIsOffline.removeEventListener(updateobjIsOfflineListner);
            updateobjIsOfflineListner = null;
        }
        super.onPause();
        IsPaused = true;
    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }
    protected void showpDialog() {
        showpDialog(getString(R.string.msg_loading));
    }
    protected void showpDialog(String Message) {
        if (Message.length() == 0)
            pDialog.setMessage(getString(R.string.msg_loading));
        else
            pDialog.setMessage(Message);
        if (!pDialog.isShowing()) {

            try {

                pDialog.show();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) {

            try {

                pDialog.dismiss();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private CustomAuthRequest GetCurrentUserRequest ;
    private boolean IsFirstTimeDone = false;
    private DatabaseReference updateobjNewUpdate;
    private ValueEventListener updateobjNewUpdateListner;
    private DatabaseReference updateobj;
    private ValueEventListener updateobjListner;
    private DatabaseReference updateobjIsOffline;
    private ValueEventListener updateobjIsOfflineListner;
    private FirebaseDatabase database;
    private void AddUpdateRef() {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (updateobj == null)
            updateobj = database.getReference("u/" + App.getInstance().getUserId() + "/c/s/v");
        if (updateobjListner == null)
            updateobjListner = updateobj.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.w(TAG, "u/" + App.getInstance().getUserId() + "/c/s/v/"+dataSnapshot.toString());
                    if (IsFirstTimeDone) {
                        Intent registrationComplete = new Intent(Config.UPDATE_UI);
                        registrationComplete.putExtra("expiry", dataSnapshot.toString());
                        LocalBroadcastManager.getInstance(_this).sendBroadcast(registrationComplete);
                    }
                    IsFirstTimeDone = true;

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        if (updateobjIsOffline == null)
            updateobjIsOffline = database.getReference("GlobalData/IsOffline");
        if (updateobjIsOfflineListner == null)
            updateobjIsOfflineListner = updateobjIsOffline.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString().equals("true")) {
                        Config.IsOffline = true;
                        Intent intent = new Intent(_this, OfflineActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        _this.startActivity(intent);
                    } else {
                        Config.IsOffline = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        if (updateobjNewUpdate == null)
            updateobjNewUpdate = database.getReference("GlobalData/AppVersionId");
        if (updateobjNewUpdateListner == null)
            updateobjNewUpdateListner = updateobjNewUpdate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long AppVersionId = Long.parseLong(dataSnapshot.getValue().toString());
                    if (AppVersionId > BuildConfig.VERSION_CODE) {
                        Config.IsOldVersion = true;
                        Intent intent = new Intent(_this, UpdateActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        _this.startActivity(intent);
                    } else {
                        Config.IsOldVersion = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
    }
}
