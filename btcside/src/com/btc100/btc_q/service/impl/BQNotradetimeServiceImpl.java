package com.btc100.btc_q.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQNotradetimeDao;
import com.btc100.btc_q.dao.BQStockSettingDao;
import com.btc100.btc_q.entity.BQNotradetime;
import com.btc100.btc_q.entity.BQStockSetting;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqNotradetimeService")
public class BQNotradetimeServiceImpl implements BQNotradetimeService {

	@Autowired
	private BQNotradetimeDao<BQNotradetime> bqNotradetimeDao;
	@Autowired
	private BQStockSettingDao<BQStockSetting> bqStockSettingDao;
	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	
	private boolean tradeTime(long transactionId) {
		long currTime = System.currentTimeMillis();
	 	long time = TimeUtil.getTimesmorning(currTime);
	 	
	 	Boolean sfzc = spyMemcachedClient.get(BTCQMemecacheKeys.Trade_Normal + "_" + transactionId + "_" + time);
	 	if (sfzc == null) {
	 		Boolean zcm = true;
	 		Map<String, Object> params = new HashMap<String, Object>();
	 		params.put("time", time);
	 		params.put("transactionId", transactionId);
	 		List<BQNotradetime> lstNotradetimes = bqNotradetimeDao.find("From BQNotradetime where h_time = :time and transaction_id = :transactionId", params, 1, 1);
	 		
	 		if (lstNotradetimes == null || lstNotradetimes.size() <= 0) {
	 			zcm = true;
	 		} else
	 			zcm = false;
	 		
	 		sfzc = zcm;
	 		
	 		spyMemcachedClient.set(BTCQMemecacheKeys.Trade_Normal + "_" + transactionId + "_" + time, BTCQMemecacheKeys.TIMES.ONE_MIN, sfzc);
	 	}
	 	
	 	if (!sfzc) {
	 		return false;
	 	}
	 	
	 	// 分钟开始
 		long minuteStart = currTime / 1000 / 60 * 60;
 		Boolean tradeTime = spyMemcachedClient.get(BTCQMemecacheKeys.STOCK_TRADE_TIME + transactionId + "_" + minuteStart);
 		if (tradeTime != null) {
 			return tradeTime;
 		}
	 	
	 	BQStockSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.STOCK_SETTING + "_" + transactionId);
		if (setting == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("transactionId", transactionId);
			String hql = "from BQStockSetting where transaction_id = :transactionId";
			List<BQStockSetting> list = bqStockSettingDao.find(hql, params);
			if (list.size() > 0) {
				setting = list.get(0);
			}
			
			if (setting != null)
				spyMemcachedClient.set(BTCQMemecacheKeys.STOCK_SETTING + "_" + transactionId, BTCQMemecacheKeys.TIMES.ONE_MIN, setting);
		}
		
		if (setting == null) return false;
		
		Boolean isTradeDay = false;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTimeInMillis(currTime);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		String tradeTimeStr = "";
		switch (weekDay) {
		case 1:
			isTradeDay = setting.getSun_state();
			tradeTimeStr = setting.getSun_trade_time();
			break;
		case 2:
			isTradeDay = setting.getMon_state();
			tradeTimeStr = setting.getMon_trade_time();
			break;
		case 3:
			isTradeDay = setting.getTue_state();
			tradeTimeStr = setting.getTue_trade_time();
			break;
		case 4:
			isTradeDay = setting.getWed_state();
			tradeTimeStr = setting.getWed_trade_time();
			break;
		case 5:
			isTradeDay = setting.getThu_state();
			tradeTimeStr = setting.getThu_trade_time();
			break;
		case 6:
			isTradeDay = setting.getFri_state();
			tradeTimeStr = setting.getFri_trade_time();
			break;
		case 7:
			isTradeDay = setting.getSat_state();
			tradeTimeStr = setting.getSat_trade_time();
			break;

		default:
			break;
		}
		
