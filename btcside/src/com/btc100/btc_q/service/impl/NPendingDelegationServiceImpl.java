package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.dao.NPendingDelegationDao;
import com.btc100.btc_q.dao.NPendingLogDao;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.Top5VO;

@Service("nPendingDelegationService")
public class NPendingDelegationServiceImpl implements NPendingDelegationService {

	@Autowired
	private NPendingDelegationDao<NPendingDelegation> pendingDelegationDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private NPendingLogDao<NPendingLog> pendingLogDao;
	@Autowired
	private NAccountContractDao<NAccountContract> bqAccountContractDao;
	@Autowired
	private NContractAccountLogDao<NContractAccountLog> contractAccountLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public int delete(String partition, String code, Long id) {
		return pendingDelegationDao.deletePending(partition, code, id);
	}

	public NPendingDelegation getBuy1(String partition, String code) {
		pendingDelegationDao.getSession().flush();
		pendingDelegationDao.getSession().clear();
		return pendingDelegationDao.getBuy1(partition, code);
	}

	public NPendingDelegation getSell1(String partition, String code) {
		pendingDelegationDao.getSession().flush();
		pendingDelegationDao.getSession().clear();
		return pendingDelegationDao.getSell1(partition, code);
	}

	public List<NPendingDelegation> getBuyTop(String partition, String code, BigDecimal minPrice) {
		return pendingDelegationDao.getBuyTop(partition, code, minPrice);
	}
	
	public List<NPendingDelegation> readBuyTop(String partition, String code, BigDecimal minPrice) {
		return pendingDelegationDao.getBuyTop(partition, code, minPrice);
	}
	
	public List<NPendingDelegation> getSellTop(String partition, String code, BigDecimal maxPrice) {
		return pendingDelegationDao.getSellTop(partition, code, maxPrice);
	}

	public List<NPendingDelegation> readSellTop(String partition, String code, BigDecimal maxPrice) {
		return pendingDelegationDao.getSellTop(partition, code, maxPrice);
	}
	
	public List<Top5DelegationVO> getBuyTop5(String partition, String transactionCode) {
		List<Top5VO> pdlist = pendingDelegationDao.getBuyTop5(partition, transactionCode);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			// vo = new Top5DelegationVO("买" + (i + 1), pd.getPrice(),
			// countAll);
			vo = new Top5DelegationVO("买(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), pd.getPriority(), countAll);

			resList.add(vo);
		}
		return resList;
	}

	public List<Top5DelegationVO> getSellTop5(String partition, String transactionCode) {
		List<Top5VO> pdlist = pendingDelegationDao.getSellTop5(partition, transactionCode);
		List<Top5DelegationVO> resList = new LinkedList<Top5DelegationVO>();
		BigDecimal countAll = new BigDecimal(0);
		Top5VO pd = null;
		Top5DelegationVO vo = null;
		for (int i = 0; i < pdlist.size(); i++) {
			pd = pdlist.get(i);
			countAll = countAll.add(pd.getCountNum());
			// vo = new Top5DelegationVO("卖" + (i + 1), pd.getPrice(),
			// countAll);
			vo = new Top5DelegationVO("卖(" + (i + 1) + ")", pd.getPrice(), pd.getCountNum(), pd.getPriority(), countAll);
			resList.add(vo);
		}
		Collections.sort(resList);
		return resList;
	}
	
	public List<NPendingDelegation> getAllByUserId(String partition, int direction, String transactionCode, long fUserId) {
		return pendingDelegationDao.queryByUserId(partition, direction, transactionCode, fUserId);
	}

