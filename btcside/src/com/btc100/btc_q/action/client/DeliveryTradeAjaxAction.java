package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPlanPendingDelegation;
import com.btc100.btc_q.entity.DeliveryStopLoss;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPlanPendingDelegationService;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionFeeRateService;
import com.btc100.btc_q.service.DeliveryTransactionRateService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.MemberTransactionVo;
import com.btc100.btc_q.vo.TradeAjaxVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/dtradeAjax")
public class DeliveryTradeAjaxAction extends BaseTradeAction {

	private static final Logger logger = Logger.getLogger(DeliveryTradeAjaxAction.class);

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryPendingDelegationCacheService deliveryPendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationService deliveryPendingDelegationService;
	@Autowired
	private DeliveryTransactionRateService deliveryTransactionRateService;
	@Autowired
	private DeliveryStopLossService deliveryStopLossService;
	@Autowired
	private DeliveryMemberTransactionService deliveryMemberTransactionService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private DeliveryAccountContractService deliveryAccountContractService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private DeliveryTradeLogService deliveryTradeLogService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private DeliveryPlanPendingDelegationService deliveryPlanPendingDelegationService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private DeliveryTransactionFeeRateService deliveryTransactionFeeRateService;
	
	/**
	 * 新盘首日
	 */
	BigDecimal newTransFirDay = new BigDecimal(0.1);

