package com.btc100.btc_q.action.api.open.v1;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.action.api.BaseApiAction;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

@Controller
@RequestMapping("/v1/market")
public class MarketOpenApiActionV1 extends BaseApiAction {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private DeliveryMemberTradeDetailService dMemberTradeDetailService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private DeliveryTradeLogService dTradeLogService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NTradeLogService nTradeLogService;
	@Autowired
	private NPendingDelegationService nPendingDelegationService;
	@Autowired
	private NMemberTradeDetailService nMemberTradeDetailService;
	
	@RequestMapping("/ticker")
	public String ticker(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		String code = request.getParameter("symbol");
		if (StringUtil.isEmpty(code)) {
			model.addAttribute("ticker", createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol为空"));
			return apiData(response); 
		}
		
		String ticker = "";
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			ticker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC + "_" + code);
		} else if ("spot".equals(type)) {
			ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + code);
		} else if ("delivery".equals(type)) {
			ticker = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_BTC + "_" + code);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + code);
		} else {
			ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + type + "_" + code);
		}
		
		ticker = ticker == null ? "{}" : ticker;
		
		JSONObject json = new JSONObject(ticker);
		Object obj = json.remove("bqCode");
		if (obj != null) {
			json.put("symbol", obj);
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		tickerJson.put("data", json);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 市场行情 获取所有市场行情，最新价，最高价，最低价，成交量，买一价和卖一价
	 * 
	 * @param model
	 * @return
	 * @throws JSONException 
	 */
	@RequestMapping("/tickers")
	public String tickers(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		String type = request.getParameter("type");
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		if ("spot".equals(type)) {
			List<BQSpotMarketCoinPair> transactionList = super.findCurrCoinPair();
			for (BQSpotMarketCoinPair bq : transactionList) {
				String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + bq.getCoin_pair_name());
				ticker = ticker == null ? "{}" : ticker;
				
				JSONObject json = new JSONObject(ticker);
				Object obj = json.remove("bqCode");
				if (obj != null) {
					json.put("symbol", obj);
				}
				
				array.put(json);
			}
		} else if ("vcontract".equals(type)) { 
			List<VTransaction> transactionList = findCurrVTrans();
			for (VTransaction bq : transactionList) {
				String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC + "_" + bq.getTransaction_code());
				ticker = ticker == null ? "{}" : ticker;
				JSONObject json = new JSONObject(ticker);
				Object obj = json.remove("bqCode");
				if (obj != null) {
					json.put("symbol", obj);
				}
				
				BigDecimal first = spyMemcachedClient.get(bq.getTransaction_code() + BTCQMemecacheKeys.V_FIRST_PRICE);
				if (first == null) {
					first = vTradeLogService.readFirstPrice(bq.getTransaction_code(), TimeUtil.getTimesmorning());
					spyMemcachedClient.set(bq.getTransaction_code() + BTCQMemecacheKeys.V_FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
				}
				
				double lastPrice = json.getDouble("last");
				double firstPrice = lastPrice;
				if (first != null) {
					firstPrice = first.doubleValue();
				}
				
				// 涨跌幅
				if (lastPrice <= 0.0) {
					json.put("change", "0");
				} else {
					DecimalFormat df = new DecimalFormat("0.00");
					double rate = 0.0;
					if (firstPrice > 0) {
						rate =  (lastPrice - firstPrice) / firstPrice;
					}
					
					String div = df.format(rate *100);
					json.put("change", div.toString());
				}
				
				array.put(json);
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryTransaction> transactionList = findCurrDTrans();
			for (DeliveryTransaction bq : transactionList) {
				String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_BTC + "_" + bq.getTransaction_code());
				if (StringUtil.isEmpty(ticker)) {
					continue;
				}
				
				JSONObject json = new JSONObject(ticker);
				Object obj = json.remove("bqCode");
				if (obj != null) {
					json.put("symbol", obj);
				}
				
				String change = spyMemcachedClient.get(bq.getTransaction_code() + "_d_ticker_change");
				if (StringUtil.isEmpty(change)) {
					BigDecimal first = spyMemcachedClient.get(bq.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE);
					if (first == null) {
						first = dTradeLogService.readFirstPrice(bq.getTransaction_code(), TimeUtil.getTimesmorning());
						spyMemcachedClient.set(bq.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
					}
					
					double lastPrice = json.getDouble("last");
					double firstPrice = lastPrice;
					if (first != null) {
						firstPrice = first.doubleValue();
					}
					
					// 涨跌幅
					if (lastPrice <= 0.0) {
						change = "0";
					} else {
						DecimalFormat df = new DecimalFormat("0.00");
						double rate = 0.0;
						if (firstPrice > 0) {
							rate =  (lastPrice - firstPrice) / firstPrice;
						}
						
						String div = df.format(rate *100);
						change = div.toString();
					}
					
					spyMemcachedClient.set(bq.getTransaction_code() + "_d_ticker_change", BTCQMemecacheKeys.TIMES.TWO_SECOND, change);
				}
				json.put("change", change);
				array.put(json);
			}
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			List<BQTransaction> transactionList = findCurrTrans();
			for (BQTransaction bq : transactionList) {
				String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + bq.getTransaction_code());
				if (StringUtil.isEmpty(ticker)) {
					continue;
				}
				
				JSONObject json = new JSONObject(ticker);
				Object obj = json.remove("bqCode");
				if (obj != null) {
					json.put("symbol", obj);
				}
				
				String change = spyMemcachedClient.get(bq.getTransaction_code() + "_ticker_change");
				if (StringUtil.isEmpty(change)) {
					BigDecimal first = spyMemcachedClient.get(bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE);
					if (first == null) {
						first = tradeLogService.readFirstPrice(bq.getTransaction_code(), TimeUtil.getTimesmorning());
						spyMemcachedClient.set(bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
					}
					
					double lastPrice = json.getDouble("last");
					double firstPrice = lastPrice;
					if (first != null) {
						firstPrice = first.doubleValue();
					}
					
					// 涨跌幅
					if (lastPrice <= 0.0) {
						change = "0";
					} else {
						DecimalFormat df = new DecimalFormat("0.00");
						double rate = 0.0;
						if (firstPrice > 0) {
							rate =  (lastPrice - firstPrice) / firstPrice;
						}
						
						String div = df.format(rate *100);
						change = div.toString();
					}
					
					spyMemcachedClient.set(bq.getTransaction_code() + "_ticker_change", BTCQMemecacheKeys.TIMES.TWO_SECOND, change);
				}
				json.put("change", change);
				array.put(json);
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NTransaction> transactionList = findNCurrTrans(part.getPart());
				for (NTransaction bq : transactionList) {
					String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + part.getPart() + "_" + bq.getTransaction_code());
					if (StringUtil.isEmpty(ticker)) {
						continue;
					}
					
					JSONObject json = new JSONObject(ticker);
					Object obj = json.remove("bqCode");
					if (obj != null) {
						json.put("symbol", obj);
					}
					
					String change = spyMemcachedClient.get(part.getPart() + "_" + bq.getTransaction_code() + "_ticker_change");
					if (StringUtil.isEmpty(change)) {
						BigDecimal first = spyMemcachedClient.get(part.getPart() + "_" + bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE);
						if (first == null) {
							first = nTradeLogService.readFirstPrice(part.getPart(), bq.getTransaction_code(), TimeUtil.getTimesmorning());
							spyMemcachedClient.set(part.getPart() + "_" + bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
						}
						
						double lastPrice = json.getDouble("last");
						double firstPrice = lastPrice;
						if (first != null) {
							firstPrice = first.doubleValue();
						}
						
						// 涨跌幅
						if (lastPrice <= 0.0) {
							change = "0";
						} else {
							DecimalFormat df = new DecimalFormat("0.00");
							double rate = 0.0;
							if (firstPrice > 0) {
								rate =  (lastPrice - firstPrice) / firstPrice;
							}
							
							String div = df.format(rate *100);
							change = div.toString();
						}
						
						spyMemcachedClient.set(part.getPart() + "_" + bq.getTransaction_code() + "_ticker_change", BTCQMemecacheKeys.TIMES.TWO_SECOND, change);
					}
					json.put("change", change);
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
	 * 市场深度 获取市场深度（委托挂单），数量：20
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/depth")
	public String depth(HttpServletRequest request, HttpServletResponse response, Model model) {
		String type =  request.getParameter("type");
		String bfx = request.getParameter("bfx");
		String code = request.getParameter("symbol");
		String transType = request.getParameter("transType");

		String depthStr = "";
		if (code == null) {
			depthStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol为空");
		} else {
			
			boolean isExistCode = false;
			int digits= 0;
			int amountDigits = 0;
			if ("spot".equals(type)) {
				BQSpotMarketCoinPair coinPair = findCurrCoinPairByName(code);
				if (coinPair == null) {
					depthStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", depthStr);
					return apiData(response);
				}
				
				digits = getSpotDigits(code);
				amountDigits = coinPair.getDecimals();
				isExistCode = true;
			} else if ("vcontract".equals(type)) { 
				List<VTransaction> transactionList = findCurrVTrans();
				VTransaction trans = null;
				
				for (VTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
				
				if (trans == null) {
					depthStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", depthStr);
					return apiData(response);
				}
				
				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else if ("delivery".equals(type)) {
				List<DeliveryTransaction> transactionList = findCurrDTrans();
				DeliveryTransaction trans = null;
				
				for (DeliveryTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
				
				if (trans == null) {
					depthStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", depthStr);
					return apiData(response);
				}
				
				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else if (type == null || "".equals(type) || "contract".equals(type)) {
				List<BQTransaction> transactionList = findCurrTrans();
				BQTransaction trans = null;
				
				for (BQTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
				
				if (trans == null) {
					depthStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", depthStr);
					return apiData(response);
				}
				
				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else {
				NContractPartition part = nContractPartitionService.readByPartition(type);
				if (part != null) {
					NTransaction transaction = findNCurrTrans(part.getPart(), code);
					if (transaction == null) {
						depthStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
						model.addAttribute("ticker", depthStr);
						return apiData(response);
					}
					
					digits = getDigits(code, type);
					amountDigits = Utils.getAmountDigtis(code);
				} else {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			JSONObject depthJson = new JSONObject();
			String data = "";
			String sell = "asks", buy = "bids";
			if (isExistCode) {
				if ("vcontract".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.V_DEPTH_CODE + code.toLowerCase());
				} else if ("spot".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase());
				} else if ("delivery".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.D_DEPTH_CODE + code.toLowerCase());
				} else if (type == null || "".equals(type) || "contract".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase());
				} else {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + type + "_" + code.toLowerCase());
				}
				if (data == null) {
					if ("spot".equals(type)) {
						data = spotPendingDelegationService.readDepth(code, 20);
					} else if ("vcontract".equals(type)) {
						data = vPendingDelegationService.readDepth(code, 20);
					} else if ("delivery".equals(type)) {
						data = dPendingDelegationService.readDepth(code, 20);
					} else if (type == null || "".equals(type) || "contract".equals(type)) {
						data = pendingDelegationService.readDepth(code, 20);
					} else {
						data = nPendingDelegationService.readDepth(type, code, 20);
					}
					
					if ("vcontract".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.V_DEPTH_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, data);
					} else if ("spot".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, data);
					} else if ("delivery".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_DEPTH_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, data);
					} else if (type == null || "".equals(type) || "contract".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, data);
					} else {
						spyMemcachedClient.set(BTCQMemecacheKeys.DEPTH_CODE + type + "_" + code.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, data);
					}
				}

				if (!"baocang".equals(transType)) {
					try {
						JSONObject transDepth = new JSONObject(data);
						JSONArray asks = (JSONArray) transDepth.get("asks");
						JSONArray bids = (JSONArray) transDepth.get("bids");
						JSONObject json = new JSONObject();

						List<JSONArray> lstBuyArray = new ArrayList<JSONArray>();
						for (int i = 0; i < asks.length(); i++) {
							JSONArray a = asks.getJSONArray(i);
							JSONArray array = new JSONArray();
							array.put(a.get(0));
							array.put(a.get(1));
							lstBuyArray.add(array);
						}
						List<JSONArray> lstSellArray = new ArrayList<JSONArray>();
						for (int i = 0; i < bids.length(); i++) {
							JSONArray a = bids.getJSONArray(i);
							JSONArray array = new JSONArray();
							array.put(a.get(0));
							array.put(a.get(1));
							lstSellArray.add(array);
						}
						json.put(sell, lstBuyArray);
						json.put(buy, lstSellArray);
						data = json.toString();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				data = (data == null || data.isEmpty()) ? "" : data;
				
				try {
					depthJson.put("code", 0);
					depthJson.put("msg", "");
					depthJson.put("data", new JSONObject(data));
					
					if (!StringUtil.isEmpty(bfx)) {
						depthJson.put("digits", digits);
						depthJson.put("amountDigits", amountDigits);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				depthStr = depthJson.toString();
			} else {
				depthStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
			}
		}

		model.addAttribute("ticker", depthStr);
		return apiData(response);
	}
	
	@RequestMapping(value = "/kline")
	public String kline(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException, ParseException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject trans = new JSONObject();
		trans.put("code", 0);
		trans.put("msg", "");
		
		String bqCode = request.getParameter("symbol");
		String timeType = request.getParameter("interval");
		String type =  request.getParameter("type");
		int last =  Convert.toInt(request.getParameter("last"), 0);
		
		if ("spot".equals(type)) {
			com.alibaba.fastjson.JSONArray arr = WebSocket.getSpotKline(bqCode, timeType, last == 1);
			JSONArray array = new JSONArray(arr.toString());
			
			int digits = getSpotDigits(bqCode);
			trans.put("data", array);
			trans.put("digits", digits);
			
		} else if ("vcontract".equals(type)) {
			com.alibaba.fastjson.JSONArray arr = WebSocket.getVKline(bqCode, timeType, last == 1);
			JSONArray array = new JSONArray(arr.toString());
			
			int digits = getDigits(bqCode, type);
			trans.put("data", array);
			trans.put("digits", digits);
		} else if ("delivery".equals(type)) {
			com.alibaba.fastjson.JSONArray arr = WebSocket.getDKline(bqCode, timeType, last == 1);
			JSONArray array = new JSONArray(arr.toString());
			
			int digits = getDigits(bqCode, type);
			trans.put("data", array);
			trans.put("digits", digits);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			com.alibaba.fastjson.JSONArray arr = WebSocket.getKline(bqCode, timeType, last == 1);
			JSONArray array = new JSONArray(arr.toString());
			
			int digits = getDigits(bqCode, type);
			trans.put("data", array);
			trans.put("digits", digits);
		} else {
			com.alibaba.fastjson.JSONArray arr = WebSocket.getNKline(type, bqCode, timeType, last == 1);
			JSONArray array = new JSONArray(arr.toString());
			
			int digits = getDigits(bqCode, type);
			trans.put("data", array);
			trans.put("digits", digits);
		}
		
		model.addAttribute("ticker", trans);
		return apiData(response);
	}
	
	/**
	 * 交易记录 获取历史成交记录，数量：50
	 * 
	 * @param model
	 * @return
	 * @throws JSONException 
	 */
	@RequestMapping("/trades")
	public String trades(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {

		String bfx = request.getParameter("bfx");
		String type = request.getParameter("type");
		String code = request.getParameter("symbol");
		String tradesStr = "";
		int digits = 0, amountDigits = 0;
		if (code == null ) {
			tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，symbol为空");
		} else {
			boolean isExistCode = false;
			if ("spot".equals(type)) {
				BQSpotMarketCoinPair coinPair = findCurrCoinPairByName(code);
				if (coinPair == null) {
					tradesStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				digits = getSpotDigits(code);
				amountDigits = coinPair.getDecimals();
				isExistCode = true;
			} else if ("vcontract".equals(type)) {
				List<VTransaction> transactionList = findCurrVTrans();
				VTransaction trans = null;
				for (VTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
	
				if (trans == null) {
					tradesStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else if ("delivery".equals(type)) {
				List<DeliveryTransaction> transactionList = findCurrDTrans();
				DeliveryTransaction trans = null;
				for (DeliveryTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
	
				if (trans == null) {
					tradesStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else if (type == null || "".equals(type) || "contract".equals(type)) {
				List<BQTransaction> transactionList = findCurrTrans();
				BQTransaction trans = null;
				for (BQTransaction bq : transactionList) {
					if (bq.getTransaction_code().equalsIgnoreCase(code)) {
						isExistCode = true;
						trans = bq;
						break;
					}
				}
	
				if (trans == null) {
					tradesStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				digits = getDigits(code, type);
				amountDigits = Utils.getAmountDigtis(code);
			} else {
				NContractPartition part = nContractPartitionService.readByPartition(type);
				if (part != null) {
					NTransaction trans = findNCurrTrans(part.getPart(), code);
					if (trans == null) {
						tradesStr = this.createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
						model.addAttribute("ticker", tradesStr);
						return apiData(response);
					}
	
					digits = getDigits(code, part.getPart());
					amountDigits = Utils.getAmountDigtis(code);
				} else {
					tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}

			JSONObject tradeJson = new JSONObject();
			String data = "";
			if (isExistCode) {
				if ("vcontract".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_CODE + code.toLowerCase());
				} else if ("spot".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase());
				} else if ("delivery".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRADE_CODE + code.toLowerCase());
				} else if (type == null || "".equals(type) || "contract".equals(type)) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase());
				} else {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + type + "_" + code.toLowerCase());
				}
				if (data == null) {
					if ("spot".equals(type)) {
						data = spotMemberTradeDetailService.readAllJsonTopN(code, Contents.TRADE_JSON).toString();
					} else if ("vcontract".equals(type)) {
						data = vMemberTradeDetailService.readAllJsonTopN(code, Contents.TRADE_JSON).toString();
					} else if ("delivery".equals(type)) {
						data = dMemberTradeDetailService.readAllJsonTopN(code, Contents.TRADE_JSON).toString();
					} else if (type == null || "".equals(type) || "contract".equals(type)) {
						data = memberTradeDetailService.readAllJsonTopN(code, Contents.TRADE_JSON).toString();
					} else {
						data = nMemberTradeDetailService.readAllJsonTopN(type, code, Contents.TRADE_JSON).toString();
					}
					if ("vcontract".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.TRADE_CODE, data);
					} else if ("spot".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.TRADE_CODE, data);
					} else if ("delivery".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_TRADE_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.TRADE_CODE, data);
					} else if (type == null || "".equals(type) || "contract".equals(type)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase(), BTCQMemecacheKeys.TIMES.TRADE_CODE, data);
					} else {
						spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_CODE + type + "_" + code.toLowerCase(), BTCQMemecacheKeys.TIMES.TRADE_CODE, data);
					}
				}
				
				data = (data == null || data.isEmpty()) ? "[]" : data;
				
				try {
					tradeJson.put("code", 0);
					tradeJson.put("msg", "");
					tradeJson.put("data", new JSONArray(data));
					
					if (!StringUtil.isEmpty(bfx)) {
						tradeJson.put("digits", digits);
						tradeJson.put("amountDigits", amountDigits);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				tradesStr = tradeJson.toString();
			} else {
				tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知symbol");
			}
		}
		
		model.addAttribute("ticker", tradesStr);
		return apiData(response);
	}
}
