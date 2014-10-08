package com.roger.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Roger on 2014/10/8.
 */
public class StreamTools {
    public static String readFromStream(InputStream is) throws IOException {
        String result = null;
        byte[]buff = new byte[1024];
        int len = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if((len=is.read(buff))!=-1){
            baos.write(buff,0,len);
        }
        is.close();
        result = baos.toString();
        baos.close();
        return result;
    }

}
