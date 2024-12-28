package com.btc100.btc_q.action.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQStopLoss;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserApiSetting;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.entity.DeliveryStopLoss;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.MemberTradeDetail;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NMemberTradeDetail;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PendingLog;
import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.entity.VStopLoss;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.service.BQStopLossService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionRateService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUserApiSettingService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.SpotPendingLogService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VAccountContractService;
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
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.BQTransVo;
import com.btc100.btc_q.vo.MemberTransactionVo;
import com.btc100.btc_q.vo.MulpitleAndStateVO;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.PendingLogVO;
import com.btc100.btc_q.vo.PlanPendingDelegationVO;
import com.btc100.btc_q.vo.TradeDetailVo;
import com.btc100.btc_q.vo.TradeLogVo;
import com.google.gson.Gson;

/**
 * 交易API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/tradeApi")
public class TradeApiAction extends BaseApiAction {
	
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	private BQTransactionTypeService bqTransactionTypeService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private DeliveryTransactionTypeService dTransactionTypeService;
	@Autowired
	private BQTransactionRateService bqTransactionRateService;
	@Autowired
	private VTransactionRateService vTransactionRateService;
	@Autowired
	private DeliveryTransactionRateService dTransactionRateService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationCacheService dPendingDelegationCacheService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private PendingLogService pendingLogService;
	@Autowired
	private VPendingLogService vPendingLogService;
	@Autowired
	private DeliveryPendingLogService dPendingLogService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private DeliveryTradeLogService dTradeLogService;
	@Autowired
	private BQStopLossService bqStopLossService;
	@Autowired
	private VStopLossService vStopLossService;
	@Autowired
	private DeliveryStopLossService dStopLossService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private SpotPendingDelegationCacheService spotPendingDelegationCacheService;
	@Autowired
	private SpotPendingLogService spotPendingLogService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private PlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private VPlanPendingDelegationService vPlanPendingDelegationService;
	@Autowired
	private DeliveryPlanPendingDelegationService dPlanPendingDelegationService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private BQUserApiSettingService bqUserApiSettingService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private DeliveryTransactionFeeRateService dTransactionFeeRateService;
	@Autowired
	private NPendingLogService nPendingLogService;
	@Autowired
	private NNotradetimeService nNotradetimeService;
	@Autowired
	private NTransactionFeeRateService nTransactionFeeRateService;
	@Autowired
	private NTransactionRateService nTransactionRateService;
	@Autowired
	private NTradeLogService nTradeLogService;
	@Autowired
	private NAccountContractService nAccountContractService;
	@Autowired
	private NMemberTradeDetailService nMemberTradeDetailService;
	
//	private static final Logger logger = Logger.getLogger(TradeApiAction.class);
	
	@RequestMapping("/historyOrders")
	public String getHistoryOrders(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		if ("spot".equals(type)) {
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(symbol);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<SpotPendingLog> pendingLogs = spotPendingLogService.readListByUserId(symbol, userId, nFrom, direct, start, end, size);
			for (SpotPendingLog pendingLog : pendingLogs) {
				PendingLogVO vo = new PendingLogVO();
				vo.setRecordId(pendingLog.getLog_id());
				vo.setOrderId(pendingLog.getPending_no() + "");
				vo.setSymbol(symbol);
				vo.setAmount(pendingLog.getDelegation_count().doubleValue());
				vo.setDealAmount(pendingLog.getDeal_count().doubleValue());
				vo.setUndealAmount(pendingLog.getUndeal_count().doubleValue());
				vo.setPrice(pendingLog.getDelegation_price().doubleValue());
				vo.setTradeAvgPrice(pendingLog.getAvg_price().doubleValue());
				vo.setCreateTime(pendingLog.getDelegation_time());
				vo.setUpdateTime(pendingLog.getDateline());
				vo.setDirection(pendingLog.getDirection());
				vo.setIncome(pendingLog.getIncome().doubleValue());
				
				int action = pendingLog.getAction();
				if (action == 0) {
					vo.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					vo.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					vo.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					vo.setState(Contents.PENDING_STATE_SUBMITTING);
				}

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if ("vcontract".equals(type)) {
			VTransaction currentTransaction = super.findCurrVTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<VPendingLog> pendingLogs = vPendingLogService.readListByUserId(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (VPendingLog pendingLog : pendingLogs) {
				PendingLogVO vo = new PendingLogVO();
				vo.setRecordId(pendingLog.getLog_id());
				vo.setOrderId(pendingLog.getPending_no() + "");
				vo.setSymbol(symbol);
				vo.setAmount(pendingLog.getDelegation_count().doubleValue());
				vo.setDealAmount(pendingLog.getDeal_count().doubleValue());
				vo.setUndealAmount(pendingLog.getUndeal_count().doubleValue());
				vo.setPrice(pendingLog.getDelegation_price().doubleValue());
				vo.setTradeAvgPrice(pendingLog.getAvg_price().doubleValue());
				vo.setCreateTime(pendingLog.getDelegation_time());
				vo.setUpdateTime(pendingLog.getDateline());
				vo.setTradeType(pendingLog.getTrade_type_id());
				vo.setMultiple(pendingLog.getMultiple());
				vo.setDeposit(pendingLog.getDeposit().doubleValue());
				vo.setIncome(pendingLog.getIncome().doubleValue());
				
				int action = pendingLog.getAction();
				if (action == 0) {
					vo.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					vo.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					vo.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					vo.setState(Contents.PENDING_STATE_SUBMITTING);
				}
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if("delivery".equals(type)) {
			DeliveryTransaction currentTransaction = super.findCurrDTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<DeliveryPendingLog> pendingLogs = dPendingLogService.readListByUserId(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (DeliveryPendingLog pendingLog : pendingLogs) {
				PendingLogVO vo = new PendingLogVO();
				vo.setRecordId(pendingLog.getLog_id());
				vo.setOrderId(pendingLog.getPending_no() + "");
				vo.setSymbol(symbol);
				vo.setAmount(pendingLog.getDelegation_count().doubleValue());
				vo.setDealAmount(pendingLog.getDeal_count().doubleValue());
				vo.setUndealAmount(pendingLog.getUndeal_count().doubleValue());
				vo.setPrice(pendingLog.getDelegation_price().doubleValue());
				vo.setTradeAvgPrice(pendingLog.getAvg_price().doubleValue());
				vo.setCreateTime(pendingLog.getDelegation_time());
				vo.setUpdateTime(pendingLog.getDateline());
				vo.setTradeType(pendingLog.getTrade_type_id());
				vo.setMultiple(pendingLog.getMultiple());
				vo.setDeposit(pendingLog.getDeposit().doubleValue());
				vo.setIncome(pendingLog.getIncome().doubleValue());
				
				int action = pendingLog.getAction();
				if (action == 0) {
					vo.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					vo.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					vo.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					vo.setState(Contents.PENDING_STATE_SUBMITTING);
				}

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction currentTransaction = super.findCurrTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<PendingLog> pendingLogs = pendingLogService.readListByUserId(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (PendingLog pendingLog : pendingLogs) {
				PendingLogVO vo = new PendingLogVO();
				vo.setRecordId(pendingLog.getLog_id());
				vo.setOrderId(pendingLog.getPending_no() + "");
				vo.setSymbol(symbol);
				vo.setAmount(pendingLog.getDelegation_count().doubleValue());
				vo.setDealAmount(pendingLog.getDeal_count().doubleValue());
				vo.setUndealAmount(pendingLog.getUndeal_count().doubleValue());
				vo.setPrice(pendingLog.getDelegation_price().doubleValue());
				vo.setTradeAvgPrice(pendingLog.getAvg_price().doubleValue());
				vo.setCreateTime(pendingLog.getDelegation_time());
				vo.setUpdateTime(pendingLog.getDateline());
				vo.setTradeType(pendingLog.getTrade_type_id());
				vo.setMultiple(pendingLog.getMultiple());
				vo.setDeposit(pendingLog.getDeposit().doubleValue());
				vo.setIncome(pendingLog.getIncome().doubleValue());
				
				int action = pendingLog.getAction();
				if (action == 0) {
					vo.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					vo.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					vo.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					vo.setState(Contents.PENDING_STATE_SUBMITTING);
				}

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction currentTransaction = super.findNCurrTrans(type, symbol);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NPendingLog> pendingLogs = nPendingLogService.readListByUserId(type, currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
				for (NPendingLog pendingLog : pendingLogs) {
					PendingLogVO vo = new PendingLogVO();
					vo.setRecordId(pendingLog.getLog_id());
					vo.setOrderId(pendingLog.getPending_no() + "");
					vo.setSymbol(symbol);
					vo.setAmount(pendingLog.getDelegation_count().doubleValue());
					vo.setDealAmount(pendingLog.getDeal_count().doubleValue());
					vo.setUndealAmount(pendingLog.getUndeal_count().doubleValue());
					vo.setPrice(pendingLog.getDelegation_price().doubleValue());
					vo.setTradeAvgPrice(pendingLog.getAvg_price().doubleValue());
					vo.setCreateTime(pendingLog.getDelegation_time());
					vo.setUpdateTime(pendingLog.getDateline());
					vo.setTradeType(pendingLog.getTrade_type_id());
					vo.setMultiple(pendingLog.getMultiple());
					vo.setDeposit(pendingLog.getDeposit().doubleValue());
					vo.setIncome(pendingLog.getIncome().doubleValue());
					
					int action = pendingLog.getAction();
					if (action == 0) {
						vo.setState(Contents.PENDING_STATE_SUBMITTED);
					} else if (action == 1) {
						vo.setState(Contents.PENDING_STATE_FILLED);
					} else if (action == 4) {
						vo.setState(Contents.PENDING_STATE_CANCELED);
					} else {
						vo.setState(Contents.PENDING_STATE_SUBMITTING);
					}

					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/orders")
	public String getOrders(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String states = request.getParameter("states");
		if (!StringUtil.isEmpty(states)) {
			map.put("states", states);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		String[] stateList = new String[0];
		if (!StringUtil.isEmpty(states)) {
			stateList = states.split(",");
		}
		if (stateList.length == 0) {
			stateList = new String[1];
			stateList[0] = "submitted";
		}
		
		if ("spot".equals(type)) {
			List<String> done = new ArrayList<String>();
			for (String state : stateList) {
				if (done.contains(state)) {
					continue;
				}
				
				done.add(state);
				if (state.equals("submitting")) {
					List<SpotPendingDelegationCache> pdCacheList = spotPendingDelegationCacheService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (SpotPendingDelegationCache cache : pdCacheList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(cache.getDelegation_id());
						data.setOrderId(cache.getPending_no() + "");
						data.setSymbol(symbol);
						data.setAmount(cache.getDelegation_count().doubleValue());
						data.setDealAmount(cache.getDeal_count().doubleValue());
						data.setUndealAmount(cache.getUndeal_count().doubleValue());
						data.setPrice(cache.getDelegation_price().doubleValue());
						data.setCreateTime(cache.getDelegation_time());
						data.setDirection(cache.getDirection());
						data.setState(Contents.PENDING_STATE_SUBMITTING);
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else if (state.equals("submitted")) {
					List<SpotPendingDelegation> pdList = spotPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (SpotPendingDelegation pd : pdList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(pd.getDelegation_id());
						data.setOrderId(pd.getPending_no() + "");
						data.setSymbol(symbol);
						data.setAmount(pd.getDelegation_count().doubleValue());
						data.setDealAmount(pd.getDeal_count().doubleValue());
						data.setUndealAmount(pd.getUndeal_count().doubleValue());
						data.setPrice(pd.getDelegation_price().doubleValue());
						data.setCreateTime(pd.getDelegation_time());
						data.setDirection(pd.getDirection());
						data.setState(Contents.PENDING_STATE_SUBMITTED);
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，不支持的states");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else if ("vcontract".equals(type)) {
			List<String> done = new ArrayList<String>();
			for (String state : stateList) {
				if (done.contains(state)) {
					continue;
				}
				
				done.add(state);
				if (state.equals("submitting")) {
					List<VPendingDelegationCache> pdCacheList = vPendingDelegationCacheService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (VPendingDelegationCache cache : pdCacheList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(cache.getDelegation_id());
						data.setOrderId(cache.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else if (state.equals("submitted")) {
					List<VPendingDelegation> pdList = vPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (VPendingDelegation pd : pdList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(pd.getDelegation_id());
						data.setOrderId(pd.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，不支持的states");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else if("delivery".equals(type)) {
			List<String> done = new ArrayList<String>();
			for (String state : stateList) {
				if (done.contains(state)) {
					continue;
				}
				
				done.add(state);
				if (state.equals("submitting")) {
					List<DeliveryPendingDelegationCache> pdCacheList = dPendingDelegationCacheService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (DeliveryPendingDelegationCache cache : pdCacheList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(cache.getDelegation_id());
						data.setOrderId(cache.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else if (state.equals("submitted")) {
					List<DeliveryPendingDelegation> pdList = dPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (DeliveryPendingDelegation pd : pdList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(pd.getDelegation_id());
						data.setOrderId(pd.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，不支持的states");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<String> done = new ArrayList<String>();
			for (String state : stateList) {
				if (done.contains(state)) {
					continue;
				}
				
				done.add(state);
				if (state.equals("submitting")) {
					List<PendingDelegationCache> pdCacheList = pendingDelegationCacheService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (PendingDelegationCache cache : pdCacheList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(cache.getDelegation_id());
						data.setOrderId(cache.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else if (state.equals("submitted")) {
					List<PendingDelegation> pdList = pendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
					for (PendingDelegation pd : pdList) {
						PendingDelegationVO data = new PendingDelegationVO();
						data.setDelegation_id(pd.getDelegation_id());
						data.setOrderId(pd.getPending_no() + "");
						data.setSymbol(symbol);
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
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(data)));
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，不支持的states");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<String> done = new ArrayList<String>();
				for (String state : stateList) {
					if (done.contains(state)) {
						continue;
					}
					
					done.add(state);
					if (state.equals("submitting")) {
						List<NPendingDelegationCache> pdCacheList = nPendingDelegationCacheService.readPendingByUserIdAndTime(type, symbol, userId, start, end, nFrom, direct, size);
						for (NPendingDelegationCache cache : pdCacheList) {
							PendingDelegationVO data = new PendingDelegationVO();
							data.setDelegation_id(cache.getDelegation_id());
							data.setOrderId(cache.getPending_no() + "");
							data.setSymbol(symbol);
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
							
							Gson gson = new Gson();
							array.put(new JSONObject(gson.toJson(data)));
						}
					} else if (state.equals("submitted")) {
						List<NPendingDelegation> pdList = nPendingDelegationService.readPendingByUserIdAndTime(type, symbol, userId, start, end, nFrom, direct, size);
						for (NPendingDelegation pd : pdList) {
							PendingDelegationVO data = new PendingDelegationVO();
							data.setDelegation_id(pd.getDelegation_id());
							data.setOrderId(pd.getPending_no() + "");
							data.setSymbol(symbol);
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
							
							Gson gson = new Gson();
							array.put(new JSONObject(gson.toJson(data)));
						}
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，不支持的states");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		tickerJson.put("size", array.length());
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/favorites")
	public String getFavorites(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String partition = "";
		int iType = Contents.MARKET_TYPE_CONTRACT;
		if ("vcontract".equals(type)) {
			iType = Contents.MARKET_TYPE_VCONTRACT;
		} else if ("spot".equals(type)) {
			iType = Contents.MARKET_TYPE_SPOT;
		} else if ("delivery".equals(type)) {
			iType = Contents.MARKET_TYPE_DELIVERY;
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			bqMemberService.checkVipLevel(user);
		} else {
			partition = type;
		}
		
		BQMemberFavoriteMarket fav = null;
		if (StringUtil.isEmpty(partition)) {
			fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), iType);
		} else {
			fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), partition);
		}
		
		if (fav != null) {
			userJson.put("data", fav.getCodes());
		} else {
			userJson.put("data", "");
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/changeFavorite", method = RequestMethod.POST)
	public String changeFavorite(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		String symbol = request.getParameter("symbol");
		int direc = ConvertUtil.toInt(request.getParameter("direc"), 0);
		
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String partition = "";
		int iType = Contents.MARKET_TYPE_CONTRACT;
		if ("vcontract".equals(type)) {
			iType = Contents.MARKET_TYPE_VCONTRACT;
		} else if ("spot".equals(type)) {
			iType = Contents.MARKET_TYPE_SPOT;
		} else if ("delivery".equals(type)) {
			iType = Contents.MARKET_TYPE_DELIVERY;
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			
		} else {
			partition = type;
		}
		
		BQMemberFavoriteMarket fav = null;
		if (StringUtil.isEmpty(partition)) {
			fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), iType);
		} else {
			fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), partition);
		}
		if (fav != null) {
			String[] codes = fav.getCodes().split(",");
			
			boolean change = false;
			List<String> codeList = new ArrayList<String>(codes.length);
			Collections.addAll(codeList, codes);
			if (codeList.contains(symbol)) {
				if (direc != 1) {
					codeList.remove(symbol);
					change = true;
				}
			} else {
				if (direc == 1) {
					codeList.add(symbol);
					change = true;
				}
			}
			if (!change) {
				userJson.put("data", codes);
			} else {
				String codeStr = "";
				for (int i = 0 ; i < codeList.size(); i++) {
					if (codeList.get(i).length() == 0) continue; 
					
					if (codeStr.length() > 0) {
						codeStr += ",";
					}
					
					codeStr += codeList.get(i);
				}
				fav.setCodes(codeStr);
				bqMemberFavoriteMarketService.update(fav);
				userJson.put("data", codeStr);
			}
		} else {
			if (direc == 1) {
				fav = new BQMemberFavoriteMarket();
				fav.setCodes(symbol);
				fav.setType(iType);
				fav.setF_user_id(user.getF_user_id());
				fav.setPart(partition);
				bqMemberFavoriteMarketService.save(fav);
				userJson.put("data", symbol);
			} else {
				userJson.put("data", "");
			}
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping("/getKnowWarning")
	public String getKnowWarning(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String,Object> mapToken = new HashMap<String, Object>();
		String checkToken = checkToken(request, mapToken);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) mapToken.get("user");
		JSONObject data = new JSONObject();
		data.put("know", user.getHas_open_position());
		
		tickerJson.put("data", data);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/updateKnowWarning", method = RequestMethod.POST)
	public String updateKnowWarning(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String,Object> mapToken = new HashMap<String, Object>();
		String checkToken = checkToken(request, mapToken);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) mapToken.get("user");
		bqMemberService.updateHasOpen(user.getF_user_id());
		
		JSONObject data = new JSONObject();
		data.put("know", 1);
		
		tickerJson.put("data", data);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/order")
	public String getOrder(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		PendingDelegationVO data = new PendingDelegationVO();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String pendingNo = request.getParameter("orderId");
		if (StringUtil.isEmpty(pendingNo)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有orderId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		map.put("orderId", pendingNo);
		map.put("symbol", symbol);

		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		data.setOrderId(pendingNo);
		data.setSymbol(symbol);
		
		if ("spot".equals(type)) {
			List<SpotPendingLog> pendingLogs = spotPendingLogService.readByPendingNo(symbol, pendingNo, userId);
			if (pendingLogs.size() > 0) {
				SpotPendingLog firstLog = pendingLogs.get(0);
				
				data.setAmount(firstLog.getDelegation_count().doubleValue());
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				data.setPrice(firstLog.getDelegation_price().doubleValue());
				data.setCreateTime(firstLog.getDelegation_time());
				data.setDirection(firstLog.getDirection());

				int action = 0;
				action = firstLog.getAction();
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				
				if (action == 0) {
					data.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					data.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					data.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					data.setState(Contents.PENDING_STATE_SUBMITTING);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOORDER, "无此订单");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(data)));
		} else if ("vcontract".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if("delivery".equals(type)) {
			List<DeliveryPendingLog> pendingLogs = dPendingLogService.readByPendingNo(symbol, pendingNo, userId);
			if (pendingLogs.size() > 0) {
				DeliveryPendingLog firstLog = pendingLogs.get(0);
				
				data.setAmount(firstLog.getDelegation_count().doubleValue());
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				data.setPrice(firstLog.getDelegation_price().doubleValue());
				data.setCreateTime(firstLog.getDelegation_time());
				data.setMultiple(firstLog.getMultiple());
				data.setDeposit(firstLog.getDeposit().doubleValue());

				if (firstLog.getTrade_type_id() == 1) {
					data.setPriority(Contents.PRIORITY_KC);
					data.setDirection(Contents.DIRECTION_BUY);
				} else if (firstLog.getTrade_type_id() == 2) {
					data.setPriority(Contents.PRIORITY_PC);
					data.setDirection(Contents.DIRECTION_SELL);
				} else if (firstLog.getTrade_type_id() == 3) {
					data.setPriority(Contents.PRIORITY_KC);
					data.setDirection(Contents.DIRECTION_SELL);
				} else if (firstLog.getTrade_type_id() == 4) {
					data.setPriority(Contents.PRIORITY_PC);
					data.setDirection(Contents.DIRECTION_BUY);
				}
				
				int action = 0;
				action = firstLog.getAction();
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				data.setDeposit(firstLog.getDeposit().doubleValue());
				
				if (action == 0) { 
					data.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					data.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					data.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					data.setState(Contents.PENDING_STATE_SUBMITTING);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOORDER, "无此订单");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(data)));
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<PendingLog> pendingLogs = pendingLogService.readByPendingNo(symbol, pendingNo, userId);
			if (pendingLogs.size() > 0) {
				PendingLog firstLog = pendingLogs.get(0);
				
				data.setAmount(firstLog.getDelegation_count().doubleValue());
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				data.setPrice(firstLog.getDelegation_price().doubleValue());
				data.setCreateTime(firstLog.getDelegation_time());
				data.setMultiple(firstLog.getMultiple());
				data.setDeposit(firstLog.getDeposit().doubleValue());

				if (firstLog.getTrade_type_id() == 1) {
					data.setPriority(Contents.PRIORITY_KC);
					data.setDirection(Contents.DIRECTION_BUY);
				} else if (firstLog.getTrade_type_id() == 2) {
					data.setPriority(Contents.PRIORITY_PC);
					data.setDirection(Contents.DIRECTION_SELL);
				} else if (firstLog.getTrade_type_id() == 3) {
					data.setPriority(Contents.PRIORITY_KC);
					data.setDirection(Contents.DIRECTION_SELL);
				} else if (firstLog.getTrade_type_id() == 4) {
					data.setPriority(Contents.PRIORITY_PC);
					data.setDirection(Contents.DIRECTION_BUY);
				}
				
				int action = 0;
				action = firstLog.getAction();
				data.setDealAmount(firstLog.getDeal_count().doubleValue());
				data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
				data.setDeposit(firstLog.getDeposit().doubleValue());
				
				if (action == 0) { 
					data.setState(Contents.PENDING_STATE_SUBMITTED);
				} else if (action == 1) {
					data.setState(Contents.PENDING_STATE_FILLED);
				} else if (action == 4) {
					data.setState(Contents.PENDING_STATE_CANCELED);
				} else {
					data.setState(Contents.PENDING_STATE_SUBMITTING);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOORDER, "无此订单");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(data)));
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NPendingLog> pendingLogs = nPendingLogService.readByPendingNo(type, symbol, pendingNo, userId);
				if (pendingLogs.size() > 0) {
					NPendingLog firstLog = pendingLogs.get(0);
					
					data.setAmount(firstLog.getDelegation_count().doubleValue());
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setPrice(firstLog.getDelegation_price().doubleValue());
					data.setCreateTime(firstLog.getDelegation_time());
					data.setMultiple(firstLog.getMultiple());
					data.setDeposit(firstLog.getDeposit().doubleValue());

					if (firstLog.getTrade_type_id() == 1) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_BUY);
					} else if (firstLog.getTrade_type_id() == 2) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 3) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 4) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_BUY);
					}
					
					int action = 0;
					action = firstLog.getAction();
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setDeposit(firstLog.getDeposit().doubleValue());
					
					if (action == 0) { 
						data.setState(Contents.PENDING_STATE_SUBMITTED);
					} else if (action == 1) {
						data.setState(Contents.PENDING_STATE_FILLED);
					} else if (action == 4) {
						data.setState(Contents.PENDING_STATE_CANCELED);
					} else {
						data.setState(Contents.PENDING_STATE_SUBMITTING);
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOORDER, "无此订单");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				Gson gson = new Gson();
				tickerJson.put("data", new JSONObject(gson.toJson(data)));
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 多个订单查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/order/multi")
	public String getOrderMulti(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String orderIds = request.getParameter("orderIds");
		if (StringUtil.isEmpty(orderIds)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有orderIds");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String[] ids = orderIds.split(",");
		Map<String, PendingDelegationVO > orderList = new HashMap<>();
		String idsStr = "";
		for (String id : ids) {
			if (!StringUtil.isEmpty(id)) {
				Long lid = Convert.toLong(id, 0);
				if (lid <= 0) continue;
				
				PendingDelegationVO order = new PendingDelegationVO();
				order.setOrderId(lid + "");
				order.setState("未找到此订单");
				orderList.put(id, order);
				idsStr += (lid + ",");
			}
		}
		
		if (idsStr.length() > 0) {
			idsStr = idsStr.substring(0, idsStr.length() - 1);
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，orderIds");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (orderList.size() > 50) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "订单数超过50");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		map.put("orderIds", orderIds);
		map.put("symbol", symbol);

		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map, true);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if("delivery".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NPendingLog> pendingLogs = nPendingLogService.readPendingByUserIdAndSymbol(type, symbol, userId, idsStr);
				for (NPendingLog firstLog : pendingLogs) {
					PendingDelegationVO data = orderList.get(firstLog.getPending_no() + "");
					if (data == null) continue;
					data.setAmount(firstLog.getDelegation_count().doubleValue());
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setPrice(firstLog.getDelegation_price().doubleValue());
					data.setCreateTime(firstLog.getDelegation_time());
					data.setMultiple(firstLog.getMultiple());
					data.setDeposit(firstLog.getDeposit().doubleValue());

					if (firstLog.getTrade_type_id() == 1) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_BUY);
					} else if (firstLog.getTrade_type_id() == 2) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 3) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 4) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_BUY);
					}
					
					int action = firstLog.getAction();
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setDeposit(firstLog.getDeposit().doubleValue());
					
					if (action == 0) { 
						data.setState(Contents.PENDING_STATE_SUBMITTED);
					} else if (action == 1) {
						data.setState(Contents.PENDING_STATE_FILLED);
					} else if (action == 4) {
						data.setState(Contents.PENDING_STATE_CANCELED);
					} else {
						data.setState(Contents.PENDING_STATE_SUBMITTING);
					}
				}
				
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		JSONArray array = new JSONArray();
		for (String orderId : orderList.keySet()) {
			Gson gson = new Gson();
			JSONObject obj = new JSONObject(gson.toJson(orderList.get(orderId)));
			array.put(obj);
		}
		tickerJson.put("data", array);
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/orders/batchplace", method = RequestMethod.POST)
	public String batchplace(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String[] orders = request.getParameterValues("orders");
		if (orders == null || orders.length == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orders参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (orders.length > 50) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "订单数超过50");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);

		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		BQMemberLevel userlevel = bqMemberLevelService.readByUserId(userId);
		JSONArray successArray = new JSONArray();
		JSONArray failedArray = new JSONArray();
		for (String order : orders) {
			String[] orderInfo = order.split(",");
			if (orderInfo.length != 4 && orderInfo.length != 3) {
				continue;
			}
			
			String amountStr = orderInfo[0];
			String priceStr = orderInfo[1];
			
			String multiple = "";
			String operation = "";
			if ("spot".equals(type)) {
				operation = orderInfo[2];
			} else {
				multiple = orderInfo[2];
				operation = orderInfo[3];
			}
			
			JSONObject json = new JSONObject();
			String allError = "";
			String tradesStr = "";
			if ("spot".equals(type)) {
				tradesStr = spotOrderPlaceOne(request, true, symbol, amountStr, priceStr, operation, userId, json);
			} else if ("delivery".equals(type)) {
				tradesStr = orderPlaceOneD(request, true, "", "", symbol, amountStr, priceStr, operation, multiple, userId, json, userlevel);
			} else if ("vcontract".equals(type)) {
				tradesStr = orderPlaceOneV(request, true, "", "", symbol, amountStr, priceStr, operation, multiple, userId, json, userlevel);
			} else if (type == null || "".equals(type) || "contract".equals(type)) {
				tradesStr = orderPlaceOne(request, true, "", "", symbol, amountStr, priceStr, operation, multiple, userId, json, userlevel);
			} else {
				tradesStr = orderPlaceOneN(request, type, true, "", "", symbol, amountStr, priceStr, operation, multiple, userId, json, userlevel);
			}
			
			if (StringUtil.isEmpty(tradesStr) && json.has("data")) {
				successArray.put(json.getString("data"));
			} else {
				allError = allError + (StringUtil.isEmpty(allError) ? "" : ",") + tradesStr;
				tickerJson.put("msg", allError);
				failedArray.put(order);
			}
		}
		
		JSONObject dataJson = new JSONObject();
		dataJson.put("success", successArray);
		dataJson.put("failed", failedArray);
		
		tickerJson.put("data", dataJson);
		model.addAttribute("ticker", tickerJson);
		
		long spendTime = System.currentTimeMillis() - startTime;
		if (spendTime > 18000) {
			System.out.println("order return: " + spendTime);
		}
	
		return apiData(response); 
	}
	
	@RequestMapping(value = "/orders/place", method = RequestMethod.POST)
	public String ordersPlace(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String orderTypeStr = request.getParameter("orderType"); // 下单类型（0或者空：限价委托;1：市价委托）
		String percentStr= request.getParameter("percent"); // 百分比
		String priceStr = request.getParameter("price");
		String amountStr = request.getParameter("amount");
		String symbol = request.getParameter("symbol");
		String multiple = request.getParameter("multiple");
		String operation = request.getParameter("operation");
		
		if (StringUtil.isEmpty(symbol) || StringUtil.isEmpty(operation)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(amountStr) && StringUtil.isEmpty(percentStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(priceStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!"spot".equals(type) && StringUtil.isEmpty(multiple)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);
		map.put("operation", operation);
		if (!StringUtil.isEmpty(multiple)) {
			map.put("multiple", multiple);
		}
		if (!StringUtil.isEmpty(orderTypeStr)) {
			map.put("orderType", orderTypeStr);
		}
		if (!StringUtil.isEmpty(priceStr)) {
			map.put("price", priceStr);
		}
		if (!StringUtil.isEmpty(amountStr)) {
			map.put("amount", amountStr);
		}
		if (!StringUtil.isEmpty(percentStr)) {
			map.put("percent", percentStr);
		}
		
		long userId = 0;
		boolean isApi = true;
		if (StringUtil.isEmpty(map.get("token"))) {
			isApi = true;
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			isApi = false;
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		String tradesStr = "";
		if ("spot".equals(type)) {
			tradesStr = spotOrderPlaceOne(request, isApi, symbol, amountStr, priceStr, operation, userId, tickerJson);
		} else if ("delivery".equals(type)) {
			tradesStr = orderPlaceOneD(request, isApi, orderTypeStr, percentStr, symbol, amountStr, priceStr, operation, multiple, userId, tickerJson, null);
		} else if ("vcontract".equals(type)) {
			tradesStr = orderPlaceOneV(request, isApi, orderTypeStr, percentStr, symbol, amountStr, priceStr, operation, multiple, userId, tickerJson, null);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			tradesStr = orderPlaceOne(request, isApi, orderTypeStr, percentStr, symbol, amountStr, priceStr, operation, multiple, userId, tickerJson, null);
		} else {
			tradesStr = orderPlaceOneN(request, type, isApi, orderTypeStr, percentStr, symbol, amountStr, priceStr, operation, multiple, userId, tickerJson, null);
		}
		
		if (!StringUtil.isEmpty(tradesStr)) {
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/positions/close", method = RequestMethod.POST)
	public String ordersClose(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String orderTypeStr = request.getParameter("orderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String symbol = request.getParameter("symbol");
		String positionId = request.getParameter("positionId");
		
		if (StringUtil.isEmpty(amount) || StringUtil.isEmpty(symbol) || StringUtil.isEmpty(positionId)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(price)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("amount", amount);
		map.put("symbol", symbol);
		map.put("positionId", positionId);
		if (!StringUtil.isEmpty(price)) {
			map.put("price", price);
		}
		if (!StringUtil.isEmpty(orderTypeStr)) {
			map.put("orderType", orderTypeStr);
		}
		
		long userId = 0;
		BQMember user = null;
		boolean isApi = false;
		if (StringUtil.isEmpty(map.get("token"))) {
			isApi = true;
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
			user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		BQMemberLevel userlevel = super.getLevelByUserBfx(user);
		if (isApi && userlevel.getEat_commission_rate().compareTo(BigDecimal.ZERO) > 0) {
			BQUserApiSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING);
			if (setting == null) {
				setting = bqUserApiSettingService.readOne();
				spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);
			}
			if (setting != null && setting.getIs_open()) {
				try {
					Thread.sleep(setting.getDelay_time());
				} catch (InterruptedException e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		}
		
		String sip = IpMacUtil.getIpAddr(request);
		int orderType = Convert.toInt(orderTypeStr, 0);

		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (Convert.toDouble(amount, 0) <= 0 || mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			VTransaction bqTransaction = super.findCurrVTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VTransactionType bqTransactionType = vTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VMemberTransaction memberTransaction = vMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			if (orderType == 1) {
				price = getVMarketOrderPrice(bqTransaction, direction);
			}
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			BigDecimal amount_client = new BigDecimal(amount);
			BigDecimal price_client = new BigDecimal(price);
			
			if (orderType == 1) {
				price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
			
			if (Convert.toDouble(price, 0) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,price错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (amount_client.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓数量精度错误，精度位数为：" + amountPrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} else if (price_client.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓价格精度错误，精度位数为：" + pricePrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (amount_client.compareTo(memberTransaction.getHold_count()) > 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量大于持仓可用数量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VTransactionRate index_price_rate = vTransactionRateService.readByCode(bqTransaction.getTransaction_code());
			BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
			BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				if (orderType == 1) {
					price_client = maxIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				if (orderType == 1) {
					price_client = minIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			// 爆仓价
			VAccountContract account = vAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, userlevel.getEat_commission_rate(), 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价格必须大于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价格必须小于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (vMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						vPendingDelegationCacheService.insertPCD(cahce, mtid, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
					} else {
						vPendingDelegationCacheService.insertPCK(cahce, mtid, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					vMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else if("delivery".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (Convert.toDouble(amount, 0) <= 0 || mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			DeliveryTransaction bqTransaction = super.findCurrDTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			DeliveryTransactionType bqTransactionType = dTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryMemberTransaction memberTransaction = dMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			if (orderType == 1) {
				price = getDMarketOrderPrice(bqTransaction, direction);
			}
			
			if (Convert.toDouble(price, 0) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,price错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			BigDecimal amount_client = new BigDecimal(amount);
			BigDecimal price_client = new BigDecimal(price);
			
			if (orderType == 1) {
				price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
			
			if (amount_client.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓数量精度错误，精度位数为：" + amountPrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} else if (price_client.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓价格精度错误，精度位数为：" + pricePrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (amount_client.compareTo(memberTransaction.getHold_count()) > 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量大于持仓可用数量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal rateUp = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				DeliveryTransactionRate index_price_rate = dTransactionRateService.getByCode(bqTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			BigDecimal oneDayMin = null;
			BigDecimal oneDayMax = null;
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + bqTransaction.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			} else if (bqTransaction.getTransaction_kind() == 2) {
				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
				if (btc_index_price == null) {
					btc_index_price = dTradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
					if (btc_index_price != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
					}
				}
				
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = dTradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			BigDecimal minIndexPrice = null;
			BigDecimal maxIndexPrice = null;
			if (btc_index_price != null) {
				minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
				minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
				maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
				maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
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
			
			if (bqTransaction.getTransaction_kind() == 2) {
				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + bqTransaction.getTransaction_code());
				if (openPrice != null) {
					minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				if (orderType == 1) {
					price_client = maxIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				if (orderType == 1) {
					price_client = minIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			// 爆仓价
			DeliveryAccountContract account = dAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价格必须大于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价格必须小于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (dMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						dPendingDelegationCacheService.insertPCD(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					} else {
						dPendingDelegationCacheService.insertPCK(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					dMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (Convert.toDouble(amount, 0) <= 0 || mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			BQTransaction bqTransaction = super.findCurrTransByCode(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			BQTransactionType bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMemberTransaction memberTransaction = bqMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			if (orderType == 1) {
				price = getMarketOrderPrice(bqTransaction, direction);
			}
			
			if (Convert.toDouble(price, 0) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,price错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			BigDecimal amount_client = new BigDecimal(amount);
			BigDecimal price_client = new BigDecimal(price);
			
			if (orderType == 1) {
				price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
			if (amount_client.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓数量精度错误，精度位数为：" + amountPrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			} else if (price_client.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓价格精度错误，精度位数为：" + pricePrecision);
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (amount_client.compareTo(memberTransaction.getHold_count()) > 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量大于持仓可用数量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal rateUp = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			BigDecimal oneDayMin = null;
			BigDecimal oneDayMax = null;
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			} else if (bqTransaction.getTransaction_kind() == 2) {
//				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
//				if (btc_index_price == null) {
//					btc_index_price = tradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
//					if (btc_index_price != null) {
//						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//					}
//				}
				
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = tradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			BigDecimal minIndexPrice = null;
			BigDecimal maxIndexPrice = null;
			if (btc_index_price != null) {
				minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
				minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
				maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
				maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			} 
			
//			else {
//				maxIndexPrice = new BigDecimal(100000);
//				minIndexPrice = BigDecimal.ZERO;
//			}
			
			if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
				minIndexPrice = oneDayMin;
			}
			
			if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
				maxIndexPrice = oneDayMax;
			}
			
//			if (bqTransaction.getTransaction_kind() == 2) {
//				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqTransaction.getTransaction_code());
//				if (openPrice != null) {
//					minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//				}
//			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				if (orderType == 1) {
					price_client = maxIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				if (orderType == 1) {
					price_client = minIndexPrice;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			// 爆仓价
			BQAccountContract account = bqAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价格必须大于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				if (orderType == 1) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
					price_client = bcj;
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价格必须小于爆仓价");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			PendingDelegationCache cahce = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (bqMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						pendingDelegationCacheService.insertPCD(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					} else {
						pendingDelegationCacheService.insertPCK(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					bqMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				long mtid = Convert.toLong(positionId, 0);
				if (Convert.toDouble(amount, 0) <= 0 || mtid <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				NTransaction bqTransaction = super.findNCurrTrans(type, symbol);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (bqTransaction.getSuspend()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
					if (!nNotradetimeService.readTradeTime(type, bqTransaction.getTransaction_id())) {
						String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
				
				NTransactionType bqTransactionType = nTransactionTypeService.readTransactionType(type, bqTransaction.getTransaction_type());
				if (bqTransactionType == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NMemberTransaction memberTransaction = nMemberTransactionService.get(type, mtid);
				if (memberTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (memberTransaction.getF_user_id() != userId) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (memberTransaction.getFlag() != 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				if (memberTransaction.getHold_count().signum() <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				int direction = Contents.DIRECTION_SELL;
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
					direction = Contents.DIRECTION_BUY;
				}
				
				if (orderType == 1) {
					price = getNMarketOrderPrice(type, bqTransaction, direction);
				}
				
				if (Convert.toDouble(price, 0) <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,price错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
				int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
				
				BigDecimal amount_client = new BigDecimal(amount);
				BigDecimal price_client = new BigDecimal(price);
				
				if (orderType == 1) {
					price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
				if (amount_client.scale() > amountPrecision) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓数量精度错误，精度位数为：" + amountPrecision);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} else if (price_client.scale() > pricePrecision) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "平仓价格精度错误，精度位数为：" + pricePrecision);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (amount_client.compareTo(memberTransaction.getHold_count()) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量大于持仓可用数量");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
				if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				BigDecimal rateUp = spyMemcachedClient.get(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
				BigDecimal rateDown = spyMemcachedClient.get(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
				if (rateUp == null || rateDown == null 
						|| rateUp.compareTo(BigDecimal.ZERO) == 0 
						|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
					NTransactionRate index_price_rate = nTransactionRateService.getByCode(type, bqTransaction.getTransaction_code());
					rateDown = index_price_rate.getDownrate();
					rateUp = index_price_rate.getUprate();
					
					spyMemcachedClient.set(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
					spyMemcachedClient.set(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
				}
				
				BigDecimal oneDayMin = null;
				BigDecimal oneDayMax = null;
				if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
					BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
					if (preClose != null)
						btc_index_price = preClose;
				} else if (bqTransaction.getTransaction_kind() == 2) {
//					btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + type + "_" + bqTransaction.getTransaction_code());
//					if (btc_index_price == null) {
//						btc_index_price = nTradeLogService.readLastHourAgoByCode(type, bqTransaction.getTransaction_code());
//						if (btc_index_price != null) {
//							spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + type + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//						}
//					}
					
					BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + type + "_" + bqTransaction.getTransaction_code());
					if (oneDayAgo == null) {
						oneDayAgo = nTradeLogService.readLastDayAgoByCode(type, bqTransaction.getTransaction_code());
						if (oneDayAgo != null) {
							spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + type + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
						}
					}
					
					if (oneDayAgo != null) {
						oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
						oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
					}
				}
				
				BigDecimal minIndexPrice = null;
				BigDecimal maxIndexPrice = null;
				if (btc_index_price != null) {
					minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
					minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
					maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
					maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				} 
				
//				else {
//					maxIndexPrice = new BigDecimal(100000);
//					minIndexPrice = BigDecimal.ZERO;
//				}
				
				if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
					minIndexPrice = oneDayMin;
				}
				
				if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
					maxIndexPrice = oneDayMax;
				}
				
//				if (bqTransaction.getTransaction_kind() == 2) {
//					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + type + "_" + bqTransaction.getTransaction_code());
//					if (openPrice != null) {
//						minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//						maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//					}
//				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
					if (orderType == 1) {
						price_client = maxIndexPrice;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
					if (orderType == 1) {
						price_client = minIndexPrice;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
				
				// 爆仓价
				NAccountContract account = nAccountContractService.readBTCByUidCode(type, memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
				BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
						.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
				
				BigDecimal feeRate = userlevel.getEat_commission_rate();
				NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(type, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
				if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
					feeRate = transactionFeeRate.getEat_commission_rate();
				}
				
				BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
						memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
						price_client = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价格必须大于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
						price_client = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价格必须小于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}

				BigDecimal multiple = memberTransaction.getTrade_multiple();
				BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
						.multiply(memberTransaction.getDeposit());

				NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
						direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
						feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

				if (nMemberTransactionService.lockToTrade(type, mtid)) {
					try {
						if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
							nPendingDelegationCacheService.insertPCD(type, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
						} else {
							nPendingDelegationCacheService.insertPCK(type, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
						}
						
						String orderId = cahce.getPending_no() + "";
						tickerJson.put("data", orderId);
					} catch (Exception e) {
						e.printStackTrace();
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					} finally {
						nMemberTransactionService.unlockToTrade(type, mtid);
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/positions/close/all", method = RequestMethod.POST)
	public String ordersCloseAll(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String symbol = request.getParameter("symbol");
		String positionId = request.getParameter("positionId");
		
		if (StringUtil.isEmpty(symbol) || StringUtil.isEmpty(positionId)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);
		map.put("positionId", positionId);
		
		long userId = 0;
		BQMember user = null;
		boolean isApi = false;
		if (StringUtil.isEmpty(map.get("token"))) {
			isApi = true;
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
			user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		String sip = IpMacUtil.getIpAddr(request);

		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			VTransaction bqTransaction = super.findCurrVTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VTransactionType bqTransactionType = vTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VMemberTransaction memberTransaction = vMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
//			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			String price = getVMarketOrderPrice(bqTransaction, direction);
			
			BigDecimal amount_client = memberTransaction.getHold_count();
			BigDecimal price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			
			if (Convert.toDouble(price, 0) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,price错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMemberLevel userlevel = bqMemberLevelService.readByUserId(userId);
			VTransactionRate index_price_rate = vTransactionRateService.readByCode(bqTransaction.getTransaction_code());
			BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
			BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				price_client = maxIndexPrice;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				price_client = minIndexPrice;
			}
			
			// 爆仓价
			VAccountContract account = vAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, userlevel.getEat_commission_rate(), 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
				price_client = bcj;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
				price_client = bcj;
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (vMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						vPendingDelegationCacheService.insertPCD(cahce, mtid, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
					} else {
						vPendingDelegationCacheService.insertPCK(cahce, mtid, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					vMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else if("delivery".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			DeliveryTransaction bqTransaction = super.findCurrDTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
					return tradesStr;
				}
			}
			
			DeliveryTransactionType bqTransactionType = dTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryMemberTransaction memberTransaction = dMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
//			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			String price = getDMarketOrderPrice(bqTransaction, direction);
			BigDecimal amount_client = memberTransaction.getHold_count();
			BigDecimal price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMemberLevel userlevel = super.getLevelByUserBfx(user);
			
			BigDecimal rateUp = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				DeliveryTransactionRate index_price_rate = dTransactionRateService.getByCode(bqTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			BigDecimal oneDayMin = null;
			BigDecimal oneDayMax = null;
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + bqTransaction.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			} else if (bqTransaction.getTransaction_kind() == 2) {
				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
				if (btc_index_price == null) {
					btc_index_price = dTradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
					if (btc_index_price != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
					}
				}
				
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = dTradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			BigDecimal minIndexPrice = null;
			BigDecimal maxIndexPrice = null;
			if (btc_index_price != null) {
				minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
				minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
				maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
				maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
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
			
			if (bqTransaction.getTransaction_kind() == 2) {
				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + bqTransaction.getTransaction_code());
				if (openPrice != null) {
					minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				price_client = maxIndexPrice;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				price_client = minIndexPrice;
			}
			
			// 爆仓价
			DeliveryAccountContract account = dAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
				price_client = bcj;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
				price_client = bcj;
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (dMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						dPendingDelegationCacheService.insertPCD(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					} else {
						dPendingDelegationCacheService.insertPCK(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					dMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			long mtid = Convert.toLong(positionId, 0);
			if (mtid <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			BQTransaction bqTransaction = super.findCurrTransByCode(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getSuspend()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
					return tradesStr;
				}
			}
			
			BQTransactionType bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMemberTransaction memberTransaction = bqMemberTransactionService.get(mtid);
			if (memberTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (memberTransaction.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			if (memberTransaction.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int direction = Contents.DIRECTION_SELL;
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
				direction = Contents.DIRECTION_BUY;
			}
			
			int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
//			int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
			
			String price = getMarketOrderPrice(bqTransaction, direction);
			BigDecimal amount_client = memberTransaction.getHold_count();
			BigDecimal price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			
			BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
			if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQMemberLevel userlevel = super.getLevelByUserBfx(user);
			
			BigDecimal rateUp = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
			BigDecimal rateDown = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
			if (rateUp == null || rateDown == null 
					|| rateUp.compareTo(BigDecimal.ZERO) == 0 
					|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
				BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());
				rateDown = index_price_rate.getDownrate();
				rateUp = index_price_rate.getUprate();
				
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
			}
			
			BigDecimal oneDayMin = null;
			BigDecimal oneDayMax = null;
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
				if (preClose != null)
					btc_index_price = preClose;
			} else if (bqTransaction.getTransaction_kind() == 2) {
//				btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
//				if (btc_index_price == null) {
//					btc_index_price = tradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
//					if (btc_index_price != null) {
//						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//					}
//				}
				
				BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
				if (oneDayAgo == null) {
					oneDayAgo = tradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
					if (oneDayAgo != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
					}
				}
				
				if (oneDayAgo != null) {
					oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
					oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				}
			}
			
			BigDecimal minIndexPrice = null;
			BigDecimal maxIndexPrice = null;
			if (btc_index_price != null) {
				minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
				minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
				maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
				maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			} 
			
//			else {
//				maxIndexPrice = new BigDecimal(100000);
//				minIndexPrice = BigDecimal.ZERO;
//			}
			
			if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
				minIndexPrice = oneDayMin;
			}
			
			if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
				maxIndexPrice = oneDayMax;
			}
			
//			if (bqTransaction.getTransaction_kind() == 2) {
//				BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqTransaction.getTransaction_code());
//				if (openPrice != null) {
//					minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//					maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//				}
//			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
				price_client = maxIndexPrice;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
				price_client = minIndexPrice;
			}
			
			// 爆仓价
			BQAccountContract account = bqAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
			BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
					.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
			
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
					memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
				price_client = bcj;
			}
			
			if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
				bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
				price_client = bcj;
			}

			BigDecimal multiple = memberTransaction.getTrade_multiple();
			BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
					.multiply(memberTransaction.getDeposit());

			PendingDelegationCache cahce = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
					feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

			if (bqMemberTransactionService.lockToTrade(mtid)) {
				try {
					if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
						pendingDelegationCacheService.insertPCD(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					} else {
						pendingDelegationCacheService.insertPCK(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
					}
					
					String orderId = cahce.getPending_no() + "";
					tickerJson.put("data", orderId);
				} catch (Exception e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				} finally {
					bqMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				long mtid = Convert.toLong(positionId, 0);
				if (mtid <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				NTransaction bqTransaction = super.findNCurrTrans(type, symbol);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (bqTransaction.getSuspend()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
					if (!nNotradetimeService.readTradeTime(type, bqTransaction.getTransaction_id())) {
						String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "休市");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}
				
				NTransactionType bqTransactionType = nTransactionTypeService.readTransactionType(type, bqTransaction.getTransaction_type());
				if (bqTransactionType == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NMemberTransaction memberTransaction = nMemberTransactionService.get(type, mtid);
				if (memberTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "合约名称与持仓不匹配");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (memberTransaction.getF_user_id() != userId) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOPOSITION, "没有找到该持仓");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (memberTransaction.getFlag() != 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				if (memberTransaction.getHold_count().signum() <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓可用数量为0");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				int direction = Contents.DIRECTION_SELL;
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
					direction = Contents.DIRECTION_BUY;
				}
				
				int pricePrecision = getDigits(bqTransaction.getTransaction_code(), type);
//				int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
				
				String price = getNMarketOrderPrice(type, bqTransaction, direction);
				BigDecimal amount_client = memberTransaction.getHold_count();
				BigDecimal price_client = new BigDecimal(price).setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				
				BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
				if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓下单失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				BQMemberLevel userlevel = super.getLevelByUserBfx(user);
				BigDecimal rateUp = spyMemcachedClient.get(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
				BigDecimal rateDown = spyMemcachedClient.get(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
				if (rateUp == null || rateDown == null 
						|| rateUp.compareTo(BigDecimal.ZERO) == 0 
						|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
					NTransactionRate index_price_rate = nTransactionRateService.getByCode(type, bqTransaction.getTransaction_code());
					rateDown = index_price_rate.getDownrate();
					rateUp = index_price_rate.getUprate();
					
					spyMemcachedClient.set(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
					spyMemcachedClient.set(type + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
				}
				
				BigDecimal oneDayMin = null;
				BigDecimal oneDayMax = null;
				if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
					BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
					if (preClose != null)
						btc_index_price = preClose;
				} else if (bqTransaction.getTransaction_kind() == 2) {
//					btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + type + "_" + bqTransaction.getTransaction_code());
//					if (btc_index_price == null) {
//						btc_index_price = nTradeLogService.readLastHourAgoByCode(type, bqTransaction.getTransaction_code());
//						if (btc_index_price != null) {
//							spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + type + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//						}
//					}
					
					BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + type + "_" + bqTransaction.getTransaction_code());
					if (oneDayAgo == null) {
						oneDayAgo = nTradeLogService.readLastDayAgoByCode(type, bqTransaction.getTransaction_code());
						if (oneDayAgo != null) {
							spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + type + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
						}
					}
					
					if (oneDayAgo != null) {
						oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
						oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
					}
				}
				
				BigDecimal minIndexPrice = null;
				BigDecimal maxIndexPrice = null;
				if (btc_index_price != null) {
					minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
					minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
					maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
					maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
				} 
				
//				else {
//					maxIndexPrice = new BigDecimal(100000);
//					minIndexPrice = BigDecimal.ZERO;
//				}
				
				if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
					minIndexPrice = oneDayMin;
				}
				
				if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
					maxIndexPrice = oneDayMax;
				}
				
//				if (bqTransaction.getTransaction_kind() == 2) {
//					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + type + "_" + bqTransaction.getTransaction_code());
//					if (openPrice != null) {
//						minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//						maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//					}
//				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
					price_client = maxIndexPrice;
				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
					price_client = minIndexPrice;
				}
				
				// 爆仓价
				NAccountContract account = nAccountContractService.readBTCByUidCode(type, memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
				BigDecimal deposit = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
						.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
				
				BigDecimal feeRate = userlevel.getEat_commission_rate();
				NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(type, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
				if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
					feeRate = transactionFeeRate.getEat_commission_rate();
				}
				
				BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit, feeRate, 
						memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.UP);
					price_client = bcj;
				}
				
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
					bcj = bcj.setScale(pricePrecision, RoundingMode.DOWN);
					price_client = bcj;
				}

				BigDecimal multiple = memberTransaction.getTrade_multiple();
				BigDecimal dp = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
						.multiply(memberTransaction.getDeposit());

				NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
						direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
						feeRate, Contents.PRIORITY_PC, memberTransaction.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

				if (nMemberTransactionService.lockToTrade(type, mtid)) {
					try {
						if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
							nPendingDelegationCacheService.insertPCD(type, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
						} else {
							nPendingDelegationCacheService.insertPCK(type, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_API, sip);
						}
						
						String orderId = cahce.getPending_no() + "";
						tickerJson.put("data", orderId);
					} catch (Exception e) {
						e.printStackTrace();
						String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单失败，服务器忙");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					} finally {
						nMemberTransactionService.unlockToTrade(type, mtid);
					}
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "服务器繁忙，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/orders/cancel", method = RequestMethod.POST)
	public String ordersCanel(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", Contents.API_ERROR_UNKNOW);
		tickerJson.put("msg", "取消失败");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String orderId = request.getParameter("orderId");
		String symbol = request.getParameter("symbol");
		
		if (symbol == null || orderId == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("orderId", orderId);
		map.put("symbol", symbol);
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		if ("spot".equals(type)) {
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(symbol);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			boolean needLog = true;
			boolean isQuantUser = checkIsSpotQuantUser(userId);
			if (isQuantUser) {
				needLog = false;
			}
			
			SpotPendingDelegation pd = spotPendingDelegationService.readPendingByPendingNo(orderId, symbol, userId);
			if (pd == null) {
				SpotPendingDelegationCache pdc = spotPendingDelegationCacheService.readPendingByPendingNo(orderId, symbol, userId);
				if (pdc == null || pdc.getF_user_id() != userId) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = spotPendingDelegationCacheService.lock(symbol, pdc.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					if (spotPendingDelegationCacheService.cancelPendingCache_one(pdc, needLog, coinPair) > 0) {
						tickerJson.put("code", 0);
						tickerJson.put("msg", "");
						tickerJson.put("data", pdc.getPending_no() + "");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pdc != null) {
						spotPendingDelegationCacheService.unLock(symbol, pdc.getDelegation_id(), lk);
					}
				}
			} else {
				String lk = spotPendingDelegationService.lock(symbol, pd.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					if (spotPendingDelegationService.cancelPending_one(pd, needLog, coinPair) > 0) {
						tickerJson.put("code", 0);
						tickerJson.put("msg", "");
						tickerJson.put("data", pd.getPending_no() + "");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pd != null) {
						spotPendingDelegationService.unLock(symbol, pd.getDelegation_id(), lk);
					}
				}
			}
		} else if ("vcontract".equals(type)) {
			VTransaction bqTransaction = super.findCurrVTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VPendingDelegation pd = vPendingDelegationService.readPendingByPendingNo(orderId, symbol, userId);
			if (pd == null) {
				VPendingDelegationCache pdc = vPendingDelegationCacheService.readPendingByPendingNo(orderId, symbol, userId);
				if (pdc == null || pdc.getF_user_id() != userId || pdc.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = vPendingDelegationCacheService.lock(symbol, pdc.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					VMemberTransaction trans = null;
					if (pdc.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pdc.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = vMemberTransactionService.readMemberTransaction(symbol, pdc.getF_user_id(), tradeType, pdc.getMultiple());
						lock = vMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (vPendingDelegationCacheService.cancelPendingCache_one(pdc, bqTransaction.getCurrency_type_id(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pdc.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pdc.getPriority() == 2 && trans != null) {
								vMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pdc != null) {
						vPendingDelegationCacheService.unLock(symbol, pdc.getDelegation_id(), lk);
					}
				}
			} else {
				if (pd.getF_user_id() != userId || pd.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = vPendingDelegationService.lock(symbol, pd.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					VMemberTransaction trans = null;
					if (pd.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pd.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = vMemberTransactionService.readMemberTransaction(symbol, pd.getF_user_id(), tradeType, pd.getMultiple());
						lock = vMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (vPendingDelegationService.cancelPending_one(pd, bqTransaction.getCurrency_type_id(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pd.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd.getPriority() == 2 && trans != null) {
								vMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pd != null) {
						vPendingDelegationService.unLock(symbol, pd.getDelegation_id(), lk);
					}
				}
			}
		} else if("delivery".equals(type)) {
			DeliveryTransaction bqTransaction = super.findCurrDTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryPendingDelegation pd = dPendingDelegationService.readPendingByPendingNo(orderId, symbol, userId);
			if (pd == null) {
				DeliveryPendingDelegationCache pdc = dPendingDelegationCacheService.readPendingByPendingNo(orderId, symbol, userId);
				if (pdc == null || pdc.getF_user_id() != userId || pdc.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = dPendingDelegationCacheService.lock(symbol, pdc.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					DeliveryMemberTransaction trans = null;
					if (pdc.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pdc.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = dMemberTransactionService.readMemberTransaction(symbol, pdc.getF_user_id(), tradeType, pdc.getMultiple());
						lock = dMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (dPendingDelegationCacheService.cancelPendingCache_one(pdc, bqTransaction.getTrade_coefficient(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pdc.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pdc.getPriority() == 2 && trans != null) {
								dMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pdc != null) {
						dPendingDelegationCacheService.unLock(symbol, pdc.getDelegation_id(), lk);
					}
				}
			} else {
				if (pd.getF_user_id() != userId || pd.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = dPendingDelegationService.lock(symbol, pd.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					DeliveryMemberTransaction trans = null;
					if (pd.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pd.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = dMemberTransactionService.readMemberTransaction(symbol, pd.getF_user_id(), tradeType, pd.getMultiple());
						lock = dMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (dPendingDelegationService.cancelPending_one(pd, bqTransaction.getTrade_coefficient(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pd.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd.getPriority() == 2 && trans != null) {
								dMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pd != null) {
						dPendingDelegationService.unLock(symbol, pd.getDelegation_id(), lk);
					}
				}
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction bqTransaction = super.findCurrTransByCode(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			PendingDelegation pd = pendingDelegationService.readPendingByPendingNo(orderId, symbol, userId);
			if (pd == null) {
				PendingDelegationCache pdc = pendingDelegationCacheService.readPendingByPendingNo(orderId, symbol, userId);
				if (pdc == null || pdc.getF_user_id() != userId || pdc.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = pendingDelegationCacheService.lock(symbol, pdc.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					BQMemberTransaction trans = null;
					if (pdc.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pdc.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = bqMemberTransactionService.readMemberTransaction(symbol, pdc.getF_user_id(), tradeType, pdc.getMultiple());
						lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (pendingDelegationCacheService.cancelPendingCache_one(pdc, bqTransaction.getTrade_coefficient(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pdc.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pdc.getPriority() == 2 && trans != null) {
								bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pdc != null) {
						pendingDelegationCacheService.unLock(symbol, pdc.getDelegation_id(), lk);
					}
				}
			} else {
				if (pd.getF_user_id() != userId || pd.getPriority() == 3) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				String lk = pendingDelegationService.lock(symbol, pd.getDelegation_id());
				if (StringUtil.isEmpty(lk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				try {
					boolean lock = true;
					BQMemberTransaction trans = null;
					if (pd.getPriority() == 2) {
						int tradeType = Contents.TRADE_TYPE_BUY;
						
						if (pd.getDirection() == Contents.DIRECTION_BUY) {
							tradeType = Contents.TRADE_TYPE_SELL;
						}
						
						trans = bqMemberTransactionService.readMemberTransaction(symbol, pd.getF_user_id(), tradeType, pd.getMultiple());
						lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					}
					
					if (lock) {
						try {
							if (pendingDelegationService.cancelPending_one(pd, bqTransaction.getTrade_coefficient(), true) > 0) {
								tickerJson.put("code", 0);
								tickerJson.put("msg", "");
								tickerJson.put("data", pd.getPending_no() + "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd.getPriority() == 2 && trans != null) {
								bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pd != null) {
						pendingDelegationService.unLock(symbol, pd.getDelegation_id(), lk);
					}
				}
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction bqTransaction = super.findNCurrTrans(type, symbol);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NPendingDelegation pd = nPendingDelegationService.readPendingByPendingNo(type, orderId, symbol, userId);
				if (pd == null) {
					NPendingDelegationCache pdc = nPendingDelegationCacheService.readPendingByPendingNo(type, orderId, symbol, userId);
					if (pdc == null || pdc.getF_user_id() != userId || pdc.getPriority() == 3) {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					String lk = nPendingDelegationCacheService.lock(type, symbol, pdc.getDelegation_id());
					if (StringUtil.isEmpty(lk)) {
						String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					try {
						boolean lock = true;
						NMemberTransaction trans = null;
						if (pdc.getPriority() == 2) {
							int tradeType = Contents.TRADE_TYPE_BUY;
							
							if (pdc.getDirection() == Contents.DIRECTION_BUY) {
								tradeType = Contents.TRADE_TYPE_SELL;
							}
							
							trans = nMemberTransactionService.readMemberTransaction(type, symbol, pdc.getF_user_id(), tradeType, pdc.getMultiple());
							lock = nMemberTransactionService.lockToAdjustSpare(type, trans.getMt_id());
						}
						
						if (lock) {
							try {
								if (nPendingDelegationCacheService.cancelPendingCache_one(type, bqTransaction.getCurrency_type_id(), pdc, bqTransaction.getTrade_coefficient(), pdc.getCommission_rate().signum() > 0) > 0) {
									tickerJson.put("code", 0);
									tickerJson.put("msg", "");
									tickerJson.put("data", pdc.getPending_no() + "");
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pdc.getPriority() == 2 && trans != null) {
									nMemberTransactionService.unlockToAdjustSpare(type, trans.getMt_id());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pdc != null) {
							nPendingDelegationCacheService.unLock(type, symbol, pdc.getDelegation_id(), lk);
						}
					}
				} else {
					if (pd.getF_user_id() != userId || pd.getPriority() == 3) {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "找不到订单，或订单状态不对");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					String lk = nPendingDelegationService.lock(type, symbol, pd.getDelegation_id());
					if (StringUtil.isEmpty(lk)) {
						String tradesStr = createErrorCode(Contents.API_ERROR_ORDER_IN_PROCESS, "订单正在处理中，请稍后再试");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					try {
						boolean lock = true;
						NMemberTransaction trans = null;
						if (pd.getPriority() == 2) {
							int tradeType = Contents.TRADE_TYPE_BUY;
							
							if (pd.getDirection() == Contents.DIRECTION_BUY) {
								tradeType = Contents.TRADE_TYPE_SELL;
							}
							
							trans = nMemberTransactionService.readMemberTransaction(type, symbol, pd.getF_user_id(), tradeType, pd.getMultiple());
							lock = nMemberTransactionService.lockToAdjustSpare(type, trans.getMt_id());
						}
						
						if (lock) {
							try {
								if (nPendingDelegationService.cancelPending_one(type, bqTransaction.getCurrency_type_id(), pd, bqTransaction.getTrade_coefficient(), pd.getCommission_rate().signum() > 0) > 0) {
									tickerJson.put("code", 0);
									tickerJson.put("msg", "");
									tickerJson.put("data", pd.getPending_no() + "");
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pd.getPriority() == 2 && trans != null) {
									nMemberTransactionService.unlockToAdjustSpare(type, trans.getMt_id());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							nPendingDelegationService.unLock(type, symbol, pd.getDelegation_id(), lk);
						}
					}
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/orders/batchCancel", method = RequestMethod.POST)
	public String ordersBatchCancel(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String code = request.getParameter("symbol");
		String side = request.getParameter("side");
		
		if (code == null || side == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", code);
		map.put("side", side);
		
		int direction = -1;
		if ("all".equals(side)) {
			direction = 0;
		} else if ("buy".equals(side)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(side)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知side");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		if ("spot".equals(type)) {
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(code);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			Map<String, Integer> ret = cancelSpotPendingWithCode(coinPair, direction, userId);
			if (ret == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "取消失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("successCount", ret.get("success"));
			dataJson.put("failedCount", ret.get("failed"));
			
			tickerJson.put("data", dataJson);
		} else if ("vcontract".equals(type)) {
			
			VTransaction bqTransaction = super.findCurrVTrans(code);
			Map<String, Integer> ret = cancelVPendingWithCode(code, bqTransaction.getCurrency_type_id(), direction, userId);
			if (ret == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "取消失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("successCount", ret.get("success"));
			dataJson.put("failedCount", ret.get("failed"));
			
			tickerJson.put("data", dataJson);
		} else if("delivery".equals(type)) {
			DeliveryTransaction bqTransaction = super.findCurrDTrans(code);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			Map<String, Integer> ret = cancelDPendingWithCode(code, direction, userId, bqTransaction.getTrade_coefficient());
			if (ret == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "取消失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("successCount", ret.get("success"));
			dataJson.put("failedCount", ret.get("failed"));
			
			tickerJson.put("data", dataJson);
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction bqTransaction = super.findCurrTrans(code);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			Map<String, Integer> ret = cancelPendingWithCode(code, direction, userId, bqTransaction.getTrade_coefficient());
			if (ret == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "取消失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("successCount", ret.get("success"));
			dataJson.put("failedCount", ret.get("failed"));
			
			tickerJson.put("data", dataJson);
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction bqTransaction = super.findNCurrTrans(type, code);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				Map<String, Integer> ret = cancelNPendingWithCode(type, bqTransaction.getCurrency_type_id(), code, direction, userId, bqTransaction.getTrade_coefficient());
				if (ret == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "取消失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				JSONObject dataJson = new JSONObject();
				dataJson.put("successCount", ret.get("success"));
				dataJson.put("failedCount", ret.get("failed"));
				
				tickerJson.put("data", dataJson);
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		
		long spendTime = System.currentTimeMillis() - startTime;
		if (spendTime > 18000) {
			System.out.println("cancel return: " + spendTime);
		}
		
		return apiData(response);
	}
	
	@RequestMapping(value = "/orders/cancel/multi", method = RequestMethod.POST)
	public String ordersCanelMulti(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,symbol错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		String orderIds = request.getParameter("orderIds");
		if (StringUtil.isEmpty(orderIds)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误,orderIds错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);
		map.put("orderIds", orderIds);
		
		String idsStr = "";
		String[] ids = orderIds.split(",");
		List<Long> failedList = new ArrayList<>();
		for (String orderId : ids) {
			if (!StringUtil.isEmpty(orderId)) {
				Long id = Convert.toLong(orderId, 0);
				if (id > 0) {
					failedList.add(id);
					idsStr += (id + ",");
				}
			}
		}
		
		if (idsStr.length() > 0) {
			idsStr = idsStr.substring(0, idsStr.length() - 1);
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，orderIds");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (failedList.size() > 50) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "订单数超过50");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		if ("spot".equals(type)) {
			boolean needLog = true;
			boolean isQuantUser = checkIsSpotQuantUser(userId);
			if (isQuantUser) {
				needLog = false;
			}
			
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(symbol);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<SpotPendingDelegationCache> plistCache = spotPendingDelegationCacheService.readPendingByUserIdAndSymbol(symbol, userId, idsStr);
			if (plistCache.size() > 0) {
				for (SpotPendingDelegationCache cache : plistCache) {
					String lock = spotPendingDelegationCacheService.lock(cache.getCoin_pair_name(), cache.getDelegation_id());
					if (!StringUtil.isEmpty(lock)) {
						try {
							if (spotPendingDelegationCacheService.cancelPendingCache_one(cache, needLog, coinPair) > 0) {
								failedList.remove(cache.getPending_no());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (cache != null) {
								spotPendingDelegationCacheService.unLock(cache.getCoin_pair_name(), cache.getDelegation_id(), lock);
							}
						}
					} 
				}
			}
			List<SpotPendingDelegation> plist = spotPendingDelegationService.readPendingByUserIdAndSymbol(symbol, userId, idsStr);
			for (SpotPendingDelegation pd : plist) {
				String lock = spotPendingDelegationService.lock(pd.getCoin_pair_name(), pd.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						if (spotPendingDelegationService.cancelPending_one(pd, needLog, coinPair) > 0) {
							failedList.remove(pd.getPending_no());
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							spotPendingDelegationService.unLock(pd.getCoin_pair_name(), pd.getDelegation_id(), lock);
						}
					}
				}
			}
		} else if (StringUtil.isEmpty(type) || "contract".equals(type)) {
			
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction bqTransaction = super.findNCurrTrans(type, symbol);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NPendingDelegationCache> plistCache = nPendingDelegationCacheService.readPendingByUserIdAndSymbol(type, symbol, userId, idsStr);
				if (plistCache.size() > 0) {
					for (NPendingDelegationCache cache : plistCache) {
						String lock = nPendingDelegationCacheService.lock(type, cache.getTransaction_code(), cache.getDelegation_id());
						if (!StringUtil.isEmpty(lock)) {
							try {
								if (nPendingDelegationCacheService.cancelPendingCache_one(type, bqTransaction.getCurrency_type_id(), cache, bqTransaction.getTrade_coefficient(), cache.getCommission_rate().signum() > 0) > 0) {
									failedList.remove(cache.getPending_no());
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (cache != null) {
									nPendingDelegationCacheService.unLock(type, cache.getTransaction_code(), cache.getDelegation_id(), lock);
								}
							}
						}
					}
				}
				List<NPendingDelegation> plist = nPendingDelegationService.readPendingByUserIdAndSymbol(type, symbol, userId, idsStr);
				for (NPendingDelegation pd : plist) {
					String lock = nPendingDelegationService.lock(type, pd.getTransaction_code(), pd.getDelegation_id());
					if (!StringUtil.isEmpty(lock)) {
						try {
							if (nPendingDelegationService.cancelPending_one(type, bqTransaction.getCurrency_type_id(), pd, bqTransaction.getTrade_coefficient(), pd.getCommission_rate().signum() > 0) > 0) {
								failedList.remove(pd.getPending_no());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								nPendingDelegationService.unLock(type, pd.getTransaction_code(), pd.getDelegation_id(), lock);
							}
						}
					} else {
					}
				}
				
			}
		}
		
		JSONObject dataJson = new JSONObject();
		JSONArray cancelFailedArray = new JSONArray();
		for (Long val : failedList) {
			cancelFailedArray.put(val);
		}
		dataJson.put("failed", cancelFailedArray);
		
		tickerJson.put("data", dataJson);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/positions")
	public String getPositions(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String code = request.getParameter("symbol");
		if (!StringUtil.isEmpty(code)) {
			map.put("symbol", code);
		}
		
		long userId = 0;
		BQMember user = null;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
			user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		BQMemberLevel userlevel = super.getLevelByUserBfx(user);
		if  ("vcontract".equals(type)) {
			if (StringUtil.isEmpty(code)) {
				List<VMemberTransaction> allTrans = vMemberTransactionService.readVMemberTrasactionByUserId(userId);
				
				for (VMemberTransaction memberTrans : allTrans) {
					int pricePrecision = getDigits(memberTrans.getTransaction_code(), type);
					VTransaction currentTransaction = super.findCurrVTrans(memberTrans.getTransaction_code());
					
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + currentTransaction.getTransaction_code());
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
					}
					
					BQTransVo transVo = vMemberTransactionService.readData(memberTrans, memberTrans.getTrade_type_id(), markPrice, userlevel.getEat_commission_rate(), 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO);
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(memberTrans.getTrade_type_id());
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = vStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				VTransaction currentTransaction = super.findCurrVTrans(code);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				int pricePrecision = getDigits(currentTransaction.getTransaction_code(), type);
				
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				// 持仓信息
				List<VMemberTransaction> buyMemberTransaction = vMemberTransactionService.readVMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_BUY);
				List<VMemberTransaction> sellMemberTransaction = vMemberTransactionService.readVMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_SELL);
				
				for (VMemberTransaction memberTrans : buyMemberTransaction) {
					BQTransVo transVo = vMemberTransactionService.readData(memberTrans, Contents.DIRECTION_BUY, markPrice, userlevel.getEat_commission_rate(), 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO);
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_BUY);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = vStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
				
				for (VMemberTransaction memberTrans : sellMemberTransaction) {
					BQTransVo transVo = vMemberTransactionService.readData(memberTrans, Contents.DIRECTION_SELL, markPrice, userlevel.getEat_commission_rate(), 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO);
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_SELL);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = vStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			}
		} else if("delivery".equals(type)) {
			if (StringUtil.isEmpty(code)) {
				List<DeliveryMemberTransaction> allTrans = dMemberTransactionService.readBQMemberTrasactionByUserId(userId);
				
				for (DeliveryMemberTransaction memberTrans : allTrans) {
					int pricePrecision = getDigits(memberTrans.getTransaction_code(), type);
					DeliveryTransaction currentTransaction = super.findCurrDTrans(memberTrans.getTransaction_code());
					
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
					}
					
					BigDecimal feeRate = userlevel.getEat_commission_rate();
					DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					BQTransVo transVo = dMemberTransactionService.readHandleData(memberTrans, memberTrans.getTrade_type_id(), markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(memberTrans.getTrade_type_id());
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = dStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				DeliveryTransaction currentTransaction = super.findCurrDTrans(code);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				BigDecimal feeRate = userlevel.getEat_commission_rate();
				DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
				if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
					feeRate = transactionFeeRate.getEat_commission_rate();
				}
				
				int pricePrecision = getDigits(currentTransaction.getTransaction_code(), type);
				
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				// 持仓信息
				List<DeliveryMemberTransaction> buyMemberTransaction = dMemberTransactionService.readBQMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_BUY);
				List<DeliveryMemberTransaction> sellMemberTransaction = dMemberTransactionService.readBQMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_SELL);
				
				for (DeliveryMemberTransaction memberTrans : buyMemberTransaction) {
					BQTransVo transVo = dMemberTransactionService.readHandleData(memberTrans, Contents.DIRECTION_BUY, markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_BUY);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = dStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
				
				for (DeliveryMemberTransaction memberTrans : sellMemberTransaction) {
					BQTransVo transVo = dMemberTransactionService.readHandleData(memberTrans, Contents.DIRECTION_SELL, markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_SELL);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					int stopLoss = dStopLossService.readHasStopLoss(memberTrans.getMt_id());
					vo.setStopLoss(stopLoss == 1);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			if (StringUtil.isEmpty(code)) {
				List<BQMemberTransaction> allTrans = bqMemberTransactionService.readBQMemberTrasactionByUserId(userId);
				
				for (BQMemberTransaction memberTrans : allTrans) {
					int pricePrecision = getDigits(memberTrans.getTransaction_code(), type);
					
					BQTransaction currentTransaction = super.findCurrTrans(memberTrans.getTransaction_code());
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + currentTransaction.getTransaction_code());
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
					}
					
					BigDecimal feeRate = userlevel.getEat_commission_rate();
					BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					BQTransVo transVo = bqMemberTransactionService.readHandleData(memberTrans, memberTrans.getTrade_type_id(), markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(memberTrans.getTrade_type_id());
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					boolean stopLoss = planPendingDelegationService.readHasStopLoss(memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
					if (!stopLoss) {
						stopLoss = bqStopLossService.readHasStopLoss(memberTrans.getMt_id()) > 0;
					}
					vo.setStopLoss(stopLoss);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				BQTransaction currentTransaction = super.findCurrTrans(code);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				BigDecimal feeRate = userlevel.getEat_commission_rate();
				BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
				if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
					feeRate = transactionFeeRate.getEat_commission_rate();
				}
				
				int pricePrecision = getDigits(currentTransaction.getTransaction_code(), type);
				
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + currentTransaction.getTransaction_code());
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
				}
				
				// 持仓信息
				List<BQMemberTransaction> buyMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_BUY);
				List<BQMemberTransaction> sellMemberTransaction = bqMemberTransactionService.readBQMemberTrasaction(userId, currentTransaction
						.getTransaction_code(), Contents.DIRECTION_SELL);
				
				for (BQMemberTransaction memberTrans : buyMemberTransaction) {
					BQTransVo transVo = bqMemberTransactionService.readHandleData(memberTrans, Contents.DIRECTION_BUY, markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_BUY);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					boolean stopLoss = planPendingDelegationService.readHasStopLoss(memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
					if (!stopLoss) {
						stopLoss = bqStopLossService.readHasStopLoss(memberTrans.getMt_id()) > 0;
					}
					vo.setStopLoss(stopLoss);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
				
				for (BQMemberTransaction memberTrans : sellMemberTransaction) {
					BQTransVo transVo = bqMemberTransactionService.readHandleData(memberTrans, Contents.DIRECTION_SELL, markPrice, feeRate, 0, 
							pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
					MemberTransactionVo vo = new MemberTransactionVo();
					vo.setPositionId(memberTrans.getMt_id() + "");
					vo.setSymbol(transVo.getBqCode());
					vo.setDirection(Contents.DIRECTION_SELL);
					vo.setMultiple(transVo.getMultiple());
					vo.setAllCount(transVo.getAllCount().doubleValue());
					vo.setHoldCount(transVo.getHoldCount().doubleValue());
					vo.setLockCount(transVo.getLockCount().doubleValue());
					vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
					vo.setDeposit(transVo.getDeposit().doubleValue());
					vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
					vo.setProfitLossRatio(transVo.getProfitLossRatio());
					vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
					boolean stopLoss = planPendingDelegationService.readHasStopLoss(memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
					if (!stopLoss) {
						stopLoss = bqStopLossService.readHasStopLoss(memberTrans.getMt_id()) > 0;
					}
					vo.setStopLoss(stopLoss);
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				if (StringUtil.isEmpty(code)) {
					List<NMemberTransaction> allTrans = nMemberTransactionService.readMemberTrasactionByUserId(type, userId);
					
					for (NMemberTransaction memberTrans : allTrans) {
						int pricePrecision = getDigits(memberTrans.getTransaction_code(), type);
						
						NTransaction currentTransaction = super.findNCurrTrans(type, memberTrans.getTransaction_code());
						BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + type + "_" + currentTransaction.getTransaction_code());
						if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
							markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
						}
						
						BigDecimal feeRate = userlevel.getEat_commission_rate();
						NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(type, currentTransaction.getTransaction_code(), userlevel.getLevel_id());
						if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
							feeRate = transactionFeeRate.getEat_commission_rate();
						}
						
						BQTransVo transVo = nMemberTransactionService.readHandleData(type, memberTrans, memberTrans.getTrade_type_id(), markPrice, feeRate, 0, 
								pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
						MemberTransactionVo vo = new MemberTransactionVo();
						vo.setPositionId(memberTrans.getMt_id() + "");
						vo.setSymbol(transVo.getBqCode());
						vo.setDirection(memberTrans.getTrade_type_id());
						vo.setMultiple(transVo.getMultiple());
						vo.setAllCount(transVo.getAllCount().doubleValue());
						vo.setHoldCount(transVo.getHoldCount().doubleValue());
						vo.setLockCount(transVo.getLockCount().doubleValue());
						vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
						vo.setDeposit(transVo.getDeposit().doubleValue());
						vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
						vo.setProfitLossRatio(transVo.getProfitLossRatio());
						vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
						boolean stopLoss = nPlanPendingDelegationService.readHasStopLoss(type, memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
						if (!stopLoss) {
							stopLoss = nStopLossService.readHasStopLoss(type, memberTrans.getMt_id()) > 0;
						}
						vo.setStopLoss(stopLoss);
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(vo)));
					}
				} else {
					NTransaction currentTransaction = super.findNCurrTrans(type, code);
					if (currentTransaction == null ) {
						String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					BigDecimal feeRate = userlevel.getEat_commission_rate();
					NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(type, currentTransaction.getTransaction_code(), userlevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					int pricePrecision = getDigits(currentTransaction.getTransaction_code(), type);
					
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + type + "_" + currentTransaction.getTransaction_code());
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(currentTransaction.getTransaction_type_code());
					}
					
					// 持仓信息
					List<NMemberTransaction> buyMemberTransaction = nMemberTransactionService.readMemberTrasaction(type, userId, currentTransaction
							.getTransaction_code(), Contents.DIRECTION_BUY);
					List<NMemberTransaction> sellMemberTransaction = nMemberTransactionService.readMemberTrasaction(type, userId, currentTransaction
							.getTransaction_code(), Contents.DIRECTION_SELL);
					
					for (NMemberTransaction memberTrans : buyMemberTransaction) {
						BQTransVo transVo = nMemberTransactionService.readHandleData(type, memberTrans, Contents.DIRECTION_BUY, markPrice, feeRate, 0, 
								pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
						MemberTransactionVo vo = new MemberTransactionVo();
						vo.setPositionId(memberTrans.getMt_id() + "");
						vo.setSymbol(transVo.getBqCode());
						vo.setDirection(Contents.DIRECTION_BUY);
						vo.setMultiple(transVo.getMultiple());
						vo.setAllCount(transVo.getAllCount().doubleValue());
						vo.setHoldCount(transVo.getHoldCount().doubleValue());
						vo.setLockCount(transVo.getLockCount().doubleValue());
						vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
						vo.setDeposit(transVo.getDeposit().doubleValue());
						vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
						vo.setProfitLossRatio(transVo.getProfitLossRatio());
						vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
						boolean stopLoss = nPlanPendingDelegationService.readHasStopLoss(type, memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
						if (!stopLoss) {
							stopLoss = nStopLossService.readHasStopLoss(type, memberTrans.getMt_id()) > 0;
						}
						vo.setStopLoss(stopLoss);
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(vo)));
					}
					
					for (NMemberTransaction memberTrans : sellMemberTransaction) {
						BQTransVo transVo = nMemberTransactionService.readHandleData(type, memberTrans, Contents.DIRECTION_SELL, markPrice, feeRate, 0, 
								pricePrecision, BigDecimal.ZERO, BigDecimal.ZERO, currentTransaction.getTrade_coefficient());
						MemberTransactionVo vo = new MemberTransactionVo();
						vo.setPositionId(memberTrans.getMt_id() + "");
						vo.setSymbol(transVo.getBqCode());
						vo.setDirection(Contents.DIRECTION_SELL);
						vo.setMultiple(transVo.getMultiple());
						vo.setAllCount(transVo.getAllCount().doubleValue());
						vo.setHoldCount(transVo.getHoldCount().doubleValue());
						vo.setLockCount(transVo.getLockCount().doubleValue());
						vo.setAvgPrice(transVo.getAveragePrice().doubleValue());
						vo.setDeposit(transVo.getDeposit().doubleValue());
						vo.setFloatingProfitLoss(transVo.getFloatingProfitLoss().doubleValue());
						vo.setProfitLossRatio(transVo.getProfitLossRatio());
						vo.setForcedLiquidationPrice(transVo.getQpPrice().doubleValue());
						boolean stopLoss = nPlanPendingDelegationService.readHasStopLoss(type, memberTrans.getTransaction_code(), userId, memberTrans.getMt_id());
						if (!stopLoss) {
							stopLoss = nStopLossService.readHasStopLoss(type, memberTrans.getMt_id()) > 0;
						}
						vo.setStopLoss(stopLoss);
						
						Gson gson = new Gson();
						array.put(new JSONObject(gson.toJson(vo)));
					}
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);	
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/trades")
	public String getTrades(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		if ("spot".equals(type)) {
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(symbol);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<SpotTradeLog> trades = spotTradeLogService.readByUserAndTime(symbol, userId, nFrom, direct, start, end, size);
			for (SpotTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setTradeTime(trade.getTrade_time());

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if ("vcontract".equals(type)) {
			VTransaction currentTransaction = super.findCurrVTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<VTradeLog> trades = vTradeLogService.readByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (VTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setForcePrice(trade.getForce_price().doubleValue());
				vo.setTradeTime(trade.getTrade_time());

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if("delivery".equals(type)) {
			DeliveryTransaction currentTransaction = super.findCurrDTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<DeliveryTradeLog> trades = dTradeLogService.readByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (DeliveryTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setForcePrice(trade.getForce_price().doubleValue());
				vo.setTradeTime(trade.getTrade_time());

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction currentTransaction = super.findCurrTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<TradeLog> trades = tradeLogService.readByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (TradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setForcePrice(trade.getForce_price().doubleValue());
				vo.setTradeTime(trade.getTrade_time());

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction currentTransaction = super.findNCurrTrans(type, symbol);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NTradeLog> trades = nTradeLogService.readByUserAndTime(type, currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
				for (NTradeLog trade : trades) {
					TradeLogVo vo = new TradeLogVo();
					vo.setSymbol(symbol);
					vo.setTradeId(trade.getTrade_id());
					vo.setTradeType(trade.getTrade_type_id());
					vo.setDirection(trade.getTrade_trans_dirction());
					vo.setMultiple(trade.getTrade_multiple());
					vo.setHoldPrice(trade.getHold_price().doubleValue());
					vo.setTradePrice(trade.getTrade_average_price().doubleValue());
					vo.setTradeCount(trade.getTrade_count().doubleValue());
					vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
					vo.setTradeIncome(trade.getTrade_income().doubleValue());
					vo.setTradeFee(trade.getTrade_fee().doubleValue());
					vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
					vo.setForcePrice(trade.getForce_price().doubleValue());
					vo.setTradeTime(trade.getTrade_time());

					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("prev/trades")
	public String getPrevTrades(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		if ("spot".equals(type)) {
			BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(symbol);
			if (coinPair == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的交易对");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<SpotTradeLog> trades = spotTradeLogService.readFromBakByUserAndTime(symbol, userId, nFrom, direct, start, end, size);
			for (SpotTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setTradeTime(trade.getTrade_time());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if ("vcontract".equals(type)) {
			VTransaction currentTransaction = super.findCurrVTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<VTradeLog> trades = vTradeLogService.readFromBakByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (VTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setForcePrice(trade.getForce_price().doubleValue());
				vo.setTradeTime(trade.getTrade_time());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if("delivery".equals(type)) {
			DeliveryTransaction currentTransaction = super.findCurrDTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<DeliveryTradeLog> trades = dTradeLogService.readFromBakByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (DeliveryTradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setForcePrice(trade.getForce_price().doubleValue());
				vo.setTradeTime(trade.getTrade_time());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction currentTransaction = super.findCurrTrans(symbol);
			if (currentTransaction == null ) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<TradeLog> trades = tradeLogService.readFromBakByUserAndTime(currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
			for (TradeLog trade : trades) {
				TradeLogVo vo = new TradeLogVo();
				vo.setSymbol(symbol);
				vo.setTradeId(trade.getTrade_id());
				vo.setTradeType(trade.getTrade_type_id());
				vo.setDirection(trade.getTrade_trans_dirction());
				vo.setMultiple(trade.getTrade_multiple());
				vo.setHoldPrice(trade.getHold_price().doubleValue());
				vo.setTradePrice(trade.getTrade_average_price().doubleValue());
				vo.setTradeCount(trade.getTrade_count().doubleValue());
				vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
				vo.setTradeIncome(trade.getTrade_income().doubleValue());
				vo.setTradeFee(trade.getTrade_fee().doubleValue());
				vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
				vo.setTradeTime(trade.getTrade_time());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction currentTransaction = super.findNCurrTrans(type, symbol);
				if (currentTransaction == null ) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "找不到合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NTradeLog> trades = nTradeLogService.readFromBakByUserAndTime(type, currentTransaction.getTransaction_code(), userId, nFrom, direct, start, end, size);
				for (NTradeLog trade : trades) {
					TradeLogVo vo = new TradeLogVo();
					vo.setSymbol(symbol);
					vo.setTradeId(trade.getTrade_id());
					vo.setTradeType(trade.getTrade_type_id());
					vo.setDirection(trade.getTrade_trans_dirction());
					vo.setMultiple(trade.getTrade_multiple());
					vo.setHoldPrice(trade.getHold_price().doubleValue());
					vo.setTradePrice(trade.getTrade_average_price().doubleValue());
					vo.setTradeCount(trade.getTrade_count().doubleValue());
					vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
					vo.setTradeIncome(trade.getTrade_income().doubleValue());
					vo.setTradeFee(trade.getTrade_fee().doubleValue());
					vo.setTradeBfxFee(trade.getTrade_bfx_fee().doubleValue());
					vo.setTradeTime(trade.getTrade_time());
					
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(vo)));
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/trade/details")
	public String getTradeDetails(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);

		
		String buyUserId = request.getParameter("buyUserId");
		if (!StringUtil.isEmpty(buyUserId)) {
			map.put("buyUserId", buyUserId);
		}
		
		String sellUserId = request.getParameter("sellUserId");
		if (!StringUtil.isEmpty(sellUserId)) {
			map.put("sellUserId", sellUserId);
		}
		
		String quantitativeId = request.getParameter("quantitativeId");
		if (!StringUtil.isEmpty(quantitativeId)) {
			map.put("quantitativeId", quantitativeId);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
		}
		
		int buyid = Convert.toInt(buyUserId, -1);
		int sellid = Convert.toInt(sellUserId, -1);
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		if (StringUtil.isEmpty(type) || type.equalsIgnoreCase("contract")) {
			List<MemberTradeDetail> list = memberTradeDetailService.readByTime(symbol, buyid, sellid, quantitativeId, start, end, nFrom, direct, size);
			for (MemberTradeDetail detail : list) {
				TradeDetailVo detailVo = new TradeDetailVo();
				detailVo.setId(detail.getId());
				detailVo.setCode(detail.getTransaction_code());
				String buyTradeType = getTradeTypeStr(detail.getTrade_buy_type_id());
				detailVo.setBuyer(String.valueOf(detail.getF_buy_uid()) + "--" + detail.getF_buy_user_email() + "--" + buyTradeType);
				String sellTradeType = getTradeTypeStr(detail.getTrade_sell_type_id());
				detailVo.setSeller(String.valueOf(detail.getF_sell_uid()) + "--" + detail.getF_sell_user_email() + "--" + sellTradeType);
				detailVo.setTradePrice(detail.getTrade_price().doubleValue());
				detailVo.setTradeCount(detail.getTrade_count().doubleValue());
				DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				detailVo.setTradeTime(formatDate.format(detail.getTrade_time() * 1000));
	
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(detailVo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NMemberTradeDetail> list = nMemberTradeDetailService.readByTime(type, symbol, buyid, sellid, quantitativeId, start, end, nFrom, direct, size);
				for (NMemberTradeDetail detail : list) {
					TradeDetailVo detailVo = new TradeDetailVo();
					detailVo.setId(detail.getId());
					detailVo.setCode(detail.getTransaction_code());
					String buyTradeType = getTradeTypeStr(detail.getTrade_buy_type_id());
					detailVo.setBuyer(String.valueOf(detail.getF_buy_uid()) + "--" + detail.getF_buy_user_email() + "--" + buyTradeType);
					String sellTradeType = getTradeTypeStr(detail.getTrade_sell_type_id());
					detailVo.setSeller(String.valueOf(detail.getF_sell_uid()) + "--" + detail.getF_sell_user_email() + "--" + sellTradeType);
					detailVo.setTradePrice(detail.getTrade_price().doubleValue());
					detailVo.setTradeCount(detail.getTrade_count().doubleValue());
					DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					detailVo.setTradeTime(formatDate.format(detail.getTrade_time() * 1000));
		
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(detailVo)));
				}
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("prev/trade/details")
	public String getPrevTradeDetails(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String buyUserId = request.getParameter("buyUserId");
		if (!StringUtil.isEmpty(buyUserId)) {
			map.put("buyUserId", buyUserId);
		}
		
		String sellUserId = request.getParameter("sellUserId");
		if (!StringUtil.isEmpty(sellUserId)) {
			map.put("sellUserId", sellUserId);
		}
		
		String quantitativeId = request.getParameter("quantitativeId");
		if (!StringUtil.isEmpty(quantitativeId)) {
			map.put("quantitativeId", quantitativeId);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
		}
		
		int buyid = Convert.toInt(buyUserId, -1);
		int sellid = Convert.toInt(sellUserId, -1);
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		List<MemberTradeDetail> list = memberTradeDetailService.readFromBakByTime(symbol, buyid, sellid, quantitativeId, start, end, nFrom, direct, size);
		for (MemberTradeDetail detail : list) {
			TradeDetailVo detailVo = new TradeDetailVo();
			detailVo.setId(detail.getId());
			detailVo.setCode(detail.getTransaction_code());
			String buyTradeType = getTradeTypeStr(detail.getTrade_buy_type_id());
			detailVo.setBuyer(String.valueOf(detail.getF_buy_uid()) + "--" + detail.getF_buy_user_email() + "--" + buyTradeType);
			String sellTradeType = getTradeTypeStr(detail.getTrade_sell_type_id());
			detailVo.setSeller(String.valueOf(detail.getF_sell_uid()) + "--" + detail.getF_sell_user_email() + "--" + sellTradeType);
			detailVo.setTradePrice(detail.getTrade_price().doubleValue());
			detailVo.setTradeCount(detail.getTrade_count().doubleValue());
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			detailVo.setTradeTime(formatDate.format(detail.getTrade_time() * 1000));
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(detailVo)));
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/trade/details/quant")
	public String getTradeDetailsQuant(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String partition = request.getParameter("partition");
		if (!StringUtil.isEmpty(partition)) {
			map.put("partition", partition);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String quantitativeId = request.getParameter("quantitativeId");
		if (StringUtil.isEmpty(quantitativeId)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有quantitativeId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("quantitativeId", quantitativeId);
		
		String typeStr = request.getParameter("type");
		if (!StringUtil.isEmpty(typeStr)) {
			map.put("type", typeStr);
		}
		
		String prev = request.getParameter("prev");
		if (!StringUtil.isEmpty(prev)) {
			map.put("prev", prev);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
		}
		
		long quantUid = Convert.toLong(quantitativeId, -1);
		int type = Convert.toInt(typeStr, 0);
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		
		boolean isBak = false;
		if (!StringUtil.isEmpty(prev) && "true".equals(prev)) {
			isBak = true;
		}
		
		if (StringUtil.isEmpty(partition) || partition.equalsIgnoreCase("contract")) {
			List<MemberTradeDetail> list = memberTradeDetailService.readByMakerTaker(symbol, quantUid, type, start, end, nFrom, direct, size, isBak);
			for (MemberTradeDetail detail : list) {
				TradeDetailVo detailVo = new TradeDetailVo();
				detailVo.setId(detail.getId());
				detailVo.setCode(detail.getTransaction_code());
				String buyTradeType = getTradeTypeStr(detail.getTrade_buy_type_id());
				detailVo.setBuyer(String.valueOf(detail.getF_buy_uid()) + "--" + detail.getF_buy_user_email() + "--" + buyTradeType);
				String sellTradeType = getTradeTypeStr(detail.getTrade_sell_type_id());
				detailVo.setSeller(String.valueOf(detail.getF_sell_uid()) + "--" + detail.getF_sell_user_email() + "--" + sellTradeType);
				detailVo.setTradePrice(detail.getTrade_price().doubleValue());
				detailVo.setTradeCount(detail.getTrade_count().doubleValue());
				DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				detailVo.setTradeTime(formatDate.format(detail.getTrade_time() * 1000));
	
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(detailVo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(partition);
			if (part != null) {
				List<NMemberTradeDetail> list = nMemberTradeDetailService.readByMakerTaker(partition, symbol, quantUid, type, start, end, nFrom, direct, size, isBak);
				for (NMemberTradeDetail detail : list) {
					TradeDetailVo detailVo = new TradeDetailVo();
					detailVo.setId(detail.getId());
					detailVo.setCode(detail.getTransaction_code());
					String buyTradeType = getTradeTypeStr(detail.getTrade_buy_type_id());
					detailVo.setBuyer(String.valueOf(detail.getF_buy_uid()) + "--" + detail.getF_buy_user_email() + "--" + buyTradeType);
					String sellTradeType = getTradeTypeStr(detail.getTrade_sell_type_id());
					detailVo.setSeller(String.valueOf(detail.getF_sell_uid()) + "--" + detail.getF_sell_user_email() + "--" + sellTradeType);
					detailVo.setTradePrice(detail.getTrade_price().doubleValue());
					detailVo.setTradeCount(detail.getTrade_count().doubleValue());
					DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					detailVo.setTradeTime(formatDate.format(detail.getTrade_time() * 1000));
		
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(detailVo)));
				}
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	private String getTradeTypeStr(int type) {
		String tradeType = "";
		switch (type) {
		case 1:
			tradeType = "多单开仓";
			break;
		case 2:
			tradeType = "多单平仓";
			break;
		case 3:
			tradeType = "空头开仓";
			break;
		case 4:
			tradeType = "空头平仓";
			break;
		case 5:
			tradeType = "多单爆仓";
			break;
		case 6:
			tradeType = "空单爆仓";
			break;

		default:
			break;
		}

		return tradeType;
	}
	
	@RequestMapping(value = "/orders/place/plan", method = RequestMethod.POST)
	public String ordersPlacePlan(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String orderTypeStr = request.getParameter("orderType"); // 下单类型（0或者空：限价委托;1：市价委托）
		String amountStr = request.getParameter("amount");
		String priceStr = request.getParameter("price");
		String triPriceStr = request.getParameter("triPrice");
		String symbol = request.getParameter("symbol");
		String multiple = request.getParameter("multiple");
		String operation = request.getParameter("operation");
		
		if (StringUtil.isEmpty(amountStr) || StringUtil.isEmpty(symbol) || StringUtil.isEmpty(operation)
				|| StringUtil.isEmpty(triPriceStr) || StringUtil.isEmpty(multiple)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(priceStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("amount", amountStr);
		map.put("triPriceStr", triPriceStr);
		map.put("symbol", symbol);
		map.put("multiple", multiple);
		map.put("operation", operation);
		if (!StringUtil.isEmpty(orderTypeStr)) {
			map.put("orderType", orderTypeStr);
		}
		if (!StringUtil.isEmpty(priceStr)) {
			map.put("price", priceStr);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		
		String tradesStr = "";
		if ("spot".equals(type)) {
			tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
		} else if ("delivery".equals(type)) {
			tradesStr = orderDPlacePlan(orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, tickerJson);
		} else if ("vcontract".equals(type)) {
			tradesStr = orderVPlacePlan(orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, tickerJson);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			tradesStr = orderPlacePlan(orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, tickerJson);
		} else {
			tradesStr = orderNPlacePlan(type, orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, tickerJson);
		}

		if (!StringUtil.isEmpty(tradesStr)) {
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}

	@RequestMapping(value = "/orders/place/planPC", method = RequestMethod.POST)	//计划委托平仓
	public String ordersPlacePlanPC(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");

		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}

		String orderTypeStr = request.getParameter("orderType"); // 下单类型（0或者空：限价委托;1：市价委托）
		String positionId = request.getParameter("positionId");
		String amountStr = request.getParameter("amount");
		String priceStr = request.getParameter("price");
		String triPriceStr = request.getParameter("triPrice");	//触发价
		String symbol = request.getParameter("symbol");
		String multiple = request.getParameter("multiple");
		String operation = request.getParameter("operation");	//方向，buy / sell

		long mid = Convert.toLong(positionId, 0);

		if (StringUtil.isEmpty(amountStr) || StringUtil.isEmpty(symbol) || StringUtil.isEmpty(operation)
				|| StringUtil.isEmpty(triPriceStr) || StringUtil.isEmpty(multiple)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(priceStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		map.put("amount", amountStr);
		map.put("triPriceStr", triPriceStr);
		map.put("symbol", symbol);
		map.put("multiple", multiple);
		map.put("operation", operation);
		if (!StringUtil.isEmpty(orderTypeStr)) {
			map.put("orderType", orderTypeStr);
		}
		if (!StringUtil.isEmpty(priceStr)) {
			map.put("price", priceStr);
		}
		if (mid > 0) {
			map.put("positionId", positionId);
		}

		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}


		String tradesStr = "";
		if ("spot".equals(type)) {
			tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
		} else if ("delivery".equals(type)) {
			tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
		} else if ("vcontract".equals(type)) {
			tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			tradesStr = orderPlacePlanPC(orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, mid, tickerJson);
		} else {
			tradesStr = orderNPlacePlanPC(type, orderTypeStr, symbol, amountStr, priceStr, triPriceStr, operation, multiple, userId, mid, tickerJson);
		}

		if (!StringUtil.isEmpty(tradesStr)) {
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}

	
	@RequestMapping("/getPlanOrders")
	public String getPlanOrders(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size > 100) size = 100;
		int index = 0;

		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			List<VPlanPendingDelegation> planPdList = vPlanPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
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
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryPlanPendingDelegation> planPdList = dPlanPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
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
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<PlanPendingDelegation> planPdList = planPendingDelegationService.readPendingByUserIdAndTime(symbol, userId, start, end, nFrom, direct, size);
			for (PlanPendingDelegation pd : planPdList) {
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

				if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_INEFFECTIVE) {
					index += 1;
				}

				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NPlanPendingDelegation> planPdList = nPlanPendingDelegationService.readPendingByUserIdAndTime(type, symbol, userId, start, end, nFrom, direct, size);
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

					if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_INEFFECTIVE) {
						index += 1;
					}

					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(data)));
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		tickerJson.put("size", index);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/orders/cancel/plan", method = RequestMethod.POST)
	public String ordersCanelPlan(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", Contents.API_ERROR_UNKNOW);
		tickerJson.put("msg", "取消失败");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String orderId = request.getParameter("orderId");
		String symbol = request.getParameter("symbol");
		
		if (symbol == null || orderId == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("orderId", orderId);
		map.put("symbol", symbol);
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long id = Convert.toLong(orderId, -1);
		if (id < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orderId参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "暂不支持该类型");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			VTransaction bqTransaction = super.findCurrVTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VPlanPendingDelegation pd = vPlanPendingDelegationService.readById(id, bqTransaction.getTransaction_code(), userId);
			if (userId != pd.getF_user_id()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orderId参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "订单已撤单或者已成交");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int ret = cancelVPlanPending(pd);
			if (ret > 0) {
				tickerJson.put("code", 0);
				tickerJson.put("msg", "");
				tickerJson.put("data", pd.getId() + "");
			}
		} else if ("delivery".equals(type)) {
			DeliveryTransaction bqTransaction = super.findCurrDTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryPlanPendingDelegation pd = dPlanPendingDelegationService.readById(id, bqTransaction.getTransaction_code(), userId);
			if (userId != pd.getF_user_id()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orderId参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "订单已撤单或者已成交");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int ret = cancelDPlanPending(pd);
			if (ret > 0) {
				tickerJson.put("code", 0);
				tickerJson.put("msg", "");
				tickerJson.put("data", pd.getId() + "");
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction bqTransaction = super.findCurrTrans(symbol);
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			PlanPendingDelegation pd = planPendingDelegationService.readById(id, bqTransaction.getTransaction_code(), userId);
			if (userId != pd.getF_user_id()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orderId参数错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "订单已撤单或者已成交");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			int ret = cancelPlanPending(pd);
			if (ret > 0) {
				tickerJson.put("code", 0);
				tickerJson.put("msg", "");
				tickerJson.put("data", pd.getId() + "");
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction bqTransaction = super.findNCurrTrans(type, symbol);
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NPlanPendingDelegation pd = nPlanPendingDelegationService.readById(type, id, bqTransaction.getTransaction_code(), userId);
				if (userId != pd.getF_user_id()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "orderId参数错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "订单已撤单或者已成交");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				int ret = cancelNPlanPending(type, pd);
				if (ret > 0) {
					tickerJson.put("code", 0);
					tickerJson.put("msg", "");
					tickerJson.put("data", pd.getId() + "");
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/getContractBalance")
	public String getContractBalance(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		tickerJson.put("token", token);
		
		String code = request.getParameter("symbol");
		if (StringUtil.isEmpty(code)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		
		Map<String, Object> infoMap = new HashMap<String, Object>();
		BigDecimal userAccountBtc = BigDecimal.ZERO;
		
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		
		if ("vcontract".equals(type)) {
			VTransaction currentTransaction = super.findCurrVTrans(code);
			BigDecimal gift = BigDecimal.ZERO;
			
			VAccountContract accountContract = vAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
			userAccountBtc = accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN);
		} else if ("delivery".equals(type)) {
			DeliveryTransaction currentTransaction = super.findCurrDTrans(code);
			BigDecimal gift = BigDecimal.ZERO;
			
			DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
			userAccountBtc = accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction currentTransaction = super.findCurrTrans(code);
			BigDecimal gift = BigDecimal.ZERO;
			
			BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(currentTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}
			
			BQAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), currentTransaction.getTransaction_code());
			userAccountBtc = accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN);
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction currentTransaction = super.findNCurrTrans(type, code);
				BigDecimal gift = BigDecimal.ZERO;
				
				NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(type, currentTransaction.getTransaction_code(), userlevel.getLevel_id());
				if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
					feeRate = transactionFeeRate.getEat_commission_rate();
				}
				
				NAccountContract accountContract = nAccountContractService.readByUidCode(type, user.getF_user_id(), currentTransaction.getTransaction_code());
				userAccountBtc = accountContract.getF_btc().add(gift).setScale(4, RoundingMode.DOWN);
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		infoMap.put("balance", userAccountBtc.doubleValue());
		infoMap.put("feeRate", feeRate.doubleValue());
		
		Gson gson = new Gson();
		tickerJson.put("data", new JSONObject(gson.toJson(infoMap)));
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/stopLossClick")
	public String stopLossClick(HttpServletRequest request, HttpServletResponse response, Model model)  throws Exception {

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
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		tickerJson.put("token", token);
		
		Gson gson = new Gson();
		String transaction_member_id = request.getParameter("id");
		if (StringUtil.isEmpty(transaction_member_id)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			List<VStopLoss> lossList = vStopLossService.readStopLossList(user.getF_user_id(), Long.valueOf(transaction_member_id));
			tickerJson.put("data", gson.toJson(lossList));
		} else if ("delivery".equals(type)) {
			List<DeliveryStopLoss> lossList = dStopLossService.readStopLossList(user.getF_user_id(), Long.valueOf(transaction_member_id));
			tickerJson.put("data", gson.toJson(lossList));
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			List<BQStopLoss> lossList = bqStopLossService.readStopLossList(user.getF_user_id(), Long.valueOf(transaction_member_id));
			if (lossList.size() == 0) {
				BQMemberTransaction bqMemberTransaction = bqMemberTransactionService.readById(ConvertUtil.toLong(transaction_member_id, new Long(0)));
				if (null != bqMemberTransaction && bqMemberTransaction.getMt_id() > 0) {
					List<PlanPendingDelegation> delegations = planPendingDelegationService.readWinAndLossByUserIdStatusAndMId(bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), 0, bqMemberTransaction.getMt_id());
					for (PlanPendingDelegation pd : delegations) {
						BQStopLoss stopLoss = new BQStopLoss();
						stopLoss.setId(pd.getId());
						stopLoss.setTransaction_code(pd.getF_code());
						stopLoss.setTrade_type_id(pd.getDirection());
						stopLoss.setStop_loss_type_id(pd.getStop_loss_type_id());
						stopLoss.setOrder_type(pd.getOrder_type());
						stopLoss.setF_user_id(pd.getF_user_id());
						stopLoss.setTransaction_member_id(pd.getTransaction_member_id());
						stopLoss.setStop_loss_price(pd.getTri_price());
						stopLoss.setPending_amount(pd.getDelegation_count());
						stopLoss.setPending_trans_price(pd.getDelegation_price());
						stopLoss.setDateline(pd.getDelegation_time());
						lossList.add(stopLoss);
					}
				}
			}
			
			tickerJson.put("data", gson.toJson(lossList));
		} else {	//usdr
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NStopLoss> lossList = nStopLossService.readStopLossList(type, user.getF_user_id(), Long.valueOf(transaction_member_id));
				if (lossList.size() == 0) {
					NMemberTransaction bqMemberTransaction = nMemberTransactionService.readById(type, ConvertUtil.toLong(transaction_member_id, new Long(0)));
					if (null != bqMemberTransaction && bqMemberTransaction.getMt_id() > 0) {
						List<NPlanPendingDelegation> delegations = nPlanPendingDelegationService.readWinAndLossByUserIdStatusAndMId(type, bqMemberTransaction.getTransaction_code(), bqMemberTransaction.getF_user_id(), 0, bqMemberTransaction.getMt_id());
						for (NPlanPendingDelegation pd : delegations) {
							NStopLoss stopLoss = new NStopLoss();
							stopLoss.setId(pd.getId());
							stopLoss.setTransaction_code(pd.getF_code());
							stopLoss.setTrade_type_id(pd.getDirection());
							stopLoss.setStop_loss_type_id(pd.getStop_loss_type_id());
							stopLoss.setOrder_type(pd.getOrder_type());
							stopLoss.setF_user_id(pd.getF_user_id());
							stopLoss.setTransaction_member_id(pd.getTransaction_member_id());
							stopLoss.setStop_loss_price(pd.getTri_price());
							stopLoss.setPending_amount(pd.getDelegation_count());
							stopLoss.setPending_trans_price(pd.getDelegation_price());
							stopLoss.setDateline(pd.getDelegation_time());
							
							lossList.add(stopLoss);
						}
					}
				}

				tickerJson.put("data", gson.toJson(lossList));
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}

	@RequestMapping("/stopLossChange")
	public String stopLossChange(HttpServletRequest request, HttpServletResponse response, Model model)  throws Exception {
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
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		tickerJson.put("token", token);

		// 校验
		String stop_win_order_type = request.getParameter("stop_win_order_type"); // 下单类型（0或者空：限价委托;1：市价委托）
		String stop_loss_order_type = request.getParameter("stop_loss_order_type"); // 下单类型（0或者空：限价委托;1：市价委托）
		String pending_amount = request.getParameter("pending_amount");
		String transaction_member_id = request.getParameter("id");
		String has_win = request.getParameter("has_win");
		String stop_win_price = request.getParameter("stop_win_price");
		String pending_win_price = request.getParameter("pending_win_price");

		String has_loss = request.getParameter("has_loss");
		String stop_loss_price = request.getParameter("stop_loss_price");
		String pending_loss_price = request.getParameter("pending_loss_price");

		int orderType = 0;
		int stopWinOrderType = Convert.toInt(stop_win_order_type, 0);
		int stopLossOrderType = Convert.toInt(stop_loss_order_type, 0);
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			VMemberTransaction bqt = vMemberTransactionService.get(Long.valueOf(transaction_member_id));
			if (bqt == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (bqt.getF_user_id() != user.getF_user_id()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "会话已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VTransaction bqTransaction = super.findCurrVTrans(bqt.getTransaction_code());
			VTransactionType bqTransactionType = vTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，合约类型错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE);
			if(lastPrice == null){
				lastPrice = vTransactionService.readTransByCode(bqt.getTransaction_code()).getLatest_transaction_price();
			}
			
			int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
			BigDecimal stop_win_price_client = new BigDecimal(0);
			BigDecimal pending_win_price_client = new BigDecimal(0);
			BigDecimal stop_loss_price_client = new BigDecimal(0);
			BigDecimal pending_loss_price_client = new BigDecimal(0);
			
			BigDecimal pending_amount_client = BigDecimal.ZERO;
			if (!StringUtil.isEmpty(pending_amount)) {
				pending_amount_client = new BigDecimal(pending_amount).setScale(Utils.getAmountDigtis(bqt.getTransaction_code()), BigDecimal.ROUND_DOWN);
			} else {
				pending_amount_client = bqt.getHold_count();
			}
			
			if (pending_amount_client.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pending_amount_client.compareTo(bqt.getHold_count()) > 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount不能大于持仓量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VAccountContract bqAccount = vAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
			if (bqt.getTrade_type_id() == 1) {	// 多仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 多仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0
								|| (orderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) <= 0)
							|| stop_win_price_client.compareTo(lastPrice) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止盈价须大于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_win)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
	
				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {	// 多仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					/* 要求止损价小于最新价 */
					if (stop_loss_price_client.compareTo(lastPrice) >= 0
							|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) >= 0)) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须小于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
							|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须大于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_loss)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {// 空仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 空仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0
								|| (orderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
	
					if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) >= 0)
							|| stop_win_price_client.compareTo(lastPrice) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止盈价须小于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_win)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {// 空仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					/* 要求止损价大于最新价 */
					if (stop_loss_price_client.compareTo(lastPrice) <= 0
							|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) <= 0)) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须大于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) >= 0)
							|| stop_loss_price_client.compareTo(baocangPrice) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须小于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_loss)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
	
			vStopLossService.updateStopLosses(Long.valueOf(transaction_member_id), has_win, stop_win_price,
					pending_win_price, has_loss, stop_loss_price, pending_loss_price, orderType, pending_amount_client.toString());
		
		} else if ("delivery".equals(type)) {
			DeliveryMemberTransaction bqt = dMemberTransactionService.readById(Long.valueOf(transaction_member_id));
			if (bqt == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (bqt.getF_user_id() != user.getF_user_id()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "会话已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryTransaction bqTransaction = super.findCurrDTrans(bqt.getTransaction_code());
			DeliveryTransactionType bqTransactionType = dTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，合约类型错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE);
			if(lastPrice == null){
				lastPrice = dTransactionService.readTransByCode(bqt.getTransaction_code()).getLatest_transaction_price();
			}
			
			int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
			BigDecimal stop_win_price_client = new BigDecimal(0);
			BigDecimal pending_win_price_client = new BigDecimal(0);
			BigDecimal stop_loss_price_client = new BigDecimal(0);
			BigDecimal pending_loss_price_client = new BigDecimal(0);
			
			BigDecimal pending_amount_client = BigDecimal.ZERO;
			if (!StringUtil.isEmpty(pending_amount)) {
				pending_amount_client = new BigDecimal(pending_amount).setScale(Utils.getAmountDigtis(bqt.getTransaction_code()), BigDecimal.ROUND_DOWN);
			} else {
				pending_amount_client = bqt.getHold_count();
			}
			if (pending_amount_client.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pending_amount_client.compareTo(bqt.getHold_count()) > 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount不能大于持仓量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryAccountContract bqAccount = dAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
			if (bqt.getTrade_type_id() == 1) {	// 多仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 多仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0
								|| (orderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) <= 0)
							|| stop_win_price_client.compareTo(lastPrice) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止盈价须大于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_win)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
	
				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {	// 多仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					/* 要求止损价小于最新价 */
					if (stop_loss_price_client.compareTo(lastPrice) >= 0
							|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) >= 0)) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须小于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
							|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须大于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_loss)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {// 空仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 空仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0
								|| (orderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
	
					if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) >= 0)
							|| stop_win_price_client.compareTo(lastPrice) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止盈价须小于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_win)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {// 空仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (orderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					/* 要求止损价大于最新价 */
					if (stop_loss_price_client.compareTo(lastPrice) <= 0 
							|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) <= 0)) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须大于最新价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) >= 0)
							|| stop_loss_price_client.compareTo(baocangPrice) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须小于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else if ("0".equalsIgnoreCase(has_loss)) {
	
				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
	
			dStopLossService.updateStopLosses(Long.valueOf(transaction_member_id), has_win, stop_win_price,
					pending_win_price, has_loss, stop_loss_price, pending_loss_price, orderType, pending_amount_client.toString());
			
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			BQMemberTransaction bqt = bqMemberTransactionService.readById(Long.valueOf(transaction_member_id));
			if (bqt == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (bqt.getF_user_id() != user.getF_user_id()) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "会话已过期");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			BQTransaction bqTransaction = super.findCurrTransByCode(bqt.getTransaction_code());
			BQTransactionType bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
			if (bqTransactionType == null) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，合约类型错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
			if(lastPrice == null){
				lastPrice = bqTransactionService.readTransByCode(bqt.getTransaction_code()).getLatest_transaction_price();
			}

			int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
			BigDecimal stop_win_price_client = new BigDecimal(0);
			BigDecimal pending_win_price_client = new BigDecimal(0);
			BigDecimal stop_loss_price_client = new BigDecimal(0);
			BigDecimal pending_loss_price_client = new BigDecimal(0);

			BigDecimal pending_amount_client = BigDecimal.ZERO;
			if (!StringUtil.isEmpty(pending_amount)) {
				pending_amount_client = new BigDecimal(pending_amount).setScale(Utils.getAmountDigtis(bqt.getTransaction_code()), BigDecimal.ROUND_DOWN);
			} else {
				pending_amount_client = bqt.getHold_count();
			}
			if (pending_amount_client.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			if (pending_amount_client.compareTo(bqt.getHold_count()) > 0) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount不能大于持仓量");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

			PlanPendingDelegation delegationWin = null;
			PlanPendingDelegation delegationLoss = null;

			BQAccountContract bqAccount = bqAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
			if (bqt.getTrade_type_id() == 1) {	// 多仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 多仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (stopWinOrderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0
								|| (stopWinOrderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stop_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须大于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stopWinOrderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止盈价须大于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					int typeWin = 0;
					if (lastPrice.compareTo(stop_win_price_client) >= 0) {
						typeWin = Contents.PLAN_PENDING_TYPE_GT;
					} else {
						typeWin = Contents.PLAN_PENDING_TYPE_LT;
					}

					delegationWin = new PlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
							stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeWin,
							Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止盈计划委托挂单创建-未生效", stopWinOrderType, 1, (int) bqt.getMt_id(), 1);

				} else if ("0".equalsIgnoreCase(has_win)) {

				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {	// 多仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (stopLossOrderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (stopLossOrderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stop_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须小于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stopLossOrderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须小于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count,
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if ((stopLossOrderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
							|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须大于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					int typeLoss = 0;
					if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
						typeLoss = Contents.PLAN_PENDING_TYPE_GT;
					} else {
						typeLoss = Contents.PLAN_PENDING_TYPE_LT;
					}

					delegationLoss = new PlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
							stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeLoss,
							Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止损计划委托挂单创建-未生效", stopLossOrderType, 1, (int) bqt.getMt_id(), 2);

				} else if ("0".equalsIgnoreCase(has_loss)) {

				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {// 空仓
				if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 空仓止盈
					try {
						stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (stopWinOrderType == 0) {
							pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_win_price_client.signum() <= 0 || (stopWinOrderType == 0 && pending_win_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stop_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须小于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stopWinOrderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止盈价须小于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					int typeWin = 0;
					if (lastPrice.compareTo(stop_win_price_client) >= 0) {
						typeWin = Contents.PLAN_PENDING_TYPE_GT;
					} else {
						typeWin = Contents.PLAN_PENDING_TYPE_LT;
					}

					delegationWin = new PlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
							stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeWin,
							Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止盈计划委托挂单创建-未生效", stopWinOrderType, 1, (int) bqt.getMt_id(), 1);

				} else if ("0".equalsIgnoreCase(has_win)) {

				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {// 空仓止损
					try {
						stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						if (stopLossOrderType == 0) {
							pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
						}
						if (stop_loss_price_client.signum() <= 0
								|| (stopLossOrderType == 0 && pending_loss_price_client.signum() <= 0)) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stop_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须大于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (stopLossOrderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须大于持仓均价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
							.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
					BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
					BigDecimal baocangPrice =  BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count,
							bqTransaction.getTrade_coefficient(), bqt.getDeposit());
					baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
					if (pending_loss_price_client.compareTo(baocangPrice) >= 0 || stop_loss_price_client.compareTo(baocangPrice) >= 0) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须小于爆仓价");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					int typeLoss = 0;
					if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
						typeLoss = Contents.PLAN_PENDING_TYPE_GT;
					} else {
						typeLoss = Contents.PLAN_PENDING_TYPE_LT;
					}

					delegationLoss = new PlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
							stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeLoss,
							Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止损计划委托挂单创建-未生效", stopLossOrderType, 1, (int) bqt.getMt_id(), 2);

				} else if ("0".equalsIgnoreCase(has_loss)) {

				} else {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}

			List<PlanPendingDelegation> delegations = new ArrayList<>();
			if (null != delegationWin) {
				delegations.add(delegationWin);
			}
			if (null != delegationLoss) {
				delegations.add(delegationLoss);
			}

			try {
				String lock = spyMemcachedClient.lock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						planPendingDelegationService.updateWinAndLoss(delegations, bqTransaction.getTransaction_code(), user.getF_user_id(), bqt.getMt_id());
					} catch (Exception e) {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "设置止盈止损失败");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					} finally {
						spyMemcachedClient.unlock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id(), lock);
					}
				}
			} catch (Exception e) {
				String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "设置止盈止损失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}

		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NMemberTransaction bqt = nMemberTransactionService.readById(type, Long.valueOf(transaction_member_id));
				if (bqt == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，id错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (bqt.getF_user_id() != user.getF_user_id()) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "会话已过期");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				NTransaction bqTransaction = super.findNCurrTrans(type, bqt.getTransaction_code());
				NTransactionType bqTransactionType = nTransactionTypeService.readTransactionType(type, bqTransaction.getTransaction_type());
				if (bqTransactionType == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，合约类型错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				BigDecimal lastPrice = spyMemcachedClient.get(type + "_" + bqt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
				if(lastPrice == null){
					lastPrice = nTransactionService.readTransByCode(type, bqt.getTransaction_code()).getLatest_transaction_price();
				}

				int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
				BigDecimal stop_win_price_client = new BigDecimal(0);
				BigDecimal pending_win_price_client = new BigDecimal(0);
				BigDecimal stop_loss_price_client = new BigDecimal(0);
				BigDecimal pending_loss_price_client = new BigDecimal(0);

				BigDecimal pending_amount_client = BigDecimal.ZERO;
				if (!StringUtil.isEmpty(pending_amount)) {
					pending_amount_client = new BigDecimal(pending_amount).setScale(Utils.getAmountDigtis(bqt.getTransaction_code()), BigDecimal.ROUND_DOWN);
				} else {
					pending_amount_client = bqt.getHold_count();
				}
				if (pending_amount_client.compareTo(BigDecimal.ZERO) <= 0) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount错误");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				if (pending_amount_client.compareTo(bqt.getHold_count()) > 0) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "参数错误，pending_amount不能大于持仓量");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				NPlanPendingDelegation delegationWin = null;
				NPlanPendingDelegation delegationLoss = null;

				NAccountContract bqAccount = nAccountContractService.readBTCByUidCode(type, bqt.getF_user_id(), bqt.getTransaction_code());
				if (bqt.getTrade_type_id() == 1) {	// 多仓
					if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 多仓止盈
						try {
							stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
							if (stopWinOrderType == 0) {
								pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
							}
							if (stop_win_price_client.signum() <= 0
									|| (stopWinOrderType == 0 && pending_win_price_client.signum() <= 0)) {
								String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
								model.addAttribute("ticker", tradesStr);
								return apiData(response);
							}
						} catch (Exception e) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stop_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须大于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stopWinOrderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止盈价须大于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						int typeWin = 0;
						if (lastPrice.compareTo(stop_win_price_client) >= 0) {
							typeWin = Contents.PLAN_PENDING_TYPE_GT;
						} else {
							typeWin = Contents.PLAN_PENDING_TYPE_LT;
						}

						delegationWin = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
								stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeWin,
								Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止盈计划委托挂单创建-未生效", stopWinOrderType, 1, (int) bqt.getMt_id(), 1);

					} else if ("0".equalsIgnoreCase(has_win)) {

					} else {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}

					if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {	// 多仓止损
						try {
							stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
							if (stopLossOrderType == 0) {
								pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
							}
							if (stop_loss_price_client.signum() <= 0
									|| (stopLossOrderType == 0 && pending_loss_price_client.signum() <= 0)) {
								String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
								model.addAttribute("ticker", tradesStr);
								return apiData(response);
							}
						} catch (Exception e) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stop_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须小于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stopLossOrderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须小于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
								.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
						BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
						BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count,
								bqTransaction.getTrade_coefficient(), bqt.getDeposit());
						baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
						if ((stopLossOrderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
								|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "多仓止损价须大于爆仓价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						int typeLoss = 0;
						if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
							typeLoss = Contents.PLAN_PENDING_TYPE_GT;
						} else {
							typeLoss = Contents.PLAN_PENDING_TYPE_LT;
						}

						delegationLoss = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
								stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeLoss,
								Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止损计划委托挂单创建-未生效", stopLossOrderType, 1, (int) bqt.getMt_id(), 2);

					} else if ("0".equalsIgnoreCase(has_loss)) {

					} else {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else {// 空仓
					if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 空仓止盈
						try {
							stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
							if (stopWinOrderType == 0) {
								pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
							}
							if (stop_win_price_client.signum() <= 0 || (stopWinOrderType == 0 && pending_win_price_client.signum() <= 0)) {
								String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price或pending_win_price错误，价格不能为非正数");
								model.addAttribute("ticker", tradesStr);
								return apiData(response);
							}
						} catch (Exception e) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_win_price错误，价格不为数字");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stop_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须小于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stopWinOrderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止盈价须小于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						int typeWin = 0;
						if (lastPrice.compareTo(stop_win_price_client) >= 0) {
							typeWin = Contents.PLAN_PENDING_TYPE_GT;
						} else {
							typeWin = Contents.PLAN_PENDING_TYPE_LT;
						}

						delegationWin = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
								stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeWin,
								Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止盈计划委托挂单创建-未生效", stopWinOrderType, 1, (int) bqt.getMt_id(), 1);

					} else if ("0".equalsIgnoreCase(has_win)) {

					} else {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_win错误，参数标识错误");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {// 空仓止损
						try {
							stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
							if (stopLossOrderType == 0) {
								pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
							}
							if (stop_loss_price_client.signum() <= 0
									|| (stopLossOrderType == 0 && pending_loss_price_client.signum() <= 0)) {
								String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price或pending_loss_price错误，价格不能为非正数");
								model.addAttribute("ticker", tradesStr);
								return apiData(response);
							}
						} catch (Exception e) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "stop_loss_price错误，价格不为数字");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stop_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "触发价须大于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						if (stopLossOrderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须大于持仓均价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
								.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
						BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
						BigDecimal baocangPrice =  BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count,
								bqTransaction.getTrade_coefficient(), bqt.getDeposit());
						baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
						if (pending_loss_price_client.compareTo(baocangPrice) >= 0 || stop_loss_price_client.compareTo(baocangPrice) >= 0) {
							String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "空仓止损价须小于爆仓价");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						}

						int typeLoss = 0;
						if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
							typeLoss = Contents.PLAN_PENDING_TYPE_GT;
						} else {
							typeLoss = Contents.PLAN_PENDING_TYPE_LT;
						}

						delegationLoss = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
								stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeLoss,
								Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止损计划委托挂单创建-未生效", stopLossOrderType, 1, (int) bqt.getMt_id(), 2);

					} else if ("0".equalsIgnoreCase(has_loss)) {

					} else {
						String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "has_loss错误，参数标识错误");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				}

				List<NPlanPendingDelegation> delegations = new ArrayList<>();
				if (null != delegationWin) {
					delegations.add(delegationWin);
				}
				if (null != delegationLoss) {
					delegations.add(delegationLoss);
				}

				try {
					String lock = spyMemcachedClient.lock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + type + "_" + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id());
					if (!StringUtil.isEmpty(lock)) {
						try {
							nPlanPendingDelegationService.updateWinAndLoss(type, delegations, bqTransaction.getTransaction_code(), user.getF_user_id(), bqt.getMt_id());
						} catch (Exception e) {
							String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "设置止盈止损失败");
							model.addAttribute("ticker", tradesStr);
							return apiData(response);
						} finally {
							spyMemcachedClient.unlock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + type + "_" + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id(), lock);
						}
					}

				} catch (Exception e) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "设置止盈止损失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/getMultiple")
	public String getMultiple(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		if ("vcontract".equals(type)) {
			VTransaction bqTransaction = null;
			List<VTransaction> list = super.findCurrVTrans();
			for (VTransaction bq : list) {
				if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
					bqTransaction = bq;
					break;
				}
			}
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				return tradesStr;
			}
			
			List<MulpitleAndStateVO> vos = getVMultipleVos(bqTransaction, userId);
			for (MulpitleAndStateVO data : vos) {
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else if ("delivery".equals(type)) {
			DeliveryTransaction bqTransaction = null;
			List<DeliveryTransaction> list = super.findCurrDTrans();
			for (DeliveryTransaction bq : list) {
				if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
					bqTransaction = bq;
					break;
				}
			}
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				return tradesStr;
			}
			
			List<MulpitleAndStateVO> vos = getDMultipleVos(bqTransaction, userId);
			for (MulpitleAndStateVO data : vos) {
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction bqTransaction = null;
			List<BQTransaction> list = super.findCurrTrans();
			for (BQTransaction bq : list) {
				if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
					bqTransaction = bq;
					break;
				}
			}
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				return tradesStr;
			}
			
			List<MulpitleAndStateVO> vos = getMultipleVos(bqTransaction, userId);
			for (MulpitleAndStateVO data : vos) {
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction bqTransaction = null;
				List<NTransaction> list = super.findNCurrTrans(type);
				for (NTransaction bq : list) {
					if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqTransaction = bq;
						break;
					}
				}
				if (bqTransaction == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					return tradesStr;
				}
				
				List<MulpitleAndStateVO> vos = getNMultipleVos(type, bqTransaction, userId);
				for (MulpitleAndStateVO data : vos) {
					Gson gson = new Gson();
					array.put(new JSONObject(gson.toJson(data)));
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/positions/liquidation")
	public String getPositionsLiquidation(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		
		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		if (userId == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "ERROR");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String buyOrderBy = "ASC";
		String sellOrderBy = "ASC";
		List<Object[]> lstBuyMembTrans = null;
		List<Object[]> lstSellMembTrans = null;
		if ("delivery".equals(type)) {
			DeliveryTransaction bqTransaction = null;
			List<DeliveryTransaction> list = super.findCurrDTrans();
			for (DeliveryTransaction bq : list) {
				if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
					bqTransaction = bq;
					break;
				}
			}
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				return tradesStr;
			}
			
			lstBuyMembTrans = dMemberTransactionService.readHolds(symbol, Contents.DIRECTION_BUY, bqTransaction.getTrade_coefficient(), buyOrderBy);
			lstSellMembTrans = dMemberTransactionService.readHolds(symbol, Contents.DIRECTION_SELL, bqTransaction.getTrade_coefficient(), sellOrderBy);
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction bqTransaction = null;
			List<BQTransaction> list = super.findCurrTrans();
			for (BQTransaction bq : list) {
				if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
					bqTransaction = bq;
					break;
				}
			}
			if (bqTransaction == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				return tradesStr;
			}
			
			lstBuyMembTrans = bqMemberTransactionService.readHolds(symbol, Contents.DIRECTION_BUY, bqTransaction.getTrade_coefficient(), buyOrderBy);
			lstSellMembTrans = bqMemberTransactionService.readHolds(symbol, Contents.DIRECTION_SELL, bqTransaction.getTrade_coefficient(), sellOrderBy);
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		JSONArray buyArray = new JSONArray();
		for (Object[] obj : lstBuyMembTrans) {
			MemberTransactionVo vo = new MemberTransactionVo();
			vo.setSymbol(symbol);
			vo.setUserId((Integer) obj[1]);
			vo.setHoldCount(((BigDecimal) obj[0]).doubleValue());
			vo.setMultiple(((BigDecimal) obj[2]).intValue());
			vo.setAvgPrice(((BigDecimal) obj[3]).doubleValue());
			vo.setDirection(1);
			vo.setForcedLiquidationPrice(((BigDecimal) obj[5]).doubleValue());
			
			Gson gson = new Gson();
			buyArray.put(new JSONObject(gson.toJson(vo)));
		}
		JSONArray sellArray = new JSONArray();
		for (Object[] obj : lstSellMembTrans) {
			MemberTransactionVo vo = new MemberTransactionVo();
			vo.setSymbol(symbol);
			vo.setUserId((Integer) obj[1]);
			vo.setHoldCount(((BigDecimal) obj[0]).doubleValue());
			vo.setMultiple(((BigDecimal) obj[2]).intValue());
			vo.setAvgPrice(((BigDecimal) obj[3]).doubleValue());
			vo.setDirection(2);
			vo.setForcedLiquidationPrice(((BigDecimal) obj[5]).doubleValue());
			
			Gson gson = new Gson();
			sellArray.put(new JSONObject(gson.toJson(vo)));
		}
		
		JSONObject data = new JSONObject();
		data.put("sell", sellArray);
		data.put("buy", buyArray);
		tickerJson.put("data", data);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	private String orderPlaceOneV(HttpServletRequest request, boolean isApi, String orderTypeStr, String percentStr,
			String symbol, String amountStr, String priceStr, String operation, String multiple, long userId,
			JSONObject tickerJson, BQMemberLevel userlevel) {
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}
		
		VTransaction bqTransaction = null;
		List<VTransaction> list = super.findCurrVTrans();
		for (VTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}

		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		VTransactionType bqTransactionType = vTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}

		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}
		
		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		int canMultiple = getVOneCanMultiple(bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "vcontract");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		// 开仓数量及价格验证
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			priceStr = getVMarketOrderPrice(bqTransaction, direction);
			if (StringUtil.isEmpty(priceStr)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "当前无买一或卖一，不支持市价下单");
				return tradesStr;
			}
		}
		
		BigDecimal coefficient = bqTransaction.getTrade_coefficient();
		BigDecimal multiple_client2 = new BigDecimal(multiple);
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal price = new BigDecimal(priceStr);
		
		if (orderType == 1) {
			price = price.setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
		}
		
		double dprice = ConvertUtil.toDouble(priceStr, 0);
		if (dprice <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
			return tradesStr;
		}

		if (price.scale() > pricePrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
			return tradesStr;
		}
		
		BigDecimal btc_index_price = super.findBPIByCode(bqTransactionType.getType_code());
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单失败，无法获取指数");
			return tradesStr;
		}
		
		VTransactionRate index_price_rate = vTransactionRateService.readByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
		
		if (direction == Contents.DIRECTION_BUY && price.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price = maxIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		if (direction == Contents.DIRECTION_SELL && price.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price = minIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
				return tradesStr;
			}
		}

		BigDecimal fee = BigDecimal.ZERO;
		if (userlevel == null) {
			userlevel = bqMemberLevelService.findByUserId(userId);
		}
		BigDecimal feeRate = userlevel.getEat_commission_rate();

		BigDecimal remainAmount = BigDecimal.ZERO;
		VAccountContract bqContract = vAccountContractService.readByUidCode(userId, bqTransaction.getTransaction_code());
		if (bqContract != null) {
			remainAmount = bqContract.getF_btc();
		}
		
		if (!StringUtil.isEmpty(percentStr)) {
			BigDecimal percent = new BigDecimal(percentStr.replace("%", "")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			if (percent.compareTo(BigDecimal.ONE) > 0) percent = BigDecimal.ONE;
			if (percent.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "百分比参数错误");
				return tradesStr;
			}
			
			BigDecimal maxAmount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
			amount = maxAmount.multiply(percent).setScale(amountPrecision, BigDecimal.ROUND_DOWN);
		} else {
			double damount = ConvertUtil.toDouble(amountStr, 0);
			if (damount <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
				return tradesStr;
			}
			
			amount = new BigDecimal(amountStr);
			
			if (amount.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
				return tradesStr;
			}
		}
		
		if (amount.compareTo(bqTransactionType.getMin_kc_nums()) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量必须大于" + bqTransactionType.getMin_kc_nums().doubleValue());
			return tradesStr;
		}
		
		fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);

		BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
				.divide(price, 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
		if (amount.compareTo(maxAmount) > 0) {
			if (orderType == 1) {
				amount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);;
				amount = amount.setScale(amountPrecision, BigDecimal.ROUND_DOWN);
				fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
				deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
				return tradesStr;
			}
		}
		
		String sip = IpMacUtil.getIpAddr(request);
		
		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount, price, BigDecimal.ZERO, amount, Integer.valueOf(multiple), deposit,
				userlevel.getEat_commission_rate(), Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			vPendingDelegationCacheService.insertCacheAndChangeAccount(cahce, bqTransaction.getCurrency_type_id(), fee, bqContract, true, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
			try {
				tickerJson.put("data", cahce.getPending_no() + "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		} catch (ServiceException e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderPlaceOne(HttpServletRequest request, boolean isApi, String orderTypeStr, String percentStr,
			String symbol, String amountStr, String priceStr, String operation, String multiple, long userId,
			JSONObject tickerJson, BQMemberLevel userlevel) {
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		BQTransaction bqTransaction = null;
		BQTransactionType bqTransactionType = null;
		List<BQTransaction> list = super.findCurrTrans();
		for (BQTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}
		
		if (!isApi) {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
			if (setting == null || setting.getF_user_safetype() == 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
				return tradesStr;
			}
		}
		
		BigDecimal fee = BigDecimal.ZERO;
		if (userlevel == null) {
			BQMember user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				return tradesStr;
			}
			userlevel = super.getLevelByUserBfx(user);
		}
		
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		boolean withLog = true;
		if (feeRate.compareTo(BigDecimal.ZERO) == 0) {
			withLog = false;
		}
		
		if (withLog) {
			String[] ganggan = bqTransaction.getMultiple().split(",");
			String[] ggzt = bqTransaction.getMultiple_state().split(",");
			boolean ggky = false;
			for (int i = 0; i < ganggan.length; i++) {
				if (multiple.equals(ganggan[i])) {
					if ("1".equals(ggzt[i])) {
						ggky = true;
						break;
					}
				}
			}
			
			if (!ggky) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
				return tradesStr;
			}
			
			int canMultiple = getOneCanMultiple(bqTransaction, userId);
			if (canMultiple > 0 && canMultiple != multiple_client) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
				return tradesStr;
			}
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "contract");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		// 开仓数量及价格验证
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			priceStr = getMarketOrderPrice(bqTransaction, direction);
			if (StringUtil.isEmpty(priceStr)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "当前无买一或卖一，不支持市价下单");
				return tradesStr;
			}
		}
		
		BigDecimal coefficient = bqTransaction.getTrade_coefficient();
		BigDecimal multiple_client2 = new BigDecimal(multiple);
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal price = new BigDecimal(priceStr);
		
		if (orderType == 1) {
			price = price.setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
		}
		
		double dprice = ConvertUtil.toDouble(priceStr, 0);
		if (dprice <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
			return tradesStr;
		}

		if (price.scale() > pricePrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
			return tradesStr;
		}

		BigDecimal btc_index_price = super.findBPIByCode(bqTransactionType.getType_code());
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单失败，无法获取指数");
			return tradesStr;
		}
		
		BigDecimal rateUp = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
		BigDecimal rateDown = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			BQTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());
			rateDown = index_price_rate.getDownrate();
			rateUp = index_price_rate.getUprate();
			
			spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
			spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
		}
		
		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
		}
		
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
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
		
