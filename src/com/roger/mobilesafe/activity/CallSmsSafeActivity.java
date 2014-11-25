package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.BlacklistDao;
import com.roger.mobilesafe.domain.BlacklistInfo;

import java.util.List;

/**
 * Created by Roger on 2014/10/28.
 */
public class CallSmsSafeActivity extends BaseActivity{
    private static final String TAG = "CallSmsSafeActivity";
    private ListView lv_blacklist;
    private List<BlacklistInfo> infos;
    private BlacklistDao dao;
    private BaseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        lv_blacklist = (ListView) findViewById(R.id.lv_callSmsSafe_blacklist);

        dao = new BlacklistDao(this);
        infos = dao.findAll();
        adapter = new BlacklistAdapter();
        lv_blacklist.setAdapter(adapter);
        lv_blacklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"item click "+position);
                showEditBlackView(position);
            }
        });
    }

    /**
     * 显示编辑黑名单对话框
     */
    private void showEditBlackView(int position) {
        BlacklistInfo info = infos.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_edit_blacklist,null);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_callSmsSafe_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_callSmsSafe_sms);
        final TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final String phone = info.getNumber();
        String oldMode = info.getMode();
        tv_phone.setText(phone);

        cb_phone.setChecked("3".equals(oldMode)||"1".equals(oldMode));
        cb_sms.setChecked("3".equals(oldMode)||"2".equals(oldMode));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean phoneChecked = cb_phone.isChecked();
                boolean smsChecked = cb_sms.isChecked();
                if(!(phoneChecked||smsChecked)){
                    Toast.makeText(CallSmsSafeActivity.this,"请至少选择一项拦截选项",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(CallSmsSafeActivity.this,"号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dao.isExist(phone)){
                    Toast.makeText(CallSmsSafeActivity.this,"号码已经存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = (phoneChecked?1:0)+(smsChecked?2:0)+"";
                dao.update(phone,mode);
                infos = dao.findAll();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    private class BlacklistAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //1、减少内存中对象创建次数
            View view;
            final ViewHolder holder;
            if(convertView==null){
                view = View.inflate(CallSmsSafeActivity.this,R.layout.item_blacklist,null);
                TextView tv_number = (TextView) view.findViewById(R.id.tv_item_number);
                TextView tv_mode = (TextView) view.findViewById(R.id.tv_item_mode);
                ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_item_delete);
                holder = new ViewHolder();
                holder.number = tv_number;
                holder.mode = tv_mode;
                holder.iv_delete = iv_delete;
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //2、减少查询子孩子次数
            final BlacklistInfo info = infos.get(position);
            holder.number.setText(info.getNumber());
            holder.mode.setText(info.getStringMode());
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定要删除这条记录么？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库的内容
                            dao.delete(infos.get(position).getNumber());
                            //更新界面。
                            infos.remove(position);
                            //通知listview数据适配器更新
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });
            return view;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     * view对象容器
     * 记录孩子的内存地址
     */
    static class ViewHolder{
        TextView number,mode;
        ImageView iv_delete;
    }

    /**
     * 添加黑名单
     * @param v
     */
    public void addBlacklist(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_add_blacklist,null);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_callSmsSafe_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_callSmsSafe_sms);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean phoneChecked = cb_phone.isChecked();
                boolean smsChecked = cb_sms.isChecked();
                String phone = et_phone.getText().toString();
                if(!(phoneChecked||smsChecked)){
                    Toast.makeText(CallSmsSafeActivity.this,"请至少选择一项拦截选项",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(CallSmsSafeActivity.this,"号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dao.isExist(phone)){
                    Toast.makeText(CallSmsSafeActivity.this,"号码已经存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = (phoneChecked?1:0)+(smsChecked?2:0)+"";
                dao.add(phone,mode);
                infos = dao.findAll();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }
}
