package com.nds.dlp;

import android.graphics.drawable.Drawable;

public class RollItem {
    private String appName;
    private String packageName;
    private Drawable drawable;

    public RollItem(String appName, String packageName, Drawable drawable){
        this.appName = appName;
        this.packageName = packageName;
        this.drawable = drawable;
    }

    public String getAppName(){
        return  appName;
    }

    public String getPackageName(){
        return packageName;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
