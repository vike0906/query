package com.vike.query.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
    private String accessToken;
    private int expiresIn;
}
