package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBfxMinHoldDao;
import com.btc100.btc_q.entity.BQBfxMinHold;
import com.btc100.btc_q.service.BQBfxMinHoldService;

@Service("bqBfxMinHoldService")
public class BQBfxMinHoldServiceImpl implements BQBfxMinHoldService {

	@Autowired
	private BQBfxMinHoldDao<BQBfxMinHold> bqBfxMinHoldDao;
	
	@Override
	public BQBfxMinHold getBfxMinHoldByUserAndTime(long userId, long time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		params.put("time", time);
		
		return bqBfxMinHoldDao.get("from BQBfxMinHold where f_user_id = :uid and day_time = :time", params);
	}
	
	@Override
	public BQBfxMinHold readBfxMinHoldByUserAndTime(long userId, long time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		params.put("time", time);
		
		return bqBfxMinHoldDao.get("from BQBfxMinHold where f_user_id = :uid and day_time = :time", params);
	}
	
	@Override
	public void save(BQBfxMinHold ba) {
		bqBfxMinHoldDao.save(ba);
	}

	@Override
	public void update(BQBfxMinHold setting) {
		bqBfxMinHoldDao.update(setting);
	}

}
