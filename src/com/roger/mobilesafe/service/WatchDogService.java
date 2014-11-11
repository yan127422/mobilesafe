package com.roger.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.roger.mobilesafe.activity.EnterPwdActivity;
import com.roger.mobilesafe.db.dao.ApplockDao;
import com.roger.mobilesafe.receiver.BootCompleteReceiver;
import com.roger.mobilesafe.utils.MyConstants;

import java.util.List;

/**
 * Created by Roger on 2014/11/11.
 * 看门狗服务、监视每个应用打开
 */
public class WatchDogService extends Service{
    private static final String TAG = "WatchDogService";
    private ActivityManager am;
    private ApplockDao applockDao;
    private boolean flag;
    private Intent enterPwdIntent;
    private BroadcastReceiver tempStopReceiver,screenOffReceiver;//临时取消程序锁广播、锁屏广播
    private String tempPackName;//临时取消保护包名
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        applockDao = new ApplockDao(this);
        flag = true;
        initReceiver();
        enterPwdIntent = new Intent(this, EnterPwdActivity.class);
        enterPwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(){
            @Override
            public void run() {
                while (flag){
                    //任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks=  am.getRunningTasks(100);
                    //当前用户操作的应用程序名
                    String packName = runningTasks.get(0).topActivity.getPackageName();
                    Log.i(TAG,packName);
                    if(applockDao.find(packName)&&(!packName.equals(tempPackName))){//需要保护的应用、弹出数量密码界面
                        enterPwdIntent.putExtra(MyConstants.PACK_NAME,packName);
                        startActivity(enterPwdIntent);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        super.onCreate();
    }

    /**
     * 注册广播事件
     */
    private void initReceiver() {
        tempStopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tempPackName = intent.getStringExtra(MyConstants.PACK_NAME);
            }
        };
        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tempPackName = null;
            }
        };

        registerReceiver(tempStopReceiver,new IntentFilter(MyConstants.BROADCAST_TEMP_STOP));
        registerReceiver(screenOffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(tempStopReceiver);
        tempStopReceiver = null;
        unregisterReceiver(screenOffReceiver);
        screenOffReceiver = null;

        super.onDestroy();
    }
}
