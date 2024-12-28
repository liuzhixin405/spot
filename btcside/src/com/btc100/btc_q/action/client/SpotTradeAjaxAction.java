package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseTradeAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.TradeAjaxVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/spotTradeAjax")
public class SpotTradeAjaxAction extends BaseTradeAction {

	private static final Logger logger = Logger.getLogger(SpotTradeAjaxAction.class);

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private SpotPendingDelegationCacheService spotPendingDelegationCacheService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;

	@RequestMapping("/tradeTrans")
	public void tradeTrans(HttpServletRequest request, Model model, HttpServletResponse response)
			throws ParseException {
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

		/*// 会话验证
		if (!super.checkToken(request)) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}*/

		checkVO.setCode("error");
		checkVO.setMsg(SpringContext.getText("msg.trade.request.orderFail", Contents.Local.LOCALE.get()));

		String amount = request.getParameter("amount");
		String price = request.getParameter("price");
		String coinPairName = request.getParameter("coinPairName");
		String payPwd = request.getParameter("payPwd");
		int direction = Convert.toInt(request.getParameter("direction"), 0);
		if (direction != Contents.DIRECTION_BUY && direction != Contents.DIRECTION_SELL) {
			checkVO.setCode("error");
			checkVO.setMsg(
					SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		// 参数验证
		final BigDecimal ZERO = BigDecimal.ZERO;
		if (StringUtil.isEmpty(amount) || StringUtil.isEmpty(price) || StringUtil.isEmpty(coinPairName)) {
			logger.info("必选参数错误");
			checkVO.setCode("error");
			checkVO.setMsg(
					SpringContext.getText("msg.trade.request.requiredParametersError", Contents.Local.LOCALE.get()));
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
			checkVO.setMsg(
					SpringContext.getText("msg.common.request.tradePwdErrorOrNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(coinPairName);
		if (coinPair == null) {
			logger.info("交易对错误");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (coinPair.getSuspend()) {
			logger.info("交易暂停：" + coinPair.getCoin_pair_name());
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.pause", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQSpotMarket market = bqSpotMarketService.readMarket(coinPair.getMarket_id());
		if (market == null) {
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
		int amountDigits = coinPair.getDecimals();

		// 挂单数量及价格验证
		BigDecimal amount_client = ZERO;
		BigDecimal price_client = ZERO;
		try {
			double damount = ConvertUtil.toDouble(amount, 0);
			double dprice = ConvertUtil.toDouble(price, 0);
			if (damount <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.numberError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			} else if (dprice <= 0) {
				checkVO.setCode("error");
				checkVO.setMsg(SpringContext.getText("msg.trade.request.priceError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			amount_client = new BigDecimal(damount).setScale(amountDigits + 1, BigDecimal.ROUND_HALF_UP).setScale(amountDigits, BigDecimal.ROUND_DOWN);
			price_client = new BigDecimal(dprice).setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);

		} catch (Exception e) {
			logger.info("价格或数量不为数字");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.priceOrNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BigDecimal frozen = BigDecimal.ZERO;
		BigDecimal commissionRate = BigDecimal.ZERO;
		int remainCoinId = 0;

		if (amount_client.compareTo(coinPair.getMin_trade_nums()) < 0) {
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.minimumPendingNumber", Contents.Local.LOCALE.get())
					+ coinPair.getMin_trade_nums() + "个");
			toWeb(response, gson.toJson(checkVO));
			return;
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
		BQAccount bqAccount = bqAccountService.readAccount(user.getF_user_id(), remainCoinId);
		if (bqAccount != null) {
			remainAmount = bqAccount.getBtc();
		}

		if (frozen.compareTo(remainAmount) > 0) {
			logger.info("账户可用余额不足以完成本次交易");
			checkVO.setCode("error");
			checkVO.setMsg(SpringContext.getText("msg.trade.request.balanceNotEnough", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		boolean needLog = true;
		boolean isQuantUser = checkIsSpotQuantUser(user.getF_user_id());
		if (isQuantUser) {
			needLog = false;
			commissionRate = BigDecimal.ZERO;
		}
		
		String risk = spotRiskCheck(coinPair.getCoin_pair_name(), coinPair.getCoin_name().toLowerCase(), coinPair.getMarket_coin_name().toLowerCase(),
				direction, price_client, amount_client, digits, amountDigits);
		if (!StringUtil.isEmpty(risk)) {
			checkVO.setCode("error");
			logger.info(risk);
			checkVO.setMsg(risk);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		SpotPendingDelegationCache cache = new SpotPendingDelegationCache(coinPair.getId(),
				coinPair.getCoin_pair_name(), direction, amount_client, price_client, BigDecimal.ZERO, amount_client,
				commissionRate, user.getF_user_id(), BigDecimal.ZERO);

		try {
			String sip = IpMacUtil.getIpAddr(request);
			spotPendingDelegationCacheService.insertCacheAndChangeAccount(cache, frozen, bqAccount, remainCoinId, needLog, Contents.DEVICE_PC, sip);
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
				List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
				for (BQSpotMarketCoinPair bqt : coinPairs) {
					cancelSpotPendingWithCode(bqt, idirection, user.getF_user_id());
				}
			} else {
				if (code == null) {
					checkVO.setCode("error");
					checkVO.setMsg(
							SpringContext.getText("msg.common.request.errorCoinPair", Contents.Local.LOCALE.get()));
					toWeb(response, gson.toJson(checkVO));
					return;
				}
				List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
				for (BQSpotMarketCoinPair bqt : coinPairs) {
					if (bqt.getCoin_pair_name().equalsIgnoreCase(code)) {
						cancelSpotPendingWithCode(bqt, idirection, user.getF_user_id());
						break;
					}
				}
			}
		}
	}

	@RequestMapping("/canclePendingAnsyc")
	public void canclePendingDelegationAnsyc(HttpServletRequest request, HttpServletResponse response)
			throws ServiceException {
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

		boolean needLog = true;
		boolean isQuantUser = checkIsSpotQuantUser(user.getF_user_id());
		if (isQuantUser) {
			needLog = false;
		}
		
		BQSpotMarketCoinPair coinPair = null;
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		for (BQSpotMarketCoinPair bqt : coinPairs) {
			if (bqt.getCoin_pair_name().equalsIgnoreCase(code)) {
				coinPair = bqt;
				break;
			}
		}
		
		if (coinPair != null) {
			SpotPendingDelegation pd = spotPendingDelegationService.readPendingByPendingNo(pdId, code, user.getF_user_id());
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				String lock = spotPendingDelegationService.lock(code, pd.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						if (spotPendingDelegationService.cancelPending_one(pd, needLog, coinPair) > 0) {
							checkVO.setCode("success");
							checkVO.setMsg(SpringContext.getText("msg.trade.request.cancelOrderSuccess", Contents.Local.LOCALE.get()));
							toWeb(response, gson.toJson(checkVO));
							return;
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							spotPendingDelegationService.unLock(code, pd.getDelegation_id(), lock);
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
	public ModelAndView canclePendingDelegation(HttpServletRequest request, HttpServletResponse response)
			throws ServiceException {
		Contents.Local.LOCALE.set(getLocale(request));

		String pdId = request.getParameter("pdId");
		String code = request.getParameter("code");
		String price = request.getParameter("price");
		String direction = request.getParameter("direction");

		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}

		boolean needLog = true;
		boolean isQuantUser = checkIsSpotQuantUser(user.getF_user_id());
		if (isQuantUser) {
			needLog = false;
		}
		
		BQSpotMarketCoinPair coinPair = null;
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		for (BQSpotMarketCoinPair bqt : coinPairs) {
			if (bqt.getCoin_pair_name().equalsIgnoreCase(code)) {
				coinPair = bqt;
				break;
			}
		}
		
		if (coinPair != null) {
			SpotPendingDelegation pd = spotPendingDelegationService.findPendingById(Long.valueOf(pdId), code);
			if (pd != null && pd.getF_user_id() == user.getF_user_id()) {
				String lock = spotPendingDelegationService.lock(code, pd.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						spotPendingDelegationService.cancelPending_one(pd, needLog, coinPair);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							spotPendingDelegationService.unLock(code, pd.getDelegation_id(), lock);
						}
					}
				}
			}
		}
		
		ModelMap map = new ModelMap();
		map.addAttribute("coinPairName", code);
		map.addAttribute("direction", direction);
		map.addAttribute("price", price);
		return new ModelAndView("redirect:/spotTrade/spotEntrust.do", map);
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
		Contents.Local.LOCALE.set(getLocale(request));
		
		String tradePair = request.getParameter("tradePair");
		if (StringUtil.isEmpty(tradePair)) { // 传递合约号失败
			return;
		}
		
		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(tradePair);
		if (coinPair == null) {
			return;
		}

		TradeAjaxVO vo = spyMemcachedClient.get(BTCQMemecacheKeys.COMMON_DATA + tradePair);

		toWeb(response, new Gson().toJson(vo));
	}
}
