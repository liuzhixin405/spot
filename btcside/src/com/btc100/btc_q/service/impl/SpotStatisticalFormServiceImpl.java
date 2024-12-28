package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.SpotStatisticalFormDao;
import com.btc100.btc_q.entity.SpotStatisticalForm;
import com.btc100.btc_q.service.SpotStatisticalFormService;

@Service("spotStatisticalFormService")
public class SpotStatisticalFormServiceImpl implements SpotStatisticalFormService {

	@Autowired
	private SpotStatisticalFormDao<SpotStatisticalForm> statisticalFormDao;

	@Override
	public void save(SpotStatisticalForm statisticalForm) {
		statisticalFormDao.save(statisticalForm);
	}

	@Override
	public List<SpotStatisticalForm> findStatisticalFormByTime(long time) {
		String hql = "From SpotStatisticalForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return statisticalFormDao.find(hql, params);
	}

}
