package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.ApplockDao;
import com.roger.mobilesafe.domain.AppInfo;
import com.roger.mobilesafe.engine.AppInfoEngine;
import com.roger.mobilesafe.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 2014/11/4.
 */
public class AppManagerActivity extends BaseActivity{
    private static final String TAG = "AppManagerActivity";
    private TextView tv_rom_avail,tv_sd_avail,tv_apps;
    private LinearLayout ll_loading;
    private ListView lv_apps;
    private List<AppInfo> userApps = new ArrayList<AppInfo>();//用户应用
    private List<AppInfo> systemApps= new ArrayList<AppInfo>();//系统应用
    private BaseAdapter adapter;
    private PopupWindow popupWindow;
    private AppInfo appInfo;//当前选中App信息
    private ApplockDao applockDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        applockDao = new ApplockDao(this);
        tv_rom_avail = (TextView) findViewById(R.id.tv_rom_avail);
        tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
        tv_apps = (TextView) findViewById(R.id.tv_apps);
        lv_apps = (ListView) findViewById(R.id.lv_apps);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String romPath = Environment.getDataDirectory().getPath();
        String availRom = Formatter.formatFileSize(this,getAvaibleSpace(romPath))+"/"+Formatter.formatFileSize(this,getTotalSpace(romPath));
        String avaiblSd = Formatter.formatFileSize(this,getAvaibleSpace(sdPath))+"/"+Formatter.formatFileSize(this,getTotalSpace(sdPath));
        tv_rom_avail.setText("内存可用："+availRom);
        tv_sd_avail.setText("SD卡可用："+avaiblSd);
        adapter = new AppsAdapter();

        fillData();


        lv_apps.setAdapter(adapter);
        lv_apps.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopUpWindow();
                if(firstVisibleItem>userApps.size()){
                    tv_apps.setText("系统程序("+systemApps.size()+")");
                }else{
                    tv_apps.setText("用户程序("+userApps.size()+")");
                }
            }
        });

        lv_apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0||position==userApps.size()+1)return;
                dismissPopUpWindow();

                if(position>userApps.size()+1){
                    position = position-userApps.size()-2;
                    appInfo = systemApps.get(position);
                }else{
                    appInfo = userApps.get(--position);
                }

                int [] location = new int[2];
                view.getLocationInWindow(location);
                View contentView = View.inflate(getApplicationContext(),R.layout.item_popup_app,null);
                initPopupViewEvents(contentView);
                popupWindow = new PopupWindow(contentView,-2,-2);
                /**
                 * 想要显示动画效果，窗体必须有背景
                 */
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int offsetX = DensityUtil.dip2px(getApplicationContext(),30);
                popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP,location[0]+offsetX,location[1]);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f,1.0f,0.3f,1.0f,
                        Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
                scaleAnimation.setDuration(300);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f,1.0f);
                alphaAnimation.setDuration(300);
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(alphaAnimation);
                contentView.startAnimation(animationSet);
            }
        });

        lv_apps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0||position==userApps.size()+1)return true;
                if(position>userApps.size()+1){
                    position = position-userApps.size()-2;
                    appInfo = systemApps.get(position);
                }else{
                    appInfo = userApps.get(--position);
                }
                String packName = appInfo.getPackName();
                if(applockDao.find(packName)){
                    applockDao.delete(packName);
                }else{
                    applockDao.add(packName);
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    /**
     * 卸载、启动、分享点击事件
     * @param contentView
     */
    private void initPopupViewEvents(View contentView) {
        TextView tv_uninstall = (TextView) contentView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) contentView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) contentView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appInfo==null)return;
                uninstallApplication();
            }
        });
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appInfo==null)return;
                startApplication();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appInfo==null)return;
                shareApplication();
            }
        });
    }

    /**
     * 分享应用
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"推荐您使用一款软件："+ appInfo.getName());
        startActivity(intent);
    }

    /**
     * 卸载应用
     */
    private void uninstallApplication() {
        /*
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
         */
        if(appInfo.isSystem()){
            Toast.makeText(this,"系统应用不能卸载",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackName()));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 刷新界面
         */
        Log.i(TAG,"fillData....");
        fillData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                List<AppInfo> infos = AppInfoEngine.getApps(AppManagerActivity.this);
                partApps(infos);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG,"user:"+userApps.size()+",system:"+systemApps.size());
                        adapter.notifyDataSetChanged();
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_apps.setText("用户程序("+userApps.size()+")");
                    }
                });
            }
        }.start();
    }

    /**
     * 启动当前选中应用
     */
    private void startApplication() {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appInfo.getPackName());
        if(intent!=null){
            startActivity(intent);
        }else {
            Toast.makeText(this,"该应用不能启动",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 关闭弹出窗口
     */
    private void dismissPopUpWindow() {
        if(popupWindow!=null){
            if(popupWindow.isShowing())popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopUpWindow();
        super.onDestroy();
    }

    private class AppsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 2+userApps.size()+systemApps.size();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo info;
            if(position==0){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序("+userApps.size()+")");
                return tv;
            }else if(position==userApps.size()+1){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序("+systemApps.size()+")");
                return tv;
            }else if(position>userApps.size()+1){
                position = position-userApps.size()-2;
                info = systemApps.get(position);
            }else{
                info = userApps.get(--position);

            }
            View view;
            ViewHolder holder;
            if(convertView!=null&&convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(),R.layout.item_app_info,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_appName);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_appLocation);
                holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);
                view.setTag(holder);
            }
            holder.iv_icon.setBackgroundDrawable(info.getIcon());
            holder.tv_name.setText(info.getName());
            holder.tv_location.setText(info.isRom()?"手机内存":"外部存储");
            int resId = applockDao.find(info.getPackName())?R.drawable.lock:R.drawable.unlock;
            holder.iv_status.setImageResource(resId);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    static class ViewHolder{
        TextView tv_name,tv_location;
        ImageView iv_icon,iv_status;
    }
    /**
     * 将应用分成用户和系统应用
     * @param infos
     */
    private void partApps(List<AppInfo> infos) {
        systemApps = new ArrayList<AppInfo>();
        userApps = new ArrayList<AppInfo>();
        for(AppInfo info:infos){
            if(info.isSystem()){
                systemApps.add(info);
            }else{
                userApps.add(info);
            }
        }
    }

    /**
     * 获取可用容量
     * @param path
     * @return
     */
    private long getAvaibleSpace(String path){
        StatFs statFs = new StatFs(path);
        long result = ((long)statFs.getAvailableBlocks())*((long)statFs.getBlockSize());
        return result;
    }

    /**
     * 获取总容量
     * @param path
     * @return
     */
    private long getTotalSpace(String path){
        StatFs statFs = new StatFs(path);
        long result =  ((long)statFs.getBlockCount())*((long)statFs.getBlockSize());
        return result;
    }
}
