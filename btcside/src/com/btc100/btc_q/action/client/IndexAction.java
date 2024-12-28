package com.btc100.btc_q.action.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQArticle;
import com.btc100.btc_q.entity.BQBonusRecord;
import com.btc100.btc_q.entity.BQBonusSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberFavoriteMarket;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.BQMineStageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionPartition;
import com.btc100.btc_q.entity.NTransactionPartition;
import com.btc100.btc_q.service.BQArticleService;
import com.btc100.btc_q.service.BQBonusRecordService;
import com.btc100.btc_q.service.BQBonusSettingService;
import com.btc100.btc_q.service.BQMemberFavoriteMarketService;
import com.btc100.btc_q.service.BQMemberInviteAmountService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMineRecordService;
import com.btc100.btc_q.service.BQMineSettingService;
import com.btc100.btc_q.service.BQMineStageSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQTransactionPartitionService;
import com.btc100.btc_q.service.NTransactionPartitionService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.Security;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.MineStatisticsVO;
import com.btc100.btc_q.vo.RebateRankingVo;
import com.google.gson.Gson;

@Controller
@RequestMapping("/home")
public class IndexAction extends BaseAction {

	@Autowired
	private BQMemberInviteAmountService bqMemberInviteAmountService;
	@Autowired
	private BQMineRecordService bqMineRecordService;
	@Autowired
	private BQBonusRecordService bqBonusRecordService;
	@Autowired
	private BQMineSettingService bqMineSettingService;
	@Autowired
	private BQMineStageSettingService bqMineStageSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQBonusSettingService bqBonusSettingService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	@Autowired
	private BQTransactionPartitionService bqTransactionPartitionService;
	@Autowired
	private NTransactionPartitionService nTransactionPartitionService;
	@Autowired
	private BQMemberFavoriteMarketService bqMemberFavoriteMarketService;
	@Autowired
	private BQArticleService bqArticleService;

	@RequestMapping("/home")
	public String home(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		String bcexLogin = request.getParameter("bcexLogin");
		String newUser = request.getParameter("newUser");
		if (!StringUtil.isEmpty(bcexLogin) && Convert.toInt(bcexLogin, 0) == 1) {
			model.addAttribute("bcexLogin", true);
		}
		
		if (!StringUtil.isEmpty(newUser) && Convert.toInt(newUser, 0) == 1) {
			String email = spyMemcachedClient.get("bcexlogin-" + request.getSession().getId());
			if (!StringUtil.isEmpty(email)) {
				model.addAttribute("newUser", true);
			}
		}
		
		String from = request.getParameter("from");
		if (!StringUtil.isEmpty(from) && "otc".equals(from)) {
			request.setAttribute("from", from);
		}
		
		List<BQTransactionPartition> partitions = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_PARTITIONS);
		if (partitions == null) {
			partitions = bqTransactionPartitionService.readAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_PARTITIONS, BTCQMemecacheKeys.TIMES.ONE_MIN, partitions);
		}
		
		request.setAttribute("partitions", partitions);
		
		List<NTransactionPartition> usdrPartitions = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_PARTITIONS + "_usdr");
		if (usdrPartitions == null) {
//			usdrPartitions = nTransactionPartitionService.readAll("usdr");
			usdrPartitions = new ArrayList<>();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_PARTITIONS  + "_usdr", BTCQMemecacheKeys.TIMES.ONE_MIN, usdrPartitions);
		}
		
		request.setAttribute("usdrPartitions", usdrPartitions);
		
		request.setAttribute("favorite", "");
		BQMember user = super.findCurrUser(request, false);
		if (user != null) {
			BQMemberFavoriteMarket fav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), Contents.MARKET_TYPE_CONTRACT);
			if (fav != null) {
				request.setAttribute("favorite", fav.getCodes());
			}

			BQMemberFavoriteMarket favSpot = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), Contents.MARKET_TYPE_SPOT);
			if (fav != null) {
				request.setAttribute("spotFavorite", favSpot.getCodes());
			}
			
