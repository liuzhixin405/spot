package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQExchangeSettingDao;
import com.btc100.btc_q.entity.BQExchangeSetting;

@Repository("bqExchangeSettingDao")
public class BQExchangeSettingDaoImpl<T> extends BaseDaoImpl<T> implements BQExchangeSettingDao<T> {

	@Override
	public BQExchangeSetting getExchangeSettingBySymbol(String symbol) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbol", symbol);
		final String hql = "from BQExchangeSetting where symbol = :symbol";
		return (BQExchangeSetting) get(hql, params);
	}
	
}
