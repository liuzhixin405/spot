package com.btc100.btc_q.action.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQApp;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionPartition;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionPartition;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionPartition;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQAppService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberWalletService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.service.BQTransactionPartitionService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.DeliveryTransactionPartitionService;
import com.btc100.btc_q.service.NTransactionPartitionService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/commonApi")
public class CommonApiAction  extends BaseApiAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQAppService bqAppService;
	@Autowired
	private BQTransactionPartitionService bqTransactionPartitionService;
	@Autowired
	private DeliveryTransactionPartitionService dTransactionPartitionService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	@Autowired
	private BQMemberWalletService bqMemberWalletService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private NTransactionPartitionService nTransactionPartitionService;
	
	Logger logger = Logger.getLogger(CommonApiAction.class);

	/**
	 * 查询平台所有支持的合约交易类型或现货交易交易对以及精度
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/symbols")
	public String getSymbols(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		if ("spot".equals(type)) {
			List<BQSpotMarketCoinPair> transactionList = super.findCurrCoinPair();
			for (BQSpotMarketCoinPair coinPair : transactionList) {
				String code = coinPair.getCoin_pair_name();
				int pricePrecision = getSpotDigits(code);
				int amountPrecision = coinPair.getDecimals();
				
				JSONObject json = new JSONObject();
				json.put("symbol", code);
				json.put("pricePrecision", pricePrecision);
				json.put("amountPrecision", amountPrecision);
				json.put("market", coinPair.getMarket_coin_name().toLowerCase());
				json.put("coin", coinPair.getCoin_name().toLowerCase());
				
				array.put(json);
			}
		} else if ("vcontract".equals(type)) { 
			List<VTransaction> transactionList = super.findCurrVTrans();
			for (VTransaction bq : transactionList) {
				int pricePrecision = getDigits(bq.getTransaction_code(), type);
				int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
				JSONObject json = new JSONObject();
				json.put("symbol", bq.getTransaction_code());
				json.put("pricePrecision", pricePrecision);
				json.put("amountPrecision", amountPrecision);
				json.put("multiple", bq.getMultiple());
				json.put("showName", bq.getTransaction_code().toUpperCase());
				
				array.put(json);
			}
		} else if ("delivery".equals(type)) { 
			List<DeliveryTransaction> transactionList = super.findCurrDTrans();
			for (DeliveryTransaction bq : transactionList) {
				int pricePrecision = getDigits(bq.getTransaction_code(), type);
				int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
				JSONObject json = new JSONObject();
				json.put("symbol", bq.getTransaction_code());
				json.put("pricePrecision", pricePrecision);
				json.put("amountPrecision", amountPrecision);
				json.put("multiple", bq.getMultiple());
				json.put("showName", bq.getTransaction_show_name());
				
				array.put(json);
			}
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> transactionList = findCurrTrans();
			for (BQTransaction bq : transactionList) {
				int pricePrecision = getDigits(bq.getTransaction_code(), type);
				int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
				JSONObject json = new JSONObject();
				json.put("symbol", bq.getTransaction_code());
				json.put("pricePrecision", pricePrecision);
				json.put("amountPrecision", amountPrecision);
				json.put("multiples", bq.getMultiple());
				json.put("showName", bq.getTransaction_show_name());
				
				array.put(json);
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = findNCurrTrans(type);
				for (NTransaction bq : transactionList) {
					int pricePrecision = getDigits(bq.getTransaction_code(), type);
					int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
					JSONObject json = new JSONObject();
					json.put("symbol", bq.getTransaction_code());
					json.put("pricePrecision", pricePrecision);
					json.put("amountPrecision", amountPrecision);
					json.put("multiples", bq.getMultiple());
					json.put("showName", bq.getTransaction_show_name());
					
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
	
	@RequestMapping("/partitions")
	public String getPartitions(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Locale locale = Contents.Local.LOCALE.get();
		
		String type = request.getParameter("type");
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		if ("delivery".equals(type)) {
			List<DeliveryTransactionPartition> partitions = dTransactionPartitionService.readAll();
			for (DeliveryTransactionPartition partition : partitions) {
				JSONObject json = new JSONObject();
				json.put("id", "partition" + partition.getId());
				json.put("codes", partition.getCodes());
				json.put("name", partition.getName_cn());
				if (locale.getLanguage().equals("en") && StringUtil.isEmpty(partition.getName_en())) {
					json.put("name", partition.getName_en());
				} else if (locale.toString().equalsIgnoreCase("zh_HK") && StringUtil.isEmpty(partition.getName_hk())) {
					json.put("name", partition.getName_hk());
				}
				
				array.put(json);
			}
		} if ("vcontract".equals(type)) {
			
		} else if (type == null || "contract".equals(type) || "".equals(type)) {
			List<BQTransactionPartition> partitions = bqTransactionPartitionService.readAll();
			for (BQTransactionPartition partition : partitions) {
				JSONObject json = new JSONObject();
				json.put("id", "partition" + partition.getId());
				json.put("codes", partition.getCodes());
				json.put("name", partition.getName_cn());
				
				if (locale.getLanguage().equals("en") && !StringUtil.isEmpty(partition.getName_en())) {
					json.put("name", partition.getName_en());
				} else if (locale.toString().equalsIgnoreCase("zh_HK") && !StringUtil.isEmpty(partition.getName_hk())) {
					json.put("name", partition.getName_hk());
				}
				
				array.put(json);
			}
		} else {
			List<NTransactionPartition> partitions = nTransactionPartitionService.readAll(type);
			for (NTransactionPartition partition : partitions) {
				JSONObject json = new JSONObject();
				json.put("id", "partition" + partition.getId());
				json.put("codes", partition.getCodes());
				json.put("name", partition.getName_cn());
				
				if (locale.getLanguage().equals("en") && StringUtil.isEmpty(partition.getName_en())) {
					json.put("name", partition.getName_en());
				} else if (locale.toString().equalsIgnoreCase("zh_HK") && StringUtil.isEmpty(partition.getName_hk())) {
					json.put("name", partition.getName_hk());
				}
				
				array.put(json);
			}
		}
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	@RequestMapping("/currencys")
	public String getCurrencys(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
		
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		for (BQCurrencyType type : currencyTypes) {
			array.put(type.getType_name().toLowerCase());
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	@RequestMapping("checkAppVersion")
	public String checkAppVersion(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String platform = request.getParameter("platform");
		BQApp app = new BQApp();
		if("ios".equalsIgnoreCase(platform)) {
			app = spyMemcachedClient.get("BFX_APP_IOS");
			if (app == null) {
				 app = bqAppService.readByPlatform("IOS");
				 if (app != null) {
					 spyMemcachedClient.set("BFX_APP_IOS", BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, app);
				 }
			}
		} else {
			app = spyMemcachedClient.get("BFX_APP_Android");
			if (app == null) {
				 app = bqAppService.readByPlatform("Android");
				 if (app != null) {
					 spyMemcachedClient.set("BFX_APP_Android", BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, app);
				 }
			}
		}
		
		if (app == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂未开放");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		} else {
			JSONObject verObject = new JSONObject();
			
			String url = app.getDownload_url();
		
			if("ios".equalsIgnoreCase(platform)) {
				url = "itms-services://?action=download-manifest&url=" + url;
			} else {
				if (url.startsWith("./")) {
					url = url.substring(2, url.length());
					String basePath = Utils.getWebRoot(request);
					url = basePath + "app/" + url;
				}
			}
			
			verObject.put("version", app.getVersion());
			verObject.put("url", url);
			verObject.put("note", app.getNote());
			verObject.put("forceVersion", app.getForce_version());
			
			tickerJson.put("data", verObject);
		}

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/markets")
	public String markets(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		List<BQSpotMarket> markets = bqSpotMarketService.readAllUsing();
		for (BQSpotMarket market : markets) {
			JSONObject json = new JSONObject();
			json.put("id", ""+ market.getId());
			json.put("marketName", market.getMarket_name());
			json.put("coinName", market.getCoin_name());
			
			array.put(json);
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	
	@RequestMapping("/checkCoinAddress")
	public String checkCoinAddress(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String address = request.getParameter("address");
		String coin = request.getParameter("coin");
		String apiKey = request.getHeader("apiKey");
		
		if (StringUtil.isEmpty(address) || StringUtil.isEmpty(coin) || StringUtil.isEmpty(apiKey)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		
		String key = UrlConfig.getValue("kuangchang.apikey");
		if (!apiKey.equals(key)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数AppKey错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		tickerJson.put("data", 0);
		String market = "btc";
		if (coin.contains("_")) {
			String[] coins = coin.split("_");
			if (coins.length != 2) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数coin错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			
			coin = coins[1];
			market = coins[0];
		}
		
		if ("usdt".equalsIgnoreCase(coin)) {
			if (!"btc".equalsIgnoreCase(market) && !"eth".equalsIgnoreCase(market)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数coin错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		BQCurrencyType currencyType = bqCurrencyTypeService.getByTypeName(coin.toUpperCase());
		if (currencyType != null) {
			if ("btc".equalsIgnoreCase(market)) {
				PagerModel pm = bqMemberWalletService.readByAddress(address, currencyType.getType_id());
				if (pm.getDatas().size() > 0) {
					tickerJson.put("data", 1);
				}
			} else if ("eth".equalsIgnoreCase(market)) {
				PagerModel pm = bqMemberWalletService.readByAddress(address, 0);
				if (pm.getDatas().size() > 0) {
					tickerJson.put("data", 1);
				}
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	@RequestMapping("/usdRate")
	public String getUsdRate(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONObject obj = new JSONObject();
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.findUsdRateOnuse(1);
		}
		
		obj.put("usdRate", usdRate);
		tickerJson.put("data", obj);

		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
}
