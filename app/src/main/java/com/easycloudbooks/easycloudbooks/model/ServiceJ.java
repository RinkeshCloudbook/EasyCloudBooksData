package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ServiceJ extends CommonJsonClass{
    public long ServiceId;
    public String ServiceName;
    public static ServiceJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, ServiceJ.class);
    }

    public static List<ServiceJ> getJSONList(String json) {
        Type listType = new TypeToken<List<ServiceJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }


}
