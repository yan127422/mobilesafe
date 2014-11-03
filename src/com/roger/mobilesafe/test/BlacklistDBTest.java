package com.roger.mobilesafe.test;

import android.test.AndroidTestCase;
import com.roger.mobilesafe.db.BlacklistDBHelper;
import com.roger.mobilesafe.db.dao.BlacklistDao;

import java.util.Random;

/**
 * Created by Roger on 2014/10/28.
 */
public class BlacklistDBTest extends AndroidTestCase{

    public void testCreateTable() throws Exception{
        BlacklistDBHelper helper = new BlacklistDBHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd()throws Exception{
        BlacklistDao dao = new BlacklistDao(getContext());
        long l = 15555215550L;
        Random random = new Random();
        for(int i=0;i<100;i++){
            dao.add(l+i+"",random.nextInt(3)+1+"");
        }
    }

    public void testIsExist()throws Exception{
        BlacklistDao dao = new BlacklistDao(getContext());
        assertEquals(true,dao.isExist("15157000111"));
    }

    public void testUpdate()throws Exception{
        BlacklistDao dao = new BlacklistDao(getContext());
        dao.update("15555215556","3");
    }

    public void testDelete()throws Exception{
        BlacklistDao dao = new BlacklistDao(getContext());
        dao.delete("15157000001");
    }
}
