package com.roger.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by Roger on 2014/11/7.
 *锁屏 自动清理进程服务
 */
public class AutoCleanService extends Service{
    private static final String TAG = "AutoCleanService";
    private BroadcastReceiver receiver;
    private ActivityManager am;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new ScreenOffReceiver();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver,intentFilter);
        super.onCreate();
    }

    private class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"屏幕锁屏...");
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info:infos){
                am.killBackgroundProcesses(info.processName);
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
