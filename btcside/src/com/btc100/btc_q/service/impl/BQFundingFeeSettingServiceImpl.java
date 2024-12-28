package com.btc100.btc_q.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQFundingFeeSettingDao;
import com.btc100.btc_q.entity.BQFundingFeeSetting;
import com.btc100.btc_q.service.BQFundingFeeSettingService;

@Service("bqFundingFeeSettingService")
public class BQFundingFeeSettingServiceImpl implements BQFundingFeeSettingService {

	@Autowired
	private BQFundingFeeSettingDao<BQFundingFeeSetting> bqFundingFeeSettingDao;
	
	@Override
	public BQFundingFeeSetting getFundingFeeSettingByCode(String code) {
		return bqFundingFeeSettingDao.getFundingFeeSettingByCode(code);
	}

	@Override
	public void save(BQFundingFeeSetting ba) {
		bqFundingFeeSettingDao.createfundingFeeTables(ba.getF_code());
		bqFundingFeeSettingDao.save(ba);
	}

	@Override
	public List<BQFundingFeeSetting> readAll() {
		return bqFundingFeeSettingDao.find("from BQFundingFeeSetting where 1=1");
	}

	@Override
	public void update(BQFundingFeeSetting setting) {
		bqFundingFeeSettingDao.update(setting);
	}

}
