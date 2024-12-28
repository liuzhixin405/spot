package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.util.StringUtil;

@Repository("bqMemberTransactionDao")
public class BQMemberTransactionDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberTransactionDao<T> {

	public BQMemberTransaction getMemberTransaction(String code, long fUserId, int trade_type_id, int multiple) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("f_user_id", fUserId);
		params.put("trade_type_id", trade_type_id);

		String hql = "";
		if (multiple == 0) {
			hql = "select t from BQMemberTransaction t where t.transaction_code = :code and f_user_id=:f_user_id and trade_type_id=:trade_type_id ";
		} else {
			params.put("multiple", new BigDecimal(multiple));
			hql = "select t from BQMemberTransaction t where t.transaction_code = :code and f_user_id=:f_user_id and trade_type_id=:trade_type_id and trade_multiple=:multiple ";
		}

		return (BQMemberTransaction) get(hql, params);
	}

	/** @deprecated */
	public com.btc100.btc_q.entity.BQMemberTransaction getMemberTransaction(String code, long fUserId, int trade_type_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("f_user_id", fUserId);
		params.put("trade_type_id", trade_type_id);

		String hql = "select t from BQMemberTransaction t where t.transaction_code = :code and f_user_id=:f_user_id and trade_type_id=:trade_type_id ";
		BQMemberTransaction mt = (BQMemberTransaction) get(hql, params);
		return mt;
	}
	
	public int updateTransPriceByID(long mtid, BigDecimal changePrice) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "UPDATE BQMemberTransaction SET average_price = average_price + :price WHERE mt_id=:mtid and average_price + :price> 0";
		map.put("price", changePrice);
		map.put("mtid", mtid);
		return this.updateCondition(hql, map);
	}

	public int updateMemberTransByID(long mtid, BigDecimal holdCount, BigDecimal freeze, BigDecimal newPrice, BigDecimal deposit, BigDecimal deposit2, BigDecimal fromremain, BigDecimal frozenremain,
			 BigDecimal frozencount, BigDecimal minfrozenremain, BigDecimal newcommission) {
		
		String holdCountCon = "";
		String holdCountHql = "";
		String hql = "UPDATE BQMemberTransaction SET ";
		Map<String, Object> map = new HashMap<String, Object>();
		if (newPrice.signum() > 0) {
			holdCountHql = "average_price=((hold_count+lock_count)*average_price+:total)/(hold_count+lock_count+:holdCount),hold_count=hold_count+:holdCount,";
			map.put("holdCount", holdCount);
			map.put("total", newPrice.multiply(holdCount));
			
			holdCountCon = " AND hold_count+:holdCount>=0";
			
		} else if (newPrice.signum() == 0) {
			hql = "UPDATE BQMemberTransaction SET ";

			if (holdCount.compareTo(BigDecimal.ZERO) != 0) {
				holdCountHql = "hold_count=hold_count+:holdCount,";
				holdCountCon = " AND hold_count+:holdCount>=0";
				map.put("holdCount", holdCount);
			}
			
		} else {
			return 0;
		}
		
		String freezeHql = "";
		String freezeCon = "";
		if (freeze.compareTo(BigDecimal.ZERO) != 0) {
			freezeHql = "lock_count=lock_count+:freeze,";
			freezeCon = " AND lock_count+:freeze>=0";
			map.put("freeze", freeze);
		}
		
		String depositHql = "";
		String depositCon = "";
		if (deposit.compareTo(BigDecimal.ZERO) != 0) {
			depositHql = "deposit=deposit+:deposit,";
			depositCon = " and deposit+:deposit>=0";
			map.put("deposit", deposit);
		}
		
		if (deposit2.compareTo(BigDecimal.ZERO) != 0) {
			depositHql += "deposit2=deposit2+:deposit2,";
			map.put("deposit2", deposit2);
		}
		
		String fromremainHql = "";
		String fromremainCon = "";
		if (fromremain.compareTo(BigDecimal.ZERO) != 0) {
			fromremainHql = "f_fromremain=f_fromremain+:fromremain,";
			fromremainCon = " and f_fromremain+:fromremain>=0";
			map.put("fromremain", fromremain);
		}
		
		String frozenremainHql = "";
		String frozenremainCon = "";
		if (frozenremain.compareTo(BigDecimal.ZERO) != 0) {
			frozenremainHql = "f_frozenremain=f_frozenremain+:frozenremain,";
			frozenremainCon = " and f_frozenremain+:frozenremain>=0";
			map.put("frozenremain", frozenremain);
		}
		
		String frozencountHql = "";
		String frozencountCon = "";
		if (frozencount.compareTo(BigDecimal.ZERO) != 0) {
			frozencountHql = "f_frozen_count=f_frozen_count+:frozencount,";
			frozencountCon = " and f_frozen_count+:frozencount>=0";
			map.put("frozencount", frozencount);
		}
		
		String minfrozenremainHql = "";
		String minfrozenremainCon = "";
		if (minfrozenremain.compareTo(BigDecimal.ZERO) != 0) {
			minfrozenremainHql = "f_min_frozenremain=f_min_frozenremain+:minfrozenremain,";
			minfrozenremainCon = " and f_min_frozenremain+:minfrozenremain>=0";
			map.put("minfrozenremain", minfrozenremain);
		}
		
		String newcommissionHql = "";
		if (newcommission.compareTo(BigDecimal.ZERO) != 0) {
			newcommissionHql = "commission=commission+:newcommission,";
			map.put("newcommission", newcommission);
		}
		
		String updateHql = holdCountHql + freezeHql + depositHql + fromremainHql + frozenremainHql + frozencountHql + minfrozenremainHql + newcommissionHql; 
		
		if (StringUtil.isEmpty(updateHql)) {
			return 0;
		}
		
		updateHql = updateHql.substring(0, updateHql.length() - 1);
		hql += updateHql + " WHERE mt_id=:mtid" + holdCountCon + freezeCon + depositCon + fromremainCon + frozenremainCon + frozencountCon + minfrozenremainCon;
		
		map.put("mtid", mtid);
		
		return this.updateCondition(hql, map);
	}

	public int deleteZEROTran(BQMemberTransaction trans) {
		if (trans == null) return 1;
		
		String hql = "DELETE FROM BQMemberTransaction WHERE mt_id=:mtId AND hold_count<=0 AND lock_count<=0";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mtId", trans.getMt_id());
		return this.deleteCondition(hql, map);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> createSQL(String sql) {
		Session session = super.getSession();
		return (List<Object[]>)session.createSQLQuery(sql).list();
	}

	@Override
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice, BigDecimal coefficient) {
		// avg_price - (deposit + deposit2 + f_fromremain) * (0.8) / ((hould_count + lock_count) * coefficient)
		// avg_price - ((deposit + deposit2) * 0.8 + f_fromremain)) / ((hould_count + lock_count) * coefficient)
		String buy_sesql = "update bq_member_transaction b left join bq_account_contract a ON b.f_user_id = a.f_user_id and b.transaction_code = a.f_code "
				+ " set b.flag = -1,b.f_lockmoney = a.f_btc WHERE (b.flag=0 or (b.flag != -1 and unix_timestamp()-flag_time > 60)) and b.trade_type_id=1 and b.f_user_id >1 and b.transaction_id=:transactionId"
				+ " AND b.average_price-(((b.deposit)*(:rate)+b.f_fromremain+b.f_frozenremain+b.f_min_frozenremain+b.deposit2+a.f_btc)/((b.hold_count+b.lock_count) * :coefficient)) >=:price";
		
		Session session = super.getSession();
		SQLQuery query = session.createSQLQuery(buy_sesql);
		query.setLong("transactionId", transactionId);
		query.setBigDecimal("price", indexPrice);
		query.setBigDecimal("coefficient", coefficient);
		query.setBigDecimal("rate", new BigDecimal(0.8));
		query.executeUpdate();
		
		// avg_price + (deposit + deposit2 + f_fromremain) * (0.8) / ((hould_count + lock_count) * coefficient)
		String sell_sesql = "update bq_member_transaction b left join bq_account_contract a ON b.f_user_id = a.f_user_id and b.transaction_code = a.f_code "
				+ " set b.flag = -1,b.f_lockmoney = a.f_btc WHERE (b.flag=0 or (b.flag != -1 and unix_timestamp()-flag_time > 60)) and b.trade_type_id=2 and b.f_user_id >1 and b.transaction_id=:transactionId"
				+ " AND b.average_price+(((b.deposit)*(:rate)+b.f_fromremain+b.f_frozenremain+b.f_min_frozenremain+b.deposit2+a.f_btc)/((b.hold_count+b.lock_count) * :coefficient)) <=:price";
		SQLQuery query1 = session.createSQLQuery(sell_sesql);
		query1.setLong("transactionId", transactionId);
		query1.setBigDecimal("price", indexPrice);
		query1.setBigDecimal("coefficient", coefficient);
		query1.setBigDecimal("rate", new BigDecimal(0.8));
		query1.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BQMemberTransaction> findBCTransactions(long transactionId) {
		getSession().flush();
		getSession().clear();
		String hql = "FROM BQMemberTransaction b WHERE b.transaction_id=" + transactionId + " and b.flag=-1";
		return (List<BQMemberTransaction>) find(hql);
	}

	@Override
	public BQMemberTransaction getMemberTransactionById(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mtId", id);
		String hql = "select t from BQMemberTransaction t where t.mt_id = :mtId";
		return (BQMemberTransaction) get(hql, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BQMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType, BigDecimal price) {
		// 多单
		String hql = "";
		if (tradeType == Contents.TRADE_TYPE_BUY) {
			// (price - avg_price) * amount / deposit
			hql = "FROM BQMemberTransaction b WHERE b.transaction_id=" + transactionId + " and b.f_user_id >1 and b.trade_type_id=" + tradeType + " and b.deposit > 0"
					+ " order by ((" + price + " - b.average_price) * (b.hold_count + b.lock_count) / (b.deposit)) desc";
			
		} else {	// 空头
			// (avg_price - price) * amount / deposit
			hql = "FROM BQMemberTransaction b WHERE b.transaction_id=" + transactionId + " and b.f_user_id >1 and b.trade_type_id=" + tradeType + " and b.deposit > 0"
					+ " order by ((b.average_price - " + price + ") * (b.hold_count + b.lock_count) / (b.deposit)) desc";
		}
		
		return (List<BQMemberTransaction>) find(hql, 1, 10);
	}

	@Override
	public BigDecimal findSumDepositByUser(String code, long userId) {
		String hql = "SELECT sum(deposit+deposit2+f_fromremain+f_min_frozenremain+f_frozenremain) from BQMemberTransaction where f_user_id = :userId and transaction_code = :code";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("code", code);
		return sum(hql, map);
	}
	
	@Override
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(deposit+deposit2+f_fromremain+f_min_frozenremain+f_frozenremain) from BQMemberTransaction where transaction_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return sum(hql, map);
	}
	
	@Override
	public BigDecimal findSumHoldsByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(hold_count + lock_count) from BQMemberTransaction where transaction_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return sum(hql, map);
	}

	@Override
	public BigDecimal findSumRemainByUser(String code, long userId, long except) {
		Map<String, Object> map = new HashMap<String, Object>();
		String hql = "SELECT sum(f_fromremain+f_min_frozenremain+f_frozenremain) from BQMemberTransaction where transaction_code = :code";
		if (userId > 0) {
			hql += " and f_user_id = :userId";
			map.put("userId", userId);
		} else {
			hql += " and f_user_id > 1 and f_user_id <> :except";
			map.put("except", except);
		}
		
		map.put("code", code);
		return sum(hql, map);
	}
}
