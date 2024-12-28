package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQMember;

@Repository("bqAccountDao")
public class BQAccountDaoImpl<T> extends BaseDaoImpl<T> implements BQAccountDao<T> {

	public int updateAmount(Long uid, int currencyId, BigDecimal amount, BigDecimal frozenAmount) {
		String hql = "UPDATE BQAccount b SET b.btc=b.btc+:btc,btc_locked=btc_locked+:lock,lastupdate=:time WHERE f_user_id=:uid"
				+ " AND currency_type_id=:currencyId AND b.btc+:btc>=0 AND btc_locked+:lock>=0";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("btc", amount);
		map.put("lock", frozenAmount);
		map.put("time", getUnix_TIMESTAMP());
		map.put("uid", uid);
		map.put("currencyId", currencyId);
		
		return this.updateCondition(hql, map);
	}

	@Override
	public com.btc100.btc_q.entity.BQAccount getByUerAndCurrencyId(long uid, int currencyId) {
		Session session = getSession();
		final String sql = "select * from  bq_account where f_user_id= ? and currency_type_id = ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, uid);
		query.setInteger(1, currencyId);
		query.addEntity(com.btc100.btc_q.entity.BQAccount.class);
		return (com.btc100.btc_q.entity.BQAccount) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertAccount(BQMember member, int currencyId) {
		BQAccount bqAccount = new BQAccount();
		bqAccount.setBtc(BigDecimal.ZERO);
		bqAccount.setBtc_locked(BigDecimal.ZERO);
		bqAccount.setF_user_id(member.getF_user_id());
		bqAccount.setF_user_email(member.getF_user_email());
		bqAccount.setLastupdate(getUnix_TIMESTAMP());
		bqAccount.setCurrency_type_id(currencyId);
		save((T) bqAccount);
	}
	
}
