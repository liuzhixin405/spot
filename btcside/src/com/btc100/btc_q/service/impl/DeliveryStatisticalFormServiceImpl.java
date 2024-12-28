package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryStatisticalFormDao;
import com.btc100.btc_q.entity.DeliveryStatisticalForm;
import com.btc100.btc_q.service.DeliveryStatisticalFormService;

@Service("deliveryStatisticalFormService")
public class DeliveryStatisticalFormServiceImpl implements DeliveryStatisticalFormService {

	@Autowired
	private DeliveryStatisticalFormDao<DeliveryStatisticalForm> deliveryStatisticalFormDao;

	@Override
	public void save(DeliveryStatisticalForm DeliveryStatisticalForm) {
		deliveryStatisticalFormDao.save(DeliveryStatisticalForm);
	}

	@Override
	public List<DeliveryStatisticalForm> findStatisticalFormByTime(long time) {
		String hql = "From DeliveryStatisticalForm where statistical_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("time", time);

		return deliveryStatisticalFormDao.find(hql, params);
	}

}
