package com.roger.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.NumberAddressUtils;

/**
 * Created by Roger on 2014/10/23.
 */
public class AddressService extends Service{
    private TelephonyManager tm;
    private MyListenerPhone listenerPhone;
    private OutCallReceiver receiver;
    private WindowManager windowManager;
    private TextView myToastView;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listenerPhone = new MyListenerPhone();
        tm.listen(listenerPhone,PhoneStateListener.LISTEN_CALL_STATE);
        //代码注册广播接受者
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver,filter);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listenerPhone,PhoneStateListener.LISTEN_NONE);
        listenerPhone = null;

        //取消广播接受者
        unregisterReceiver(receiver);
        receiver = null;
    }

    private void showToast(String message,boolean isIn){
        myToastView = new TextView(getApplicationContext());
        myToastView.setText(message);
        myToastView.setTextSize(22);
        myToastView.setTextColor(Color.WHITE);
        myToastView.setBackgroundResource(R.drawable.call_locate_blue);
        int resId = isIn?android.R.drawable.sym_call_incoming:android.R.drawable.sym_call_outgoing;
        Drawable drableLeft = getResources().getDrawable(resId);
        myToastView.setCompoundDrawablesWithIntrinsicBounds(drableLeft,null,null,null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        windowManager.addView(myToastView,params);
    }

    private void removeToast(){
        if(myToastView==null)return;
        windowManager.removeView(myToastView);
    }
    /**
     * 去电
     */
    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = NumberAddressUtils.queryNumber(number);
            showToast(address,false);
        }
    }

    private class MyListenerPhone extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressUtils.queryNumber(incomingNumber);
                    showToast(address,true);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    removeToast();
                    break;
                default:
                    break;
            }
        }
    }
}
