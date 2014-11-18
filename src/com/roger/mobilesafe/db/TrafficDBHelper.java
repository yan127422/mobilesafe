package com.roger.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Roger on 2014/11/15.
 * 流量统计
 */
public class TrafficDBHelper extends SQLiteOpenHelper {
    public TrafficDBHelper(Context context) {
        super(context, "traffics.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table traffics(_id integer primary key autoincrement,packname varchar(50),gprs bigint,wifi bigint,daytime bigint)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
