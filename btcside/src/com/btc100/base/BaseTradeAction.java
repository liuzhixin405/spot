package com.btc100.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQMarketOrderSetting;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserApiWhite;
import com.btc100.btc_q.entity.BQUserOpenLimit;
import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.DeliveryUserOpenLimit;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQMarketOrderSettingService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotQuantSettingService;
import com.btc100.btc_q.service.BQUserApiWhiteService;
import com.btc100.btc_q.service.BQUserOpenLimitService;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.service.DeliveryUserOpenLimitService;
import com.btc100.btc_q.service.NArbitrageSettingService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.MulpitleAndStateVO;
import com.btc100.btc_q.vo.Top5DelegationVO;

public class BaseTradeAction extends BaseAction {
	
	private static final Logger logger = Logger.getLogger(BaseTradeAction.class);

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	protected NPendingDelegationService nPendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private NArbitrageSettingService nArbitrageSettingService;
	@Autowired
	private DeliveryArbitrageSettingService dArbitrageSettingService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	protected NPendingDelegationCacheService nPendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationCacheService dPendingDelegationCacheService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;
	@Autowired
	private SpotPendingDelegationCacheService spotPendingDelegationCacheService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private BQSpotQuantSettingService bqQuantSettingService;
	@Autowired
	private PlanPendingDelegationService planPendingDelegationService;
	@Autowired
	protected NPlanPendingDelegationService nPlanPendingDelegationService;
	@Autowired
	private VPlanPendingDelegationService vPlanPendingDelegationService;
	@Autowired
	private DeliveryPlanPendingDelegationService dPlanPendingDelegationService;
	@Autowired
	private BQUserOpenLimitService bqUserOpenLimitService;
	@Autowired
	private DeliveryUserOpenLimitService dUserOpenLimitService;
	@Autowired
	private BQUserApiWhiteService bqUserApiWhiteService;
	@Autowired
	private BQMarketOrderSettingService bqMarketOrderSettingService;
	@Autowired
	protected NMemberTransactionService nMemberTransactionService;
	@Autowired
	protected NContractPartitionService nContractPartitionService;
	@Autowired
	protected NStopLossService nStopLossService;
	
	protected boolean inWhiteList(String uid) {
		List<String> whiteList = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_WHITE_LIST);
		if (whiteList == null) {
			whiteList = new ArrayList<String>();
			List<BQUserTradeWhite> userList = bqUserTradeWhiteService.readAll();
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
	
	protected boolean inApiWhiteList(String uid) {
		List<String> whiteList = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_LIST);
		if (whiteList == null) {
			whiteList = new ArrayList<String>();
			List<BQUserApiWhite> userList = bqUserApiWhiteService.readAll();
			for (BQUserApiWhite whiteUser : userList) {
				whiteList.add(String.valueOf(whiteUser.getF_user_id()));
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, whiteList);
		}
		
		if (whiteList != null && whiteList.contains(uid)) {
			return true;
		}
		
		return false;
	}
	
	protected String spotRiskCheck(String coinPair, String coin, String market, int direction, 
			BigDecimal price, BigDecimal amount, int priceDigits, int amountDigits) {
		String ret = "";
		
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + coinPair);
		if (setting == null) {
			setting = bqSpotArbitrageSettingService.readArbitrageSettingByCode(coinPair);
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + coinPair, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
		String symbol = market;
		if (direction == Contents.DIRECTION_SELL) {
			symbol = coin;
		}
		
		BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + symbol);
		if (symbol.equals("usdt") && coin.equalsIgnoreCase("okb")) {
			balance = spyMemcachedClient.get("okex_" + BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + symbol);
		}
		
		if (balance == null) {
			 balance = BigDecimal.ZERO;
		}
		
