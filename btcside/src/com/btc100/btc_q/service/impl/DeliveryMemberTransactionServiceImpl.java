package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.DeliveryAccountContractDao;
import com.btc100.btc_q.dao.DeliveryMemberTransactionDao;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BQTransVo;

@Service("deliveryMemberTransactionService")
public class DeliveryMemberTransactionServiceImpl implements DeliveryMemberTransactionService {

	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> deliveryMemberTransactionDao;
	@Autowired
	private DeliveryAccountContractDao<DeliveryAccountContract> dAccountContractDao;

	public List<DeliveryMemberTransaction> getBQMemberTrasaction(long fUserId, String transactionCode, int directionSell) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		params.put("transactionCode", transactionCode);
		params.put("directionSell", directionSell);
		List<DeliveryMemberTransaction> bqmt = deliveryMemberTransactionDao.find(
				" from DeliveryMemberTransaction t where f_user_id=:fUserId and trade_type_id=:directionSell and t.transaction_code=:transactionCode ", params);
		return bqmt;
	}
	
	public List<DeliveryMemberTransaction> readBQMemberTrasaction(long fUserId, String transactionCode, int directionSell) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		params.put("transactionCode", transactionCode);
		params.put("directionSell", directionSell);
		List<DeliveryMemberTransaction> bqmt = deliveryMemberTransactionDao.find(
				" from DeliveryMemberTransaction t where f_user_id=:fUserId and trade_type_id=:directionSell and t.transaction_code=:transactionCode ", params);
		return bqmt;
	}
	
	public List<DeliveryMemberTransaction> readBQMemberTrasactionByUserId(long fUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		List<DeliveryMemberTransaction> bqmt = deliveryMemberTransactionDao.find(
				" from DeliveryMemberTransaction t where f_user_id=:fUserId", params);
		return bqmt;
	}

	public List<DeliveryMemberTransaction> getBQMemberTrasactionByUsers(String UsersId) {
		if (StringUtil.isEmpty(UsersId)) {
			return new ArrayList<DeliveryMemberTransaction>();
		}
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT * FROM delivery_member_transaction WHERE f_user_id in (" + UsersId + ")");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(DeliveryMemberTransaction.class);
		List<DeliveryMemberTransaction> bqmt = query.list();
		return bqmt;
	}

	public List<DeliveryMemberTransaction> getTransactionByUserId(String bqCode, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", userId);
		params.put("transactionCode", bqCode);
		return deliveryMemberTransactionDao.find(" from DeliveryMemberTransaction t where t.transaction_code = :transactionCode and f_user_id = :fUserId ", params);
	}
	
	public List<DeliveryMemberTransaction> readTransactionByUserId(String bqCode, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", userId);
		params.put("transactionCode", bqCode);
		return deliveryMemberTransactionDao.find(" from DeliveryMemberTransaction t where t.transaction_code = :transactionCode and f_user_id = :fUserId ", params);
	}

	public List<DeliveryMemberTransaction> findTransByCode(String bqCode) {
		String hql = " from DeliveryMemberTransaction t where t.transaction_code = :bqCode ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bqCode", bqCode);
		return deliveryMemberTransactionDao.find(hql, map);
	}
	
	public List<DeliveryMemberTransaction> findTransByCode(String bqCode, int size) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transactionCode", bqCode);
		List<DeliveryMemberTransaction> bqmt = deliveryMemberTransactionDao.find(
				" from DeliveryMemberTransaction t where t.transaction_code=:transactionCode AND t.f_user_id > 1 ORDER BY mt_id DESC ", params, 0, size);
		return bqmt;
	}

	public void delMemberTransaction(DeliveryMemberTransaction memberTransaction) {
		deliveryMemberTransactionDao.delete(memberTransaction);
	}

	public void save(DeliveryMemberTransaction bqmtDuo) {
		deliveryMemberTransactionDao.save(bqmtDuo);
	}

	public void delAllByCode(String bqCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bqCode", bqCode);
		deliveryMemberTransactionDao.deleteCondition("delete DeliveryMemberTransaction where transaction_code = :bqCode", params);
	}

	public double getHoldCount(String tranCode) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT sum(hold_count+lock_count) FROM delivery_member_transaction WHERE transaction_code=?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setString(0, tranCode);
		double dvols = ConvertUtil.toDouble(query.uniqueResult(), 0);

		return dvols;
	}

	public BigDecimal readDepositByUid(Long uid) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT sum(deposit) FROM delivery_member_transaction WHERE f_user_id=?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, uid);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0.0));
	}

	public List<Object[]> readDegreerisk(BigDecimal lastPrice, String bqCode, BigDecimal coefficient, int type) {
		Session session = deliveryMemberTransactionDao.getSession();
		String fxdSql = "", andSql = "", bcjSql = "";
		if (type == Contents.DIRECTION_BUY) {
			fxdSql = "((:lastPrice-a.average_price)/:coefficient)*(a.hold_count+a.lock_count)/(a.deposit) ";
			bcjSql = "a.average_price-((a.deposit)*:coefficient/(a.hold_count+a.lock_count)) ";
			andSql = "AND :lastPrice < a.average_price ";
		} else if (type == Contents.DIRECTION_SELL) {
			fxdSql = "((a.average_price-:lastPrice)/:coefficient)*(a.hold_count+a.lock_count)/(a.deposit) ";
			bcjSql = "a.average_price+((a.deposit)*:coefficient/(a.hold_count+a.lock_count)) ";
			andSql = "AND a.average_price < :lastPrice ";
		}
		StringBuffer sql = new StringBuffer("SELECT ");// ID 风险度 方向 持仓均价 爆仓价 持仓量
		// 杠杆 追加
		sql.append(fxdSql).append("fxd, a.f_user_id, a.trade_type_id, a.average_price, ");
		sql.append(bcjSql).append("bcj, (a.hold_count+a.lock_count) ccl, a.trade_multiple, b.value, d.f_btc, a.deposit ");
		sql.append("FROM delivery_member_transaction a JOIN bq_member_deposit_onoff b ON a.f_user_id = b.f_user_id ");
		sql.append("JOIN delivery_account_contract d ON a.f_user_id = d.f_user_id AND a.transaction_code = d.f_code ");
		sql.append("WHERE a.trade_type_id = :type AND a.f_user_id <> :userId AND a.transaction_code = :bqCode ");
		sql.append(andSql); // 只统计负的风险度
		sql.append("ORDER BY fxd asc");

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("lastPrice", lastPrice);
		query.setParameter("coefficient", coefficient);
		query.setParameter("type", type);
		query.setParameter("userId", Contents.BQMEMBER_ADMIN);
		query.setParameter("bqCode", bqCode);
		return query.list();
	}

	@Override
	public List<Object[]> readHolds(String bqCode, int type, BigDecimal coefficient, String orderBy) {
		Session session = deliveryMemberTransactionDao.getSession();
		String qpjSql = "";
		
		if (type == Contents.DIRECTION_BUY) {
			qpjSql = "a.average_price-"
					+ "((a.deposit+a.deposit2+a.f_frozenremain+a.f_fromremain+a.f_min_frozenremain+d.f_btc+d.f_lockbtc-0.2*(a.deposit))/((a.hold_count+a.lock_count)*:coefficient)) ";
		} else if (type == Contents.DIRECTION_SELL) {
			qpjSql = "a.average_price+"
					+ "((a.deposit+a.deposit2+a.f_frozenremain+a.f_fromremain+a.f_min_frozenremain+d.f_btc+d.f_lockbtc-0.2*(a.deposit))/((a.hold_count+a.lock_count)*:coefficient)) ";
		}
		StringBuffer sql = new StringBuffer("SELECT a.hold_count + a.lock_count count, a.f_user_id, a.trade_multiple, a.average_price, 0 as value, ");
		sql.append(qpjSql).append("bcj, d.f_btc, a.deposit ");
		sql.append("FROM delivery_member_transaction a ");
		sql.append("JOIN delivery_account_contract d ON a.f_user_id = d.f_user_id AND a.transaction_code = d.f_code ");

		sql.append("WHERE a.f_user_id <> :userId AND a.transaction_code = :bqCode AND a.trade_type_id = :type ");
		if ("ASC".equals(orderBy)) {
			sql.append("ORDER BY bcj ASC");
		} else if ("DESC".equals(orderBy)) {
			sql.append("ORDER BY bcj DESC");
		} else {
			sql.append("ORDER BY count DESC");
		}

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", Contents.BQMEMBER_ADMIN);
		query.setParameter("bqCode", bqCode);
		query.setParameter("type", type);
		query.setParameter("coefficient", coefficient);
		return query.list();
	}

	public DeliveryMemberTransaction get(Long id) {
		return deliveryMemberTransactionDao.get(DeliveryMemberTransaction.class, id);
	}
	
	public DeliveryMemberTransaction readById(Long id) {
		return deliveryMemberTransactionDao.get(DeliveryMemberTransaction.class, id);
	}

	public BigDecimal readAllUserAmount(String bqCode, int state, int type) {
		Session session = deliveryMemberTransactionDao.getSession();
		String wheresql = state == 1 ? "hold_count" : "lock_count";
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(" + wheresql + "), 0) FROM delivery_member_transaction WHERE transaction_code=? AND trade_type_id=?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setString(0, bqCode);
		query.setInteger(1, type);
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal getSZTrasactionYKByUsers(long UsersId,BigDecimal lastPrice,String scode) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(sum(("+lastPrice+"-average_price)*(hold_count+lock_count)), 0) FROM delivery_member_transaction WHERE transaction_code='"+scode+"' AND f_user_id="+UsersId+" AND trade_type_id=1");
		SQLQuery query = session.createSQLQuery(sql.toString());
		BigDecimal dcks = (BigDecimal) query.uniqueResult();

		StringBuffer sql2 = new StringBuffer("SELECT ifnull(sum((average_price-"+lastPrice+")*(hold_count+lock_count)), 0) FROM delivery_member_transaction WHERE transaction_code='"+scode+"' AND f_user_id="+UsersId+" AND trade_type_id=2");
		SQLQuery query2 = session.createSQLQuery(sql2.toString());
		BigDecimal kcks = (BigDecimal) query2.uniqueResult();
		
		return dcks.add(kcks);
	}
	
	@Override
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice, BigDecimal coefficient) {
		deliveryMemberTransactionDao.changeFlagForBC(transactionId, indexPrice, coefficient);
	}
	
	@Override
	public List<DeliveryMemberTransaction> findBCTransactions(long transactionId) {
		return deliveryMemberTransactionDao.findBCTransactions(transactionId);
	}

	@Override
	public long getUnix_TIMESTAMP() {
		return deliveryMemberTransactionDao.getUnix_TIMESTAMP();
	}

	@Override
	public int updateMemberTransByID(long mtid, BigDecimal thisHoldCount, BigDecimal thisFreeze, BigDecimal thisPrice, BigDecimal thisDeposit, 
			BigDecimal thisFromremain, BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain, BigDecimal newcommission) {
		return deliveryMemberTransactionDao.updateMemberTransByID(mtid, thisHoldCount, thisFreeze, thisPrice, thisDeposit, BigDecimal.ZERO, thisFromremain, thisFrozenremain, thisFrozenCount, thisMinFrozenremain, newcommission);
	}

	@Override
	public DeliveryMemberTransaction getMemberTransactionById(long id) {
		return deliveryMemberTransactionDao.getMemberTransactionById(id);
	}

	@Override
	public List<DeliveryMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType,
			BigDecimal price) {
		
		return deliveryMemberTransactionDao.findMaxProfitRateTransactions(transactionId, tradeType, price);
	}

	@Override
	public void delete(DeliveryMemberTransaction trans) {
		deliveryMemberTransactionDao.delete(trans);
	}
	
	@Override
	public BQTransVo readHandleData(DeliveryMemberTransaction memberTransaction, int dir, BigDecimal lastPrice, BigDecimal vip, int stopGL, 
			int digits, BigDecimal buy1Price, BigDecimal sell1Price, BigDecimal coefficient) {
		
		DeliveryAccountContract account = dAccountContractDao.findByUserIDCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
		BQTransVo transVo = new BQTransVo();
		transVo.setStopLoss(stopGL);
		transVo.setMt_id(memberTransaction.getMt_id());
		transVo.setTrade_type_id(memberTransaction.getTrade_type_id());
		transVo.setBqCode(memberTransaction.getTransaction_code());// 合约号
		transVo.setDirect(dir == 1 ? "买多" : "卖出");// 成交类型(1:多单开仓,买多/2:空头开仓,卖空)
		transVo.setMultiple(memberTransaction.getTrade_multiple().intValue());// 杠杆
		transVo.setAllCount(memberTransaction.getHold_count().add(memberTransaction.getLock_count()).setScale(4, BigDecimal.ROUND_DOWN));// 总持仓
		transVo.setHoldCount(memberTransaction.getHold_count().setScale(4, BigDecimal.ROUND_DOWN));// 可用持仓
		transVo.setLockCount(memberTransaction.getLock_count().setScale(4, BigDecimal.ROUND_DOWN));// 冻结量
		transVo.setAveragePrice(memberTransaction.getAverage_price().setScale(digits, BigDecimal.ROUND_HALF_UP));// 合约均价
		transVo.setAccuratePrice(memberTransaction.getAverage_price());// 合约均价
		transVo.setDeposit(memberTransaction.getDeposit().setScale(4, BigDecimal.ROUND_DOWN));// 保证金
		transVo.setQpPrice(BTCQMath.QPJ(dir, memberTransaction.getAverage_price(), transVo.getDeposit().add(memberTransaction.getF_frozenremain())
				.add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_min_frozenremain()).add(memberTransaction.getDeposit2()).add(account.getF_btc()).add(account.getF_lockbtc()), 
				memberTransaction.getTrade_multiple(), transVo.getAllCount(), coefficient, memberTransaction.getDeposit())
				.setScale(digits, BigDecimal.ROUND_DOWN));
		transVo.setDigits(digits);
		if (dir == 1) {
			transVo.setLastPrice(buy1Price);
		} else {
			transVo.setLastPrice(sell1Price);
		}

		BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
		
		BigDecimal fee = BTCQMath.getFee(memberTransaction.getLock_count().add(memberTransaction.getHold_count()), lastPrice, vip, coefficient);
		if (dir == Contents.DIRECTION_BUY) {
			ykdiff = lastPrice.subtract(memberTransaction.getAverage_price()).multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count())).multiply(coefficient);
			if (transVo.getDeposit().compareTo(BigDecimal.ZERO) != 0) 
				ykRate = ykdiff.divide(transVo.getDeposit(), 4, BigDecimal.ROUND_DOWN);
			ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
		} else if (dir == Contents.DIRECTION_SELL) {
			ykdiff = memberTransaction.getAverage_price().subtract(lastPrice).multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count())).multiply(coefficient);
			if (transVo.getDeposit().compareTo(BigDecimal.ZERO) != 0) 
				ykRate = ykdiff.divide(transVo.getDeposit(), 4, BigDecimal.ROUND_DOWN);
			ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
		}
		
		transVo.setFloatingProfitLoss(ykdiff);	// 浮动盈亏
		NumberFormat percent = NumberFormat.getPercentInstance();
		percent.setMaximumFractionDigits(2);
		transVo.setProfitLossRatio(percent.format(ykRate));	// 盈亏比
		transVo.setCommission(fee.setScale(4, RoundingMode.UP));
		return transVo;
	}

	@Override
	public DeliveryMemberTransaction getMemberTransaction(String code, long fUserId, int trade_type_id, int mutiple) {
		return deliveryMemberTransactionDao.getMemberTransaction(code, fUserId, trade_type_id, mutiple);
	}
	
	@Override
	public DeliveryMemberTransaction readMemberTransaction(String code, long fUserId, int trade_type_id, int mutiple) {
		return deliveryMemberTransactionDao.getMemberTransaction(code, fUserId, trade_type_id, mutiple);
	}

	@Override
	public boolean lockToTrade(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", 1);
		params.put("mt_id", id);
		int ret = deliveryMemberTransactionDao.updateCondition("update DeliveryMemberTransaction b set b.flag=b.flag+:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and b.flag >= 0", params);
		if (ret <= 0) {
			ret = deliveryMemberTransactionDao.updateCondition("update DeliveryMemberTransaction b set b.flag=:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and (b.flag < 0 and b.flag != -1 and unix_timestamp()-flag_time > 60)", params);
		}
		return ret > 0;
	}

	@Override
	public void unlockToTrade(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", -1);
		params.put("mt_id", id);
		deliveryMemberTransactionDao.updateCondition("update DeliveryMemberTransaction b set b.flag=b.flag+:flag where b.mt_id=:mt_id and b.flag+:flag >= 0", params);
	}
	
	@Override
	public boolean lockToAdjustSpare(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", -2);
		params.put("mt_id", id);
		int ret = deliveryMemberTransactionDao.updateCondition("update DeliveryMemberTransaction b set b.flag=:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and (b.flag = 0 or (b.flag != -1 and unix_timestamp()-flag_time > 60))", params);
		return ret > 0;
	}

	@Override
	public void unlockToAdjustSpare(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", 0);
		params.put("mt_id", id);
		deliveryMemberTransactionDao.updateCondition("update DeliveryMemberTransaction b set b.flag=:flag,b.flag_time=0 where b.mt_id=:mt_id and b.flag=-2", params);
	}

	@Override
	public List<DeliveryMemberTransaction> findLossOrRemain(long transactionId, BigDecimal price, BigDecimal coefficient) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("transactionId", transactionId);
		map.put("price", price);
		map.put("coefficient", coefficient);
		map.put("zero", BigDecimal.ZERO);

		String hql = " from DeliveryMemberTransaction b where b.transaction_id = :transactionId and b.f_user_id > 1 and"
				+ " ((b.trade_type_id = 1 and ((b.average_price > :price and b.f_fromremain != (b.average_price - :price) * (b.hold_count + b.lock_count) * :coefficient) or (b.f_fromremain != :zero and b.average_price <= :price))) or"
				+ "  (b.trade_type_id = 2 and ((b.average_price < :price and b.f_fromremain != (:price - b.average_price) * (b.hold_count + b.lock_count) * :coefficient) or (b.f_fromremain != :zero and b.average_price >= :price))))";
		
		return deliveryMemberTransactionDao.find(hql, map);
	}

	@Override
	public void adjustRemain(long mId, long userId, String code, BigDecimal changeRemain, BigDecimal changeFrozenRemain) throws ServiceException {
		BigDecimal allChange = changeFrozenRemain.add(changeRemain);
		if (allChange.compareTo(BigDecimal.ZERO) != 0) {
			if (dAccountContractDao.updateAmount(userId, code, BigDecimal.ZERO, allChange.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
		
		if (changeRemain.compareTo(BigDecimal.ZERO) != 0 || changeFrozenRemain.compareTo(BigDecimal.ZERO) != 0) {
			if (deliveryMemberTransactionDao.updateMemberTransByID(mId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeRemain, changeFrozenRemain, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
	}

	@Override
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except) {
		return deliveryMemberTransactionDao.findSumMinDepositByUser(code, userId, except);
	}

	@Override
	public BigDecimal findSumHoldsByUser(String code, long userId, long except) {
		return deliveryMemberTransactionDao.findSumHoldsByUser(code, userId, except);
	}
	
	@Override
	public int deleteZEROTran(DeliveryMemberTransaction trans) {
		return deliveryMemberTransactionDao.deleteZEROTran(trans);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readSumHoldsOrderByUser(long userId) {
		Session session = deliveryMemberTransactionDao.getSession();
		String sql = "SELECT f_user_id, IFNULL(sum(deposit + f_min_frozenremain + f_frozenremain + f_fromremain), 0) AS balance FROM delivery_member_transaction where 1 = 1";
		if (userId > 0) {
			sql += " and f_user_id = :uid";
		} else {
			sql += " and f_user_id > 1 group by f_user_id";
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (userId > 0) {
			query.setLong("uid", userId);
		}
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readDepositAndFloatingPL(long userId) {
		Session session = deliveryMemberTransactionDao.getSession();
		String sql = "SELECT transaction_code, f_user_id, trade_type_id, average_price, hold_count, lock_count"
				+ ", deposit + f_min_frozenremain + f_frozenremain + f_fromremain AS balance"
				+ " FROM delivery_member_transaction where 1 = 1";
		if (userId > 0) {
			sql += " and f_user_id = :uid";
		} else {
			sql += " and f_user_id > 1";
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		if (userId > 0) {
			query.setLong("uid", userId);
		}
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findSumHold(int type) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer(
				"SELECT transaction_code, ifnull(SUM(hold_count + lock_count), 0) FROM delivery_member_transaction WHERE trade_type_id = ? GROUP BY transaction_code");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setInteger(0, type);
		return query.list();
	}
	
	@Override
	public BigDecimal getSumEliteHold(String code, int type, int eliteCount) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT SUM(sumCount) FROM (");
		sql.append(" SELECT SUM(hold_count + lock_count) AS sumCount FROM delivery_member_transaction WHERE transaction_code = :code");
		sql.append(" AND trade_type_id = :type AND f_user_id > 1 AND f_user_id <> 8670 AND f_user_id <> 8774 AND f_user_id <> 1260");
		sql.append(" GROUP BY f_user_id ORDER BY SUM(hold_count + lock_count) DESC LIMIT :eliteCount) t;");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setString("code", code);
		query.setInteger("type", type);
		query.setInteger("eliteCount", eliteCount);
		return (BigDecimal)query.uniqueResult();
	}

	@Override
	public DeliveryMemberTransaction readOneMemberTransaction(String code, long userId, BigDecimal multiple) {
		Session session = deliveryMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("select * from delivery_member_transaction where transaction_code = :code and f_user_id = :userId and trade_multiple = :multiple limit 0,1 ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("code", code);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(DeliveryMemberTransaction.class);
		return (DeliveryMemberTransaction) query.uniqueResult();
	}
	
	@Override
	public List<DeliveryMemberTransaction> readMemberTransaction(String code, long userId, BigDecimal multiple) {
		StringBuffer hql = new StringBuffer(" from DeliveryMemberTransaction where transaction_code = :code and f_user_id = :userId and trade_multiple = :multiple ");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("userId", userId);
		map.put("multiple", multiple);
		return deliveryMemberTransactionDao.find(hql.toString(), map);
	}
}
