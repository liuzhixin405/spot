package com.btc100.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.alibaba.fastjson.JSONArray;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.DATA_SessionKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQBfxMinHold;
import com.btc100.btc_q.entity.BQBfxVipSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.entity.BQExchangeTicker;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQBfxMinHoldService;
import com.btc100.btc_q.service.BQBfxVipSettingService;
import com.btc100.btc_q.service.BQExchangeSettingService;
import com.btc100.btc_q.service.BQExchangeTickerService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.SendtelcodeUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

public class BaseAction {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	private BQExchangeTickerService bqExchangeTickerService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQBfxMinHoldService bqBfxMinHoldService;
	@Autowired
	private BQBfxVipSettingService bqBfxVipSettingService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	protected NTransactionService nTransactionService;
	
	protected String getRequestRoot(HttpServletRequest request) {
		return Utils.getWebRoot(request);
	}

	protected void toWeb(HttpServletResponse response, Object object) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter objPw = response.getWriter();

			objPw.print(object);
			objPw.flush();
			objPw.close();
		} catch (Exception ex) {

		}
	}

	public static String getIpAddr(HttpServletRequest request) {
		return IpMacUtil.getIpAddr(request);
	}
	
	public JSONArray getIndexs(List<BQTransaction> transList) {
		
		String bpiData = spyMemcachedClient.get(BTCQMemecacheKeys.BPI_DATA);
		if (bpiData == null) {
			bpiData = "[]";
		}
		
		JSONArray array = JSONArray.parseArray(bpiData);
		return array;
	}
	
	public List<VTransaction> findCurrVTrans() {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		return transactionList;
	}

	/**
	 * 返回当前的合约
	 */
	public List<BQTransaction> findCurrTrans() {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		return transactionList;
	}
	
	public List<NTransaction> findNCurrTrans(String partition) {
		List<NTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
		if (transactionList == null) {
			transactionList = nTransactionService.readTransByState_new(partition, Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, transactionList);
		}
		return transactionList;
	}
	
	public VTransaction findCurrVTrans(String scode) {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		for (VTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
				return bqTransaction;
			}
		}

		return null;
	}

	public BQTransaction findCurrTrans(String scode) {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		for (BQTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
				return bqTransaction;
			}
		}

		return null;
	}
	
	public NTransaction findNCurrTrans(String partition, String scode) {
		List<NTransaction> transactionList = findNCurrTrans(partition);

		for (NTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
				return bqTransaction;
			}
		}

		return null;
	}
	
	public List<DeliveryTransaction> findCurrDTrans() {
		List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		return transactionList;
	}
	
	public DeliveryTransaction findCurrDTrans(String scode) {
		List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		for (DeliveryTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
				return bqTransaction;
			}
		}
		
		return null;
	}
	
	public List<VTransaction> findCurrVTrans(int itype) {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		List<VTransaction> bqs = new ArrayList<VTransaction>();
		for (VTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_type() == itype) {
				bqs.add(bqTransaction);
			}
		}

		return bqs;
	}

	/**
	 * 返回当前的合约
	 */
	public List<BQTransaction> findCurrTrans(int itype) {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		List<BQTransaction> bqs = new ArrayList<BQTransaction>();
		for (BQTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_type() == itype) {
				bqs.add(bqTransaction);
			}
		}

		return bqs;
	}

	public BQTransaction findCurrTrans(int itype, String scode) {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		List<BQTransaction> bqs = new ArrayList<BQTransaction>();
		for (BQTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_type() == itype) {
				bqs.add(bqTransaction);
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
					return bqTransaction;
				}
			}
		}

		return bqs.get(0);
	}

	public BQTransaction findCurrTransByCode(String code) {
		BQTransaction bqTransaction = null;
		List<BQTransaction> trans = findCurrTrans();
		for (BQTransaction bqTran : trans) {
			if (bqTran.getTransaction_code().equalsIgnoreCase(code)) {
				bqTransaction = bqTran;
				break;
			}
		}
		return bqTransaction;
	}

	/**
	 * 找到某一个指定的合约
	 * 
	 * @return
	 */
	public BQTransaction findTransByCode(String tcode) {
		BQTransaction transaction = transactionService.readTransByCode(tcode);
		return transaction;
	}
	
	
	/**
	 * 根据code查找指数
	 * 
	 * @return
	 */
	public BigDecimal findBPIByCode(String typeCode) {
		BigDecimal btc_index_price = spyMemcachedClient.get(typeCode);
		if (btc_index_price == null) {
			BQPriceIndexCache cache = bqPriceIndexCacheService.readByCode(typeCode);
			if (cache == null) return BigDecimal.ZERO;
			btc_index_price = cache.getPrice();
		}

		return btc_index_price;
	}

	/**
	 * 当前可用的所有现货交易对
	 * @return List<BQSpotMarketCoinPair>
	 */
	public List<BQSpotMarketCoinPair> findCurrCoinPair() {
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		return coinPairs;
	}

	/**
	 * 根据交易区id查找交易对列表
	 * @param marketId
	 * @return List<BQSpotMarketCoinPair>
	 */
	public List<BQSpotMarketCoinPair> findCurrCoinPairs(int marketId) {
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}

		List<BQSpotMarketCoinPair> bqs = new ArrayList<BQSpotMarketCoinPair>();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getMarket_id() == marketId) {
				bqs.add(coinPair);
			}
		}

		return bqs;
	}

	/**
	 * 根据交易对名称查找交易对
	 * @param coinPairName
	 * @return BQSpotMarketCoinPair
	 */
	public BQSpotMarketCoinPair findCurrCoinPairByName(String coinPairName) {
		BQSpotMarketCoinPair markrtCoinPair = null;
		List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getCoin_pair_name().equalsIgnoreCase(coinPairName)) {
				markrtCoinPair = coinPair;
				break;
			}
		}
		return markrtCoinPair;
	}
	
	public BQSpotMarketCoinPair findCurrCoinPairById(int coinPairId) {
		BQSpotMarketCoinPair markrtCoinPair = null;
		List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getId() == coinPairId) {
				markrtCoinPair = coinPair;
				break;
			}
		}
		return markrtCoinPair;
	}
	
