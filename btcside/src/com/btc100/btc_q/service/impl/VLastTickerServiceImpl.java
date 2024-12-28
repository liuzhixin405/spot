package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.VLastTickerDao;
import com.btc100.btc_q.entity.VLastTicker;
import com.btc100.btc_q.service.VLastTickerService;

@Service("vLastTickerService")
public class VLastTickerServiceImpl implements VLastTickerService {

	@Autowired
	private VLastTickerDao<VLastTicker> vLastTickerDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public VLastTicker findByCode(String code) {
		VLastTicker bqLastTicker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC_OBJ + "_" + code);
		if (bqLastTicker == null) {
			String hql = "FROM VLastTicker WHERE f_ticker_code=:scode";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("scode", code);
			List<VLastTicker> list = vLastTickerDao.find(hql, params);
			if (list == null || list.size() <= 0) {
				return null;
			}

			bqLastTicker = list.get(0);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_BTC_OBJ + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, bqLastTicker);
		}
		return bqLastTicker;
	}
	
	public VLastTicker readByCode(String code) {
		VLastTicker bqLastTicker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC_OBJ + "_" + code);
		if (bqLastTicker == null) {
			String hql = "FROM VLastTicker WHERE f_ticker_code=:scode";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("scode", code);
			List<VLastTicker> list = vLastTickerDao.find(hql, params);
			if (list == null || list.size() <= 0) {
				return null;
			}

			bqLastTicker = list.get(0);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_BTC_OBJ + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, bqLastTicker);
		}
		return bqLastTicker;
	}

	public int insertLastTicker(VLastTicker entity) {
		vLastTickerDao.save(entity);
		return entity.getF_ticker_id();
	}

	public int insertOrUploadLastTicker(VLastTicker entity) {
		VLastTicker bqLastTicker = findByCode(entity.getF_ticker_code());
		if (bqLastTicker == null) {
			vLastTickerDao.save(entity);
		} else {
			entity.setF_ticker_id(bqLastTicker.getF_ticker_id());
			updateLastTicker(entity);
		}
		return entity.getF_ticker_id();
	}

	public int updateLastTicker(VLastTicker entity) {
		String hql = "update VLastTicker set f_ticker_high=:high,f_ticker_low=:low,f_ticker_buy=:buy,f_ticker_sell=:sell,f_ticker_last=:last,f_ticker_vol=:vol,f_ticker_trades=:trades WHERE f_ticker_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("high", entity.getF_ticker_high());
		params.put("low", entity.getF_ticker_low());
		params.put("buy", entity.getF_ticker_buy());
		params.put("sell", entity.getF_ticker_sell());
		params.put("last", entity.getF_ticker_last());
		params.put("vol", entity.getF_ticker_vol());
		params.put("id", entity.getF_ticker_id());
		params.put("trades", entity.getF_ticker_trades());

		return vLastTickerDao.updateCondition(hql, params);
	}

	public int deleteLastTicker(int f_ticker_id) {
		String hql = "DELETE FROM VLastTicker WHERE f_ticker_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", f_ticker_id);
		return vLastTickerDao.deleteCondition(hql, params);
	}
	
}
