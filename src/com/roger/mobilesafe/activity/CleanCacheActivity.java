package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.domain.ScanInfo;
import com.roger.mobilesafe.ui.HoloCircularProgressBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Roger on 2014/11/12.
 * 清理缓存
 */
public class CleanCacheActivity extends Activity{
    private static final int SCANNING = 0;//正在扫描
    private static final int FINISH = 1;//扫描完成
    private static final int ERROR = 2;
    private static final String TAG = "CleanCacheActivity";
    private TextView tv_scanning,
                     tv_progress;
    private HoloCircularProgressBar hcp_progress;
    private LinearLayout ll_container;

    private PackageManager pm;
    private Method getPackageSizeInfoMethod,//获取包相关大小信息的方法
                   freeStorageAndNotifyMethod;//清理缓存
    private float size;
    private int n = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        pm = getPackageManager();
        tv_scanning = (TextView) findViewById(R.id.tv_scanning);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        hcp_progress = (HoloCircularProgressBar) findViewById(R.id.hcp_progress);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        getMethods();
        tv_progress.setClickable(false);
        tv_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_progress.setClickable(false);
                scanCache();
            }
        });
        scanCache();
    }

    /**
     * 通过反射获取方法
     */
    private void getMethods() {
        try {
            getPackageSizeInfoMethod = PackageManager.class
                    .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            for(Method method:PackageManager.class.getMethods()){
                if("freeStorageAndNotify".equals(method.getName())){
                    freeStorageAndNotifyMethod = method;
                    break;
                }
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANNING:
                    ScanInfo info = (ScanInfo) msg.obj;
                    float progress = n/size;
                    if(info.getCacheSize()>0) {
                        ll_container.addView(getCacheView(info), 0);
                    }
                    hcp_progress.setProgress(progress);
                    tv_scanning.setText("正在扫描："+info.getName());
                    tv_progress.setText((int)(progress*100)+"%");
                    break;
                case FINISH:
                    tv_scanning.setText("扫描完成");
                    tv_progress.setClickable(true);
                    tv_progress.setText("开始");
                    break;
                case ERROR:
                    tv_scanning.setText("发生错误");
                    tv_progress.setClickable(true);
                    tv_progress.setText("开始");
                    break;
            }
        }
    };

    /**
     * 获取扫描缓存应用界面
     * @param scanInfo
     * @return
     */
    private View getCacheView(ScanInfo scanInfo) {
        View view = View.inflate(this,R.layout.item_app_cache,null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_appName = (TextView) view.findViewById(R.id.tv_appName);
        TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache);
        ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

        iv_icon.setImageDrawable(scanInfo.getIcon());
        tv_appName.setText(scanInfo.getName());
        tv_cache.setText("缓存大小："+ Formatter.formatFileSize(this,scanInfo.getCacheSize()));
        return view;
    }

    /**
     * 扫描缓存
     */
    private void scanCache() {
        ll_container.removeAllViews();
        new Thread(){
            @Override
            public void run() {
                try {
                    List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                    size = packageInfos.size();
                    n=0;
                    for (PackageInfo info : packageInfos) {
                        getPackageSizeInfoMethod.invoke(pm,
                                info.applicationInfo.packageName,
                                new MyPackageStatsObserver(info));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    private class MyPackageStatsObserver extends IPackageStatsObserver.Stub{
        private PackageInfo packageInfo;

        private MyPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            Message message = Message.obtain();
            message.what = SCANNING;
            ScanInfo scanInfo = new ScanInfo();
            scanInfo.setCacheSize(pStats.cacheSize + pStats.externalCacheSize);
            scanInfo.setCodeSize(pStats.codeSize);
            scanInfo.setDataSize(pStats.dataSize);
            scanInfo.setPackName(pStats.packageName);
            scanInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
            scanInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
            message.obj = scanInfo;
            n++;
            handler.sendMessage(message);

            if(n==size){
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }
    }
    /**
     * 一键清理
     * @param v
     */
    public void clearAll(View v){
        try {
            freeStorageAndNotifyMethod.invoke(pm,Integer.MAX_VALUE,new MyPackageDataObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
