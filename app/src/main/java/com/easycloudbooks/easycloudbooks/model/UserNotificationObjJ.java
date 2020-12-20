package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class UserNotificationObjJ extends CommonJsonClass{
    public long Id;
    public  String OT;
    public  String OST;
    public  String OV;

    public static UserNotificationObjJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, UserNotificationObjJ.class);
    }

    public static List<UserNotificationObjJ> getJSONList(String json) {
        Type listType = new TypeToken<List<UserNotificationObjJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    @Override
    public  String toString() {
        return  App.getInstance().getGSON().toJson(this );
    }


}
