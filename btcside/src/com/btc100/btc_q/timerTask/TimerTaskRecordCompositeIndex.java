package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQBenchmarkPrice;
import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQBenchmarkPriceService;
import com.btc100.btc_q.service.BQCompositeIndexTypeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;

public class TimerTaskRecordCompositeIndex extends BaseTask {

	private static final Logger logger = Logger.getLogger(TimerTaskRecordCompositeIndex.class);

	@Autowired
	private BQBenchmarkPriceService bqBenchmarkPriceService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQCompositeIndexTypeService bqCompositeIndexTypeService;

	public void run() {
		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		try {
			getBenchmarkPrice(lstTrads);
		} catch (Exception e) {
			logger.info("0点获取合约基准价格异常。。。。");
			e.printStackTrace();
		}

		try {
			recordCompositeIndexPrice(lstTrads);
		} catch (Exception e) {
			logger.info("每日0点记录综合指数异常。。。。");
			e.printStackTrace();
		}
	}

	private void getBenchmarkPrice(List<BQTransaction> lstTrads) {
		for (BQTransaction bqTransaction : lstTrads) {
			if (bqTransaction.getTransaction_kind() == 3)
				continue;

			String code = bqTransaction.getTransaction_code();
			
			BQBenchmarkPrice benchmarkPrice = bqBenchmarkPriceService.getByCode(code);
			if (benchmarkPrice != null)
				continue;

			BigDecimal btc_index_price = spyMemcachedClient.get(code);
			if (btc_index_price == null) {
				BQTransactionType transType = transTypeService.getTransTypeByCode(code);
				BQPriceIndexCache bqPriceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code());
				if (bqPriceIndexCache != null) {
					btc_index_price = bqPriceIndexCache.getPrice();
				} else {
					btc_index_price = BigDecimal.ZERO;
				}
			}

			benchmarkPrice = new BQBenchmarkPrice();
			benchmarkPrice.setTransaction_code(code);
			benchmarkPrice.setBenchmark_price(btc_index_price);
			bqBenchmarkPriceService.save(benchmarkPrice);

			spyMemcachedClient.set(BTCQMemecacheKeys.BENCHMARK_PRICE + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY,
					btc_index_price);
		}
	}

	private void recordCompositeIndexPrice(List<BQTransaction> lstTrads) {
		for (BQTransaction bqTransaction : lstTrads) {
			if (bqTransaction.getTransaction_kind() != 3)
				continue;

			String code = bqTransaction.getTransaction_code();
			BQCompositeIndexType type = bqCompositeIndexTypeService.getByCode(code);
			if (type == null || !type.getIs_using())
				continue;

			BigDecimal last = spyMemcachedClient.get(code + BTCQMemecacheKeys.BTCLATESTPRICE);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = bqTransaction.getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;

			spyMemcachedClient.set(BTCQMemecacheKeys.COLSING_PRICE + code, BTCQMemecacheKeys.TIMES.TWO_DAY,
					last);
			
			type.setClosing_price(last);
			bqCompositeIndexTypeService.update(type);
		}
	}
}
