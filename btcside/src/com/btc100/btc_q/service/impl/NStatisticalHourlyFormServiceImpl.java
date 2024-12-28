package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NStatisticalHourlyFormDao;
import com.btc100.btc_q.entity.NStatisticalHourlyForm;
import com.btc100.btc_q.service.NStatisticalHourlyFormService;

@Service("nStatisticalHourlyFormService")
public class NStatisticalHourlyFormServiceImpl implements NStatisticalHourlyFormService {

	@Autowired
	private NStatisticalHourlyFormDao<NStatisticalHourlyForm> nStatisticalHourlyFormDao;

	@Override
	public void save(String partition, NStatisticalHourlyForm form) {
		nStatisticalHourlyFormDao.insert(partition, form);
	}

	@Override
	public List<NStatisticalHourlyForm> findStatisticalFormByTime(String partition, long time) {
		String hql = "select * from " + partition + "_statistical_hourly_form where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return nStatisticalHourlyFormDao.findBySql(hql, params);
	}

	@Override
	public NStatisticalHourlyForm getByCodeAndTime(String partition, String code, long time) {
		String hql = "select * from " + partition + "_statistical_hourly_form where code = :code and statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("time", time);

		return nStatisticalHourlyFormDao.getBySql(hql, params);
	}

}
