package com.vike.query.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: lsl
 * @createDate: 2019/9/23
 */
public class HttpUtil {

    /** 设置连接主机服务器的超时时间：15000毫秒 */
    private static final int CONNECT_OUT_TIME = 15000;
    /** 设置读取远程返回的数据时间：60000毫秒 */
    private static final int RESPONSE_OUT_TIME = 60000;



    /**
     * @description: 处理GET请求
     * @params [httpUrl]
     * @return java.lang.String
     */
    public static String doGet(String httpUrl) {

        HttpURLConnection connection = null;
        // 返回结果字符串
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            /** 设置连接方式：get */
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_OUT_TIME);
            connection.setReadTimeout(RESPONSE_OUT_TIME);
            /** 发送请求 */
            connection.connect();
            if (connection.getResponseCode() == 200) {
                result = readResponse(connection);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return result;
    }

    /**
     * @description: 处理POST请求
     * @params [httpUrl, param]
     * @return java.lang.String
     */
    public static String doPost(String httpUrl, String param) {

        HttpURLConnection connection = null;
        OutputStream os = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_OUT_TIME);
            connection.setReadTimeout(RESPONSE_OUT_TIME);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            /**发送和接收json字符串*/
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            os = connection.getOutputStream();
            os.write(param.getBytes());
            if (connection.getResponseCode() == 200) {
                result = readResponse(connection);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }
        return result;
    }

    /**
     * @description: 通过连接对象获取一个输入流，向远程读取
     * @params [connection]
     * @return java.lang.String
     */
    private static String readResponse(HttpURLConnection connection){
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer sbf = new StringBuffer();
        try {
            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            sbf = new StringBuffer();
            String temp;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sbf.toString();
    }
}
