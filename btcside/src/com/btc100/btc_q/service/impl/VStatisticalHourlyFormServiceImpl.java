package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VStatisticalHourlyFormDao;
import com.btc100.btc_q.entity.VStatisticalHourlyForm;
import com.btc100.btc_q.service.VStatisticalHourlyFormService;

@Service("vStatisticalHourlyFormService")
public class VStatisticalHourlyFormServiceImpl implements VStatisticalHourlyFormService {

	@Autowired
	private VStatisticalHourlyFormDao<VStatisticalHourlyForm> vStatisticalHourlyFormDao;

	@Override
	public void save(VStatisticalHourlyForm form) {
		vStatisticalHourlyFormDao.save(form);
	}

	@Override
	public List<VStatisticalHourlyForm> findStatisticalFormByTime(long time) {
		String hql = "From VStatisticalHourlyForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return vStatisticalHourlyFormDao.find(hql, params);
	}

	@Override
	public VStatisticalHourlyForm getByCodeAndTime(String code, long time) {
		String hql = "From VStatisticalHourlyForm where code = :code and statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("time", time);

		return vStatisticalHourlyFormDao.get(hql, params);
	}

}
