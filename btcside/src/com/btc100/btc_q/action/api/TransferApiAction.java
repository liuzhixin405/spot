package com.btc100.btc_q.action.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.entity.BQSubMemberTransferLog;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.ContractAccountLog;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NFundingFeeRecord;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQSubMemberService;
import com.btc100.btc_q.service.BQSubMemberTransferLogService;
import com.btc100.btc_q.service.ContractAccountLogService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryContractAccountLogService;
import com.btc100.btc_q.service.DeliveryTransferAccountService;
import com.btc100.btc_q.service.FundingFeeRecordService;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NContractAccountLogService;
import com.btc100.btc_q.service.NFundingFeeRecordService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NTransferAccountService;
import com.btc100.btc_q.service.TransferAccountService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VContractAccountLogService;
import com.btc100.btc_q.service.VTransferAccountService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TransferVO;
import com.google.gson.Gson;

/**
 * 转入转出API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/transferApi")
public class TransferApiAction extends BaseApiAction {

	@Autowired
	private ContractAccountLogService contractAccountLogService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private TransferAccountService transferAccountService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VContractAccountLogService vContractAccountLogService;
	@Autowired
	private VTransferAccountService vTransferAccountService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private DeliveryContractAccountLogService dContractAccountLogService;
	@Autowired
	private DeliveryTransferAccountService dTransferAccountService;
	@Autowired
	private FundingFeeRecordService fundingFeeRecordService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQSubMemberService bqSubMemberService;
	@Autowired
	private BQSubMemberTransferLogService bqTransferLogService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private NContractAccountLogService nContractAccountLogService;
	@Autowired
	private NAccountContractService nAccountContractService;
	@Autowired
	private NTransferAccountService nTransferAccountService;
	@Autowired
	private NNotradetimeService nNotradetimeService;
	@Autowired
	private NFundingFeeRecordService nFundingFeeRecordService;
	
	/**
	 * 多条转入转出记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/records")
	public String getRecords(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		
		String side = request.getParameter("side");
		int operation = -1;
		if ("all".equals(side)) {
			operation = 0;
		} else if ("in".equals(side)) {
			operation = Contents.TRANS_TYPE_CZ;
		} else if ("out".equals(side)) {
			operation = Contents.TRANS_TYPE_TX;
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知side");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("side", side);
		
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
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (size > 100) size = 100;
		
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
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			List<VTransaction> transactionList = super.findCurrVTrans();
			VTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (VTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<VContractAccountLog> logs = vContractAccountLogService.readByUserIdAndTime(symbol, operation, userId, nFrom, direct, start, end, size);
			for (VContractAccountLog log : logs) {
				TransferVO vo = new TransferVO();
				vo.setRecordId(log.getLog_id());
				vo.setAmount(log.getChange_amount().doubleValue());
				vo.setRecordTime(log.getDateline());
				vo.setOperation(log.getOperation());
				vo.setSymbol(log.getF_code());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryTransaction> transactionList = super.findCurrDTrans();
			DeliveryTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (DeliveryTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<DeliveryContractAccountLog> logs = dContractAccountLogService.readByUserIdAndTime(symbol, operation, userId, nFrom, direct, start, end, size);
			for (DeliveryContractAccountLog log : logs) {
				TransferVO vo = new TransferVO();
				vo.setRecordId(log.getLog_id());
				vo.setAmount(log.getChange_amount().doubleValue());
				vo.setRecordTime(log.getDateline());
				vo.setOperation(log.getOperation());
				vo.setSymbol(log.getF_code());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> transactionList = super.findCurrTrans();
			BQTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (BQTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			List<ContractAccountLog> logs = contractAccountLogService.readByUserIdAndTime(symbol, operation, userId, nFrom, direct, start, end, size);
			for (ContractAccountLog log : logs) {
				TransferVO vo = new TransferVO();
				vo.setRecordId(log.getLog_id());
				vo.setAmount(log.getChange_amount().doubleValue());
				vo.setRecordTime(log.getDateline());
				vo.setOperation(log.getOperation());
				vo.setSymbol(log.getF_code());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = super.findNCurrTrans(type);
				NTransaction bqt = null;
				if (!StringUtil.isEmpty(symbol)) {
					for (NTransaction bqTransaction : transactionList) {
						if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
							bqt = bqTransaction;
							break;
						}
					}
				}
				
				if (bqt == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NContractAccountLog> logs = nContractAccountLogService.readByUserIdAndTime(type, symbol, operation, userId, nFrom, direct, start, end, size);
				for (NContractAccountLog log : logs) {
					TransferVO vo = new TransferVO();
					vo.setRecordId(log.getLog_id());
					vo.setAmount(log.getChange_amount().doubleValue());
					vo.setRecordTime(log.getDateline());
					vo.setOperation(log.getOperation());
					vo.setSymbol(log.getF_code());
					
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
		
	/**
	 * 单条转入转出记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/record")
	public String getRecord(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		if (symbol == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有symbol");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);
		
		String recordId = request.getParameter("recordId");
		if (recordId == null || Convert.toLong(recordId, 0) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，recordID错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("recordId", recordId);
		
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
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			List<VTransaction> transactionList = super.findCurrVTrans();
			VTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (VTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VContractAccountLog log = vContractAccountLogService.readByLogIdAndUserId(symbol, Convert.toLong(recordId, 0), userId);
			if (log == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "未找到记录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			TransferVO vo = new TransferVO();
			vo.setRecordId(log.getLog_id());
			vo.setAmount(log.getChange_amount().doubleValue());
			vo.setRecordTime(log.getDateline());
			vo.setOperation(log.getOperation());
			vo.setSymbol(log.getF_code());
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(vo)));
		} else if ("delivery".equals(type)) {
			List<DeliveryTransaction> transactionList = super.findCurrDTrans();
			DeliveryTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (DeliveryTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryContractAccountLog log = dContractAccountLogService.readByLogIdAndUserId(symbol, Convert.toLong(recordId, 0), userId);
			if (log == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "未找到记录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			TransferVO vo = new TransferVO();
			vo.setRecordId(log.getLog_id());
			vo.setAmount(log.getChange_amount().doubleValue());
			vo.setRecordTime(log.getDateline());
			vo.setOperation(log.getOperation());
			vo.setSymbol(log.getF_code());
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(vo)));
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> transactionList = super.findCurrTrans();
			BQTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (BQTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			ContractAccountLog log = contractAccountLogService.readByLogIdAndUserId(symbol, Convert.toLong(recordId, 0), userId);
			if (log == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "未找到记录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			TransferVO vo = new TransferVO();
			vo.setRecordId(log.getLog_id());
			vo.setAmount(log.getChange_amount().doubleValue());
			vo.setRecordTime(log.getDateline());
			vo.setOperation(log.getOperation());
			vo.setSymbol(log.getF_code());
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(vo)));
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = super.findNCurrTrans(type);
				NTransaction bqt = null;
				if (!StringUtil.isEmpty(symbol)) {
					for (NTransaction bqTransaction : transactionList) {
						if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
							bqt = bqTransaction;
							break;
						}
					}
				}
				
				if (bqt == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NContractAccountLog log = nContractAccountLogService.readByLogIdAndUserId(type, symbol, Convert.toLong(recordId, 0), userId);
				if (log == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "未找到记录");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				TransferVO vo = new TransferVO();
				vo.setRecordId(log.getLog_id());
				vo.setAmount(log.getChange_amount().doubleValue());
				vo.setRecordTime(log.getDateline());
				vo.setOperation(log.getOperation());
				vo.setSymbol(log.getF_code());
				
				Gson gson = new Gson();
				tickerJson.put("data", new JSONObject(gson.toJson(vo)));
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
	 * 转入转出下单
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public String doTransfer(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		String amountStr = request.getParameter("amount");
		if (StringUtil.isEmpty(symbol) || StringUtil.isEmpty(amountStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("symbol", symbol);
		map.put("amount", amountStr);
		
		String directionStr = request.getParameter("operation");
		if (!"in".equals(directionStr) && !"out".equals(directionStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，转账方向错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		int direction = Contents.TRANS_TYPE_CZ;
		if ("out".equals(directionStr)) {
			direction = Contents.TRANS_TYPE_TX;
		}
		
		map.put("operation", directionStr);
		
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
		
		BigDecimal amount = new BigDecimal(amountStr).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，amount错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if ("spot".equals(type)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else if ("vcontract".equals(type)) {
			List<VTransaction> transactionList = super.findCurrVTrans();
			VTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (VTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
	
			if (direction == Contents.TRANS_TYPE_CZ && !bqt.getTransfer_in_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (direction == Contents.TRANS_TYPE_TX && !bqt.getTransfer_out_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
	
			if (direction == Contents.TRANS_TYPE_CZ) {	// 转入
				BQAccount account = bqAccountService.readAccount(userId, bqt.getCurrency_type_id());
				if (amount.compareTo(account.getBtc()) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				// 创建账户如果不存在
				VAccountContract accountContract = vAccountContractService.readWithNullByUidCode(userId, symbol);
				if (accountContract == null) {
					String lock = spyMemcachedClient.lock(symbol + BTCQMemecacheKeys.V_ACCOUNT_CONTRACT + userId);
					if (!StringUtil.isEmpty(lock)) {
						try {
							vAccountContractService.createByUidCode(userId, symbol);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							spyMemcachedClient.unlock(symbol + BTCQMemecacheKeys.V_ACCOUNT_CONTRACT + userId, lock);
						}
					}
				}
		
				long logId = 0;
				try {
					String transNo = vTransferAccountService.updateAccountForZC(userId, bqt.getCurrency_type_id(), symbol, amount.negate());
					if (!StringUtil.isEmpty(transNo)) {
						VContractAccountLog log = vContractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {	// 转出
				VAccountContract accountContract = vAccountContractService.readByUidCode(userId, symbol);
				BigDecimal minbtc = accountContract.getF_btc();
				if (amount.compareTo(minbtc) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				long logId = 0;
				try {
					String transNo = vTransferAccountService.updateAccountForZC(userId, bqt.getCurrency_type_id(), symbol, amount);
					if (!StringUtil.isEmpty(transNo)) {
						VContractAccountLog log = vContractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else if("delivery".equals(type)) {
			List<DeliveryTransaction> transactionList = super.findCurrDTrans();
			DeliveryTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (DeliveryTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
	
			if (direction == Contents.TRANS_TYPE_CZ && !bqt.getTransfer_in_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (direction == Contents.TRANS_TYPE_TX && !bqt.getTransfer_out_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
	
			if (direction == Contents.TRANS_TYPE_CZ) {	// 转入
				BQAccount account = bqAccountService.readAccount(userId, Contents.TRADE_CURRENCY_TYPE_ID);
				if (amount.compareTo(account.getBtc()) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				// 创建账户如果不存在
				DeliveryAccountContract accountContract = dAccountContractService.readWithNullByUidCode(userId, symbol);
				if (accountContract == null) {
					String lock = spyMemcachedClient.lock(symbol + BTCQMemecacheKeys.D_ACCOUNT_CONTRACT + userId);
					if (!StringUtil.isEmpty(lock)) {
						try {
							dAccountContractService.createByUidCode(userId, symbol);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							spyMemcachedClient.unlock(symbol + BTCQMemecacheKeys.D_ACCOUNT_CONTRACT + userId, lock);
						}
					}
				}
		
				long logId = 0;
				try {
					String transNo = dTransferAccountService.updateAccountForZC(userId, symbol, amount.negate());
					if (!StringUtil.isEmpty(transNo)) {
						DeliveryContractAccountLog log = dContractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {	// 转出
				DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(userId, symbol);
				BigDecimal minbtc = accountContract.getF_btc();
				if (amount.compareTo(minbtc) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				long logId = 0;
				try {
					String transNo = dTransferAccountService.updateAccountForZC(userId, symbol, amount);
					if (!StringUtil.isEmpty(transNo)) {
						DeliveryContractAccountLog log = dContractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> transactionList = super.findCurrTrans();
			BQTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (BQTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
	
			if (direction == Contents.TRANS_TYPE_CZ && !bqt.getTransfer_in_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (direction == Contents.TRANS_TYPE_TX && !bqt.getTransfer_out_enable()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出功能暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (direction == Contents.TRANS_TYPE_CZ) {	// 转入
				BQAccount account = bqAccountService.readAccount(userId, Contents.TRADE_CURRENCY_TYPE_ID);
				if (amount.compareTo(account.getBtc()) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				// 创建账户如果不存在
				BQAccountContract accountContract = bqAccountContractService.readWithNullByUidCode(userId, symbol);
				if (accountContract == null) {
					String lock = spyMemcachedClient.lock(symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId);
					if (!StringUtil.isEmpty(lock)) {
						try {
							bqAccountContractService.createByUidCode(userId, symbol);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							spyMemcachedClient.unlock(symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId, lock);
						}
					}
				}
		
				long logId = 0;
				try {
					String transNo = transferAccountService.updateAccountForZC(userId, symbol, amount.negate());
					if (!StringUtil.isEmpty(transNo)) {
						ContractAccountLog log = contractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			} else {	// 转出
				BQAccountContract accountContract = bqAccountContractService.readByUidCode(userId, symbol);
				BigDecimal minbtc = accountContract.getF_btc();
				if (amount.compareTo(minbtc) > 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) { // 非交易时间暂停转出
					if (!bqNotradetimeService.isTradeTime(bqt.getTransaction_id())) {
						List<BQMemberTransaction> transList = bqMemberTransactionService
								.readTransactionByUserId(bqt.getTransaction_code(), userId);
						if (transList.size() > 0) {
							BigDecimal deposit = BigDecimal.ZERO;
							for (BQMemberTransaction trans : transList) {
								deposit = deposit.add(trans.getDeposit()).subtract(trans.getDeposit2())
										.add(trans.getF_fromremain()).add(trans.getF_min_frozenremain())
										.add(trans.getF_frozenremain());
							}
							// 最多转出 = 合约可用 - 保证金*2
							BigDecimal maxTransferOut = minbtc.subtract(deposit.multiply(new BigDecimal(2)));
							if (amount.compareTo(maxTransferOut) > 0) {
								String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE,
										"非交易时间，可转出的金额为：" + maxTransferOut.doubleValue());
								model.addAttribute("ticker", tradesStr);
								return apiData(response);
							}
						}
					}
				}
				
				long logId = 0;
				try {
					String transNo = transferAccountService.updateAccountForZC(userId, symbol, amount);
					if (!StringUtil.isEmpty(transNo)) {
						ContractAccountLog log = contractAccountLogService.readByTransNoAndUserId(symbol, transNo, userId);
						if (log != null) {
							logId = log.getLog_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (logId > 0) {
					tickerJson.put("data", logId);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出失败");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = super.findNCurrTrans(type);
				NTransaction bqt = null;
				if (!StringUtil.isEmpty(symbol)) {
					for (NTransaction bqTransaction : transactionList) {
						if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
							bqt = bqTransaction;
							break;
						}
					}
				}
				
				if (bqt == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
		
				if (direction == Contents.TRANS_TYPE_CZ && !bqt.getTransfer_in_enable()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入功能暂停");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (direction == Contents.TRANS_TYPE_TX && !bqt.getTransfer_out_enable()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出功能暂停");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (direction == Contents.TRANS_TYPE_CZ) {	// 转入
					BQAccount account = bqAccountService.readAccount(userId, part.getCurrency_type_id());
					if (amount.compareTo(account.getBtc()) > 0) {
						String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					// 创建账户如果不存在
					NAccountContract accountContract = nAccountContractService.readWithNullByUidCode(type, userId, symbol);
					if (accountContract == null) {
						String lock = spyMemcachedClient.lock(type + "_" + symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId);
						if (!StringUtil.isEmpty(lock)) {
							try {
								nAccountContractService.createByUidCode(type, userId, symbol);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								spyMemcachedClient.unlock(type + "_" + symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId, lock);
							}
						}
					}
			
					long logId = 0;
					try {
						String transNo = nTransferAccountService.updateAccountForZC(type, userId, bqt.getCurrency_type_id(), symbol, amount.negate());
						if (!StringUtil.isEmpty(transNo)) {
							NContractAccountLog log = nContractAccountLogService.readByTransNoAndUserId(type, symbol, transNo, userId);
							if (log != null) {
								logId = log.getLog_id();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (logId > 0) {
						tickerJson.put("data", logId);
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转入失败");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
				} else {	// 转出
					NAccountContract accountContract = nAccountContractService.readByUidCode(type, userId, symbol);
					BigDecimal minbtc = accountContract.getF_btc();
					if (amount.compareTo(minbtc) > 0) {
						String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) { // 非交易时间暂停转出
						if (!nNotradetimeService.isTradeTime(type, bqt.getTransaction_id())) {
							List<NMemberTransaction> transList = nMemberTransactionService
									.readTransactionByUserId(type, bqt.getTransaction_code(), userId);
							if (transList.size() > 0) {
								BigDecimal deposit = BigDecimal.ZERO;
								for (NMemberTransaction trans : transList) {
									deposit = deposit.add(trans.getDeposit()).subtract(trans.getDeposit2())
											.add(trans.getF_fromremain()).add(trans.getF_min_frozenremain())
											.add(trans.getF_frozenremain());
								}
								// 最多转出 = 合约可用 - 保证金*2
								BigDecimal maxTransferOut = minbtc.subtract(deposit.multiply(new BigDecimal(2)));
								if (amount.compareTo(maxTransferOut) > 0) {
									String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE,
											"非交易时间，可转出的金额为：" + maxTransferOut.doubleValue());
									model.addAttribute("ticker", tradesStr);
									return apiData(response);
								}
							}
						}
					}
					
					long logId = 0;
					try {
						String transNo = nTransferAccountService.updateAccountForZC(type, userId, bqt.getCurrency_type_id(), symbol, amount);
						if (!StringUtil.isEmpty(transNo)) {
							NContractAccountLog log = nContractAccountLogService.readByTransNoAndUserId(type, symbol, transNo, userId);
							if (log != null) {
								logId = log.getLog_id();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (logId > 0) {
						tickerJson.put("data", logId);
					} else {
						String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转出失败");
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
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 资金费用明细
	 * 
	 */
	@RequestMapping("/fundingFeeRecord")
	public String fundingFeeRecord(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "success");
		
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
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("symbol", symbol);
		
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
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		
		com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
		if (type == null || type.equals("") || type.equalsIgnoreCase("contract")) {
			List<BQTransaction> transactionList = super.findCurrTrans();
			BQTransaction bqt = null;
			if (!StringUtil.isEmpty(symbol)) {
				for (BQTransaction bqTransaction : transactionList) {
					if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
						bqt = bqTransaction;
						break;
					}
				}
			}
			
			if (bqt == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			
			List<FundingFeeRecord> list = fundingFeeRecordService.readByUserAndTime(bqt.getTransaction_code(), user.getF_user_id(), nFrom, direct, start, end, size);
			for (FundingFeeRecord record : list) {
				JSONObject obj = new JSONObject();
				obj.put("recordId", record.getId());
				obj.put("symbol", record.getF_code());
				obj.put("typeId", record.getTrans_type_id());
				obj.put("holdCount", record.getHold_count());
				obj.put("currPrice", record.getCurr_price());
				obj.put("positionValue", record.getPosition_value());
				obj.put("feeRate", record.getFee_rate());
				obj.put("fundingFee", record.getFunding_fee());
				obj.put("recordTime", record.getDateline());
				array.add(obj);
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = super.findNCurrTrans(type);
				NTransaction bqt = null;
				if (!StringUtil.isEmpty(symbol)) {
					for (NTransaction bqTransaction : transactionList) {
						if (bqTransaction.getTransaction_code().equalsIgnoreCase(symbol)) {
							bqt = bqTransaction;
							break;
						}
					}
				}
				
				if (bqt == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NFundingFeeRecord> list = nFundingFeeRecordService.readByUserAndTime(part.getPart(), bqt.getTransaction_code(), user.getF_user_id(), nFrom, direct, start, end, size);
				for (NFundingFeeRecord record : list) {
					JSONObject obj = new JSONObject();
					obj.put("recordId", record.getId());
					obj.put("symbol", record.getF_code());
					obj.put("typeId", record.getTrans_type_id());
					obj.put("holdCount", record.getHold_count());
					obj.put("currPrice", record.getCurr_price());
					obj.put("positionValue", record.getPosition_value());
					obj.put("feeRate", record.getFee_rate());
					obj.put("fundingFee", record.getFunding_fee());
					obj.put("recordTime", record.getDateline());
					array.add(obj);
				}
			}
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	
	/**
	 * 母子账户转账
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/subUserTransfer", method = RequestMethod.POST)
	public String subUserTransfer(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		
		String subUserName = request.getParameter("subUserName"); // 子账号登录名
		String symbol = request.getParameter("symbol"); // 币种名称（小写字母）
		String amountStr = request.getParameter("amount");
		if (StringUtil.isEmpty(subUserName) || StringUtil.isEmpty(symbol) || StringUtil.isEmpty(amountStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("subUserName", subUserName);
		map.put("symbol", symbol);
		map.put("amount", amountStr);
		
		String directionStr = request.getParameter("operation");
		if (!"in".equals(directionStr) && !"out".equals(directionStr)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，转账方向错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		int direction = Contents.SUB_TRANSFER_IN;
		if ("out".equals(directionStr)) {
			direction = Contents.SUB_TRANSFER_OUT;
		}
		
		map.put("operation", directionStr);
		
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
		
		BQMember subUser = bqMemberService.readSubUser(subUserName);
		if (subUser == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，subUserName错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(userId, subUser.getF_user_id());
		if (bqSubMember == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，subUserName错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQMember motherUser = bqMemberService.readUserById(userId);
		
		BigDecimal amount = new BigDecimal(amountStr).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，amount错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		symbol = symbol.toUpperCase();
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeName(symbol);
		if (currency == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (currency.getType_name().toUpperCase().equals("BFC")) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQSubMemberTransferLog log = new BQSubMemberTransferLog();
		log.setAmount(amount);
		log.setCurrency_type_id(currency.getType_id());
		log.setCurrency_name(currency.getType_name());
		log.setDateline(System.currentTimeMillis() / 1000);
		log.setMother_user_id(motherUser.getF_user_id());
		log.setMother_user_email(motherUser.getF_user_email());
		log.setSub_user_id(subUser.getF_user_id());
		log.setSub_user_email(subUser.getF_user_email());
		log.setOperation(direction);
		String remark = "转入子账号";
		if (direction == Contents.SUB_TRANSFER_OUT) {
			remark = "子账号转出";
		}
		log.setRemark(remark);
		try {
			boolean success = bqTransferLogService.updateAccountForTransfer(log);
			if (!success) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "转账失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		tickerJson.put("data", log.getId());
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 多条母子账户转账记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/subUserTransferRecords")
	public String subUserTransferRecords(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		if (!StringUtil.isEmpty(symbol)) {
			map.put("symbol", symbol);
		}
		
		
		String side = request.getParameter("side");
		int operation = -1;
		if ("all".equals(side)) {
			operation = 0;
		} else if ("in".equals(side)) {
			operation = Contents.TRANS_TYPE_CZ;
		} else if ("out".equals(side)) {
			operation = Contents.TRANS_TYPE_TX;
		}
		
		if (!StringUtil.isEmpty(side)) {
			map.put("side", side);
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
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (size > 100) size = 100;
		
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
		
		List<BQSubMemberTransferLog> logs = bqTransferLogService.readByUserId(userId, symbol, operation, nFrom, direct, start, end, size);
		
		for (BQSubMemberTransferLog log : logs) {
			TransferVO vo = new TransferVO();
			vo.setRecordId(log.getId());
			vo.setOperation(log.getOperation());
			vo.setSubUser(log.getSub_user_email());
			vo.setAmount(log.getAmount().doubleValue());
			vo.setRecordTime(log.getDateline());
			vo.setSymbol(log.getCurrency_name());
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(vo)));
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
}