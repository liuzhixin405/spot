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
import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPlanPendingDelegation;
import com.btc100.btc_q.entity.VStopLoss;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.service.VStopLossService;
import com.btc100.btc_q.service.VTransactionRateService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
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
@RequestMapping("/vtradeAjax")
public class VTradeAjaxAction extends BaseTradeAction {

	private static final Logger logger = Logger.getLogger(VTradeAjaxAction.class);

	@Autowired
	private VTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private VPendingDelegationService pendingDelegationService;
	@Autowired
	private VTransactionRateService bqTransactionRateService;
	@Autowired
	private VStopLossService bqStopLossService;
	@Autowired
	private VMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private VAccountContractService bqAccountContractService;
	@Autowired
	private VTransactionTypeService transTypeService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private VArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private VPlanPendingDelegationService vPlanPendingDelegationService;
	
	private VTransaction yzgg(String transSelect, AjaxCheckVO checkVO, String multiple, Long uid) {

		VTransaction bqTransaction = null;
		List<VTransaction> list = super.findCurrVTrans();
		for (VTransaction bq : list) {
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
		if (multiple_client <= 0) {
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
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.errorMultiple", Contents.Local.LOCALE.get()));
			return null;
		}
		
		int canMultiple = getVOneCanMultiple(bqTransaction, uid);
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

		VTransaction bqTransaction = yzgg(transSelect, checkVO, multiple, uid);
		if (bqTransaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		int amountDigits = Utils.getAmountDigtis(bqTransaction.getTransaction_code());
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getVMarketOrderPrice(bqTransaction, direction);
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
		
		BigDecimal btc_index_price = super.findBPIByCode(bqTransactionType.getType_code());
		VTransactionRate index_price_rate = bqTransactionRateService.readByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
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
		// 费率
		BQMemberLevel userlevel = bqMemberLevelService.readById(user.getF_user_level_id());
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		
		BigDecimal remainAmount = BigDecimal.ZERO;
		VAccountContract bqContract = bqAccountContractService.readByUidCode(uid, bqTransaction.getTransaction_code());
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

		fee = BTCQMath.getFee(amount_client, price_client, userlevel.getEat_commission_rate(), coefficient);

		BigDecimal deposit = BTCQMath.getDeposit(amount_client, price_client, multiple_client2, coefficient);

		BigDecimal maxAmount = remainAmount.subtract(fee).multiply(multiple_client2)
				.divide(price_client, 11, BigDecimal.ROUND_HALF_UP).setScale(10, BigDecimal.ROUND_HALF_DOWN);
		if (amount_client.compareTo(maxAmount) > 0) {
			if (orderType == 1) {
				amount_client = BTCQMath.getMaxAmount(remainAmount, price_client, multiple_client2, coefficient, feeRate);
				amount_client = amount_client.setScale(amountDigits, BigDecimal.ROUND_DOWN);
				fee = BTCQMath.getFee(amount_client, price_client, userlevel.getEat_commission_rate(), coefficient);
				deposit = BTCQMath.getDeposit(amount_client, price_client, multiple_client2, coefficient);
			} else {
				logger.info("账户可用余额不足以完成本次交易");
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.balanceNotEnough", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
	
		String risk = riskCheckV(bqTransaction.getTransaction_code(), direction, price_client, amount_client, digits);
		if (!StringUtil.isEmpty(risk)) {
			checkVO.setCode("error");
			logger.info(risk);
			checkVO.setMsg(risk);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount_client, price_client, BigDecimal.ZERO, amount_client, Integer.valueOf(multiple), deposit,
				userlevel.getEat_commission_rate(), Contents.PRIORITY_KC, uid, BigDecimal.ZERO, BigDecimal.ZERO);

		try {
			String sip = IpMacUtil.getIpAddr(request);
			pendingDelegationCacheService.insertCacheAndChangeAccount(cahce, bqTransaction.getCurrency_type_id(), fee, bqContract, true, Contents.DEVICE_PC, sip);
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

		VTransaction bqTransaction = super.findCurrVTrans(transSelect);
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
		
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getVMarketOrderPrice(bqTransaction, Contents.DIRECTION_SELL);
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
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		VMemberTransaction buyMemberTransaction = bqMemberTransactionService.get(mtid);
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
		
		BQMemberLevel userlevel = bqMemberLevelService.readById(user.getF_user_level_id());
		VTransactionRate index_price_rate = bqTransactionRateService.readByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
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
		VAccountContract account = bqAccountContractService.readBTCByUidCode(buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, buyMemberTransaction.getAverage_price(), deposit1, userlevel.getEat_commission_rate(), 
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

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (bqMemberTransactionService.lockToTrade(mtid)) {
				try {
					String sip = IpMacUtil.getIpAddr(request);
					pendingDelegationCacheService.insertPCD(cahce, mtid, Contents.DEVICE_PC, sip);
					toWeb(response, gson.toJson(checkVO));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.trade.request.tryAgain", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				} finally {
					bqMemberTransactionService.unlockToTrade(mtid);
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

		VTransaction bqTransaction = super.findCurrVTrans(transSelect);
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
		
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int orderType = Convert.toInt(orderTypeStr, 0);
		if (orderType == 1) {
			price = getVMarketOrderPrice(bqTransaction, Contents.DIRECTION_BUY);
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
		
		VMemberTransaction buyMemberTransaction = bqMemberTransactionService.get(mtid);// user.getF_user_id(),
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
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = bqMemberLevelService.readById(user.getF_user_level_id());
		VTransactionRate index_price_rate = bqTransactionRateService.readByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
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
		VAccountContract account = bqAccountContractService.readBTCByUidCode(buyMemberTransaction.getF_user_id(), buyMemberTransaction.getTransaction_code());
		BigDecimal deposit1 = buyMemberTransaction.getDeposit().add(buyMemberTransaction.getDeposit2()).add(buyMemberTransaction.getF_fromremain()).add(buyMemberTransaction.getF_frozenremain())
				.add(buyMemberTransaction.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, buyMemberTransaction.getAverage_price(), deposit1, userlevel.getEat_commission_rate(), 
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

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!bqMemberTransactionService.lockToTrade(mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				pendingDelegationCacheService.insertPCK(cahce, mtid, Contents.DEVICE_PC, sip);
				toWeb(response, gson.toJson(checkVO));
				return;
			} catch (Exception e) {
				e.printStackTrace();
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} finally {
				bqMemberTransactionService.unlockToTrade(mtid);
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

		VTransaction bqTransaction = super.findCurrVTrans(symbol);
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
		
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.illegalParameter", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		VMemberTransaction memberTransaction = bqMemberTransactionService.get(mtid);// user.getF_user_id(),
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

		String price = getVMarketOrderPrice(bqTransaction, direction);

		// 校验
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderSuccess", Contents.Local.LOCALE.get()));

		int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
		BigDecimal amount_client = memberTransaction.getHold_count().setScale(5, BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_DOWN);
		BigDecimal price_client = new BigDecimal(price).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		BigDecimal btc_index_price = findBPIByCode(bqTransactionType.getType_code());
		if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.indexPriceFail", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMemberLevel userlevel = getLevelByUserBfx(user);
		BigDecimal feeRate = userlevel.getEat_commission_rate();
		
		VTransactionRate index_price_rate = bqTransactionRateService.getByCode(bqTransaction.getTransaction_code());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_SELL && price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY && price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		VAccountContract account = bqAccountContractService.readBTCByUidCode(memberTransaction.getF_user_id(), memberTransaction.getTransaction_code());
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

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (checkVO.getCode().equalsIgnoreCase("success")) {
			if (!bqMemberTransactionService.lockToTrade(mtid)) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			try {
				String sip = IpMacUtil.getIpAddr(request);
				if (memberTransaction.getTrade_type_id() == Contents.DIRECTION_BUY) {
					pendingDelegationCacheService.insertPCD(cahce, mtid, Contents.DEVICE_PC, sip);
				} else {
					pendingDelegationCacheService.insertPCK(cahce, mtid, Contents.DEVICE_PC, sip);
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
				bqMemberTransactionService.unlockToTrade(mtid);
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
				List<VTransaction> transactionlist = super.findCurrVTrans();
				for (VTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					cancelVPendingWithCode(code, bqt.getCurrency_type_id(), idirection, user.getF_user_id());
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				
				VTransaction bqt = super.findCurrVTrans(code);
				cancelVPendingWithCode(code, bqt.getCurrency_type_id(), idirection, user.getF_user_id());
			}
		}
	}

	@RequestMapping("/canclePendingAnsyc")
	public void cancleVPendingDelegationAnsyc(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
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
		
		VTransaction bqt = super.findCurrVTrans(code);
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
			VPendingDelegationCache pd = pendingDelegationCacheService.readPendingByPendingNo(pdId, code, user.getF_user_id());
			if (pd != null) done = true;
			
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationCacheService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							VMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationCacheService.cancelPendingCache_one(pd, bqt.getCurrency_type_id(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
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
								pendingDelegationCacheService.unLock(code, pd.getDelegation_id(), lk);
							}
						}
					}
				}
			}
		}
		
		if (!done) {
			VPendingDelegation pd = pendingDelegationService.readPendingByPendingNo(pdId, code, user.getF_user_id());
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				if (pd.getPriority() != 3) {
					String lk = pendingDelegationService.lock(code, pd.getDelegation_id());
					if (!StringUtil.isEmpty(lk)) {
						try {
							boolean lock = true;
							VMemberTransaction trans = null;
							if (pd.getPriority() == 2) {
								int tradeType = Contents.TRADE_TYPE_BUY;
								
								if (pd.getDirection() == Contents.DIRECTION_BUY) {
									tradeType = Contents.TRADE_TYPE_SELL;
								}
								
								trans = bqMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
								lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
							}
							
							if (lock) {
								try {
									if (pendingDelegationService.cancelPending_one(pd, bqt.getCurrency_type_id(), true) > 0) {
										checkVO.setCode("success");
										checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
										toWeb(response, gson.toJson(checkVO));
										return;
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
								pendingDelegationService.unLock(code, pd.getDelegation_id(), lk);
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

	@RequestMapping("/cancleVPendingDelegation")
	public ModelAndView cancleVPendingDelegation(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		String order = request.getParameter("order");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		VTransaction bqt = super.findCurrVTrans(code);
		VPendingDelegation pd = pendingDelegationService.findPendingById(Long.valueOf(pdId), code);
		if (pd.getF_user_id() == user.getF_user_id()) {
			if (pd.getPriority() != 3) {
				String lk = pendingDelegationService.lock(code, pd.getDelegation_id());
				if (!StringUtil.isEmpty(lk)) {
					try {
						
						boolean lock = true;
						VMemberTransaction trans = null;
						if (pd.getPriority() == 2) {
							int tradeType = Contents.TRADE_TYPE_BUY;
							
							if (pd.getDirection() == Contents.DIRECTION_BUY) {
								tradeType = Contents.TRADE_TYPE_SELL;
							}
							
							trans = bqMemberTransactionService.readMemberTransaction(code, pd.getF_user_id(), tradeType, pd.getMultiple());
							lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
						}
						
						if (lock) {
							try {
								pendingDelegationService.cancelPending_one(pd, bqt.getCurrency_type_id(), true);
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
							pendingDelegationService.unLock(code, pd.getDelegation_id(), lk);
						}
					}
				}
			}
		}

		ModelMap map = new ModelMap();
		map.addAttribute("pendingcode", code);
		map.addAttribute("pendingorder", order);
		map.addAttribute("pendingtype", type);
		return new ModelAndView("redirect:/vtrade/entrust.do", map);
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

		VTransaction bqTransaction = yzgg(transSelect, checkVO, multiple, uid);
		if (bqTransaction == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
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
		BigDecimal lastPrice = spyMemcachedClient.get(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(bqTransaction.getTransaction_code()).getLatest_transaction_price();
		}
		if (lastPrice.compareTo(triPrice_client) >= 0) {
			type = Contents.PLAN_PENDING_TYPE_GT;
		} else {
			type = Contents.PLAN_PENDING_TYPE_LT;
		}
		
		VPlanPendingDelegation delegation = new VPlanPendingDelegation(bqTransaction.getTransaction_code(), uid,
				triPrice_client, price_client, amount_client, Integer.valueOf(multiple), direction, type,
				Contents.PLAN_PENDING_STATUS_INEFFECTIVE, "计划委托挂单创建-未生效", orderType);
		
		VPlanPendingDelegation pd = vPlanPendingDelegationService.insert(delegation);
		
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
				List<VTransaction> transactionlist = super.findCurrVTrans();
				for (VTransaction bqt : transactionlist) {
					code = bqt.getTransaction_code();
					List<VPlanPendingDelegation> pdList = vPlanPendingDelegationService.readByUserIdDirection(idirection,
							code, user.getF_user_id());
					for (VPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelVPlanPending(pd);
					}
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				VTransaction bqt = super.findCurrVTrans(code);
				if (bqt != null) {
					List<VPlanPendingDelegation> pdList = vPlanPendingDelegationService.readByUserIdDirection(idirection,
							code, user.getF_user_id());
					for (VPlanPendingDelegation pd : pdList) {
						if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
							continue;
						}
						cancelVPlanPending(pd);
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
		
		VTransaction bqt = super.findCurrVTrans(code);
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
		
		VPlanPendingDelegation pd = vPlanPendingDelegationService.readById(id, code, user.getF_user_id());
		if (pd.getStatus() == Contents.PLAN_PENDING_STATUS_CANCEL || pd.getStatus() == Contents.PLAN_PENDING_STATUS_EFFECTIVE){
			checkVO.setCode("error");
			checkVO.setMsg("Canceled or Effective");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int ret = cancelVPlanPending(pd);
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

		List<VTransactionRate> list = bqTransactionRateService.readList();
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
		
		TradeAjaxVO vo = spyMemcachedClient.get(BTCQMemecacheKeys.V_COMMON_DATA + bqCode);
		toWeb(response, new Gson().toJson(vo));
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
		List<VMemberTransaction> list = bqMemberTransactionService.readMemberTransaction(code, user.getF_user_id(), new BigDecimal(multiple));
		if (list.size() > 0) {
			for (VMemberTransaction memberTrans : list) {
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

		List<VStopLoss> lossList = bqStopLossService.readStopLossList(user.getF_user_id(), Long.valueOf(transaction_member_id));
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
		VMemberTransaction bqt = bqMemberTransactionService.get(Long.valueOf(transaction_member_id));
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
		
		VTransaction bqTransaction = super.findCurrVTrans(bqt.getTransaction_code());
		VTransactionType bqTransactionType = transTypeService.readTransactionType(bqTransaction.getTransaction_type());
		if (bqTransactionType == null) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE);
		if(lastPrice == null){
			lastPrice = transactionService.readTransByCode(bqt.getTransaction_code()).getLatest_transaction_price();
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
		
		VAccountContract bqAccount = bqAccountContractService.readBTCByUidCode(bqt.getF_user_id(), bqt.getTransaction_code());
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
			bqStopLossService.updateStopLosses(Long.valueOf(transaction_member_id), has_win, stop_win_price,
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

	private String riskCheckV(String code, int direction, BigDecimal price, BigDecimal amount, int priceDigits) {
		String ret = "";
		
		VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.readArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new VArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
				setting.setAuto_trade(false);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.ONE_SECOND, setting);
		}
		
		if (setting == null || !setting.getRisk_manage()) return ret;
		
		String symbol = "usdt";
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}
		
		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.V_HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.V_HEDGING_BALANCE + symbol);
		}
		
		if (balance == null) {
			 balance = BigDecimal.ZERO;
		}
		
		if (direction == Contents.DIRECTION_SELL) {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<VPendingDelegation> list = pendingDelegationService.readBuyTop(code, price);
			for (VPendingDelegation pd : list) {
				if (pd.getF_user_id() == setting.getDepth_uid()) {
					BigDecimal undeal = pd.getUndeal_count();
					if ((needDepthDo.add(undeal)).compareTo(totalAmount) >= 0) {
						undeal = totalAmount.subtract(needDepthDo);
					}
					
					needDepthDo = needDepthDo.add(undeal);
					if (needDepthDo.compareTo(totalAmount) >= 0) {
						needDepthDo = totalAmount;
						break;
					}
				} else {
					
					BigDecimal undeal = pd.getUndeal_count();
					if ((totalAmount.subtract(undeal)).compareTo(BigDecimal.ZERO) < 0) {
						undeal = totalAmount;
					}
					
					userDo = userDo.add(undeal);
					totalAmount = totalAmount.subtract(undeal);
					if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
						totalAmount = BigDecimal.ZERO;
						break;
					}
				}
			}
			
			if (needDepthDo.compareTo(BigDecimal.ZERO) <= 0) {
				return ret;
			} 
			
			if (needDepthDo.compareTo(balance) > 0) {
				logger.info("开空仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanSell", Contents.Local.LOCALE.get())
						+ balance.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		} else {
			BigDecimal totalAmount = amount;
			BigDecimal needDepthDo = BigDecimal.ZERO;
			BigDecimal needVol = BigDecimal.ZERO;
			BigDecimal userDo = BigDecimal.ZERO;
			List<VPendingDelegation> list = pendingDelegationService.readSellTop(code, price);
			for (VPendingDelegation pd : list) {
				if (pd.getF_user_id() == setting.getDepth_uid()) {
					BigDecimal undeal = pd.getUndeal_count();
					if ((needDepthDo.add(undeal)).compareTo(totalAmount) >= 0) {
						undeal = totalAmount.subtract(needDepthDo);
					}
					
					needDepthDo = needDepthDo.add(undeal);
					needVol = needVol.add(undeal.multiply(pd.getDelegation_price()));
					if (needDepthDo.compareTo(totalAmount) >= 0) {
						needDepthDo = totalAmount;
						break;
					}
				} else {
					BigDecimal undeal = pd.getUndeal_count();
					if ((totalAmount.subtract(undeal)).compareTo(BigDecimal.ZERO) < 0) {
						undeal = totalAmount;
					}
					userDo = userDo.add(undeal);
					totalAmount = totalAmount.subtract(undeal);
					if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
						totalAmount = BigDecimal.ZERO;
						break;
					}
				}
			}
			
			if (needDepthDo.compareTo(BigDecimal.ZERO) <= 0) {
				return ret;
			} 
			
			price = needVol.divide(needDepthDo, 8, BigDecimal.ROUND_UP);
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			price = price.setScale(priceDigits, BigDecimal.ROUND_DOWN);
			
			int digtis = Utils.getAmountDigtis(code);
			BigDecimal canOpen = balance.divide(price, digtis, BigDecimal.ROUND_DOWN);
			if (needDepthDo.compareTo(canOpen) > 0) {
				logger.info("开多仓限制：needDepthDo: " + needDepthDo.doubleValue() + ", balance: " + balance.doubleValue() + ", canOpen: " + canOpen.doubleValue());
				ret = SpringContext.getText("msg.trade.risk.riskCurrent", Contents.Local.LOCALE.get())
						+ code.toUpperCase() + SpringContext.getText("msg.trade.risk.riskCanBuy", Contents.Local.LOCALE.get())
						+ canOpen.add(userDo).doubleValue() +  SpringContext.getText("msg.trade.risk.tryAgain", Contents.Local.LOCALE.get());
			}
		}
		
		return ret;
	}
	
}
