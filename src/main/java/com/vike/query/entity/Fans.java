package com.vike.query.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wx_fans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Fans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "open_id",nullable = false)
    private String openId;

    @Column(name = "is_subscribe", nullable = false)
    private int isSubscribe;

    @Column(name = "is_collect_info", nullable = false)
    private int isCollectInfo;

    @Column(name = "agent_tag")
    private String agentTag;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;

}
