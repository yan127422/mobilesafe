package com.roger.mobilesafe;

import android.app.Activity;
import android.os.Bundle;

/**
 * 引导界面
 */
public class SplashActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
