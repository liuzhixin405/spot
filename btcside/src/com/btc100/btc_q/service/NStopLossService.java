package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NStopLoss;

public interface NStopLossService {
	
	public List<NStopLoss> getStopLossByMid(String partition, long tmp_mId);

	public void updateStopLosses(String partition, Long valueOf, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount);

	public List<NStopLoss> getStopWinByPrice(String partition, BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public List<NStopLoss> getStopLossByPrice(String partition, BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public void deleteByMid(String partition, long mId, int action, String remark);
	
	
	/************************************ for read only **************************************/
	
	public List<NStopLoss> readStopLossList(String partition, long uid,Long transaction_member_id);
	
	public int readHasStopLoss(String partition, long mtId);
	
}
