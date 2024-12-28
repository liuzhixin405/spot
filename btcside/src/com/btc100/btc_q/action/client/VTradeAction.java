package com.btc100.btc_q.action.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionPartition;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VLastTicker;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQTransactionPartitionService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VLastTickerService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPendingLogService;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.service.VStopLossService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionRateService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.MulpitleAndStateVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.PlanPendingDelegationVO;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/vtrade")
public class VTradeAction extends BaseAction {

	@Autowired
	private VTransactionService transactionService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private VTransactionTypeService transactionTypeService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private VMemberTransactionService bqMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VPendingDelegationService pendingDelegationService;
	@Autowired
	private VPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private VTradeLogService tradeLogService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private VAccountContractService bqAccountContractService;
	@Autowired
	private VTransactionRateService bqTransactionRateService;
	@Autowired
	private VLastTickerService bqLastTickerService;
	@Autowired
	private VStopLossService bqStopLossService;
	@Autowired
	private BQTransactionPartitionService bqTransactionPartitionService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private VPlanPendingDelegationService vPlanPendingDelegationService;
	@Autowired
	private VPendingLogService vPendingLogService;
	
	@RequestMapping("/tickersData")
	public void tickersData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		toWeb(response, gson.toJson(super.currVTrans(super.findCurrVTrans())));
	}
	
	@RequestMapping("/jisuanMemTransData")
	public void jisuanMemTransData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, "not login");
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<VMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<VMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		JSONObject data = new JSONObject();
		Gson gson = new Gson();
		data.put("buyMemberTransaction", gson.toJson(buyMemberTransaction));
		data.put("sellMemberTransaction", gson.toJson(sellMemberTransaction));
		
		VAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
		data.put("balance", accountContract.getF_btc().add(accountContract.getF_lockbtc()));
		data.put("coefficient", currentTransaction.getTrade_coefficient().doubleValue());
		Integer digits = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS);
		if (digits == null) {
			VTransactionType transType = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		data.put("digits", digits);
		
		toWeb(response, data.toString());
	}

	@RequestMapping("/memTransData")
	public void memTransData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		VTransactionType type = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
		if (type == null) {
			return;
		}
		
		JSONObject data = new JSONObject();
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = pendingDelegationService.readBuyTop5(currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = pendingDelegationService.readSellTop5(currentTransaction.getTransaction_code());
		}
		
		int digits = Utils.getDigitsFromMinFloat(type.getMin_float_price());
		BigDecimal buy1Price = BigDecimal.ZERO;
		BigDecimal sell1Price = BigDecimal.ZERO;
		if (buytop5List != null) {
			for (Top5DelegationVO vo : buytop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(4, RoundingMode.DOWN));
			}
			
			if (buytop5List.size() > 0)
				buy1Price = buytop5List.get(0).getPrice();
		}
		
		if (selltop5List != null) {
			for (Top5DelegationVO vo : selltop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(4, RoundingMode.DOWN));
			}
			
			if (selltop5List.size() > 0) {
				sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
			}
		}
		
		BQMemberLevel userlevel = bqMemberLevelService.readById(user.getF_user_level_id());
		
		// 持仓信息
		List<VMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<VMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (VMemberTransaction bqMemberTransaction : buyMemberTransaction) {
				int buyStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readData(bqMemberTransaction, 1, markPrice, userlevel.getEat_commission_rate(), buyStopLoss, 
						digits, buy1Price, sell1Price);
				if (transVo != null) {
					listTransVos.add(transVo);
				}
			}
		}
		Gson gson = new Gson();
		data.put("buyMemberTransaction", gson.toJson(listTransVos));
		data.put("buyMemeberTransCount", listTransVos.size());
		data.put("lastPrice", currentTransaction.getLatest_transaction_price().doubleValue());

		List<BQTransVo> listTranssellVos = new ArrayList<BQTransVo>();
		if (sellMemberTransaction != null) {
			for (VMemberTransaction bqMemberTransaction : sellMemberTransaction) {
				int sellStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readData(bqMemberTransaction, 2, markPrice, userlevel.getEat_commission_rate(), sellStopLoss, 
						digits, buy1Price, sell1Price);
				if (transVo != null) {
					listTranssellVos.add(transVo);
				}
			}
		}
		
		data.put("sellMemberTransaction", gson.toJson(listTranssellVos));
		data.put("sellMemeberTransCount", listTranssellVos.size());
		data.put("digits", digits);
		data.put("buy1", buy1Price.doubleValue() > 0 ? buy1Price.doubleValue() : "");
		data.put("sell1", sell1Price.doubleValue() > 0 ? sell1Price.doubleValue() : "");
		
		int amountDigits = Utils.getAmountDigtis(transactionCode);
		data.put("amountDigits", amountDigits);
		
		toWeb(response, data.toString());
	}
	
	@RequestMapping("/historyPendingData")
	public void historyPendingData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long startTime = morningTime - 6 * 24 * 3600;
		List<VPendingLog> pdLogList = vPendingLogService.readListByUserId(transactionCode, user.getF_user_id(), 0, "", startTime, 0, 50);

		Gson gson = new Gson();
		toWeb(response, gson.toJson(pdLogList));
	}
	
	@RequestMapping("/pendingData")
	public void pendingData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<VPendingDelegation> pdList = pendingDelegationService
				.readAllByUserId(currentTransaction.getTransaction_code(), "", "", user.getF_user_id(), "");
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (VPendingDelegation pd : pdList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(pd.getDelegation_id());
			data.setOrderId(pd.getPending_no() + "");
			data.setSymbol(pd.getTransaction_code());
			data.setAmount(pd.getDelegation_count().doubleValue());
			data.setDealAmount(pd.getDeal_count().doubleValue());
			data.setUndealAmount(pd.getUndeal_count().doubleValue());
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setPriority(pd.getPriority());
			data.setDirection(pd.getDirection());
			data.setDeposit(pd.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTED);
			
			listPending.add(data);
		}
		
		List<VPendingDelegationCache> pdCacheList = new ArrayList<VPendingDelegationCache>();
		if (user != null) {
			pdCacheList = pendingDelegationCacheService.readPendingByUserIdAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, 0, 0);
		}
		
		for (VPendingDelegationCache cache : pdCacheList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(cache.getDelegation_id());
			data.setOrderId(cache.getPending_no() + "");
			data.setSymbol(cache.getTransaction_code());
			data.setAmount(cache.getDelegation_count().doubleValue());
			data.setDealAmount(cache.getDeal_count().doubleValue());
			data.setUndealAmount(cache.getUndeal_count().doubleValue());
			data.setPrice(cache.getDelegation_price().doubleValue());
			data.setCreateTime(cache.getDelegation_time());
			data.setMultiple(cache.getMultiple());
			data.setPriority(cache.getPriority());
			data.setDirection(cache.getDirection());
			data.setDeposit(cache.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTING);
			
			listPending.add(data);
		}
		
		Gson gson = new Gson();
		toWeb(response, gson.toJson(listPending));
	}
	
	@RequestMapping("/planPendingData")
	public void planPendingData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<PlanPendingDelegationVO> planPdVOList = new ArrayList<PlanPendingDelegationVO>();
		List<VPlanPendingDelegation> planPdList = vPlanPendingDelegationService.readByUserId(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, -1);
		for (VPlanPendingDelegation pd : planPdList) {
			PlanPendingDelegationVO data = new PlanPendingDelegationVO();
			data.setDelegation_id(pd.getId());
			data.setSymbol(pd.getF_code());
			data.setAmount(pd.getDelegation_count().doubleValue());	
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setTriPrice(pd.getTri_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setDirection(pd.getDirection());
			data.setStatus(pd.getStatus());
			data.setNote(pd.getNote());
			data.setType(pd.getType());
			
			planPdVOList.add(data);
		}
		
		Gson gson = new Gson();
		toWeb(response, gson.toJson(planPdVOList));
	}
	
	@RequestMapping("/tradeLogData")
	public void tradeLogData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long startTime = morningTime - 6 * 24 * 3600;
		
		List<VTradeLog> trades = tradeLogService.readByCodeAndUserAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), startTime, 0, 50);
		Gson gson = new Gson();
		toWeb(response, gson.toJson(trades));
	}
	
	@RequestMapping("/tradedata")
	public void getDataByCode(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}
		
		JSONObject gson = new JSONObject();

		// 当前合约
		String transactionCode = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}

		// 账户信息
		BigDecimal gift = BigDecimal.ZERO;
		VAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
		gson.put("userAccountBtc", accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN));
		
		toWeb(response, gson.toString());
	}
	
	@RequestMapping("/getMultiple")
	public void getMultiple(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		long uid = 0;
		BQMember user = super.findCurrUser(request, true);
		if (user != null) {
			uid = user.getF_user_id();
		}
		
		JSONObject data = new JSONObject();
		
		// 当前合约
		String code = request.getParameter("transactionCode");
		VTransaction currentTransaction = super.findCurrVTrans(code);
		if (currentTransaction == null ) {
			return;
		}
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = getMultipleVos(currentTransaction, uid);
		
		Gson gson = new Gson();
		data.put("multiples", gson.toJson(listMulpitleAndStateVOs));
		toWeb(response, data);
	}
	
	private List<MulpitleAndStateVO> getMultipleVos(VTransaction transaction, long uid) {
		String code = transaction.getTransaction_code();
		int canMultiple = 0;
		if (uid > 0) {
			List<VMemberTransaction> memberTrans = bqMemberTransactionService.readTransactionByUserId(code, uid);
			if (memberTrans.size() > 0) {
				VMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<VPendingDelegationCache> caches = pendingDelegationCacheService.readPendingByUserIdAndTime(code, uid, 0, 0, 1);
				if (caches.size() > 0) {
					VPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<VPendingDelegation> pendings = pendingDelegationService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, "", 1);
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

	private void tradeEx(HttpServletRequest request, HttpServletResponse response, Model model) {
		BQMember member = super.findCurrUser(request, true);
		
		List<BQTransaction> trans = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (trans == null) {
			trans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		List<VTransactionType> vtypes = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
		if (vtypes == null) {
			vtypes = transactionTypeService.readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, vtypes);
		}
		
		List<BQTransactionPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_PARTITIONS);
		if (partitions == null) {
			partitions = bqTransactionPartitionService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_PARTITIONS,BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		request.setAttribute("vTransTypes", vtypes);
		request.setAttribute("indexs", getIndexs(trans));
		
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		if (member != null) {
			BQMemberPayword payword = bqMemberPaywordService.readById(member.getF_user_id());
			if (payword != null && payword.getState() != 0) {
				model.addAttribute("payPwd", true);
			} else {
				model.addAttribute("payPwd", false);
			}
		} else {
			model.addAttribute("payPwd", false);
		}

		// 当前合约
		String transactionCode = request.getParameter("transactionCode");
		if (StringUtil.isEmpty(transactionCode)) {
			transactionCode = CookieHelper.getCookieValue(request, "transactionCode");
			if (StringUtil.isEmpty(transactionCode)) {
				transactionCode = "btc";
			}
		}
		CookieHelper.setCookie(response, "transactionCode", transactionCode);
		
		String skinType = request.getParameter("skin");
		if (skinType != null && !skinType.isEmpty()) {
			CookieHelper.setCookie(response, "skinType", skinType);
		} else {
			skinType = CookieHelper.getCookieValue(request, "skinType");
			if (skinType == null || skinType.isEmpty()) {
				skinType = "black";
			}
		}
		model.addAttribute("skinType", skinType);
		
		List<VTransaction> transactionList = super.findCurrVTrans();
		model.addAttribute("transactionList", transactionList);
		VTransaction currentTransaction = null;
		
		if (!StringUtil.isEmpty(transactionCode)) {
			for (VTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(transactionCode)) {
					currentTransaction = bqTransaction;
					break;
				}
			}
		}
		
		if (currentTransaction == null && transactionList.size() > 0) {
			currentTransaction = transactionList.get(0);
		}
		if (currentTransaction == null ) {
			model.addAttribute("hashy", false);
			return;
		}
		
		VTransactionType type = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
		if (type == null) {
			return;
		}
		
		model.addAttribute("minKcNums", type.getMin_kc_nums().doubleValue());
		model.addAttribute("minFloatPrice", type.getMin_float_price().doubleValue());
		model.addAttribute("typeCode", currentTransaction.getTransaction_type_code().toUpperCase());
		model.addAttribute("typeCodeName", currentTransaction.getTransaction_type_code().toUpperCase());
		model.addAttribute("hashy", true);
		model.addAttribute("currentTransaction", currentTransaction);

		BigDecimal eatRate = BigDecimal.ZERO;
		BigDecimal pendingRate = BigDecimal.ZERO;
		if (member != null) {
			BQMemberLevel userlevel = bqMemberLevelService.readById(member.getF_user_level_id());
			model.addAttribute("userlevel", userlevel);
			eatRate = userlevel.getEat_commission_rate();
			pendingRate = userlevel.getPending_commission_rate();
		}

		int digits = Utils.getDigitsFromMinFloat(type.getMin_float_price());
		int amountDigits = Utils.getAmountDigtis(currentTransaction.getTransaction_code());
		model.addAttribute("amountDigits", amountDigits);
		
		// 对应币种指数
		BigDecimal btc_index_price = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		model.addAttribute("btc_index_price", btc_index_price.setScale(digits, BigDecimal.ROUND_HALF_UP));

		// 合约价格浮动
		VTransactionRate index_price_rate = bqTransactionRateService.readByCode(currentTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		model.addAttribute("maxBuy", maxIndexPrice);
		model.addAttribute("minSell", minIndexPrice);
		model.addAttribute("digits", digits);
		
		VLastTicker bqLastTicker = bqLastTickerService.readByCode(currentTransaction.getTransaction_code());
		if (bqLastTicker == null) {
			bqLastTicker = new VLastTicker();
			bqLastTicker.setF_ticker_high(BigDecimal.ZERO);
			bqLastTicker.setF_ticker_low(BigDecimal.ZERO);
			bqLastTicker.setF_ticker_vol(BigDecimal.ZERO);
			bqLastTicker.setF_ticker_trades(BigDecimal.ZERO);
		}
		
		model.addAttribute("todayHigh", bqLastTicker.getF_ticker_high().setScale(digits, BigDecimal.ROUND_DOWN)); // 今日最高
		model.addAttribute("todayLow", bqLastTicker.getF_ticker_low().setScale(digits, BigDecimal.ROUND_DOWN)); // 今日最低
		model.addAttribute("tradevols", bqLastTicker.getF_ticker_vol().setScale(digits, BigDecimal.ROUND_DOWN)); // 因为定时器里面获取的成交量是24小时前的成交量，时间不对照，所以此方法不用memcache
		model.addAttribute("trades", bqLastTicker.getF_ticker_trades().setScale(digits, BigDecimal.ROUND_DOWN));
		
		// 账户信息
		BQAccount bqAccount = new BQAccount();
		if (member != null) {
			bqAccount = bqAccountService.readAccount(member.getF_user_id(), currentTransaction.getCurrency_type_id());
		} else {
			bqAccount.setBtc(BigDecimal.ZERO);
			bqAccount.setBtc_locked(BigDecimal.ZERO);
		}
		
		model.addAttribute("bqAccount", bqAccount);

		// 保证金自动追加
		model.addAttribute("depositOnOff", false);

		if (member != null) {
			BigDecimal gift = BigDecimal.ZERO;
			VAccountContract accountContract = bqAccountContractService.readByUidCode(member.getF_user_id(), currentTransaction.getTransaction_code());
			model.addAttribute("userAccountBtc", accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN)); // 当前可用
			BigDecimal minbtc = accountContract.getF_btc().compareTo(accountContract.getF_initbtc()) >= 0 ? accountContract.getF_initbtc() : accountContract
					.getF_btc();
			model.addAttribute("userAccountGainBtc", minbtc.setScale(4, RoundingMode.DOWN));// 可转出
		} else {
			model.addAttribute("userAccountBtc", BigDecimal.ZERO); // 当前可用
			model.addAttribute("userAccountGainBtc", BigDecimal.ZERO);// 可转出
		}

		// 持仓信息
		List<VMemberTransaction> buyMemberTransaction = new ArrayList<VMemberTransaction>();
		List<VMemberTransaction> sellMemberTransaction = new ArrayList<VMemberTransaction>();
		if (member !=  null) {
			buyMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(member.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
			sellMemberTransaction = bqMemberTransactionService.readVMemberTrasaction(member.getF_user_id(), currentTransaction
					.getTransaction_code(), Contents.DIRECTION_SELL);
		}

		// 获取当前最新成交价
		BigDecimal lastPrice = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(currentTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = pendingDelegationService.readBuyTop5(currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.V_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = pendingDelegationService.readSellTop5(currentTransaction.getTransaction_code());
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
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + currentTransaction.getTransaction_code());
		if (markPrice != null) {
			model.addAttribute("markPrice", markPrice.doubleValue());
		} else {
			model.addAttribute("markPrice", "--");
		}
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}
		
		List<MulpitleAndStateVO> multiples = new ArrayList<MulpitleAndStateVO>();
		
		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (VMemberTransaction bqMemberTransaction : buyMemberTransaction) {
				int buyStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readData(bqMemberTransaction, 1, markPrice, eatRate, buyStopLoss, 
						digits, buy1Price, sell1Price);
				if (transVo != null) {
					listTransVos.add(transVo);
				}
				
				BigDecimal trade_multiple = bqMemberTransaction.getTrade_multiple();
				boolean have = false;
				for (int i = 0; i < multiples.size(); i++) {
					if (multiples.get(i).getMultiple().equals(trade_multiple.intValue() + "")) {
						have = true;
						break;
					}
				}
				if (!have) {
					multiples.add(new MulpitleAndStateVO(1, trade_multiple.intValue() + ""));
				}
			}
		}
		model.addAttribute("buyMemberTransaction", listTransVos);
		model.addAttribute("buyMemeberTransCount", listTransVos.size());
		
		List<BQTransVo> listTranssellVos = new ArrayList<BQTransVo>();
		if (sellMemberTransaction != null && sellMemberTransaction.size() > 0) {
			for (VMemberTransaction bqMemberTransaction : sellMemberTransaction) {
				int sellStopLoss = bqStopLossService.readHasStopLoss(bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readData(bqMemberTransaction, 2, markPrice, eatRate, sellStopLoss, 
						digits, buy1Price, sell1Price);
				if (transVo != null) {
					listTranssellVos.add(transVo);
				}
				
				BigDecimal trade_multiple = bqMemberTransaction.getTrade_multiple();
				boolean have = false;
				for (int i = 0; i < multiples.size(); i++) {
					if (multiples.get(i).getMultiple().equals(trade_multiple.intValue() + "")) {
						have = true;
						break;
					}
				}
				if (!have) {
					multiples.add(new MulpitleAndStateVO(1, trade_multiple.intValue() + ""));
				}
			}
		}
		
		model.addAttribute("sellMemberTransaction", listTranssellVos);
		model.addAttribute("sellMemeberTransCount", listTranssellVos.size());
		model.addAttribute("multiples", multiples);
		
		List<VPendingDelegation> pdList = new ArrayList<VPendingDelegation>();
		if (member != null) {
			pdList = pendingDelegationService
				.readAllByUserId(currentTransaction.getTransaction_code(), "", "", member.getF_user_id(), "");
		}
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (VPendingDelegation pd : pdList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(pd.getDelegation_id());
			data.setOrderId(pd.getPending_no() + "");
			data.setSymbol(pd.getTransaction_code());
			data.setAmount(pd.getDelegation_count().doubleValue());
			data.setDealAmount(pd.getDeal_count().doubleValue());
			data.setUndealAmount(pd.getUndeal_count().doubleValue());
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setPriority(pd.getPriority());
			data.setDirection(pd.getDirection());
			data.setDeposit(pd.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTED);
			
			listPending.add(data);
		}
		
		List<VPendingDelegationCache> pdCacheList = new ArrayList<VPendingDelegationCache>();
		if (member != null) {
			pdCacheList = pendingDelegationCacheService.readPendingByUserIdAndTime(currentTransaction.getTransaction_code(), member.getF_user_id(), 0, 0, 0);
		}
		
		for (VPendingDelegationCache cache : pdCacheList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(cache.getDelegation_id());
			data.setOrderId(cache.getPending_no() + "");
			data.setSymbol(cache.getTransaction_code());
			data.setAmount(cache.getDelegation_count().doubleValue());
			data.setDealAmount(cache.getDeal_count().doubleValue());
			data.setUndealAmount(cache.getUndeal_count().doubleValue());
			data.setPrice(cache.getDelegation_price().doubleValue());
			data.setCreateTime(cache.getDelegation_time());
			data.setMultiple(cache.getMultiple());
			data.setPriority(cache.getPriority());
			data.setDirection(cache.getDirection());
			data.setDeposit(cache.getDeposit().doubleValue());
			data.setState(Contents.PENDING_STATE_SUBMITTING);
			
			listPending.add(data);
		}
		model.addAttribute("pdList", listPending);
		
		List<PlanPendingDelegationVO> planPdVOList = new ArrayList<PlanPendingDelegationVO>();
		List<VPlanPendingDelegation> planPdList = new ArrayList<VPlanPendingDelegation>();
		if (member != null) {
			planPdList = vPlanPendingDelegationService.readByUserId(currentTransaction.getTransaction_code(), member.getF_user_id(), 0, -1);
		}
		for (VPlanPendingDelegation pd : planPdList) {
			PlanPendingDelegationVO data = new PlanPendingDelegationVO();
			data.setDelegation_id(pd.getId());
			data.setSymbol(pd.getF_code());
			data.setAmount(pd.getDelegation_count().doubleValue());	
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setTriPrice(pd.getTri_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setMultiple(pd.getMultiple());
			data.setDirection(pd.getDirection());
			data.setStatus(pd.getStatus());
			data.setNote(pd.getNote());
			data.setType(pd.getType());
			
			planPdVOList.add(data);
		}
		model.addAttribute("planPdVOList", planPdVOList);
		
		// 杠杆
		long userId = member == null ? 0 : member.getF_user_id();
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = getMultipleVos(currentTransaction, userId);
		String defaultMultiple = "";
		if (listMulpitleAndStateVOs.size() > 0) {
			defaultMultiple = listMulpitleAndStateVOs.get(0).getMultiple();
		}
		
		model.addAttribute("pendingRate", pendingRate);
		model.addAttribute("myCommissionRate", eatRate);
		model.addAttribute("multiple", listMulpitleAndStateVOs);
		model.addAttribute("defaultMultiple", defaultMultiple);
		model.addAttribute("coeffcient", currentTransaction.getTrade_coefficient());
		model.addAttribute("buytop5List", buytop5List);
		model.addAttribute("selltop5List", selltop5List);
		model.addAttribute("lastPrice", lastPrice.doubleValue());
		model.addAttribute("isTradeTime", true);
		BigDecimal fundingFee = spyMemcachedClient.get(BTCQMemecacheKeys.V_FUNDING_FEE_RATE + currentTransaction.getTransaction_code());
		model.addAttribute("fundingFeeRate", fundingFee);
		
		List<VTradeLog> trades = new ArrayList<VTradeLog>();
		if (member != null) {
			long morningTime = TimeUtil.getTimesmorning();
			long t7 = morningTime - 6 * 24 * 3600;
			trades = tradeLogService.readByCodeAndUserAndTime(currentTransaction.getTransaction_code(), member.getF_user_id(), t7, 0, 50);
		}
		model.addAttribute("tradeLogList", trades);
		
		if (member != null) {
			BQMemberFavoriteMarket fav = bqMemberFavoriteMarketService.readByUserId(member.getF_user_id(), Contents.MARKET_TYPE_VCONTRACT);
			if (fav != null) {
				request.setAttribute("favorite", fav.getCodes());
			}
		}
		
		List<VTransaction> listTransactions = super.findCurrVTrans();
		model.addAttribute("transactionList", listTransactions);
		model.addAttribute("listMap", currVTrans(listTransactions));
	}
	
	/**
	 * 买卖
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@RequestMapping("/trade")
	public String trade(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		tradeEx(request, response, model);
		return "jsp/full_trade";
	}
	
	@RequestMapping("/full")
	public String fullTrade(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		tradeEx(request, response, model);
		return "jsp/full_trade";
	}
	
	@RequestMapping("/pendingQuery")
	public String pendingQuery(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return super.toLogin();
		}
		
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		
		Map<String, String> czlxMap = new LinkedHashMap<String, String>();
		czlxMap.put("all", SpringContext.getText("msg.pg.tradeEntrust.allTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("1", SpringContext.getText("msg.pg.tradeEntrust.buyfTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("2", SpringContext.getText("msg.pg.tradeEntrust.sellsTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("3", SpringContext.getText("msg.pg.tradeEntrust.sellfTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("4", SpringContext.getText("msg.pg.tradeEntrust.buysTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("5", SpringContext.getText("msg.pg.tradeEntrust.duoBaoTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("6", SpringContext.getText("msg.pg.tradeEntrust.kongBaoTrade", Contents.Local.LOCALE.get()));
		model.addAttribute("czlxMap", czlxMap);
		
		return pendingQuerydo(request, model, typeId, user);
	}

	/**
	 * 交易查询
	 * 
	 * @return
	 */
	@RequestMapping("/tradeQuery")
	public String tradeQuery(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return super.toLogin();
		}
		int typeId = Convert.toInt(request.getParameter("typeId"), 0);
		Map<String, String> czlxMap = new LinkedHashMap<String, String>();
		czlxMap.put("all", SpringContext.getText("msg.pg.tradeEntrust.allTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("1", SpringContext.getText("msg.pg.tradeEntrust.buyfTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("2", SpringContext.getText("msg.pg.tradeEntrust.sellsTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("3", SpringContext.getText("msg.pg.tradeEntrust.sellfTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("4", SpringContext.getText("msg.pg.tradeEntrust.buysTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("5", SpringContext.getText("msg.pg.tradeEntrust.duoBaoTrade", Contents.Local.LOCALE.get()));
		czlxMap.put("6", SpringContext.getText("msg.pg.tradeEntrust.kongBaoTrade", Contents.Local.LOCALE.get()));
		model.addAttribute("czlxMap", czlxMap);
		return tradeQuerydo(request, model, typeId, user);
	}

	private String tradeQuerydo(HttpServletRequest request, Model model, int iTranstype, BQMember user) {
		List<VTransaction> transactionList = super.findCurrVTrans();
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		VTransaction currentTransaction = null;
		for (VTransaction transaction : transactionList) {
			if (transaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
				currentTransaction = transaction;
				break;
			}
		}
		
		if (currentTransaction == null) {
			currentTransaction = transactionList.get(0);
		}
		
		bqCode = currentTransaction.getTransaction_code();
		model.addAttribute("currentTransaction", currentTransaction);

		String startTimeStr = request.getParameter("startTimeStr");
		String endTimeStr = request.getParameter("endTimeStr");

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (!StringUtil.isEmpty(startTimeStr)) {
			try {
				startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr + " 00:00:00"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(endTimeStr)) {
			try {
				endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr + " 00:00:00"));
				endTime += 86400L;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -89);
		long t90 = cal.getTimeInMillis() / 1000;
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
		}
		
		if (startTime < t90) {
			startTime = t90;
			startTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(startTime * 1000l));
		}
		
		if (t90 <= startTime) {
			model.addAttribute("startTimeForStartDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(t90 * 1000l)));
		} else {
			model.addAttribute("startTimeForStartDate", startTimeStr);
		}
		
		model.addAttribute("endTimeForEndDate", endTimeStr);
		
		if (startTime != 0l && endTime != 0l) {
			model.addAttribute("endTimeStr", endTimeStr);
			model.addAttribute("startTimeStr", startTimeStr);
		} else {
			
			String currentNearlyDays = request.getParameter("currentNearlyDays");

			long morningTime = TimeUtil.getTimesmorning();
			long t7 = morningTime - 6 * 24 * 3600;
			
			if (!StringUtil.isEmpty(currentNearlyDays)) {
				int d = Integer.valueOf(currentNearlyDays);
				if (d != 7 && d != 30 && d != 90) {
					startTime = t7;
					currentNearlyDays = "7";
				} else {
					startTime = morningTime - (d - 1) * 24 * 3600;
				}
			} else {
				startTime = t7;
				currentNearlyDays = "7";
			}
			endTime = 0l;
			model.addAttribute("currentNearlyDays", currentNearlyDays);
		}

		String czlx = request.getParameter("czlx");

		PagerModel tradeLogList = new PagerModel();
		if (StringUtil.isEmpty(czlx) || "all".equalsIgnoreCase(czlx)) {
			tradeLogList = tradeLogService.readByUserIdAndTransaction(bqCode, user.getF_user_id(), 0, startTime, endTime);
			model.addAttribute("currentCzlx", "all");
		} else {
			tradeLogList = tradeLogService.readByUserIdAndTransaction(bqCode, user.getF_user_id(), Integer.valueOf(czlx), startTime, endTime);
			model.addAttribute("currentCzlx", czlx);
		}

		model.addAttribute("tradeLogList", tradeLogList);
		model.addAttribute("pageN", "vtrade/tradeQuery.do");
		model.addAttribute("issz", false);

		return "jsp/trade_query";
	}
	
	private String pendingQuerydo(HttpServletRequest request, Model model, int iTranstype, BQMember user) {
		List<VTransaction> transactionList = super.findCurrVTrans();
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		VTransaction currentTransaction = null;
		for (VTransaction transaction : transactionList) {
			if (transaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
				currentTransaction = transaction;
				break;
			}
		}
		
		if (currentTransaction == null) {
			currentTransaction = transactionList.get(0);
		}
		
		bqCode = currentTransaction.getTransaction_code();
		model.addAttribute("currentTransaction", currentTransaction);
		
		String startTimeStr = request.getParameter("startTimeStr");
		String endTimeStr = request.getParameter("endTimeStr");

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (!StringUtil.isEmpty(startTimeStr)) {
			try {
				startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr + " 00:00:00"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(endTimeStr)) {
			try {
				endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr + " 00:00:00"));
				endTime += 86400L;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		Calendar cal90 = new GregorianCalendar();
		cal90.add(Calendar.DAY_OF_MONTH, -89);
		long t90 = cal90.getTimeInMillis() / 1000;
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
		}
		
		if (startTime < t90) {
			startTime = t90;
			startTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(startTime * 1000l));
		}
		
		if (t90 <= startTime) {
			model.addAttribute("startTimeForStartDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(t90 * 1000l)));
		} else {
			model.addAttribute("startTimeForStartDate", startTimeStr);
		}
		
		model.addAttribute("endTimeForEndDate", endTimeStr);
		
		if (startTime != 0l && endTime != 0l) {
			model.addAttribute("startTimeStr", startTimeStr);
			model.addAttribute("endTimeStr", endTimeStr);
		} else {
			
			String currentNearlyDays = request.getParameter("currentNearlyDays");
			
			long morningTime = TimeUtil.getTimesmorning();
			long t7 = morningTime - 6 * 24 * 3600;
			
			if (!StringUtil.isEmpty(currentNearlyDays)) {
				int d = Integer.valueOf(currentNearlyDays);
				if (d != 7 && d != 30 && d != 90) {
					startTime = t7;
					currentNearlyDays = "7";
				} else {
					startTime = morningTime - (d - 1) * 24 * 3600;
				}
			} else {
				startTime = t7;
				currentNearlyDays = "7";
			}
			endTime = 0l;
			model.addAttribute("currentNearlyDays", currentNearlyDays);
		}

		String czlx = request.getParameter("czlx");

		PagerModel pendingLogList = new PagerModel();
		int tradeType = 0;
		if (StringUtil.isEmpty(czlx) || "all".equalsIgnoreCase(czlx)) {
			model.addAttribute("currentCzlx", "all");
		} else {
			tradeType = Integer.valueOf(czlx);
			model.addAttribute("currentCzlx", czlx);
		}
		
		pendingLogList = vPendingLogService.readPagerMode(bqCode, user.getF_user_id(), tradeType, startTime, endTime);

		model.addAttribute("pendingLogList", pendingLogList);
		model.addAttribute("pageN", "trade/pendingQuery.do");

		return "jsp/pending_query";
	}
}
