package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQExchangeTickerDao;
import com.btc100.btc_q.entity.BQExchangeTicker;

@Repository("bqExchangeTickerDao")
public class BQExchangeTickerDaoImpl<T> extends BaseDaoImpl<T> implements BQExchangeTickerDao<T> {

	@Override
	public BQExchangeTicker getExchangeTickerByCurrencyType(int typeId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("typeId", typeId);
		final String hql = "from BQExchangeTicker where currency_type_id = :typeId";
		return (BQExchangeTicker) get(hql, params);
	}
	
}
