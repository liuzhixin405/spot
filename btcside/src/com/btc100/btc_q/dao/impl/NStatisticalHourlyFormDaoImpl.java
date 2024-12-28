package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NStatisticalHourlyFormDao;
import com.btc100.btc_q.entity.NStatisticalHourlyForm;

@Repository("nStatisticalHourlyFormDao")
public class NStatisticalHourlyFormDaoImpl extends BaseDaoImpl<NStatisticalHourlyForm> implements NStatisticalHourlyFormDao<NStatisticalHourlyForm> {

	@Override
	public int insert(String partition, NStatisticalHourlyForm form) {
		Session session = getSession();
		String sql = "insert into " + partition + "_statistical_hourly_form "
				+ "(statistical_time,total_trade_user,total_fee,total_bfx_fee,total_rebate,total_bfx_rebate,code,hold_mount,"
				+ "deposit_mount,contract_balance,trade_count,trade_user,trade_fee,trade_bfx_fee,rebate,bfx_rebate) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setLong(0, form.getStatistical_time());
		query.setLong(1, form.getTotal_trade_user());
		query.setBigDecimal(2, form.getTotal_fee());
		query.setBigDecimal(3, form.getTotal_bfx_fee());
		query.setBigDecimal(4, form.getTotal_rebate());
		query.setBigDecimal(5, form.getTotal_bfx_rebate());
		query.setString(6, form.getCode());
		query.setBigDecimal(7, form.getHold_mount());
		query.setBigDecimal(8, form.getDeposit_mount());
		query.setBigDecimal(9, form.getContract_balance());
		query.setBigDecimal(10, form.getTrade_count());
		query.setLong(11, form.getTrade_user());
		query.setBigDecimal(12, form.getTrade_fee());
		query.setBigDecimal(13, form.getTrade_bfx_fee());
		query.setBigDecimal(14, form.getRebate());
		query.setBigDecimal(15, form.getBfx_rebate());
		
		return query.executeUpdate();
	}

}
