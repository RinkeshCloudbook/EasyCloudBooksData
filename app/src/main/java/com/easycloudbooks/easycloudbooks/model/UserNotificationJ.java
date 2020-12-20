package com.easycloudbooks.easycloudbooks.model;

import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

public class UserNotificationJ extends CommonJsonClass{
     public String Id;
    public String T;
    public String D;
    public String Href;
    public  String MT;
    public  String CT;
    public  String DT;
    public  long DI;

    public  String DSI;
    public  DateTime CD;
    public  boolean ST;
    public  boolean IN;
    public  boolean NobjStatus;

    public  UserSmallJ BY ;
    public  long ACId;
    public  CompanyRelationJ CR;
    public  List<UserNotificationDataJ> Data;
    public  List<UserNotificationObjJ> Objs;


    public static UserNotificationJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, UserNotificationJ.class);
    }

    public static List<UserNotificationJ> getJSONList(String json) {
        Type listType = new TypeToken<List<UserNotificationJ>>(){}.getType();
        return App.getInstance().getGSON().fromJson(json,listType);
    }


    public String GetNobjStatus() {
        String Status = "unknown";
        if(Data != null) {
            for (UserNotificationDataJ data : Data) {
                if(data.N.equals("nobjstatus"))
                {
                    Status =data.ObjV;
                    break;
                }
            }
        }
        return Status;
    }

    public String GetImage() {
        String image = "";
        if(Data != null) {
            for (UserNotificationObjJ data : Objs) {
                if(data.OT != null && data.OT.equals("image"))
                {
                    image = data.OV;
                    break;
                }
            }
        }
        return image;
    }

    public void SetNobjStatus(String Status) {
        if(Data != null) {
            for (UserNotificationDataJ data : Data) {
                if(data.N.equals("nobjstatus"))
                {
                    data.ObjV = Status;
                    break;
                }
            }
        }
    }

    public Long GetProjectId() {
        Long ProjectId = 0L;
        if(Data != null) {
            for (UserNotificationDataJ data : Data) {
                if(data.N.equals("Project"))
                {
                    ProjectId = data.ObjId;
                    break;
                }
            }
        }
        return ProjectId;
    }

    public Long GetPaymentRequestId() {
        Long Id = 0L;
        if(Data != null) {
            for (UserNotificationDataJ data : Data) {
                if(data.N.equals("PaymentRequest"))
                {
                    Id = data.ObjId;
                    break;
                }
            }
        }
        return Id;
    }

    public Long GetCompanyRelationId() {
        Long Id = 0L;
        if(Data != null) {
            for (UserNotificationDataJ data : Data) {
                if(data.N.equals("CompanyRelationSub"))
                {
                    Id = data.ObjId;
                    break;
                }
            }
        }
        return Id;
    }

    public void SetIsNew(boolean isnew) {
       this.IN = isnew  ;
    }


}
