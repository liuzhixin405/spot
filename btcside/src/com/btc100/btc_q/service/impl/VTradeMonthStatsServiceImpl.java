package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VTradeMonthStatsDao;
import com.btc100.btc_q.entity.VTradeMonthStats;
import com.btc100.btc_q.service.VTradeMonthStatsService;

@Service("vTradeMonthStatsSrevice")
public class VTradeMonthStatsServiceImpl implements VTradeMonthStatsService {

	@Autowired
	private VTradeMonthStatsDao<VTradeMonthStats> vTradeMonthStatsDao;

	@Override
	public void saveOrUpdate(VTradeMonthStats o) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", o.getUser_id());
		params.put("recordTime", o.getRecord_time());
		
		VTradeMonthStats vStats = vTradeMonthStatsDao.get("From VTradeMonthStats where record_time = :recordTime and user_id = :userId", params);
		if (vStats == null) {
			vTradeMonthStatsDao.save(o);
			return;
		}
		
		vStats.setSum_depoist(o.getSum_depoist());
		vStats.setSum_income(o.getSum_income());
		vStats.setYield_rate(o.getYield_rate());
		
		vTradeMonthStatsDao.update(vStats);
	}

}