		if (direction == Contents.DIRECTION_SELL) {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<SpotPendingDelegation> list = spotPendingDelegationService.readPendingBetweenPrice(coinPair, Contents.DIRECTION_BUY, price, new BigDecimal(10000000));
			for (SpotPendingDelegation pd : list) {
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
				logger.info("开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue());
				ret = "风控系统当前限制" + coin.toUpperCase() + "可卖" + balance.add(userDo).doubleValue() + "，请稍后再试！";
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<SpotPendingDelegation> list = spotPendingDelegationService.readPendingBetweenPrice(coinPair, Contents.DIRECTION_SELL, BigDecimal.ZERO, price);
			for (SpotPendingDelegation pd : list) {
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
			
			BigDecimal canOpen = balance.divide(price, amountDigits, BigDecimal.ROUND_DOWN);
			if (needDepthDo.compareTo(canOpen) > 0) {
				logger.info("开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue());
				ret = "风控系统当前限制" + coin.toUpperCase() + "可买" +  canOpen.add(userDo).doubleValue() + "，请稍后再试！";
			}
		}
		
		return ret;
	}
	
	protected String riskCheck(String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";
		
		BQArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.readArbitrageSettingByCode(code);
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
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
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
			List<PendingDelegation> list = pendingDelegationService.readBuyTop(code, price);
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
				logger.info("开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanSell", Contents.Local.LOCALE.get())
						+ balance.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<PendingDelegation> list = pendingDelegationService.readSellTop(code, price);
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
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			price = price.setScale(priceDigits, BigDecimal.ROUND_DOWN);
			
			int digtis = Utils.getAmountDigtis(code);
			BigDecimal canOpen = balance.divide(price, digtis, BigDecimal.ROUND_DOWN);
			if (needDepthDo.compareTo(canOpen) > 0) {
				logger.info("开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanBuy", Contents.Local.LOCALE.get())
						+ canOpen.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		}
		
		return ret;
	}
	
	protected String nRiskCheck(String partition, String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";
		
		NArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + code);
		if (setting == null) {
			setting = nArbitrageSettingService.readArbitrageSettingByCode(partition, code);
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
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
		String symbol = partition;
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}
		
		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + partition);
		} else if ("elf".equals(code) && "usdt".equals(symbol)) {
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
			List<NPendingDelegation> list = nPendingDelegationService.readBuyTop(partition, code, price);
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
				logger.info("开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanSell", Contents.Local.LOCALE.get())
						+ balance.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<NPendingDelegation> list = nPendingDelegationService.readSellTop(partition, code, price);
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
				logger.info("开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanBuy", Contents.Local.LOCALE.get())
						+ canOpen.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		}
		
		return ret;
	}
	
	protected String deliveryRiskCheck(String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";
		
		DeliveryArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = dArbitrageSettingService.readArbitrageSettingByCode(code);
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
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
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
			List<DeliveryPendingDelegation> list = dPendingDelegationService.readBuyTop(code, price);
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
				logger.info("开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanSell", Contents.Local.LOCALE.get())
						+ balance.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<DeliveryPendingDelegation> list = dPendingDelegationService.readSellTop(code, price);
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
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			price = price.setScale(priceDigits, BigDecimal.ROUND_DOWN);
			
			int digtis = Utils.getAmountDigtis(code);
			BigDecimal canOpen = balance.divide(price, digtis, BigDecimal.ROUND_DOWN);
			if (needDepthDo.compareTo(canOpen) > 0) {
				logger.info("开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanBuy", Contents.Local.LOCALE.get())
						+ canOpen.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		}
		
		return ret;
	}
	
	protected String maxKcCheck(BQTransactionType bqTransactionType, String code, int direction, long userId, BigDecimal amount) {
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
		List<BQMemberTransaction> transList = bqMemberTransactionService.readTransactionByUserId(code, userId);
		for (BQMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}
	
		BigDecimal pendingUndeal = pendingDelegationService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = pendingDelegationCacheService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.riskLimt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
		
	}
	
	protected String nMaxKcCheck(String partition, NTransactionType bqTransactionType, String code, int direction, long userId, BigDecimal amount) {
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
		List<NMemberTransaction> transList = nMemberTransactionService.readTransactionByUserId(partition, code, userId);
		for (NMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}
	
		BigDecimal pendingUndeal = nPendingDelegationService.readSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = nPendingDelegationService.readSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.riskLimt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
		
	}

	protected String deliveryMaxKcCheck(DeliveryTransactionType bqTransactionType, String code, int direction, long userId, BigDecimal amount) {
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
		List<DeliveryMemberTransaction> transList = dMemberTransactionService.readTransactionByUserId(code, userId);
		for (DeliveryMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}
		
		BigDecimal pendingUndeal = dPendingDelegationService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = dPendingDelegationCacheService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.riskLimt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
		
	}
	
