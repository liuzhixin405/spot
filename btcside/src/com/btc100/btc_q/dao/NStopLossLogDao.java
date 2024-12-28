package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NStopLossLog;

public interface NStopLossLogDao<T> extends BaseDaoI<T> {

	void insertLog(String partition, NStopLossLog log);

}
