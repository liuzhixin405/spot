package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseTradeAction;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NLastTickerService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.MemberTransactionVo;
import com.btc100.btc_q.vo.PlanPendingDelegationVO;
import com.btc100.btc_q.vo.TradeAjaxVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/ntradeAjax/{partition}")
public class NTradeAjaxAction extends BaseTradeAction {

	private static final Logger logger = Logger.getLogger(NTradeAjaxAction.class);

	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NTransactionRateService bqTransactionRateService;
	@Autowired
	private NStopLossService bqStopLossService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private NAccountContractService bqAccountContractService;
	@Autowired
	NLastTickerService bqLastTickerService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private NPlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private NTransactionFeeRateService bqTransactionFeeRateService;
	
	/**
	 * 新盘首日
	 */
	BigDecimal newTransFirDay = new BigDecimal(0.1);

	private NTransaction yzgg(String partition, String transSelect, AjaxCheckVO checkVO, String multiple, Long uid) {

		NTransaction bqTransaction = null;
		List<NTransaction> list = super.findNCurrTrans(partition);
		for (NTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(transSelect)) {
				bqTransaction = bq;
				break;
			}
		}

		if (bqTransaction == null) {
			logger.info("合约错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0 || multiple_client > 500) {
			logger.info("杠杆错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.errorMultiple", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		
		if (bqTransaction.getSuspend()) {
			logger.info("交易暂停：" + bqTransaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				// toWeb(response, gson.toJson(checkVO));
				return null;
			}
		}
		
		int canMultiple =  getNOneCanMultiple(partition, bqTransaction, uid);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			checkVO.setCode("error");
			String msg = SpringContext.getText("msg.trade.request.canMultiple", Contents.Local.LOCALE.get());
			msg = msg.replace("{0}", "" + canMultiple);
			checkVO.setMsg(msg);
			return null;
		}

		return bqTransaction;
	}

	@RequestMapping("/tradeTrans")
	public void tradeTrans(@PathVariable("partition") String partition, HttpServletRequest request, Model model, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 会话验证
		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));

