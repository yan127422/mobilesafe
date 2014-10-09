package com.roger.mobilesafe.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/9.
 */
public class SwitchButton extends View implements View.OnClickListener {
    private Bitmap back,back_on_off,thumb;
    private int width,height;
    private int thumbX;
    private boolean on;
    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Resources res = getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.switch_back);
        back_on_off = BitmapFactory.decodeResource(res,R.drawable.switch_on_off);
        thumb = BitmapFactory.decodeResource(res,R.drawable.switch_thumb);
        width = back.getWidth();
        height = back.getHeight();
        this.setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        thumbX = on?width-height:0;
        int on_offX = on?0:height-width;
        canvas.drawBitmap(back_on_off,on_offX,0,paint);
        canvas.drawBitmap(back,0,0,paint);
        canvas.drawBitmap(thumb,thumbX,0,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 设置view的尺寸
         */
        setMeasuredDimension(width,height);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        invalidate();
    }

    @Override
    public void onClick(View v) {
        on = !on;
        invalidate();
    }
}
