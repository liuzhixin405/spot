package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.dao.NFundingFeeRecordDao;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NFundingFeeRecord;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NFundingFeeRecordService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("nFundingFeeRecordService")
public class NFundingFeeRecordServiceImpl implements NFundingFeeRecordService {
	
	@Autowired
	private NAccountContractDao<NAccountContract> nAccountContractDao;
	@Autowired
	private NContractAccountLogDao<NContractAccountLog> contractAccountLogDao;
	@Autowired
	private NFundingFeeRecordDao<NFundingFeeRecord> fundingFeeRecordDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> nMemberTransactionDao;

	@Override
	public void doAccountFundingFee(String partition, NFundingFeeRecord record, int currencyId) throws ServiceException {
		BigDecimal changeAmount = record.getFunding_fee();
		
		NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, record.getF_user_id(), record.getF_code());
		if (nAccountContractDao.updateAmount(partition, record.getF_user_id(), record.getF_code(), BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
			throw new ServiceException("");
		}
		
		NContractAccountLog accountlog = new NContractAccountLog("资金费", currencyId, Contents.TRANS_TYPE_JY, changeAmount.signum(),
				accountContract.getF_btc(), changeAmount.abs(), accountContract.getF_btc().add(changeAmount), accountContract.getF_lockbtc(), 
				accountContract.getF_lockbtc(), nAccountContractDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTrans_no(), 0, accountContract.getF_code());
		contractAccountLogDao.insertLog(partition, accountlog);
		
		fundingFeeRecordDao.insertRecord(partition, record);
	}
	
	@Override
	public void doMemberTransFundingFee(String partition, NFundingFeeRecord record, long mtId, BigDecimal coff, int currencyId) throws ServiceException {
		BigDecimal changeAmount = record.getFunding_fee();
		if (changeAmount.signum() < 0) {
			// 扣持仓保证金
			
			NMemberTransaction trans = nMemberTransactionDao.getMemberTransactionById(partition, mtId);
			if (trans.getF_fromremain().compareTo(changeAmount.abs()) < 0) {
				// 更改deposit2
				int ret = nMemberTransactionDao.updateMemberTransByID(partition, mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (ret <= 0) {
					throw new ServiceException("减少deposit2失败");
				}
			} else {
				BigDecimal needChangeAmount = BigDecimal.ZERO;
				int ret = nMemberTransactionDao.updateMemberTransByID(partition, mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (ret <= 0) {
					needChangeAmount = record.getFunding_fee();
				} else {
					needChangeAmount = changeAmount.abs().add(record.getFunding_fee());
				}
				
				if (needChangeAmount.compareTo(BigDecimal.ZERO) < 0) {
					// 更改价格
//					BigDecimal holds = trans.getHold_count().add(trans.getLock_count());
//					BigDecimal changePrice = needChangeAmount.abs().divide(holds.multiply(coff), 8, BigDecimal.ROUND_DOWN);
//					BigDecimal realChange = changePrice.multiply(holds).multiply(coff);
//					
//					if (trans.getTrade_type_id() == Contents.DIRECTION_SELL) {
//						changePrice = changePrice.negate();
//					}
//					
//					if (nMemberTransactionDao.updateTransPriceByID(partition, mtId, changePrice) <= 0) {
//						record.setFunding_fee(record.getFunding_fee().subtract(needChangeAmount));
//					} else {
//						BigDecimal diff = realChange.add(needChangeAmount);
//						record.setPrice_change_fee(realChange);
//						if (diff.compareTo(BigDecimal.ZERO) < 0) {
//							record.setFunding_fee(record.getFunding_fee().subtract(diff));
//						}
//					}
					
					// 更改deposit2
					ret = nMemberTransactionDao.updateMemberTransByID(partition, mtId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, changeAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (ret <= 0) {
						throw new ServiceException("减少deposit2失败");
					}
				}
			}
			
			NAccountContract accountContract = nAccountContractDao.findByUserIDCode(partition, record.getF_user_id(), record.getF_code());
			NContractAccountLog accountlog = new NContractAccountLog("资金费", currencyId, Contents.TRANS_TYPE_JY, record.getFunding_fee().signum(),
					accountContract.getF_btc().subtract(record.getFunding_fee()), record.getFunding_fee().abs(), accountContract.getF_btc(), accountContract.getF_lockbtc(), 
					accountContract.getF_lockbtc(), nAccountContractDao.getUnix_TIMESTAMP(), record.getF_user_id(), RandomUtil.getTrans_no(), 0, accountContract.getF_code());
			contractAccountLogDao.insertLog(partition, accountlog);
			
			fundingFeeRecordDao.insertRecord(partition, record);
			
		} else {
			throw new ServiceException("");
		}
	}
	
	@Override
	public PagerModel readBySymbol(String partition, String symbol, long uid) {
		return fundingFeeRecordDao.findBySymbol(partition, symbol, uid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NFundingFeeRecord> readByUserAndTime(String partition, String symbol, long userId, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = fundingFeeRecordDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + symbol + "_funding_fee_record where 1 = 1");
		
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
		
		query.addEntity(NFundingFeeRecord.class);
		return query.list();
	}
}
