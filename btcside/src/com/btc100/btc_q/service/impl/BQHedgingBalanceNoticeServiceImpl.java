package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQHedgingBalanceNoticeDao;
import com.btc100.btc_q.entity.BQHedgingBalanceNotice;
import com.btc100.btc_q.service.BQHedgingBalanceNoticeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqHedgingBalanceNoticeService")
public class BQHedgingBalanceNoticeServiceImpl implements BQHedgingBalanceNoticeService {

	@Autowired
	private BQHedgingBalanceNoticeDao<BQHedgingBalanceNotice>  bqHedgingBalanceNoticeDao;
	
	@Override
	public PagerModel readPager(int type, String coinName) {
		String hql = "from BQHedgingBalanceNotice where 1 = 1";
		Map<String, Object> params = new HashMap<String, Object>();
		if (type > 0) {
			hql += " and type = :type";
			params.put("type", type);
		}
		if (!StringUtil.isEmpty(coinName)) {
			hql += " and currency_name = :coinName";
			params.put("coinName", coinName);
		}
		
		return bqHedgingBalanceNoticeDao.searchHql(hql, params);
	}
	
	@Override
	public BQHedgingBalanceNotice getByTypeAndCoinName(int type, String coinName) {
		String hql = "from BQHedgingBalanceNotice where 1 = 1 and type = :type and currency_name = :coinName";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("coinName", coinName);
		params.put("type", type);
		return bqHedgingBalanceNoticeDao.get(hql, params);
	}

	@Override
	public BQHedgingBalanceNotice readByTypeAndCoinName(int type, String coinName) {
		String hql = "from BQHedgingBalanceNotice where 1 = 1 and type = :type and currency_name = :coinName";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("coinName", coinName);
		params.put("type", type);
		return bqHedgingBalanceNoticeDao.get(hql, params);
	}

	@Override
	public BQHedgingBalanceNotice get(long id) {
		return bqHedgingBalanceNoticeDao.get(BQHedgingBalanceNotice.class, id);
	}

	@Override
	public void save(BQHedgingBalanceNotice notice) {
		bqHedgingBalanceNoticeDao.save(notice);
	}

	@Override
	public void update(BQHedgingBalanceNotice notice) {
		bqHedgingBalanceNoticeDao.update(notice);
	}

	@Override
	public void delete(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqHedgingBalanceNoticeDao.deleteCondition("delete from BQHedgingBalanceNotice where id = :id", params);
	}

}
