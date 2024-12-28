package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.TradeLogReconciliaDao;
import com.btc100.btc_q.entity.TradeLogReconcilia;
import com.btc100.btc_q.service.TradeLogReconciliaService;

@Service("tradeLogReconciliaService")
public class TradeLogReconciliaServiceImpl implements TradeLogReconciliaService {

	@Autowired
	private TradeLogReconciliaDao<TradeLogReconcilia> dao;
	
	@Override
	public void save(TradeLogReconcilia re) {
		dao.save(re);
	}

}
