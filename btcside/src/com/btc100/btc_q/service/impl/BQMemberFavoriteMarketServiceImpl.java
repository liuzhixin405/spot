package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberFavoriteMarketDao;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;

@Service("bqMemberFavoriteMarketService")
public class BQMemberFavoriteMarketServiceImpl implements BQMemberFavoriteMarketService {

	@Autowired
	private BQMemberFavoriteMarketDao<BQMemberFavoriteMarket> bqMemberFavoriteMarketDao;

	@Override
	public void save(BQMemberFavoriteMarket market) {
		bqMemberFavoriteMarketDao.save(market);
	}

	@Override
	public void delete(BQMemberFavoriteMarket market) {
		bqMemberFavoriteMarketDao.delete(market);
	}

	@Override
	public void update(BQMemberFavoriteMarket market) {
		bqMemberFavoriteMarketDao.update(market);
	}

	@Override
	public BQMemberFavoriteMarket readByUserId(long uId, int type) {
		String hql = "FROM BQMemberFavoriteMarket b WHERE b.f_user_id = :id AND type = :type and part = :partition";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", uId);
		params.put("type", type);
		params.put("partition", "");
		return bqMemberFavoriteMarketDao.get(hql, params);
	}

	@Override
	public BQMemberFavoriteMarket readByUserId(long uId, String partition) {
		String hql = "FROM BQMemberFavoriteMarket b WHERE b.f_user_id = :id and part = :partition";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", uId);
		params.put("partition", partition);
		return bqMemberFavoriteMarketDao.get(hql, params);
	}

}
