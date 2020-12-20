package com.easycloudbooks.easycloudbooks.model;


import android.graphics.Color;

public class Avatar {
    public  String Name;
    public  String Image;
    public  int color;
    public  int shape; //0= Circle; 1=Squere
    public Avatar(String Name,String Image, int color, int shape)
    {
        this.Image = Image;
        this.Name = Name;
        this.color = color;
        this.shape = shape;
    }

    public Avatar UserAvatar( String Name,String Image)
    {
        this.Image = Image;
        this.Name = Name;
        this.color = Color.parseColor("#00ff00");
        this.shape = 0;
        return this;
    }
    public Avatar CompanyAvatar( String Name,String Image)
    {
        this.Image = Image;
        this.Name = Name;
        this.color = Color.parseColor("#00ff00");
        this.shape = 1;
        return this;
    }

    public int getColor()
    {
        return  color;
    }

    public String getName()
    {
        return  Name;
    }

    public String getImage()
    {
        return  Image;
    }
    public int getShape()
    {
        if(this.shape > 0)
             return  this.shape;
        else return  0;
    }
}
