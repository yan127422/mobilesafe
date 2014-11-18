package com.roger.mobilesafe.domain;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

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
    private long traffic;//流量

    public long getTraffic() {
        return traffic;
    }

    public void setTraffic(long traffic) {
        this.traffic = traffic;
    }

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

    /**
     * 流量排序(倒序)
     */
    public static class TrafficComparator implements Comparator<AppInfo>{

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            long l = lhs.getTraffic()-rhs.getTraffic();
            if(l==0)return 0;
            return l>0?-1:1;
        }
    }

}
