package com.btc100.btc_q.action.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.btc100.btc_q.common.*;
import com.btc100.btc_q.common.Utils;
import com.btc100.btc_q.util.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.CoinAssetsVO;
import com.btc100.btc_q.vo.InviteCurrencyRebateVo;
import com.btc100.btc_q.vo.InviteVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.RewardRecordVo;
import com.btc100.btc_q.vo.SystemContext;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.UserAssetsVO;
import com.btc100.btc_q.vo.UserInviteMemberVo;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import sun.misc.BASE64Decoder;

@Controller
@RequestMapping("/center")
public class UserCenterAction extends BaseAction {

	@Autowired
	private BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberLevelBuyAmountLogService bqMemberLevelBuyAmountLogService;
	@Autowired
	private BQCommonCreditLogService bqCommonCreditLogService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberPartnerService bqMemberPartnerService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQStopLossService bqStopLossService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQTransactionTypeService transactionTypeService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQMemberWalletService bqMemberWalletService;
	@Autowired
	private BQWalletQueueBtcService bqWalletQueueBtcService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private BQMemberWalletAddressService bqMemberWalletAddressService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberAreaService bqMemberAreaService;
	@Autowired
	private BQAreaCountryService bqAreaCountryService;
	@Autowired
	private UserCenterService userCenterService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	private BQExchangeRecordService bqExchangeRecordService;
	@Autowired
	private BQIeoAccountService bqIeoAccountService; 
	@Autowired
	private BQBonusRecordService bqBonusRecordService;
	@Autowired
	private BQMineRecordService bqMineRecordService;
	@Autowired
	private BQMineSettingService bqMineSettingService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQMemberInviteAmountService bqMemberInviteAmountService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQMemberCertificateService bqMemberCertificateService;
	@Autowired
	private BQMemberValidateInfoService bqMemberValidateInfoService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private BQUserRiskControlService bqUserRiskControlService;
	@Autowired
	private FundingFeeRecordService fundingFeeRecordService;
	@Autowired
	private BQFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private VFundingFeeRecordService vFundingFeeRecordService;
	@Autowired
	private VFundingFeeSettingService vFundingFeeSettingService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQBfxMinHoldService bqBfxMinHoldService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQExchangeBfcSettingService bqExchangeBfcSettingService;
	@Autowired
	private BQExchangeBfcRecordService bqExchangeBfcRecordService;
	@Autowired
	private BQMemberLevelBuySettingService bqMemberLevelBuySettingService;
	@Autowired
	private BQMemberLevelBuyRebateReconciliaService bqMemberLevelBuyRebateReconciliaService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NMemberTransactionService nMemberTransactionService;
	@Autowired
	private NPendingDelegationService nPendingDelegationService;
	@Autowired
	private NAccountContractService nAccountContractService;
	@Autowired
	private NFundingFeeSettingService nFundingFeeSettingService;
	@Autowired
	private NFundingFeeRecordService nFundingFeeRecordService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQMemberWalletService bqMemberWalletServcie;
	@Autowired
	private BQMemberModifyInformationRecordService bqMemberModifyInformationRecordService;
	
	Logger logger = Logger.getLogger(UserCenterAction.class);

	// 我的资产
	@RequestMapping("assets")
	public ModelAndView myAssets(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		BigDecimal totalUsdt = BigDecimal.ZERO;
		List<CoinAssetsVO> bqAccounts = new ArrayList<CoinAssetsVO>();
		List<BQCurrencyType> currencies = bqCurrencyTypeService.readAll();
		List<BQAccount> assets = bqAccountService.readAllByUserId(user.getF_user_id());
		for (BQAccount bqAccount : assets) {
			BQCurrencyType currencyType = null;
			for (BQCurrencyType currType : currencies) {
				if (currType.getType_id() == bqAccount.getCurrency_type_id()) {
					currencyType = currType;
					break;
				}
			}
			
			if (currencyType == null) continue;
			
			String coinName = currencyType.getType_name();
			CoinAssetsVO coinAssetsVO = new CoinAssetsVO();
			coinAssetsVO.setCoinId(bqAccount.getCurrency_type_id());
			coinAssetsVO.setFreeze(bqAccount.getBtc_locked().doubleValue());
			coinAssetsVO.setAvailable(bqAccount.getBtc().doubleValue());
			BigDecimal total = bqAccount.getBtc_locked().add(bqAccount.getBtc());
			coinAssetsVO.setTotal(total.doubleValue());
			coinAssetsVO.setCoinName(currencyType.getType_name());
			coinAssetsVO.setCoinDescribleZh(currencyType.getDescription_zh());
			coinAssetsVO.setCoinDescribleEn(currencyType.getDescription_en());
			coinAssetsVO.setCoinDescribleJp(currencyType.getDescription_jp());
			coinAssetsVO.setCoinDescribleKr(currencyType.getDescription_kr());
			coinAssetsVO.setCoinDescribleHk(currencyType.getDescription_hk());
			coinAssetsVO.setEnableRecharge(currencyType.isEnable_recharge());
			coinAssetsVO.setEnableWithdraw(currencyType.isEnable_withdraw());
			bqAccounts.add(coinAssetsVO);
			
			if (!coinName.equalsIgnoreCase("BFC")) {
				if (bqAccount.getCurrency_type_id() == Contents.TRADE_CURRENCY_TYPE_ID || coinName.equalsIgnoreCase("usdr")) {
					totalUsdt = totalUsdt.add(bqAccount.getBtc().add(bqAccount.getBtc_locked()));
				} else {
					BigDecimal indexPrice = findBPIByCode(coinName.toLowerCase());
					if (indexPrice != null && indexPrice.compareTo(BigDecimal.ZERO) > 0) {
						totalUsdt = totalUsdt.add(total.multiply(indexPrice));
					} else {
						BQSpotMarketCoinPair coinPair = findCurrCoinPairByName(coinName.toLowerCase() + "usdt");
						if (coinPair != null) {
							String pairName = coinPair.getCoin_pair_name();
							BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
							if (last == null) {
								last = coinPair.getLatest_price();
							}
							if (last != null && last.compareTo(BigDecimal.ZERO) > 0) {
								totalUsdt = totalUsdt.add(total.multiply(last));
							}
						} else {
							coinPair = findCurrCoinPairByName(coinName.toLowerCase() + "usdr");
							if (coinPair != null) {
								String pairName = coinPair.getCoin_pair_name();
								BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
								if (last == null) {
									last = coinPair.getLatest_price();
								}
								if (last != null && last.compareTo(BigDecimal.ZERO) > 0) {
									totalUsdt = totalUsdt.add((bqAccount.getBtc().add(bqAccount.getBtc_locked())).multiply(last));
								}
							}
						}
					}
				}
			}
		}
		
		
		BigDecimal ieoLock = BigDecimal.ZERO;
		BigDecimal ieoUnlock = BigDecimal.ZERO;
		BQIeoAccount account = bqIeoAccountService.getAccountByUserId(user.getF_user_id());
		if (account != null) {
			ieoLock = account.getLocked();
			ieoUnlock = account.getUnlocked();
		}
		
		Object[] retObj = (Object [])bqMemberLevelBuyAmountLogService.readSumBfxLock(user.getF_user_id());
		if (retObj.length == 2) {
			ieoLock = ieoLock.add((BigDecimal)retObj[0]);
			ieoUnlock = ieoUnlock.add((BigDecimal)retObj[1]);
		}
		
		if (ieoLock.compareTo(BigDecimal.ZERO) > 0 || ieoUnlock.compareTo(BigDecimal.ZERO) > 0) {
			model.addAttribute("ieoLock", ieoLock.doubleValue());
			model.addAttribute("ieoUnlock", ieoUnlock.doubleValue());
			
			BQSpotMarketCoinPair coinPair = findCurrCoinPairByName("bfxusdr");
			if (coinPair == null) {
				coinPair = findCurrCoinPairByName("bfxusdt");
			}
			if (coinPair != null) {
				String pairName = coinPair.getCoin_pair_name();
				BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (last == null) {
					last = coinPair.getLatest_price();
				}
				if (last != null && last.compareTo(BigDecimal.ZERO) > 0) {
					totalUsdt = totalUsdt.add((ieoLock.subtract(ieoUnlock)).multiply(last));
				}
			}
			
			for (CoinAssetsVO coinAssetsVO : bqAccounts) {
				if (coinAssetsVO.getCoinName().equalsIgnoreCase("bfx")) {
					coinAssetsVO.setTotal(coinAssetsVO.getTotal() + (ieoLock.doubleValue() - ieoUnlock.doubleValue()));
					break;
				}
			}
		}
		
		PagerModel pm = bqBonusRecordService.readRecords(user.getF_user_id(), 0, 0);
		model.addAttribute("pm", pm);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
		}
		
		model.addAttribute("usdtAvailable", totalUsdt.doubleValue());
		
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		if (StringUtil.isEmpty(usdRate)) { 
			usdRate = new BigDecimal(7);
		}
		BigDecimal totalCny = totalUsdt.multiply(usdRate).setScale(2, BigDecimal.ROUND_DOWN);
		model.addAttribute("totalCny", totalCny);
		model.addAttribute("usdRate", usdRate);
		
