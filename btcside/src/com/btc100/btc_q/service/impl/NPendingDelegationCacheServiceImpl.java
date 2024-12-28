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
import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.dao.NPendingDelegationCacheDao;
import com.btc100.btc_q.dao.NPendingLogDao;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("nNPendingDelegationCacheService")
public class NPendingDelegationCacheServiceImpl implements NPendingDelegationCacheService {

	@Autowired
	private NPendingDelegationCacheDao<NPendingDelegationCache> pendingDelegationCacheDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private NPendingLogDao<NPendingLog> pendingLogDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private NAccountContractDao<NAccountContract> bqAccountContractDao;
	@Autowired
	private NContractAccountLogDao<NContractAccountLog> contractAccountLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	public void insertCacheForDepth(String partition, List<NPendingDelegationCache> caches, long uid,  BigDecimal changeAccount, String code) throws ServiceException {
		if (bqAccountContractDao.updateAmount(partition, uid, code, BigDecimal.ZERO, changeAccount.negate(), changeAccount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足, 请重试");
		}
		
		if (!pendingDelegationCacheDao.insertCache(partition, caches, code)) {
			throw new ServiceException("插入错误,请重试");
		}
	}

	/**
	 * 开仓
	 * 
	 * @throws ServiceException
	 */
	public long insertCacheAndChangeAccount(String partition, int currencyId, NPendingDelegationCache cache, BigDecimal fee, NAccountContract bqAccountContract, boolean needLog, int device, String ipAddr) throws ServiceException {

		long uid = cache.getF_user_id();
		BigDecimal changeAmount = cache.getDeposit().add(fee);
		BigDecimal changeRemainNum = changeAmount.negate();
		if (bqAccountContractDao.updateAmount(partition, uid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("系统繁忙, 请重试");
		}

		cache = pendingDelegationCacheDao.insertCache(partition, cache);
		
		if (needLog) {
			NContractAccountLog log = new NContractAccountLog("多单开仓，余额转冻结", currencyId, Contents.TRANS_TYPE_JY, changeRemainNum.signum(), bqAccountContract
					.getF_btc(), changeRemainNum.abs(), bqAccountContract.getF_btc().add(changeRemainNum), bqAccountContract.getF_lockbtc(),
					bqAccountContract.getF_lockbtc().add(changeAmount), bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid,
					RandomUtil.getTrans_no(), cache.getDelegation_id(), cache.getTransaction_code());
	
			contractAccountLogDao.insertLog(partition, log);
		}
		
		if (needLog || uid == 4240 || uid == 530597) {
			NPendingLog pendingLog = null;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_DDKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
						cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			} else {
				pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_KTKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
						cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			}
			pendingLogDao.insertLog(partition, pendingLog);
		}
		
		return cache.getDelegation_id();
	}

	public void delete(String partition, String code, Long id) {
		pendingDelegationCacheDao.deletePending(partition, code, id);

	}

	public NPendingDelegationCache getBuy1(String partition, String code) {
		pendingDelegationCacheDao.getSession().flush();
		pendingDelegationCacheDao.getSession().clear();
		return pendingDelegationCacheDao.getBuy1(partition, code);
	}

	public NPendingDelegationCache getSell1(String partition, String code) {
		pendingDelegationCacheDao.getSession().flush();
		pendingDelegationCacheDao.getSession().clear();
		return pendingDelegationCacheDao.getSell1(partition, code);
	}

	public NPendingDelegationCache getBuy1(String partition, String code, long fUserId) {
		pendingDelegationCacheDao.getSession().flush();
		pendingDelegationCacheDao.getSession().clear();
		return pendingDelegationCacheDao.getBuy1(partition, code, fUserId);
	}

	public NPendingDelegationCache getSell1(String partition, String code, long fUserId) {
		pendingDelegationCacheDao.getSession().flush();
		pendingDelegationCacheDao.getSession().clear();
		return pendingDelegationCacheDao.getSell1(partition, code, fUserId);
	}

	// /**
	// * 平空仓
	// * @deprecated
	// */
	// public void insertPCK(String transactionCode, Map<String, Object>
	// paramMap) {
	// pendingDelegationCacheDao.insert(transactionCode, paramMap);
	//		
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("fUserId", paramMap.get("f_user_id"));
	// params.put("transactionCode", paramMap.get("transaction_code"));
	// params.put("directionSell", Contents.DIRECTION_SELL);
	// NMemberTransaction bqmt =
	// bqMemberTransactionDao.get(" from NMemberTransaction t where t.transaction_code=:transactionCode and f_user_id=:fUserId and trade_type_id=:directionSell ",
	// params);
	// //更新持仓表信息
	// bqmt.setHold_count(bqmt.getHold_count().subtract((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqmt.setLock_count(bqmt.getLock_count().add((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqMemberTransactionDao.save(bqmt);
	// }
	// /**
	// * 平多仓
	// * @deprecated
	// */
	// public void insertPCD(String transactionCode, Map<String, Object>
	// paramMap) {
	// pendingDelegationCacheDao.insert(transactionCode, paramMap);
	//		
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("fUserId", paramMap.get("f_user_id"));
	// params.put("transactionCode", paramMap.get("transaction_code"));
	// params.put("directionSell", Contents.DIRECTION_BUY);
	// NMemberTransaction bqmt =
	// bqMemberTransactionDao.get(" from NMemberTransaction t where t.transaction_code=:transactionCode and f_user_id=:fUserId and trade_type_id=:directionSell ",
	// params);
	// //更新持仓表信息
	// bqmt.setHold_count(bqmt.getHold_count().subtract((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqmt.setLock_count(bqmt.getLock_count().add((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqMemberTransactionDao.save(bqmt);
	//		
	// }
	
	private int updateMemberTransaction(String partition, NPendingDelegationCache cache, long mtid, BigDecimal coefficient) throws ServiceException {
		BigDecimal minRemain = BigDecimal.ZERO;
		NMemberTransaction trans = bqMemberTransactionDao.getMemberTransactionById(partition, mtid);
		
		BigDecimal fee = BTCQMath.getFee(cache.getDelegation_count(), cache.getDelegation_price(), cache.getCommission_rate(), coefficient);
		BigDecimal pendingIncome = BigDecimal.ZERO;
		if (cache.getDirection() == Contents.DIRECTION_SELL) {
			pendingIncome = cache.getDelegation_count().multiply(coefficient).multiply(cache.getDelegation_price().subtract(trans.getAverage_price())).subtract(fee);
		} else {
			pendingIncome = cache.getDelegation_count().multiply(coefficient).multiply(trans.getAverage_price().subtract(cache.getDelegation_price())).subtract(fee);
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
			if (bqAccountContractDao.updateAmount(partition, cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("系统繁忙, 请重试");
			}
		}

		cache.setF_frozenremain(minRemain.negate());
		int flag = bqMemberTransactionDao.updateMemberTransByID(partition, mtid, cache.getDelegation_count().negate(), cache.getDelegation_count(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
				changeRemain.negate(), frozenRemain, frozenCount, minRemain.negate(), BigDecimal.ZERO);
		
		return flag;
	}

	/**
	 * 平多仓
	 */
	public long insertPCD(String partition, NPendingDelegationCache cache, long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(partition, cache, mtid, coefficient);
		if (flag > 0) {
			cache = pendingDelegationCacheDao.insertCache(partition, cache);

			NPendingLog pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_DDPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache.getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			pendingLogDao.insertLog(partition, pendingLog);
			
			return cache.getDelegation_id();
		}

		return 0;
	}

	/**
	 * 平空仓
	 * @throws ServiceException 
	 */
	public long insertPCK(String partition, NPendingDelegationCache cache, long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(partition, cache, mtid, coefficient);
		if (flag > 0) {
			cache = pendingDelegationCacheDao.insertCache(partition, cache);

			NPendingLog pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_KTPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache .getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			pendingLogDao.insertLog(partition, pendingLog);
		}

		return cache.getDelegation_id();
	}

	public List<NPendingDelegationCache> getAllByUserId(String partition, int direction, String code, Long uid) {
		return pendingDelegationCacheDao.queryByUserId(partition, direction, code, uid);
	}

	@Override
	public NPendingDelegationCache findPendingById(String partition, long orderId, String bqCode) {
		return pendingDelegationCacheDao.findPendingById(partition, orderId, bqCode);
	}
	
	@Override
	public NPendingDelegationCache readPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId) {
		return pendingDelegationCacheDao.getPendingByPendingNo(partition, pendingNo, bqCode, userId);
	}

	@Override
	public List<NPendingDelegationCache> findPendingAtPriceByUser(String partition, long uid, String code, int direction,
			BigDecimal price) {
		return pendingDelegationCacheDao.findPendingAtPriceByUser(partition, uid, code, direction, price);
	}

	@Override
	public List<NPendingDelegationCache> findPendingByMemberTransInfo(String partition, String code, long userId, int priority,
			int direction, int multiple) {
		return pendingDelegationCacheDao.findPendingByMemberTransInfo(partition, code, userId, priority, direction, multiple);
	}

	@Override
	public NPendingDelegationCache saveCache(String partition, NPendingDelegationCache cache) throws HibernateException {
		return pendingDelegationCacheDao.insertCache(partition, cache);
	}

	@Override
	public List<NPendingDelegationCache> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple) {
		return pendingDelegationCacheDao.queryPCDelegations(partition, direction, code, fUserId, multiple);
	}

	@Override
	public String lock(String partition, String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + partition + "_" + code + "_" + id);
	}

