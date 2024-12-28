package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryTransactionRateDao;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.service.DeliveryTransactionRateService;

@Service("deliveryTransactionRateService")
public class DeliveryTransactionRateServiceImpl implements DeliveryTransactionRateService {

	@Autowired
	private DeliveryTransactionRateDao<DeliveryTransactionRate> deliveryTransactionRateDao;

	public List<DeliveryTransactionRate> readList() {
		@SuppressWarnings("unchecked")
		List<DeliveryTransactionRate> list = deliveryTransactionRateDao.createQuery("from DeliveryTransactionRate t order by t.transaction_code asc ").list();
		return list;
	}

	public DeliveryTransactionRate readByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return deliveryTransactionRateDao.get(" from DeliveryTransactionRate t where t.transaction_code = :code ", params);
	}
	
	public DeliveryTransactionRate getByCode(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", transactionCode);
		return deliveryTransactionRateDao.get(" from DeliveryTransactionRate t where t.transaction_code = :code ", params);
	}

	public List<DeliveryTransactionRate> readAll() {
		return deliveryTransactionRateDao.find("From DeliveryTransactionRate");
	}

	public void saveBqTransRate(DeliveryTransactionRate bqTransRate) {
		deliveryTransactionRateDao.save(bqTransRate);
	}

	public DeliveryTransactionRate readTransRateById(long id) {
		return deliveryTransactionRateDao.get(DeliveryTransactionRate.class, id);
	}

	public void updateBqTransRate(DeliveryTransactionRate bqTransRate) {
		deliveryTransactionRateDao.update(bqTransRate);
	}

	public void delBqTransRate(long parseInt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", parseInt);
		deliveryTransactionRateDao.deleteCondition("Delete from DeliveryTransactionRate where id = :id", params);
	}

}
