package com.roger.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.roger.mobilesafe.service.UpdateWidgetService;

/**
 * Created by Roger on 2014/11/10.
 */
public class MyWidget extends AppWidgetProvider {
    private static final String TAG = "MyWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG,"onUpdate...");

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive...");
        Intent updateWidgetIndent = new Intent(context, UpdateWidgetService.class);
        context.startService(updateWidgetIndent);
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG,"onEnabled...");
        Intent updateWidgetIndent = new Intent(context, UpdateWidgetService.class);
        context.startService(updateWidgetIndent);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG,"onDisabled...");
        Intent updateWidgetIndent = new Intent(context, UpdateWidgetService.class);
        context.stopService(updateWidgetIndent);
        super.onDisabled(context);
    }
}
