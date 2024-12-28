package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestMapping;
import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQIndexDescription;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.BQIndexDescriptionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.BpiVO;
import com.btc100.btc_q.vo.TickerVo;
import com.google.gson.Gson;

@Controller
@RequestMapping("/bpiAction")
public class BPIAction extends BaseAction{
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQIndexDescriptionService bqIndexDescService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	
	Logger logger = Logger.getLogger(BPIAction.class);
	
	@RequestMapping("/bpiIndex")
	public String trade(HttpServletRequest request, Model model) throws JSONException {
		request.setAttribute("locale", Utils.getLocale(request));
		
		String code = request.getParameter("code");
		if (StringUtil.isEmpty(code)) return "jsp/trade_index";
		
		BQIndexDescription indexDescription = bqIndexDescService.readByCode(code);
		if (indexDescription == null) indexDescription = new BQIndexDescription();
		model.addAttribute("indexDescription", indexDescription);
		
		List<BpiVO> listBpiVo = new ArrayList<BpiVO>();
		int kind = getListBpi(code.toLowerCase(), listBpiVo);

		BigDecimal price = spyMemcachedClient.get(code.toLowerCase());
		if (price == null) price = BigDecimal.ZERO;
		model.addAttribute("price", price.doubleValue());

		model.addAttribute("listBpiVo", listBpiVo);
		model.addAttribute("coinType", "USDT");
		model.addAttribute("code", code);
		model.addAttribute("transactionKind", kind);
		
		return "jsp/trade_index";
	}
	
	@RequestMapping("/getIndex")
	public void getIndex(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject data = new JSONObject();
		String code = request.getParameter("code");

		if (StringUtil.isEmpty(code)) {
			data.put("code", "error");
			data.put("msg","合约号错误");
			toWeb(response, data);
			return;
		}
		
		JSONArray array = new JSONArray();
		List<BpiVO> listBpiVo = new ArrayList<BpiVO>();
		getListBpi(code.toLowerCase(), listBpiVo);
		for (BpiVO bpiVO : listBpiVo) {
			array.put(new JSONObject(new Gson().toJson(bpiVO)));
		}
		
		data.put("code", "success");
		data.put("msg","");
		data.put("data", array);
		
		BigDecimal btc_index_price = spyMemcachedClient.get(code.toLowerCase());
		if (btc_index_price == null) {
			BQPriceIndexCache priceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(code.toLowerCase());
			if (priceIndexCache != null) {
				btc_index_price = priceIndexCache.getPrice();
			}
		}
		
		if (btc_index_price == null) btc_index_price = BigDecimal.ZERO;
		
		data.put("indexPrice", btc_index_price);
		
		toWeb(response, data);
	}
	
	private int getListBpi(String typeCode, List<BpiVO> listBpiVo) {

		int kind = 0;
		BQTransaction transaction = findCurrTrans(typeCode);
		if (transaction != null) {
			if (transaction.getTransaction_kind() == 3) {
				getCompListBpi(typeCode, listBpiVo);
				return transaction.getTransaction_kind();
			}
			
			kind = transaction.getTransaction_kind();
		} else {
			NTransaction nTrans = findNCurrTrans("usdr", typeCode);
			if (nTrans != null) {
				if (nTrans.getTransaction_kind() == 3) {
					getCompListBpi(typeCode, listBpiVo);
					return nTrans.getTransaction_kind();
				}
			} else {
				return 0;
			}
			
			kind = nTrans.getTransaction_kind();
		}
		
		List<TickerVo> tickers = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER + typeCode);
		if (tickers == null) return kind;
		
		long nowTime = System.currentTimeMillis() / 1000;
		for (TickerVo vo : tickers) {
			BpiVO bpiVO = new BpiVO();
			bpiVO.setSiteName(vo.getMarket());
			bpiVO.setLatestPrice(vo.getLast().doubleValue());
			bpiVO.setHighestPrice(vo.getHigh().doubleValue());
			bpiVO.setLowestPrice(vo.getLow().doubleValue());
			NumberFormat percent = NumberFormat.getPercentInstance();
			percent.setMaximumFractionDigits(2);
			bpiVO.setDealCount(percent.format(vo.getRatio().doubleValue()));
			bpiVO.setRatio(vo.getRatio().doubleValue());
			bpiVO.setLatestUsdPrice(vo.getLastUsd().doubleValue());
			bpiVO.setTransPair(vo.getTrans_pair());
			Long vol = vo.getVol().longValue();
			bpiVO.setLargeVol(false);
			if (vol >= 100000L) {
				vol = vol / 10000;
				bpiVO.setLargeVol(true);
			}
			bpiVO.setVolume(vol);
			bpiVO.setUpdateTime(formatUpdateTime(nowTime, vo.getTime()));
			listBpiVo.add(bpiVO);
		}

		Collections.sort(listBpiVo);
		return kind;
	}
	
	private void getCompListBpi(String typeCode, List<BpiVO> listBpiVo) {
		List<TickerVo> compTickers = spyMemcachedClient.get(BTCQMemecacheKeys.COMPOSITE_TICKER + typeCode);
		if (compTickers == null) return;
		long nowTime = System.currentTimeMillis() / 1000;
		for (TickerVo vo : compTickers) {
			BpiVO bpiVO = new BpiVO();
			bpiVO.setSiteName(vo.getMarket());
			bpiVO.setTransPair(vo.getTrans_pair());
			bpiVO.setLatestPrice(vo.getLast().doubleValue());
			bpiVO.setLatestUsdPrice(vo.getLast().doubleValue());
			NumberFormat percent = NumberFormat.getPercentInstance();
			percent.setMaximumFractionDigits(2);
			bpiVO.setDealCount(percent.format(vo.getRatio().doubleValue()));
			bpiVO.setUpdateTime(formatUpdateTime(nowTime, vo.getTime()));
			listBpiVo.add(bpiVO);
		}
	}
	
	private String formatUpdateTime(long nowTime, long time) {
		long timeDiff = nowTime - time;
		if (timeDiff < 60) {
			return "刚刚";
		} else if (timeDiff < 120) {
			return "1分钟前";
		} else if (timeDiff < 180) {
			return "2分钟前";
		} else if (timeDiff < 240) {
			return "3分钟前";
		} else if (timeDiff < 300) {
			return "4分钟前";
		} else if (timeDiff < 360) {
			return "5分钟前";
		} else if (timeDiff < 420) {
			return "6分钟前";
		} else if (timeDiff < 480) {
			return "7分钟前";
		} else if (timeDiff < 540) {
			return "8分钟前";
		} else if (timeDiff < 600) {
			return "9分钟前";
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
	}

}
