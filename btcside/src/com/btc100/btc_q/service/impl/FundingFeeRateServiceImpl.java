package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.FundingFeeRateDao;
import com.btc100.btc_q.entity.FundingFeeRate;
import com.btc100.btc_q.service.FundingFeeRateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("fundingFeeRateService")
public class FundingFeeRateServiceImpl implements FundingFeeRateService {
	
	@Autowired
	private FundingFeeRateDao<FundingFeeRate> fundingFeeRateDao;

	@Override
	public FundingFeeRate getByTime(String symbol, long time) {
		return fundingFeeRateDao.getByTime(symbol, time);
	}

	@Override
	public int updateByTime(String symbol, BigDecimal feeRate, long time) {
		return fundingFeeRateDao.updateByTime(symbol, feeRate, time);
	}

	@Override
	public FundingFeeRate saveFeeRate(FundingFeeRate feeRate) {
		return fundingFeeRateDao.saveFeeRate(feeRate);
	}
	
	@Override
	public void updateRate(FundingFeeRate feeRate, BigDecimal rate, int count) {
		Session session = fundingFeeRateDao.getSession();
		String sql = "update " + feeRate.getF_code() + "_funding_fee_rate set fee_rate = :rate,"
				+ "count_times = :count where id = :id";
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("rate", rate);
		query.setInteger("count", count);
		query.setLong("id", feeRate.getId());
		
		query.executeUpdate();
	}
	
	@Override
	public void deleteFeeRate(FundingFeeRate feeRate) {
		Session session = fundingFeeRateDao.getSession();
		String sql = "delete from " + feeRate.getF_code() + "_funding_fee_rate where id = :id";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("id", feeRate.getId());
		
		query.executeUpdate();
	}

	@Override
	public int clampByTime(String symbol, BigDecimal feeRate, long time) {
		return fundingFeeRateDao.clampByTime(symbol, feeRate, time);
	}

	@Override
	public PagerModel readBySymbol(String symbol) {
		return fundingFeeRateDao.findBySymbol(symbol);
	}

	@Override
	public FundingFeeRate readLast(String symbol) {
		return fundingFeeRateDao.getLast(symbol);
	}
	
	@Override
	public FundingFeeRate getLast(String symbol) {
		return fundingFeeRateDao.getLast(symbol);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundingFeeRate> readList(String symbol, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = fundingFeeRateDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + symbol + "_funding_fee_rate where done = 1");
		
		if (startTime > 0) {
			sql.append(" and dateline >= :startTime");
		}
		
		if (endTime > 0) {
			sql.append(" and dateline <= :endTime");
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql.append(" and id " + ">=" + " :from");
		} else if (from >= 0 && "next".equals(direct)) {
			sql.append(" and id " + "<" + " :from");
		}
		
		sql.append(" ORDER BY id DESC");
		
		if (limit > 0) {
			sql.append(" limit " + limit);	
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());

		if (startTime > 0) {
			query.setLong("dateline", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("dateline", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(com.btc100.btc_q.entity.FundingFeeRate.class);
		return query.list();
	}
}
