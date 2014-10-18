package com.roger.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.ui.SettingItemView;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/15.
 */
public class Setup2Activity extends BaseSettingActivity{
    private SettingItemView settingItemView;
    private TelephonyManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        settingItemView = (SettingItemView) findViewById(R.id.siv_setup2_bind);
        settingItemView.setText("绑定Sim卡");
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        settingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !settingItemView.isChecked();
                settingItemView.setChecked(isChecked);
                //保存Sim卡序列号
                String sim = isChecked?manager.getSimSerialNumber():null;
                SharedPreferences.Editor editor = config.edit();
                editor.putString(MyConstants.SIM_SERIAL_NUMBER,sim);
                editor.commit();
            }
        });
        String sim = config.getString(MyConstants.SIM_SERIAL_NUMBER,null);
        settingItemView.setChecked(sim!=null);
    }

    @Override
    public void showNext() {
        //是否绑定Sim卡
        String sim = config.getString(MyConstants.SIM_SERIAL_NUMBER,null);
        if(sim==null){
            Toast.makeText(this,"SIM卡未绑定",Toast.LENGTH_LONG).show();
            return;
        }
        toNextActivity(Setup3Activity.class);
    }

    @Override
    public void showPre() {
        toPreActivity(Setup1Activity.class);
    }
}
