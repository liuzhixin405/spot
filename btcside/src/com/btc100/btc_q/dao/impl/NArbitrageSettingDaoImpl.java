package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NArbitrageSettingDao;
import com.btc100.btc_q.entity.NArbitrageSetting;

@Repository("nArbitrageSettingDao")
public class NArbitrageSettingDaoImpl extends BaseDaoImpl<NArbitrageSetting> implements NArbitrageSettingDao<NArbitrageSetting> {

	@Override
	public NArbitrageSetting getArbitrageSettingByCode(String partition, String code) {
		
		final String sql = "select * from " + partition + "_arbitrage_setting where f_code = :code limit 0,1";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("code", code);
		query.addEntity(NArbitrageSetting.class);
		
		return (NArbitrageSetting) query.uniqueResult();
	}
}
