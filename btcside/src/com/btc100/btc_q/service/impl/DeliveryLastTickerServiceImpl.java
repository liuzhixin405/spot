package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.DeliveryLastTickerDao;
import com.btc100.btc_q.entity.DeliveryLastTicker;
import com.btc100.btc_q.service.DeliveryLastTickerService;

@Service("deliveryLastTickerService")
public class DeliveryLastTickerServiceImpl implements DeliveryLastTickerService {

	@Autowired
	private DeliveryLastTickerDao<DeliveryLastTicker> deliveryLastTickerDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public DeliveryLastTicker readByCode(String code) {
		DeliveryLastTicker DeliveryLastTicker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC_OBJ + "_" + code);
		if (DeliveryLastTicker == null) {
			String hql = "FROM DeliveryLastTicker WHERE f_ticker_code=:scode";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("scode", code);
			List<DeliveryLastTicker> list = deliveryLastTickerDao.find(hql, params);
			if (list == null || list.size() <= 0) {
				return null;
			}

			DeliveryLastTicker = list.get(0);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_BTC_OBJ + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, DeliveryLastTicker);
		}
		return DeliveryLastTicker;
	}

	public int insertLastTicker(DeliveryLastTicker entity) {
		deliveryLastTickerDao.save(entity);
		return entity.getF_ticker_id();
	}

	public int insertOrUploadLastTicker(DeliveryLastTicker entity) {
		DeliveryLastTicker DeliveryLastTicker = readByCode(entity.getF_ticker_code());
		if (DeliveryLastTicker == null) {
			deliveryLastTickerDao.save(entity);
		} else {
			entity.setF_ticker_id(DeliveryLastTicker.getF_ticker_id());
			updateDeliveryLastTicker(entity);
		}
		return entity.getF_ticker_id();
	}

	public int updateDeliveryLastTicker(DeliveryLastTicker entity) {
		String hql = "update DeliveryLastTicker set f_ticker_high=:high,f_ticker_low=:low,f_ticker_buy=:buy,f_ticker_sell=:sell,f_ticker_last=:last,f_ticker_vol=:vol,f_ticker_trades=:trades WHERE f_ticker_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("high", entity.getF_ticker_high());
		params.put("low", entity.getF_ticker_low());
		params.put("buy", entity.getF_ticker_buy());
		params.put("sell", entity.getF_ticker_sell());
		params.put("last", entity.getF_ticker_last());
		params.put("vol", entity.getF_ticker_vol());
		params.put("id", entity.getF_ticker_id());
		params.put("trades", entity.getF_ticker_trades());

		return deliveryLastTickerDao.updateCondition(hql, params);
	}

	public int deleteLastTicker(int f_ticker_id) {
		String hql = "DELETE FROM DeliveryLastTicker WHERE f_ticker_id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", f_ticker_id);
		return deliveryLastTickerDao.deleteCondition(hql, params);
	}
}
