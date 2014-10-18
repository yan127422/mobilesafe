package com.roger.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;

/**
 * Created by Roger on 2014/10/15.
 */
public class Setup3Activity extends BaseSettingActivity{
    private Button btn_select;
    private EditText et_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        btn_select = (Button) findViewById(R.id.btn_setup3_select);
        et_number = (EditText) findViewById(R.id.et_setup3_number);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setup3Activity.this,ContactSelectActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void showNext() {
        String safeNumber = et_number.getText().toString().trim();
        if(TextUtils.isEmpty(safeNumber)){
            Toast.makeText(this,"安全号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = config.edit();
        editor.putString(MyConstants.SAFE_NUMBER,safeNumber);
        editor.commit();
        toNextActivity(Setup4Activity.class);
    }

    @Override
    public void showPre() {
        toPreActivity(Setup2Activity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            et_number.setText(data.getStringExtra(MyConstants.SAFE_NUMBER).replace("-","").replace(" ",""));
        }
    }
}
