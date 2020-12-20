package com.easycloudbooks.easycloudbooks.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;


public class DataAuthInfo {
    public DataAuthInfo(Context mcontext,long mUserId,long mDId ,String  mDeviceId, String mFCMReturnToken){
        context = mcontext;
        UserId = mUserId;
        DId = mDId;
        DeviceId = mDeviceId;
        FCMReturnToken = mFCMReturnToken;
        FCMAuthReturnToken = FirebaseInstanceId.getInstance().getToken();
    }
    public DataAuthInfo(Context mcontext,String mFCMReturnToken){
        context = mcontext;
        UserId = App.getInstance().getUserId();
        DId =  App.getInstance().getDeviceId();
        DeviceId =  Settings.Secure.getString(mcontext.getContentResolver(), Settings.Secure.ANDROID_ID);
        FCMReturnToken = mFCMReturnToken ;
        FCMAuthReturnToken = FirebaseInstanceId.getInstance().getToken();
    }
    public DataAuthInfo(SharedPreferences pref,String  mDeviceId,String mFCMReturnToken){
        UserId = pref.getLong("User.UserId",0);
        DId =  pref.getLong("User.DId",0);
        DeviceId =  mDeviceId;
        FCMReturnToken = mFCMReturnToken;
        FCMAuthReturnToken = FirebaseInstanceId.getInstance().getToken();
    }

    public Context context;
    public long UserId;
    public long  DId;
    public String  DeviceId;
    public String FCMReturnToken;
    public String FCMAuthReturnToken;
}
