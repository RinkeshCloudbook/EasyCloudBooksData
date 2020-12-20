package com.easycloudbooks.easycloudbooks.widget;

import android.content.Intent;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import  	androidx.appcompat.app.AppCompatDelegate;
import android.view.View;

public class AboutUs_Element {

    private String title;
    private Integer iconDrawable;
    private Integer colorDay;
    private Integer colorNight;
    private String value;
    private Intent intent;
    private Integer gravity;
    private Boolean autoIconColor = true;

    private View.OnClickListener onClickListener;

    public AboutUs_Element() {

    }

    public AboutUs_Element(String title, Integer iconDrawable) {
        this.title = title;
        this.iconDrawable = iconDrawable;
    }

    /**
     * Get the onClickListener for this Element
     * @see View.OnClickListener
     * @return
     */
    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public AboutUs_Element setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public Integer getGravity() {
        return gravity;
    }


    public AboutUs_Element setGravity(Integer gravity) {
        this.gravity = gravity;
        return this;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public AboutUs_Element setTitle(String title) {
        this.title = title;
        return this;
    }

    @DrawableRes
    @Nullable
    public Integer getIconDrawable() {
        return iconDrawable;
    }

    public AboutUs_Element setIconDrawable(@DrawableRes Integer iconDrawable) {
        this.iconDrawable = iconDrawable;
        return this;
    }


    @ColorRes
    @Nullable
    public Integer getIconTint() {
        return colorDay;
    }

    public AboutUs_Element setIconTint(@ColorRes Integer color) {
        this.colorDay = color;
        return this;
    }

    @ColorRes
    public Integer getIconNightTint() {
        return colorNight;
    }

    public AboutUs_Element setIconNightTint(@ColorRes Integer colorNight) {
        this.colorNight = colorNight;
        return this;
    }

    public String getValue() {
        return value;
    }

    public AboutUs_Element setValue(String value) {
        this.value = value;
        return this;
    }


    public Intent getIntent() {
        return intent;
    }

    public AboutUs_Element setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    /**
     * @return the AutoIcon
     */
    public Boolean getAutoApplyIconTint() {
        return autoIconColor;
    }

    /**
     * Automatically apply tint to this Elements icon.
     *
     * @param autoIconColor
     * @return this Element instance for builder pattern support
     */
    public AboutUs_Element setAutoApplyIconTint(Boolean autoIconColor) {
        this.autoIconColor = autoIconColor;
        return this;
    }
}
