package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NTransactionFeeRateDao;
import com.btc100.btc_q.entity.NTransactionFeeRate;

@Repository("nTransactionFeeRateDao")
public class NTransactionFeeRateDaoImpl extends BaseDaoImpl<NTransactionFeeRate> implements NTransactionFeeRateDao<NTransactionFeeRate> {

	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public NTransactionFeeRate getByCodeAndLevel(String partition, String code, long levelId) {
		NTransactionFeeRate rate = spyMemcachedClient.get(partition + "_" + code + levelId);
		if (rate != null) {
			return rate;
		}
		
		String hql = "select * FROM " + partition + "_transaction_fee_rate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		rate = (NTransactionFeeRate) getBySql(hql, params);
		if (rate == null) {
			rate = new NTransactionFeeRate();
			rate.setEat_commission_rate(BigDecimal.ZERO);
			rate.setPending_commission_rate(BigDecimal.ZERO);
		}
		spyMemcachedClient.set(partition + "_" + code + levelId, BTCQMemecacheKeys.TIMES.ONE_MIN, rate);
		return rate;
	}
	
	@Override
	public NTransactionFeeRate findLevelByEatRate(String partition, String code, BigDecimal rate) {
		String hql = "select * from " + partition + "_transaction_fee_rate t where t.f_code = :code and t.eat_commission_rate = :rate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("rate", rate);
		return (NTransactionFeeRate) getBySql(hql, params);
	}

	@Override
	public NTransactionFeeRate getByCodeAndLevelNoCache(String partition, String code, long levelId) {
		String hql = "select * FROM " + partition + "_transaction_fee_rate WHERE f_code = :code AND level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("levelId", levelId);
		return (NTransactionFeeRate) getBySql(hql, params);
	}

	@Override
	public int insert(String partition, NTransactionFeeRate form) {
		Session session = getSession();
		String sql = "insert into " + partition + "_transaction_fee_rate "
				+ "(f_code,level_id,level_name,pending_commission_rate,eat_commission_rate) values (?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, form.getF_code());
		query.setLong(1, form.getLevel_id());
		query.setString(2, form.getLevel_name());
		query.setBigDecimal(3, form.getPending_commission_rate());
		query.setBigDecimal(4, form.getEat_commission_rate());
		
		return query.executeUpdate();
	}

	@Override
	public int update(String partition, NTransactionFeeRate form) {
		Session session = getSession();
		String sql = "update " + partition + "_transaction_fee_rate set "
				+ "f_code=?,level_id=?,level_name=?,pending_commission_rate=?,eat_commission_rate=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, form.getF_code());
		query.setLong(1, form.getLevel_id());
		query.setString(2, form.getLevel_name());
		query.setBigDecimal(3, form.getPending_commission_rate());
		query.setBigDecimal(4, form.getEat_commission_rate());
		query.setLong(5, form.getId());
		
		return query.executeUpdate();
	}

	@Override
	public int delete(String partition, NTransactionFeeRate form) {
		Session session = getSession();
		String sql = "delete from " + partition + "_transaction_fee_rate where id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, form.getId());
		return query.executeUpdate();
	}

}
