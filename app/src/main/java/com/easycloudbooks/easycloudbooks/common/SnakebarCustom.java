package com.easycloudbooks.easycloudbooks.common;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.easycloudbooks.easycloudbooks.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by Mitul on 26-05-2017.
 */

public class SnakebarCustom {
    public  static final int  LongDuration = 5000;


    public static void danger(Context mContext, View view, String Message, int Duration)
    {
        Snackbar snackbar = Snackbar.make(view, Message, Duration);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cpb_red));
        snackbar.show();
    }

    public static void info(Context mContext, View view, String Message, int Duration)
    {
        Snackbar snackbar = Snackbar.make(view, Message, Duration);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cpb_blue));
        snackbar.show();
    }


    public static void success(Context mContext, View view, String Message, int Duration)
    {
        Snackbar snackbar = Snackbar.make(view, Message, Duration);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cpb_green_dark));
        snackbar.show();
    }

}
