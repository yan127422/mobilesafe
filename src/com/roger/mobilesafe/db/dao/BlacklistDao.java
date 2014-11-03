package com.roger.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.roger.mobilesafe.db.BlacklistDBHelper;
import com.roger.mobilesafe.domain.BlacklistInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 2014/10/28.
 */
public class BlacklistDao {
    private BlacklistDBHelper helper;
    private static final String TABLE_NAME = "blacklist";

    public BlacklistDao(Context context) {
        this.helper = new BlacklistDBHelper(context);
    }

    /**
     * 查询所有黑名单
     * @return
     */
    public List<BlacklistInfo> findAll(){
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select number,mode from blacklist order by _id desc",new String[]{});
        List<BlacklistInfo> infos = new ArrayList<BlacklistInfo>();
        while(cursor.moveToNext()){
            BlacklistInfo info = new BlacklistInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            infos.add(info);
        }
        cursor.close();
        database.close();
        return infos;
    }

    /**
     * 黑名单是否存在
     * @param number
     * @return
     */
    public boolean isExist(String number){
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from blacklist where number = ?", new String[]{number});
        boolean b = cursor.moveToNext();
        cursor.close();
        database.close();
        return b;
    }

    /**
     * 添加黑名单
     * @param number 手机号码
     * @param mode 模式：1、电话拦截 2、短信拦截 3、全部拦截
     */
    public void add(String number,String mode){
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        database.insert(TABLE_NAME,null,values);
        database.close();
    }

    /**
     * 修改拦截模式
     * @param number 手机号码
     * @param newMode 模式：1、电话拦截 2、短信拦截 3、全部拦截
     */
    public void update(String number,String newMode){
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",newMode);
        database.update(TABLE_NAME,values,"number = ?",new String[]{number});
        database.close();
    }

    /**
     * 删除黑名单
     * @param number
     */
    public void delete(String number){
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(TABLE_NAME,"number = ?",new String[]{number});
        database.close();
    }

    /**
     * 查询黑名单
     * @param number
     * @return
     */
    public BlacklistInfo findByNumber(String number) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select number,mode from blacklist where number = ?", new String[]{number});
        BlacklistInfo info = new BlacklistInfo();
        if(cursor.moveToNext()){
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
        }
        cursor.close();
        database.close();
        return info;
    }
}
