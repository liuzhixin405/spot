package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotMarketCoinPairDao;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqSpotMarketCoinPairService")
public class BQSpotMarketCoinPairServiceImpl implements BQSpotMarketCoinPairService {

	@Autowired
	private BQSpotMarketCoinPairDao<BQSpotMarketCoinPair> bqSpotMarketCoinPairDao;

	@Override
	public void createTable(String code) {
		bqSpotMarketCoinPairDao.createTransactionRelated(code);
	}
	
	@Override
	public Boolean addMarketCoinPair(BQSpotMarketCoinPair coinPair) {
		bqSpotMarketCoinPairDao.save(coinPair);
		bqSpotMarketCoinPairDao.createTransactionRelated(coinPair.getCoin_pair_name());
		return true;
	}

	@Override
	public void update(BQSpotMarketCoinPair marketCoinPair) {
		bqSpotMarketCoinPairDao.update(marketCoinPair);
	}

	@Override
	public long time() {
		return bqSpotMarketCoinPairDao.getUnix_TIMESTAMP();
	}

	@Override
	public List<BQSpotMarketCoinPair> findByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		List<BQSpotMarketCoinPair> list = bqSpotMarketCoinPairDao
				.find("FROM BQSpotMarketCoinPair WHERE state = :state ", params);

		return list;
	}
	
	@Override
	public List<BQSpotMarketCoinPair> readByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		List<BQSpotMarketCoinPair> list = bqSpotMarketCoinPairDao
				.find("FROM BQSpotMarketCoinPair WHERE state = :state ", params);

		return list;
	}

	@Override
	public PagerModel readByState_pm(String coinPairName, int state) {
		StringBuilder whereHql = new StringBuilder("");
		Map<String, Object> params = new HashMap<String, Object>();
		if (state != 0) {
			params.put("state", state);
			whereHql.append(" and state = :state");
		}
		if (coinPairName != null && !coinPairName.isEmpty()) {
			params.put("coinPairName", coinPairName.trim().toUpperCase());
			whereHql.append(" and coin_pair_name = :coinPairName");
		}
		return bqSpotMarketCoinPairDao.searchHql(
				"From BQSpotMarketCoinPair where 1 = 1 " + whereHql.toString() + " order by state ASC", params);
	}

	@Override
	public BQSpotMarketCoinPair getByName(String coinPairName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("coinPairName", coinPairName);
		return bqSpotMarketCoinPairDao.get(" from BQSpotMarketCoinPair t where t.coin_pair_name = :coinPairName ",
				params);
	}
	
	@Override
	public BQSpotMarketCoinPair readByName(String coinPairName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("coinPairName", coinPairName);
		return bqSpotMarketCoinPairDao.get(" from BQSpotMarketCoinPair t where t.coin_pair_name = :coinPairName ",
				params);
	}

	@Override
	public BQSpotMarketCoinPair getById(long coinPairId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", coinPairId);
		return bqSpotMarketCoinPairDao.get(" from BQSpotMarketCoinPair t where t.id = :id ", params);
	}

	@Override
	public int updateHighAndLow(long coinPairId, BigDecimal highest, BigDecimal lowest) {
		String hql = "UPDATE BQSpotMarketCoinPair SET highest_price=:high,lowest_price=:low where id = :coinPairId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("high", highest);
		params.put("low", lowest);
		params.put("coinPairId", coinPairId);
		return bqSpotMarketCoinPairDao.updateCondition(hql, params);
	}
	
	@Override
	public int updateHighAndLowAndLast(long coinPairId, BigDecimal highest, BigDecimal lowest, BigDecimal last) {
		String hql = "UPDATE BQSpotMarketCoinPair SET highest_price=:high,lowest_price=:low,latest_price=:last where id = :coinPairId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("high", highest);
		params.put("low", lowest);
		params.put("last", last);
		params.put("coinPairId", coinPairId);
		return bqSpotMarketCoinPairDao.updateCondition(hql, params);
	}

}
