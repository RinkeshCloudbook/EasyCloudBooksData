package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MenuJ extends CommonJsonClass{
    public int PC ;
    public int NC;

    public MenuJ()
    {
        this.PC = 0;
        this.NC = 0;
    }
    public static MenuJ getJSON(String  json) {
       return App.getInstance().getGSON().fromJson(json, MenuJ.class);
    }



}
