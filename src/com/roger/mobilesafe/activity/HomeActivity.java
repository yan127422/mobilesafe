package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        config = getSharedPreferences("config",MODE_PRIVATE);
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
                    case 0://手机防盗
                        showLostFindDialog();
                        break;
                }
            }
        });
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
                editor.putString(MyConstants.SETUP_PWD,password);
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
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.equals(config.getString(MyConstants.SETUP_PWD,""))){
                    //进入手机防盗界面
                    Log.i(TAG,"进入手机防盗界面");
                    dialog.dismiss();
                    enterLostFindActivity();
                }else{
                    Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
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
