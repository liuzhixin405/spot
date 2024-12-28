package com.btc100.btc_q.action.api.open.v1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.action.api.BaseApiAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.util.Utils;

@Controller
@RequestMapping("/v1/common")
public class CommonOpenApiActionV1 extends BaseApiAction  {
	
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	/**
	 * 获取所有合约品种和现货交易对
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/symbols")
	public String symbols(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
			List<DeliveryTransaction> transactionList = findCurrDTrans();
			for (DeliveryTransaction bq : transactionList) {
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
				List<NTransaction> transactionList = findNCurrTrans(part.getPart());
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
	
	/**
	 * 获取所有币种
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/currencys")
	public String currencys(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
	
}
