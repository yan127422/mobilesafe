package com.roger.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Roger on 2014/10/23.
 */
public class ServiceUtils{
    /**
     * 校验服务是否活着
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = manager.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info:infos){
            if(serviceName.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
