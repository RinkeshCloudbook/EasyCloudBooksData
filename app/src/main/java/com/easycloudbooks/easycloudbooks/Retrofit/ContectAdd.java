package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContectAdd {
    @SerializedName("L1")
    @Expose
    private String l1;
    @SerializedName("T")
    @Expose
    private String t;
    @SerializedName("L2")
    @Expose
    private String l2;
    @SerializedName("C")
    @Expose
    private String c;
    @SerializedName("A")
    @Expose
    private String a;
    @SerializedName("S")
    @Expose
    private String s;
    @SerializedName("CR")
    @Expose
    private String cR;
    @SerializedName("Zip")
    @Expose
    private String zip;
    @SerializedName("N")
    @Expose
    private String n;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Lan")
    @Expose
    private String lan;
    @SerializedName("Lat")
    @Expose
    private String lat;
    @SerializedName("IP")
    @Expose
    private Boolean iP;
    @SerializedName("F")
    @Expose
    private Object f;

    public String getL1() {
        return l1;
    }

    public void setL1(String l1) {
        this.l1 = l1;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getL2() {
        return l2;
    }

    public void setL2(String l2) {
        this.l2 = l2;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getCR() {
        return cR;
    }

    public void setCR(String cR) {
        this.cR = cR;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Boolean getIP() {
        return iP;
    }

    public void setIP(Boolean iP) {
        this.iP = iP;
    }

    public Object getF() {
        return f;
    }

    public void setF(Object f) {
        this.f = f;
    }
}
