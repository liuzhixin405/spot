package com.btc100.btc_q.action.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
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
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryLastTicker;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionPartition;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryLastTickerService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionPartitionService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
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
@RequestMapping("/dtrade")
public class DeliveryTradeAction extends BaseAction {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTransactionTypeService transactionTypeService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private DeliveryMemberTransactionService deliveryMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryPendingDelegationService deliveryPendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationCacheService deliveryPendingDelegationCacheService;
	@Autowired
	private DeliveryTradeLogService deliveryTradeLogService;
	@Autowired
	private DeliveryStopLossService deliveryStopLossService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private DeliveryAccountContractService deliveryAccountContractService;
	@Autowired
	private DeliveryTransactionRateService deliveryTransactionRateService;
	@Autowired
	private DeliveryLastTickerService deliveryLastTickerService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private DeliveryTransactionPartitionService deliveryTransactionPartitionService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private DeliveryPlanPendingDelegationService deliveryPlanPendingDelegationService;
	@Autowired
	private DeliveryPendingLogService deliveryPendingLogService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private DeliveryTransactionFeeRateService deliveryTransactionFeeRateService;
	
	@RequestMapping("/getFloatGainLoss")
	public void getFloatGaiinLoss(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}

		List<DeliveryMemberTransaction> buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<DeliveryMemberTransaction> sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);

		JSONObject data = new JSONObject();
		
		List<BQTransVo> listTransBuyVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (DeliveryMemberTransaction memberTransaction : buyMemberTransaction) {
				BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
				BigDecimal deposit = (memberTransaction.getDeposit().add(memberTransaction.getDeposit2())).setScale(4, BigDecimal.ROUND_DOWN);
				ykdiff = markPrice.subtract(memberTransaction.getAverage_price()).multiply(currentTransaction.getTrade_coefficient())
						.multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count()));
				if (deposit.compareTo(BigDecimal.ZERO) != 0) 
					ykRate = ykdiff.divide(deposit, 4, BigDecimal.ROUND_DOWN);
				ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
				
				BQTransVo transVo = new BQTransVo();
				transVo.setMt_id(memberTransaction.getMt_id());
				transVo.setFloatingProfitLoss(ykdiff);	// 浮动盈亏
				NumberFormat percent = NumberFormat.getPercentInstance();
				percent.setMaximumFractionDigits(2);
				transVo.setProfitLossRatio(percent.format(ykRate));	// 盈亏比
				listTransBuyVos.add(transVo);
			}
		}
		
		List<BQTransVo> listTransSellVos = new ArrayList<BQTransVo>();
		if (sellMemberTransaction != null && sellMemberTransaction.size() > 0) {
			for (DeliveryMemberTransaction memberTransaction : sellMemberTransaction) {
				BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
				BigDecimal deposit = (memberTransaction.getDeposit().add(memberTransaction.getDeposit2())).setScale(4, BigDecimal.ROUND_DOWN);
				ykdiff = memberTransaction.getAverage_price().subtract(markPrice).multiply(currentTransaction.getTrade_coefficient())
						.multiply(memberTransaction.getLock_count().add(memberTransaction.getHold_count()));
				if (deposit.compareTo(BigDecimal.ZERO) != 0) 
					ykRate = ykdiff.divide(deposit, 4, BigDecimal.ROUND_DOWN);
				
				ykdiff = ykdiff.setScale(4, BigDecimal.ROUND_DOWN);
			
				BQTransVo transVo = new BQTransVo();
				transVo.setMt_id(memberTransaction.getMt_id());
				transVo.setFloatingProfitLoss(ykdiff);	// 浮动盈亏
				NumberFormat percent = NumberFormat.getPercentInstance();
				percent.setMaximumFractionDigits(2);
				transVo.setProfitLossRatio(percent.format(ykRate));	// 盈亏比
				listTransSellVos.add(transVo);
			}
		}
		
		Gson gson = new Gson();
		data.put("buyMemberTransaction", gson.toJson(listTransBuyVos));
		data.put("sellMemberTransaction", gson.toJson(listTransSellVos));
		
		toWeb(response, data.toString());
	}
	
	@RequestMapping("/tickersData")
	public void tickersData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		toWeb(response, gson.toJson(super.currDTrans(super.findCurrDTrans())));
	}
	
	@RequestMapping("/jisuanMemTransData")
	public void jisuanMemTransData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, "not login");
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<DeliveryMemberTransaction> buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<DeliveryMemberTransaction> sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		JSONObject data = new JSONObject();
		Gson gson = new Gson();
		data.put("buyMemberTransaction", gson.toJson(buyMemberTransaction));
		data.put("sellMemberTransaction", gson.toJson(sellMemberTransaction));
		
		DeliveryAccountContract accountContract = deliveryAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
		data.put("balance", accountContract.getF_btc().add(accountContract.getF_lockbtc()));
		data.put("coefficient", currentTransaction.getTrade_coefficient().doubleValue());
		Integer digits = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
		if (digits == null) {
			DeliveryTransactionType transType = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		DeliveryTransactionType type = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
		if (type == null) {
			return;
		}
		
		JSONObject data = new JSONObject();
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = deliveryPendingDelegationService.getBuyTop5(currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = deliveryPendingDelegationService.getSellTop5(currentTransaction.getTransaction_code());
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
		
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		// 持仓信息
		List<DeliveryMemberTransaction> buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<DeliveryMemberTransaction> sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (DeliveryMemberTransaction memberTransaction : buyMemberTransaction) {
				int buyStopLoss = deliveryStopLossService.readHasStopLoss(memberTransaction.getMt_id());
				BQTransVo transVo = deliveryMemberTransactionService.readHandleData(memberTransaction, 1, markPrice, feeRate, buyStopLoss, 
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
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
			for (DeliveryMemberTransaction memberTransaction : sellMemberTransaction) {
				int sellStopLoss = deliveryStopLossService.readHasStopLoss(memberTransaction.getMt_id());
				BQTransVo transVo = deliveryMemberTransactionService.readHandleData(memberTransaction, 2, markPrice, feeRate, sellStopLoss, 
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long startTime = morningTime - 6 * 24 * 3600;
		List<DeliveryPendingLog> pdLogList = deliveryPendingLogService.readListByUserId(transactionCode, user.getF_user_id(), 0, "", startTime, 0, 50);

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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<DeliveryPendingDelegation> pdList = deliveryPendingDelegationService
				.readAllByUserId(currentTransaction.getTransaction_code(), "", "", user.getF_user_id(), "");
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (DeliveryPendingDelegation pd : pdList) {
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
		
		List<DeliveryPendingDelegationCache> pdCacheList = new ArrayList<DeliveryPendingDelegationCache>();
		if (user != null) {
			pdCacheList = deliveryPendingDelegationCacheService.readPendingByUserIdAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, 0, 0, 0, true);
		}
		
		for (DeliveryPendingDelegationCache cache : pdCacheList) {
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<PlanPendingDelegationVO> planPdVOList = new ArrayList<PlanPendingDelegationVO>();
		List<DeliveryPlanPendingDelegation> planPdList = deliveryPlanPendingDelegationService.readByUserId(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, -1);
		for (DeliveryPlanPendingDelegation pd : planPdList) {
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long t7 = morningTime - 6 * 24 * 3600;
		
		List<DeliveryTradeLog> trades = deliveryTradeLogService.readByUserAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), -1, "", t7, 0, 50);
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(transactionCode);
		if (currentTransaction == null ) {
			return;
		}

		// 账户信息
		BigDecimal gift = BigDecimal.ZERO;
		DeliveryAccountContract accountContract = deliveryAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
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
		DeliveryTransaction currentTransaction = super.findCurrDTrans(code);
		if (currentTransaction == null ) {
			return;
		}
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = getMultipleVos(currentTransaction, uid);
		
		Gson gson = new Gson();
		data.put("multiples", gson.toJson(listMulpitleAndStateVOs));
		toWeb(response, data);
	}
	
	private List<MulpitleAndStateVO> getMultipleVos(DeliveryTransaction transaction, long uid) {
		String code = transaction.getTransaction_code();
		int canMultiple = 0;
		if (uid > 0) {
			List<DeliveryMemberTransaction> memberTrans = deliveryMemberTransactionService.readTransactionByUserId(code, uid);
			if (memberTrans.size() > 0) {
				DeliveryMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<DeliveryPendingDelegationCache> caches = deliveryPendingDelegationCacheService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, 1, false);
				if (caches.size() > 0) {
					DeliveryPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<DeliveryPendingDelegation> pendings = deliveryPendingDelegationService.readPendingByUserIdAndTime(code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						DeliveryPendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						DeliveryPlanPendingDelegation planPending = deliveryPlanPendingDelegationService.readOneByStatus(code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
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
	
	private String tradeEx(HttpServletRequest request, HttpServletResponse response, Model model) {
		BQMember user = super.findCurrUser(request, true);
		
		List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		List<DeliveryTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
		if (types == null) {
			types = transactionTypeService.readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		List<DeliveryTransactionPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_PARTITIONS);
		if (partitions == null) {
			partitions = deliveryTransactionPartitionService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_PARTITIONS,BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		request.setAttribute("transTypes", types);
		request.setAttribute("trans", transactionList);
		request.setAttribute("partitions", partitions);
		request.setAttribute("listMap", currDTrans(transactionList));
		
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		if (user != null) {
			BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
			if (payword != null && payword.getState() != 0) {
				model.addAttribute("payPwd", true);
			} else {
				model.addAttribute("payPwd", false);
			}
			
			BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
			if (bqs == null) {
				model.addAttribute("applayerror", true);
			} else {
				if (bqs.getF_user_safetype() == 0) {
					model.addAttribute("applayerror", true);
				}
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
		
		model.addAttribute("transactionList", transactionList);
		DeliveryTransaction currentTransaction = null;
		if (!StringUtil.isEmpty(transactionCode)) {
			for (DeliveryTransaction transaction : transactionList) {
				if (transaction.getTransaction_code().equalsIgnoreCase(transactionCode)) {
					currentTransaction = transaction;
					break;
				}
			}
		}
		
		if (currentTransaction == null && transactionList.size() > 0) {
			currentTransaction = transactionList.get(0);
		}
		if (currentTransaction == null ) {
			model.addAttribute("hashy", false);
			return "jsp/full_trade";
		}
		
		DeliveryTransactionType type = transactionTypeService.readTransactionType(currentTransaction.getTransaction_type());
		if (type == null) {
			model.addAttribute("hashy", false);
			return "jsp/full_trade";
		}
		
		if (currentTransaction.getTransaction_kind() == 2) {
			if (user == null) {
				return super.toLogin(); 
			}
			
			if (user.getHas_open_position() == 0) {
				model.addAttribute("showRiskWarning", true);
			}
		}
		
		model.addAttribute("minKcNums", type.getMin_kc_nums().doubleValue());
		model.addAttribute("minFloatPrice", type.getMin_float_price().doubleValue());
		model.addAttribute("typeCode", currentTransaction.getTransaction_type_code().toUpperCase());
		model.addAttribute("typeCodeName", currentTransaction.getTransaction_show_name());
		model.addAttribute("hashy", true);
		model.addAttribute("currentTransaction", currentTransaction);
		if (currentTransaction.getTransaction_kind() != null && currentTransaction.getTransaction_kind() == 1
				&& !bqNotradetimeService.readTradeTime(currentTransaction.getTransaction_id())) {
			model.addAttribute("isTradeTime", false);
		} else {
			model.addAttribute("isTradeTime", true);
		}

		BigDecimal eatRate = BigDecimal.ZERO;
		BigDecimal pendingRate = BigDecimal.ZERO;
		if (user != null) {
			BQMemberLevel userlevel = getLevelByUserBfx(user);
			model.addAttribute("userlevel", userlevel);
			eatRate = userlevel.getEat_commission_rate();
			pendingRate = userlevel.getPending_commission_rate();
			DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				eatRate = transactionFeeRate.getEat_commission_rate();
				pendingRate = transactionFeeRate.getPending_commission_rate();
			}
		}

		int digits = Utils.getDigitsFromMinFloat(type.getMin_float_price());
		int amountDigits = Utils.getAmountDigtis(currentTransaction.getTransaction_code());
		model.addAttribute("amountDigits", amountDigits);
		
		// 对应币种指数
		BigDecimal btc_index_price = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		model.addAttribute("btc_index_price", btc_index_price.setScale(digits, BigDecimal.ROUND_HALF_UP));
		
		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (currentTransaction.getTransaction_kind() != null && currentTransaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + currentTransaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (currentTransaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO  + currentTransaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = deliveryTradeLogService.readLastHourAgoByCode(currentTransaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + currentTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + currentTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = deliveryTradeLogService.readLastDayAgoByCode(currentTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + currentTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}

		// 合约价格浮动
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			
			BigDecimal rateUp = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				DeliveryTransactionRate index_price_rate = deliveryTransactionRateService.getByCode(currentTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		} else {
			minIndexPrice = BigDecimal.ZERO;
			maxIndexPrice = new BigDecimal(100000);
		}
		
		if (oneDayMin != null && oneDayMin.compareTo(minIndexPrice) > 0) {
			minIndexPrice = oneDayMin;
		}
		
		if (oneDayMax != null && oneDayMax.compareTo(maxIndexPrice) < 0) {
			maxIndexPrice = oneDayMax;
		}
		
		if (currentTransaction.getTransaction_kind() == 2) {
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + currentTransaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
		
		model.addAttribute("maxBuy", maxIndexPrice);
		model.addAttribute("minSell", minIndexPrice);
		model.addAttribute("digits", digits);
		
		DeliveryLastTicker ticker = deliveryLastTickerService.readByCode(currentTransaction.getTransaction_code());
		if (ticker == null) {
			ticker = new DeliveryLastTicker();
			ticker.setF_ticker_high(BigDecimal.ZERO);
			ticker.setF_ticker_low(BigDecimal.ZERO);
			ticker.setF_ticker_vol(BigDecimal.ZERO);
			ticker.setF_ticker_trades(BigDecimal.ZERO);
		} 
		
		model.addAttribute("todayHigh", ticker.getF_ticker_high().setScale(digits, BigDecimal.ROUND_DOWN)); // 今日最高
		model.addAttribute("todayLow", ticker.getF_ticker_low().setScale(digits, BigDecimal.ROUND_DOWN)); // 今日最低
		model.addAttribute("tradevols", ticker.getF_ticker_vol().setScale(digits, BigDecimal.ROUND_DOWN)); // 因为定时器里面获取的成交量是24小时前的成交量，时间不对照，所以此方法不用memcache
		model.addAttribute("trades", ticker.getF_ticker_trades().setScale(digits, BigDecimal.ROUND_DOWN));
		
		// 账户信息
		if (user != null) {
			BQAccount bqAccount = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
			model.addAttribute("bqAccount", bqAccount);
		} else {
			BQAccount bqAccount = new BQAccount();
			bqAccount.setBtc(BigDecimal.ZERO);
			bqAccount.setBtc_locked(BigDecimal.ZERO);
			model.addAttribute("bqAccount", bqAccount);
		}

		BigDecimal gift = BigDecimal.ZERO;
		
		if (user != null) {
			DeliveryAccountContract accountContract = deliveryAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
			model.addAttribute("userAccountBtc", accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN)); // 当前可用
			BigDecimal minbtc = accountContract.getF_btc().compareTo(accountContract.getF_initbtc()) >= 0 ? accountContract.getF_initbtc() : accountContract
					.getF_btc();
			model.addAttribute("userAccountGainBtc", minbtc.setScale(4, RoundingMode.DOWN));// 可转出
		} else {
			model.addAttribute("userAccountBtc", BigDecimal.ZERO); // 当前可用
			model.addAttribute("userAccountGainBtc", BigDecimal.ZERO);// 可转出
		}

		// 持仓信息
		List<DeliveryMemberTransaction> buyMemberTransaction = new ArrayList<DeliveryMemberTransaction>();
		List<DeliveryMemberTransaction> sellMemberTransaction = new ArrayList<DeliveryMemberTransaction>();
		
		if (user != null) {
			buyMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
					.getTransaction_code(), Contents.DIRECTION_BUY);
			sellMemberTransaction = deliveryMemberTransactionService.readBQMemberTrasaction(user.getF_user_id(), currentTransaction
					.getTransaction_code(), Contents.DIRECTION_SELL);
		}

		// 获取当前最新成交价
		BigDecimal lastPrice = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(currentTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = deliveryPendingDelegationService.getBuyTop5(currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentTransaction.getTransaction_code() + BTCQMemecacheKeys.D_SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = deliveryPendingDelegationService.getSellTop5(currentTransaction.getTransaction_code());
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
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
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
			for (DeliveryMemberTransaction memberTransaction : buyMemberTransaction) {
				int buyStopLoss = deliveryStopLossService.readHasStopLoss(memberTransaction.getMt_id());
				BQTransVo transVo = deliveryMemberTransactionService.readHandleData(memberTransaction, 1, markPrice, eatRate, buyStopLoss, 
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
				if (transVo != null) {
					listTransVos.add(transVo);
				}
				
				BigDecimal trade_multiple = memberTransaction.getTrade_multiple();
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
			for (DeliveryMemberTransaction memberTransaction : sellMemberTransaction) {
				int sellStopLoss = deliveryStopLossService.readHasStopLoss(memberTransaction.getMt_id());
				BQTransVo transVo = deliveryMemberTransactionService.readHandleData(memberTransaction, 2, markPrice, eatRate, sellStopLoss, 
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
				if (transVo != null) {
					listTranssellVos.add(transVo);
				}
				
				BigDecimal trade_multiple = memberTransaction.getTrade_multiple();
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
		
		List<DeliveryPendingDelegation> pdList = new ArrayList<DeliveryPendingDelegation>();
		if (user != null) {
			pdList = deliveryPendingDelegationService
				.readAllByUserId(currentTransaction.getTransaction_code(), "", "", user.getF_user_id(), "");
		}
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (DeliveryPendingDelegation pd : pdList) {
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
		
		List<DeliveryPendingDelegationCache> pdCacheList = new ArrayList<DeliveryPendingDelegationCache>();
		if (user != null) {
			pdCacheList = deliveryPendingDelegationCacheService.readPendingByUserIdAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, 0, 0, 0, true);
		}
		
		for (DeliveryPendingDelegationCache cache : pdCacheList) {
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
		List<DeliveryPlanPendingDelegation> planPdList = new ArrayList<DeliveryPlanPendingDelegation>();
		if (user != null) {
			planPdList = deliveryPlanPendingDelegationService.readByUserId(currentTransaction.getTransaction_code(), user.getF_user_id(), 0, -1);
		}
		for (DeliveryPlanPendingDelegation pd : planPdList) {
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
		long userId = user == null ? 0 : user.getF_user_id();
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
//		BigDecimal fundingFee = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_RATE + currentTransaction.getTransaction_code());
//		model.addAttribute("fundingFeeRate", fundingFee);
		
		List<DeliveryTradeLog> trades = new ArrayList<DeliveryTradeLog>();
		if (user != null) {
			long morningTime = TimeUtil.getTimesmorning();
			long t7 = morningTime - 6 * 24 * 3600;
			trades = deliveryTradeLogService.readByUserAndTime(currentTransaction.getTransaction_code(), user.getF_user_id(), -1, "", t7, 0, 50);
		}
		model.addAttribute("tradeLogList", trades);
		
		List<DeliveryTransaction> listTransactions = super.findCurrDTrans();
		model.addAttribute("transactionList", listTransactions);
		model.addAttribute("listMap", currDTrans(listTransactions));
		
		if (user != null) {
			BQMemberFavoriteMarket fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), Contents.MARKET_TYPE_DELIVERY);
			if (fav != null) {
				request.setAttribute("favorite", fav.getCodes());
			}
		}

		return "jsp/full_trade";
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
		return tradeEx(request, response, model);
	}
	
	@RequestMapping("/full")
	public String fullTrade(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		tradeEx(request, response, model);
		return "jsp/full_trade";
	}
	
	/**
	 * 历史委托查询
	 * 
	 * @return
	 */
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
		List<DeliveryTransaction> transactionList = super.findCurrDTrans();
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		DeliveryTransaction currentTransaction = null;
		for (DeliveryTransaction transaction : transactionList) {
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

		PagerModel tradeLogList = new PagerModel();
		if (StringUtil.isEmpty(czlx) || "all".equalsIgnoreCase(czlx)) {
			tradeLogList = deliveryTradeLogService.readByUserIdAndTransaction(bqCode, user.getF_user_id(), 0, startTime, endTime);
			model.addAttribute("currentCzlx", "all");
		} else {
			tradeLogList = deliveryTradeLogService.readByUserIdAndTransaction(bqCode, user.getF_user_id(), Integer.valueOf(czlx), startTime, endTime);
			model.addAttribute("currentCzlx", czlx);
		}

		model.addAttribute("tradeLogList", tradeLogList);
		model.addAttribute("pageN", "trade/tradeQuery.do");
		model.addAttribute("issz", false);

		if (iTranstype == Contents.BQTRANS_TYPE_SZ)
			return "jsp/trade_query_sz";
		else
			return "jsp/trade_query";
	}
	
	private String pendingQuerydo(HttpServletRequest request, Model model, int iTranstype, BQMember user) {
		List<DeliveryTransaction> transactionList = super.findCurrDTrans();
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		DeliveryTransaction currentTransaction = null;
		for (DeliveryTransaction transaction : transactionList) {
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
		
		pendingLogList = deliveryPendingLogService.readPagerMode(bqCode, user.getF_user_id(), tradeType, startTime, endTime);

		model.addAttribute("pendingLogList", pendingLogList);
		model.addAttribute("pageN", "trade/pendingQuery.do");

		return "jsp/pending_query";
	}
}
