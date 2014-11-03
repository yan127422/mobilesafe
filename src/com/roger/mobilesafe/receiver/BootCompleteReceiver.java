package com.roger.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/16.
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    private static final String TAG = "BootCompleteReceiver";
    private SharedPreferences config;
    private TelephonyManager tm;
    @Override
    public void onReceive(Context context, Intent intent) {
        //对比Sim卡信息
        config = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        String savedSim = config.getString(MyConstants.SIM_SERIAL_NUMBER,"")+"-";
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String realSim = tm.getSimSerialNumber();
        Log.e(TAG,realSim);
        if(!savedSim.equals(realSim)){//sim不一样
            Log.e(TAG,"sim卡变更");
            String safenumber = config.getString(MyConstants.SAFE_NUMBER, "");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(safenumber, null, "sim card has changed!", null, null);
        }
    }
}
