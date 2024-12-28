package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBenchmarkPriceDao;
import com.btc100.btc_q.entity.BQBenchmarkPrice;
import com.btc100.btc_q.service.BQBenchmarkPriceService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqBenchmarkPriceService")
public class BQBenchmarkPriceServiceImpl implements BQBenchmarkPriceService {

	@Autowired
	private BQBenchmarkPriceDao<BQBenchmarkPrice> dao;

	@Override
	public void save(BQBenchmarkPrice price) {
		dao.save(price);
	}

	@Override
	public BQBenchmarkPrice getById(int id) {
		return dao.get(BQBenchmarkPrice.class, id);
	}

	@Override
	public BQBenchmarkPrice getByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return dao.get(" from BQBenchmarkPrice t where t.transaction_code = :code ", params);
	}

	@Override
	public PagerModel find(String code) {
		String hql = "from BQBenchmarkPrice t where 1 = 1";
		if (!StringUtil.isEmpty(code)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			hql += " and t.transaction_code = :code";
			return dao.searchHql(hql, params);
		}
		return dao.searchHql(hql);
	}

}
