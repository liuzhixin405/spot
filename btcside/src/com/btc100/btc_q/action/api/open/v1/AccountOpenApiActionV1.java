package com.btc100.btc_q.action.api.open.v1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.action.api.BaseApiAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.ContractAccountLog;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VContractAccountLog;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.ContractAccountLogService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryContractAccountLogService;
import com.btc100.btc_q.service.DeliveryTransferAccountService;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NContractAccountLogService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NTransferAccountService;
import com.btc100.btc_q.service.TransferAccountService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VContractAccountLogService;
import com.btc100.btc_q.service.VTransferAccountService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TransferVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/v1/account")
public class AccountOpenApiActionV1 extends BaseApiAction  {
	
	Logger logger = Logger.getLogger(AccountOpenApiActionV1.class);
	
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private TransferAccountService transferAccountService;
	@Autowired
	private VContractAccountLogService vContractAccountLogService;
	@Autowired
	private ContractAccountLogService contractAccountLogService;
	@Autowired
	private VTransferAccountService vTransferAccountService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private DeliveryContractAccountLogService dContractAccountLogService;
	@Autowired
	private DeliveryTransferAccountService dTransferAccountService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NAccountContractService nAccountContractService;
	@Autowired
	private NTransferAccountService nTransferAccountService;
	@Autowired
	private NContractAccountLogService nContractAccountLogService;
	@Autowired
	private NNotradetimeService nNotradetimeService;
	@Autowired
	private NMemberTransactionService nMemberTransactionService;
	
