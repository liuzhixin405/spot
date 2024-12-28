package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberTransactionSettleDao;
import com.btc100.btc_q.entity.BQMemberTransactionSettle;
import com.btc100.btc_q.service.BQMemberTransactionSettleService;

@Service("bqMemberTransactionSettleService")
public class BQMemberTransactionSettleServiceImpl implements BQMemberTransactionSettleService {

	@Autowired
	private BQMemberTransactionSettleDao<BQMemberTransactionSettle> bqMemberTransactionSettleDao;

	public void saveSettle(BQMemberTransactionSettle settle) {
		bqMemberTransactionSettleDao.save(settle);
	}

	public int updateSettle(BigDecimal fentanbi, String scode) {
		String sql = "UPDATE BQMemberTransactionSettle SET apportion=profit_lost*:ftb WHERE transaction_code=:code AND profit_lost>0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ftb", fentanbi);
		params.put("code", scode);

		return bqMemberTransactionSettleDao.updateCondition(sql, params);
	}

}