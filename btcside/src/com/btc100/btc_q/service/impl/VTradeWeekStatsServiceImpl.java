package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VTradeWeekStatsDao;
import com.btc100.btc_q.entity.VTradeWeekStats;
import com.btc100.btc_q.service.VTradeWeekStatsService;

@Service("vTradeWeekStatsSrevice")
public class VTradeWeekStatsServiceImpl implements VTradeWeekStatsService {

	@Autowired
	private VTradeWeekStatsDao<VTradeWeekStats> vTradeWeekStatsDao;

	@Override
	public void saveOrUpdate(VTradeWeekStats o) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", o.getUser_id());
		params.put("recordTime", o.getRecord_time());
		
		VTradeWeekStats vStats = vTradeWeekStatsDao.get("From VTradeWeekStats where record_time = :recordTime and user_id = :userId", params);
		if (vStats == null) {
			vTradeWeekStatsDao.save(o);
			return;
		}
		
		vStats.setSum_depoist(o.getSum_depoist());
		vStats.setSum_income(o.getSum_income());
		vStats.setYield_rate(o.getYield_rate());
		
		vTradeWeekStatsDao.update(vStats);
	}

	@Override
	public List<VTradeWeekStats> readAll(long recordTime, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recordTime", recordTime);
		
		return vTradeWeekStatsDao.find("from VTradeWeekStats where 1=1 and record_time = :recordTime order by yield_rate desc", params, 1, limit);
	}

}
