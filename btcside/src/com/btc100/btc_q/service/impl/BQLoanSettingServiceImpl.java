package com.btc100.btc_q.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQLoanSettingDao;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.service.BQLoanSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanSettingService")
public class BQLoanSettingServiceImpl implements BQLoanSettingService {

	@Autowired
	private BQLoanSettingDao<BQLoanSetting> bqLoanSettingDao;

	@Override
	public BQLoanSetting getLoanSettingByCode(String borrow, String pledge) {
		return bqLoanSettingDao.getLoanSettingByCode(borrow, pledge);
	}

	@Override
	public void save(BQLoanSetting setting) {
		bqLoanSettingDao.save(setting);
	}

	@Override
	public void update(BQLoanSetting setting) {
		bqLoanSettingDao.update(setting);
	}

	@Override
	public BQLoanSetting readLoanSettingByCode(String borrow, String pledge) {
		return bqLoanSettingDao.getLoanSettingByCode(borrow, pledge);
	}

	@Override
	public BQLoanSetting readOne() {
		return bqLoanSettingDao.get("from BQLoanSetting");
	}

	@Override
	public PagerModel readAll() {
		return bqLoanSettingDao.searchHql("from BQLoanSetting");
	}

	@Override
	public BQLoanSetting readOne(long id) {
		return bqLoanSettingDao.get(BQLoanSetting.class, id);
	}

	@Override
	public List<BQLoanSetting> readAllList() {
		return bqLoanSettingDao.find("from BQLoanSetting");
	}

	@Override
	public List<BQLoanSetting> findAll() {
		return bqLoanSettingDao.find("from BQLoanSetting");
	}
	
}
