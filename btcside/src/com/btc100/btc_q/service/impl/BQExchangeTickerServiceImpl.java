package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQExchangeTickerDao;
import com.btc100.btc_q.entity.BQExchangeTicker;
import com.btc100.btc_q.service.BQExchangeTickerService;
import com.btc100.btc_q.vo.TickerVo;

@Service("bqExchangeTickerService")
public class BQExchangeTickerServiceImpl implements BQExchangeTickerService {

	@Autowired
	private BQExchangeTickerDao<BQExchangeTicker> bqExchangeTickerDao;
	
	@Override
	public BQExchangeTicker getExchangeTickerByCurrencyType(int type) {
		return bqExchangeTickerDao.getExchangeTickerByCurrencyType(type);
	}
	
	@Override
	public BQExchangeTicker readExchangeTickerByCurrencyType(int type) {
		return bqExchangeTickerDao.getExchangeTickerByCurrencyType(type);
	}

	@Override
	public void saveOrUpdate(TickerVo vo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", vo.getTypeId());
		params.put("market", vo.getMarket());
		
		boolean isSave = false;
		BQExchangeTicker ticker = bqExchangeTickerDao.get("From BQExchangeTicker where currency_type_id = :type and market = :market", params);
		if (ticker == null) {
			ticker = new BQExchangeTicker();
			ticker.setCurrency_type_id(vo.getTypeId());
			ticker.setMarket(vo.getMarket());
			isSave = true;
		}
		
		ticker.setDateline(vo.getTime());
		ticker.setHigh(vo.getHigh());
		ticker.setLow(vo.getLow());
		ticker.setLast(vo.getLast());
		ticker.setVol(vo.getVol());
		ticker.setBuy(vo.getBuy());
		ticker.setSell(vo.getSell());
		ticker.setTrans_pair(vo.getTrans_pair());
		
		if (isSave) {
			bqExchangeTickerDao.save(ticker);
		} else {
			bqExchangeTickerDao.update(ticker);
		}
	}

	@Override
	public void update(BQExchangeTicker setting) {
		bqExchangeTickerDao.update(setting);
	}

}
