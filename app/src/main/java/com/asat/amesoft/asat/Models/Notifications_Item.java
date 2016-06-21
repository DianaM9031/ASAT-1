package com.asat.amesoft.asat.Models;

import android.graphics.Bitmap;

/**
 * Created by Jorge on 20/06/2016.
 */
public class Notifications_Item {

    private String id;
    private String date;
    private String title;
    private String text;
    private String url;
    private Bitmap icon;


    public Notifications_Item(String id, String date, String title, String text, String url, Bitmap icon) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.text = text;
        this.url = url;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
