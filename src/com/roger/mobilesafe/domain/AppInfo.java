package com.roger.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用信息
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String packName;
    private boolean isRom;//是否安装在手机内部（而不是SD卡上）
    private boolean isSystem;//是否系统应用
    private int uid;//用户id

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean isRom) {
        this.isRom = isRom;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }
}
