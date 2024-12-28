package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryNotradetime;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryNotradetimeService {

	/**
	 * 判断该日期是否是正常交易时间
	 * 
	 * @param time
	 *            日期戳
	 * @return
	 */
	public boolean isTradeTime(long transactionId);

	public void add(DeliveryNotradetime noTrade);

	public DeliveryNotradetime getById(long id);

	public void deleteById(long id);

	public void updateById(long id, long time);

	public void addList(List<DeliveryNotradetime> lstNotrades);

	
	
	/************************************ for read only **************************************/
	
	public boolean readTradeTime(long transactionId);
	
	public PagerModel findAllpage(long time, int type, long transactionId);

}
