package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSpotBetterTokenTradeDao;
import com.btc100.btc_q.entity.BQSpotBetterTokenTrade;
import com.btc100.btc_q.service.BQSpotBetterTokenTradeService;
import com.btc100.btc_q.util.StringUtil;

@Service("bqSpotBetterTokenTradeService")
public class BQSpotBetterTokenTradeServiceImpl implements BQSpotBetterTokenTradeService {

	@Autowired
	private BQSpotBetterTokenTradeDao<BQSpotBetterTokenTrade> bqSpotBetterTokenTradeDao;

	@Override
	public BQSpotBetterTokenTrade getByOrderId(Long orderId) {
		String hql = "FROM BQSpotBetterTokenTrade b where b.order_id =:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", orderId);

		return bqSpotBetterTokenTradeDao.get(hql, map);
	}

	@Override
	public void save(BQSpotBetterTokenTrade order) {
		bqSpotBetterTokenTradeDao.save(order);
	}

	@Override
	public void update(BQSpotBetterTokenTrade order) {
		bqSpotBetterTokenTradeDao.update(order);
	}

	@Override
	public List<BQSpotBetterTokenTrade> findTradesAboveId(Long id) {
		String hql = "SELECT SUM(deal_amount) FROM BQSpotBetterTokenTrade b where b.id > :id ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return bqSpotBetterTokenTradeDao.find(hql, map);
	}

	@Override
	public List<BQSpotBetterTokenTrade> findTrades(long startTime, long endTime) {
		String hql = "SELECT SUM(deal_amount) FROM BQSpotBetterTokenTrade b "
				+ "where b.update_on >= :startTime and b.update_on < :endTime ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return bqSpotBetterTokenTradeDao.find(hql, map);
	}

	@Override
	public BigDecimal sumAmountNotSelf(long startTime, long endTime, String side, BigDecimal price) {
		String hql = "SELECT SUM(deal_amount) FROM BQSpotBetterTokenTrade b where b.is_self <> :isSelf ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isSelf", true);
		if (startTime > 0) {
			map.put("startTime", startTime);
			hql += " and b.update_on >= :startTime ";
		}
		if (endTime > 0) {
			map.put("endTime", endTime);
			hql += " and b.update_on < :endTime ";
		}
		if (!StringUtil.isEmpty(side)) {
			map.put("side", side);
			hql += " and b.side = :side ";
		}
		if (price.compareTo(BigDecimal.ZERO) > 0) {
			map.put("price", price);
			hql += " and b.entrust_price = :price ";
		}
		return bqSpotBetterTokenTradeDao.sum(hql, map);
	}

}
