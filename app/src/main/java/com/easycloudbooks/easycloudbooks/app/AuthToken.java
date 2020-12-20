package com.easycloudbooks.easycloudbooks.app;


import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.util.CustomRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_API_GetToken;


public class AuthToken {


    public  static  String TAG = "AuthToken";
    // setting the listener


    public interface OnAuthTokenListner {
        public void onAuthToken(String token);
    }



    // my synchronous task
    public static void GetToken(FirebaseUser user,final OnAuthTokenListner mListner)
    {
        // perform any operation
        System.out.println("GetToken");

        // check if listener is registered.
        if (mListner != null) {


            Task<GetTokenResult> task = user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    Log.w(TAG, "getFirebaseAuthTokenresult");
                    if (task.isSuccessful()) {
                        Log.w(TAG, "getFirebaseAuthToken-success");
                        mListner.onAuthToken(task.getResult().getToken());
                    }
                    else{
                        Log.w(TAG, "getFirebaseAuthToken-error");
                        mListner.onAuthToken("");
                    }
                }
            });
        }
    }

}
