package com.vike.query.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lsl
 * @createDate: 2019/10/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    private String type;
    private String name;
    private String url;
}
