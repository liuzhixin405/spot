package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQLoanRecord;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQLoanRecordService;
import com.btc100.btc_q.service.BQLoanSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;

public class LoanCloseTimerTask {
	
	@Autowired
	private BQLoanRecordService bqLoanRecordService;
	@Autowired
	private BQLoanSettingService bqLoanSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	
	
	public void run() {
		List<BQLoanSetting> settings = bqLoanSettingService.findAll();
		
		for (BQLoanSetting setting : settings) {
			BigDecimal lastPrice = null;
			if (setting.getId() > 0) {
				String symbol = setting.getPledge_symbol().toLowerCase() + setting.getBorrow_symbol().toLowerCase();
				lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice == null) {
					BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.getByName(symbol);
					if (currentCoinPair != null) {
						lastPrice = currentCoinPair.getLatest_price();
					}
				}
			}
			
			if (lastPrice == null || lastPrice.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			
			List<BQLoanRecord> records = bqLoanRecordService.findAllLargeRate(lastPrice, setting.getPledge_coin_id(), setting.getBorrow_coin_id(), setting.getClose_pledge_rate());
			for (BQLoanRecord record : records) {
				try {
					bqLoanRecordService.closeCoin(record, lastPrice);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<BQLoanRecord> records = bqLoanRecordService.findAllOverCloseTime(3 * 24 * 3600);
		for (BQLoanRecord record : records) {
			try {
				BigDecimal lastPrice = null;
				String symbol = record.getPledge_symbol().toLowerCase() + record.getBorrow_symbol().toLowerCase();
				lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice == null) {
					BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.getByName(symbol);
					if (currentCoinPair != null) {
						lastPrice = currentCoinPair.getLatest_price();
					}
				}
				
				if (lastPrice == null || lastPrice.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
				
				bqLoanRecordService.closeCoin(record, lastPrice);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
	
}
