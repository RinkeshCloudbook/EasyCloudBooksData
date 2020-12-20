package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContectD {
    @SerializedName("D")
    @Expose
    private String d;
    @SerializedName("T")
    @Expose
    private String t;
    @SerializedName("IV")
    @Expose
    private Boolean iV;
    @SerializedName("NR")
    @Expose
    private Boolean nR;
    @SerializedName("Id")
    @Expose
    private Integer id;

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
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

    public Boolean getNR() {
        return nR;
    }

    public void setNR(Boolean nR) {
        this.nR = nR;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
