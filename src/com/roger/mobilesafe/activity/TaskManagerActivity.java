package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.domain.AppInfo;
import com.roger.mobilesafe.domain.TaskInfo;
import com.roger.mobilesafe.engine.AppInfoEngine;
import com.roger.mobilesafe.engine.TaskInfoEngine;
import com.roger.mobilesafe.utils.MyConstants;
import com.roger.mobilesafe.utils.SystemInfoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 进程管理
 */
public class TaskManagerActivity extends BaseActivity{
    private static final String TAG = "TaskManagerActivity";
    private TextView tv_processCount,tv_memory,tv_tasksInfo;
    private LinearLayout ll_loading;
    private ListView lv_tasks;
    private List<TaskInfo> userTasks = new ArrayList<TaskInfo>();
    private List<TaskInfo> sysTasks = new ArrayList<TaskInfo>();
    private TaskInfoAdapter adapter = new TaskInfoAdapter();
    private String availMem,totalMem;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getSharedPreferences("config",MODE_PRIVATE);
        setContentView(R.layout.activity_task_manager);
        tv_processCount = (TextView) findViewById(R.id.tv_processCount);
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_tasks = (ListView) findViewById(R.id.lv_tasks);
        tv_tasksInfo = (TextView) findViewById(R.id.tv_tasksInfo);

        setMemTitle();

        initListView();
    }

    private void setMemTitle(){
        availMem = Formatter.formatFileSize(this, SystemInfoUtil.getAvailMemory(this));
        totalMem = Formatter.formatFileSize(this,SystemInfoUtil.getTotalMemory(this));
        tv_memory.setText("内存使用："+availMem+"/"+totalMem);
    }
    /**
     * 全选
     * @param v
     */
    public void selectAll(View v){
        for(TaskInfo info:userTasks){
            if(getPackageName().equals(info.getPackName()))continue;
            info.setChecked(true);
        }
        for(TaskInfo info:sysTasks){
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     * @param v
     */
    public void selectOp(View v){
        for(TaskInfo info:userTasks){
            if(getPackageName().equals(info.getPackName()))continue;
            info.setChecked(!info.isChecked());
        }
        for(TaskInfo info:sysTasks){
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    private void setProcessCount(){
        tv_processCount.setText("运行中的进程："+(userTasks.size()+sysTasks.size()));
    }
    /**
     * 清理选中内存
     * @param v
     */
    public void clearMem(View v){
        List<TaskInfo> tasks = new ArrayList<TaskInfo>();
        tasks.addAll(userTasks);tasks.addAll(sysTasks);
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int n = 0;
        long mem = 0;
        for(TaskInfo taskInfo:tasks){
            if(taskInfo.isChecked()){
                n++;
                mem+=taskInfo.getMemSize();
                am.killBackgroundProcesses(taskInfo.getPackName());
                userTasks.remove(taskInfo);
                sysTasks.remove(taskInfo);
            }
        }
        adapter.notifyDataSetChanged();
        setMemTitle();setProcessCount();
        Toast.makeText(this,"杀死了"+n+"个进程，清理了"+Formatter.formatFileSize(this,mem)+"内存",Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置
     * @param v
     */
    public void setup(View v){
        Intent intent = new Intent(this,TaskSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化ListView、添加适配器、点击事件
     */
    private void initListView() {
        lv_tasks.setAdapter(adapter);
        lv_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0||position==userTasks.size()+1)return;
                TaskInfo info;
                if(position>userTasks.size()+1){
                    position = position-userTasks.size()-2;
                    info = sysTasks.get(position);
                }else{
                    info = userTasks.get(--position);
                }
                if(getPackageName().equals(info.getPackName()))return;
                info.setChecked(!info.isChecked());
                adapter.notifyDataSetChanged();
            }
        });
        lv_tasks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem>userTasks.size()){
                    tv_tasksInfo.setText("系统程序("+sysTasks.size()+")");
                }else{
                    tv_tasksInfo.setText("用户程序("+userTasks.size()+")");
                }
            }
        });
        fillData();
    }

    private void fillData(){
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                List<TaskInfo> infos = TaskInfoEngine.getTaskInfos(TaskManagerActivity.this);
                partTasks(infos);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        ll_loading.setVisibility(View.INVISIBLE);
                        tv_tasksInfo.setText("用户进程(" + userTasks.size() + ")");
                        setProcessCount();
                    }
                });
            }
        }.start();
    }
    /**
     * 区分用户进程和系统进程
     * @param infos
     */
    private void partTasks(List<TaskInfo> infos) {
        userTasks = new ArrayList<TaskInfo>();
        sysTasks = new ArrayList<TaskInfo>();
        for(TaskInfo info:infos){
            if(info.isSystem()){
                sysTasks.add(info);
            }else{
                userTasks.add(info);
            }
        }
    }

    private class TaskInfoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            int size = config.getBoolean(MyConstants.IS_SHOW_SYSTEM_TASK,false)?sysTasks.size()+1:0;
            return userTasks.size()+size+1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo info;
            if(position==0){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户进程("+userTasks.size()+")");
                return tv;
            }else if(position==userTasks.size()+1){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统进程("+sysTasks.size()+")");
                return tv;
            }else if(position>userTasks.size()+1){
                position = position-userTasks.size()-2;
                info = sysTasks.get(position);
            }else{
                info = userTasks.get(--position);

            }
            View view;
            ViewHolder holder;
            if(convertView!=null&&convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(),R.layout.item_task_info,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_appName);
                holder.tv_memSize = (TextView) view.findViewById(R.id.tv_memSize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.iv_icon.setBackgroundDrawable(info.getIcon());
            holder.tv_name.setText(info.getName());
            holder.tv_memSize.setText("内存占用：" + info.getFormatMemSize(TaskManagerActivity.this));
            holder.cb_status.setChecked(info.isChecked());
            holder.cb_status.setVisibility(getPackageName().equals(info.getPackName())?View.INVISIBLE:View.VISIBLE);
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
        TextView tv_name,tv_memSize;
        ImageView iv_icon;
        CheckBox cb_status;
    }
}
