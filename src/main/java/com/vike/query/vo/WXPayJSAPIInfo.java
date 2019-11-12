package com.vike.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: lsl
 * @createDate: 2019/11/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WXPayJSAPIInfo {

    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String pkg;
    private String signType;
    private String paySign;
}
