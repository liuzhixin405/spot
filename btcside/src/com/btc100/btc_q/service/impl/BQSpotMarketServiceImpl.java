package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQSpotMarketDao;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqSpotMarketService")
public class BQSpotMarketServiceImpl implements BQSpotMarketService {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Autowired
	private BQSpotMarketDao<BQSpotMarket> bqSpotMarketDao;


	@Override
	public BQSpotMarket readSpotMarketById(int id) {
		return bqSpotMarketDao.getSpotMarketById(id);
	}

	@Override
	public BQSpotMarket getSpotMarketByName(String name) {
		return bqSpotMarketDao.getSpotMarketByName(name);
	}

	@Override
	public BQSpotMarket getSpotMarketByCoinName(String coinName) {
		return bqSpotMarketDao.getSpotMarketByCoinName(coinName);
	}
	
	@Override
	public List<BQSpotMarket> findAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return bqSpotMarketDao.find(" from BQSpotMarket t where t.is_using = :isUsing ", params);
	}
	
	@Override
	public List<BQSpotMarket> readAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return bqSpotMarketDao.find(" from BQSpotMarket t where t.is_using = :isUsing ", params);
	}

	@Override
	public PagerModel readPagerModel() {
		return bqSpotMarketDao.searchHql(" from BQSpotMarket");
	}

	@Override
	public void updateSpotMarket(BQSpotMarket market) {
		if (market == null || StringUtil.isEmpty(market.getCoin_name()) || StringUtil.isEmpty(market.getCoin_id())) {
			return;
		}

		if (market.getId() != null && market.getId() > 0) {
			bqSpotMarketDao.update(market);
		} else {
			market.setId(null);
			bqSpotMarketDao.save(market);
		}

	}

	@Override
	public BQSpotMarket readMarket(int marketId) {
		List<BQSpotMarket> markets = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_MARKET);
		if (markets == null) {
			markets = readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_MARKET,BTCQMemecacheKeys.TIMES.ONE_MIN, markets);
		}
		
		if (markets == null) return null;
		
		for (BQSpotMarket market : markets) {
			if (market.getId() == marketId) {
				return market;
			}
		}
		
		return readSpotMarketById(marketId);
	}

}