	/**
	 * 单合约单用户限制
	 * @param code
	 * @param direction
	 * @param userId
	 * @param amount
	 * @return
	 */
	protected String maxUserKcCheck(String code, int direction, long userId, BigDecimal amount) {
		String ret = "";
		List<BQUserOpenLimit> limitList = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_OPEN_LIMTI_LIST);
		BQUserOpenLimit limit = null;
		if (limitList == null) {
			limitList = bqUserOpenLimitService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_OPEN_LIMTI_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, limitList);
		}
		if (limitList == null || limitList.size() <= 0) return ret;
		
		for (BQUserOpenLimit bqlimit : limitList) {
			if (bqlimit.getF_code().equals(code) && bqlimit.getF_user_id() == userId) {
				limit = bqlimit;
				break;
			}
		}

		if (limit == null) return ret;
		
		BigDecimal maxkc = BigDecimal.ZERO;
		if (direction == Contents.DIRECTION_BUY) {
			maxkc = limit.getMax_kdc_nums();
		} else {
			maxkc = limit.getMax_kkc_nums();
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

		BigDecimal pendingUndeal = pendingDelegationService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = pendingDelegationCacheService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.Limt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
	}
	
	protected String nMaxUserKcCheck(String partition, String code, int direction, long userId, BigDecimal amount) {
		String ret = "";
		List<BQUserOpenLimit> limitList = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_OPEN_LIMTI_LIST);
		BQUserOpenLimit limit = null;
		if (limitList == null) {
			limitList = bqUserOpenLimitService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_OPEN_LIMTI_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, limitList);
		}
		if (limitList == null || limitList.size() <= 0) return ret;
		
		for (BQUserOpenLimit bqlimit : limitList) {
			if (bqlimit.getF_code().equals(code) && bqlimit.getF_user_id() == userId) {
				limit = bqlimit;
				break;
			}
		}

		if (limit == null) return ret;
		
		BigDecimal maxkc = BigDecimal.ZERO;
		if (direction == Contents.DIRECTION_BUY) {
			maxkc = limit.getMax_kdc_nums();
		} else {
			maxkc = limit.getMax_kkc_nums();
		}
		
		if (maxkc == null || maxkc.compareTo(BigDecimal.ZERO) <= 0) {
			return ret;
		}
		
		BigDecimal haveKc = BigDecimal.ZERO;
		// 持仓
		List<NMemberTransaction> transList = nMemberTransactionService.getTransactionByUserId(partition, code, userId);
		for (NMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}

		BigDecimal pendingUndeal = nPendingDelegationService.readSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = nPendingDelegationCacheService.readSumByUIDAction(partition, code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.Limt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
	}
	
	/**
	 * 交割合约-单合约单用户限制
	 * @param code
	 * @param direction
	 * @param userId
	 * @param amount
	 * @return
	 */
	protected String deliveryMaxUserKcCheck(String code, int direction, long userId, BigDecimal amount) {
		String ret = "";
		List<DeliveryUserOpenLimit> limitList = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_TRADE_OPEN_LIMTI_LIST);
		DeliveryUserOpenLimit limit = null;
		if (limitList == null) {
			limitList = dUserOpenLimitService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.DELIVERY_TRADE_OPEN_LIMTI_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, limitList);
		}
		if (limitList == null || limitList.size() <= 0) return ret;
		
		for (DeliveryUserOpenLimit bqlimit : limitList) {
			if (bqlimit.getF_code().equals(code) && bqlimit.getF_user_id() == userId) {
				limit = bqlimit;
				break;
			}
		}
		
		if (limit == null) return ret;
		
		BigDecimal maxkc = BigDecimal.ZERO;
		if (direction == Contents.DIRECTION_BUY) {
			maxkc = limit.getMax_kdc_nums();
		} else {
			maxkc = limit.getMax_kkc_nums();
		}
		
		if (maxkc == null || maxkc.compareTo(BigDecimal.ZERO) <= 0) {
			return ret;
		}
		
		BigDecimal haveKc = BigDecimal.ZERO;
		// 持仓
		List<DeliveryMemberTransaction> transList = dMemberTransactionService.getTransactionByUserId(code, userId);
		for (DeliveryMemberTransaction trans : transList) {
			if (trans.getTrade_type_id() == direction) {
				haveKc = haveKc.add(trans.getHold_count().add(trans.getLock_count()));
			}
		}
		
		BigDecimal pendingUndeal = dPendingDelegationService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		pendingUndeal = dPendingDelegationCacheService.readSumByUIDAction(code, userId, direction, Contents.PRIORITY_KC);
		if (pendingUndeal != null) {
			haveKc = haveKc.add(pendingUndeal);
		}
		
		BigDecimal cankc = maxkc.subtract(haveKc);
		if (amount.compareTo(cankc) > 0) {
			if (cankc.compareTo(BigDecimal.ZERO) < 0) cankc = BigDecimal.ZERO;
			ret = SpringContext.getText("msg.trade.risk.Limt", Contents.Local.LOCALE.get());
			ret = ret.replace("{0}", "" + cankc.doubleValue());
		}
		
		return ret;
	}
	
	protected Map<String, Integer> cancelVPendingWithCode(String code, int currencyId, int direction, long uid) throws ServiceException {
		int dealCount = 0;
		int cancelSuccessCount = 0;
		int cancelFailedCount = 0;
		
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		List<VMemberTransaction> transList = vMemberTransactionService.readTransactionByUserId(code, uid);
		try {
			boolean locked = true;
			for (VMemberTransaction trans : transList) {
				if (!vMemberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
					locked = false;
					break;
				} 
				
				dealCount++;
			}
	
			if (!locked) return null;
			
			List<VPendingDelegationCache> plistCache = vPendingDelegationCacheService.getAllByUserId(direction, code, uid);
			for (VPendingDelegationCache pendingDelegationCache : plistCache) {
				cancelFailedCount++;
				String lock = vPendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						vPendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, currencyId, true);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							vPendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<VPendingDelegation> plist = vPendingDelegationService.getAllByUserId(direction, code, uid);
			for (VPendingDelegation pendingDelegation : plist) {
				cancelFailedCount++;
				String lock = vPendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						vPendingDelegationService.cancelPending_one(pendingDelegation, currencyId, true);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							vPendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (VMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					vMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
			}
		}
		
		ret.put("success", cancelSuccessCount);
		ret.put("failed", cancelFailedCount);
		return ret;
	}
	
	protected Map<String, Integer> cancelPendingWithCode(String code, int direction, long uid, BigDecimal coefficient) throws ServiceException {
		int dealCount = 0;
		int cancelSuccessCount = 0;
		int cancelFailedCount = 0;
		
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		List<BQMemberTransaction> transList = bqMemberTransactionService.readTransactionByUserId(code, uid);
		try {
			boolean locked = true;
			for (BQMemberTransaction trans : transList) {
				if (!bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
					locked = false;
					break;
				} 
				
				dealCount++;
			}
	
			if (!locked) return null;
			
			List<PendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(direction, code, uid);
			for (PendingDelegationCache pendingDelegationCache : plistCache) {
				cancelFailedCount++;
				String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, coefficient, pendingDelegationCache.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(direction, code, uid);
			for (PendingDelegation pendingDelegation : plist) {
				cancelFailedCount++;
				String lock = pendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(pendingDelegation, coefficient, pendingDelegation.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							pendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (BQMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
			}
		}
		
		ret.put("success", cancelSuccessCount);
		ret.put("failed", cancelFailedCount);
		return ret;
	}
	
	protected Map<String, Integer> cancelNPendingWithCode(String partition, int currencyId, String code, int direction, long uid, BigDecimal coefficient) throws ServiceException {
		int dealCount = 0;
		int cancelSuccessCount = 0;
		int cancelFailedCount = 0;
		
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		List<NMemberTransaction> transList = nMemberTransactionService.readTransactionByUserId(partition, code, uid);
		try {
			boolean locked = true;
			for (NMemberTransaction trans : transList) {
				if (!nMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id())) {
					locked = false;
					break;
				} 
				
				dealCount++;
			}
	
			if (!locked) return null;
			
			List<NPendingDelegationCache> plistCache = nPendingDelegationCacheService.getAllByUserId(partition, direction, code, uid);
			for (NPendingDelegationCache pendingDelegationCache : plistCache) {
				cancelFailedCount++;
				String lock = nPendingDelegationCacheService.lock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						nPendingDelegationCacheService.cancelPendingCache_one(partition, currencyId, pendingDelegationCache, coefficient, pendingDelegationCache.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							nPendingDelegationCacheService.unLock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<NPendingDelegation> plist = nPendingDelegationService.getAllByUserId(partition, direction, code, uid);
			for (NPendingDelegation pendingDelegation : plist) {
				cancelFailedCount++;
				String lock = nPendingDelegationService.lock(partition, pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						nPendingDelegationService.cancelPending_one(partition, currencyId, pendingDelegation, coefficient, pendingDelegation.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							nPendingDelegationService.unLock(partition, pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (NMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					nMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
				}
				dealCount--;
			}
		}
		
		ret.put("success", cancelSuccessCount);
		ret.put("failed", cancelFailedCount);
		return ret;
	}
	
	protected Map<String, Integer> cancelDPendingWithCode(String code, int direction, long uid, BigDecimal coefficient) throws ServiceException {
		int dealCount = 0;
		int cancelSuccessCount = 0;
		int cancelFailedCount = 0;
		
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		List<DeliveryMemberTransaction> transList = dMemberTransactionService.readTransactionByUserId(code, uid);
		try {
			boolean locked = true;
			for (DeliveryMemberTransaction trans : transList) {
				if (!dMemberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
					locked = false;
					break;
				} 
				
				dealCount++;
			}
			
			if (!locked) return null;
			
			List<DeliveryPendingDelegationCache> plistCache = dPendingDelegationCacheService.getAllByUserId(direction, code, uid);
			for (DeliveryPendingDelegationCache pendingDelegationCache : plistCache) {
				cancelFailedCount++;
				String lock = dPendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						dPendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, coefficient, pendingDelegationCache.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							dPendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<DeliveryPendingDelegation> plist = dPendingDelegationService.getAllByUserId(direction, code, uid);
			for (DeliveryPendingDelegation pendingDelegation : plist) {
				cancelFailedCount++;
				String lock = dPendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						dPendingDelegationService.cancelPending_one(pendingDelegation, coefficient, pendingDelegation.getCommission_rate().compareTo(BigDecimal.ZERO) > 0);
						cancelSuccessCount++;
						cancelFailedCount--;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							dPendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (DeliveryMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					dMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
			}
		}
		
		ret.put("success", cancelSuccessCount);
		ret.put("failed", cancelFailedCount);
		return ret;
	}
	
	protected Map<String, Integer> cancelSpotPendingWithCode(BQSpotMarketCoinPair coinPair, int direction, long uid) throws ServiceException {
		int cancelSuccessCount = 0;
		int cancelFailedCount = 0;
		
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		boolean needLog = true;
		boolean isQuantUser = checkIsSpotQuantUser(uid);
		if (isQuantUser) {
			needLog = false;
		}
		
		List<SpotPendingDelegationCache> plistCache = spotPendingDelegationCacheService.getAllByUserId(direction, coinPair.getCoin_pair_name(), uid);
		for (SpotPendingDelegationCache pendingDelegationCache : plistCache) {
			cancelFailedCount++;
			String lock = spotPendingDelegationCacheService.lock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					spotPendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, needLog, coinPair);
					cancelSuccessCount++;
					cancelFailedCount--;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						spotPendingDelegationCacheService.unLock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> plist = spotPendingDelegationService.getAllByUserId(direction, coinPair.getCoin_pair_name(), uid);
		for (SpotPendingDelegation pendingDelegation : plist) {
			cancelFailedCount++;
			String lock = spotPendingDelegationService.lock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					spotPendingDelegationService.cancelPending_one(pendingDelegation, needLog, coinPair);
					cancelSuccessCount++;
					cancelFailedCount--;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						spotPendingDelegationService.unLock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
		
		ret.put("success", cancelSuccessCount);
		ret.put("failed", cancelFailedCount);
		return ret;
	}
	
	protected boolean checkIsSpotQuantUser(long userId) {
		boolean isQuantUser = false;
		BQSpotQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_QUANT_SETTING);
		if (setting == null) {
			setting = bqQuantSettingService.read();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		if (setting != null) {
			String[] quantUsers = setting.getUids().split(",");
			for (String quantUserId : quantUsers) {
				Long uid = Convert.toLong(quantUserId, 0);
				if (userId == uid) {
					isQuantUser = true; 
					break;
				}
			}
		}
		return isQuantUser;
	}
	
	protected int cancelDPlanPending(DeliveryPlanPendingDelegation pd) {
		int ret = 0;
		String lock = dPlanPendingDelegationService.lock(pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				int status = Contents.PLAN_PENDING_STATUS_CANCEL;
				String note = "计划委托挂单撤销";
				ret = dPlanPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
				ret = 0;
			} finally {
				if (pd != null) {
					dPlanPendingDelegationService.unLock(pd.getF_code(), pd.getId(), lock);
				}
			}
		}
		return ret;
	}
	
	protected int cancelPlanPending(PlanPendingDelegation pd) {
		int ret = 0;
		String lock = planPendingDelegationService.lock(pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				int status = Contents.PLAN_PENDING_STATUS_CANCEL;
				String note = "计划委托挂单撤销";
				ret = planPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
				ret = 0;
			} finally {
				if (pd != null) {
					planPendingDelegationService.unLock(pd.getF_code(), pd.getId(), lock);
				}
			}
		}
		return ret;
	}
	
	protected int cancelNPlanPending(String partition, NPlanPendingDelegation pd) {
		int ret = 0;
		String lock = nPlanPendingDelegationService.lock(partition, pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				int status = Contents.PLAN_PENDING_STATUS_CANCEL;
				String note = "计划委托挂单撤销";
				ret = nPlanPendingDelegationService.updateStatusAndNote(partition, pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
				ret = 0;
			} finally {
				if (pd != null) {
					nPlanPendingDelegationService.unLock(partition, pd.getF_code(), pd.getId(), lock);
				}
			}
		}
		return ret;
	}
	
	protected int cancelVPlanPending(VPlanPendingDelegation pd) {
		int ret = 0;
		String lock = vPlanPendingDelegationService.lock(pd.getF_code(), pd.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				int status = Contents.PLAN_PENDING_STATUS_CANCEL;
				String note = "计划委托挂单撤销";
				ret = vPlanPendingDelegationService.updateStatusAndNote(pd.getF_code(), pd.getId(), status, note);
			} catch (Exception e) {
				e.printStackTrace();
				ret = 0;
			} finally {
				if (pd != null) {
					vPlanPendingDelegationService.unLock(pd.getF_code(), pd.getId(), lock);
				}
			}
		}
		return ret;
	}
	
	protected int getNOneCanMultiple(String partition, NTransaction bqTransaction, long uid) {
		String code = bqTransaction.getTransaction_code();
		int canMultiple = 0;
		
		if (uid > 0) {
			List<NMemberTransaction> memberTrans = nMemberTransactionService.readTransactionByUserId(partition, code, uid);
			if (memberTrans.size() > 0) {
				NMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<NPendingDelegationCache> caches = nPendingDelegationCacheService.readPendingByUserIdAndTime(partition, code, uid, 0, 0, 0, 1, false);
				if (caches.size() > 0) {
					NPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<NPendingDelegation> pendings = nPendingDelegationService.readPendingByUserIdAndTime(partition, code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						NPendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						NPlanPendingDelegation planPending = nPlanPendingDelegationService.readOneByStatus(partition, code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
						if (planPending != null) {
							canMultiple = planPending.getMultiple();
						}
					}
				}
			}
		}
		
		return canMultiple;
	}

	protected int getOneCanMultiple(BQTransaction bqTransaction, long uid) {
		String code = bqTransaction.getTransaction_code();
		int canMultiple = 0;
		if (uid > 0) {
			List<BQMemberTransaction> memberTrans = bqMemberTransactionService.readTransactionByUserId(code, uid);
			if (memberTrans.size() > 0) {
				BQMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<PendingDelegationCache> caches = pendingDelegationCacheService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, 1, false);
				if (caches.size() > 0) {
					PendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<PendingDelegation> pendings = pendingDelegationService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						PendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						PlanPendingDelegation planPending = planPendingDelegationService.readOneByStatus(code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
						if (planPending != null) {
							canMultiple = planPending.getMultiple();
						}
					}
				}
			}
		}
		
		return canMultiple;
	}
	
	protected int getVOneCanMultiple(VTransaction transaction, long uid) {
		String code = transaction.getTransaction_code();
		int canMultiple = 0;		
		if (uid > 0) {
			List<VMemberTransaction> memberTrans = vMemberTransactionService.readTransactionByUserId(code, uid);
			if (memberTrans.size() > 0) {
				VMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<VPendingDelegationCache> caches = vPendingDelegationCacheService.readPendingByUserIdAndTime(code, uid, 0, 0, 1);
				if (caches.size() > 0) {
					VPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<VPendingDelegation> pendings = vPendingDelegationService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						VPendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						VPlanPendingDelegation planPending = vPlanPendingDelegationService.readOneByStatus(code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
						if (planPending != null) {
							canMultiple = planPending.getMultiple();
						}
					}
				}
			}
		}
		
		return canMultiple;
	}
	
	protected int getDOneCanMultiple(DeliveryTransaction transaction, long uid) {
		String code = transaction.getTransaction_code();
		int canMultiple = 0;
		
		if (uid > 0) {
			List<DeliveryMemberTransaction> memberTrans = dMemberTransactionService.readTransactionByUserId(code, uid);
			if (memberTrans.size() > 0) {
				DeliveryMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<DeliveryPendingDelegationCache> caches = dPendingDelegationCacheService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, 1, false);
				if (caches.size() > 0) {
					DeliveryPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<DeliveryPendingDelegation> pendings = dPendingDelegationService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						DeliveryPendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						DeliveryPlanPendingDelegation planPending = dPlanPendingDelegationService.readOneByStatus(code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
						if (planPending != null) {
							canMultiple = planPending.getMultiple();
						}
					}
				}
			}
		}
		
		return canMultiple;
	}
	
	protected List<MulpitleAndStateVO> getNMultipleVos(String type, NTransaction bqTransaction, long uid) {
		int canMultiple = getNOneCanMultiple(type, bqTransaction, uid);
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		if (canMultiple > 0) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(1), canMultiple + ""));
		} else {
			String[] multiples = bqTransaction.getMultiple().split(",");
			String[] multipleStates = bqTransaction.getMultiple_state().split(",");
			for (int i = 0; i < multiples.length; i++) {
				if ("1".equals(multipleStates[i])) {
					listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multipleStates[i]), multiples[i]));
				}
			}
		}
		
		return listMulpitleAndStateVOs;
	}
	
	protected List<MulpitleAndStateVO> getMultipleVos(BQTransaction bqTransaction, long uid) {
		int canMultiple = getOneCanMultiple(bqTransaction, uid);
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		if (canMultiple > 0) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(1), canMultiple + ""));
		} else {
			String[] multiples = bqTransaction.getMultiple().split(",");
			String[] multipleStates = bqTransaction.getMultiple_state().split(",");
			for (int i = 0; i < multiples.length; i++) {
				if ("1".equals(multipleStates[i])) {
					listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multipleStates[i]), multiples[i]));
				}
			}
		}
		
		return listMulpitleAndStateVOs;
	}
	
	protected List<MulpitleAndStateVO> getVMultipleVos(VTransaction transaction, long uid) {
		int canMultiple = getVOneCanMultiple(transaction, uid);
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		if (canMultiple > 0) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(1), canMultiple + ""));
		} else {
			String[] multiples = transaction.getMultiple().split(",");
			String[] multipleStates = transaction.getMultiple_state().split(",");
			for (int i = 0; i < multiples.length; i++) {
				if ("1".equals(multipleStates[i])) {
					listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multipleStates[i]), multiples[i]));
				}
			}
		}
		
		return listMulpitleAndStateVOs;
	}
	
	protected List<MulpitleAndStateVO> getDMultipleVos(DeliveryTransaction transaction, long uid) {
		int canMultiple = getDOneCanMultiple(transaction, uid);
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = new ArrayList<MulpitleAndStateVO>();
		if (canMultiple > 0) {
			listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(1), canMultiple + ""));
		} else {
			String[] multiples = transaction.getMultiple().split(",");
			String[] multipleStates = transaction.getMultiple_state().split(",");
			for (int i = 0; i < multiples.length; i++) {
				if ("1".equals(multipleStates[i])) {
					listMulpitleAndStateVOs.add(new MulpitleAndStateVO(Integer.valueOf(multipleStates[i]), multiples[i]));
				}
			}
		}
		
		return listMulpitleAndStateVOs;
	}
	
	protected BQMarketOrderSetting getMarketOrderSetting() {
		BQMarketOrderSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARKET_ORDER_SETTING);
		if (setting == null) {
			setting = bqMarketOrderSettingService.read();
			if(setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MARKET_ORDER_SETTING, BTCQMemecacheKeys.TIMES.ONE_DAY, setting);
			}
		}
		return setting;
	}
	
	protected String getNMarketOrderPrice(String partition, NTransaction transaction, int direction) {
		BQMarketOrderSetting setting = getMarketOrderSetting();
		if (setting != null && setting.isRunning()) {
			// 根据买卖方向， 获取买一卖一价，计算价格
			if (direction == 1) { // 买多或者平空，挂单价 = 卖一价 * （1 + price_range）
				List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(partition + "_" + transaction.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
				if (selltop5List == null) {
					selltop5List = nPendingDelegationService.getSellTop5(partition, transaction.getTransaction_code());
				}
				
				BigDecimal sell1Price = BigDecimal.ZERO;
				if (selltop5List != null && selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
				if (sell1Price.compareTo(BigDecimal.ZERO) > 0) {
					sell1Price = sell1Price.multiply(BigDecimal.ONE.add(setting.getPrice_range()));
					return sell1Price.toString();
				}
			} else if (direction == 2) { // 卖空或者平多，挂单价 = 买一价 * （1 - price_range）
				List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(partition + "_" + transaction.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
				if (buytop5List == null) {
					buytop5List = nPendingDelegationService.getBuyTop5(partition, transaction.getTransaction_code());
				}
				BigDecimal buy1Price = BigDecimal.ZERO;
				if (buytop5List != null && buytop5List.size() > 0) {
					buy1Price = buytop5List.get(0).getPrice();
				}
				if (buy1Price.compareTo(BigDecimal.ZERO) > 0) {
					buy1Price = buy1Price.multiply(BigDecimal.ONE.subtract(setting.getPrice_range()));
					return buy1Price.toString();
				}
			}
			
		}
		return "";
	}
	
	protected String getMarketOrderPrice(BQTransaction transaction, int direction) {
		BQMarketOrderSetting setting = getMarketOrderSetting();
		if (setting != null && setting.isRunning()) {
			// 根据买卖方向， 获取买一卖一价，计算价格
			if (direction == 1) { // 买多或者平空，挂单价 = 卖一价 * （1 + price_range）
				List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
				if (selltop5List == null) {
					selltop5List = pendingDelegationService.getSellTop5(transaction.getTransaction_code());
				}
				
				BigDecimal sell1Price = BigDecimal.ZERO;
				if (selltop5List != null && selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
				if (sell1Price.compareTo(BigDecimal.ZERO) > 0) {
					sell1Price = sell1Price.multiply(BigDecimal.ONE.add(setting.getPrice_range()));
					return sell1Price.toString();
				}
			} else if (direction == 2) { // 卖空或者平多，挂单价 = 买一价 * （1 - price_range）
				List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
				if (buytop5List == null) {
					buytop5List = pendingDelegationService.getBuyTop5(transaction.getTransaction_code());
				}
				BigDecimal buy1Price = BigDecimal.ZERO;
				if (buytop5List != null && buytop5List.size() > 0) {
					buy1Price = buytop5List.get(0).getPrice();
				}
				if (buy1Price.compareTo(BigDecimal.ZERO) > 0) {
					buy1Price = buy1Price.multiply(BigDecimal.ONE.subtract(setting.getPrice_range()));
					return buy1Price.toString();
				}
			}
			
		}
		return "";
	}
	
	protected String getDMarketOrderPrice(DeliveryTransaction transaction, int direction) {
		BQMarketOrderSetting setting = getMarketOrderSetting();
		if (setting != null && setting.isRunning()) {
			// 根据买卖方向， 获取买一卖一价，计算价格
			if (direction == 1) { // 买多或者平空，挂单价 = 卖一价 * （1 + price_range）
				List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_SELLTOP5LIST);
				if (selltop5List == null) {
					selltop5List = dPendingDelegationService.getSellTop5(transaction.getTransaction_code());
				}
				
				BigDecimal sell1Price = BigDecimal.ZERO;
				if (selltop5List != null && selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
				if (sell1Price.compareTo(BigDecimal.ZERO) > 0) {
					sell1Price = sell1Price.multiply(BigDecimal.ONE.add(setting.getPrice_range()));
					return sell1Price.toString();
				}
			} else if (direction == 2) { // 卖空或者平多，挂单价 = 买一价 * （1 - price_range）
				List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_BUYTOP5LIST);
				if (buytop5List == null) {
					buytop5List = dPendingDelegationService.getBuyTop5(transaction.getTransaction_code());
				}
				BigDecimal buy1Price = BigDecimal.ZERO;
				if (buytop5List != null && buytop5List.size() > 0) {
					buy1Price = buytop5List.get(0).getPrice();
				}
				if (buy1Price.compareTo(BigDecimal.ZERO) > 0) {
					buy1Price = buy1Price.multiply(BigDecimal.ONE.subtract(setting.getPrice_range()));
					return buy1Price.toString();
				}
			}
			
		}
		return "";
	}
	
	protected String getVMarketOrderPrice(VTransaction transaction, int direction) {
		BQMarketOrderSetting setting = getMarketOrderSetting();
		if (setting != null && setting.isRunning()) {
			// 根据买卖方向， 获取买一卖一价，计算价格
			if (direction == 1) { // 买多或者平空，挂单价 = 卖一价 * （1 + price_range）
				List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.V_SELLTOP5LIST);
				if (selltop5List == null) {
					selltop5List = vPendingDelegationService.getSellTop5(transaction.getTransaction_code());
				}
				
				BigDecimal sell1Price = BigDecimal.ZERO;
				if (selltop5List != null && selltop5List.size() > 0) {
					sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
				}
				if (sell1Price.compareTo(BigDecimal.ZERO) > 0) {
					sell1Price = sell1Price.multiply(BigDecimal.ONE.add(setting.getPrice_range()));
					return sell1Price.toString();
				}
			} else if (direction == 2) { // 卖空或者平多，挂单价 = 买一价 * （1 - price_range）
				List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.V_BUYTOP5LIST);
				if (buytop5List == null) {
					buytop5List = vPendingDelegationService.getBuyTop5(transaction.getTransaction_code());
				}
				BigDecimal buy1Price = BigDecimal.ZERO;
				if (buytop5List != null && buytop5List.size() > 0) {
					buy1Price = buytop5List.get(0).getPrice();
				}
				if (buy1Price.compareTo(BigDecimal.ZERO) > 0) {
					buy1Price = buy1Price.multiply(BigDecimal.ONE.subtract(setting.getPrice_range()));
					return buy1Price.toString();
				}
			}
			
		}
		return "";
	}
	
	protected BigDecimal getApproachMaxOpenAmount(BigDecimal remainAmount, BigDecimal maxAmount, BigDecimal price,
			BigDecimal feeRate, BigDecimal multiple, BigDecimal coefficient) {
		BigDecimal amount_client = maxAmount;
		BigDecimal fee = BTCQMath.getFee(amount_client, price, feeRate, coefficient);
		maxAmount = remainAmount.subtract(fee).multiply(multiple)
				.divide(price.multiply(coefficient), 5, BigDecimal.ROUND_HALF_UP)
				.setScale(4, BigDecimal.ROUND_HALF_DOWN);
		if (amount_client.compareTo(maxAmount) > 0) {
			amount_client = getApproachMaxOpenAmount(remainAmount, maxAmount, price, feeRate, multiple, coefficient);
		}
		return amount_client;
	}
}
