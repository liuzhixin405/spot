package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VTransactionRateDao;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.service.VTransactionRateService;

@Service("vTransactionRateService")
public class VTransactionRateServiceImpl implements VTransactionRateService {

	@Autowired
	private VTransactionRateDao<VTransactionRate> vTransactionRateDao;

	public List<VTransactionRate> readList() {
		@SuppressWarnings("unchecked")
		List<VTransactionRate> list = vTransactionRateDao.createQuery("from VTransactionRate t order by t.transaction_code asc ").list();
		return list;
	}

	public VTransactionRate getByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return vTransactionRateDao.get(" from VTransactionRate t where t.transaction_code = :code ", params);
	}
	
	public VTransactionRate readByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return vTransactionRateDao.get(" from VTransactionRate t where t.transaction_code = :code ", params);
	}

	public void saveBqTransRate(VTransactionRate vTransRate) {
		vTransactionRateDao.save(vTransRate);
	}

	public void updateBqTransRate(VTransactionRate vTransRate) {
		vTransactionRateDao.update(vTransRate);
	}

	public void delBqTransRate(long parseInt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", parseInt);
		vTransactionRateDao.deleteCondition("Delete from VTransactionRate where id = :id", params);
	}

}
