package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryStopLossLog;

public interface DeliveryStopLossLogDao<T> extends BaseDaoI<T> {

	void insertLog(DeliveryStopLossLog log);

}
