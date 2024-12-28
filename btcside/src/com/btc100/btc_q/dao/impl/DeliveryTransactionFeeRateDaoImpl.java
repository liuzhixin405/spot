package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.DeliveryTransactionFeeRateDao;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;

@Repository("deliveryTransactionFeeRateDao")
public class DeliveryTransactionFeeRateDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryTransactionFeeRateDao<T> {

	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public DeliveryTransactionFeeRate getByCodeAndLevel(String code, long levelId) {
		DeliveryTransactionFeeRate rate = spyMemcachedClient.get(code + levelId);
		if (rate != null) {
			return rate;
		}
		
		String hql = "FROM DeliveryTransactionFeeRate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		rate = (DeliveryTransactionFeeRate) get(hql, params);
		if (rate == null) {
			rate = new DeliveryTransactionFeeRate();
			rate.setEat_commission_rate(BigDecimal.ZERO);
			rate.setPending_commission_rate(BigDecimal.ZERO);
		}
		spyMemcachedClient.set(code + levelId, BTCQMemecacheKeys.TIMES.ONE_MIN, rate);
		return rate;
	}
	
	@Override
	public DeliveryTransactionFeeRate findLevelByEatRate(String code, BigDecimal rate) {
		String hql = "from DeliveryTransactionFeeRate t where t.f_code = :code and t.eat_commission_rate = :rate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("rate", rate);
		return (DeliveryTransactionFeeRate) get(hql, params);
	}

	@Override
	public DeliveryTransactionFeeRate getByCodeAndLevelNoCache(String code, long levelId) {
		String hql = "FROM DeliveryTransactionFeeRate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		return (DeliveryTransactionFeeRate) get(hql, params);
	}

}
