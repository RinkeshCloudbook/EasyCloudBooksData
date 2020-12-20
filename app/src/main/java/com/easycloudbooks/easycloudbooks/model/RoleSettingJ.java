package com.easycloudbooks.easycloudbooks.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoleSettingJ extends CommonJsonClass {
    private String TAG = RoleSettingJ.class.getSimpleName();

    public String N ;//Role Name
    public long URPId; //UserRolePropertyId
    public String PN ;//PageName


    public RoleSettingJ() {

    }

    public RoleSettingJ(long URPId, String N, String PN) {
        this.URPId = URPId;
        this.N = N;
        this.PN = PN;
    }

    public RoleSettingJ(JSONObject jsonData) {
        try {
            this.URPId = (jsonData.getLong("URPId"));
            this.N = (jsonData.getString("N"));
            this.PN = (jsonData.getString("PN"));
        } catch (Throwable t) {

            Log.e(TAG, "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        }
    }

    public static RoleJ getJSON(String  json) {
        return App.getInstance().getGSON().fromJson(json, RoleJ.class);
    }

    public static List<RoleJ> getJSONList(String json) {
        Type listType = new TypeToken<List<RoleJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    protected RoleSettingJ(Parcel in) {
        this.URPId = in.readLong();
        this.N = in.readString();
        this.PN = in.readString();
    }

    public long getId() {
        return URPId;
    }

    public void setId(long id) {
        this.URPId = id;
    }

    public String getName() {
        return N;
    }



}
