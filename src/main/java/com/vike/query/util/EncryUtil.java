package com.vike.query.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryUtil{

    /**SHA*/
    public static String Sha1(String inputStr){
        if(inputStr!=null) {
            try {
                MessageDigest sha1 = MessageDigest.getInstance("SHA1");
                byte[] results =  sha1.digest(inputStr.getBytes("UTF-8"));
                return bytesToHexString(results);
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**将字节数组转换成十六进制字符串*/
    private static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        for (int i = 0;i< bytes.length;i++){
            String temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}