		model.addAttribute("bqAccounts", bqAccounts);
		model.addAttribute("currencies", currencies);
		mv.setViewName("jsp/user_assets");
		return mv;
	}
	
	// 合约资产
	@RequestMapping("/center")
	public ModelAndView center(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		Long id = user.getF_user_id();
		BQAccount bqAccount = bqAccountService.readAccount(id, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal deposit = bqMemberTransactionService.readDepositByUid(id);
		BigDecimal freez = bqAccountContractService.readLockAmountByUid(id);
		BigDecimal availableBtc = bqAccountContractService.readAmountByUid(id);
		BigDecimal account = bqAccount.getBtc();// 我的钱包
		
		model.addAttribute("deposit", deposit);// 持仓保证金
		model.addAttribute("freez", freez); // 挂单冻结
		model.addAttribute("availableBtc", availableBtc); // 合约总计
		model.addAttribute("account", account);

		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() != 0) {
			model.addAttribute("payPwd", true);
		} else {
			model.addAttribute("payPwd", false);
		}
		
		BigDecimal floatGainLoss = new BigDecimal(0);
		BigDecimal depositAndYk = BigDecimal.ZERO;
		BigDecimal fundingFee = BigDecimal.ZERO;
		
		// 盈亏计算...
		List<BQMemberTransaction> allBuyAndSellMemberTransaction = bqMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
		for (BQMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
			int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
			String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
			BQTransaction currentTransaction = super.findCurrTrans(transactionCode);
			if (currentTransaction == null ) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			} else {
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
//						ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
					floatGainLoss = floatGainLoss.add(ykdiff);
					
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
					floatGainLoss = floatGainLoss.add(ykdiff);
				}
				
				fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
				depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
						.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
						.add(buyOrSellMemberTransaction.getF_frozenremain());
			}
		}
		
		model.addAttribute("floatGainLoss", floatGainLoss);
		model.addAttribute("depositAndYk", depositAndYk);
		
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		if (StringUtil.isEmpty(usdRate)) { 
			usdRate = new BigDecimal(7);
		}
		BigDecimal total = depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee);
		BigDecimal totalCny = total.multiply(usdRate).setScale(2, BigDecimal.ROUND_DOWN);
		model.addAttribute("totalCny", totalCny);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_center");
		return mv;
	}
	
	@RequestMapping("/nCenter/{partition}")
	public ModelAndView ncenter(@PathVariable("partition") String partition, HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		NContractPartition part = nContractPartitionService.readByPartition(partition);
		Long id = user.getF_user_id();
		BQAccount bqAccount = bqAccountService.readAccount(id, part.getCurrency_type_id());
		BigDecimal deposit = nMemberTransactionService.readDepositByUid(partition, id);
		BigDecimal freez = nAccountContractService.readLockAmountByUid(partition, id);
		BigDecimal availableBtc = nAccountContractService.readAmountByUid(partition, id);
		BigDecimal account = bqAccount.getBtc();// 我的钱包
		
		model.addAttribute("deposit", deposit);// 持仓保证金
		model.addAttribute("freez", freez); // 挂单冻结
		model.addAttribute("availableBtc", availableBtc); // 合约总计
		model.addAttribute("account", account);

		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() != 0) {
			model.addAttribute("payPwd", true);
		} else {
			model.addAttribute("payPwd", false);
		}
		
		BigDecimal floatGainLoss = new BigDecimal(0);
		BigDecimal depositAndYk = BigDecimal.ZERO;
		BigDecimal fundingFee = BigDecimal.ZERO;
		
		List<NMemberTransaction> allBuyAndSellMemberTransaction = nMemberTransactionService.readMemberTrasactionByUserId(partition, user.getF_user_id());
		for (NMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
			int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
			String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
			NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
			if (currentTransaction == null ) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			} else {
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
//						ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
					floatGainLoss = floatGainLoss.add(ykdiff);
					
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
					floatGainLoss = floatGainLoss.add(ykdiff);
				}
				
				fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
				depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
						.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
						.add(buyOrSellMemberTransaction.getF_frozenremain());
			}
		}
		
		model.addAttribute("floatGainLoss", floatGainLoss);
		model.addAttribute("depositAndYk", depositAndYk);
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		if (StringUtil.isEmpty(usdRate)) { 
			usdRate = new BigDecimal(7);
		}
		BigDecimal total = depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee);
		BigDecimal totalCny = total.multiply(usdRate).setScale(2, BigDecimal.ROUND_DOWN);
		model.addAttribute("totalCny", totalCny);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_center");
		return mv;
	}
	
	@RequestMapping("/vCenter")
	public ModelAndView vCenter(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(list == null) {
			list = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		int currencyId = 0;
		if (list.size() > 0) {
			currencyId = list.get(0).getCurrency_type_id();
		}
		
		Long id = user.getF_user_id();
		BQAccount bqAccount = bqAccountService.readAccount(id, currencyId);
		BigDecimal deposit = vMemberTransactionService.readDepositByUid(id);
		BigDecimal freez = vAccountContractService.readLockAmountByUid(id);
		BigDecimal availableBtc = vAccountContractService.readAmountByUid(id);
		BigDecimal account = BigDecimal.ZERO;
		if (bqAccount != null) {
			account = bqAccount.getBtc();// 我的钱包
		}
		
		model.addAttribute("deposit", deposit);// 持仓保证金
		model.addAttribute("freez", freez); // 挂单冻结
		model.addAttribute("availableBtc", availableBtc); // 合约总计
		model.addAttribute("account", account);
		
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() != 0) {
			model.addAttribute("payPwd", true);
		} else {
			model.addAttribute("payPwd", false);
		}

		BigDecimal floatGainLoss = new BigDecimal(0);
		BigDecimal depositAndYk = BigDecimal.ZERO;
		BigDecimal fundingFee = BigDecimal.ZERO;
		
		List<VMemberTransaction> allBuyAndSellVMemberTransaction = vMemberTransactionService.readVMemberTrasactionByUserId(user.getF_user_id());
		for (VMemberTransaction buyOrSellVMemberTransaction : allBuyAndSellVMemberTransaction) {
			int buyOrSellTradeTypeId = buyOrSellVMemberTransaction.getTrade_type_id();
			String transactionCode = buyOrSellVMemberTransaction.getTransaction_code();
			VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
			if (currentTransaction == null ) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			} else {
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(buyOrSellVMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellVMemberTransaction.getLock_count().add(buyOrSellVMemberTransaction.getHold_count()));
//						ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
					floatGainLoss = floatGainLoss.add(ykdiff);
					
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = buyOrSellVMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellVMemberTransaction.getLock_count().add(buyOrSellVMemberTransaction.getHold_count()));
					floatGainLoss = floatGainLoss.add(ykdiff);
				}
				
				fundingFee = fundingFee.add(buyOrSellVMemberTransaction.getDeposit2());
				depositAndYk = depositAndYk.add(buyOrSellVMemberTransaction.getDeposit())
						.add(buyOrSellVMemberTransaction.getF_fromremain()).add(buyOrSellVMemberTransaction.getF_min_frozenremain())
						.add(buyOrSellVMemberTransaction.getF_frozenremain());
			}
		}

		model.addAttribute("floatGainLoss", floatGainLoss);
		model.addAttribute("depositAndYk", depositAndYk);
		model.addAttribute("fundingFee", fundingFee);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_center");
		return mv;
	}
	
	@RequestMapping("/otc")
	public ModelAndView otc(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		if (StringUtil.isEmpty(usdRate)) { 
			usdRate = new BigDecimal(7);
		}
		model.addAttribute("usdRate", usdRate);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_otc");
		return mv;
	}
	
	@RequestMapping("/dCenter")
	public ModelAndView dCenter(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		Long id = user.getF_user_id();
		BQAccount bqAccount = bqAccountService.readAccount(id, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal deposit = dMemberTransactionService.readDepositByUid(id);
		BigDecimal freez = dAccountContractService.readLockAmountByUid(id);
		BigDecimal availableBtc = dAccountContractService.readAmountByUid(id);
		BigDecimal account = bqAccount.getBtc();// 我的钱包
		
		model.addAttribute("deposit", deposit);// 持仓保证金
		model.addAttribute("freez", freez); // 挂单冻结
		model.addAttribute("availableBtc", availableBtc); // 合约总计
		model.addAttribute("account", account);

		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() != 0) {
			model.addAttribute("payPwd", true);
		} else {
			model.addAttribute("payPwd", false);
		}
		
		BigDecimal floatGainLoss = new BigDecimal(0);
		BigDecimal depositAndYk = BigDecimal.ZERO;
		BigDecimal fundingFee = BigDecimal.ZERO;

		List<DeliveryMemberTransaction> allBuyAndSellMemberTransaction = dMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
		for (DeliveryMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
			int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
			String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
			BQTransaction currentTransaction = super.findCurrTrans(transactionCode);
			if (currentTransaction == null ) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			} else {
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
//						ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
					floatGainLoss = floatGainLoss.add(ykdiff);
					
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
							.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
					floatGainLoss = floatGainLoss.add(ykdiff);
				}
				
				fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
				depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
						.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
						.add(buyOrSellMemberTransaction.getF_frozenremain());
			}
		}
		
		model.addAttribute("floatGainLoss", floatGainLoss);
		model.addAttribute("depositAndYk", depositAndYk);
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		if (StringUtil.isEmpty(usdRate)) { 
			usdRate = new BigDecimal(7);
		}
		BigDecimal total = depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee);
		BigDecimal totalCny = total.multiply(usdRate).setScale(2, BigDecimal.ROUND_DOWN);
		model.addAttribute("totalCny", totalCny);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_center");
		return mv;
	}
	
	/**
	 * 用户中心-合约资产
	 * 
	 */
	@RequestMapping("/detail")
	public void detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Gson gson = new Gson();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			AjaxCheckVO checkVO = new AjaxCheckVO();
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		
		// 资产详情
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		
		String type = request.getParameter("type");
		if (type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
			if (list == null) {
				list = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			
			List<BQMemberTransaction> userAllTrans = bqMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
			for (BQTransaction bqTransaction : list) {
				String code = bqTransaction.getTransaction_code();
				
				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_show_name());
				uav.setTransfer_in_enable(bqTransaction.getTransfer_in_enable());
				uav.setTransfer_out_enable(bqTransaction.getTransfer_out_enable());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				
				// 持仓保证金（当前合约总合计）
				BigDecimal holdDeposit = BigDecimal.ZERO;
				// 浮动盈亏（当前合约总合计）
				BigDecimal floatGainLoss = BigDecimal.ZERO;
				// 做多持仓量
				BigDecimal buyAccount = BigDecimal.ZERO;
				// 做空持仓量
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (BQMemberTransaction bqmt1 : userAllTrans) {
					if (bqmt1.getTransaction_code().equals(code)) {
						BigDecimal lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.BTCLATESTPRICE);
						if (lastPrice == null) {
							lastPrice = transactionService.readTransByCode(code).getLatest_transaction_price();
						}
						if (bqmt1.getTrade_type_id() == 1) {
							buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = lastPrice.subtract(bqmt1.getAverage_price()).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						} else {
							sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = bqmt1.getAverage_price().subtract(lastPrice).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						holdDeposit = bqmt1.getDeposit();
					}
				}
				
				uav.setBuyAmount(buyAccount);
				uav.setSellAmount(sellAccount);
				
//				// 持仓总量
//				BigDecimal bgsum = buyAccount.add(sellAccount);
//				uav.setTotalAmount(bgsum);
				
				uav.setHoldDeposit(holdDeposit);
				uav.setFloatGainLoss(floatGainLoss);
				
				// 做多委托
				BigDecimal deleBuyAccount = pendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = pendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
//				// 委托总量
//				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
//				uav.setDelegationAmount(delesum);
				// 委托开仓保证金（当前合约总合计）
				BigDecimal delegationDeposit = pendingDelegationService.readDelegationDeposit(id, code);
				uav.setDelegationDeposit(delegationDeposit);
				// 当前合约可用
				BQAccountContract accountContract = bqAccountContractService.readByUidCode(id, code);
				uav.setF_btc(accountContract.getF_btc());
				
				// 合约价值
				uav.setContractValue(delegationDeposit.add(holdDeposit).add(floatGainLoss).add(accountContract.getF_btc()));
				
				array.put(new JSONObject(gson.toJson(uav)));
			}
		} else if ("vcontract".equals(type)) {
			// 资产详情
			List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
			if(list == null) {
				list = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
			}
			
			List<VMemberTransaction> userAllTrans = vMemberTransactionService.readVMemberTrasactionByUserId(user.getF_user_id());
			for (VTransaction bqTransaction : list) {
				String code = bqTransaction.getTransaction_code();
				
				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_code().toUpperCase());
				uav.setTransfer_in_enable(bqTransaction.getTransfer_in_enable());
				uav.setTransfer_out_enable(bqTransaction.getTransfer_out_enable());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				
				// 持仓保证金（当前合约总合计）
				BigDecimal holdDeposit = BigDecimal.ZERO;
				// 浮动盈亏（当前合约总合计）
				BigDecimal floatGainLoss = BigDecimal.ZERO;
				// 做多持仓量
				BigDecimal buyAccount = BigDecimal.ZERO;
				// 做空持仓量
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (VMemberTransaction bqmt1 : userAllTrans) {
					if (bqmt1.getTransaction_code().equals(code)) {
						BigDecimal lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_BTCLATESTPRICE);
						if (lastPrice == null) {
							lastPrice = vTransactionService.readTransByCode(code).getLatest_transaction_price();
						}
						if (bqmt1.getTrade_type_id() == 1) {
							buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = lastPrice.subtract(bqmt1.getAverage_price()).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						} else {
							sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = bqmt1.getAverage_price().subtract(lastPrice).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						holdDeposit = bqmt1.getDeposit();
					}
				}
				
				uav.setBuyAmount(buyAccount);
				uav.setSellAmount(sellAccount);
				
//				// 持仓总量
//				BigDecimal bgsum = buyAccount.add(sellAccount);
//				uav.setTotalAmount(bgsum);
				
				uav.setHoldDeposit(holdDeposit);
				uav.setFloatGainLoss(floatGainLoss);
				
				// 做多委托
				BigDecimal deleBuyAccount = vPendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = vPendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
//				// 委托总量
//				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
//				uav.setDelegationAmount(delesum);
				// 委托开仓保证金（当前合约总合计）
				BigDecimal delegationDeposit = vPendingDelegationService.readDelegationDeposit(id, code);
				uav.setDelegationDeposit(delegationDeposit);
				// 当前合约可用
				VAccountContract accountContract = vAccountContractService.readByUidCode(id, code);
				uav.setF_btc(accountContract.getF_btc());
				
				// 合约价值
				uav.setContractValue(delegationDeposit.add(holdDeposit).add(floatGainLoss).add(uav.getF_btc()));
				
				array.put(new JSONObject(gson.toJson(uav)));
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
			if (list == null) {
				list = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			
			List<DeliveryMemberTransaction> userAllTrans = dMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
			for (DeliveryTransaction bqTransaction : list) {
				String code = bqTransaction.getTransaction_code();
				
				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_show_name());
				uav.setTransfer_in_enable(bqTransaction.getTransfer_in_enable());
				uav.setTransfer_out_enable(bqTransaction.getTransfer_out_enable());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				
				// 持仓保证金（当前合约总合计）
				BigDecimal holdDeposit = BigDecimal.ZERO;
				// 浮动盈亏（当前合约总合计）
				BigDecimal floatGainLoss = BigDecimal.ZERO;
				// 做多持仓量
				BigDecimal buyAccount = BigDecimal.ZERO;
				// 做空持仓量
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (DeliveryMemberTransaction bqmt1 : userAllTrans) {
					if (bqmt1.getTransaction_code().equals(code)) {
						BigDecimal lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
						if (lastPrice == null) {
							lastPrice = transactionService.readTransByCode(code).getLatest_transaction_price();
						}
						if (bqmt1.getTrade_type_id() == 1) {
							buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = lastPrice.subtract(bqmt1.getAverage_price()).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						} else {
							sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							BigDecimal ykdiff = BigDecimal.ZERO;
							ykdiff = bqmt1.getAverage_price().subtract(lastPrice).multiply(bqTransaction.getTrade_coefficient())
									.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						holdDeposit = bqmt1.getDeposit();
					}
				}
				
				uav.setBuyAmount(buyAccount);
				uav.setSellAmount(sellAccount);
				
//				// 持仓总量
//				BigDecimal bgsum = buyAccount.add(sellAccount);
//				uav.setTotalAmount(bgsum);
				
				uav.setHoldDeposit(holdDeposit);
				uav.setFloatGainLoss(floatGainLoss);
				
				// 做多委托
				BigDecimal deleBuyAccount = dPendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = dPendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
//				// 委托总量
//				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
//				uav.setDelegationAmount(delesum);
				// 委托开仓保证金（当前合约总合计）
				BigDecimal delegationDeposit = dPendingDelegationService.readDelegationDeposit(id, code);
				uav.setDelegationDeposit(delegationDeposit);
				// 当前合约可用
				DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(id, code);
				uav.setF_btc(accountContract.getF_btc());
				
				// 合约价值
				uav.setContractValue(delegationDeposit.add(holdDeposit).add(floatGainLoss).add(accountContract.getF_btc()));
				
				array.put(new JSONObject(gson.toJson(uav)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> list = super.findNCurrTrans(type);
				List<NMemberTransaction> userAllTrans = nMemberTransactionService.readMemberTrasactionByUserId(type, user.getF_user_id());
				for (NTransaction bqTransaction : list) {
					String code = bqTransaction.getTransaction_code();
					
					// 合约号
					UserAssetsVO uav = new UserAssetsVO();
					uav.setShowName(bqTransaction.getTransaction_show_name());
					uav.setTransfer_in_enable(bqTransaction.getTransfer_in_enable());
					uav.setTransfer_out_enable(bqTransaction.getTransfer_out_enable());
					uav.setCode(code);
					uav.setTransactionType(bqTransaction.getTransaction_type());
					
					// 持仓保证金（当前合约总合计）
					BigDecimal holdDeposit = BigDecimal.ZERO;
					// 浮动盈亏（当前合约总合计）
					BigDecimal floatGainLoss = BigDecimal.ZERO;
					// 做多持仓量
					BigDecimal buyAccount = BigDecimal.ZERO;
					// 做空持仓量
					BigDecimal sellAccount = BigDecimal.ZERO;
					for (NMemberTransaction bqmt1 : userAllTrans) {
						if (bqmt1.getTransaction_code().equals(code)) {
							BigDecimal lastPrice = spyMemcachedClient.get(type + "_" +  code + BTCQMemecacheKeys.BTCLATESTPRICE);
							if (lastPrice == null) {
								lastPrice = nTransactionService.readTransByCode(type, code).getLatest_transaction_price();
							}
							if (bqmt1.getTrade_type_id() == 1) {
								buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
								BigDecimal ykdiff = BigDecimal.ZERO;
								ykdiff = lastPrice.subtract(bqmt1.getAverage_price()).multiply(bqTransaction.getTrade_coefficient())
										.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
								floatGainLoss = floatGainLoss.add(ykdiff);
							} else {
								sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
								BigDecimal ykdiff = BigDecimal.ZERO;
								ykdiff = bqmt1.getAverage_price().subtract(lastPrice).multiply(bqTransaction.getTrade_coefficient())
										.multiply(bqmt1.getLock_count().add(bqmt1.getHold_count()));
								floatGainLoss = floatGainLoss.add(ykdiff);
							}
							holdDeposit = bqmt1.getDeposit();
						}
					}
					
					uav.setBuyAmount(buyAccount);
					uav.setSellAmount(sellAccount);
					
//					// 持仓总量
//					BigDecimal bgsum = buyAccount.add(sellAccount);
//					uav.setTotalAmount(bgsum);
					
					uav.setHoldDeposit(holdDeposit);
					uav.setFloatGainLoss(floatGainLoss);
					
					// 做多委托
					BigDecimal deleBuyAccount = nPendingDelegationService.readDelegation(type, id, code, 1);
					uav.setBuydelegation(deleBuyAccount);
					// 做空委托
					BigDecimal deleSellAccount = nPendingDelegationService.readDelegation(type, id, code, 2);
					uav.setSelldelegation(deleSellAccount);
//					// 委托总量
//					BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
//					uav.setDelegationAmount(delesum);
					// 委托开仓保证金（当前合约总合计）
					BigDecimal delegationDeposit = nPendingDelegationService.readDelegationDeposit(type, id, code);
					uav.setDelegationDeposit(delegationDeposit);
					// 当前合约可用
					NAccountContract accountContract = nAccountContractService.readByUidCode(type, id, code);
					uav.setF_btc(accountContract.getF_btc());
					
					// 合约价值
					uav.setContractValue(delegationDeposit.add(holdDeposit).add(floatGainLoss).add(accountContract.getF_btc()));
					
					array.put(new JSONObject(gson.toJson(uav)));
				}
			} else {
				
			}
		}
		
		userJson.put("data", array);
		toWeb(response, userJson);
	}

	// 合约流水 分页
	// type:是充值还是提现
	@RequestMapping("/money")
	public ModelAndView money(HttpServletRequest request, Model mo) throws UnsupportedEncodingException {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		String[] coins = new String[50];
		List<BQCurrencyType> types = bqCurrencyTypeService.readAll();
		for (BQCurrencyType type : types) {
			coins[type.getType_id()] = type.getType_name().toUpperCase();
		}
		
		PagerModel list = bqAccountAmountLogService.readListRechargeWithdraw(user.getF_user_id());
		mo.addAttribute("pm", list);
		mo.addAttribute("coins", coins);
		
		mv.setViewName("jsp/user_money");
		return mv;
	}
	
	// 账户安全
	@RequestMapping("/accountSafe")
	public ModelAndView acountSafe(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		// 基本信息
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		// 等级
		BQMemberLevel bql = bqMemberLevelService.readById(user.getF_user_level_id());
		String name = bql.getLevel_name();// 级别名称
		model.addAttribute("name", name);
		if (bql.getLevel_id() < bqMemberLevelService.readMaxLevel().getLevel_id()) {
			BQMemberLevel bql2 = bqMemberLevelService.findNextLevelById(user.getF_user_level_id());
			String name2 = bql2.getLevel_name();// 级别名称
			model.addAttribute("name2", name2);
			
			long cha2 = user.getF_user_credits() - bql.getLevel_credits();
			long cha = bql2.getLevel_credits() - bql.getLevel_credits();
			BigDecimal b1 = new BigDecimal(cha2);
			BigDecimal b2 = new BigDecimal(cha);
			
			double rate = b2.compareTo(BigDecimal.ZERO) == 0 ? 0 : b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
			model.addAttribute("rate", rate);
			
			BQMemberLevel bfxLevel = super.getLevelByUserBfx(user);
			if (bfxLevel.getLevel_id() > bql.getLevel_id()) {
				long dayTime = TimeUtil.getTimesmorning(System.currentTimeMillis() - 24 * 3600 * 1000);
				BQBfxMinHold minHold = bqBfxMinHoldService.readBfxMinHoldByUserAndTime(user.getF_user_id(), dayTime);
				model.addAttribute("bfxBalance", minHold.getMin_hold());
				model.addAttribute("bfxLevel", bfxLevel.getLevel_name());
			}
		}
		// 交易密码
		BQMemberPayword bqmp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqmp != null) {
			if (bqmp.getPwd() != null) {
				model.addAttribute("payword", 1);
				if (bqmp.getState() == 1) {
					model.addAttribute("checked", 1);
				} else {
					model.addAttribute("checked", 0);
				}
			}
		}
		// 所在地
		BQMemberArea bqma = bqMemberAreaService.readByUserId(user.getF_user_id());
		String country = bqma.getCountry_name();
		String pro = bqma.getProvince_name();
		String city = bqma.getCity_name();
		
		List<BQAreaCountry> lis = bqAreaCountryService.readCountry();
		if ("en_US".equals(getLocale(request).toString())) {
			if ("CN".equals(bqma.getCountry_code())) {
				country = "China";
				pro = StringUtil.getPinyinString(pro).replaceAll("sheng", "").replaceAll("shi", "");
				city = StringUtil.getPinyinString(city).replaceAll("shi", "").replaceAll("diqu", "");
			} else if (!"xx".equals(bqma.getCountry_code())) {
				for (BQAreaCountry bqAreaCountry : lis) {
					if (bqAreaCountry.getCode().equals(bqma.getCountry_code())) {
						country = bqAreaCountry.getEn_name();
						pro = "";
						city = "";
					}
				}
			}
		}
		if (StringUtil.isEmpty(pro) || StringUtil.isEmpty(city)) {
			model.addAttribute("area", country);
		} else {
			model.addAttribute("area", country + "---" + pro + "---" + city);
		}
		
		// 手机、谷歌
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1) {
			if (StringUtil.isEmpty(bqss.getF_user_tel())) {
				bqss.setF_user_safetype(0);
				bqMemberSafeSettingsService.updateTel(bqss);
			}
		} else if (bqss.getF_user_safetype() == 2) {
			if (StringUtil.isEmpty(bqss.getF_user_safegoogle())) {
				bqss.setF_user_safetype(0);
				bqMemberSafeSettingsService.updateTel(bqss);
			}
		} else if (bqss.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(bqss.getF_user_tel())) {
				int safeType = 2;
				if (StringUtil.isEmpty(bqss.getF_user_safegoogle())) {
					safeType = 0;
				}
				bqss.setF_user_safetype(safeType);
				bqMemberSafeSettingsService.updateTel(bqss);
			} else if (StringUtil.isEmpty(bqss.getF_user_safegoogle())) {
				int safeType = 1;
				if (StringUtil.isEmpty(bqss.getF_user_tel())) {
					safeType = 0;
				}
				bqss.setF_user_safetype(safeType);
				bqMemberSafeSettingsService.updateTel(bqss);
			}
		}
		
		if (bqss.getF_user_safetype() == 0) {
			model.addAttribute("tel", 0);
			model.addAttribute("google", 0);
		} else if (bqss.getF_user_safetype() == 1) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("google", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			model.addAttribute("tel", 0);
			model.addAttribute("google", 1);
		} else if (bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("google", 1);
		} else {
			model.addAttribute("tel", 0);
			model.addAttribute("google", 0);
		}
		
		String email = user.getF_user_email();// 登录邮箱
		model.addAttribute("email", email);
		model.addAttribute("realEmail", user.getF_real_email());
		
		model.addAttribute("feeRateType", user.getF_fee_rate_type());
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_acountSafe");
		return mv;
	}

	// 绑定谷歌
	@RequestMapping("/bindGoogle")
	public ModelAndView bindGoogle(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			} 
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.bindGoogle.request.success", Contents.Local.LOCALE.get()));
			}
		}
		ModelAndView mv = new ModelAndView();

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		String email = user.getF_user_email();
		String mobile = bqs.getF_user_tel();
		model.addAttribute("tyle", bqs.getF_user_safetype());
		model.addAttribute("email", email);
		model.addAttribute("mobile", mobile);
		String secretKey = null;
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		} 
		secretKey = GoogleAuthenticator.generateSecretKey();
		model.addAttribute("secretKey", secretKey);
		mv.setViewName("jsp/user_bindGoogle");
		return mv;
	}

	@RequestMapping("/bindGoogledo")
	public ModelAndView bindGoogledo(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		String ip = super.getIpAddr(request);
		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		} 
		
		String code = request.getParameter("code");// 手机验证码
		String google = request.getParameter("google");
		String secretKey = request.getParameter("secretKey");
		String tel = request.getParameter("mobile");
		if (bqs.getF_user_safetype() == 1) {
			if (code == null) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
			BQTelCode codenum = (BQTelCode) session.getAttribute("telcode" + tel);
			if (codenum == null) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
			if (!codenum.getF_user_tel().equals(tel)) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
			if (!codenum.getF_auth_code().equals(code)) {
				
				codenum.setF_error_times(codenum.getF_error_times() + 1);
				if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + tel);
				} else {
					session.setAttribute("telcode" + tel, codenum);
				}
				
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
		} else {
			String emailCode = request.getParameter("emailCode");// 邮箱验证码
			if (StringUtil.isEmpty(emailCode)) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
			
			BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
			if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
				if (cacheEmailCode != null) {
					cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
					spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
				}
				
				if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					model.addAttribute("error", "error4");
					return new ModelAndView("redirect:/center/bindGoogle.do", model);
				} 
				
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
			
			if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				model.addAttribute("error", "error4");
				return new ModelAndView("redirect:/center/bindGoogle.do", model);
			}
		}
		
		if (google == null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/bindGoogle.do", model);
		}

		// BQTelCode codenum =spyMemcachedClient.get("telcode");

		// BQMemberSafeSettings bqs =
		// bqMemberSafeSettingsService.findById(user.getF_user_id());

		if (GoogleAuthenticator.check_code(secretKey, Convert.toLong(google, 0), System.currentTimeMillis())) {
			bqs.setF_user_safegoogle(secretKey);
			if (bqs.getF_user_safetype() == 1) {
				bqs.setF_user_safetype(3);
			} else {
				bqs.setF_user_safetype(2);
			}
			// bqs.setF_user_safetype(3);
			bqMemberSafeSettingsService.updateSafe(bqs);

			BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
			informationRecord.setF_user_id(user.getF_user_id());
			informationRecord.setF_old_information("");
			informationRecord.setF_new_information(secretKey);
			informationRecord.setF_information_type(3);
			informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
			informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
			informationRecord.setF_modify_server(1);
			informationRecord.setF_modify_user("");
			informationRecord.setF_operation_type(2);
			bqMemberModifyInformationRecordService.save(informationRecord);

			/*
			if (user.getF_user_oms() == 0) {
				// 增加积分记录
				BQCommonCreditLog bqcl = new BQCommonCreditLog();
				bqcl.setF_user_id(user.getF_user_id());
				bqcl.setCredit_type_id(Contents.CREDIT_TYPE_BINDGOOGLE);
				bqcl.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDGOOGLE);
				bqcl.setTitle("绑定谷歌");
				bqcl.setDateline(DateUtil.DateToUnix(new Date()));
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl, false, 1);
			}
			*/
		} else {
			model.addAttribute("error", "error2");
			return new ModelAndView("redirect:/center/bindGoogle.do", model);
		}
		
		if (bqs.getF_user_safetype() == 1) {
			session.removeAttribute("telcode" + tel);
		} else {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		}
		
		model.addAttribute("success", "success1");
		return new ModelAndView("redirect:/center/bindGoogle.do", model);
	}
	
	@RequestMapping("/sliderChangePhone")
	public void sliderChangePhone(HttpServletRequest request, HttpServletResponse response) {
			toWeb(response, "true");
	}

	@RequestMapping("/changePhone")
	public String changePhoneNum(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return super.toLogin();
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		model.addAttribute("tel", bqms.getF_user_tel());
		
		if (!StringUtil.isEmpty(request.getParameter("success"))) {
			model.addAttribute("success", SpringContext.getText("msg.changePhone.request.success", Contents.Local.LOCALE.get()));
		}
		
		return "jsp/user_changePhone";
	}

	/* 验证手机号 */
	@RequestMapping("/changePhonedo")
	public ModelAndView changePhonedo(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		HttpSession session = request.getSession();
		String ip = super.getIpAddr(request);

		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());

		// 手机验证码
		String code = request.getParameter("code");// 旧手机验证码
		String tel = bqms.getF_user_tel(); // 使用Session中用户的手机号 // request.getParameter("mobile");
		String phoneNum = request.getParameter("mobilenew");// 新手机号
		String codenew = request.getParameter("codenew");// 新手机验证码
		String zone = request.getParameter("zone");
		ModelAndView mv = new ModelAndView();
		
		// 判断是否绑定手机号
		if (null == tel || tel.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.changePhone.request.unboundPhone", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		
		// 旧手机验证码判断
		if (null == code || code.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.changePhone.request.codeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		BQTelCode codenum = (BQTelCode) session.getAttribute("telcode" + tel);
		if (codenum == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		if (!codenum.getF_user_tel().equals(tel)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		if (!code.equals(codenum.getF_auth_code())) {
			
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				session.removeAttribute("telcode" + tel);
			} else {
				session.setAttribute("telcode" + tel, codenum);
			}
			
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		
		// 新手机验证码判断
		if (null == codenew || codenew.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}

		String key = (StringUtil.isEmpty(zone) ? "" : zone) + phoneNum;
		BQTelCode codenum2 = (BQTelCode) session.getAttribute("telcode" + key);
		if (codenum2 == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		if (!codenum2.getF_user_tel().equals(phoneNum)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		if (!codenew.equals(codenum2.getF_auth_code())) {
			
			codenum2.setF_error_times(codenum2.getF_error_times() + 1);
			if (codenum2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				session.removeAttribute("telcode" + key);
			} else {
				session.setAttribute("telcode" + key, codenum2);
			}
			
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			model.addAttribute("tel", bqms.getF_user_tel());
			mv.setViewName("jsp/user_changePhone");
			return mv;
		}
		
		session.removeAttribute("telcode" + tel);
		session.removeAttribute("telcode" + key);

		bqms.setF_user_safequertion(bqms.getF_user_safequertion());
		bqms.setF_user_id(bqms.getF_user_id());
		bqms.setF_user_safeanswer(bqms.getF_user_safeanswer());
		bqms.setF_user_safegoogle(bqms.getF_user_safegoogle());
		int type = bqms.getF_user_safetype();
		if (type == 1) {
			bqms.setF_user_safetype(1);
		} else {
			bqms.setF_user_safetype(3);
		}
		bqms.setF_user_tel(phoneNum);
		bqms.setF_user_zone(zone == null ? "" : zone);
		
		if (user.getF_user_email_status() != 1 || user.getF_user_email().equals(tel)) {
			bqMemberService.updatePhone(bqms);
			
			super.findCurrUser(request, true);
		} else {
			bqMemberSafeSettingsService.updateTel(bqms);			
			
			SendMail themail = new SendMail();
			String content = SpringContext.getText("msg.changePhone.request.content1", Contents.Local.LOCALE.get());
			try {
				themail.send(user.getF_real_email(), content);
				model.addAttribute("success", SpringContext.getText("msg.changePhone.request.success", Contents.Local.LOCALE.get()));
			} catch (Exception e) {
				logger.error("修改手机号码时，发送邮件失败", e);
			}
		}

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information(tel);
		informationRecord.setF_new_information(phoneNum);
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(1);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(3);
		bqMemberModifyInformationRecordService.save(informationRecord);

		model.addAttribute("tel", phoneNum);
		
		mv.setViewName("jsp/user_changePhone");
		
		
		return new ModelAndView("redirect:/center/changePhone.do?success=1");
	}
	
	@RequestMapping("/sliderBindPhone")
	public void sliderBindPhone(HttpServletRequest request, HttpServletResponse response) {
			toWeb(response, "true");
	}

	@RequestMapping("/bindPhone")
	public ModelAndView bindPhoneNum(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (!StringUtil.isEmpty(bqms.getF_user_tel())) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		}
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_bindPhone");
		return mv;
	}
	
	/* 验证手机号 */
	@RequestMapping("/bindPhonedo")
	public ModelAndView bindPhonedo(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		// 手机验证码
		HttpSession session = request.getSession();

		String ip = super.getIpAddr(request);
		ModelAndView mv = new ModelAndView();
		
		String phoneNum = request.getParameter("mobile");
		String code = request.getParameter("code");
		String emailCode = request.getParameter("emailCode");
		String zone = request.getParameter("zone");
		
		if (null == code || code.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		
		if (null == emailCode || emailCode.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}

		String key = (StringUtil.isEmpty(zone) ? "" : zone) + phoneNum;
		BQTelCode codenum = (BQTelCode) session.getAttribute("telcode" + key);
		if (codenum == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		if (!codenum.getF_user_tel().equals(phoneNum)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		if (!code.equals(codenum.getF_auth_code())) {
			
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				session.removeAttribute("telcode" + key);
			} else {
				session.setAttribute("telcode" + key, codenum);
			}
			
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (!StringUtil.isEmpty(bqms.getF_user_tel())) {
			model.addAttribute("error", SpringContext.getText("msg.bindPhone.request.hasBeenBound", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
			if (cacheEmailCode != null) {
				cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			}
			
			if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_bindPhone");
				return mv;
			} 
			
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		
		if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindPhone");
			return mv;
		}
		
		bqms.setF_user_safequertion(bqms.getF_user_safequertion());
		bqms.setF_user_id(bqms.getF_user_id());
		bqms.setF_user_safeanswer(bqms.getF_user_safeanswer());
		bqms.setF_user_safegoogle(bqms.getF_user_safegoogle());
		if (bqms.getF_user_safetype() == 2) {
			bqms.setF_user_safetype(3);
		} else {
			bqms.setF_user_safetype(1);
		}
		
		bqms.setF_user_tel(phoneNum);
		bqms.setF_user_zone(StringUtil.isEmpty(zone) ? "" : zone);
		bqMemberSafeSettingsService.updateTel(bqms);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(phoneNum);
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(1);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(2);
		bqMemberModifyInformationRecordService.save(informationRecord);

		session.removeAttribute("telcode" + key);
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		
		/*
		if (user.getF_user_oms() == 0) {
			// 增加积分记录
			BQCommonCreditLog bqcl = new BQCommonCreditLog();
			bqcl.setF_user_id(user.getF_user_id());
			bqcl.setCredit_type_id(Contents.CREDIT_TYPE_BINDPHONE);
			bqcl.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
			bqcl.setTitle("绑定手机");
			bqcl.setDateline(DateUtil.DateToUnix(new Date()));
			bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl, false, 1);
		}
		*/

		BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(user.getF_user_id());
		if (log != null) {
			BQMember inviter = bqMemberService.readUserById(log.getInvite_uid());
			if (inviter != null) {
				BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
				if (setting != null && setting.getInvite_reward().compareTo(BigDecimal.ZERO) > 0) {
					BQBftRewardRecord record = new BQBftRewardRecord();
					record.setF_user_id(inviter.getF_user_id());
					record.setCurrency_type_id(setting.getCurrency_type_id());
					record.setReason(Contents.REWARD_REASON_INVITE);
					record.setAmount(setting.getInvite_reward());
					record.setNote("邀请用户赠送");
					
					bqBftRewardRecordService.bonus(record);
				}
			}
		}
		
		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.bindPhone.request.content1", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
			model.addAttribute("success", SpringContext.getText("msg.bindPhone.request.success", Contents.Local.LOCALE.get()));
		} catch (Exception e) {
			logger.error("绑定手机号码时，发送邮件失败", e);
		}
		
		mv.setViewName("jsp/user_bindPhone");
		return mv;
	}

	@RequestMapping("/changeEmail")
	public String changeEmail(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return super.toLogin();
		}

		model.addAttribute("email", user.getF_real_email());

		if (!StringUtil.isEmpty(request.getParameter("success"))) {
			model.addAttribute("success", SpringContext.getText("msg.changeEmail.request.success", Contents.Local.LOCALE.get()));
		}

		return "jsp/user_changeEmail";
	}

	@RequestMapping("/changeEmaildo")
	public ModelAndView changeEmaildo(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		HttpSession session = request.getSession();
		String ip = super.getIpAddr(request);

		String oldEmailCode = request.getParameter("oldEmailCode");
		String oldEmail = user.getF_real_email();
		String newEmailCode = request.getParameter("newEmailCode");
		String newEmail = request.getParameter("newEmail");

		ModelAndView mv = new ModelAndView();

		if (null == oldEmail || oldEmail.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.changeEmail.request.unboundEmail", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}

		// 旧邮箱验证码判断
		if (null == oldEmailCode || oldEmailCode.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.changeEmail.request.codeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}
		BQTelCode codenum = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + oldEmail);
		if (codenum == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailOrPwdNull", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}
		if (!codenum.getF_user_tel().equals(oldEmail)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}
		if (!oldEmailCode.equals(codenum.getF_auth_code())) {

			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + oldEmail);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + oldEmail,BTCQMemecacheKeys.TIMES.BPI, codenum);
			}

			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}

		// 新手机验证码判断
		if (null == newEmailCode || newEmailCode.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}

		BQTelCode codenum2 = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + newEmail);
		if (codenum2 == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeNull", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}
		if (!codenum2.getF_user_tel().equals(newEmail)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}
		if (!newEmailCode.equals(codenum2.getF_auth_code())) {

			codenum2.setF_error_times(codenum2.getF_error_times() + 1);
			if (codenum2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + newEmail);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + newEmail,BTCQMemecacheKeys.TIMES.BPI, codenum2);
			}

			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			model.addAttribute("email", user.getF_real_email());
			mv.setViewName("jsp/user_changeEmail");
			return mv;
		}

		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + oldEmail);
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + newEmail);

		if (user.getF_user_email_status() == 1 && user.getF_user_email().equals(user.getF_real_email())) {
			bqMemberService.updateEmailByState(user.getF_user_id(),newEmail,true);
		} else {
			bqMemberService.updateEmailByState(user.getF_user_id(),newEmail,false);
		}
		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information(oldEmail);
		informationRecord.setF_new_information(newEmail);
		informationRecord.setF_information_type(2);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(1);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(3);
		bqMemberModifyInformationRecordService.save(informationRecord);

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.changEmail.request.content1", Contents.Local.LOCALE.get());
		try {
			themail.send(newEmail, content);
			model.addAttribute("success", SpringContext.getText("msg.changeEmail.request.success", Contents.Local.LOCALE.get()));
		} catch (Exception e) {
			logger.error("修改邮箱时时，发送邮件失败", e);
		}

		model.addAttribute("email", newEmail);

		mv.setViewName("jsp/user_changeEmail");


		return new ModelAndView("redirect:/center/changeEmail.do?success=1");
	}

	@RequestMapping("/bindEmail")
	public ModelAndView bindEmail(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		if (!StringUtil.isEmpty(user.getF_real_email())) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		}

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_bindEmail");
		return mv;
	}


	/**
	 * 发送邮箱验证码，成功后存入缓存 还没有绑定邮箱之前
	 */
	@RequestMapping("/sendEmailCodeBeforeBindEmail")
	public void sendEmailCodeBeforeBindEmail(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));
		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "error");
			return;
		}
		String email = request.getParameter("email");
		if (email.equals("")) {
			logger.error("用户提交的邮箱为空！提交号码：" + email);
			toWeb(response, SpringContext.getText("msg.pg.login.nullEmail", Contents.Local.LOCALE.get()));
			return;
		}

		BQMember bq = bqMemberService.readByRealEmail(email);
		if (null != bq) {
			logger.error("该邮箱已被使用" + email);
			toWeb(response, SpringContext.getText("msg.pg.register.hasEmail", Contents.Local.LOCALE.get()));
			return;
		}

		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			toWeb(response, SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			return;
		}

		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		try {
			SendMail themail = new SendMail();
			themail.send(email, themail.getApiStr(com.btc100.btc_q.util.Utils.getWebRoot(request), code));

			if (cacheEmailCode == null) {
				cacheEmailCode = new BQTelCode();
				cacheEmailCode.setF_error_times(0);
			}

			cacheEmailCode.setF_user_id(user.getF_user_id());
			cacheEmailCode.setF_user_tel(email);
			cacheEmailCode.setF_auth_code(code);

			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);

		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			toWeb(response, SpringContext.getText("msg.register.request.sendCodeFail", Contents.Local.LOCALE.get()));
			return;
		}
		toWeb(response, "success");
		return;
	}

	@RequestMapping("/checkEmailCode")
	public void checkEmailCode(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "false");
			return;
		}

		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email) && !StringUtil.isEmpty(user.getF_real_email())) email = user.getF_real_email();

		String code = request.getParameter("code");
		BQTelCode code2 = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (code2 == null) {
			toWeb(response, "false");
			return;
		}
		if (!email.equals(code2.getF_user_tel())) {
			toWeb(response, "false");
			return;
		}
		if (!code.equals(code2.getF_auth_code())) {
			code2.setF_error_times(code2.getF_error_times() + 1);
			if (code2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, code2);
			}
			toWeb(response, "false");
			return;
		}
		toWeb(response, "true");
		return;
	}

	@RequestMapping("/bindEmaildo")
	public ModelAndView bindEmaildo(HttpServletRequest request, Model model)  {
		Contents.Local.LOCALE.set(getLocale(request));
		HttpSession session = request.getSession();

		String ip = super.getIpAddr(request);
		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		String email = request.getParameter("email");
		String emailCode = request.getParameter("emailCode");
		String telCode = request.getParameter("code");

		if (!StringUtil.isEmpty(user.getF_real_email())){
			model.addAttribute("error", SpringContext.getText("msg.pg.register.hasEmail", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}
		if (null == telCode || telCode.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}
		if (null == emailCode || emailCode.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}

		BQTelCode cacheCodeEmail = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (cacheCodeEmail == null) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}
		if (!cacheCodeEmail.getF_user_tel().equals(email)) {
			model.addAttribute("error", SpringContext.getText("msg.pg.registerSuccess.note1", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}
		if (!emailCode.equals(cacheCodeEmail.getF_auth_code())) {

			cacheCodeEmail.setF_error_times(cacheCodeEmail.getF_error_times() + 1);
			if (cacheCodeEmail.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheCodeEmail);
			}

			model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}

		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		BQTelCode cacheTelCode = (BQTelCode) session.getAttribute("telcode" + bqms.getF_user_tel());
		if (cacheTelCode == null || !telCode.equals(cacheTelCode.getF_auth_code())) {
			if (cacheTelCode != null) {
				cacheTelCode.setF_error_times(cacheTelCode.getF_error_times() + 1);
				session.setAttribute("telcode" + bqms.getF_user_tel(), cacheTelCode);
			}
			if (cacheTelCode != null && cacheTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.codeTimes", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_bindEmail");
				return mv;
			}

			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}

		if (cacheTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeTimes", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_bindEmail");
			return mv;
		}

		bqMemberService.updateEmailByState(user.getF_user_id(), email,false);
		bqMemberService.updateEmailStatus(user.getF_user_id(), 1);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(email);
		informationRecord.setF_information_type(2);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(1);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(2);
		bqMemberModifyInformationRecordService.save(informationRecord);

		session.removeAttribute("telcode" + bqms.getF_user_tel());
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);

		BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(user.getF_user_id());
		if (log != null) {
			BQMember inviter = bqMemberService.readUserById(log.getInvite_uid());
			if (inviter != null) {
				BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
				if (setting != null && setting.getInvite_reward().compareTo(BigDecimal.ZERO) > 0) {
					BQBftRewardRecord record = new BQBftRewardRecord();
					record.setF_user_id(inviter.getF_user_id());
					record.setCurrency_type_id(setting.getCurrency_type_id());
					record.setReason(Contents.REWARD_REASON_INVITE);
					record.setAmount(setting.getInvite_reward());
					record.setNote("邀请用户赠送");
					bqBftRewardRecordService.bonus(record);
				}
			}
		}

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.bindEmail.request.content1", Contents.Local.LOCALE.get());
		try {
			themail.send(email, content);
			model.addAttribute("success", SpringContext.getText("msg.bindEmail.request.success", Contents.Local.LOCALE.get()));
		} catch (Exception e) {
			logger.error("绑定邮箱时，发送邮件失败", e);
		}

		mv.setViewName("jsp/user_bindEmail");
		return mv;
	}

	@RequestMapping("/sendOldTelCode")
	public void sendOldTelCode(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "error");
			return;
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (StringUtil.isEmpty(bqms.getF_user_tel())) {
			logger.error("发送失败！");
			toWeb(response, "error");
			return;
		}
		
		int random = MessageInterface.random6();
		BQTelCode code = (BQTelCode) session.getAttribute("telcode" + bqms.getF_user_tel());
		if (code != null) {
			session.removeAttribute("telcode" + bqms.getF_user_tel());
		}
		
		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), bqms.getF_user_tel(), random, bqms.getF_user_zone());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			code = new BQTelCode();
			code.setF_user_tel(bqms.getF_user_tel());
			code.setF_auth_code(String.valueOf(random));
			session.setAttribute("telcode" + bqms.getF_user_tel(), code);
			toWeb(response, "success");
			return;
		} else {
			toWeb(response, sendRet);
			return;
		}
	}
	
	@RequestMapping(value = "/sendEmailCode", method = RequestMethod.POST)
	public void sendEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "请重新登录！");
			return;
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			toWeb(response, SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			return;
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		try {
			SendMail themail = new SendMail();
			themail.send(user.getF_real_email(), themail.getApiStr(com.btc100.btc_q.util.Utils.getWebRoot(request), code));
			
			if (cacheEmailCode == null) {
				cacheEmailCode = new BQTelCode();
				cacheEmailCode.setF_error_times(0);
			}
			
			cacheEmailCode.setF_user_id(user.getF_user_id());
			cacheEmailCode.setF_auth_code(code);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			
		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			toWeb(response, "发送邮箱验证码失败，请稍后重试！");
			return;
		}
		
		toWeb(response, "success");
		return;
	}

	@RequestMapping(value = "/sendOldEmailCode", method = RequestMethod.POST)
	public void sendOldEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "请重新登录！");
			return;
		}

		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email());
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			toWeb(response, SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			return;
		}

		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		try {
			SendMail themail = new SendMail();
			themail.send(user.getF_real_email(), themail.getApiStr(com.btc100.btc_q.util.Utils.getWebRoot(request), code));

			if (cacheEmailCode == null) {
				cacheEmailCode = new BQTelCode();
				cacheEmailCode.setF_error_times(0);
			}

			cacheEmailCode.setF_user_id(user.getF_user_id());
			cacheEmailCode.setF_user_tel(user.getF_real_email());
			cacheEmailCode.setF_auth_code(code);

			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);

		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			toWeb(response, "发送邮箱验证码失败，请稍后重试！");
			return;
		}

		toWeb(response, "success");
		return;
	}

	/**
	 * 发送手机验证码，成功后存入缓存 还没有绑定手机号码前
	 */
	@RequestMapping("/sendTelCodeBeforeBindTel")
	public void sendTelCodeBeforeBindTel(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			logger.error("用户会话超时，请重新登录！");
			toWeb(response, "error");
			return;
		}

		String tel = request.getParameter("Tel");
		if (tel.equals("")) {
			logger.error("用户提交的号码为空！提交号码：" + tel);
			toWeb(response, SpringContext.getText("msg.pg.userBindPhone.phoneNull", Contents.Local.LOCALE.get()));
			return;
		}

		if (bqMemberSafeSettingsService.readHaveTel(tel)) {
			logger.error("改号码已被使用" + tel);
			toWeb(response, SpringContext.getText("msg.pg.userBindPhone.phoneRegistered", Contents.Local.LOCALE.get()));
			return;
		}
		
		int random = MessageInterface.random6();

		String zone = request.getParameter("zone");
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		BQTelCode code = (BQTelCode) session.getAttribute("telcode" + key);
		if (code != null) {
			if (code.getF_user_tel().equals(tel)) {
				session.removeAttribute("telcode" + key);
			}
		}

		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), tel, random, zone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			code = new BQTelCode();
			code.setF_user_tel(tel);
			code.setF_auth_code(String.valueOf(random));
			session.setAttribute("telcode" + key, code);
			toWeb(response, "success");
			return;
		} else {
			toWeb(response, sendRet);
			return;
		}
	}



	/**
	 * 发送语音验证码 还没有绑定手机号码前
	 */
	@RequestMapping("/sendTelCodeVoiceBeforeBindTel")
	public void sendTelCodeVoiceBeforeBindTel(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		try {
			BQMember user = super.findCurrUser(request, false);
			if (user == null) {
				logger.error("用户会话已过期，请重新登录！");
				toWeb(response, "error");
				return;
			}
			String tel = request.getParameter("Tel");
			if (tel.equals("")) {
				logger.error("用户提交的号码为空，提交号码：" + tel);
				toWeb(response, "error:用户提交的号码为空！");
				return;
			}
			BQTelCode code = null;
			code = (BQTelCode) request.getSession().getAttribute("telcode" + tel);
			if (code != null) {
				logger.error("已发送过验证码，请稍候再试！");
				request.getSession().removeAttribute("telcode" + tel);
				toWeb(response, "error");
				return;
			}

			int random = MessageInterface.random6();
			// 存入缓存，成功后发送验证码
			code = new BQTelCode();
			code.setF_user_tel(tel);
			code.setF_auth_code(String.valueOf(random));
			// 发送验证码
			if (sendTelCodeVoice(code, tel, random, request)) {
				toWeb(response, "success");
				return;
			} else {
				logger.error("发送语音验证码失败！");
				toWeb(response, "error");
				return;
			}

		} catch (Exception e) {
			logger.error("发送语音验证码崩溃：" + e.getMessage() + "\r\n" + e.getStackTrace());
			toWeb(response, "error");
			return;
		}
	}

	/**
	 * 发送手机验证码
	 * 
	 * @param telNum
	 *            手机号
	 * @param random
	 *            6位随机码
	 * @return
	 */
	private boolean sendTelCodeVoice(BQTelCode code, String telNum, int random, HttpServletRequest request) throws Exception {
		String tel = telNum;
		try {
			HttpSession session = request.getSession();
			logger.error("语音验证码发送 sendTelCodeVoice\r\n");

			String host = "sandboxapp.cloopen.com";
			String port = "8883";
			String version = "2013-12-26";
			String account_sid = "aaf98fda42e022750142fc62cb75037e";
			String authtoken = "1c89774ccc5f410bb13b6861839c22a0";
			String appid = "aaf98f8945742c0e0145746c3a10006e";
			String playTimes = "2";
			String displayNum = "";
			String respUrl = "https://123.149.20.127:89/vsmsapi/vsms?action=VoiceCode";

			StringBuffer sb = new StringBuffer();

			if (host.startsWith("http"))
				host = host.replace("http://", "");
			if (port.equals(""))
				port = "8883";

			sb = new StringBuffer();
			if (!playTimes.equals(""))
				playTimes = "2"; // 语音验证码默认读两遍

			if (displayNum.equals(""))
				displayNum = "037155031576"; // 默认显示公司固话号码

			if (telNum.length() == 11 && (telNum.startsWith("13") || telNum.startsWith("14")))
				telNum = "0086" + telNum;

			sb.append("{\"appId\":\"" + appid + "\",\"verifyCode\":\"" + random + "\",\"playTimes\":\"" + playTimes + "\"," + "\"to\":\"" + telNum
					+ "\",\"displayNum\":\"" + displayNum + "\",\"respUrl\":\"" + respUrl + "\"}");

			String postData = sb.toString();
			logger.error("语音验证码发送HttpsPost Before\r\n");

			String ret = Utils.HttpsPost(host, Utils.StrToInt(port, 8883), account_sid, authtoken, postData, version);

			logger.error("语音验证码发送HttpsPost After\r\n");
			// 响应 成功后数据存入缓存
			try {
				org.json.JSONObject obj = new org.json.JSONObject(ret);
				String code1 = obj.getString("statusCode");
				if (code1.equals("000000")) {
					session.setAttribute("telcode" + tel, code);
					return true;
				}
				logger.error("语音验证码发送失败：" + ret + "\n");

				return false;

			} catch (Exception ex) {

				logger.error("语音验证码发送失败：" + ret + "\r\n" + ex.getMessage() + "\r\n" + ex.getStackTrace());

				return false;
			}

		} catch (Exception ex) {

			logger.error("语音验证码网络通信错误：" + ex.getMessage() + "\r\n" + ex.getStackTrace());

			return false;
		}
	}

	// 检查google验证码
	@RequestMapping("/checkGoogle")
	public void checkGoogle(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		String code = request.getParameter("code");
		// String secretKey = request.getParameter("secretKey");
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, "false");
			return;
		}
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		String secretKey = bqs.getF_user_safegoogle();
		if (GoogleAuthenticator.check_code(secretKey, Convert.toLong(code, 0), System.currentTimeMillis())) {
			toWeb(response, "true");
		} else {
			toWeb(response, "false");
		}
	}

	// 检查手机验证码
	@RequestMapping("/checkTelCode")
	public void checkTelCode(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		String tel = request.getParameter("tel");
		String zone = request.getParameter("zone");

		if (StringUtil.isEmpty(tel)) {
			
			BQMember user = super.findCurrUser(request, true);
			if(user==null){
				toWeb(response, "false");
				return;
			}
			
			BQMemberSafeSettings setting =  bqMemberSafeSettingsService.readById(user.getF_user_id());
			if (setting != null && !StringUtil.isEmpty(setting.getF_user_tel())) {
				tel = setting.getF_user_tel();
			}
		}

		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		String code = request.getParameter("code");
		BQTelCode code2 = (BQTelCode) session.getAttribute("telcode" + key);
		if (code2 == null) {
			toWeb(response, "false");
			return;
		}
		if (!tel.equals(code2.getF_user_tel())) {
			toWeb(response, "false");
			return;
		}
		if (!code.equals(code2.getF_auth_code())) {
			
			code2.setF_error_times(code2.getF_error_times() + 1);
			if (code2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				session.removeAttribute("telcode" + key);
			} else {
				session.setAttribute("telcode" + key, code2);
			}
			
			toWeb(response, "false");
			return;
		}
		toWeb(response, "true");
		return;
	}

	@RequestMapping("/invite")
	public ModelAndView invite(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();
		// 判断是否是经纪人
		/*BQMemberApply bqmApp = bqMemberApplyService.searchApplyByUserid(user.getF_user_id());
		if (bqmApp == null) {
			PagerModel pm = new PagerModel();
			model.addAttribute("pm", pm);
			model.addAttribute("applayerror", "认证否");
			mv.setViewName("jsp/user_invite");
			return mv;
		} else {
			if (bqmApp.getState() == 1) {
				if (bqmApp.getUser_type() == Contents.BQMEMBER_APPLY_BROKER || bqmApp.getUser_type() == Contents.BQMEMBER_APPLY_ADVANCEDBROKER) {
					model.addAttribute("applayerror", "");
				} else {
					PagerModel pm = new PagerModel();
					model.addAttribute("pm", pm);
					model.addAttribute("applayerror", "认证否");
					mv.setViewName("jsp/user_invite");

					return mv;
				}
			} else {
				PagerModel pm = new PagerModel();
				model.addAttribute("pm", pm);
				model.addAttribute("applayerror", "认证否");
				mv.setViewName("jsp/user_invite");

				return mv;
			}
		}*/

		long id = user.getF_user_id();
		try {
			String cid = Security.encryptToHex(String.valueOf(id), Security.des_registerkey);
			model.addAttribute("id", cid.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("name", user.getF_user_email());

		InviteVO invite = new InviteVO();
		long sumClick = bqMemberInviteAmountService.readSumClick(id);
		long sumSuccess = bqMemberInviteAmountService.readSumSuccess(id);
		BQMemberInviteAmount amount = bqMemberInviteAmountService.readTodayAmount(id, "");
		invite.setSumClick(sumClick);
		invite.setSumSuccess(sumSuccess);
		invite.setTodayClick(amount == null ? 0 : amount.getF_day_click());
		invite.setTodaySuccess(amount == null ? 0 : amount.getF_day_success());


		BQBfxFeeSetting bfxSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxSetting == null) {
			bfxSetting = bqBfxFeeSettingService.readOne();
			if (bfxSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxSetting);
			}
		}
		List<NContractPartition> parts = nContractPartitionService.readAll();		//获取所有分区
		List<Map<String,Object>> allRebatel = new ArrayList<>();
		List<Object> listAll = bqMemberInviteRebateLogService.readAllSumRebateByTime(id);	// 获取用户所有返佣数值
		List<Object> listWeek = bqMemberInviteRebateLogService.readAllSumRebateByTimeWeekOrMonth(id,TimeUtil.FromWeek(),TimeUtil.ToWeek());	// 获取用户周返佣数值
		List<Object> listMonth = bqMemberInviteRebateLogService.readAllSumRebateByTimeWeekOrMonth(id,TimeUtil.FromMonth(),TimeUtil.ToMonth());	// 获取用户月数值

		NContractPartition usdtPart = new NContractPartition();
		usdtPart.setCurrency_type_id(Contents.TRADE_CURRENCY_TYPE_ID);
		usdtPart.setPart("USDT");
		parts.add(usdtPart);

//		if (bfxSetting != null) {
//			NContractPartition bfxPart = new NContractPartition();
//			bfxPart.setCurrency_type_id(bfxSetting.getCoin_id());
//			bfxPart.setPart("BFX");
//			parts.add(bfxPart);
//		}

		//循环分区列表
		for (NContractPartition part : parts) {
			Map<String,Object> anRebate = new HashMap<>();
			anRebate.put("rebateName", part.getPart().toUpperCase());
			anRebate.put("weekRebatel", BigDecimal.ZERO);
			anRebate.put("monthRebatel", BigDecimal.ZERO);
			anRebate.put("totalRebatel", BigDecimal.ZERO);

			for (Object data : listAll) {
				Short currencyId = (Short) ((Object[])data)[0];
				if (currencyId == part.getCurrency_type_id()) {
					anRebate.put("totalRebatel", ((Object[])data)[1]);
					break;
				}
			}

			for (Object data : listWeek) {
				Short currencyId = (Short) ((Object[])data)[0];
				if (currencyId == part.getCurrency_type_id()) {
					anRebate.put("weekRebatel", ((Object[])data)[1]);
					break;
				}
			}

			for (Object data : listMonth) {
				Short currencyId = (Short) ((Object[])data)[0];
				if (currencyId == part.getCurrency_type_id()) {
					anRebate.put("monthRebatel", ((Object[])data)[1]);
					break;
				}
			}
			allRebatel.add(anRebate);
		}

		invite.setAllRebate(allRebatel);
		model.addAttribute("invite", invite);
		
		//PagerModel pm = bqMemberInviteLogService.findInviteLogByUserId(TimeUtil.FromWeek(), TimeUtil.ToWeek(), TimeUtil.FromMonth(), TimeUtil.ToMonth(), id);
		PagerModel pm = bqMemberInviteLogService.readPagerByUserId(user.getF_user_id());
		
		if (pm.getTotal() > 0) {
			@SuppressWarnings("unchecked")
			List<Object[]> list = (List<Object[]>) pm.getDatas();
			for (Object[] object : list) {
				long userId = Convert.toLong(object[0].toString(), 0);
				if (userId > 0) {
					BQMemberLevelBuyRebateReconcilia reconcilia = bqMemberLevelBuyRebateReconciliaService.readByUserId(userId);
					object[3] = reconcilia == null ? 0 : reconcilia.getTotal_bfx_lock().longValue();
				} else {
					object[3] = 0;
				}
			}
		}
		
		model.addAttribute("pm", pm);
		model.addAttribute("identity", user.getF_user_identity());

		mv.setViewName("jsp/user_invite");
		return mv;
	}
	
	@RequestMapping("/inviteImage")
	public void inviteImage(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		JSONObject obj = new JSONObject();
		if (user == null) {
			return;
		}
		
		String targetFilePath = getFilePath(request, user);
		
		try {
			obj.put("path", targetFilePath);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		toWeb(response, obj);
	}
	
	@RequestMapping("/downLoadInviteImage")
	public void downLoadInviteImage(HttpServletRequest request, HttpServletResponse response, String filePath) throws Exception{
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		filePath = "";
		
		if (StringUtil.isEmpty(filePath)) {
			filePath = getFilePath(request, user);
		}
		String storePath = Contents.BASE_UPLOAD_PATH + filePath;
		File file = new File(storePath);
		if (!file.exists()) {
			logger.info("资源文件不存在：" + file);
			toWeb(response, "no file");
			return;
		}
		
		OutputStream out = null;
		InputStream is = null;
				
        try {
        	String fileName =  System.currentTimeMillis() + storePath.substring(storePath.lastIndexOf("."));
    		
        	fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
    		response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);// 设定输出文件头   
            response.setContentType("text/x-plain");// 定义输出类型 
            response.setCharacterEncoding("UTF-8");
            
            out = response.getOutputStream();
            is = new FileInputStream(file);
            
            byte[] cache = new byte[4 * 1024];
            for (int offset = is.read(cache); offset != -1; offset = is.read(cache)) {
            	out.write(cache, 0, offset);
            }
            
            out.flush();
            
        } catch (Exception e) {
            logger.error("下载资源文件异常，信息：", e);
            response.reset();
            toWeb(response, "对不起，程序异常！");
        } finally {
        	if (null != out) {
        		try {
        			out.close();
        		} catch (Exception e) {
        			logger.error("下载资源文件关闭输出流异常，信息：", e);
        		}
        	}
        	if (null != is) {
        		try {
        			is.close();
        		} catch (Exception e) {
        			logger.error("下载资源文件关闭输出流异常，信息：", e);
        		}
        	}
        }
		
		return;
	}

	private String getFilePath (HttpServletRequest request, BQMember user) {
		if (user == null) return null;
		String cid = "";
		String targetFilePath = "";
		try {
			cid = Security.encryptToHex(String.valueOf(user.getF_user_id()), Security.des_registerkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cid = cid.toLowerCase();
		String baseStorePath = Contents.BASE_UPLOAD_PATH;
		targetFilePath = "/file-upload/invite/" + cid + ".jpg";
		String iconBasePath = baseStorePath + "/file-upload/bitmatrix/s" + cid + ".jpg";
		String targetPath = baseStorePath + targetFilePath;
		File bitMatrixFile = new File(iconBasePath);
		File targetFile = new File(targetPath);
		File parentFile = bitMatrixFile.getParentFile();
		File targetParentFile = targetFile.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!targetParentFile.exists()) {
			targetParentFile.mkdirs();
		}
		
		String basePath = super.getRequestRoot(request);
		String format = "jpg";
		if (!targetFile.exists()) {
			try {
				bitMatrixFile.createNewFile();
				targetFile.createNewFile();
				
				String srcImagePath = request.getSession().getServletContext().getRealPath("common/images/bfx-invite.png");
				String inviteUrl = basePath + "home/s" + cid + ".do";
				int width = 202, height = 202;
				
				Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
				// 内容所使用编码
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				hints.put(EncodeHintType.MARGIN, 0);
				
				BitMatrix bitMatrix = new MultiFormatWriter().encode(inviteUrl, BarcodeFormat.QR_CODE, width, height, hints);
				MatrixToImageWriter.writeToFile(bitMatrix, format, bitMatrixFile);
				Integer degree = 0;
				Integer left = 571;
				Integer top = 1205;
				Float clarity = 1F;
				boolean success = false;
				success = ImageUtil.waterMarkImageByIcon(iconBasePath, srcImagePath, targetPath, degree, left, top, clarity);
				String fileName = success ? targetPath : "";
				BufferedImage image = ImageIO.read(new FileInputStream(fileName));
				
				ImageIO.write(image, format, targetFile);
			} catch (WriterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return targetFilePath;
	}
	
	@RequestMapping("/inviteManagement")
	public ModelAndView inviteManagement(HttpServletRequest request, Model model, String userName, String inviter) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		long uId = user.getF_user_id();
		String email = user.getF_user_email();
		long levelId = user.getF_user_level_id();
		if (user.getOpen_invite_management() == 0) {
			model.addAttribute("notManagement", true);
			model.addAttribute("pm", new PagerModel());
			ModelAndView mv = new ModelAndView();
			mv.setViewName("jsp/user_inviteManagement");
			return mv;
		}

		if (!StringUtil.isEmpty(userName)) {
			BQMember inviteMember = bqMemberService.findUserByEmail(userName);
			if (inviteMember != null) {
				uId = inviteMember.getF_user_id();
				email = userName;
				levelId = inviteMember.getF_user_level_id();
			}
			if (StringUtil.isEmpty(inviter)) {
				long inviterUserId = bqMemberInviteLogService.findInviterByUserId(uId);
				BQMember inviteUser = bqMemberService.readUserById(inviterUserId);
				if (inviteUser != null) {
					inviter = inviteUser.getF_user_email();
				}
			}
			model.addAttribute("inviter", inviter);
		}

		
		String level = bqMemberLevelService.findByLevelId(levelId).getLevel_name();
		Long inviteNums = spyMemcachedClient.get(BTCQMemecacheKeys.INVITE_COUNT + uId);
		if (inviteNums == null) {
			inviteNums = bqMemberInviteLogService.readCountByUserId(uId);
			spyMemcachedClient.set(BTCQMemecacheKeys.INVITE_COUNT + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, inviteNums);
		}
		Long allInviteNums = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_INVITE_COUNT + uId);
		if (allInviteNums == null) {
			allInviteNums = bqMemberInviteLogService.readAllCount(uId);
			spyMemcachedClient.set(BTCQMemecacheKeys.ALL_INVITE_COUNT + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, allInviteNums);
		}

		model.addAttribute("email", email);
		model.addAttribute("level", level);
		model.addAttribute("inviteNums", inviteNums);
		model.addAttribute("allInviteNums", allInviteNums);
		model.addAttribute("pageN", "center/inviteManagement.do");

		PagerModel pm = new PagerModel();
		int offset = SystemContext.getOffset();
		List<UserInviteMemberVo> allInviteVos = null;
		if (offset > 0) {
			allInviteVos = spyMemcachedClient.get(BTCQMemecacheKeys.USER_ALL_INVITE_VOS + uId);
		}

		if (allInviteVos == null) {
			allInviteVos = new ArrayList<UserInviteMemberVo>();
			List<Object[]> list = bqMemberInviteLogService.readAllByUserId(uId);
			BQBfxFeeSetting bfxSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
			if (bfxSetting == null) {
				bfxSetting = bqBfxFeeSettingService.readOne();
				if (bfxSetting != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND,
							bfxSetting);
				}
			}
			if (list.size() > 0) {
				for (Object[] object : list) {
					UserInviteMemberVo vo = new UserInviteMemberVo();
					long useId = (Integer) object[0];
					vo.setUserId(object[0].toString());
					vo.setUserName(object[1].toString());
					vo.setLevel(object[2].toString());
					vo.setInviteNums(bqMemberInviteLogService.readCountByUserId(useId));
					vo.setAllInviteNums(bqMemberInviteLogService.readAllCount(useId));
					BigDecimal usdtRebate = bqMemberInviteRebateLogService.readSumRebateByUserAndSub(uId, useId,
							Contents.TRADE_CURRENCY_TYPE_ID);
					BigDecimal bfxRebate = bqMemberInviteRebateLogService.readSumRebateByUserAndSub(uId, useId,
							bfxSetting.getCoin_id());
					vo.setBfxRebate(bfxRebate.doubleValue());
					vo.setUsdtRebate(usdtRebate.doubleValue());

					allInviteVos.add(vo);
				}
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.USER_ALL_INVITE_VOS + uId, BTCQMemecacheKeys.TIMES.ONE_HOUR, allInviteVos);
		}

		pm.setTotal(allInviteVos.size());

		List<UserInviteMemberVo> datas = new ArrayList<UserInviteMemberVo>();
		int begin = SystemContext.getOffset();
		int size = SystemContext.getPageSize();

		for (int i = begin; i < begin + size; i++) {
			if (allInviteVos.size() <= i) {
				break;
			}
			datas.add(allInviteVos.get(i));
		}
		pm.setDatas(datas);
		model.addAttribute("pm", pm);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_inviteManagement");
		return mv;
	}

	@RequestMapping("/rebateMore")
	public void rebateMore(HttpServletRequest request, HttpServletResponse response, String userName)
			throws JSONException {
		JSONObject obj = new JSONObject();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			obj.put("code", "error");
			obj.put("msg", "no login");
			toWeb(response, obj);
			return;
		}
		long userId = user.getF_user_id();
		long subUserId = 0;
		if (!StringUtil.isEmpty(userName)) {
			BQMember inviteMember = bqMemberService.readUserByEmail(userName);
			if (inviteMember != null) {
				subUserId = inviteMember.getF_user_id();
			}
		}

		if (subUserId == 0) {
			obj.put("code", "error");
			obj.put("msg", "userName error");
			toWeb(response, obj);
			return;
		}

		JSONArray array = new JSONArray();
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		for (BQCurrencyType coin : coins) {
			InviteCurrencyRebateVo vo = new InviteCurrencyRebateVo();
			int coinId = coin.getType_id();
			BigDecimal weekRebate = bqMemberInviteRebateLogService.readSumRebateByTimeAndSub(TimeUtil.FromWeek(),
					TimeUtil.ToWeek(), userId, subUserId, coinId);
			BigDecimal monthRebate = bqMemberInviteRebateLogService.readSumRebateByTimeAndSub(TimeUtil.FromMonth(),
					TimeUtil.ToMonth(), userId, subUserId, coinId);
			BigDecimal totalRebate = bqMemberInviteRebateLogService.readSumRebateByUserAndSub(userId, subUserId,
					coinId);
			vo.setCurrenyId(coinId);
			vo.setCurrenyName(coin.getType_name());
			vo.setWeekRebate(weekRebate.doubleValue());
			vo.setMonthRebate(monthRebate.doubleValue());
			vo.setTotalRebate(totalRebate.doubleValue());
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(vo)));
		}

		obj.put("data", array);
		obj.put("code", "success");
		toWeb(response, obj);
	}
	
	/**
	 * 二级用户
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/inviteMember")
	public ModelAndView inviteMember(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();

		String id = request.getParameter("inviteeId");
		long inviteeId = ConvertUtil.toLong(id, 0l);
		if (inviteeId > 0) {
			BQMember inviteUser = bqMemberService.findUserById(inviteeId);
			if (inviteUser == null) {
				return new ModelAndView("redirect:/center/invite.do");
			}
		}
		long inviterId = bqMemberInviteLogService.findInviterByUserId(inviteeId);
		if (inviterId != user.getF_user_id()) {
			return new ModelAndView("redirect:/center/invite.do");
		}
		PagerModel pm = bqMemberInviteLogService.readPagerByUserId(inviteeId);
		if (pm.getTotal() > 0) {
			@SuppressWarnings("unchecked")
			List<Object[]> list = (List<Object[]>) pm.getDatas();
			for (Object[] object : list) {
				long userId = Convert.toLong(object[0].toString(), 0);
				if (userId > 0) {
					BQMemberLevelBuyRebateReconcilia reconcilia = bqMemberLevelBuyRebateReconciliaService.readByUserId(userId);
					object[3] = reconcilia == null ? 0 : reconcilia.getTotal_bfx_lock().longValue();
				} else {
					object[3] = 0;
				}
			}
		}
		
		
		model.addAttribute("pm", pm);
		model.addAttribute("identity", user.getF_user_identity());
		model.addAttribute("inviteeId", inviteeId);

		mv.setViewName("jsp/user_inviteMember");
		return mv;
	}
	
	/**
	 * 三级用户
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/inviteThirdMember")
	public ModelAndView inviteThirdMember(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();

		String id = request.getParameter("inviteeId");
		long inviteeId = ConvertUtil.toLong(id, 0l);
		BQMember inviteUser = new BQMember();
		if (inviteeId > 0) {
			inviteUser = bqMemberService.findUserById(inviteeId);
			if (inviteUser == null) {
				return new ModelAndView("redirect:/center/invite.do");
			}
		}
		long inviterId = bqMemberInviteLogService.findInviterByUserId(inviteeId);
		if (inviterId > 0l) {
			long inviterId2 = bqMemberInviteLogService.findInviterByUserId(inviterId);
			if (inviterId2 != user.getF_user_id()) {
				return new ModelAndView("redirect:/center/invite.do");
			}
		}
		PagerModel pm = bqMemberInviteLogService.readPagerByUserId(inviteeId);
		if (pm.getTotal() > 0) {
			@SuppressWarnings("unchecked")
			List<Object[]> list = (List<Object[]>) pm.getDatas();
			for (Object[] object : list) {
				long userId = Convert.toLong(object[0].toString(), 0);
				if (userId > 0) {
					BQMemberLevelBuyRebateReconcilia reconcilia = bqMemberLevelBuyRebateReconciliaService.readByUserId(userId);
					object[3] = reconcilia == null ? 0 : reconcilia.getTotal_bfx_lock().longValue();
				} else {
					object[3] = 0;
				}
			}
		}
		
		model.addAttribute("pm", pm);
		model.addAttribute("identity", user.getF_user_identity());
		model.addAttribute("inviteeId", inviteeId);

		mv.setViewName("jsp/user_inviteThirdMember");
		return mv;
	}
	
	/**
	 * 用户下级客户仓位信息
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/positionInformation")
	public ModelAndView positionInformation(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();
		
		if (user.getF_user_identity() != 1) {
			return new ModelAndView("redirect:/center/invite.do");
		}
		String id = request.getParameter("inviteeId");
		long inviteeId = ConvertUtil.toLong(id, 0l);
		BQMember inviteUser = new BQMember();
		if (inviteeId > 0) {
			inviteUser = bqMemberService.findUserById(inviteeId);
			if (inviteUser == null) {
				return new ModelAndView("redirect:/center/invite.do");
			}
		}
		long inviterId = bqMemberInviteLogService.findInviterByUserId(inviteeId);
		if (inviterId > 0l) {
			long inviterId2 = bqMemberInviteLogService.findInviterByUserId(inviterId);
			long inviterId3 = bqMemberInviteLogService.findInviterByUserId(inviterId2);
			if (inviterId != user.getF_user_id() && inviterId2 != user.getF_user_id() && inviterId3 != user.getF_user_id()) {
				return new ModelAndView("redirect:/center/invite.do");
			}
		}
		model.addAttribute("email", inviteUser.getF_user_email());
		model.addAttribute("inviteeId", inviteeId);
		
		List<BQTransaction> transactionList = super.findCurrTrans();
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("code");

		if (transactionList == null || transactionList.size() <= 0) {
			model.addAttribute("tradeLogList", new PagerModel());
		}

		BQTransaction currentTransaction = null;
		if (StringUtil.isEmpty(bqCode)) {
			currentTransaction = transactionList.get(0);
			bqCode = currentTransaction.getTransaction_code();
		} else {
			currentTransaction = bqTransactionService.readTransByCode(bqCode);
		}
		model.addAttribute("currentTransaction", currentTransaction);

		String type = request.getParameter("type");
		if (StringUtil.isEmpty(type)) {
			type = "hold";
		}
		model.addAttribute("type", type);
		if ("pcrecord".equals(type)) {
//			PagerModel tradeLogList = tradeLogService.readByPCTrade(bqCode, inviteeId);
			PagerModel tradeLogList = new PagerModel();
			
			model.addAttribute("tradeLogList", tradeLogList);
			model.addAttribute("pageN", "center/positionInformation.do");
			mv.setViewName("jsp/user_positionInfo");
			return mv;
		}
		
		BQMemberLevel userlevel = bqMemberLevelService.readByUserId(inviteeId);
		BigDecimal eatRate = userlevel.getEat_commission_rate();
		
		BQTransactionType transactionType = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
		int digits = com.btc100.btc_q.util.Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
		int amountDigits = com.btc100.btc_q.util.Utils.getAmountDigtis(currentTransaction.getTransaction_code());
		
		// 持仓信息
		List<BQMemberTransaction> memberTransaction = bqMemberTransactionService.readTransactionByUserId(bqCode, inviteeId);
		
		// 获取标记价格
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = pendingDelegationService.getBuyTop5(currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = pendingDelegationService.getSellTop5(currentTransaction.getTransaction_code());
		}
		
		BigDecimal buy1Price = BigDecimal.ZERO;
		BigDecimal sell1Price = BigDecimal.ZERO;
		if (buytop5List != null) {
			for (Top5DelegationVO vo : buytop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(amountDigits, RoundingMode.DOWN));
			}
			
			if (buytop5List.size() > 0)
				buy1Price = buytop5List.get(0).getPrice();
		}
		
		if (selltop5List != null) {
			for (Top5DelegationVO vo : selltop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(amountDigits, RoundingMode.DOWN));
			}
			
			if (selltop5List.size() > 0) {
				sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
			}
		}
		
		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();
		if (memberTransaction != null && memberTransaction.size() > 0) {
			for (BQMemberTransaction transaction : memberTransaction) {
				int buyStopLoss = bqStopLossService.readHasStopLoss(transaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readHandleData(transaction, transaction.getTrade_type_id(), markPrice, eatRate, buyStopLoss, 
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
				if (transVo != null) {
					listTransVos.add(transVo);
				}
			}
		}
		model.addAttribute("memberTransaction", listTransVos);
		
		mv.setViewName("jsp/user_positionInfo");
		return mv;
	}
	
	@RequestMapping("/myCertificate")
	public ModelAndView myCertificates(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();
		PagerModel pm = bqMemberCertificateService.readPageByUserId(user.getF_user_id());
		
		if (pm.getTotal() > 0) {
			BQMemberValidateInfo validateInfo = bqMemberValidateInfoService.readByUserId(user.getF_user_id());
			if (validateInfo == null) {
				model.addAttribute("noRealName", true);
				mv.setViewName("jsp/user_realName");
				return mv;
			}
			model.addAttribute("pm", pm);
			model.addAttribute("pageN", "center/myCertificate.do");
			mv.setViewName("jsp/user_myCertificates");
			return mv;
		}
		
		model.addAttribute("noCertificate", true);
		mv.setViewName("jsp/user_realName");
		return mv;
	}
	
	@RequestMapping("/getRealName")
	public ModelAndView getRealName(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		BQMemberValidateInfo info = bqMemberValidateInfoService.readByUserId(user.getF_user_id());
		if (info != null) {
			return new ModelAndView("redirect:/center/myCertificate.do");
		}
		String realName = request.getParameter("realName");
		String realName2 = request.getParameter("realName2");
		ModelAndView mv = new ModelAndView();
		if (StringUtil.isEmpty(realName) || StringUtil.isEmpty(realName2)) {
			model.addAttribute("noRealName", true);
			model.addAttribute("error", SpringContext.getText("msg.userRealName.null", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_realName");
			return mv;
		}
		
		if (!realName.equals(realName2)) {
			model.addAttribute("noRealName", true);
			model.addAttribute("error", SpringContext.getText("msg.userRealName.notEq", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_realName");
			return mv;
		}
		
		if (!StringUtil.validateString(realName) || !StringUtil.validateString(realName2)) {
			model.addAttribute("noRealName", true);
			model.addAttribute("error", SpringContext.getText("msg.common.request.illegalCharacters", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_realName");
			return mv;
		}
		
		BQMemberValidateInfo validateInfo = new BQMemberValidateInfo();
		
		validateInfo.setReal_name(realName);
		validateInfo.setUser_id(user.getF_user_id());
		validateInfo.setValidate_time(System.currentTimeMillis() / 1000);
		bqMemberValidateInfoService.save(validateInfo);
		
		return new ModelAndView("redirect:/center/myCertificate.do");
	}
	
	@RequestMapping("/certificateDetail")
	public ModelAndView certificateDetail(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		ModelAndView mv = new ModelAndView();
		BQMemberValidateInfo validateInfo = bqMemberValidateInfoService.readByUserId(user.getF_user_id());
		if (validateInfo == null) {
			model.addAttribute("noRealName", true);
			mv.setViewName("jsp/user_realName");
			return mv;
		}
		String id = request.getParameter("id");
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		
		model.addAttribute("validateInfo", validateInfo);
		model.addAttribute("certificate", certificate);
		mv.setViewName("jsp/user_certificateDetail");
		return mv;
	}
	
	@RequestMapping("/getCertificateImage")
	public void getCertificateImage(HttpServletResponse response, String id) {
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		
		if (StringUtil.isEmpty(certificate)) {
			toWeb(response, "证书不存在");
			return;
		}
		JSONObject obj = new JSONObject();
		String storeFolderPath = "file-upload/honourimage/";
		String fileName = "c" + cid + ".png";
		String filePath = storeFolderPath + fileName;
		
		if (!OSSFileUtil.isFileExist(filePath)) {
			filePath = "";
		} else {
			filePath = OSSFileUtil.getFileUrl(filePath);
		}
		
		try {
			obj.put("path", filePath);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		toWeb(response, obj);
		return;
	}
	
	@RequestMapping("/cvsToImage")
	public void cvsToImage(HttpServletResponse response, String base64ImgData, String id) {
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		
		if (StringUtil.isEmpty(certificate)) {
			toWeb(response, "证书不存在");
			return;
		}
		JSONObject obj = new JSONObject();
		String storeFolderPath = "file-upload/honourimage/";
		String fileName = "c" + cid + ".png";
		String filePath = storeFolderPath + fileName;
		
		if (!OSSFileUtil.isFileExist(filePath)) {
			try {
				BASE64Decoder d = new BASE64Decoder();
				// 去掉头部声明
				base64ImgData = base64ImgData.substring(base64ImgData.indexOf(",") + 1);
				
				byte[] f = d.decodeBuffer(base64ImgData);
				for (int i = 0; i < f.length; ++i) {
					if (f[i] < 0) {
						f[i] += 256;
					}
				}
				
				if (!ImageUtil.isValidFile("jpg", f) && !ImageUtil.isValidFile("png", f)) {
					obj.put("path", "");
					toWeb(response, obj);
					return;
				}
				
				filePath = OSSFileUtil.uploadFile(f, filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			obj.put("path", filePath);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		toWeb(response, obj);
		return;
	}
	
	// 交易api
	@RequestMapping("/tradeapi")
	public ModelAndView tradeapi(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.tradeapi.request.settingSecurity", Contents.Local.LOCALE.get()));
				model.addAttribute("err", 1);
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
			}
		}

		// 判断api是否开通
		BQMemberPartner bqp = bqMemberPartnerService.readApiById(user.getF_user_id());
		if (bqp == null) {
			model.addAttribute("open", 1);
		} else {
			model.addAttribute("show", 1);
		}
		mv.setViewName("jsp/user_tradeapi");
		return mv;
	}

	// 获取api
	@RequestMapping("/tradegotapi")
	public ModelAndView tradegotapi(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		// 判断安全验证方式
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss == null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/tradeapi.do", model);
		}
		int safetype = bqss.getF_user_safetype();
		if (safetype == 1) {
			model.addAttribute("tel", 1);
			model.addAttribute("mobile", bqss.getF_user_tel());
		} else if (safetype == 2) {
			model.addAttribute("google", 2);

			model.addAttribute("secrec", bqss.getF_user_safegoogle());
		} else if (safetype == 3) {
			model.addAttribute("tel", 1);
			model.addAttribute("google", 2);
			model.addAttribute("mobile", bqss.getF_user_tel());
			model.addAttribute("secrec", bqss.getF_user_safegoogle());
		} else {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/tradeapi.do", model);
		}
		// 判断开头api否
		BQMemberPartner bqp = bqMemberPartnerService.readApiById(user.getF_user_id());
		if (bqp == null) {
			model.addAttribute("open", 1);
		} else {
			model.addAttribute("show", 1);
		}
		model.addAttribute("but", 1);
		return new ModelAndView("jsp/user_tradeapi");
	}

	@RequestMapping("/openapi")
	public ModelAndView openapi(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		// 判断安全验证方式
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs == null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/tradeapi.do", model);
		}
		// 判断验证码
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");

		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			if (telCode == null || telCode.isEmpty()) {
				model.addAttribute("error", "error4");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}

			BQTelCode tecode = (BQTelCode) session.getAttribute("telcode" + bqs.getF_user_tel());
			if (tecode == null) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + bqs.getF_user_tel());
				} else {
					session.setAttribute("telcode" + bqs.getF_user_tel(), tecode);
				}
				
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}

		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) {
			if (googleCode == null || googleCode.isEmpty()) {
				model.addAttribute("error", "error5");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/tradeapi.do", model);
			}

		}
		
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			session.removeAttribute("telcode" + bqs.getF_user_tel());
		}
		
		// 从数据库中取值
		BQMemberPartner bqp = bqMemberPartnerService.readApiById(user.getF_user_id());
		if (bqp == null) {
			// 存入数据库
			String spartner = null;
			String secretKey = null;
			try {
				spartner = APIEnDecrype.encryptDES(Long.toString(user.getF_user_id() * 11 + 5));
				secretKey = MD5Service.encryptString(user.getF_user_email() + System.currentTimeMillis() + RandomUtil.getRandomNormalString(5));
			} catch (Exception e) {
				e.printStackTrace();
			}

			bqp = new BQMemberPartner();
			bqp.setF_user_id(user.getF_user_id());
			bqp.setPartner(spartner);
			bqp.setSecret_key(secretKey);
			bqp.setDateline(DateUtil.DateToUnix(new Date()));
			bqMemberPartnerService.save(bqp);
			model.addAttribute("show", 1);
			// model.addAttribute("openapi", 1);
			return new ModelAndView("jsp/user_tradeapi");
		} else {
			String spartner = bqp.getPartner();
			String secretKey = bqp.getSecret_key();
			model.addAttribute("spartner", spartner);
			model.addAttribute("secretKey", secretKey);
			model.addAttribute("show", 1);
			model.addAttribute("openapi", 1);
			return new ModelAndView("jsp/user_tradeapi");
		}
	}

	@RequestMapping("/showapi")
	public ModelAndView showapi(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		// 从数据库中取值
		BQMemberPartner bqp = bqMemberPartnerService.readApiById(user.getF_user_id());
		String spartner = bqp.getPartner();
		String secretKey = bqp.getSecret_key();
		model.addAttribute("spartner", spartner);
		model.addAttribute("secretKey", secretKey);
		model.addAttribute("show", 1);
		model.addAttribute("openapi", 1);
		mv.setViewName("jsp/user_tradeapi");
		return mv;
	}

	@RequestMapping("/viplist")
	public ModelAndView viplist(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.levelBuyAmountError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.login.request.accountIsFrozen", Contents.Local.LOCALE.get()));
			}  else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.cantBuy", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.insufficientBalance", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.tradePwdError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error6")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.cantBuyLowLevel", Contents.Local.LOCALE.get()));
			} else if (num.equals("error7")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.cantBuyLowLevelAmountNegative", Contents.Local.LOCALE.get()));
			} else if (num.equals("error8")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error9")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error10")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.tradePwdError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error11")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.bqBfxFeeSettingIsNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error12")) {
				model.addAttribute("error", SpringContext.getText("msg.pg.userCenter.gobind", Contents.Local.LOCALE.get()));
			} else if (num.equals("error13")) {
				model.addAttribute("error", SpringContext.getText("msg.memberLevelBuyAmount.omsCantBuy", Contents.Local.LOCALE.get()));
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.memberLevelBuyAmount.buySuccess", Contents.Local.LOCALE.get()));
			}
		}

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		bqMemberService.checkVipLevel(user);

//		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
//		model.addAttribute("balance", bqa.getBtc());
//		long credits = user.getF_user_credits();// 积分

		BQMemberLevel bql = bqMemberLevelService.readById(user.getF_user_level_id());
		String name = bql.getLevel_name();// 级别名称
//		long nameID = bql.getLevel_id();// 级别名称ID
//		long nameIDMax = bqMemberLevelService.readMaxLevel().getLevel_id();
//		long nameIDMaxForLevelBuyAmount = bqMemberLevelService.readMaxLevelForLevelBuyAmount().getLevel_id();

//		model.addAttribute("credits", credits);
		model.addAttribute("name", name);
//		model.addAttribute("nameID", nameID);
//		model.addAttribute("nameIDMax", nameIDMaxForLevelBuyAmount);

//		BQBfxFeeSetting setting = bqBfxFeeSettingService.readOne();
//		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(setting.getCoin_id());
//		model.addAttribute("coinName", currencyType == null ? "" : currencyType.getType_name());
//		BQMemberLevelBuySetting buySetting = bqMemberLevelBuySettingService.readById();
//		if (buySetting == null) buySetting = new BQMemberLevelBuySetting();

//		buySetting.getPay_coin_name();
//		model.addAttribute("payCoinName", buySetting.getPay_coin_name());
//		model.addAttribute("exchangeRate", buySetting.getExchange_rate());


//		if (bql.getLevel_id() < nameIDMax) {
//			BQMemberLevel bql2 = bqMemberLevelService.findNextLevelById(user.getF_user_level_id());
//			String name2 = bql2.getLevel_name();// 级别名称
//			model.addAttribute("name2", name2);
//			model.addAttribute("credits2", bql2.getLevel_credits() - credits);
//
//			BQMemberLevel bfxLevel = super.getLevelByUserBfx(user);
//			if (bfxLevel.getLevel_id() > bql.getLevel_id()) {
//				long dayTime = TimeUtil.getTimesmorning(System.currentTimeMillis() - 24 * 3600 * 1000);
//				BQBfxMinHold minHold = bqBfxMinHoldService.readBfxMinHoldByUserAndTime(user.getF_user_id(), dayTime);
//				model.addAttribute("bfxBalance", minHold.getMin_hold());
//				model.addAttribute("bfxLevel", bfxLevel.getLevel_name());
//			}
//		}

//		List<BQCommonCreditLog> list = bqCommonCreditLogService.readByUserAndTime(user.getF_user_id(), -1, "", 0, 0, 10);// 等到积分记录
//		model.addAttribute("list", list);
//		List<BQMemberLevel> levelRules = bqMemberLevelService.findAll();// 等级规则制度
//		model.addAttribute("levelRles", levelRules);
//
//		Gson gson = new Gson();
//		model.addAttribute("levelRlesJson", gson.toJson(levelRules));
//
//		// 判断是否安全验证
//		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
//		if (bqs == null) {
//			model.addAttribute("tel", 0);
//			model.addAttribute("google", 0);
//			model.addAttribute("applayerror", true);
//		} else {
//			if (bqs.getF_user_safetype() == 0) {
//				model.addAttribute("tel", 0);
//				model.addAttribute("google", 0);
//				model.addAttribute("applayerror", true);
//			} else if (bqs.getF_user_safetype() == 1) {
//				if (StringUtil.isEmpty(bqs.getF_user_tel())) {
//					model.addAttribute("tel", 0);
//					model.addAttribute("mobile", bqs.getF_user_tel());
//				} else {
//					model.addAttribute("tel", 1);
//					model.addAttribute("mobile", bqs.getF_user_tel());
//				}
//				model.addAttribute("google", 0);
//			} else if (bqs.getF_user_safetype() == 2) {
//				if (StringUtil.isEmpty(bqs.getF_user_tel())) {
//					model.addAttribute("tel", 0);
//				} else {
//					model.addAttribute("tel", 1);
//					model.addAttribute("mobile", bqs.getF_user_tel());
//				}
//
//				model.addAttribute("google", 2);
//				model.addAttribute("secretKey", bqs.getF_user_safegoogle());
//
//			} else if (bqs.getF_user_safetype() == 3) {
//				model.addAttribute("tel", 1);
//				model.addAttribute("mobile", bqs.getF_user_tel());
//				model.addAttribute("google", 2);
//				model.addAttribute("secretKey", bqs.getF_user_safegoogle());
//			}  else {
//				model.addAttribute("tel", 0);
//				model.addAttribute("google", 0);
//				model.addAttribute("applayerror", true);
//			}
//		}
//		model.addAttribute("user_safetype", bqs.getF_user_safetype());
//
//		// 判断充值
//		long size = bqMemberRechargeService.readRechargeCount(user.getF_user_id());
//		if (size > 0) {
//			model.addAttribute("recharge", 1);
//		} else {
//			model.addAttribute("recharge", 0);
//		}

		// 判断交易
		/*
		int firstTrade = bqCommonCreditLogService.readCount(user.getF_user_id(), Contents.CREDIT_TYPE_FIRST_TRADE);
		if (firstTrade > 0) {
			model.addAttribute("trade", 1);
		} else {
			
			boolean isTrade = false;
			List<BQTransaction> transactionList = spyMemcachedClient.get("transactionList_key");
			if (transactionList == null) {
				transactionList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
			for (BQTransaction bqTra : transactionList) {
				List<TradeLog> listtrade = tradeLogService.readAllByUserIdTopN(bqTra.getTransaction_code(), Long.valueOf(user.getF_user_id()).intValue(), 1);
				if (listtrade == null) {
					model.addAttribute("trade", 0);
				} else {
					if (listtrade.size() > 0) {
						isTrade = true;
						break;
					} else {
						model.addAttribute("trade", 0);
					}
				}
			}
			
			if (isTrade && user.getF_user_oms() == 0) {
				BQCommonCreditLog creditLog = new BQCommonCreditLog();
				creditLog.setF_user_id(user.getF_user_id());
				creditLog.setCredit_type_id(Contents.CREDIT_TYPE_FIRST_TRADE);
				creditLog.setCredits(DATA_CreditNumber.CREDIT_ONE_FIRST_TRADE);
				creditLog.setTitle("首次交易");
				creditLog.setDateline(bqCommonCreditLogService.getTime());
				
				BQMemberLevel level = bqMemberLevelService.readByUserId(creditLog.getF_user_id());
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(creditLog, level.getState() == 1, 1);
				
				model.addAttribute("credits", credits + DATA_CreditNumber.CREDIT_ONE_FIRST_TRADE);
				model.addAttribute("trade", 1);
			} else {
				model.addAttribute("trade", 0);
			}
		}
		*/
		
		mv.setViewName("jsp/user_vip");
		return mv;
	}

	// 积分分页
	@RequestMapping("/vipRecordList")
	public ModelAndView vipRecordList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		// BQMember user = bqMemberService.findUserById(103);
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		PagerModel list = bqCommonCreditLogService.readVipRecords(user.getF_user_id());
		model.addAttribute("pm", list);
		mv.setViewName("jsp/user_vipRecordList");
		return mv;
	}

	// 购买VIP等级记录分页
	@RequestMapping("/vipTradeBuyRecordList")
	public ModelAndView vipTradeBuyRecordList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		// BQMember user = bqMemberService.findUserById(103);
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		BQMemberLevelBuySetting setting = bqMemberLevelBuySettingService.readById();
		model.addAttribute("coinName", setting.getPay_coin_name());
		
		PagerModel list = bqMemberLevelBuyAmountLogService.readLevelBuyAmountLogPager(user.getF_user_id());
		model.addAttribute("pm", list);
		mv.setViewName("jsp/user_vipTradeBuyRecordList");
		return mv;
	}

	// 购买VIP等级 - 购买积分
	@RequestMapping("/vipTradeBuy")
	public ModelAndView vipTradeBuy(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		HttpSession session = request.getSession();
		
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		if (user.getF_user_oms() != 0) {
			model.addAttribute("error", "error13");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				model.addAttribute("error", "error8");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
			BQTelCode tecode = (BQTelCode) session.getAttribute("telcode" + bqs.getF_user_tel());
			if (tecode == null) {
				model.addAttribute("error", "error8");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + bqs.getF_user_tel());
				} else {
					session.setAttribute("telcode" + bqs.getF_user_tel(), tecode);
				}
				
				model.addAttribute("error", "error8");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				model.addAttribute("error", "error9");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				model.addAttribute("error", "error9");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				model.addAttribute("error", "error9");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
		}

		if (bqs.getF_user_safetype() != 1 && bqs.getF_user_safetype() != 2 && bqs.getF_user_safetype() != 3) {
			model.addAttribute("error", "error12");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		long select_level_id = ConvertUtil.toLong(request.getParameter("select_level_id"), (long) 0);
		
		if (select_level_id <= 0) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		if (user.getF_user_state() == 2) {
			model.addAttribute("error", "error2");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		
		//检查用户上传上来想要购买的等级，跟目前已有等级是否符合标准，最高等级不能再购买低等级
		
		BQMemberLevel bql = bqMemberLevelService.readById(user.getF_user_level_id());
		long nameID = bql.getLevel_id();// 用户级别名称ID
		long nameIDMax = bqMemberLevelService.readMaxLevelForLevelBuyAmount().getLevel_id();
		
		if (nameID >= nameIDMax) {
			model.addAttribute("error", "error3");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		if (select_level_id <= nameID) {
			model.addAttribute("error", "error6");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		

		//检查用户已有资金是否足够购买
		BQMemberLevel levelBuyAmount = bqMemberLevelService.findLevelBuyAmountById(nameID);
		BigDecimal amount = levelBuyAmount.getLevel_buy_amount();
		
		BQMemberLevel levelBuyAmountSelect = bqMemberLevelService.findLevelBuyAmountById(select_level_id);
		BigDecimal amountSelect = levelBuyAmountSelect.getLevel_buy_amount();
		
		BigDecimal total = amountSelect.subtract(amount);
		BigDecimal lockBfx = total;
		
		BQMemberLevelBuySetting paySetting = bqMemberLevelBuySettingService.readById();
		if (paySetting == null) paySetting = new BQMemberLevelBuySetting();
		
		total = total.multiply(paySetting.getExchange_rate());
		if (total.compareTo(BigDecimal.ZERO) <= 0) {
			model.addAttribute("error", "error7");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), paySetting.getPay_coin_id());
		BigDecimal btc = bqa.getBtc();
		
		if (btc.compareTo(total) == -1) {
			model.addAttribute("error", "error4");
			return new ModelAndView("redirect:/center/viplist.do", model);
		}
		
		BigDecimal bqsu = btc.subtract(total);
		if (bqsu.compareTo(BigDecimal.ZERO) >= 0) {
//			String title = SpringContext.getText("msg.memberLevelBuyAmount.title1", Contents.Local.LOCALE.get()) + bql.getLevel_name() + SpringContext.getText("msg.memberLevelBuyAmount.title2", Contents.Local.LOCALE.get()) + levelBuyAmountSelect.getLevel_name() + SpringContext.getText("msg.memberLevelBuyAmount.title3", Contents.Local.LOCALE.get()) + total.doubleValue() + SpringContext.getText("msg.pg.trade.ge", Contents.Local.LOCALE.get()) + currencyType.getType_name();
			String title = "购买VIP等级:由" + bql.getLevel_name() + "升级到" + levelBuyAmountSelect.getLevel_name() + ",扣除" + total.doubleValue() + "个" + paySetting.getPay_coin_name();
			
			// 增加购买VIP记录
			BQMemberLevelBuyAmountLog bqmlba = new BQMemberLevelBuyAmountLog();
//			bqmlba.setTitle(title);
			bqmlba.setCurrency_type_id(paySetting.getPay_coin_id());
//			bqmlba.setOperation(5);
//			bqmlba.setDirection(-1);
//			bqmlba.setCurrent_amount(bqa.getBtc());
			bqmlba.setChange_amount(total);
//			bqmlba.setLastest_amount(bqsu);
			bqmlba.setDateline(DateUtil.DateToUnix(new Date()));
			bqmlba.setF_user_id(user.getF_user_id());
//			bqmlba.setCurrency_type_name(currencyType.getType_name());
			bqmlba.setBefore_level_name_id(nameID);
			bqmlba.setAfter_level_name_id(levelBuyAmountSelect.getLevel_id());
			bqmlba.setBefore_level_name(bql.getLevel_name());
			bqmlba.setAfter_level_name(levelBuyAmountSelect.getLevel_name());
			bqmlba.setBfx_lock(lockBfx);
			bqmlba.setBfx_unlock(BigDecimal.ZERO);
			try {
				bqMemberLevelBuyAmountLogService.updateAccountAndLog(bqmlba, title, paySetting);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("error", "error4");
				return new ModelAndView("redirect:/center/viplist.do", model);
			}
			
			model.addAttribute("success", "success1");
			
			// 添加session
			request.getSession().setAttribute("userinfo", user);
			// Session最大生命周期（秒） 20 * 60 = 20分钟
			request.getSession().setMaxInactiveInterval(60 * 60);
			
			if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { 
				session.removeAttribute("telcode" + bqs.getF_user_tel());
			}
			
			return new ModelAndView("redirect:/center/viplist.do", model);
		} else {
			model.addAttribute("error", "error4");
			return new ModelAndView("redirect:/center/viplist.do", model);
		
		}
	}

	//
	@RequestMapping("/checkTrue")
	public void checkTrue(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		String total = request.getParameter("total");
		int amount = Integer.valueOf(total);
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, false);
			return;
		}
		if (user.getF_user_state() == -1) {// 冻结不可购买积分
			toWeb(response, false);
			return;
		}

		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (bqa == null) {
			toWeb(response, false);
			return;
		}
		BigDecimal bg = new BigDecimal(amount);
		if (bqa.getBtc().compareTo(bg) == -1) {// 帐户余额不足
			toWeb(response, false);
			return;
		}
	}
	
	/**
	 * 充值页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/recharge")
	public ModelAndView recharge(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		// 子账号不能充值
		if (user.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
			mv.setViewName("jsp/user_recharge");
			return mv;
		}
		
		String coinId = request.getParameter("coinId");
		
		int omni = Convert.toInt(request.getParameter("omni"), 1);
		int iCoinId = Utils.StrToInt(coinId, 1);
		
		int trc20 = Convert.toInt(request.getParameter("trc20"), 0);
		
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(iCoinId);
		model.addAttribute("coinId", iCoinId);
		model.addAttribute("omni", omni);
		model.addAttribute("trc20", trc20);
		model.addAttribute("coinName", currencyType.getType_name());
		List<BQMemberWallet> listWallets = null;
		
		boolean isTrc20 = false;
		boolean isErc20 = (currencyType.getErc20() && (currencyType.getType_name().equalsIgnoreCase("USDT") && omni == 0));
		if (isErc20) {
			listWallets = bqMemberWalletService.readErc20AddressByUser(user.getF_user_id());// 获取BTC钱包地址
		} else {
			isTrc20 = (currencyType.getTrc20() && (currencyType.getType_name().equalsIgnoreCase("USDT") && trc20 == 1));
			if (isTrc20) {
				listWallets = bqMemberWalletService.readTrc20AddressByUser(user.getF_user_id());// 获取BTC钱包地址
			} else {
				listWallets = bqMemberWalletService.readById(user.getF_user_id(), currencyType.getType_id());// 获取BTC钱包地址
			}
		}
		BQMemberWallet bqw = null;
		if (listWallets != null && listWallets.size() > 0) {
			bqw = listWallets.get(0);
		}

		if (bqw == null) {
			boolean locked = false;
			BQWalletQueueBtc bqb = null;
			int tryTimes = 0;
			do {
				tryTimes++;
				if (isErc20) {
					bqb = bqWalletQueueBtcService.getErc20();
				} else if (isTrc20) {
					bqb = bqWalletQueueBtcService.getTrc20();
				} else {
					bqb = bqWalletQueueBtcService.get(currencyType.getType_id());// 从钱包队列中获取一个
				}
				if (bqb == null || bqb.equals("")) {
					break;
				}
				
				locked = bqWalletQueueBtcService.lock(bqb.getWallet_id());
				
			} while(!locked && tryTimes < 3);
			
			if (locked) {
				bqw = new BQMemberWallet();
				bqw.setF_user_id(user.getF_user_id());
				bqw.setWallet_address(bqb.getWallet_address());
				if (isErc20) {
					bqw.setCurrency_type_id(0);
					bqw.setErc20(true);
				} else if (isTrc20) { 
					bqw.setCurrency_type_id(10000);
					bqw.setTrc20(true);
				} else {
					bqw.setErc20(currencyType.getErc20());
					bqw.setTrc20(currencyType.getTrc20());
					bqw.setCurrency_type_id(currencyType.getType_id());
				}
				bqw.setWallet_address_pic(bqb.getWallet_pic());
				
				if (bqWalletQueueBtcService.deleteById(bqb.getWallet_id()) > 0) {
					bqMemberWalletService.save(bqw);
				}
			}
		}
		
		if (bqw != null) {
			model.addAttribute("address", bqw.getWallet_address());
			model.addAttribute("pic", bqw.getWallet_address_pic());
		}
		
		BQMineSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MINE_SETTING);
		if (setting == null) {
			setting = bqMineSettingService.read();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MINE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting != null) {
			if (currencyType.getType_id() == setting.getCurrency_type_id()) {
				
				List<RewardRecordVo> rewards = bqBftRewardRecordService.readRewardAndMineRecords(user.getF_user_id(), currencyType.getType_id(), 10);
				if (rewards.size() > 0) {
					model.addAttribute("rewards", rewards);
				}
				
				/*
				List<BQIeoUnlockRecord> unlockRecords = bqIeoUnlockRecordService.findRecords(user.getF_user_id(), 10);
				List<BQMineRecord> mineRecords = bqMineRecordService.findRecords(user.getF_user_id(), 10);
				if (mineRecords.size() > 0) {
					model.addAttribute("mineRecords", mineRecords);
				}
				if (unlockRecords.size() > 0) {
					model.addAttribute("unlockRecords", unlockRecords);
				}*/
			}
		}
		
		// 获取所有充值记录
		PagerModel pm = bqMemberRechargeService.readRechargeAll(user.getF_user_id(), currencyType.getType_id());
		model.addAttribute("pm", pm);
		model.addAttribute("pageN", "center/recharge.do");
		mv.setViewName("jsp/user_recharge");
		return mv;
	}
	
	/**
	 * 挖矿记录页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/mineRecordsMore")
	public ModelAndView mineRecordsMore(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		
		BQMineSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MINE_SETTING);
		if (setting == null) {
			setting = bqMineSettingService.read();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MINE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting != null) {
			PagerModel mineRecords = bqMineRecordService.readRecords(user.getF_user_id(), 0, 0);
			if (mineRecords != null) {
				model.addAttribute("mineRecords", mineRecords);
			}
		}
		
		model.addAttribute("pageN", "center/mineRecordsMore.do");
		mv.setViewName("jsp/user_mineRecords");
		return mv;
	}
	
	/**
	 * 兑换页面
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("exchange")
	public ModelAndView exchange(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName);
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName);

		BigDecimal price = getBuyOrSellPrice(originCoin, targetCoin);
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
		PagerModel pm = bqExchangeRecordService.readRecordsOrderByTime(user.getF_user_id(), 0, originCoin.getType_id(), targetCoin.getType_id());
		
		String symbol = "";
		if ("USDT".equals(originCoin.getType_name().toUpperCase())) {
			symbol = targetCoinName;
		} else {
			symbol = originCoinName;
		}
		
		BQExchangeSetting setting = bqExchangeSettingService.readExchangeSettingBySymbol(symbol);
		if (setting != null) {
			model.addAttribute("minAmount", setting.getMin_amount().doubleValue());
		}
		
		String msg = request.getParameter("msg");
		if (!StringUtil.isEmpty(msg)) {
			msg = SpringContext.getText("msg.request." + msg, Contents.Local.LOCALE.get());
			if (request.getParameter("msg").equals("exchangeMin")) {
				msg += setting.getMin_amount().doubleValue() + originCoin.getType_name();
			}
			model.addAttribute("msg", msg);
		}
		
		String success = request.getParameter("success");
		if (!StringUtil.isEmpty(success)) {
			success = SpringContext.getText("msg.request." + success, Contents.Local.LOCALE.get());
			model.addAttribute("success", success);
		}
	
		model.addAttribute("originCoinName", originCoinName);
		model.addAttribute("targetCoinName", targetCoinName);
		model.addAttribute("running", setting.getRunning());
		/*model.addAttribute("contraryOriginCoin", targetCoinName);
		model.addAttribute("contraryTargetCoin", originCoinName);*/
		model.addAttribute("price", price);
		model.addAttribute("balance", balance);
		model.addAttribute("pageN", "center/exchange.do");
		model.addAttribute("pm", pm);
		mv.setViewName("jsp/user_exchange");
		return mv;
	}

	
	
	/**
	 * 用户可兑换币的全部数量
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchangeableAmount")
	public void checkExchangeableAmount(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		if(user==null){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BQExchangeSetting setting = null;
		BigDecimal price = getBuyOrSellPrice(originCoin, targetCoin);
		
		BigDecimal frozenRate = BigDecimal.ZERO; // 冻结资金率
		BigDecimal exchangeableAmount = BigDecimal.ZERO; // 可兑换的数量
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc().setScale(4, BigDecimal.ROUND_DOWN); // 对应币种账户余额
		if ("USDT".equals(originCoinName)) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
		} else {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
		}
		
		if (setting == null) {
			checkVO.setCode("error");
			checkVO.setMsg("Error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if ("USDT".equals(originCoin.getType_name().toUpperCase())) { // USDT兑换=>BTC/ETH/EOS
			frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
			exchangeableAmount = balance.divide(price.multiply(new BigDecimal(1).add(frozenRate)), 4, BigDecimal.ROUND_DOWN);
		} else { // BTC/ETH/EOS兑换=>USDT
			exchangeableAmount = balance.multiply(price).setScale(4, BigDecimal.ROUND_DOWN);
		}
		checkVO.setCode("success");
		checkVO.setMsg(exchangeableAmount.toString());
		
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 兑换之前：判断账户余额是否足够，计算兑换金额
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchange")
	public void checkExchange(HttpServletRequest request, Model model, HttpServletResponse response)
			throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		if(user==null){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		
		if (!TypeParse.IsDouble(quantity)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.request.nullExchange", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BQExchangeSetting setting = null;
		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal price = getBuyOrSellPrice(originCoin, targetCoin);
		if ("USDT".equals(originCoinName)) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
			if (setting == null) {
				checkVO.setCode("error");
				checkVO.setMsg("Error");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (!setting.getRunning()) {
				checkVO.setCode("error");
				checkVO.setMsg("暂停兑换");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (toAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + targetCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
		} else {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
			if (setting == null) {
				checkVO.setCode("error");
				checkVO.setMsg("Error");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (!setting.getRunning()) {
				checkVO.setCode("error");
				checkVO.setMsg("暂停兑换");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
			
			if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + originCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		boolean check = false;
		BigDecimal frozenRate = BigDecimal.ZERO; // 冻结资金率
		BigDecimal frozee = BigDecimal.ZERO; // 冻结资金
		BigDecimal balance = BigDecimal.ZERO; // 对应币种账户余额
		if ("USDT".equals(originCoin.getType_name().toUpperCase())) { // USDT兑换=>BTC/ETH/EOS
			frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
			frozee = fromAmount.multiply(frozenRate.add(new BigDecimal(1)));
			balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
			if (balance.compareTo(frozee) >= 0) {
				check = true;
			}
		} else { // BTC/ETH/EOS兑换=>USDT
			balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
			if (balance.compareTo(fromAmount) >= 0) {
				check = true;
			}
		}
		
		if (!check) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance",Contents.Local.LOCALE.get()));
		} else {
			checkVO.setCode("success");
			checkVO.setMsg(fromAmount.toString());
			checkVO.setFrozen(frozee.doubleValue());
		}
		
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 提交兑换
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("doExchange")
	public void doExchange(HttpServletRequest request, Model model, HttpServletResponse response) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		if (user == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		
		if (!originCoinName.equals("USDT") && !targetCoinName.equals("USDT")) {
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (originCoinName.equals(targetCoinName)) {
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种

		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal frozenAmount = BigDecimal.ZERO;
		BigDecimal frozenRate = fromAmount;
		BigDecimal price = getBuyOrSellPrice(originCoin, targetCoin);
		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQExchangeSetting setting = null;
		if ("USDT".equals(originCoinName.toUpperCase())) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
			if (setting == null) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
			frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
			frozenAmount = fromAmount.multiply(frozenRate.add(new BigDecimal(1)));
			
			if (toAmount.compareTo(setting.getMin_amount()) < 0) {	
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + targetCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
		} else {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
			if (setting == null) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
			frozenAmount = fromAmount;
			
			if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + originCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
		if (balance.compareTo(fromAmount) < 0) {
			checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance",Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		int state = Contents.EXCHANGE_STATE_MANUAL;
		String riskNote = ", 转人工";
//		int state = Contents.EXCHANGE_STATE_SUBMIT;
//		String riskNote = "";
//		BQUserRiskControl riskControl = bqUserRiskControlService.readOne(user.getF_user_id());
//		if (riskControl != null) {
//			state = Contents.EXCHANGE_STATE_MANUAL;
//			riskNote = ",风控用户，转人工";
//		}
		BQExchangeRecord exchangeRecord = new BQExchangeRecord();
		exchangeRecord.setF_user_id(user.getF_user_id());
		exchangeRecord.setOri_currency_type_id(originCoin.getType_id());
		exchangeRecord.setOri_currency_name(originCoin.getType_name());
		exchangeRecord.setTarget_currency_type_id(targetCoin.getType_id());
		exchangeRecord.setTarget_currency_name(targetCoin.getType_name());
		exchangeRecord.setFrozen_rate(frozenRate);
		exchangeRecord.setFrozen_amount(frozenAmount);
		exchangeRecord.setOri_amount(fromAmount);
		exchangeRecord.setTarget_amount(toAmount);
		exchangeRecord.setPresent_price(price);
		exchangeRecord.setState(state);
		exchangeRecord.setNote("用户提交兑换：" + originCoin.getType_name() + "兑换" + targetCoin.getType_name() + riskNote);
		if ("USDT".equals(originCoinName.toUpperCase())) { // USDT兑换=>BTC/ETH/EOS 外部市场买
			exchangeRecord.setPrice_range(setting.getOutside_buy_range());
			exchangeRecord.setDirection(Contents.DIRECTION_BUY);
			exchangeRecord.setOrder_price(price.multiply(BigDecimal.ONE.add(exchangeRecord.getPrice_range().multiply(new BigDecimal(0.01)))).setScale(setting.getDigits(), BigDecimal.ROUND_HALF_DOWN));
			
			exchangeRecord.setFee_rate(setting.getFee_rate_from_usdt());
		} else {
			exchangeRecord.setPrice_range(setting.getOutside_sell_range());
			exchangeRecord.setDirection(Contents.DIRECTION_SELL);
			exchangeRecord.setOrder_price(price.multiply(BigDecimal.ONE.subtract(exchangeRecord.getPrice_range().multiply(new BigDecimal(0.01)))).setScale(setting.getDigits(), BigDecimal.ROUND_HALF_DOWN));
			exchangeRecord.setFee_rate(setting.getFee_rate_to_usdt());
		}
		
		try {
			bqExchangeRecordService.saveRecord(exchangeRecord);
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeSuccess",Contents.Local.LOCALE.get()));
			
			if (state == Contents.EXCHANGE_STATE_MANUAL) {
				final String email = UrlConfig.getValue("exchange.manualNotify.email");
				final String msg = exchangeRecord.getOri_currency_name() + "兑换" + exchangeRecord.getTarget_currency_name();
				Runnable runnable = new Runnable() {
		            public void run() {
		            	try {
		            		SendMail themail = new SendMail();
		            		String content = msg + "需人工审核，请登录xkd后台管理处理";
		        			themail.send(email, content);
		            	} catch (Exception e) {
		            	}
		            }
		        };
		        
		        Thread thread = new Thread(runnable);
		        thread.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed",Contents.Local.LOCALE.get()));
		}
		
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	/**
	 * 兑换BFC页面
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("exchangeBfc")
	public ModelAndView exchangeBfc(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName);
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName);

		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
		PagerModel pm = bqExchangeBfcRecordService.readRecordsOrderByTime(user.getF_user_id(), 0, originCoin.getType_id(), targetCoin.getType_id());
		
		String symbol = "";
		if ("BFC".equals(originCoin.getType_name().toUpperCase())) {
			symbol = targetCoinName;
		} else {
			symbol = originCoinName;
		}
		
		BQExchangeBfcSetting setting = bqExchangeBfcSettingService.readBySymbol(symbol);
		if (setting != null) {
			model.addAttribute("minAmount", setting.getMin_amount().doubleValue());
		} else {
			return new ModelAndView("redirect:/center/assets.do");
		}
		
		String msg = request.getParameter("msg");
		if (!StringUtil.isEmpty(msg)) {
			msg = SpringContext.getText("msg.request." + msg, Contents.Local.LOCALE.get());
			if (request.getParameter("msg").equals("exchangeMin")) {
				msg += setting.getMin_amount().doubleValue() + originCoin.getType_name();
			}
			model.addAttribute("msg", msg);
		}
		
		BigDecimal price = setting.getExchange_rate();
		
		String success = request.getParameter("success");
		if (!StringUtil.isEmpty(success)) {
			success = SpringContext.getText("msg.request." + success, Contents.Local.LOCALE.get());
			model.addAttribute("success", success);
		}
	
		model.addAttribute("originCoinName", originCoinName);
		model.addAttribute("targetCoinName", targetCoinName);
		model.addAttribute("price", price);
		model.addAttribute("balance", balance);
		model.addAttribute("pageN", "center/exchangeBfc.do");
		model.addAttribute("pm", pm);
		mv.setViewName("jsp/user_exchangeBfc");
		return mv;
	}
	
	/**
	 * 用户可兑换币的全部数量
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchangeBfcAmount")
	public void checkExchangeBfcAmount(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		if(user==null){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BQExchangeBfcSetting setting = null;
		
		BigDecimal exchangeableAmount = BigDecimal.ZERO; // 可兑换的数量
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc().setScale(4, BigDecimal.ROUND_DOWN); // 对应币种账户余额
		if ("BFC".equals(originCoinName)) {
			setting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
		} else {
			setting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
		}
		
		if (setting == null) {
			checkVO.setCode("error");
			checkVO.setMsg("Error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal price = setting.getExchange_rate();
		
		if ("BFC".equals(originCoin.getType_name().toUpperCase())) { // BFC兑换=>BFX
			exchangeableAmount = balance.divide(price, 4, BigDecimal.ROUND_DOWN);
		} else { // BFX兑换=>BFC
			exchangeableAmount = balance.multiply(price).setScale(4, BigDecimal.ROUND_DOWN);
		}
		checkVO.setCode("success");
		checkVO.setMsg(exchangeableAmount.toString());
		
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 兑换之前：判断账户余额是否足够，计算兑换金额
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchangeBfc")
	public void checkExchangeBfc(HttpServletRequest request, Model model, HttpServletResponse response)
			throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		if(user==null){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		
		if (!TypeParse.IsDouble(quantity)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.request.nullExchange", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BQExchangeBfcSetting setting = null;
		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		if ("BFC".equals(originCoinName)) {
			setting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
			if (setting == null) {
				checkVO.setCode("error");
				checkVO.setMsg("Error");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (new BigDecimal(toAmount.intValue()).compareTo(toAmount) != 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeInteger",Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			if (toAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + targetCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			price = setting.getExchange_rate();
			fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
		} else {
			setting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
			if (setting == null) {
				checkVO.setCode("error");
				checkVO.setMsg("Error");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			price = setting.getExchange_rate();
			fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
			
			if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + originCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (new BigDecimal(fromAmount.intValue()).compareTo(fromAmount) != 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeInteger",Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		boolean check = false;
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
		if (balance.compareTo(fromAmount) >= 0) {
			check = true;
		}
		
		if (!check) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance",Contents.Local.LOCALE.get()));
		} else {
			checkVO.setCode("success");
			checkVO.setMsg(fromAmount.toString());
		}
		
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 提交兑换
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("doExchangeBfc")
	public void doExchangeBfc(HttpServletRequest request, Model model, HttpServletResponse response) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		if (user == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		
		if (!originCoinName.equals("BFC") && !targetCoinName.equals("BFC")) {
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (originCoinName.equals(targetCoinName)) {
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种

		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		
		BQExchangeBfcSetting setting = null;
		if ("BFC".equals(originCoinName.toUpperCase())) {
			setting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
			if (setting == null) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			price = setting.getExchange_rate();
			fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
			
			if (new BigDecimal(toAmount.intValue()).compareTo(toAmount) != 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeInteger",Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (toAmount.compareTo(setting.getMin_amount()) < 0) {	
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + targetCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
		} else {
			setting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
			if (setting == null) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			price = setting.getExchange_rate();
			fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
			
			if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeMin",Contents.Local.LOCALE.get()) + setting.getMin_amount().doubleValue() + originCoin.getType_name());
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			if (new BigDecimal(fromAmount.intValue()).compareTo(fromAmount) != 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.request.exchangeInteger",Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
		if (balance.compareTo(fromAmount) < 0) {
			checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance",Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int state = Contents.EXCHANGE_STATE_SUBMIT;
		String riskNote = ""; 
		BQUserRiskControl riskControl = bqUserRiskControlService.readOne(user.getF_user_id());
		if (riskControl != null) {
			state = Contents.EXCHANGE_STATE_MANUAL;
			riskNote = ",风控用户，转人工";
		}
		BQExchangeBfcRecord exchangeRecord = new BQExchangeBfcRecord();
		exchangeRecord.setF_user_id(user.getF_user_id());
		exchangeRecord.setOri_currency_type_id(originCoin.getType_id());
		exchangeRecord.setOri_currency_name(originCoin.getType_name());
		exchangeRecord.setTarget_currency_type_id(targetCoin.getType_id());
		exchangeRecord.setTarget_currency_name(targetCoin.getType_name());
		exchangeRecord.setOri_amount(fromAmount);
		exchangeRecord.setFrozen_amount(fromAmount);
		exchangeRecord.setTarget_amount(toAmount);
		exchangeRecord.setState(state);
		exchangeRecord.setOrder_price(price);
		exchangeRecord.setNote("用户提交兑换：" + originCoin.getType_name() + "兑换" + targetCoin.getType_name() + riskNote);
		
		try {
			bqExchangeBfcRecordService.saveRecord(exchangeRecord);
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeSuccess",Contents.Local.LOCALE.get()));
			
			if (state == Contents.EXCHANGE_STATE_MANUAL) {
				final String email = UrlConfig.getValue("exchange.manualNotify.email");
				final String msg = exchangeRecord.getOri_currency_name() + "兑换" + exchangeRecord.getTarget_currency_name();
				Runnable runnable = new Runnable() {
		            public void run() {
		            	try {
		            		SendMail themail = new SendMail();
		            		String content = msg + "需人工审核，请登录xkd后台管理处理";
		        			themail.send(email, content);
		            	} catch (Exception e) {
		            	}
		            }
		        };
		        
		        Thread thread = new Thread(runnable);
		        thread.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			checkVO.setMsg(SpringContext.getText("msg.request.exchangeFailed",Contents.Local.LOCALE.get()));
		}
		
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	/**
	 * 提现页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/withdraw")
	public ModelAndView withdraw(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.accountIsFrozen", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.amountError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.addressError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error6")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.tradePwdError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error7")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.insufficientBalance", Contents.Local.LOCALE.get()));
			} else if (num.equals("error8")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.addressNotExistent", Contents.Local.LOCALE.get()));
			} else if (num.equals("error9")) {
				model.addAttribute("error", SpringContext.getText("msg.withdraw.request.addressNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error10")) {
				model.addAttribute("error", "目前仅支持站内提币");
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.withdraw.request.applyWithdrawSuccess", Contents.Local.LOCALE.get()));
			} else if (sus.equals("success2")) {
				model.addAttribute("success", SpringContext.getText("msg.withdraw.request.deleteAddressSuccess", Contents.Local.LOCALE.get()));
			} else if (sus.equals("success3")) {
				model.addAttribute("success", SpringContext.getText("msg.withdraw.request.defaultAddressSuccess", Contents.Local.LOCALE.get()));
			} else if (sus.equals("success4")) {
				model.addAttribute("success", SpringContext.getText("msg.withdraw.request.saveAddressSuccess", Contents.Local.LOCALE.get()));
			}
		}

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
			mv.setViewName("jsp/user_withdraw");
			return mv;
		}
		int omni = 0;	//Convert.toInt(request.getParameter("omni"), 0);
		int trc20 = Convert.toInt(request.getParameter("trc20"), 0);
		String coinId = request.getParameter("coinId");
		int iCoinId = Utils.StrToInt(coinId, 1);
		boolean erc20usdt = false;
		boolean trc20usdt = false;
		
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(iCoinId);
		model.addAttribute("coinId", currencyType.getType_id());
		model.addAttribute("omni", omni);
		model.addAttribute("trc20", trc20);
		model.addAttribute("coinName", currencyType.getType_name());
		model.addAttribute("minWithdraw", currencyType.getMin_withdraw());
		model.addAttribute("maxWithdraw", currencyType.getMax_widthdraw());
		if (currencyType.getType_name().equalsIgnoreCase("USDT") && (omni == 0 || trc20 == 1)) {
			if (trc20 == 1) {
				trc20usdt = true;
				model.addAttribute("fee", currencyType.getWithdraw_fee_trc20());
			} else {
				erc20usdt = true;
				model.addAttribute("fee", currencyType.getWithdraw_fee_erc20());
			}
		} else {
			model.addAttribute("fee", currencyType.getWithdraw_fee());
		}
		model.addAttribute("erc20usdt", erc20usdt);
		model.addAttribute("trc20usdt", trc20usdt);
		
		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), currencyType.getType_id());
		model.addAttribute("balance", bqa.getBtc());
		
		// 获取所有提现记录
		PagerModel pm = bqMemberRechargeService.readWithdrawAll(user.getF_user_id(), currencyType.getType_id());
		model.addAttribute("pageN", "center/withdraw.do");
		model.addAttribute("pm", pm);
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs == null) {
			model.addAttribute("applayerror", true);
			mv.setViewName("jsp/user_withdraw");
			return mv;
		}
		// 交易密码
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqp != null && bqp.getState() != 0) {
			model.addAttribute("state", 1);
			model.addAttribute("email", user.getF_user_email());
		} else {
			model.addAttribute("state", 0);
		}

		if (bqs.getF_user_safetype() == 0) {
			model.addAttribute("applayerror", true);
			model.addAttribute("tel", 0);
			model.addAttribute("google", 0);
			mv.setViewName("jsp/user_withdraw");
			return mv;
		}

		if (bqs.getF_user_safetype() == 1) {
			model.addAttribute("tel", 1);
			model.addAttribute("mobile", bqs.getF_user_tel());
		} else if (bqs.getF_user_safetype() == 2) {
			model.addAttribute("google", 2);
			model.addAttribute("secretKey", bqs.getF_user_safegoogle());
		} else if (bqs.getF_user_safetype() == 3) {
			model.addAttribute("tel", 1);
			model.addAttribute("mobile", bqs.getF_user_tel());
			model.addAttribute("google", 2);
			model.addAttribute("secretKey", bqs.getF_user_safegoogle());
		} else {
			model.addAttribute("tel", 0);
			model.addAttribute("google", 0);
		}

		// 所有钱包地址列表
		List<BQMemberWalletAddress> list = new ArrayList<BQMemberWalletAddress>();
		if (erc20usdt) {
			list = bqMemberWalletAddressService.readErc20ByUserId(user.getF_user_id(), currencyType.getType_id());
		} else if (trc20usdt) {
			list = bqMemberWalletAddressService.readTrc20ByUserId(user.getF_user_id(), currencyType.getType_id());
		} else {
			list = bqMemberWalletAddressService.readByUserId(user.getF_user_id(), currencyType.getType_id());
		}
		
		model.addAttribute("addressList", list);
		model.addAttribute("accountBtc", bqa.getBtc().setScale(4, BigDecimal.ROUND_DOWN));

		mv.setViewName("jsp/user_withdraw");
		return mv;
	}

	// 设置提现的地址
	@RequestMapping("withdrawAddress")
	public String withdrawAddress(HttpServletRequest request, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			tickerJson.put("code", 10002);
			tickerJson.put("msg", SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			model.addAttribute("ticker", tickerJson);
			
			return "jsp/api/ticker";
		}
		
		String address = request.getParameter("address");
		String currencyId = request.getParameter("currencyId");
		String label = request.getParameter("label");
		Boolean erc20usdt = Boolean.valueOf(request.getParameter("erc20usdt"));
		Boolean trc20usdt = Boolean.valueOf(request.getParameter("trc20usdt"));
		
		HttpSession session = request.getSession();
		String code = request.getParameter("phoneCode");
		String googleCode = request.getParameter("googleCode");
		String tel = "";
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms.getF_user_safetype() == 0) {
			tickerJson.put("code", 10001);
			tickerJson.put("msg", SpringContext.getText("msg.request.notBindPhone", Contents.Local.LOCALE.get()));
			model.addAttribute("ticker", tickerJson);
			return "jsp/api/ticker";
		}
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			tel = bqms.getF_user_tel();
			BQTelCode bqTelCode = (BQTelCode) session.getAttribute("telcode" + tel);
			if (StringUtil.isEmpty(code)) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}
			if (bqTelCode == null) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + tel);
				} else {
					session.setAttribute("telcode" + tel, bqTelCode);
				}
				
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}
		}
		
		// 判断谷歌验证码
		if (bqms.getF_user_safetype() == 2 || bqms.getF_user_safetype() == 3) {
			if (googleCode == null || googleCode.isEmpty()) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}
			boolean bresult = GoogleAuthenticator.check_code(bqms.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
				model.addAttribute("ticker", tickerJson);
				return "jsp/api/ticker";
			}
		}
		
		int currency = Convert.toInt(currencyId, 0);
		if (StringUtil.isEmpty(address) || StringUtil.isEmpty(currencyId) || currency <= 0) {
			
			tickerJson.put("code", 10001);
			tickerJson.put("msg", SpringContext.getText("msg.withdraw.request.addressNull", Contents.Local.LOCALE.get()));
			model.addAttribute("ticker", tickerJson);

			return "jsp/api/ticker";
		}
		
		if (!StringUtil.validateString(address) || !StringUtil.validateString(label)) {
			
			tickerJson.put("code", 10001);
			tickerJson.put("msg", SpringContext.getText("msg.pg.userCenter.specialChar", Contents.Local.LOCALE.get()));
			model.addAttribute("ticker", tickerJson);

			return "jsp/api/ticker";
		}
		
		int count = bqMemberWalletAddressService.readCountByUserAndAddress(user.getF_user_id(), address, currency);
		if (count > 0) {
			tickerJson.put("code", 10001);
			tickerJson.put("msg", "请勿重复添加！");
			model.addAttribute("ticker", tickerJson);
			
			return "jsp/api/ticker";
		}
		
		count = bqMemberWalletAddressService.readCountByUserAndCurrency(user.getF_user_id(), currency);
		if (count >= 20) {
			tickerJson.put("code", 10001);
			tickerJson.put("msg", "请删除一些已有地址后继续操作！");
			model.addAttribute("ticker", tickerJson);
			
			return "jsp/api/ticker";
		}
		
		BQMemberWalletAddress bqwa = new BQMemberWalletAddress();
		bqwa.setF_user_id(user.getF_user_id());
		bqwa.setCurrency_type_id(currency);
		bqwa.setWallet_address(address);
		bqwa.setName(label);
		bqwa.setIs_default(0);
		if (erc20usdt == null) {
			bqwa.setErc20(false);
		} else {
			bqwa.setErc20(erc20usdt);
		}
		if (trc20usdt == null) {
			bqwa.setTrc20(false);
		} else {
			bqwa.setTrc20(trc20usdt);
		}
		bqMemberWalletAddressService.save(bqwa);
		
		if (!StringUtil.isEmpty(tel)) {
			session.removeAttribute("telcode" + tel);
		}
		tickerJson.put("msg", SpringContext.getText("msg.withdraw.request.saveAddressSuccess", Contents.Local.LOCALE.get()));
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker";
	}

	// 删除
	@RequestMapping("deleteAddress")
	public String deleteAddress(HttpServletRequest request, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			tickerJson.put("code", 10002);
			tickerJson.put("msg", SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			model.addAttribute("ticker", tickerJson);
			
			return "jsp/api/ticker";
		}
		
		String id = request.getParameter("id");

		BQMemberWalletAddress bwa = bqMemberWalletAddressService.readById(Long.parseLong(id));
		if (bwa == null || bwa.getF_user_id() != user.getF_user_id()) {
			tickerJson.put("code", 10001);
			tickerJson.put("msg", "删除失败");
			model.addAttribute("ticker", tickerJson);
			
			return "jsp/api/ticker";
		} else {
			bqMemberWalletAddressService.deleteById(Long.parseLong(id));
			
			tickerJson.put("msg", "删除成功");
			model.addAttribute("ticker", tickerJson);
			return "jsp/api/ticker";
		}
	}

	// 设置为默认
	@RequestMapping("defaultAddress")
	public ModelAndView defaultAddress(HttpServletRequest request, ModelMap mmap) {
		Contents.Local.LOCALE.set(getLocale(request));

		String id = request.getParameter("id");
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		String currencyId = request.getParameter("currencyId");
		int currency = Convert.toInt(currencyId, 0);
		if (currency <= 0) {
			mmap.addAttribute("error", "error9");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
		mmap.addAttribute("coinId", currency);
		long lid = ConvertUtil.toLong(id, 0l);
		if (lid <= 0) {
			mmap.addAttribute("error", "error9");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
		bqMemberWalletAddressService.defaultAddr(user.getF_user_id(), lid, currency);

		mmap.addAttribute("success", "success3");
		return new ModelAndView("redirect:/center/withdraw.do", mmap);
	}
	
	// 检查单日提币限额
	@RequestMapping("/checkDayMaxWithdraw")
	public void checkDayMaxWithdraw(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, true);
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		if(user == null){
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int currencyId = ConvertUtil.toInt(request.getParameter("currencyId"), 0);
		if (currencyId <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg("currencyId error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currencyType == null) {
			checkVO.setCode("error");
			checkVO.setMsg("currencyId error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		long startTime = TimeUtil.getTimesmorning();
		long endTime = startTime + 86400l;
		BigDecimal sumWithdraw = bqMemberRechargeService.getSumBetweenTime(user.getF_user_id(), currencyId, startTime, endTime);
		checkVO.setCode("success");
		checkVO.setMsg(sumWithdraw.doubleValue() + "");
		checkVO.setFrozen(currencyType.getDay_max_widthdraw().doubleValue());
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/withdrawdo")
	public ModelAndView withdrawdo(HttpServletRequest request, ModelMap mmap) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();

		BigDecimal curFee  = new BigDecimal(0);
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			mmap.addAttribute("error", "子账号不能提币");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}

		BigDecimal bgAmount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0)).setScale(5, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN);
		String address = request.getParameter("withdrawaddress");
		String paypass = request.getParameter("password");
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		int currencyId = Convert.toInt(request.getParameter("coinId"), 0);
		
		Boolean erc20usdt = Boolean.valueOf(request.getParameter("erc20usdt"));
		if (erc20usdt == null) erc20usdt = false;
		
		Boolean trc20usdt = Boolean.valueOf(request.getParameter("trc20usdt"));
		if (trc20usdt == null) trc20usdt = false;
		
		if (trc20usdt) {
			mmap.addAttribute("trc20", 1);
		}
		
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currency == null) {
			mmap.addAttribute("error", "没有该币种");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
//		String change = request.getParameter("change");

//		if (change != null && change.equals("on")) {
//			curFee = new BigDecimal(0.0001);
//		}

		mmap.addAttribute("coinId", currency.getType_id());
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		BQAccount bqAccount = bqAccountService.readAccount(user.getF_user_id(), currencyId);

		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				mmap.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
			BQTelCode tecode = (BQTelCode) session.getAttribute("telcode" + bqs.getF_user_tel());
			if (tecode == null) {
				mmap.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + bqs.getF_user_tel());
				} else {
					session.setAttribute("telcode" + bqs.getF_user_tel(), tecode);
				}
				
				mmap.addAttribute("error", "error2");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				mmap.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				mmap.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				mmap.addAttribute("error", "error3");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
		}

		if (bgAmount.compareTo(BigDecimal.ZERO) < 0) {
			mmap.addAttribute("error", "error4");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
		long startTime = TimeUtil.getTimesmorning();
		long endTime = startTime + 86400l;
		BigDecimal sumWithdraw = bqMemberRechargeService.getSumBetweenTime(user.getF_user_id(), currencyId, startTime, endTime);
		BigDecimal totalAmount = sumWithdraw.add(bgAmount);
		if (totalAmount.compareTo(currency.getDay_max_widthdraw()) > 0) {
			mmap.addAttribute("error", "error4");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
		if (address == null || address.equals("")) {
			mmap.addAttribute("error", "error5");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}

		if (erc20usdt || currency.getType_name().equalsIgnoreCase("eth")) {
			if (address.length() != 42 || !address.toLowerCase().startsWith("0x")) {
				mmap.addAttribute("error", "error5");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
		} else if (trc20usdt) {
			if (address.length() != 34 || !address.startsWith("T")) {
				mmap.addAttribute("error", "error5");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
		}

		if (bqp != null && bqp.getState() != 0) {
			if (paypass == null || paypass.equals("")) {
				mmap.addAttribute("error", "error6");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}
			try {
				paypass = MD5Service.encryptString(paypass).toLowerCase() + bqp.getSalt().toLowerCase();
				paypass = MD5Service.encryptString(paypass).toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 比较交易密码（交易密码放到最前面，有利于用户余额信息不外泄）
			if (!paypass.equals(bqp.getPwd())) {
				mmap.addAttribute("error", "error6");
				return new ModelAndView("redirect:/center/withdraw.do", mmap);
			}

		}
		// 比较金额是否符合
		if (bqAccount.getBtc().compareTo(bgAmount) == -1) {
			mmap.addAttribute("error", "error7");
			return new ModelAndView("redirect:/center/withdraw.do", mmap);
		}
		
		// 手续费
		curFee = currency.getWithdraw_fee();
		if (erc20usdt) {
			curFee = currency.getWithdraw_fee_erc20();
		} else if (trc20usdt) {
			curFee = currency.getWithdraw_fee_trc20();
		}
		
		BQMemberWallet walletAddress = bqMemberWalletServcie.readByAddress(address);
		if (walletAddress != null && currency.getInside_withdraw_fee().compareTo(BigDecimal.ZERO) >= 0) {
			curFee = currency.getInside_withdraw_fee();
		}
		
		int state = Contents.RECHARGE_STATE_ING;
		String remark = "";
		BQUserRiskControl riskControl = bqUserRiskControlService.readOne(user.getF_user_id());
		if (riskControl != null) {
			state = Contents.RECHARGE_STATE_MANUAL;
			remark= "风控用户提币，转人工处理";
		}
		
		if (currency.getType_name().equalsIgnoreCase("USDR")) {
			if (walletAddress == null) {
				if (currency.getType_name().equalsIgnoreCase("USDR")) {
					state = Contents.RECHARGE_STATE_MANUAL;
					remark= "USDR提币，转人工处理";
				}
			}
		}
		
		if (Contents.RECHARGE_STATE_ING == state && currency.getManul_max_widthdraw().compareTo(BigDecimal.ZERO) > 0
				&& totalAmount.compareTo(currency.getManul_max_widthdraw()) > 0) {
			state = Contents.RECHARGE_STATE_MANUAL;
			remark= "单日达到限额转人工,限额：" + currency.getManul_max_widthdraw().doubleValue();
		}
		// 生成提现记录
		BQMemberRecharge bqr = new BQMemberRecharge();
		bqr.setCurrency_type_id(currencyId);// 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)
		bqr.setOperation(2); // 操作类型(1:充值/2:提现)',
		bqr.setAmount(bgAmount);// 充值/提现)金额',
		bqr.setFee(curFee);// 手续费(只在提现的时候有)
		bqr.setReceice_card_type(1);// 接收卡号类型(1:普通/2:在线)'
		bqr.setReceice_card_no(address);// 接收卡号(比特币、莱特币钱包地址)
		bqr.setIp(IpMacUtil.getIpAddr(request)); // 申请IP地址
		bqr.setDateline(System.currentTimeMillis()/1000l); // 申请时间 时间戳
		bqr.setState(state);// '0:未受理 1:受理成功 -1:拒绝',
		bqr.setRemark(remark);
		bqr.setF_user_id(user.getF_user_id());// 用户ID
		bqr.setWallet_trade_id("");
		bqr.setF_user_email(user.getF_user_email());// 用户名
		bqr.setUsdt_erc20(erc20usdt);
		bqr.setUsdt_trc20(trc20usdt);
		
		try {
			userCenterService.DoWithdraw(user, bqr);
			
			if (state == Contents.RECHARGE_STATE_MANUAL) {
				final String email = UrlConfig.getValue("exchange.manualNotify.email");
				final String symbol = currency.getType_name().toUpperCase();
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							
							SendMail themail = new SendMail();
							String content = symbol + "币提币需人工审核，请登录xkd后台管理处理";
							themail.send(email, content);
						} catch (Exception e) {
							
						}
					}
				};
				
				Thread thread = new Thread(runnable);
				thread.start();
			}
	        
		} catch (com.btc100.btc_q.exception.ServiceException e) {
			e.printStackTrace();
		}
		
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { 
			session.removeAttribute("telcode" + bqs.getF_user_tel());
		}

		mmap.addAttribute("success", "success1");
		return new ModelAndView("redirect:/center/withdraw.do", mmap);

	}
	
	@RequestMapping("bindAppKey")
	public String bindAppKey(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return super.toLogin();
		}
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(user.getF_user_id());
		if (bqUserAppKey == null) {
			model.addAttribute("noAppkey", true);
		}
		Boolean bindSuccess = (Boolean)request.getSession().getAttribute("bindSuccess");
		if (bindSuccess != null && bindSuccess) {
			if (bqUserAppKey != null) {
				model.addAttribute("appkey", bqUserAppKey);
			}
			request.getSession().removeAttribute("bindSuccess");
		}
		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.appKeyNotNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.tradePwdNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.request.notSetTradePwd", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.tradePwdError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.appKeyNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error6")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.formatNotMatch", Contents.Local.LOCALE.get()));
			} else if (num.equals("error7")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error8")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error9")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.gtTenIp", Contents.Local.LOCALE.get()));
			} else if (num.equals("error10")) {
				model.addAttribute("error", SpringContext.getText("msg.request.notBindPhone", Contents.Local.LOCALE.get()));
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.request.deleteSuccess", Contents.Local.LOCALE.get()));
			}
		}
		
		// 交易密码
		BQMemberPayword bqmp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqmp != null) {
			if (bqmp.getPwd() != null) {
				model.addAttribute("payword", 1);
			}
		}
		// 手机号
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(bqms.getF_user_tel())) {
				model.addAttribute("tel", 0);
			} else {
				model.addAttribute("tel", bqms.getF_user_tel());
			}
		} else {
			model.addAttribute("tel", 0);
		}
		
		PagerModel pm = bqUserAppKeyService.readPagerModel(user.getF_user_id());
		model.addAttribute("pm", pm);
		
		return "jsp/user_api";
	}
	
	@RequestMapping("doBindAppKey")
	public ModelAndView doBindAppKey(HttpServletRequest request, ModelMap model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if(user == null){
			return new ModelAndView(super.toLogin());
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(user.getF_user_id());
		if (bqUserAppKey != null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		
		HttpSession session = request.getSession();
		String tel = request.getParameter("mobile");
		String code = request.getParameter("code");
		BQTelCode bqTelCode = (BQTelCode) session.getAttribute("telcode" + tel);
		if (StringUtil.isEmpty(code)) {
			model.addAttribute("error", "error7");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms.getF_user_safetype() != 1 && bqms.getF_user_safetype() != 3) {
			model.addAttribute("error", "error10");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		if (StringUtil.isEmpty(bqms.getF_user_tel())) {
			model.addAttribute("error", "error10");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		if (bqTelCode == null) {
			model.addAttribute("error", "error7");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		if (!bqTelCode.getF_user_tel().equals(tel)) {
			model.addAttribute("error", "error8");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		if (!bqTelCode.getF_auth_code().equals(code)) {
			
			bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
			if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				session.removeAttribute("telcode" + tel);
			} else {
				session.setAttribute("telcode" + tel, bqTelCode);
			}
			
			model.addAttribute("error", "error8");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		
		String paypwd = request.getParameter("paypwd");
		if (StringUtil.isEmpty(paypwd)) {
			model.addAttribute("error", "error2");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqp == null) {
			model.addAttribute("error", "error3");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		
		}
		try {
			paypwd = MD5Service.encryptString(paypwd).toLowerCase() + bqp.getSalt().toLowerCase();
			paypwd = MD5Service.encryptString(paypwd).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 比较交易密码
		if (!paypwd.equals(bqp.getPwd())) {
			model.addAttribute("error", "error4");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		
		String allowIp = request.getParameter("allowIp");
		if (!StringUtil.isEmpty(allowIp)) {
			// 验证IP格式
			String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
					+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
					+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
			if (!allowIp.matches(regex)) {
				model.addAttribute("error", "error6");
				return new ModelAndView("redirect:/center/bindAppKey.do", model);
			}
			// API最多只能绑定10个IP
			String[] ipArray = allowIp.split(",");
			if (ipArray.length > 10) {
				model.addAttribute("error", "error9");
				return new ModelAndView("redirect:/center/bindAppKey.do", model);
			}
		}
		BQUserAppKey userAppKey = new BQUserAppKey();
		userAppKey.setF_user_id(user.getF_user_id());
		userAppKey.setAllow_ip(allowIp);
		userAppKey.setApp_key(UUID.randomUUID().toString().replaceAll("-", ""));
		userAppKey.setApp_secret(UUID.randomUUID().toString().replaceAll("-", ""));
		long overTime = 0; 
		if (StringUtil.isEmpty(allowIp)) overTime = System.currentTimeMillis() / 1000 + 90 * 24 * 3600;
		userAppKey.setOver_time(overTime);
		userAppKey.setState(1);
		userAppKey.setUpdate_time(System.currentTimeMillis() / 1000);
		
		bqUserAppKeyService.save(userAppKey);
		session.removeAttribute("telcode" + tel);
		
		request.getSession().setAttribute("bindSuccess", true);
		return new ModelAndView("redirect:/center/bindAppKey.do", model);
	}
	
	@RequestMapping("/deleteAppKey")
	public ModelAndView deleteAppKey(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if(user == null){
			return new ModelAndView(super.toLogin());
		}
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(user.getF_user_id());
		if (bqUserAppKey == null) {
			model.addAttribute("error", "error5");
			return new ModelAndView("redirect:/center/bindAppKey.do", model);
		}
		
		bqUserAppKeyService.delete(user.getF_user_id());
		model.addAttribute("success", "success1");
		return new ModelAndView("redirect:/center/bindAppKey.do", model);
	}
	
	@RequestMapping("/fundingFee")
	public ModelAndView fundingFee(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		ModelAndView mv = new ModelAndView();
		
		String code = request.getParameter("code");
		String partition = "usdt";
		
		List<NContractPartition> partitions = new ArrayList<>();
		NContractPartition usdtPart = new NContractPartition();
		usdtPart.setPart("usdt");
		usdtPart.setCurrency_type_id(Contents.TRADE_CURRENCY_TYPE_ID);
		partitions.add(0, usdtPart);

		List<BQFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS);
		if (settings == null) {
			settings = bqFundingFeeSettingService.readAll();
			if (settings != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
			}
		}

		if (settings == null) {
			settings = new ArrayList<BQFundingFeeSetting>();
		}

		if (StringUtil.isEmpty(code)) {
			if (settings.size() > 0) code = settings.get(0).getF_code();
		}

		model.addAttribute("settings", settings);

		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(code)) {
			BQFundingFeeSetting tran = null;
			for (BQFundingFeeSetting trans : settings) {
				if (trans.getF_code().equalsIgnoreCase(code)) {
					tran = trans;
					break;
				}
			}
			if (tran == null && settings.size() > 0) tran = settings.get(0);

			if (tran != null) {
				code = tran.getF_code();
				pm = fundingFeeRecordService.readBySymbol(code, user.getF_user_id());
			}
		}

		model.addAttribute("pm", pm);

		model.addAttribute("code", code);
		model.addAttribute("partition", partition);
		model.addAttribute("partitions", partitions);

		mv.setViewName("jsp/user_fundingFee");
		return mv;
	}
	
	@RequestMapping("/vFundingFee")
	public ModelAndView vFundingFee(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		ModelAndView mv = new ModelAndView();
		
		String code = request.getParameter("code");
		
		List<VFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS);
		if (settings == null) {
			settings = vFundingFeeSettingService.readAll();
			if (settings != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
			}
		}
		
		if (settings == null) {
			settings = new ArrayList<VFundingFeeSetting>(); 
		}
		
		if (StringUtil.isEmpty(code)) {
			if (settings.size() > 0) code = settings.get(0).getF_code();
		}
		
		model.addAttribute("settings", settings);
		model.addAttribute("code", code);
		
		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(code)) {
			pm = vFundingFeeRecordService.readBySymbol(code, user.getF_user_id());
		}
		
		model.addAttribute("pm", pm);
		
		mv.setViewName("jsp/user_fundingFee");
		return mv;
	}

	public int getTime(Date date) {// 得到当前时间戳
		Timestamp appointTime = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date;
		try {
			date = df.parse(String.valueOf(appointTime));
			long s = date.getTime();
			String unixDate = String.valueOf(s).substring(0, 10);
			return Integer.parseInt(unixDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@RequestMapping("/checkAmount")
	public void checkAmount(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		String amount = request.getParameter("amount");
		int amou = Integer.valueOf(amount);
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, false);
			return;
		}
		if (user.getF_user_state() == 2) {// 冻结不可体现
			toWeb(response, false);
			return;
		}
		if (amou <= 0) {// 体现金额不可<0
			toWeb(response, false);
			return;
		}
		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal bg = new BigDecimal(amou);
		if (bqa.getBtc().compareTo(bg) == -1) {
			toWeb(response, false);
			return;
		}
	}

	@RequestMapping("/usersafe")
	public String userSafe(String username, String password, Model model) {
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) { // 非空验证
			model.addAttribute("msg", SpringContext.getText("msg.common.request.emailOrPwdNull", Contents.Local.LOCALE.get()));
			return "jsp/binding_account";
		}
		try {

			List<String> params = new ArrayList<String>();
			params.add("method=userSafe");
			params.add("password=" + password);
			params.add("username=" + username);

			String result = HttpToServer.sendGetAutoChange(UrlConfig.getValue("strSafeBtc100"), getEnceypt(params));
			JSONObject obj = new JSONObject(result);

			if (obj.getBoolean("result")) {
				int safeType = ConvertUtil.toInt(obj.get("code"), 0); // 1短信3谷歌3双重
				model.addAttribute("safeType", safeType);
				model.addAttribute("username", APIEnDecrype.encryptDES(username));
				model.addAttribute("password", APIEnDecrype.encryptDES(password));
			} else {
				model.addAttribute("msg", SpringContext.getText("msg.common.request.emailOrPwdError", Contents.Local.LOCALE.get()));
				return "jsp/binding_account";
			}
		} catch (Exception e) {
			model.addAttribute("msg", SpringContext.getText("msg.usersafe.request.bindingFail", Contents.Local.LOCALE.get()));
			return "jsp/binding_account";
		}
		return "jsp/binding_account1";
	}

	@RequestMapping("/sendTel")
	public void userSafeTel(String username, String password, HttpServletResponse response, Model model) {
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) { // 非空验证
			toWeb(response, 0);
			return;
		}
		try {
			username = APIEnDecrype.decrypt(username);
			password = APIEnDecrype.decrypt(password);

			List<String> params = new ArrayList<String>();
			params.add("method=sendTel");
			params.add("password=" + password);
			params.add("username=" + username);

			String result = HttpToServer.sendGetAutoChange(UrlConfig.getValue("strSafeBtc100"), getEnceypt(params));
			JSONObject obj = new JSONObject(result);
			// 1发送成功
			toWeb(response, ConvertUtil.toInt(obj.get("code"), 0));
		} catch (Exception e) {
			toWeb(response, 0);
		}
	}

	@RequestMapping("/sendVoice")
	public void userSafeVoice(String username, String password, HttpServletResponse response, Model model) throws JSONException {
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) { // 非空验证
			toWeb(response, 0);
			return;
		}
		try {
			username = APIEnDecrype.decrypt(username);
			password = APIEnDecrype.decrypt(password);

			List<String> params = new ArrayList<String>();
			params.add("method=sendVoice");
			params.add("password=" + password);
			params.add("username=" + username);

			String result = HttpToServer.sendGetAutoChange(UrlConfig.getValue("strSafeBtc100"), getEnceypt(params));
			JSONObject obj = new JSONObject(result);
			// 1发送成功
			toWeb(response, ConvertUtil.toInt(obj.get("code"), 0));
		} catch (Exception e) {
			toWeb(response, 0);
		}
	}

	@RequestMapping("loadAmount")
	public void loadAmount(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			toWeb(response, 0);
			return;
		}
		
		BigDecimal totalAccount = BigDecimal.ZERO;
		BQAccount account = (BQAccount) request.getSession().getAttribute("userAccount");
		if (account == null || account.getLastupdate() + 60 < System.currentTimeMillis() / 1000) {
			
			List<BQCurrencyType> currencies = bqCurrencyTypeService.readAll();
			List<BQAccount> assets = bqAccountService.readAllByUserId(user.getF_user_id());
			for (BQAccount bqAccount : assets) {
				BQCurrencyType currencyType = null;
				for (BQCurrencyType currType : currencies) {
					if (currType.getType_id() == bqAccount.getCurrency_type_id()) {
						currencyType = currType;
						break;
					}
				}
				
				if (currencyType == null) continue;
				
				String coinName = currencyType.getType_name();
				if (!coinName.equalsIgnoreCase("BFC")) {
					if (coinName.equalsIgnoreCase("usdt") || coinName.equalsIgnoreCase("usdr")) {
						totalAccount = totalAccount.add(bqAccount.getBtc().add(bqAccount.getBtc_locked()));
					} else {
						BigDecimal indexPrice = findBPIByCode(coinName.toLowerCase());
						if (indexPrice != null && indexPrice.compareTo(BigDecimal.ZERO) > 0) {
							totalAccount = totalAccount.add((bqAccount.getBtc().add(bqAccount.getBtc_locked())).multiply(indexPrice));
						} else {
							BigDecimal bfxLock = BigDecimal.ZERO;
							if (coinName.toLowerCase().equals("bfx")) {
								BigDecimal ieoLock = BigDecimal.ZERO;
								BigDecimal ieoUnlock = BigDecimal.ZERO;
								BQIeoAccount act = bqIeoAccountService.getAccountByUserId(user.getF_user_id());
								if (act != null) {
									ieoLock = act.getLocked();
									ieoUnlock = act.getUnlocked();
								}
								
								Object[] retObj = (Object [])bqMemberLevelBuyAmountLogService.readSumBfxLock(user.getF_user_id());
								if (retObj.length == 2) {
									ieoLock = ieoLock.add((BigDecimal)retObj[0]);
									ieoUnlock = ieoUnlock.add((BigDecimal)retObj[1]);
								}
								
								if (ieoLock.compareTo(BigDecimal.ZERO) > 0 || ieoUnlock.compareTo(BigDecimal.ZERO) > 0) {
									bfxLock = ieoLock.subtract(ieoUnlock);
								}
							}
							
							BQSpotMarketCoinPair coinPair = findCurrCoinPairByName(coinName.toLowerCase() + "usdt");
							if (coinPair != null) {
								String pairName = coinPair.getCoin_pair_name();
								BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
								if (last == null) {
									last = coinPair.getLatest_price();
								}
								if (last != null && last.compareTo(BigDecimal.ZERO) > 0) {
									totalAccount = totalAccount.add((bfxLock.add(bqAccount.getBtc()).add(bqAccount.getBtc_locked())).multiply(last));
								}
							} else {
								coinPair = findCurrCoinPairByName(coinName.toLowerCase() + "usdr");
								if (coinPair != null) {
									String pairName = coinPair.getCoin_pair_name();
									BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
									if (last == null) {
										last = coinPair.getLatest_price();
									}
									if (last != null && last.compareTo(BigDecimal.ZERO) > 0) {
										totalAccount = totalAccount.add((bfxLock.add(bqAccount.getBtc()).add(bqAccount.getBtc_locked())).multiply(last));
									}
								}
							}
						}
					}
				}
			}
			
			
			Long id = user.getF_user_id();
			{
				BigDecimal freez = bqAccountContractService.readLockAmountByUid(id);
				BigDecimal availableBtc = bqAccountContractService.readAmountByUid(id);
				BigDecimal floatGainLoss = new BigDecimal(0);
				BigDecimal depositAndYk = BigDecimal.ZERO;
				BigDecimal fundingFee = BigDecimal.ZERO;
				
				List<BQMemberTransaction> allBuyAndSellMemberTransaction = bqMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
				for (BQMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
					int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
					String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
					BQTransaction currentTransaction = super.findCurrTrans(transactionCode);
					if (currentTransaction == null ) {
						continue;
					} else {
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + currentTransaction.getTransaction_code());
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
						}
						
						if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
	//							ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
							floatGainLoss = floatGainLoss.add(ykdiff);
							
						} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						
						fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
						depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
								.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
								.add(buyOrSellMemberTransaction.getF_frozenremain());
					}
				}
				
				totalAccount = totalAccount.add(depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee));
			}
			
			{
				BigDecimal freez = dAccountContractService.readLockAmountByUid(id);
				BigDecimal availableBtc = dAccountContractService.readAmountByUid(id);

				BigDecimal floatGainLoss = new BigDecimal(0);
				BigDecimal depositAndYk = BigDecimal.ZERO;
				BigDecimal fundingFee = BigDecimal.ZERO;

				List<DeliveryMemberTransaction> allBuyAndSellMemberTransaction = dMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
				for (DeliveryMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
					int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
					String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
					BQTransaction currentTransaction = super.findCurrTrans(transactionCode);
					if (currentTransaction == null ) {
						continue;
					} else {
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
						}
						
						if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
//								ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
							floatGainLoss = floatGainLoss.add(ykdiff);
							
						} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						
						fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
						depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
								.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
								.add(buyOrSellMemberTransaction.getF_frozenremain());
					}
				}
				
				totalAccount = totalAccount.add(depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee));
			}
			
			List<NContractPartition> partitions = nContractPartitionService.readAll();
			for (NContractPartition partition : partitions) {
				BigDecimal freez = nAccountContractService.readLockAmountByUid(partition.getPart(), id);
				BigDecimal availableBtc = nAccountContractService.readAmountByUid(partition.getPart(), id);
				BigDecimal floatGainLoss = new BigDecimal(0);
				BigDecimal depositAndYk = BigDecimal.ZERO;
				BigDecimal fundingFee = BigDecimal.ZERO;
				
				List<NMemberTransaction> allBuyAndSellMemberTransaction = nMemberTransactionService.readMemberTrasactionByUserId(partition.getPart(), user.getF_user_id());
				for (NMemberTransaction buyOrSellMemberTransaction : allBuyAndSellMemberTransaction) {
					int buyOrSellTradeTypeId = buyOrSellMemberTransaction.getTrade_type_id();
					String transactionCode = buyOrSellMemberTransaction.getTransaction_code();
					BQTransaction currentTransaction = super.findCurrTrans(transactionCode);
					if (currentTransaction == null ) {
						continue;
					} else {
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition.getPart() + "_" + currentTransaction.getTransaction_code());
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
						}
						
						if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = markPrice.subtract(buyOrSellMemberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
//								ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
							floatGainLoss = floatGainLoss.add(ykdiff);
							
						} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
							BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
							ykdiff = buyOrSellMemberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
									.multiply(buyOrSellMemberTransaction.getLock_count().add(buyOrSellMemberTransaction.getHold_count()));
							floatGainLoss = floatGainLoss.add(ykdiff);
						}
						
						fundingFee = fundingFee.add(buyOrSellMemberTransaction.getDeposit2());
						depositAndYk = depositAndYk.add(buyOrSellMemberTransaction.getDeposit())
								.add(buyOrSellMemberTransaction.getF_fromremain()).add(buyOrSellMemberTransaction.getF_min_frozenremain())
								.add(buyOrSellMemberTransaction.getF_frozenremain());
					}
				}
				
				 totalAccount = totalAccount.add(depositAndYk.add(freez).add(availableBtc).add(floatGainLoss).add(fundingFee));
			}
			
			account = new BQAccount();
			account.setBtc(totalAccount);
			account.setLastupdate(System.currentTimeMillis() / 1000);
			
			request.getSession().setAttribute("userAccount", account);
		} else {
			totalAccount = account.getBtc();
		}
		
		toWeb(response, totalAccount.setScale(4, RoundingMode.DOWN));
	}
	
	@RequestMapping(value="changeFeeRateType", method = RequestMethod.POST)
	public void changeFeeRateType(HttpServletRequest request, HttpServletResponse response) {

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, "failed");
			return;
		}
		
		String typeStr =  request.getParameter("type");
		if (StringUtil.isEmpty(typeStr)) {
			toWeb(response, "failed");
			return;
		}
		
		int type = Convert.toInt(typeStr, 0);
		if (type != 0 && type != 1) {
			toWeb(response, "failed");
			return;
		}
		
		if (type != user.getF_fee_rate_type()) {
			bqMemberService.updateFeeRateType(user.getF_user_id(), type);
			super.findCurrUser(request, true);
		}
		
		toWeb(response, "success");
	}

	private String getEnceypt(List<String> paras) {
		Collections.sort(paras);
		StringBuilder sb = new StringBuilder("");

		for (String para : paras) {
			sb.append(para).append("&");
		}
		if (sb.toString().equals("")) {
			return "";
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
}
