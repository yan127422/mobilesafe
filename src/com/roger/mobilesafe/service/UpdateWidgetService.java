package com.roger.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.engine.TaskInfoEngine;
import com.roger.mobilesafe.receiver.MyWidget;
import com.roger.mobilesafe.utils.SystemInfoUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 更新小工具界面
 */
public class UpdateWidgetService extends Service {
    private static final String TAG = "UpdateWidgetService";
    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        awm = AppWidgetManager.getInstance(this);
        task = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "更新widget...");
                //设置更新的组件
                ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                remoteViews.setTextViewText(R.id.process_count, "运行的进行：" + TaskInfoEngine.getTaskInfos(getApplicationContext()).size() + "个");
                String memory = Formatter.formatFileSize(getApplicationContext(), SystemInfoUtil.getAvailMemory(getApplicationContext()));
                remoteViews.setTextViewText(R.id.process_memory, "可用内存：" + memory);
                //描述一个动作，这个动作有另外一个应用程序执行
                //自定义一个广播事件，杀死后台进程的事件
                Intent intent = new Intent();
                intent.setAction("com.roger.mobilesafe.killall");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                awm.updateAppWidget(provider, remoteViews);
            }
        };
        timer.schedule(task, 0, 3000);
    }
}
