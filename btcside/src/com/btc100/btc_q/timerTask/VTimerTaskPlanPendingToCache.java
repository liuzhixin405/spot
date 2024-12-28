package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.service.VTransactionRateService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 计划委托转Cache任务
 * 
 * @author Administrator
 * 
 */
@Component
public class VTimerTaskPlanPendingToCache extends BaseMemberLevelTask {

	@Autowired
	private VTransactionService transactionService;
	@Autowired
	private VTransactionTypeService transTypeService;
	@Autowired
	private VPlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VPendingDelegationService pendingDelegationService;
	@Autowired
	private VPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private VTransactionRateService bqTransactionRateService;
	@Autowired
	private VAccountContractService bqAccountContractService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private VArbitrageSettingService bqArbitrageSettingService;

	public void run() {

		List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(list == null) {
			list = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		for (VTransaction bqTransaction : list) {
			String code = bqTransaction.getTransaction_code(); // 获取合约号
			
			BigDecimal markPrice = spyMemcachedClient.get(bqTransaction.getTransaction_type_code());
			BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
			if (setting != null && setting.isRunning()) {
				markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + code);
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			List<VPlanPendingDelegation> gtPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_GT, markPrice, 300);
			
			if (gtPlanPendings.size() > 0) {
				for (VPlanPendingDelegation pd : gtPlanPendings) {
					checkPlanPdToCache(pd, bqTransaction, markPrice);
				}
			}
			
			/*******************************************************************************************************/
			List<VPlanPendingDelegation> ltPlanPendings = planPendingDelegationService.findByTypeAndLastPrice(code,
					Contents.PLAN_PENDING_TYPE_LT, markPrice, 300);
			if (ltPlanPendings.size() > 0) {
				for (VPlanPendingDelegation pd : ltPlanPendings) {
					checkPlanPdToCache(pd, bqTransaction, markPrice);
				}
			}
			
		}
	}

	private void checkPlanPdToCache(VPlanPendingDelegation pd, VTransaction bqTransaction, BigDecimal lastPrice) {
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
	
	private String planPendingToCache(VPlanPendingDelegation pd, VTransaction bqTransaction) {
		VTransactionType transType = transTypeService.getTransactionType(bqTransaction.getTransaction_type());
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
			price = getMarketOrderPrice("vcontract", bqTransaction.getTransaction_code(), direction);
			if (price.compareTo(BigDecimal.ZERO) <= 0) {
				return "无市价:" + bqTransaction.getTransaction_code();
			}
		} else {
			price = pd.getDelegation_price();
		}

		BigDecimal fee = BigDecimal.ZERO;
		BQMemberLevel userlevel = bqMemberLevelService.findByUserId(pd.getF_user_id());

		if (amount.compareTo(transType.getMin_kc_nums()) < 0) {
			return "挂单数量小于最小开仓数量" + "(" + transType.getMin_kc_nums() + "个)";
		}

		BigDecimal btc_index_price = spyMemcachedClient.get(bqTransaction.getTransaction_code());
		if (btc_index_price == null) {
			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
		}
		
		VTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);

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

		fee = BTCQMath.getFee(amount, price, userlevel.getEat_commission_rate(),
				bqTransaction.getTrade_coefficient());

		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple,
				bqTransaction.getTrade_coefficient());

		BigDecimal remainAmount = BigDecimal.ZERO;
		VAccountContract bqContract = bqAccountContractService.findByUidCode(pd.getF_user_id(),
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

		String risk = riskCheckV(bqTransaction.getTransaction_code(), direction, price, amount,
				digits);
		if (!StringUtil.isEmpty(risk)) {
			return risk;
		}

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(),
				bqTransaction.getTransaction_code(), direction, amount, price, BigDecimal.ZERO,
				amount, Integer.valueOf(multiple), deposit, userlevel.getEat_commission_rate(),
				Contents.PRIORITY_KC, pd.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			pendingDelegationCacheService.insertCacheAndChangeAccount(cahce, bqTransaction.getCurrency_type_id(), fee, bqContract, true, Contents.DEVICE_SERVER_PLAN, "");

			return "";
		} catch (ServiceException e) {
			e.printStackTrace();
			return "下单错误";
		}
		
	}

	private String riskCheckV(String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";
		
		VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new VArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
				setting.setAuto_trade(false);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.ONE_SECOND, setting);
		}
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
		String symbol = "usdt";
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}
		
		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.V_HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.V_HEDGING_BALANCE + symbol);
		}
		
		if (balance == null) {
			 balance = BigDecimal.ZERO;
		}
		
		if (direction == Contents.DIRECTION_SELL) {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<VPendingDelegation> list = pendingDelegationService.getBuyTop(code, price);
			for (VPendingDelegation pd : list) {
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
			List<VPendingDelegation> list = pendingDelegationService.getSellTop(code, price);
			for (VPendingDelegation pd : list) {
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
				ret = "开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue();
			}
		}
		
		return ret;
	}

}
