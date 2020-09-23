package com.vike.query.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vike.query.common.GlobalConstant;
import com.vike.query.entity.Fans;
import com.vike.query.entity.FansInfo;
import com.vike.query.pojo.Button;
import com.vike.query.pojo.Menu;
import com.vike.query.service.FansService;
import com.vike.query.util.HttpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/24
 */
@Slf4j
@Component
public class WXApiInfoComment {

    @Value("${system.wx.app_id:app_id}")
    private String APP_ID;
    @Value("${system.wx.app_secret:app_secret}")
    private String APP_SECRET;
    @Autowired
    FansService fansService;


    /**通过网页授权code获取用户id*/
    public long getFansIdByCode(String code, String state){

        return getOauth2AccessToken(code,state);

    }

    /**
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf0e81c3bee622d60&redirect_uri=http%3A%2F%2Fnba.bluewebgame.com%2Foauth_response.php&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
     */
    public String getUrlWithAuthorize(String url, String state){
        try {
            url = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String menuUrl = String.format(GlobalConstant.CODE_MENU_URL, APP_ID, url, state);
        log.info("组装链接为：{}", menuUrl);
        return menuUrl;
    }

    private static String createUrlWithAuthorize(String url, String state, String appId){
        try {
            url = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String menuUrl = String.format(GlobalConstant.CODE_MENU_URL, appId, url, state);
        log.info("组装链接为：{}", menuUrl);
        return menuUrl;
    }

    private static AccessToken getAccessToken(String appId, String appSecret){

        String url = String.format(GlobalConstant.ACCESS_TOKEN_URL, appId, appSecret);

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        log.info("取得AccessToken为：{}", result);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();
        String accessToken = jsonObject.getAsJsonPrimitive("access_token").getAsString();
        int expiresIn = jsonObject.getAsJsonPrimitive("expires_in").getAsInt();

        return new AccessToken(accessToken,expiresIn);
    }

    public static void main(String [] args){

        String appid = "wxe4d7a76e1414928a";
        String appSecret = "09496ecd52c61b453f8f96183d862acf";

        AccessToken accessToken = getAccessToken(appid, appSecret);

        String link = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe4d7a76e1414928a&redirect_uri=https%3A%2F%2Fvike0906.com%2Fwx%2Fsb%2Finit&response_type=code&scope=snsapi_base&state=sxzq123456#wechat_redirect";
        System.out.println(link);
        Menu menu1 = new Menu("view","加入我们", link);
        Menu menu2 = new Menu("view","了解更多", link);

        Menu [] menus = {menu1,menu2};
        Button button = new Button(menus);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String s = gson.toJson(button);
        log.info(s);

        String url = String.format(GlobalConstant.CREATE_MENU_URL, accessToken.getAccessToken());

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_POST, s);
        log.info(result);
//        try {
//            String encode = URLEncoder.encode("https://vike0906.com/wx/sb/code2Token", "utf-8");
//            System.out.println(encode);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }


    /**
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * 通过code换取网页授权access_token
     */
    private long getOauth2AccessToken(String code, String state){

        String url = String.format(GlobalConstant.WEB_ACCESS_TOKEN_URL, APP_ID, APP_SECRET, code);

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        log.info("获取网页AccessToken结果：{}",result);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();

        String accessToken = jsonObject.getAsJsonPrimitive("access_token").getAsString();
        String openid = jsonObject.getAsJsonPrimitive("openid").getAsString();

        Optional<Fans> optional = fansService.findByOpenId(openid);

        if(optional.isPresent()){
            Fans fans = optional.get();
            if(fans.getIsCollectInfo()==1){
                /**采集基础信息*/
                collectInfoByAccessToken(accessToken, openid, fans.getId());
                fans.setIsCollectInfo(2).setUpdateTime(new Date(System.currentTimeMillis()));
                fansService.saveFans(fans);
            }
            return fans.getId();
        }else{
            if(state.length()==GlobalConstant.AGENT_TAG_LENGTH){
                Fans fans = new Fans();
                fans.setOpenId(openid).setIsSubscribe(2).setIsCollectInfo(3).setAgentTag(state);
                Fans fans1 = fansService.saveFans(fans);
                return fans1.getId();
            }
            return -1L;
        }
    }

    /**
     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
     * 拉取用户信息
     */
    private void collectInfoByAccessToken(String accessToken, String openId, Long fansId){

        String url = String.format(GlobalConstant.COLLECT_INFO_URL, accessToken, openId);

        String result = HttpsUtil.httpsRequest(url, HttpsUtil.METHOD_GET);

        log.info("拉取用户信息结果：{}",result);

        Gson gson = new Gson();
        FansInfo fansInfo = gson.fromJson(result, FansInfo.class);
        String privilegeStr = gson.toJson(fansInfo.getPrivilege());
        fansInfo.setFansId(fansId).setPrivilegeStr(privilegeStr);
        fansInfo.setCreateTime(new Date(System.currentTimeMillis()));

        fansService.saveFansInfo(fansInfo);
    }

}
