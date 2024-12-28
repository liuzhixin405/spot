package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMineStageSettingDao;
import com.btc100.btc_q.entity.BQMineStageSetting;
import com.btc100.btc_q.service.BQMineStageSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMineStageSettingService")
public class BQMineStageSettingServiceImpl implements BQMineStageSettingService {

	@Autowired
	private BQMineStageSettingDao<BQMineStageSetting> bqMineStageSettingDao;

	@Override
	public BQMineStageSetting readMineStageSettingById(long id) {
		return bqMineStageSettingDao.get(BQMineStageSetting.class, id);
	}

	@Override
	public void save(BQMineStageSetting ba) {
		bqMineStageSettingDao.save(ba);
	}

	@Override
	public void update(BQMineStageSetting setting) {
		bqMineStageSettingDao.update(setting);
	}

	@Override
	public void updateOutNum(BQMineStageSetting setting) {
		String hql = "update BQMineStageSetting set stage_amount=:stageAmount and rate=:rate where id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stageAmount", setting.getstage_amount());
		map.put("rate", setting.getRate());
		map.put("id", setting.getId());

		bqMineStageSettingDao.updateCondition(hql, map);
	}

	@Override
	public void deleteById(long id) {
		String hql = "delete from BQMineStageSetting where id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);

		bqMineStageSettingDao.deleteCondition(hql, params);
	}

	@Override
	public PagerModel readAllStages() {
		return bqMineStageSettingDao.searchHql("from BQMineStageSetting where 1 = 1");
	}

	@Override
	public BigDecimal getRateByStageAmount(BigDecimal amount) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);
		BQMineStageSetting setting = bqMineStageSettingDao.get("from BQMineStageSetting where stage_amount <= :amount order by stage_amount desc", params);
		if (setting == null) return BigDecimal.ZERO;
		
		return setting.getRate();
	}

	@Override
	public BQMineStageSetting getByStageAmount(BigDecimal amount) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);

		return bqMineStageSettingDao.get("from BQMineStageSetting where stage_amount <= :amount order by stage_amount desc", params);
	}

	@Override
	public BQMineStageSetting getNextSetting(int num) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("num", num);

		return bqMineStageSettingDao.get("from BQMineStageSetting where num > :num order by num", params);
	}

	@Override
	public BQMineStageSetting readByStageAmount(BigDecimal amount) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);
		
		return bqMineStageSettingDao.get("from BQMineStageSetting where stage_amount <= :amount order by stage_amount desc", params);
	}

}
