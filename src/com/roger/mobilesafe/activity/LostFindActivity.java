package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/14.
 */
public class LostFindActivity extends BaseActivity{
    private SharedPreferences config;
    private TextView tv_number,tv_enterSetup;
    private ImageView iv_lock;
    private boolean isOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getSharedPreferences("config",MODE_PRIVATE);
        //判断是否做过设置向导
        if(config.getBoolean(MyConstants.CONFIGED,false)) {
            setContentView(R.layout.activity_lost_find);
            tv_number = (TextView) findViewById(R.id.tv_lostFind_number);
            iv_lock = (ImageView) findViewById(R.id.iv_lostFind_lock);
            tv_enterSetup = (TextView) findViewById(R.id.tv_lostFind_enterSetup);
            //设置数据
            isOpen = config.getBoolean(MyConstants.IS_PROTECT,false);
            setImageResource(isOpen);
            tv_number.setText(config.getString(MyConstants.SAFE_NUMBER,""));

            tv_enterSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterSetup();
                }
            });
        }else{
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }

    }

    /**
     * 开启防盗保护
     * @param v
     */
    public void switchOpen(View v){
        isOpen = !isOpen;
        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean(MyConstants.IS_PROTECT,isOpen);
        editor.commit();
        setImageResource(isOpen);
    }

    private void setImageResource(boolean isLock){
        int resId = isLock?R.drawable.lock:R.drawable.unlock;
        iv_lock.setImageResource(resId);
    }
    public void enterSetup(){
        Intent intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
