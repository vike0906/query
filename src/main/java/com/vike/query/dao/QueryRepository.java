package com.vike.query.dao;

import com.vike.query.entity.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
public interface QueryRepository extends JpaRepository<Query,Long>, JpaSpecificationExecutor<Query> {


}
