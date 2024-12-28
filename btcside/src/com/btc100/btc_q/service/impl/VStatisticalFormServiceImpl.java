package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VStatisticalFormDao;
import com.btc100.btc_q.entity.VStatisticalForm;
import com.btc100.btc_q.service.VStatisticalFormService;

@Service("vStatisticalFormService")
public class VStatisticalFormServiceImpl implements VStatisticalFormService {

	@Autowired
	private VStatisticalFormDao<VStatisticalForm> vStatisticalFormDao;

	@Override
	public void save(VStatisticalForm statisticalForm) {
		vStatisticalFormDao.save(statisticalForm);
	}

	@Override
	public List<VStatisticalForm> findStatisticalFormByTime(long time) {
		String hql = "From VStatisticalForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return vStatisticalFormDao.find(hql, params);
	}

	@Override
	public VStatisticalForm getByCodeAndTime(String code, long time) {
		String hql = "From VStatisticalForm where code = :code and statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("time", time);

		return vStatisticalFormDao.get(hql, params);
	}

}
