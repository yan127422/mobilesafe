package com.roger.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 系统信息
 */
public class SystemInfoUtil {
    public static long getAvailMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
//    public static long getTotalMemory2(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//        return memoryInfo.totalMem;//api 16
//    }
    /**
     * 获取总内存
     * @param context
     * @return byte
     */
    public static long getTotalMemory(Context context){
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String totalMemary = br.readLine();
            /*
            /proc/meminfo
                MemTotal: 16344972 kB
                MemFree: 13634064 kB
                Buffers: 3656 kB
                Cached: 1195708 kB
             */
            StringBuffer sb = new StringBuffer();
            for(char c : totalMemary.toCharArray()){
                if(c>='0'&&c<='9'){
                    sb.append(c);
                }
            }
            fis.close();
            br.close();
            return Long.parseLong(sb.toString())*1024;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