//		if (bqTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//			}
//		}
		
		if (direction == Contents.DIRECTION_BUY && price.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price = maxIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		if (direction == Contents.DIRECTION_SELL && price.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price = minIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		BQAccountContract bqContract = null;
		BigDecimal remainAmount = BigDecimal.ZERO;
		if (withLog) {
			bqContract = bqAccountContractService.readByUidCode(userId, bqTransaction.getTransaction_code());
			if (bqContract != null) {
				remainAmount = bqContract.getF_btc();
			}
		}
		
		if (!StringUtil.isEmpty(percentStr)) {
			BigDecimal percent = new BigDecimal(percentStr.replace("%", "")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			if (percent.compareTo(BigDecimal.ONE) > 0) percent = BigDecimal.ONE;
			if (percent.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "百分比参数错误");
				return tradesStr;
			}
			
			BigDecimal maxAmount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
			amount = maxAmount.multiply(percent).setScale(amountPrecision, BigDecimal.ROUND_DOWN);
		} else {
			double damount = ConvertUtil.toDouble(amountStr, 0);
			if (damount <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
				return tradesStr;
			}
			
			amount = new BigDecimal(amountStr);
			
			if (amount.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
				return tradesStr;
			}
		}
		
		if (amount.compareTo(bqTransactionType.getMin_kc_nums()) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量必须大于" + bqTransactionType.getMin_kc_nums().doubleValue());
			return tradesStr;
		}
		
		fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);

		String sip = "";
		if (withLog) {
			BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
					.divide(price.multiply(coefficient), 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
			if (amount.compareTo(maxAmount) > 0) {
				if (orderType == 1) {
					amount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
					amount = amount.setScale(amountPrecision, BigDecimal.ROUND_DOWN);
					fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
					deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					return tradesStr;
				}
			}
			
			sip = IpMacUtil.getIpAddr(request);
		
			if (!inWhiteList(String.valueOf(userId))) {
				String ret = maxUserKcCheck(bqTransaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(ret)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, ret);
					return tradesStr;
				}
				
				String risk = riskCheck( bqTransaction.getTransaction_code(), direction, price, amount, pricePrecision);
				if (!StringUtil.isEmpty(risk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, risk);
					return tradesStr;
				}
				
				String maxkc = maxKcCheck(bqTransactionType, bqTransaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(maxkc)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, maxkc);
					return tradesStr;
				}
			}
			
			if (!inApiWhiteList(String.valueOf(userId)) && isApi) {
				BQUserApiSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING);
				if (setting == null) {
					setting = bqUserApiSettingService.readOne();
					spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);
				}
				if (setting != null && setting.getIs_open()) {
					try {
						Thread.sleep(setting.getDelay_time());
					} catch (InterruptedException e) {
						System.out.println("userId: " + userId + ", contract: " + bqTransaction.getTransaction_code());
						e.printStackTrace();
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
						return tradesStr;
					}
				}
			}
		}

		PendingDelegationCache cache = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount, price, BigDecimal.ZERO, amount, Integer.valueOf(multiple), deposit,
				feeRate, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
		try {
			pendingDelegationCacheService.insertCacheAndChangeAccount(cache, fee, bqContract, withLog, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
			try {
				tickerJson.put("data", cache.getPending_no() + "");
				WebSocket.addPendingToMemcached(cache);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		} catch (ServiceException e) {
			System.out.println("userId: " + userId + ", contract: " + bqTransaction.getTransaction_code());
			e.printStackTrace();
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderPlaceOneN(HttpServletRequest request, String partition, boolean isApi, String orderTypeStr, String percentStr,
			String symbol, String amountStr, String priceStr, String operation, String multiple, long userId,
			JSONObject tickerJson, BQMemberLevel userlevel) {
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, symbol);
		NTransactionType bqTransactionType = null;
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = nTransactionTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0 || multiple_client > 500) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!nNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}
		
		if (!isApi) {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
			if (setting == null || setting.getF_user_safetype() == 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
				return tradesStr;
			}
		}
		
		BigDecimal fee = BigDecimal.ZERO;
		if (userlevel == null) {
			BQMember user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				return tradesStr;
			}
			userlevel = super.getLevelByUserBfx(user);
		}
		
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		boolean withLog = true;
		if (feeRate.compareTo(BigDecimal.ZERO) == 0) {
			withLog = false;
		}
		
		if (withLog) {
			int canMultiple = getNOneCanMultiple(partition, bqTransaction, userId);
			if (canMultiple > 0 && canMultiple != multiple_client) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
				return tradesStr;
			}
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), partition);
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		// 开仓数量及价格验证
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			priceStr = getNMarketOrderPrice(partition, bqTransaction, direction);
			if (StringUtil.isEmpty(priceStr)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "当前无买一或卖一，不支持市价下单");
				return tradesStr;
			}
		}
		
		BigDecimal coefficient = bqTransaction.getTrade_coefficient();
		BigDecimal multiple_client2 = new BigDecimal(multiple);
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal price = new BigDecimal(priceStr);
		
		if (orderType == 1) {
			price = price.setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
		}
		
		double dprice = ConvertUtil.toDouble(priceStr, 0);
		if (dprice <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
			return tradesStr;
		}

		if (price.scale() > pricePrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
			return tradesStr;
		}

		BigDecimal btc_index_price = super.findBPIByCode(bqTransactionType.getType_code());
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单失败，无法获取指数");
			return tradesStr;
		}
		
		BigDecimal rateUp = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP);
		BigDecimal rateDown = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			NTransactionRate index_price_rate = nTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());
			rateDown = index_price_rate.getDownrate();
			rateUp = index_price_rate.getUprate();
			
			spyMemcachedClient.set(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
			spyMemcachedClient.set(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
		}
		
		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = nTradeLogService.readLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = nTradeLogService.readLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
		}
		
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
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
		
