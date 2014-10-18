package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.roger.mobilesafe.R;

/**
 * Created by Roger on 2014/10/15.
 */
public  abstract class BaseSettingActivity extends Activity{
    private static final String TAG = "BaseSettingActivity";
    protected GestureDetector detector;
    protected SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getSharedPreferences("config",MODE_PRIVATE);
        detector = new GestureDetector(this,new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityX>0){
                    showPre();
                    Log.i(TAG,"pre..."+velocityX);
                }else if(velocityX<0){
                    showNext();
                    Log.i(TAG,"next..."+velocityX);
                }
                return false;
            }
        });
    }

    public abstract void showNext();//下一步
    public abstract void showPre();//上一步

    protected void toNextActivity(Class<?>clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.left_enter,R.anim.left_exit);
        finish();
    }
    protected void toPreActivity(Class<?>clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.right_enter,R.anim.right_exit);
        finish();
    }
    public void next(View v){
        showNext();
    }
    public void pre(View v){
        showPre();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
