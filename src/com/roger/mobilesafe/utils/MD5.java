package com.roger.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Roger on 2014/10/11.
 */
public class MD5 {
    public static String encode(String str){
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");

            byte [] results = digest.digest(str.getBytes());
            for(byte b:results){
                int number = b&0xff;
                String x = Integer.toHexString(number);
                if(x.length()==1){sb.append("0");}
                sb.append(x);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }
}
