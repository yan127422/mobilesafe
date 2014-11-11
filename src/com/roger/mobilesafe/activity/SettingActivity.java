package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.service.AddressService;
import com.roger.mobilesafe.service.CallSmsSafeService;
import com.roger.mobilesafe.service.WatchDogService;
import com.roger.mobilesafe.ui.SettingItemView;
import com.roger.mobilesafe.utils.MyConstants;
import com.roger.mobilesafe.utils.ServiceUtils;

/**
 * Created by Roger on 2014/10/9.
 */
public class SettingActivity extends Activity{
    private static final String TAG = "SettingActivity";
    private SettingItemView autoUpdate,addressQuery,callSms,siv_setting_watchDog;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        config = getSharedPreferences("config",MODE_PRIVATE);
        boolean isAuto = config.getBoolean(MyConstants.IS_AUTO_UPDATE,false);
        autoUpdate = (SettingItemView) findViewById(R.id.siv_setting_update);
        addressQuery = (SettingItemView) findViewById(R.id.siv_setting_address);
        callSms = (SettingItemView) findViewById(R.id.siv_setting_callSms);
        siv_setting_watchDog = (SettingItemView) findViewById(R.id.siv_setting_watchDog);
        addressQuery.setText("设置显示号码归属地");
        callSms.setText("开启黑名单拦截");
        siv_setting_watchDog.setText("开启程序锁服务");
        autoUpdate.setChecked(isAuto);
        autoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !autoUpdate.isChecked();
                autoUpdate.setChecked(isChecked);
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean(MyConstants.IS_AUTO_UPDATE,isChecked);
                editor.commit();
            }
        });
        boolean isShowAddress = ServiceUtils.isServiceRunning(this,AddressService.class.getName());
        addressQuery.setChecked(isShowAddress);
        final Intent addressService = new Intent(SettingActivity.this, AddressService.class);
        addressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !addressQuery.isChecked();
                addressQuery.setChecked(isChecked);

                if(isChecked){
                    startService(addressService);
                }else{
                    stopService(addressService);
                }
            }
        });

        boolean isCallSmsRunning = ServiceUtils.isServiceRunning(this,CallSmsSafeService.class.getName());
        callSms.setChecked(isCallSmsRunning);
        final Intent callSmsService = new Intent(this,CallSmsSafeService.class);
        callSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !callSms.isChecked();
                callSms.setChecked(isChecked);
                if(isChecked){
                    startService(callSmsService);
                }else{
                    stopService(callSmsService);
                }
            }
        });

        boolean isWatchDogRunning = ServiceUtils.isServiceRunning(this, WatchDogService.class.getName());
        siv_setting_watchDog.setChecked(isWatchDogRunning);
        final Intent watchDogService = new Intent(this,WatchDogService.class);
        siv_setting_watchDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !siv_setting_watchDog.isChecked();
                siv_setting_watchDog.setChecked(isChecked);
                if(isChecked){
                    startService(watchDogService);
                }else{
                    stopService(watchDogService);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isShowAddress = ServiceUtils.isServiceRunning(this,AddressService.class.getName());
        addressQuery.setChecked(isShowAddress);
    }
}
