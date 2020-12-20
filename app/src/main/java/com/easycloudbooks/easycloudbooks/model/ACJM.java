package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ACJM extends CommonJsonClass{
    public long ACId ;
    public long CRId;
    public String N ;
    public String I;
    public int PC; //CurrentProjectCount
    public int PCC; //ProjectCompletedCount
    public int CC; //CompanyCount
    public int CCount; //ContactCount
    public int NC; //NotificationCount
    public int NCN; //NewNotificationCount
    public int PRC; //PaymentGateway
    public String R; //Role

    public static ACJM getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, ACJM.class);
    }

    public static List<ACJM> getJSONList(String json) {
        Type listType = new TypeToken<List<ACJM>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }


}
