package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NNotradetime;
import com.btc100.btc_q.vo.PagerModel;

public interface NNotradetimeService {

	/**
	 * 判断该日期是否是正常交易时间
	 * 
	 * @param time
	 *            日期戳
	 * @return
	 */
	public boolean isTradeTime(String partition, long transactionId);

	public void add(String partition, NNotradetime noTrade);

	public NNotradetime getById(String partition, long id);

	public void deleteById(String partition, long id);

	public void updateById(String partition, long id, long time);

	public void addList(String partition, List<NNotradetime> lstNotrades);

	
	
	/************************************ for read only **************************************/
	
	public boolean readTradeTime(String partition, long transactionId);
	
	public PagerModel findAllpage(String partition, long time, int type, long transactionId);

}
