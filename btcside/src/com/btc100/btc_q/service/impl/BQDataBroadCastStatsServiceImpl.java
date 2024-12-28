package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQDataBroadCastStatsDao;
import com.btc100.btc_q.entity.BQDataBroadCastStats;
import com.btc100.btc_q.service.BQDataBroadCastStatsService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqDataBroadCastStatsService")
public class BQDataBroadCastStatsServiceImpl implements BQDataBroadCastStatsService {

	@Autowired
	private BQDataBroadCastStatsDao<BQDataBroadCastStats> bqDataBroadCastSettingDao;

	@Override
	public void save(BQDataBroadCastStats stats) {
		bqDataBroadCastSettingDao.save(stats);
	}

	@Override
	public BQDataBroadCastStats getByCodeAndTime(String code, long time) {
		String hql = "from BQDataBroadCastStats where f_code = :code and record_time = :time";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("time", time);
		return bqDataBroadCastSettingDao.get(hql, params);
	}

	@Override
	public BQDataBroadCastStats getLast(String code) {
		String hql = "from BQDataBroadCastStats where f_code = :code order by record_time desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return bqDataBroadCastSettingDao.get(hql, params);
	}

	@Override
	public PagerModel readBySymbol(String code, long startTime, long endTime) {
		String hql = "from BQDataBroadCastStats where f_code = :code";
		if (startTime > 0) {
			hql += " and record_time >= :startTime";
		}
		if (endTime > 0) {
			hql += " and record_time < :endTime";
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		if (startTime > 0) {
			params.put("startTime", startTime);
		}
		if (endTime > 0) {
			params.put("endTime", endTime);
		}
		hql += " order by record_time desc";
		return bqDataBroadCastSettingDao.searchHql(hql, params);
	}

}
