package com.vike.query.util;

import javax.net.ssl.*;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
public class HttpsUtil implements X509TrustManager {


    public static final String METHOD_POST = "POST";

    public static final String METHOD_GET = "GET";

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public static String httpsRequest(@NotNull String requestUrl, @NotNull String requestMethod){
        return httpsRequest(requestUrl, requestMethod, "");
    }

    public static String httpsRequest(@NotNull String requestUrl, @NotNull String requestMethod, Map<String, String> params){
        String outputStr = "";
        if(params!=null&&!params.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for(Map.Entry<String, String> entrySet:params.entrySet()){
                sb.append(entrySet.getKey()).append("=").append(entrySet.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            outputStr = sb.toString();
        }
        return httpsRequest(requestUrl, requestMethod, outputStr);
    }

    public static String httpsRequest(@NotNull String requestUrl, @NotNull String requestMethod, String outputStr){
        StringBuffer buffer=null;
        try{
            /**创建SSLContext*/
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new HttpsUtil()};
            /**初始化*/
            sslContext.init(null, tm, new java.security.SecureRandom());
            /**获取SSLSocketFactory*/
            SSLSocketFactory ssf=sslContext.getSocketFactory();
            URL url=new URL(requestUrl);
            HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            /**设置当前实例使用的SSLSoctetFactory*/
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            /**往服务器端写内容*/
            if(outputStr!=null&&!"".equals(outputStr)){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            /**读取服务器端返回的内容*/
            InputStream is=conn.getInputStream();
            InputStreamReader isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            buffer=new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }



    public static void main(String[] args) {
        String s=httpsRequest("https://www.baidu.com","GET");
        System.out.println(s);
    }
}
