package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

public class ProjectJ extends CommonJsonClass{
     public long ProjectId;
    public String CDDN;
    public String UPDDN;
    public String UId;
    public  String CUN;
    public  String Title;
    public  String Year;
    public  String Description;
    public  String Status;
    public  DateTime CreateDate;
    public  DateTime DueDate;
    public  DateTime CUD;
    public  DateTime CDD;
    public  DateTime UPDD;
    public String pId;

    public  AccountantJ PMR;
    public  ServiceJ Service;
    public  CompanyRelationJ CR;
    public  List<ProjectNoteJ> Notes;

    public static ProjectJ getJSON(String  json) {
        Log.w("ProjectJ", "getJSON: "+json );
       return App.getInstance().getGSON().fromJson(json, ProjectJ.class);
    }

    public static List<ProjectJ> getJSONList(String json) {
        Type listType = new TypeToken<List<ProjectJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    public String getNameLetter() {
        return "MP";
    }

    /*public String getNameLetter() {
        return FN.substring(0, 1) + LN.substring(0, 1);
    }*/



}
