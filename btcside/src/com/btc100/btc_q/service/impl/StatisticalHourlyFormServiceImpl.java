package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.StatisticalHourlyFormDao;
import com.btc100.btc_q.entity.StatisticalHourlyForm;
import com.btc100.btc_q.service.StatisticalHourlyFormService;

@Service("statisticalHourlyFormService")
public class StatisticalHourlyFormServiceImpl implements StatisticalHourlyFormService {

	@Autowired
	private StatisticalHourlyFormDao<StatisticalHourlyForm> statisticalHourlyFormDao;

	@Override
	public void save(StatisticalHourlyForm form) {
		statisticalHourlyFormDao.save(form);
	}

	@Override
	public List<StatisticalHourlyForm> findStatisticalFormByTime(long time) {
		String hql = "From StatisticalHourlyForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return statisticalHourlyFormDao.find(hql, params);
	}

}
