package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionRateService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 计划委托转Cache任务
 * 
 * @author Administrator
 * 
 */
@Component
public class TimerTaskPlanPendingToCache extends BaseMemberLevelTask {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private PlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private BQTransactionRateService bqTransactionRateService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;

	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}

		if (contractArray == null || contractArray.length == 0)
			return;

		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST,
					lstTrads);
		}

		for (String contract : contractArray) {
			BQTransaction bqTransaction = null;
			for (BQTransaction transaction : lstTrads) {
				if (transaction.getTransaction_code().equals(contract)) {
					bqTransaction = transaction;
					break;
				}
			}

			if (bqTransaction == null)
				continue;

			if (bqTransaction.getSuspend()) {
				continue;
			}

			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(bqTransaction.getTransaction_id())) {
					continue;
				}
			}

			String code = bqTransaction.getTransaction_code();
			
			BigDecimal markPrice = spyMemcachedClient.get(bqTransaction.getTransaction_type_code());
			if (bqTransaction.getTransaction_kind() == 2) {
				BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_type_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					markPrice = lastPrice;
				}
			} else {
				BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + bqTransaction.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
				}
				if (setting != null && setting.isRunning()) {
					markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + bqTransaction.getTransaction_code());
				}
				
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());	//合约波动指数
			if(index_price_rate == null){
				continue;
			}

			List<PlanPendingDelegation> gtPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_GT, markPrice, 100);
			
			if (gtPlanPendings.size() > 0) {
				for (PlanPendingDelegation pd : gtPlanPendings) {
					if (pd.getTrade_type() == 1) {
						handUp(markPrice, index_price_rate, pd, bqTransaction);
					} else {
						checkPlanPdToCache(pd, bqTransaction, markPrice);
					}


				}
			}
			
			/*******************************************************************************************************/
			List<PlanPendingDelegation> ltPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_LT, markPrice, 100);
			if (ltPlanPendings.size() > 0) {
				for (PlanPendingDelegation pd : ltPlanPendings) {
					if (pd.getTrade_type() == 1) {
						handUp(markPrice, index_price_rate, pd, bqTransaction);
					} else {
						checkPlanPdToCache(pd, bqTransaction, markPrice);
					}
				}
			}
			
		}
	}

	private void checkPlanPdToCache(PlanPendingDelegation pd, BQTransaction bqTransaction, BigDecimal lastPrice) {
		String lk = planPendingDelegationService.lock(pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lk)) {
			try {
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_GT && lastPrice.compareTo(pd.getTri_price()) > 0) {
					return;
				}
				
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_LT && lastPrice.compareTo(pd.getTri_price()) < 0) {
					return;
				}
				
				String note = planPendingToCache(pd, bqTransaction);
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
	
	private String planPendingToCache(PlanPendingDelegation pd, BQTransaction bqTransaction) {
		BQTransactionType transType = transTypeService.getTransactionType(bqTransaction.getTransaction_type());
		if (transType == null) {
			return "暂无此合约";
		}
		int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());

		// 开仓数量及价格验证
		BigDecimal amount = pd.getDelegation_count();
		int multiple = pd.getMultiple();
		int direction = pd.getDirection();
		BigDecimal price = BigDecimal.ZERO;
		int orderType = pd.getOrder_type();
		if (orderType == 1) {
			price = getMarketOrderPrice("contract", bqTransaction.getTransaction_code(), direction);
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价:" + bqTransaction.getTransaction_code();
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
		BigDecimal btc_index_price = spyMemcachedClient.get(bqTransaction.getTransaction_code());
		if (btc_index_price == null) {
			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
		}
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
			if (btc_index_price == null) {
				return "委托下单失败";
			}
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient
//					.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.getLastHourAgoByCode(bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(
//							BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(),
//							BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}

			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(),
							BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}

			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}

		BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());

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

		if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
			minIndexPrice = oneDayMin;
		}

		if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
			maxIndexPrice = oneDayMax;
		}

