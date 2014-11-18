package com.roger.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.roger.mobilesafe.db.DatabaseManager;
import com.roger.mobilesafe.db.TrafficDBHelper;
import com.roger.mobilesafe.domain.AppTrafficInfo;
import com.roger.mobilesafe.utils.DateUtil;

import java.util.List;

/**
 * Created by Roger on 2014/11/15.
 * 流量统计dao
 */
public class TrafficDao {
    private static final String TABLE_NAME = "traffics";
    private static final String TAG = "TrafficDao";
    private TrafficDBHelper helper;
    private Context context;

    public TrafficDao(Context context){
        helper = new TrafficDBHelper(context);
        DatabaseManager.initializeInstance(helper);
        this.context = context;
    }

    /**
     * 更新或插入
     * @param info
     */
    private void saveOrUpdate(AppTrafficInfo info,SQLiteDatabase db){
        String query = "select * from "+TABLE_NAME+" where packname = ? and daytime = ?";
        boolean isExist = false;
        Cursor cursor = db.rawQuery(query, new String[]{info.getPackName(), info.getDayTime() + ""});
        if(cursor.moveToNext())isExist=true;
        cursor.close();
        if(isExist){//update
            String update = "update "+TABLE_NAME+" set gprs=gprs+"+info.getGprs()
                            +",wifi=wifi+"+info.getWifi()+" where packname=? and daytime=?";
            db.execSQL(update,new Object[]{info.getPackName(),info.getDayTime()});
            if(info.getGprs()>0|info.getWifi()>0) {
                Log.i(TAG, info.getPackName() + ":" + update);
            }
        }else{//insert
            String insert = "insert into "+TABLE_NAME+"(packname,gprs,wifi,daytime) values(?,?,?,?)";
            Object[] params = {info.getPackName(),info.getGprs(),info.getWifi(),info.getDayTime()};
            db.execSQL(insert,params);
            if(info.getGprs()>0|info.getWifi()>0) {
                Log.i(TAG, info.getPackName() + ":" + insert);
            }
        }
    }
    public void saveOrUpdate(List<AppTrafficInfo> infos){

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        for(AppTrafficInfo info:infos){
            saveOrUpdate(info,db);
        }
        DatabaseManager.getInstance().closeDatabase();
    }


    /**
     * 获取某应用流量
     * @param packName
     * @param column
     * @param flag
     * @return
     */
    public long getAppTraffic(String packName,String column,int flag){
        long ret = 0;
        long total = 0;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = null;
        if(flag==1){//今天
            String sql= "select sum("+column+") from "+TABLE_NAME+" where daytime = ? and packname = ?";
            long morningTime = DateUtil.getMorningTime();
            cursor = db.rawQuery(sql,new String[]{morningTime+"",packName});

        }else if(flag==2){//昨天
            long monthTime = DateUtil.getYesterdayTime();
            String sql = "select sum("+column+") from "+TABLE_NAME+" where daytime >=? and packname = ?";
            cursor = db.rawQuery(sql,new String[]{monthTime+"",packName});
        }else if(flag==3){//当月
            long yesterdayTime = DateUtil.getYesterdayTime();
            long todayTime = DateUtil.getMonthTime();
            String sql = "select sum("+column+") from "+TABLE_NAME+" where daytime >=? and daytime<? and packname=?";
            cursor = db.rawQuery(sql,new String[]{yesterdayTime+"",todayTime+"",packName});
        }
        if(cursor!=null&&cursor.moveToNext()){
            total = cursor.getLong(0);
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();
        return total;
    }

    /**
     * 查询总流量
     * @param column gprs、wifi
     * @param flag 1：今天 2：昨天 3：每月
     * @return
     */
    public long findTotal(String column,int flag){
        long total = 0;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = null;
        if(flag==1){//今天
            String sql= "select sum("+column+") from "+TABLE_NAME+" where daytime = ?";
            long morningTime = DateUtil.getMorningTime();
            cursor = db.rawQuery(sql,new String[]{morningTime+""});

        }else if(flag==2){//昨天
            long monthTime = DateUtil.getYesterdayTime();
            String sql = "select sum("+column+") from "+TABLE_NAME+" where daytime >=?";
            cursor = db.rawQuery(sql,new String[]{monthTime+""});
        }else if(flag==3){//当月
            long yesterdayTime = DateUtil.getYesterdayTime();
            long todayTime = DateUtil.getMonthTime();
            String sql = "select sum("+column+") from "+TABLE_NAME+" where daytime >=? and daytime<?";
            cursor = db.rawQuery(sql,new String[]{yesterdayTime+"",todayTime+""});
        }
        if(cursor!=null&&cursor.moveToNext()){
            total = cursor.getLong(0);
            cursor.close();
        }
        DatabaseManager.getInstance().closeDatabase();
        return total;
    }
}
