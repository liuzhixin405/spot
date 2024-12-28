package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.OTHERTradesdata;

public interface OTHERTradesdataDao<T> extends BaseDaoI<T> {

	public List<T> findOneByFlag(int flag);

	public OTHERTradesdata findSumByFlag(int flag);

	public BigDecimal findMaxByFlag(int flag);

	public BigDecimal findMinByFlag(int flag);

	public T findLastByFlag(int flag);

	public int deleteByFlagAndTime(int flag, long time);
}
