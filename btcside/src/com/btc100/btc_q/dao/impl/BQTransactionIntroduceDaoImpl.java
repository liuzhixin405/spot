package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQTransactionIntroduceDao;

@Repository("bqTransactionIntroduceDao")
public class BQTransactionIntroduceDaoImpl<T> extends BaseDaoImpl<T> implements BQTransactionIntroduceDao<T> {

}
