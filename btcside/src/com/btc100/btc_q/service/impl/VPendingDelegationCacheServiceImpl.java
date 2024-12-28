package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.VAccountContractDao;
import com.btc100.btc_q.dao.VContractAccountLogDao;
import com.btc100.btc_q.dao.VMemberTransactionDao;
import com.btc100.btc_q.dao.VPendingDelegationCacheDao;
import com.btc100.btc_q.dao.VPendingLogDao;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.util.RandomUtil;

@Service("vPendingDelegationCacheService")
public class VPendingDelegationCacheServiceImpl implements VPendingDelegationCacheService {

	@Autowired
	private VPendingDelegationCacheDao<VPendingDelegationCache> vPendingDelegationCacheDao;
	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@Autowired
	private VPendingLogDao<VPendingLog> vPendingLogDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;
	@Autowired
	private VContractAccountLogDao<VContractAccountLog> vContractAccountLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	/**
	 * 开仓
	 * 
	 * @throws ServiceException
	 */
	public long insertCacheAndChangeAccount(VPendingDelegationCache cache, int currencyId, BigDecimal fee, VAccountContract bqAccountContract, boolean needLog, int device, String ipAddr) throws ServiceException {

		long uid = cache.getF_user_id();
		BigDecimal changeAmount = cache.getDeposit().add(fee);
		BigDecimal changeRemainNum = changeAmount.negate();
		if (vAccountContractDao.updateAmount(uid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("系统繁忙, 请重试");
		}

		cache = vPendingDelegationCacheDao.insertCache(cache);
		
		if (needLog) {
			VContractAccountLog log = new VContractAccountLog("多单开仓，余额转冻结", currencyId, Contents.TRANS_TYPE_JY, changeRemainNum.signum(), bqAccountContract
					.getF_btc(), changeRemainNum.abs(), bqAccountContract.getF_btc().add(changeRemainNum), bqAccountContract.getF_lockbtc(),
					bqAccountContract.getF_lockbtc().add(changeAmount), bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid,
					RandomUtil.getTrans_no(), cache.getDelegation_id(), cache.getTransaction_code());
	
			vContractAccountLogDao.insertLog(log);
			
			VPendingLog pendingLog = null;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_DDKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO, cache.getDelegation_count(), 
						cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			} else {
				pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_KTKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO, cache.getDelegation_count(),
						cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			}
			vPendingLogDao.insertLog(pendingLog);
		}
		
		return cache.getDelegation_id();
	}

	public void delete(String code, Long id) {
		vPendingDelegationCacheDao.deletePending(code, id);

	}

	public VPendingDelegationCache getBuy1(String code) {
		vPendingDelegationCacheDao.getSession().flush();
		vPendingDelegationCacheDao.getSession().clear();
		return vPendingDelegationCacheDao.getBuy1(code);
	}

	public VPendingDelegationCache getSell1(String code) {
		vPendingDelegationCacheDao.getSession().flush();
		vPendingDelegationCacheDao.getSession().clear();
		return vPendingDelegationCacheDao.getSell1(code);
	}

	private int updateMemberTransaction(VPendingDelegationCache cache, long mtid) throws ServiceException {
		BigDecimal minRemain = BigDecimal.ZERO;
		VMemberTransaction trans = vMemberTransactionDao.get(VMemberTransaction.class, mtid);
		
		BigDecimal fee = BTCQMath.getFee(cache.getDelegation_count(), cache.getDelegation_price(), cache.getCommission_rate(), BigDecimal.ONE);
		BigDecimal pendingIncome = BigDecimal.ZERO;
		if (cache.getDirection() == Contents.DIRECTION_SELL) {
			pendingIncome = cache.getDelegation_count().multiply(cache.getDelegation_price().subtract(trans.getAverage_price())).subtract(fee);
		} else {
			pendingIncome = cache.getDelegation_count().multiply(trans.getAverage_price().subtract(cache.getDelegation_price())).subtract(fee);
		}
		
		if (pendingIncome.compareTo(BigDecimal.ZERO) < 0) {
			BigDecimal rate = cache.getDelegation_count().divide(trans.getHold_count().add(trans.getLock_count()), 20, BigDecimal.ROUND_HALF_DOWN);
			BigDecimal deposit = rate.multiply(trans.getDeposit());
			
			if (pendingIncome.add(deposit).compareTo(BigDecimal.ZERO) < 0) {
				minRemain = deposit.add(pendingIncome);
			}
		}
		
		BigDecimal changeRemain = BigDecimal.ZERO;
		BigDecimal frozenRemain = BigDecimal.ZERO;
		BigDecimal frozenCount = BigDecimal.ZERO;
		BigDecimal changeAmount = BigDecimal.ZERO;
		minRemain = minRemain.setScale(8, BigDecimal.ROUND_UP);
		if (minRemain.compareTo(BigDecimal.ZERO) < 0) {
			// (delegationcount / (holdcount + lockcount - frozencount)) * fromeremain;
			changeRemain = cache.getDelegation_count().divide(trans.getHold_count().add(trans.getLock_count()).subtract(trans.getF_frozen_count()), 20, BigDecimal.ROUND_DOWN).multiply(trans.getF_fromremain());
			changeRemain = changeRemain.setScale(8, BigDecimal.ROUND_UP);
			changeAmount = minRemain.add(changeRemain);
			if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
				frozenRemain = changeAmount;
				changeAmount = BigDecimal.ZERO;
			}
			
			frozenCount = cache.getDelegation_count();
		}
		
