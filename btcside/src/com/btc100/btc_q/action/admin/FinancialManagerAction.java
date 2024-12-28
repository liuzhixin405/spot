package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberLevelBuyAmountLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionOverloadLogService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionOverloadLogService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.ContractBalanceVo;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.QuantContractVo;
import com.btc100.btc_q.vo.StatisticalFormVo;
import com.btc100.btc_q.vo.SystemContext;
import com.btc100.btc_q.vo.UserAccountVo;

/**
 * 财务管理
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/managerFinancial")
public class FinancialManagerAction extends BaseAction {

	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberTransactionOverloadLogService overloadLogService;
	@Autowired
	private DeliveryMemberTransactionOverloadLogService deliveryOverloadLogService;
	@Autowired
	private BQMemberLevelBuyAmountLogService bqMemberLevelBuyAmountLogService;
	
	@RequestMapping("/overloadAmount")
	public String overloadAmount(String code, Long uid, String startTime, String endTime, Model model) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
		long endtime = StringUtil.isEmpty(endTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(endTime));
		
		PagerModel pm = overloadLogService.readAll(starttime, endtime, uid, code);
		model.addAttribute("pm", pm);
		
		BigDecimal allBalance = overloadLogService.readSumBalance(starttime, endtime, uid, code);
		model.addAttribute("allBalance", allBalance.doubleValue());
		
		model.addAttribute("code", code);
		model.addAttribute("uid", uid);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		
		return super.url_JSP("overloadAmount");
	}
	
	@RequestMapping("/deliveryOverloadAmount")
	public String deliveryOverloadAmount(String code, Long uid, String startTime, String endTime, Model model) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
		long endtime = StringUtil.isEmpty(endTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(endTime));
		
		PagerModel pm = deliveryOverloadLogService.readAll(starttime, endtime, uid, code);
		model.addAttribute("pm", pm);
		
		BigDecimal allBalance = deliveryOverloadLogService.readSumBalance(starttime, endtime, uid, code);
		model.addAttribute("allBalance", allBalance.doubleValue());
		
		model.addAttribute("code", code);
		model.addAttribute("uid", uid);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		
		return super.url_JSP("overloadAmount");
	}

	@RequestMapping("/loadCount")
	public String loadCount(String startTime, String endTime, Model model) throws ParseException {

		if (StringUtil.isEmpty(startTime) && StringUtil.isEmpty(endTime)) {
			return super.url_JSP("timeCount");
		}

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
		long endtime = StringUtil.isEmpty(endTime) ? bqTransactionService.time() : DateUtil.DateToUnix(formatDate.parse(endTime));

		List<BQTransaction> lstTrans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		if (lstTrans.size() <= 0) { // 没有可用合约
			return super.url_JSP("timeCount");
		}
		List<CountVo> lstCountVoAll = new ArrayList<CountVo>();
		for (BQTransaction trans : lstTrans) {
			String bqCode = trans.getTransaction_code();
			List<CountVo> lstCountVoBuy = memberTradeDetailService.finCountByTime(bqCode, 1, starttime, endtime);
			if (lstCountVoBuy != null && lstCountVoBuy.size() > 0)
				lstCountVoAll.addAll(lstCountVoBuy);
			List<CountVo> lstCountVoSell = memberTradeDetailService.finCountByTime(bqCode, 2, starttime, endtime);
			if (lstCountVoSell != null && lstCountVoSell.size() > 0)
				lstCountVoAll.addAll(lstCountVoSell);
		}
		model.addAttribute("lstCounts", lstCountVoAll);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		return super.url_JSP("timeCount");
	}

	@RequestMapping("/loadNetTrade")
	public String loadNetTrade(String bqCode, String type, Model model) throws ParseException {
		List<BQTransaction> lstTrans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		if (lstTrans.size() <= 0) { // 没有可用合约
			return super.url_JSP("netTrade");
		}
		model.addAttribute("lstTrans", lstTrans);

		bqCode = StringUtil.isEmpty(bqCode) ? lstTrans.get(0).getTransaction_code() : bqCode;
		int dirType = ConvertUtil.toInt(type, 0);
		if (dirType != Contents.DIRECTION_BUY && dirType != Contents.DIRECTION_SELL) {
			dirType = Contents.DIRECTION_BUY;
		}
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("type", dirType);

		PagerModel pm = memberTradeDetailService.findDetailsByPara(bqCode, dirType);
		model.addAttribute("pm", pm);

		return super.url_JSP("netTrade");
	}
	
	@RequestMapping("/statisticsData")
	public String statisticsData(HttpServletRequest request, String orderBy, String userName, String userId, String coinId, Model model) {
		int currencyId = ConvertUtil.toInt(coinId, Contents.TRADE_CURRENCY_TYPE_ID);
		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
		}
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readByRealEmail(userName);
		}
		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		BQAccount bqAccount = bqAccountService.readAccountStatistics(currencyId);
		
		BQCurrencyType currencyType = null;
		for (BQCurrencyType type : coins) {
			if (type.getType_id() == currencyId) {
				currencyType = type;
				break;
			}
		}
		
		PagerModel pm = bqAccountService.readStatistics(orderBy, user.getF_user_id(), currencyId);
		model.addAttribute("allCoin", coins);
		model.addAttribute("bqAccount", bqAccount);
		model.addAttribute("pm", pm);
		model.addAttribute("orderBy", StringUtil.isEmpty(orderBy) ? "ALL" : orderBy);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("coinId", currencyId);
		
		if (pm.getTotal() > 0) {
			List<UserAccountVo> newList = new ArrayList<>();
			for (Object obj : pm.getDatas()) {
				BQAccount account = (BQAccount) obj;
				UserAccountVo accvo = new UserAccountVo();
				accvo.setUserId(account.getF_user_id());
				accvo.setCoinId(account.getCurrency_type_id());
				accvo.setUserEmail(account.getF_user_email());
				accvo.setAvailable(account.getBtc().setScale(4, RoundingMode.DOWN));
				accvo.setFrozen(account.getBtc_locked().setScale(4, RoundingMode.DOWN));
				accvo.setWalletAddress("");
				accvo.setBfxToUnlock(BigDecimal.ZERO);
				
				if (currencyType != null && currencyType.getType_name().equals("BFX")) {
					Object[] objs = (Object[]) bqMemberLevelBuyAmountLogService.readSumBfxLock(account.getF_user_id());
					BigDecimal allLock = (BigDecimal) objs[0];
					BigDecimal allUnlock = (BigDecimal) objs[1];
					accvo.setBfxToUnlock(allLock.subtract(allUnlock));
				}
				
				newList.add(accvo);
			}
			
			pm.setDatas(newList);
		}
		
		if (currencyType != null && currencyType.getType_name().equals("BFX")) {
			Object[] objs = (Object[]) bqMemberLevelBuyAmountLogService.readSumBfxLock(0);
			BigDecimal allLock = (BigDecimal) objs[0];
			BigDecimal allUnlock = (BigDecimal) objs[1];
			model.addAttribute("toUnlock", allLock.subtract(allUnlock).doubleValue());
		} else {
			model.addAttribute("toUnlock", 0);
		}
		
		
		return super.url_JSP("AccountStatistics");
	}
	
	@RequestMapping("/contractAccountStatistics")
	public String contractAccountStatistics(HttpServletRequest request, String orderBy, String userName, String userId, Boolean refresh, Model model) {
		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
		}
		
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
		}
		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}
		
		try {
		List<ContractBalanceVo> contractBalances = null;
		ContractBalanceVo allBalance = new ContractBalanceVo();
		
		if (user.getF_user_id() > 0) {
			ContractBalanceVo balance = new ContractBalanceVo();
			contractBalances = new ArrayList<ContractBalanceVo>();
			contractBalances.add(balance);
			
			balance.setUserId(user.getF_user_id());
			BQAccount accountUser = bqAccountService.readAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
			if (accountUser != null) {
				balance.setUserName(accountUser.getF_user_email());
				balance.setAccountBalance(accountUser.getBtc().doubleValue());
				balance.setAccountFrozen(accountUser.getBtc_locked().doubleValue());
				balance.setAccountAll(balance.getAccountBalance() + balance.getAccountFrozen());
				balance.setAll(balance.getAccountAll());
			}
			
			// 永续合约账户资金大于0
			List<Object[]> contractAccounts = bqAccountContractService.readBalanceAndFrozen(user.getF_user_id());
			for (int i = 0; i < contractAccounts.size(); i++) {
				Object[] contractAccount = contractAccounts.get(i);
				balance.setContractBalance(((BigDecimal) contractAccount[1]).doubleValue());
				balance.setContractFrozen(((BigDecimal) contractAccount[2]).doubleValue());
				balance.setContractAll(balance.getContractBalance() + balance.getContractFrozen());
				balance.setAll(balance.getContractAll() + balance.getAll());
			}
			
			// 交割合约账户资金大于0
			List<Object[]> deliveryContractAccounts = dAccountContractService.readBalanceAndFrozen(user.getF_user_id());
			for (int i = 0; i < deliveryContractAccounts.size(); i++) {
				Object[] contractAccount = deliveryContractAccounts.get(i);
				balance.setDeliveryContractBalance(((BigDecimal) contractAccount[1]).doubleValue());
				balance.setDeliveryContractFrozen(((BigDecimal) contractAccount[2]).doubleValue());
				balance.setDeliveryContractAll(balance.getDeliveryContractBalance() + balance.getDeliveryContractFrozen());
				balance.setAll(balance.getDeliveryContractAll() + balance.getAll());
			}
			
			// 永续合约持仓资金大于0
//			List<Object[]> transAccounts = bqMemberTransactionService.readSumHoldsOrderByUser(user.getF_user_id());
//			for (int i = 0; i < transAccounts.size(); i++) {
//				Object[] contractAccount = transAccounts.get(i);
//				BigDecimal bal = (BigDecimal) contractAccount[1];
//				balance.setContractDeposit(bal.doubleValue());
//				balance.setContractAll(bal.doubleValue() + balance.getContractAll());
//				balance.setAll(bal.doubleValue() + balance.getAll());
//			}
			
			BigDecimal floatGainLoss = BigDecimal.ZERO;
			BigDecimal depositAndRemain = BigDecimal.ZERO;	
			List<Object[]> memberTrans = bqMemberTransactionService.readDepositAndFloatingPL(user.getF_user_id());
			for (int i = 0; i < memberTrans.size(); i++) {
				Object[] contractAccount = memberTrans.get(i);
				String code = (String) contractAccount[0];
				Character type = (Character) contractAccount[2];
				int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
				BigDecimal avgPrice = (BigDecimal) contractAccount[3];
				BigDecimal hold = (BigDecimal) contractAccount[4];
				BigDecimal lock = (BigDecimal) contractAccount[5];
				depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
				
				BQTransaction bqTransaction = super.findCurrTransByCode(code);
				if (bqTransaction == null) {
					break;
				}
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + code);
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(code);
				}
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
					floatGainLoss = floatGainLoss.add(ykdiff);
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
							.multiply(lock.add(hold));
					floatGainLoss = floatGainLoss.add(ykdiff);
				}
			}
			balance.setContractDeposit(depositAndRemain.doubleValue());
			balance.setContractFloatGainLoss(floatGainLoss.doubleValue());
			balance.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + balance.getContractAll());
			balance.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + balance.getAll());
			
			// 交割合约持仓资金大于0
//			List<Object[]> deliveryMemberTrans = dMemberTransactionService.readSumHoldsOrderByUser(user.getF_user_id());
//			for (int i = 0; i < deliveryMemberTrans.size(); i++) {
//				Object[] contractAccount = deliveryMemberTrans.get(i);
//				BigDecimal bal = (BigDecimal) contractAccount[1];
//				balance.setDeliveryContractDeposit(bal.doubleValue());
//				balance.setDeliveryContractAll(bal.doubleValue() + balance.getDeliveryContractAll());
//				balance.setAll(bal.doubleValue() + balance.getAll());
//			}
			BigDecimal deliveryFloatingPL = BigDecimal.ZERO;
			BigDecimal deliveryDepositAndRemain = BigDecimal.ZERO;	
			List<Object[]> deliveryMemberTrans = dMemberTransactionService.readDepositAndFloatingPL(user.getF_user_id());
			for (int i = 0; i < deliveryMemberTrans.size(); i++) {
				Object[] contractAccount = deliveryMemberTrans.get(i);
				String code = (String) contractAccount[0];
				Character type = (Character) contractAccount[2];
				int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
				BigDecimal avgPrice = (BigDecimal) contractAccount[3];
				BigDecimal hold = (BigDecimal) contractAccount[4];
				BigDecimal lock = (BigDecimal) contractAccount[5];
				deliveryDepositAndRemain = deliveryDepositAndRemain.add((BigDecimal) contractAccount[6]);
				
				DeliveryTransaction bqTransaction = super.findDTransByCode(code);
				if (bqTransaction == null) {
					break;
				}
				BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + code);
				if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
					markPrice = super.findBPIByCode(code);
				}
				if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
					deliveryFloatingPL = deliveryFloatingPL.add(ykdiff);
				} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
					BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
					ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
							.multiply(lock.add(hold));
					deliveryFloatingPL = deliveryFloatingPL.add(ykdiff);
				}
			}
			balance.setDeliveryContractDeposit(deliveryDepositAndRemain.doubleValue());
			balance.setDeliveryContractFloatGainLoss(deliveryFloatingPL.doubleValue());
			balance.setContractAll(deliveryDepositAndRemain.doubleValue() + deliveryFloatingPL.doubleValue() + balance.getContractAll());
			balance.setAll(deliveryDepositAndRemain.doubleValue() + deliveryFloatingPL.doubleValue() + balance.getAll());
		} else {
			contractBalances = spyMemcachedClient.get("contractAccountStatistics");
			allBalance = spyMemcachedClient.get("contractAccountStatistics_all");
			if (contractBalances == null) {
				contractBalances = new ArrayList<ContractBalanceVo>();
				allBalance = new ContractBalanceVo();
				
				// 总账户资金大于0
				List<Object[]> accountUsers = bqAccountService.readBalanceAndFrozen(Contents.TRADE_CURRENCY_TYPE_ID);
				for (Object[] account : accountUsers) {
					ContractBalanceVo balance = new ContractBalanceVo();
					balance.setUserId((Integer) account[0]);
					balance.setUserName((String) account[1]);
					balance.setAccountBalance(((BigDecimal) account[2]).doubleValue());
					balance.setAccountFrozen(((BigDecimal) account[3]).doubleValue());
					balance.setAccountAll(balance.getAccountBalance() + balance.getAccountFrozen());
					balance.setAll(balance.getAccountAll());
					allBalance.setAccountAll(allBalance.getAccountAll() + balance.getAccountAll());
					contractBalances.add(balance);
				}
				
				// 永续合约账户资金大于0
				List<Object[]> contractAccounts = bqAccountContractService.readBalanceAndFrozen(0);
				for (int i = 0; i < contractAccounts.size(); i++) {
					Object[] contractAccount = contractAccounts.get(i);
					int uid = (Integer) contractAccount[0];
					boolean have = false;
					BigDecimal balance = (BigDecimal) contractAccount[1];
					BigDecimal frozen = (BigDecimal) contractAccount[2];
					
					for (int j = 0; j < contractBalances.size(); j++) {
						ContractBalanceVo account = contractBalances.get(j);
						long id = account.getUserId();
						if (id == uid) {
							account.setContractBalance(balance.doubleValue());
							account.setContractFrozen(frozen.doubleValue());
							account.setContractAll(account.getContractBalance() + account.getContractFrozen());
							account.setAll(account.getContractAll() + account.getAll());
							have = true;
							break;
						}
					}
					
					if (!have) {
						ContractBalanceVo balanceVo = new ContractBalanceVo();
						balanceVo.setUserId(uid);
						balanceVo.setContractBalance(balance.doubleValue());
						balanceVo.setContractFrozen(frozen.doubleValue());
						balanceVo.setContractAll(balanceVo.getContractBalance() + balanceVo.getContractFrozen());
						balanceVo.setAll(balanceVo.getContractAll());
						contractBalances.add(balanceVo);
					}
				}
				
				// 交割合约账户资金大于0
				List<Object[]> deliveryContractAccounts = dAccountContractService.readBalanceAndFrozen(0);
				for (int i = 0; i < deliveryContractAccounts.size(); i++) {
					Object[] contractAccount = deliveryContractAccounts.get(i);
					int uid = (Integer) contractAccount[0];
					boolean have = false;
					BigDecimal balance = (BigDecimal) contractAccount[1];
					BigDecimal frozen = (BigDecimal) contractAccount[2];
					
					for (int j = 0; j < contractBalances.size(); j++) {
						ContractBalanceVo account = contractBalances.get(j);
						long id = account.getUserId();
						if (id == uid) {
							account.setDeliveryContractBalance(balance.doubleValue());
							account.setDeliveryContractFrozen(frozen.doubleValue());
							account.setDeliveryContractAll(account.getDeliveryContractBalance() + account.getDeliveryContractFrozen());
							account.setAll(account.getDeliveryContractAll() + account.getAll());
							have = true;
							break;
						}
					}
					
					if (!have) {
						ContractBalanceVo balanceVo = new ContractBalanceVo();
						balanceVo.setUserId(uid);
						balanceVo.setDeliveryContractBalance(balance.doubleValue());
						balanceVo.setDeliveryContractFrozen(frozen.doubleValue());
						balanceVo.setDeliveryContractAll(balanceVo.getDeliveryContractBalance() + balanceVo.getDeliveryContractFrozen());
						balanceVo.setAll(balanceVo.getDeliveryContractAll());
						contractBalances.add(balanceVo);
					}
				}
				
				// 永续合约持仓资金大于0
//				List<Object[]> transAccounts = bqMemberTransactionService.readSumHoldsOrderByUser(0);
//				for (int i = 0; i < transAccounts.size(); i++) {
//					Object[] contractAccount = transAccounts.get(i);
//					int uid = (Integer) contractAccount[0];
//					boolean have = false;
//					BigDecimal balance = (BigDecimal) contractAccount[1];
//					
//					for (int j = 0; j < contractBalances.size(); j++) {
//						ContractBalanceVo account = contractBalances.get(j);
//						long id = account.getUserId();
//						if (id == uid) {
//							account.setContractDeposit(balance.doubleValue());
//							account.setContractAll(account.getContractAll() + account.getContractDeposit());
//							account.setAll(balance.doubleValue() + account.getAll());
//							have = true;
//							break;
//						}
//					}
//					
//					if (!have) {
//						ContractBalanceVo balanceVo = new ContractBalanceVo();
//						balanceVo.setUserId(uid);
//						balanceVo.setContractDeposit(balance.doubleValue());
//						balanceVo.setContractAll(balance.doubleValue());
//						balanceVo.setAll(balance.doubleValue());
//						contractBalances.add(balanceVo);
//					}
//					
//				}
				
				List<Object[]> memberTrans = bqMemberTransactionService.readDepositAndFloatingPL(0);
				for (int i = 0; i < memberTrans.size(); i++) {
					BigDecimal floatGainLoss = BigDecimal.ZERO;
					BigDecimal depositAndRemain = BigDecimal.ZERO;
					
					Object[] contractAccount = memberTrans.get(i);
					String code = (String) contractAccount[0];
					Character type = (Character) contractAccount[2];
					int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
					BigDecimal avgPrice = (BigDecimal) contractAccount[3];
					BigDecimal hold = (BigDecimal) contractAccount[4];
					BigDecimal lock = (BigDecimal) contractAccount[5];
					depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
					
					BQTransaction bqTransaction = super.findCurrTransByCode(code);
					if (bqTransaction == null) {
						continue;
					}
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + code);
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(code);
					}
					if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
								.multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					}
					
					int uid = (Integer) contractAccount[1];
					boolean have = false;
					
					for (int j = 0; j < contractBalances.size(); j++) {
						ContractBalanceVo account = contractBalances.get(j);
						long id = account.getUserId();
						if (id == uid) {
							account.setContractDeposit(depositAndRemain.doubleValue() + account.getContractDeposit());
							account.setContractFloatGainLoss(floatGainLoss.doubleValue() + account.getContractFloatGainLoss());
							account.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getContractAll());
							account.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getAll());
							have = true;
							break;
						}
					}
					
					if (!have) {
						ContractBalanceVo balanceVo = new ContractBalanceVo();
						balanceVo.setUserId(uid);
						balanceVo.setContractDeposit(depositAndRemain.doubleValue());
						balanceVo.setContractFloatGainLoss(floatGainLoss.doubleValue());
						balanceVo.setContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
						balanceVo.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
						contractBalances.add(balanceVo);
					}
				}
				
				// 交割合约持仓资金大于0
//				List<Object[]> deliveryMemberTrans = dMemberTransactionService.readSumHoldsOrderByUser(0);
//				for (int i = 0; i < deliveryMemberTrans.size(); i++) {
//					Object[] contractAccount = deliveryMemberTrans.get(i);
//					int uid = (Integer) contractAccount[0];
//					boolean have = false;
//					BigDecimal balance = (BigDecimal) contractAccount[1];
//					
//					for (int j = 0; j < contractBalances.size(); j++) {
//						ContractBalanceVo account = contractBalances.get(j);
//						long id = account.getUserId();
//						if (id == uid) {
//							account.setDeliveryContractDeposit(balance.doubleValue());
//							account.setDeliveryContractAll(account.getDeliveryContractAll() + account.getDeliveryContractDeposit());
//							account.setAll(balance.doubleValue() + account.getAll());
//							have = true;
//							break;
//						}
//					}
//					
//					if (!have) {
//						ContractBalanceVo balanceVo = new ContractBalanceVo();
//						balanceVo.setUserId(uid);
//						balanceVo.setDeliveryContractDeposit(balance.doubleValue());
//						balanceVo.setDeliveryContractAll(balance.doubleValue());
//						balanceVo.setAll(balance.doubleValue());
//						contractBalances.add(balanceVo);
//					}
//					
//				}
				List<Object[]> deliveryMemberTrans = dMemberTransactionService.readDepositAndFloatingPL(0);
				for (int i = 0; i < deliveryMemberTrans.size(); i++) {
					BigDecimal floatGainLoss = BigDecimal.ZERO;
					BigDecimal depositAndRemain = BigDecimal.ZERO;
					
					Object[] contractAccount = deliveryMemberTrans.get(i);
					String code = (String) contractAccount[0];
					Character type = (Character) contractAccount[2];
					int buyOrSellTradeTypeId = Integer.parseInt(String.valueOf(type));
					BigDecimal avgPrice = (BigDecimal) contractAccount[3];
					BigDecimal hold = (BigDecimal) contractAccount[4];
					BigDecimal lock = (BigDecimal) contractAccount[5];
					depositAndRemain = depositAndRemain.add((BigDecimal) contractAccount[6]);
					
					DeliveryTransaction bqTransaction = super.findDTransByCode(code);
					if (bqTransaction == null) {
						continue;
					}
					BigDecimal markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_MARK_PRICE + code);
					if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
						markPrice = super.findBPIByCode(code);
					}
					if (buyOrSellTradeTypeId == Contents.DIRECTION_BUY) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = markPrice.subtract(avgPrice).multiply(bqTransaction.getTrade_coefficient()).multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					} else if (buyOrSellTradeTypeId == Contents.DIRECTION_SELL) {
						BigDecimal ykdiff = BigDecimal.ZERO;	// 盈亏变动
						ykdiff = avgPrice.subtract(markPrice).multiply(bqTransaction.getTrade_coefficient())
								.multiply(lock.add(hold));
						floatGainLoss = floatGainLoss.add(ykdiff);
					}
					
					int uid = (Integer) contractAccount[1];
					boolean have = false;
					
					for (int j = 0; j < contractBalances.size(); j++) {
						ContractBalanceVo account = contractBalances.get(j);
						long id = account.getUserId();
						if (id == uid) {
							account.setDeliveryContractDeposit(depositAndRemain.doubleValue() + account.getDeliveryContractDeposit());
							account.setDeliveryContractFloatGainLoss(floatGainLoss.doubleValue() + account.getDeliveryContractFloatGainLoss());
							account.setDeliveryContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getContractAll());
							account.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue() + account.getAll());
							have = true;
							break;
						}
					}
					
					if (!have) {
						ContractBalanceVo balanceVo = new ContractBalanceVo();
						balanceVo.setUserId(uid);
						balanceVo.setDeliveryContractDeposit(depositAndRemain.doubleValue());
						balanceVo.setDeliveryContractFloatGainLoss(floatGainLoss.doubleValue());
						balanceVo.setDeliveryContractAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
						balanceVo.setAll(depositAndRemain.doubleValue() + floatGainLoss.doubleValue());
						contractBalances.add(balanceVo);
					}
				}
				
				for (ContractBalanceVo balance : contractBalances) {
					allBalance.setContractAll(balance.getContractAll() + balance.getDeliveryContractAll() + allBalance.getContractAll());
					allBalance.setAll(balance.getAll() + allBalance.getAll());
				}
				
				spyMemcachedClient.set("contractAccountStatistics", BTCQMemecacheKeys.TIMES.HALF_HOUR, contractBalances);
				spyMemcachedClient.set("contractAccountStatistics_all", BTCQMemecacheKeys.TIMES.HALF_HOUR, allBalance);
			}
			Collections.sort(contractBalances);
		}
		
		PagerModel pm = new PagerModel();
		pm.setTotal(contractBalances.size());
		
		List<ContractBalanceVo> datas = new ArrayList<ContractBalanceVo>();
		int begin = SystemContext.getOffset();
		int size = SystemContext.getPageSize();
		
		for (int i = begin; i < begin + size; i++) {
			if (contractBalances.size() <= i) {
				break;
			}
			datas.add(contractBalances.get(i));
		}
		
		pm.setDatas(datas);
		
		model.addAttribute("pm", pm);
		model.addAttribute("allBalance", allBalance);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.url_JSP("contractAccountStatistics");
	}
	
	@RequestMapping("/quantTradeAmount")
	public String quantTradeAmount(Model mo, String code, String startTimeStr, String endTimeStr, String userName,
			String userId, String tag) throws ParseException {
		List<BQTransaction> transactionList = super.findCurrTrans();
		mo.addAttribute("lstTrans", transactionList);
		
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(code)) {
				code = transactionList.get(0).getTransaction_code();
			}
		} else {
			return super.url_JSP("quantTradeAmount");
		}
		
		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
			uId = user.getF_user_id();
		}
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		if (uId > 0) {
			if (StringUtil.isEmpty(tag)) {
				// 所有与量化的成交额
//				BigDecimal amount = memberTradeDetailService.getTradeAmountWithQuant(code, startTime, endTime, uId, 0);
				// 量化maker
				BigDecimal makerAmount = memberTradeDetailService.getTradeAmountWithQuant(code, startTime, endTime, uId, 1);
				// 量化taker
				BigDecimal takerAmount = memberTradeDetailService.getTradeAmountWithQuant(code, startTime, endTime, uId, 2);
				BigDecimal amount = makerAmount.add(takerAmount);
				
				mo.addAttribute("amount", amount.doubleValue());
				mo.addAttribute("makerAmount", makerAmount.doubleValue());
				mo.addAttribute("takerAmount", takerAmount.doubleValue());
			} else {
				endTime += 86400L;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Long startDay = dateFormat.parse(dateFormat.format(startTime * 1000L)).getTime() / 1000L;
				Long endDay = dateFormat.parse(dateFormat.format(endTime * 1000L)).getTime() / 1000L;
				
				long start = startDay;
				long totalDays = (endDay - startDay) / 86400L;
				List<StatisticalFormVo> reportForms = new ArrayList<StatisticalFormVo>();
				for (int i = 0; i < totalDays; i++) {
					long end = 86400L + start;
					StatisticalFormVo form = getStatisticalForm(start, end, uId, transactionList);
					form.setTime(dateFormat.format(start * 1000L));
					reportForms.add(form);
					start = end;
				}
				mo.addAttribute("reportForms", reportForms);
			}
		}
		mo.addAttribute("current", code);
		mo.addAttribute("userId", userId);
		mo.addAttribute("uId", uId > 0 ? uId : "");
		mo.addAttribute("userName", userName);
		mo.addAttribute("startTime", startTimeStr);
		mo.addAttribute("endTime", endTimeStr);
		
		return super.url_JSP("quantTradeAmount");
	}
	
	private StatisticalFormVo getStatisticalForm(long startTime, long endTime, long uId, List<BQTransaction> list) {
		StatisticalFormVo formVo = new StatisticalFormVo();
		List<QuantContractVo> voList = new ArrayList<QuantContractVo>();
		for (BQTransaction bqTransaction : list) {
			QuantContractVo vo = new QuantContractVo();
			String code = bqTransaction.getTransaction_code();
			// 量化maker
			BigDecimal makerAmount = memberTradeDetailService.getTradeAmountWithQuant(code, startTime, endTime, uId, 1);
			// 量化taker
			BigDecimal takerAmount = memberTradeDetailService.getTradeAmountWithQuant(code, startTime, endTime, uId, 2);
			BigDecimal amount = makerAmount.add(takerAmount);
			vo.setCode(bqTransaction.getTransaction_code());
			vo.setAmount(amount.doubleValue());
			vo.setMakerAmount(makerAmount.doubleValue());
			vo.setTakerAmount(takerAmount.doubleValue());
			
			voList.add(vo);
		}
		formVo.setQuantContractVos(voList);
		return formVo;
	}

}
