package com.vike.query.dao;

import com.vike.query.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
public interface PayRepository extends JpaRepository<Pay,Long>, JpaSpecificationExecutor<Pay> {

    @Modifying
    @Query(value = "update wx_pay set status = ?2, pay_fee = ?3, update_time = now() where id = ?1 and status = 'init'",nativeQuery = true)
    int updateStatus(long id, String status, int payFee);

    Optional<Pay> findPayByOrderNo(String orderNo);

}
