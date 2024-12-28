package com.btc100.btc_q.dao.impl;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberLevelBuyAmountLogDao;
import com.btc100.btc_q.util.StringUtil;
@Repository("bqMemberLevelBuyAmountLogDao")
public class BQMemberLevelBuyAmountLogDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberLevelBuyAmountLogDao<T>{

	@Override
	public BigDecimal sumAmount(long userId, int currencyId) {
		String sql = "select IFNULL(sum(b.change_amount),0) from bq_member_level_buy_amount_log b where currency_type_id = :currencyId";
		
		if (userId > 0) {
			sql += " and b.f_user_id = :uid";
		}
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("currencyId", currencyId);
		if (userId > 0) {
			query.setLong("uid", userId);
		}
		
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public int updateRecord(long id, BigDecimal locked, BigDecimal unlocked) {
		String hql = "UPDATE BQMemberLevelBuyAmountLog b";
		
		String whereStr = " where b.log_id = :id ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		
		String setStr = "";
		if (locked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.bfx_lock = b.bfx_lock + :locked";
			whereStr += " and b.bfx_lock + :locked >= 0";
			map.put("locked", locked);
		}
		
		if (unlocked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.bfx_unlock = b.bfx_unlock + :unlocked";
			whereStr += " and b.bfx_unlock + :unlocked >= 0";
			map.put("unlocked", unlocked);
		}
		
		if (StringUtil.isEmpty(setStr)) {
			return 0;
		}
		
		hql += (setStr + whereStr);
		return this.updateCondition(hql, map);
	}

}
