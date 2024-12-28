package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VTradeDayStatsDao;
import com.btc100.btc_q.entity.VTradeDayStats;
import com.btc100.btc_q.service.VTradeDayStatsService;

@Service("vTradeDayStatsSrevice")
public class VTradeDayStatsServiceImpl implements VTradeDayStatsService {

	@Autowired
	private VTradeDayStatsDao<VTradeDayStats> vTradeDayStatsDao;

	@Override
	public void saveOrUpdate(VTradeDayStats o) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", o.getUser_id());
		params.put("recordTime", o.getRecord_time());
		
		VTradeDayStats vStats = vTradeDayStatsDao.get("From VTradeDayStats where record_time = :recordTime and user_id = :userId", params);
		if (vStats == null) {
			vTradeDayStatsDao.save(o);
			return;
		}
		
		vStats.setSum_depoist(o.getSum_depoist());
		vStats.setSum_income(o.getSum_income());
		vStats.setYield_rate(o.getYield_rate());
		
		vTradeDayStatsDao.update(vStats);
	}

	@Override
	public VTradeDayStats getLastBeforeTimeByUserId(long userId, long beforeTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("beforeTime", beforeTime);
		
		VTradeDayStats vStats = vTradeDayStatsDao.get("From VTradeDayStats where user_id = :userId and record_time < :beforeTime order by record_time desc", params);
		return vStats;
	}

}
