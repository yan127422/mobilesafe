package com.roger.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.service.GpsService;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/18.
 */
public class SmsReceiver extends BroadcastReceiver{
    private static final String TAG = "SmsReceiver";
    private SharedPreferences preferences;
    private DevicePolicyManager dpm;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        Object messages[] = (Object[]) bundle.get("pdus");
        for(Object o:messages){
            SmsMessage sms = SmsMessage.createFromPdu((byte[])o);
            String sender = sms.getOriginatingAddress();
            String body = sms.getMessageBody();
            Log.i(TAG,"sender="+sender+",body="+body);
            String safeNumber = context.getSharedPreferences("config",Context.MODE_PRIVATE)
                                       .getString(MyConstants.SAFE_NUMBER, "");
            if(!sender.equals(safeNumber))return;
            if("#*location*#".equals(body)){
                Log.i(TAG,"获取GPS数据");
                //终止广播
                Intent intent1 = new Intent(context, GpsService.class);
                context.startService(intent1);
                String location = preferences.getString(MyConstants.LOCATION,null);
                if(TextUtils.isEmpty(location)){
                    SmsManager.getDefault().sendTextMessage(sender,null,"get location...",null,null);
                }else{
                    SmsManager.getDefault().sendTextMessage(sender,null,location,null,null);
                }
                abortBroadcast();
            }else if("#*alarm*#".equals(body)){
                Log.i(TAG,"播放报警音乐");
                MediaPlayer player = MediaPlayer.create(context,R.raw.ylzs);
                player.setLooping(true);
                player.setVolume(1.0f,1.0f);
                player.start();
                abortBroadcast();
            }else if("#*wipedata*#".equals(body)){
                Log.i(TAG,"清除数据");
                if(isAdmin(context)){
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    dpm.wipeData(0);
                }
                abortBroadcast();
            }else if(body.startsWith("#*lockscreen*#")){
                Log.i(TAG,"远程锁屏");
                String password = body.replace("#*lockscreen*#","");
                if(isAdmin(context)){
                    dpm.lockNow();
                    dpm.resetPassword(password,0);
                }
                abortBroadcast();
            }
        }
    }
    private boolean isAdmin(Context context){
        ComponentName mDeviceAdmin = new ComponentName(context, MyAdminReceiver.class);
        return dpm.isAdminActive(mDeviceAdmin);
    }
}
