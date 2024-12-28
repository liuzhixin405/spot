package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskExperiBPI {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private TradeLogService tradeLogService;

	private static final Logger logger = Logger.getLogger(TimerTaskExperiBPI.class);
	
	public void run() {
		try {
			List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
			if (transTypes == null) {
				transTypes = transTypeService.findAllUsing();
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
			}
			if (transTypes == null || transTypes.size() == 0) return;
			
			List<BQTransaction> stockTransList = transactionService.findTransByKind(2, Contents.TRANSACTION_STATE_ZC);
			for (BQTransaction bqTransaction : stockTransList) {
				String typeCode = bqTransaction.getTransaction_type_code();
				
				TickerVo ticker = new TickerVo();
				
				long time = System.currentTimeMillis() / 1000;
				
				ticker.setVol(BigDecimal.ZERO);
				ticker.setLast(bqTransaction.getLatest_transaction_price());
				ticker.setLastUsd(bqTransaction.getLatest_transaction_price());
				ticker.setHigh(bqTransaction.getTransaction_highest());
				ticker.setLow(bqTransaction.getTransaction_lowest());
				ticker.setBuy(BigDecimal.ZERO);
				ticker.setSell(BigDecimal.ZERO);
				ticker.setTime(time);
				ticker.setTrans_pair(typeCode.toUpperCase() + "/USDT");
				ticker.setMarket("BFX");
				ticker.setTypeId(bqTransaction.getTransaction_type());
				ticker.setTypeCode(typeCode);
				ticker.setRatio(BigDecimal.ONE);
				
				List<TickerVo> tickers = new ArrayList<TickerVo>();
				tickers.add(ticker);
				
				BigDecimal last = spyMemcachedClient.get(typeCode);
				for (TickerVo vo : tickers) {
					spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, vo.getLast());
					if (last != null && vo.getLast().compareTo(last) != 0) {
						bqPriceIndexCacheService.updateBtcCache(vo, vo.getLast());
					}
				}
				
				spyMemcachedClient.set(typeCode, BTCQMemecacheKeys.TIMES.BPI, ticker.getLast());
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + typeCode, BTCQMemecacheKeys.TIMES.BPI, tickers);
				
				long morning = TimeUtil.getTimesmorning();
				BigDecimal morningIndex = spyMemcachedClient.get(typeCode + "_" + String.valueOf(morning));
				if (morningIndex == null || morningIndex.compareTo(BigDecimal.ZERO) <= 0) {
					morningIndex = ticker.getLast();
					spyMemcachedClient.set(typeCode + "_" + String.valueOf(morning), BTCQMemecacheKeys.TIMES.ONE_DAY + BTCQMemecacheKeys.TIMES.ONE_HOUR, morningIndex);
					spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, "0.00%");
				} else {
					BigDecimal indexFudu = ticker.getLast().subtract(morningIndex).divide(morningIndex, 4, BigDecimal.ROUND_HALF_UP);
					DecimalFormat df = new DecimalFormat("0.00");
					String fudu = df.format(indexFudu.doubleValue() * 100) + "%";
					spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, fudu);
				}
				
				// 1分钟前价格
				BigDecimal oneHourAgo = tradeLogService.getLastHourAgoByCode(bqTransaction.getTransaction_code());
				if (oneHourAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneHourAgo);
				}
				
				BigDecimal oneDayAgo = tradeLogService.getLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
		} catch (Exception e) {
			logger.info("获取指数服务异常。。。。");
			e.printStackTrace();
		}
	}
}
