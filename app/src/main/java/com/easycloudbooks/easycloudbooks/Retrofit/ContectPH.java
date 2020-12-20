package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContectPH {
    @SerializedName("CC")
    @Expose
    private String cC;
    @SerializedName("Ph")
    @Expose
    private String ph;
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
    @SerializedName("VSD")
    @Expose
    private Object vSD;
    @SerializedName("SV")
    @Expose
    private Boolean sV;
    @SerializedName("Id")
    @Expose
    private Integer id;

    public String getCC() {
        return cC;
    }

    public void setCC(String cC) {
        this.cC = cC;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
