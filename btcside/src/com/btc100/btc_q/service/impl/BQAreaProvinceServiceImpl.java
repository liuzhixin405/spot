package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQAreaProvinceDao;
import com.btc100.btc_q.entity.BQAreaProvince;
import com.btc100.btc_q.service.BQAreaProvinceService;


@Service("bqAreaProvinceService")

public class BQAreaProvinceServiceImpl implements BQAreaProvinceService { 

	@Autowired
	private BQAreaProvinceDao<BQAreaProvince> bqAreaProvinceDao;
	
	
	@SuppressWarnings("unchecked")
	public List<BQAreaProvince> readProvice(String countryCode) {
		Session session=bqAreaProvinceDao.getSession();
		String sql = "select * from bq_area_province where country_code=?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, countryCode);
		query.addEntity(BQAreaProvince.class);
		return query.list();
	}
	
	
	public BQAreaProvince readProviceByCode(String provice) {
		Session session=bqAreaProvinceDao.getSession();
		String sql = "select * from bq_area_province where code=?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, provice);
		query.addEntity(BQAreaProvince.class);
		return (BQAreaProvince) query.uniqueResult();
	}
	
	
	@Override
	public BQAreaProvince readProviceByName(String provice) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", provice);
		return bqAreaProvinceDao.get("from BQAreaProvince where name=:name", params);
	}

}
