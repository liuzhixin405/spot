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
import com.btc100.btc_q.dao.DeliveryAccountContractDao;
import com.btc100.btc_q.dao.DeliveryMemberTransactionDao;
import com.btc100.btc_q.dao.DeliveryContractAccountLogDao;
import com.btc100.btc_q.dao.DeliveryPendingDelegationCacheDao;
import com.btc100.btc_q.dao.DeliveryPendingLogDao;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("DeliveryPendingDelegationCacheService")
public class DeliveryPendingDelegationCacheServiceImpl implements DeliveryPendingDelegationCacheService {

	@Autowired
	private DeliveryPendingDelegationCacheDao<DeliveryPendingDelegationCache> deliveryPendingDelegationCacheDao;
	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> deliveryMemberTransactionDao;
	@Autowired
	private DeliveryPendingLogDao<DeliveryPendingLog> deliveryPendingLogDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private DeliveryAccountContractDao<DeliveryAccountContract> deliveryAccountContractDao;
	@Autowired
	private DeliveryContractAccountLogDao<DeliveryContractAccountLog> deliveryContractAccountLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	/**
	 * 开仓
	 * 
	 * @throws ServiceException
	 */
	@Override
	public long insertCacheAndChangeAccount(DeliveryPendingDelegationCache cache, BigDecimal fee, DeliveryAccountContract DeliveryAccountContract, boolean needLog, int device, String ipAddr) throws ServiceException {

		long uid = cache.getF_user_id();
		BigDecimal changeAmount = cache.getDeposit().add(fee);
		BigDecimal changeRemainNum = changeAmount.negate();
		if (deliveryAccountContractDao.updateAmount(uid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("系统繁忙, 请重试");
		}

		cache = deliveryPendingDelegationCacheDao.insertCache(cache);
		
		if (needLog) {
			DeliveryContractAccountLog log = new DeliveryContractAccountLog("多单开仓，余额转冻结", Contents.TRADE_CURRENCY_TYPE_ID, Contents.TRANS_TYPE_JY, changeRemainNum.signum(), DeliveryAccountContract
					.getF_btc(), changeRemainNum.abs(), DeliveryAccountContract.getF_btc().add(changeRemainNum), DeliveryAccountContract.getF_lockbtc(),
					DeliveryAccountContract.getF_lockbtc().add(changeAmount), bqAccountAmountLogDao.getUnix_TIMESTAMP(), uid,
					RandomUtil.getTrans_no(), cache.getDelegation_id(), cache.getTransaction_code());
	
			deliveryContractAccountLogDao.insertLog(log);
			
			DeliveryPendingLog DeliveryPendingLog = null;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				DeliveryPendingLog = new DeliveryPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_DDKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
						cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			} else {
				DeliveryPendingLog = new DeliveryPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
						Contents.CZLX_KTKC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
						cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), device, ipAddr);
			}
			deliveryPendingLogDao.insertLog(DeliveryPendingLog);
		}
		
		return cache.getDelegation_id();
	}

	public void delete(String code, Long id) {
		deliveryPendingDelegationCacheDao.deletePending(code, id);

	}

	public DeliveryPendingDelegationCache getBuy1(String code) {
		deliveryPendingDelegationCacheDao.getSession().flush();
		deliveryPendingDelegationCacheDao.getSession().clear();
		return deliveryPendingDelegationCacheDao.getBuy1(code);
	}

	public DeliveryPendingDelegationCache getSell1(String code) {
		deliveryPendingDelegationCacheDao.getSession().flush();
		deliveryPendingDelegationCacheDao.getSession().clear();
		return deliveryPendingDelegationCacheDao.getSell1(code);
	}

	public DeliveryPendingDelegationCache getBuy1(String code, long fUserId) {
		deliveryPendingDelegationCacheDao.getSession().flush();
		deliveryPendingDelegationCacheDao.getSession().clear();
		return deliveryPendingDelegationCacheDao.getBuy1(code, fUserId);
	}

	public DeliveryPendingDelegationCache getSell1(String code, long fUserId) {
		deliveryPendingDelegationCacheDao.getSession().flush();
		deliveryPendingDelegationCacheDao.getSession().clear();
		return deliveryPendingDelegationCacheDao.getSell1(code, fUserId);
	}

	// /**
	// * 平空仓
	// * @deprecated
	// */
	// public void insertPCK(String transactionCode, Map<String, Object>
	// paramMap) {
	// DeliveryPendingDelegationCacheDao.insert(transactionCode, paramMap);
	//		
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("fUserId", paramMap.get("f_user_id"));
	// params.put("transactionCode", paramMap.get("transaction_code"));
	// params.put("directionSell", Contents.DIRECTION_SELL);
	// DeliveryMemberTransaction bqmt =
	// DeliveryMemberTransactionDao.get(" from DeliveryMemberTransaction t where t.transaction_code=:transactionCode and f_user_id=:fUserId and trade_type_id=:directionSell ",
	// params);
	// //更新持仓表信息
	// bqmt.setHold_count(bqmt.getHold_count().subtract((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqmt.setLock_count(bqmt.getLock_count().add((BigDecimal)
	// paramMap.get("delegation_count")));
	// DeliveryMemberTransactionDao.save(bqmt);
	// }
	// /**
	// * 平多仓
	// * @deprecated
	// */
	// public void insertPCD(String transactionCode, Map<String, Object>
	// paramMap) {
	// DeliveryPendingDelegationCacheDao.insert(transactionCode, paramMap);
	//		
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("fUserId", paramMap.get("f_user_id"));
	// params.put("transactionCode", paramMap.get("transaction_code"));
	// params.put("directionSell", Contents.DIRECTION_BUY);
	// DeliveryMemberTransaction bqmt =
	// DeliveryMemberTransactionDao.get(" from DeliveryMemberTransaction t where t.transaction_code=:transactionCode and f_user_id=:fUserId and trade_type_id=:directionSell ",
	// params);
	// //更新持仓表信息
	// bqmt.setHold_count(bqmt.getHold_count().subtract((BigDecimal)
	// paramMap.get("delegation_count")));
	// bqmt.setLock_count(bqmt.getLock_count().add((BigDecimal)
	// paramMap.get("delegation_count")));
	// DeliveryMemberTransactionDao.save(bqmt);
	//		
	// }
	
	private int updateMemberTransaction(DeliveryPendingDelegationCache cache, long mtid, BigDecimal coefficient) throws ServiceException {
		BigDecimal minRemain = BigDecimal.ZERO;
		DeliveryMemberTransaction trans = deliveryMemberTransactionDao.get(DeliveryMemberTransaction.class, mtid);
		
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
			if (deliveryAccountContractDao.updateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("系统繁忙, 请重试");
			}
		}

		cache.setF_frozenremain(minRemain.negate());
		int flag = deliveryMemberTransactionDao.updateMemberTransByID(mtid, cache.getDelegation_count().negate(), cache.getDelegation_count(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
				changeRemain.negate(), frozenRemain, frozenCount, minRemain.negate(), BigDecimal.ZERO);
		
		return flag;
	}

	/**
	 * 平多仓
	 */
	public long insertPCD(DeliveryPendingDelegationCache cache, long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(cache, mtid, coefficient);
		if (flag > 0) {
			cache = deliveryPendingDelegationCacheDao.insertCache(cache);

			DeliveryPendingLog DeliveryPendingLog = new DeliveryPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_DDPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache.getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			deliveryPendingLogDao.insertLog(DeliveryPendingLog);
			
			return cache.getDelegation_id();
		}

		return 0;
	}

	/**
	 * 平空仓
	 * @throws ServiceException 
	 */
	public long insertPCK(DeliveryPendingDelegationCache cache, long mtid, BigDecimal coefficient, int device, String ipAddr) throws ServiceException {
		int flag = updateMemberTransaction(cache, mtid, coefficient);
		if (flag > 0) {
			cache = deliveryPendingDelegationCacheDao.insertCache(cache);

			DeliveryPendingLog DeliveryPendingLog = new DeliveryPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(),
					cache.getTransaction_code(), Contents.CZLX_KTPC, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), 
					cache .getDeal_count(), cache.getUndeal_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), 
					cache.getPending_no(), device, ipAddr);
			deliveryPendingLogDao.insertLog(DeliveryPendingLog);
		}

		return cache.getDelegation_id();
	}

	public List<DeliveryPendingDelegationCache> getAllByUserId(int direction, String code, Long uid) {
		return deliveryPendingDelegationCacheDao.queryByUserId(direction, code, uid);
	}

	@Override
	public DeliveryPendingDelegationCache findPendingById(long orderId, String bqCode) {
		return deliveryPendingDelegationCacheDao.findPendingById(orderId, bqCode);
	}
	
	@Override
	public DeliveryPendingDelegationCache readPendingByPendingNo(String pendingNo, String bqCode, long userId) {
		return deliveryPendingDelegationCacheDao.getPendingByPendingNo(pendingNo, bqCode, userId);
	}

	@Override
	public List<DeliveryPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction,
			BigDecimal price) {
		return deliveryPendingDelegationCacheDao.findPendingAtPriceByUser(uid, code, direction, price);
	}

	@Override
	public List<DeliveryPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority,
			int direction, int multiple) {
		return deliveryPendingDelegationCacheDao.findPendingByMemberTransInfo(code, userId, priority, direction, multiple);
	}

	@Override
	public DeliveryPendingDelegationCache saveCache(DeliveryPendingDelegationCache cache) throws HibernateException {
		return deliveryPendingDelegationCacheDao.insertCache(cache);
	}

	@Override
	public List<DeliveryPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple) {
		return deliveryPendingDelegationCacheDao.queryPCDelegations(direction, code, fUserId, multiple);
	}

	@Override
	public String lock(String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + code + "_" + id);
	}

	@Override
	public void unLock(String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PENDING_CACHE_LOCK + code + "_" + id, value);
	}

	@Override
	public int cancelPendingCache_one(DeliveryPendingDelegationCache pd, BigDecimal coefficient, boolean needLog) throws ServiceException {
		if (pd == null) return 0;
		
		long userid = pd.getF_user_id(); // 用户id
		String code = pd.getTransaction_code(); // 合约号
		
		pd = deliveryPendingDelegationCacheDao.findPendingById(pd.getDelegation_id(), code);
		if (pd == null) return 0;
		
		if (pd.getPriority() != Contents.PRIORITY_BC) {
			if (deliveryPendingDelegationCacheDao.deletePending(code, pd.getDelegation_id()) <= 0) {
				throw new ServiceException("系统繁忙, 请稍后重试");
			}
		}

		// 需要恢复的保证金
		BigDecimal deposit = pd.getUndeal_count().multiply(pd.getDelegation_price()).multiply(coefficient).divide(new BigDecimal(pd.getMultiple()), 8, BigDecimal.ROUND_DOWN);
		if (pd.getPriority() == Contents.PRIORITY_KC) {
			// 恢复冻结资金
			BigDecimal fee = pd.getUndeal_count().multiply(coefficient).multiply(pd.getDelegation_price()).multiply(pd.getCommission_rate()).setScale(8, BigDecimal.ROUND_DOWN);
			BigDecimal changeAmount = deposit.add(fee);
			if (deliveryAccountContractDao.updateAmount(pd.getF_user_id(), code, BigDecimal.ZERO, changeAmount, changeAmount.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("cancelPending_one error, 删除挂单失败！");
			}
			
			if (needLog) {
				DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(pd.getF_user_id(), code);
				DeliveryContractAccountLog accountlog = new DeliveryContractAccountLog("开仓(cache)撤销，冻结转余额", Contents.TRADE_CURRENCY_TYPE_ID, Contents.TRANS_TYPE_JY, changeAmount.signum(),
						accountContract.getF_btc().subtract(changeAmount), changeAmount, accountContract.getF_btc(), accountContract.getF_lockbtc().add(changeAmount), 
						accountContract.getF_lockbtc(), deliveryAccountContractDao.getUnix_TIMESTAMP(), pd.getF_user_id(), RandomUtil.getTrans_no(), pd.getDelegation_id(), accountContract.getF_code());
				deliveryContractAccountLogDao.insertLog(accountlog);
			
			
				deliveryPendingLogDao.updateActionByPendingNo(pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
		} else if (pd.getPriority() == Contents.PRIORITY_PC) {

			int type = pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			DeliveryMemberTransaction manyPosition = deliveryMemberTransactionDao.getMemberTransaction(code, userid, type, pd.getMultiple());

			if (needLog) {
				deliveryPendingLogDao.updateActionByPendingNo(pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
			
			BigDecimal chagneRemain = pd.getF_frozenremain();
			BigDecimal changeFrozenCount = BigDecimal.ZERO;
			if (chagneRemain.compareTo(BigDecimal.ZERO) > 0) {
				changeFrozenCount = pd.getUndeal_count().negate();
			}
			if (deliveryMemberTransactionDao.updateMemberTransByID(manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, changeFrozenCount, chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("持仓冻结异常! ");
			}
		}

		return 1;
	}

	@Override
	public BigDecimal getSumByUIDAction(String tranCode, long uid, int direction, int priority) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + tranCode + "_pending_delegation_cache WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal readSumByUIDAction(String tranCode, long uid, int direction, int priority) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + tranCode + "_pending_delegation_cache WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, int priority, int direction, long statrtTime,
			long endTime, int start, int size, boolean sort) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where f_user_id = :userId";
		
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
		
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list(); 
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, int priority, long statrtTime,
			long endTime, int size, boolean sort) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where f_user_id = :userId";
		
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
		
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list(); 
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DeliveryPendingDelegationCache> findBuyBelowPrice(String code, BigDecimal price, int size) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where delegation_price <= :price and direction = 1 order by delegation_price desc";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DeliveryPendingDelegationCache> findWithSize(String code, int size) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DeliveryPendingDelegationCache> findSellAbovePrice(String code, BigDecimal price, int size) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where delegation_price >= :price and direction = 2 order by delegation_price";
		
		if (size > 0) {
			sql += " limit " + size;
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", price);
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingDelegationCache> findPendingByUserIdAndTime(String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryPendingDelegationCache> readPendingByUserIdAndTime(String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select * from " + code + "_pending_delegation_cache where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(DeliveryPendingDelegationCache.class);
		
		return query.list();
	}

	@Override
	public Long getUnix_TIMESTAMP() {
		return deliveryPendingDelegationCacheDao.getUnix_TIMESTAMP();
	}

	@Override
	public PagerModel readPagerModel(int direct, Long uid, String semail, String scode) {
		return deliveryPendingDelegationCacheDao.getPagerModel(direct, uid, semail, scode);
	}

	@Override
	public DeliveryPendingDelegationCache readOnePending(String bqCode, long userId, int multiple) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "SELECT * FROM " + bqCode + "_pending_delegation_cache WHERE f_user_id = :userId and multiple = :multiple limit 0,1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(DeliveryPendingDelegationCache.class);
		return (DeliveryPendingDelegationCache) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findUsers(String code, int n) {
		Session session = deliveryPendingDelegationCacheDao.getSession();
		String sql = "select f_user_id from " + code + "_pending_delegation_cache group by f_user_id limit 0," + n;
		
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}

	@Override
	public void insertCacheForDepth(List<DeliveryPendingDelegationCache> caches, long uid, BigDecimal changeAccount,
			String code) throws ServiceException {
		if (deliveryAccountContractDao.updateAmount(uid, code, BigDecimal.ZERO, changeAccount.negate(), changeAccount, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("余额不足, 请重试");
		}
		
		if (!deliveryPendingDelegationCacheDao.insertCache(caches, code)) {
			throw new ServiceException("插入错误,请重试");
		}
		
	}
	
	
}
