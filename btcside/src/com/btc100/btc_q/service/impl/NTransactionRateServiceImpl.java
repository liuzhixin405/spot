package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NTransactionRateDao;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.service.NTransactionRateService;

@Service("nTransactionRateService")
public class NTransactionRateServiceImpl implements NTransactionRateService {

	@Autowired
	private NTransactionRateDao<NTransactionRate> bqTransactionRateDao;

	public List<NTransactionRate> readList(String partition) {
		@SuppressWarnings("unchecked")
		List<NTransactionRate> list = bqTransactionRateDao.createSQLQuery("select * from " + partition + "_transaction_rate t order by t.transaction_code asc ").list();
		return list;
	}

	public NTransactionRate readByCode(String partition, String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return bqTransactionRateDao.getBySql("select * from " + partition + "_transaction_rate t where t.transaction_code = :code ", params);
	}
	
	public NTransactionRate getByCode(String partition, String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return bqTransactionRateDao.getBySql("select * from " + partition + "_transaction_rate t where t.transaction_code = :code ", params);
	}

	public List<NTransactionRate> readAll(String partition) {
		return bqTransactionRateDao.findBySql("select * from " + partition + "_transaction_rate");
	}

	public void saveBqTransRate(String partition, NTransactionRate bqTransRate) {
		bqTransactionRateDao.insert(partition, bqTransRate);
	}

	public NTransactionRate readTransRateById(String partition, long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return bqTransactionRateDao.getBySql("select * from " + partition + "_transaction_rate t where id = :id", params);
	}

	public void updateBqTransRate(String partition, NTransactionRate bqTransRate) {
		bqTransactionRateDao.update(partition, bqTransRate);
	}

	public void delBqTransRate(String partition, long parseInt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", parseInt);
		bqTransactionRateDao.deleteConditionBySql("delete from " + partition + "_transaction_rate where id = :id", params);
	}

}
