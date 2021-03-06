package com.roger.mobilesafe.utils;

import android.content.IntentFilter;

/**
 * Created by Roger on 2014/10/9.
 */
public class MyConstants {
    public static final String IS_AUTO_UPDATE = "isAutoUpdate";
    public static final String SETUP_PWD = "setupPWD";//手机防盗密码
    public static final String CONFIGED = "configed";//是否进行过设置向导
    public static final String IS_PROTECT = "isProtect";//是否防盗保护
    public static final String SAFE_NUMBER = "safeNumber";//安全号码
    public static final String SIM_SERIAL_NUMBER = "simSerialNumber";//手机Sim卡序列号
    public static final String LOCATION = "location";
    public static final String IS_SHOW_ADDRESS = "isShowAddress";//号码归属地开启
    public static final String IS_SHORTCUT_CREATED = "isShortcutCreated";//快捷方式是否创建
    public static final String IS_SHOW_SYSTEM_TASK = "isShowSystemTask";//进程管理是否显示系统进程
    public static final String BROADCAST_TEMP_STOP = "com.roger.mobilesafe.tempstop";//暂时停止程序锁广播
    public static final String PACK_NAME = "packName";//应用程序包名
    public static final String BROADCAST_APP_LOCK_CHANGED = "com.roger.mobilesafe.appLockChanged";//程序锁列表改变广播
}
