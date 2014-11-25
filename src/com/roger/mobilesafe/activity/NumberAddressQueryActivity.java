package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.NumberAddressUtils;

/**
 * Created by Roger on 2014/10/23.
 */
public class NumberAddressQueryActivity extends BaseActivity{
    private EditText et_number;
    private TextView tv_address;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        et_number = (EditText) findViewById(R.id.et_number);
        tv_address = (TextView) findViewById(R.id.tv_numberAddress);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null&&s.length()>=3){
                    String address = NumberAddressUtils.queryNumber(s.toString());
                    tv_address.setText("归属地："+address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 号码归属地查询
     * @param view
     */
    public void addressQuery(View view){
        String number = et_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this,"号码为空",Toast.LENGTH_SHORT).show();
            Animation animation = AnimationUtils.loadAnimation(this,R.anim.shake);
            et_number.startAnimation(animation);
            vibrator.vibrate(new long[]{200,200,300,300,1000,2000},-1);
            return;
        }
        String address = NumberAddressUtils.queryNumber(number);
        tv_address.setText("归属地："+address);
    }

}
