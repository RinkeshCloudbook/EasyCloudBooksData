package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

public class AccountJ extends CommonJsonClass{
     public long Id;
    public String N;
    public String AT;

    public DateTime CD;

    public  CompanyRelationJ CR;

    public static AccountJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, AccountJ.class);
    }

    public static List<AccountJ> getJSONList(String json) {
        Type listType = new TypeToken<List<AccountJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }





}
