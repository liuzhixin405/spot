package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMineSettingDao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository("bqMineSettingDao")
public class BQMineSettingDaoImpl<BQMineSetting> extends BaseDaoImpl<BQMineSetting> implements BQMineSettingDao<BQMineSetting> {

    @Override
    public int addMinedAmount(long id, BigDecimal amount) {
        String hql = "UPDATE BQMineSetting a SET a.mined_amount=a.mined_amount+:amount WHERE a.id=:id"
                + " AND a.mined_amount+:amount<=a.total_amount";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("amount", amount);
        map.put("id", id);
        return this.updateCondition(hql, map);
    }
}
