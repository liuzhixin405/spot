package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VStopLoss;

public interface VStopLossService {
	
	public void updateStopLosses(Long valueOf, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount);

	public List<VStopLoss> getStopWinByPrice(BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public List<VStopLoss> getStopLossByPrice(BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public void deleteAllByBqCode(String bqCode);
	
	public void deleteByMid(long mId);
	
	
	
	///************************************ for read only **************************************/
	
	public List<VStopLoss> readStopLossList(long uid, Long transaction_member_id);
	
	public int readHasStopLoss(long mtId);

}
