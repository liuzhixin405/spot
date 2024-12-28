package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQTransactionRateDao;

@Repository("bqTransactionRateDao")
public class BQTransactionRateDaoImpl<T> extends BaseDaoImpl<T> implements BQTransactionRateDao<T> { }
