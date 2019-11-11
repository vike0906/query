package com.vike.query.service.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vike.query.common.*;
import com.vike.query.dao.FansRepository;
import com.vike.query.entity.Fans;
import com.vike.query.pojo.VerificationCodeRequest;
import com.vike.query.service.QueryService;
import com.vike.query.util.HttpUtil;
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
    FansRepository fansRepository;

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
                    //TODO 创建订单并写入数据库

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
