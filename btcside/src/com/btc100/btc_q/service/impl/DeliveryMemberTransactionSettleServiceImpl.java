package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryMemberTransactionSettleDao;
import com.btc100.btc_q.entity.DeliveryMemberTransactionSettle;
import com.btc100.btc_q.service.DeliveryMemberTransactionSettleService;

@Service("deliveryMemberTransactionSettleService")
public class DeliveryMemberTransactionSettleServiceImpl implements DeliveryMemberTransactionSettleService {

	@Autowired
	private DeliveryMemberTransactionSettleDao<DeliveryMemberTransactionSettle> deliveryMemberTransactionSettleDao;

	public void saveSettle(DeliveryMemberTransactionSettle settle) {
		deliveryMemberTransactionSettleDao.save(settle);
	}

	public int updateSettle(BigDecimal fentanbi, String scode) {
		String sql = "UPDATE DeliveryMemberTransactionSettle SET apportion=profit_lost*:ftb WHERE transaction_code=:code AND profit_lost>0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ftb", fentanbi);
		params.put("code", scode);

		return deliveryMemberTransactionSettleDao.updateCondition(sql, params);
	}

}