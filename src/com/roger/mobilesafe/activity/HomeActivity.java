package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.receiver.MyAdminReceiver;
import com.roger.mobilesafe.utils.MD5;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/8.
 */
public class HomeActivity extends Activity{
    private static final String TAG = "HomeActivity";
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
    private SharedPreferences config;
    private DevicePolicyManager dpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        config = getSharedPreferences("config",MODE_PRIVATE);
         dpm = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        gridView = (GridView) findViewById(R.id.gv_home_gridview);
        gridView.setAdapter(new MyAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position){
                    case 8://设置中心
                        intent = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case 0://手机防盗
                        showLostFindDialog();
                        break;
                    case 1://通信卫士（黑名单）
                        intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2://软件管家
                        intent = new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3://进程管理
                        intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4://流量统计
                        intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 5://杀毒
                        intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 6://缓存清理
                        intent = new Intent(HomeActivity.this,CleanCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 7://高级工具
                        intent = new Intent(HomeActivity.this,AtoolsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        if(!isAdmin()){//开启管理员权限
            openAdmin();
        }
    }

    private boolean isAdmin(){
        ComponentName mDeviceAdmin = new ComponentName(this, MyAdminReceiver.class);
        return dpm.isAdminActive(mDeviceAdmin);
    }
    /**
     * 开启设备管理器
     */
    private void openAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName mDeviceAdmin = new ComponentName(this, MyAdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"开启管理员权限");
        startActivity(intent);
    }
    /**
     * 手机防盗对话框
     */
    private void showLostFindDialog() {
        if(isSetupPwd()){
            showEnterDialog();
        }else{
            showSetupPwdDialog();
        }
    }

    private AlertDialog dialog;
    /**
     * 设置手机防盗密码对话框
     */
    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.dialog_setup_password,null);
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        final EditText et_confirm = (EditText) view.findViewById(R.id.et_confirm);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取出密码
                String password = et_password.getText().toString().trim();
                String confirm = et_confirm.getText().toString().trim();
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirm)){
                    Toast.makeText(getApplicationContext(),"密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences.Editor editor = config.edit();
                editor.putString(MyConstants.SETUP_PWD, MD5.encode(password));
                editor.commit();
                dialog.dismiss();
                //进入手机防盗界面
                enterLostFindActivity();
            }
        });
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    private void enterLostFindActivity() {
        Intent intent = new Intent(this,LostFindActivity.class);
        startActivity(intent);
    }

    /**
     * 进入手机防盗对话框
     */
    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.dialog_enter_password,null);
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取出密码
                String password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MD5.encode(password).equals(config.getString(MyConstants.SETUP_PWD, ""))) {
                    //进入手机防盗界面
                    Log.i(TAG, "进入手机防盗界面");
                    dialog.dismiss();
                    enterLostFindActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    /**
     * 是否设置了手机防盗的密码
     * @return
     */
    private boolean isSetupPwd() {
        return !TextUtils.isEmpty(config.getString(MyConstants.SETUP_PWD,null));
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