	private DeliveryTransaction yzgg(String transSelect, AjaxCheckVO checkVO, String multiple, Long uid) {

		DeliveryTransaction transaction = null;
		List<DeliveryTransaction> list = super.findCurrDTrans();
		for (DeliveryTransaction bq : list) {
			if (bq.getTransaction_code().equalsIgnoreCase(transSelect)) {
				transaction = bq;
				break;
			}
		}

		if (transaction == null) {
			logger.info("合约错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		int multiple_client = ConvertUtil.toInt(multiple, 0);
		if (multiple_client <= 0) {
			logger.info("杠杆错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.errorMultiple", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		
		if (transaction.getSuspend()) {
			logger.info("交易暂停：" + transaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			// toWeb(response, gson.toJson(checkVO));
			return null;
		}
		
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(transaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				// toWeb(response, gson.toJson(checkVO));
				return null;
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
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.errorMultiple", Contents.Local.LOCALE.get()));
			return null;
		}
		
		int canMultiple =  getDOneCanMultiple(transaction, uid);
		if (canMultiple > 0 && canMultiple != multiple_client) {
			checkVO.setCode("error");
			String msg = SpringContext.getText("msg.trade.request.canMultiple", Contents.Local.LOCALE.get());
			msg = msg.replace("{0}", "" + canMultiple);
			checkVO.setMsg(msg);
			return null;
		}

		return transaction;
	}

	@RequestMapping("/tradeTrans")
	public void tradeTrans(HttpServletRequest request, Model model, HttpServletResponse response) throws ParseException {
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

		DeliveryTransaction transaction = yzgg(transSelect, checkVO, multiple, uid);
		if (transaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		DeliveryTransactionType transactionType = transTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
		int amountDigits = Utils.getAmountDigtis(transaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getDMarketOrderPrice(transaction, direction);
			if (StringUtil.isEmpty(price)) {
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 开仓数量及价格验证
		BigDecimal amount_client = ZERO;
		BigDecimal price_client = ZERO;
		BigDecimal multiple_client2 = ZERO;
		BigDecimal coefficient = transaction.getTrade_coefficient();
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
		BigDecimal btc_index_price = super.findBPIByCode(transactionType.getType_code());
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
			if (btc_index_price == null) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = deliveryTradeLogService.readLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
			
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = deliveryTradeLogService.readLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
			
		DeliveryTransactionRate index_price_rate = deliveryTransactionRateService.getByCode(transaction.getTransaction_code());
			
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
			
		if (oneDayMin != null && oneDayMin.compareTo(minIndexPrice) > 0) {
			minIndexPrice = oneDayMin;
		}
			
		if (oneDayMax != null && oneDayMax.compareTo(maxIndexPrice) < 0) {
			maxIndexPrice = oneDayMax;
		}
			
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
			
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
		DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(transaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal remainAmount = BigDecimal.ZERO;
		DeliveryAccountContract bqContract = deliveryAccountContractService.readByUidCode(uid, transaction.getTransaction_code());
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
		
		if (amount_client.compareTo(transactionType.getMin_kc_nums()) < 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.minimumOpeningNumber", Contents.Local.LOCALE.get()) + transactionType.getMin_kc_nums() + "个");
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
		
		String ret = deliveryMaxUserKcCheck(transaction.getTransaction_code(), direction, user.getF_user_id(), amount_client);
		if (!StringUtil.isEmpty(ret)) {
			checkVO.setCode("error");
			logger.info(ret);
			checkVO.setMsg(ret);
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!inWhiteList(String.valueOf(uid))) {
			String risk = deliveryRiskCheck(transaction.getTransaction_code(), direction, price_client, amount_client, digits);
			if (!StringUtil.isEmpty(risk)) {
				checkVO.setCode("error");
				logger.info(risk);
				checkVO.setMsg(risk);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			String maxkc = deliveryMaxKcCheck(transactionType, transaction.getTransaction_code(), direction, user.getF_user_id(), amount_client);
			if (!StringUtil.isEmpty(maxkc)) {
				checkVO.setCode("error");
				logger.info(maxkc);
				checkVO.setMsg(maxkc);
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		DeliveryPendingDelegationCache cache = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				direction, amount_client, price_client, BigDecimal.ZERO, amount_client, Integer.valueOf(multiple), deposit,
				feeRate, Contents.PRIORITY_KC, uid, BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			String sip = IpMacUtil.getIpAddr(request);
			deliveryPendingDelegationCacheService.insertCacheAndChangeAccount(cache, fee, bqContract, true, Contents.DEVICE_PC, sip);
			// websocket加入推送
			WebSocket.addDPendingToMemcached(cache);
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
	public void tradePDC(HttpServletRequest request, HttpServletResponse response) throws ParseException {
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

		DeliveryTransaction transaction = super.findCurrDTrans(transSelect);
		if (transaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (transaction.getSuspend()) {
			logger.info("交易暂停：" + transaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(transaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		DeliveryTransactionType transactionType = transTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getDMarketOrderPrice(transaction, Contents.DIRECTION_SELL);
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
		
		int digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
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
		
		BigDecimal btc_index_price = findBPIByCode(transactionType.getType_code());
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO  + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = deliveryTradeLogService.readLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		DeliveryMemberTransaction buyMemberTransaction = deliveryMemberTransactionService.get(mtid);
		if (buyMemberTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!transaction.getTransaction_code().equals(buyMemberTransaction.getTransaction_code())) {
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
		DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(transaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		DeliveryTransactionRate index_price_rate = deliveryTransactionRateService.getByCode(transaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = deliveryTradeLogService.readLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMin = oneDayAgo.subtract(oneDayAgo.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);
				if (oneDayMin.compareTo(minIndexPrice) > 0) {
					minIndexPrice = oneDayMin;
				}
			}
			
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				minIndexPrice = openPrice.subtract(openPrice.multiply(new BigDecimal(0.5))).setScale(digits, RoundingMode.HALF_UP);;
			}
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
		DeliveryAccountContract account = deliveryAccountContractService.readBTCByUidCode(buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, buyMemberTransaction.getAverage_price(), deposit1, feeRate, 
				buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), transaction.getTrade_coefficient());
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

		DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (deliveryMemberTransactionService.lockToTrade(mtid)) {
				try {
					String sip = IpMacUtil.getIpAddr(request);
					deliveryPendingDelegationCacheService.insertPCD(cahce, mtid, transaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
					toWeb(response, gson.toJson(checkVO));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.tryAgain", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				} finally {
					deliveryMemberTransactionService.unlockToTrade(mtid);
				}
			} else {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.tryAgain", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
	}

	/**
	 * 针对做空仓 进行平仓
	 * 
	 * @param request
	 * @param response
	 * @throws ParseException
	 */
	@RequestMapping("/tradePKC")
	public void tradePKC(HttpServletRequest request, HttpServletResponse response) throws ParseException {
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

		DeliveryTransaction transaction = super.findCurrDTrans(transSelect);
		if (transaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (transaction.getSuspend()) {
			logger.info("交易暂停：" + transaction.getTransaction_code());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			if (!bqNotradetimeService.readTradeTime(transaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		DeliveryTransactionType transactionType = transTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getDMarketOrderPrice(transaction, Contents.DIRECTION_BUY);
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

		int digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
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
		
		DeliveryMemberTransaction buyMemberTransaction = deliveryMemberTransactionService.get(mtid);// user.getF_user_id(),
		if (buyMemberTransaction == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.NotFoundPosition", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (!transaction.getTransaction_code().equals(buyMemberTransaction.getTransaction_code())) {
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
		
		BigDecimal btc_index_price = findBPIByCode(transactionType.getType_code());
		if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + transaction.getTransaction_code());
		} else if (transaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = deliveryTradeLogService.readLastHourAgoByCode(transaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(transaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		DeliveryTransactionRate index_price_rate = deliveryTransactionRateService.getByCode(transaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (transaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = deliveryTradeLogService.readLastDayAgoByCode(transaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
				}
			}
			
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + transaction.getTransaction_code());
			if (openPrice != null) {
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
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
		DeliveryAccountContract account = deliveryAccountContractService.readBTCByUidCode(buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, buyMemberTransaction.getAverage_price(), deposit1, feeRate, 
				buyMemberTransaction.getHold_count().add(buyMemberTransaction.getLock_count()), transaction.getTrade_coefficient());
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

		DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!deliveryMemberTransactionService.lockToTrade(mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				deliveryPendingDelegationCacheService.insertPCK(cahce, mtid, transaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
				toWeb(response, gson.toJson(checkVO));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} finally {
				deliveryMemberTransactionService.unlockToTrade(mtid);
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
	public void tradePC(HttpServletRequest request, HttpServletResponse response) throws ParseException {
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

		DeliveryTransaction bqTransaction = super.findCurrDTrans(symbol);
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
			if (!bqNotradetimeService.readTradeTime(bqTransaction.getTransaction_id())) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
				 toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		DeliveryTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		DeliveryMemberTransaction memberTransaction = deliveryMemberTransactionService.get(mtid);// user.getF_user_id(),
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

		String price = getDMarketOrderPrice(bqTransaction, direction);

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal amount_client = memberTransaction.getHold_count().setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
		BigDecimal price_client = new BigDecimal(price).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + bqTransaction.getTransaction_code());
		} else if (bqTransaction.getTransaction_kind() == 2) {
			btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code());
			if (btc_index_price == null) {
				btc_index_price = deliveryTradeLogService.readLastHourAgoByCode(bqTransaction.getTransaction_code());
				if (btc_index_price != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_HOUR_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, btc_index_price);
				}
			}
		}
		
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = deliveryTransactionFeeRateService.readByCodeAndLevel(bqTransaction.getTransaction_code(), userlevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		DeliveryTransactionRate index_price_rate = deliveryTransactionRateService.getByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (bqTransaction.getTransaction_kind() == 2) {
			BigDecimal oneDayAgo = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code());
			if (oneDayAgo == null) {
				oneDayAgo = deliveryTradeLogService.readLastDayAgoByCode(bqTransaction.getTransaction_code());
				if (oneDayAgo != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_PRICE_ONE_DAY_AGO + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.BPI, oneDayAgo);
				}
			}
			
			if (oneDayAgo != null) {
				BigDecimal oneDayMax = oneDayAgo.add(oneDayAgo.multiply(new BigDecimal(1))).setScale(digits, RoundingMode.HALF_DOWN);
				if (oneDayMax.compareTo(maxIndexPrice)< 0) {
					maxIndexPrice = oneDayMax;
				}
			}
			
			BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_OPEN_PRICE + bqTransaction.getTransaction_code());
			if (openPrice != null) {
				maxIndexPrice = openPrice.add(openPrice.multiply(new BigDecimal(2))).setScale(digits, BigDecimal.ROUND_DOWN);
			}
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		DeliveryAccountContract account = deliveryAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
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

		DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!deliveryMemberTransactionService.lockToTrade(mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
					deliveryPendingDelegationCacheService.insertPCD(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
				} else {
					deliveryPendingDelegationCacheService.insertPCK(cahce, mtid, bqTransaction.getTrade_coefficient(), Contents.DEVICE_PC, sip);
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
				deliveryMemberTransactionService.unlockToTrade(mtid);
			}
		}
	}

	@RequestMapping("/cancleDeal")
	public void cancleDeal(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
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
				List<DeliveryTransaction> transactionlist = super.findCurrDTrans();
				for (DeliveryTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					cancelDPendingWithCode(code, idirection, user.getF_user_id(), bqt.getTrade_coefficient());
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				DeliveryTransaction bqt = super.findCurrDTrans(code);
				if (bqt != null) {
					cancelDPendingWithCode(code, idirection, user.getF_user_id(), bqt.getTrade_coefficient());
				}
			}
		}
	}

	@RequestMapping("/canclePendingAnsyc")
	public void canclePendingDelegationAnsyc(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
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
		
		DeliveryTransaction bqt = super.findCurrDTrans(code);
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
			DeliveryPendingDelegationCache pd = deliveryPendingDelegationCacheService.readPendingByPendingNo(pdId, code, user.getF_user_id());
			if (pd != null) done = true;
			
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = deliveryPendingDelegationCacheService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							DeliveryMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = deliveryMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = deliveryMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (deliveryPendingDelegationCacheService.cancelPendingCache_one(pd, bqt.getTrade_coefficient(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										deliveryMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								deliveryPendingDelegationCacheService.unLock(code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		}
		
		if (!done) {
			DeliveryPendingDelegation pd = deliveryPendingDelegationService.readPendingByPendingNo(pdId, code, user.getF_user_id());
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = deliveryPendingDelegationService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							DeliveryMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = deliveryMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = deliveryMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (deliveryPendingDelegationService.cancelPending_one(pd, bqt.getTrade_coefficient(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (pd.getPriority() == 2 && trans != null) {
										deliveryMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (pd != null) {
								deliveryPendingDelegationService.unLock(code, pd.getDelegation_id(), lk);
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

	@RequestMapping("/canclePendingDelegation")
	public ModelAndView canclePendingDelegation(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		String order = request.getParameter("order");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		DeliveryTransaction bqt = super.findCurrDTrans(code);
		if (bqt == null) {
			ModelMap map = new ModelMap();
			map.addAttribute("pendingcode", code);
			map.addAttribute("pendingorder", order);
			map.addAttribute("pendingtype", type);
			return new ModelAndView("redirect:/trade/entrust.do", map);
		}

		DeliveryPendingDelegation pd = deliveryPendingDelegationService.findPendingById(Long.valueOf(pdId), code);
		if (pd.getF_user_id() == user.getF_user_id()) {
			if (pd.getPriority() != 3) {
				String lk = deliveryPendingDelegationService.lock(code, pd.getDelegation_id());
				if (!StringUtil.isEmpty(lk)) {
					try {
						
						boolean lock = true;
						DeliveryMemberTransaction trans = null;
						if (pd.getPriority() == 2) {
							int tradeType = Contents.TRADE_TYPE_BUY;
							
							if (pd.getDirection() == Contents.DIRECTION_BUY) {
								tradeType = Contents.TRADE_TYPE_SELL;
							}
							
							trans = deliveryMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
							lock = deliveryMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
						}
						
						if (lock) {
							try {
								deliveryPendingDelegationService.cancelPending_one(pd, bqt.getTrade_coefficient(), true);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (pd.getPriority() == 2 && trans != null) {
									deliveryMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							deliveryPendingDelegationService.unLock(code, pd.getDelegation_id(), lk);
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
	public void orderPlanPending(HttpServletRequest request, Model model, HttpServletResponse response) throws ParseException {
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
		if (StringUtil.isEmpty(orderTypeStr) && StringUtil.isEmpty(price)) {
			logger.info("必选参数错误,price错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 参数验证
		final BigDecimal ZERO = BigDecimal.ZERO;
		if (StringUtil.isEmpty(amount) || StringUtil.isEmpty(price) || StringUtil.isEmpty(transSelect) || StringUtil.isEmpty(multiple) || StringUtil.isEmpty(triPrice)) {
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

		DeliveryTransaction transaction = yzgg(transSelect, checkVO, multiple, uid);
		if (transaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		DeliveryTransactionType transactionType = transTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
		
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
		BigDecimal lastPrice = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(transaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice_client) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		DeliveryPlanPendingDelegation delegation = new DeliveryPlanPendingDelegation(transaction.getTransaction_code(), uid,
				triPrice_client, price_client, amount_client, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-未生效", orderType);
		
		DeliveryPlanPendingDelegation pd = deliveryPlanPendingDelegationService.insert(delegation);
		
		if (pd.getId() > 0) {
			checkVO.setCode("success");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));
		}
		
		toWeb(response, gson.toJson(checkVO));
	}
	
	@RequestMapping("/canclePlanDeal")
	public void canclePlanDeal(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
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
				List<DeliveryTransaction> transactionlist = super.findCurrDTrans();
				for (DeliveryTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					List<DeliveryPlanPendingDelegation> pdList = deliveryPlanPendingDelegationService.readByUserIdDirection(idirection,
							code, user.getF_user_id());
					for (DeliveryPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelDPlanPending(pd);
					}
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				DeliveryTransaction bqt = super.findCurrDTrans(code);
				if (bqt != null) {
					List<DeliveryPlanPendingDelegation> pdList = deliveryPlanPendingDelegationService.readByUserIdDirection(idirection,
							code, user.getF_user_id());
					for (DeliveryPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelDPlanPending(pd);
					}
				}
			}
		}
	}

	@RequestMapping("/canclePlanPendingAnsyc")
	public void canclePlanPendingAnsyc(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
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
		
		DeliveryTransaction bqt = super.findCurrDTrans(code);
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
		
		DeliveryPlanPendingDelegation pd = deliveryPlanPendingDelegationService.readById(id, code, user.getF_user_id());
		if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
			checkVO.setCode("error");
			checkVO.setMsg("Canceled or Effective");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int ret = cancelDPlanPending(pd);
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
	public void getTransactionCodeRate(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		List<DeliveryTransactionRate> list = deliveryTransactionRateService.readList();
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
	public void findCommonData(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		String bqCode = request.getParameter("bqCode");
		if (StringUtil.isEmpty(bqCode)) { // 传递合约号失败
			return;
		}
		
		TradeAjaxVO vo = spyMemcachedClient.get(BTCQMemecacheKeys.D_COMMON_DATA + bqCode);
		toWeb(response, new Gson().toJson(vo));
	}
	
	@RequestMapping("/knowRiskWarning")
	public void knowRiskWarning(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		
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
	public void getBPIData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));

		String bpiData = spyMemcachedClient.get(BTCQMemecacheKeys.BPI_DATA);
		if (bpiData == null) {
			bpiData = "[]";
		}
		
		JSONArray array = new JSONArray(bpiData);
		
		toWeb(response, array);
	}
	
	@RequestMapping("/getMemberTransaction")
	public void getMemberTransaction(HttpServletRequest request, HttpServletResponse response) throws JSONException {
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
		List<DeliveryMemberTransaction> list = deliveryMemberTransactionService.readMemberTransaction(code, user.getF_user_id(), new BigDecimal(multiple));
		if (list.size() > 0) {
			for (DeliveryMemberTransaction memberTrans : list) {
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
	public void stopLossClick(HttpServletRequest request, HttpServletResponse response) {
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

		List<DeliveryStopLoss> lossList = deliveryStopLossService.readStopLossList(user.getF_user_id(), Long.valueOf(transaction_member_id));
		toWeb(response, gson.toJson(lossList));
	}

	@RequestMapping("/stopLossChange")
	public void stopLossChange(HttpServletRequest request, HttpServletResponse response) {
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
		DeliveryMemberTransaction bqt = deliveryMemberTransactionService.readById(Long.valueOf(transaction_member_id));
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
		
		DeliveryTransaction transaction = super.findCurrDTrans(bqt.getTransaction_code());
		DeliveryTransactionType transactionType = transTypeService.readTransactionType(transaction.getTransaction_type());
		if (transactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE);
		if(lastPrice == null){
			lastPrice = transactionService.readTransByCode(bqt.getTransaction_code()).getLatest_transaction_price();
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		
		int digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
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
		
		DeliveryAccountContract bqAccount = deliveryAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
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
				if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) <= 0)
						|| stop_win_price_client.compareTo(lastPrice) <= 0) {
					logger.info("多仓止盈价须大于最新价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceGtHoldPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
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
				/* 要求止损价小于最新价 */
				if (stop_loss_price_client.compareTo(lastPrice) >= 0
						|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) >= 0)) {
					logger.info("多仓止损价须小于最新价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceLtAvgPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
						.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
				BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
				BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
						transaction.getTrade_coefficient(), bqt.getDeposit());
				baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
				if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) <= 0)
						|| stop_loss_price_client.compareTo(baocangPrice) <= 0) {
					logger.info("多仓止损价须大于爆仓价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.multiplePriceGtBaoPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
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

				if ((orderType == 0 && pending_win_price_client.compareTo(lastPrice) >= 0)
						|| stop_win_price_client.compareTo(lastPrice) >= 0) {
					logger.info("空仓止盈价须小于最新价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceLtHoldPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
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
				
				/* 要求止损价大于最新价 */
				if (stop_loss_price_client.compareTo(lastPrice) <= 0 
						|| (orderType == 0 && pending_loss_price_client.compareTo(lastPrice) <= 0)) {
					logger.info("空仓止损价须大于最新价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceGtAvgPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				BigDecimal deposit = bqt.getDeposit().add(bqt.getDeposit2()).add(bqt.getF_fromremain()).add(bqt.getF_frozenremain()).add(bqt.getF_min_frozenremain())
						.add(bqAccount.getF_btc().add(bqAccount.getF_lockbtc()));
				BigDecimal all_count = bqt.getHold_count().add(bqt.getLock_count());
				BigDecimal baocangPrice = BTCQMath.QPJ(bqt.getTrade_type_id(), bqt.getAverage_price(), deposit, bqt.getTrade_multiple(), all_count, 
						transaction.getTrade_coefficient(), bqt.getDeposit());
				baocangPrice = baocangPrice.setScale(digits, BigDecimal.ROUND_DOWN);
				if ((orderType == 0 && pending_loss_price_client.compareTo(baocangPrice) >= 0)
						|| stop_loss_price_client.compareTo(baocangPrice) >= 0) {
					logger.info("空仓止损价须小于爆仓价");
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.shortPriceLtBaoPrice", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
			} else if ("0".equalsIgnoreCase(has_loss)) {

			} else {
				logger.info("参数标识不正确");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.paramsIncorrect", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			deliveryStopLossService.updateStopLosses(Long.valueOf(transaction_member_id), has_win, stop_win_price,
					pending_win_price, has_loss, stop_loss_price, pending_loss_price, orderType, pending_amount);
		}

		if (!StringUtil.isEmpty(has_win) && "0".equalsIgnoreCase(has_win) && !StringUtil.isEmpty(has_loss) && "0".equalsIgnoreCase(has_loss)) {
			checkVO.setCode("success2");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelProfitAndLoss", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		toWeb(response, gson.toJson(checkVO));

	}
	
}
