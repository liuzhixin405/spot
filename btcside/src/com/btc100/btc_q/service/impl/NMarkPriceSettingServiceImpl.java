package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NMarkPriceSettingDao;
import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.service.NMarkPriceSettingService;
import com.btc100.btc_q.vo.PagerModel;
	
@Service("nMarkPriceSettingService")
public class NMarkPriceSettingServiceImpl implements NMarkPriceSettingService {
	
	@Autowired
	private NMarkPriceSettingDao<NMarkPriceSetting> nMarkPriceSettingDao;

	@Override
	public void save(String partition, NMarkPriceSetting setting) {
		
		Session session = nMarkPriceSettingDao.getSession();
		String sql = "insert into " + partition + "_mark_price_setting "
				+ "(code,running,fixed,fixed_price,moving_period,open_width_filter,index_rate_filter) values (?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, setting.getCode());
		query.setBoolean(1, setting.isRunning());
		query.setBoolean(2, setting.isFixed());
		query.setBigDecimal(3, setting.getFixed_price());
		query.setInteger(4, setting.getMoving_period());
		query.setBigDecimal(5, setting.getOpen_width_filter());
		query.setBigDecimal(6, setting.getIndex_rate_filter());
		
		query.executeUpdate();
	}

	@Override
	public void update(String partition, NMarkPriceSetting setting) {
		Session session = nMarkPriceSettingDao.getSession();
		String sql = "update " + partition + "_mark_price_setting set "
				+ "code=?,running=?,fixed=?,fixed_price=?,moving_period=?,open_width_filter=?,index_rate_filter=? where id=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, setting.getCode());
		query.setBoolean(1, setting.isRunning());
		query.setBoolean(2, setting.isFixed());
		query.setBigDecimal(3, setting.getFixed_price());
		query.setInteger(4, setting.getMoving_period());
		query.setBigDecimal(5, setting.getOpen_width_filter());
		query.setBigDecimal(6, setting.getIndex_rate_filter());
		query.setLong(7, setting.getId());
		
		query.executeUpdate();
	}
	
	@Override
	public void delete(String partition, Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		nMarkPriceSettingDao.deleteConditionBySql("delete from " + partition + "_mark_price_setting where id = :id", params);
	}

	@Override
	public NMarkPriceSetting get(String partition) {
		return nMarkPriceSettingDao.getBySql("select * from " + partition + "_mark_price_setting where code = ''");
	}

	@Override
	public NMarkPriceSetting readOne(String partition) {
		return nMarkPriceSettingDao.getBySql("select * from " + partition + "_mark_price_setting where code = ''");
	}

	@Override
	public NMarkPriceSetting getByCode(String partition, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return nMarkPriceSettingDao.getBySql("select * from " + partition + "_mark_price_setting where code = :code", params);
	}

	@Override
	public NMarkPriceSetting readByCode(String partition, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return nMarkPriceSettingDao.getBySql("select * from " + partition + "_mark_price_setting where code = :code", params);
	}

	@Override
	public PagerModel readList(String partition) {
		return nMarkPriceSettingDao.searchSql("select * from " + partition + "_mark_price_setting");
	}

}
