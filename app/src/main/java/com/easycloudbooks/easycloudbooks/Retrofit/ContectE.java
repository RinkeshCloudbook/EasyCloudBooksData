package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContectE {

    @SerializedName("E")
    @Expose
    private String e;
    @SerializedName("T")
    @Expose
    private String t;
    @SerializedName("IV")
    @Expose
    private Boolean iV;
    @SerializedName("IP")
    @Expose
    private Boolean iP;
    @SerializedName("SN")
    @Expose
    private Boolean sN;
    @SerializedName("NV")
    @Expose
    private Boolean nV;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("VSD")
    @Expose
    private Object vSD;
    @SerializedName("SV")
    @Expose
    private Boolean sV;

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public Boolean getIV() {
        return iV;
    }

    public void setIV(Boolean iV) {
        this.iV = iV;
    }

    public Boolean getIP() {
        return iP;
    }

    public void setIP(Boolean iP) {
        this.iP = iP;
    }

    public Boolean getSN() {
        return sN;
    }

    public void setSN(Boolean sN) {
        this.sN = sN;
    }

    public Boolean getNV() {
        return nV;
    }

    public void setNV(Boolean nV) {
        this.nV = nV;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getVSD() {
        return vSD;
    }

    public void setVSD(Object vSD) {
        this.vSD = vSD;
    }

    public Boolean getSV() {
        return sV;
    }

    public void setSV(Boolean sV) {
        this.sV = sV;
    }
}
