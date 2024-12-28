package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQTransactionFeeRateDao;
import com.btc100.btc_q.entity.BQTransactionFeeRate;

@Repository("bqTransactionFeeRateDao")
public class BQTransactionFeeRateDaoImpl<T> extends BaseDaoImpl<T> implements BQTransactionFeeRateDao<T> {

	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public BQTransactionFeeRate getByCodeAndLevel(String code, long levelId) {
		BQTransactionFeeRate rate = spyMemcachedClient.get(code + levelId);
		if (rate != null) {
			return rate;
		}
		
		String hql = "FROM BQTransactionFeeRate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		rate = (BQTransactionFeeRate) get(hql, params);
		if (rate == null) {
			rate = new BQTransactionFeeRate();
			rate.setEat_commission_rate(BigDecimal.ZERO);
			rate.setPending_commission_rate(BigDecimal.ZERO);
		}
		spyMemcachedClient.set(code + levelId, BTCQMemecacheKeys.TIMES.ONE_MIN, rate);
		return rate;
	}
	
	@Override
	public BQTransactionFeeRate findLevelByEatRate(String code, BigDecimal rate) {
		String hql = "from BQTransactionFeeRate t where t.f_code = :code and t.eat_commission_rate = :rate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("rate", rate);
		return (BQTransactionFeeRate) get(hql, params);
	}

	@Override
	public BQTransactionFeeRate getByCodeAndLevelNoCache(String code, long levelId) {
		String hql = "FROM BQTransactionFeeRate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		return (BQTransactionFeeRate) get(hql, params);
	}

}
