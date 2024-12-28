package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQLuckObtainRecordDao<T> extends BaseDaoI<T>{

    BigDecimal findCountCoin(long uid);
}
