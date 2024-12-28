package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQUsdrate;

public interface BQUsdrateService {

	public BigDecimal findUsdRateOnuse(int coinStateOnuse);

	public void saveUsdRate(BQUsdrate usdrate);

	public void updateUsdRate(BQUsdrate usdrate);

	public void delUsdRate(int id);

	public List<BQUsdrate> findAll();

	public BQUsdrate findUsdRateById(int id);

	public long getTime();

	/**
	 * 找出在使用的汇率对象
	 * @param state 1使用
	 * @return
	 */
	public BQUsdrate findUsdOnuse(int state);
	
	
	/************************************ for read only **************************************/
	
	public BigDecimal readUsdRateOnuse(int coinStateOnuse);

}
