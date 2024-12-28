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
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NArbitrageSettingService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 计划委托转Cache任务
 * 
 * @author Administrator
 * 
 */
@Component
public class NTimerTaskPlanPendingToCache extends BaseMemberLevelTask {

	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NPlanPendingDelegationService planNPendingDelegationService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NTransactionRateService bqTransactionRateService;
	@Autowired
	private NAccountContractService bqAccountContractService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;
	@Autowired
	private NArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
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

		if (contractArray == null || contractArray.length == 0)
			return;

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

			if (bqTransaction.getSuspend()) {
				continue;
			}

			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(partition, bqTransaction.getTransaction_id())) {
					continue;
				}
			}

			String code = bqTransaction.getTransaction_code();
			
			BigDecimal markPrice = spyMemcachedClient.get(bqTransaction.getTransaction_type_code());
			if (bqTransaction.getTransaction_kind() == 2) {
				BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_type_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					markPrice = lastPrice;
				}
			} else {
				NMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition + "_" + bqTransaction.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition);
				}
				if (setting != null && setting.isRunning()) {
					markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + bqTransaction.getTransaction_code());
				}
				
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());	//合约波动指数
			if(index_price_rate == null){
				continue;
			}
			
			List<NPlanPendingDelegation> gtNPlanPendings = planNPendingDelegationService.findByTypeAndLastPrice(partition, code,
					Contents.PLAN_PENDING_TYPE_GT, markPrice, 100);
			
			if (gtNPlanPendings.size() > 0) {
				for (NPlanPendingDelegation pd : gtNPlanPendings) {
					if (pd.getTrade_type() == 1) {
						handUp(partition, markPrice, index_price_rate, pd, bqTransaction);
					} else {
						checkPlanPdToCache(partition, pd, bqTransaction, markPrice);
					}
				}
			}
			
			/*******************************************************************************************************/
			List<NPlanPendingDelegation> ltNPlanPendings = planNPendingDelegationService.findByTypeAndLastPrice(partition, code,
					Contents.PLAN_PENDING_TYPE_LT, markPrice, 100);
			if (ltNPlanPendings.size() > 0) {
				for (NPlanPendingDelegation pd : ltNPlanPendings) {
					if (pd.getTrade_type() == 1) {
						handUp(partition, markPrice, index_price_rate, pd, bqTransaction);
					} else {
						checkPlanPdToCache(partition, pd, bqTransaction, markPrice);
					}
				}
			}
			
		}
	}

	private void checkPlanPdToCache(String partition, NPlanPendingDelegation pd, NTransaction bqTransaction, BigDecimal lastPrice) {
		String lk = planNPendingDelegationService.lock(partition, pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lk)) {
			try {
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_GT && lastPrice.compareTo(pd.getTri_price()) > 0) {
					return;
				}
				
				if (pd.getType() == Contents.PLAN_PENDING_TYPE_LT && lastPrice.compareTo(pd.getTri_price()) < 0) {
					return;
				}
				
				String note = planPendingToCache(partition, pd, bqTransaction);
				int status = 0;
				if (StringUtil.isEmpty(note)) {
					status = Contents.PLAN_PENDING_STATUS_EFFECTIVE;
					note = "计划委托挂单生效";
				} else {
					status = Contents.PLAN_PENDING_STATUS_FAIL;
					note = "计划委托挂单失败，失败原因：" + note;
				}
				planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pd != null) {
					planNPendingDelegationService.unLock(partition, pd.getF_code(), pd.getId(), lk);
				}
			}
		}
	}
	
	private String planPendingToCache(String partition, NPlanPendingDelegation pd, NTransaction bqTransaction) {
		NTransactionType transType = transTypeService.getTransactionType(partition, bqTransaction.getTransaction_type());
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
			price = getMarketOrderPrice(partition, bqTransaction.getTransaction_code(), direction);
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
//					.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.getLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(
//							BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(),
//							BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
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
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}

		NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());

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
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
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
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		fee = BTCQMath.getFee(amount, price, feeRate, bqTransaction.getTrade_coefficient());

		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple,
				bqTransaction.getTrade_coefficient());

		BigDecimal remainAmount = BigDecimal.ZERO;
		NAccountContract bqContract = bqAccountContractService.findByUidCode(partition, pd.getF_user_id(),
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
			String risk = riskCheck(partition, bqTransaction.getTransaction_code(), direction, price, amount,
					digits);
			if (!StringUtil.isEmpty(risk)) {
				return risk;
			}

			String maxkc = maxKcCheck(partition, transType, bqTransaction.getTransaction_code(), direction, pd.getF_user_id(),
					amount);
			if (!StringUtil.isEmpty(maxkc)) {
				return maxkc;
			}
		}

		NPendingDelegationCache cache = new NPendingDelegationCache(bqTransaction.getTransaction_id(),
				bqTransaction.getTransaction_code(), direction, amount, price, BigDecimal.ZERO,
				amount, Integer.valueOf(multiple), deposit, feeRate,
				Contents.PRIORITY_KC, pd.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			pendingDelegationCacheService.insertCacheAndChangeAccount(partition, bqTransaction.getCurrency_type_id(), cache, fee, bqContract, true, Contents.DEVICE_SERVER_PLAN, "");
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

	protected String riskCheck(String partition, String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";

		NArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(partition, code);
			if (setting == null) {
				setting = new NArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}

		if (setting == null || !setting.getRisk_manage())
			return ret;

		String symbol = partition;
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}

		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && partition.equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + partition);
		} else if ("elf".equals(code) && partition.equals(symbol)) {
			balance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.HEDGING_BALANCE + partition);
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
			List<NPendingDelegation> list = pendingDelegationService.getBuyTop(partition, code, price);
			for (NPendingDelegation pd : list) {
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
			List<NPendingDelegation> list = pendingDelegationService.getSellTop(partition, code, price);
			for (NPendingDelegation pd : list) {
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
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
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

	protected String maxKcCheck(String partition, NTransactionType bqTransactionType, String code, int direction, long userId,
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
		List<NMemberTransaction> transList = bqMemberTransactionService.getTransactionByUserId(partition, code, userId);
		for (NMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}

		BigDecimal pendingUndeal = pendingDelegationService.getSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}

		pendingUndeal = pendingDelegationCacheService.getSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
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

	private void handUp(String partition, BigDecimal btc_index_price, NTransactionRate index_price_rate, NPlanPendingDelegation pd, NTransaction transaction) {
		//止盈止损单子只能生效一个，先失效，在进行止盈止损操作
		planNPendingDelegationService.updateStatusAndNoteByMid(partition, pd.getF_code(), pd.getF_user_id(), pd.getTransaction_member_id(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失效");

		NTransactionType nTransType = transTypeService.getTransactionType(partition, transaction.getTransaction_type());
		if (nTransType == null) {
			planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：暂无此合约");
		}
		int digits = Utils.getDigitsFromMinFloat(nTransType.getMin_float_price());
		NMemberTransaction  bqmt = new NMemberTransaction();
		if (pd.getTransaction_member_id() <= 0) {	//计划平仓单子可能没有持仓ID
			NMemberTransaction ntransaction = bqMemberTransactionService.getMemberTransaction(partition, pd.getF_code(), pd.getF_user_id(), pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY, pd.getMultiple());
			if (ntransaction != null) bqmt = ntransaction;
		} else {
			bqmt = bqMemberTransactionService.get(partition, pd.getTransaction_member_id());
		}

		if (bqmt.getMt_id() <= 0) {
			planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：无持仓");
			return ;
		}

		if (bqmt.getTrade_multiple().intValue() != pd.getMultiple()) {
			planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：杠杆不一致");
			return ;
		}

		BigDecimal pendingAmount = pd.getDelegation_count();
		if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (pendingAmount.compareTo(bqmt.getHold_count()) <= 0) {
				try {
					String note = "";
					int status = 0;
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						note = tradePDC(partition, btc_index_price, index_price_rate, transaction, bqmt, pd, pendingAmount, digits);
					} else {
						note = tradePKC(partition, btc_index_price, index_price_rate, transaction, bqmt, pd, pendingAmount, digits);
					}
					if (StringUtil.isEmpty(note)) {
						status = Contents.PLAN_PENDING_STATUS_EFFECTIVE;
						note = "计划委托挂单生效";
					} else {
						status = Contents.PLAN_PENDING_STATUS_FAIL;
						note = "计划委托挂单失败，失败原因：" + note;
					}
					planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), status, note);
				} catch (Exception e) {
					e.printStackTrace();
					planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：下单失败");
				}
			} else {
				planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：止盈止损设置挂单数量大于持仓数量");
			}
		} else { // 针对原有未设置挂单数量的止盈止损，全仓平
			if (bqmt.getHold_count().signum() > 0) {
				try {
					pendingAmount = bqmt.getHold_count();
					String note = "";
					int status = 0;
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						note = tradePDC(partition, btc_index_price, index_price_rate, transaction, bqmt, pd, pendingAmount, digits);
					} else {
						note = tradePKC(partition, btc_index_price, index_price_rate, transaction, bqmt, pd, pendingAmount, digits);
					}
					if (StringUtil.isEmpty(note)) {
						status = Contents.PLAN_PENDING_STATUS_EFFECTIVE;
						note = "计划委托挂单生效";
					} else {
						status = Contents.PLAN_PENDING_STATUS_FAIL;
						note = "计划委托挂单失败，失败原因：" + note;
					}
					planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), status, note);
				} catch (Exception e) {
					e.printStackTrace();
					planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：下单失败");
				}
			} else {
				planNPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), Contents.PLAN_PENDING_STATUS_FAIL, "计划委托挂单失败，失败原因：持仓数量为0");
			}
		}
		//删除30天前且不是已提交状态的计划委托单子
		planNPendingDelegationService.deleteLossAmonthAgo(partition, pd.getF_code(), pd.getF_user_id());
	}

	private String tradePDC(String partition, BigDecimal btc_index_price, NTransactionRate index_price_rate,
								  NTransaction bqTransaction, NMemberTransaction trans, NPlanPendingDelegation pd, BigDecimal amount_client, int digits) {

		BigDecimal price_client = BigDecimal.ZERO;
		int orderType = pd.getOrder_type();
		if (orderType == 1) {	//市价委托
			price_client = getMarketOrderPrice(partition, bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价：" + bqTransaction.getTransaction_code();
			}
		} else {
			price_client = pd.getDelegation_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return "数量或价格小于0，amount：" + amount_client.doubleValue() + ", price：" + price_client.doubleValue();
		}

		BQMember user = bqMemberService.findUserById(trans.getF_user_id());
		if (user == null) {
			return "用户不存在，userid: " + trans.getF_user_id();
		}
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
				minIndexPrice = oneDayMin;
			}
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
				return "价格小于爆仓价， 挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue();
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, BigDecimal.ZERO, amount_client, multiple.intValue(), deposit,
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
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "下单错误";
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, trans.getMt_id());
			}
		} else {
			return "锁仓失败，userId:" + trans.getF_user_id();
		}
	}

	private String tradePKC(String partition, BigDecimal btc_index_price, NTransactionRate index_price_rate,
								  NTransaction bqTransaction, NMemberTransaction trans, NPlanPendingDelegation pd, BigDecimal amount_client, int digits) {

		BigDecimal price_client = BigDecimal.ZERO;
		int orderType = pd.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice(partition, bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
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
				maxIndexPrice = oneDayMax;
			}
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
				return "价格大于爆仓价，挂单价格: " + price_client.doubleValue() + ", 爆仓价：" + bcj.doubleValue();
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, BigDecimal.ZERO, amount_client, multiple.intValue(), deposit,
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
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "下单失败";
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, trans.getMt_id());
			}
		} else {
			return "锁仓失败，userId：" + trans.getF_user_id();
		}
	}

}
