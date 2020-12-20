package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class CompanyRelationJ  extends CommonJsonClass{
    public  long CRId;
    public  String Name;
    public  String  I;
    public  String LSO;
    public  String IndO;
    public   String LS;
    public   String Ind;
    public   String UId;
    public   int PP;
    public   int SC;
    public   AccountantJ CM;
    public  List<CompanyRelationSubServiceJ> Services;

    public static CompanyRelationJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, CompanyRelationJ.class);
    }

    public static List<CompanyRelationJ> getJSONList(String json) {
        Type listType = new TypeToken<List<CompanyRelationJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }


}
