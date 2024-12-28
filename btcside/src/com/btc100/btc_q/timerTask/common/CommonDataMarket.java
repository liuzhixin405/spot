package com.btc100.btc_q.timerTask.common;

import java.io.Serializable;

import org.json.JSONException;

import com.btc100.btc_q.vo.TickerVo;

public interface CommonDataMarket extends Serializable {

	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException;
}
