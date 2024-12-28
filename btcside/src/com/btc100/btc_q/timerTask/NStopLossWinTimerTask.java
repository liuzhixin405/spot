package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.entity.NStopLossLog;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.NStopLossLogService;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 *  止盈止损服务
 *  //TODO:在交割的时候把合约相关的止盈止损表的记录删除掉
 * @author gyl
 *
 */
public class NStopLossWinTimerTask extends BaseMemberLevelTask {
	Logger logger = Logger.getLogger(NStopLossWinTimerTask.class);
	
	@Autowired
	private NStopLossService bqNStopLossService;
	@Autowired
	private NStopLossLogService bqNStopLossLogService;
	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private NTransactionRateService bqTransactionRateService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NAccountContractService bqAccountContractService;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NTransactionFeeRateService bqTransactionFeeRateService;
	
	public static String[] contractArray = null;
	
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
			
			NTransaction bqt = transactionService.findTransByCode(partition, contract);
			if (bqt == null) continue;
			
			if (bqt.getSuspend()) {
				continue;
			}
			
			if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(partition, bqt.getTransaction_id())) {
					continue;
				}
			}
			
			BigDecimal btc_index_price = spyMemcachedClient.get(bqt.getTransaction_type_code());
			if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqt.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			}  else if (bqt.getTransaction_kind() == 2) {
//				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqt.getTransaction_code());
//				if (btc_index_price == null) {
//					btc_index_price = tradeLogService.getLastHourAgoByCode(partition, bqt.getTransaction_code());
//					if (btc_index_price != null) {
//						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqt.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//					}
//				}
			} else {
				if (btc_index_price == null) {
					BQPriceIndexCache cache = bqPriceIndexCacheService.getBtcIndexPrice(bqt.getTransaction_type_code());
					if (cache != null) {
						btc_index_price = cache.getPrice();
					} else {
						btc_index_price = BigDecimal.ZERO;
					}
				}
			}
			
			// 获取标记价格
			BigDecimal markPrice = btc_index_price;
			NMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition + "_" + bqt.getTransaction_code());
			if (setting == null) {
				setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition);
			}
			if (setting != null && setting.isRunning()) {
				markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + bqt.getTransaction_code());
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			BigDecimal lastDonePrice = spyMemcachedClient.get("last_price_stop_loss_win_" + partition + "_" + bqt.getTransaction_code());
			if (lastDonePrice != null && lastDonePrice.compareTo(markPrice) == 0) {
				continue;
			}
			
			spyMemcachedClient.set("last_price_stop_loss_win_" + partition + "_" + bqt.getTransaction_code(), BTCQMemecacheKeys.TIMES.ONE_MIN, markPrice);
			
			List<NStopLoss> stopWinManyList = bqNStopLossService.getStopWinByPrice(partition, markPrice,Contents.TRADE_TYPE_BUY, bqt.getTransaction_code());
			List<NStopLoss> stopLossManyList = bqNStopLossService.getStopLossByPrice(partition, markPrice,Contents.TRADE_TYPE_BUY, bqt.getTransaction_code());
			
			List<NStopLoss> stopWinEmptyList = bqNStopLossService.getStopWinByPrice(partition, markPrice,Contents.TRADE_TYPE_SELL, bqt.getTransaction_code());
			List<NStopLoss> stopLossEmptyList = bqNStopLossService.getStopLossByPrice(partition, markPrice,Contents.TRADE_TYPE_SELL, bqt.getTransaction_code());
			
			NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqt.getTransaction_code());
			if(btc_index_price == null || btc_index_price.signum()<=0){
				continue;
			}
			if(index_price_rate == null){
				continue;
			}
			
			NTransactionType transType = null;
			List<NTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
			if (types == null) {
				types = transTypeService.findAllUsing(partition);
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
			}
			
			if (types == null) break;
			
			for (NTransactionType type : types) {
				if (type.getType_id() == bqt.getTransaction_type()) {
					transType = type;
					break;
				}
			}
			
			if (transType == null) continue;
			int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			
			for(NStopLoss sl : stopWinManyList){
				handUp(partition, btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(NStopLoss sl : stopLossManyList){
				handUp(partition, btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(NStopLoss sl : stopWinEmptyList){
				handUp(partition, btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(NStopLoss sl : stopLossEmptyList){
				handUp(partition, btc_index_price,index_price_rate,sl,bqt, digits);
			}
			
		}
		
	}
	
	
	private void handUp(String partition, BigDecimal btc_index_price,NTransactionRate index_price_rate,NStopLoss sl, NTransaction bqt, int digits) {
		NMemberTransaction  bqmt = bqMemberTransactionService.get(partition, sl.getTransaction_member_id());
		NStopLossLog log = null;
		BigDecimal pendingAmount = sl.getPending_amount();
		if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (pendingAmount.compareTo(bqmt.getHold_count()) <= 0) {
				try {
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						log = tradePDC(partition, btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						log = tradePKC(partition, btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log = new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，执行失败");
				}
			} else {
				log = new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，止盈止损设置挂单数量大于持仓数量");
			}
		} else { // 针对原有未设置挂单数量的止盈止损，全仓平
			if (bqmt.getHold_count().signum() > 0) {
				try {
					pendingAmount = bqmt.getHold_count();
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						log = tradePDC(partition, btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						log = tradePKC(partition, btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log = new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，执行失败");
				}
			} else {
				log = new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，持仓数量为0");
			}
		}
		
		if (log != null) {
			bqNStopLossLogService.saveNStopLossLog(partition, log);
		}
		
		//删除止盈止损记录
		bqNStopLossService.deleteByMid(partition, sl.getTransaction_member_id(), Contents.STOP_LOSS_LOG_ACTION_SC, "触发成功或失败后，删除持仓对应的止盈止损单");
	}
	
	private NStopLossLog tradePDC(String partition, BigDecimal btc_index_price, NTransactionRate index_price_rate,
			NTransaction bqTransaction, NMemberTransaction trans, NStopLoss sl, BigDecimal amount_client, int digits) {

		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("contract", bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("多单，止盈止损挂卖单失败，insertPCD调用失败！！无市价:" + bqTransaction.getTransaction_code());
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			logger.info("多单，止盈止损挂单失败，amount_client.signum() <= 0 || price_client.signum() <= 0，userId:" 
					+ trans.getF_user_id() + ", price: " + price_client.doubleValue());
			
			return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue());
		}
	
		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，用户不存在，userid: " + trans.getF_user_id());
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(partition, trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
//				if (oneDayMin.compareTo(minIndexPrice) > 0) {
					minIndexPrice = oneDayMin;
//				}
			}
			
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
//			}
		}
		
		if (price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		NAccountContract account = bqAccountContractService.findBTCByUidCode(partition, trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, trans.getAverage_price(), deposit, feeRate, 
				trans.getHold_count().add(trans.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) < 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.UP);
				price_client = bcj;
			} else {
				logger.info("多单，止盈止损挂单失败，价格小于爆仓价，userId:" + trans.getF_user_id() + ", 挂单价格: " + price_client.doubleValue()
				+ ", 爆仓价：" + bcj.doubleValue());
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，价格小于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue());
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
		
		int tryTimes = 0;
		boolean lock = bqMemberTransactionService.lockToTrade(partition, trans.getMt_id());
		while (!lock && tryTimes < 30) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tryTimes++;
			lock = bqMemberTransactionService.lockToTrade(partition, trans.getMt_id());
		}
		
		if (lock) {
			try {
				pendingDelegationCacheService.insertPCD(partition, cahce, trans.getMt_id(), bqTransaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_CG, "触发成功 ");
			} catch (Exception e) {
				logger.error("多单，止盈止损挂卖单失败，insertPCD调用失败！！userId:" + trans.getF_user_id());
				e.printStackTrace();
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, trans.getMt_id());
			}
		} else {
			logger.info("多单，止盈止损挂单失败，锁仓失败，userId:" + trans.getF_user_id());
			return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，锁仓失败");
		}
	}

	private NStopLossLog tradePKC(String partition, BigDecimal btc_index_price, NTransactionRate index_price_rate,
			NTransaction bqTransaction, NMemberTransaction trans, NStopLoss sl, BigDecimal amount_client, int digits) {

		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("contract", bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("空单，止盈止损挂买单失败，insertPKC调用失败！！无市价:" + bqTransaction.getTransaction_code());
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPKC调用失败");
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			logger.info("空单，止盈止损挂单失败，amount_client.signum() <= 0 || price_client.signum() <= 0，userId:" 
					+ trans.getF_user_id() + ", price: " + price_client.doubleValue());
			return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue());
		}
	
		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，用户不存在，userid: " + trans.getF_user_id());
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(partition, trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
//				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
//				}
			}
			
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
		}
		
		if (price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		// 爆仓价
		NAccountContract account = bqAccountContractService.findBTCByUidCode(partition, trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, trans.getAverage_price(), deposit, feeRate, 
				trans.getHold_count().add(trans.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) > 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.DOWN);
				price_client = bcj;
			} else {
				logger.info("空单，止盈止损挂单失败，价格大于爆仓价，userId:" + trans.getF_user_id() + ", 挂单价格: " + price_client.doubleValue()
				+ ", 爆仓价：" + bcj.doubleValue());
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，价格大于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue());
			}
		}
		
		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		int tryTimes = 0;
		boolean lock = bqMemberTransactionService.lockToTrade(partition, trans.getMt_id());
		while (!lock && tryTimes < 20) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tryTimes++;
			lock = bqMemberTransactionService.lockToTrade(partition, trans.getMt_id());
		}
		
		if (lock) {
			try {
				pendingDelegationCacheService.insertPCK(partition, cahce, trans.getMt_id(), bqTransaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_CG, "触发成功 ");
			} catch (Exception e) {
				logger.error("空单，止盈止损挂买单失败，insertPCD调用失败！！userId:" + trans.getF_user_id());
				return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, trans.getMt_id());
			}
		} else {
			logger.info("空单，止盈止损挂单失败，锁仓失败，userId:" + trans.getF_user_id());
			return new NStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，锁仓失败");
		}
	}
	
}