//			BQMemberFavoriteMarket usdrFav = bqMemberFavoriteMarketService.readByUserId(user.getF_user_id(), "usdr");
//			if (usdrFav != null) {
//				request.setAttribute("usdrFavorite", usdrFav.getCodes());
//			}
		}
		
		try {
			List<BQTransaction> listTransactions = super.findCurrTrans();
			model.addAttribute("transactionList", listTransactions);
			model.addAttribute("listMap", currTrans(listTransactions));
			model.addAttribute("daysAndAmount", getDaysAndTradeAmount());
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "jsp/index";
	}
	
	@RequestMapping("/s{name}")
	public String invite(@PathVariable("name") String name, HttpServletResponse response) {
		if (!StringUtil.isEmpty(name)) {
			String id = "";
			try {
				id = Security.decryptByHex(name.toUpperCase(), Security.des_registerkey);
				long userId = Long.parseLong(id);
				bqMemberInviteAmountService.updateClickByUserId(userId);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			CookieHelper.setCookie(response, "inviter", name, 3600, false);
		}

		return "redirect:/register/register.do?invid=" + name;
	}
	
	@RequestMapping("/setLocale")
	public void setLocale(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = (Locale) request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		CookieHelper.setCookie(response, "language", locale.toString(), CookieHelper.COOKIE_MAX_AGE * 12, false);
		toWeb(response, "true");
	}

	@RequestMapping("/tickersData")
	public void tickersData(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		toWeb(response, gson.toJson(super.currTrans(super.findCurrTrans())));
	}
	
	@RequestMapping("/getDaysAndAmount")
	public void getDaysAndAmount(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		toWeb(response, getDaysAndTradeAmount());
	}
	
	private JSONObject getDaysAndTradeAmount() throws JSONException {
		String dataStr = spyMemcachedClient.get(BTCQMemecacheKeys.PLATFORM_DAYS_AMOUNT);
		JSONObject data = new JSONObject();
		if (StringUtil.isEmpty(dataStr)) {
			BigDecimal allAmount = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_TRADE_AMOUNT);
			BigDecimal oneDayTrades = spyMemcachedClient.get(BTCQMemecacheKeys.ONE_DAY_TRADE_AMOUNT);
	        data.put("amount", allAmount == null ? 0 : allAmount.longValue());
	        data.put("dayAmount", oneDayTrades == null ? 0 : oneDayTrades.longValue());

	        spyMemcachedClient.set(BTCQMemecacheKeys.PLATFORM_DAYS_AMOUNT, BTCQMemecacheKeys.TIMES.ONE_MIN, data.toString());
		} else {
			data = new JSONObject(dataStr);
		}
		
		return data;
	}
	
	@RequestMapping("/getExpressNews")
	public void getExpressNews(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String categoryIdStr = request.getParameter("categoryId");
		int categoryId = Convert.toInt(categoryIdStr, 4);
		JSONArray array = new JSONArray();
		
		List<BQArticle> list = spyMemcachedClient.get("index_express_news");
		if (list == null) {
			list = bqArticleService.readByCategoryAndTime(categoryId, 0, "", 0, 0, 3);
			if (list == null) {
				list = new ArrayList<>();
			}
			spyMemcachedClient.set("index_express_news", BTCQMemecacheKeys.TIMES.TEN_SECOND, list);
		}
		
		for (BQArticle article : list) {
			Map<String,Object> newsMap = new HashMap<String, Object>();
			newsMap.put("article_id", article.getArticle_id()); // 记录id
			newsMap.put("category_id", article.getCategory_id()); // 记录id
			newsMap.put("title", article.getTitle()); // 标题
			newsMap.put("dateline", article.getDateline()); // 时间
			newsMap.put("description", article.getDescription()); // 描述
			newsMap.put("keyword", article.getKeyword()); // 关键字
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(newsMap)));
		}
		
		userJson.put("data", array);
		
		toWeb(response, userJson);
	}

	@RequestMapping("/getInviteRanking")
	public void getInviteRanking(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		List<Object[]> list = spyMemcachedClient.get(BTCQMemecacheKeys.INVITE_RANKING);
		if (list == null) {
			list = bqMemberInviteAmountService.readRanking(5);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.INVITE_RANKING, BTCQMemecacheKeys.TIMES.ONE_HOUR, list);
		}
		
		for (Object[] obj : list) {
			String email = (String) obj[0];
			obj[0] = formatEmail(email);
		}
		toWeb(response, gson.toJson(list));
	}

	@RequestMapping("/getRebateRanking")
	public void getRebateRanking(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		List<RebateRankingVo> voList = spyMemcachedClient.get(BTCQMemecacheKeys.REBATE_RANKING);
		if (voList == null) {
			voList = new ArrayList<RebateRankingVo>();
		}

		toWeb(response, gson.toJson(voList));
	}

	@RequestMapping("/getIncomeRanking")
	public void getIncomeRanking(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Gson gson = new Gson();
		
		List<RebateRankingVo> voList = spyMemcachedClient.get(BTCQMemecacheKeys.INCOME_RANKING);
		if (voList == null) {
			voList = new ArrayList<RebateRankingVo>();
		}

		toWeb(response, gson.toJson(voList));
	}
	
	@SuppressWarnings("unused")
	private MineStatisticsVO getMineStatistics() {
		
		MineStatisticsVO mineStatisticsVO = spyMemcachedClient.get(BTCQMemecacheKeys.MINE_STATISTICS);
		if (mineStatisticsVO == null) {
			mineStatisticsVO = new MineStatisticsVO();
		
			BQMineSetting setting = bqMineSettingService.read();
			if (setting == null) return null;
			
			// 当前挖矿总数量
			BigDecimal currentAmount = setting.getMined_amount();
			
			BQMineStageSetting stageSetting = bqMineStageSettingService.readByStageAmount(currentAmount);
			// 获取挖矿阶段
			int stage = stageSetting.getNum();
			
			
			Calendar timeNoon = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			long nowTime = timeNoon.getTimeInMillis() / 1000;
			
			timeNoon.set(Calendar.HOUR_OF_DAY, 12);
			timeNoon.set(Calendar.MINUTE, 0);
			timeNoon.set(Calendar.SECOND, 0);
			timeNoon.set(Calendar.MILLISECOND, 0);
			
			long todayNone = timeNoon.getTimeInMillis() / 1000;
			
			timeNoon.add(Calendar.DATE, -1);
			long todayZero = timeNoon.getTimeInMillis() / 1000;
			if (nowTime >= todayNone) {
				todayZero = todayNone;
			}
			
			long tomorrowZero = todayZero + 24*60*60;
			
			// 获取今日挖矿时时数量
			BigDecimal todayAmount = BigDecimal.ZERO;
			// 当前二级市场流通量
			BigDecimal parallelAmount = currentAmount;
			BigDecimal rebateAmount = currentAmount.multiply(new BigDecimal(2));
			
			BQBonusSetting bonusSetting = bqBonusSettingService.readOne();
			if (bonusSetting == null) return mineStatisticsVO;
			
			if (nowTime <= bonusSetting.getBegin_time()) {
				BQMineSetting mineSetting = bqMineSettingService.read();
				todayZero = mineSetting.getBegin_time();
			}
			
			List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
			if (coinPairs == null) {
				coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
			}
			
			/*
			// 今日待分配收入
			BigDecimal todayFee = bqMineRecordService.sumFeeBetweenTime(0, todayZero, tomorrowZero);
			for (BQSpotMarketCoinPair coinPair : coinPairs) {
				if (coinPair.getCoin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_BUY, todayZero, tomorrowZero);
						todayFee = todayFee.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (coinPair.getMarket_coin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_SELL, todayZero, tomorrowZero);
						todayFee = todayFee.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			BigDecimal rebateToday = bqMemberInviteRebateLogService.getSumRebateByTime(todayZero, tomorrowZero, 0, Contents.TRADE_CURRENCY_TYPE_ID);
			todayFee = todayFee.subtract(rebateToday);
			
			// 昨日已分配收入
			BigDecimal yesterdayFee = BigDecimal.ZERO;
			BQBonusRecord bonusReord = bqBonusRecordService.getLast(todayZero, Contents.TRADE_CURRENCY_TYPE_ID);
			if (bonusReord != null) {
				yesterdayFee = bonusReord.getTotal_fee();
			}
			*/
			
			// 今日年化返利收益率
			BigDecimal lastPrice = BigDecimal.ZERO;
			BigDecimal todayFee = bqMineRecordService.readSumFeeBetweenTime(0, todayZero, tomorrowZero);
			for (BQSpotMarketCoinPair coinPair : coinPairs) {
				if (coinPair.getCoin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.readFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_BUY, todayZero, tomorrowZero);
						todayFee = todayFee.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					if (coinPair.getMarket_coin_name().toLowerCase().equals("usdt")) {
						lastPrice = coinPair.getLatest_price();
					}
				} else if (coinPair.getMarket_coin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.readFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_SELL, todayZero, tomorrowZero);
						todayFee = todayFee.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			BigDecimal rebateToday = bqMemberInviteRebateLogService.readSumRebateByTime(todayZero, tomorrowZero, 0, Contents.TRADE_CURRENCY_TYPE_ID);
			todayFee = todayFee.subtract(rebateToday);
			
			
			BigDecimal last = spyMemcachedClient.get("bfxusd" + BTCQMemecacheKeys.BTCLATESTPRICE);
			if (last == null) {
				last = lastPrice;
			}
			if (last == null || last.compareTo(BigDecimal.ZERO) <= 0) {
				todayFee = BigDecimal.ZERO;
			} else {
				todayFee = todayFee.multiply(new BigDecimal(365)).divide(rebateAmount, 15, BigDecimal.ROUND_HALF_UP).divide(last, 6, BigDecimal.ROUND_HALF_UP);
			}
			
			// 昨日年化返利收益率
			BigDecimal yesterdayFee = BigDecimal.ZERO;
			// 累计返利收益率
			BigDecimal totalFee = BigDecimal.ZERO;
			
			BigDecimal price = spotMemberTradeDetailService.readLastPriceBeforeTime("bfxusd", todayZero);
			BQBonusRecord bonusReord = bqBonusRecordService.readLast(0, Contents.TRADE_CURRENCY_TYPE_ID);
			if (bonusReord != null) {
				yesterdayFee = bonusReord.getRate();
				if (price.compareTo(BigDecimal.ZERO) > 0) {
					yesterdayFee = bonusReord.getRate().multiply(new BigDecimal(365)).divide(price, 6, BigDecimal.ROUND_HALF_UP);
				}
			}
			
			if (price.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal totalBonus = bqBonusRecordService.readSumBonusRate(Contents.TRADE_CURRENCY_TYPE_ID);
				totalFee = totalBonus.divide(price, 6, BigDecimal.ROUND_HALF_UP);
			}
			
			mineStatisticsVO.setStage(stage);
			mineStatisticsVO.setTodayAmount(todayAmount);
			mineStatisticsVO.setCurrentAmount(currentAmount);
			mineStatisticsVO.setRebateAmount(rebateAmount);
			mineStatisticsVO.setParallelAmount(parallelAmount);
			mineStatisticsVO.setTodayFee(todayFee);
			mineStatisticsVO.setYesterdayFee(yesterdayFee);
			mineStatisticsVO.setTotalFee(totalFee);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.MINE_STATISTICS, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, mineStatisticsVO);
		}
		
		return mineStatisticsVO;
	}

}
