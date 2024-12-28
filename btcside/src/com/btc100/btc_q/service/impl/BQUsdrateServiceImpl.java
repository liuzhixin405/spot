package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQUsdrateDao;
import com.btc100.btc_q.entity.BQUsdrate;
import com.btc100.btc_q.service.BQUsdrateService;

@Service("bqUsdrateService")
public class BQUsdrateServiceImpl implements BQUsdrateService {

	@Autowired
	private BQUsdrateDao<BQUsdrate> bqUsdrateDao;

	public BigDecimal findUsdRateOnuse(int coinStateOnuse) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ex_use", coinStateOnuse);
		BQUsdrate usdrate = (BQUsdrate) bqUsdrateDao.find("From BQUsdrate where ex_use = :ex_use and ex_type=1", params, 1, 1).get(0);
		return usdrate.getEx_rate();
	}
	
	public BigDecimal readUsdRateOnuse(int coinStateOnuse) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ex_use", coinStateOnuse);
		BQUsdrate usdrate = (BQUsdrate) bqUsdrateDao.find("From BQUsdrate where ex_use = :ex_use and ex_type=1", params, 1, 1).get(0);
		return usdrate.getEx_rate();
	}

	public void saveUsdRate(BQUsdrate usdrate) {
		bqUsdrateDao.save(usdrate);
	}

	public void delUsdRate(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqUsdrateDao.deleteCondition("Delete from BQUsdrate where id = :id", params);
	}

	public void updateUsdRate(BQUsdrate usdrate) {
		bqUsdrateDao.update(usdrate);
	}

	public List<BQUsdrate> findAll() {
		return bqUsdrateDao.find("From BQUsdrate");
	}

	public BQUsdrate findUsdRateById(int id) {
		return bqUsdrateDao.get(BQUsdrate.class, id);
	}

	public long getTime() {
		return bqUsdrateDao.getUnix_TIMESTAMP();
	}

	public BQUsdrate findUsdOnuse(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ex_use", state);
		return bqUsdrateDao.find("From BQUsdrate where ex_use = :ex_use and ex_type=1", params, 1, 1).get(0);
	}

}
