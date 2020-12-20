package com.easycloudbooks.easycloudbooks.common;

import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.crashlytics.android.Crashlytics;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Mitul on 24-04-2017.
 */

public class DataGlobal extends Application {

    public static String TAG = "DataGlobal";


    public static void SaveLog(String Tag, Exception ex,Context mContext) {
        SaveLog(TAG,ex);
    }
    public static void SaveLog(String Tag, Exception ex) {
        try {
            Log.e(TAG, "SaveLog: "+ ex.getMessage() );
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONObject LogData = new JSONObject();
            if (user != null) {
                LogData.put("UId", user.getUid());
            }
            if (ex != null) {
                LogData.put("exMessage", ex.getMessage());
                LogData.put("StackTrace", Log.getStackTraceString(ex));
            }
            String android_id = App.getInstance().getAndroidId();
            LogData.put("android_id", android_id);
            Crashlytics.log(Log.ERROR, Tag, LogData.toString());
            Crashlytics.logException(ex);
        } catch (JSONException ex1) {
            Crashlytics.logException(ex);
            Crashlytics.logException(ex1);
        }
    }

    public static Bundle getLeftActivityAnimation(Context mContext) {
        return ActivityOptions.makeCustomAnimation(mContext, R.anim.push_left_in, R.anim.push_left_out).toBundle();
    }

    public static Bundle getRightActivityAnimation(Context mContext) {
        return ActivityOptions.makeCustomAnimation(mContext, R.anim.push_right_in, R.anim.push_right_out).toBundle();
    }

    public static Bundle getFadeInActivityAnimation(Context mContext) {
        return ActivityOptions.makeCustomAnimation(mContext, R.anim.fade_in, R.anim.fade_out).toBundle();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    public  static  void SetGlide(Context mContext, String Image, ImageView view){
        SetGlide(mContext, Image, view, false);
    }
    public  static  void SetGlide(Context mContext, String Image, ImageView view, boolean Reset){
        Glide.with(mContext).load(Image)
                .transition(withCrossFade())
                .apply( new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_action_gallery))
                .into(view);
    }
    public  static RequestOptions ProfileBigPicture(){
        return   new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_action_gallery);
    }
}
