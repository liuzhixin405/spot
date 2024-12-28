package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NAccountDepositLogDao;
import com.btc100.btc_q.entity.NAccountDepositLog;

@Repository("nAccountDepositLogDao")
public class NAccountDepositLogDaoImpl extends BaseDaoImpl<NAccountDepositLog> implements NAccountDepositLogDao<NAccountDepositLog> {
	
}
