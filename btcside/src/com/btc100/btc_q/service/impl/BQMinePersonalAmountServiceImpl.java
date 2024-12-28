package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQMinePersonalAmountDao;
import com.btc100.btc_q.entity.BQMinePersonalAmount;
import com.btc100.btc_q.service.BQMinePersonalAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("bqMinePersonalAmountService")
public class BQMinePersonalAmountServiceImpl implements BQMinePersonalAmountService {
    @Autowired
    private BQMinePersonalAmountDao<BQMinePersonalAmount> bqMinePersonalAmountDao;

    @Override
    public BigDecimal getMinedAmountByUid(long userId) {
        BQMinePersonalAmount minePersonalAmount = bqMinePersonalAmountDao.findByUserId(userId);
        if (minePersonalAmount == null) {
            minePersonalAmount = new BQMinePersonalAmount();
            minePersonalAmount.setUid(userId);
            minePersonalAmount.setMined_amount(BigDecimal.ZERO);
            bqMinePersonalAmountDao.save(minePersonalAmount);
        }
        return minePersonalAmount.getMined_amount();
    }
}
