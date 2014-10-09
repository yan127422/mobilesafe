package com.roger.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/9.
 */
public class SettingItemView extends RelativeLayout {
    private SwitchButton switchButton;
    private TextView textView;
    private boolean checked;//是否选择
    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.setting_item,this);
        switchButton = (SwitchButton) findViewById(R.id.sb_item_btn);
        switchButton.setClickable(false);
        textView = (TextView) findViewById(R.id.tv_item_name);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        switchButton.setOn(checked);
    }

    /**
     * 设置名称
     * @param text
     */
    public void setText(String text){
        textView.setText(text);
    }
}
