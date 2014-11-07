package com.roger.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 进程信息获取
 */
public class TaskInfoEngine {
    public static List<TaskInfo> getTaskInfos(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for(ActivityManager.RunningAppProcessInfo info:processInfos){
            TaskInfo taskInfo = new TaskInfo();
            String packName = info.processName;
            taskInfo.setPackName(packName);
            Debug.MemoryInfo[] memSizes = am.getProcessMemoryInfo(new int[]{info.pid});
            long memSize = memSizes[0].getTotalPrivateDirty()*1024;
            taskInfo.setMemSize(memSize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packName, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                String name = applicationInfo.loadLabel(pm).toString();
                boolean isSystem = (applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 1;
                taskInfo.setIcon(icon);
                taskInfo.setName(name);
                taskInfo.setSystem(isSystem);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setName(packName);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setSystem(true);
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
