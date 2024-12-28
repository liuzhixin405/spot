package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQSpotMarketDao;
import com.btc100.btc_q.entity.BQSpotMarket;

@Repository("bqSpotMarketDao")
public class BQSpotMarketDaoImpl extends BaseDaoImpl<BQSpotMarket> implements BQSpotMarketDao<BQSpotMarket> {

	@Override
	public BQSpotMarket getSpotMarketById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return get(" from BQSpotMarket t where t.id = :id ", params);
	}

	@Override
	public BQSpotMarket getSpotMarketByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return get(" from BQSpotMarket t where t.market_name = :name ", params);
	}

	@Override
	public BQSpotMarket getSpotMarketByCoinName(String coinName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("coinName", coinName);
		return get(" from BQSpotMarket t where t.coin_name = :coinName ", params);
	}
	
}
