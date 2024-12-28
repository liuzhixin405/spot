package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.KLineTradeDao;
import com.btc100.btc_q.entity.KLineTrade;
import com.btc100.btc_q.service.KLineTradeService;

@Service("kLineTradeService")
public class KLineTradeServiceImpl implements KLineTradeService {

	@Autowired
	private KLineTradeDao kLineTradeDao;

	@Override
	public void insert(KLineTrade trade, String timeType) {
		kLineTradeDao.insert(trade, timeType);
	}
	
	@Override
	public KLineTrade updateFromOneMinute(KLineTrade oneMinuteTrade, String timeType, long time) {
		KLineTrade newKline = null;
		KLineTrade kline = kLineTradeDao.getAtTime(oneMinuteTrade.getCode(), timeType, time);
		if (kline == null) {
			newKline = new KLineTrade(oneMinuteTrade);
			newKline.setTime(time);
			kLineTradeDao.insert(newKline, timeType);
		} else {
			newKline = new KLineTrade(kline);
			newKline.setLast(oneMinuteTrade.getLast());
			newKline.setVol(newKline.getVol().add(oneMinuteTrade.getVol()));
			if (newKline.getHigh().compareTo(oneMinuteTrade.getHigh()) < 0) {
				newKline.setHigh(oneMinuteTrade.getHigh());
			}
			
			if (newKline.getLow().compareTo(oneMinuteTrade.getLow()) > 0) {
				newKline.setLow(oneMinuteTrade.getLow());
			}
			
			newKline.setLast(oneMinuteTrade.getLast());
			kLineTradeDao.update(newKline, timeType);
		}
		
		return newKline;
	}

	@Override
	public JSONArray findByTimeJson(String code, String timeType, long startTime, long endTime) {
		String sql = "SELECT * FROM " + code + timeType + "_trade WHERE time > :startTime and time <= :endTime ORDER BY time";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		List<KLineTrade> trades = kLineTradeDao.findBySql(sql, map);
		
		return trades2JSON(trades);
	}

	@Override
	public long getlastTime(String code, String timeType) {
		return kLineTradeDao.getlastTime("SELECT ifnull(MAX(time),0) FROM " + code + timeType + "_trade");
	}
	
	@Override
	public long getMinTime(String code, String timeType) {
		return kLineTradeDao.getlastTime("SELECT ifnull(MIN(time),0) FROM " + code + timeType + "_trade");
	}

	private JSONArray trades2JSON(List<KLineTrade> trades) {
		JSONArray array = new JSONArray();
		if (null != trades) {
			try {
				for (KLineTrade trade : trades) {
					JSONObject json = new JSONObject();
					json.put("code", trade.getCode());
					json.put("high", trade.getHigh());
					json.put("low", trade.getLow());
					json.put("last", trade.getLast());
					json.put("first", trade.getFirst());
					json.put("vol", trade.getVol());
					json.put("time", trade.getTime());
					array.put(json);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	@Override
	public JSONArray findPreJson(String code, String timeType) {
		List<KLineTrade> trades = kLineTradeDao.findBySql("SELECT * FROM (SELECT * FROM " 
				+ code + timeType + "_trade ORDER BY id DESC LIMIT 2000) a ORDER BY id");
		return trades2JSON(trades);
	}
	
	@Override
	public JSONArray readPreJson(String code, String timeType) {
		List<KLineTrade> trades = kLineTradeDao.findBySql("SELECT * FROM (SELECT * FROM " 
				+ code + timeType + "_trade ORDER BY id DESC LIMIT 2000) a ORDER BY id");
		return trades2JSON(trades);
	}

	@Override
	public KLineTrade getAtTime(String code, String timeType, long time) {
		return kLineTradeDao.getAtTime(code, timeType, time);
	}
}
