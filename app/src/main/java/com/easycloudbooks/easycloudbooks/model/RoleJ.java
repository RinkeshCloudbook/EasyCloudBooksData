package com.easycloudbooks.easycloudbooks.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoleJ extends CommonJsonClass {
    private String TAG = RoleJ.class.getSimpleName();

    public String N;//Role Name
    @Nullable
    public long RId;
    public String PN;//PageName
    public String EPN;//EquavalentPageName
    public List<RoleSettingJ> S; //Settings
    public boolean IMO;


    public RoleJ() {

    }

    public RoleJ(long RId, String N, String PN, String EPN, boolean IMO) {
        this.RId = RId;
        this.N = N;
        this.PN = PN;
        this.EPN = EPN;
        this.IMO = IMO;
    }

    public static RoleJ getJSON(String  json) {
        return App.getInstance().getGSON().fromJson(json, RoleJ.class);
    }

    public static List<RoleJ> getJSONList(String json) {
        Type listType = new TypeToken<List<RoleJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    public boolean HavePermission(String Page, String Property) {
        boolean HP = false;
        if (this.IMO) HP = true;
        else {
            if (this.S != null && this.S.size() > 0)
                for (int i = 0; i < this.S.size(); i++) {
                    RoleSettingJ s = this.S.get(i);
                    if (s.PN.equals(Page) && s.N.equals(Property)) {
                        HP = true;
                        break;
                    }
                }
        }
        return HP;
    }

    public void setSs(JSONArray jsonDatas) {
        try {
            this.S = new ArrayList<>();
            for (int i = 0; i < jsonDatas.length(); i++) {
                this.S.add(new RoleSettingJ(jsonDatas.getJSONObject(i)));
            }
        } catch (Throwable t) {
            Log.e(TAG, "Could not parse malformed JSON:");
        }
    }

    public long getId() {
        return RId;
    }

    public void setId(long id) {
        this.RId = id;
    }

    public String getName() {
        return N;
    }


}
