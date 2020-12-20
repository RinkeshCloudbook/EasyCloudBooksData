package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CompanyRelationSubServiceJ extends CommonJsonClass{
    public long Id;
    public  String N;
    public  long SId;

    public static CompanyRelationSubServiceJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, CompanyRelationSubServiceJ.class);
    }

    public static List<CompanyRelationSubServiceJ> getJSONList(String json) {
        Type listType = new TypeToken<List<CompanyRelationSubServiceJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    @Override
    public  String toString() {
        Log.w("ProjectNoteJ", "toString: " +App.getInstance().getGSON().toJson(this ) );
        return  App.getInstance().getGSON().toJson(this );
    }


}
