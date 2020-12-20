package com.easycloudbooks.easycloudbooks.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import io.fabric.sdk.android.Fabric;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.crashlytics.android.Crashlytics;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.*;
import com.easycloudbooks.easycloudbooks.constants.Constants;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.easycloudbooks.easycloudbooks.util.LruBitmapCache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.okhttp.MultipartBuilder;

public class App extends Application implements Constants{

    private FirebaseAnalytics mFirebaseAnalytics;
    public static final String TAG = App.class.getSimpleName();
    public static final String AppUserPath = "https://www.easycloudbooks.com/user/";

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static App mInstance;
    private static FirebaseUser mUser;
    private static UserSmallJ mUserJ;
    private  static long TimeZoneOffset = 0;
    private FirebaseAuth mAuth;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
                public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    //return new DateTime(json.getAsJsonPrimitive().getAsLong());
                    //Log.w(TAG, "deserialize: "+json.getAsJsonPrimitive().getAsString() );
                    DateTime dt = DataDateTime.getJodaTime(json.getAsJsonPrimitive().getAsString());
                    //Log.w(TAG, "deserialize: "+dt.toString() );
                    return  DataDateTime.getJodaTime(json.getAsJsonPrimitive().getAsString());
                }
            })
                    .registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {

                        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
                            return new JsonPrimitive(src.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
                        }
                    }).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    private SharedPreferences sharedPref;

    //private GPSTracker gps;
    private String firstname, middlename, lastname, username, fullname, accessToken, AndroidId, gcmToken = "", FCMToken = "", fb_id = "", photoUrl, coverUrl, area = "", country = "", city = "";
    private Double lat = 0.000000, lng = 0.000000;
    private long id, UserId, DeviceId;
    private DateTime token_expiry;
    private long CompanyCount, ContactCount, ProjectCount, CompanyNewCount, ContactNewCount, ProjectNewCount, DefaultACId, CurrentACId = 0L;
    private int sex,state, admob, ghost, verify, balance, allowShowMyInfo, allowShowMyVideos, allowShowMyFriends, allowShowMyPhotos, allowShowMyGifts, allowPhotosComments, allowVideoComments, allowComments, allowMessages, allowLikesGCM, allowCommentsGCM, allowFollowersGCM, allowGiftsGCM, allowMessagesGCM, allowCommentReplyGCM, errorCode, currentChatId = 0, notificationsCount = 0, messagesCount = 0, guestsCount = 0, newFriendsCount = 0;
    public String SharedPrefName = "account";

    public void logUser() {

         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            long userid = getUserId();
            if(userid > 0)
                Crashlytics.setUserIdentifier(String.valueOf(userid));
            else
                Crashlytics.setUserIdentifier("FB-"+user.getUid());
            Crashlytics.setUserEmail(user.getEmail());
            Crashlytics.setUserName(user.getDisplayName());
        }
        else{

        }
    }


    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "App OnCreate 1- " + (mUser != null ? mUser.getDisplayName() : "None"));
        logUser();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FirebaseAnalytics");
        if(mUser  != null) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mUser.getEmail());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        sharedPref = this.getSharedPreferences(SharedPrefName, Context.MODE_PRIVATE);

        this.readData();
        TimeZone timezone = TimeZone.getDefault();
        TimeZoneOffset = timezone.getRawOffset();
        DataDateTime.Offset = TimeZoneOffset;
        //getLocation();
        Log.i(TAG, "App OnCreate - " + (mUser != null ? mUser.getDisplayName() : "None"));
    }

    public  void FirebaseAnalyticsLog(String Event) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FirebaseAnalytics");
        if(mUser  != null) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,Event);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        }
        mFirebaseAnalytics.logEvent(Event, bundle);
    }

 /*   public  void FirebaseCrashLog(String Message) {
        Crashlytics.log(Message);
        if (Message == null || Message.isEmpty())
            Message = "Custom Notification";
        Exception ex = new Exception(Message);
        Crashlytics.logException(ex);
        Log.i(TAG, Message);
    }

    public  void FirebaseCrashLog(Exception ex) {
        Crashlytics.log(ex.getMessage());
        Exception ex1 = new Exception("Custom Notification");
        Crashlytics.logException(ex1);
    }*/

    public void getLocation() {

        if (App.getInstance().isConnected()) {

            /*gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();
                if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {
                    new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_SET_GEO_LOCATION, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (!response.getBoolean("error")) {

//                                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        Log.d("Set GEO Success", response.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("Set GEO Error", error.toString());
                        }
                    }) {
                        protected JSONObject getParams() {
                            try {
                                JSONObject params = new JSONObject();
                                params.put("lat", Double.toString(latitude));
                                params.put("lng", Double.toString(longitude));
                                return params;
                            } catch (JSONException ex) {
                                return null;
                            }
                        }

                        @Override
                        protected void onCreateFinished(CustomAuthRequest request) {
                            App.getInstance().addToRequestQueue(request);
                        }
                    };
                }

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                try {

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if (addresses.size() > 0) {

                        App.getInstance().setLat(latitude);
                        App.getInstance().setLng(longitude);

                        sharedPref.edit().putString(getString(R.string.settings_account_lat), Double.toString(this.getLat())).apply();
                        sharedPref.edit().putString(getString(R.string.settings_account_lng), Double.toString(this.getLng())).apply();

                        App.getInstance().setArea(addresses.get(0).getAdminArea());
                        App.getInstance().setCity(addresses.get(0).getLocality());
                        App.getInstance().setCountry(addresses.get(0).getCountryName());

                        Toast.makeText(this, addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }*/
        }
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        }

        return false;
    }

    public void logout() {

        /*if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {


                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FirebaseAuth.getInstance().signOut();
                    App.getInstance().removeData();
                    App.getInstance().readData();
                }
            }) {

            };
        }*/
        Log.w(TAG, "logout: called" );
        Log.w(TAG, "onCreate: SignOut1" );
        FirebaseAuth.getInstance().signOut();
        App.getInstance().removeData();
        App.getInstance().readData();
    }

    public JSONObject GetCommonGSON() {
        return GetCommonGSON(null);
    }

    public JSONObject GetCommonGSON(String Authtoken) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("clientId", 1);
            jsonParam.put("DId", App.getInstance().getDeviceId());
            jsonParam.put("DeviceId", App.getInstance().getAndroidId());

            return jsonParam;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
        return null;
    }

    public String getFirebaseAuthToken() {
        final StringBuilder token = new StringBuilder();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    token.append(task.getResult().getToken());
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(30L, TimeUnit.SECONDS);
            return token.toString();
        } catch (InterruptedException ie) {
            return null;
        }
    }

    public void reload() {

        if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

            /*new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_AUTHORIZE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!App.getInstance().authorize(response)) {

                                Log.i("mitul", "reload");
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("mitul", "reload");
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("gcm_regId", App.getInstance().getGcmToken());
                        return params;
                    } catch (JSONException ex) {
                        DataGlobal.SaveLog(TAG, ex);
                        return null;
                    }
                }
            };*/
        }
    }

    public void getSettings() {
       /* try {
            if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {
                JSONObject params = new JSONObject();
                params.put("lat", Double.toString(App.getInstance().getLat()));
                params.put("lng", Double.toString(App.getInstance().getLng()));
                new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_GET_SETTINGS, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        if (response.has("messagesCount")) {

                                            App.getInstance().setMessagesCount(response.getInt("messagesCount"));
                                        }
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {

                                    Log.d("Settings", response.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("mitul", "getSettings");
                        Toast.makeText(getApplicationContext(), getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();

                        Log.e("Settings", error.toString());
                    }
                }) {
                    @Override
                    protected void onCreateFinished(CustomAuthRequest request) {
                        App.getInstance().addToRequestQueue(request);
                    }
                };
            }
        } catch (JSONException ex) {
            DataGlobal.SaveLog(TAG, ex);
        }*/
    }

    public void updateGeoLocation() {

       /* if (App.getInstance().isConnected() && App.getInstance().getId() != 0 && App.getInstance().getLat() == 0.000000 && App.getInstance().getLat() == 0.000000) {
            new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_SET_GEO_LOCATION, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

//                    Toast.makeText(getApplicationContext(), getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected void onCreateFinished(CustomAuthRequest request) {
                    App.getInstance().addToRequestQueue(request);
                }
            };
        }*/
    }

    public Boolean authorizeSimple(JSONObject authObj) {

        try {

            if (authObj.has("error_code")) {

                this.setErrorCode(authObj.getInt("error_code"));
            }

            if (!authObj.has("MessageType")) {

                return false;
            }


            return true;
        } catch (JSONException e) {

            DataGlobal.SaveLog(TAG, e);
            return false;
        }
    }

    public Boolean authorize(JSONObject authObj) {

        try {

            if (authObj.has("error_code")) {

                this.setErrorCode(authObj.getInt("error_code"));
            }

            if (!authObj.has("error")) {

                return false;
            }

            if (authObj.getBoolean("error")) {

                return false;
            }

            if (!authObj.has("account")) {

                return false;
            }

            JSONArray accountArray = authObj.getJSONArray("account");

            if (accountArray.length() > 0) {

                JSONObject accountObj = (JSONObject) accountArray.get(0);

                this.setUsername(accountObj.getString("username"));
                this.setFullname(accountObj.getString("fullname"));
                this.setState(accountObj.getInt("state"));
                this.setAdmob(accountObj.getInt("admob"));
                this.setGhost(accountObj.getInt("ghost"));
                this.setVerify(accountObj.getInt("verify"));
                this.setGender(accountObj.getInt("sex"));

                this.setPhotoUrl(accountObj.getString("lowPhotoUrl"));
                this.setCoverUrl(accountObj.getString("coverUrl"));

                this.setAllowShowMyInfo(accountObj.getInt("allowShowMyInfo"));
                this.setAllowShowMyVideos(accountObj.getInt("allowShowMyVideos"));
                this.setAllowShowMyFriends(accountObj.getInt("allowShowMyFriends"));
                this.setAllowShowMyPhotos(accountObj.getInt("allowShowMyPhotos"));
                this.setAllowShowMyGifts(accountObj.getInt("allowShowMyGifts"));

                this.setNotificationsCount(accountObj.getInt("notificationsCount"));
                this.setMessagesCount(accountObj.getInt("messagesCount"));

                //this.setGuestsCount(accountObj.getInt("guestsCount"));

                /*if (App.getInstance().getLat() == 0.000000 && App.getInstance().getLng() == 0.000000) {

                    this.setLat(accountObj.getDouble("lat"));
                    this.setLng(accountObj.getDouble("lng"));
                }*/
            }

            this.setUserId(authObj.getLong("accountId"));
            this.setAccessToken(authObj.getString("accessToken"));

            this.saveData();

            this.getSettings();

            if (getGcmToken().length() != 0) {

                setGcmToken(getGcmToken());
            }

            return true;

        } catch (JSONException e) {
            DataGlobal.SaveLog(TAG, e);
            return false;
        }
    }

    public long getId() {

        return this.id;
    }


    public boolean IsLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    public long getUserId() {
        return this.UserId;
    }
    public Gson getGSON() {
        return this.gson;
    }

    public void setUserId(long id) {
        this.UserId = id;
        this.id = id;

        Crashlytics.setUserIdentifier(String.valueOf(id));
    }

    public void setFCMToken(String token) {
        this.FCMToken = token;
    }

    public FirebaseUser getUser() {
        return this.mUser;
    }
    public UserSmallJ getUserJ() {
        return this.mUserJ;
    }

    public void setUserJ(UserSmallJ user) {
         this.mUserJ = user;
    }

    public void setDeviceId(long id) {
        this.DeviceId = id;
    }

    public long getDeviceId() {

        return this.DeviceId;
    }

    public void setAndroidId(String id) {
        this.AndroidId = id;
    }

    public String getAndroidId() {
        return this.AndroidId;
    }

    public void setGcmToken(final String gcmToken) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                user.getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                String  android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                String IDToken = task.getResult().getToken();
                                JSONObject jsonParam = new JSONObject();
                                jsonParam.put("idToken", IDToken);
                                jsonParam.put("app", AppType);
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
                                                Log.w(TAG, "onResponse: FCM Saved" );
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.w(TAG, "onResponse: FCM Save Error" );
                                    }
                                });
                                App.getInstance().addToRequestQueue(jsonReq);

                        } catch (JSONException ex) {
                                Log.w(TAG, "onResponse: FCM Save Error 1" );
                        }
                        }
                    }
                });

                /*JSONObject params = new JSONObject();
                params.put("gcm_regId", (gcmToken));
                new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_SET_FCM_TOKEN, params,0,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                    hidepDialog();
                    }
                }) {
                    @Override
                    protected void onCreateFinished(CustomAuthRequest request) {
                        int socketTimeout = 0;//0 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        request.customRequest.setRetryPolicy(policy);
                        App.getInstance().addToRequestQueue(request);
                    }
                };*/

            } catch (Exception ex) {
                DataGlobal.SaveLog(TAG, ex);
            }
        }

        this.gcmToken = gcmToken;
    }



    public String getGcmToken() {

        if (this.gcmToken == null) {

            this.gcmToken = "";
        }

        return this.gcmToken;
    }

    public void setFacebookId(String fb_id) {

        this.fb_id = fb_id;
    }

    public String getFacebookId() {

        return this.fb_id;
    }

    public void setState(int state) {

        this.state = state;
    }

    public int getState() {

        return this.state;
    }

    public void setNotificationsCount(int notificationsCount) {

        this.notificationsCount = notificationsCount;
    }

    public int getNotificationsCount() {

        return this.notificationsCount;
    }

    public void setMessagesCount(int messagesCount) {

        this.messagesCount = messagesCount;
    }

    public int getMessagesCount() {

        return this.messagesCount;
    }

    public void setDefaultACId(long ACId) {
        this.DefaultACId = ACId;
    }

    public long getDefaultACId() {
        return this.DefaultACId;
    }

    public  String GetCustonDomain (long ACId, String URL)
    {
        return  URL.replace("###","ac-"+Long.toString(ACId));

    }

    public void setCurrentACId(long ACId) {

        this.CurrentACId = ACId;
    }

    public  void BroadcastACIdChanged(Context context )
    {
        Intent registrationComplete = new Intent(Config.ACID_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(registrationComplete);
    }
    public long getCurrentACId() {
        //Log.w("Mitul", "getCurrentACId: "+this.CurrentACId );
        return this.CurrentACId;
    }

    public void setNewFriendsCount(int newFriendsCount) {

        this.newFriendsCount = newFriendsCount;
    }

    public int getNewFriendsCount() {

        return this.newFriendsCount;
    }

    public void setAllowMessagesGCM(int allowMessagesGCM) {

        this.allowMessagesGCM = allowMessagesGCM;
    }

    public int getAllowMessagesGCM() {

        return this.allowMessagesGCM;
    }

    public void setAllowCommentReplyGCM(int allowCommentReplyGCM) {

        this.allowCommentReplyGCM = allowCommentReplyGCM;
    }

    public int getAllowCommentReplyGCM() {

        return this.allowCommentReplyGCM;
    }

    public void setAllowFollowersGCM(int allowFollowersGCM) {

        this.allowFollowersGCM = allowFollowersGCM;
    }

    public int getAllowFollowersGCM() {

        return this.allowFollowersGCM;
    }

    public void setAllowGiftsGCM(int allowGiftsGCM) {

        this.allowGiftsGCM = allowGiftsGCM;
    }

    public int getAllowGiftsGCM() {

        return this.allowGiftsGCM;
    }

    public void setAllowCommentsGCM(int allowCommentsGCM) {

        this.allowCommentsGCM = allowCommentsGCM;
    }

    public int getAllowCommentsGCM() {

        return this.allowCommentsGCM;
    }

    public void setAllowLikesGCM(int allowLikesGCM) {

        this.allowLikesGCM = allowLikesGCM;
    }

    public int getAllowLikesGCM() {

        return this.allowLikesGCM;
    }

    public void setAllowMessages(int allowMessages) {

        this.allowMessages = allowMessages;
    }

    public int getAllowMessages() {

        return this.allowMessages;
    }

    public void setAllowComments(int allowComments) {

        this.allowComments = allowComments;
    }

    public int getAllowComments() {

        return this.allowComments;
    }

    public void setAllowPhotosComments(int allowPhotosComments) {

        this.allowPhotosComments = allowPhotosComments;
    }

    public int getAllowPhotosComments() {

        return this.allowPhotosComments;
    }

    public void setAllowVideoComments(int allowVideoComments) {

        this.allowVideoComments = allowVideoComments;
    }

    public int getAllowVideoComments() {

        return this.allowVideoComments;
    }

    public void setAdmob(int admob) {

        this.admob = admob;
    }

    public int getAdmob() {

        return this.admob;
    }

    public void setGhost(int ghost) {

        this.ghost = ghost;
    }

    public int getGhost() {

        return this.ghost;
    }

    public void setCurrentChatId(int currentChatId) {

        this.currentChatId = currentChatId;
    }

    public int getCurrentChatId() {

        return this.currentChatId;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }

    public int getErrorCode() {

        return this.errorCode;
    }

    public String getUsername() {

        if (this.username == null) {

            this.username = "";
        }

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public boolean isTokenExpired() {
        if(this.token_expiry == null || this.token_expiry.isBefore(DataDateTime.Now()))
                return  true;
        else return  false;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }


    public void setTokenExpiry(String tokenExpiry) {

        Log.w(TAG, "setTokenExpiry: "+token_expiry );
        this.token_expiry = DataDateTime.getJodaTime(tokenExpiry);
        Log.w(TAG, "setTokenExpiry1: "+this.token_expiry );
        if(this.token_expiry == null)
            this.token_expiry = DataDateTime.Now().plusMinutes(5);
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }

    public void setFirstName(String firstname) {

        this.firstname = firstname;
    }

    public void setMiddleName(String middlename) {

        this.middlename = middlename;
    }

    public void setLastName(String lastname) {

        this.lastname = lastname;
    }

    public String getFirstName() {

        return this.firstname;
    }


    public String getName() {

        return mUserJ.FN + " " + mUserJ.LN;
    }

    public String getMiddleName() {

        return  mUserJ.MN;
    }

    public String getLastName() {

        return mUserJ.LN;
    }

    public String getFullname() {

        return mUserJ.FN + " " + mUserJ.LN;

    }

    public void setVerify(int verify) {

        this.verify = verify;
    }

    public int getVerify() {

        return this.verify;
    }

    public void setBalance(int balance) {

        this.balance = balance;
    }

    public int getBalance() {

        return this.balance;
    }

    public void setPhotoUrl(String photoUrl) {

        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {

        if (this.photoUrl == null) {

            this.photoUrl = "";
        }

        return this.photoUrl;
    }

    public void setCoverUrl(String coverUrl) {

        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {

        if (coverUrl == null) {

            this.coverUrl = "";
        }

        return this.coverUrl;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCountry() {

        if (this.country == null) {

            this.setCountry("");
        }

        return this.country;
    }
    public void setGender(int sex) {

        this.sex = sex;
    }

    public int getGender() {
        return this.sex;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCity() {

        if (this.city == null) {

            this.setCity("");
        }

        return this.city;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getArea() {

        if (this.area == null) {

            this.setArea("");
        }

        return this.area;
    }

    public void setLat(Double lat) {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        this.lat = lat;
    }

    public Double getLat() {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        return this.lat;
    }

    public void setLng(Double lng) {

        if (this.lng == null) {

            this.lng = 0.000000;
        }

        this.lng = lng;
    }

    public Double getLng() {

        return this.lng;
    }

    // Privacy

    public void setAllowShowMyInfo(int allowShowMyInfo) {

        this.allowShowMyInfo = allowShowMyInfo;
    }

    public int getAllowShowMyInfo() {

        return this.allowShowMyInfo;
    }

    public void setAllowShowMyVideos(int allowShowMyVideos) {

        this.allowShowMyVideos = allowShowMyVideos;
    }

    public int getAllowShowMyVideos() {

        return this.allowShowMyVideos;
    }

    public void setAllowShowMyFriends(int allowShowMyFriends) {

        this.allowShowMyFriends = allowShowMyFriends;
    }

    public int getAllowShowMyFriends() {

        return this.allowShowMyFriends;
    }

    public void setAllowShowMyPhotos(int allowShowMyPhotos) {

        this.allowShowMyPhotos = allowShowMyPhotos;
    }

    public int getAllowShowMyPhotos() {

        return this.allowShowMyPhotos;
    }

    public void setAllowShowMyGifts(int allowShowMyGifts) {

        this.allowShowMyGifts = allowShowMyGifts;
    }

    public int getAllowShowMyGifts() {

        return this.allowShowMyGifts;
    }

private List<ACJM> ACs;

    public void setACs(List<ACJM> ACs) {
        this.ACs = ACs;
    }

    public List<ACJM> getACs() {
        return this.ACs;
    }


    public boolean haveACId(long ACId) {
        boolean haveacid = false;
        if(this.ACs != null && this.ACs.size() > 0)
        {
            for ( ACJM AC: this.ACs  ) {
                if(AC.ACId == ACId)
                {haveacid = true;
                    break;
                }
            }
        }

        return haveacid;
    }

    public ACJM getAC(long ACId) {
        ACJM ACG = null;
        if(this.ACs != null && this.ACs.size() > 0)
        {
            for ( ACJM AC: this.ACs  ) {
                if(AC.ACId == ACId)
                {ACG = AC;
                    break;
                }
            }
        }

        return ACG;
    }

    public String ValidateACImage(String image) {
        if (image == null || image.equals("") || image.equals("null"))
            image = Config.CompanyImage;


        return image;
    }

    public String getACName(long ACId) {
        String name = "";
        if(this.ACs != null && this.ACs.size() > 0)
        {
            for ( ACJM AC: this.ACs  ) {
                if(AC.ACId == ACId)
                {
                    name = AC.N;
                    break;
                }
            }
        }
        if (name == null || name.equals("") || name.equals("null"))
            name = "";


        return name;
    }

    public String getACImage(long ACId) {
        String image = "";
        if(this.ACs != null && this.ACs.size() > 0)
        {
            for ( ACJM AC: this.ACs  ) {
                if(AC.ACId == ACId)
                {
                    image = AC.I;
                    break;
                }
            }
        }
        if (image == null || image.equals("") || image.equals("null"))
            image = Config.CompanyImage;


        return image;
    }

    public void readData() {
       // this.setFCMToken(FirebaseInstanceId.getInstance().getToken());
        this.setUserId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.setDeviceId(sharedPref.getLong(getString(R.string.settings_user_device_id), 0));
        this.setAndroidId(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        this.setAccessToken(sharedPref.getString(getString(R.string.settings_account_access_token), ""));

        this.setAllowMessagesGCM(sharedPref.getInt(getString(R.string.settings_account_allow_messages_gcm), 1));
        this.setAllowCommentsGCM(sharedPref.getInt(getString(R.string.settings_account_allow_comments_gcm), 1));
        this.setAllowCommentReplyGCM(sharedPref.getInt(getString(R.string.settings_account_allow_comments_reply_gcm), 1));

        this.setLat(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lat), "0.000000")));
        this.setLng(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lng), "0.000000")));


        this.setUserJ(UserSmallJ.getJSON(sharedPref.getString(getString(R.string.settings_account_user), "")));


        this.setCurrentACId(sharedPref.getLong(getString(R.string.settings_account_current_acid), 0));
        this.setDefaultACId(sharedPref.getLong(getString(R.string.settings_account_default_acid), 0));
        try {
            this.setACs(ACJM.getJSONList (sharedPref.getString(getString(R.string.settings_account_acs), "[]")));
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            //App.getInstance().FirebaseCrashLog(ex);
            this.setACs(new ArrayList<ACJM>());
        }

    }
    public static int CCount;

    public void saveData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), this.getId()).apply();
        sharedPref.edit().putLong(getString(R.string.settings_user_device_id), this.getDeviceId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), this.getAccessToken()).apply();

        sharedPref.edit().putString(getString(R.string.settings_account_user), this.getUserJ().toString()).apply();
        sharedPref.edit().putLong(getString(R.string.settings_account_current_acid), this.getCurrentACId()).apply();
        sharedPref.edit().putLong(getString(R.string.settings_account_default_acid), this.getDefaultACId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_acs), App.getInstance().getGSON().toJson(this.getACs())).apply();
    }


    public void SaveInvite(String invite) {
        sharedPref.edit().putString(getString(R.string.settings_app_invites), invite).apply();
    }


    public String GetInvite() {
        return sharedPref.getString(getString(R.string.settings_app_invites), "");
    }

    public void removeData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), "").apply();

        sharedPref.edit().putString(getString(R.string.settings_account_lat), "0.000000").apply();
        sharedPref.edit().putString(getString(R.string.settings_account_lng), "0.000000").apply();
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(CustomAuthRequest req) {
        req.customRequest.setTag(TAG);
        getRequestQueue().add(req.customRequest);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String SetData(Context context, JSONObject ResultJ) {
        if (ResultJ != null) {
            try {
                Log.w(TAG, "SetData");
                JSONObject obj = ResultJ;
                App.getInstance().setDeviceId(obj.getLong("DId"));
                UserSmallJ userj = UserSmallJ.getJSON(obj.getString("user"));
                App.getInstance().setUserJ(userj);
                List<ACJM> ACs = ACJM.getJSONList(obj.getString("SC"));
                List<ACJM> newACs =new ArrayList<ACJM>();
                App.getInstance().setFirstName(userj.FN);
                App.getInstance().setMiddleName(userj.MN);
                App.getInstance().setLastName(userj.LN);
                App.getInstance().setPhotoUrl(userj.I);
                App.getInstance().setCoverUrl("");

                JSONObject tokenj = obj.getJSONObject("token");
                App.getInstance().setAccessToken(tokenj.getString("token"));
                App.getInstance().setTokenExpiry(tokenj.getString("token_expiry"));

                App.getInstance().setUserId(userj.Id);

                for (int i = 0; i < ACs.size(); i++) {
                    ACJM acobj = ACs.get(i);
                    if (acobj != null) {
                        String Image = acobj.I;
                        if (Image == null || Image.equals("") || Image.equals("null"))
                            Image = "";
                        acobj.I = DataText.GetImagePath(Image);
                        newACs.add(acobj);
                    }
                }
                ACs = newACs;
                App.getInstance().setACs(ACs);
                long CurrentACId = userj.DACId;
                if (CurrentACId > 0) {
                    boolean IsValid = false;
                    for (int i = 0; i < ACs.size(); i++) {
                        ACJM acobj = ACs.get(i);
                        if (acobj != null) {
                            long AccountantCompanyId = acobj.ACId;
                            if (AccountantCompanyId > 0 && CurrentACId == AccountantCompanyId) {
                                IsValid = true;
                                break;
                            }
                        }
                    }
                    if (!IsValid)
                        CurrentACId = 0;
                }
                if (!(CurrentACId > 0)) {
                    for (int i = 0; i < ACs.size(); i++) {
                        ACJM acobj = ACs.get(i);
                        if (acobj != null && !(CurrentACId > 0)) {
                            long AccountantCompanyId = acobj.ACId;
                            if (AccountantCompanyId > 0) {
                                CurrentACId = AccountantCompanyId;
                                break;
                            }
                        }
                    }
                }
                if (CurrentACId > 0) {
                    App.getInstance().setCurrentACId(CurrentACId);
                    App.getInstance().setDefaultACId(CurrentACId);
                    App.getInstance().saveData();
                    return "success";
                } else {
                    App.getInstance().setCurrentACId(0L);
                    App.getInstance().setDefaultACId(0L);
                    App.getInstance().saveData();
                    return "success";
                    //return "No Valid Companies Found";
                }
            } catch (JSONException ex) {
                DataGlobal.SaveLog(TAG, ex, context);
            }
        } else
            ClearData(context);
        return "Invalid Login";
    }

    public static String VerifyUpdateData(Context context, JSONObject ResultJ) {
        if (ResultJ != null) {
            long DId = App.getInstance().getDeviceId();
            long UserId =App.getInstance().getUserId();
            try {
                boolean IsValid = false;
                 // Or use new GsonBuilder().create();


                JSONObject obj = ResultJ.getJSONObject("obj");
                if (obj != null) {
                    Long CurrentVersion = (long)1262;// obj.getLong("AppId");
                    boolean IsOffline = false;//obj.getBoolean("IsOffline");
                    UserSmallJ userj = UserSmallJ.getJSON(obj.getString("user"));
                    List<ACJM> ACs = ACJM.getJSONList(obj.getString("SC"));
                    if (DId > 0 && UserId > 0 && DId == obj.getLong("DId") && userj != null && ACs != null && UserId == userj.Id) {
                        IsValid = true;
                    }
                    if (IsValid) {
                        List<ACJM> newACs =  new ArrayList();

                        App.getInstance().setFirstName(userj.FN);
                        App.getInstance().setMiddleName(userj.MN);
                        App.getInstance().setLastName(userj.LN);
                        for (int i = 0; i < ACs.size(); i++) {
                            ACJM acobj = ACs.get(i);
                            if (acobj != null) {
                                String Image = acobj.I;
                                if (Image == null || Image.equals("") || Image.equals("null"))
                                    Image = "";
                                acobj.I = DataText.GetImagePath(Image);
                                newACs.add(acobj);
                            }
                        }
                        ACs = newACs;
                        App.getInstance().setACs(ACs);
                        long CurrentACId = App.getInstance().getCurrentACId();
                        long OldCurrentACId = App.getInstance().getCurrentACId();
                        if (CurrentACId > 0) {
                            boolean IsValid1 = false;
                            for (int i = 0; i < ACs.size(); i++) {
                                ACJM acobj = ACs.get(i);
                                if (acobj != null) {
                                    long AccountantCompanyId = acobj.ACId;
                                    if (AccountantCompanyId > 0 && CurrentACId == AccountantCompanyId) {
                                        IsValid1 = true;
                                        break;
                                    }
                                }
                            }
                            if (!IsValid1)
                                CurrentACId = 0;
                        }
                        if (!(CurrentACId > 0)) {
                            for (int i = 0; i < ACs.size(); i++) {
                                ACJM acobj = ACs.get(i);
                                if (acobj != null && !(CurrentACId > 0)) {
                                    long AccountantCompanyId = acobj.ACId;
                                    if (AccountantCompanyId > 0) {
                                        CurrentACId = AccountantCompanyId;
                                        break;
                                    }
                                }
                            }
                        }
                        if (CurrentACId > 0) {
                            App.getInstance().setCurrentACId(CurrentACId);
                            App.getInstance().setDefaultACId(CurrentACId);
                            App.getInstance().saveData();
                            if (CurrentVersion > BuildConfig.VERSION_CODE) {
                                return "update";
                            } else if (IsOffline) {
                                return "offline";
                            } else if (CurrentACId != OldCurrentACId) {
                                if(OldCurrentACId == 0)
                                    return "newacid";
                                else
                                    return "changeacid";
                            } else
                                return "success";
                        } else {
                            App.getInstance().setCurrentACId(0L);
                            App.getInstance().setDefaultACId(0L);
                            App.getInstance().saveData();
                            if (CurrentACId != OldCurrentACId) {
                                return "changeacid";
                            }
                            else return "success";
                            //return "No Valid Companies Found";
                        }
                    } else {

                    }
                } else {

                }



            } catch (JSONException ex) {
                DataGlobal.SaveLog(TAG, ex, context);
            }
        } else
            ClearData(context);
        return "Invalid Login";
    }

    public static void ClearData(Context context) {
        String Invite = App.getInstance(). GetInvite();

        SharedPreferences pref = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        if(Invite != null && Invite != "")
        App.getInstance().SaveInvite(Invite);
    }

    public static ACJM GetACJson(Context context, long ACId) {
        try {
            ACJM robj = null;
            if (ACId > 0) {
                List<ACJM> ACs = App.getInstance().getACs();
                for (int i = 0; i < ACs.size(); i++) {
                    ACJM obj = ACs.get(i);
                    if (obj != null) {
                        long AccountantCompanyId = obj.ACId;
                        if (AccountantCompanyId > 0 && AccountantCompanyId == ACId) {
                            robj = obj;
                        }
                    }
                }
            }
            return robj;
        } catch (Exception e) {
            DataGlobal.SaveLog(TAG, e, context);
            return null;
        }
    }

    @Nullable
    public static ACJM GetCurrentACJson(Context context) {
        try {
            ACJM robj = null;
            long ACId = App.getInstance().getCurrentACId();
            if (ACId > 0) {
               List<ACJM> ACs = App.getInstance().getACs();
                for (int i = 0; i < ACs.size(); i++) {
                    ACJM obj = ACs.get(i);
                    if (obj != null) {
                        long AccountantCompanyId = obj.ACId;
                        if (AccountantCompanyId > 0 && AccountantCompanyId == ACId) {
                            robj = obj;
                        }
                    }
                }
            }
            return robj;
        } catch (Exception e) {
            DataGlobal.SaveLog(TAG, e, context);
            return null;
        }
    }

    public static List<ACJM> GetAllACJson(Context context) {
        return App.getInstance().getACs();
    }




}