package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQIndexState;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQIndexStateService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.timerTask.common.CommonDataAllcoin;
import com.btc100.btc_q.timerTask.common.CommonDataBiki;
import com.btc100.btc_q.timerTask.common.CommonDataBinance;
import com.btc100.btc_q.timerTask.common.CommonDataBitfinex;
import com.btc100.btc_q.timerTask.common.CommonDataFCoin;
import com.btc100.btc_q.timerTask.common.CommonDataGateio;
import com.btc100.btc_q.timerTask.common.CommonDataHuobi;
import com.btc100.btc_q.timerTask.common.CommonDataKuCoin;
import com.btc100.btc_q.timerTask.common.CommonDataMarket;
import com.btc100.btc_q.timerTask.common.CommonDataMxc;
import com.btc100.btc_q.timerTask.common.CommonDataOkcoin;
import com.btc100.btc_q.timerTask.common.CommonDataZB;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskBPI {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private BQIndexStateService bqIndexStateService;
	
	private static final Logger logger = Logger.getLogger(TimerTaskBPI.class);
	
	private static final String[] COINS = {"USDT", "CKUSD"};
	private static final CommonDataMarket[] MARKETS = { new CommonDataOkcoin(), new CommonDataHuobi(),
			new CommonDataBitfinex(), new CommonDataBinance(), new CommonDataFCoin(), new CommonDataAllcoin(),
			new CommonDataZB(), new CommonDataGateio(), new CommonDataMxc(), new CommonDataBiki(),
			new CommonDataBitfinex(), new CommonDataKuCoin() };

	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		BQIndexState indexState = spyMemcachedClient.get("index_state_setting");
		if (indexState == null) {
			indexState = bqIndexStateService.get();
			if (indexState == null) {
				indexState = new BQIndexState();
			}
			
			spyMemcachedClient.set("index_state_setting", BTCQMemecacheKeys.TIMES.TEN_SECOND, indexState);
		}

		
		for (String contract : contractArray) {
			if (StringUtil.isEmpty(contract)) {
				continue;
			}

			BQTransaction bqTransaction = transactionService.findTransByCode(contract);
			if (bqTransaction == null || bqTransaction.getTransaction_kind() != 0) continue;
			
			String typeCode = bqTransaction.getTransaction_type_code();
			Integer digits = spyMemcachedClient.get(typeCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				BQTransactionType transType = null;
				List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
				if (transTypes == null) {
					transType = transTypeService.getTransTypeByCode(typeCode);
				} else {
					for (BQTransactionType type : transTypes) {
						if (type.getType_code().equalsIgnoreCase(typeCode)) {
							transType = type;
						}
					}
				}
				
				if (transType == null) continue;
				
				digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
				spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			doGetIndex(bqTransaction.getTransaction_type(), typeCode, digits, indexState);
		}
	}
	
	private void doGetIndex(int typeId, String typeCode, int digits, BQIndexState indexState) {
		List<TickerVo> tickers = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER + typeCode);
		if (tickers == null) tickers = new ArrayList<TickerVo>();
		
		for (String coin : COINS) {
			if (typeCode.toUpperCase().equals(coin)) continue;
			if (typeCode.toLowerCase().equals("btc") && !coin.equals("USDT")) continue;
			if (typeCode.toLowerCase().equals("ft") && !coin.equals("USDT")) continue;
			if (!typeCode.toLowerCase().equals("alc") && coin.equals("CKUSD")) continue;
			if (typeCode.toLowerCase().equals("alc") && !coin.equals("CKUSD")) continue;

			String transPair = typeCode.toUpperCase() + "/" + coin;
			for (CommonDataMarket market : MARKETS) {
				if (!coin.equals("CKUSD")) {
					if (!coin.equals("USDT")) {
						continue;
					}
				}
				
				
				if (market.getClass().equals(CommonDataHuobi.class) && indexState.getHuobi_state() == 0) {
					for (TickerVo vo : tickers) {
						if (vo.getMarket().equals("Huobi-Pro")) {
							tickers.remove(vo);
						}
						break;
					}
					continue;
				}
				
				if (market.getClass().equals(CommonDataBinance.class) && indexState.getBinance_state() == 0) {
					for (TickerVo vo : tickers) {
						if (vo.getMarket().equals("Binance")) {
							tickers.remove(vo);
						}
						break;
					}
					continue;
				}
				
				if (market.getClass().equals(CommonDataOkcoin.class) && indexState.getOkex_state() == 0) {
					for (TickerVo vo : tickers) {
						if (vo.getMarket().equals("OKEX")) {
							tickers.remove(vo);
						}
						break;
					}
					continue;
				}
				
				if (typeCode.toLowerCase().equals("ht") && !market.getClass().equals(CommonDataHuobi.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("tt") && !market.getClass().equals(CommonDataHuobi.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("rsr") && !market.getClass().equals(CommonDataHuobi.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("alv") && !market.getClass().equals(CommonDataOkcoin.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataFCoin.class) && !typeCode.toLowerCase().equals("ft") && !typeCode.toLowerCase().equals("fmex")) {
					continue;
				}
				
				if ((typeCode.toLowerCase().equals("ft") || typeCode.toLowerCase().equals("fmex"))
						&& !market.getClass().equals(CommonDataFCoin.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataAllcoin.class) && !typeCode.toLowerCase().equals("alc")) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("alc") && !market.getClass().equals(CommonDataAllcoin.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("bnb") && !market.getClass().equals(CommonDataBinance.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("okb") && !market.getClass().equals(CommonDataOkcoin.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("zb") && !market.getClass().equals(CommonDataZB.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataZB.class) && !typeCode.toLowerCase().equals("zb")) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("gt") && !market.getClass().equals(CommonDataGateio.class)
						&& !market.getClass().equals(CommonDataHuobi.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("mbl") && !market.getClass().equals(CommonDataGateio.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("gmat") && !market.getClass().equals(CommonDataGateio.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataGateio.class) && !typeCode.toLowerCase().equals("gt") 
						&& !typeCode.toLowerCase().equals("mbl") && !typeCode.toLowerCase().equals("gmat")) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("bsv") && market.getClass().equals(CommonDataBinance.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("vds")) {
					if (!market.getClass().equals(CommonDataMxc.class) && !market.getClass().equals(CommonDataBiki.class)) {
						continue;
					}
				}
				
				if (market.getClass().equals(CommonDataBiki.class) && !typeCode.toLowerCase().equals("vds") && !typeCode.toLowerCase().equals("biki")) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataMxc.class) && !typeCode.toLowerCase().equals("vds") && !typeCode.toLowerCase().equals("mx")) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("mx") && !market.getClass().equals(CommonDataMxc.class)) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("biki") && !market.getClass().equals(CommonDataBiki.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataBitfinex.class) && !typeCode.toLowerCase().equals("leo")) {
					continue;
				}
				
				if (!market.getClass().equals(CommonDataBitfinex.class) && typeCode.toLowerCase().equals("leo")) {
					continue;
				}
				
				if (typeCode.toLowerCase().equals("kcs") && !market.getClass().equals(CommonDataKuCoin.class)) {
					continue;
				}
				
				if (market.getClass().equals(CommonDataKuCoin.class) && !typeCode.toLowerCase().equals("kcs")) {
					continue;
				}
				
				TickerVo ticker = null;
				try {
					ticker = market.findTicker(typeCode, coin.toLowerCase());
				} catch (Exception e) {
					logger.error("getTick - " + market.getClass() + " error: " + transPair);
				}

					if (ticker != null && ticker.getLast().compareTo(BigDecimal.ZERO) > 0) {
					List<TickerVo> toRemove = new ArrayList<TickerVo>();
					for (TickerVo vo : tickers) {
						if (typeCode.equals(vo.getTypeCode()) && ticker.getMarket().equals(vo.getMarket())) {
							toRemove.add(vo);
						}
					}
					
					tickers.removeAll(toRemove);
					ticker.setTypeId(typeId);
					ticker.setTypeCode(typeCode);
					tickers.add(ticker);
				}
			}
		}
		
		long currTime = System.currentTimeMillis() / 1000;
		BigDecimal sumVol = BigDecimal.ZERO;
		List<TickerVo> toRemove = new ArrayList<TickerVo>();
		for (TickerVo vo : tickers) {
			String payCoin = vo.getTrans_pair().split("/")[1];
			if (payCoin.equals("USDT") || payCoin.equals("USD") || payCoin.equals("CKUSD")) {
				vo.setLastUsd(vo.getLast());	// USD
			} else {
				BigDecimal payCoinPrice = spyMemcachedClient.get(payCoin.toLowerCase() + vo.getMarket());
				if (payCoinPrice == null || payCoinPrice.compareTo(BigDecimal.ZERO) <= 0) {
					toRemove.add(vo);
					continue;
				}
				vo.setLastUsd(vo.getLast().multiply(payCoinPrice));
			}
			
			long timeDiff = currTime - vo.getTime();
			double rate = 1.0;
			if (timeDiff > 3 * 60) {
				rate = 0.0;
			}
			
			vo.setRatio(new BigDecimal(rate));
			if (vo.getRatio().compareTo(BigDecimal.ZERO) <= 0) {
				toRemove.add(vo);
			} else {
				sumVol = sumVol.add(BigDecimal.ONE);
			}
		}
		
		if (toRemove.size() > 0) {
			tickers.removeAll(toRemove);
		}
		
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal ratios = BigDecimal.ZERO;
		if (sumVol.compareTo(BigDecimal.ZERO) > 0) {
			for (TickerVo vo : tickers) {
				BigDecimal ratio = vo.getRatio().divide(sumVol, 8, BigDecimal.ROUND_HALF_UP);
				vo.setRatio(ratio);
				ratios = ratios.add(ratio);
				price = price.add(vo.getRatio().multiply(vo.getLastUsd()));
			}

			price = price.setScale(digits, BigDecimal.ROUND_HALF_UP);	
			for (TickerVo vo : tickers) {
				spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, vo.getLastUsd());
				bqPriceIndexCacheService.updateBtcCache(vo, price);
			}
			
			spyMemcachedClient.set(typeCode, BTCQMemecacheKeys.TIMES.BPI, price);
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + typeCode, BTCQMemecacheKeys.TIMES.BPI, tickers);
			
			long morning = TimeUtil.getTimesmorning();
			BigDecimal morningIndex = spyMemcachedClient.get(typeCode + "_" + String.valueOf(morning));
			if (morningIndex == null || morningIndex.compareTo(BigDecimal.ZERO) <= 0) {
				morningIndex = price;
				spyMemcachedClient.set(typeCode + "_" + String.valueOf(morning), BTCQMemecacheKeys.TIMES.ONE_DAY + BTCQMemecacheKeys.TIMES.ONE_HOUR, morningIndex);
				spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, "0.00%");
			} else {
				BigDecimal indexFudu = price.subtract(morningIndex).divide(morningIndex, 4, BigDecimal.ROUND_HALF_UP);
				DecimalFormat df = new DecimalFormat("0.00");
				String fudu = df.format(indexFudu.doubleValue() * 100) + "%";
				spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, fudu);
			}
		}
	}
}
