package com.easycloudbooks.easycloudbooks.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

/**
 * Created by Ahmed on 12/19/2015.
 */
public class UIUtils {
    private static int screenWidth = -1;

    // size to the
    // screen width
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {

        if (screenWidth != -1)
            return screenWidth;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();

        int width;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();

        }
        screenWidth = width;

        return width;
    }

    public static void resizeView(View view, int width, int height) {
        ViewGroup.LayoutParams layout = view.getLayoutParams();
        layout.width = width;
        layout.height = height;
        view.setLayoutParams(layout);
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int getScreenWidth(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.heightPixels;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixels(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
