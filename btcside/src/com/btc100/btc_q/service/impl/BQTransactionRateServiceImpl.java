package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQTransactionRateDao;
import com.btc100.btc_q.entity.BQTransactionRate;
import com.btc100.btc_q.service.BQTransactionRateService;

@Service("bqTransactionRateService")
public class BQTransactionRateServiceImpl implements BQTransactionRateService {

	@Autowired
	private BQTransactionRateDao<BQTransactionRate> bqTransactionRateDao;

	public List<BQTransactionRate> readList() {
		@SuppressWarnings("unchecked")
		List<BQTransactionRate> list = bqTransactionRateDao.createQuery("from BQTransactionRate t order by t.transaction_code asc ").list();
		return list;
	}

	public BQTransactionRate readByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return bqTransactionRateDao.get(" from BQTransactionRate t where t.transaction_code = :code ", params);
	}
	
	public BQTransactionRate getByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return bqTransactionRateDao.get(" from BQTransactionRate t where t.transaction_code = :code ", params);
	}

	public List<BQTransactionRate> readAll() {
		return bqTransactionRateDao.find("From BQTransactionRate");
	}

	public void saveBqTransRate(BQTransactionRate bqTransRate) {
		bqTransactionRateDao.save(bqTransRate);
	}

	public BQTransactionRate readTransRateById(long id) {
		return bqTransactionRateDao.get(BQTransactionRate.class, id);
	}

	public void updateBqTransRate(BQTransactionRate bqTransRate) {
		bqTransactionRateDao.update(bqTransRate);
	}

	public void delBqTransRate(long parseInt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", parseInt);
		bqTransactionRateDao.deleteCondition("Delete from BQTransactionRate where id = :id", params);
	}

}
