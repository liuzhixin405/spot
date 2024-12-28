package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NTradeLogIncomeReconciliaDao;
import com.btc100.btc_q.entity.NTradeLogIncomeReconcilia;
import com.btc100.btc_q.service.NTradeLogIncomeReconciliaService;

@Service("nNTradeLogIncomeReconciliaService")
public class NTradeLogIncomeReconciliaServiceImpl implements NTradeLogIncomeReconciliaService {

	@Autowired
	private NTradeLogIncomeReconciliaDao<NTradeLogIncomeReconcilia> dao;

	@Override
	public void add(String partition, NTradeLogIncomeReconcilia obj) {
		dao.insert(partition, obj);
	}

	@Override
	public void update(String partition, NTradeLogIncomeReconcilia obj) {
		dao.update(partition, obj);
	}
	
	@Override
	public NTradeLogIncomeReconcilia get(String partition) {
		String sql = "select * from " + partition + "_trade_log_income_reconcilia";
		return dao.getBySql(sql);
	}

	@Override
	public List<NTradeLogIncomeReconcilia> findAll(String partition, int limit) {
		String hql = "select * from " + partition + "_trade_log_income_reconcilia order by income desc ";
		return dao.findBySql(hql, 1, limit);
	}

	@Override
	public Long getTime() {
		return dao.getUnix_TIMESTAMP();
	}

	@Override
	public NTradeLogIncomeReconcilia getByUserId(String partition, long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		NTradeLogIncomeReconcilia re = dao.getBySql("select * from " + partition + "_trade_log_income_reconcilia where f_user_id = :userId", params);
		return re;
	}

}
