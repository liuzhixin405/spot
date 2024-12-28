package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQPriceIndexCache;

public interface BQPriceIndexCacheDao<T> extends BaseDaoI<T> {

	public BQPriceIndexCache getBtcIndexPrice(String code);
}
