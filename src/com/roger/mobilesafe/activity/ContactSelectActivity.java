package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roger on 2014/10/18.
 */
public class ContactSelectActivity extends BaseActivity{
    private ListView lv_contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);
        lv_contacts = (ListView) findViewById(R.id.lv_contactSelect_contacts);
        final List<Map<String,String>> data = getContacts();

        lv_contacts.setAdapter(new SimpleAdapter(this,data,R.layout.item_contact,
                new String[]{"name","phone"},
                new int[]{R.id.et_item_name,R.id.et_item_phone}));
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = data.get(position).get("phone");
                Intent intent = new Intent();
                intent.putExtra(MyConstants.SAFE_NUMBER,phone);
                setResult(0, intent);
                finish();
            }
        });
    }

    private List<Map<String, String>> getContacts() {
        List<Map<String,String>>list = new ArrayList<Map<String, String>>();
        ContentResolver resolver = getContentResolver();
        Uri rawUri = ContactsContract.RawContacts.CONTENT_URI;
        Uri dataUri = ContactsContract.Data.CONTENT_URI;
        Cursor cursor = resolver.query(rawUri,new String[]{ContactsContract.RawContacts.CONTACT_ID},null,null,null);
        while(cursor.moveToNext()){
            Map<String,String>map = new HashMap<String, String>();
            String contactId = cursor.getString(0);
            if(contactId==null)continue;
            Cursor dataCursor = resolver.query(dataUri,
                    new String[]{"mimetype", "data1"},
                    "contact_id = ?",new String[]{contactId},null);
            while(dataCursor.moveToNext()){
                String typeId = dataCursor.getString(0);
                String data1 = dataCursor.getString(1);
                if("vnd.android.cursor.item/phone_v2".equals(typeId)){
                    map.put("phone",data1);
                }else if("vnd.android.cursor.item/name".equals(typeId)){
                    map.put("name",data1);
                }
            }
            dataCursor.close();
            list.add(map);
        }
        cursor.close();
        return list;
    }
}