	public int cancelPending_one(String partition, int currencyId, NPendingDelegation pd, BigDecimal coefficient, boolean needLog) throws ServiceException {
		if (pd == null) return 0;
		
		long userid = pd.getF_user_id(); // 用户id
		String code = pd.getTransaction_code(); // 合约号
		
		pd = pendingDelegationDao.findPendingById(partition, pd.getDelegation_id(), code);
		if (pd == null) return 0;
		
		if (pd.getPriority() != Contents.PRIORITY_BC) {
			if (pendingDelegationDao.deletePending(partition, code, pd.getDelegation_id()) <= 0) {
				throw new ServiceException("cancelPending_one error, 删除挂单失败！");
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
				throw new ServiceException("cancelPending_one error, 恢复冻结资金失败！");
			}
			
			if (needLog) {
				NAccountContract accountContract = bqAccountContractDao.findByUserIDCode(partition, pd.getF_user_id(), code);
				NContractAccountLog accountlog = new NContractAccountLog("开仓撤销，冻结转余额", currencyId, Contents.TRANS_TYPE_JY, changeAmount.signum(),
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

			// 恢复冻结持仓
			BigDecimal chagneRemain = pd.getF_frozenremain();
			BigDecimal changeFrozenCount = BigDecimal.ZERO;
			if (chagneRemain.compareTo(BigDecimal.ZERO) > 0) {
				changeFrozenCount = pd.getUndeal_count().negate();
			}
			if (bqMemberTransactionDao.updateMemberTransByID(partition, manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, changeFrozenCount, chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				throw new ServiceException("撤单操作，恢复冻结持仓失败: mtId: " + manyPosition.getMt_id() + ", undeal_count: " + pd.getUndeal_count() + ", changeFrozenCount: " + changeFrozenCount);
			}
		}
		
		return 1;
	}

	public BigDecimal readDelegation(String partition, Long id, String code, int i) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select ifnull(sum(undeal_count), 0) as undeal_count from " + partition + "_" + code
				+ "_pending_delegation where f_user_id=? and transaction_code=? and direction=?  and priority!=3 ";
		// String sql =
		// "select ifnull(sum(undeal_count), 0) as undeal_count from " + code
		// +
		// "_pending_delegation where f_user_id=? and transaction_code=? and direction=? group by transaction_code desc Order by transaction_code";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		query.setString(1, code);
		query.setInteger(2, i);
		BigDecimal bg = (BigDecimal) query.uniqueResult();
		if (bg == null) {
			bg = new BigDecimal(0);
		}
		return bg;
	}
	
	public BigDecimal readDelegationDeposit(String partition, Long id, String code) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select ifnull(sum(deposit), 0) as deposit from " + partition + "_" + code
				+ "_pending_delegation where f_user_id=? and transaction_code=? and priority!=3 ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		query.setString(1, code);
		BigDecimal bg = (BigDecimal) query.uniqueResult();
		if (bg == null) {
			bg = new BigDecimal(0);
		}
		return bg;
	}

	public List<Object[]> getBuyTopN(String partition, String bqCode, int n) {
		return pendingDelegationDao.getBuyTopN(partition, bqCode, n);
	}
	
	public List<Object[]> readBuyTopN(String partition, String bqCode, int n) {
		return pendingDelegationDao.getBuyTopN(partition, bqCode, n);
	}

	public List<Object[]> getSellTopN(String partition, String bqCode, int n) {
		return pendingDelegationDao.getSellTopN(partition, bqCode, n);
	}
	
	public List<Object[]> readSellTopN(String partition, String bqCode, int n) {
		return pendingDelegationDao.getSellTopN(partition, bqCode, n);
	}
	
	public PagerModel getPagerModel(String partition, int direct, Long uid, String semail, String scode) {
		return pendingDelegationDao.getPagerModel(partition, direct, uid, semail, scode);
	}
	
	public NPendingDelegation findPendingById(String partition, long orderId, String bqCode) {
		return pendingDelegationDao.findPendingById(partition, orderId, bqCode);
	}
	
	@Override
	public NPendingDelegation readPendingByPendingNo(String partition, String pendingNo, String bqCode, long userId) {
		return pendingDelegationDao.getPendingByPendingNo(partition, pendingNo, bqCode, userId);
	}

	public List<NPendingDelegation> readAllByUserId(String partition, String transactionCode, String direction, String priority, long fUserId, String orderby) {
		return pendingDelegationDao.queryByTypeUId(partition, transactionCode, direction, priority, fUserId, orderby);
	}

	public List<NPendingDelegation> getAllByUserId(String partition, List<NTransaction> transactionList, String direction, String priority, long fUserId, String orderby) {
		List<NPendingDelegation> relList = new ArrayList<NPendingDelegation>();
		for (NTransaction bqt : transactionList) {
			relList.addAll(pendingDelegationDao.queryByTypeUId(partition, bqt.getTransaction_code(), direction, priority, fUserId, orderby));
		}

		return relList;
	}

	public BigDecimal findDelegationByCode(String partition, String tranCode, String type, long yesterdayTime) {
		Session session = pendingDelegationDao.getSession();
		StringBuilder sbBuilder = new StringBuilder();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(undeal_count), 0) from " + partition + "_" + tranCode + "_pending_delegation ");
		if (type.equals("+")) {
			sbBuilder.append("where priority = 1");
		}
		if (type.equals("-")) {
			sbBuilder.append("where priority = 2");
		}
		if (type.equals("<>")) {
			sbBuilder.append("where priority = 3");
		}
		sbBuilder.append(" and delegation_time>" + yesterdayTime);
		SQLQuery query = session.createSQLQuery(sql.toString() + sbBuilder.toString());

		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal getSumByAction(String partition, String tranCode, int direction, int priority) {
		Session session = pendingDelegationDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + partition + "_" + tranCode + "_pending_delegation WHERE direction=" + direction + " AND priority=" + priority;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal getSumByUIDAction(String partition, String tranCode, long uid, int direction, int priority) {
		Session session = pendingDelegationDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + partition + "_" + tranCode + "_pending_delegation WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}
	
	public BigDecimal readSumByUIDAction(String partition, String tranCode, long uid, int direction, int priority) {
		Session session = pendingDelegationDao.getSession();
		String sql = "SELECT sum(undeal_count) FROM " + partition + "_" + tranCode + "_pending_delegation WHERE direction=" + direction + " AND priority=" + priority;
		if (uid > 0) {
			sql = sql + " and f_user_id=" + uid;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public List<NPendingDelegation> queryPCDelegations(String partition, int direction, String code, Long fUserId, int multiple) {
		return pendingDelegationDao.queryPCDelegations(partition, direction, code, fUserId, multiple);
	}

	@Override
	public int deletePending(String partition, String code, Long id) throws HibernateException {
		return pendingDelegationDao.deletePending(partition, code, id);
	}

	@Override
	public List<NPendingDelegation> findPendingBetweenPriceByUserId(String partition, long uid, String code, int direction, BigDecimal minPrice,
			BigDecimal maxPrice) {
		return pendingDelegationDao.findPendingBetweenPriceByUserId(partition, uid, code, direction, minPrice, maxPrice);
	}
	
	@Override
	public List<NPendingDelegation> findPendingAtPriceByUser(String partition, long uid, String code, int direction, BigDecimal price) {
		return pendingDelegationDao.findPendingAtPriceByUser(partition, uid, code, direction, price);
	}

	@Override
	public List<NPendingDelegation> findBCPendingOverOneMinute(String partition, String code, int direction) {
		return pendingDelegationDao.findBCPendingOverOneMinute(partition, code, direction);
	}

	@Override
	public List<NPendingDelegation> findPendingByMemberTransInfo(String partition, String code, long userId, int priority, int direction,
			int multiple) {
		return pendingDelegationDao.findPendingByMemberTransInfo(partition, code, userId, priority, direction, multiple);
	}

	@Override
	public String lock(String partition, String code, long id) {
		return spyMemcachedClient.lock(BTCQMemecacheKeys.PENDING_LOCK + partition + "_" + code + "_" + id);
	}

	@Override
	public void unLock(String partition, String code, long id, String value) {
		spyMemcachedClient.unlock(BTCQMemecacheKeys.PENDING_LOCK + partition + "_" + code + "_" + id, value);
	}

	@Override
	public String readDepth(String partition, String bqCode, int n) {
		String sell = "asks", buy = "bids";
		JSONObject json = new JSONObject();

		try {
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			lstBuyObjs = readBuyTopN(partition, bqCode, n);
			
			List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstBuyObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				array.put(ConvertUtil.toInt(obj[2], 0));
				lstBuyArray2.add(array);
			}

			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			lstSellObjs = readSellTopN(partition, bqCode, n);
			List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstSellObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				array.put(ConvertUtil.toInt(obj[2], 0));
				lstSellArray2.add(array);
			}
			json.put(buy, lstBuyArray2);
			json.put(sell, lstSellArray2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

	@Override
	public String getDepth(String partition, String bqCode, int n) {
		String sell = "asks", buy = "bids";
		JSONObject json = new JSONObject();

		try {
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			lstBuyObjs = getBuyTopN(partition, bqCode, n);
			
			List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstBuyObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				array.put(ConvertUtil.toInt(obj[2], 0));
				lstBuyArray2.add(array);
			}

			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			lstSellObjs = getSellTopN(partition, bqCode, n);
			List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
			for (Object[] obj : lstSellObjs) {
				JSONArray array = new JSONArray();
				BigDecimal price = new BigDecimal(obj[0].toString());
				BigDecimal amount = new BigDecimal(obj[1].toString());

				array.put(price);
				array.put(amount);
				array.put(ConvertUtil.toInt(obj[2], 0));
				lstSellArray2.add(array);
			}
			json.put(buy, lstBuyArray2);
			json.put(sell, lstSellArray2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegation> findPendingByUserIdAndTime(String partition, String code, long userId,  int priority, int direction, long statrtTime, long endTime,
			int start, int size, boolean sort) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation where f_user_id = :userId";
		
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
		
		if (size > 0) {
			if (start > 0) {
				sql += " limit " + start + "," + size;
			} else {
				sql += " limit " + size;
			}
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
		
		query.addEntity(NPendingDelegation.class);
		
		return query.list(); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegation> findPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(NPendingDelegation.class);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegation> readPendingByUserIdAndTime(String partition, String code, long userId, long startTime, long endTime,
			long from, String direct, int size) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + code + "_pending_delegation where f_user_id = :userId and priority != 3";
		
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
		
		query.addEntity(NPendingDelegation.class);
		
		return query.list();
	}

	@Override
	public NPendingDelegation readOnePending(String partition, String bqCode, long userId, int multiple) {
		Session session = pendingDelegationDao.getSession();
		String sql = "SELECT * FROM " + partition + "_" + bqCode + "_pending_delegation WHERE f_user_id = :userId and multiple = :multiple limit 0,1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(NPendingDelegation.class);
		return (NPendingDelegation) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPendingDelegation> readPendingByUserIdAndSymbol(String partition, String bqCode, long userId,
			String orderIds) {
		Session session = pendingDelegationDao.getSession();
		String sql = "select * from " + partition + "_" + bqCode + "_pending_delegation where f_user_id = :userId ";
		sql += " and pending_no in (" + orderIds + ")";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		query.addEntity(NPendingDelegation.class);
		
		return query.list();
	}
}
