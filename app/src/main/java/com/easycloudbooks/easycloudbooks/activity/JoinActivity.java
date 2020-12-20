package com.easycloudbooks.easycloudbooks.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.FragmentDrawer;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.DataText;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.ComingSoonFragment;
import com.easycloudbooks.easycloudbooks.fragment.HomeFragment;
import com.easycloudbooks.easycloudbooks.fragment.NotificationFragment;
import com.easycloudbooks.easycloudbooks.fragment.ProjectFragment;
import com.easycloudbooks.easycloudbooks.model.DummyModel;
import com.easycloudbooks.easycloudbooks.model.ProjectJ;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.model.UserSmallJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.view.kbv.KenBurnsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class JoinActivity extends ActivityBase         {
    private static String TAG = JoinActivity.class.getSimpleName();


    // used to store app title
    private CharSequence mTitle;



    public View mView;
    Context mContext;
    Context _this;
    private FirebaseDatabase database;
    private KenBurnsView mKenBurns;
    private ImageView mLogo;
    private TextView welcomeText,detail_text;
    private FButton ok_button,cancel_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_join);
        mContext = getApplicationContext();
        _this= this;
        database = FirebaseDatabase.getInstance();

        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mLogo = (ImageView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        detail_text = (TextView) findViewById(R.id.detail_text);
        ok_button = (FButton) findViewById(R.id.ok_button);
        cancel_button = (FButton) findViewById(R.id.cancel_button);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mView = findViewById(R.id.parentview);
        initpDialog();



        mTitle = "Join as Staff";

        UserSmallJ userj = App.getInstance().getUserJ();
        if (user != null && userj != null) {
            setAnimation();
            Log.w(TAG, "onCreate: "+userj.FN );
            welcomeText.setText("Welcome " + userj.FN + "!");
            detail_text.setText("Checking Invitation Link.. Please Wait");
            ok_button.setVisibility(View.GONE);
            cancel_button.setVisibility(View.GONE);
            CheckInviteLink();
        }
        else
        {
            showContentScreen();
            finish();
        }
    }

    public void showContentScreen() {
        Intent intent = new Intent(JoinActivity.this,
                LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
    }

    private void CheckInviteLink()
    {
        String PendingInvite = App.getInstance().GetInvite();
        Log.w(TAG, "CheckInviteLink: "+PendingInvite );
        if(PendingInvite != null && PendingInvite != "")
        {
             PendingInvite = PendingInvite.replace("https://app.easycloudbooks.com/staff/join/","");
            VerifyInvite(PendingInvite);
        }
        else {
            Intent intent = new Intent(JoinActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
            finish();
        }
    }

    private  void CancelRequest (String Message)
    {
        Log.w(TAG, "CancelRequest: " );
        //swc.ErrorMessage( Message);
        //swc.ShowMessage(Context context, DialogInterface.OnDismissListener dismissListener, DialogInterface.OnCancelListener cancelListener)
        if(Message.equals("Rejected"))
        {
            SweetAlertCustom.ShowSuccessMessage(this, "Request Rejected", new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    App.getInstance().SaveInvite("");
                    Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                    finish();
                }
            }, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    App.getInstance().SaveInvite("");
                    Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                    finish();
                }
            });
        }
        else {
            SweetAlertCustom.ShowErrorMessage(this, Message, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    App.getInstance().SaveInvite("");
                    Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                    finish();
                }
            }, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    App.getInstance().SaveInvite("");
                    Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                    finish();
                }
            });
        }
    }

    boolean loading = false;
    private SweetAlertCustom swc;

    protected void initpDialog() {
        if (swc == null)
            swc = new SweetAlertCustom(this);
        swc.CreatingLoadingDialog("Loading");
    }
    protected void showpDialog() {
        loading = true;
        swc.ShowLoading();
    }

    protected void hidepDialog() {
        loading = false;
        swc.HideLoading();
    }

    private void VerifyInvite(final String PendingInvite) {
        showpDialog();
        new CustomAuthRequest(Request.Method.POST, METHOD_APP_INVITE_GET, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString() );
                        hidepDialog();
                            try {
                                Log.w(TAG, response.getString("MessageType") );
                                if (response.getString("MessageType").equals("success")) {
                                JSONObject jsonData = response.getJSONObject("obj");
                                /*I = AC.CompanyRelation.Image,
                                        ACId = AC.AccountantCompanyId,
                                        CN = AC.CompanyRelation.Name,
                                        CUF = CUother.FirstName,
                                        CUL = CUother.LastName,
                                        CUI = CUother.Image,;*/

                                    String image = jsonData.getString("I");
                                    if (image!= null && !TextUtils.isEmpty(image)) {
                                        Glide.with(mContext).load(DataText.GetImagePath(image))
                                                .thumbnail(0.5f)
                                                .transition(withCrossFade())
                                                .apply( new RequestOptions().circleCrop()
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                                .into(mLogo);
                                        mLogo.setColorFilter(null);
                                    } else {
                                        mLogo.setImageResource(R.drawable.bg_circle);
                                        mLogo.setColorFilter(null);
                                    }
                                    ok_button.setVisibility(View.VISIBLE);
                                    cancel_button.setVisibility(View.VISIBLE);
                                    detail_text.setText("You are invited by "+jsonData.getString("CN") +" as a "+jsonData.getString("CUF")+ " " + jsonData.getString("CUL")+"(Client)! Do you want to join ?");

                                    ok_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            JoinClient(PendingInvite);
                                        }
                                    });
                                    cancel_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CancelRequest("Rejected");
                                        }
                                    });
                                } else {
                                    try {
                                        if (response.getString("Message") != null)
                                            CancelRequest(response.getString("Message"));
                                    } catch (JSONException ex) {
                                        CancelRequest("Unexpected Error");
                                    }
                                }
                            } catch (JSONException ex) {
                                Log.w(TAG, "onResponse: "+ex.getMessage() );
                                DataGlobal.SaveLog(TAG, ex);
                                CancelRequest("Unable to fetch Details");
                            }

                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Log.w(TAG, "onResponse: "+error.getMessage() );
                DataGlobal.SaveLog(TAG, error);
                CancelRequest("Unable to fetch Details");
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("InviteId", PendingInvite);
                    return params;
                } catch (JSONException ex) {
                    DataGlobal.SaveLog(TAG, ex);
                    return null;
                }
            }

            @Override
            protected void onCreateFinished(CustomAuthRequest request) {
                int socketTimeout = 300000;//0 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.customRequest.setRetryPolicy(policy);
                App.getInstance().addToRequestQueue(request);
            }
        };
    }

    private void JoinClient(final String PendingInvite) {
        showpDialog();
        new CustomAuthRequest(Request.Method.POST, METHOD_APP_INVITE_JOIN, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString() );
                        hidepDialog();
                        try {
                            Log.w(TAG, response.getString("MessageType") );
                            if (response.getString("MessageType").equals("success")) {



                                ok_button.setVisibility(View.GONE);
                                cancel_button.setVisibility(View.GONE);
                                detail_text.setText("");
                                SweetAlertCustom.ShowSuccessMessage(_this, response.getString("Message"), new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        App.getInstance().SaveInvite("");
                                        Intent intent = new Intent(JoinActivity.this, SplashActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                                        finish();
                                    }
                                }, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        App.getInstance().SaveInvite("");
                                        Intent intent = new Intent(JoinActivity.this, SplashActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent, DataGlobal.getFadeInActivityAnimation(getApplicationContext()));
                                        finish();
                                    }
                                });

                            } else {
                                try {
                                    if (response.getString("Message") != null)
                                        CancelRequest(response.getString("Message"));
                                } catch (JSONException ex) {
                                    CancelRequest("Unexpected Error");
                                }
                            }
                        } catch (JSONException ex) {
                            DataGlobal.SaveLog(TAG, ex);
                            CancelRequest("Unable to fetch Details");
                        }

                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                CancelRequest("Unable to fetch Details");
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("InviteId", PendingInvite);
                    return params;
                } catch (JSONException ex) {
                    DataGlobal.SaveLog(TAG, ex);
                    return null;
                }
            }

            @Override
            protected void onCreateFinished(CustomAuthRequest request) {
                int socketTimeout = 300000;//0 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.customRequest.setRetryPolicy(policy);
                App.getInstance().addToRequestQueue(request);
            }
        };
    }

    private void setAnimation() {
        mKenBurns.setImageResource(R.drawable.splash_screen_option_three);
        //animation1();
        animation3();
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

/*
    @Override
    public void onCloseSettingsDialog(int searchGender, int searchOnline) {

        SearchFragment p = (SearchFragment) fragment;
        p.onCloseSettingsDialog(searchGender, searchOnline);
    }
*/









    @Override
    public void onBackPressed() {

            super.onBackPressed();
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
