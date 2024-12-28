package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.VFundingFeeRate;
import com.btc100.btc_q.entity.VFundingFeeSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.VFundingFeeRateService;
import com.btc100.btc_q.service.VFundingFeeSettingService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionRateService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
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
public class VGetTradeDateTop5 {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private VTransactionRateService vTransactionRateService;
	@Autowired
	private BQTransactionTypeService transactionTypeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private VFundingFeeRateService fundingFeeRateService;
	@Autowired
	private VFundingFeeSettingService bqFundingFeeSettingService;
	
	public void run() {
		
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransactionList);

		for (VTransaction vTransaction : vTransactionList) {
			String vCode = vTransaction.getTransaction_code();
			spyMemcachedClient.set(vCode + BTCQMemecacheKeys.V_BTCCOEFFICIENT, BTCQMemecacheKeys.TIMES.BTCCOEFFICIENT,
					vTransaction.getTrade_coefficient());
			spyMemcachedClient.set(vCode + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, 
					vTransaction.getLatest_transaction_price());
			
			getCommonData(vTransaction);
		}
	}
	
	private int getDigits(VTransaction trans) {
		Integer digits = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS);
		if (digits == null) {
			VTransactionType transType = vTransactionTypeService.getTransactionType(trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void getCommonData(VTransaction trans) {
		
		String code = trans.getTransaction_code();
		String tranCode = trans.getTransaction_type_code();
		TradeAjaxVO vo = new TradeAjaxVO();
		BigDecimal lastPrice = BigDecimal.ZERO;
		BigDecimal coeffcient = BigDecimal.ZERO;
		BigDecimal btc_index_price = spyMemcachedClient.get(code);
		if (btc_index_price == null) {
			BQTransactionType transType = transactionTypeService.getTransTypeByCode(tranCode);
			if (transType == null) {
				btc_index_price = BigDecimal.ZERO;
			} else {
				btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
			}
		}
		
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
		lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = trans.getLatest_transaction_price();
		}
		coeffcient = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_BTCCOEFFICIENT);
		if (coeffcient == null) {
			coeffcient = trans.getTrade_coefficient();
		}
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = new ArrayList<Top5DelegationVO>();
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = new ArrayList<Top5DelegationVO>();
		}
		digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_DIGITS);
		if (digits == null) {
			digits = getDigits(trans);
		}
		
		List<BigDecimal> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE_LIST + code);
		BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
		if (setting != null && setting.isRunning()) {
			do {
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
					if (setting.getIndex_rate_filter().compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal max = btc_index_price.multiply(BigDecimal.ONE.add(setting.getIndex_rate_filter()));
						BigDecimal min = btc_index_price.multiply(BigDecimal.ONE.subtract(setting.getIndex_rate_filter()));
						
						if (markPrice.compareTo(max) > 0 || markPrice.compareTo(min) < 0) {
							break;
						}
					}
					
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
					spyMemcachedClient.set(BTCQMemecacheKeys.V_MARK_PRICE_LIST + code, BTCQMemecacheKeys.TIMES.ONE_DAY, list);
				} else {
					markPrice = btc_index_price.add(markPriceDis);
					if (list != null) {
						spyMemcachedClient.delete(BTCQMemecacheKeys.V_MARK_PRICE_LIST + code);
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
					
					spyMemcachedClient.set(BTCQMemecacheKeys.V_MARK_PRICE + code, BTCQMemecacheKeys.TIMES.FIVE_MIN, markPrice);
					vo.setMarkPrice(markPrice.doubleValue() + "");
				}
			} while(false);
		} else {
			if (list != null) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.V_MARK_PRICE_LIST + code);
			}
			
			spyMemcachedClient.delete(BTCQMemecacheKeys.V_MARK_PRICE + code); 
		}
		
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC + "_" + code);
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		rateUp = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_INDEX_PRICE_RATE_UP);
		rateDown = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			VTransactionRate index_price_rate = vTransactionRateService.getByCode(trans.getTransaction_code());
			rateDown = index_price_rate.getDownrate();
			rateUp = index_price_rate.getUprate();
			
			spyMemcachedClient.set(code + BTCQMemecacheKeys.V_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.V_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
		}
		
		vo.setSuspend(trans.getSuspend());
		vo.setTradeTime(true);
		// 杠杆
		multiples = trans.getMultiple().split(",");
		multiplestates = trans.getMultiple_state().split(",");
		
		vo.setBuytop5List(buytop5List);
		vo.setSelltop5List(selltop5List);
		vo.setLastPrice(lastPrice);
		vo.setTransCoeffcient(coeffcient);
		vo.setBtc_index_price(btc_index_price); // 当前价格指数
		vo.setIndexPrice(btc_index_price);
		vo.setShowName(code.toUpperCase());
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
			BigDecimal high = vTradeLogService.findHighPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			BigDecimal low = vTradeLogService.findLowPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			
			BigDecimal vol = vTradeLogService.findBTCVol(code, time);
			vol = vol == null ? new BigDecimal(0) : vol;
			
			BigDecimal trades = vTradeLogService.findTradeBTC( code, time);
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
			
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
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
			
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
		}
		
		BigDecimal first = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.V_FIRST_PRICE);
		if (first == null) {
			first = vTradeLogService.findFirstPrice(trans.getTransaction_code(), TimeUtil.getTimesmorning());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.V_FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
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
		
		vo.setTodayHigh(new BigDecimal(high + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最高
		vo.setTodayLow(new BigDecimal(low + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最低
		vo.setTradevols(new BigDecimal(vol + "").setScale(4, BigDecimal.ROUND_DOWN));
		vo.setTrades(new BigDecimal(trades + "").setScale(4, BigDecimal.ROUND_DOWN));
		
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(new BigDecimal(1).add(rateUp));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);

		vo.setMaxPrice(maxIndexPrice);
		vo.setMinPrice(minIndexPrice);
		
		VFundingFeeSetting feeRateSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(trans.getTransaction_code());
		if (feeRateSetting != null && feeRateSetting.getRunning()) {
			VFundingFeeRate feeRate = fundingFeeRateService.getLast(trans.getTransaction_code());
			if (feeRate != null) {
				BigDecimal rate = feeRate.getFee_rate();
				if (rate != null) {
					if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
						rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
					} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
						rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
					}
					
					vo.setFundingFeeRate(rate);
					spyMemcachedClient.set(BTCQMemecacheKeys.V_FUNDING_FEE_RATE + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.FIVE_MIN, rate);
				}
			}
		}

		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		for (int i = 0; i < multiples.length; i++) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multiplestates[i]), multiples[i]));
		}
		vo.setMultiples(listMulpitleAndStateVOs);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.V_COMMON_DATA + code, BTCQMemecacheKeys.TIMES.ONE_HOUR, vo);
	}
}
