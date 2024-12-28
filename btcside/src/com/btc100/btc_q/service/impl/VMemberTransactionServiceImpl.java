package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.VAccountContractDao;
import com.btc100.btc_q.dao.VMemberTransactionDao;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.BQTransVo;

@Service("vMemberTransactionService")
public class VMemberTransactionServiceImpl implements VMemberTransactionService {

	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public List<VMemberTransaction> getVMemberTrasaction(long fUserId, String transactionCode, int directionSell) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		params.put("transactionCode", transactionCode);
		params.put("directionSell", directionSell);
		List<VMemberTransaction> bqmt = vMemberTransactionDao.find(
				" from VMemberTransaction t where f_user_id=:fUserId and trade_type_id=:directionSell and t.transaction_code=:transactionCode ", params);
		return bqmt;
	}
	
	public List<VMemberTransaction> readVMemberTrasaction(long fUserId, String transactionCode, int directionSell) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		params.put("transactionCode", transactionCode);
		params.put("directionSell", directionSell);
		List<VMemberTransaction> bqmt = vMemberTransactionDao.find(
				" from VMemberTransaction t where f_user_id=:fUserId and trade_type_id=:directionSell and t.transaction_code=:transactionCode ", params);
		return bqmt;
	}
	
	public List<VMemberTransaction> readVMemberTrasactionByUserId(long fUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", (long) fUserId);
		List<VMemberTransaction> bqmt = vMemberTransactionDao.find(
				" from VMemberTransaction t where f_user_id=:fUserId", params);
		return bqmt;
	}

	public List<VMemberTransaction> readTransactionByUserId(String vCode, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", userId);
		params.put("transactionCode", vCode);
		return vMemberTransactionDao.find(" from VMemberTransaction t where t.transaction_code = :transactionCode and f_user_id = :fUserId ", params);
	}
	
	public List<VMemberTransaction> findTransByCode(String bqCode) {
		String hql = " from VMemberTransaction t where t.transaction_code = :bqCode ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bqCode", bqCode);
		return vMemberTransactionDao.find(hql, map);
	}

	public void delMemberTransaction(VMemberTransaction memberTransaction) {
		vMemberTransactionDao.delete(memberTransaction);
	}

	public void save(VMemberTransaction bqmtDuo) {
		vMemberTransactionDao.save(bqmtDuo);
	}

	public void delAllByCode(String vCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vCode", vCode);
		vMemberTransactionDao.deleteCondition("delete VMemberTransaction where transaction_code = :vCode", params);
	}

	public double getHoldCount(String tranCode) {
		Object vols = spyMemcachedClient.get(tranCode+BTCQMemecacheKeys.V_TICKER_HOLDVOL);
		double dvols = 0;
		if (vols == null||Double.parseDouble(vols.toString())==0) {
			Session session = vMemberTransactionDao.getSession();
			StringBuffer sql = new StringBuffer("SELECT sum(hold_count+lock_count) FROM v_member_transaction WHERE transaction_code=?");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.setString(0, tranCode);
			dvols = ConvertUtil.toDouble(query.uniqueResult(), 0);
			//spyMemcachedClient.set(tranCode+BTCQMemecacheKeys.TICKER_HOLDVOL, BTCQMemecacheKeys.TIMES.TICKER_HOLDVOL, dvols);
		}

		return dvols;
	}

	public BigDecimal readDepositByUid(Long userId) {
		Session session = vMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT sum(deposit) FROM v_member_transaction WHERE f_user_id=?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, userId);
		return new BigDecimal(ConvertUtil.toDouble(query.uniqueResult(), 0.0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findHolds(String vCode, int type, BigDecimal coefficient) {
		Session session = vMemberTransactionDao.getSession();
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
		sql.append("FROM v_member_transaction a ");
		sql.append("JOIN v_account_contract d ON a.f_user_id = d.f_user_id AND a.transaction_code = d.f_code ");

		sql.append("WHERE a.f_user_id <> :userId AND a.transaction_code = :vCode AND a.trade_type_id = :type ");
		sql.append("ORDER BY count DESC");

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", Contents.BQMEMBER_ADMIN);
		query.setParameter("vCode", vCode);
		query.setParameter("type", type);
		query.setParameter("coefficient", coefficient);
		return query.list();
	}

	public VMemberTransaction get(Long id) {
		return vMemberTransactionDao.get(VMemberTransaction.class, id);
	}

	public BigDecimal findAllUserAmount(String vCode, int state, int type) {
		Session session = vMemberTransactionDao.getSession();
		String wheresql = state == 1 ? "hold_count" : "lock_count";
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(" + wheresql + "), 0) FROM v_member_transaction WHERE transaction_code=? AND trade_type_id=?");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setString(0, vCode);
		query.setInteger(1, type);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice) {
		vMemberTransactionDao.changeFlagForBC(transactionId, indexPrice);
	}
	
	@Override
	public List<VMemberTransaction> findBCTransactions(long transactionId) {
		return vMemberTransactionDao.findBCTransactions(transactionId);
	}

	@Override
	public long getUnix_TIMESTAMP() {
		return vMemberTransactionDao.getUnix_TIMESTAMP();
	}

	@Override
	public int updateMemberTransByID(long mtid, BigDecimal thisHoldCount, BigDecimal thisFreeze, BigDecimal thisPrice, BigDecimal thisDeposit, 
			BigDecimal thisFromremain, BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain, BigDecimal newcommission) {
		return vMemberTransactionDao.updateMemberTransByID(mtid, thisHoldCount, thisFreeze, thisPrice, thisDeposit, BigDecimal.ZERO, thisFromremain, thisFrozenremain, thisFrozenCount, thisMinFrozenremain, newcommission);
	}

	@Override
	public VMemberTransaction getMemberTransactionById(long id) {
		return vMemberTransactionDao.getMemberTransactionById(id);
	}

	@Override
	public List<VMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType,
			BigDecimal price) {
		
		return vMemberTransactionDao.findMaxProfitRateTransactions(transactionId, tradeType, price);
	}

	@Override
	public void delete(VMemberTransaction trans) {
		vMemberTransactionDao.delete(trans);
	}
	
	@Override
	public BQTransVo readData(VMemberTransaction memberTransaction, int dir, BigDecimal lastPrice, BigDecimal vip, int stopGL, 
			int digits, BigDecimal buy1Price, BigDecimal sell1Price) {
		
		VAccountContract account = vAccountContractDao.findByUserIDCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
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
		transVo.setQpPrice(BTCQMath.QPJ(dir, memberTransaction.getAverage_price(), memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_frozenremain())
				.add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc()).add(account.getF_lockbtc()), 
				memberTransaction.getTrade_multiple(), transVo.getAllCount(), BigDecimal.ONE, memberTransaction.getDeposit())
				.setScale(digits, BigDecimal.ROUND_DOWN));
		transVo.setDigits(digits);
		if (dir == 1) {
			transVo.setLastPrice(buy1Price);
		} else {
			transVo.setLastPrice(sell1Price);
		}

		BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
		
		BigDecimal fee = BTCQMath.getFee(memberTransaction.getLock_count().add(memberTransaction.getHold_count()), lastPrice, vip, BigDecimal.ONE);
		if (dir == Contents.DIRECTION_BUY) {
			ykdiff = lastPrice.subtract(memberTransaction.getAverage_price()).multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count()));
			if (transVo.getDeposit().compareTo(BigDecimal.ZERO) != 0) 
				ykRate = ykdiff.divide(transVo.getDeposit(), 4, BigDecimal.ROUND_DOWN);
			ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
		} else if (dir == Contents.DIRECTION_SELL) {
			ykdiff = memberTransaction.getAverage_price().subtract(lastPrice).multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count()));
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
	public VMemberTransaction getMemberTransaction(String code, long fUserId, int trade_type_id, int mutiple) {
		return vMemberTransactionDao.getMemberTransaction(code, fUserId, trade_type_id, mutiple);
	}
	
	@Override
	public VMemberTransaction readMemberTransaction(String code, long fUserId, int trade_type_id, int mutiple) {
		return vMemberTransactionDao.getMemberTransaction(code, fUserId, trade_type_id, mutiple);
	}

	@Override
	public boolean lockToTrade(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", 1);
		params.put("mt_id", id);
		int ret = vMemberTransactionDao.updateCondition("update VMemberTransaction b set b.flag=b.flag+:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and b.flag >= 0", params);
		if (ret <= 0) {
			ret = vMemberTransactionDao.updateCondition("update VMemberTransaction b set b.flag=:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and (b.flag < 0 and b.flag != -1 and unix_timestamp()-flag_time > 60)", params);
		}
		return ret > 0;
	}

	@Override
	public void unlockToTrade(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", -1);
		params.put("mt_id", id);
		vMemberTransactionDao.updateCondition("update VMemberTransaction b set b.flag=b.flag+:flag where b.mt_id=:mt_id and b.flag+:flag >= 0", params);
	}
	
	@Override
	public boolean lockToAdjustSpare(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", -2);
		params.put("mt_id", id);
		int ret = vMemberTransactionDao.updateCondition("update VMemberTransaction b set b.flag=:flag,b.flag_time=unix_timestamp() where b.mt_id=:mt_id and (b.flag = 0 or (b.flag != -1 and unix_timestamp()-flag_time > 60))", params);
		return ret > 0;
	}

	@Override
	public void unlockToAdjustSpare(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("flag", 0);
		params.put("mt_id", id);
		vMemberTransactionDao.updateCondition("update VMemberTransaction b set b.flag=:flag,b.flag_time=0 where b.mt_id=:mt_id and b.flag=-2", params);
	}
	
	@Override
	public List<VMemberTransaction> findLossOrRemain(long transactionId, BigDecimal price) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("transactionId", transactionId);
		map.put("price", price);
		map.put("zero", BigDecimal.ZERO);

		String hql = " from VMemberTransaction b where b.transaction_id = :transactionId and b.f_user_id > 1 and"
				+ " ((b.trade_type_id = 1 and ((b.average_price > :price and b.f_fromremain != (b.average_price - :price) * (b.hold_count + b.lock_count)) or (b.f_fromremain != :zero and b.average_price <= :price))) or"
				+ "  (b.trade_type_id = 2 and ((b.average_price < :price and b.f_fromremain != (:price - b.average_price) * (b.hold_count + b.lock_count)) or (b.f_fromremain != :zero and b.average_price >= :price))))";
		
		return vMemberTransactionDao.find(hql, map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VMemberTransaction> findHaveFundingFee(long transactionId) {
		String sql = "select b.* from v_member_transaction b left join v_account_contract a on a.f_user_id = b.f_user_id and a.f_code = b.transaction_code"
				+ " where b.transaction_id = :transactionId and b.f_user_id > 1 and b.deposit2 < 0 and a.f_btc > 0.0001";
		
		SQLQuery query = (SQLQuery) vMemberTransactionDao.createSQLQuery(sql);
		query.setParameter("transactionId", transactionId);
		query.addEntity(VMemberTransaction.class);
		
		return query.list();
	}

	@Override
	public void adjustRemain(long mId, long userId, String code, BigDecimal changeRemain, BigDecimal changeFrozenRemain) throws ServiceException {
		BigDecimal allChange = changeFrozenRemain.add(changeRemain);
		if (allChange.compareTo(BigDecimal.ZERO) != 0) {
			if (vAccountContractDao.updateAmount(userId, code, BigDecimal.ZERO, allChange.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
		
		if (changeRemain.compareTo(BigDecimal.ZERO) != 0 || changeFrozenRemain.compareTo(BigDecimal.ZERO) != 0) {
			if (vMemberTransactionDao.updateMemberTransByID(mId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeRemain, changeFrozenRemain, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
	}
	
	@Override
	public void adjustFundingFee(long mId, long userId, String code, BigDecimal changeAmount) throws ServiceException {
		if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
			if (vAccountContractDao.updateAmount(userId, code, BigDecimal.ZERO, changeAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
		
		if (changeAmount.compareTo(BigDecimal.ZERO) != 0) {
			if (vMemberTransactionDao.updateMemberTransByID(mId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, 
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
				throw new ServiceException("");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findLossUser(long transactionId, BigDecimal price) {
		Session session = vMemberTransactionDao.getSession();
		String sql = "select f_user_id from v_member_transaction b where b.transaction_id = :transactionId and"
				+ " ((b.trade_type_id = 1 and b.average_price > :price) or (b.trade_type_id = 2 and b.average_price < :price)) and b.f_user_id != 1 group by b.f_user_id";
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("transactionId", transactionId);
		query.setParameter("price", price);
		return query.list();
	}

	@Override
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except) {
		return vMemberTransactionDao.findSumMinDepositByUser(code, userId, except);
	}

	@Override
	public BigDecimal findSumHoldsByUser(String code, long userId, long except) {
		return vMemberTransactionDao.findSumHoldsByUser(code, userId, except);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findSumIncomeAndDeposit(String code, long except, BigDecimal price) {
		String sql = "SELECT f_user_id AS f_user_id, ifnull(SUM((? - t.average_price) * (t.hold_count + t.lock_count)), 0) AS trade_income, ifnull(SUM(t.deposit), 0) AS deposit from " 
				+ "v_member_transaction t where t.transaction_code = ? and t.trade_type_id = 1 and t.f_user_id > 1 and t.f_user_id <> ? group by f_user_id";
				
		Session session = vMemberTransactionDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, price);
		query.setString(1, code);
		query.setLong(2, except);
		
		List<Object[]> list = query.list();
		
		sql = "SELECT f_user_id AS f_user_id, ifnull(SUM((t.average_price - ?) * (t.hold_count + t.lock_count)), 0) AS trade_income, ifnull(SUM(t.deposit), 0) AS deposit from " 
				+ "v_member_transaction t where t.transaction_code = ? and t.trade_type_id = 2 and t.f_user_id > 1 and t.f_user_id <> ? group by f_user_id";
				
		SQLQuery query2 = session.createSQLQuery(sql);
		query2.setBigDecimal(0, price);
		query2.setString(1, code);
		query2.setLong(2, except);
		List<Object[]> list2 = query2.list();
		
		list.addAll(list2);
		
		return list;
	}

	@Override
	public int deleteZEROTran(long mId) {
		return vMemberTransactionDao.deleteZEROTran(mId);
	}

	@Override
	public VMemberTransaction readOneMemberTransaction(String code, long userId, BigDecimal multiple) {
		Session session = vMemberTransactionDao.getSession();
		StringBuffer sql = new StringBuffer("select * from v_member_transaction where transaction_code = :code and f_user_id = :userId and trade_multiple = :multiple limit 0,1 ");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("code", code);
		query.setParameter("userId", userId);
		query.setParameter("multiple", multiple);
		query.addEntity(VMemberTransaction.class);
		return (VMemberTransaction) query.uniqueResult();
	}

	@Override
	public List<VMemberTransaction> readMemberTransaction(String code, long userId, BigDecimal multiple) {
		StringBuffer hql = new StringBuffer(" from VMemberTransaction where transaction_code = :code and f_user_id = :userId and trade_multiple = :multiple ");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("userId", userId);
		map.put("multiple", multiple);
		return vMemberTransactionDao.find(hql.toString(), map);
	}
}
