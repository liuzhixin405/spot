package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 计划委托转Cache任务
 * 
 * @author Administrator
 * 
 */
@Component
public class DeliveryTimerTaskPlanPendingToCache extends BaseMemberLevelTask {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryPlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private DeliveryNotradetimeService bqNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private DeliveryTransactionRateService transactionRateService;
	@Autowired
	private DeliveryAccountContractService deliveryAccountContractService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;
	@Autowired
	private DeliveryArbitrageSettingService dArbitrageSettingService;
	@Autowired
	private DeliveryMemberTransactionService memberTransactionService;
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
				if (!bqNotradetimeService.isTradeTime(transaction.getTransaction_id())) {
					continue;
				}
			}

			String code = transaction.getTransaction_code();
			
			BigDecimal markPrice = spyMemcachedClient.get(transaction.getTransaction_type_code());
			if (transaction.getTransaction_kind() == 2) {
				BigDecimal lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					markPrice = lastPrice;
				}
			} else {
				BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + transaction.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
				}
				if (setting != null && setting.isRunning()) {
					markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + code);
				}
				
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<DeliveryPlanPendingDelegation> gtPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_GT, markPrice, 100);
			
			if (gtPlanPendings.size() > 0) {
				for (DeliveryPlanPendingDelegation pd : gtPlanPendings) {
					checkPlanPdToCache(pd, transaction, markPrice);
				}
			}
			
			/*******************************************************************************************************/
			List<DeliveryPlanPendingDelegation> ltPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_LT, markPrice, 100);
			if (ltPlanPendings.size() > 0) {
				for (DeliveryPlanPendingDelegation pd : ltPlanPendings) {
					checkPlanPdToCache(pd, transaction, markPrice);
				}
			}
			
		}
	}

	private void checkPlanPdToCache(DeliveryPlanPendingDelegation pd, DeliveryTransaction transaction, BigDecimal lastPrice) {
		String lk = planPendingDelegationService.lock(pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lk)) {
			try {
				
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_GT && lastPrice.compareTo(pd.getTri_price()) > 0) {
					return;
				}
				
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_LT && lastPrice.compareTo(pd.getTri_price()) < 0) {
					return;
				}
				
				String note = planPendingToCache(pd, transaction);
				int status = 0;
				if (StringUtil.isEmpty(note)) {
					status = Contents.PLAN_PENDING_STATUS_EFFECTIVE;
					note = "计划委托挂单生效";
				} else {
					status = Contents.PLAN_PENDING_STATUS_FAIL;
					note = "计划委托挂单失败，失败原因：" + note;
				}
				planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pd != null) {
					planPendingDelegationService.unLock(pd.getF_code(), pd.getId(), lk);
				}
			}
		}
	}
	
	private String planPendingToCache(DeliveryPlanPendingDelegation pd, DeliveryTransaction transaction) {
		DeliveryTransactionType transType = transTypeService.getTransactionType(transaction.getTransaction_type());
		if (transType == null) {
			return "暂无此合约";
		}
		int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());

		// 开仓数量及价格验证
		BigDecimal amount = pd.getDelegation_count();
		int multiple = pd.getMultiple();
		int direction = pd.getDirection();
		BigDecimal price = new BigDecimal(0);
		int orderType = pd.getOrder_type();
		if (orderType == 1) {
			price = getMarketOrderPrice("delivery", transaction.getTransaction_code(), direction);
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价:" + transaction.getTransaction_code();
			}
		} else {
			price = pd.getDelegation_price();
		}

		BigDecimal fee = BigDecimal.ZERO;
		BQMember user = bqMemberService.findUserById(pd.getF_user_id());
		if (user == null) {
			return "子账号用户已无效";
		}
		BQMemberLevel userlevel = getLevelByUserBfx(user);

		if (amount.compareTo(transType.getMin_kc_nums()) < 0) {
			return "挂单数量小于最小开仓数量" + "(" + transType.getMin_kc_nums() + "个)";
		}

		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		BigDecimal btc_index_price = spyMemcachedClient.get(transType.getType_code());
		if (btc_index_price == null) {
			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
		}
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
			if (btc_index_price == null) {
				return "委托下单失败";
			}
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient
					.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = tradeLogService.getLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(
							BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(),
							BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}

			BigDecimal oneDayAgo = spyMemcachedClient
					.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(
							BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(),
							BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}

			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits,
						RoundingMode.HALF_UP);
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits,
						BigDecimal.ROUND_DOWN);
			}
		}

		DeliveryTransactionRate index_price_rate = transactionRateService.getByCode(transaction.getTransaction_code());

		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		} else {
			maxIndexPrice = new BigDecimal(100000);
			minIndexPrice = BigDecimal.ZERO;
		}

		if (oneDayMin != null && oneDayMin.compareTo(minIndexPrice) > 0) {
			minIndexPrice = oneDayMin;
		}

		if (oneDayMax != null && oneDayMax.compareTo(maxIndexPrice) < 0) {
			maxIndexPrice = oneDayMax;
		}

		if (transaction.getTransaction_kind() == 2) {
			BigDecimal openPrice = spyMemcachedClient
					.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits,
						RoundingMode.HALF_UP);
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits,
						BigDecimal.ROUND_DOWN);
			}
		}

		if (direction == Contents.DIRECTION_BUY && price.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price = maxIndexPrice;
			} else {
				return "买入价必须小于" + maxIndexPrice;
			}
		}

		if (direction == Contents.DIRECTION_SELL && price.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price = minIndexPrice;
			} else {
				return "卖出价必须大于" + minIndexPrice;
			}
		}
		
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = transactionFeeRateService.getByCodeAndLevel(transaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		fee = BTCQMath.getFee(amount, price, feeRate, transaction.getTrade_coefficient());

		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple,
				transaction.getTrade_coefficient());

		BigDecimal remainAmount = BigDecimal.ZERO;
		DeliveryAccountContract bqContract = deliveryAccountContractService.findByUidCode(pd.getF_user_id(),
				transaction.getTransaction_code());
		if (bqContract != null) {
			remainAmount = bqContract.getF_btc();
		}

		BigDecimal maxAmount = remainAmount.subtract(fee).multiply(new BigDecimal(multiple))
				.divide(price.multiply(transaction.getTrade_coefficient()), 5, BigDecimal.ROUND_HALF_UP)
				.setScale(4, BigDecimal.ROUND_HALF_DOWN);
		if (amount.compareTo(maxAmount) > 0) {
			return "账户可用余额不足以完成本次交易";
		}

		if (!inWhiteList(String.valueOf(pd.getF_user_id()))) {
			String risk = riskCheck(transaction.getTransaction_code(), direction, price, amount,
					digits);
			if (!StringUtil.isEmpty(risk)) {
				return risk;
			}

			String maxkc = maxKcCheck(transType, transaction.getTransaction_code(), direction, pd.getF_user_id(),
					amount);
			if (!StringUtil.isEmpty(maxkc)) {
				return maxkc;
			}
		}

		DeliveryPendingDelegationCache cache = new DeliveryPendingDelegationCache(transaction.getTransaction_id(),
				transaction.getTransaction_code(), direction, amount, price, BigDecimal.ZERO,
				amount, Integer.valueOf(multiple), deposit, feeRate,
				Contents.PRIORITY_KC, pd.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			pendingDelegationCacheService.insertCacheAndChangeAccount(cache, fee, bqContract, true, Contents.DEVICE_SERVER_PLAN, "");
//			WebSocket.addPendingToMemcached(cache, spyMemcachedClient);
			return "";
		} catch (ServiceException e) {
			e.printStackTrace();
			return "下单错误";
		}
		
	}

	protected boolean inWhiteList(String uid) {
		List<String> whiteList = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_WHITE_LIST);
		if (whiteList == null) {
			whiteList = new ArrayList<String>();
			List<BQUserTradeWhite> userList = bqUserTradeWhiteService.findAll();
			for (BQUserTradeWhite whiteUser : userList) {
				whiteList.add(String.valueOf(whiteUser.getF_user_id()));
			}

			spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_WHITE_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, whiteList);
		}

		if (whiteList != null && whiteList.contains(uid)) {
			return true;
		}

		return false;
	}

	protected String riskCheck(String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";

		DeliveryArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = dArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new DeliveryArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}

		if (setting == null || !setting.getRisk_manage())
			return ret;

		String symbol = "usdt";
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}

		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt");
		} else if ("elf".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.D_HEDGING_BALANCE + symbol);
		}

		if (balance == null) {
			balance = BigDecimal.ZERO;
		}

		if (direction == Contents.DIRECTION_SELL) {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<DeliveryPendingDelegation> list = pendingDelegationService.getBuyTop(code, price);
			for (DeliveryPendingDelegation pd : list) {
				if (pd.getF_user_id() == setting.getDepth_uid()) {
					BigDecimal undeal = pd.getUndeal_count();
					if ((needDepthDo.add(undeal)).compareTo(totalAmount) >= 0) {
						undeal = totalAmount.subtract(needDepthDo);
					}

					needDepthDo = needDepthDo.add(undeal);
					if (needDepthDo.compareTo(totalAmount) >= 0) {
						needDepthDo = totalAmount;
						break;
					}
				} else {

					BigDecimal undeal = pd.getUndeal_count();
					if ((totalAmount.subtract(undeal)).compareTo(BigDecimal.ZERO) < 0) {
						undeal = totalAmount;
					}

					userDo = userDo.add(undeal);
					totalAmount = totalAmount.subtract(undeal);
					if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
						totalAmount = BigDecimal.ZERO;
						break;
					}
				}
			}

			if (needDepthDo.compareTo(BigDecimal.ZERO) <= 0) {
				return ret;
			}

			if (needDepthDo.compareTo(balance) > 0) {
				ret = "开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue();
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<DeliveryPendingDelegation> list = pendingDelegationService.getSellTop(code, price);
			for (DeliveryPendingDelegation pd : list) {
				if (pd.getF_user_id() == setting.getDepth_uid()) {
					BigDecimal undeal = pd.getUndeal_count();
					if ((needDepthDo.add(undeal)).compareTo(totalAmount) >= 0) {
						undeal = totalAmount.subtract(needDepthDo);
					}

					needDepthDo = needDepthDo.add(undeal);
					needVol = needVol.add(undeal.multiply(pd.getDelegation_price()));
					if (needDepthDo.compareTo(totalAmount) >= 0) {
						needDepthDo = totalAmount;
						break;
					}
				} else {
					BigDecimal undeal = pd.getUndeal_count();
					if ((totalAmount.subtract(undeal)).compareTo(BigDecimal.ZERO) < 0) {
						undeal = totalAmount;
					}
					userDo = userDo.add(undeal);
					totalAmount = totalAmount.subtract(undeal);
					if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
						totalAmount = BigDecimal.ZERO;
						break;
					}
				}
			}

			if (needDepthDo.compareTo(BigDecimal.ZERO) <= 0) {
				return ret;
			}

			price = needVol.divide(needDepthDo, 8, BigDecimal.ROUND_UP);
			price = price
					.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			price = price.setScale(priceDigits, BigDecimal.ROUND_DOWN);

			int digtis = Utils.getAmountDigtis(code);
			BigDecimal canOpen = balance.divide(price, digtis, BigDecimal.ROUND_DOWN);
			if (needDepthDo.compareTo(canOpen) > 0) {
				ret = "开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue()
						+ ", canOpen: " + canOpen.doubleValue();
			}
		}

		return ret;
	}

	protected String maxKcCheck(DeliveryTransactionType transactionType, String code, int direction, long userId,
			BigDecimal amount) {
		String ret = "";
		BigDecimal maxkc = BigDecimal.ZERO;
		if (direction == Contents.DIRECTION_BUY) {
			maxkc = transactionType.getMax_kdc_nums();
		} else {
			maxkc = transactionType.getMax_kkc_nums();
		}

		if (maxkc == null || maxkc.compareTo(BigDecimal.ZERO) <= 0) {
			return ret;
		}

		BigDecimal haveKc = BigDecimal.ZERO;
		// 持仓
		List<DeliveryMemberTransaction> transList = memberTransactionService.getTransactionByUserId(code, userId);
		for (DeliveryMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}

		BigDecimal pendingUndeal = pendingDelegationService.getSumByUIDAction(code, userId, direction,
				Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}

		pendingUndeal = pendingDelegationCacheService.getSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}

		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0)
				cankc = BigDecimal.ZERO;
			ret = "风控系统控制您当前最大开仓数量：" + cankc.doubleValue();
		}

		return ret;

	}

}