	/**
	 * 所有合约、币币资产查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/accounts")
	public String getAccounts(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONArray array = new JSONArray();
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
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		if ("spot".equals(type)) {
			List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
			List<BQAccount> accounts = bqAccountService.readAllByUserId(userId);
			for (BQAccount account : accounts) {
				JSONObject json = new JSONObject();
				boolean contain = false;
				for (BQCurrencyType currency : currencyTypes) {
					if (currency.getType_id() == account.getCurrency_type_id()) {
						contain = true;
						json.put("symbol", currency.getType_name().toLowerCase());
						break;
					}
				}
				
				if (!contain) {
					continue;
				}
				
				json.put("available", account.getBtc().doubleValue());
				json.put("frozen", account.getBtc_locked().doubleValue());
				array.put(json);
			}
			
		} else if ("vcontract".equals(type)) {
			List<VAccountContract> accounts = vAccountContractService.readAllByUid(userId);
			for (VAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryAccountContract> accounts = dAccountContractService.readAllByUid(userId);
			for (DeliveryAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQAccountContract> accounts = bqAccountContractService.readAllByUid(userId);
			for (BQAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else {
			
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NAccountContract> accounts = nAccountContractService.readAllByUid(part.getPart(), userId);
				for (NAccountContract account : accounts) {
					JSONObject json = new JSONObject();
					json.put("symbol", account.getF_code());
					json.put("available", account.getF_btc().doubleValue());
					json.put("frozen", account.getF_lockbtc().doubleValue());
					array.put(json);
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
	 * 单个合约、币币资产查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/account")
	public String getAccount(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
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
		if (!StringUtil.isEmpty(symbol)) {
			map.put("symbol", symbol);
		}
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		JSONObject data = new JSONObject();
		long userId = Convert.toLong(map.get("userId"), 0);
		if ("spot".equals(type)) {
			List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
			BQCurrencyType currency = null;
			for (BQCurrencyType curr : currencyTypes) {
				if (curr.getType_name().equalsIgnoreCase(symbol)) {
					currency = curr;
					break;
				}
			}
			
			if (currency == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "不支持的symbol");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQAccount account = bqAccountService.readAccount(userId, currency.getType_id());
			data.put("symbol", currency.getType_name().toLowerCase());
			data.put("available", account.getBtc().doubleValue());
			data.put("frozen", account.getBtc_locked().doubleValue());
			
		} else if ("vcontract".equals(type)) {
			VTransaction currTrans = null;
			List<VTransaction> transactionList = super.findCurrVTrans();
			for (VTransaction bq : transactionList) {
				if (bq.getTransaction_code().equals(symbol)) {
					currTrans = bq;
					break;
				}
			}
			
			if (currTrans == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "不支持的symbol");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			VAccountContract account = vAccountContractService.readByUidCode(userId, symbol);
			data.put("symbol", account.getF_code());
			data.put("available", account.getF_btc().doubleValue());
			data.put("frozen", account.getF_lockbtc().doubleValue());
		} else if ("delivery".equals(type)) {
			DeliveryTransaction currTrans = null;
			List<DeliveryTransaction> transactionList = super.findCurrDTrans();
			for (DeliveryTransaction bq : transactionList) {
				if (bq.getTransaction_code().equals(symbol)) {
					currTrans = bq;
					break;
				}
			}
			
			if (currTrans == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "不支持的symbol");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			DeliveryAccountContract account = dAccountContractService.readByUidCode(userId, symbol);
			data.put("symbol", account.getF_code());
			data.put("available", account.getF_btc().doubleValue());
			data.put("frozen", account.getF_lockbtc().doubleValue());
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQTransaction currTrans = null;
			List<BQTransaction> transactionList = findCurrTrans();
			for (BQTransaction bq : transactionList) {
				if (bq.getTransaction_code().equals(symbol)) {
					currTrans = bq;
					break;
				}
			}
			
			if (currTrans == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "不支持的symbol");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQAccountContract account = bqAccountContractService.readByUidCode(userId, symbol);
			data.put("symbol", account.getF_code());
			data.put("available", account.getF_btc().doubleValue());
			data.put("frozen", account.getF_lockbtc().doubleValue());
		} else {
			
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NTransaction currTrans = findNCurrTrans(part.getPart(), symbol);
				if (currTrans == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "不支持的symbol");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				NAccountContract account = nAccountContractService.readByUidCode(part.getPart(), userId, symbol);
				data.put("symbol", account.getF_code());
				data.put("available", account.getF_btc().doubleValue());
				data.put("frozen", account.getF_lockbtc().doubleValue());
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		}
		
		tickerJson.put("data", data);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	/**
	 * 资金划转（币币-合约之间）
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
				NTransaction bqt = findNCurrTrans(part.getPart(), symbol);
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
					NAccountContract accountContract = nAccountContractService.readWithNullByUidCode(part.getPart(), userId, symbol);
					if (accountContract == null) {
						String lock = spyMemcachedClient.lock(part.getPart() + "_" + symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId);
						if (!StringUtil.isEmpty(lock)) {
							try {
								nAccountContractService.createByUidCode(part.getPart(), userId, symbol);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								spyMemcachedClient.unlock(part.getPart() + "_" + symbol + BTCQMemecacheKeys.ACCOUNT_CONTRACT + userId, lock);
							}
						}
					}
			
					long logId = 0;
					try {
						String transNo = nTransferAccountService.updateAccountForZC(part.getPart(), userId, bqt.getCurrency_type_id(), symbol, amount.negate());
						if (!StringUtil.isEmpty(transNo)) {
							NContractAccountLog log = nContractAccountLogService.readByTransNoAndUserId(part.getPart(), symbol, transNo, userId);
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
					NAccountContract accountContract = nAccountContractService.readByUidCode(part.getPart(), userId, symbol);
					BigDecimal minbtc = accountContract.getF_btc();
					if (amount.compareTo(minbtc) > 0) {
						String tradesStr = createErrorCode(Contents.API_ERROR_LOWBALANCE, "余额不足");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
					
					if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) { // 非交易时间暂停转出
						if (!nNotradetimeService.isTradeTime(part.getPart(), bqt.getTransaction_id())) {
							List<NMemberTransaction> transList = nMemberTransactionService
									.readTransactionByUserId(part.getPart(), bqt.getTransaction_code(), userId);
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
						String transNo = nTransferAccountService.updateAccountForZC(part.getPart(), userId, bqt.getCurrency_type_id(), symbol, amount);
						if (!StringUtil.isEmpty(transNo)) {
							NContractAccountLog log = nContractAccountLogService.readByTransNoAndUserId(part.getPart(), symbol, transNo, userId);
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
	 * 资金划转历史记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/transfer/records")
	public String getTransferRecords(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
				NTransaction bqt = findNCurrTrans(part.getPart(), symbol);
				if (bqt == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_NOTRANSACTION, "不支持的合约");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				List<NContractAccountLog> logs = nContractAccountLogService.readByUserIdAndTime(part.getPart(), symbol, operation, userId, nFrom, direct, start, end, size);
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
	
}