		sfzc = isTradeDay;
		if (!sfzc) {
	 		spyMemcachedClient.set(BTCQMemecacheKeys.STOCK_TRADE_TIME + transactionId + "_" + minuteStart, BTCQMemecacheKeys.TIMES.TWO_MIN, false);
	 		return false;
	 	}
		
		if (StringUtil.isEmpty(tradeTimeStr)) {
	 		spyMemcachedClient.set(BTCQMemecacheKeys.STOCK_TRADE_TIME + transactionId + "_" + minuteStart, BTCQMemecacheKeys.TIMES.TWO_MIN, false);
	 		return false;
		}
		
		String[] tradeTimArr = tradeTimeStr.split(",");
		Boolean isTradeTime = false;
		for (String trTime : tradeTimArr) {
			isTradeTime = checkTradeTime(trTime, minuteStart - time);
			if (isTradeTime) break;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.STOCK_TRADE_TIME + transactionId + "_" + minuteStart, BTCQMemecacheKeys.TIMES.TWO_MIN, isTradeTime);
		
		return isTradeTime;
	}
	
	private Boolean checkTradeTime(String tradeTime, long todayTime) {
		Boolean isTrade = false;
		if (StringUtil.isEmpty(tradeTime))
			return isTrade;

		if (!tradeTime.contains("-") || !tradeTime.contains(":"))
			return isTrade;

		String startStr = tradeTime.substring(0, tradeTime.indexOf("-"));
		String endStr = tradeTime.substring(tradeTime.indexOf("-") + 1, tradeTime.length());
		
		String[] startHourMinute = startStr.split(":");
		if (startHourMinute.length != 2) {
			return isTrade;
		}
		
		int hour = Integer.valueOf(startHourMinute[0]);
		int minute = Integer.valueOf(startHourMinute[1]);
		
		if (hour < 0 || hour > 24 || minute < 0 || minute > 60) {
			return isTrade;
		}
		
		long startTime = hour * 3600 + minute * 60;
		
		String[] endHourMinute = endStr.split(":");
		if (endHourMinute.length != 2) {
			return isTrade;
		}
		
		hour = Integer.valueOf(endHourMinute[0]);
		minute = Integer.valueOf(endHourMinute[1]);
		
		if (hour < 0 || hour > 24 || minute < 0 || minute > 60) {
			return isTrade;
		}
		
		long endTime = hour * 3600 + minute * 60;
		
		if (todayTime >= startTime && todayTime <= endTime) {
			isTrade = true;
		}

		return isTrade;
	}

	public boolean isTradeTime(long transactionId) {
		return tradeTime(transactionId);
	}
	
	public boolean readTradeTime(long transactionId) {
		return tradeTime(transactionId);
	}
	
	public void add(BQNotradetime noTrade) {
		bqNotradetimeDao.save(noTrade);
	}

	public BQNotradetime getById(long id) {
		return bqNotradetimeDao.get(BQNotradetime.class, id);
	}

	public void deleteById(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqNotradetimeDao.deleteCondition("delete from BQNotradetime where h_id = :id", params);
	}

	public void updateById(long id, long time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("time", time);
		bqNotradetimeDao.updateCondition("update BQNotradetime set h_time = :time where h_id = :id", params);
	}

	public void addList(List<BQNotradetime> lstNotrades) {
		for (BQNotradetime notrade : lstNotrades) {
			bqNotradetimeDao.save(notrade);
		}
	}

	public PagerModel findAllpage(long time, int type, long transactionId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		
		String hql = "From BQNotradetime where h_type = :type";
		
		if (time > 0) {
			params.put("time", time);
			hql += " and h_time = :time";
		}
		
		if (transactionId > 0) {
			params.put("transactionId", transactionId);
			hql += " and transaction_id = :transactionId";
		}
		
		hql += " order by h_id desc";
		
		return bqNotradetimeDao.searchHql(hql, params);
	}

}
