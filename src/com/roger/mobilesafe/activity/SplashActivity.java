package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;
import com.roger.mobilesafe.utils.StreamTools;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 引导界面
 */
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int ENTER_HOME = 0;
    private static final int SHOW_UPDATE_DIALOG = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private TextView tv_version,tv_progress;
    private View rl_root;
    private String version;
    private String description;
    private String apkurl;
    private SharedPreferences config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_progress = (TextView) findViewById(R.id.tv_splash_progress);
        tv_version.setText("版本"+getVersionName());
        rl_root = findViewById(R.id.rl_root);
        config = getSharedPreferences("config",MODE_PRIVATE);
        boolean isAutoUpdate = config.getBoolean(MyConstants.IS_AUTO_UPDATE,false);
        if(isAutoUpdate) {
            checkUpdate();
        }else{
            Message message = new Message();
            message.what = ENTER_HOME;
            handler.sendMessageDelayed(message,2000);
        }
        AlphaAnimation animation = new AlphaAnimation(0.2f,1.0f);
        animation.setDuration(1000);
        rl_root.setAnimation(animation);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_UPDATE_DIALOG:
                    Log.i(TAG,"显示升级对话框");
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    Log.i(TAG,"进入主页面");
                    enterHome();
                    break;
                case URL_ERROR:
                    Toast.makeText(getApplicationContext(), "URL错误",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case JSON_ERROR:
                    Toast.makeText(getApplicationContext(),"JSON解析错误",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
            }
        }
    };

    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        builder.setMessage(description);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("立即升级",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Log.i(TAG,"SD卡存在");
                    //afinal下载
                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download(apkurl,Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe2.0.apk",new AjaxCallBack<File>() {
                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            installApk(file);
                        }

                        /**
                         * 安装apk
                         * @param file
                         */
                        private void installApk(File file) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            int progress = (int) (current*100/count);
                            tv_progress.setText("下载进度："+progress+"%");
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"没有SD卡",Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"SD卡不存在");
                }
            }
        });
        builder.setNegativeButton("下次再说",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 检查版本更新
     */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.uptimeMillis();
                String updateUrl = getResources().getString(R.string.updateUrl);
                Message message = Message.obtain();
                try {
                    URL url = new URL(updateUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();

                    if(code==200){
                        //联网成功
                        InputStream is = conn.getInputStream();
                        String updateInfo = StreamTools.readFromStream(is);
                        Log.i(TAG, "updateInfo:"+updateInfo);
                        JSONObject json = new JSONObject(updateInfo);
                        version = json.getString("version");
                        description = json.getString("description");
                        apkurl = json.getString("apkurl");

                        if(getVersionName().equals(version)){
                            Log.i(TAG,"版本一致，进入主页面");
                            message.what = ENTER_HOME;
                        }else{
                            Log.i(TAG,"弹出升级对话框");
                            message.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    message.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_ERROR;
                    e.printStackTrace();
                }finally {
                    //总共延迟两秒进入主界面
                    long endTime = SystemClock.uptimeMillis();
                    long dTime = endTime-startTime;
                    if(dTime<2000){
                        handler.sendMessageDelayed(message,2000-dTime);
                    }else{
                        handler.sendMessage(message);
                    }

                }
            }
        }).start();
    }


    private String getVersionName(){
        String version = "";
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
