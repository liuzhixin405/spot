package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBenchmarkPrice;
import com.btc100.btc_q.vo.PagerModel;

public interface BQBenchmarkPriceService {

	void save(BQBenchmarkPrice price);

	BQBenchmarkPrice getById(int id);

	BQBenchmarkPrice getByCode(String code);

	PagerModel find(String code);
}
