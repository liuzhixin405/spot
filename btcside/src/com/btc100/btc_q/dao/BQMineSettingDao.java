package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQMineSetting;

import java.math.BigDecimal;

public interface BQMineSettingDao<BQMineSetting> extends BaseDaoI<BQMineSetting> {

    int addMinedAmount(long id, BigDecimal amount);
}
