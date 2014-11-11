package com.roger.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.roger.mobilesafe.db.ApplockDBHelper;
import com.roger.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 2014/11/11.
 * 程序锁dao
 */
public class ApplockDao {
    private static final String TABLE_NAME = "applock";
    private ApplockDBHelper helper;
    private Context context;

    public ApplockDao(Context context){
        helper = new ApplockDBHelper(context);
        this.context = context;
    }

    /**
     * 添加程序锁
     */
    public void add(String packname){
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname",packname);
        database.insert(TABLE_NAME,null,values);
        database.close();
        Intent intent = new Intent();
        intent.setAction(MyConstants.BROADCAST_APP_LOCK_CHANGED);
        context.sendBroadcast(intent);
    }

    /**
     * 删除程序锁
     * @param packname
     */
    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME,"packname=?",new String[]{packname});
        db.close();
        Intent intent = new Intent();
        intent.setAction(MyConstants.BROADCAST_APP_LOCK_CHANGED);
        context.sendBroadcast(intent);
    }

    /**
     * 查询程序锁是否存在
     * @param packname
     * @return
     */
    public boolean find(String packname){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "packname=?", new String[]{packname}, null, null, null);
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> findAll(){
        List<String>packnames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
