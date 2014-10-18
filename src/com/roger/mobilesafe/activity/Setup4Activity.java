package com.roger.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.ui.SettingItemView;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/15.
 */
public class Setup4Activity extends BaseSettingActivity{
    private SettingItemView settingItemView;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getSharedPreferences("config",MODE_PRIVATE);
        setContentView(R.layout.activity_setup4);
        settingItemView = (SettingItemView) findViewById(R.id.siv_setup4_open);
        settingItemView.setText("开启防盗保护");
        settingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !settingItemView.isChecked();
                settingItemView.setChecked(isChecked);
            }
        });
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean(MyConstants.CONFIGED,true);
        editor.putBoolean(MyConstants.IS_PROTECT,settingItemView.isChecked());
        editor.commit();
        toNextActivity(LostFindActivity.class);
    }

    @Override
    public void showPre() {
        toPreActivity(Setup3Activity.class);
    }
}
