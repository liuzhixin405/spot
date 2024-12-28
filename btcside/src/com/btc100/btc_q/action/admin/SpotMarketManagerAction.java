package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerSpotMarket")
public class SpotMarketManagerAction extends BaseAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private BQSpotMarketCoinPairService bqCoinPairService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;

	@RequestMapping("/loadManagerSpotMarket")
	public String loadManagerTransType(HttpServletRequest request, Model mo) {
		PagerModel marketList = bqSpotMarketService.readPagerModel();
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		mo.addAttribute("pm", marketList);
		mo.addAttribute("coins", coins);
		return super.url_JSP("spotMarketList");
	}

	@RequestMapping("/editManagerSpotMarket")
	public String editManagerTransType(HttpServletRequest request, Model mo) throws Exception {
		int id = Convert.toInt(request.getParameter("id"), 0);
		BQSpotMarket market = bqSpotMarketService.readSpotMarketById(id);
		if (market == null) {
			return super.errorPage(mo, "找不到该交易区");
		}
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		mo.addAttribute("coins", coins);
		mo.addAttribute("market", market);
		return super.url_JSP("spotMarketEdit");
	}

	@RequestMapping("/updateManagerSpotMarket")
	public String updateManagerSpotMarket(HttpServletRequest request, Model mo) throws Exception {
		int id = Convert.toInt(request.getParameter("id"), 0);
		int coinId = Convert.toInt(request.getParameter("coinId"), 0);
		String marketName = request.getParameter("marketName");
		String indexAddress = request.getParameter("indexAddress");
		Boolean isUsing = (Convert.toInt(request.getParameter("isUsing"), 0) == 1);
		String password = request.getParameter("password");
		BQCurrencyType coin = bqCurrencyTypeService.readByTypeId(coinId);
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		if (marketName == null || marketName.isEmpty()) {
			return super.errorPage(mo, "缺少交易区名称");
		}

		if (id <= 0 && bqSpotMarketService.getSpotMarketByName(marketName) != null) {
			return super.errorPage(mo, "已有同名交易区");
		}

		if (coinId <= 0) {
			return super.errorPage(mo, "交易区币种不能为空");
		}

		if (id <= 0 && bqSpotMarketService.getSpotMarketByCoinName(coin.getType_name()) != null) {
			return super.errorPage(mo, "已有该币种类型交易区");
		}

		BQSpotMarket market = new BQSpotMarket();
		if (id > 0) {
			market.setId(id);
		}

		market.setMarket_name(marketName);
		market.setCoin_name(coin.getType_name());
		market.setCoin_id(coin.getType_id());
		market.setIs_using(isUsing);
		market.setIndex_address(indexAddress);

		bqSpotMarketService.updateSpotMarket(market);

		List<BQSpotMarket> markets = bqSpotMarketService.readAllUsing();
		spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_MARKET, BTCQMemecacheKeys.TIMES.ONE_MIN, markets);

		return redirect("/managerSpotMarket/loadManagerSpotMarket.do");
	}

	@RequestMapping("/loadCoinPair")
	public String loadCoinPair(HttpServletRequest request, Model mo) {
		PagerModel list = bqCoinPairService.readByState_pm("", 0);
		List<BQSpotMarket> markets = bqSpotMarketService.readAllUsing();
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		mo.addAttribute("pm", list);
		mo.addAttribute("markets", markets);
		mo.addAttribute("coins", coins);
		return super.url_JSP("spotMarketCoinPairList");
	}

	@RequestMapping("/editCoinPair")
	public String editCoinPair(HttpServletRequest request, Model mo) {
		int id = Convert.toInt(request.getParameter("id"), 0);
		BQSpotMarketCoinPair coinPair = bqCoinPairService.getById(id);
		if (null == coinPair) {
			return super.errorPage(mo, "找不到该交易对");
		}
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		List<BQSpotMarket> markets = bqSpotMarketService.readAllUsing();
		mo.addAttribute("markets", markets);
		mo.addAttribute("coins", coins);
		mo.addAttribute("coinPair", coinPair);
		return super.url_JSP("spotMarketCoinPairEdit");
	}

	@RequestMapping("/addCoinPair")
	public String addCoinPair(HttpServletRequest request, Model mo) throws Exception {
		BigDecimal ZERO = BigDecimal.ZERO;
		String state = request.getParameter("state");
		String password = request.getParameter("password");
		int decimals = Convert.toInt(request.getParameter("decimals"), 4);
		String minNums = request.getParameter("minTradeNums");
		String minFloatPri = request.getParameter("minFloatPrice");
		BigDecimal minTradeNums = ZERO;
		BigDecimal minFloatPrice = ZERO;
		BigDecimal coinBuyRate = new BigDecimal(request.getParameter("coinBuyRate"));
		BigDecimal coinSellRate = new BigDecimal(request.getParameter("coinSellRate"));

		int marketId = ConvertUtil.toInt(request.getParameter("marketId"), 0);
		int coinId = ConvertUtil.toInt(request.getParameter("coinId"), 0);
		if (marketId <= 0) {
			return super.errorPage(mo, "请选择交易区!");
		}

		BQSpotMarket market = bqSpotMarketService.readSpotMarketById(marketId);
		if (market == null) {
			return super.errorPage(mo, "交易区不存在!");
		}

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		int istate = ConvertUtil.toInt(state, 0);

		if (istate != 1 && istate != 2 && istate != 3) {
			return super.errorPage(mo, "交易对状态错误，1:正常 2:关闭 3:其他");
		}

		if (!StringUtil.isEmpty(minNums)) {
			minTradeNums = new BigDecimal(Convert.toDouble(minNums, 0.0));
		} else {
			return super.errorPage(mo, "最小交易数量为空");
		}
		
		if (!StringUtil.isEmpty(minFloatPri)) {
			minFloatPrice = new BigDecimal(Convert.toDouble(minFloatPri, 0.0));
		} else {
			return super.errorPage(mo, "最小浮动价格为空");
		}
		
		BQCurrencyType coin = bqCurrencyTypeService.readByTypeId(coinId);
		
		String coinPairName = coin.getType_name() + market.getCoin_name();
		coinPairName = coinPairName.toLowerCase();
		BQSpotMarketCoinPair coinPair = bqCoinPairService.readByName(coinPairName);
		if (coinPair != null) {
			return super.errorPage(mo, "此交易对已存在");
		}

		BQSpotMarketCoinPair spotMarketCoinPair = new BQSpotMarketCoinPair(coinPairName, coin.getType_name(),
				coin.getType_id(), istate, minTradeNums, minFloatPrice, decimals, coinBuyRate, coinSellRate, ZERO, ZERO,
				ZERO, market.getId(), market.getCoin_id(), market.getCoin_name());

		bqCoinPairService.addMarketCoinPair(spotMarketCoinPair);

		return redirect("/managerSpotMarket/loadCoinPair.do");
	}

	@RequestMapping("/updateCoinPair")
	public String updateCoinPair(HttpServletRequest request, Model mo) throws Exception {
		String password = request.getParameter("password");
		String buyRate = request.getParameter("coinBuyRate");
		String sellRate = request.getParameter("coinSellRate");
		int coinPairId = ConvertUtil.toInt(request.getParameter("coinPairId"), 0);
		int state = ConvertUtil.toInt(request.getParameter("state"), 0);
		
		int decimals = Convert.toInt(request.getParameter("decimals"), 4);
		String minNums = request.getParameter("minTradeNums");
		String minFloatPri = request.getParameter("minFloatPrice");
		BigDecimal ZERO = BigDecimal.ZERO;
		BigDecimal minTradeNums = ZERO;
		BigDecimal minFloatPrice = ZERO;
		BigDecimal coinBuyRate = new BigDecimal(request.getParameter("coinBuyRate"));
		BigDecimal coinSellRate = new BigDecimal(request.getParameter("coinSellRate"));
		
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		if (state != 1 && state != 2 && state != 3) {
			return super.errorPage(mo, "交易对状态错误，1:正常 2:关闭 3:其他");
		}
		if (StringUtil.isEmpty(buyRate)) {
			return super.errorPage(mo, "币种买入费率为空");
		}
		if (StringUtil.isEmpty(sellRate)) {
			return super.errorPage(mo, "币种卖出费率为空");
		}
		if (!StringUtil.isEmpty(minNums)) {
			minTradeNums = new BigDecimal(Convert.toDouble(minNums, 0.0));
		} else {
			return super.errorPage(mo, "最小交易数量为空");
		}
		if (!StringUtil.isEmpty(minFloatPri)) {
			minFloatPrice = new BigDecimal(Convert.toDouble(minFloatPri, 0.0));
		} else {
			return super.errorPage(mo, "最小浮动价格为空");
		}
		
		String suspend = request.getParameter("suspend");
		if (coinPairId > 0) {
			BQSpotMarketCoinPair marketCoinPair = bqCoinPairService.getById(coinPairId);
			marketCoinPair.setState(state);
			marketCoinPair.setCoin_buy_rate(coinBuyRate);
			marketCoinPair.setCoin_sell_rate(coinSellRate);
			marketCoinPair.setDecimals(decimals);
			marketCoinPair.setMin_trade_nums(minTradeNums);
			marketCoinPair.setMin_float_price(minFloatPrice);
			marketCoinPair.setSuspend("true".equals(suspend) ? true : false);
			bqCoinPairService.update(marketCoinPair);
		} else {
			return super.errorPage(mo, "该交易对不存在");
		}

		return redirect("/managerSpotMarket/loadCoinPair.do");
	}

}
