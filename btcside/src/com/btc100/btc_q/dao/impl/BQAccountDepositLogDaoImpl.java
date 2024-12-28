package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQAccountDepositLogDao;

@Repository("bqAccountDepositLogDao")
public class BQAccountDepositLogDaoImpl<T> extends BaseDaoImpl<T> implements BQAccountDepositLogDao<T> { }
