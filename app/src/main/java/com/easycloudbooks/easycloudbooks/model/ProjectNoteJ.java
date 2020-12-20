package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.firebase.database.logging.LogWrapper;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

public class ProjectNoteJ extends CommonJsonClass{
    public long Id;
    public  String N;
    public DateTime CD;
    public DateTime ND;
    public AccountantJ CB;

    public static ProjectNoteJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, ProjectNoteJ.class);
    }

    public static List<ProjectNoteJ> getJSONList(String json) {
        Type listType = new TypeToken<List<ProjectNoteJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    @Override
    public  String toString() {
        Log.w("ProjectNoteJ", "toString: " +App.getInstance().getGSON().toJson(this ) );
        return  App.getInstance().getGSON().toJson(this );
    }


}
