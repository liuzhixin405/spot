package com.btc100.btc_q.action.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.MatrixToImageWriter;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.common.Utils;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.ImageUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.OSSFileUtil;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.Security;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.CoinAssetsVO;
import com.btc100.btc_q.vo.InviteVO;
import com.btc100.btc_q.vo.UserAccountAmountLogVo;
import com.btc100.btc_q.vo.UserAssetsVO;
import com.btc100.btc_q.vo.UserBonusRecordVo;
import com.btc100.btc_q.vo.UserContractAccountVo;
import com.btc100.btc_q.vo.UserInviteMemberVo;
import com.btc100.btc_q.vo.UserRechargeVo;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import sun.misc.BASE64Decoder;

/**
 * 用户信息API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/userCenterApi")
public class UserCenterApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(UserCenterApiAction.class);
	
	@Autowired
	private BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberLevelBuyAmountLogService bqMemberLevelBuyAmountLogService;
	@Autowired
	private BQCommonCreditLogService bqCommonCreditLogService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
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
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQBonusRecordService bqBonusRecordService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
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
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQSubMemberService bqSubMemberService;
	@Autowired
	private BQMemberLevelBuySettingService bqMemberLevelBuySettingService;
	@Autowired
	private BQMemberLevelBuyRebateReconciliaService bqMemberLevelBuyRebateReconciliaService;
	@Autowired
	private BQIeoAccountService bqIeoAccountService;
	@Autowired
	private NAccountContractService nAccountContractService;
	@Autowired
	private BQMemberWalletService bqMemberWalletServcie;
	@Autowired
	private BQMemberModifyInformationRecordService bqMemberModifyInformationRecordService;
	
	
	/**
	 * 用户中心-我的资产
	 * 
	 */
	@RequestMapping("/myAssets")
	public String myAssets(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		JSONArray array = new JSONArray();
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
			
			if (currencyType.getType_name().equalsIgnoreCase("BFX")) {
				BigDecimal ieoLock = BigDecimal.ZERO;
				BigDecimal ieoUnlock = BigDecimal.ZERO;
				BQIeoAccount ieoAccount = bqIeoAccountService.getAccountByUserId(user.getF_user_id());
				if (ieoAccount != null) {
					ieoLock = ieoAccount.getLocked();
					ieoUnlock = ieoAccount.getUnlocked();
				}
				
				Object[] retObj = (Object [])bqMemberLevelBuyAmountLogService.readSumBfxLock(user.getF_user_id());
				if (retObj.length == 2) {
					ieoLock = ieoLock.add((BigDecimal)retObj[0]);
					ieoUnlock = ieoUnlock.add((BigDecimal)retObj[1]);
				}
				
				coinAssetsVO.setLock(ieoLock.doubleValue());
				coinAssetsVO.setUnlock(ieoUnlock.doubleValue());
			}
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(coinAssetsVO)));
		}

		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-我的资产(包含所有币种折合成USDT)
	 * 
	 */
	@RequestMapping("/assets/allCoin")
	public String totalUsdt(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BigDecimal totalUsdt = BigDecimal.ZERO; // 钱包账户USDT价值总和
		JSONArray array = new JSONArray();
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
			coinAssetsVO.setCoinName(coinName);
			coinAssetsVO.setCoinDescribleZh(currencyType.getDescription_zh());
			coinAssetsVO.setCoinDescribleEn(currencyType.getDescription_en());
			coinAssetsVO.setCoinDescribleJp(currencyType.getDescription_jp());
			coinAssetsVO.setCoinDescribleKr(currencyType.getDescription_kr());
			coinAssetsVO.setCoinDescribleHk(currencyType.getDescription_hk());
			coinAssetsVO.setEnableRecharge(currencyType.isEnable_recharge());
			coinAssetsVO.setEnableWithdraw(currencyType.isEnable_withdraw());
			
			if (currencyType.getType_name().equalsIgnoreCase("BFX")) {
				BigDecimal ieoLock = BigDecimal.ZERO;
				BigDecimal ieoUnlock = BigDecimal.ZERO;
				BQIeoAccount ieoAccount = bqIeoAccountService.getAccountByUserId(user.getF_user_id());
				if (ieoAccount != null) {
					ieoLock = ieoAccount.getLocked();
					ieoUnlock = ieoAccount.getUnlocked();
				}
				
				Object[] retObj = (Object [])bqMemberLevelBuyAmountLogService.readSumBfxLock(user.getF_user_id());
				if (retObj.length == 2) {
					ieoLock = ieoLock.add((BigDecimal)retObj[0]);
					ieoUnlock = ieoUnlock.add((BigDecimal)retObj[1]);
				}
				
				coinAssetsVO.setLock(ieoLock.doubleValue());
				coinAssetsVO.setUnlock(ieoUnlock.doubleValue());
			}
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(coinAssetsVO)));
			
			if (!coinName.equalsIgnoreCase("BFC")) {
				if (bqAccount.getCurrency_type_id() == Contents.TRADE_CURRENCY_TYPE_ID || coinName.equalsIgnoreCase("usdr")) {
					totalUsdt = totalUsdt.add(bqAccount.getBtc());
				}
				
				BigDecimal indexPrice = findBPIByCode(coinName.toLowerCase());
				if (indexPrice != null && indexPrice.compareTo(BigDecimal.ZERO) > 0) {
					totalUsdt = totalUsdt.add(total.multiply(indexPrice));
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
							totalUsdt = totalUsdt.add(bfxLock.add(total).multiply(last));
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
								totalUsdt = totalUsdt.add(bfxLock.add(total).multiply(last));
							}
						}
					}
				}
			}
		}

		JSONObject data = new JSONObject();
		data.put("assets", array);
		data.put("totalUsdt", totalUsdt);
		userJson.put("data", data);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-持有bfx返利记录列表
	 * 
	 */
	@RequestMapping("/bonusRecords")
	public String bonusRecords(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<BQBonusRecord> records = bqBonusRecordService.readByUserAndTime(user.getF_user_id(), nFrom, direct, start,
				end, size);
		for (BQBonusRecord record : records) {
			UserBonusRecordVo vo = new UserBonusRecordVo();
			vo.setRecordId(record.getId());
			vo.setDay(record.getDay());
			vo.setCurrencyName("USDT");
			vo.setBfxHold(record.getHold_amount().doubleValue() + record.getFrozen_amount().doubleValue());
			vo.setBonusRate(record.getRate().doubleValue() * 1000000);
			vo.setBonusAmount(record.getAmount().doubleValue());
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(vo)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-永续合约资产
	 * 
	 */
	@RequestMapping("/center/basic")
	public String centerBasic(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 基本信息
		Long id = user.getF_user_id();
		
		if (type == null || "".equals(type) || "contract".equals(type)) {

			BQAccount bqAccount = bqAccountService.readAccount(id, Contents.TRADE_CURRENCY_TYPE_ID);
			BigDecimal freez = bqAccountContractService.readLockAmountByUid(id);
			BigDecimal deposit = bqMemberTransactionService.readDepositByUid(id);
			BigDecimal availableBtc = bqAccountContractService.readAmountByUid(id);
			BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
			if (StringUtil.isEmpty(usdRate)) {
				usdRate = bqUsdrateService.readUsdRateOnuse(1);
				spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
			}
			BigDecimal cnyAccount = bqAccount.getBtc().multiply(usdRate);
			BigDecimal account = bqAccount.getBtc();// 我的钱包
			UserContractAccountVo accountVo = new UserContractAccountVo();
			accountVo.setUserId(id);
			accountVo.setAccount(account.doubleValue());// 我的钱包(美元)
			accountVo.setCnyAccount(cnyAccount.doubleValue());// 我的钱包(人民币)
			accountVo.setFreez(freez.doubleValue());
			accountVo.setDeposit(deposit.doubleValue());
			accountVo.setAvailableBtc(availableBtc.doubleValue());
			BigDecimal total = freez.add(deposit).add(availableBtc);
			BigDecimal cnyTotal = total.multiply(usdRate);
			accountVo.setContractTotal(total.doubleValue());
			accountVo.setCnyContractTotal(cnyTotal.doubleValue());
			Gson gson = new Gson();
			userJson.put("data", new JSONObject(gson.toJson(accountVo)));
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				BQAccount bqAccount = bqAccountService.readAccount(id, part.getCurrency_type_id());
				BigDecimal freez = nAccountContractService.readLockAmountByUid(type, id);
				BigDecimal deposit = nMemberTransactionService.readDepositByUid(type, id);
				BigDecimal availableBtc = nAccountContractService.readAmountByUid(type, id);
				BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
				if (StringUtil.isEmpty(usdRate)) {
					usdRate = bqUsdrateService.readUsdRateOnuse(1);
					spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
				}
				BigDecimal cnyAccount = bqAccount.getBtc().multiply(usdRate);
				BigDecimal account = bqAccount.getBtc();// 我的钱包
				UserContractAccountVo accountVo = new UserContractAccountVo();
				accountVo.setUserId(id);
				accountVo.setAccount(account.doubleValue());// 我的钱包(美元)
				accountVo.setCnyAccount(cnyAccount.doubleValue());// 我的钱包(人民币)
				accountVo.setFreez(freez.doubleValue());
				accountVo.setDeposit(deposit.doubleValue());
				accountVo.setAvailableBtc(availableBtc.doubleValue());
				BigDecimal total = freez.add(deposit).add(availableBtc);
				BigDecimal cnyTotal = total.multiply(usdRate);
				accountVo.setContractTotal(total.doubleValue());
				accountVo.setCnyContractTotal(cnyTotal.doubleValue());
				Gson gson = new Gson();
				userJson.put("data", new JSONObject(gson.toJson(accountVo)));
			}
		}

		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-永续合约资产详情
	 * 
	 */
	@RequestMapping("/center/detail")
	public String centerDetail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 资产详情
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		
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
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
	
				// 做多
				BigDecimal buyAccount = BigDecimal.ZERO;
				// 做空
				BigDecimal sellAccount = BigDecimal.ZERO;
				for (BQMemberTransaction bqmt1 : userAllTrans) {
					if (bqmt1.getTransaction_code().equals(code)) {
						if (bqmt1.getTrade_type_id() == 1) {
							buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
						} else {
							sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
						}
					}
				}
				
				uav.setBuyAmount(buyAccount);
				uav.setSellAmount(sellAccount);
				
				// 持仓市值
				BigDecimal bgsum = buyAccount.add(sellAccount);
				uav.setTotalAmount(bgsum);
	
				// 做多委托
				BigDecimal deleBuyAccount = pendingDelegationService.readDelegation(id, code, 1);
				uav.setBuydelegation(deleBuyAccount);
				// 做空委托
				BigDecimal deleSellAccount = pendingDelegationService.readDelegation(id, code, 2);
				uav.setSelldelegation(deleSellAccount);
				// 委托市值
				BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
				uav.setDelegationAmount(delesum);
				// 当前合约可用
				BQAccountContract accountContract = bqAccountContractService.readByUidCode(id, code);
				uav.setF_btc(accountContract.getF_btc());
	
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(uav)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + type);
				if (list == null) {
					list = nTransactionService.readTransByState_new(type, Contents.TRANSACTION_STATE_ZC);
				}
		
				List<NMemberTransaction> userAllTrans = nMemberTransactionService.readMemberTrasactionByUserId(type, user.getF_user_id());
				for (NTransaction bqTransaction : list) {
					String code = bqTransaction.getTransaction_code();
		
					// 合约号
					UserAssetsVO uav = new UserAssetsVO();
					uav.setShowName(bqTransaction.getTransaction_show_name());
					uav.setCode(code);
					uav.setTransactionType(bqTransaction.getTransaction_type());
		
					// 做多
					BigDecimal buyAccount = BigDecimal.ZERO;
					// 做空
					BigDecimal sellAccount = BigDecimal.ZERO;
					for (NMemberTransaction bqmt1 : userAllTrans) {
						if (bqmt1.getTransaction_code().equals(code)) {
							if (bqmt1.getTrade_type_id() == 1) {
								buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							} else {
								sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
							}
						}
					}
					
					uav.setBuyAmount(buyAccount);
					uav.setSellAmount(sellAccount);
					
					// 持仓市值
					BigDecimal bgsum = buyAccount.add(sellAccount);
					uav.setTotalAmount(bgsum);
		
					// 做多委托
					BigDecimal deleBuyAccount = nPendingDelegationService.readDelegation(type, id, code, 1);
					uav.setBuydelegation(deleBuyAccount);
					// 做空委托
					BigDecimal deleSellAccount = nPendingDelegationService.readDelegation(type, id, code, 2);
					uav.setSelldelegation(deleSellAccount);
					// 委托市值
					BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
					uav.setDelegationAmount(delesum);
					// 当前合约可用
					NAccountContract accountContract = nAccountContractService.readByUidCode(type, id, code);
					uav.setF_btc(accountContract.getF_btc());
		
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(uav)));
				}
			}
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-永续合约资产余额
	 * 
	 */
	@RequestMapping("/center/contractBalance")
	public String contractBalance(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 资产详情
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		
		if (type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
			if (list == null) {
				list = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			}
	
			for (BQTransaction bqTransaction : list) {
				String code = bqTransaction.getTransaction_code();
	
				// 合约号
				UserAssetsVO uav = new UserAssetsVO();
				uav.setShowName(bqTransaction.getTransaction_show_name());
				uav.setCode(code);
				uav.setTransactionType(bqTransaction.getTransaction_type());
				// 当前合约可用
				BQAccountContract accountContract = bqAccountContractService.readByUidCode(id, code);
				uav.setF_btc(accountContract.getF_btc());
	
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(uav)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + type);
				if (list == null) {
					list = nTransactionService.readTransByState_new(type, Contents.TRANSACTION_STATE_ZC);
				}
		
				for (NTransaction bqTransaction : list) {
					String code = bqTransaction.getTransaction_code();
		
					// 合约号
					UserAssetsVO uav = new UserAssetsVO();
					uav.setShowName(bqTransaction.getTransaction_show_name());
					uav.setCode(code);
					uav.setTransactionType(bqTransaction.getTransaction_type());
					// 当前合约可用
					NAccountContract accountContract = nAccountContractService.readByUidCode(type, id, code);
					uav.setF_btc(accountContract.getF_btc());
		
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(uav)));
				}
			}
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-模拟合约资产基本信息
	 * 
	 */
	@RequestMapping("/vCenter/basic")
	public String vCenterBasic(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 基本信息
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
		double freez = vAccountContractService.readLockAmountByUid(id).doubleValue();
		double deposit = vMemberTransactionService.readDepositByUid(id).doubleValue();
		double availableBtc = vAccountContractService.readAmountByUid(id).doubleValue();
		double account = bqAccount.getBtc().doubleValue();// 我的钱包
		UserContractAccountVo vAccountVo = new UserContractAccountVo();
		vAccountVo.setUserId(id);
		vAccountVo.setAccount(account);// 我的钱包
		vAccountVo.setFreez(freez);
		vAccountVo.setDeposit(deposit);
		vAccountVo.setAvailableBtc(availableBtc);
		vAccountVo.setContractTotal(freez + deposit + availableBtc);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(vAccountVo)));

		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-模拟合约资产详情
	 * 
	 */
	@RequestMapping("/vCenter/detail")
	public String vCenterDetail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 资产详情
		List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(list == null) {
			list = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		
		List<VMemberTransaction> userAllTrans = vMemberTransactionService.readVMemberTrasactionByUserId(user.getF_user_id());
		for (VTransaction bqTransaction : list) {
			String code = bqTransaction.getTransaction_code();

			// 合约号
			UserAssetsVO uav = new UserAssetsVO();
			uav.setShowName(bqTransaction.getTransaction_code().toUpperCase());
			uav.setCode(code);
			uav.setTransactionType(bqTransaction.getTransaction_type());

			// 做多
			BigDecimal buyAccount = BigDecimal.ZERO;
			// 做空
			BigDecimal sellAccount = BigDecimal.ZERO;
			for (VMemberTransaction bqmt1 : userAllTrans) {
				if (bqmt1.getTransaction_code().equals(code)) {
					if (bqmt1.getTrade_type_id() == 1) {
						buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
					} else {
						sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
					}
				}
			}
			
			uav.setBuyAmount(buyAccount);
			uav.setSellAmount(sellAccount);
			
			// 持仓市值
			BigDecimal bgsum = buyAccount.add(sellAccount);
			uav.setTotalAmount(bgsum);
			
			// 做多委托
			BigDecimal deleBuyAccount = vPendingDelegationService.readDelegation(id, code, 1);
			uav.setBuydelegation(deleBuyAccount);
			// 做空委托
			BigDecimal deleSellAccount = vPendingDelegationService.readDelegation(id, code, 2);
			uav.setSelldelegation(deleSellAccount);
			// 委托市值
			BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
			uav.setDelegationAmount(delesum);
			// 当前合约可用
			VAccountContract accountContract = vAccountContractService.readByUidCode(id, code);
			uav.setF_btc(accountContract.getF_btc());
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(uav)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-交割合约资产余额
	 * 
	 */
	@RequestMapping("/center/delivery/contractBalance")
	public String deliveryContractBalance(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 资产详情
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		List<DeliveryTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (list == null) {
			list = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		for (DeliveryTransaction bqTransaction : list) {
			String code = bqTransaction.getTransaction_code();

			// 合约号
			UserAssetsVO uav = new UserAssetsVO();
			uav.setShowName(bqTransaction.getTransaction_show_name());
			uav.setCode(code);
			uav.setTransactionType(bqTransaction.getTransaction_type());
			// 当前合约可用
			DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(id, code);
			uav.setF_btc(accountContract.getF_btc());

			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(uav)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-交割合约资产基本信息
	 * 
	 */
	@RequestMapping("/dCenter/basic")
	public String dCenterBasic(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 基本信息
		Long id = user.getF_user_id();
		BQAccount bqAccount = bqAccountService.readAccount(id, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal freez = dAccountContractService.readLockAmountByUid(id);
		BigDecimal deposit = dMemberTransactionService.readDepositByUid(id);
		BigDecimal availableBtc = dAccountContractService.readAmountByUid(id);
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.readUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		BigDecimal cnyAccount = bqAccount.getBtc().multiply(usdRate);
		BigDecimal account = bqAccount.getBtc();// 我的钱包
		UserContractAccountVo accountVo = new UserContractAccountVo();
		accountVo.setUserId(id);
		accountVo.setAccount(account.doubleValue());// 我的钱包(美元)
		accountVo.setCnyAccount(cnyAccount.doubleValue());// 我的钱包(人民币)
		accountVo.setFreez(freez.doubleValue());
		accountVo.setDeposit(deposit.doubleValue());
		accountVo.setAvailableBtc(availableBtc.doubleValue());
		BigDecimal total = freez.add(deposit).add(availableBtc);
		BigDecimal cnyTotal = total.multiply(usdRate);
		accountVo.setContractTotal(total.doubleValue());
		accountVo.setCnyContractTotal(cnyTotal.doubleValue());
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(accountVo)));

		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-交割合约资产详情
	 * 
	 */
	@RequestMapping("/dCenter/detail")
	public String dCenterDetail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 资产详情
		List<DeliveryTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if(list == null) {
			list = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		Long id = user.getF_user_id();
		JSONArray array = new JSONArray();
		
		List<DeliveryMemberTransaction> userAllTrans = dMemberTransactionService.readBQMemberTrasactionByUserId(user.getF_user_id());
		for (DeliveryTransaction bqTransaction : list) {
			String code = bqTransaction.getTransaction_code();
			
			// 合约号
			UserAssetsVO uav = new UserAssetsVO();
			uav.setShowName(bqTransaction.getTransaction_show_name());
			uav.setCode(code);
			uav.setTransactionType(bqTransaction.getTransaction_type());
			
			// 做多
			BigDecimal buyAccount = BigDecimal.ZERO;
			// 做空
			BigDecimal sellAccount = BigDecimal.ZERO;
			for (DeliveryMemberTransaction bqmt1 : userAllTrans) {
				if (bqmt1.getTransaction_code().equals(code)) {
					if (bqmt1.getTrade_type_id() == 1) {
						buyAccount = buyAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
					} else {
						sellAccount = sellAccount.add(bqmt1.getHold_count().add(bqmt1.getLock_count()));
					}
				}
			}
			
			uav.setBuyAmount(buyAccount);
			uav.setSellAmount(sellAccount);
			
			// 持仓市值
			BigDecimal bgsum = buyAccount.add(sellAccount);
			uav.setTotalAmount(bgsum);
			
			// 做多委托
			BigDecimal deleBuyAccount = dPendingDelegationService.readDelegation(id, code, 1);
			uav.setBuydelegation(deleBuyAccount);
			// 做空委托
			BigDecimal deleSellAccount = dPendingDelegationService.readDelegation(id, code, 2);
			uav.setSelldelegation(deleSellAccount);
			// 委托市值
			BigDecimal delesum = deleBuyAccount.add(deleSellAccount);
			uav.setDelegationAmount(delesum);
			// 当前合约可用
			DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(id, code);
			uav.setF_btc(accountContract.getF_btc());
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(uav)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-合约流水
	 * 
	 */
	@RequestMapping("/money")
	public String money(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<BQAccountAmountLog> records = bqAccountAmountLogService.readByUserAndTime(user.getF_user_id(), nFrom, direct, start,
				end, size);
		for (BQAccountAmountLog record : records) {
			UserAccountAmountLogVo recordVo = new UserAccountAmountLogVo();
			recordVo.setRecordId(record.getLog_id());
			recordVo.setDateline(record.getDateline());
			String currencyName = "";
			List<BQCurrencyType> types = bqCurrencyTypeService.readAll();
			for (BQCurrencyType type : types) {
				if (type.getType_id().equals(record.getCurrency_type_id())) {
					currencyName = type.getType_name();
				}
			}
			recordVo.setCurrencyName(currencyName);
			recordVo.setTitle(record.getTitle());
			recordVo.setDirection(record.getDirection());
			recordVo.setCurrentAmount(record.getCurrent_amount().doubleValue());
			recordVo.setChangeAmount(record.getChange_amount().doubleValue());
			recordVo.setLastestAmount(record.getLastest_amount().doubleValue());
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(recordVo)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-个人基本信息
	 * 
	 */
	@RequestMapping("/basic")
	public String basic(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			String checkToken = checkToken(request, map);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMember user = (BQMember) map.get("user");
			String token = (String) map.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("code", 0);
			userJson.put("msg", "");
			userJson.put("token", token);
			
			Map<String, Object> basicinfoMap = new HashMap<String, Object>();
			
			// 等级
			BQMemberLevel bql = bqMemberLevelService.findById(user.getF_user_level_id());
			String name = bql.getLevel_name();// 级别名称
			basicinfoMap.put("currentLevel", name);
			if (bql.getLevel_id() < bqMemberLevelService.readMaxLevel().getLevel_id()) {
				BQMemberLevel bql2 = bqMemberLevelService.findNextLevelById(user.getF_user_level_id());
				String name2 = bql2.getLevel_name();// 级别名称
				basicinfoMap.put("nextLevel", name2);
				long cha2 = user.getF_user_credits() - bql.getLevel_credits();
				long cha = bql2.getLevel_credits() - bql.getLevel_credits();
				BigDecimal b1 = new BigDecimal(cha2);
				BigDecimal b2 = new BigDecimal(cha);
				
				double rate = b2.compareTo(BigDecimal.ZERO) == 0 ? 0 : b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
				basicinfoMap.put("ratio", rate);
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
				basicinfoMap.put("area", country);
			} else {
				basicinfoMap.put("area", country + "---" + pro + "---" + city);
			}
			basicinfoMap.put("email", user.getF_user_email());
			basicinfoMap.put("realEmail", user.getF_real_email());
			basicinfoMap.put("credits", user.getF_user_credits());// 积分
			basicinfoMap.put("isSubUser", user.getIs_sub_user() == 0 ? false : true);// 是否是子账号
			Gson gson = new Gson();
			userJson.put("data", new JSONObject(gson.toJson(basicinfoMap)));
			model.addAttribute("ticker", userJson);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return apiData(response);
	}
	
	/**
	 * 用户中心-设置指纹或者手势密码登录
	 * 
	 */
	@RequestMapping(value = "/setQuicklogin", method = RequestMethod.POST)
	public String setQuicklogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String password = request.getParameter("password");
		if (StringUtil.isEmpty(password)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		password = super.encryptPwd(password, user.getF_user_salt());
		if (!password.equals(user.getF_user_pwd())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "密码错误");
			model.addAttribute("ticker", tradesStr);	
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-判断绑定手机和谷歌
	 * 
	 */
	@RequestMapping("/checkTelAndGoogle")
	public String checkTelAndGoogle(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		// 手机、谷歌
		Map<String, Object> checkMap = new HashMap<String, Object>();
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
			checkMap.put("tel", 0);
			checkMap.put("google", 0);
		} else if (bqss.getF_user_safetype() == 1) {
			checkMap.put("tel", bqss.getF_user_tel());
			checkMap.put("google", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			checkMap.put("tel", 0);
			checkMap.put("google", 1);
		} else if (bqss.getF_user_safetype() == 3) {
			checkMap.put("tel", bqss.getF_user_tel());
			checkMap.put("google", 1);
		} else {
			checkMap.put("tel", 0);
			checkMap.put("google", 0);
		}
		checkMap.put("email", user.getF_user_email());
		checkMap.put("isSubUser", user.getIs_sub_user() == 0 ? false : true);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(checkMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-判断资金密码
	 * 
	 */
	@RequestMapping("/checkPaypwd")
	public String checkPaypwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		// 交易密码
		Map<String, Object> checkMap = new HashMap<String, Object>();
		BQMemberPayword bqmp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqmp != null) {
			if (!StringUtil.isEmpty(bqmp.getPwd())) {
				checkMap.put("have", 1);
				if (bqmp.getState() == 1) {
					checkMap.put("state", 1);
				} else {
					checkMap.put("state", 0);
				}
			}
		} else {
			checkMap.put("have", 0);
		}
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(checkMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取谷歌所需信息
	 * 
	 */
	@RequestMapping("/getGoogleInfo")
	public String getGoogleInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号绑定谷歌需母账户操作");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		Map<String, Object> googleInfoMap = new HashMap<String, Object>();
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 2 || bqss.getF_user_safetype() == 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已绑定谷歌");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		googleInfoMap.put("type", bqss.getF_user_safetype());
		googleInfoMap.put("email", user.getF_user_email());
		googleInfoMap.put("mobile", bqss.getF_user_tel());
		String secretKey = GoogleAuthenticator.generateSecretKey();
		googleInfoMap.put("secretKey", secretKey);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(googleInfoMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-绑定谷歌
	 * 
	 */
	@RequestMapping(value = "/bindGoogledo", method = RequestMethod.POST)
	public String bindGoogledo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String ip = super.getIpAddr(request);
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号绑定谷歌需母账户操作");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已绑定谷歌");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String code = request.getParameter("code");// 手机验证码
		String google = request.getParameter("google");
		String secretKey = request.getParameter("secretKey");
		String tel = request.getParameter("mobile");
		if (bqs.getF_user_safetype() == 1) {
			if (StringUtil.isEmpty(code)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQTelCode codenum = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (StringUtil.isEmpty(codenum)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!codenum.getF_user_tel().equals(tel)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!codenum.getF_auth_code().equals(code)) {
				
				codenum.setF_error_times(codenum.getF_error_times() + 1);
				if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, codenum);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			String emailCode = request.getParameter("emailCode");// 邮箱验证码
			
			if (StringUtil.isEmpty(emailCode)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
			if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
				if (cacheEmailCode != null) {
					cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
					spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
				}
				
				if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} 
				
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		if (StringUtil.isEmpty(google)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (GoogleAuthenticator.check_code(secretKey, Convert.toLong(google, 0), System.currentTimeMillis())) {
			bqs.setF_user_safegoogle(secretKey);
			if (bqs.getF_user_safetype() == 1) {
				bqs.setF_user_safetype(3);
			} else {
				bqs.setF_user_safetype(2);
			}
			
			bqMemberSafeSettingsService.updateSafe(bqs);

			BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
			informationRecord.setF_user_id(user.getF_user_id());
			informationRecord.setF_old_information("");
			informationRecord.setF_new_information(secretKey);
			informationRecord.setF_information_type(3);
			informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
			informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
			informationRecord.setF_modify_server(2);
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
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		if (bqs.getF_user_safetype() != 1) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 发送手机验证码，成功后存入缓存 还没有绑定手机号码前
	 * 
	 */
	@RequestMapping(value = "/sendTelCodeBeforeBindTel", method = RequestMethod.POST)
	public String sendTelCodeBeforeBindTel(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);

		String tel = request.getParameter("tel");
		if (StringUtil.isEmpty(tel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机号不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (bqMemberSafeSettingsService.readHaveTel(tel)) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		int random = MessageInterface.random6();
		
		String zone = request.getParameter("zone");
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		BQTelCode code = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + key);
		if (code != null) {
			if (code.getF_user_tel().equals(tel)) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + key);
			}
		}
		
		BQMember user = (BQMember) map.get("user");
		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), zone, tel, random);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			code = new BQTelCode();
			code.setF_user_tel(tel);
			code.setF_auth_code(String.valueOf(random));
			spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + key, BTCQMemecacheKeys.TIMES.FIVE_MIN, code);
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, sendRet);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/sliderBindPhone")
	public String sliderBindPhone(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");

		String random = RandomUtil.getTransNo();
		userJson.put("data", random);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-绑定手机操作
	 * 
	 */
	@RequestMapping(value = "/bindPhonedo", method = RequestMethod.POST)
	public String bindPhonedo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String ip = super.getIpAddr(request);
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能绑定手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已绑定手机号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String tel = request.getParameter("tel");
		String code = request.getParameter("code");
		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		String zone = request.getParameter("zone");
		
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		BQTelCode codenum = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + key);
		if (StringUtil.isEmpty(codenum)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!codenum.getF_user_tel().equals(tel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!code.equals(codenum.getF_auth_code())) {
			
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + key);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + key, BTCQMemecacheKeys.TIMES.FIVE_MIN, codenum);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
			if (cacheEmailCode != null) {
				cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			}
			
			if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} 
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + key);
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		
		bqs.setF_user_safequertion(bqs.getF_user_safequertion());
		bqs.setF_user_id(bqs.getF_user_id());
		bqs.setF_user_safeanswer(bqs.getF_user_safeanswer());
		bqs.setF_user_safegoogle(bqs.getF_user_safegoogle());
		if (bqs.getF_user_safetype() == 2) {
			bqs.setF_user_safetype(3);
		} else {
			bqs.setF_user_safetype(1);
		}
		
		bqs.setF_user_tel(tel);
		bqs.setF_user_zone(zone == null ? "" : zone);
		bqMemberSafeSettingsService.updateTel(bqs);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(tel);
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(2);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(2);
		bqMemberModifyInformationRecordService.save(informationRecord);
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
		} catch (Exception e) {
			logger.error("绑定手机号码时，发送邮件失败", e);
		}
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/sendEmailCodeBeforeBindEmail", method = RequestMethod.POST)
	public String sendEmailCodeBeforeBindEmail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);

		String tel = request.getParameter("email");
		if (StringUtil.isEmpty(tel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "邮箱不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember member = bqMemberService.readByRealEmail(tel);
		if (member != null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		int random = MessageInterface.random6();
		BQTelCode code = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (code != null) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		}
		
		code = new BQTelCode();
		code.setF_user_tel(tel);
		code.setF_auth_code(String.valueOf(random));
		
		try {
			SendMail themail = new SendMail();
			themail.send(tel, themail.getApiStr(UrlConfig.getValue("webRoot"), code.getF_auth_code()));
		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送验证码失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.FIVE_MIN, code);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-绑定邮箱操作
	 * 
	 */
	@RequestMapping(value = "/bindEmaildo", method = RequestMethod.POST)
	public String bindEmaildo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String ip = super.getIpAddr(request);
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能绑定邮箱");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(user.getF_real_email())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已绑定邮箱");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() != 1 && bqs.getF_user_safetype() != 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未绑定手机号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String email = request.getParameter("tel");
		String code = request.getParameter("code");
		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQTelCode codenum = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
		if (StringUtil.isEmpty(codenum)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!code.equals(codenum.getF_auth_code())) {
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel(), BTCQMemecacheKeys.TIMES.FIVE_MIN, codenum);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code()) || !cacheEmailCode.getF_user_tel().equals(email)) {
			if (cacheEmailCode != null) {
				cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			}
			
			if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} 
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		bqMemberService.updateEmailByState(user.getF_user_id(), cacheEmailCode.getF_user_tel(), false);
		bqMemberService.updateEmailStatus(user.getF_user_id(), 1);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(email);
		informationRecord.setF_information_type(2);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(2);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(2);
		bqMemberModifyInformationRecordService.save(informationRecord);

		spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.bindEmail.request.content1", Contents.Local.LOCALE.get());
		try {
			themail.send(email, content);
			model.addAttribute("success", SpringContext.getText("msg.bindEmail.request.success", Contents.Local.LOCALE.get()));
		} catch (Exception e) {
			logger.error("绑定邮箱时，发送邮件失败", e);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/sliderChangePhone")
	public String sliderChangePhone(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");

		String random = RandomUtil.getTransNo();
		userJson.put("data", random);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-更改手机操作
	 * 
	 */
	@RequestMapping(value = "/changePhonedo", method = RequestMethod.POST)
	public String changePhonedo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String ip = super.getIpAddr(request);
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能修改手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() != 1 && bqs.getF_user_safetype() != 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未绑定手机号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String tel = bqs.getF_user_tel();
		String code = request.getParameter("code"); // 旧手机验证码
		String newTel = request.getParameter("newTel");// 新手机号
		String newCode = request.getParameter("newCode");// 新手机验证码
		String zone = request.getParameter("zone");
		
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧手机验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQTelCode codenum = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
		if (StringUtil.isEmpty(codenum)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!codenum.getF_user_tel().equals(tel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!code.equals(codenum.getF_auth_code())) {
			
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, codenum);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(newCode)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + newTel;
		BQTelCode codenum2 = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + key);
		if (StringUtil.isEmpty(codenum2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!codenum2.getF_user_tel().equals(newTel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!newCode.equals(codenum2.getF_auth_code())) {
			
			codenum2.setF_error_times(codenum2.getF_error_times() + 1);
			if (codenum2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + key);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + key, BTCQMemecacheKeys.TIMES.FIVE_MIN, codenum2);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + key);
		
		bqs.setF_user_safequertion(bqs.getF_user_safequertion());
		bqs.setF_user_id(bqs.getF_user_id());
		bqs.setF_user_safeanswer(bqs.getF_user_safeanswer());
		bqs.setF_user_safegoogle(bqs.getF_user_safegoogle());
		int type = bqs.getF_user_safetype();
		if (type == 1) {
			bqs.setF_user_safetype(1);
		} else {
			bqs.setF_user_safetype(3);
		}
		bqs.setF_user_tel(newTel);
		bqs.setF_user_zone(zone == null ? "" : zone);
		
		if (user.getF_user_email_status() != 1 || user.getF_user_email().equals(tel)) {
			bqMemberService.updatePhone(bqs);
		} else {
			bqMemberSafeSettingsService.updateTel(bqs);
		}

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information(tel);
		informationRecord.setF_new_information(newTel);
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(2);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(3);
		bqMemberModifyInformationRecordService.save(informationRecord);

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.changePhone.request.content1", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改手机号码时，发送邮件失败", e);
		}
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 用户中心-更改手机操作
	 * 
	 */
	@RequestMapping(value = "/changeEmaildo", method = RequestMethod.POST)
	public String changeEmaildo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String ip = super.getIpAddr(request);
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能修改手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String email = user.getF_real_email();
		String code = request.getParameter("code"); // 旧邮箱验证码
		String newTel = request.getParameter("newTel");// 新邮箱
		String newCode = request.getParameter("newCode");// 新邮箱验证码
		
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧邮箱验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQTelCode codenum = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (StringUtil.isEmpty(codenum)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧邮箱验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		if (!code.equals(codenum.getF_auth_code())) {
			codenum.setF_error_times(codenum.getF_error_times() + 1);
			if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, codenum);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(newCode)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode codenum2 = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (StringUtil.isEmpty(codenum2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新邮箱验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!codenum2.getF_user_tel().equals(newTel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!newCode.equals(codenum2.getF_auth_code())) {
			codenum2.setF_error_times(codenum2.getF_error_times() + 1);
			if (codenum2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, codenum2);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "新邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (user.getF_user_email().equals(email)) {
			bqMemberService.updateEmailByState(user.getF_user_id(), newTel, true);
		} else {
			bqMemberService.updateEmailByState(user.getF_user_id(), newTel, false);
		}

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(user.getF_user_id());
		informationRecord.setF_old_information(email);
		informationRecord.setF_new_information(newTel);
		informationRecord.setF_information_type(2);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
		informationRecord.setF_modify_server(2);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(3);
		bqMemberModifyInformationRecordService.save(informationRecord);

		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email());
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.changeEmail.request.success", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改手机号码时，发送邮件失败", e);
		}
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-发送手机验证码（已绑定手机号）
	 * 
	 */
	@RequestMapping("/sendOldTelCode")
	public String sendOldTelCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() != 1 && bqs.getF_user_safetype() != 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未绑定手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String tel = bqs.getF_user_tel();
		int random = MessageInterface.random6();
		BQTelCode code = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
		if (code != null) {
			if (code.getF_user_tel().equals(tel)) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
			}
		}
		
		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), bqs.getF_user_zone(), tel, random);
		} catch (Exception e) {
			logger.error("绑定手机号码时，发送邮件失败", e);
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			code = new BQTelCode();
			code.setF_user_tel(tel);
			code.setF_auth_code(String.valueOf(random));
			spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.BPI, code);
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, sendRet);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取手机号
	 * 
	 */
	@RequestMapping("/getPhone")
	public String getPhone(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		// 手机号
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(bqms.getF_user_tel())) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "异常，用户手机号为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			userJson.put("data", bqms.getF_user_tel());
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未绑定手机号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-检查谷歌验证码是否正确
	 * 
	 */
	@RequestMapping(value = "/checkGoogleCode", method = RequestMethod.POST)
	public String checkGoogleCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String code = request.getParameter("code");
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，code为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		String secretKey = bqs.getF_user_safegoogle();
		if (!GoogleAuthenticator.check_code(secretKey, Convert.toLong(code, 0), System.currentTimeMillis())) {
			userJson.put("data", false);
		} else {
			userJson.put("data", true);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 检查手机号唯一
	 * 
	 */
	@RequestMapping(value = "/checkUserPhone", method = RequestMethod.POST)
	public String checkUserPhone(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String tel = request.getParameter("tel");
		try {
			if (bqMemberSafeSettingsService.readHaveTel(tel)) {
				userJson.put("data", true);
				model.addAttribute("ticker", userJson);
				return apiData(response);
			} else {
				userJson.put("data", false);
				model.addAttribute("ticker", userJson);
				return apiData(response);
			}
		} catch (Exception e) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
	}

	/**
	 * 检查手机验证码 (验证码未从缓存中移走)
	 * 
	 */
	@RequestMapping(value = "/checkTelCode", method = RequestMethod.POST)
	public String checkTelCode(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String tel = request.getParameter("tel");
		String code = request.getParameter("code");
		BQTelCode code2 = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
		if (code2 == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，code已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!tel.equals(code2.getF_user_tel())) {
			userJson.put("data", false);
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		if (!code.equals(code2.getF_auth_code())) {
			
			code2.setF_error_times(code2.getF_error_times() + 1);
			if (code2.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, code2);
			}
			
			userJson.put("data", false);
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		userJson.put("data", true);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-邀请信息
	 * 
	 */
	@RequestMapping("/invite")
	public String invite(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		Map<String, Object> inviteMap = new HashMap<String, Object>();
		long id = user.getF_user_id();
		try {
			String cid = Security.encryptToHex(String.valueOf(id), Security.des_registerkey);
			inviteMap.put("id", cid.toLowerCase());
		} catch (Exception e) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		inviteMap.put("name", user.getF_user_email());
		inviteMap.put("identity", user.getF_user_identity());

		InviteVO invite = new InviteVO();
		long sumClick = bqMemberInviteAmountService.readSumClick(id);
		long sumSuccess = bqMemberInviteAmountService.readSumSuccess(id);
		BQMemberInviteAmount amount = bqMemberInviteAmountService.readTodayAmount(id, "");
		invite.setSumClick(sumClick);
		invite.setSumSuccess(sumSuccess);
		invite.setTodayClick(amount == null ? 0 : amount.getF_day_click());
		invite.setTodaySuccess(amount == null ? 0 : amount.getF_day_success());

		inviteMap.put("invite", invite);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(inviteMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-邀请返佣
	 * 
	 */
	@RequestMapping("/inviteRebate")
	public String inviteRebate(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		Map<String, Object> inviteMap = new HashMap<String, Object>();
		long userId = user.getF_user_id();

		InviteVO invite = new InviteVO();
		BigDecimal weekRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromWeek(), TimeUtil.ToWeek(), userId, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal monthRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromMonth(), TimeUtil.ToMonth(), userId, Contents.TRADE_CURRENCY_TYPE_ID);
		BigDecimal totalRebate = bqMemberInviteRebateLogService.readSumRebateByUser(userId, Contents.TRADE_CURRENCY_TYPE_ID);
		invite.setWeekRebate(weekRebate.doubleValue());
		invite.setMonthRebate(monthRebate.doubleValue());
		invite.setTotalRebate(totalRebate.doubleValue());
		
		BigDecimal weekBfxRebate = BigDecimal.ZERO;
		BigDecimal monthBfxRebate = BigDecimal.ZERO;
		BigDecimal totalBfxRebate = BigDecimal.ZERO;
		BQBfxFeeSetting bfxSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxSetting == null) {
			bfxSetting = bqBfxFeeSettingService.readOne();
			if (bfxSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxSetting);
			}
		}
		if (bfxSetting != null) {
			weekBfxRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromWeek(), TimeUtil.ToWeek(), userId, bfxSetting.getCoin_id());
			monthBfxRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromMonth(), TimeUtil.ToMonth(), userId, bfxSetting.getCoin_id());
			totalBfxRebate = bqMemberInviteRebateLogService.readSumRebateByUser(userId, bfxSetting.getCoin_id());
		}
		invite.setWeekBfxRebate(weekBfxRebate.doubleValue());
		invite.setMonthBfxRebate(monthBfxRebate.doubleValue());
		invite.setTotalBfxRebate(totalBfxRebate.doubleValue());
		
		int usdrId = bqCurrencyTypeService.getByTypeName("USDR").getType_id();
		BigDecimal weekUsdrRebate = BigDecimal.ZERO;
		BigDecimal monthUsdrRebate = BigDecimal.ZERO;
		BigDecimal totalUsdrRebate = BigDecimal.ZERO;
		weekUsdrRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromWeek(), TimeUtil.ToWeek(), userId, usdrId);
		monthUsdrRebate = bqMemberInviteRebateLogService.readSumRebateByTime(TimeUtil.FromMonth(), TimeUtil.ToMonth(), userId, usdrId);
		totalUsdrRebate = bqMemberInviteRebateLogService.readSumRebateByUser(userId, usdrId);
		invite.setWeekUsdrRebate(weekUsdrRebate.doubleValue());
		invite.setMonthUsdrRebate(monthUsdrRebate.doubleValue());
		invite.setTotalUsdrRebate(totalUsdrRebate.doubleValue());
		
		inviteMap.put("invite", invite);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(inviteMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-邀请成员
	 * 
	 */
	@RequestMapping("/inviteMember")
	public String inviteMember(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<Object[]> list = bqMemberInviteLogService.readByUserAndTime(user.getF_user_id(), nFrom, direct, start, end, size);
		for (Object[] object : list) {
			UserInviteMemberVo vo = new UserInviteMemberVo();
			vo.setUserId(object[0].toString());
			vo.setUserName(object[1].toString());
			vo.setLevel(object[2].toString());
			
			long userId = Convert.toLong(object[0].toString(), 0);
			if (userId > 0) {
				BQMemberLevelBuyRebateReconcilia reconcilia = bqMemberLevelBuyRebateReconciliaService.readByUserId(userId);
				vo.setPoints(reconcilia == null ? "0" : reconcilia.getTotal_bfx_lock().longValue() + "");
			} else {
				vo.setPoints("0");
			}
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(vo)));
		}
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-邀请图片路径
	 * 
	 */
	@RequestMapping("/inviteImage")
	public String inviteImage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String targetFilePath = getFilePath(request, user);
		
		try {
			userJson.put("data", targetFilePath);
		} catch (JSONException e) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取图片失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/getInviteQRCode")
	public String geteInviteQRCode(HttpServletRequest request, HttpServletResponse response, Model model, String filePath) throws Exception{
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		filePath = "";
		
		BQMember user = (BQMember) map.get("user");
		String cid = "";
		try {
			cid = Security.encryptToHex(String.valueOf(user.getF_user_id()), Security.des_registerkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(cid)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "生成二维码失败！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		cid = cid.toLowerCase();
		String baseStorePath = Contents.BASE_UPLOAD_PATH;
		String relativePath = "/file-upload/bitmatrix/s" + cid + ".jpg";
		String iconBasePath = baseStorePath + relativePath;
		File bitMatrixFile = new File(iconBasePath);
		File parentFile = bitMatrixFile.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		
		String basePath = UrlConfig.getValue("webRoot");
		String format = "jpg";
		if (!bitMatrixFile.exists()) {
	        try {
				bitMatrixFile.createNewFile();
				
				String inviteUrl = basePath + "home/s" + cid + ".do";
				int width = 202, height = 202;
				
				Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
				// 内容所使用编码
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				hints.put(EncodeHintType.MARGIN, 0);
				
				BitMatrix bitMatrix = new MultiFormatWriter().encode(inviteUrl, BarcodeFormat.QR_CODE, width, height, hints);
				MatrixToImageWriter.writeToFile(bitMatrix, format, bitMatrixFile);
				
			} catch (WriterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		userJson.put("data", basePath + relativePath);
		
        model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-下载邀请图片
	 * 
	 */
	@RequestMapping("/downLoadInviteImage")
	public String downLoadInviteImage(HttpServletRequest request, HttpServletResponse response, Model model, String filePath) throws Exception{
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		filePath = "";
		
		if (StringUtil.isEmpty(filePath)) {
			filePath = getFilePath(request, (BQMember) map.get("user"));
		}
		
		String storePath = Contents.BASE_UPLOAD_PATH + filePath;
		File file = new File(storePath);
		if (!file.exists()) {
			logger.info("资源文件不存在：" + file);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "资源文件不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
            String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "下载异常，稍后重试");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
		
        model.addAttribute("ticker", userJson);
		return apiData(response);
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
		
		String basePath = UrlConfig.getValue("webRoot");
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
	
	/**
	 * 用户中心-获取用户证书列表
	 * 
	 */
	@RequestMapping("/myCertificate")
	public String myCertificate(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<BQMemberCertificate> list = bqMemberCertificateService.readByUserAndTime(user.getF_user_id(), nFrom, direct, start, end, size);
		for (BQMemberCertificate certificate : list) {
			Map<String,Object> certiMap = new HashMap<String, Object>();
			certiMap.put("recordId", certificate.getId());
			certiMap.put("certificateName", certificate.getCertificate_name());
			certiMap.put("activityName", certificate.getActivity_name());
			certiMap.put("issueTime", certificate.getIssue_time());
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(certiMap)));
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取单个用户证书
	 * 
	 */
	@RequestMapping("/certificateDetail")
	public String certificateDetail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String id = request.getParameter("id");
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		Map<String,Object> certiMap = new HashMap<String, Object>();
		certiMap.put("recordId", certificate.getId());
		certiMap.put("certificateName", certificate.getCertificate_name());
		certiMap.put("activityName", certificate.getActivity_name());
		certiMap.put("issueTime", certificate.getIssue_time());
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(certiMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-检查数据库，用户真实姓名，有，值为真实姓名
	 * 
	 */
	@RequestMapping("/checkRealName")
	public String checkRealName(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		BQMemberValidateInfo validateInfo = bqMemberValidateInfoService.readByUserId(user.getF_user_id());
		if (validateInfo == null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		userJson.put("data", validateInfo.getReal_name());
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-用户提交真实姓名
	 * 
	 */
	@RequestMapping(value = "/doValidate", method = RequestMethod.POST)
	public String doValidate(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		BQMemberValidateInfo info = bqMemberValidateInfoService.readByUserId(user.getF_user_id());
		if (info != null && !StringUtil.isEmpty(info.getReal_name())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已存在用户真实姓名");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String realName = request.getParameter("realName");
		String realName2 = request.getParameter("realName2");
		if (StringUtil.isEmpty(realName) || StringUtil.isEmpty(realName2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，realName或realName2错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!realName.equals(realName2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，realName或realName2错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.validateString(realName) || !StringUtil.validateString(realName2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数包含非法字符");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberValidateInfo validateInfo = new BQMemberValidateInfo();
		
		validateInfo.setReal_name(realName);
		validateInfo.setUser_id(user.getF_user_id());
		validateInfo.setValidate_time(System.currentTimeMillis() / 1000);
		bqMemberValidateInfoService.save(validateInfo);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取证书图片路径
	 * 
	 */
	@RequestMapping("/getCertificateImage")
	public String getCertificateImage(HttpServletRequest request, HttpServletResponse response, Model model, String id)
			throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		
		if (StringUtil.isEmpty(certificate)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "证书不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String storeFolderPath = "file-upload/honourimage/";
		String fileName = "c" + cid + ".png";
		String filePath = storeFolderPath + fileName;
		if (!OSSFileUtil.isFileExist(filePath)) {
			filePath = "";
		} else {
			filePath = OSSFileUtil.getFileUrl(filePath);
		}
		
		try {
			userJson.put("data", filePath);
		} catch (JSONException e) {
			logger.error("获取证书路径异常", e);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取证书路径异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-证书canvas转图片，返回路径
	 * 
	 */
	@RequestMapping("/cvsToImage")
	public String cvsToImage(HttpServletRequest request, HttpServletResponse response, Model model,
			String base64ImgData, String id) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		long cid = ConvertUtil.toLong(id, 0l);
		BQMemberCertificate certificate = bqMemberCertificateService.readById(cid);
		
		if (StringUtil.isEmpty(certificate)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "证书不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

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
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取证书异常");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				filePath = OSSFileUtil.uploadFile(f, filePath);
			} catch (Exception e) {
				logger.error("获取证书路径异常", e);
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取证书路径异常");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		try {
			userJson.put("data", filePath);
		} catch (JSONException e) {
			logger.error("获取证书路径异常", e);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "获取证书路径异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-用户等级信息
	 * 
	 */
	@RequestMapping("/levelInfo")
	public String levelInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		Map<String, Object> levelMap = new HashMap<String, Object>();
		
		// 等级
		BQMemberLevel bql = bqMemberLevelService.readById(user.getF_user_level_id());
		String name = bql.getLevel_name();// 级别名称
		levelMap.put("currentLevel", name);
		levelMap.put("eatRate", bql.getEat_commission_rate().multiply(new BigDecimal(100)));
		levelMap.put("penddingRate", bql.getPending_commission_rate().multiply(new BigDecimal(100)));
		levelMap.put("rebateRate", bql.getBroker_commission_rate().multiply(new BigDecimal(100)));
		
		long credits = user.getF_user_credits();// 积分
		levelMap.put("credits", credits);
		if (bql.getLevel_id() < bqMemberLevelService.readMaxLevel().getLevel_id()) {
			BQMemberLevel bql2 = bqMemberLevelService.findNextLevelById(user.getF_user_level_id());
			String name2 = bql2.getLevel_name();// 级别名称
			levelMap.put("nextLevel", name2);
			levelMap.put("credits2", bql2.getLevel_credits() - credits);
			long cha2 = user.getF_user_credits() - bql.getLevel_credits();
			long cha = bql2.getLevel_credits() - bql.getLevel_credits();
			BigDecimal b1 = new BigDecimal(cha2);
			BigDecimal b2 = new BigDecimal(cha);
			
			double rate = b2.compareTo(BigDecimal.ZERO) == 0 ? 0 : b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
			levelMap.put("ratio", rate);
		}
		
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(levelMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取积分记录列表
	 * 
	 */
	@RequestMapping("/vipRecordList")
	public String vipRecordList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<BQCommonCreditLog> list = bqCommonCreditLogService.readByUserAndTime(user.getF_user_id(), nFrom, direct, start, end, size);
		for (BQCommonCreditLog log : list) {
			Map<String,Object> logMap = new HashMap<String, Object>();
			logMap.put("recordId", log.getCredit_log_id());
			logMap.put("title", log.getTitle());
			logMap.put("recordTime", log.getDateline());
			logMap.put("points", log.getCredits());
			logMap.put("creditType", log.getCredit_type_id());
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(logMap)));
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-检查用户是否有充值和交易
	 * 
	 */
	@RequestMapping("/checkRechargeAndTrade")
	public String checkRechargeAndTrade(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		Map<String, Object> infoMap = new HashMap<String, Object>();
		
		// 判断充值
		long size = bqMemberRechargeService.readRechargeCount(user.getF_user_id());
		if (size > 0) {
			infoMap.put("recharge", 1);
		} else {
			infoMap.put("recharge", 0);
		}
		// 判断交易
		List<BQTransaction> transactionList = spyMemcachedClient.get("transactionList_key");
		if (transactionList == null) {
			transactionList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		for (BQTransaction bqTra : transactionList) {
			List<TradeLog> listtrade = tradeLogService.readAllByUserIdTopN(bqTra.getTransaction_code(), Long.valueOf(user.getF_user_id()).intValue(), 1);
			if (listtrade == null) {
				infoMap.put("trade", 0);
			} else {
				if (listtrade.size() > 0) {
					infoMap.put("trade", 1);
					break;
				} else {
					infoMap.put("trade", 0);
				}
			}
		}
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(infoMap)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据币种id获取币种信息
	 * 
	 */
	@RequestMapping("/getCurrency")
	public String getCurrency(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 获取币种信息
		String coinId = request.getParameter("coinId");
		int iCoinId = Utils.StrToInt(coinId, 1);
		
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(iCoinId);
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(currencyType)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据币种id获取充值地址
	 * 
	 */
	@RequestMapping("/toRecharge")
	public String toRecharge(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 子账号不能充值
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能充值");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 获取币种信息
		String coinId = request.getParameter("coinId");
		String walletType = request.getParameter("walletType");
		
		int iCoinId = Utils.StrToInt(coinId, -1);
		if (iCoinId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(iCoinId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if ("USDT".equalsIgnoreCase(currencyType.getType_name()) && 
				!"ERC20".equalsIgnoreCase(walletType) && !"TRC20".equalsIgnoreCase(walletType)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "充提暂停中");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		boolean isErc20 = (currencyType.getErc20() && (currencyType.getType_name().equalsIgnoreCase("USDT") && "ERC20".equals(walletType)));
		boolean isTrc20 = (currencyType.getTrc20() && (currencyType.getType_name().equalsIgnoreCase("USDT") && "TRC20".equals(walletType)));
		// 获取充值地址
		List<BQMemberWallet> listWallets = null;
		if (isErc20) {
			listWallets = bqMemberWalletService.readErc20AddressByUser(user.getF_user_id());// 获取BTC钱包地址
		} else if (isTrc20) {
			listWallets = bqMemberWalletService.readTrc20AddressByUser(user.getF_user_id());// 获取BTC钱包地址
		} else {
			listWallets = bqMemberWalletService.readById(user.getF_user_id(), currencyType.getType_id());// 获取BTC钱包地址
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
				bqw.setWallet_address_pic(bqb.getWallet_pic());
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
				
				if (bqWalletQueueBtcService.deleteById(bqb.getWallet_id()) > 0) {
					bqMemberWalletService.save(bqw);
				}
			}
		}
		Map<String, Object> infoMap = new HashMap<String, Object>();
		if (bqw != null) {
			infoMap.put("address", bqw.getWallet_address());
			infoMap.put("addressPic", bqw.getWallet_address_pic());
		}
		
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(infoMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-获取充值或提币记录列表
	 * 
	 */
	@RequestMapping("/rechargeOrWithdrawList")
	public String rechargeOrWithdrawList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 子账号不能充值
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能充值");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String coinIdStr = request.getParameter("coinId");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		String side = request.getParameter("side");
		int coinId = Convert.toInt(coinIdStr, -1);
		if (coinId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(coinId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		int operation = -1;
		if ("all".equals(side)) {
			operation = 0;
		} else if ("in".equals(side)) {
			operation = Contents.TRANS_TYPE_CZ;
		} else if ("out".equals(side)) {
			operation = Contents.TRANS_TYPE_TX;
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，未知side");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		JSONArray array = new JSONArray();
		List<BQMemberRecharge> list = bqMemberRechargeService.readByUserAndTime(user.getF_user_id(), coinId, operation, nFrom, direct, start, end, size);
		for (BQMemberRecharge bqMemberRecharge : list) {
			UserRechargeVo vo = new UserRechargeVo();
			vo.setRecordId(bqMemberRecharge.getRecord_id());
			vo.setOperation(bqMemberRecharge.getOperation());
			vo.setCurrencyName(currencyType.getType_name());
			vo.setDateline(bqMemberRecharge.getDateline());
			vo.setAddress(bqMemberRecharge.getReceice_card_no());
			vo.setAmount(bqMemberRecharge.getAmount().doubleValue());
			vo.setRemark(bqMemberRecharge.getRemark());
			vo.setTxid(bqMemberRecharge.getWallet_trade_id());
			if (bqMemberRecharge.getState() == 0) {
				vo.setState(Contents.RECHARGE_STATE_SUBMITTING);
			} else if (bqMemberRecharge.getState() == 1) {
				vo.setState(Contents.RECHARGE_STATE_DONE);
			} else if (bqMemberRecharge.getState() == 2) {
				vo.setState(Contents.RECHARGE_STATE_REFUSE);
			} else if (bqMemberRecharge.getState() == 3) {
				vo.setState(Contents.RECHARGE_STATE_PROCESS);
			} else if (bqMemberRecharge.getState() == 4) {
				vo.setState(Contents.RECHARGE_STATE_MANUAL_PROCESS);
			} else if (bqMemberRecharge.getState() == 5) {
				vo.setState(Contents.RECHARGE_STATE_MANUAL_CHECKED);
			}
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(vo)));
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据币种id获取默认提现地址
	 * 
	 */
	@RequestMapping("/toWithdraw")
	public String toWithdraw(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");	
		userJson.put("token", token);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能提币");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 获取币种信息
		String coinId = request.getParameter("coinId");
		int iCoinId = Utils.StrToInt(coinId, -1);
		if (iCoinId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(iCoinId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 获取默认提现地址
		BQMemberWalletAddress wallAddress = bqMemberWalletAddressService.readDefaultAddress(user.getF_user_id(), currencyType.getType_id());
		if (wallAddress != null) {
			Gson gson = new Gson();
			userJson.put("data", new JSONObject(gson.toJson(wallAddress)));
		} else {
			userJson.put("data", "[]");
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据币种id获取账户
	 * 
	 */
	@RequestMapping("/getAccount")
	public String getAccount(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String coinIdStr = request.getParameter("coinId");
		int coinId = Convert.toInt(coinIdStr, -1);
		if (coinId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(coinId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), currencyType.getType_id());
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(bqa)));
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据币种id获取提现地址列表
	 * 
	 */
	@RequestMapping("/getWithdrawAddressList")
	public String getWithdrawAddressList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能提币");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String coinIdStr = request.getParameter("coinId");
		String walletType = request.getParameter("walletType");
		int coinId = Convert.toInt(coinIdStr, -1);
		if (coinId < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(coinId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数coinId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		// 所有钱包地址列表
		
		boolean erc20usdt = false;
		if (currencyType.getType_name().equalsIgnoreCase("USDT") && "ERC20".equals(walletType)) {
			erc20usdt = true;
		}
		
		boolean trc20usdt = false;
		if (currencyType.getType_name().equalsIgnoreCase("USDT") && "TRC20".equals(walletType)) {
			trc20usdt = true;
		}
		
		JSONArray array = new JSONArray();
		List<BQMemberWalletAddress> list = new ArrayList<BQMemberWalletAddress>();
		if (erc20usdt) {
			list = bqMemberWalletAddressService.readErc20ByUserId(user.getF_user_id(), currencyType.getType_id());
		} else if (trc20usdt) {
			list = bqMemberWalletAddressService.readTrc20ByUserId(user.getF_user_id(), currencyType.getType_id());
		} else {
			list = bqMemberWalletAddressService.readByUserId(user.getF_user_id(), currencyType.getType_id());
		}
		
		for (BQMemberWalletAddress address : list) {
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(address)));
		}
		
		userJson.put("data", array);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-根据id获取提现地址
	 * 
	 */
	@RequestMapping("/getWithdrawAddress")
	public String getWithdrawAddress(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");	
		userJson.put("token", token);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能提币");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 获取币种信息
		String id = request.getParameter("id");
		int iId = Utils.StrToInt(id, 0);
		if (iId <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，参数id");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 获取默认提现地址
		BQMemberWalletAddress wallAddress = bqMemberWalletAddressService.readById(iId);
		if (wallAddress != null) {
			Gson gson = new Gson();
			userJson.put("data", new JSONObject(gson.toJson(wallAddress)));
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "id错误，未找到");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-设置和修改提现地址
	 * 
	 */
	@RequestMapping(value = "/addOrUpdateAddress", method = RequestMethod.POST)
	public String addOrUpdateAddress(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能提币");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String address = request.getParameter("address");
		String currencyId = request.getParameter("coinId");
		String label = request.getParameter("label");
		String walletType = request.getParameter("walletType");
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		
		int currency = Convert.toInt(currencyId, 0);
		if (StringUtil.isEmpty(address) || StringUtil.isEmpty(label) || StringUtil.isEmpty(currencyId) || currency <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "地址或标签或币种不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		String tel = "";
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			tel = bqs.getF_user_tel();
			BQTelCode tecode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (tecode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, tecode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		if (!StringUtil.validateString(address) || !StringUtil.validateString(label)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "请勿输入特殊符号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(currency);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "未找到该币种！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		int count = bqMemberWalletAddressService.readCountByUserAndAddress(user.getF_user_id(), address, currency);
		if (count > 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "请勿重复添加！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		count = bqMemberWalletAddressService.readCountByUserAndCurrency(user.getF_user_id(), currency);
		if (count >= 20) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "请删除一些已有地址后继续操作");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		boolean erc20usdt = false;
		if ("USDT".equalsIgnoreCase(currencyType.getType_name()) && "ERC20".equalsIgnoreCase(walletType)) {
			erc20usdt = true;
		}
		
		boolean trc20usdt = false;
		if ("USDT".equalsIgnoreCase(currencyType.getType_name()) && "TRC20".equalsIgnoreCase(walletType)) {
			trc20usdt = true;
		}

		BQMemberWalletAddress bqwa = new BQMemberWalletAddress();
		bqwa.setF_user_id(user.getF_user_id());
		bqwa.setCurrency_type_id(currency);
		bqwa.setWallet_address(address);
		bqwa.setName(label);
		bqwa.setIs_default(0);
		bqwa.setErc20(erc20usdt);
		bqwa.setTrc20(trc20usdt);
		
		bqMemberWalletAddressService.save(bqwa);
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-删除提现地址
	 * 
	 */
	@RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
	public String deleteAddress(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		// 子账号不能提币
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号不能提币");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String id = request.getParameter("id");
		BQMemberWalletAddress bwa = bqMemberWalletAddressService.readById(Long.parseLong(id));
		if (bwa == null || bwa.getF_user_id() != user.getF_user_id()) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "删除提现地址失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else {
			bqMemberWalletAddressService.deleteById(Long.parseLong(id));
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-设置默认提现地址
	 * 
	 */
	@RequestMapping(value = "/defaultAddress", method = RequestMethod.POST)
	public String defaultAddress(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String id = request.getParameter("id");
		String coidId = request.getParameter("coinId");
		int currencyId = Convert.toInt(coidId, 0);
		if (currencyId <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，currencyId错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long lid = ConvertUtil.toLong(id, 0l);
		if (lid <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		bqMemberWalletAddressService.defaultAddr(user.getF_user_id(), lid, currencyId);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	// 检查单日提币限额
	@RequestMapping("/checkDayMaxWithdraw")
	public String checkDayMaxWithdraw(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		int currencyId = ConvertUtil.toInt(request.getParameter("currencyId"), 0);
		if (currencyId <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "币种ID错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currencyType == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "币种ID错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long startTime = TimeUtil.getTimesmorning();
		long endTime = startTime + 86400l;
		BigDecimal sumWithdraw = bqMemberRechargeService.getSumBetweenTime(user.getF_user_id(), currencyId, startTime, endTime);
		
		userJson.put("sumWithdraw", sumWithdraw.doubleValue());
		userJson.put("dayMaxWithdraw", currencyType.getDay_max_widthdraw().doubleValue());
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-提现操作
	 * 
	 */
	@RequestMapping(value = "/withdrawDo", method = RequestMethod.POST)
	public String withdrawDo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BigDecimal bgAmount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0))
				.setScale(5, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN);
		if (bgAmount.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提币数量必须大于零");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String address = request.getParameter("address");
		String paypass = request.getParameter("password");
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String walletType = request.getParameter("walletType");
		int currencyId = Convert.toInt(request.getParameter("coinId"), 0);
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currency == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，coinId错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if ("USDT".equalsIgnoreCase(currency.getType_name()) && 
				!"ERC20".equalsIgnoreCase(walletType) && !"TRC20".equalsIgnoreCase(walletType)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "充提暂停中");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
				
		if (bgAmount.compareTo(currency.getMin_withdraw()) < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "该币种最小提币数量：" + currency.getMin_withdraw().doubleValue());
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long startTime = TimeUtil.getTimesmorning();
		long endTime = startTime + 86400l;
		BigDecimal sumWithdraw = bqMemberRechargeService.getSumBetweenTime(user.getF_user_id(), currencyId, startTime, endTime);
		BigDecimal totalAmount = sumWithdraw.add(bgAmount);
		if (totalAmount.compareTo(currency.getDay_max_widthdraw()) > 0) {
			BigDecimal subtractAmount = currency.getDay_max_widthdraw().subtract(sumWithdraw);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "单日限额：" + currency.getDay_max_widthdraw().doubleValue() + "，您还可提币数量为：" + subtractAmount.doubleValue());
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		BQAccount bqAccount = bqAccountService.readAccount(user.getF_user_id(), currencyId);

		String tel = "";
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			tel = bqs.getF_user_tel();
			BQTelCode tecode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (tecode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, tecode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		if (bgAmount.compareTo(BigDecimal.ZERO) < 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提币数量错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		
		boolean erc20usdt = false;
		if ("USDT".equalsIgnoreCase(currency.getType_name()) && "ERC20".equalsIgnoreCase(walletType)) {
			erc20usdt = true;
		}
		
		boolean trc20usdt = false;
		if ("USDT".equalsIgnoreCase(currency.getType_name()) && "TRC20".equalsIgnoreCase(walletType)) {
			trc20usdt = true;
		}
		
		if (address == null || address.equals("")) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提币地址不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (erc20usdt || currency.getType_name().equalsIgnoreCase("eth")) {
			if (address.length() != 42 || !address.toLowerCase().startsWith("0x")) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提币地址格式错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else if (trc20usdt) {
			if (address.length() != 34 || !address.startsWith("T")) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "提币地址格式错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		if (bqp != null && bqp.getState() != 0) {
			if (paypass == null || paypass.equals("")) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			try {
				paypass = MD5Service.encryptString(paypass).toLowerCase() + bqp.getSalt().toLowerCase();
				paypass = MD5Service.encryptString(paypass).toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 比较交易密码（交易密码放到最前面，有利于用户余额信息不外泄）
			if (!paypass.equals(bqp.getPwd())) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

		}
		// 比较金额是否符合
		if (bqAccount.getBtc().compareTo(bgAmount) == -1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_INSUFFICIENTBALANCE, "余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 手续费
		BigDecimal curFee = currency.getWithdraw_fee();
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
			logger.error("用户提币处理异常", e);
		}
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-申请api
	 * 
	 */
	@RequestMapping(value = "/doBindAppKey", method = RequestMethod.POST)
	public String doBindAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(user.getF_user_id());
		if (bqUserAppKey != null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "已申请api");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String tel = request.getParameter("tel");
		String code = request.getParameter("code");
		BQTelCode bqTelCode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms.getF_user_safetype() != 1 && bqms.getF_user_safetype() != 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "未绑定手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(bqms.getF_user_tel())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "未绑定手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (bqTelCode == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "验证码过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!bqTelCode.getF_user_tel().equals(tel)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!bqTelCode.getF_auth_code().equals(code)) {
			
			bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
			if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
			} else {
				spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqTelCode);
			}
			
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String paypwd = request.getParameter("paypwd");
		if (StringUtil.isEmpty(paypwd)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqp == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未设置资金密码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		try {
			paypwd = MD5Service.encryptString(paypwd).toLowerCase() + bqp.getSalt().toLowerCase();
			paypwd = MD5Service.encryptString(paypwd).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 比较交易密码
		if (!paypwd.equals(bqp.getPwd())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String allowIp = request.getParameter("allowIp");
		if (!StringUtil.isEmpty(allowIp)) {
			// 验证IP格式
			String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
					+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
					+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
			if (!allowIp.matches(regex)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "formatNotMatch");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			// API最多只能绑定10个IP
			String[] ipArray = allowIp.split(",");
			if (ipArray.length > 10) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "最多绑定10个IP");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
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
		spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-删除API
	 * 
	 */
	@RequestMapping(value = "/deleteAppKey", method = RequestMethod.POST)
	public String deleteAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(user.getF_user_id());
		if (bqUserAppKey == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "该api已不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		bqUserAppKeyService.delete(user.getF_user_id());
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 母账户操作子账号申请api
	 * 
	 */
	@RequestMapping(value = "/subAccount/bindAppKey", method = RequestMethod.POST)
	public String doSubBindAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember motherUser = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账户id错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMember subUser = bqMemberService.findUserById(subUid);
		if (subUser == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey != null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "子账号已申请api");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String code = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String tel = "";
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqms.getF_user_safetype() == 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "未绑定谷歌和手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			tel = bqms.getF_user_tel();
			if (StringUtil.isEmpty(tel)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "手机验号为空,请联系客服");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (StringUtil.isEmpty(code)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQTelCode bqTelCode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			
			if (bqTelCode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "验证码过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqTelCode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		// 判断谷歌验证码
		if (bqms.getF_user_safetype() == 2 || bqms.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(googleCode)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqms.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

		}
		
		String allowIp = request.getParameter("allowIp");
		if (StringUtil.isEmpty(allowIp)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，allowIp错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 验证IP格式
		String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
				+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
				+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
		if (!allowIp.matches(regex)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "ip地址格式不正确");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		// API最多只能绑定10个IP
		String[] ipArray = allowIp.split(",");
		if (ipArray.length > 10) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "最多绑定10个IP");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey userAppKey = new BQUserAppKey();
		userAppKey.setF_user_id(subUser.getF_user_id());
		userAppKey.setAllow_ip(allowIp);
		userAppKey.setApp_key(UUID.randomUUID().toString().replaceAll("-", ""));
		userAppKey.setApp_secret(UUID.randomUUID().toString().replaceAll("-", ""));
		long overTime = 0;
		if (StringUtil.isEmpty(allowIp)) overTime = System.currentTimeMillis() / 1000 + 90 * 24 * 3600;
		userAppKey.setOver_time(overTime);
		userAppKey.setState(1);
		userAppKey.setUpdate_time(System.currentTimeMillis() / 1000);
		
		bqUserAppKeyService.save(userAppKey);
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 母账户操作子账号删除api
	 * 
	 */
	@RequestMapping("/subAccount/deleteAppKey")
	public String deleteSubAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember motherUser = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账户id错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMember subUser = bqMemberService.findUserById(subUid);
		if (subUser == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "appkey已删除");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		bqUserAppKeyService.delete(subUid);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 母账户操作子账号更新api的ip地址
	 * 
	 */
	@RequestMapping(value = "/subAccount/updateAppKey", method = RequestMethod.POST)
	public String updateSubAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember motherUser = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账户id错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMember subUser = bqMemberService.findUserById(subUid);
		if (subUser == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "appkey已不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String code = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String tel = "";
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqms.getF_user_safetype() == 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "未绑定谷歌和手机");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			tel = bqms.getF_user_tel();
			if (StringUtil.isEmpty(tel)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "手机验号为空,请联系客服");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (StringUtil.isEmpty(code)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQTelCode bqTelCode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			
			if (bqTelCode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "验证码过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqTelCode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		// 判断谷歌验证码
		if (bqms.getF_user_safetype() == 2 || bqms.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(googleCode)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqms.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

		}
		
		String allowIp = request.getParameter("allowIp");
		if (StringUtil.isEmpty(allowIp)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，allowIp错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 验证IP格式
		String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
				+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
				+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
		if (!allowIp.matches(regex)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "ip地址格式不正确");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		// API最多只能绑定10个IP
		String[] ipArray = allowIp.split(",");
		if (ipArray.length > 10) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "最多绑定10个IP");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		bqUserAppKey.setAllow_ip(allowIp);
		bqUserAppKeyService.update(bqUserAppKey);
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 修改登录密码提交
	 * 
	 */
	@RequestMapping(value = "/changeLoginPwddo", method = RequestMethod.POST)
	public String changeLoginPwddo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String password = request.getParameter("password"); // 旧密码
		String newpassword = request.getParameter("newpwd1"); // 新密码
		String newpassword2 = request.getParameter("newpwd2"); // 确认新密码
		String code = request.getParameter("code"); // 手机验证码（若绑定了手机）

		String tel = "";
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(code)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			tel = bqss.getF_user_tel();
			BQTelCode bqTelCode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (bqTelCode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqTelCode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		if (StringUtil.isEmpty(password) || StringUtil.isEmpty(newpassword) || StringUtil.isEmpty(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "密码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!newpassword.equals(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "两次密码不相等");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		try {
			password = MD5Service.encryptString(password).toLowerCase();
			password = MD5Service.encryptString(password + user.getF_user_salt()).toLowerCase();
			if (!password.equals(user.getF_user_pwd())) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "原密码不正确");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase();
			newpassword1 = MD5Service.encryptString(newpassword1 + user.getF_user_salt()).toLowerCase();

			user.setF_user_pwd(newpassword);
			bqMemberService.updatepassword(user.getF_user_id(), newpassword1);

		} catch (Exception e1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "提交异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}

		try {
			SendMail themail = new SendMail();
			String content = SpringContext.getText("msg.password.request.content1", Contents.Local.LOCALE.get());
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改密码时，发送邮件异常：", e);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 设置资金密码或找回资金密码
	 * 
	 */
	@RequestMapping(value = "/tradePwddo", method = RequestMethod.POST)
	public String tradePwddo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String newpassword1 = request.getParameter("newpwd1");
		String newpassword2 = request.getParameter("newpwd2");
		String code = request.getParameter("code"); // 手机验证码 （绑定手机时需要）
		String googleCode = request.getParameter("google"); // 谷歌验证码 （绑定谷歌时需要）

		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());// 得到交易密码表
		if (bqp == null) {
			bqp = new BQMemberPayword();
		}
		
		String tel = "";
		// 判断是否绑定手机
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(code)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			tel = bqss.getF_user_tel();
			BQTelCode bqTelCode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (bqTelCode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, bqTelCode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		// 判断谷歌验证码
		if (bqss.getF_user_safetype() == 2 || bqss.getF_user_safetype() == 3) {
			if (StringUtil.isEmpty(googleCode)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqss.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		if (StringUtil.isEmpty(newpassword1) || StringUtil.isEmpty(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!newpassword1.equals(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "两次密码不相等");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String random6 = RandomUtil.getRandomString(6);
		String newpassword = MD5Service.encryptString(newpassword1).toLowerCase() + random6.toLowerCase();
		newpassword = MD5Service.encryptString(newpassword).toLowerCase();

		bqp.setF_user_id(user.getF_user_id());
		bqp.setPwd(newpassword);
		bqp.setSalt(random6);
		bqp.setState(0);
		bqp.setF_readszms(0);
		bqMemberPaywordService.save(bqp);
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}

		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.password.request.content2", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改密码时，发送邮件异常：", e);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "发送邮件异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 修改资金密码提交
	 * 
	 */
	@RequestMapping(value = "/changeTradePwddo", method = RequestMethod.POST)
	public String changeTradePwddo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String password = request.getParameter("password"); // 旧密码
		String newpassword = request.getParameter("newpwd1"); // 新密码
		String newpassword2 = request.getParameter("newpwd2"); // 确认新密码
		if (StringUtil.isEmpty(password) || StringUtil.isEmpty(newpassword) || StringUtil.isEmpty(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "密码不能有空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (!newpassword.equals(newpassword2)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "两次输入密码不相等");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		BQMemberPayword bqmP = bqMemberPaywordService.readById(user.getF_user_id());
		try {
			password = MD5Service.encryptString(password).toLowerCase() + bqmP.getSalt().toLowerCase();
			password = MD5Service.encryptString(password).toLowerCase();

			if (!password.equals(bqmP.getPwd())) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "旧密码不正确");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} catch (Exception e1) {
			logger.error("修改密码异常：", e1);
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "提交异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase() + bqmP.getSalt().toLowerCase();
			newpassword1 = MD5Service.encryptString(newpassword1).toLowerCase();
			bqmP.setPwd(newpassword1);
			bqMemberPaywordService.save(bqmP);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			SendMail themail = new SendMail();
			String content = SpringContext.getText("msg.password.request.content2", Contents.Local.LOCALE.get());
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改密码时，发送邮件异常：", e);
		}

		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户设置使用资金密码（修改state）
	 * 
	 */
	@RequestMapping(value = "/changeTradeInput", method = RequestMethod.POST)
	public String changeTradeInput(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BQMemberPayword bqmP = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqmP == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未设置过资金密码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		bqmP.setState(1);
		bqMemberPaywordService.save(bqmP);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户设置不使用资金密码（修改state，提交谷歌或者手机相关验证，以及密码）
	 * 
	 */
	@RequestMapping(value = "/changeTradeNotInput", method = RequestMethod.POST)
	public String changeTradeNotInput(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String paypass = request.getParameter("password");

		String tel = "";
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (StringUtil.isEmpty(telCode)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			tel = bqs.getF_user_tel();
			BQTelCode tecode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + tel);
			if (tecode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + tel, BTCQMemecacheKeys.TIMES.FIVE_MIN, tecode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (StringUtil.isEmpty(googleCode)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqp == null || StringUtil.isEmpty(bqp.getPwd())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未设置过资金密码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(paypass)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		try {
			paypass = MD5Service.encryptString(paypass).toLowerCase() + bqp.getSalt().toLowerCase();
			paypass = MD5Service.encryptString(paypass).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 比较交易密码（交易密码放到最前面，有利于用户余额信息不外泄）
		if (!paypass.equals(bqp.getPwd())) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		bqp.setState(0);
		bqMemberPaywordService.save(bqp);
		
		if (!StringUtil.isEmpty(tel)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + tel);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/sliderFindPwd")
	public String sliderFindPwd(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");

		String random = RandomUtil.getTransNo();
		userJson.put("data", random);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 找回登录密码(用邮箱验证码来验证)
	 * 
	 */
	@RequestMapping(value = "/findLoginPwddo", method = RequestMethod.POST)
	public String findLoginPwddo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		String newpassword = request.getParameter("newpwd1"); // 新密码
		String newpassword2 = request.getParameter("newpwd2"); // 确认新密码
		
		if (StringUtil.isEmpty(email)){
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(newpassword) || StringUtil.isEmpty(newpassword2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "密码不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (StringUtil.isEmpty(cacheEmailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() >= 5) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!emailCode.equals(cacheEmailCode.getF_auth_code())) {
			cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!newpassword.equals(newpassword2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "两次输入密码不相等");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = bqMemberService.readCheckUserEmail(email);
		if (user == null) {
			if (email.matches("^1[0-9]{10}$")) { 
				BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(email);
				if (setting !=null) {
					user = bqMemberService.readUserById(setting.getF_user_id());
				}
			} else {
				user = bqMemberService.readByRealEmail(email);
			}
		}
		
		if (user == null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase() + user.getF_user_salt();
			newpassword1 = MD5Service.encryptString(newpassword1).toLowerCase();
			bqMemberService.updatepassword(user.getF_user_id(), newpassword1);
		} catch (Exception e) {
			logger.error("用户更换新密码异常：", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 找回密码，发送邮箱验证码，成功后存入缓存
	 * 
	 */
	@RequestMapping(value = "/sendEmailCode", method = RequestMethod.POST)
	public String sendEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");

		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号不能发送邮箱/手机验证码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (!StringUtil.isEmpty(cacheEmailCode)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		
		boolean isEmail = email.equals(user.getF_real_email());
		if (isEmail) {
			try {
				SendMail themail = new SendMail();
				themail.send(user.getF_real_email(), themail.getApiStr(com.btc100.btc_q.util.Utils.getWebRoot(request), code));
			} catch (Exception e) {
				logger.error("发送邮箱验证码失败", e);
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送邮箱/手机验证码失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(email);
			String sendRet = "error";
			try {
				sendRet = sendTelCode(Convert.toLong(email, 0), setting.getF_user_zone(), email, random6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!StringUtil.isEmpty(sendRet)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送邮箱/手机验证码失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		cacheEmailCode = new BQTelCode();
		cacheEmailCode.setF_auth_code(code);
		cacheEmailCode.setF_error_times(0);
		cacheEmailCode.setF_user_tel(user.getF_real_email());
		
		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	public BQMember getUserByTelOrEmail(String emailOrTel) {
		BQMember user = bqMemberService.readCheckUserEmail(emailOrTel);
		if (user != null) return user;
		
		if (emailOrTel.matches("^1[0-9]{10}$")){
			String tel = emailOrTel;
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(tel);
			if (setting !=null) {
				user = bqMemberService.readUserById(setting.getF_user_id());
				return user;
			}
		} else {
			user = bqMemberService.readByRealEmail(emailOrTel);
			return user;
		}
		
		return null;
	}
	
	/**
	 * 离线验证邮箱验证码（根据email参数），不移除缓存
	 * 
	 */
	@RequestMapping(value = "/checkEmailCode", method = RequestMethod.POST)
	public String checkEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = bqMemberService.readByRealEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "用户未注册");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (StringUtil.isEmpty(cacheEmailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() >= 5) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!cacheEmailCode.getF_auth_code().equals(emailCode)) {
			cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 在线发送邮箱验证码（根据token），成功后存入缓存
	 * 
	 */
	@RequestMapping(value = "/sendOnlineEmailCode", method = RequestMethod.POST)
	public String sendOnlineEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号不能发送邮箱验证码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱验证码错误次数过多");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送邮箱验证码失败，请稍候重试");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/sendOldEmailCode", method = RequestMethod.POST)
	public String sendOldEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getIs_sub_user() == 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号不能发送邮箱验证码");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(user.getF_real_email())) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "尚未绑定邮箱");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email());
		if (!StringUtil.isEmpty(cacheEmailCode)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email());
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		try {
			SendMail themail = new SendMail();
			themail.send(user.getF_real_email(), themail.getApiStr(UrlConfig.getValue("webRoot"), code));
		} catch (Exception e) {
			logger.error("发送邮箱验证码失败", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送邮箱验证码失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		cacheEmailCode = new BQTelCode();
		cacheEmailCode.setF_auth_code(code);
		cacheEmailCode.setF_error_times(0);
		cacheEmailCode.setF_user_tel(user.getF_real_email());
		
		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_real_email(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 在线验证邮箱验证码（根据token参数），不移除缓存
	 * 
	 */
	@RequestMapping(value = "/checkOnlineEmailCode", method = RequestMethod.POST)
	public String checkOnlineEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (StringUtil.isEmpty(cacheEmailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱验证码错误次数过多");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!cacheEmailCode.getF_auth_code().equals(emailCode)) {
			cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/getFeeRateType")
	public String getFeeRateType(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		Map<String, Object> basicinfoMap = new HashMap<String, Object>();
		basicinfoMap.put("feeRateType", user.getF_fee_rate_type());// bfx抵扣手续费(0:否,1:是)
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(basicinfoMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value="changeFeeRateType", method = RequestMethod.POST)
	public String changeFeeRateType(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String typeStr =  request.getParameter("type");
		if (StringUtil.isEmpty(typeStr)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "type参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		int type = Convert.toInt(typeStr, 0);
		if (type != 0 && type != 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "type参数不为0也不为1");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (type != user.getF_fee_rate_type()) {
			bqMemberService.updateFeeRateType(user.getF_user_id(), type);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户中心-查询可以购买的VIP最大等级
	 * 
	 */
//	@RequestMapping("/getMaxLevelForLevelBuyAmount")
//	public String getMaxLevelForLevelBuyAmount(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
//		JSONObject tickerJson = new JSONObject();
//		tickerJson.put("code", 0);
//		tickerJson.put("msg", "");
//		
//		Map<String, Object> vipTradeBuyMap = new HashMap<String, Object>();
//		
//		BQMemberLevel levelMax = bqMemberLevelService.getMaxLevelForLevelBuyAmount();
//		String levelMaxName = levelMax.getLevel_name();
//		long levelMaxID = levelMax.getLevel_id();
//		vipTradeBuyMap.put("levelMaxName", levelMaxName);
//		vipTradeBuyMap.put("levelMaxID", levelMaxID);
//		
//		Gson gson = new Gson();
//		tickerJson.put("data", new JSONObject(gson.toJson(vipTradeBuyMap)));
//		model.addAttribute("ticker", tickerJson);
//
//		return apiData(response);
//	}
	
	/**
	 * 用户中心-根据用户uid获取该用户可以购买的VIP等级
	 * 
	 */
	@RequestMapping("/getVipTradeBuy")
	public String getVipTradeBuy(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");	
		userJson.put("token", token);
		
		BQMemberLevel userLevel = bqMemberLevelService.readById(user.getF_user_level_id());
		String userLevelName = userLevel.getLevel_name();// 级别名称
		long userLevelID = userLevel.getLevel_id();// 级别名称ID
		
		BQMemberLevel levelMax = bqMemberLevelService.readMaxLevelForLevelBuyAmount();
		String levelMaxName = levelMax.getLevel_name();
		long levelMaxID = levelMax.getLevel_id();
		
		BQMemberLevel userLevelBuyAmount = bqMemberLevelService.findLevelBuyAmountById(userLevelID);
		

		
		BQMemberLevelBuySetting buySetting = bqMemberLevelBuySettingService.readById();
		
		Map<String, Object> vipTradeBuyMap = new HashMap<String, Object>();
		BQBfxFeeSetting setting = bqBfxFeeSettingService.readOne();
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(setting.getCoin_id());
		vipTradeBuyMap.put("coinId", currencyType.getType_id());
		vipTradeBuyMap.put("coinName", currencyType.getType_name());
		vipTradeBuyMap.put("userLevelName", userLevelName);
		vipTradeBuyMap.put("userLevelID", userLevelID);
		vipTradeBuyMap.put("levelMaxName", levelMaxName);
		vipTradeBuyMap.put("levelMaxID", levelMaxID);
		vipTradeBuyMap.put("backPrice", buySetting.getBack_price());
		
		List<BQMemberLevel> vipLevelsUserCanBuyList = new ArrayList<BQMemberLevel>();
		
		if (userLevelID < levelMaxID) {
			List<BQMemberLevel> levelRules = bqMemberLevelService.findAll();// 等级规则制度
			for (BQMemberLevel l : levelRules) {
				BQMemberLevel bqml = new BQMemberLevel();
				if (l.getLevel_buy_amount().compareTo(BigDecimal.ZERO) > 0 && userLevelID < l.getLevel_id()) {
					
					
					bqml.setLevel_buy_amount(l.getLevel_buy_amount().subtract(userLevelBuyAmount.getLevel_buy_amount()));
					
					bqml.setLevel_id(l.getLevel_id());
					bqml.setLevel_name(l.getLevel_name());
					bqml.setState(l.getState());
					
					bqml.setBroker_commission_rate(buySetting == null ? BigDecimal.ZERO : buySetting.getExchange_rate());
					bqml.setDelegation_tel(l.getDelegation_tel());
					bqml.setLevel_credits(l.getLevel_credits());
					
					vipLevelsUserCanBuyList.add(bqml);
				}
				
			}
		}
		vipTradeBuyMap.put("vipLevels", vipLevelsUserCanBuyList);

		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(vipTradeBuyMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 用户中心-购买VIP等级操作
	 * 
	 */
	@RequestMapping(value = "/vipTradeBuyDo", method = RequestMethod.POST)
	public String vipTradeBuyDo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		if (user.getF_user_oms() != 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "您不能购买积分等级");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");

		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQTelCode tecode = (BQTelCode) spyMemcachedClient.get(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
			if (tecode == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				
				tecode.setF_error_times(tecode.getF_error_times() + 1);
				if (tecode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
				} else {
					spyMemcachedClient.set(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel(), BTCQMemecacheKeys.TIMES.FIVE_MIN, tecode);
				}
				
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "手机验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码不能为空");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "谷歌验证码错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		if (bqs.getF_user_safetype() != 1 && bqs.getF_user_safetype() != 2 && bqs.getF_user_safetype() != 3) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "对不起  您尚未绑定手机或者谷歌验证，为了您的账户安全请前去绑定!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

//		String paypass = request.getParameter("password");
//		BQMemberPayword bqp = bqMemberPaywordService.findById(user.getF_user_id());

//		if (bqp != null && bqp.getState() != 0) {
//			if (paypass == null || paypass.equals("")) {
//				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码不能为空");
//				model.addAttribute("ticker", tradesStr);
//				return apiData(response);
//			}
//			try {
//				paypass = MD5Service.encryptString(paypass).toLowerCase() + bqp.getSalt().toLowerCase();
//				paypass = MD5Service.encryptString(paypass).toLowerCase();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			// 比较交易密码（交易密码放到最前面，有利于用户余额信息不外泄）
//			if (!paypass.equals(bqp.getPwd())) {
//				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "资金密码错误");
//				model.addAttribute("ticker", tradesStr);
//				return apiData(response);
//			}
//		}
		
		
		long select_level_id = ConvertUtil.toLong(request.getParameter("select_level_id"), (long) 0);
		
		if (select_level_id <= 0) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "购买VIP等级不正确");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (user.getF_user_state() == 2) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "该账户已被冻结,请联系客服！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		//检查用户上传上来想要购买的等级，跟目前已有等级是否符合标准，最高可买VIP等级不能再购买低等级
		
		BQMemberLevel bql = bqMemberLevelService.readById(user.getF_user_level_id());
		long nameID = bql.getLevel_id();// 用户级别名称ID
		long nameIDMax = bqMemberLevelService.readMaxLevelForLevelBuyAmount().getLevel_id();
		
		if (nameID >= nameIDMax) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "您已经是最高可买VIP等级，不能购买");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (select_level_id <= nameID) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "不能购买比自己低或相等的VIP等级");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
			String tradesStr = createJwtCode(token, Contents.API_ERROR_INSUFFICIENTBALANCE, "可用余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQAccount bqa = bqAccountService.readAccount(user.getF_user_id(), paySetting.getPay_coin_id());
		BigDecimal btc = bqa.getBtc();
		
		if (btc.compareTo(total) == -1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_INSUFFICIENTBALANCE, "可用余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BigDecimal bqsu = btc.subtract(total);
		if (bqsu.compareTo(BigDecimal.ZERO) >= 0) {
			
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(paySetting.getPay_coin_id());

//			String title = SpringContext.getText("msg.memberLevelBuyAmount.title1", Contents.Local.LOCALE.get()) + bql.getLevel_name() + SpringContext.getText("msg.memberLevelBuyAmount.title2", Contents.Local.LOCALE.get()) + levelBuyAmountSelect.getLevel_name() + SpringContext.getText("msg.memberLevelBuyAmount.title3", Contents.Local.LOCALE.get()) + total.doubleValue() + SpringContext.getText("msg.pg.trade.ge", Contents.Local.LOCALE.get()) + currencyType.getType_name();
			String title = "购买VIP等级:由" + bql.getLevel_name() + "升级到" + levelBuyAmountSelect.getLevel_name() + ",扣除" + total.doubleValue() + "个" + currencyType.getType_name();
			
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
			bqMemberLevelBuyAmountLogService.updateAccountAndLog(bqmlba, title, paySetting);
		} else {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_INSUFFICIENTBALANCE, "可用余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(bqs.getF_user_tel())) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.TEL_CODE + bqs.getF_user_tel());
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	
	/**
	 * 用户中心-获取购买VIP等级记录列表
	 * 
	 */
	@RequestMapping("/vipTradeBuyRecordList")
	public String vipTradeBuyRecordList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		userJson.put("token", token);
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		
		List<BQMemberLevelBuyAmountLog> list = bqMemberLevelBuyAmountLogService.readLevelBuyAmountLog(user.getF_user_id(), nFrom, direct, start, end, size);
		JSONArray array = new JSONArray();
		if (list != null) {
			for (BQMemberLevelBuyAmountLog log : list) {
				Map<String,Object> logMap = new HashMap<String, Object>();
				logMap.put("logId", log.getLog_id());
				logMap.put("currencyTypeId", log.getCurrency_type_id());
				logMap.put("changeAmount", log.getChange_amount());
				logMap.put("dateline", log.getDateline());
				logMap.put("fUserId", log.getF_user_id());
				logMap.put("beforeLevelNameId", log.getBefore_level_name_id());
				logMap.put("afterLevelNameId", log.getAfter_level_name_id());
				logMap.put("beforeLevelName", log.getBefore_level_name());
				logMap.put("afterLevelName", log.getAfter_level_name());
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(logMap)));
			}
		}
		
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
}
