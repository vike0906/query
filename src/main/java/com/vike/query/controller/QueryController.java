package com.vike.query.controller;

import com.vike.query.common.QueryException;
import com.vike.query.service.QueryService;
import com.vike.query.vo.Response;
import com.vike.query.vo.WXPayJSAPIInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lsl
 * @createDate: 2019/10/24
 */
@RestController
@Slf4j
@RequestMapping("query")
public class QueryController {

    @Autowired
    QueryService queryService;

    @PostMapping("gain")
    public Response gain(@RequestParam(required = false) Long fansId,
                         @RequestParam String userName,
                         @RequestParam String idNo,
                         @RequestParam String creditCardNo,
                         @RequestParam String phone){
        if(fansId == null) fansId=0L;
        try {
            String orderNo = queryService.gainVerificationCode(fansId, userName, idNo, creditCardNo, phone);
            return new Response(Response.SUCCESS, orderNo);
        }catch (QueryException e){
            return new Response(Response.ERROR, e.getMessage());
        }
    }

    /**创建查询订单*/
    @PostMapping("check")
    public Response<WXPayJSAPIInfo> check(@RequestParam String orderNo, @RequestParam String code){
        try {
            boolean b = queryService.checkVerificationCode(orderNo, code);
            if(b){
                log.info("验证码校验通过");
                WXPayJSAPIInfo wxPayJSAPIInfo = queryService.perOrder(orderNo);
                return new Response<>(wxPayJSAPIInfo);
            }else {
                return new Response<>(Response.ERROR, "验证码校验失败");
            }

        }catch (QueryException e){
            return new Response<>(Response.ERROR, e.getMessage());
        }
    }

    @PostMapping("summit")
    public Response summit(@RequestParam String code,
                         @RequestParam String orderNo){
        try {
            String url = queryService.queryCardData( code, orderNo);
            return new Response(Response.SUCCESS, url);
        }catch (QueryException e){
            return new Response(Response.ERROR, e.getMessage());
        }
    }

    /**发起支付，获取预付单信息*/
    //TODO 下单

    /**查询支付结果*/
    @PostMapping("query")
    public Response check(@RequestParam String orderNo){
        try {
            boolean b = queryService.orderQuery(orderNo);
            if(b){
                log.info("订单：{}已支付",orderNo);
                return new Response(Response.SUCCESS,"已支付");
            }else {
                return new Response(Response.ERROR, "未支付");
            }

        }catch (QueryException e){
            return new Response(Response.ERROR, e.getMessage());
        }
    }

    /**验证通过，查询并返回查询结果*/

    /**全部查询结果*/


}
