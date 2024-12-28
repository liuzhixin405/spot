package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.OTHERTradesdata;
import com.btc100.btc_q.vo.BPIDataVO;

public interface OTHERTradesdataService {
	
	public List<OTHERTradesdata> findLastOneByFlag(int flag);

	public OTHERTradesdata findSumByFlag(int flag);

	public BigDecimal findMaxByFlag(int flag);

	public BigDecimal findMinByFlag(int flag);

	public OTHERTradesdata findLastByFlag(int flag);

	public int deleteByFlagAndTime(int flag, long lTime);

	public void insertList(List<OTHERTradesdata> data);
	
	public void insertOne(OTHERTradesdata data);
	
	public int insertList2(List<BPIDataVO> data,int flag);
}
