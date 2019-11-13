package com.vike.query.service.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vike.query.common.*;
import com.vike.query.component.WXPayComment;
import com.vike.query.dao.FansRepository;
import com.vike.query.dao.OrderRepository;
import com.vike.query.dao.PayRepository;
import com.vike.query.entity.Fans;
import com.vike.query.entity.Order;
import com.vike.query.entity.Pay;
import com.vike.query.pojo.VerificationCodeRequest;
import com.vike.query.service.QueryService;
import com.vike.query.util.HttpUtil;
import com.vike.query.util.RandomUtil;
import com.vike.query.vo.WXPayJSAPIInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/24
 */
@Slf4j
@Service
public class QueryServiceImpl implements QueryService {

    @Value("${system.hp.app_id:app_id}")
    private String APP_ID;
    @Value("${system.hp.app_secret:app_secret}")
    private String APP_SECRET;
    @Autowired
    WXPayComment wxPayComment;
    @Autowired
    FansRepository fansRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PayRepository payRepository;

    @Override
    public String gainVerificationCode(long fansId, String name, String idCard, String bankCard, String mobile) throws QueryException{

        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("account", APP_ID);
        paramsMap.put("bankCard", bankCard);
        paramsMap.put("idCard", idCard);
        paramsMap.put("mobile", mobile);
        paramsMap.put("name", name);
        String orderNo = OrderHelp.createOrderNo();
        paramsMap.put("orderNo", orderNo);
        String sign = HihippoHelp.sign(paramsMap,APP_SECRET);
        log.info("构造sign:{}",sign);
        paramsMap.put("sign", sign);

        Gson gson = new Gson();
        String params = gson.toJson(paramsMap);
        String response = HttpUtil.doPost(GlobalConstant.HP_AUTH_SMS, params);
        log.info("请求发送验证码接口返回结果：{}",response);

        JsonObject data = parseResponse(response);
        if(data!=null){
            String serialNumber = data.getAsJsonPrimitive("serialNumber").getAsString();
            log.info("解析返回结果获得serialNumber：{}",serialNumber);
            VerificationCodeRequest vc = new VerificationCodeRequest();
            Long orderNoLong = Long.valueOf(orderNo);
            Optional<Fans> op = fansRepository.findById(fansId);
            String agentTag = "agentTag";
            if(op.isPresent()) agentTag = op.get().getAgentTag();
            vc.setFansId(fansId).setAgentTag(agentTag).setOrderNo(orderNoLong).setSerialNumber(serialNumber)
                    .setRealName(name).setIdNO(idCard).setCreditCardNo(bankCard).setPhone(mobile);
            LocalCache.putVerificationCodeRequest(orderNoLong,vc);
            return orderNo;
        }
        log.info("返回结果解析失败");
        throw QueryException.fail("返回结果解析失败");
    }

    /**
     * 1:校验成功
     * 2:未通过校验
     * 3:验证码过期
     * 4:返回status异常
     * 5:返回结果解析失败
     */
    @Override
    public boolean checkVerificationCode(String orderNo, String code) throws QueryException{

        Map<String,String> paramsMap = new HashMap<>();
        VerificationCodeRequest vc = LocalCache.getVerificatonCodeRequest(Long.valueOf(orderNo));
        if(vc==null){
            log.info("本地缓存不存在该单号，已过期");
            throw QueryException.fail("本地缓存不存在该单号，已过期");
        }
        paramsMap.put("account", APP_ID);
        paramsMap.put("identifyingCode", code);
        paramsMap.put("orderNo", orderNo);
        paramsMap.put("serialNumber", vc.getSerialNumber());
        String sign = HihippoHelp.sign(paramsMap,APP_SECRET);
        log.info("构造sign:{}",sign);
        paramsMap.put("sign", sign);

        Gson gson = new Gson();
        String params = gson.toJson(paramsMap);
        String response = HttpUtil.doPost(GlobalConstant.HP_AUTH_SMS_CHECK, params);
        log.info("请求校验验证码接口返回结果：{}",response);

        JsonObject data = parseResponse(response);
        if(data!=null){
            int status = data.getAsJsonPrimitive("status").getAsInt();
            if(status==0){
                log.info("验证码{}过期",code);
                throw QueryException.fail("验证码过期");
            }else if(status==1){
                boolean isConsistent = data.getAsJsonPrimitive("isConsistent").getAsBoolean();
                if(isConsistent){
                    Order order = new Order();
                    order.setOrderNo(vc.getOrderNo()).setFansId(vc.getFansId()).setAgentTag(vc.getAgentTag())
                            .setName(vc.getRealName()).setIdNo(vc.getIdNO()).setCreditCardNo(vc.getCreditCardNo())
                            .setMobile(vc.getPhone()).setSerialNumber(vc.getSerialNumber()).setVerificationCode(code)
                            .setPrice(wxPayComment.price()).setOrderStatus(1).setBonusStatus(1);
                    orderRepository.save(order);
                    return true;
                }
                throw QueryException.fail("验证码错误");
            }
            throw QueryException.fail("返回status异常");
        }
        throw QueryException.fail("返回结果解析失败");
    }

