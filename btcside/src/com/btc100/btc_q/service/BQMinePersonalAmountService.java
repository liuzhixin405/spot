package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMinePersonalAmount;

import java.math.BigDecimal;

public interface BQMinePersonalAmountService {

    BigDecimal getMinedAmountByUid(long userId);

}