//		if (bqTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
//			}
//		}
		
		if (direction == Contents.DIRECTION_BUY && price.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price = maxIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		if (direction == Contents.DIRECTION_SELL && price.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price = minIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		NAccountContract bqContract = null;
		BigDecimal remainAmount = BigDecimal.ZERO;
		if (withLog) {
			bqContract = nAccountContractService.readByUidCode(partition, userId, bqTransaction.getTransaction_code());
			if (bqContract != null) {
				remainAmount = bqContract.getF_btc();
			}
		}
		
		if (!StringUtil.isEmpty(percentStr)) {
			BigDecimal percent = new BigDecimal(percentStr.replace("%", "")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			if (percent.compareTo(BigDecimal.ONE) > 0) percent = BigDecimal.ONE;
			if (percent.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "百分比参数错误");
				return tradesStr;
			}
			
			BigDecimal maxAmount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
			amount = maxAmount.multiply(percent).setScale(amountPrecision, BigDecimal.ROUND_DOWN);
		} else {
			double damount = ConvertUtil.toDouble(amountStr, 0);
			if (damount <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
				return tradesStr;
			}
			
			amount = new BigDecimal(amountStr);
			
			if (amount.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
				return tradesStr;
			}
		}
		
		if (amount.compareTo(bqTransactionType.getMin_kc_nums()) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量必须大于" + bqTransactionType.getMin_kc_nums().doubleValue());
			return tradesStr;
		}
		
		fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);

		String sip = "";
		if (withLog) {
			BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
					.divide(price.multiply(coefficient), 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
			if (amount.compareTo(maxAmount) > 0) {
				if (orderType == 1) {
					amount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
					amount = amount.setScale(amountPrecision, BigDecimal.ROUND_DOWN);
					fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
					deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					return tradesStr;
				}
			}
			
			sip = IpMacUtil.getIpAddr(request);
		
			if (!inWhiteList(String.valueOf(userId))) {
				String ret = maxUserKcCheck(bqTransaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(ret)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, ret);
					return tradesStr;
				}
				
				String risk = riskCheck( bqTransaction.getTransaction_code(), direction, price, amount, pricePrecision);
				if (!StringUtil.isEmpty(risk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, risk);
					return tradesStr;
				}
				
				String maxkc = nMaxKcCheck(partition, bqTransactionType, bqTransaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(maxkc)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, maxkc);
					return tradesStr;
				}
			}
			
			if (!inApiWhiteList(String.valueOf(userId)) && isApi) {
				BQUserApiSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING);
				if (setting == null) {
					setting = bqUserApiSettingService.readOne();
					spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);
				}
				if (setting != null && setting.getIs_open()) {
					try {
						Thread.sleep(setting.getDelay_time());
					} catch (InterruptedException e) {
						System.out.println("userId: " + userId + ", contract: " + bqTransaction.getTransaction_code());
						e.printStackTrace();
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
						return tradesStr;
					}
				}
			}
		}

		NPendingDelegationCache cache = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount, price, BigDecimal.ZERO, amount, Integer.valueOf(multiple), deposit,
				feeRate, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
		try {
			nPendingDelegationCacheService.insertCacheAndChangeAccount(partition, bqTransaction.getCurrency_type_id(), cache, fee, bqContract, withLog, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
			try {
				tickerJson.put("data", cache.getPending_no() + "," + amountStr + "," + priceStr);
				WebSocket.addNPendingToMemcached(partition, cache);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		} catch (ServiceException e) {
			System.out.println("userId: " + userId + ", " + partition + ": " + bqTransaction.getTransaction_code());
			e.printStackTrace();
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderPlaceOneD(HttpServletRequest request, boolean isApi, String orderTypeStr, String percentStr,
			String symbol, String amountStr, String priceStr, String operation, String multiple, long userId,
			JSONObject tickerJson, BQMemberLevel userlevel) {
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}
		
		DeliveryTransaction transaction = null;
		DeliveryTransactionType transactionType = null;
		List<DeliveryTransaction> list = super.findCurrDTrans();
		for (DeliveryTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				transaction = bq;
				break;
			}
		}
		if (transaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		transactionType = dTransactionTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (transaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(transaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}
		
		String[] ganggan = transaction.getMultiple().split(",");
		String[] ggzt = transaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}
		
		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		int canMultiple = getDOneCanMultiple(transaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(transaction.getTransaction_code(), "delivery");
		int amountPrecision = Utils.getAmountDigtis(transaction.getTransaction_code());
		
		// 开仓数量及价格验证
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			priceStr = getDMarketOrderPrice(transaction, direction);
			if (StringUtil.isEmpty(priceStr)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "当前无买一或卖一，不支持市价下单");
				return tradesStr;
			}
		}
		
		BigDecimal coefficient = transaction.getTrade_coefficient();
		BigDecimal multiple_client2 = new BigDecimal(multiple);
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal price = new BigDecimal(priceStr);
		
		if (orderType == 1) {
			price = price.setScale(pricePrecision + 1, BigDecimal.ROUND_HALF_UP).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
		}
		
		double dprice = ConvertUtil.toDouble(priceStr, 0);
		if (dprice <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
			return tradesStr;
		}

		if (price.scale() > pricePrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
			return tradesStr;
		}

		BigDecimal btc_index_price = super.findBPIByCode(transactionType.getType_code());
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单失败，无法获取指数");
			return tradesStr;
		}
		
		BigDecimal rateUp = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP);
		BigDecimal rateDown = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN);
		if (rateUp == null || rateDown == null 
				|| rateUp.compareTo(BigDecimal.ZERO) == 0 
				|| rateDown.compareTo(BigDecimal.ZERO) == 0) {
			DeliveryTransactionRate index_price_rate = dTransactionRateService.getByCode(transaction.getTransaction_code());
			rateDown = index_price_rate.getDownrate();
			rateUp = index_price_rate.getUprate();
			
			spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_UP, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateUp);
			spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_INDEX_PRICE_RATE_DOWN, BTCQMemecacheKeys.TIMES.ONE_HOUR, rateDown);
		}
		
		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
			if (preClose != null)
				btc_index_price = preClose;
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = dTradeLogService.readLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
		}
		
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(rateDown));
			minIndexPrice = minIndexPrice.setScale(pricePrecision, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(rateUp.add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN);
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
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(pricePrecision, RoundingMode.HALF_UP);;
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(pricePrecision, BigDecimal.ROUND_DOWN);
			}
		}
		
		if (direction == Contents.DIRECTION_BUY && price.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price = maxIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单买价必须小于" + maxIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		if (direction == Contents.DIRECTION_SELL && price.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price = minIndexPrice;
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单卖价必须大于" + minIndexPrice.doubleValue());
				return tradesStr;
			}
		}
		
		BigDecimal fee = BigDecimal.ZERO;
		if (userlevel == null) {
			BQMember user = bqMemberService.readUserById(userId);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "该用户已无效");
				return tradesStr;
			}
			userlevel = super.getLevelByUserBfx(user);
		}
		
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = dTransactionFeeRateService.readByCodeAndLevel(transaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		boolean withLog = true;
		if (feeRate.compareTo(BigDecimal.ZERO) == 0) {
			withLog = false;
		}
		
		DeliveryAccountContract bqContract = null;
		BigDecimal remainAmount = BigDecimal.ZERO;
		if (withLog) {
			bqContract = dAccountContractService.readByUidCode(userId, transaction.getTransaction_code());
			if (bqContract != null) {
				remainAmount = bqContract.getF_btc();
			}
		}
		
		if (!StringUtil.isEmpty(percentStr)) {
			BigDecimal percent = new BigDecimal(percentStr.replace("%", "")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			if (percent.compareTo(BigDecimal.ONE) > 0) percent = BigDecimal.ONE;
			if (percent.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "百分比参数错误");
				return tradesStr;
			}
			
			BigDecimal maxAmount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
			amount = maxAmount.multiply(percent).setScale(amountPrecision, BigDecimal.ROUND_DOWN);
		} else {
			double damount = ConvertUtil.toDouble(amountStr, 0);
			if (damount <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
				return tradesStr;
			}
			
			amount = new BigDecimal(amountStr);
			
			if (amount.scale() > amountPrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
				return tradesStr;
			}
		}
		
		if (amount.compareTo(transactionType.getMin_kc_nums()) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量必须大于" + transactionType.getMin_kc_nums().doubleValue());
			return tradesStr;
		}
		
		fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
		BigDecimal deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);
		
		String sip = "";
		if (withLog) {
			BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
					.divide(price.multiply(coefficient), 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
			if (amount.compareTo(maxAmount) > 0) {
				if (orderType == 1) {
					amount = BTCQMath.getMaxAmount(remainAmount, price, multiple_client2, coefficient, feeRate);
					amount = amount.setScale(amountPrecision, BigDecimal.ROUND_DOWN);
					fee = BTCQMath.getFee(amount, price, feeRate, coefficient);
					deposit = BTCQMath.getDeposit(amount, price, multiple_client2, coefficient);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					return tradesStr;
				}
			}
			
			sip = IpMacUtil.getIpAddr(request);
		
			if (!inWhiteList(String.valueOf(userId))) {
				String ret = deliveryMaxUserKcCheck(transaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(ret)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, ret);
					return tradesStr;
				}
				
				String risk = deliveryRiskCheck(transaction.getTransaction_code(), direction, price, amount, pricePrecision);
				if (!StringUtil.isEmpty(risk)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, risk);
					return tradesStr;
				}
				
				String maxkc = deliveryMaxKcCheck(transactionType, transaction.getTransaction_code(), direction, userId, amount);
				if (!StringUtil.isEmpty(maxkc)) {
					String tradesStr = createErrorCode(Contents.API_ERROR_RISK, maxkc);
					return tradesStr;
				}
			}
			
			if (!inApiWhiteList(String.valueOf(userId)) && isApi) {
				BQUserApiSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING);
				if (setting == null) {
					setting = bqUserApiSettingService.readOne();
					spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);
				}
				if (setting != null && setting.getIs_open()) {
					try {
						Thread.sleep(setting.getDelay_time());
					} catch (InterruptedException e) {
						System.out.println("userId: " + userId + ", contract: " + transaction.getTransaction_code());
						e.printStackTrace();
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
						return tradesStr;
					}
				}
			}
		}
		
		DeliveryPendingDelegationCache cache = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				direction, amount, price, BigDecimal.ZERO, amount, Integer.valueOf(multiple), deposit,
				feeRate, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
		try {
			dPendingDelegationCacheService.insertCacheAndChangeAccount(cache, fee, bqContract, withLog, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
			try {
				tickerJson.put("data", cache.getPending_no() + "");
				// websocket加入推送
				WebSocket.addDPendingToMemcached(cache);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		} catch (ServiceException e) {
			System.out.println("userId: " + userId + ", contract: " + transaction.getTransaction_code());
			e.printStackTrace();
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String spotOrderPlaceOne(HttpServletRequest request, boolean isApi, String coinPairName, String amount, String price, String operation, long userId,
			JSONObject tickerJson) {

		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(coinPairName);
		if (coinPair == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol错误");
			return tradesStr;
		}
		
		if (coinPair.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		BQSpotMarket market = bqSpotMarketService.readMarket(coinPair.getMarket_id());
		if (market == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "参数错误，symbol错误");
			return tradesStr;
		}
		
		if (!isApi) {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
			if (setting == null || setting.getF_user_safetype() == 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
				return tradesStr;
			}
		}
		
		int digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
		int amountDigits = coinPair.getDecimals();
		
		// 挂单数量及价格验证
		final BigDecimal ZERO = BigDecimal.ZERO;
		BigDecimal amount_client = ZERO;
		BigDecimal price_client = ZERO;
		try {
			double damount = ConvertUtil.toDouble(amount, 0);
			double dprice = ConvertUtil.toDouble(price, 0);
			if (damount <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "数量不正确");
				return tradesStr;
			} else if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "价格不正确");
				return tradesStr;
			}

			amount_client = new BigDecimal(damount).setScale(amountDigits + 1, BigDecimal.ROUND_HALF_UP).setScale(amountDigits,
					BigDecimal.ROUND_DOWN);
			price_client = new BigDecimal(dprice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits,
					BigDecimal.ROUND_DOWN);

		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "价格或数量不为数字");
			return tradesStr;
		}

		BigDecimal frozen = BigDecimal.ZERO;
		BigDecimal commissionRate = BigDecimal.ZERO;
		int remainCoinId = 0;

		if (amount_client.compareTo(coinPair.getMin_trade_nums()) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "最小挂单数量" + coinPair.getMin_trade_nums() + "个");
			return tradesStr;
		}

		if (direction == Contents.DIRECTION_BUY) {
			commissionRate = coinPair.getCoin_buy_rate();
			remainCoinId = coinPair.getMarket_coin_id();
			frozen = amount_client.multiply(price_client);
		} else {
			remainCoinId = coinPair.getCoin_id();
			commissionRate = coinPair.getCoin_sell_rate();
			frozen = amount_client;
		}
		
		BigDecimal remainAmount = BigDecimal.ZERO;
		BQAccount bqAccount = bqAccountService.readAccount(userId, remainCoinId);
		if (bqAccount != null) {
			remainAmount = bqAccount.getBtc();
		}

		if (frozen.compareTo(remainAmount) > 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "账户可用余额不足以完成本次交易");
			return tradesStr;
		}
		
		String sip = "";
		boolean needLog = true;
		boolean isQuantUser = checkIsSpotQuantUser(userId);
		if (isQuantUser) {
			needLog = false;
			commissionRate = BigDecimal.ZERO;
		} else {
			sip = IpMacUtil.getIpAddr(request);
		}
		
		if (userId == 6604) {
			commissionRate = BigDecimal.ZERO;
		}
		
		if (!isQuantUser && !inApiWhiteList(String.valueOf(userId)) && isApi) {
			BQUserApiSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING);
			if (setting == null) {
				setting = bqUserApiSettingService.readOne();
				spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);
			}
			if (setting != null && setting.getIs_open()) {
				try {
					Thread.sleep(setting.getDelay_time());
				} catch (InterruptedException e) {
					e.printStackTrace();
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
					return tradesStr;
				}
			}
		}

		SpotPendingDelegationCache cache = new SpotPendingDelegationCache(coinPair.getId(),
				coinPair.getCoin_pair_name(), direction, amount_client, price_client, BigDecimal.ZERO, amount_client,
				commissionRate, userId, BigDecimal.ZERO);
		
		try {
			spotPendingDelegationCacheService.insertCacheAndChangeAccount(cache, frozen, bqAccount, remainCoinId, needLog, isApi ? Contents.DEVICE_API : Contents.DEVICE_APP, sip);
			try {
				tickerJson.put("data", cache.getPending_no() + "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println("------------------------------------------------------------------------------>");
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}

	}
	
	private String orderNPlacePlan(String partition, String orderTypeStr, String symbol, String amountStr, String priceStr,
			String triPriceStr, String operation, String multiple, long userId, JSONObject tickerJson) {
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);
		BigDecimal price = BigDecimal.ZERO;
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, symbol);
		NTransactionType bqTransactionType = null;
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = nTransactionTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0 || multiple_client > 500) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!nNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
		if (setting == null || setting.getF_user_safetype() == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
			return tradesStr;
		}
		
		int canMultiple = getNOneCanMultiple(partition, bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), partition);
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}
		
		if (orderType == 0) {
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}
		
		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = nTransactionService.readTransByCode(partition, bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		NPlanPendingDelegation delegation = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType);
		
		NPlanPendingDelegation pd = nPlanPendingDelegationService.insert(partition, delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderPlacePlan(String orderTypeStr, String symbol, String amountStr, String priceStr,
			String triPriceStr, String operation, String multiple, long userId, JSONObject tickerJson) {
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);
		BigDecimal price = BigDecimal.ZERO;
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		BQTransaction bqTransaction = null;
		BQTransactionType bqTransactionType = null;
		List<BQTransaction> list = super.findCurrTrans();
		for (BQTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
		if (setting == null || setting.getF_user_safetype() == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
			return tradesStr;
		}
		
		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}
		
		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		int canMultiple = getOneCanMultiple(bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "contract");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}
		
		if (orderType == 0) {
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}
		
		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = bqTransactionService.readTransByCode(bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		PlanPendingDelegation delegation = new PlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType);
		
		PlanPendingDelegation pd = planPendingDelegationService.insert(delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderVPlacePlan(String orderTypeStr, String symbol, String amountStr, String priceStr,
			String triPriceStr, String operation, String multiple, long userId, JSONObject tickerJson) {
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);
		BigDecimal price = BigDecimal.ZERO;
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		VTransaction bqTransaction = null;
		VTransactionType bqTransactionType = null;
		List<VTransaction> list = super.findCurrVTrans();
		for (VTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = vTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}
		
		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		int canMultiple = getVOneCanMultiple(bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "vcontract");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}
		
		if (orderType == 0) {
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}
		
		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = bqTransactionService.readTransByCode(bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		VPlanPendingDelegation delegation = new VPlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType);
		
		VPlanPendingDelegation pd = vPlanPendingDelegationService.insert(delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}
	
	private String orderDPlacePlan(String orderTypeStr, String symbol, String amountStr, String priceStr,
			String triPriceStr, String operation, String multiple, long userId, JSONObject tickerJson) {
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);
		BigDecimal price = BigDecimal.ZERO;
		
		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		DeliveryTransaction bqTransaction = null;
		DeliveryTransactionType bqTransactionType = null;
		List<DeliveryTransaction> list = super.findCurrDTrans();
		for (DeliveryTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		bqTransactionType = dTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}
		
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
		if (setting == null || setting.getF_user_safetype() == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
			return tradesStr;
		}
		
		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}
		
		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}
		
		int canMultiple = getDOneCanMultiple(bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}
		
		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "delivery");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}
		
		if (orderType == 0) {
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}
		
		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = dTransactionService.readTransByCode(bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		DeliveryPlanPendingDelegation delegation = new DeliveryPlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType);
		
		DeliveryPlanPendingDelegation pd = dPlanPendingDelegationService.insert(delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}


	private String orderPlacePlanPC(String orderTypeStr, String symbol, String amountStr, String priceStr,
								  String triPriceStr, String operation, String multiple, long userId, long mid, JSONObject tickerJson) {
		if (StringUtil.isEmpty(priceStr)) priceStr = "0";
		
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal pricest  = new BigDecimal(priceStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);//触发价
		BigDecimal price = BigDecimal.ZERO;
		BQMember user = bqMemberService.findUserById(userId);
		if (null == user || user.getF_user_id() <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "会话超时，请重新登录！");
			return tradesStr;
		}

		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		BQTransaction bqTransaction = null;
		BQTransactionType bqTransactionType = null;
		List<BQTransaction> list = super.findCurrTrans();
		for (BQTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}

		bqTransactionType = bqTransactionTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}

		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}

		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}

		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}

		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
		if (setting == null || setting.getF_user_safetype() == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
			return tradesStr;
		}

		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}

		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}

		int canMultiple = getOneCanMultiple(bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}

		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), "contract");
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());

		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}

		if (orderType == 0) {	//限价委托
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		
		BQMemberTransaction bqt = bqMemberTransactionService.readById(mid);
		if (mid > 0) {
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "未找到该持仓！");
				return tradesStr;
			}

			if (!bqTransaction.getTransaction_code().equals(bqt.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "合约名称和该持仓不匹配！");
				return tradesStr;
			}
			
			if (bqt.getTrade_multiple().intValue() != multiple_client) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "杠杆和该持仓不匹配！");
				return tradesStr;
			}
	
			if (bqt.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "会话超时，请重新登录！");
				return tradesStr;
			}
	
			if (bqt.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "操作失败！");
				return tradesStr;
			}
	
			if (((direction == Contents.DIRECTION_BUY && bqt.getTrade_type_id() != Contents.DIRECTION_SELL)
					|| (direction == Contents.DIRECTION_SELL && bqt.getTrade_type_id() != Contents.DIRECTION_BUY))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "未找到该持仓！");
				return tradesStr;
			}
	
			if (bqt.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓数量必须大于0！");
				return tradesStr;
			}
			
			if (amount.compareTo(bqt.getHold_count()) > 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量不能大于持仓数量！");
				return tradesStr;
			}

			//有持仓时，判断爆仓价格
			BQMemberLevel userlevel = getLevelByUserBfx(user);
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}

			// 爆仓价
			BQAccountContract account = bqAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
			BigDecimal deposit1 = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain())
					.add(bqt.getF_min_frozenremain()).add(account.getF_btc());
			if (direction == Contents.DIRECTION_BUY) {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (pricest.compareTo(bcj) > 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.DOWN);
						pricest = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "价格不能高于爆仓价！" + bcj);
						return tradesStr;
					}
				}
			} else {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (pricest.compareTo(bcj) < 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.UP);
						pricest = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "价格不能低于爆仓价！" + bcj);
						return tradesStr;
					}
				}
			}
		}

		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = bqTransactionService.readTransByCode(bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}

		PlanPendingDelegation delegation = new PlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType, 1, mid, 0);

		PlanPendingDelegation pd = planPendingDelegationService.insert(delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}

	private String orderNPlacePlanPC(String partition, String orderTypeStr, String symbol, String amountStr, String priceStr,
									String triPriceStr, String operation, String multiple, long userId, long mid, JSONObject tickerJson) {
		if (StringUtil.isEmpty(priceStr)) priceStr = "0";
		
		BigDecimal amount = new BigDecimal(amountStr);
		BigDecimal pricest  = new BigDecimal(priceStr);
		BigDecimal triPrice = new BigDecimal(triPriceStr);//触发价
		BigDecimal price = BigDecimal.ZERO;
		BQMember user = bqMemberService.findUserById(userId);
		if (null == user || user.getF_user_id() <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "会话超时，请重新登录！");
			return tradesStr;
		}

		int direction = 0;
		if ("buy".equals(operation)) {
			direction = Contents.DIRECTION_BUY;
		} else if ("sell".equals(operation)) {
			direction = Contents.DIRECTION_SELL;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，operation错误");
			return tradesStr;
		}

		NTransaction bqTransaction = null;
		NTransactionType bqTransactionType = null;
		List<NTransaction> list = super.findNCurrTrans(partition);
		for (NTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(symbol)) {
				bqTransaction = bq;
				break;
			}
		}
		if (bqTransaction == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}

		bqTransactionType = nTransactionTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
			return tradesStr;
		}

		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}

		if (bqTransaction.getSuspend()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "交易暂停");
			return tradesStr;
		}

		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!nNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "非交易时间");
				return tradesStr;
			}
		}

		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(userId);
		if (setting == null || setting.getF_user_safetype() == 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_TRANSSUSPEND, "您尚未绑定手机或谷歌验证，请前往设置-安全中心绑定");
			return tradesStr;
		}

		String[] ganggan = bqTransaction.getMultiple().split(",");
		String[] ggzt = bqTransaction.getMultiple_state().split(",");
		boolean ggky = false;
		for (int i = 0; i < ganggan.length; i++) {
			if (multiple.equals(ganggan[i])) {
				if ("1".equals(ggzt[i])) {
					ggky = true;
					break;
				}
			}
		}

		if (!ggky) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的杆杠");
			return tradesStr;
		}

		int canMultiple = getNOneCanMultiple(partition, bqTransaction, userId);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "当前支持的杆杠倍数：" + canMultiple);
			return tradesStr;
		}

		int pricePrecision = getDigits(bqTransaction.getTransaction_code(), partition);
		int amountPrecision = Utils.getAmountDigtis(bqTransaction.getTransaction_code());

		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		double damount = ConvertUtil.toDouble(amountStr, 0);
		if (damount <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单数量错误！");
			return tradesStr;
		}
		if (amount.scale() > amountPrecision) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单数量精度错误，精度位数为：" + amountPrecision);
			return tradesStr;
		}

		if (orderType == 0) {	//限价委托
			double dprice = ConvertUtil.toDouble(priceStr, 0);
			if (dprice <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "下单价格错误！");
				return tradesStr;
			}
			price = new BigDecimal(priceStr);
			if (price.scale() > pricePrecision) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PRECISION, "下单价格精度错误，精度位数为：" + pricePrecision);
				return tradesStr;
			}
		}

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		
		NMemberTransaction bqt = nMemberTransactionService.readById(partition, mid);
		
		if (mid > 0) {	
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "未找到该持仓！");
				return tradesStr;
			}
	
			if (!bqTransaction.getTransaction_code().equals(bqt.getTransaction_code())) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "合约名称和该持仓不匹配！");
				return tradesStr;
			}
			
			if (bqt.getTrade_multiple().intValue() != multiple_client) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "杠杆和该持仓不匹配！");
				return tradesStr;
			}
	
			if (bqt.getF_user_id() != userId) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "会话超时，请重新登录！");
				return tradesStr;
			}
	
			if (bqt.getFlag() != 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "操作失败！");
				return tradesStr;
			}
	
			if (((direction == Contents.DIRECTION_BUY && bqt.getTrade_type_id() != Contents.DIRECTION_SELL)
					|| (direction == Contents.DIRECTION_SELL && bqt.getTrade_type_id() != Contents.DIRECTION_BUY))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "未找到该持仓！");
				return tradesStr;
			}
	
			if (bqt.getHold_count().signum() <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "持仓数量必须大于0！");
				return tradesStr;
			}
	
			if (amount.compareTo(bqt.getHold_count()) > 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "平仓数量不能大于持仓数量！");
				return tradesStr;
			}

			//有持仓时，判断爆仓价格
			BQMemberLevel userlevel = getLevelByUserBfx(user);
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			NTransactionFeeRate transactionFeeRate = nTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}

			// 爆仓价
			NAccountContract account = nAccountContractService.readBTCByUidCode(partition, bqt.getF_user_id(), bqt.getTransaction_code());
			BigDecimal deposit1 = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain())
					.add(bqt.getF_min_frozenremain()).add(account.getF_btc());
			if (direction == Contents.DIRECTION_BUY) {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (pricest.compareTo(bcj) > 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.DOWN);
						pricest = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "价格不能高于爆仓价！" + bcj);
						return tradesStr;
					}
				}
			} else {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (pricest.compareTo(bcj) < 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.UP);
						pricest = bcj;
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "价格不能低于爆仓价！" + bcj);
						return tradesStr;
					}
				}
			}
		}

		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = nTransactionService.readTransByCode(partition, bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}

		NPlanPendingDelegation delegation = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), userId,
				triPrice, price, amount, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-初始", orderType, 1, mid, 0);

		NPlanPendingDelegation pd = nPlanPendingDelegationService.insert(partition, delegation);
		if (pd.getId() > 0) {
			return "";
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "下单出错，请稍后再试");
			return tradesStr;
		}
	}

}
