package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.service.AutoCleanService;
import com.roger.mobilesafe.ui.SettingItemView;
import com.roger.mobilesafe.utils.MyConstants;
import com.roger.mobilesafe.utils.ServiceUtils;

/**
 * Created by Roger on 2014/11/7.
 */
public class TaskSettingActivity extends Activity{
    private static final String TAG = "TaskSettingActivity";
    private SettingItemView siv_showSystem,siv_autoClean;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        config = getSharedPreferences("config",MODE_PRIVATE);
        siv_showSystem = (SettingItemView) findViewById(R.id.siv_showSystem);
        siv_autoClean = (SettingItemView) findViewById(R.id.siv_autoClean);
        siv_autoClean.setText("自动清理进程");
        siv_showSystem.setText("显示系统进程");

        siv_showSystem.setChecked(config.getBoolean(MyConstants.IS_SHOW_SYSTEM_TASK,false));
        siv_showSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !siv_showSystem.isChecked();
                siv_showSystem.setChecked(isChecked);
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean(MyConstants.IS_SHOW_SYSTEM_TASK,isChecked);
                editor.commit();
            }
        });

        siv_autoClean.setChecked(ServiceUtils.isServiceRunning(this,AutoCleanService.class.getName()));
        siv_autoClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !siv_autoClean.isChecked();
                siv_autoClean.setChecked(isChecked);
                Log.i(TAG,"autoClean:"+isChecked);
                Intent intent = new Intent(TaskSettingActivity.this,AutoCleanService.class);
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        siv_autoClean.setChecked(ServiceUtils.isServiceRunning(this,AutoCleanService.class.getName()));
        super.onRestart();
    }
}
