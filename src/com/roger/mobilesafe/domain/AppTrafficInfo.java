package com.roger.mobilesafe.domain;

/**
 * Created by Roger on 2014/11/15.
 * app流量信息
 */
public class AppTrafficInfo {
    private String packName;//包名
    private long gprs;//3g流量
    private long wifi;//wifi流量
    private long dayTime;//当天零点的时间戳

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getGprs() {
        return gprs;
    }

    public void setGprs(long gprs) {
        this.gprs = gprs;
    }

    public long getWifi() {
        return wifi;
    }

    public void setWifi(long wifi) {
        this.wifi = wifi;
    }

    public long getDayTime() {
        return dayTime;
    }

    public void setDayTime(long dayTime) {
        this.dayTime = dayTime;
    }
}
