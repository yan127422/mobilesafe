package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/11/11.
 * 程序锁：输入密码界面
 */
public class EnterPwdActivity extends Activity{
    private EditText et_password;
    private TextView tv_packName;
    private ImageView iv_icon;
    private Intent sender;
    private PackageManager pm;
    private String packName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        pm = getPackageManager();
        et_password = (EditText) findViewById(R.id.et_password);
        tv_packName = (TextView) findViewById(R.id.tv_packName);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        sender = getIntent();
        packName = sender.getStringExtra(MyConstants.PACK_NAME);
        try {
            ApplicationInfo info = pm.getApplicationInfo(packName, 0);
            tv_packName.setText(info.loadLabel(pm));
            iv_icon.setImageDrawable(info.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //返回桌面
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        //当前activity最小化
    }

    /**
     * 确定
     * @param view
     */
    public void confirm(View view){
        String password = et_password.getText().toString();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("123".equals(password)){
            //发消息暂时停止程序锁保护
            //通过广播发送消息
            Intent intent = new Intent();
            intent.setAction(MyConstants.BROADCAST_TEMP_STOP);
            intent.putExtra(MyConstants.PACK_NAME,packName);
            sendBroadcast(intent);
            finish();
        }else{
            Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
