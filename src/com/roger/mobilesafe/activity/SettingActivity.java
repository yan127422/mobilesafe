package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.ui.SettingItemView;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/9.
 */
public class SettingActivity extends Activity{
    private SettingItemView autoUpdate;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        config = getSharedPreferences("config",MODE_PRIVATE);
        boolean isAuto = config.getBoolean(MyConstants.IS_AUTO_UPDATE,false);
        autoUpdate = (SettingItemView) findViewById(R.id.siv_setting_update);
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
    }
}
