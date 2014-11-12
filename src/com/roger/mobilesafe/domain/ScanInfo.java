package com.roger.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Roger on 2014/11/12.
 * 扫描信息(病毒、缓存)
 */
public class ScanInfo {
    private String packName;
    private String name;
    private Drawable icon;
    private boolean Virus;
    private long cacheSize;
    private long codeSize;
    private long dataSize;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(long codeSize) {
        this.codeSize = codeSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

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
