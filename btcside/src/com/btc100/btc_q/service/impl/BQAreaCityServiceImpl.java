package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQAreaCityDao;
import com.btc100.btc_q.entity.BQAreaCity;
import com.btc100.btc_q.service.BQAreaCityService;


@Service("bqAreaCityService")

public class BQAreaCityServiceImpl implements BQAreaCityService { 

@Autowired
private BQAreaCityDao<BQAreaCity> bqAreaCityDao;

	@SuppressWarnings("unchecked")
	public List<BQAreaCity> readCitys(String provinceCode) {
		Session session=bqAreaCityDao.getSession();
		String sql = "select * from bq_area_city where provincecode=?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, provinceCode);
		query.addEntity(BQAreaCity.class);
		return query.list();
	}

	@Override
	public BQAreaCity readCityByName(String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		return bqAreaCityDao.get("from BQAreaCity where name=:name", params);
	}

	@Override
	public BQAreaCity readCityByCode(String code) {
		Map<String, Object> params = new HashMap<>();
		params.put("code", code);
		return bqAreaCityDao.get("from BQAreaCity where code=:code", params);
	}

}
