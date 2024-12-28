package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQBftRewardSettingDao;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.service.BQBftRewardSettingService;

@Service("bqBftRewardSettingService")
public class BQBftRewardSettingServiceImpl implements BQBftRewardSettingService {
	
	@Autowired
	private BQBftRewardSettingDao<BQBftRewardSetting> bqBftRewardSettingDao;
	
	@Override
	public void save(BQBftRewardSetting setting) {
		bqBftRewardSettingDao.save(setting);
	}

	@Override
	public void update(BQBftRewardSetting setting) {
		String hql = "UPDATE BQBftRewardSetting b SET b.currency_type_id=:currencyId,b.login_reward=:loginReward,b.invite_reward=:inviteReward,b.register_reward=:registerReward WHERE b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", setting.getId());
		map.put("currencyId", setting.getCurrency_type_id());
		map.put("loginReward", setting.getLogin_reward());
		map.put("inviteReward", setting.getInvite_reward());
		map.put("registerReward", setting.getRegister_reward());
		bqBftRewardSettingDao.updateCondition(hql, map);
	}

	@Override
	public BQBftRewardSetting get() {
		return bqBftRewardSettingDao.get("from BQBftRewardSetting where 1=1");
	}
	
	@Override
	public BQBftRewardSetting readOne() {
		return bqBftRewardSettingDao.get("from BQBftRewardSetting where 1=1");
	}

	@Override
	public boolean bfxRewarded(long id) {
		String hql = "UPDATE BQBftRewardSetting b SET b.bfx_rewarded=:bfxRewarded WHERE b.id=:id and b.bfx_rewarded = false";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("bfxRewarded", true);
		return bqBftRewardSettingDao.updateCondition(hql, map) > 0;
	}
	
	@Override
	public boolean oldRegisterRewarded(long id) {
		String hql = "UPDATE BQBftRewardSetting b SET b.old_register_rewarded=true WHERE b.id=:id and b.old_register_rewarded = false";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return bqBftRewardSettingDao.updateCondition(hql, map) > 0;
	}

}
