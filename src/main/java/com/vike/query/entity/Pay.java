package com.vike.query.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/13
 */
@Entity
@Table(name = "wx_pay")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "order_no")
    private String orderNo;
    private int way;
    @Column(name = "perpay_id")
    private String perpayId;
    @Column(name = "total_fee")
    private int totalFee;
    @Column(name = "pay_fee")
    private int payFee;
    private String status;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_time")
    private Date createTime;
}
