package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryStopLoss;
import com.btc100.btc_q.entity.DeliveryStopLossLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryStopLossLogService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.Utils;

/**
 *  止盈止损服务
 *  //TODO:在交割的时候把合约相关的止盈止损表的记录删除掉
 * @author gyl
 *
 */
@Component
public class DeliveryStopLossWinTimerTask extends BaseMemberLevelTask {
	Logger logger = Logger.getLogger(DeliveryStopLossWinTimerTask.class);
	
	@Autowired
	private DeliveryStopLossService stopLossService;
	@Autowired
	private DeliveryStopLossLogService stopLossLogService;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private DeliveryTransactionRateService transactionRateService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryAccountContractService accountContractService;
	@Autowired
	private DeliveryPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private DeliveryMemberTransactionService memberTransactionService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private DeliveryNotradetimeService dNotradetimeService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private DeliveryTransactionFeeRateService transactionFeeRateService;
	
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
			
			if (transaction.getSuspend() || transaction.getTransaction_state() != 1) {
				continue;
			}
			
			if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
				if (!dNotradetimeService.isTradeTime(transaction.getTransaction_id())) {
					continue;
				}
			}
			
			// 获取标记价格
			BigDecimal markPrice = spyMemcachedClient.get(transaction.getTransaction_type_code());
			if (transaction.getTransaction_kind() == 2) {
				BigDecimal lastPrice = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					markPrice = lastPrice;
				}
			} else {
				BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + transaction.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
				}
				if (setting != null && setting.isRunning()) {
					markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + transaction.getTransaction_code());
				}
				
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			BigDecimal lastDonePrice = spyMemcachedClient.get("d_last_price_stop_loss_win_" + transaction.getTransaction_code());
			if (lastDonePrice != null && lastDonePrice.compareTo(markPrice) == 0) {
				continue;
			}
			
			spyMemcachedClient.set("d_last_price_stop_loss_win_" + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.ONE_MIN, markPrice);
			
			List<DeliveryStopLoss> stopWinManyList = stopLossService.getStopWinByPrice(markPrice,Contents.TRADE_TYPE_BUY, transaction.getTransaction_code());
			List<DeliveryStopLoss> stopLossManyList = stopLossService.getStopLossByPrice(markPrice,Contents.TRADE_TYPE_BUY, transaction.getTransaction_code());
			
			List<DeliveryStopLoss> stopWinEmptyList = stopLossService.getStopWinByPrice(markPrice,Contents.TRADE_TYPE_SELL, transaction.getTransaction_code());
			List<DeliveryStopLoss> stopLossEmptyList = stopLossService.getStopLossByPrice(markPrice,Contents.TRADE_TYPE_SELL, transaction.getTransaction_code());
			
			BigDecimal btc_index_price = null;
			if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			}  else if (transaction.getTransaction_kind() == 2) {
				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
				if (btc_index_price == null) {
					btc_index_price = tradeLogService.getLastHourAgoByCode(transaction.getTransaction_code());
					if (btc_index_price != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
					}
				}
			} else {
				btc_index_price = spyMemcachedClient.get(transaction.getTransaction_type_code());
				if (btc_index_price == null) {
					BQPriceIndexCache cache = bqPriceIndexCacheService.getBtcIndexPrice(transaction.getTransaction_type_code());
					if (cache != null) {
						btc_index_price = cache.getPrice();
					} else {
						btc_index_price = BigDecimal.ZERO;
					}
				}
			}
			DeliveryTransactionRate index_price_rate = transactionRateService.getByCode(transaction.getTransaction_code());
			if(btc_index_price == null || btc_index_price.signum()<=0){
				continue;
			}
			if(index_price_rate == null){
				continue;
			}
			
			DeliveryTransactionType transType = null;
			List<DeliveryTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
			if (types == null) {
				types = transTypeService.findAllUsing();
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
			}
			
			if (types == null) break;
			
			for (DeliveryTransactionType type : types) {
				if (type.getType_id() == transaction.getTransaction_type()) {
					transType = type;
					break;
				}
			}
			
			if (transType == null) continue;
			int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			
			for(DeliveryStopLoss sl : stopWinManyList){
				handUp(btc_index_price,index_price_rate,sl,transaction, digits);
			}
			for(DeliveryStopLoss sl : stopLossManyList){
				handUp(btc_index_price,index_price_rate,sl,transaction, digits);
			}
			for(DeliveryStopLoss sl : stopWinEmptyList){
				handUp(btc_index_price,index_price_rate,sl,transaction, digits);
			}
			for(DeliveryStopLoss sl : stopLossEmptyList){
				handUp(btc_index_price,index_price_rate,sl,transaction, digits);
			}
			
		}
		
	}
	
	
	private void handUp(BigDecimal btc_index_price, DeliveryTransactionRate index_price_rate, DeliveryStopLoss sl, DeliveryTransaction bqt, int digits) {
		DeliveryMemberTransaction  bqmt = memberTransactionService.get(sl.getTransaction_member_id());
		DeliveryStopLossLog log = null;
		BigDecimal pendingAmount = sl.getPending_amount();
		if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (pendingAmount.compareTo(bqmt.getHold_count()) <= 0) {
				try {
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						log = tradePDC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						log = tradePKC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log = new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，执行失败");
				}
			} else {
				log = new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，止盈止损设置挂单数量大于持仓数量");
			}
		} else { // 针对原有未设置挂单数量的止盈止损，全仓平
			if (bqmt.getHold_count().signum() > 0) {
				try {
					pendingAmount = bqmt.getHold_count();
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						log = tradePDC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						log = tradePKC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log = new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，执行失败");
				}
			} else {
				log = new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，持仓数量为0");
			}
		}
		
		if (log != null) {
			stopLossLogService.save(log);
		}
		
		//删除止盈止损记录
		stopLossService.deleteByMid(sl.getTransaction_member_id(), Contents.STOP_LOSS_LOG_ACTION_SC, "触发成功或失败后，删除持仓对应的止盈止损单");
	}
	
	private DeliveryStopLossLog tradePDC(BigDecimal btc_index_price, DeliveryTransactionRate index_price_rate,
			DeliveryTransaction transaction, DeliveryMemberTransaction trans, DeliveryStopLoss sl,
			BigDecimal amount_client, int digits) {
		
		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("delivery", transaction.getTransaction_code(), Contents.DIRECTION_SELL);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("多单，止盈止损挂卖单失败，insertPCD调用失败！！无市价:" + transaction.getTransaction_code());
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			logger.info("多单，止盈止损挂单失败，amount_client.signum() <= 0 || price_client.signum() <= 0，userId:" 
					+ trans.getF_user_id() + ", price: " + price_client.doubleValue());
			
			return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue());
		}
	
		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，用户不存在，userid: " + trans.getF_user_id());
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = transactionFeeRateService.getByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
				if (oneDayMin.compareTo(minIndexPrice) > 0) {
					minIndexPrice = oneDayMin;
				}
			}
			
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
			}
		}
		
		if (price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		DeliveryAccountContract account = accountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, trans.getAverage_price(), deposit, feeRate, 
				trans.getHold_count().add(trans.getLock_count()), transaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) < 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.UP);
				price_client = bcj;
			} else {
				logger.info("多单，止盈止损挂单失败，价格小于爆仓价，userId:" + trans.getF_user_id() + ", 挂单价格: " + price_client.doubleValue()
				+ ", 爆仓价：" + bcj.doubleValue());
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，价格小于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue());
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
		
		int tryTimes = 0;
		boolean lock = memberTransactionService.lockToTrade(trans.getMt_id());
		while (!lock && tryTimes < 30) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tryTimes++;
			lock = memberTransactionService.lockToTrade(trans.getMt_id());
		}
		
		if (lock) {
			try {
				pendingDelegationCacheService.insertPCD(cahce, trans.getMt_id(), transaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_CG, "触发成功 ");
			} catch (Exception e) {
				logger.error("多单，止盈止损挂卖单失败，insertPCD调用失败！！userId:" + trans.getF_user_id());
				e.printStackTrace();
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			} finally {
				memberTransactionService.unlockToTrade(trans.getMt_id());
			}
		} else {
			logger.info("多单，止盈止损挂单失败，锁仓失败，userId:" + trans.getF_user_id());
			return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，锁仓失败");
		}
	}

	private DeliveryStopLossLog tradePKC(BigDecimal btc_index_price, DeliveryTransactionRate index_price_rate,
			DeliveryTransaction transaction, DeliveryMemberTransaction trans, DeliveryStopLoss sl,
			BigDecimal amount_client, int digits) {

		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("delivery", transaction.getTransaction_code(), Contents.DIRECTION_BUY);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("空单，止盈止损挂买单失败，insertPKC调用失败！！无市价:" + transaction.getTransaction_code());
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPKC调用失败");
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			logger.info("空单，止盈止损挂单失败，amount_client.signum() <= 0 || price_client.signum() <= 0，userId:" 
					+ trans.getF_user_id() + ", price: " + price_client.doubleValue());
			return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue());
		}
	
		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，用户不存在，userid: " + trans.getF_user_id());
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = transactionFeeRateService.getByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
				}
			}
			
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
		
		if (price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		// 爆仓价
		DeliveryAccountContract account = accountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, trans.getAverage_price(), deposit, feeRate, 
				trans.getHold_count().add(trans.getLock_count()), transaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) > 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.DOWN);
				price_client = bcj;
			} else {
				logger.info("空单，止盈止损挂单失败，价格大于爆仓价，userId:" + trans.getF_user_id() + ", 挂单价格: " + price_client.doubleValue()
				+ ", 爆仓价：" + bcj.doubleValue());
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，价格大于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue());
			}
		}
		
		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		int tryTimes = 0;
		boolean lock = memberTransactionService.lockToTrade(trans.getMt_id());
		while (!lock && tryTimes < 20) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tryTimes++;
			lock = memberTransactionService.lockToTrade(trans.getMt_id());
		}
		
		if (lock) {
			try {
				pendingDelegationCacheService.insertPCK(cahce, trans.getMt_id(), transaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_CG, "触发成功 ");
			} catch (Exception e) {
				logger.error("空单，止盈止损挂买单失败，insertPCD调用失败！！userId:" + trans.getF_user_id());
				return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，insertPCD调用失败");
			} finally {
				memberTransactionService.unlockToTrade(trans.getMt_id());
			}
		} else {
			logger.info("空单，止盈止损挂单失败，锁仓失败，userId:" + trans.getF_user_id());
			return new DeliveryStopLossLog(sl, Contents.STOP_LOSS_LOG_ACTION_SB, "触发挂单失败，锁仓失败");
		}
	}
	
}
