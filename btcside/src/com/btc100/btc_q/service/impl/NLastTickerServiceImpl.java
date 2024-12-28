package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NLastTickerDao;
import com.btc100.btc_q.entity.NLastTicker;
import com.btc100.btc_q.service.NLastTickerService;

@Service("nLastTickerService")
public class NLastTickerServiceImpl implements NLastTickerService {

	@Autowired
	private NLastTickerDao<NLastTicker> nLastTickerDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public NLastTicker findByCode(String partition, String code) {
		NLastTicker nLastTicker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC_OBJ + "_" + partition + "_" + code);
		if (nLastTicker == null) {
			String sql = "select * FROM " + partition + "_last_ticker WHERE f_ticker_code=:scode";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("scode", code);
			nLastTicker = nLastTickerDao.getBySql(sql, params);
			if (nLastTicker != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC_OBJ + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, nLastTicker);
			}
		}
		return nLastTicker;
	}
	
	public NLastTicker readByCode(String partition, String code) {
		NLastTicker nLastTicker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC_OBJ + "_" + partition + "_" + code);
		if (nLastTicker == null) {
			String hql = "select * FROM " + partition + "_last_ticker WHERE f_ticker_code=:scode";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("scode", code);
			nLastTicker = nLastTickerDao.getBySql(hql, params);
			if (nLastTicker != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC_OBJ + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, nLastTicker);
			}
		}
		return nLastTicker;
	}

	public void insertLastTicker(String partition, NLastTicker entity) {
		nLastTickerDao.insert(partition, entity);
	}

	public int insertOrUploadLastTicker(String partition, NLastTicker entity) {
		NLastTicker nLastTicker = findByCode(partition, entity.getF_ticker_code());
		if (nLastTicker == null) {
			nLastTickerDao.insert(partition, entity);
		} else {
			entity.setF_ticker_id(nLastTicker.getF_ticker_id());
			updateLastTicker(partition, entity);
		}
		return entity.getF_ticker_id();
	}

	public int updateLastTicker(String partition, NLastTicker entity) {
		return nLastTickerDao.update(partition, entity);
	}

	public int deleteLastTicker(String partition, int f_ticker_id) {
		String sql = "DELETE FROM " + partition + "_last_ticker WHERE f_ticker_id=:id";
		Query query = nLastTickerDao.createSQLQuery(sql);
		query.setParameter("id", f_ticker_id);
		return query.executeUpdate();
	}
	
}
