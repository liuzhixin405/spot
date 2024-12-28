package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NOverloadMatchSettingDao;
import com.btc100.btc_q.entity.NOverloadMatchSetting;

@Repository("nOverloadMatchSettingDao")
public class NOverloadMatchSettingDaoImpl extends BaseDaoImpl<NOverloadMatchSetting> implements NOverloadMatchSettingDao<NOverloadMatchSetting> {

	@Override
	public NOverloadMatchSetting getSettingByCode(String partition, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		final String hql = "select * from " + partition + "_overload_match_setting where f_code = :code";
		return (NOverloadMatchSetting) getBySql(hql, params);
	}
	
	@Override
	public int insert(String partition, NOverloadMatchSetting setting) {

		Session session = getSession();
		String sql = "insert into " + partition + "_overload_match_setting "
				+ "(f_code,f_user_id,running) values (?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, setting.getF_code());
		query.setLong(1, setting.getF_user_id());
		query.setBoolean(2, setting.getRunning());
		
		return query.executeUpdate();
	}
	
	@Override
	public int update(String partition, NOverloadMatchSetting setting) {

		Session session = getSession();
		String sql = "update " + partition + "_overload_match_setting set "
				+ "f_code=?,f_user_id=?,running=?"
				+ " where id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, setting.getF_code());
		query.setLong(1, setting.getF_user_id());
		query.setBoolean(2, setting.getRunning());
		query.setLong(3, setting.getId());
		
		return query.executeUpdate();
	}
	
}
