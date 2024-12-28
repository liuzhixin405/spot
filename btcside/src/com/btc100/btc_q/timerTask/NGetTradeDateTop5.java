package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NFundingFeeSetting;
import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NFundingFeeRate;
import com.btc100.btc_q.service.NFundingFeeSettingService;
import com.btc100.btc_q.service.NMarkPriceSettingService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.NFundingFeeRateService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.MulpitleAndStateVO;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.TradeAjaxVO;

/**
 * 获取市场挂单top5
 * 
 * @author Administrator
 * 
 */
public class NGetTradeDateTop5 {

	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private NTransactionTypeService transactionTypeService;
	@Autowired
	private NNotradetimeService nNotradetimeService;
	@Autowired
	private NTransactionRateService nTransactionRateService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NFundingFeeRateService fundingFeeRateService;
	@Autowired
	private NFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private NMarkPriceSettingService nMarkPriceSettingService;
	
	private static String[] contractArray = null;

	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bqTransaction = transactionService.findTransByCode(partition, contract);
			if (bqTransaction == null) continue;
			
			String bqCode = bqTransaction.getTransaction_code();
			spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.BTCCOEFFICIENT, BTCQMemecacheKeys.TIMES.BTCCOEFFICIENT,
					bqTransaction.getTrade_coefficient());
			
			BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqCode + BTCQMemecacheKeys.BTCLATESTPRICE);
			if (lastPrice == null) {
				spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, 
						bqTransaction.getLatest_transaction_price());
			} else {
				NTransaction bqt_temp = transactionService.findTransToUpdateByCode(partition, bqCode);
				if (lastPrice.compareTo(bqt_temp.getLatest_transaction_price()) != 0) {
					
					BigDecimal highPrice = bqt_temp.getTransaction_highest();
					BigDecimal changeHigh = BigDecimal.ZERO;
					if (lastPrice.compareTo(highPrice) > 0) {
						changeHigh = lastPrice;
					}
					
					BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
					BigDecimal changeLow = BigDecimal.ZERO;
					if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
						changeLow = lastPrice;
					}
					
					transactionService.updateTrans(partition, bqt_temp.getTransaction_id(), changeHigh, changeLow, lastPrice);
				}
			}
			
			getCommonData(partition, bqTransaction);
		}


		for (String pContract : contractArray) {
			
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			BigDecimal btc_index_price = spyMemcachedClient.get(contract);
			if (btc_index_price == null) {
				
				List<NTransactionType> transactionTypeList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
				if (transactionTypeList == null) {
					transactionTypeList = transactionTypeService.findAllUsing(partition);
					spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition,BTCQMemecacheKeys.TIMES.TEN_SECOND, transactionTypeList);
				}
				
				NTransactionType bqTransactionType = null;
				for (NTransactionType type : transactionTypeList) {
					if (type.getType_code().equals(contract)) {
						bqTransactionType = type;
						break;
					}
				}
				
				if (bqTransactionType == null) {
					continue;
				}
				
				BQPriceIndexCache cache = bqPriceIndexCacheService.getBtcIndexPrice(bqTransactionType.getType_code());
				if (cache != null) {
					btc_index_price = cache.getPrice();
				}
			}
			
			if (btc_index_price != null) {
				spyMemcachedClient.set(contract, BTCQMemecacheKeys.TIMES.BTCINDEXPRICE, btc_index_price);
			}
		}
	}
	
	private int getDigits(String partition, NTransaction trans) {
		Integer digits = spyMemcachedClient.get(partition + "_" + trans.getTransaction_code() + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			NTransactionType transType = transactionTypeService.getTransactionType(partition, trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(partition + "_" + trans.getTransaction_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void getCommonData(String partition, NTransaction bqTransaction) {
		
		String code = bqTransaction.getTransaction_code();
		String tranCode = bqTransaction.getTransaction_type_code();
		TradeAjaxVO vo = new TradeAjaxVO();
		BigDecimal lastPrice = BigDecimal.ZERO;
		BigDecimal coeffcient = BigDecimal.ZERO;
		BigDecimal btc_index_price = spyMemcachedClient.get(code);
		if (btc_index_price == null) {
			BQPriceIndexCache priceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(bqTransaction.getTransaction_type_code());
			if (priceIndexCache != null) {
				btc_index_price = priceIndexCache.getPrice();
			}
		}
		
		if (btc_index_price == null) return;
		
		vo.setTypeCode(tranCode);
		String indexFudu = spyMemcachedClient.get(BTCQMemecacheKeys.INDEX_FUDU + tranCode);
		if (StringUtil.isEmpty(indexFudu)) {
			vo.setIndexFudu("0.00%");
		} else {
			vo.setIndexFudu(indexFudu);
		}
		
		Integer digits = 0;
		int amountDigits = Utils.getAmountDigtis(code);
		JSONObject bqLastTicker = null;
		BigDecimal rateUp = BigDecimal.ZERO;
		BigDecimal rateDown = BigDecimal.ZERO;
		
		
		String[] multiples = null;
		String[] multiplestates = null;
		
		lastPrice = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = bqTransaction.getLatest_transaction_price();
		}
		coeffcient = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.BTCCOEFFICIENT);
		if (coeffcient == null) {
			coeffcient = bqTransaction.getTrade_coefficient();
		}
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = new ArrayList<Top5DelegationVO>();
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = new ArrayList<Top5DelegationVO>();
		}
		digits = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			digits = getDigits(partition, bqTransaction);
		}
		
		List<BigDecimal> list = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_LIST + partition + "_" + code);
		NMarkPriceSetting setting = nMarkPriceSettingService.getByCode(partition, code);
		if (setting == null) {
			setting = nMarkPriceSettingService.get(partition);
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition, BTCQMemecacheKeys.TIMES.BPI, setting);
			}
		} else {
			spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition + "_" + code, BTCQMemecacheKeys.TIMES.BPI, setting);
		}
		if (setting != null && setting.isRunning()) {
			if (setting.isFixed() && setting.getFixed_price().compareTo(BigDecimal.ZERO) > 0) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + code, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting.getFixed_price());
				vo.setMarkPrice(setting.getFixed_price().doubleValue() + "");
			} else {
			
				do {
					if (btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
					
					BigDecimal markPriceDis = BigDecimal.ZERO;
					if (buytop5List.size() == 0 || selltop5List.size() == 0) {
						markPriceDis = BigDecimal.ZERO;
					} else {
						BigDecimal buy1 = buytop5List.get(0).getPrice();
						BigDecimal sell1 = selltop5List.get(selltop5List.size() - 1).getPrice();
						if (buy1.compareTo(BigDecimal.ZERO) <= 0 || sell1.compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						if (setting.getOpen_width_filter().compareTo(BigDecimal.ZERO) > 0) {
							if (sell1.subtract(buy1).divide(buy1, digits + 2, BigDecimal.ROUND_HALF_UP).compareTo(setting.getOpen_width_filter()) > 0) {
								break;
							}
						}
						
						BigDecimal markPrice = sell1.add(buy1).divide(new BigDecimal(2), digits, BigDecimal.ROUND_HALF_UP);
						markPriceDis = markPrice.subtract(btc_index_price);
					}
					
					BigDecimal markPrice = BigDecimal.ZERO;
					if (setting.getMoving_period() > 1) {
						if (list == null) {
							list = new ArrayList<BigDecimal>();
						}
						while (list.size() >= setting.getMoving_period()) {
							list.remove(0);
						}
						
						list.add(markPriceDis);
						
						BigDecimal all = BigDecimal.ZERO;
						for (BigDecimal num : list) {
							all = all.add(num);
						}
						
						markPrice = btc_index_price.add(all.divide(new BigDecimal(list.size()), digits, BigDecimal.ROUND_HALF_UP));
						spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE_LIST + partition + "_"  + code, BTCQMemecacheKeys.TIMES.ONE_DAY, list);
					} else {
						markPrice = btc_index_price.add(markPriceDis);
						if (list != null) {
							spyMemcachedClient.delete(BTCQMemecacheKeys.MARK_PRICE_LIST + partition + "_"  + code);
						}
					}
					
					if (markPrice.compareTo(BigDecimal.ZERO) > 0) {
						if (setting.getIndex_rate_filter().compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal max = btc_index_price.multiply(BigDecimal.ONE.add(setting.getIndex_rate_filter())).setScale(digits, BigDecimal.ROUND_HALF_UP);
							BigDecimal min = btc_index_price.multiply(BigDecimal.ONE.subtract(setting.getIndex_rate_filter())).setScale(digits, BigDecimal.ROUND_HALF_UP);
							
							if (markPrice.compareTo(max) > 0) {
								markPrice = max;
							}
							
							if (markPrice.compareTo(min) < 0) {
								markPrice = min;
							}
						}
						
						spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + code, BTCQMemecacheKeys.TIMES.FIVE_MIN, markPrice);
						vo.setMarkPrice(markPrice.doubleValue() + "");
					}
				} while(false);
			}
		} else {
			if (list != null) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.MARK_PRICE_LIST + partition + "_" + code);
			}
			
			spyMemcachedClient.delete(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + code); 
		}
		
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + partition + "_" + code);
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		rateUp = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
		rateDown = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			NTransactionRate index_price_rate = nTransactionRateService.getByCode(partition, code);
			rateDown = index_price_rate.getDownrate();
			rateUp = index_price_rate.getUprate();
			
			spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
			spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
		}
		
		vo.setTransactionKind(bqTransaction.getTransaction_kind());
		vo.setShowName(bqTransaction.getTransaction_show_name());
		vo.setShowNameEn(bqTransaction.getTransaction_show_name_en());
		vo.setShowNameJp(bqTransaction.getTransaction_show_name_jp());
		vo.setShowNameKr(bqTransaction.getTransaction_show_name_kr());
		vo.setShowNameKr(bqTransaction.getTransaction_show_name_kr());
		vo.setShowNameHk(bqTransaction.getTransaction_show_name_hk());
		vo.setSuspend(bqTransaction.getSuspend());
		// 交易时间 nNotradetimeService判断
		vo.setTradeTime(true);
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (nNotradetimeService != null) {
				if (!nNotradetimeService.isTradeTime(partition, bqTransaction.getTransaction_id())) {
					vo.setTradeTime(false);
				}
			}
		}
		// 杠杆
		multiples = bqTransaction.getMultiple().split(",");
		multiplestates = bqTransaction.getMultiple_state().split(",");
		
		vo.setBuytop5List(buytop5List);
		vo.setSelltop5List(selltop5List);
		vo.setLastPrice(lastPrice);
		vo.setTransCoeffcient(coeffcient);
		vo.setBtc_index_price(btc_index_price); // 当前价格指数
		vo.setIndexPrice(btc_index_price);
		
		vo.setDigits(digits);
		vo.setAmountDigits(amountDigits);
		
		BigDecimal cnyRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(cnyRate)) {
			cnyRate = bqUsdrateService.findUsdRateOnuse(1);
			if (cnyRate == null) cnyRate = new BigDecimal(7);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, cnyRate);
		}
		vo.setCnyRate(cnyRate.doubleValue());
		

		if (bqLastTicker == null) {
			long time = TimeUtil.getYesterdayTime();
			BigDecimal high = tradeLogService.findHighPrice(partition, code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			BigDecimal low = tradeLogService.findLowPrice(partition, code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			
			BigDecimal vol = tradeLogService.findBTCVol(partition, code, time);
			vol = vol == null ? new BigDecimal(0) : vol;
			
			BigDecimal trades = tradeLogService.findTradeBTC(partition, code, time);
			trades = (trades == null ? BigDecimal.ZERO : trades);

			BigDecimal buy = new BigDecimal(0);
			BigDecimal sell = new BigDecimal(0);

			high = high.setScale(digits, BigDecimal.ROUND_DOWN);
			low = low.setScale(digits, BigDecimal.ROUND_DOWN);
			buy = buy.setScale(digits, BigDecimal.ROUND_DOWN);
			sell = sell.setScale(digits, BigDecimal.ROUND_DOWN);
			vol = vol.setScale(digits, BigDecimal.ROUND_DOWN);
			trades = trades.setScale(digits, BigDecimal.ROUND_DOWN);
			
			if (high.doubleValue() < lastPrice.doubleValue() || high.doubleValue() == 0) {
				high = lastPrice;
			}
			
			if (low.doubleValue() > lastPrice.doubleValue() || low.doubleValue() == 0) {
				low = lastPrice;
			}
			
			bqLastTicker = new JSONObject();
			bqLastTicker.put("bqCode", code);
			bqLastTicker.put("high", high);
			bqLastTicker.put("low", low);
			bqLastTicker.put("buy", 0);
			bqLastTicker.put("sell", 0);
			bqLastTicker.put("last", lastPrice.doubleValue());
			bqLastTicker.put("vol", vol);
			bqLastTicker.put("trades", trades);
		}
		
		Double high = bqLastTicker.getDouble("high");
		Double low = bqLastTicker.getDouble("low");
		Double vol = bqLastTicker.getDouble("vol");
		Double trades = bqLastTicker.getDouble("trades");
		
		boolean updateTicker = false;
		if (high < lastPrice.doubleValue() || high == 0) {
			high = lastPrice.doubleValue();
			updateTicker = true;
		}
		
		if (low > lastPrice.doubleValue() || low == 0) {
			low = lastPrice.doubleValue();
			updateTicker = true;
		}
		
		if (updateTicker) {
			bqLastTicker.put("high", high);
			bqLastTicker.put("low", low);
		}
		
		BigDecimal last = lastPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		bqLastTicker.put("last", last);
		spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
		
		vo.setTodayHigh(new BigDecimal(high + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最高
		vo.setTodayLow(new BigDecimal(low + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最低
		vo.setTradevols(new BigDecimal(vol + "").setScale(4, BigDecimal.ROUND_DOWN));
		vo.setTrades(new BigDecimal(trades + "").setScale(4, BigDecimal.ROUND_DOWN));
		

		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.getLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}

			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
		
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
//		if (btc_index_price == null) {
//			minIndexPrice = BigDecimal.ZERO;
//			maxIndexPrice = new BigDecimal(100000);
//		} else {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(new BigDecimal(1).add(rateUp));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
//		}
		
		if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
			minIndexPrice = oneDayMin;
		}
		
		if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
			maxIndexPrice = oneDayMax;
		}
		
//		if (bqTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
//		}

		vo.setMaxPrice(maxIndexPrice);
		vo.setMinPrice(minIndexPrice);
		
		BigDecimal first = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE);
		if (first == null) {
			first = tradeLogService.findFirstPrice(partition, bqTransaction.getTransaction_code(), TimeUtil.getTimesmorning());
			spyMemcachedClient.set(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
		}
		
		double firstPrice = 0.0;
		if (first != null) {
			firstPrice = first.doubleValue();
		}
		// 涨跌幅
		if (lastPrice.doubleValue() <= 0.0) {
			vo.setFudu("0%");
		} else {
			DecimalFormat df = new DecimalFormat("0.00");
			double rate = 0.0;
			if (firstPrice > 0) {
				rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
			}
			
			String div = df.format(rate *100);
			vo.setFudu(div.toString() + "%");
		}
		
		NFundingFeeSetting feeRateSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(partition, bqTransaction.getTransaction_code());
		if (feeRateSetting != null && feeRateSetting.getRunning()) {
			NFundingFeeRate feeRate = fundingFeeRateService.getLast(partition, bqTransaction.getTransaction_code());
			if (feeRate != null) {
				BigDecimal rate = feeRate.getFee_rate();
				if (rate != null) {
					if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
						rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
					} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
						rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
					}
					
					vo.setFundingFeeRate(rate);
					spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_RATE + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, rate);
				}
			}
		}

		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		for (int i = 0; i < multiples.length; i++) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multiplestates[i]), multiples[i]));
		}
		vo.setMultiples(listMulpitleAndStateVOs);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.COMMON_DATA + partition + "_" + code, BTCQMemecacheKeys.TIMES.ONE_HOUR, vo);
	}
}
