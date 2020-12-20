package com.easycloudbooks.easycloudbooks.model;

import com.easycloudbooks.easycloudbooks.app.App;

import java.io.Serializable;

public class CommonJsonClass implements Serializable {

    public boolean isExpanded;
    public boolean isRead;
    public boolean isPinned;
    private int color = -1;

    @Override
    public String toString() {
        return  App.getInstance().getGSON().toJson(this );
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean Expanded) {
        isExpanded = Expanded;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isPinned() {
        return isPinned;
    }

}
