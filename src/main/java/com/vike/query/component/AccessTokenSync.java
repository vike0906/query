package com.vike.query.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vike.query.common.GlobalConstant;
import com.vike.query.util.HttpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * @Author: lsl
 * @Date: Create in 2018/11/16
 */
@Component
@Slf4j
public class AccessTokenSync implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${system.wx.app_id:app_id}")
    private String APP_ID;
    @Value("${system.wx.app_secret:app_secret}")
    private String APP_SECRET;

    private static AccessToken accessToken= null;

    public static String getAccessToken(){
        if(accessToken!=null){
            return accessToken.getAccessToken();
        }
        return null;
    }


    private synchronized AccessToken getAccessToken(String appId, String appSecret) {

        String url = String.format(GlobalConstant.ACCESS_TOKEN_URL, appId, appSecret);

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        log.info("取得AccessToken为：{}", result);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();
        String accessToken = jsonObject.getAsJsonPrimitive("access_token").getAsString();
        int expiresIn = jsonObject.getAsJsonPrimitive("expires_in").getAsInt();

        return new AccessToken(accessToken,expiresIn);
    }
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        log.info("项目启动完成");

        new Thread(()->{
            while (true){
                try {
                    accessToken = getAccessToken(APP_ID, APP_SECRET);
                    if(accessToken!=null){
                        Thread.sleep(7000*1000);
                    }else{
                        Thread.sleep(3*1000);
                    }
                } catch (InterruptedException e) {
                    log.error("获取AccessToken失败，休眠10s后重试");
                    e.printStackTrace();
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
