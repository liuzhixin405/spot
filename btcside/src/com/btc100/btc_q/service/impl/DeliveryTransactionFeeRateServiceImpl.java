package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryTransactionFeeRateDao;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryTransactionFeeRateService")
public class DeliveryTransactionFeeRateServiceImpl implements DeliveryTransactionFeeRateService {

	@Autowired
	private DeliveryTransactionFeeRateDao<DeliveryTransactionFeeRate> deliveryTransactionFeeRateDao;
	
	@Override
	public void save(DeliveryTransactionFeeRate rate) {
		deliveryTransactionFeeRateDao.save(rate);
	}

	@Override
	public void update(DeliveryTransactionFeeRate rate) {
		deliveryTransactionFeeRateDao.update(rate);
	}

	@Override
	public void delete(DeliveryTransactionFeeRate rate) {
		deliveryTransactionFeeRateDao.delete(rate);
	}

	@Override
	public DeliveryTransactionFeeRate getByCodeAndLevel(String code, long levelId) {
		return deliveryTransactionFeeRateDao.getByCodeAndLevel(code, levelId);
	}

	@Override
	public DeliveryTransactionFeeRate readById(long id) {
		return deliveryTransactionFeeRateDao.get(DeliveryTransactionFeeRate.class, id);
	}

	@Override
	public DeliveryTransactionFeeRate readByCodeAndLevel(String code, long levelId) {
		return deliveryTransactionFeeRateDao.getByCodeAndLevel(code, levelId);
	}

	@Override
	public DeliveryTransactionFeeRate readByCodeAndLevelNoCache(String code, long levelId) {
		return deliveryTransactionFeeRateDao.getByCodeAndLevelNoCache(code, levelId);
	}

	@Override
	public PagerModel readByCode(String code) {
		String hql = "FROM DeliveryTransactionFeeRate WHERE f_code = :code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return deliveryTransactionFeeRateDao.searchHql(hql, params);
	}

}
