package com.roger.mobilesafe.activity;

import android.os.Bundle;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/14.
 */
public class Setup1Activity extends BaseSettingActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showNext() {
       toNextActivity(Setup2Activity.class);
    }

    @Override
    public void showPre() {

    }
}
