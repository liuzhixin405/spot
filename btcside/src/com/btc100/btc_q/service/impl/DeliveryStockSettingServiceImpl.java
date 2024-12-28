package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.DeliveryStockSettingDao;
import com.btc100.btc_q.entity.DeliveryStockSetting;
import com.btc100.btc_q.service.DeliveryStockSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryStockSettingService")
public class DeliveryStockSettingServiceImpl implements DeliveryStockSettingService {

	@Autowired
	private DeliveryStockSettingDao<DeliveryStockSetting> deliveryStockSettingDao;
	
	@Override
	public DeliveryStockSetting read() {
		String hql = "from DeliveryStockSetting";
		List<DeliveryStockSetting> list = deliveryStockSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}
	
	@Override
	public DeliveryStockSetting get() {
		String hql = "from DeliveryStockSetting";
		List<DeliveryStockSetting> list = deliveryStockSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void add(DeliveryStockSetting o) {
		deliveryStockSettingDao.save(o);
	}
	
	@Override
	public void update(DeliveryStockSetting o) {
		deliveryStockSettingDao.update(o);
	}

	@Override
	public PagerModel readAll() {
		String hql = "from DeliveryStockSetting";
		return deliveryStockSettingDao.searchHql(hql);
	}

	@Override
	public DeliveryStockSetting readById(int id) {
		return deliveryStockSettingDao.get(DeliveryStockSetting.class, id);
	}

	@Override
	public void deleteById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		deliveryStockSettingDao.deleteCondition("delete from DeliveryStockSetting where id = :id", params);
	}

	@Override
	public DeliveryStockSetting readByTransId(long transactionId) {
		String hql = "from DeliveryStockSetting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return deliveryStockSettingDao.get(hql, params);
	}
	
	@Override
	public DeliveryStockSetting getByTransId(long transactionId) {
		String hql = "from DeliveryStockSetting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return deliveryStockSettingDao.get(hql, params);
	}

}
