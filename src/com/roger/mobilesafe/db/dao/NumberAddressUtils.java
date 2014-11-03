package com.roger.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Roger on 2014/10/23.
 */
public class NumberAddressUtils {
    private static String path = "data/data/com.roger.mobilesafe/files/address.db";
    /**
     * 归属地查询
     * @param number
     * @return
     */
    public static String queryNumber(String number){
        if(number==null)return "";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        String address = "";
        if(number.matches("^1[34568]\\d{9}$")) {//手机号码
            Cursor cursor = database.rawQuery("SELECT location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
            if (cursor.moveToFirst()) {
                address = cursor.getString(0);
                cursor.close();
            }
        }else{
            switch (number.length()){
                case 3:
                    address = "公共号码";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 6://10068,
                    address = "客户号码";
                    break;
                case 7:
                    address = "本地号码";
                    break;
                case 8:
                    address = "本地号码";
                    break;
                default:
                    if(number.length()>=10&&number.startsWith("0")){//长途010-4945431
                        Cursor cursor = database.rawQuery("select location from data2 where area = ?",new String[]{number.substring(1,3)});
                        if(cursor.moveToFirst()){
                            address = cursor.getString(0);
                        }
                        cursor.close();
                        if(number.length()==12){//0571 80101230
                            cursor = database.rawQuery("select location from data2 where area = ?",new String[]{number.substring(1,4)});
                            if(cursor.moveToFirst()){
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
                    break;
            }
        }
        return address;
    }
}
