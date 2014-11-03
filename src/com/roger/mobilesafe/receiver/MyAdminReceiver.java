package com.roger.mobilesafe.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Roger on 2014/10/23.
 */
public class MyAdminReceiver extends DeviceAdminReceiver{
    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "onEnabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "onDisableRequested";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "onDisabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "onPasswordChanged");
    }

}
