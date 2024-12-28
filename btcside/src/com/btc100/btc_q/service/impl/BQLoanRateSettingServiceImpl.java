package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQLoanRateSettingDao;
import com.btc100.btc_q.entity.BQLoanRateSetting;
import com.btc100.btc_q.service.BQLoanRateSettingService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqLoanRateSettingService")
public class BQLoanRateSettingServiceImpl implements BQLoanRateSettingService {

	@Autowired
	private BQLoanRateSettingDao<BQLoanRateSetting> bqLoanRateSettingDao;

	@Override
	public void save(BQLoanRateSetting setting) {
		bqLoanRateSettingDao.save(setting);
	}

	@Override
	public void update(BQLoanRateSetting setting) {
		bqLoanRateSettingDao.update(setting);
	}

	@Override
	public BQLoanRateSetting getLoanRateSettingByLoanIdAndCycle(long id, int cycle) {
		return bqLoanRateSettingDao.getLoanRateSettingByIdAndCycle(id, cycle);
	}

	@Override
	public List<BQLoanRateSetting> findAllByLoanId(long id) {
		return bqLoanRateSettingDao.findAllById(id);
	}

	@Override
	public BQLoanRateSetting readLoanRateSettingByLoanIdAndCycle(long id, int cycle) {
		return bqLoanRateSettingDao.getLoanRateSettingByIdAndCycle(id, cycle);
	}

	@Override
	public List<BQLoanRateSetting> readAllByLoanId(long id) {
		return bqLoanRateSettingDao.findAllById(id);
	}

	@Override
	public BQLoanRateSetting readById(long id) {
		return bqLoanRateSettingDao.get(BQLoanRateSetting.class, id);
	}

	@Override
	public PagerModel readPagerByLoanId(long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		return bqLoanRateSettingDao.searchHql("from BQLoanRateSetting where loan_setting_id = :id", params);
	}

	@Override
	public void delete(long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		bqLoanRateSettingDao.deleteCondition("delete from BQLoanRateSetting where id = :id", params);
	}
	
}