		if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
			if (vAccountContractDao.updateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("系统繁忙, 请重试");
			}
		}

		cache.setF_frozenremain(minRemain.negate());
		int flag = vMemberTransactionDao.updateMemberTransByID(mtid, cache.getDelegation_count().negate(), cache.getDelegation_count(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
				changeRemain.negate(), frozenRemain, frozenCount, minRemain.negate(), BigDecimal.ZERO);
		
		return flag;
	}

	/**
	 * 平多仓
	 */
	public long insertPCD(VPendingDelegationCache cache, long mtid, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(cache, mtid);
		if (flag > 0) {
			cache = vPendingDelegationCacheDao.insertCache(cache);

			VPendingLog pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_DDPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache.getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			vPendingLogDao.insertLog(pendingLog);
			
			return cache.getDelegation_id();
		}

		return 0;
	}

	/**
	 * 平空仓
	 * @throws ServiceException 
	 */
	public long insertPCK(VPendingDelegationCache cache, long mtid, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(cache, mtid);
		if (flag > 0) {
			cache = vPendingDelegationCacheDao.insertCache(cache);

			VPendingLog pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_KTPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache.getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			vPendingLogDao.insertLog(pendingLog);
		}

		return cache.getDelegation_id();
	}

	public List<VPendingDelegationCache> getAllByUserId(int direction, String code, Long uid) {
		return vPendingDelegationCacheDao.queryByUserId(direction, code, uid);
	}
	
	@Override
	public VPendingDelegationCache findPendingById(long orderId, String vCode) {
		return vPendingDelegationCacheDao.findPendingById(orderId, vCode);
	}
	
	@Override
	public VPendingDelegationCache readPendingByPendingNo(String pendingNo, String bqCode, long userId) {
		return vPendingDelegationCacheDao.getPendingByPendingNo(pendingNo, bqCode, userId);
	}

	@Override
	public List<VPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction,
			BigDecimal price) {
		return vPendingDelegationCacheDao.findPendingAtPriceByUser(uid, code, direction, price);
	}

	@Override
	public List<VPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority,
			int direction, int multiple) {
		return vPendingDelegationCacheDao.findPendingByMemberTransInfo(code, userId, priority, direction, multiple);
	}

	@Override
	public VPendingDelegationCache saveCache(VPendingDelegationCache cache) throws HibernateException {
		return vPendingDelegationCacheDao.insertCache(cache);
	}

	@Override
	public List<VPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple) {
		return vPendingDelegationCacheDao.queryPCDelegations(direction, code, fUserId, multiple);
	}

	@Override
	public String lock(String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.V_PENDING_CACHE_LOCK + code + "_" + id);
	}

	@Override
	public void unLock(String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.V_PENDING_CACHE_LOCK + code + "_" + id, value);
	}

	@Override
	public int cancelPendingCache_one(VPendingDelegationCache pd, int currencyId, boolean needLog) throws ServiceException {
		if (pd == null) return 1;
		
		long userid = pd.getF_user_id(); // 用户id
		String code = pd.getTransaction_code(); // 合约号
		
		pd = vPendingDelegationCacheDao.findPendingById(pd.getDelegation_id(), code);
		if (pd == null) return 1;
		
		if (pd.getPriority() != Contents.PRIORITY_BC) {
			if (vPendingDelegationCacheDao.deletePending(code, pd.getDelegation_id()) <= 0) {
				throw new ServiceException("系统繁忙, 请稍后重试");
			}
		}

		// 需要恢复的保证金
		BigDecimal deposit = pd.getUndeal_count().multiply(pd.getDelegation_price()).divide(new BigDecimal(pd.getMultiple()), 8, BigDecimal.ROUND_DOWN);
		if (pd.getPriority() == Contents.PRIORITY_KC) {
			// 恢复冻结资金
			BigDecimal fee = pd.getUndeal_count().multiply(pd.getDelegation_price()).multiply(pd.getCommission_rate()).setScale(8, BigDecimal.ROUND_DOWN);
			BigDecimal changeAmount = deposit.add(fee);
			if (vAccountContractDao.updateAmount(pd.getF_user_id(), code, BigDecimal.ZERO, changeAmount, changeAmount.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("cancelPending_one error, 删除挂单失败！");
			}
			
			if (needLog) {
				VAccountContract accountContract = vAccountContractDao.findByUserIDCode(pd.getF_user_id(), code);
				VContractAccountLog accountlog = new VContractAccountLog("开仓(cache)撤销，冻结转余额", currencyId, Contents.TRANS_TYPE_JY, changeAmount.signum(),
						accountContract.getF_btc().subtract(changeAmount), changeAmount, accountContract.getF_btc(), accountContract.getF_lockbtc().add(changeAmount), 
						accountContract.getF_lockbtc(), vAccountContractDao.getUnix_TIMESTAMP(), pd.getF_user_id(), RandomUtil.getTrans_no(), pd.getDelegation_id(), accountContract.getF_code());
				vContractAccountLogDao.insertLog(accountlog);

				vPendingLogDao.updateActionByPendingNo(pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
		} else if (pd.getPriority() == Contents.PRIORITY_PC) {

			int type = pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			VMemberTransaction manyPosition = vMemberTransactionDao.getMemberTransaction(code, userid, type, pd.getMultiple());

			if (needLog) {
				vPendingLogDao.updateActionByPendingNo(pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
			
			BigDecimal chagneRemain = pd.getF_frozenremain();
			BigDecimal changeFrozenCount = BigDecimal.ZERO;
			if (chagneRemain.compareTo(BigDecimal.ZERO) > 0) {
				changeFrozenCount = pd.getUndeal_count().negate();
			}
			if (vMemberTransactionDao.updateMemberTransByID(manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, changeFrozenCount, chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("持仓冻结异常! userid: " + pd.getF_user_id() + ", getUndeal_count: " +  pd.getUndeal_count().doubleValue() + ", chagneRemain: " + pd.getF_frozenremain().doubleValue());
			}
		}

		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long statrtTime,
			long endTime, int size) {
		Session session = vPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where f_user_id = :userId and priority != 3";
		if (statrtTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		sql += " order by delegation_time desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		if (statrtTime > 0) {
			query.setLong("startTime", statrtTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(VPendingDelegationCache.class);
		
		return query.list(); 
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = vPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where f_user_id = :userId and priority != 3";
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
		
		query.addEntity(VPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public VPendingDelegationCache readOnePending(String bqCode, long userId, int multiple) {
		Session session = vPendingDelegationCacheDao.getSession();
		String sql = "SELECT * FROM " + bqCode + "_v_pending_delegation_cache WHERE f_user_id = :userId and multiple = :multiple limit 0,1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(VPendingDelegationCache.class);
		return (VPendingDelegationCache) query.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<VPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size) {
		Session session = vPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where delegation_price <= :price and direction = 1 order by delegation_price desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(VPendingDelegationCache.class);
		
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size) {
		Session session = vPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where delegation_price >= :price and direction = 2 order by delegation_price";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(VPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public void insertCacheForDepth(List<VPendingDelegationCache> caches, long uid, BigDecimal changeAccount,
			String code) throws ServiceException {
		if (vAccountContractDao.updateAmount(uid, code, BigDecimal.ZERO, changeAccount.negate(), changeAccount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足, 请重试");
		}
		
		if (!vPendingDelegationCacheDao.insertCache(caches, code)) {
			throw new ServiceException("插入错误,请重试");
		}
	}

}
