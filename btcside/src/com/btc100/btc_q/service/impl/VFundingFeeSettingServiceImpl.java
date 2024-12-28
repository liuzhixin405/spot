package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VFundingFeeSettingDao;
import com.btc100.btc_q.entity.VFundingFeeSetting;
import com.btc100.btc_q.service.VFundingFeeSettingService;

@Service("vFundingFeeSettingService")
public class VFundingFeeSettingServiceImpl implements VFundingFeeSettingService {

	@Autowired
	private VFundingFeeSettingDao<VFundingFeeSetting> bqFundingFeeSettingDao;
	
	@Override
	public VFundingFeeSetting getFundingFeeSettingByCode(String code) {
		return bqFundingFeeSettingDao.getFundingFeeSettingByCode(code);
	}

	@Override
	public void save(VFundingFeeSetting setting) {
		bqFundingFeeSettingDao.createfundingFeeTables(setting.getF_code());
		bqFundingFeeSettingDao.save(setting);
	}

	@Override
	public List<VFundingFeeSetting> readAll() {
		return bqFundingFeeSettingDao.find("from VFundingFeeSetting where 1=1");
	}

	@Override
	public void update(VFundingFeeSetting setting) {
		bqFundingFeeSettingDao.update(setting);
	}

}
