package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQAreaCountryDao;
import com.btc100.btc_q.entity.BQAreaCountry;
import com.btc100.btc_q.service.BQAreaCountryService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("bqAreaCountryService")

public class BQAreaCountryServiceImpl implements BQAreaCountryService { 

@Autowired
private BQAreaCountryDao<BQAreaCountry> bqAreaCountryDao;

	@SuppressWarnings("unchecked")
	public List<BQAreaCountry> readCountry() {
		Session session=bqAreaCountryDao.getSession();
		String sql = "select * from bq_area_country ";
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(BQAreaCountry.class);
		return query.list();
	}

	@Override
	public BQAreaCountry readCountyByName(String countyName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", countyName);
		return bqAreaCountryDao.get("from BQAreaCountry where zh_name=:name", params);
	}

	@Override
	public BQAreaCountry readCountyByCode(String countyCode) {
		Map<String, Object> params = new HashMap<>();
		params.put("code", countyCode);
		return bqAreaCountryDao.get("from BQAreaCountry where code=:code", params);
	}

}
