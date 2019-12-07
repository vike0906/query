package com.vike.query.controller;

import com.vike.query.entity.Fans;
import com.vike.query.pojo.WxMessage;
import com.vike.query.service.FansService;
import com.vike.query.util.EncryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@RestController
@Slf4j
public class DockingController {

    @Autowired
    FansService fansService;

    @GetMapping("wx")
    public String wx(@RequestParam(required = false)String signature,
                     @RequestParam(required = false)String timestamp,
                     @RequestParam(required = false)String nonce,
                     @RequestParam(required = false)String echostr){

        log.info("接收信息：signature:{}, timestamp:{}, nonce:{}, echostr:{},",signature, timestamp, nonce, echostr);

        if(StringUtils.isEmpty(signature)||StringUtils.isEmpty(timestamp)||StringUtils.isEmpty(nonce)||StringUtils.isEmpty(echostr)){
            return "Hello World";
        }

        String[] arr = new String[]{"wx123456",timestamp,nonce};

        Arrays.sort(arr);

        StringBuffer content = new StringBuffer();

        for(int i=0;i<arr.length;i++){
            content.append(arr[i]);
        }

        String temp = EncryUtil.Sha1(content.toString());

        if(temp.equals(signature)){
            return echostr;
        }

        return "Hello World";
    }

    @PostMapping("wx")
    public String wx(HttpServletRequest request){
        try {

            InputStream inputStream = request.getInputStream();

            WxMessage wxMessage = JAXB.unmarshal(inputStream, WxMessage.class);
            StringWriter sw = new StringWriter();
            JAXB.marshal(wxMessage, sw);
            log.info("接收到消息：{}", sw.toString());
            String formUser = wxMessage.getFromUserName();
            String toUser = wxMessage.getToUserName();
            StringWriter swr = new StringWriter();
            if("event".equals(wxMessage.getMsgType())){
                wxMessage.setMsgType("text");
                wxMessage.setContent("欢迎关注友信快查！一秒查询征信信息。");
                Date currentTime = new Date(System.currentTimeMillis());
                if("subscribe".equals(wxMessage.getEvent())){
                    Fans fans = new Fans();
                    Optional<Fans> optional = fansService.findByOpenId(wxMessage.getFromUserName());
                    if(optional.isPresent()){
                        fans = optional.get();
                        fans.setIsSubscribe(1).setUpdateTime(currentTime);
                    }else {
                        fans.setOpenId(wxMessage.getFromUserName()).setIsSubscribe(1).setIsCollectInfo(1).setUpdateTime(currentTime);
                    }
                    fansService.saveFans(fans);
                }else if("unsubscribe".equals(wxMessage.getEvent())){
                    Optional<Fans> optional = fansService.findByOpenId(wxMessage.getFromUserName());
                    if(optional.isPresent()){
                        Fans fans = optional.get();
                        fans.setIsSubscribe(2).setUpdateTime(currentTime);
                        fansService.saveFans(fans);
                    }
                }
            }else {
                wxMessage.setContent("您好");
            }
            wxMessage.setFromUserName(toUser);
            wxMessage.setToUserName(formUser);
            wxMessage.setCreateTime(String.valueOf(System.currentTimeMillis()));
            JAXB.marshal(wxMessage, swr);
            log.info("回复的消息：{}", swr.toString());
            return swr.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }

//    @GetMapping("token")
//    public String wx(){
//        return AccessTokenSync.getAccessToken();
//    }

    /**微信支付回调通知*/


}
