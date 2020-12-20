package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ContactDetails {
    public  long CRId;
    public String FN;
    public String MN;
    public String LN;
    public String Sex;
    public String date;
    public String email;
    public String Ph;
    public String userId;
    public String address;
    public String imageUrl;
    public String industryType;
    public String position;
    public String DOB;
    public String jdate;
    public String cId;
    public String cName;
    public String fullName;
    public int totalCount;
    public long contectId;

    public static ContactDetails getJSON(String  json) {
        return App.getInstance().getGSON().fromJson(json, ContactDetails.class);
    }

    public static List<ContactDetails> getJSONList(String json) {
        Type listType = new TypeToken<List<ContactDetails>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }




}