//		if (bqTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
//		}

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
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		fee = BTCQMath.getFee(amount, price, feeRate, bqTransaction.getTrade_coefficient());

		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple,
				bqTransaction.getTrade_coefficient());

		BigDecimal remainAmount = BigDecimal.ZERO;
		BQAccountContract bqContract = bqAccountContractService.findByUidCode(pd.getF_user_id(),
				bqTransaction.getTransaction_code());
		if (bqContract != null) {
			remainAmount = bqContract.getF_btc();
		}

		BigDecimal maxAmount = remainAmount.subtract(fee).multiply(new BigDecimal(multiple))
				.divide(price.multiply(bqTransaction.getTrade_coefficient()), 5, BigDecimal.ROUND_HALF_UP)
				.setScale(4, BigDecimal.ROUND_HALF_DOWN);
		if (amount.compareTo(maxAmount) > 0) {
			return "账户可用余额不足以完成本次交易";
		}

		if (!inWhiteList(String.valueOf(pd.getF_user_id()))) {
			String risk = riskCheck(bqTransaction.getTransaction_code(), direction, price, amount,
					digits);
			if (!StringUtil.isEmpty(risk)) {
				return risk;
			}

			String maxkc = maxKcCheck(transType, bqTransaction.getTransaction_code(), direction, pd.getF_user_id(),
					amount);
			if (!StringUtil.isEmpty(maxkc)) {
				return maxkc;
			}
		}

		PendingDelegationCache cache = new PendingDelegationCache(bqTransaction.getTransaction_id(),
				bqTransaction.getTransaction_code(), direction, amount, price, BigDecimal.ZERO,
				amount, Integer.valueOf(multiple), deposit, feeRate,
				Contents.PRIORITY_KC, pd.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			pendingDelegationCacheService.insertCacheAndChangeAccount(cache, fee, bqContract, true, Contents.DEVICE_SERVER_PLAN, "");
			WebSocket.addPendingToMemcached(cache, spyMemcachedClient);
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

		BQArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}

		if (setting == null || !setting.getRisk_manage())
			return ret;

		String symbol = "usdt";
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}

		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		} else if ("elf".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + symbol);
		}

		if (balance == null) {
			balance = BigDecimal.ZERO;
		}

		if (direction == Contents.DIRECTION_SELL) {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<PendingDelegation> list = pendingDelegationService.getBuyTop(code, price);
			for (PendingDelegation pd : list) {
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
			List<PendingDelegation> list = pendingDelegationService.getSellTop(code, price);
			for (PendingDelegation pd : list) {
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

	protected String maxKcCheck(BQTransactionType bqTransactionType, String code, int direction, long userId,
			BigDecimal amount) {
		String ret = "";
		BigDecimal maxkc = BigDecimal.ZERO;
		if (direction == Contents.DIRECTION_BUY) {
			maxkc = bqTransactionType.getMax_kdc_nums();
		} else {
			maxkc = bqTransactionType.getMax_kkc_nums();
		}

		if (maxkc == null || maxkc.compareTo(BigDecimal.ZERO) <= 0) {
			return ret;
		}

		BigDecimal haveKc = BigDecimal.ZERO;
		// 持仓
		List<BQMemberTransaction> transList = bqMemberTransactionService.getTransactionByUserId(code, userId);
		for (BQMemberTransaction trans : transList) {
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


	/*******************************止盈止损********************************/

	private void handUp(BigDecimal btc_index_price, BQTransactionRate index_price_rate, PlanPendingDelegation pd, BQTransaction bqt) {
		//止盈止损单子只能生效一个，先失效，在进行止盈止损操作
		planPendingDelegationService.updateStatusAndNoteByMid(pd.getF_code(), pd.getF_user_id(), pd.getTransaction_member_id(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失效");

		BQTransactionType transType = transTypeService.getTransactionType(bqt.getTransaction_type());
		if (transType == null) {
			planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：暂无此合约");
			return ;
		}
		int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
		BQMemberTransaction bqmt = new BQMemberTransaction();
		if (pd.getTransaction_member_id() <= 0) {	//计划平仓单子可能会没有持仓ID
			BQMemberTransaction transaction = bqMemberTransactionService.getMemberTransaction(pd.getF_code(), pd.getF_user_id(), pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY, pd.getMultiple());
			if (transaction != null) bqmt = transaction;
		} else {
			bqmt = bqMemberTransactionService.get(pd.getTransaction_member_id());
		}

		if (bqmt.getMt_id() <= 0) {
			planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：无持仓");
			return ;
		}

		if (bqmt.getTrade_multiple().intValue() != pd.getMultiple()) {
			planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：杠杆不一致");
			return ;
		}

		BigDecimal pendingAmount = pd.getDelegation_count();
		if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (pendingAmount.compareTo(bqmt.getHold_count()) <= 0) {
				try {
					String note = "";
					int status = 0;
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						note = tradePDC(btc_index_price, index_price_rate, bqt, bqmt, pd, pendingAmount, digits);
					} else {
						note = tradePKC(btc_index_price, index_price_rate, bqt, bqmt, pd, pendingAmount, digits);
					}
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
					planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：下单失败");
				}
			} else {
				planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：止盈止损设置挂单数量大于持仓数量");
			}
		} else { // 针对原有未设置挂单数量的止盈止损，全仓平
			if (bqmt.getHold_count().signum() > 0) {
				try {
					pendingAmount = bqmt.getHold_count();
					String note = "";
					int status = 0;
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						note = tradePDC(btc_index_price, index_price_rate, bqt, bqmt, pd, pendingAmount, digits);
					} else {
						note = tradePKC(btc_index_price, index_price_rate, bqt, bqmt, pd, pendingAmount, digits);
					}
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
					planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：下单失败");
				}
			} else {
				planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：持仓数量为0");
			}
		}

		//删除30天前且不是已提交状态的计划委托单子
		planPendingDelegationService.deleteLossAmonthAgo(pd.getF_code(), pd.getF_user_id());
	}

	private String tradePDC(BigDecimal btc_index_price, BQTransactionRate index_price_rate,
								   BQTransaction bqTransaction, BQMemberTransaction trans, PlanPendingDelegation pd, BigDecimal amount_client, int digits) {

		BigDecimal price_client = BigDecimal.ZERO;
		int orderType = pd.getOrder_type();
		if (orderType == 1) {	//市价委托
			price_client = getMarketOrderPrice("contract", bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价：" + bqTransaction.getTransaction_code();
			}
		} else {
			price_client = pd.getDelegation_price(); //委托价格
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return "数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue();
		}

		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) {
			return "用户不存在，userid: " + trans.getF_user_id();
		}
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);

		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}

			if (oneDayAgo != null) {
				BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
				minIndexPrice = oneDayMin;
			}
		}

		if (price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}

		// 爆仓价
		BQAccountContract account = bqAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, trans.getAverage_price(), deposit, feeRate,
				trans.getHold_count().add(trans.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) < 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.UP);
				price_client = bcj;
			} else {
				return "价格小于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue();
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		PendingDelegationCache cahce = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, BigDecimal.ZERO, amount_client, multiple.intValue(), deposit,
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		int tryTimes = 0;
		boolean lock = bqMemberTransactionService.lockToTrade(trans.getMt_id());
		while (!lock && tryTimes < 30) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			tryTimes++;
			lock = bqMemberTransactionService.lockToTrade(trans.getMt_id());
		}

		if (lock) {
			try {
				pendingDelegationCacheService.insertPCD(cahce, trans.getMt_id(), bqTransaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "下单错误";
			} finally {
				bqMemberTransactionService.unlockToTrade(trans.getMt_id());
			}
		} else {
			return "锁仓失败，userId:" + trans.getF_user_id();
		}
	}

	private String tradePKC(BigDecimal btc_index_price, BQTransactionRate index_price_rate,
								   BQTransaction bqTransaction, BQMemberTransaction trans, PlanPendingDelegation pd, BigDecimal amount_client, int digits) {

		BigDecimal price_client = BigDecimal.ZERO;
		int orderType = pd.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("contract", bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价:" + bqTransaction.getTransaction_code();
			}
		} else {
			price_client = pd.getDelegation_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return "数量或价格小于0，amount：" + amount_client.doubleValue() + ", price: " + price_client.doubleValue();
		}

		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) {
			return "用户不存在，userid: " + trans.getF_user_id();
		}
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);

		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.getLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}

			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
				maxIndexPrice = oneDayMax;
			}

		}

		if (price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}

		// 爆仓价
		BQAccountContract account = bqAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, trans.getAverage_price(), deposit, feeRate,
				trans.getHold_count().add(trans.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) > 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.DOWN);
				price_client = bcj;
			} else {
				return "价格大于爆仓价，挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue();
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		PendingDelegationCache cahce = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, BigDecimal.ZERO, amount_client, multiple.intValue(), deposit,
				feeRate, Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		int tryTimes = 0;
		boolean lock = bqMemberTransactionService.lockToTrade(trans.getMt_id());
		while (!lock && tryTimes < 20) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			tryTimes++;
			lock = bqMemberTransactionService.lockToTrade(trans.getMt_id());
		}

		if (lock) {
			try {
				pendingDelegationCacheService.insertPCK(cahce, trans.getMt_id(), bqTransaction.getTrade_coefficient(), Contents.DEVICE_SERVER_SLW, "");
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "下单失败";
			} finally {
				bqMemberTransactionService.unlockToTrade(trans.getMt_id());
			}
		} else {
			return "锁仓失败，userId：" + trans.getF_user_id();
		}
	}
}
