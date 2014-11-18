package com.roger.mobilesafe.test;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Created by Roger on 2014/11/14.
 * app文件列表
 */
public class AppFilesTest extends AndroidTestCase{
    private static final String TAG = "AppFilesTest";
    private static final int NET_ERROR = 0;
    private static final int NET_GPRS = 1;
    private static final int NET_WIFI = 2;
    private PackageManager pm;

    public void testGetPath()throws Exception{
        pm = getContext().getPackageManager();
        List<PackageInfo> pis = pm.getInstalledPackages(0);
        PackageInfo pi = pis.get(0);
        Log.i(TAG,"packAgeInfo:"+pi.applicationInfo.dataDir);
        File file = new File(pi.applicationInfo.dataDir);
        Log.i(TAG,file.canRead()+"");

    }


    public void testGetNet()throws Exception{
        Log.i(TAG,"net:"+getNetStat(getContext()));
    }
    /**
     * 获取网络状态
     * @return
     */
    private int getNetStat(Context context){
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo==null)return NET_ERROR;
        boolean available = networkInfo.isAvailable();
        if(!available)return NET_ERROR;
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(NetworkInfo.State.CONNECTED==state)return NET_GPRS;
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(NetworkInfo.State.CONNECTED==state)return NET_WIFI;
        return NET_ERROR;
    }
}
