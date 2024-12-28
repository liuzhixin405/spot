package com.btc100.btc_q.dao.impl;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBenchmarkPriceDao;

@Repository("bqBenchmarkPriceDao")
public class BQBenchmarkPriceDaoImpl<T> extends BaseDaoImpl<T> implements BQBenchmarkPriceDao<T> {

}
