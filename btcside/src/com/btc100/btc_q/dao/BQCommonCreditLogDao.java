package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQCommonCreditLog;

public interface BQCommonCreditLogDao<T> extends BaseDaoI<T>{

	BQCommonCreditLog getByUserIdAndType(long userId, int type);
}
