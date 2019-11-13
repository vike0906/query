package com.vike.query.component;

import com.vike.query.wxpay.MyWXPayConfig;
import com.vike.query.wxpay.WXPay;
import com.vike.query.wxpay.WXPayConstants;
import com.vike.query.wxpay.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @author: lsl
 * @createDate: 2019/11/13
 */
@Slf4j
@Component
public class WXPayComment {

    @Value("${system.wxpay.appId:appId}")
    private String appId;
    @Value("${system.wxpay.machId:machId}")
    private String machId;
    @Value("${system.wxpay.key:key}")
    private String key;
    @Value("${system.wxpay.notifyUrl:notifyUrl}")
    private String notifyUrl;
    @Value("${system.wxpay.useSandBox:false}")
    private boolean useSandBox;
    @Value("${system.wxpay.totalFee:2999}")
    private int totalFee;

    private static MyWXPayConfig MY_WX_PAY_CONFIG;


    public void init(){
        try {
            MY_WX_PAY_CONFIG = new MyWXPayConfig(appId,machId,key);
            log.error("支付配置初始化成功");
        }catch (Exception e){
            log.error("支付配置初始化失败");
        }
    }

    public int price(){
        return totalFee;
    }

    public Map<String, String> unifieOrder(Map<String, String> data) throws Exception {
        WXPay wxpay = new WXPay(MY_WX_PAY_CONFIG, notifyUrl,false,useSandBox);
        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            log.info(resp.toString());
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> orderQuery(Map<String, String> data) throws Exception{
        WXPay wxpay = new WXPay(MY_WX_PAY_CONFIG, notifyUrl,false,useSandBox);
        try {
            Map<String, String> resp = wxpay.orderQuery(data);
            log.info(resp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSignatureValid(Map<String,String> data) throws Exception{
        return WXPayUtil.isSignatureValid(data,key,WXPayConstants.SignType.MD5);
    }

    public String generateSignature(Map<String ,String > map) throws Exception {
        return WXPayUtil.generateSignature(map,key,WXPayConstants.SignType.MD5);
    }


    /*Map<String, String> data = new HashMap<String, String>();
    data.put("body", "腾讯充值中心-QQ会员充值");
    data.put("out_trade_no", "2016090910595900000012");
    data.put("device_info", "");
    data.put("fee_type", "CNY");
    data.put("total_fee", "1");
    data.put("spbill_create_ip", "123.12.12.123");
    data.put("notify_url", "http://www.example.com/wxpay/notify");
    data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
    data.put("product_id", "12");*/
}
