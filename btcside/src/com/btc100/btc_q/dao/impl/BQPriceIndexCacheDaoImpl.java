package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.entity.BQPriceIndexCache;

@Repository("bqPriceIndexCacheDao")
public class BQPriceIndexCacheDaoImpl<T> extends BaseDaoImpl<T> implements com.btc100.btc_q.dao.BQPriceIndexCacheDao<T> {
	

	@Override
	public BQPriceIndexCache getBtcIndexPrice(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type_code", code);
		
		return (BQPriceIndexCache) get("from BQPriceIndexCache t where t.type_code = :type_code", params);
	}
}
