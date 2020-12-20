package com.easycloudbooks.easycloudbooks.app;


import android.os.Build;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_ACCOUNT_GET_CURRENT_USER;


public class ECBGetCurrentUser {
    public  static  String TAG = "ECBGetCurrentUser";
    // setting the listener

    public interface OnECBCurrentUserListner {
        public void onECBCurrentUser( JSONObject ResultJ);
    }
    public static CustomAuthRequest Get(final boolean MenuRequired,final OnECBCurrentUserListner listner)
    {
        if (listner != null) {
            try {
            String android_id = App.getInstance().getAndroidId();
            JSONObject jsonParam = new JSONObject();
                jsonParam.put("MR", MenuRequired);
               return new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_GET_CURRENT_USER, jsonParam,0,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listner.onECBCurrentUser(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listner.onECBCurrentUser(null);
                    }
                }) {

                    @Override
                    protected void onCreateFinished(CustomAuthRequest request) {
                        int socketTimeout = 0;//0 seconds - change to what you want
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        request.customRequest.setRetryPolicy(policy);
                        App.getInstance().addToRequestQueue(request);
                    }
                };
            } catch (JSONException ex) {
                listner.onECBCurrentUser(null);
            }
        }
        return  null;
    }

}
