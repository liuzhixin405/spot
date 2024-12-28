package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQBenchmarkPrice;
import com.btc100.btc_q.entity.BQCompositeIndexDetail;
import com.btc100.btc_q.entity.BQCompositeIndexType;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQBenchmarkPriceService;
import com.btc100.btc_q.service.BQCompositeIndexDetailService;
import com.btc100.btc_q.service.BQCompositeIndexTypeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskCompositeBPI {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQCompositeIndexTypeService bqCompositeIndexTypeService;
	@Autowired
	private BQCompositeIndexDetailService bqCompositeIndexService;
	@Autowired
	private BQBenchmarkPriceService bqBenchmarkPriceService;

	private static final Logger logger = Logger.getLogger(TimerTaskCompositeBPI.class);

	public void run() {
		try {
			List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
			if (transTypes == null) {
				transTypes = transTypeService.findAllUsing();
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
			}
			if (transTypes == null || transTypes.size() == 0)
				return;

			List<BQTransactionType> indexTransTypes = new ArrayList<BQTransactionType>();
			List<BQTransaction> stockTransList = transactionService.findTransByKind(3, Contents.TRANSACTION_STATE_ZC);
			for (BQTransaction bqTransaction : stockTransList) {
				for (BQTransactionType transType : transTypes) {
					if (bqTransaction.getTransaction_code().equals(transType.getType_code())) {
						indexTransTypes.add(transType);
					}
				}
			}
			if (indexTransTypes == null || indexTransTypes.size() == 0)
				return;

			for (BQTransactionType transType : indexTransTypes) {
				doGetIndex(transType);
			}

		} catch (Exception e) {
			logger.info("获取指数服务异常。。。。");
			e.printStackTrace();
		}
	}

	private void doGetIndex(BQTransactionType transType) {
		if (transType == null)
			return;
		String typeCode = transType.getType_code();

		BQCompositeIndexType type = bqCompositeIndexTypeService.getByCode(typeCode);
		if (type == null || !type.getIs_using())
			return;
		if (type.getBenchmark_index().compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("获取指数服务异常," + typeCode + type.getType_name() + "基准开盘指数=" + type.getBenchmark_index().doubleValue());
			return;
		}

		int indexType = type.getType_id();
		List<BQCompositeIndexDetail> indexs = bqCompositeIndexService.findByType(indexType);
		if (indexs == null || indexs.size() == 0)
			return;

		BigDecimal totalRate = BigDecimal.ZERO;
		for (BQCompositeIndexDetail index : indexs) {
			totalRate = totalRate.add(index.getRate());
		}
		if (totalRate.compareTo(BigDecimal.ONE) < 0) {
			logger.info("获取指数服务异常," + typeCode + type.getType_name() + "配比小于1");
			return;
		}

		TickerVo ticker = new TickerVo();
		BigDecimal indexPrice = BigDecimal.ZERO; // 最新
		List<TickerVo> compTickers = new ArrayList<TickerVo>();

		// 综合指数价格 = 基准指数 * Σ[(P/P0)*Y]
		for (BQCompositeIndexDetail index : indexs) {
			String indexCode = index.getTransaction_code();
			BigDecimal rate = index.getRate();

			BigDecimal p0 = spyMemcachedClient.get(BTCQMemecacheKeys.BENCHMARK_PRICE + indexCode);
			if (p0 == null) {
				BQBenchmarkPrice benchmarkPrice = bqBenchmarkPriceService.getByCode(indexCode);
				if (benchmarkPrice == null) {
					logger.info("获取指数服务异常," + typeCode + type.getType_name() + "配比," + indexCode + "合约暂无基准价格");
					break;
				}
				p0 = benchmarkPrice.getBenchmark_price();
				spyMemcachedClient.set(BTCQMemecacheKeys.BENCHMARK_PRICE + indexCode, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, p0);
			}
			
			BigDecimal p = spyMemcachedClient.get(indexCode);
			if (p == null) {
				BQTransactionType indexTransType = transTypeService.getTransTypeByCode(indexCode);
				BQPriceIndexCache bqPriceIndexCache = bqPriceIndexCacheService
						.getBtcIndexPrice(indexTransType.getType_code());
				if (bqPriceIndexCache != null) {
					p = bqPriceIndexCache.getPrice();
				} else {
					p = BigDecimal.ZERO;
				}
			}
			indexPrice = indexPrice.add(p.divide(p0, 8, BigDecimal.ROUND_HALF_UP).multiply(rate));
			
			String market = "";
			List<TickerVo> indexTickers = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER + indexCode);
			if (indexTickers != null) {
				for (TickerVo vo : indexTickers) {
					market += vo.getMarket() + "/";
				}
				market = market.substring(0, market.length() - 1);
			}
			long time = System.currentTimeMillis() / 1000;
			
			TickerVo compTicker = new TickerVo();
			compTicker.setTrans_pair(indexCode.toUpperCase() + "/USDT");
			compTicker.setLast(p);
			compTicker.setRatio(rate);
			compTicker.setMarket(market);
			compTicker.setTime(time);
			compTickers.add(compTicker);
			
			index.setLast(p);
			index.setMarket(market);
			index.setDateline(time);
			bqCompositeIndexService.update(index);
		}
		spyMemcachedClient.set(BTCQMemecacheKeys.COMPOSITE_TICKER + transType.getType_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, compTickers);

		indexPrice = indexPrice.multiply(type.getBenchmark_index());
		Integer digits = spyMemcachedClient.get(typeCode + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS,
					BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}

		indexPrice = indexPrice.setScale(digits, BigDecimal.ROUND_HALF_UP);
		long time = System.currentTimeMillis() / 1000;

		ticker.setVol(BigDecimal.ZERO);
		ticker.setLast(indexPrice);
		ticker.setLastUsd(indexPrice);
		ticker.setHigh(BigDecimal.ZERO);
		ticker.setLow(BigDecimal.ZERO);
		ticker.setBuy(BigDecimal.ZERO);
		ticker.setSell(BigDecimal.ZERO);
		ticker.setTime(time);
		ticker.setTrans_pair(typeCode.toUpperCase() + "/USDT");
		ticker.setMarket("BFX");
		ticker.setTypeId(transType.getType_id());
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
	}

}
