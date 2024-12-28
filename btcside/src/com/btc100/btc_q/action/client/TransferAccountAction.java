package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberPaywordService;
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
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping("/transferAccount")
public class TransferAccountAction extends BaseAction {

	Logger logger = Logger.getLogger(TransferAccountAction.class);

	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private TransferAccountService transferAccountService;
	@Autowired
	private ContractAccountLogService contractAccountLogService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
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
	private NContractAccountLogService nContractAccountLogService;
	@Autowired
	private NTransferAccountService nTransferAccountService;
	@Autowired
	private NNotradetimeService nNotradetimeService;
	@Autowired
	private NMemberTransactionService nMemberTransactionService;
	
	@RequestMapping("/getAmount")
	public void getAmount(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String bqCode = request.getParameter("bqCode");
		String contractType = request.getParameter("contractType");
		int currenyId = Contents.TRADE_CURRENCY_TYPE_ID;
		if ("vcontract".equals(contractType)) {
			VTransaction vTrans = super.findCurrVTrans(bqCode);
			if (vTrans == null) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			currenyId = vTrans.getCurrency_type_id();
			bqCode = vTrans.getTransaction_code();
		} else if ("delivery".equals(contractType)) {
			DeliveryTransaction vTrans = super.findCurrDTrans(bqCode);
			if (vTrans == null) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			bqCode = vTrans.getTransaction_code();
		} else if (contractType == null || "".equals(contractType) || "contract".equals(contractType)) {
			BQTransaction bqt = super.findCurrTrans(bqCode);
			if (bqt == null) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			bqCode = bqt.getTransaction_code();
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(contractType);
			if (part != null) {
				NTransaction bqt = super.findNCurrTrans(contractType, bqCode);
				if (bqt == null) {
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				bqCode = bqt.getTransaction_code();
				currenyId = bqt.getCurrency_type_id();
			} else {
				checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		String type = request.getParameter("type");
		BigDecimal amount = BigDecimal.ZERO;
		if (!"2".equals(type)) { // 转入
			amount = bqAccountService.readAccount(user.getF_user_id(), currenyId).getBtc().setScale(4, RoundingMode.DOWN);
		} else { // 转出
			if ("vcontract".equals(contractType)) {
				VAccountContract vAccountContract = vAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
				amount = vAccountContract.getF_btc().setScale(4, RoundingMode.DOWN);
			} else if ("delivery".equals(contractType)) {
				DeliveryAccountContract dAccountContract = dAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
				amount = dAccountContract.getF_btc().setScale(4, RoundingMode.DOWN);
			} else if (contractType == null || "".equals(contractType) || "contract".equals(contractType)) {
				BQAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
				amount = accountContract.getF_btc().setScale(4, RoundingMode.DOWN);
			} else {
				NAccountContract accountContract = nAccountContractService.readByUidCode(contractType, user.getF_user_id(), bqCode);
				amount = accountContract.getF_btc().setScale(4, RoundingMode.DOWN);
			}
		}
		
		checkVO.setCode("success");
		checkVO.setFrozen(amount.doubleValue());
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/transfer")
	public String transferTo(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		List<BQTransaction> transactionList = super.findCurrTrans();
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("bqCode");
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (BQTransaction trans : transactionList) {
				if (trans.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = trans;
					break;
				}
			}
		}
		if (bqt == null) {
			bqt = transactionList.get(0);
		}

		model.addAttribute("currentTransaction", bqt);

		String type = request.getParameter("type");
		model.addAttribute("type", type);
		
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
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
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

		PagerModel pm = new PagerModel();
		if (!"2".equals(type)) { // 转入
			pm = contractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_CZ, startTime, endTime);
		} else { // 转出
			pm = contractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_TX, startTime, endTime);
		}
		
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("pm", pm);
		model.addAttribute("pageN", "transferAccount/transfer.do");

		return "jsp/transfer-to";
	}

	@RequestMapping("/transfertodo")
	public void transferAccountIn(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		List<BQTransaction> transactionList = super.findCurrTrans();
		String bqCode = request.getParameter("bqCode");
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!bqt.getTransfer_in_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferIn", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0)).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorIntoNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (amount.compareTo(account.getBtc()) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.intoNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		// 创建账户如果不存在
		BQAccountContract accountContract = bqAccountContractService.readWithNullByUidCode(user.getF_user_id(), bqCode);
		if (accountContract == null) {
			String lock = spyMemcachedClient.lock(bqCode + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					bqAccountContractService.createByUidCode(user.getF_user_id(), bqCode);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					spyMemcachedClient.unlock(bqCode + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id(), lock);
				}
			}
		}

		try {
			String transNo = transferAccountService.updateAccountForZC(user.getF_user_id(), bqCode, amount.negate());
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/transferoutdo")
	public void transferAccountOut(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		List<BQTransaction> transactionList = super.findCurrTrans();
		String bqCode = request.getParameter("bqCode");
		BQTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqt.getTransfer_out_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
	
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorOutNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccountContract accountContract = bqAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
		BigDecimal minbtc = accountContract.getF_btc();
		
		if (amount.compareTo(minbtc) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.outNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) { // 非交易时间暂停转出
			if (!bqNotradetimeService.isTradeTime(bqt.getTransaction_id())) {
				List<BQMemberTransaction> transList = bqMemberTransactionService
						.readTransactionByUserId(bqt.getTransaction_code(), user.getF_user_id());
				if (transList.size() > 0) {
					BigDecimal deposit = BigDecimal.ZERO;
					for (BQMemberTransaction trans : transList) {
						deposit = deposit.add(trans.getDeposit()).subtract(trans.getDeposit2()).add(trans.getF_fromremain())
								.add(trans.getF_min_frozenremain()).add(trans.getF_frozenremain());
					}
					// 最多转出 = 合约可用 - 保证金*2
					BigDecimal maxTransferOut = minbtc.subtract(deposit.multiply(new BigDecimal(2))).setScale(4, BigDecimal.ROUND_DOWN);
					if (amount.compareTo(maxTransferOut) > 0) {
						String ret = SpringContext.getText("msg.transfer.request.noTransferOut",
								Contents.Local.LOCALE.get());
						ret = ret.replace("{0}", "" + maxTransferOut.doubleValue());
						checkVO.setMsg(ret);
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				}
			}
		}
		
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		try {
			String transNo = transferAccountService.updateAccountForZC(user.getF_user_id(), bqCode, amount);
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	@RequestMapping("/ntransfer")
	public String ntransfer(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		String partition = request.getParameter("partition");
		
		List<NTransaction> transactionList = super.findNCurrTrans(partition);
		model.addAttribute("transactionList", transactionList);
		String bqCode = request.getParameter("bqCode");
		
		NTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (NTransaction trans : transactionList) {
				if (trans.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = trans;
					break;
				}
			}
		}
		if (bqt == null) {
			bqt = transactionList.get(0);
		}

		model.addAttribute("currentTransaction", bqt);

		String type = request.getParameter("type");
		model.addAttribute("type", type);
		
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
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
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

		PagerModel pm = new PagerModel();
		if (!"2".equals(type)) { // 转入
			pm = nContractAccountLogService.readPageAccount(partition, user.getF_user_id(), bqCode, Contents.TRANS_TYPE_CZ, startTime, endTime);
		} else { // 转出
			pm = nContractAccountLogService.readPageAccount(partition, user.getF_user_id(), bqCode, Contents.TRANS_TYPE_TX, startTime, endTime);
		}
		
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("partition", partition);
		model.addAttribute("pm", pm);
		model.addAttribute("pageN", "transferAccount/transfer.do");

		return "jsp/transfer-to";
	}

	@RequestMapping("/ntransfertodo")
	public void ntransfertodo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		String partition = request.getParameter("partition");
		List<NTransaction> transactionList = super.findNCurrTrans(partition);
		String bqCode = request.getParameter("bqCode");
		NTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (NTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!bqt.getTransfer_in_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferIn", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0)).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorIntoNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), bqt.getCurrency_type_id());
		if (amount.compareTo(account.getBtc()) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.intoNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		// 创建账户如果不存在
		NAccountContract accountContract = nAccountContractService.readWithNullByUidCode(partition, user.getF_user_id(), bqCode);
		if (accountContract == null) {
			String lock = spyMemcachedClient.lock(partition + "_" + bqCode + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					nAccountContractService.createByUidCode(partition, user.getF_user_id(), bqCode);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					spyMemcachedClient.unlock(partition + "_" + bqCode + BTCQMemecacheKeys.ACCOUNT_CONTRACT + user.getF_user_id(), lock);
				}
			}
		}

		try {
			String transNo = nTransferAccountService.updateAccountForZC(partition, user.getF_user_id(), bqt.getCurrency_type_id(), bqCode, amount.negate());
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/ntransferoutdo")
	public void ntransferoutdo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);
		String partition = request.getParameter("partition");
		List<NTransaction> transactionList = super.findNCurrTrans(partition);
		String bqCode = request.getParameter("bqCode");
		NTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (NTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqt.getTransfer_out_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
	
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorOutNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NAccountContract accountContract = nAccountContractService.readByUidCode(partition, user.getF_user_id(), bqCode);
		BigDecimal minbtc = accountContract.getF_btc();
		
		if (amount.compareTo(minbtc) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.outNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) { // 非交易时间暂停转出
			if (!nNotradetimeService.isTradeTime(partition, bqt.getTransaction_id())) {
				List<NMemberTransaction> transList = nMemberTransactionService
						.readTransactionByUserId(partition, bqt.getTransaction_code(), user.getF_user_id());
				if (transList.size() > 0) {
					BigDecimal deposit = BigDecimal.ZERO;
					for (NMemberTransaction trans : transList) {
						deposit = deposit.add(trans.getDeposit()).subtract(trans.getDeposit2()).add(trans.getF_fromremain())
								.add(trans.getF_min_frozenremain()).add(trans.getF_frozenremain());
					}
					// 最多转出 = 合约可用 - 保证金*2
					BigDecimal maxTransferOut = minbtc.subtract(deposit.multiply(new BigDecimal(2))).setScale(4, BigDecimal.ROUND_DOWN);
					if (amount.compareTo(maxTransferOut) > 0) {
						String ret = SpringContext.getText("msg.transfer.request.noTransferOut",
								Contents.Local.LOCALE.get());
						ret = ret.replace("{0}", "" + maxTransferOut.doubleValue());
						checkVO.setMsg(ret);
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				}
			}
		}
		
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		try {
			String transNo = nTransferAccountService.updateAccountForZC(partition, user.getF_user_id(), bqt.getCurrency_type_id(), bqCode, amount);
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/vtransfer")
	public String vtransfer(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		List<VTransaction> transactionList = super.findCurrVTrans();
		model.addAttribute("transactionList", transactionList);
		
		String bqCode = request.getParameter("bqCode");
		VTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (VTransaction trans : transactionList) {
				if (trans.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = trans;
					break;
				}
			}
		}
		if (bqt == null) {
			bqt = transactionList.get(0);
		}

		model.addAttribute("currentTransaction", bqt);

		String type = request.getParameter("type");
		model.addAttribute("type", type);
		
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
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
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

		PagerModel pm = new PagerModel();
		if (!"2".equals(type)) { // 转入
			pm = vContractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_CZ, startTime, endTime);
		} else { // 转出
			pm = vContractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_TX, startTime, endTime);
		}
		
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("pm", pm);
		model.addAttribute("pageN", "transferAccount/vtransfer.do");

		return "jsp/transfer-to";
	}

	@RequestMapping("/vtransfertodo")
	public void vtransfertodo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		List<VTransaction> transactionList = super.findCurrVTrans();
		String bqCode = request.getParameter("bqCode");
		VTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (VTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!bqt.getTransfer_in_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferIn", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0)).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorIntoNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), bqt.getCurrency_type_id());
		if (amount.compareTo(account.getBtc()) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.intoNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		// 创建账户如果不存在
		VAccountContract accountContract = vAccountContractService.readWithNullByUidCode(user.getF_user_id(), bqCode);
		if (accountContract == null) {
			String lock = spyMemcachedClient.lock(bqCode + BTCQMemecacheKeys.V_ACCOUNT_CONTRACT + user.getF_user_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					vAccountContractService.createByUidCode(user.getF_user_id(), bqCode);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					spyMemcachedClient.unlock(bqCode + BTCQMemecacheKeys.V_ACCOUNT_CONTRACT + user.getF_user_id(), lock);
				}
			}
		}

		try {
			String transNo = vTransferAccountService.updateAccountForZC(user.getF_user_id(), bqt.getCurrency_type_id(), bqCode, amount.negate());
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/vtransferoutdo")
	public void vtransferoutdo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);
		List<VTransaction> transactionList = super.findCurrVTrans();
		String bqCode = request.getParameter("bqCode");
		VTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (VTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqt.getTransfer_out_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorOutNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		VAccountContract accountContract = vAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
		BigDecimal minbtc = accountContract.getF_btc();
		if (amount.compareTo(minbtc) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.outNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		try {
			String transNo = vTransferAccountService.updateAccountForZC(user.getF_user_id(), bqt.getCurrency_type_id(), bqCode, amount);
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	@RequestMapping("/dtransfer")
	public String dtransfer(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		List<DeliveryTransaction> transactionList = super.findCurrDTrans();
		model.addAttribute("transactionList", transactionList);
		
		String bqCode = request.getParameter("bqCode");
		DeliveryTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (DeliveryTransaction trans : transactionList) {
				if (trans.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = trans;
					break;
				}
			}
		}
		if (bqt == null) {
			bqt = transactionList.get(0);
		}

		model.addAttribute("currentTransaction", bqt);

		String type = request.getParameter("type");
		model.addAttribute("type", type);
		
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
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
			endTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(endTime * 1000l));
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

		PagerModel pm = new PagerModel();
		if (!"2".equals(type)) { // 转入
			pm = dContractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_CZ, startTime, endTime);
		} else { // 转出
			pm = dContractAccountLogService.readPageAccount(user.getF_user_id(), bqCode, Contents.TRANS_TYPE_TX, startTime, endTime);
		}
		
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("pm", pm);
		model.addAttribute("pageN", "transferAccount/dtransfer.do");

		return "jsp/transfer-to";
	}

	@RequestMapping("/dtransfertodo")
	public void dtransfertodo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		List<DeliveryTransaction> transactionList = super.findCurrDTrans();
		String bqCode = request.getParameter("bqCode");
		DeliveryTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (DeliveryTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!bqt.getTransfer_in_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferIn", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0)).setScale(8, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorIntoNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (amount.compareTo(account.getBtc()) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.intoNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		// 创建账户如果不存在
		DeliveryAccountContract accountContract = dAccountContractService.readWithNullByUidCode(user.getF_user_id(), bqCode);
		if (accountContract == null) {
			String lock = spyMemcachedClient.lock(bqCode + BTCQMemecacheKeys.D_ACCOUNT_CONTRACT + user.getF_user_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					dAccountContractService.createByUidCode(user.getF_user_id(), bqCode);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					spyMemcachedClient.unlock(bqCode + BTCQMemecacheKeys.D_ACCOUNT_CONTRACT + user.getF_user_id(), lock);
				}
			}
		}

		try {
			String transNo = dTransferAccountService.updateAccountForZC(user.getF_user_id(), bqCode, amount.negate());
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferIntoFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	@RequestMapping("/dtransferoutdo")
	public void dtransferoutdo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");
		Gson gson = new Gson();

		BQMember user = super.findCurrUser(request, true);

		List<DeliveryTransaction> transactionList = super.findCurrDTrans();
		String bqCode = request.getParameter("bqCode");
		DeliveryTransaction bqt = null;
		if (!StringUtil.isEmpty(bqCode)) {
			for (DeliveryTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(bqCode)) {
					bqt = bqTransaction;
					break;
				}
			}
		}
		if (bqt == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqt.getTransfer_out_enable()) {
			checkVO.setMsg(SpringContext.getText("msg.risk.pause", Contents.Local.LOCALE.get())
					+ bqt.getTransaction_code().toUpperCase() + SpringContext.getText("msg.risk.transferOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorOutNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		DeliveryAccountContract accountContract = dAccountContractService.readByUidCode(user.getF_user_id(), bqCode);
		BigDecimal minbtc = accountContract.getF_btc();
		
		if (amount.compareTo(minbtc) > 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.outNumberBeyond", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String payPwd = request.getParameter("payPwd");
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					logger.error("MD5加密失败" + e.toString(), e);
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		try {
			String transNo = dTransferAccountService.updateAccountForZC(user.getF_user_id(), bqCode, amount);
			if (!StringUtil.isEmpty(transNo)) {
				checkVO.setCode("success");
				checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutSuccess", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (Exception e) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			e.printStackTrace();
			return;
		}
		
		checkVO.setMsg(SpringContext.getText("msg.transfer.request.transferOutFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}
}
