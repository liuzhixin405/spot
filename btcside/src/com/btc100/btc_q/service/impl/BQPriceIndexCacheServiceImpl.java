package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQPriceIndexCacheDao;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.vo.TickerVo;

@Service("bqPriceIndexCacheService")
public class BQPriceIndexCacheServiceImpl implements BQPriceIndexCacheService {

	@Autowired
	private BQPriceIndexCacheDao<BQPriceIndexCache> bqPriceIndexCacheDao;

	public BQPriceIndexCache getBtcIndexPrice(String typeCode) {
		return bqPriceIndexCacheDao.getBtcIndexPrice(typeCode);
	}
	
	public BQPriceIndexCache readByCode(String typeCode) {
		return bqPriceIndexCacheDao.getBtcIndexPrice(typeCode);
	}

	public void updateBtcCache(TickerVo vo, BigDecimal price) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", vo.getTypeCode());
		params.put("market", vo.getMarket());
		
		BQPriceIndexCache cache = bqPriceIndexCacheDao.get("From BQPriceIndexCache where type_code = :type and market = :market", params);
		if (cache == null) {
			cache = new BQPriceIndexCache();
		}
		
		if (cache.getCurrency_type_id() <= 0) {
			cache.setCurrency_type_id(vo.getTypeId());
		}
		cache.setPrice(price);
		cache.setDateline(vo.getTime());
		cache.setOtherprice(vo.getLastUsd());
		cache.setType_code(vo.getTypeCode());
		cache.setHigh(vo.getHigh());
		cache.setLow(vo.getLow());
		cache.setLast(vo.getLast());
		cache.setVol(vo.getVol());
		cache.setTrans_pair(vo.getTrans_pair());
		cache.setMarket(vo.getMarket());
		cache.setRatio(vo.getRatio());
		
		if (cache.getId() <= 0) {
			bqPriceIndexCacheDao.save(cache);
		} else {
			bqPriceIndexCacheDao.update(cache);
		}
	}

}
