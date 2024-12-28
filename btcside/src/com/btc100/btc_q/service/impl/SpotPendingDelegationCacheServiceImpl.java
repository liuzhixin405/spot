package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.SpotAccountLogDao;
import com.btc100.btc_q.dao.SpotPendingDelegationCacheDao;
import com.btc100.btc_q.dao.SpotPendingLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotAccountLog;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.util.StringUtil;

@Service("spotPendingDelegationCacheService")
public class SpotPendingDelegationCacheServiceImpl implements SpotPendingDelegationCacheService {

	@Autowired
	private SpotPendingDelegationCacheDao<SpotPendingDelegationCache> cacheDao;
	@Autowired
	private BQAccountDao<BQAccount> accountDao;
	@Autowired
	private SpotAccountLogDao<SpotAccountLog> spotAccountLogDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private SpotPendingLogDao<SpotPendingLog> spotPendingLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Override
	public String lock(String coinPairName, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + coinPairName + "_" + id);
	}

	@Override
	public void unLock(String coinPairName, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + coinPairName + "_" + id, value);
	}

	@Override
	public int cancelPendingCache_one(SpotPendingDelegationCache pd, boolean needLog, BQSpotMarketCoinPair coinPair) throws ServiceException {
		if (pd == null || StringUtil.isEmpty(pd.getCoin_pair_name())) return 0;
		
		pd = cacheDao.findPendingById(pd.getDelegation_id(), pd.getCoin_pair_name());
		if (pd == null) return 0;
		
		if (coinPair == null) {
			return 0;
		}
		
		if (cacheDao.deletePending(pd.getCoin_pair_name(), pd.getDelegation_id()) <= 0) {
			throw new ServiceException("cancelPending_one error, 删除挂单失败！");
		}
		
		int currencyId = 0;
		BigDecimal changeAmount = BigDecimal.ZERO;
		if (pd.getDirection() == 1) {
			currencyId = coinPair.getMarket_coin_id();
			changeAmount = pd.getDelegation_price().multiply(pd.getUndeal_count());
		} else {
			currencyId = coinPair.getCoin_id();
			changeAmount = pd.getUndeal_count();
		}
		
		long uid = pd.getF_user_id();
		if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
			BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(uid, currencyId);
			if (bqAccountDao.updateAmount(uid, currencyId, changeAmount, changeAmount.negate()) <= 0) {
				throw new ServiceException("系统繁忙, 请重试");
			}
		
			String direction = "买入";
			if (pd.getDirection() == 2) {
				direction = "卖出";
			}
			
			if (needLog) {
				SpotAccountLog log = new SpotAccountLog();
				log.setTitle("现货交易" + pd.getCoin_pair_name() + "撤单，撤单" + direction + "，冻结转余额");
				log.setCurrency_type_id(currencyId);
				log.setOperation(3);	// 交易
				log.setDirection(1);
				log.setCurrent_amount(bqAccount.getBtc());
				log.setChange_amount(changeAmount);
				log.setLastest_amount(bqAccount.getBtc().add(changeAmount));
				log.setBefore_frozen(bqAccount.getBtc_locked());
				log.setAfter_frozen(bqAccount.getBtc_locked().add(changeAmount.negate()));
				log.setDateline(spotAccountLogDao.getUnix_TIMESTAMP());
				log.setF_user_id(uid);
				log.setTrans_no(pd.getDelegation_id() + "");
				log.setRecord_id(0);
				log.setF_coin_pair_name(pd.getCoin_pair_name());
				spotAccountLogDao.insertLog(log);
				
				spotPendingLogDao.updateActionByPendingNo(pd.getCoin_pair_name(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
		}

		return 1;
	}

	@Override
	public SpotPendingDelegationCache getBuy1(String coinPairName) {
		return cacheDao.getBuy1(coinPairName);
	}

	@Override
	public SpotPendingDelegationCache getSell1(String coinPairName) {
		return cacheDao.getSell1(coinPairName);
	}

	@Override
	public SpotPendingDelegationCache findPendingById(long orderId, String coinPairName) {
		return cacheDao.findPendingById(orderId, coinPairName);
	}

	@Override
	public long insertCacheAndChangeAccount(SpotPendingDelegationCache cache, BigDecimal amount, BQAccount bqAccount,
			int currencyId, boolean needLog, int device, String ipAddr) throws ServiceException {
		long uid = cache.getF_user_id();
		if (accountDao.updateAmount(uid, currencyId, amount.negate(), amount) <= 0) {
			throw new ServiceException("系统繁忙, 请重试");
		}
		
		cache = cacheDao.insertCache(cache);

		String direction = "买入";
		if (cache.getDirection() == 2) {
			direction = "卖出";
		}

		if (needLog) {
			SpotAccountLog log = new SpotAccountLog();
			log.setTitle("现货交易" + cache.getCoin_pair_name() + "， 挂单" + direction + "，余额转冻结");
			log.setCurrency_type_id(currencyId);
			log.setOperation(3);	// 交易
			log.setDirection(-1);
			log.setCurrent_amount(bqAccount.getBtc());
			log.setChange_amount(amount);
			log.setLastest_amount(bqAccount.getBtc().add(amount.negate()));
			log.setBefore_frozen(bqAccount.getBtc_locked());
			log.setAfter_frozen(bqAccount.getBtc_locked().add(amount));
			log.setDateline(spotAccountLogDao.getUnix_TIMESTAMP());
			log.setF_user_id(uid);
			log.setTrans_no(cache.getDelegation_id() + "");
			log.setRecord_id(0);
			log.setF_coin_pair_name(cache.getCoin_pair_name());
			spotAccountLogDao.insertLog(log);
			
			SpotPendingLog pendingLog = new SpotPendingLog(cache.getDelegation_id(), cache.getDelegation_time(),
					cache.getCoin_pair_id(), cache.getCoin_pair_name(), cache.getDirection(), cache.getDelegation_count(),
					cache.getDelegation_price(), cache.getDeal_count(), cache.getUndeal_count(),
					Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			spotPendingLogDao.insertLog(pendingLog);
		}

		return cache.getDelegation_id();
	}

	@Override
	public List<SpotPendingDelegationCache> getAllByUserId(int direction, String coinPairName, Long uid) {
		return cacheDao.queryByUserId(direction, coinPairName, uid);
	}

	@Override
	public List<SpotPendingDelegationCache> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price) {
		return cacheDao.findPendingAtPriceByUser(uid, coinPairName, direction, price);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> readPendingByUserIdAndTime(String coinPairName, long userId, long startTime,
			long endTime, long from, String direct, int size) {
		Session session = cacheDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where f_user_id = :userId";
		
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql += " and delegation_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			sql += " and delegation_id < :from";
		}
		
		sql += " order by delegation_id desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list(); 
	}

	@Override
	public SpotPendingDelegationCache readPendingByPendingNo(String pendingNo, String coinPairName, long userId) {
		return cacheDao.getPendingByPendingNo(pendingNo, coinPairName, userId);
	}

	@Override
	public Long getUnix_TIMESTAMP() {
		return cacheDao.getUnix_TIMESTAMP();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> findPendingByUserIdAndTime(String coinPairName, long userId, long startTime,
			long endTime, int size, boolean sort) {
		Session session = cacheDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where f_user_id = :userId";
		
		if (startTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (sort) {
			sql += " order by delegation_time desc";
		}
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list(); 
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> readPendingByUserIdAndSymbol(String coinPairName, long userId,
			String orderIds) {
		Session session = cacheDao.getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where f_user_id = :userId ";
		
		sql += " and pending_no in (" + orderIds + ")";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public void insertCacheForDepth(List<SpotPendingDelegationCache> caches, long uid, BigDecimal changeAccount,
			int direction, BQSpotMarketCoinPair  bqTransaction) throws ServiceException {
		if (caches.size() == 0) return;
		
		int remainCoinId = 0;
		if (direction == Contents.DIRECTION_BUY) {
			remainCoinId = bqTransaction.getMarket_coin_id();
		} else {
			remainCoinId = bqTransaction.getCoin_id();
		}
		
		if (accountDao.updateAmount(uid, remainCoinId, changeAccount.negate(), changeAccount) <= 0) {
			throw new ServiceException("余额不足, 请重试");
		}
		
		if (!cacheDao.insertCache(caches, bqTransaction.getCoin_pair_name())) {
			throw new ServiceException("插入错误,请重试");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SpotPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size) {
		Session session = cacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where delegation_price <= :price and direction = 1 order by delegation_price desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<SpotPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size) {
		Session session = cacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where delegation_price >= :price and direction = 2 order by delegation_price";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public BigDecimal findSumBuyFrozen(String coinPairName, long userId) {
		return cacheDao.findSumBuyFrozen(coinPairName, userId);
	}

	@Override
	public BigDecimal findSumSellFrozen(String coinPairName, long userId) {
		return cacheDao.findSumSellFrozen(coinPairName, userId);
	}
}
