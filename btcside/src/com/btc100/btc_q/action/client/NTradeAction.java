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
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NLastTicker;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberDepositOnoff;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionPartition;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.NLastTickerService;
import com.btc100.btc_q.service.BQMemberDepositOnoffService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionPartitionService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
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
@RequestMapping("/ntrade/{partition}")
public class NTradeAction extends BaseAction {

	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NTransactionTypeService transactionTypeService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQMemberDepositOnoffService bqMemberDepositOnoffService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NStopLossService bqStopLossService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private NAccountContractService bqAccountContractService;
	@Autowired
	private NTransactionRateService bqTransactionRateService;
	@Autowired
	private NLastTickerService bqLastTickerService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private NTransactionPartitionService bqTransactionPartitionService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private NPlanPendingDelegationService planNPendingDelegationService;
	@Autowired
	private NPendingLogService pendingLogService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private NTransactionFeeRateService bqTransactionFeeRateService;
	
	@RequestMapping("/getFloatGainLoss")
	public void getFloatGaiinLoss(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}

		List<NMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<NMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);

		JSONObject data = new JSONObject();
		
		// 盈亏计算...
		List<BQTransVo> listTransBuyVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (NMemberTransaction memberTransaction : buyMemberTransaction) {
				BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
				BigDecimal deposit = memberTransaction.getDeposit().setScale(4, BigDecimal.ROUND_DOWN);
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
			for (NMemberTransaction memberTransaction : sellMemberTransaction) {
				BigDecimal ykRate = BigDecimal.ZERO, ykdiff = BigDecimal.ZERO;	// 盈亏变动
				BigDecimal deposit = memberTransaction.getDeposit().setScale(4, BigDecimal.ROUND_DOWN);
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
	public void tickersData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		toWeb(response, gson.toJson(super.currNTrans(partition, super.findNCurrTrans(partition))));
	}
	
	@RequestMapping("/jisuanMemTransData")
	public void jisuanMemTransData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			toWeb(response, "not login");
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<NMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<NMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		JSONObject data = new JSONObject();
		Gson gson = new Gson();
		data.put("buyMemberTransaction", gson.toJson(buyMemberTransaction));
		data.put("sellMemberTransaction", gson.toJson(sellMemberTransaction));
		
		NAccountContract accountContract = bqAccountContractService.readByUidCode(partition, user.getF_user_id(), currentTransaction.getTransaction_code());
		data.put("balance", accountContract.getF_btc().add(accountContract.getF_lockbtc()));
		data.put("coefficient", currentTransaction.getTrade_coefficient().doubleValue());
		Integer digits = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			NTransactionType transType = transactionTypeService.readTransactionType(partition, currentTransaction.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		data.put("digits", digits);
		
		toWeb(response, data.toString());
	}

	@RequestMapping("/memTransData")
	public void memTransData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		NTransactionType type = transactionTypeService.readTransactionType(partition, currentTransaction.getTransaction_type());
		if (type == null) {
			return;
		}
		
		JSONObject data = new JSONObject();
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + currentTransaction.getTransaction_code());
		if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
			markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = pendingDelegationService.getBuyTop5(partition, currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = pendingDelegationService.getSellTop5(partition, currentTransaction.getTransaction_code());
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
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, currentTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		// 持仓信息
		List<NMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_BUY);
		List<NMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
				.getTransaction_code(), Contents.DIRECTION_SELL);
		
		List<BQTransVo> listTransVos = new ArrayList<BQTransVo>();
		if (buyMemberTransaction != null && buyMemberTransaction.size() > 0) {
			for (NMemberTransaction bqMemberTransaction : buyMemberTransaction) {
				int buyStopLoss = planNPendingDelegationService.readWinAndLossByUserIdStatusAndMId(partition, bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), Contents.PLAN_PENDING_STATUS_INEFFECTIVE, bqMemberTransaction.getMt_id()).size() > 0 ? 1 : 0;
				if (buyStopLoss == 0) buyStopLoss = bqStopLossService.readHasStopLoss(partition, bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readHandleData(partition, bqMemberTransaction, 1, markPrice, feeRate, buyStopLoss,
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
			for (NMemberTransaction bqMemberTransaction : sellMemberTransaction) {
				int sellStopLoss = planNPendingDelegationService.readWinAndLossByUserIdStatusAndMId(partition, bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), Contents.PLAN_PENDING_STATUS_INEFFECTIVE, bqMemberTransaction.getMt_id()).size() > 0 ? 1 : 0;
				if (sellStopLoss == 0) sellStopLoss = bqStopLossService.readHasStopLoss(partition, bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readHandleData(partition, bqMemberTransaction, 2, markPrice, feeRate, sellStopLoss,
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
	public void historyPendingData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long startTime = morningTime - 6 * 24 * 3600;
		List<NPendingLog> pdLogList = pendingLogService.readListByUserId(partition, transactionCode, user.getF_user_id(), 0, "", startTime, 0, 50);

		Gson gson = new Gson();
		toWeb(response, gson.toJson(pdLogList));
	}
	
	@RequestMapping("/pendingData")
	public void pendingData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<NPendingDelegation> pdList = pendingDelegationService
				.readAllByUserId(partition, currentTransaction.getTransaction_code(), "", "", user.getF_user_id(), "");
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (NPendingDelegation pd : pdList) {
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
		
		List<NPendingDelegationCache> pdCacheList = new ArrayList<NPendingDelegationCache>();
		if (user != null) {
			pdCacheList = pendingDelegationCacheService.readPendingByUserIdAndTime(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), 0, 0, 0, 0, true);
		}
		
		for (NPendingDelegationCache cache : pdCacheList) {
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
		Map<String, Object> params = new HashMap<>();
		params.put("data", listPending);
		params.put("size", 0);
		if (listPending.size() > 0) {
			params.put("size", listPending.size());
		}
		toWeb(response, gson.toJson(params));
	}
	
	@RequestMapping("/planPendingData")
	public void planPendingData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		List<PlanPendingDelegationVO> planPdVOList = new ArrayList<PlanPendingDelegationVO>();
		List<NPlanPendingDelegation> planPdList = planNPendingDelegationService.readByUserId(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), 0, -1);
		for (NPlanPendingDelegation pd : planPdList) {
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
			data.setTrade_type(pd.getTrade_type());
			data.setStop_loss_type_id(pd.getStop_loss_type_id());

			planPdVOList.add(data);
		}
		
		Gson gson = new Gson();
		Map<String, Object> params = new HashMap<>();
		params.put("data", planPdVOList);

		int index = 0;
		for (PlanPendingDelegationVO pendingDelegationVO : planPdVOList) {
			if (pendingDelegationVO.getStatus() == Contents.PLAN_PENDING_STATUS_INEFFECTIVE) {
				index += 1;
			}
		}
		params.put("size", index);

		toWeb(response, gson.toJson(params));
	}
	
	@RequestMapping("/tradeLogData")
	public void tradeLogData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);	
		if (user == null) {
			return;
		}
		
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long t7 = morningTime - 6 * 24 * 3600;
		
		List<NTradeLog> trades = tradeLogService.readByUserAndTime(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), -1, "", t7, 0, 50);
		Gson gson = new Gson();
		toWeb(response, gson.toJson(trades));
	}
	
	@RequestMapping("/tradedata")
	public void getDataByCode(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}
		
		JSONObject gson = new JSONObject();

		// 当前合约
		String transactionCode = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, transactionCode);
		if (currentTransaction == null ) {
			return;
		}

		// 账户信息
		BigDecimal gift = BigDecimal.ZERO;
		NAccountContract accountContract = bqAccountContractService.readByUidCode(partition, user.getF_user_id(), currentTransaction.getTransaction_code());
		gson.put("userAccountBtc", accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN));
		
		toWeb(response, gson.toString());
	}
	
	@RequestMapping("/getMultiple")
	public void getMultiple(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		long uid = 0;
		BQMember user = super.findCurrUser(request, true);
		if (user != null) {
			uid = user.getF_user_id();
		}
		
		JSONObject data = new JSONObject();
		
		// 当前合约
		String code = request.getParameter("transactionCode");
		NTransaction currentTransaction = super.findNCurrTrans(partition, code);
		if (currentTransaction == null ) {
			return;
		}
		
		// 杠杆
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = getMultipleVos(partition, currentTransaction, uid);
		
		Gson gson = new Gson();
		data.put("multiples", gson.toJson(listMulpitleAndStateVOs));
		toWeb(response, data);
	}
	
	private List<MulpitleAndStateVO> getMultipleVos(String partition, NTransaction bqTransaction, long uid) {
		String code = bqTransaction.getTransaction_code();
		int canMultiple = 0;

		if (uid > 0) {
			List<NMemberTransaction> memberTrans = bqMemberTransactionService.readTransactionByUserId(partition, code, uid);
			if (memberTrans.size() > 0) {
				NMemberTransaction trans = memberTrans.get(0);
				canMultiple = trans.getTrade_multiple().intValue();
			} else {
				List<NPendingDelegationCache> caches = pendingDelegationCacheService.readPendingByUserIdAndTime(partition, code, uid, 0, 0, 0, 1, false);
				if (caches.size() > 0) {
					NPendingDelegationCache cache = caches.get(0);
					canMultiple = cache.getMultiple();
				} else {
					List<NPendingDelegation> pendings = pendingDelegationService.readPendingByUserIdAndTime(partition, code, uid, 0, 0, 0, "", 1);
					if (pendings.size() > 0) {
						NPendingDelegation pending = pendings.get(0);
						canMultiple = pending.getMultiple();
					} else {
						NPlanPendingDelegation planPending = planNPendingDelegationService.readOneByStatus(partition, code, uid, Contents.PLAN_PENDING_STATUS_INEFFECTIVE, 0);
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
	
	private String tradeEx(String partition, HttpServletRequest request, HttpServletResponse response, Model model) {
		BQMember user = super.findCurrUser(request, true);
		
		List<NTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(partition, Contents.TRANSACTION_STATE_ZC);
		}
		
		List<NTransactionPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_PARTITIONS + "_" + partition);
		if (partitions == null) {
			partitions = bqTransactionPartitionService.readAll(partition);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_PARTITIONS + "_" + partition, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		request.setAttribute("partitions", partitions);
		request.setAttribute("listMap", currNTrans(partition, transactionList));
		
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
		
		NTransaction currentTransaction = null;
		if (!StringUtil.isEmpty(transactionCode)) {
			for (NTransaction bqTransaction : transactionList) {
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
			return "jsp/full_trade";
		}
		
		NTransactionType type = transactionTypeService.readTransactionType(partition, currentTransaction.getTransaction_type());
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
				&& !bqNotradetimeService.readTradeTime(partition, currentTransaction.getTransaction_id())) {
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
			NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, currentTransaction.getTransaction_code(), userlevel.getLevel_id());
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
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + currentTransaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (currentTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO +  partition + "_" + currentTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(partition, currentTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + currentTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO +  partition + "_" + currentTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(partition, currentTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO +  partition + "_" + currentTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
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
			
			BigDecimal rateUp = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, currentTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		} 
		
//		else {
//			minIndexPrice = BigDecimal.ZERO;
//			maxIndexPrice = new BigDecimal(100000);
//		}
		
		if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
			minIndexPrice = oneDayMin;
		}
		
		if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
			maxIndexPrice = oneDayMax;
		}
		
//		if (currentTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + currentTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
//		}
		
		model.addAttribute("maxBuy", maxIndexPrice);
		model.addAttribute("minSell", minIndexPrice);
		model.addAttribute("digits", digits);
		
		NLastTicker bqLastTicker = bqLastTickerService.readByCode(partition, currentTransaction.getTransaction_code());
		if (bqLastTicker == null) {
			bqLastTicker = new NLastTicker();
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
		if (user != null) {
			BQAccount bqAccount = bqAccountService.readAccount(user.getF_user_id(), currentTransaction.getCurrency_type_id());
			model.addAttribute("bqAccount", bqAccount);
		} else {
			BQAccount bqAccount = new BQAccount();
			bqAccount.setBtc(BigDecimal.ZERO);
			bqAccount.setBtc_locked(BigDecimal.ZERO);
			model.addAttribute("bqAccount", bqAccount);
		}

		// 保证金自动追加
		if (user != null) {
			BQMemberDepositOnoff depositOnOff = bqMemberDepositOnoffService.readByUserId(user.getF_user_id());
			model.addAttribute("depositOnOff", depositOnOff);
		} else {
			model.addAttribute("depositOnOff", false);
		}

		BigDecimal gift = BigDecimal.ZERO;
		
		if (user != null) {
			NAccountContract accountContract = bqAccountContractService.readByUidCode(partition, user.getF_user_id(), currentTransaction.getTransaction_code());
			model.addAttribute("userAccountBtc", accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN)); // 当前可用
			BigDecimal minbtc = accountContract.getF_btc().compareTo(accountContract.getF_initbtc()) >= 0 ? accountContract.getF_initbtc() : accountContract
					.getF_btc();
			model.addAttribute("userAccountGainBtc", minbtc.setScale(4, RoundingMode.DOWN));// 可转出
		} else {
			model.addAttribute("userAccountBtc", BigDecimal.ZERO); // 当前可用
			model.addAttribute("userAccountGainBtc", BigDecimal.ZERO);// 可转出
		}

		// 持仓信息
		List<NMemberTransaction> buyMemberTransaction = new ArrayList<NMemberTransaction>();
		List<NMemberTransaction> sellMemberTransaction = new ArrayList<NMemberTransaction>();
		
		if (user != null) {
			buyMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
					.getTransaction_code(), Contents.DIRECTION_BUY);
			sellMemberTransaction = bqMemberTransactionService.readMemberTrasaction(partition, user.getF_user_id(), currentTransaction
					.getTransaction_code(), Contents.DIRECTION_SELL);
		}

		// 获取当前最新成交价
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(partition, currentTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = pendingDelegationService.getBuyTop5(partition, currentTransaction.getTransaction_code());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(partition + "_" + currentTransaction.getTransaction_code() + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = pendingDelegationService.getSellTop5(partition, currentTransaction.getTransaction_code());
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
		
		BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + currentTransaction.getTransaction_code());
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
			for (NMemberTransaction bqMemberTransaction : buyMemberTransaction) {
				int buyStopLoss = planNPendingDelegationService.readWinAndLossByUserIdStatusAndMId(partition, bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), 0, bqMemberTransaction.getMt_id()).size() > 0 ? 1 : 0;
				if (buyStopLoss == 0) buyStopLoss = bqStopLossService.readHasStopLoss(partition, bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readHandleData(partition, bqMemberTransaction, 1, markPrice, eatRate, buyStopLoss,
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
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
			for (NMemberTransaction bqMemberTransaction : sellMemberTransaction) {
				int sellStopLoss = planNPendingDelegationService.readWinAndLossByUserIdStatusAndMId(partition, bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), 0, bqMemberTransaction.getMt_id()).size() > 0 ? 1 : 0;
				if (sellStopLoss == 0) sellStopLoss = bqStopLossService.readHasStopLoss(partition, bqMemberTransaction.getMt_id());
				BQTransVo transVo = bqMemberTransactionService.readHandleData(partition, bqMemberTransaction, 2, markPrice, eatRate, sellStopLoss,
						digits, buy1Price, sell1Price, currentTransaction.getTrade_coefficient());
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
		
		List<NPendingDelegation> pdList = new ArrayList<NPendingDelegation>();
		if (user != null) {
			pdList = pendingDelegationService
				.readAllByUserId(partition, currentTransaction.getTransaction_code(), "", "", user.getF_user_id(), "");
		}
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		for (NPendingDelegation pd : pdList) {
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
		
		List<NPendingDelegationCache> pdCacheList = new ArrayList<NPendingDelegationCache>();
		if (user != null) {
			pdCacheList = pendingDelegationCacheService.readPendingByUserIdAndTime(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), 0, 0, 0, 0, true);
		}
		
		for (NPendingDelegationCache cache : pdCacheList) {
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
		List<NPlanPendingDelegation> planPdList = new ArrayList<NPlanPendingDelegation>();
		if (user != null) {
			planPdList = planNPendingDelegationService.readByUserId(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), 0, -1);
		}
		for (NPlanPendingDelegation pd : planPdList) {
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
			data.setTrade_type(pd.getTrade_type());
			data.setStop_loss_type_id(pd.getStop_loss_type_id());
			
			planPdVOList.add(data);
		}
		model.addAttribute("planPdVOList", planPdVOList);
		
		// 杠杆
		long userId = user == null ? 0 : user.getF_user_id();
		List<MulpitleAndStateVO> listMulpitleAndStateVOs = getMultipleVos(partition, currentTransaction, userId);
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
		BigDecimal fundingFee = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_RATE + partition + "_" + currentTransaction.getTransaction_code());
		model.addAttribute("fundingFeeRate", fundingFee);
		
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
		if (user != null) {
			long morningTime = TimeUtil.getTimesmorning();
			long t7 = morningTime - 6 * 24 * 3600;
			trades = tradeLogService.readByUserAndTime(partition, currentTransaction.getTransaction_code(), user.getF_user_id(), -1, "", t7, 0, 50);
		}
		model.addAttribute("tradeLogList", trades);
		
		if (user != null) {
			BQMemberFavoriteMarket fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), partition);
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
	public String trade(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		try {
			tradeEx(partition, request, response, model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "jsp/full_trade";
	}
	
	@RequestMapping("/full")
	public String fullTrade(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		tradeEx(partition, request, response, model);
		return "jsp/full_trade";
	}
	
	/**
	 * 历史委托查询
	 * 
	 * @return
	 */
	@RequestMapping("/pendingQuery")
	public String pendingQuery(@PathVariable("partition") String partition, HttpServletRequest request, Model model) {
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
		
		return pendingQuerydo(partition, request, model, typeId, user);
	}

	/**
	 * 交易查询
	 * 
	 * @return
	 */
	@RequestMapping("/tradeQuery")
	public String tradeQuery(@PathVariable("partition") String partition, HttpServletRequest request, Model model) {
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
		return tradeQuerydo(partition, request, model, typeId, user);
	}

	private String tradeQuerydo(String partition, HttpServletRequest request, Model model, int iTranstype, BQMember user) {
		List<NTransaction> transactionList = super.findNCurrTrans(partition);
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		NTransaction currentTransaction = null;
		for (NTransaction transaction : transactionList) {
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
			tradeLogList = tradeLogService.readByUserIdAndTransaction(partition, bqCode, user.getF_user_id(), 0, startTime, endTime);
			model.addAttribute("currentCzlx", "all");
		} else {
			tradeLogList = tradeLogService.readByUserIdAndTransaction(partition, bqCode, user.getF_user_id(), Integer.valueOf(czlx), startTime, endTime);
			model.addAttribute("currentCzlx", czlx);
		}

		model.addAttribute("tradeLogList", tradeLogList);
		model.addAttribute("pageN", "ntrade/" + partition + "/tradeQuery.do");
		model.addAttribute("issz", false);

		if (iTranstype == Contents.BQTRANS_TYPE_SZ)
			return "jsp/trade_query_sz";
		else
			return "jsp/trade_query";
	}
	
	private String pendingQuerydo(String partition, HttpServletRequest request, Model model, int iTranstype, BQMember user) {
		List<NTransaction> transactionList = super.findNCurrTrans(partition);
		if (transactionList == null || transactionList.size() <= 0) {
			return "jsp/pending_query";
		}
		
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("pendingcode");
		
		NTransaction currentTransaction = null;
		for (NTransaction transaction : transactionList) {
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
		
		pendingLogList = pendingLogService.readPagerMode(partition, bqCode, user.getF_user_id(), tradeType, startTime, endTime);

		model.addAttribute("pendingLogList", pendingLogList);
		model.addAttribute("pageN", "trade/pendingQuery.do");

		return "jsp/pending_query";
	}
}
