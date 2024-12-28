package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.TradeLogIncomeReconciliaDao;
import com.btc100.btc_q.entity.TradeLogIncomeReconcilia;
import com.btc100.btc_q.service.TradeLogIncomeReconciliaService;

@Service("tradeLogIncomeReconciliaService")
public class TradeLogIncomeReconciliaServiceImpl implements TradeLogIncomeReconciliaService {

	@Autowired
	private TradeLogIncomeReconciliaDao<TradeLogIncomeReconcilia> dao;

	@Override
	public void add(TradeLogIncomeReconcilia obj) {
		dao.save(obj);
	}

	@Override
	public void update(TradeLogIncomeReconcilia obj) {
		dao.update(obj);
	}
	
	@Override
	public TradeLogIncomeReconcilia get() {
		String hql = "from TradeLogIncomeReconcilia";
		List<TradeLogIncomeReconcilia> list = dao.find(hql, 1, 1);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public List<TradeLogIncomeReconcilia> findAll(int limit) {
		String hql = "from TradeLogIncomeReconcilia order by income desc ";
		return dao.find(hql, 1, limit);
	}

	@Override
	public Long getTime() {
		return dao.getUnix_TIMESTAMP();
	}

	@Override
	public TradeLogIncomeReconcilia getByUserId(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		TradeLogIncomeReconcilia re = dao.get("From TradeLogIncomeReconcilia where f_user_id = :userId", params);
		return re;
	}

}
