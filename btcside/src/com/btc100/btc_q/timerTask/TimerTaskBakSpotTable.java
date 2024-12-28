package com.btc100.btc_q.timerTask;

import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.SpotAccountLogService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotPendingLogService;
import com.btc100.btc_q.service.SpotTradeLogService;

/**
 * 备份币币交易相关数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class TimerTaskBakSpotTable extends BaseTask {

	@Autowired
	private SpotAccountLogService spotAccountLogService;
	@Autowired
	private SpotMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private SpotPendingLogService pendingLogService;
	@Autowired
	private SpotTradeLogService tradeLogService;

	private static String[] marketArray = null;

	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		doContractBak(bakEndTime);
	}

	private void doContractBak(long endTime) {
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}

		if (marketArray == null || marketArray.length == 0)
			return;

		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST,
					coinPairs);
		}

		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}

			if (coinPair == null)
				continue;
			String code = coinPair.getCoin_pair_name();

			try {
				// 随机休眠0~2分钟
				int first = 0, second = 2 * 1000 * 60;
				Thread.sleep((first + (int) (Math.random() * (second - first))));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			spotAccountLogService.createAccountLogBak(code);
			memberTradeDetailService.createMemberTradeDetailBak(code);
			pendingLogService.createPendingLogBak(code);
			tradeLogService.createTradeLogBak(code);

			spotAccountLogService.insertAccountLogBak(code, endTime);
			memberTradeDetailService.insertMemberTradeDetailBak(code, endTime);
			pendingLogService.insertPendingLogBak(code, endTime);
			tradeLogService.insertTradeLogBak(code, endTime);
		}
	}
}
