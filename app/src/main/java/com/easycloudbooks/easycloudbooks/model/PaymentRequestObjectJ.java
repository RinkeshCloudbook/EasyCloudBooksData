package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

public class PaymentRequestObjectJ extends CommonJsonClass{
    public long Id;
    public  String ObjT;
    public  String ObjV;
    public  DateTime ObjD;
    public long ObjId;

    public static PaymentRequestObjectJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, PaymentRequestObjectJ.class);
    }

    public static List<PaymentRequestObjectJ> getJSONList(String json) {
        Type listType = new TypeToken<List<PaymentRequestObjectJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }

    @Override
    public  String toString() {
        return  App.getInstance().getGSON().toJson(this );
    }


}
