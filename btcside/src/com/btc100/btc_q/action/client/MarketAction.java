package com.btc100.btc_q.action.client;

import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/market")
public class MarketAction extends BaseAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private KLineTradeService kLineTradeService;
	
	@RequestMapping(value = "/marketKlineAjax")
	public void marketKlineAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException, ParseException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		String bqCode = request.getParameter("bqCode");
		String timeType = request.getParameter("timeType");
		BQTransaction bqt = super.findCurrTransByCode(bqCode);
		if (bqt == null) {
			toWeb(response,SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			return;
		}
		
		String tradesStr = getTradesStr(timeType, bqCode);
		
		toWeb(response, tradesStr);
	}
	
	@RequestMapping(value = "/marketSpotKlineAjax")
	public void marketSpotKlineAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException, ParseException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		String coinPairName = request.getParameter("coinPairName");
		String timeType = request.getParameter("timeType");
		BQSpotMarketCoinPair coinPair = super.findCurrCoinPairByName(coinPairName);
		if (coinPair == null) {
			toWeb(response, SpringContext.getText("msg.common.request.errorCoinPair", Contents.Local.LOCALE.get()));
			return;
		}
		String tradesStr = getTradesStr(timeType, coinPairName);
		
		toWeb(response, tradesStr);
	}
	
	private String getTradesStr(String timeType,String code) {
		String tradesStr = null;
		if (!StringUtil.isEmpty(timeType) && "onemonth".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_one_month").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "oneweek".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_one_week").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "oneday".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "onemin".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "threemin".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "fivemin".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "fifteenmin".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "halfhour".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "onehour".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "twohours".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "fourhours".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "sixhours".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "twelvehours".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(code, "_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
	
	@RequestMapping(value = "/marketSimuKlineAjax")
	public void marketSimuKlineAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException, ParseException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		String vCode = request.getParameter("bqCode");
		String timeType = request.getParameter("timeType");
		VTransaction vt = super.findCurrVTrans(vCode);
		if (vt == null) {
			toWeb(response,SpringContext.getText("msg.common.request.errorTransaction", Contents.Local.LOCALE.get()));
			return;
		}
		
		String tradesStr = getVTradesStr(timeType, vCode);
		
		toWeb(response, tradesStr);
	}

	private String getVTradesStr(String _v_timeTypeStr,String vCode) {
		String tradesStr = null;
		if (!StringUtil.isEmpty(_v_timeTypeStr) && "oneday".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEDAY + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEDAY + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "onemin".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "threemin".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_THREEMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_THREEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "fivemin".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FIVEMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIVEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "fifteenmin".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "halfhour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_HALFHOUR + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_HALFHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "onehour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEHOUR + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "twohours".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_TWOHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWOHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "fourhours".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FOURHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FOURHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "sixhours".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_SIXHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_SIXHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "twelvehours".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_TWELVEHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.readPreJson(vCode, "_v_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWELVEHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
}
