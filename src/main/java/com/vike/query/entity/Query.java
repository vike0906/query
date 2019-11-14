package com.vike.query.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/14
 */
@Entity
@Table(name = "wx_query")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "fans_id")
    private long fansId;
    @Column(name = "order_no")
    private long orderNo;
    @Column(name = "href")
    private String href;
    @Column(name = "create_time")
    private Date createTime;
}
