package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NTransactionFeeRateDao;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nTransactionFeeRateService")
public class NTransactionFeeRateServiceImpl implements NTransactionFeeRateService {

	@Autowired
	private NTransactionFeeRateDao<NTransactionFeeRate> bqTransactionFeeRateDao;
	
	@Override
	public void save(String partition, NTransactionFeeRate rate) {
		bqTransactionFeeRateDao.insert(partition, rate);
	}

	@Override
	public void update(String partition, NTransactionFeeRate rate) {
		bqTransactionFeeRateDao.update(partition, rate);
	}

	@Override
	public void delete(String partition, NTransactionFeeRate rate) {
		bqTransactionFeeRateDao.delete(partition, rate);
	}

	@Override
	public NTransactionFeeRate getByCodeAndLevel(String partition, String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevel(partition, code, levelId);
	}

	@Override
	public NTransactionFeeRate readById(String partition, long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return bqTransactionFeeRateDao.getBySql("select * from " + partition + "_transaction_fee_rate WHERE id = :id", params);
	}

	@Override
	public NTransactionFeeRate readByCodeAndLevel(String partition, String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevel(partition, code, levelId);
	}

	@Override
	public NTransactionFeeRate readByCodeAndLevelNoCache(String partition, String code, long levelId) {
		return bqTransactionFeeRateDao.getByCodeAndLevelNoCache(partition, code, levelId);
	}

	@Override
	public PagerModel readByCode(String partition, String code) {
		String hql = "select * FROM " + partition + "_transaction_fee_rate WHERE f_code = :code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqTransactionFeeRateDao.searchSql(hql, params);
	}

}
