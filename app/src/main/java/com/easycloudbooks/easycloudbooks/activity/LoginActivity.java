package com.easycloudbooks.easycloudbooks.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.easycloudbooks.easycloudbooks.constants.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.SignInButton;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import androidx.annotation.NonNull;

import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActivityBase {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mCustomToken;
    SharedPreferences pref;
    private ProgressDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    EditText _emailText;
    EditText _passwordText;
    SignInButton _loginButton;
    //TextView _signupLink;
    private View mView;
    private String android_id;
    private JSONObject ResultJ;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startIntent(Context context, int flags) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //_emailText = (EditText) this.findViewById(R.id.input_email);
        //_signupLink = (TextView) this.findViewById(R.id.link_signup);

        _loginButton = (SignInButton)this.findViewById(R.id.btn_login);
        //_passwordText = (EditText)this.findViewById(R.id.input_password);


        mView = this.findViewById(R.id.LoginView);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();
        Log.w(TAG, "onCreate: SignOut2" );
        mAuth.signOut();
        App.getInstance().ClearData(getApplicationContext());
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUI(user);
            }
        };


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

     /*   _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });*/
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String RMessage = App.getInstance().SetData(getApplicationContext(), ResultJ);
            if (RMessage == "success") {
                Log.i(TAG, "success" + App.getInstance().getCurrentACId());
                //App.getInstance().updateGeoLocation();
                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, RMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            _loginButton.setEnabled(true);
        }
    }

    private void setCustomToken(String token) {
        mCustomToken = token;
        String status;
        if (mCustomToken != null) {
            status = "Token:" + mCustomToken;
        } else {
            status = "Token: null";
        }
    }

    public void login() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        showpDialog("Authenticating...");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hidepDialog();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Crashlytics.log("signInWithCredential:success");
                            App.getInstance().FirebaseAnalyticsLog("Login");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getFirebaseAuthToken(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Crashlytics.log("signInWithCredential:failure1");
                            Crashlytics.logException(task.getException());
                            SnakebarCustom.danger(LoginActivity.this, mView, getText(R.string.error_signin).toString(), 1000);
                            updateUI(null);


                        }

                        // [START_EXCLUDE]
                       // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void showProgressDialog(String Message)
    {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(Message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else{
            progressDialog.show();
        }
    }

    public void hideProgressDialog()
    {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
        else  if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public void onDestroy() {
        mGoogleSignInClient.signOut();
        super.onDestroy();
        hidepDialog();
    }

    public void getFirebaseAuthToken(final FirebaseUser user) {
        Log.w(TAG, "getFirebaseAuthToken");
        Log.e("TEST","Login Value");
        showpDialog("Getting Details...");
        Log.w(TAG, "getFirebaseAuthToken 1");
        user.getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    Log.w(TAG, "getFirebaseAuthToken-success");
                    Log.w(TAG, task.getResult().getToken());
                    ECBLogin(user,task.getResult().getToken());

                }
                else{
                    Log.w(TAG, "getFirebaseAuthToken-error");
                }
                hidepDialog();
            }
        });
        /*try {
            countDownLatch.await(30L, TimeUnit.SECONDS);

        } catch (InterruptedException ie) {
            hidepDialog();
        }*/
    }


    public void ECBLogin(final FirebaseUser user, String IDToken) {
        try {
            Log.w(TAG, "ECBLogin");
            Log.e("TEST", "ECBLogin");

            Log.w(TAG, IDToken);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("idToken",IDToken);
            jsonParam.put("app", Constants.AppType);
            jsonParam.put("fcmtoken", "");
            jsonParam.put("version", Build.VERSION.CODENAME);
            jsonParam.put("brand", Build.BRAND);
            jsonParam.put("device", Build.DEVICE);
            jsonParam.put("deviceid", android_id);
            jsonParam.put("model", android.os.Build.MODEL);
            jsonParam.put("manufacturer", Build.MANUFACTURER);
            jsonParam.put("product", Build.PRODUCT);
            jsonParam.put("androidid", android_id);
            jsonParam.put("uniqueid", android_id);
            jsonParam.put("appv", BuildConfig.VERSION_CODE);
            jsonParam.put("os", android.os.Build.VERSION.RELEASE);
            jsonParam.put("platform", "ANDROID");

            Log.w(TAG, "Mitul");
            CustomRequest jsonReq = new CustomRequest(METHOD_API_GetToken, jsonParam, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {

                                Log.w(TAG, response.toString());
                                ResultJ = response.getJSONObject("obj");

                                Log.w(TAG, ResultJ.toString());
                                JSONObject reader = new JSONObject(String.valueOf(ResultJ));
                                Log.e("TEST","Response :"+ResultJ);
                                String acit = reader.getString("DefaultACId");
                                Log.e("TEST","ACID :"+acit);
                                String a = Constants.API_User_DOMAIN.replace("###", acit);
                                Log.e("TEST","Domain :"+a);

                                boolean IsValid = false;
                                String ToastMessage = "";
                                if (ResultJ != null) {
                                    try {
                                        String Message = response.getString("Message");
                                        String MessageType = response.getString("MessageType");
                                        if (MessageType.equals("success")) {
                                            hidepDialog();
                                            updateUI(user);
                                            IsValid = true;
                                        } else {
                                            hidepDialog();
                                            if (Message != null && Message != "") {
                                                ToastMessage = Message;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        DataGlobal.SaveLog(TAG, e);
                                    }
                                }
                                if (!IsValid) {
                                    hideProgressDialog();
                                    if (ToastMessage != "")
                                        Toast.makeText(getBaseContext(), ToastMessage, Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(getBaseContext(), "Not able To Login", Toast.LENGTH_LONG).show();
                                }
                                hidepDialog();
                            } catch (JSONException ex) {
                                _loginButton.setEnabled(true);
                                Crashlytics.logException(ex);
                                SnakebarCustom.danger(LoginActivity.this, mView, getText(R.string.error_signin).toString(), 1000);
                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    _loginButton.setEnabled(true);
                    Log.w(TAG, "onErrorResponse: "+error.getMessage() );
                    if( error.networkResponse != null)
                    Log.w(TAG, "onErrorResponse: "+ error.networkResponse.statusCode );
                    Log.w(TAG, "onErrorResponse: ",error );
                    SnakebarCustom.danger(LoginActivity.this, mView, getText(R.string.error_data_loading).toString(), 1000);
                    hidepDialog();
                }
            });
            App.getInstance().addToRequestQueue(jsonReq);
        } catch (JSONException ex) {
            _loginButton.setEnabled(true);
            SnakebarCustom.danger(LoginActivity.this, mView, "Unable to Login", 1000);
        }
    }


}

