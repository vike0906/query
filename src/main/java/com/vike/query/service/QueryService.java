package com.vike.query.service;

import com.vike.query.common.QueryException;

/**
 * @author: lsl
 * @createDate: 2019/10/24
 */
public interface QueryService {

    /**获取验证码*/
    String gainVerificationCode(long fansId, String agentTag, String name, String idCard, String bankCard, String mobile) throws QueryException;

    /**验证码校验*/
    boolean checkVerificationCode(String orderNo, String code) throws QueryException;

    /**查询结果*/
    String queryCardData(String name, String idCard, String bankCard, String mobile, String code, String orderNo) throws QueryException;

    /**全部查询结果*/
}
