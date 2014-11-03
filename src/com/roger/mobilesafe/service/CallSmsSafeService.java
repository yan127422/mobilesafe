package com.roger.mobilesafe.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import com.roger.mobilesafe.db.dao.BlacklistDao;
import com.roger.mobilesafe.db.dao.NumberAddressUtils;
import com.roger.mobilesafe.domain.BlacklistInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Roger on 2014/10/30.
 */
public class CallSmsSafeService extends Service{
    private static final String TAG = "CallSmsSafeService";
    private BlacklistDao dao;
    private TelephonyManager tm;
    private MyListenerPhone listenerPhone;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object messages[] = (Object[]) intent.getExtras().get("pdus");
            for(Object o:messages) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) o);
                String sender = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                BlacklistInfo info = dao.findByNumber(sender);
                if(info.isSmsSafe()){
                    Log.i(TAG,"拦截短信：sender:"+sender);
                    abortBroadcast();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlacklistDao(this);
        registerReceiver(smsReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listenerPhone = new MyListenerPhone();
        tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
    }
    private class MyListenerPhone extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    BlacklistInfo info = dao.findByNumber(incomingNumber);
                    if(info.isCallSafe()){
                        endCall();
                    }
                    break;
            }
        }
    }

    /**
     * 中断电话
     */
    private void endCall() {
//        ServiceManager.getService(TELEPHONY_SERVICE);
        try {
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
            Log.i(TAG,"endCall:"+iTelephony);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
        smsReceiver = null;
        super.onDestroy();
    }
}
