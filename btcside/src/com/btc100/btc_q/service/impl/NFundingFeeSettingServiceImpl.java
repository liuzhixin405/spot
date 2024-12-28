package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NFundingFeeSettingDao;
import com.btc100.btc_q.entity.NFundingFeeSetting;
import com.btc100.btc_q.service.NFundingFeeSettingService;

@Service("nFundingFeeSettingService")
public class NFundingFeeSettingServiceImpl implements NFundingFeeSettingService {

	@Autowired
	private NFundingFeeSettingDao<NFundingFeeSetting> nFundingFeeSettingDao;
	
	@Override
	public NFundingFeeSetting getFundingFeeSettingByCode(String partition, String code) {
		return nFundingFeeSettingDao.getFundingFeeSettingByCode(partition, code);
	}

	@Override
	public void save(String partition, NFundingFeeSetting setting) {
		nFundingFeeSettingDao.createfundingFeeTables(partition, setting.getF_code());
		nFundingFeeSettingDao.insert(partition, setting);
	}

	@Override
	public List<NFundingFeeSetting> readAll(String partition) {
		return nFundingFeeSettingDao.findBySql("select * from " + partition + "_funding_fee_setting where 1=1");
	}

	@Override
	public void update(String partition, NFundingFeeSetting setting) {
		nFundingFeeSettingDao.update(partition, setting);
	}

}
