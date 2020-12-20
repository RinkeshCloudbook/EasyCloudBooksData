package com.easycloudbooks.easycloudbooks.model;

public class Document_upload_model {

    public String volumeid;
    public String dirs;
    public String name;
    public String error;
    public String path;
    public String hash;
    public String lhash;
    public String mime;
    public String icon;
    public String csscls;
    public String phash;
    public String isspecial;
    public int ts;
    public int size;
    public int read;
    public int write;
    public int isroot;
    public int locked;
    public String guid;

    public String cName;
    public String CRId;
    public String cwdHash;

    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean flage = false;

   /* public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

    public String getHash() {
        return hash;
    }

    public void setHash(String mHash) {
        this.hash = mHash;
    }


}
