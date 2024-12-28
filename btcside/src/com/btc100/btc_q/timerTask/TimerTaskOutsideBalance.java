package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQHedgingBalanceNotice;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQHedgingBalanceNoticeService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.SendMail;

public class TimerTaskOutsideBalance {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQHedgingBalanceNoticeService bqHedgingBalanceNoticeService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NTransactionService nTransactionService;
	
	Logger logger = Logger.getLogger(TimerTaskOutsideBalance.class);
	private static HuobiApiUtil exchangeApiUtil = null;
	
	public void run() {
		List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if(list == null) {
			list = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		BigDecimal usdtBalance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		sendNoticeEmail(1, "usdt", usdtBalance, "Huobi");
			
		List<String> codes = new ArrayList<String>();
		for(BQTransaction trans :list) {
			String code = trans.getTransaction_code();
			codes.add(code);
			if (!"ft".equals(code) && !"elf".equals(code)) {
				BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + code);
				sendNoticeEmail(1, code, balance, "Huobi");
			}
		}
		
		List<NContractPartition> partitions = nContractPartitionService.findAll();
		for (NContractPartition partition : partitions) {
			List<NTransaction> list1 = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
			if(list1 == null) {
				list1 = nTransactionService.findTransByState_new(partition.getPart(), Contents.TRANSACTION_STATE_ZC);
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list1);
			}
			
			for(NTransaction trans : list1) {
				String code = trans.getTransaction_code();
				if (codes.contains(code)) continue;
				
				codes.add(code);
				if (!"ft".equals(code) && !"elf".equals(code)) {
					BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + code);
					sendNoticeEmail(1, code, balance, "Huobi");
				}
			}
		}
		
		BigDecimal ftBalance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + "ft");
		sendNoticeEmail(1, "ft", ftBalance, "Fcoin");
		
		BigDecimal elfBalance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + "elf");
		sendNoticeEmail(1, "elf", elfBalance, "Fcoin");
		
		BigDecimal fcoinBalance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		sendNoticeEmail(1, "usdt", fcoinBalance, "Fcoin");
		
		BigDecimal elfusdtBalance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		sendNoticeEmail(1, "usdt", elfusdtBalance, "ELF");

		
		Map<String, BigDecimal> map = null;
		if (exchangeApiUtil == null) {
			try {
				exchangeApiUtil = new HuobiApiUtil(UrlConfig.getValue("ExchangeHuobiSecretKey"), UrlConfig.getValue("ExchangeHuobiAccessKey"));
				exchangeApiUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		try {
			map = exchangeApiUtil.getBalance();
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
		}
		
		if (map != null) {
			List<BQCurrencyType> coins = bqCurrencyTypeService.findAll();
			for (BQCurrencyType currency : coins) {
				String currencyTypeName = currency.getType_name();
				if (!"BFX".equalsIgnoreCase(currencyTypeName) && !"BFC".equalsIgnoreCase(currencyTypeName)
						&& !("USD").equalsIgnoreCase(currencyTypeName)) {
					if (map.containsKey(currencyTypeName)) {
						sendNoticeEmail(2, currencyTypeName, map.get(currencyTypeName), "Huobi");
					}
				}
			}
		}
		
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		for (BQSpotMarketCoinPair pair : coinPairs) {
			String coinName = pair.getCoin_name().toLowerCase();
			BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + coinName);
			if ("okb".equals(coinName)) {
				sendNoticeEmail(3, coinName, balance, "Okex");
			} else {
				sendNoticeEmail(3, coinName, balance, "Huobi");
			}
		}
		List<BQSpotMarket> markets = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_MARKET);
		if (markets == null) {
			markets = bqSpotMarketService.findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_MARKET,BTCQMemecacheKeys.TIMES.ONE_MIN, markets);
		}
		for (BQSpotMarket market : markets) {
			String marketName = market.getMarket_name().toLowerCase();
			BigDecimal spotUsdtBalance = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + marketName);
			sendNoticeEmail(3, marketName, spotUsdtBalance, "Huobi");
			
			spotUsdtBalance = spyMemcachedClient.get("okex_" + BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + marketName);
			sendNoticeEmail(3, marketName, spotUsdtBalance, "Okex");
		}
		
	}
	
	private void sendNoticeEmail(final int type, final String code, final BigDecimal balance, final String market) {
		if (balance == null) {
			return;
		}
		
		BQHedgingBalanceNotice notice = bqHedgingBalanceNoticeService.getByTypeAndCoinName(type, code);
		if (notice != null) {
			final BigDecimal threshold = notice.getThreshold();
			final String email = notice.getEmail();
			if (notice.getThreshold().compareTo(balance) > 0) {
				Integer sendCount = spyMemcachedClient.get("check_balance_count_" + notice.getId());
				if (sendCount == null) {
					sendCount = 0;
				}
				if (sendCount >= 2) {
					return;
				}
				
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							SendMail themail = new SendMail();
							String content = market + "合约对冲账户余额:";
							if (type == 2) {
								content = market + "币币兑换账户余额:";
							} else if (type == 3) {
								content = market + "币币交易的对冲账户余额:";
							}
							String content1 = "，低于设定值:";
							themail.send(email, code + content + balance.doubleValue() + content1 + threshold.doubleValue());
						} catch (Exception e) {
						}
					}
				};
				
				Thread thread = new Thread(runnable);
				thread.start();
				sendCount += 1;
				
				spyMemcachedClient.set("check_balance_count_" + notice.getId(), BTCQMemecacheKeys.TIMES.BPI, sendCount);
			} else {
				spyMemcachedClient.set("check_balance_count_" + notice.getId(), BTCQMemecacheKeys.TIMES.BPI, 0);
			}
		}
	}

}
