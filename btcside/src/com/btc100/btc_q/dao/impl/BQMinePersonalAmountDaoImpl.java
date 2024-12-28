package com.btc100.btc_q.dao.impl;

import com.btc100.btc_q.dao.BQMinePersonalAmountDao;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("bqMinePersonalAmountDao")
public class BQMinePersonalAmountDaoImpl<BQMinePersonalAmount> extends BaseDaoImpl<BQMinePersonalAmount> implements BQMinePersonalAmountDao<BQMinePersonalAmount> {
    @Override
    public BQMinePersonalAmount findByUserId(long userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);

        String hql = "FROM BQMinePersonalAmount a WHERE a.uid = :userId";
        List<BQMinePersonalAmount> list = find(hql, map);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public int updateAmount(long userId, BigDecimal amount, BigDecimal maxAmount) {
        String hql = "UPDATE BQMinePersonalAmount a SET a.mined_amount=a.mined_amount+:amount WHERE a.uid=:userId"
                + " AND a.mined_amount+:amount<=:maxAmount";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("amount", amount);
        map.put("userId", userId);
        map.put("maxAmount", maxAmount);

        return this.updateCondition(hql, map);
    }
}
