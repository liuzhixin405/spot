package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NTradeLogReconciliaDao;
import com.btc100.btc_q.entity.NTradeLogReconcilia;
import com.btc100.btc_q.service.NTradeLogReconciliaService;

@Service("nTradeLogReconciliaService")
public class NTradeLogReconciliaServiceImpl implements NTradeLogReconciliaService {

	@Autowired
	private NTradeLogReconciliaDao<NTradeLogReconcilia> dao;
	
	@Override
	public void save(String partition, NTradeLogReconcilia re) {
		dao.insert(partition, re);
	}

}
