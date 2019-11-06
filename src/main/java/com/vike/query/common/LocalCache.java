package com.vike.query.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vike.query.pojo.VerificationCodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: lsl
 * @createDate: 2019/10/25
 */
public class LocalCache {

    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);
    /**
     * 构建缓存容器用于储存短信验证码请求
     * 30分钟后过期
     */
    private static LoadingCache<Long, VerificationCodeRequest> verificationCodeRequestCache = CacheBuilder.newBuilder()
            .initialCapacity(500)
            .concurrencyLevel(10)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, VerificationCodeRequest>() {
                @Override
                public VerificationCodeRequest load(Long s){
                    return null;
                }
            });

    /**储存获取短信验证码请求*/
    public static void putVerificationCodeRequest(Long orderNo, VerificationCodeRequest verificationCodeRequest){
        log.info("储存获取短信验证码请求，当前容器储存数量：{}",verificationCodeRequestCache.size());
        verificationCodeRequestCache.put(orderNo,verificationCodeRequest);
    }

    /**通过serialNumber获取短信验证码请求*/
    public static VerificationCodeRequest getVerificatonCodeRequest(Long orderNo){
        log.info("读取获取短信验证码请求，当前容器储存数量：{}",verificationCodeRequestCache.size());
        return verificationCodeRequestCache.getIfPresent(orderNo);
    }
}