//	/**
//	 * 查找BPI指数
//	 * 
//	 * @return
//	 */
//	public BigDecimal findBPI_BTC() {
//		BigDecimal btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.BPI);
//		if (btc_index_price == null) {
//			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(Contents.COIN_CURRENCY_BTC).getPrice();
//		}
//
//		return btc_index_price;
//	}
//
//	/**
//	 * 查找上证指数
//	 * 
//	 * @return
//	 */
//	public BigDecimal findBPI_SZ() {
//		BigDecimal btc_index_price = spyMemcachedClient.get(BTCQMemecacheKeys.SZI);
//		if (btc_index_price == null) {
//			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(Contents.COIN_CURRENCY_SZI).getPrice();
//		}
//		return btc_index_price;
//	}

	public BigDecimal findBPI_Other_ByCode(String typeCode) {
		String codeOther = typeCode + "_other";
		BigDecimal btc_index_price = spyMemcachedClient.get(codeOther);
		if (btc_index_price == null) {
			BQTransactionType transType = bqTransTypeService.readTransTypeByCode(typeCode);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.readByCode(transType.getType_code()).getOtherprice();
			spyMemcachedClient.set(codeOther, BTCQMemecacheKeys.TIMES.ONE_HOUR, btc_index_price);
		}
		return btc_index_price;
	}

	/**
	 * 获取一分钟交易价
	 * 
	 * @param transactionCode
	 * @return
	 
	public BigDecimal findpriceOneminago(String transactionCode) {
		BigDecimal lastpriceOneMinAgo = spyMemcachedClient.get(transactionCode + BTCQMemecacheKeys.LASTPRICEONEMINAGO);
		if (lastpriceOneMinAgo == null) {
			lastpriceOneMinAgo = tradeLogService.findLastPriceOneMinAgoByCode(transactionCode);
		}
		return lastpriceOneMinAgo;
	}
	*/

	/**
	 * 获取最新成交价
	 * 
	 * @param transactionCode
	 * @return
	 */
	public BigDecimal findpricelast(String transactionCode) {
		BigDecimal lastPrice = spyMemcachedClient.get(transactionCode + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = transactionService.readTransByCode(transactionCode).getLatest_transaction_price();
		}
		return lastPrice;
	}

	/**
	 * 加密用户的登录密码
	 * 
	 * @param pwd
	 *            登录密码
	 * @param salt
	 *            随机码
	 * @return
	 * @throws Exception
	 */
	public String encryptPwd(String pwd, String salt) throws Exception {
		pwd = MD5Service.encryptString(pwd).toLowerCase() + salt;
		pwd = MD5Service.encryptString(pwd).toLowerCase();
		return pwd;
	}

	/**
	 * 加密用户的交易密码
	 * 
	 * @param payword
	 *            交易密码
	 * @param payword
	 *            交易密码
	 * @return
	 * @throws Exception
	 */
	public String encryptPayword(String payword, String random) throws Exception {
		payword = MD5Service.encryptString(payword + random.toLowerCase()).toLowerCase();
		payword = MD5Service.encryptString(payword).toLowerCase();
		return payword;
	}

	public BQMember findCurrUser(HttpServletRequest request, boolean isupdate) {
		HttpSession session = request.getSession();
		Object o = session.getAttribute(DATA_SessionKeys.USERINFO);
		if (o == null) {
			return null;
		}

		BQMember user = (BQMember) o;
		if (isupdate) {
			user = bqMemberService.readUserById(user.getF_user_id());
			if (user == null) return null;
			session.removeAttribute(DATA_SessionKeys.USERINFO);
			if (user.getF_user_state() != 1) {
				return null;
			} else {
				session.setAttribute(DATA_SessionKeys.USERINFO, user);
			}

			return user;
		} else {
			return user;
		}
	}

	public boolean checkToken(HttpServletRequest request) {
		String _token = request.getParameter("token");
		Cookie cookie = CookieHelper.getCookie(request, "_token");
		if (cookie == null)
			return false;
		if (StringUtil.isEmpty(_token) || !_token.equalsIgnoreCase(cookie.getValue())) {
			return false;
		} else {
			return true;
		}
	}

	public int szCodeCanTrade(String bqCode, long time) throws ParseException {
		Integer nt = spyMemcachedClient.get(BTCQMemecacheKeys.Trade_Normal + "_" + bqCode);
		if (nt != null) {
			return nt;
		}

		int normalTrade = 0;
		// String strtime = bqCode.substring(2, bqCode.length()); // MMdd
//		BQTransaction bqTransaction = findCurrTrans(bqCode);
		// 交割日0点
//		int endtime = TimeUtil.getTimesmorning((int) bqTransaction.getTransaction_end_time());
		// 今天0点
//		int nomalTime = TimeUtil.getTimesmorning();
		// DateUtil.DateToUnix(DateUtil.StrToDate(DateUtil.dateToStr(DateUtil.UnixToDate(time),11)
		// + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));

		// time 周六周日
		if (TimeUtil.boleanSatudaySunday(time)) {
			return normalTrade;
		}

		BQTransaction currTrans = findCurrTrans(bqCode);
		if (currTrans.getTransaction_kind() != null && currTrans.getTransaction_kind() == 1) {
			if (bqNotradetimeService.readTradeTime(currTrans.getTransaction_id())) { // 今天可以参与交易
				normalTrade = 1;
			}
		}

		spyMemcachedClient.set(BTCQMemecacheKeys.Trade_Normal + "_" + bqCode, BTCQMemecacheKeys.TIMES.TRANSACTIONLISTSZ, normalTrade);
		return normalTrade;
	}

	public String toLogin() {
		return "redirect:/login/login.do";
	}
	
	protected Locale getLocale(HttpServletRequest request) {
		Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
		Contents.Local.LOCALE.set(locale);
		return locale;
	}

	protected List<Map<String, Object>> currTrans(List<BQTransaction> listTransactions) {

		List<Map<String,Object>> lisMap = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.CONTRACT_BASE);
		if (tmpMap == null) return lisMap;
		
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		for (BQTransaction trans : transactionList) {
			for (Map<String, Object> map : tmpMap) {
				if (map.get("typeCode").equals(trans.getTransaction_code())) {
					BigDecimal lastPrice = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
					if (lastPrice != null) {
						map.put("lastPrice", lastPrice.doubleValue());
						
						double high = (double) map.get("highPrice");
						double low = (double) map.get("lowPrice");

						boolean updateTicker = false;
						if (high < lastPrice.doubleValue() || high == 0) {
							high = lastPrice.doubleValue();
							updateTicker = true;
						}
						
						if (low > lastPrice.doubleValue() || low == 0) {
							low = lastPrice.doubleValue();
							updateTicker = true;
						}
						
						if (updateTicker) {
							map.put("highPrice", high);
							map.put("lowPrice", low);
						}
						
						double firstPrice = (double) map.get("firstPrice");
						
						// 涨跌幅
						if (lastPrice.doubleValue() <= 0.0) {
							map.put("fudu", "0%");
						} else {
							DecimalFormat df = new DecimalFormat("0.00");
							double rate = 0.0;
							if (firstPrice > 0) {
								rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
							}
							
							String div = df.format(rate *100);
							map.put("fudu", div.toString() + "%");
						}
					}
					
					lisMap.add(map);
					tmpMap.remove(map);
					break;
				}
			}
		}
		
		return lisMap;
	}
	
	protected List<Map<String, Object>> currNTrans(String partition, List<NTransaction> listTransactions) {
		List<Map<String, Object>> lisMap = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.CONTRACT_BASE + "_" + partition);
		if (tmpMap == null) return lisMap;
		
		List<NTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
		if (transactionList == null) {
			transactionList = nTransactionService.readTransByState_new(partition, Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		for (NTransaction trans : transactionList) {
			for (Map<String, Object> map : tmpMap) {
				if (map.get("typeCode").equals(trans.getTransaction_code())) {
					BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + trans.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
					if (lastPrice != null) {
						map.put("lastPrice", lastPrice.doubleValue());
						
						double high = (double) map.get("highPrice");
						double low = (double) map.get("lowPrice");

						boolean updateTicker = false;
						if (high < lastPrice.doubleValue() || high == 0) {
							high = lastPrice.doubleValue();
							updateTicker = true;
						}
						
						if (low > lastPrice.doubleValue() || low == 0) {
							low = lastPrice.doubleValue();
							updateTicker = true;
						}
						
						if (updateTicker) {
							map.put("highPrice", high);
							map.put("lowPrice", low);
						}
						
						double firstPrice = (double) map.get("firstPrice");
						
						// 涨跌幅
						if (lastPrice.doubleValue() <= 0.0) {
							map.put("fudu", "0%");
						} else {
							DecimalFormat df = new DecimalFormat("0.00");
							double rate = 0.0;
							if (firstPrice > 0) {
								rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
							}
							
							String div = df.format(rate *100);
							map.put("fudu", div.toString() + "%");
						}
					}
					
					lisMap.add(map);
					tmpMap.remove(map);
					break;
				}
			}
		}
		
		return lisMap;
	}
	
	protected List<Map<String, Object>> currDTrans(List<DeliveryTransaction> listTransactions) {
		// 当前开放合约
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_BASE2);
		
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_BASE);
			if (tmpMap == null) return lisMap;
			
			List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
			if (transactionList == null) {
				transactionList = dTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
			}
			for (DeliveryTransaction trans : transactionList) {
				for (Map<String, Object> map : tmpMap) {
					if (map.get("tranCode").equals(trans.getTransaction_code())) {
						lisMap.add(map);
						tmpMap.remove(map);
						break;
					}
				}
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.DELIVERY_BASE2, BTCQMemecacheKeys.TIMES.TWO_SECOND, lisMap);
		}
		
		return lisMap;
	}
	
	protected List<Map<String, Object>> currVTrans(List<VTransaction> listTransactions) {
		// 当前开放合约
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.V_CONTRACT_BASE);
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String, Object>>();
		}
		
		return lisMap;
	}
	
	protected List<Map<String, Object>> currCoinPair() {
		// 当前开放合约
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_BASE2);
		
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_BASE);
			List<BQSpotMarketCoinPair> transactionList = findCurrCoinPair();			
			Map<String, Object> bfxusdrmap = null;
			for (BQSpotMarketCoinPair trans : transactionList) {
				if (tmpMap != null) {
					for (Map<String, Object> map : tmpMap) {
						if (map.get("pairName").equals(trans.getCoin_pair_name())) {
							if (trans.getCoin_pair_name().equalsIgnoreCase("bfxusdr")) {
								bfxusdrmap = map;
							} else {
								lisMap.add(map);
							}
							tmpMap.remove(map);
							break;
						}
					}
				}
			}
			if (bfxusdrmap != null) {
				lisMap.add(0, bfxusdrmap);
			}
			
			
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_BASE2, BTCQMemecacheKeys.TIMES.ONE_SECOND, lisMap);
		}
		
		return lisMap;
	}

	/**
	 * 获取市场外部的买一价或卖一价
	 * 
	 * @param originCoin
	 * @param targetCoin
	 * @return
	 * @throws JSONException
	 */
	protected BigDecimal getBuyOrSellPrice(BQCurrencyType originCoin, BQCurrencyType targetCoin) throws JSONException {
		if (originCoin.getType_id() == targetCoin.getType_id()) {
			return BigDecimal.ONE;
		}
		
		BigDecimal price = BigDecimal.ZERO; // 外部市场现价
		if ("USDT".equals(originCoin.getType_name())) { // USDT兑换=>BTC/ETH/EOS
			
			BQExchangeSetting setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
			if (setting.getInside_exchange()) {
				return setting.getExchange_rate();
			}
			
			
			TickerVo ticker = spyMemcachedClient.get(BTCQMemecacheKeys.EXCHANGE_TICKER + targetCoin.getType_name()); // 从缓存中取出行情
			if (null != ticker) {
				price = ticker.getSell();
			}
			
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				BQExchangeTicker exchangeTicker = bqExchangeTickerService.readExchangeTickerByCurrencyType(targetCoin.getType_id());
				if (exchangeTicker != null) {
					price = exchangeTicker.getSell();
				}
			}
			
			if (price == null) return BigDecimal.ZERO;
		}
		
		if ("USDT".equals(targetCoin.getType_name())) { // BTC/ETH/EOS兑换=>USDT
			BQExchangeSetting setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
			if (setting.getInside_exchange()) {
				return setting.getExchange_rate();
			}
			
			TickerVo ticker = spyMemcachedClient.get(BTCQMemecacheKeys.EXCHANGE_TICKER + originCoin.getType_name()); // 从缓存中取出行情
			if (null != ticker) {
				price = ticker.getBuy();
			}
			
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				BQExchangeTicker exchangeTicker = bqExchangeTickerService.getExchangeTickerByCurrencyType(originCoin.getType_id());
				if (exchangeTicker != null) {
					price = exchangeTicker.getBuy();
				}
			}
			
			if (price == null) return BigDecimal.ZERO;
		}
		
		return price;
	}
	
	public String sendTelCode(long userId, String phone, int code) throws IOException {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDay = sdf.format(d);

		Integer sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
		if (sendLimit != null && sendLimit > Contents.TEL_CODE_LIMIT) {
			return "超过日发送限额";
		}

		String sendRet = SendtelcodeUtil.sendTelCode(phone, code);
		if (StringUtil.isEmpty(sendRet)) {
			sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
			if (sendLimit == null) sendLimit = 0;
			sendLimit += 1;

			spyMemcachedClient.set(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId, BTCQMemecacheKeys.TIMES.ONE_DAY, sendLimit);
		}

		return sendRet;
	}

	public String sendTelCode(long userId, String phone, int code, String zone) throws IOException {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDay = sdf.format(d);

		Integer sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
		if (sendLimit != null && sendLimit > Contents.TEL_CODE_LIMIT) {
			return "超过日发送限额";
		}

		String sendRet = SendtelcodeUtil.sendTelCode(zone, phone, code);
		if (StringUtil.isEmpty(sendRet)) {
			sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
			if (sendLimit == null) sendLimit = 0;
			sendLimit += 1;

			spyMemcachedClient.set(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId, BTCQMemecacheKeys.TIMES.ONE_DAY, sendLimit);
		}

		return sendRet;
	}
	
	public BQMemberLevel getLevelByUserBfx(BQMember user) {
		if (user == null) return null;
		
		BQMemberLevel userlevel = bqMemberLevelService.findByLevelId(user.getF_user_level_id());
		if (userlevel.getEat_commission_rate().compareTo(BigDecimal.ZERO) == 0) {
			return userlevel;
		}
		
		if (user.getF_user_oms() != 0) {
			return userlevel;
		}
		
		BQBfxFeeSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (setting == null) {
			setting = bqBfxFeeSettingService.readOne();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null) {
			return userlevel;
		}
		
		// 昨日
		long dayTime = TimeUtil.getTimesmorning(System.currentTimeMillis() - 24 * 3600 * 1000);
		if (setting.getVip_state() != 1) {
			return userlevel;
		}
		
		BQBfxMinHold minHold = bqBfxMinHoldService.readBfxMinHoldByUserAndTime(user.getF_user_id(), dayTime);
		if (minHold != null && minHold.getMin_hold().compareTo(BigDecimal.ZERO) > 0) {
			List<BQBfxVipSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_VIP_SETTINGS);
			if (settings == null) {
				settings = bqBfxVipSettingService.readAll();
				if (settings != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.BFX_VIP_SETTINGS, BTCQMemecacheKeys.TIMES.ONE_MIN, settings);
				}
			}
			
			BQBfxVipSetting vipSetting = null;
			for (BQBfxVipSetting vipSet : settings) {
				if (minHold.getMin_hold().compareTo(vipSet.getHold_count()) >= 0) {
					vipSetting = vipSet;
					break;
				}
			}
			
			if (vipSetting != null) {
				if (vipSetting.getLevel_id() > userlevel.getLevel_id()) {
					BQMemberLevel level =  bqMemberLevelService.findByLevelId(vipSetting.getLevel_id());
					if (level != null) {
						return level;
					}
				}
			}
		}
		
		return userlevel;
	}
	
	protected String formatEmail(String email) {
		String[] emails = email.split("@");
		if (emails.length > 1) {
			String name = emails[0];
			if (name.length() < 4) {
				email = name + "****@" + emails[1];
			} else {
				email = name.substring(0, 3) + "****" + name.substring(name.length() - 1, name.length()) + "@" + emails[1];
			}
		} else {
			if (email.length() < 4) {
				email = email.substring(0, 1) + "****" + email.substring(email.length() - 1, email.length());
			} else {
				email = email.substring(0, 3) + "****" + email.substring(email.length() - 1, email.length());
			}
		}

		return email;
	}
}
