package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.BQMarkPriceSettingService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
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
@Component
public class DeliveryGetTradeDateTop5 {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private DeliveryTransactionTypeService transactionTypeService;
	@Autowired
	private DeliveryNotradetimeService bqNotradetimeService;
	@Autowired
	private DeliveryTransactionRateService transactionRateService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQMarkPriceSettingService bqMarkPriceSettingService;
	
	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			String bqCode = transaction.getTransaction_code();
			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.D_BTCCOEFFICIENT, BTCQMemecacheKeys.TIMES.BTCCOEFFICIENT,
					transaction.getTrade_coefficient());
			
			BigDecimal lastPrice = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.D_BTCLATESTPRICE);
			if (lastPrice == null) {
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, 
						transaction.getLatest_transaction_price());
			} else {
				
				DeliveryTransaction bqt_temp = transactionService.findTransToUpdateByCode(bqCode);
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
					
					transactionService.updateTrans(bqt_temp.getTransaction_id(), changeHigh, changeLow, lastPrice);
				}
			}
			
			getCommonData(transaction);
		}

	}
	
	private int getDigits(DeliveryTransaction trans) {
		Integer digits = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
		if (digits == null) {
			DeliveryTransactionType transType = transactionTypeService.getTransactionType(trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void getCommonData(DeliveryTransaction transaction) {
		
		String code = transaction.getTransaction_code();
		String typeCode = transaction.getTransaction_type_code();
		TradeAjaxVO vo = new TradeAjaxVO();
		BigDecimal lastPrice = BigDecimal.ZERO;
		BigDecimal coeffcient = BigDecimal.ZERO;
		BigDecimal btc_index_price = spyMemcachedClient.get(typeCode);
		if (btc_index_price == null) {
			BQPriceIndexCache priceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(typeCode);
			if (priceIndexCache != null) {
				btc_index_price = priceIndexCache.getPrice();
			}
		}
		
		if (btc_index_price == null) return;
		
		vo.setTypeCode(typeCode);
		String indexFudu = spyMemcachedClient.get(BTCQMemecacheKeys.INDEX_FUDU + typeCode);
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
		
		lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transaction.getLatest_transaction_price();
		}
		coeffcient = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCCOEFFICIENT);
		if (coeffcient == null) {
			coeffcient = transaction.getTrade_coefficient();
		}
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = new ArrayList<Top5DelegationVO>();
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = new ArrayList<Top5DelegationVO>();
		}
		digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_DIGITS);
		if (digits == null) {
			digits = getDigits(transaction);
		}
		
		List<BigDecimal> list = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE_LIST + code);
		BQMarkPriceSetting setting = bqMarkPriceSettingService.getByCode(code);
		if (setting == null) {
			setting = bqMarkPriceSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE_SETTING, BTCQMemecacheKeys.TIMES.BPI, setting);
			}
		} else {
			spyMemcachedClient.set(BTCQMemecacheKeys.MARK_PRICE_SETTING + code, BTCQMemecacheKeys.TIMES.BPI, setting);
		}
		
		if (setting != null && setting.isRunning()) {
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
					spyMemcachedClient.set(BTCQMemecacheKeys.D_MARK_PRICE_LIST + code, BTCQMemecacheKeys.TIMES.ONE_DAY, list);
				} else {
					markPrice = btc_index_price.add(markPriceDis);
					if (list != null) {
						spyMemcachedClient.delete(BTCQMemecacheKeys.D_MARK_PRICE_LIST + code);
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
					
					spyMemcachedClient.set(BTCQMemecacheKeys.D_MARK_PRICE + code, BTCQMemecacheKeys.TIMES.FIVE_MIN, markPrice);
					vo.setMarkPrice(markPrice.doubleValue() + "");
				}
			} while(false);
		} else {
			if (list != null) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.D_MARK_PRICE_LIST + code);
			}
			
			spyMemcachedClient.delete(BTCQMemecacheKeys.D_MARK_PRICE + code); 
		}
		
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_BTC + "_" + code);
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		rateUp = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP);
		rateDown = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			DeliveryTransactionRate index_price_rate = transactionRateService.getByCode(code);
			if (index_price_rate != null) {
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(code + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(code + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
		}
		
		vo.setTransactionKind(transaction.getTransaction_kind());
		vo.setShowName(transaction.getTransaction_show_name());
		vo.setShowNameEn(transaction.getTransaction_show_name_en());
		vo.setShowNameJp(transaction.getTransaction_show_name_jp());
		vo.setShowNameKr(transaction.getTransaction_show_name_kr());
		vo.setShowNameKr(transaction.getTransaction_show_name_kr());
		vo.setShowNameHk(transaction.getTransaction_show_name_hk());
		vo.setSuspend(transaction.getSuspend());
		// 交易时间 bqNotradetimeService判断
		vo.setTradeTime(true);
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			if (bqNotradetimeService != null) {
				if (!bqNotradetimeService.isTradeTime(transaction.getTransaction_id())) {
					vo.setTradeTime(false);
				}
			}
		}
		// 杠杆
		multiples = transaction.getMultiple().split(",");
		multiplestates = transaction.getMultiple_state().split(",");
		
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
			BigDecimal high = tradeLogService.findHighPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			BigDecimal low = tradeLogService.findLowPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			
			BigDecimal vol = tradeLogService.findBTCVol(code, time);
			vol = vol == null ? new BigDecimal(0) : vol;
			
			BigDecimal trades = tradeLogService.findTradeBTC( code, time);
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
			
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
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
			bqLastTicker.put("last", lastPrice.doubleValue());
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
		}
		
		vo.setTodayHigh(new BigDecimal(high + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最高
		vo.setTodayLow(new BigDecimal(low + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最低
		vo.setTradevols(new BigDecimal(vol + "").setScale(4, BigDecimal.ROUND_DOWN));
		vo.setTrades(new BigDecimal(trades + "").setScale(4, BigDecimal.ROUND_DOWN));
		

		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = tradeLogService.getLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}

			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
		
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price == null) {
			minIndexPrice = BigDecimal.ZERO;
			maxIndexPrice = new BigDecimal(100000);
		} else {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(new BigDecimal(1).add(rateUp));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		}
		
		if (oneDayMin != null && oneDayMin.compareTo(minIndexPrice) > 0) {
			minIndexPrice = oneDayMin;
		}
		
		if (oneDayMax != null && oneDayMax.compareTo(maxIndexPrice) < 0) {
			maxIndexPrice = oneDayMax;
		}
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}

		vo.setMaxPrice(maxIndexPrice);
		vo.setMinPrice(minIndexPrice);
		
		BigDecimal first = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE);
		if (first == null) {
			first = tradeLogService.findFirstPrice(transaction.getTransaction_code(), TimeUtil.getTimesmorning());
			spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
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
		
		/*BQFundingFeeSetting feeRateSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(transaction.getTransaction_code());
		if (feeRateSetting != null && feeRateSetting.getRunning()) {
			FundingFeeRate feeRate = fundingFeeRateService.getLast(transaction.getTransaction_code());
			if (feeRate != null) {
				BigDecimal rate = feeRate.getFee_rate();
				if (rate != null) {
					if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
						rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
					} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
						rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
					}
					
					vo.setFundingFeeRate(rate);
					spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_RATE + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.FIVE_MIN, rate);
				}
			}
		}*/

		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		for (int i = 0; i < multiples.length; i++) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multiplestates[i]), multiples[i]));
		}
		vo.setMultiples(listMulpitleAndStateVOs);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.D_COMMON_DATA + code, BTCQMemecacheKeys.TIMES.ONE_HOUR, vo);
	}
}
