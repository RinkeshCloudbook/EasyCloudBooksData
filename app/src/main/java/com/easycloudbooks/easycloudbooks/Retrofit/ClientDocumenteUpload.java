package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClientDocumenteUpload {
    @SerializedName("phash")
    @Expose
    private String phash;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isroot")
    @Expose
    private Integer isroot;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("lhash")
    @Expose
    private Object lhash;
    @SerializedName("mime")
    @Expose
    private String mime;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("csscls")
    @Expose
    private String csscls;
    @SerializedName("ts")
    @Expose
    private Integer ts;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("read")
    @Expose
    private Integer read;
    @SerializedName("write")
    @Expose
    private Integer write;
    @SerializedName("locked")
    @Expose
    private Integer locked;
    @SerializedName("guid")
    @Expose
    private String guid;

    public String getPhash() {
        return phash;
    }

    public void setPhash(String phash) {
        this.phash = phash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsroot() {
        return isroot;
    }

    public void setIsroot(Integer isroot) {
        this.isroot = isroot;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Object getLhash() {
        return lhash;
    }

    public void setLhash(Object lhash) {
        this.lhash = lhash;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCsscls() {
        return csscls;
    }

    public void setCsscls(String csscls) {
        this.csscls = csscls;
    }

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    public Integer getWrite() {
        return write;
    }

    public void setWrite(Integer write) {
        this.write = write;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

}
