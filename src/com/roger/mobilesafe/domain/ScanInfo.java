package com.roger.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Roger on 2014/11/12.
 * 病毒扫描信息
 */
public class ScanInfo {
    private String name;
    private Drawable icon;
    private boolean Virus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isVirus() {
        return Virus;
    }

    public void setVirus(boolean virus) {
        Virus = virus;
    }
}
