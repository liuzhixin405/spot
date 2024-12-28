package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQMinePersonalAmountDao<BQMinePersonalAmount> extends BaseDaoI<BQMinePersonalAmount> {

    BQMinePersonalAmount findByUserId(long userId);

    int updateAmount(long userId, BigDecimal amount, BigDecimal maxAmount);

}
