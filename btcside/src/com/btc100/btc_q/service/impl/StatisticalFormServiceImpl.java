package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.StatisticalFormDao;
import com.btc100.btc_q.entity.StatisticalForm;
import com.btc100.btc_q.service.StatisticalFormService;

@Service("statisticalFormService")
public class StatisticalFormServiceImpl implements StatisticalFormService {

	@Autowired
	private StatisticalFormDao<StatisticalForm> statisticalFormDao;

	@Override
	public void save(StatisticalForm statisticalForm) {
		statisticalFormDao.save(statisticalForm);
	}

	@Override
	public List<StatisticalForm> findStatisticalFormByTime(long time) {
		String hql = "From StatisticalForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return statisticalFormDao.find(hql, params);
	}

}
