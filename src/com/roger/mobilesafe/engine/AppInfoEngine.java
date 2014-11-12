package com.roger.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.roger.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用业务类
 */
public class AppInfoEngine {
    /**
     * 获取应用列表
     * @param context
     * @return
     */
    public static List<AppInfo> getApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pis = pm.getInstalledPackages(0);
        List<AppInfo>infos = new ArrayList<AppInfo>();
        for(PackageInfo pi:pis){
            AppInfo info = new AppInfo();
            info.setPackName(pi.packageName);
            info.setIcon(pi.applicationInfo.loadIcon(pm));
            info.setName(pi.applicationInfo.loadLabel(pm).toString());
            info.setRom((pi.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=1);
            info.setSystem((pi.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==1);
            info.setUid(pi.applicationInfo.uid);
            infos.add(info);
        }
        return infos;
    }
}
