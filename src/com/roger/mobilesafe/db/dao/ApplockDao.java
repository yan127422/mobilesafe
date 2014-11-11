package com.roger.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.roger.mobilesafe.db.ApplockDBHelper;

/**
 * Created by Roger on 2014/11/11.
 * 程序锁dao
 */
public class ApplockDao {
    private static final String TABLE_NAME = "applock";
    private ApplockDBHelper helper;

    public ApplockDao(Context context){
        helper = new ApplockDBHelper(context);
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
    }

    /**
     * 删除程序锁
     * @param packname
     */
    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME,"packname=?",new String[]{packname});
        db.close();
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
}
