package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountContractDao;
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.dao.ContractAccountLogDao;
import com.btc100.btc_q.dao.FundingFeeRecordDao;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.ContractAccountLog;
import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.FundingFeeRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("fundingFeeRecordService")
public class FundingFeeRecordServiceImpl implements FundingFeeRecordService {
	
	@Autowired
	private BQAccountContractDao<BQAccountContract> bqAccountContractDao;
	@Autowired
	private ContractAccountLogDao<ContractAccountLog> contractAccountLogDao;
	@Autowired
	private FundingFeeRecordDao<FundingFeeRecord> fundingFeeRecordDao;
	@Autowired
	private BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao;

	@Override
	public void doAccountFundingFee(FundingFeeRecord record) throws ServiceException {
		BigDecimal changeAmount = record.getFunding_fee();
		if (bqAccountContractDao.updateAmount(record.getF_user_id(), record.getF_code(), BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("");
		}
		
		BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(record.getF_user_id(), record.getF_code());
		ContractAccountLog accountlog = new ContractAccountLog("资金费", Contents.TRADE_CURRENCY_TYPE_ID, Contents.TRANS_TYPE_JY, changeAmount.signum(),
				accountContract.getF_btc().subtract(changeAmount), changeAmount.abs(), accountContract.getF_btc(), accountContract.getF_lockbtc(), 
				accountContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTrans_no(), 0, accountContract.getF_code());
		contractAccountLogDao.insertLog(accountlog);
		
		fundingFeeRecordDao.insertRecord(record);
	}
	
	@Override
	public void doMemberTransFundingFee(FundingFeeRecord record, long mtId, BigDecimal coff) throws ServiceException {
		BigDecimal changeAmount = record.getFunding_fee();
		if (changeAmount.signum() < 0) {
			// 扣持仓保证金
			
			BQMemberTransaction trans = bqMemberTransactionDao.getMemberTransactionById(mtId);
			if (trans.getF_fromremain().compareTo(changeAmount.abs()) < 0) {
				// 更改deposit2
				int ret = bqMemberTransactionDao.updateMemberTransByID(mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (ret <= 0) {
					throw new ServiceException("减少deposit2失败");
				}
			} else {
				BigDecimal needChangeAmount = BigDecimal.ZERO;
				int ret = bqMemberTransactionDao.updateMemberTransByID(mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (ret <= 0) {
					needChangeAmount = record.getFunding_fee();
				} else {
					needChangeAmount = changeAmount.abs().add(record.getFunding_fee());
				}
				
				if (needChangeAmount.compareTo(BigDecimal.ZERO) < 0) {
//					// 更改价格
//					BigDecimal holds = trans.getHold_count().add(trans.getLock_count());
//					BigDecimal changePrice = needChangeAmount.abs().divide(holds.multiply(coff), 8, BigDecimal.ROUND_DOWN);
//					BigDecimal realChange = changePrice.multiply(holds).multiply(coff);
//					
//					if (trans.getTrade_type_id() == Contents.DIRECTION_SELL) {
//						changePrice = changePrice.negate();
//					}
//					
//					if (bqMemberTransactionDao.updateTransPriceByID(mtId, changePrice) <= 0) {
//						record.setFunding_fee(record.getFunding_fee().subtract(needChangeAmount));
//					} else {
//						BigDecimal diff = realChange.add(needChangeAmount);
//						record.setPrice_change_fee(realChange);
//						if (diff.compareTo(BigDecimal.ZERO) < 0) {
//							record.setFunding_fee(record.getFunding_fee().subtract(diff));
//						}
//					}
					
					// 更改deposit2
					ret = bqMemberTransactionDao.updateMemberTransByID(mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (ret <= 0) {
						throw new ServiceException("减少deposit2失败");
					}
				}
			}
			
			BQAccountContract accountContract = bqAccountContractDao.findByUserIDCode(record.getF_user_id(), record.getF_code());
			ContractAccountLog accountlog = new ContractAccountLog("资金费", Contents.TRADE_CURRENCY_TYPE_ID, Contents.TRANS_TYPE_JY, record.getFunding_fee().signum(),
					accountContract.getF_btc().subtract(record.getFunding_fee()), record.getFunding_fee().abs(), accountContract.getF_btc(), accountContract.getF_lockbtc(), 
					accountContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTrans_no(), 0, accountContract.getF_code());
			contractAccountLogDao.insertLog(accountlog);
			
			fundingFeeRecordDao.insertRecord(record);
			
		} else {
			throw new ServiceException("");
		}
	}
	
	@Override
	public PagerModel readBySymbol(String symbol, long uid) {
		return fundingFeeRecordDao.findBySymbol(symbol, uid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundingFeeRecord> readByUserAndTime(String symbol, long userId, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = fundingFeeRecordDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + symbol + "_funding_fee_record where 1 = 1");
		
		if (userId > 0) {
			sql.append(" and f_user_id = :userId");
		}
		
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
			sql.append(" LIMIT " + limit);	
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		
		if (userId > 0) {
			query.setLong("userId", userId);
		}
		
		if (startTime > 0) {
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(FundingFeeRecord.class);
		return query.list();
	}
}
