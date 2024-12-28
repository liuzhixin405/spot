package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQTransactionFeeRateDao;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransactionFeeRateService")
public class BQTransactionFeeRateServiceImpl implements BQTransactionFeeRateService {

	@Autowired
	private BQTransactionFeeRateDao<BQTransactionFeeRate> bqTransactionFeeRateDao;
	
	@Override
	public void save(BQTransactionFeeRate rate) {
		bqTransactionFeeRateDao.save(rate);
	}

	@Override
	public void update(BQTransactionFeeRate rate) {
		bqTransactionFeeRateDao.update(rate);
	}

	@Override
	public void delete(BQTransactionFeeRate rate) {
		bqTransactionFeeRateDao.delete(rate);
	}

	@Override
	public BQTransactionFeeRate getByCodeAndLevel(String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevel(code, levelId);
	}

	@Override
	public BQTransactionFeeRate readById(long id) {
		return bqTransactionFeeRateDao.get(BQTransactionFeeRate.class, id);
	}

	@Override
	public BQTransactionFeeRate readByCodeAndLevel(String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevel(code, levelId);
	}

	@Override
	public BQTransactionFeeRate readByCodeAndLevelNoCache(String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevelNoCache(code, levelId);
	}

	@Override
	public PagerModel readByCode(String code) {
		String hql = "FROM BQTransactionFeeRate WHERE f_code = :code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqTransactionFeeRateDao.searchHql(hql, params);
	}

}
