package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/23.
 */
public class AtoolsActivity extends Activity{
    private TextView tv_query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        tv_query = (TextView) findViewById(R.id.tv_atools_query);


        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入号码归属地查询页面
                Intent intent = new Intent(AtoolsActivity.this,NumberAddressQueryActivity.class);
                startActivity(intent);
            }
        });
    }
}
