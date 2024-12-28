package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQStockSettingDao;
import com.btc100.btc_q.entity.BQStockSetting;
import com.btc100.btc_q.service.BQStockSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqStockSettingService")
public class BQStockSettingServiceImpl implements BQStockSettingService {

	@Autowired
	private BQStockSettingDao<BQStockSetting> bqStockSettingDao;
	
	@Override
	public BQStockSetting read() {
		String hql = "from BQStockSetting";
		List<BQStockSetting> list = bqStockSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}
	
	@Override
	public BQStockSetting get() {
		String hql = "from BQStockSetting";
		List<BQStockSetting> list = bqStockSettingDao.find(hql);
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void add(BQStockSetting o) {
		bqStockSettingDao.save(o);
	}
	
	@Override
	public void update(BQStockSetting o) {
		bqStockSettingDao.update(o);
	}

	@Override
	public PagerModel readAll() {
		String hql = "from BQStockSetting";
		return bqStockSettingDao.searchHql(hql);
	}

	@Override
	public BQStockSetting readById(int id) {
		return bqStockSettingDao.get(BQStockSetting.class, id);
	}

	@Override
	public void deleteById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqStockSettingDao.deleteCondition("delete from BQStockSetting where id = :id", params);
	}

	@Override
	public BQStockSetting readByTransId(long transactionId) {
		String hql = "from BQStockSetting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return bqStockSettingDao.get(hql, params);
	}
	
	@Override
	public BQStockSetting getByTransId(long transactionId) {
		String hql = "from BQStockSetting where transaction_id = :transactionId";
		Map<String, Object> params = new HashMap<String, Object>();
 		params.put("transactionId", transactionId);
 		return bqStockSettingDao.get(hql, params);
	}

}
