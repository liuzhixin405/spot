package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.service.BQExchangeSettingService;
import com.btc100.btc_q.service.BQExchangeTickerService;
import com.btc100.btc_q.timerTask.common.CommonDataHuobi;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskGetTicker {
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	BQExchangeTickerService bqExchangeTickerService;
	
	public void run() {
		
		List<BQExchangeSetting> exchangeSettings = bqExchangeSettingService.findAll();
		for (BQExchangeSetting setting : exchangeSettings) {
			try {
				TickerVo vo = CommonDataHuobi.findDetailMerged(setting.getSymbol(), "usdt");
				if (vo != null && vo.getBuy().compareTo(BigDecimal.ZERO) > 0 && vo.getSell().compareTo(BigDecimal.ZERO) > 0) {
					vo.setTypeCode(setting.getSymbol());
					vo.setTypeId(setting.getCurrency_type_id());
					spyMemcachedClient.set(BTCQMemecacheKeys.EXCHANGE_TICKER + setting.getSymbol(), BTCQMemecacheKeys.TIMES.BPI, vo);
					bqExchangeTickerService.saveOrUpdate(vo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
