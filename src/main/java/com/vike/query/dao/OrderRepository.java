package com.vike.query.dao;


import com.vike.query.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

    @Modifying
    @Query(value = "update wx_order set status = ?2 where id = ?1 and status = 3",nativeQuery = true)
    int updateOrderStatus(long id, int status);

    @Modifying
    @Query(value = "update wx_order set order_status = ?2, bonus_status=?3 where id = ?1 and order_status = 1 and bonus_status = 1",nativeQuery = true)
    int updateStatusAndBonus(long id, int orderStatus, int bonusStatus);

}
