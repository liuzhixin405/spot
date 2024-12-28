package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NFundingFeeRateDao;
import com.btc100.btc_q.entity.NFundingFeeRate;
import com.btc100.btc_q.service.NFundingFeeRateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nFundingFeeRateService")
public class NFundingFeeRateServiceImpl implements NFundingFeeRateService {
	
	@Autowired
	private NFundingFeeRateDao<NFundingFeeRate> fundingFeeRateDao;

	@Override
	public NFundingFeeRate getByTime(String partition, String symbol, long time) {
		return fundingFeeRateDao.getByTime(partition, symbol, time);
	}

	@Override
	public int updateByTime(String partition, String symbol, BigDecimal feeRate, long time) {
		return fundingFeeRateDao.updateByTime(partition, symbol, feeRate, time);
	}

	@Override
	public NFundingFeeRate saveFeeRate(String partition, NFundingFeeRate feeRate) {
		return fundingFeeRateDao.saveFeeRate(partition, feeRate);
	}
	
	@Override
	public void updateRate(String partition, NFundingFeeRate feeRate, BigDecimal rate, int count) {
		Session session = fundingFeeRateDao.getSession();
		String sql = "update " + partition + "_" + feeRate.getF_code() + "_funding_fee_rate set fee_rate = :rate,"
				+ "count_times = :count where id = :id";
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("rate", rate);
		query.setInteger("count", count);
		query.setLong("id", feeRate.getId());
		
		query.executeUpdate();
	}
	
	@Override
	public void deleteFeeRate(String partition, NFundingFeeRate feeRate) {
		Session session = fundingFeeRateDao.getSession();
		String sql = "delete from " + partition + "_" + feeRate.getF_code() + "_funding_fee_rate where id = :id";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("id", feeRate.getId());
		
		query.executeUpdate();
	}

	@Override
	public int clampByTime(String partition, String symbol, BigDecimal feeRate, long time) {
		return fundingFeeRateDao.clampByTime(partition, symbol, feeRate, time);
	}

	@Override
	public PagerModel readBySymbol(String partition, String symbol) {
		return fundingFeeRateDao.findBySymbol(partition, symbol);
	}

	@Override
	public NFundingFeeRate readLast(String partition, String symbol) {
		return fundingFeeRateDao.getLast(partition, symbol);
	}
	
	@Override
	public NFundingFeeRate getLast(String partition, String symbol) {
		return fundingFeeRateDao.getLast(partition, symbol);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NFundingFeeRate> readList(String partition, String symbol, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = fundingFeeRateDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + symbol + "_funding_fee_rate where done = 1");
		
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
		
		query.addEntity(NFundingFeeRate.class);
		return query.list();
	}
}
