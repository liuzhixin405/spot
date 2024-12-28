package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQWorkOrderDao;

@Repository("bqWorkOrderDao")
public class BQWorkOrderDaoImpl<T> extends BaseDaoImpl<T> implements BQWorkOrderDao<T> {
	
}