	@Override
	public void unLock(String partition, String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + partition + "_" + code + "_" + id, value);
	}

	@Override
	public int cancelPendingCache_one(String partition, int currencyId, NPendingDelegationCache pd, BigDecimal coefficient, boolean needLog) throws ServiceException {
		if (pd == null) return 0;
		
		long userid = pd.getF_user_id(); // 用户id
		String code = pd.getTransaction_code(); // 合约号
		
		pd = pendingDelegationCacheDao.findPendingById(partition, pd.getDelegation_id(), code);
		if (pd == null) return 0;
		
		if (pd.getPriority() != Contents.PRIORITY_BC) {
			if (pendingDelegationCacheDao.deletePending(partition, code, pd.getDelegation_id()) <= 0) {
				throw new ServiceException("系统繁忙, 请稍后重试");
			}
		}

		// 需要恢复的保证金
		BigDecimal deposit = pd.getUndeal_count().multiply(pd.getDelegation_price()).multiply(coefficient).divide(new BigDecimal(pd.getMultiple()), 8, BigDecimal.ROUND_DOWN);
		if (pd.getPriority() == Contents.PRIORITY_KC) {
			// 恢复冻结资金
			BigDecimal fee = pd.getUndeal_count().multiply(coefficient).multiply(pd.getDelegation_price()).multiply(pd.getCommission_rate()).setScale(8, BigDecimal.ROUND_DOWN);
			BigDecimal changeAmount = deposit.add(fee);
			int nRet = bqAccountContractDao.updateAmount(partition, pd.getF_user_id(), code, BigDecimal.ZERO, changeAmount, changeAmount.negate(), BigDecimal.ZERO);
			if (nRet <= 0 && userid != 4240 && userid != 530597) {
				throw new ServiceException("cancelPending_one error, 删除挂单失败！");
			}
			
			if (needLog) {
				NAccountContract accountContract = bqAccountContractDao.findByUserIDCode(partition, pd.getF_user_id(), code);
				NContractAccountLog accountlog = new NContractAccountLog("开仓(cache)撤销，冻结转余额", currencyId, Contents.TRANS_TYPE_JY, changeAmount.signum(),
						accountContract.getF_btc().subtract(changeAmount), changeAmount, accountContract.getF_btc(), accountContract.getF_lockbtc().add(changeAmount), 
						accountContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), pd.getF_user_id(), RandomUtil.getTrans_no(), pd.getDelegation_id(), accountContract.getF_code());
				contractAccountLogDao.insertLog(partition, accountlog);
			}
			
			if (needLog || userid == 4240 || userid == 530597) {
				pendingLogDao.updateActionByPendingNo(partition, pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
		} else if (pd.getPriority() == Contents.PRIORITY_PC) {

			int type = pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			NMemberTransaction manyPosition = bqMemberTransactionDao.getMemberTransaction(partition, code, userid, type, pd.getMultiple());

			if (needLog || userid == 4240 || userid == 530597) {
				pendingLogDao.updateActionByPendingNo(partition, pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
			
			BigDecimal chagneRemain = pd.getF_frozenremain();
			BigDecimal changeFrozenCount = BigDecimal.ZERO;
			if (chagneRemain.compareTo(BigDecimal.ZERO) > 0) {
				changeFrozenCount = pd.getUndeal_count().negate();
			}
			if (bqMemberTransactionDao.updateMemberTransByID(partition, manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, changeFrozenCount, chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("持仓冻结异常! ");
			}
		}

		return 1;
	}

	@Override
	public BigDecimal getSumByUIDAction(String partition, String tranCode, long uid, int direction, int priority) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + partition + "_" + tranCode + "_pending_delegation_cache WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal readSumByUIDAction(String partition, String tranCode, long uid, int direction, int priority) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + partition + "_" + tranCode + "_pending_delegation_cache WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegationCache> findPendingByUserIdAndTime(String partition, String code, long userId, int priority, int direction, long statrtTime,
			long endTime, int start, int size, boolean sort) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where f_user_id = :userId";
		
		if (priority > 0) {
			sql += " and priority = :priority";
		} else {
			sql += " and priority != 3";
		}
		
		if (direction > 0) {
			sql += " and direction = :direction";
		}
		
		if (statrtTime > 0) {
			sql += " and delegation_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and delegation_time <= :endTime";
		}
		
		if (sort) {
			sql += " order by delegation_id desc";
		}
		
		if (start > 0 && size > 0) {
			sql += " limit " + start + "," + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		if (priority > 0) {
			query.setLong("priority", priority);
		}
		
		if (direction > 0) {
			query.setLong("direction", direction);
		}
		
		if (statrtTime > 0) {
			query.setLong("startTime", statrtTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list(); 
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegationCache> readPendingByUserIdAndTime(String partition, String code, long userId, int priority, long statrtTime,
			long endTime, int size, boolean sort) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where f_user_id = :userId";
		
		if (priority > 0) {
			sql += " and priority = :priority";
		} else {
			sql += " and priority != 3";
		}
		
		if (statrtTime > 0) {
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
		if (priority > 0) {
			query.setLong("priority", priority);
		}
		
		if (statrtTime > 0) {
			query.setLong("startTime", statrtTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list(); 
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<NPendingDelegationCache> findBuyBelowPrice(String partition, String code, BigDecimal price, int size) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where delegation_price <= :price and direction = 1 order by delegation_price desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<NPendingDelegationCache> findSellAbovePrice(String partition, String code, BigDecimal price, int size) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where delegation_price >= :price and direction = 2 order by delegation_price";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegationCache> findPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegationCache> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation_cache where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public Long getUnix_TIMESTAMP() {
		return pendingDelegationCacheDao.getUnix_TIMESTAMP();
	}

	@Override
	public PagerModel readPagerModel(String partition, int direct, Long uid, String semail, String scode) {
		return pendingDelegationCacheDao.getPagerModel(partition, direct, uid, semail, scode);
	}

	@Override
	public NPendingDelegationCache readOnePending(String partition, String bqCode, long userId, int multiple) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "SELECT * FROM " + partition + "_" + bqCode + "_pending_delegation_cache WHERE f_user_id = :userId and multiple = :multiple limit 0,1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(NPendingDelegationCache.class);
		return (NPendingDelegationCache) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegationCache> readPendingByUserIdAndSymbol(String partition, String bqCode, long userId,
			String orderIds) {
		Session session = pendingDelegationCacheDao.getSession();
		String sql = "select * from " + partition + "_" + bqCode + "_pending_delegation_cache where f_user_id = :userId ";
		sql += " and pending_no in (" + orderIds + ")";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.addEntity(NPendingDelegationCache.class);
		
		return query.list();
	}
}
