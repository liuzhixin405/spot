package com.btc100.btc_q.action.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import org.springframework.web.bind.annotation.RequestMapping;
import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.SpotPendingLogService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/spotTrade")
public class SpotTradeAction extends BaseAction {

	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private SpotPendingDelegationCacheService spotPendingDelegationCacheService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private SpotPendingLogService pendingLogService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	
	@RequestMapping("/tradeLogData")
	public void tradeLogData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}

		// 当前交易对
		String coinPairName = request.getParameter("coinPairName");
		BQSpotMarketCoinPair currentCoinPair = super.findCurrCoinPairByName(coinPairName);
		if (currentCoinPair == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long t7 = morningTime - 6 * 24 * 3600;
		
		List<SpotTradeLog> trades = spotTradeLogService.readByCodeAndUserAndTime(currentCoinPair.getCoin_pair_name(), user.getF_user_id(), t7, 0, 50);
		Gson gson = new Gson();
		toWeb(response, gson.toJson(trades));
	}
	
	@RequestMapping("/historyPendingData")
	public void historyPendingData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}
		
		String coinPairName = request.getParameter("coinPairName");
		BQSpotMarketCoinPair currentCoinPair = super.findCurrCoinPairByName(coinPairName);
		if (currentCoinPair == null ) {
			return;
		}
		
		long morningTime = TimeUtil.getTimesmorning();
		long startTime = morningTime - 6 * 24 * 3600;
		List<SpotPendingLog> pdLogList = pendingLogService.readListByUserId(coinPairName, user.getF_user_id(), 0, "", startTime, 0, 50);

		Gson gson = new Gson();
		toWeb(response, gson.toJson(pdLogList));
	}
	
	@RequestMapping("/pendingData")
	public void pendingData(HttpServletRequest request, HttpServletResponse response) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return;
		}

		// 当前交易对
		String coinPairName = request.getParameter("coinPairName");
		BQSpotMarketCoinPair currentCoinPair = super.findCurrCoinPairByName(coinPairName);
		if (currentCoinPair == null ) {
			return;
		}
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		
		List<SpotPendingDelegation> pdList = spotPendingDelegationService
				.readAllByUserId(currentCoinPair.getCoin_pair_name(), "", user.getF_user_id(), "");
		for (SpotPendingDelegation pd : pdList) {
			PendingDelegationVO data = new PendingDelegationVO();
			data.setDelegation_id(pd.getDelegation_id());
			data.setOrderId(pd.getPending_no() + "");
			data.setSymbol(currentCoinPair.getCoin_pair_name());
			data.setAmount(pd.getDelegation_count().doubleValue());
			data.setDealAmount(pd.getDeal_count().doubleValue());
			data.setUndealAmount(pd.getUndeal_count().doubleValue());
			data.setPrice(pd.getDelegation_price().doubleValue());
			data.setCreateTime(pd.getDelegation_time());
			data.setDirection(pd.getDirection());
			data.setState(Contents.PENDING_STATE_SUBMITTED);
			data.setCoinPairName(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
			
			listPending.add(data);
		}
		List<SpotPendingDelegationCache> pdCacheList = spotPendingDelegationCacheService.readPendingByUserIdAndTime(coinPairName, user.getF_user_id(), 0, 0, 0, "", 0);
		if (pdCacheList != null) {
			for (SpotPendingDelegationCache cache : pdCacheList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(cache.getDelegation_id());
				data.setOrderId(cache.getDelegation_id() + "");
				data.setSymbol(currentCoinPair.getCoin_pair_name());
				data.setAmount(cache.getDelegation_count().doubleValue());
				data.setDealAmount(cache.getDeal_count().doubleValue());
				data.setUndealAmount(cache.getUndeal_count().doubleValue());
				data.setPrice(cache.getDelegation_price().doubleValue());
				data.setCreateTime(cache.getDelegation_time());
				data.setDirection(cache.getDirection());
				data.setState(Contents.PENDING_STATE_SUBMITTING);
				data.setCoinPairName(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
				
				listPending.add(data);
			}
		}
		
		Gson gson = new Gson();
		toWeb(response, gson.toJson(listPending));
	}

	@RequestMapping("/spotTradeData")
	public void spotTradeData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		BQMember user = super.findCurrUser(request, true);
		if (user == null) {
			return;
		}

		// 当前交易对
		String coinPairName = request.getParameter("coinPairName");
		BQSpotMarketCoinPair currentCoinPair = super.findCurrCoinPairByName(coinPairName);
		if (currentCoinPair == null ) {
			return;
		}
		
		JSONObject gson = new JSONObject();
		
		// 账户信息
		BQAccount coinAccount = bqAccountService.readAccount(user.getF_user_id(), currentCoinPair.getCoin_id());
		gson.put("coinAccount", coinAccount.getBtc().setScale(4, BigDecimal.ROUND_DOWN).doubleValue());
		// 账户信息
		BQAccount maeketCoinAccount = bqAccountService.readAccount(user.getF_user_id(), currentCoinPair.getMarket_coin_id());
		gson.put("maeketCoinAccount", maeketCoinAccount.getBtc().setScale(4, BigDecimal.ROUND_DOWN).doubleValue());

		// 获取当前最新成交价 
		BigDecimal lastPrice = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = currentCoinPair.getLatest_price();
		}
		
		// 获取买5卖5
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = spotPendingDelegationService.readBuyTop5(currentCoinPair.getCoin_pair_name());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = spotPendingDelegationService.readSellTop5(currentCoinPair.getCoin_pair_name());
		}
		
		int digits = Utils.getDigitsFromMinFloat(currentCoinPair.getMin_float_price());
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
		
		gson.put("buy1", buy1Price.doubleValue());
		gson.put("sell1", sell1Price.doubleValue());
		
		toWeb(response, gson.toString());
	}
	
	
	/**
	 * 买卖
	 * 
	 */
	@RequestMapping("/spotCenterTrade")
	public String spotCenterTrade(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		String coinPairName = request.getParameter("coinPairName");
		BQSpotMarketCoinPair coinPair = findCurrCoinPairByName(coinPairName);
		if (coinPair == null) {
			return "redirect:/spotTrade/spotTrade.do";
		}
		
		return "redirect:/spotTrade/spotTrade.do?coinPairId=" + coinPair.getId();
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
	@RequestMapping("/spotTrade")
	public String trade(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ClassNotFoundException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember member = super.findCurrUser(request, true);
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		if (member != null) {
			BQMemberPayword payword = bqMemberPaywordService.readById(member.getF_user_id());
			if (payword != null && payword.getState() != 0) {
				model.addAttribute("payPwd", true);
			} else {
				model.addAttribute("payPwd", false);
			}
			
			BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(member.getF_user_id());
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

		// 当前交易对
		int coinPairId = Convert.toInt(request.getParameter("coinPairId"), 0);
		if (coinPairId == 0) {
			String codeVale = CookieHelper.getCookieValue(request, "coinPairId");
			coinPairId = Convert.toInt(codeVale, 1);
		} 
		CookieHelper.setCookie(response, "coinPairId", coinPairId + "");
		
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
		
		BQSpotMarketCoinPair currentCoinPair = super.findCurrCoinPairById(coinPairId);
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();
		
		if (currentCoinPair == null && coinPairs.size() > 0) {
			currentCoinPair = coinPairs.get(0);
		}
		if (currentCoinPair == null ) {
			model.addAttribute("hashy", false);
			return "jsp/full_spotTrade";
		}
		
		BQSpotMarket spotMarket = bqSpotMarketService.readSpotMarketById(currentCoinPair.getMarket_id());
		if (spotMarket == null) {
			return "jsp/full_spotTrade";
		}
		
		model.addAttribute("coinPairs", coinPairs);
		model.addAttribute("minKcNums", currentCoinPair.getMin_trade_nums().doubleValue());
		model.addAttribute("minFloatPrice", currentCoinPair.getMin_float_price().doubleValue());
		model.addAttribute("coinPairName", currentCoinPair.getCoin_pair_name().toUpperCase());
		model.addAttribute("marketName", currentCoinPair.getMarket_coin_name());
		model.addAttribute("coinName", currentCoinPair.getCoin_name());
		model.addAttribute("hashy", true);
		model.addAttribute("currentCoinPair", currentCoinPair);
		
		int digits = Utils.getDigitsFromMinFloat(currentCoinPair.getMin_float_price());
		
		// 账户信息
		BQAccount coinAccount = new BQAccount();
		if (member != null) {
			coinAccount = bqAccountService.readAccount(member.getF_user_id(), currentCoinPair.getCoin_id());
		} else {
			coinAccount.setBtc(BigDecimal.ZERO);
			coinAccount.setBtc_locked(BigDecimal.ZERO);
		}
		
		model.addAttribute("coinAccount", coinAccount);
		// 账户信息
		BQAccount maeketCoinAccount = new BQAccount();
		if (member != null) {
			maeketCoinAccount = bqAccountService.readAccount(member.getF_user_id(), currentCoinPair.getMarket_coin_id());
		} else {
			maeketCoinAccount.setBtc(BigDecimal.ZERO);
			maeketCoinAccount.setBtc_locked(BigDecimal.ZERO);
		}
		model.addAttribute("maeketCoinAccount", maeketCoinAccount);
		
		BQCurrencyType coin = bqCurrencyTypeService.readByTypeId(currentCoinPair.getCoin_id());
		if (coin != null) {
			model.addAttribute("coin", coin);
		} else {
			model.addAttribute("coin", new BQCurrencyType());
		}
  
		BQCurrencyType marketCoin = bqCurrencyTypeService.readByTypeId(currentCoinPair.getMarket_coin_id());
		if (marketCoin != null) {
			model.addAttribute("marketCoin", marketCoin);
		} else {
			model.addAttribute("marketCoin", new BQCurrencyType());
		}

		// 获取当前最新成交价 
		BigDecimal lastPrice = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = currentCoinPair.getLatest_price();
		}
		
		// 获取买5卖5
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.BUYTOP5LIST);
		if (buytop5List == null) {
			buytop5List = spotPendingDelegationService.readBuyTop5(currentCoinPair.getCoin_pair_name());
		}
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(currentCoinPair.getCoin_pair_name() + BTCQMemecacheKeys.SELLTOP5LIST);
		if (selltop5List == null) {
			selltop5List = spotPendingDelegationService.readSellTop5(currentCoinPair.getCoin_pair_name());
		}
		
		BigDecimal buy1Price = BigDecimal.ZERO;
		BigDecimal sell1Price = BigDecimal.ZERO;
		if (buytop5List != null) {
			for (Top5DelegationVO vo : buytop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(currentCoinPair.getDecimals(), RoundingMode.DOWN));
			}
			
			if (buytop5List.size() > 0)
				buy1Price = buytop5List.get(0).getPrice();
		}
		
		if (selltop5List != null) {
			for (Top5DelegationVO vo : selltop5List) {
				vo.setPrice(vo.getPrice().setScale(digits, RoundingMode.DOWN));
				vo.setCountNum(vo.getCountNum().setScale(currentCoinPair.getDecimals(), RoundingMode.DOWN));
			}
			
			if (selltop5List.size() > 0) {
				sell1Price = selltop5List.get(selltop5List.size() - 1).getPrice();
			}
		}
		
		List<PendingDelegationVO> listPending = new ArrayList<PendingDelegationVO>();
		
		if (member != null) {
			List<SpotPendingDelegation> pdList = spotPendingDelegationService
					.readAllByUserId(currentCoinPair.getCoin_pair_name(), "", member.getF_user_id(), "");
			for (SpotPendingDelegation pd : pdList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(pd.getDelegation_id());
				data.setOrderId(pd.getPending_no() + "");
				data.setSymbol(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
				data.setAmount(pd.getDelegation_count().doubleValue());
				data.setDealAmount(pd.getDeal_count().doubleValue());
				data.setUndealAmount(pd.getUndeal_count().doubleValue());
				data.setPrice(pd.getDelegation_price().doubleValue());
				data.setCreateTime(pd.getDelegation_time());
				data.setDirection(pd.getDirection());
				data.setState(Contents.PENDING_STATE_SUBMITTED);
				data.setCoinPairName(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
				
				listPending.add(data);
			}
			List<SpotPendingDelegationCache> pdCacheList = spotPendingDelegationCacheService.readPendingByUserIdAndTime(currentCoinPair.getCoin_pair_name(), member.getF_user_id(), 0, 0, 0, "", 0);
			if (pdCacheList != null) {
				for (SpotPendingDelegationCache cache : pdCacheList) {
					PendingDelegationVO data = new PendingDelegationVO();
					data.setDelegation_id(cache.getDelegation_id());
					data.setOrderId(cache.getPending_no() + "");
					data.setSymbol(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
					data.setAmount(cache.getDelegation_count().doubleValue());
					data.setDealAmount(cache.getDeal_count().doubleValue());
					data.setUndealAmount(cache.getUndeal_count().doubleValue());
					data.setPrice(cache.getDelegation_price().doubleValue());
					data.setCreateTime(cache.getDelegation_time());
					data.setDirection(cache.getDirection());
					data.setState(Contents.PENDING_STATE_SUBMITTING);
					data.setCoinPairName(currentCoinPair.getCoin_name() + "/" + currentCoinPair.getMarket_coin_name());
					
					listPending.add(data);
				}
			}
		}
		
		model.addAttribute("pdList", listPending);
		model.addAttribute("buytop5List", buytop5List);
		model.addAttribute("selltop5List", selltop5List);
		model.addAttribute("lastPrice", lastPrice.doubleValue());
		model.addAttribute("buy1Price", buy1Price);
		model.addAttribute("sell1Price", sell1Price);
		model.addAttribute("digits", digits);
		model.addAttribute("amountDigits", currentCoinPair.getDecimals());
		if (sell1Price != null && sell1Price.compareTo(BigDecimal.ZERO) > 0) {
			model.addAttribute("maxBuyAmount",
				maeketCoinAccount.getBtc().divide(sell1Price, currentCoinPair.getDecimals(), BigDecimal.ROUND_DOWN));
		}
		model.addAttribute("maxSellAmount", coinAccount.getBtc().setScale(currentCoinPair.getDecimals(), BigDecimal.ROUND_DOWN));
		
		List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
		if (member != null) {
			long morningTime = TimeUtil.getTimesmorning();
			long startTime = morningTime - 6 * 24 * 3600;
			trades = spotTradeLogService.readByCodeAndUserAndTime(currentCoinPair.getCoin_pair_name(), member.getF_user_id(), startTime, 0, 50);
		}
		
		model.addAttribute("tradeLogList", trades);
		
		if (member != null) {
			BQMemberFavoriteMarket fav = bqMemberFavoriteMarketService.readByUserId(member.getF_user_id(), Contents.MARKET_TYPE_SPOT);
			if (fav != null) {
				request.setAttribute("favorite", fav.getCodes());
			}
		}
		
		return "jsp/full_spotTrade";
	}
	
	@RequestMapping("/spotPendingQuery")
	public String spotPendingQuery(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return super.toLogin();
		}
		
		Map<String, String> directionMap = new LinkedHashMap<String, String>();
		directionMap.put("all", SpringContext.getText("msg.pg.tradeEntrust.allTrade", Contents.Local.LOCALE.get()));
		directionMap.put("1", SpringContext.getText("msg.pg.trade.buyIn", Contents.Local.LOCALE.get()));
		directionMap.put("2", SpringContext.getText("msg.pg.cointrade.sell", Contents.Local.LOCALE.get()));
		model.addAttribute("directionMap", directionMap);
		
		return pendingQuerydo(request, model, user);
	}

	/**
	 * 交易查询
	 * 
	 * @return
	 */
	@RequestMapping("/spotTradeQuery")
	public String tradeQuery(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return super.toLogin();
		}
		Map<String, String> directionMap = new LinkedHashMap<String, String>();
		directionMap.put("all", SpringContext.getText("msg.pg.tradeEntrust.allTrade", Contents.Local.LOCALE.get()));
		directionMap.put("1", SpringContext.getText("msg.pg.trade.buyIn", Contents.Local.LOCALE.get()));
		directionMap.put("2", SpringContext.getText("msg.pg.cointrade.sell", Contents.Local.LOCALE.get()));
		model.addAttribute("directionMap", directionMap);
		return tradeQuerydo(request, model, user);
	}

	private String tradeQuerydo(HttpServletRequest request, Model model, BQMember user) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();

		model.addAttribute("coinPairs", coinPairs);
		String coinPairName = request.getParameter("coinPairName");// all hyh

		if (coinPairs == null || coinPairs.size() <= 0) {
			model.addAttribute("currentCzlx", "all");
			model.addAttribute("tradeLogList", new PagerModel());

			return "jsp/trade_query";
		}

		BQSpotMarketCoinPair currentCoinPair = null;
		if (StringUtil.isEmpty(coinPairName) || "all".equalsIgnoreCase(coinPairName)) {
			currentCoinPair = coinPairs.get(0);
			coinPairName = currentCoinPair.getCoin_pair_name();
		} else {
			currentCoinPair = bqSpotMarketCoinPairService.readByName(coinPairName);
		}
		model.addAttribute("currentCoinPair", currentCoinPair);

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
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -89);
		long t90 = cal.getTimeInMillis() / 1000;
		
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

		String direction = request.getParameter("direction");

		PagerModel tradeLogList = null;
		if (StringUtil.isEmpty(direction) || "all".equalsIgnoreCase(direction)) {
			tradeLogList = spotTradeLogService.findByUserIdAndTransaction(coinPairName, user.getF_user_id(), 0, startTime, endTime);
			model.addAttribute("currentDirection", "all");
		} else {
			tradeLogList = spotTradeLogService.findByUserIdAndTransaction(coinPairName, user.getF_user_id(), Integer.valueOf(direction), startTime, endTime);
			model.addAttribute("currentDirection", direction);
		}

		model.addAttribute("tradeLogList", tradeLogList);
		return "jsp/coin_query";
	}
	
	private String pendingQuerydo(HttpServletRequest request, Model model, BQMember user) {
		List<BQSpotMarketCoinPair> coinPairs = super.findCurrCoinPair();

		model.addAttribute("coinPairs", coinPairs);
		String coinPairName = request.getParameter("coinPairName");

		if (coinPairs == null || coinPairs.size() <= 0) {
			model.addAttribute("currentCzlx", "all");
			model.addAttribute("tradeLogList", new PagerModel());

			return "jsp/coin_pending_query";
		}

		BQSpotMarketCoinPair currentCoinPair = null;
		if (StringUtil.isEmpty(coinPairName) || "all".equalsIgnoreCase(coinPairName)) {
			currentCoinPair = coinPairs.get(0);
			coinPairName = currentCoinPair.getCoin_pair_name();
		} else {
			currentCoinPair = bqSpotMarketCoinPairService.readByName(coinPairName);
		}
		model.addAttribute("currentCoinPair", currentCoinPair);

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
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -89);
		long t90 = cal.getTimeInMillis() / 1000;
		
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

		String direction = request.getParameter("direction");

		PagerModel pendingLogList = null;
		if (StringUtil.isEmpty(direction) || "all".equalsIgnoreCase(direction)) {
			pendingLogList = pendingLogService.readPagerMode(coinPairName, user.getF_user_id(), 0, startTime, endTime);
			model.addAttribute("currentDirection", "all");
		} else {
			pendingLogList = pendingLogService.readPagerMode(coinPairName, user.getF_user_id(), Integer.valueOf(direction), startTime, endTime);
			model.addAttribute("currentDirection", direction);
		}

		model.addAttribute("pendingLogList", pendingLogList);
		return "jsp/coin_pending_query";
	}
}