		String orderTypeStr= request.getParameter("orderType"); // 下单类型（0或者空：限价委托;1：市价委托）
		String percentStr= request.getParameter("percent"); // 百分比
		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String transSelect = request.getParameter("transaction_code");
		String multiple = request.getParameter("multiple");
		String payPwd = request.getParameter("payPwd");
		int direction = Convert.toInt(request.getParameter("direction"), 0);
		if (direction != Contents.DIRECTION_BUY && direction != Contents.DIRECTION_SELL) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 参数验证
		final BigDecimal ZERO = BigDecimal.ZERO;
		if (StringUtil.isEmpty(transSelect) || StringUtil.isEmpty(multiple)) {
			logger.info("必选参数错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(price)) {
			logger.info("必选参数错误,price错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(amount) && StringUtil.isEmpty(percentStr)) {
			logger.info("必选参数错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 登录验证
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		} else {
			BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
			if (bqs == null) {
				toWeb(response, gson.toJson(checkVO));
				return;
			} else {
				if (bqs.getF_user_safetype() == 0) {
					toWeb(response, gson.toJson(checkVO));
					return;
				}
			}
		}
		
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查 -- 交易密码验证
		Long uid = user.getF_user_id();
		boolean bol_paypwd = false;
		BQMemberPayword payword = bqMemberPaywordService.readById(uid);
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
			logger.info("交易密码为空或者错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NTransaction bqTransaction = yzgg(partition, transSelect, checkVO, multiple, uid);
		if (bqTransaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		int amountDigits = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getNMarketOrderPrice(partition, bqTransaction, direction);
			if (StringUtil.isEmpty(price)) {
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 开仓数量及价格验证
		BigDecimal amount_client = ZERO;
		BigDecimal price_client = ZERO;
		BigDecimal multiple_client2 = ZERO;
		BigDecimal coefficient = bqTransaction.getTrade_coefficient();
		try {
			double dprice = ConvertUtil.toDouble(price, 0);
			if (dprice <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			price_client = new BigDecimal(dprice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
			multiple_client2 = new BigDecimal(multiple);

		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BigDecimal oneDayMin = null;
		BigDecimal oneDayMax = null;
		BigDecimal btc_index_price = super.findBPIByCode(bqTransactionType.getType_code());
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
			if (btc_index_price == null) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO +  partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO +  partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO +  partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO +  partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
			
		NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());
			
		BigDecimal minIndexPrice = null;
		BigDecimal maxIndexPrice = null;
		if (btc_index_price != null) {
			minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
			minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
			maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
			maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		} else {
			maxIndexPrice = new BigDecimal(100000);
			minIndexPrice = BigDecimal.ZERO;
		}
			
		if (oneDayMin != null /*&& oneDayMin.compareTo(minIndexPrice) > 0*/) {
			minIndexPrice = oneDayMin;
		}
			
		if (oneDayMax != null /*&& oneDayMax.compareTo(maxIndexPrice) < 0*/) {
			maxIndexPrice = oneDayMax;
		}
			
//		if (bqTransaction.getTransaction_kind() == 2) {
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE +  partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
//		}
			
		if (direction == Contents.DIRECTION_BUY && price_client.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price_client = maxIndexPrice;
			} else {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.buyPrice", Contents.Local.LOCALE.get()) + maxIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
			
		if (direction == Contents.DIRECTION_SELL && price_client.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price_client = minIndexPrice;
			} else {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.sellPrice", Contents.Local.LOCALE.get()) + minIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		BigDecimal fee = BigDecimal.ZERO;
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal remainAmount = BigDecimal.ZERO;
		NAccountContract bqContract = bqAccountContractService.readByUidCode(partition, uid, bqTransaction.getTransaction_code());
		if (bqContract != null) {
			remainAmount = bqContract.getF_btc();
		}
		
		if (!StringUtil.isEmpty(percentStr)) {
			BigDecimal percent = new BigDecimal(percentStr.replace("%", "")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			if (percent.compareTo(BigDecimal.ONE) > 0) percent = BigDecimal.ONE;
			if (percent.compareTo(BigDecimal.ZERO) <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			BigDecimal maxAmount = BTCQMath.getMaxAmount(remainAmount, price_client, multiple_client2, coefficient, feeRate);
			amount_client = maxAmount.multiply(percent).setScale(amountDigits, BigDecimal.ROUND_DOWN);
		} else {
			try {
				double damount = ConvertUtil.toDouble(amount, 0);
				if (damount <= 0) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				amount_client = new BigDecimal(damount).setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
			} catch (Exception e) {
				logger.info("价格或数量不为数字");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		if (amount_client.compareTo(bqTransactionType.getMin_kc_nums()) < 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.minimumOpeningNumber", Contents.Local.LOCALE.get()) + bqTransactionType.getMin_kc_nums() + "个");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		fee = BTCQMath.getFee(amount_client, price_client, feeRate, coefficient);

		BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple_client2, coefficient);

		BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
				.divide(price_client.multiply(coefficient), 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
		if (amount_client.compareTo(maxAmount) > 0) {
			if (orderType == 1) {
				amount_client = BTCQMath.getMaxAmount(remainAmount, price_client, multiple_client2, coefficient, feeRate);
				amount_client = amount_client.setScale(amountDigits, BigDecimal.ROUND_DOWN);
				fee = BTCQMath.getFee(amount_client, price_client, feeRate, coefficient);
				deposit = BTCQMath.getDeposit(amount_client, price_client, multiple_client2, coefficient);
			} else {
				logger.info("账户可用余额不足以完成本次交易");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.balanceNotEnough", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		String ret = maxUserKcCheck(bqTransaction.getTransaction_code(), direction, user.getF_user_id(), amount_client);
		if (!StringUtil.isEmpty(ret)) {
			checkVO.setCode("error");
			logger.info(ret);
			checkVO.setMsg(ret);
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!inWhiteList(String.valueOf(uid))) {
			String risk = riskCheck(bqTransaction.getTransaction_code(), direction, price_client, amount_client, digits);
			if (!StringUtil.isEmpty(risk)) {
				checkVO.setCode("error");
				logger.info(risk);
				checkVO.setMsg(risk);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			String maxkc = nMaxKcCheck(partition, bqTransactionType, bqTransaction.getTransaction_code(), direction, user.getF_user_id(), amount_client);
			if (!StringUtil.isEmpty(maxkc)) {
				checkVO.setCode("error");
				logger.info(maxkc);
				checkVO.setMsg(maxkc);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		NPendingDelegationCache cache = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount_client, price_client, BigDecimal.ZERO, amount_client, Integer.valueOf(multiple), deposit,
				feeRate, Contents.PRIORITY_KC, uid, BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			String sip = IpMacUtil.getIpAddr(request);
			pendingDelegationCacheService.insertCacheAndChangeAccount(partition, bqTransaction.getCurrency_type_id(), cache, fee, bqContract, true, Contents.DEVICE_PC, sip);
			WebSocket.addNPendingToMemcached(partition, cache);
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));
		} catch (ServiceException e) {
			logger.info(e.getMessage());
			checkVO.setCode("error");
			checkVO.setMsg(e.getMessage());
			e.printStackTrace();
		}
		
		toWeb(response, gson.toJson(checkVO));
	}

	/**
	 * 针对做多仓 进行平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePDC")
	public void tradePDC(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String orderTypeStr= request.getParameter("orderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String transSelect = request.getParameter("code");
		long mtid = Convert.toLong(request.getParameter("id"), 0l);

		if (Convert.toDouble(amount, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.rightAmount", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(orderTypeStr) && Convert.toDouble(price, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.rightPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(transSelect) || mtid <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, transSelect);
		if (bqTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getSuspend()) {
			logger.info("交易暂停：" + bqTransaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getNMarketOrderPrice(partition, bqTransaction, Contents.DIRECTION_SELL);
		}

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));
		
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal amount_client = new BigDecimal(0);
		BigDecimal price_client = new BigDecimal(0);
		try {
			amount_client = new BigDecimal(amount).setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
			price_client = new BigDecimal(price).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
			if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO  + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NMemberTransaction buyMemberTransaction = bqMemberTransactionService.get(partition, mtid);
		if (buyMemberTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqTransaction.getTransaction_code().equals(buyMemberTransaction.getTransaction_code())) {
			checkVO.setCode("error");
			checkVO.setMsg("合约名称与持仓不匹配");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getF_user_id() != user.getF_user_id()) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getFlag() != 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.operationFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getTrade_type_id() != Contents.DIRECTION_BUY) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (buyMemberTransaction.getHold_count().signum() <= 0) {
			logger.info("持仓数量须大于0");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.holdCountGtZero", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (amount_client.compareTo(buyMemberTransaction.getHold_count()) > 0) {
			logger.info("平仓数量不能大于持仓量");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.liquidationPriceLtHoldPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
//				if (oneDayMin.compareTo(minIndexPrice) > 0) {
					minIndexPrice = oneDayMin;
//				}
			}
			
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
//			}
		}
		
		if (price_client.compareTo(minIndexPrice) < 0) {
			if (orderType == 1) {
				price_client = minIndexPrice;
			} else {
				logger.info("价格不能低于" + minIndexPrice);
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.sellPrice", Contents.Local.LOCALE.get()) + minIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 爆仓价
		NAccountContract account = bqAccountContractService.readBTCByUidCode(partition, buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, buyMemberTransaction.getAverage_price(), deposit1, feeRate, 
				buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) < 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.UP);
				price_client = bcj;
			} else {
				logger.info("价格不能低于爆仓价：" + bcj);
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceCantLtBaoPrice", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		BigDecimal multiple = buyMemberTransaction.getTrade_multiple();
		BigDecimal deposit = buyMemberTransaction.getHold_count().divide(buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
				.multiply(buyMemberTransaction.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (bqMemberTransactionService.lockToTrade(partition, mtid)) {
				try {
					String sip = IpMacUtil.getIpAddr(request);
					pendingDelegationCacheService.insertPCD(partition, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
					toWeb(response, gson.toJson(checkVO));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.tryAgain", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				} finally {
					bqMemberTransactionService.unlockToTrade(partition, mtid);
				}
			} else {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.tryAgain", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
	}

	//平仓计划委托
	@RequestMapping("/orderPlanPendingPC")
	public void orderPlanPendingDC(@PathVariable("partition") String partition, HttpServletRequest request, Model model, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));

		String orderTypeStr= request.getParameter("planOrderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String amount = request.getParameter("amount");
		long transaction_member_id = Convert.toLong(request.getParameter("transactionMemberId"), 0);
		String price = request.getParameter("planPrice");//委托价
		String triPrice = request.getParameter("planTriPrice");//触发价
		String transSelect = request.getParameter("planCode");
		String multiple = request.getParameter("planMultiple");
		int direction = Convert.toInt(request.getParameter("planDirection"), 0);
		if (direction != Contents.DIRECTION_BUY && direction != Contents.DIRECTION_SELL) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		//参数验证
		if (Convert.toDouble(amount, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.rightAmount", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(orderTypeStr) && Convert.toDouble(price, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.rightPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(transSelect)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		//登录验证
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		} else {
			if (bqs.getF_user_safetype() == 0) {
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, transSelect);
		if (bqTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (bqTransaction.getSuspend()) {
			logger.info("交易暂停：" + bqTransaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		Long uid = user.getF_user_id();

		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		int orderType = Convert.toInt(orderTypeStr, 0);
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(partition, bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}

		BigDecimal amount_client = new BigDecimal(0);
		BigDecimal price_client = new BigDecimal(0);
		BigDecimal triPrice_client = new BigDecimal(0);

		try {
			double damount = ConvertUtil.toDouble(amount, 0);
			double dTriPrice = ConvertUtil.toDouble(triPrice, 0);
			if (damount <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} else if (dTriPrice <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.triPriceError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			amount_client = new BigDecimal(damount).setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
			triPrice_client = new BigDecimal(dTriPrice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (orderType == 0) {	//限价委托委托价操作，若是市价委托委托价为0
			try {
				double dprice = ConvertUtil.toDouble(price, 0);
				if (dprice <= 0) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.openPriceError", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				price_client = new BigDecimal(dprice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
			} catch (Exception e) {
				logger.info("价格或数量不为数字");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		NMemberTransaction bqt = bqMemberTransactionService.readById(partition, transaction_member_id);
		if (transaction_member_id > 0 && bqt == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0 && !bqTransaction.getTransaction_code().equals(bqt.getTransaction_code())) {
			checkVO.setCode("error");
			checkVO.setMsg("合约名称与持仓不匹配");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0 && bqt.getF_user_id() != user.getF_user_id()) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0 && bqt.getFlag() != 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.operationFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0 && ((direction == Contents.DIRECTION_BUY && bqt.getTrade_type_id() != Contents.DIRECTION_SELL)
				|| (direction == Contents.DIRECTION_SELL && bqt.getTrade_type_id() != Contents.DIRECTION_BUY))) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0 && bqt.getHold_count().signum() <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.holdCountGtZero", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (transaction_member_id > 0 && amount_client.compareTo(bqt.getHold_count()) > 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.liquidationPriceLtHoldPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (transaction_member_id > 0) {	//有持仓时，判断爆仓价格
			BQMemberLevel userlevel = getLevelByUserBfx(user);
			BigDecimal feeRate = userlevel.getEat_commission_rate();
			NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
			if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
				feeRate = transactionFeeRate.getEat_commission_rate();
			}

			// 爆仓价

			NAccountContract account = bqAccountContractService.readBTCByUidCode(partition, bqt.getF_user_id(), bqt.getTransaction_code());
			BigDecimal deposit1 = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain())
					.add(bqt.getF_min_frozenremain()).add(account.getF_btc());
			if (direction == Contents.DIRECTION_BUY) {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (price_client.compareTo(bcj) > 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.DOWN);
						price_client = bcj;
					} else {
						logger.info("价格不能高于爆仓价：" + bcj);
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.priceCantGtBaoPrice", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				}
			} else {

				BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, bqt.getAverage_price(), deposit1, feeRate,
						bqt.getHold_count().add(bqt.getLock_count()), bqTransaction.getTrade_coefficient());
				if (price_client.compareTo(bcj) < 0) {
					if (orderType == 1) {
						bcj = bcj.setScale(digits, RoundingMode.UP);
						price_client = bcj;
					} else {
						logger.info("价格不能低于爆仓价：" + bcj);
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.priceCantLtBaoPrice", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				}
			}
		}

		int type = 0;
		if (lastPrice.compareTo(triPrice_client) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}

		NPlanPendingDelegation delegation = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), uid,
				triPrice_client, price_client, amount_client, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-未生效", orderType, 1,transaction_member_id,0);
		NPlanPendingDelegation pd = planPendingDelegationService.insert(partition, delegation);

		if (pd.getId() > 0) {
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));
		}

		toWeb(response, gson.toJson(checkVO));

	}

	/**
	 * 针对做空仓 进行平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePKC")
	public void tradePKC(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String orderTypeStr= request.getParameter("orderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String transSelect = request.getParameter("code");
		long mtid = Convert.toLong(request.getParameter("id"), 0l);

		if (Convert.toDouble(amount, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.rightAmount", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(orderTypeStr) && Convert.toDouble(price, 0) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText ("msg.trade.request.rightPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(transSelect) || mtid <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, transSelect);
		if (bqTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getSuspend()) {
			logger.info("交易暂停：" + bqTransaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getNMarketOrderPrice(partition, bqTransaction, Contents.DIRECTION_BUY);
		}

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal amount_client = new BigDecimal(0);
		BigDecimal price_client = new BigDecimal(0);
		try {
			amount_client = new BigDecimal(amount).setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
			price_client = new BigDecimal(price).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
			if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NMemberTransaction buyMemberTransaction = bqMemberTransactionService.get(partition, mtid);// user.getF_user_id(),
		if (buyMemberTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqTransaction.getTransaction_code().equals(buyMemberTransaction.getTransaction_code())) {
			checkVO.setCode("error");
			checkVO.setMsg("合约名称与持仓不匹配");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getF_user_id() != user.getF_user_id()) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getFlag() != 0) {
			checkVO.setCode("error");
			checkVO.setMsg("操作失败");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.operationFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (buyMemberTransaction.getTrade_type_id() != Contents.DIRECTION_SELL) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (buyMemberTransaction.getHold_count().signum() <= 0) {
			logger.info("持仓数量须大于0");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.holdCountGtZero", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (amount_client.compareTo(buyMemberTransaction.getHold_count()) > 0) {
			logger.info("平仓数量不能大于持仓量");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.liquidationPriceLtHoldPrice", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
//				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
//				}
			}
			
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
		}
		
		if (price_client.compareTo(maxIndexPrice) > 0) {
			if (orderType == 1) {
				price_client = maxIndexPrice;
			} else {
				logger.info("价格不能高于" + maxIndexPrice);
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.buyPriceCantGt", Contents.Local.LOCALE.get()) + maxIndexPrice);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 爆仓价
		NAccountContract account = bqAccountContractService.readBTCByUidCode(partition, buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, buyMemberTransaction.getAverage_price(), deposit1, feeRate, 
				buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
		if (price_client.compareTo(bcj) > 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.DOWN);
				price_client = bcj;
			} else {
				logger.info("价格不能高于爆仓价：" + bcj);
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceCantGtBaoPrice", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		BigDecimal multiple = buyMemberTransaction.getTrade_multiple();
		BigDecimal deposit = buyMemberTransaction.getHold_count().divide(buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
				.multiply(buyMemberTransaction.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!bqMemberTransactionService.lockToTrade(partition, mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				pendingDelegationCacheService.insertPCK(partition, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
				toWeb(response, gson.toJson(checkVO));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, mtid);
			}
		}
	}

	/**
	 * 一键平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePC")
	public void tradePC(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String symbol = request.getParameter("code");
		long mtid = Convert.toLong(request.getParameter("id"), 0l);

		if (StringUtil.isEmpty(symbol) || mtid <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NTransaction bqTransaction = super.findNCurrTrans(partition, symbol);
		if (bqTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getSuspend()) {
			logger.info("交易暂停：" + bqTransaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(partition, bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NMemberTransaction memberTransaction = bqMemberTransactionService.get(partition, mtid);// user.getF_user_id(),
		if (memberTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!bqTransaction.getTransaction_code().equals(memberTransaction.getTransaction_code())) {
			checkVO.setCode("error");
			checkVO.setMsg("合约名称与持仓不匹配");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (memberTransaction.getF_user_id() != user.getF_user_id()) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (memberTransaction.getFlag() != 0) {
			checkVO.setCode("error");
			checkVO.setMsg("操作失败");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.operationFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (memberTransaction.getHold_count().signum() <= 0) {
			logger.info("持仓数量须大于0");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.holdCountGtZero", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int direction = Contents.DIRECTION_SELL;
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL) {
			direction = Contents.DIRECTION_BUY;
		}

		String price = getNMarketOrderPrice(partition, bqTransaction, direction);

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal amount_client = memberTransaction.getHold_count().setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
		BigDecimal price_client = new BigDecimal(price).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
		} else if (bqTransaction.getTransaction_kind() == 2) {
//			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code());
//			if (btc_index_price == null) {
//				btc_index_price = tradeLogService.readLastHourAgoByCode(partition, bqTransaction.getTransaction_code());
//				if (btc_index_price != null) {
//					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_HOUR_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
//				}
//			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.readByCodeAndLevel(partition, bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		NTransactionRate index_price_rate = bqTransactionRateService.getByCode(partition, bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = tradeLogService.readLastDayAgoByCode(partition, bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.LAST_PRICE_ONE_DAY_AGO + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
//				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
//				}
			}
			
//			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqTransaction.getTransaction_code());
//			if (openPrice != null) {
//				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
//			}
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		NAccountContract account = bqAccountContractService.readBTCByUidCode(partition, memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
		BigDecimal deposit1 = memberTransaction.getDeposit().add(memberTransaction.getDeposit2()).add(memberTransaction.getF_fromremain()).add(memberTransaction.getF_frozenremain())
				.add(memberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(memberTransaction.getTrade_type_id(), memberTransaction.getAverage_price(), deposit1, feeRate, 
				memberTransaction.getHold_count().add(memberTransaction.getLock_count()), bqTransaction.getTrade_coefficient());
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(bcj) < 0) {
			bcj = bcj.setScale(digits, RoundingMode.UP);
			price_client = bcj;
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(bcj) > 0) {
			bcj = bcj.setScale(digits, RoundingMode.DOWN);
			price_client = bcj;
		}

		BigDecimal multiple = memberTransaction.getTrade_multiple();
		BigDecimal deposit = memberTransaction.getHold_count().divide(memberTransaction.getHold_count().add(memberTransaction.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
				.multiply(memberTransaction.getDeposit());

		NPendingDelegationCache cahce = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!bqMemberTransactionService.lockToTrade(partition, mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
					pendingDelegationCacheService.insertPCD(partition, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
				} else {
					pendingDelegationCacheService.insertPCK(partition, cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
				}
				toWeb(response, gson.toJson(checkVO));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} finally {
				bqMemberTransactionService.unlockToTrade(partition, mtid);
			}
		}
	}

	@RequestMapping("/cancleDeal")
	public void cancleDeal(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String direction = request.getParameter("direction");
		String code = request.getParameter("code");

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		int idirection = ConvertUtil.toInt(direction, -1);
		if (idirection >= 0) {
			if ("all".equalsIgnoreCase(code)) {
				List<NTransaction> transactionlist = super.findNCurrTrans(partition);
				for (NTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					cancelPendingWithCode(code, idirection, user.getF_user_id(), bqt.getTrade_coefficient());
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				NTransaction bqt = super.findNCurrTrans(partition, code);
				if (bqt != null) {
					cancelPendingWithCode(code, idirection, user.getF_user_id(), bqt.getTrade_coefficient());
				}
			}
		}
	}

	@RequestMapping("/canclePendingAnsyc")
	public void cancleNPendingDelegationAnsyc(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		Gson gson = new Gson();
		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		String cache = request.getParameter("cache");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NTransaction bqt = super.findNCurrTrans(partition, code);
		if (bqt == null) {
			checkVO.setCode("error");
			checkVO.setMsg("error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		boolean isCache = false;
		if (!StringUtil.isEmpty(cache)) {
			isCache = Boolean.valueOf(cache);
		}
		
		boolean done = false;
		if (isCache) {
			NPendingDelegationCache pd = pendingDelegationCacheService.readPendingByPendingNo(partition, pdId, code, user.getF_user_id());
			if (pd != null) done = true;
			
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationCacheService.lock(partition, code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							NMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(partition, code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationCacheService.cancelPendingCache_one(partition, bqt.getCurrency_type_id(), pd, bqt.getTrade_coefficient(), pd.getCommission_rate().signum() > 0) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								pendingDelegationCacheService.unLock(partition, code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		}
		
		if (!done) {
			NPendingDelegation pd = pendingDelegationService.readPendingByPendingNo(partition, pdId, code, user.getF_user_id());
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationService.lock(partition, code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							NMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(partition, code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationService.cancelPending_one(partition, bqt.getCurrency_type_id(), pd, bqt.getTrade_coefficient(), pd.getCommission_rate().signum() > 0) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								pendingDelegationService.unLock(partition, code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		}

		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
	}

	@RequestMapping("/cancleNPendingDelegation")
	public ModelAndView cancleNPendingDelegation(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		String order = request.getParameter("order");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		NTransaction bqt = super.findNCurrTrans(partition, code);
		if (bqt == null) {
			ModelMap map = new ModelMap();
			map.addAttribute("pendingcode", code);
			map.addAttribute("pendingorder", order);
			map.addAttribute("pendingtype", type);
			return new ModelAndView("redirect:/trade/entrust.do", map);
		}

		NPendingDelegation pd = pendingDelegationService.findPendingById(partition, Long.valueOf(pdId), code);
		if (pd.getF_user_id() == user.getF_user_id()) {
			if (pd.getPriority() != 3) {
				String lk = pendingDelegationService.lock(partition, code, pd.getDelegation_id());
				if (!StringUtil.isEmpty(lk)) {
					try {
						
						boolean lock = true;
						NMemberTransaction trans = null;
						if (pd.getPriority() == 2) {
							int tradeType = Contents.TRADE_TYPE_BUY;
							
							if (pd.getDirection() == Contents.DIRECTION_BUY) {
								tradeType = Contents.TRADE_TYPE_SELL;
							}
							
							trans = bqMemberTransactionService.readMemberTransaction(partition, code, pd.getF_user_id(), tradeType, pd.getMultiple());
							lock = bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id());
						}
						
						if (lock) {
							try {
								pendingDelegationService.cancelPending_one(partition, bqt.getCurrency_type_id(), pd, bqt.getTrade_coefficient(), pd.getCommission_rate().signum() > 0);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pd.getPriority() == 2 && trans != null) {
									bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							pendingDelegationService.unLock(partition, code, pd.getDelegation_id(), lk);
						}
					}
				}
			}
		}

		ModelMap map = new ModelMap();
		map.addAttribute("pendingcode", code);
		map.addAttribute("pendingorder", order);
		map.addAttribute("pendingtype", type);
		return new ModelAndView("redirect:/trade/entrust.do", map);
	}
	
	@RequestMapping("/orderPlanPending")
	public void orderPlanPending(@PathVariable("partition") String partition, HttpServletRequest request, Model model, HttpServletResponse response) throws ParseException {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		String msg = UrlConfig.getValue("trademsg");
		if (!StringUtil.isEmpty(msg)) {
			checkVO.setCode("error");
			checkVO.setMsg(msg);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 会话验证
		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));

		String orderTypeStr= request.getParameter("planOrderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String amount = request.getParameter("planAmount");
		String price = request.getParameter("planPrice");//委托价
		String triPrice = request.getParameter("planTriPrice");//触发价
		String transSelect = request.getParameter("planCode");
		String multiple = request.getParameter("planMultiple");
		String payPwd = request.getParameter("planPayPwd");
		int direction = Convert.toInt(request.getParameter("planDirection"), 0);
		if (direction != Contents.DIRECTION_BUY && direction != Contents.DIRECTION_SELL) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 参数验证
		final BigDecimal ZERO = BigDecimal.ZERO;
		if (StringUtil.isEmpty(amount) || StringUtil.isEmpty(transSelect) || StringUtil.isEmpty(multiple) || StringUtil.isEmpty(triPrice)) {
			logger.info("必选参数错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(price)) {
			logger.info("必选参数错误,price错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 登录验证
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		} else {
			if (bqs.getF_user_safetype() == 0) {
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查 -- 交易密码验证
		Long uid = user.getF_user_id();
		boolean bol_paypwd = false;
		BQMemberPayword payword = bqMemberPaywordService.readById(uid);
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
			logger.info("交易密码为空或者错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NTransaction bqTransaction = yzgg(partition, transSelect, checkVO, multiple, uid);
		if (bqTransaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		// 开仓数量及价格验证
		BigDecimal amount_client = ZERO;
		BigDecimal price_client = ZERO;
		BigDecimal triPrice_client = ZERO;
		try {
			double damount = ConvertUtil.toDouble(amount, 0);
			double dTriPrice = ConvertUtil.toDouble(triPrice, 0);
			if (damount <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} else if (dTriPrice <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.triPriceError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			amount_client = new BigDecimal(damount).setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
			triPrice_client = new BigDecimal(dTriPrice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (orderType == 0) {
			try {
				double dprice = ConvertUtil.toDouble(price, 0);
				if (dprice <= 0) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.openPriceError", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				price_client = new BigDecimal(dprice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
			} catch (Exception e) {
				logger.info("价格或数量不为数字");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		int type = 0;
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(partition, bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice_client) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		NPlanPendingDelegation delegation = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), uid,
				triPrice_client, price_client, amount_client, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-未生效", orderType);
		
		NPlanPendingDelegation pd = planPendingDelegationService.insert(partition, delegation);
		
		if (pd.getId() > 0) {
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));
		}
		
		toWeb(response, gson.toJson(checkVO));
	}
	
	@RequestMapping("/canclePlanDeal")
	public void canclePlanDeal(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String direction = request.getParameter("direction");
		String code = request.getParameter("code");

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		int idirection = ConvertUtil.toInt(direction, -1);
		if (idirection >= 0) {
			if ("all".equalsIgnoreCase(code)) {
				List<NTransaction> transactionlist = super.findNCurrTrans(partition);
				for (NTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					List<NPlanPendingDelegation> pdList = planPendingDelegationService.readByUserIdDirection(partition, idirection,
							code, user.getF_user_id());
					for (NPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelNPlanPending(partition, pd);
					}
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				NTransaction bqt = super.findNCurrTrans(partition, code);
				if (bqt != null) {
					List<NPlanPendingDelegation> pdList = planPendingDelegationService.readByUserIdDirection(partition, idirection,
							code, user.getF_user_id());
					for (NPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelNPlanPending(partition, pd);
					}
				}
			}
		}
	}

	@RequestMapping("/canclePlanPendingAnsyc")
	public void canclePlanPendingAnsyc(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		AjaxCheckVO checkVO = new AjaxCheckVO();
		Gson gson = new Gson();
		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NTransaction bqt = super.findNCurrTrans(partition, code);
		if (bqt == null) {
			checkVO.setCode("error");
			checkVO.setMsg("error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		long id = Convert.toLong(pdId, -1);
		if (id < 0) {
			checkVO.setCode("error");
			checkVO.setMsg("pdId error");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NPlanPendingDelegation pd = planPendingDelegationService.readById(partition, id, code, user.getF_user_id());
		if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
			checkVO.setCode("error");
			checkVO.setMsg("Canceled or Effective");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int ret = cancelNPlanPending(partition, pd);
		if (ret > 0) {
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderFail", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
	}

	/*
	@RequestMapping("/depositOnOff")
	public void depositOnOff(HttpServletRequest request, HttpServletResponse response) {
		String status = request.getParameter("status");
		int istat = Integer.valueOf(status);
		if (istat != 0 && istat != 1) {
			return;
		}

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}

		if (!super.checkToken(request)) {
			return;
		}

		BQMemberDepositOnoff depositOnOff = bqMemberDepositOnoffService.getByUserId(user.getF_user_id());
		depositOnOff.setValue(istat);
		bqMemberDepositOnoffService.update(depositOnOff);
	}
	*/

	/** 获得不同合约围绕价格指数的上下波动限制 */
	@RequestMapping("/getTransactionCodeRate")
	public void getTransactionCodeRate(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		List<NTransactionRate> list = bqTransactionRateService.readList(partition);
		Gson gson = new Gson();
		toWeb(response, gson.toJson(list));
	}

	/**
	 * 异步请求获取常用数据
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/getCommonData")
	public void findCommonData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		String bqCode = request.getParameter("bqCode");
		if (StringUtil.isEmpty(bqCode)) { // 传递合约号失败
			return;
		}
		
		TradeAjaxVO vo = spyMemcachedClient.get(BTCQMemecacheKeys.COMMON_DATA + partition + "_" + bqCode);
		toWeb(response, new Gson().toJson(vo));
	}
	
	@RequestMapping("/knowRiskWarning")
	public void knowRiskWarning(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (user.getHas_open_position() == 0) {
			bqMemberService.updateHasOpen(user.getF_user_id());
		}
		
		checkVO.setCode("success");
		checkVO.setMsg("提交成功");
		
		toWeb(response, new Gson().toJson(checkVO));
	}

	@RequestMapping("/getBPIData")
	public void getBPIData(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		String bpiData = spyMemcachedClient.get(BTCQMemecacheKeys.BPI_DATA + "_" + partition);
		if (bpiData == null) {
			bpiData = "[]";
		}
		
		JSONArray array = new JSONArray(bpiData);
		
		toWeb(response, array);
	}
	
	@RequestMapping("/getMemberTransaction")
	public void getMemberTransaction(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject json = new JSONObject();
		json.put("code", "success");
		json.put("msg", "");
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			json.put("code", "error");
			json.put("msg", SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, json);
			return;
		}

		String multiple = request.getParameter("multiple");
		String code = request.getParameter("code");
		if (StringUtil.isEmpty(code) || StringUtil.isEmpty(multiple)) {
			json.put("code", "error");
			json.put("msg", SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, json);
			return;
		}
		
		JSONArray array = new JSONArray();
		List<NMemberTransaction> list = bqMemberTransactionService.readMemberTransaction(partition, code, user.getF_user_id(), new BigDecimal(multiple));
		if (list.size() > 0) {
			for (NMemberTransaction memberTrans : list) {
				MemberTransactionVo vo = new MemberTransactionVo();
				vo.setPositionId(memberTrans.getMt_id() + "");
				vo.setSymbol(memberTrans.getTransaction_code());
				vo.setDirection(memberTrans.getTrade_type_id());
				vo.setMultiple(memberTrans.getTrade_multiple().intValue());
				vo.setHoldCount(memberTrans.getHold_count().doubleValue());
				vo.setLockCount(memberTrans.getLock_count().doubleValue());
				vo.setAvgPrice(memberTrans.getAverage_price().doubleValue());
				vo.setDeposit(memberTrans.getDeposit().doubleValue());
				
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(vo)));
			}
		}
		
		json.put("data", array);
		toWeb(response, json);
	}

	@RequestMapping("/stopLossClick")
	public void stopLossClick(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
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

		String transaction_member_id = request.getParameter("transaction_member_id");
		List<NPlanPendingDelegation> delegations = new ArrayList<>();
		NMemberTransaction nMemberTransaction = bqMemberTransactionService.readById(partition, ConvertUtil.toLong(transaction_member_id, new Long(0)));
		if (null != nMemberTransaction && nMemberTransaction.getMt_id() > 0) {
			delegations = planPendingDelegationService.readWinAndLossByUserIdStatusAndMId(partition, nMemberTransaction.getTransaction_code(), nMemberTransaction.getF_user_id(), Contents.PLAN_PENDING_STATUS_INEFFECTIVE, nMemberTransaction.getMt_id());
		}

		List<PlanPendingDelegationVO> stopLossVOS = new ArrayList<>();
		for (NPlanPendingDelegation pd : delegations) {
			PlanPendingDelegationVO stopLoss = new PlanPendingDelegationVO();
			stopLoss.setDelegation_id(pd.getId());
			stopLoss.setSymbol(pd.getF_code());
			stopLoss.setAmount(pd.getDelegation_count().doubleValue());
			stopLoss.setPrice(pd.getDelegation_price().doubleValue());
			stopLoss.setTriPrice(pd.getTri_price().doubleValue());
			stopLoss.setCreateTime(pd.getDelegation_time());
			stopLoss.setMultiple(pd.getMultiple());
			stopLoss.setDirection(pd.getDirection());
			stopLoss.setStatus(pd.getStatus());
			stopLoss.setNote(pd.getNote());
			stopLoss.setType(pd.getType());
			stopLoss.setTrade_type(pd.getOrder_type());
			stopLoss.setStop_loss_type_id(pd.getStop_loss_type_id());

			stopLossVOS.add(stopLoss);
		}

		List<NStopLoss> lossList = bqStopLossService.readStopLossList(partition, user.getF_user_id(), Long.valueOf(transaction_member_id));
		if (lossList.size() > 0) {
			List<PlanPendingDelegationVO> delegationVOS = new ArrayList<>();
			for (NStopLoss bqStopLoss : lossList) {
				PlanPendingDelegationVO stopLoss = new PlanPendingDelegationVO();
				stopLoss.setDelegation_id(bqStopLoss.getId());
				stopLoss.setSymbol(bqStopLoss.getTransaction_code());
				stopLoss.setAmount(bqStopLoss.getPending_amount().doubleValue());
				stopLoss.setPrice(bqStopLoss.getPending_trans_price().doubleValue());
				stopLoss.setTriPrice(bqStopLoss.getStop_loss_price().doubleValue());
				stopLoss.setCreateTime(bqStopLoss.getDateline());
				stopLoss.setMultiple(0);
				stopLoss.setDirection(bqStopLoss.getTrade_type_id());
				stopLoss.setStatus(Contents.PLAN_PENDING_STATUS_INEFFECTIVE);
				stopLoss.setNote("");
				stopLoss.setType(0);
				stopLoss.setTrade_type(bqStopLoss.getOrder_type());
				stopLoss.setStop_loss_type_id(bqStopLoss.getStop_loss_type_id());

				delegationVOS.add(stopLoss);
			}
			toWeb(response, gson.toJson(delegationVOS));
		}
		toWeb(response, gson.toJson(stopLossVOS));
	}

	@RequestMapping("/stopLossChange")
	public void stopLossChange(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		// 校验
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String orderTypeStr= request.getParameter("orderType"); // 下单类型（0或者空：限价平仓;1：市价平仓）
		String pending_amount = request.getParameter("pending_amount");
		String transaction_member_id = request.getParameter("transaction_member_id");
		String has_win = request.getParameter("has_win");
		String stop_win_price = request.getParameter("stop_win_price");
		String pending_win_price = request.getParameter("pending_win_price");

		String has_loss = request.getParameter("has_loss");
		String stop_loss_price = request.getParameter("stop_loss_price");
		String pending_loss_price = request.getParameter("pending_loss_price");

		checkVO.setCode("success");
		checkVO.setMsg("提交成功");
		NMemberTransaction bqt = bqMemberTransactionService.readById(partition, Long.valueOf(transaction_member_id));
		if (bqt == null) {
			logger.info("持仓不存在");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (bqt.getF_user_id() != user.getF_user_id()) {
			logger.info("会话已过期");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (("1".equalsIgnoreCase(has_win) || "1".equalsIgnoreCase(has_loss)) && StringUtil.isEmpty(pending_amount)) {
			logger.info("挂单数量错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		NTransaction bqTransaction = super.findNCurrTrans(partition, bqt.getTransaction_code());
		NTransactionType bqTransactionType = transTypeService.readTransactionType(partition, bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if(lastPrice == null){
			lastPrice = transactionService.readTransByCode(partition, bqt.getTransaction_code()).getLatest_transaction_price();
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal stop_win_price_client = new BigDecimal(0);
		BigDecimal pending_win_price_client = new BigDecimal(0);
		BigDecimal stop_loss_price_client = new BigDecimal(0);
		BigDecimal pending_loss_price_client = new BigDecimal(0);
		
		BigDecimal pending_amount_client = new BigDecimal(pending_amount);
		if (!StringUtil.isEmpty(pending_amount) && pending_amount_client.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("挂单数量错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (!StringUtil.isEmpty(pending_amount) && pending_amount_client.compareTo(bqt.getHold_count()) > 0) {
			logger.info("挂单数量不能大于持仓量");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		NPlanPendingDelegation delegationWin = null;
		NPlanPendingDelegation delegationLoss = null;
		
		NAccountContract bqAccount = bqAccountContractService.readBTCByUidCode(partition, bqt.getF_user_id(), bqt.getTransaction_code());
		if (bqt.getTrade_type_id() == 1) {	// 多仓
			if (!StringUtil.isEmpty(has_win) && "1".equalsIgnoreCase(has_win)) {	// 多仓止盈
				try {
					stop_win_price_client = new BigDecimal(stop_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
					if (orderType == 0) {
						pending_win_price_client = new BigDecimal(pending_win_price).setScale(digits, BigDecimal.ROUND_DOWN);
					}
					if (stop_win_price_client.signum() <= 0
							|| (orderType == 0 && pending_win_price_client.signum() <= 0)) {
						throw new RuntimeException("价格不能为非正数");
					}
				} catch (Exception e) {
					logger.info("价格不为数字");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.pricenotNumber", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (stop_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
					logger.info("委托价须大于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.entrustedPriceGtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (orderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) <= 0) {
					logger.info("多仓止盈价须大于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceGtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				int typeWin = 0;
				if (lastPrice.compareTo(stop_win_price_client) >= 0) {
					typeWin = Contents.PLAN_PENDING_TYPE_GT;
				} else {
					typeWin = Contents.PLAN_PENDING_TYPE_LT;
				}

				delegationWin = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
						stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeWin,
						Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止盈计划委托挂单创建-未生效", orderType, 1, (int) bqt.getMt_id(), 1);

			} else if ("0".equalsIgnoreCase(has_win)) {

			} else {
				logger.info("参数标识不正确");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.paramsIncorrect", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {	// 多仓止损
				try {
					stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
					if (orderType == 0) {
						pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
					}
					if (stop_loss_price_client.signum() <= 0
							|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.priceError", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				} catch (Exception e) {
					logger.info("价格不为数字");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.pricenotNumber", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (stop_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
					logger.info("委托价须小于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.entrustedPriceLtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (orderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) >= 0) {
					logger.info("多仓止损价须小于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceLtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
						.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
				BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
				BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
						bqTransaction.getTrade_coefficient(), bqt.getDeposit());
				baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
				if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
						|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
					logger.info("多仓止损价须大于爆仓价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceGtBaoPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				int typeLoss = 0;
				if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
					typeLoss = Contents.PLAN_PENDING_TYPE_GT;
				} else {
					typeLoss = Contents.PLAN_PENDING_TYPE_LT;
				}

				delegationLoss = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
						stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_SELL, typeLoss,
						Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "用户止损计划委托挂单创建-未生效", orderType, 1, (int) bqt.getMt_id(), 2);

			} else if ("0".equalsIgnoreCase(has_loss)) {

			} else {
				logger.info("参数标识不正确");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.paramsIncorrect", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
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
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.priceError", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
						return;
					}
				} catch (Exception e) {
					logger.info("价格不为数字");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.pricenotNumber", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (stop_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
					logger.info("委托价须小于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.entrustedPriceLtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (orderType == 0 && pending_win_price_client.compareTo(bqt.getAverage_price()) >= 0) {
					logger.info("空仓止盈价须小于持仓均价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceLtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				int typeWin = 0;
				if (lastPrice.compareTo(stop_win_price_client) >= 0) {
					typeWin = Contents.PLAN_PENDING_TYPE_GT;
				} else {
					typeWin = Contents.PLAN_PENDING_TYPE_LT;
				}

				delegationWin = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
						stop_win_price_client, pending_win_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeWin,
						Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止盈计划委托挂单创建-未生效", orderType, 1, (int) bqt.getMt_id(), 1);

			} else if ("0".equalsIgnoreCase(has_win)) {

			} else {
				logger.info("参数标识不正确");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.paramsIncorrect", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			if (!StringUtil.isEmpty(has_loss) && "1".equalsIgnoreCase(has_loss)) {// 空仓止损
				try {
					stop_loss_price_client = new BigDecimal(stop_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
					if (orderType == 0) {
						pending_loss_price_client = new BigDecimal(pending_loss_price).setScale(digits, BigDecimal.ROUND_DOWN);
					}
					if (stop_loss_price_client.signum() <= 0
							|| (orderType == 0 && pending_loss_price_client.signum() <= 0)) {
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.priceError", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
					}
				} catch (Exception e) {
					logger.info("价格不为数字");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.pricenotNumber", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (stop_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
					logger.info("委托价须大于持仓均价价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.entrustedPriceGtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				if (orderType == 0 && pending_loss_price_client.compareTo(bqt.getAverage_price()) <= 0) {
					logger.info("空仓止损价须大于持仓均价价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceGtAveragePrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
						.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
				BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
				BigDecimal baocangPrice =  BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
						bqTransaction.getTrade_coefficient(), bqt.getDeposit());
				baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
				if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) >= 0)
						|| stop_loss_price_client.compareTo(baocangPrice) >= 0) {
					logger.info("空仓止损价须小于爆仓价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceLtBaoPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}

				int typeLoss = 0;
				if (lastPrice.compareTo(stop_loss_price_client) >= 0) {
					typeLoss = Contents.PLAN_PENDING_TYPE_GT;
				} else {
					typeLoss = Contents.PLAN_PENDING_TYPE_LT;
				}

				delegationLoss = new NPlanPendingDelegation(bqTransaction.getTransaction_code(), user.getF_user_id(),
						stop_loss_price_client, pending_loss_price_client, pending_amount_client, bqt.getTrade_multiple().intValue(), Contents.DIRECTION_BUY, typeLoss,
						Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "止损计划委托挂单创建-未生效", orderType, 1, (int) bqt.getMt_id(), 2);

			} else if ("0".equalsIgnoreCase(has_loss)) {

			} else {
				logger.info("参数标识不正确");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.paramsIncorrect", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		List<NPlanPendingDelegation> delegations = new ArrayList<>();
		if (null != delegationWin) {
			delegations.add(delegationWin);
		}
		if (null != delegationLoss) {
			delegations.add(delegationLoss);
		}


		if (checkVO.getCode().equalsIgnoreCase("success")) {
			try {
				String lock = spyMemcachedClient.lock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + partition + "_" + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						planPendingDelegationService.updateWinAndLoss(partition, delegations, bqTransaction.getTransaction_code(), user.getF_user_id(), bqt.getMt_id());
					} catch (Exception e) {
						checkVO.setCode("error");
						checkVO.setMsg(SpringContext.getText("msg.trade.request.failProfitAndLoss", Contents.Local.LOCALE.get()));
						toWeb(response, gson.toJson(checkVO));
						return;
					} finally {
						spyMemcachedClient.unlock(BTCQMemecacheKeys.PLAN_PENDING_LOCK + partition + "_" + bqTransaction.getTransaction_code() + "_" + bqt.getMt_id() + "_" + user.getF_user_id(), lock);
					}
				}
			} catch (Exception e) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.failProfitAndLoss", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		if (!StringUtil.isEmpty(has_win) && "0".equalsIgnoreCase(has_win) && !StringUtil.isEmpty(has_loss) && "0".equalsIgnoreCase(has_loss)) {
			checkVO.setCode("success2");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelProfitAndLoss", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		toWeb(response, gson.toJson(checkVO));

	}
	
	@RequestMapping("/addOrUpdateFavorite")
	public void addOrUpdateFavorite(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		String code = request.getParameter("code");
		if (StringUtil.isEmpty(code)) {
			checkVO.setCode("error");
			checkVO.setMsg("合约号为空");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		int iType = 0;
		int direc = ConvertUtil.toInt(request.getParameter("direc"), 0);
		
		NContractPartition part = nContractPartitionService.readByPartition(partition);
		if (part == null) {
			checkVO.setCode("error");
			checkVO.setMsg("合约号错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberFavoriteMarket market = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), partition);
		if (market == null) {
			if (direc == 2) {
				checkVO.setCode("error");
				checkVO.setMsg("不存在该记录无法删除");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			market = new BQMemberFavoriteMarket();
			market.setF_user_id(user.getF_user_id());
			market.setCodes(code);
			market.setType(iType);
			market.setPart(partition);
			bqMemberFavoriteMarketService.save(market);
		} else {
			String codes = market.getCodes();
			String[] codeArr = codes.split(",");
			
			List<String> listCodes = new ArrayList<String>();
			for (int i = 0; i < codeArr.length; i++) {
				listCodes.add(codeArr[i]);
			}
			
			if (direc == 1) {
				if (codes.contains(code)) {
					checkVO.setCode("error");
					checkVO.setMsg("已收藏过该合约(或交易对)");
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				listCodes.add(code);
			} else {
				if (!codes.contains(code)) {
					checkVO.setCode("error");
					checkVO.setMsg("未收藏过该合约(或交易对)");
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				int index = listCodes.size();
				for (int i = 0; i < listCodes.size(); i++) {
					if (code.equals(listCodes.get(i))) {
						index = i;
					}
				}
				listCodes.remove(index);
			}
			codes = StringUtils.join(listCodes, ",");
			market.setCodes(codes);
			bqMemberFavoriteMarketService.update(market);
		}
		
		checkVO.setCode("success");
		checkVO.setMsg("提交成功");
		
		toWeb(response, new Gson().toJson(checkVO));
	}
	
	@RequestMapping("/getFavorite")
	public void getFavorite(@PathVariable("partition") String partition, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		AjaxCheckVO checkVO = new AjaxCheckVO();
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, new Gson().toJson(checkVO));
			return;
		}
		
		NContractPartition part = nContractPartitionService.readByPartition(partition);
		if (part == null) {
			checkVO.setCode("error");
			checkVO.setMsg("合约号错误");
			toWeb(response, new Gson().toJson(checkVO));
			return;
		}
	
		BQMemberFavoriteMarket market = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), partition);
		JSONObject dataJson = new JSONObject();
		dataJson.put("code", "success");
		dataJson.put("msg", "");
		dataJson.put("data", new Gson().toJson(market));
		
		toWeb(response, dataJson);
	}
}
