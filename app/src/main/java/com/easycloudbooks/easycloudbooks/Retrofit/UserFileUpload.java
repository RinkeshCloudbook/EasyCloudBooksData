package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserFileUpload {
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MessageType")
    @Expose
    private String messageType;
    @SerializedName("obj")
    @Expose
    private UploadResult obj;
    @SerializedName("obj1")
    @Expose
    private Object obj1;
    @SerializedName("TotalCount")
    @Expose
    private Double totalCount;
    @SerializedName("R")
    @Expose
    private Boolean r;
    @SerializedName("W")
    @Expose
    private Boolean w;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public UploadResult getObj() {
        return obj;
    }

    public void setObj(UploadResult obj) {
        this.obj = obj;
    }

    public Object getObj1() {
        return obj1;
    }

    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    public Double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Double totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getR() {
        return r;
    }

    public void setR(Boolean r) {
        this.r = r;
    }

    public Boolean getW() {
        return w;
    }

    public void setW(Boolean w) {
        this.w = w;
    }
}
