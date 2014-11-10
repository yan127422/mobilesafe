package com.roger.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by Roger on 2014/11/10.
 * 杀死进程接受者
 */
public class KillAllReceiver extends BroadcastReceiver{

    private static final String TAG = "KillAllReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"接收到清理进程的广播");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:infos){
            am.killBackgroundProcesses(info.processName);
        }
    }
}
