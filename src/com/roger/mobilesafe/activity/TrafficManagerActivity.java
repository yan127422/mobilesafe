package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.TrafficDao;
import com.roger.mobilesafe.domain.AppInfo;
import com.roger.mobilesafe.domain.AppTrafficInfo;
import com.roger.mobilesafe.engine.AppInfoEngine;
import com.roger.mobilesafe.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Roger on 2014/11/11.
 * 流量管理
 */
public class TrafficManagerActivity extends BaseActivity{
    private static final String TAG = "AppManagerActivity";
    private TextView tv_totalTraffic,tv_apps;
    private LinearLayout ll_loading;
    private ListView lv_apps;

    private List<AppInfo> userApps = new ArrayList<AppInfo>();//用户应用
    private List<AppInfo> systemApps= new ArrayList<AppInfo>();//系统应用
    private BaseAdapter adapter;
    private boolean isWIFI;//是否是wifi
    private int flag=1;//
    private AppInfo appInfo;//当前选中App信息
    private TrafficDao trafficDao;
    private long totalTraffic;//总流量
    private float maxTraffic=0.0f;//最大流量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        trafficDao = new TrafficDao(this);
        tv_apps = (TextView) findViewById(R.id.tv_apps);
        tv_totalTraffic = (TextView) findViewById(R.id.tv_totalTraffic);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_apps = (ListView) findViewById(R.id.lv_apps);
        adapter = new AppTrafficAdapter();
        lv_apps.setAdapter(adapter);
        fillData();
    }
    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                List<AppInfo> infos = AppInfoEngine.getApps(TrafficManagerActivity.this);
                partApps(infos);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "user:" + userApps.size() + ",system:" + systemApps.size());
                        adapter.notifyDataSetChanged();
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_apps.setText("用户程序("+userApps.size()+")");
                        tv_totalTraffic.setText(Formatter.formatFileSize(getApplicationContext(),totalTraffic));
                    }
                });
            }
        }.start();
    }

    /**
     * 将应用分成用户和系统应用
     * @param infos
     */
    private void partApps(List<AppInfo> infos) {
        systemApps = new ArrayList<AppInfo>();
        userApps = new ArrayList<AppInfo>();
        long total = 0;
        for(AppInfo info:infos){
            String column = isWIFI?"wifi":"gprs";
            long traffic = trafficDao.getAppTraffic(info.getPackName(),column,flag);
            info.setTraffic(traffic);
            total+=traffic;
            if(traffic>maxTraffic)maxTraffic = traffic;
            if(traffic==0)continue;
            if(info.isSystem()){
                systemApps.add(info);
            }else{
                userApps.add(info);
            }
        }
        totalTraffic = total;
        Collections.sort(userApps,new AppInfo.TrafficComparator());
        Collections.sort(systemApps,new AppInfo.TrafficComparator());
    }
    private class AppTrafficAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return userApps.size()+systemApps.size()+2;
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
                view = View.inflate(getApplicationContext(),R.layout.item_traffic_info,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_appName);
                holder.tv_traffic = (TextView) view.findViewById(R.id.tv_traffic);
                holder.v_traffic = view.findViewById(R.id.v_traffic);
                view.setTag(holder);
            }
            holder.iv_icon.setBackgroundDrawable(info.getIcon());
            holder.tv_name.setText(info.getName());
            long traffic = info.getTraffic();
            holder.tv_traffic.setText(Formatter.formatFileSize(getApplicationContext(), traffic));
            ViewGroup.LayoutParams layoutParams = holder.v_traffic.getLayoutParams();
            layoutParams.width = (int) (DensityUtil.dip2px(getApplicationContext(),245)*(traffic/maxTraffic));
            holder.v_traffic.setLayoutParams(layoutParams);
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
        TextView tv_name,tv_traffic;
        ImageView iv_icon;
        View v_traffic;
    }
}
