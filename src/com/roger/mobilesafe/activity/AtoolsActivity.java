package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.SmsUtils;

/**
 * Created by Roger on 2014/10/23.
 */
public class AtoolsActivity extends BaseActivity{
    private static final int SUCCESS = 0;
    private static final int FAILURE = 1;
    private static final int START = 2;
    private static final int PROGRESS = 3;
    private TextView tv_query;
    private ProgressDialog progressDialog;
    private int max,progress;
    private boolean isProgress;
    private String dialogTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        tv_query = (TextView) findViewById(R.id.tv_atools_query);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入号码归属地查询页面
                Intent intent = new Intent(AtoolsActivity.this,NumberAddressQueryActivity.class);
                startActivity(intent);
            }
        });
    }

    private Handler backHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(getApplicationContext(),"备份短信成功",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"备份短信失败",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case START:
                    progressDialog.setMax(max);
                    progressDialog.show();
                    break;
                case PROGRESS:
                    progressDialog.setProgress(progress);
                    break;
            }
        }
    };

    private Handler restoreHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Toast.makeText(getApplicationContext(),"备份还原成功",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"备份还原失败",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case START:
                    progressDialog.setMax(max);
                    progressDialog.setTitle(dialogTitle);
                    progressDialog.show();
                    break;
                case PROGRESS:
                    progressDialog.setProgress(progress);
                    break;
            }
        }
    };
    /**
     * 短信备份
     * @param view
     */
    public void backupSms(View view){
        if(isProgress){
           progressDialog.show();
           return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.backUpSms(AtoolsActivity.this,new SmsUtils.SmsCallBack() {
                        @Override
                        public void onProgress(int p) {
                            progress = p;
                            Message message = new Message();
                            message.what = PROGRESS;
                            backHandler.sendMessage(message);
                            dialogTitle = "正在备份短信";
                            isProgress = true;
                        }

                        @Override
                        public void beforeProgress(int m) {
                            max = m;
                            Message message = new Message();
                            message.what = START;
                            backHandler.sendMessage(message);
                            dialogTitle = "正在备份短信";
                        }
                    });
                    Message message = new Message();
                    message.what = SUCCESS;
                    backHandler.sendMessage(message);
                    isProgress = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = FAILURE;
                    backHandler.sendMessage(message);
                    isProgress = false;
                }
            }
        }).start();
    }

    /**
     * 短信还原
     * @param v
     */
    public void restoreSms(View v){
        if(isProgress){
            progressDialog.show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.restoreSms(AtoolsActivity.this,true,new SmsUtils.SmsCallBack() {
                        @Override
                        public void onProgress(int p) {
                            progress = p;
                            Message message = new Message();
                            message.what = PROGRESS;
                            restoreHandler.sendMessage(message);
                            dialogTitle = "正在还原短信";
                            isProgress = true;
                        }

                        @Override
                        public void beforeProgress(int m) {
                            max = m;
                            Message message = new Message();
                            message.what = START;
                            restoreHandler.sendMessage(message);
                            dialogTitle = "正在还原短信";
                        }
                    });
                    Message message = new Message();
                    message.what = SUCCESS;
                    isProgress = false;
                    restoreHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = FAILURE;
                    isProgress = false;
                    restoreHandler.sendMessage(message);
                }
            }
        }).start();
    }
}
