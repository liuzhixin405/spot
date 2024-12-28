package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NStockSettingDao;
import com.btc100.btc_q.entity.NStockSetting;
import com.btc100.btc_q.service.NStockSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nStockSettingService")
public class NStockSettingServiceImpl implements NStockSettingService {

	@Autowired
	private NStockSettingDao<NStockSetting> nStockSettingDao;
	
	@Override
	public NStockSetting read(String partition) {
		String hql = "select * from " + partition + "_stock_setting";
		return nStockSettingDao.getBySql(hql);
	}
	
	@Override
	public NStockSetting get(String partition) {
		String hql = "select * from " + partition + "_stock_setting";
		return nStockSettingDao.getBySql(hql);
	}

	@Override
	public void add(String partition, NStockSetting setting) {
		
		Session session = nStockSettingDao.getSession();
		String sql = "insert into " + partition + "_stock_setting "
				+ "(am_begin_time,am_end_time,pm_begin_time,pm_end_time,holiday_url,holiday_url_using,transaction_id,mon_state,"
				+ "mon_trade_time,tue_state,tue_trade_time,wed_state,wed_trade_time,thu_state,thu_trade_time,fri_state,fri_trade_time,"
				+ "sat_state,sat_trade_time,sun_state,sun_trade_time) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, setting.getAm_begin_time());
		query.setInteger(1, setting.getAm_end_time());
		query.setInteger(2, setting.getPm_begin_time());
		query.setInteger(3, setting.getPm_end_time());
		query.setString(4, setting.getHoliday_url());
		query.setBoolean(5, setting.getHoliday_url_using());
		query.setLong(6, setting.getTransaction_id());
		query.setBoolean(7, setting.getMon_state());
		query.setString(8, setting.getMon_trade_time());
		query.setBoolean(9, setting.getTue_state());
		query.setString(10, setting.getTue_trade_time());
		query.setBoolean(11, setting.getWed_state());
		query.setString(12, setting.getWed_trade_time());
		query.setBoolean(13, setting.getThu_state());
		query.setString(14, setting.getThu_trade_time());
		query.setBoolean(15, setting.getFri_state());
		query.setString(16, setting.getFri_trade_time());
		query.setBoolean(17, setting.getSat_state());
		query.setString(18, setting.getSat_trade_time());
		query.setBoolean(19, setting.getSun_state());
		query.setString(20, setting.getSun_trade_time());
		
		query.executeUpdate();
	}
	
	@Override
	public void update(String partition, NStockSetting setting) {
		Session session = nStockSettingDao.getSession();
		String sql = "update " + partition + "_stock_setting set "
				+ "am_begin_time=?,am_end_time=?,pm_begin_time=?,pm_end_time=?,holiday_url=?,holiday_url_using=?,transaction_id=?,mon_state=?,"
				+ "mon_trade_time=?,tue_state=?,tue_trade_time=?,wed_state=?,wed_trade_time=?,thu_state=?,thu_trade_time=?,fri_state=?,fri_trade_time=?,"
				+ "sat_state=?,sat_trade_time=?,sun_state=?,sun_trade_time=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, setting.getAm_begin_time());
		query.setInteger(1, setting.getAm_end_time());
		query.setInteger(2, setting.getPm_begin_time());
		query.setInteger(3, setting.getPm_end_time());
		query.setString(4, setting.getHoliday_url());
		query.setBoolean(5, setting.getHoliday_url_using());
		query.setLong(6, setting.getTransaction_id());
		query.setBoolean(7, setting.getMon_state());
		query.setString(8, setting.getMon_trade_time());
		query.setBoolean(9, setting.getTue_state());
		query.setString(10, setting.getTue_trade_time());
		query.setBoolean(11, setting.getWed_state());
		query.setString(12, setting.getWed_trade_time());
		query.setBoolean(13, setting.getThu_state());
		query.setString(14, setting.getThu_trade_time());
		query.setBoolean(15, setting.getFri_state());
		query.setString(16, setting.getFri_trade_time());
		query.setBoolean(17, setting.getSat_state());
		query.setString(18, setting.getSat_trade_time());
		query.setBoolean(19, setting.getSun_state());
		query.setString(20, setting.getSun_trade_time());
		query.setInteger(21, setting.getId());
		
		query.executeUpdate();
	}

	@Override
	public PagerModel readAll(String partition) {
		String hql = "select * from " + partition + "_stock_setting";
		return nStockSettingDao.searchSql(hql);
	}

	@Override
	public NStockSetting readById(String partition, int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		String hql = "select * from " + partition + "_stock_setting where id = :id";
		return nStockSettingDao.getBySql(hql, params);
	}

	@Override
	public void deleteById(String partition, int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		nStockSettingDao.deleteConditionBySql("delete from " + partition + "_stock_setting where id = :id", params);
	}

	@Override
	public NStockSetting readByTransId(String partition, long transactionId) {
		String hql = "select * from " + partition + "_stock_setting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return nStockSettingDao.getBySql(hql, params);
	}
	
	@Override
	public NStockSetting getByTransId(String partition, long transactionId) {
		String hql = "select * from " + partition + "_stock_setting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return nStockSettingDao.getBySql(hql, params);
	}

}
