package com.easycloudbooks.easycloudbooks.app;


import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.constants.Constants;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_API_GetToken;


public class ECBAuthToken {
    public  static  String TAG = "ECBAuthToken";
    // setting the listener

    public interface OnECBAuthTokenListner {
        public void onECBAuthToken(String token,JSONObject ResultJ);
    }
    public static void GetECBToken(final String fcmtoken,final OnECBAuthTokenListner listner)
    {
        // perform any operation

        //Log.w(TAG, "refreshAuthToken-stage-0");
        // check if listener is registered.
        if (listner != null) {
            //Log.w(TAG, "refreshAuthToken-stage-1");
            AuthToken.GetToken(FirebaseAuth.getInstance().getCurrentUser(), new AuthToken.OnAuthTokenListner() {
                @Override
                public void onAuthToken(String FBToken) {
                    if(FBToken != "")
                    {
                        try {
                            Log.w(TAG, FBToken);
                            String android_id = App.getInstance().getAndroidId();
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("idToken", FBToken);
                            jsonParam.put("app", Constants.AppType);
                            jsonParam.put("fcmtoken", fcmtoken);
                            jsonParam.put("version", Build.VERSION.CODENAME);
                            jsonParam.put("brand", Build.BRAND);
                            jsonParam.put("device", Build.DEVICE);
                            jsonParam.put("deviceid", android_id);
                            jsonParam.put("model", Build.MODEL);
                            jsonParam.put("manufacturer", Build.MANUFACTURER);
                            jsonParam.put("product", Build.PRODUCT);
                            jsonParam.put("androidid", android_id);
                            jsonParam.put("uniqueid", android_id);
                            jsonParam.put("appv", BuildConfig.VERSION_CODE);
                            jsonParam.put("os", Build.VERSION.RELEASE);
                            jsonParam.put("platform", "ANDROID");

                            //Log.w(TAG, "Mitul");
                            CustomRequest jsonReq = new CustomRequest(METHOD_API_GetToken, jsonParam, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(final JSONObject response) {
                                            try {
                                                //Log.w(TAG, "refreshAuthToken-success-1");
                                                Log.w(TAG, response.toString());
                                                JSONObject ResultJ = response.getJSONObject("obj");
                                                if (ResultJ != null) {
                                                    try {
                                                        String Message = response.getString("Message");
                                                        String MessageType = response.getString("MessageType");
                                                        if (MessageType.equals("success")) {
                                                            JSONObject ECBToken = ResultJ.getJSONObject("token");
                                                            App.getInstance().setAccessToken(ECBToken.getString("token"));
                                                            App.getInstance().setAccessToken(ECBToken.getString("token"));
                                                            App.getInstance().setTokenExpiry(ECBToken.getString("token_expiry"));

                                                            //Log.w(TAG, response.toString());
                                                            listner.onECBAuthToken(ECBToken.getString("token"),ResultJ);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        DataGlobal.SaveLog(TAG, e);
                                                        listner.onECBAuthToken("",null);
                                                    }
                                                }
                                            } catch (JSONException ex) {
                                                listner.onECBAuthToken("",null);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.w(TAG, "refreshAuthToken-fail-1",error);
                                    listner.onECBAuthToken("",null);
                                }
                            });
                            App.getInstance().addToRequestQueue(jsonReq);
                        } catch (JSONException ex) {
                            Log.w(TAG, "refreshAuthToken-fail-2");
                            listner.onECBAuthToken("",null);
                        }
                    }
                    else
                    {
                        Log.w(TAG, "refreshAuthToken-fail-3");
                        listner.onECBAuthToken("",null);
                    }
                }
            });



        }
    }

}
