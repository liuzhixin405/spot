package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryStatisticalHourlyFormDao;
import com.btc100.btc_q.entity.DeliveryStatisticalHourlyForm;
import com.btc100.btc_q.service.DeliveryStatisticalHourlyFormService;

@Service("deliveryStatisticalHourlyFormService")
public class DeliveryStatisticalHourlyFormServiceImpl implements DeliveryStatisticalHourlyFormService {

	@Autowired
	private DeliveryStatisticalHourlyFormDao<DeliveryStatisticalHourlyForm> deliveryStatisticalHourlyFormDao;

	@Override
	public void save(DeliveryStatisticalHourlyForm form) {
		deliveryStatisticalHourlyFormDao.save(form);
	}

	@Override
	public List<DeliveryStatisticalHourlyForm> findStatisticalFormByTime(long time) {
		String hql = "From DeliveryStatisticalHourlyForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return deliveryStatisticalHourlyFormDao.find(hql, params);
	}

}