    @Override
    public String queryCardData(String name, String idCard, String bankCard, String mobile, String code, String orderNo) throws QueryException {
        Map<String,String> paramsMap = new HashMap<>();
        VerificationCodeRequest vc = LocalCache.getVerificatonCodeRequest(Long.valueOf(orderNo));
        paramsMap.put("account", APP_ID);
        paramsMap.put("bankCard", bankCard);
        paramsMap.put("idCard", idCard);
        paramsMap.put("identifyingCode", code);
        paramsMap.put("mobile", mobile);
        paramsMap.put("name", name);
        paramsMap.put("orderNo", orderNo);
        paramsMap.put("serialNumber", vc.getSerialNumber());
        String sign = HihippoHelp.sign(paramsMap,APP_SECRET);
        log.info("构造sign:{}",sign);
        paramsMap.put("sign", sign);

        Gson gson = new Gson();
        String params = gson.toJson(paramsMap);
        String response = HttpUtil.doPost(GlobalConstant.HP_AUTH_SMS_CHECK, params);
        log.info("请求卡测评接口返回结果：{}",response);

        JsonObject data = parseResponse(response);
        if(data!=null){
            return data.getAsJsonPrimitive("url").getAsString();
        }
        throw QueryException.fail("返回结果解析失败");
    }

    @Override
    public WXPayJSAPIInfo perOrder(String orderNo) throws QueryException {
        Map<String,String> paramsMap = new HashMap<>();
        VerificationCodeRequest vc = LocalCache.getVerificatonCodeRequest(Long.valueOf(orderNo));
        Optional<Fans> op = fansRepository.findById(vc.getFansId());
        if(!op.isPresent()) throw QueryException.fail("系统异常");
        paramsMap.put("body", "付费查询");
        paramsMap.put("out_trade_no", orderNo);
        paramsMap.put("fee_type", "CNY");
        paramsMap.put("total_fee", String.valueOf(wxPayComment.price()));
        paramsMap.put("nonce_str", RandomUtil.UUID());
        paramsMap.put("spbill_create_ip", "123.12.12.123");
        paramsMap.put("trade_type", "JSAPI");
        paramsMap.put("product_id", "1");
        paramsMap.put("openid",op.get().getOpenId());
        try {
            Map<String, String> resp = wxPayComment.unifieOrder(paramsMap);
            String return_code = resp.get("return_code");
            if("SUCCESS".equals(return_code)){
                boolean res = wxPayComment.isSignatureValid(resp);
                if(!res) throw QueryException.fail("返回数据校验失败");
                String result_code = resp.get("result_code");
                if("SUCCESS".equals(result_code)){
                    String prepay_id = resp.get("prepay_id");
                    WXPayJSAPIInfo wxPayJSAPIInfo = new WXPayJSAPIInfo();
                    Map<String,String> map = new HashMap<>();
                    String timeStamp  = String.valueOf(System.currentTimeMillis()/1000);
                    String nonceStr = RandomUtil.UUID();
                    String prepay = "prepay_id="+prepay_id;
                    map.put("appId",APP_ID);
                    map.put("timeStamp",timeStamp);
                    map.put("nonceStr",nonceStr);
                    map.put("package",prepay);
                    map.put("signType","MD5");
                    String paySign = wxPayComment.generateSignature(map);
                    wxPayJSAPIInfo.setAppId(APP_ID).setNonceStr(nonceStr).setSignType("MD5")
                            .setTimeStamp(timeStamp).setPkg(prepay).setPaySign(paySign);
                    Pay pay = new Pay();
                    pay.setOrderNo(orderNo).setWay(1).setPerpayId(prepay_id).setTotalFee(wxPayComment.price()).setStatus("init");
                    payRepository.save(pay);
                    return wxPayJSAPIInfo;
                }
            }else {
                throw QueryException.fail("下单失败");
            }
        } catch (QueryException e){
            throw e;
        }catch (Exception e) {
            throw QueryException.fail("下单失败");
        }
        return null;
    }

    @Override
    public boolean orderQuery(String orderNo) throws QueryException {
        Optional<Pay> op = payRepository.findPayByOrderNo(orderNo);
        if(op.isPresent()){
            Pay pay = op.get();
            if("SUCCESS".equals(pay.getStatus())){
                return true;
            }else if("init".equals(pay.getStatus())){
                /**查询微信平台*/
                Map<String,String> paramsMap = new HashMap<>();
                paramsMap.put("out_trade_no",orderNo);
                paramsMap.put("nonce_str",RandomUtil.UUID());
                try {
                    Map<String, String> resp = wxPayComment.orderQuery(paramsMap);
                    String return_code = resp.get("return_code");
                    if("SUCCESS".equals(return_code)){
                        boolean res = wxPayComment.isSignatureValid(resp);
                        if(!res) throw QueryException.fail("返回数据校验失败");
                        String result_code = resp.get("result_code");
                        if("SUCCESS".equals(result_code)){
                            String trade_state = resp.get("trade_state");
                            if("SUCCESS".equals(trade_state)){
                                String total_fee = resp.get("total_fee");
                                payRepository.updateStatus(pay.getId(),trade_state,Integer.parseInt(total_fee));
                                orderRepository.updateStatusAndBonus(Long.valueOf(orderNo),3,2);
                                return total_fee.equals(String.valueOf(pay.getTotalFee()));
                            }else {
                                payRepository.updateStatus(pay.getId(),trade_state,0);
                                return false;
                            }
                        }
                    }else {
                        throw QueryException.fail("下单失败");
                    }
                } catch (QueryException e){
                    throw e;
                }catch (Exception e) {
                    throw QueryException.fail("创建订单查询失败");
                }
            }else {
                return false;
            }
        }else {
            throw QueryException.fail("系统异常");
        }
        return false;
    }


    private JsonObject parseResponse(String response){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        String code = jsonObject.getAsJsonPrimitive("code").getAsString();
        String result = jsonObject.getAsJsonPrimitive("result").getAsString();
        JsonObject data = jsonObject.getAsJsonObject("data");
        if("0".equals(code)&&"0000".equals(result)){
            return data;
        }
        return null;
    }
}
