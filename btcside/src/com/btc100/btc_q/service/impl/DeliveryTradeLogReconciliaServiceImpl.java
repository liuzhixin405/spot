package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryTradeLogReconciliaDao;
import com.btc100.btc_q.entity.DeliveryTradeLogReconcilia;
import com.btc100.btc_q.service.DeliveryTradeLogReconciliaService;

@Service("deliveryTradeLogReconciliaService")
public class DeliveryTradeLogReconciliaServiceImpl implements DeliveryTradeLogReconciliaService {

	@Autowired
	private DeliveryTradeLogReconciliaDao<DeliveryTradeLogReconcilia> dao;
	
	@Override
	public void save(DeliveryTradeLogReconcilia re) {
		dao.save(re);
	}

}
