package com.roger.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import com.roger.mobilesafe.db.dao.TrafficDao;
import com.roger.mobilesafe.domain.AppTrafficInfo;
import com.roger.mobilesafe.utils.DateUtil;

import java.util.*;

/**
 * Created by Roger on 2014/11/14.
 * 流量统计服务
 */
public class TrafficService extends Service{
    private static final int NET_ERROR = 0;//网络不可用
    private static final int NET_GPRS = 1;//3g/2g连接
    private static final int NET_WIFI = 2;//wifi连接
    private static final String TAG = "TrafficService";
    private static final int NET_ERROR2 = 3;
    private PackageManager pm;
    private Map<String,Long> lastTraffics;//上一次流量
    private BroadcastReceiver receiver;
    private int netState;//网络状态
    private Timer timer;
    private TimerTask task;
    private TrafficDao trafficDao;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = getPackageManager();
        trafficDao = new TrafficDao(this);
        //getTraffics
        updateTraffics();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetReceiver();
        registerReceiver(receiver,filter);
        startTimer();
    }

    /**
     * 读取更新流量
     */
    private void updateTraffics() {
        if(lastTraffics==null)lastTraffics = new HashMap<String, Long>();
        List<ApplicationInfo> appliactaionInfos = pm.getInstalledApplications(0);
        for(ApplicationInfo applicationInfo : appliactaionInfos){
            int uid = applicationInfo.uid;
            //proc/uid_stat/10086
            long tx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
            long rx = TrafficStats.getUidRxBytes(uid);//下载的流量 byte
            lastTraffics.put(applicationInfo.packageName,(tx+rx));
        }
    }

    /**
     * 开始任务、流量统计
     */
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                saveTraffics();
            }
        };
        timer.schedule(task,0,60*1000);
    }

    /**
     * 保存流量数据
     */
    private void saveTraffics() {
        //FIXME 保存流量信息到数据库
        if(lastTraffics==null)lastTraffics = new HashMap<String, Long>();
        long morningTime = DateUtil.getMorningTime();
        List<ApplicationInfo> appliactaionInfos = pm.getInstalledApplications(0);
        List<AppTrafficInfo>trafficInfos = new ArrayList<AppTrafficInfo>();
        for(ApplicationInfo applicationInfo : appliactaionInfos){
            int uid = applicationInfo.uid;
            //proc/uid_stat/10086
            long tx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
            long rx = TrafficStats.getUidRxBytes(uid);//下载的流量 byte
            long totalX = tx+rx;
            //上一次流量
            Long lastTraffic = lastTraffics.get(applicationInfo.packageName);
            lastTraffic = lastTraffic!=null?lastTraffic:totalX;
            AppTrafficInfo trafficInfo = new AppTrafficInfo();
            trafficInfo.setPackName(applicationInfo.packageName);
            trafficInfo.setDayTime(morningTime);
            long increment = totalX-lastTraffic;
            if(netState==NET_GPRS){//3g
                trafficInfo.setGprs(increment);
            }else if(netState==NET_WIFI){//wifi
                trafficInfo.setWifi(increment);
            }
            trafficInfos.add(trafficInfo);
        }
        trafficDao.saveOrUpdate(trafficInfos);
        updateTraffics();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        stopTimer();
        super.onDestroy();
    }

    private void stopTimer() {
        if(timer!=null&&task!=null){
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }
    }
    /**
     * 3g/wifi广播接收
     */
    private class NetReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
                int currentStat = getNetStat();
                if(netState!=currentStat){//网络状态改变
                    netState = currentStat;
                    saveTraffics();
                }
            }
        }
    }
    /**
     * 获取网络状态
     * @return
     */
    private int getNetStat(){
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);// 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo==null)return NET_ERROR;
        boolean available = networkInfo.isAvailable();
        if(!available)return NET_ERROR;
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(NetworkInfo.State.CONNECTED==state)return NET_GPRS;
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(NetworkInfo.State.CONNECTED==state)return NET_WIFI;
        return NET_ERROR2;
    }


}
