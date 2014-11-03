package com.roger.mobilesafe.domain;

/**
 * Created by Roger on 2014/10/28.
 * 黑名单实体
 */
public class BlacklistInfo {
    private String number;
    private String mode;//拦截模式：1、电话拦截 2、短信拦截 3、全部拦截

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStringMode(){
        String stringMode = "";
        if("1".equals(mode)){
            stringMode = "电话拦截";
        }else if("2".equals(mode)){
            stringMode = "短信拦截";
        }else if("3".equals(mode)){
            stringMode = "全部拦截";
        }
        return stringMode;
    }

    /**
     * 是否拦截短信
     * @return
     */
    public boolean isSmsSafe(){
        return "2".equals(mode)||"3".equals(mode);
    }

    /**
     * 判断是否拦截电话
     * @return
     */
    public boolean isCallSafe(){
        return "1".equals(mode)||"3".equals(mode);
    }
}
