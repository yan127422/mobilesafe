package com.roger.mobilesafe.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Roger on 2014/11/3.
 */
public class SmsUtils {

    private static final String TAG = "SmsUtils";

    /**
     * 短信备份回调
     */
    public interface SmsCallBack{
        public void onProgress(int progress);
        public void beforeProgress(int max);
    }
    /**
     * 备份短信
     * @param context
     * @throws Exception
     */
    public static void backUpSms(Context context,SmsCallBack callBack)throws Exception{
        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos,"utf-8");
        serializer.startDocument("utf-8",true);
        serializer.startTag(null,"smss");

        //读取写入短信
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver()
                .query(uri,new String[]{"address","body","date","type"},null,null,null);
        callBack.beforeProgress(cursor.getCount());
        serializer.attribute(null,"max",cursor.getCount()+"");
        int i=0;
        while (cursor.moveToNext()){
            serializer.startTag(null,"sms");

            serializer.startTag(null,"address");
            serializer.text(cursor.getString(0));
            serializer.endTag(null,"address");

            serializer.startTag(null,"body");
            serializer.text(cursor.getString(1));
            serializer.endTag(null,"body");

            serializer.startTag(null,"date");
            serializer.text(cursor.getString(2));
            serializer.endTag(null,"date");

            serializer.startTag(null,"type");
            serializer.text(cursor.getString(3));
            serializer.endTag(null,"type");
            serializer.endTag(null,"sms");
            callBack.onProgress(++i);
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fos.close();
    }

    /**
     * 短信回复
     * @param context
     * @param flag 是否清理原来的短信
     * @param callBack
     */
    public static void restoreSms(Context context,boolean flag,SmsCallBack callBack)throws Exception{
        Uri uri = Uri.parse("content://sms");
        if(flag) {//清理原来的短信
            context.getContentResolver().delete(uri, null, null);
        }

        XmlPullParser parser = Xml.newPullParser();
        //得到文件流，并设置编码方式
        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
        FileInputStream inputStream= new FileInputStream(file);
        parser.setInput(inputStream, "utf-8");
        int eventType = parser.getEventType();
        ContentValues contentValues = null;
        int n = 0;
        while(eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            switch (eventType){
                case XmlPullParser.START_TAG:

                    if("smss".equals(tag)){
                        String max = parser.getAttributeValue(null,"max");
                        callBack.beforeProgress(Integer.valueOf(max));
                    }else if("sms".equals(tag)){
                        contentValues = new ContentValues();
                    }else if("address".equals(tag)){
                        contentValues.put("address",parser.nextText());
                    }else if("body".equals(tag)){
                        contentValues.put("body",parser.nextText());
                    }else if("date".equals(tag)){
                        contentValues.put("date",parser.nextText());
                    }else if("type".equals(tag)){
                        contentValues.put("type",parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if("sms".equals(tag)) {
                        context.getContentResolver().insert(uri, contentValues);
                        callBack.onProgress(++n);
                        Thread.sleep(100);
                    }
                    break;
            }
            eventType = parser.next();
        }
        Log.i(TAG,"备份成功");
        inputStream.close();
    }
}
