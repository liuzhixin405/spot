package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQStopLoss;

public interface BQStopLossService {
	
	public List<BQStopLoss> getStopLossByMid(long tmp_mId);

	public void updateStopLosses(Long valueOf, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount);

	public List<BQStopLoss> getStopWinByPrice(BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public List<BQStopLoss> getStopLossByPrice(BigDecimal lastPrice, int tradeTypeBuy, String transactionCode);

	public void deleteByMid(long mId, int action, String remark);
	
	
	/************************************ for read only **************************************/
	
	public List<BQStopLoss> readStopLossList(long uid,Long transaction_member_id);
	
	public int readHasStopLoss(long mtId);
	
}
