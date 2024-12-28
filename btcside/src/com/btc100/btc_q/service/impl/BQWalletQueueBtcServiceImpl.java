package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQWalletQueueBtcDao;
import com.btc100.btc_q.entity.BQWalletQueueBtc;
import com.btc100.btc_q.service.BQWalletQueueBtcService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqWalletQueueBtcService")
public class BQWalletQueueBtcServiceImpl implements BQWalletQueueBtcService {

	@Autowired
	private BQWalletQueueBtcDao<BQWalletQueueBtc> bqWalletQueueBtcDao;

	public BQWalletQueueBtc get(int currencyId) {
		Session session = bqWalletQueueBtcDao.getSession();
		String sql = "select * from bq_wallet_queue_btc where currency_type_id = :currencyId and state = 0 order by wallet_id limit 0,1";		
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("currencyId", currencyId);
        query.addEntity(BQWalletQueueBtc.class);       
        return (BQWalletQueueBtc) query.uniqueResult();
	}
	
	@Override
	public BQWalletQueueBtc getErc20() {
		Session session = bqWalletQueueBtcDao.getSession();
		String sql = "select * from bq_wallet_queue_btc where erc20 = true and state = 0 order by wallet_id limit 0,1";		
		SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(BQWalletQueueBtc.class);       
        return (BQWalletQueueBtc) query.uniqueResult();
	}
	
	@Override
	public BQWalletQueueBtc getTrc20() {
		Session session = bqWalletQueueBtcDao.getSession();
		String sql = "select * from bq_wallet_queue_btc where trc20 = true and state = 0 order by wallet_id limit 0,1";		
		SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(BQWalletQueueBtc.class);       
        return (BQWalletQueueBtc) query.uniqueResult();
	}
	
	public boolean lock(int walletId) {
		String hql = "update BQWalletQueueBtc set state = 1 where wallet_id=:walletId and state = 0";	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("walletId", walletId);
		return bqWalletQueueBtcDao.updateCondition(hql, params) > 0;
	}

	public int deleteById(int walletId) {
		String sql = "delete from BQWalletQueueBtc where wallet_id=:walletId";	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("walletId", walletId);
		return bqWalletQueueBtcDao.deleteCondition(sql, params);
		
	}

	public PagerModel findAll(int currencyId) {
		String hql="FROM BQWalletQueueBtc where currency_type_id = :currencyId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currencyId", currencyId);
		return bqWalletQueueBtcDao.searchHql(hql, params);
	}
	
	public Map<String,Object> saveList(int currencyId, List<BQWalletQueueBtc> list) {
		return bqWalletQueueBtcDao.insertDatas(list);		
	}

}
