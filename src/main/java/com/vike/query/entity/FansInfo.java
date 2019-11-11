package com.vike.query.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/10/24
 */
@Entity
@Table(name = "wx_fans_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FansInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fansId" ,nullable = false)
    private long fansId;

    @Column(name = "openid")
    private String openid;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "sex")
    private Integer sex;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "headimgurl")
    private String headimgurl;

    @Column(name = "privilege")
    private String privilegeStr;

    @Transient
    private String[] privilege;

    @Column(name = "unionid")
    private String unionid;

    @Column(name = "create_time")
    private Date createTime;

}
