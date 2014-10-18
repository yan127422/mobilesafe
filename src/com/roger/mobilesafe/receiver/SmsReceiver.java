package com.roger.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/18.
 */
public class SmsReceiver extends BroadcastReceiver{
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
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
                abortBroadcast();
            }else if("#*lockscreen*#".equals(body)){
                Log.i(TAG,"远程锁屏");
                abortBroadcast();
            }
        }
    }
}
