package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.VAccountDepositLogDao;

@Repository("vAccountDepositLogDao")
public class VAccountDepositLogDaoImpl<T> extends BaseDaoImpl<T> implements VAccountDepositLogDao<T> { }
