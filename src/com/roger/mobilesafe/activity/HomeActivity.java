package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/8.
 */
public class HomeActivity extends Activity{
    private GridView gridView;
    private static final String titles[] =
            {"手机防盗","通讯卫士","软件管理","进程管理",
             "浏览统计","手机杀毒","缓存清理","高级工具","设置中心"};
    private static final int rids[] =
                         {R.drawable.toolbox_icon_location,
                          R.drawable.system_master_anti_harass,
                          R.drawable.dx_app_mgr_main_category,
                          R.drawable.toolbox_icon_memorymgr,
                          R.drawable.toolbox_icon_netflow,
                          R.drawable.system_master_phone_acc,
                          R.drawable.dx_app_mgr_main_uninstall,
                          R.drawable.ic_module_toolbox,
                          R.drawable.toolbox_icon_shortcut
                         };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gridView = (GridView) findViewById(R.id.gv_home_gridview);
        gridView.setAdapter(new MyAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 8://设置中心
                        Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
    private class MyAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,R.layout.home_item,null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(rids[position]);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(titles[position]);
            return view;
        }
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
