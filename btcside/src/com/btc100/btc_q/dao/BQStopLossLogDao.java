package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQStopLossLog;

public interface BQStopLossLogDao<T> extends BaseDaoI<T> {

	void insertLog(BQStopLossLog log);

}
