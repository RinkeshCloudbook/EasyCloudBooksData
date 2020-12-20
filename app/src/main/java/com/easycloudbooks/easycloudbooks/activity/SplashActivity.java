package com.easycloudbooks.easycloudbooks.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.app.ECBAuthToken;
import com.easycloudbooks.easycloudbooks.model.UserSmallJ;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.view.kbv.KenBurnsView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends ActivityBase implements GoogleApiClient.ConnectionCallbacks {
    private static String TAG = SplashActivity.class.getSimpleName();
    public static final String GOOGLE_API_VERIFY_URL = "https://www.googleapis.com/androidcheck/v1/attestations/";
    private GoogleApiClient mGoogleApiClient;
    private boolean isConnected = false;
    private KenBurnsView mKenBurns;
    private ImageView mLogo;
    private TextView welcomeText;
    private Handler mHandler;
    private Runnable mRunnable;
    private ProgressBar mProgressBar;
    private Bundle intentBundle ;
    private String Mode ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        initClient();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        ActionBar bar = getSupportActionBar();
        if(bar != null)
            bar.hide();
        setContentView(R.layout.activity_splash);
        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //App.getInstance().setGcmToken(refreshedToken);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mLogo = (ImageView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "onCreate: Debug" );
            Toast.makeText(this,"Debug Mode",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.w(TAG, "onCreate: Live" );
            //Toast.makeText(this,"Live Mode",Toast.LENGTH_LONG);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserSmallJ userj = App.getInstance().getUserJ();
        if (user != null && userj != null) {
            App.getInstance().logUser();
            Log.w(TAG, "onCreate: "+userj.FN );
            welcomeText.setText("Welcome " + userj.FN + "!");
            Crashlytics.log("User: "+userj.FN );


        }
        else
        {
            if (user == null)
                Log.w(TAG, "onCreate: None FB" );
            else
                Log.w(TAG, "onCreate: "+ user.getDisplayName() );

            if (userj == null)
                Log.w(TAG, "onCreate: None" );
            else
                Log.w(TAG, "onCreate: "+ userj.FN );
        }
        Crashlytics.logException(new Exception("Upload Log exception"));
        setAnimation();
        ShowResult();
        intentBundle = getIntent().getExtras();
        Mode = "";
        Long ACId = 0L;
        if(intentBundle != null) {
            Mode = intentBundle.getString("mode");
            ACId = intentBundle.getLong("acid");
            String nfid = intentBundle.getString("nfid");
        }

        if(Mode != null && Mode.equals("notification") && ACId > 0)
        {
            Authorize();
        }
        else{
            Mode = "";
        }
        if(Mode == null ||  Mode.equals(""))
        {
            Uri deepLink = getIntent().getData();
            if (deepLink != null) {
                Log.w(TAG, deepLink.toString());
                //Log.w(TAG, deepLink.getQuery());
            } else
                Log.w(TAG, "null link");
            GetDeepLink();
        }
    }

    public void GetDeepLink()
    {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if(deepLink != null) {
                            Log.w(TAG, deepLink.toString());
                            //Log.w(TAG, deepLink.getQuery());
                        }
                        else
                            Log.w(TAG, "null link" );
                        if(deepLink != null) {
                                App.getInstance().SaveInvite(deepLink.toString());
                            Authorize();
                        }
                        else
                            { Authorize();}


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });

    }

    public void Authorize() {
        if (App.getInstance().isConnected() && App.getInstance().getId() != 0 && App.getInstance().IsLoggedIn()) {
            if(true || App.getInstance().isTokenExpired())
            {
                FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            // Log and toast
                            ECBAuthToken.GetECBToken(token,new ECBAuthToken.OnECBAuthTokenListner() {
                                @Override
                                public void onECBAuthToken(String Token,JSONObject ResultJ) {
                                    Log.w(TAG, Token);
                                    if(Token != null && Token.length() > 0)
                                    {
                                        String PendingInvite = App.getInstance().GetInvite();

                                        if(PendingInvite != null && PendingInvite != "")
                                        {
                                            Intent intent = new Intent(SplashActivity.this, JoinActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            startActivity(intent);
                                        }
                                        else {
                                            if(ResultJ != null)
                                                App.getInstance().SetData(getApplicationContext(), ResultJ);
                                            Log.w(TAG, "onCreate Mode:-"+Mode );
                                            if(Mode.equals("notification")) {
                                                Long ACId = intentBundle.getLong("acid");
                                                if(App.getInstance().haveACId(ACId)) {
                                                    App.getInstance().setCurrentACId(ACId);

                                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("mode", "notification");
                                                    intent.putExtra("bundle",intentBundle);
                                                    finish();
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    finish();
                                                    startActivity(intent);
                                                }
                                            }
                                            else {
                                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                finish();
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                    else{
                                        showContentScreen();
                                    }
                                }
                            });

                        }
                    });



                /*new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_SET_FCM_TOKEN, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));

                        *//*
                        if (App.getInstance().authorize(response)) {
                            if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {
                                App.getInstance().updateGeoLocation();
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                            } else {
                                showContentScreen();
                                App.getInstance().logout();
                            }
                        } else {
                            showContentScreen();
                        }*//*
                        finish();
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showContentScreen();
                        finish();
                    }
                }) {
                };*/
            }
            else
            {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                App.getInstance().setGcmToken(token);
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                                // Log and toast
                                Log.w("Refreshed token 1", token);

                            }
                        });

            }
        } else {
            if (!App.getInstance().isConnected()) {
                Log.i(TAG,"NE");
                SnakebarCustom.info(getApplicationContext(), mKenBurns, "No Internet Connection", 5000);
            }else
            {
                showContentScreen();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public void showContentScreen() {
        Intent intent = new Intent(SplashActivity.this,
                LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
    }

    private boolean CheckCalled = false;

    private void initClient() {

    }

    public void ShowResult() {
        if (isConnected && !CheckCalled) {
            SharedPreferences pref = this.getSharedPreferences(App.getInstance().SharedPrefName, Context.MODE_PRIVATE);
            if (pref.getBoolean("App.integrityCheck", false) != true) {
                CheckCalled = true;
                mProgressBar.setVisibility(View.VISIBLE);
                startVerification();
            } else {
                if (pref.getBoolean("App.integrity", false) && pref.getBoolean("App.cts", false)) {
                } else {
                    Snackbar snackbar = Snackbar.make(mKenBurns, "You are using Custom ROM Or Your configuration Fails with Current Standard", Snackbar.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) snackbar.getView();
                    group.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cpb_red));
                    snackbar.show();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
        ShowResult();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

        isConnected = false;
    }

    private void startVerification() {
        displayResults(true, true);
    }


    private void decodeJWS() {


    }

    private byte[] getRequestNonce() {

        String data = String.valueOf(System.currentTimeMillis());

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        Random random = new Random();
        random.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }

    /**
     * Animation depends on category.
     */
    private void setAnimation() {
        mKenBurns.setImageResource(R.drawable.splash_screen_option_three);
        animation1();
        animation3();
    }

    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    private void displayResults(boolean integrity, boolean cts) {
SharedPreferences pref = this.getSharedPreferences(App.getInstance().SharedPrefName, Context.MODE_PRIVATE);
        mProgressBar.setVisibility(View.GONE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("App.integrityCheck", true);
        editor.putBoolean("App.integrity", integrity);
        editor.putBoolean("App.cts", cts);
        editor.commit();
        if (integrity && cts) {
        } else {
            Snackbar snackbar = Snackbar.make(mKenBurns, "You are using Custom ROM Or You configuration Fails with Current Standard", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
