package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.VTransactionRateDao;

@Repository("vTransactionRateDao")
public class VTransactionRateDaoImpl<T> extends BaseDaoImpl<T> implements VTransactionRateDao<T> { }
