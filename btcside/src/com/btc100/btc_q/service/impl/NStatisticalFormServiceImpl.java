package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NStatisticalFormDao;
import com.btc100.btc_q.entity.NStatisticalForm;
import com.btc100.btc_q.service.NStatisticalFormService;

@Service("nStatisticalFormService")
public class NStatisticalFormServiceImpl implements NStatisticalFormService {

	@Autowired
	private NStatisticalFormDao<NStatisticalForm> nStatisticalFormDao;

	@Override
	public void save(String partition, NStatisticalForm statisticalForm) {
		nStatisticalFormDao.insert(partition, statisticalForm);
	}

	@Override
	public List<NStatisticalForm> findStatisticalFormByTime(String partition, long time) {
		String hql = "select * From " + partition + "_statistical_form where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return nStatisticalFormDao.findBySql(hql, params);
	}

	@Override
	public NStatisticalForm getByCodeAndTime(String partition, String code, long time) {
		String hql = "select * From " + partition + "_statistical_form where code = :code and statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("time", time);

		return nStatisticalFormDao.getBySql(hql, params);
	}

}
