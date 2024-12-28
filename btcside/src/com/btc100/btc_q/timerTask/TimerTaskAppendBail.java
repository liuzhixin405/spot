package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberDepositOnoffService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.ServiceAddDesposit;

/**
 * 自动追加保证金
 * 
 * @author dong'hui
 */
public class TimerTaskAppendBail {

	@Autowired
	BQMemberDepositOnoffService bqMemberDepositOnoffService;
	@Autowired
	BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	ServiceAddDesposit serviceAddDesposit;
	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	public BQPriceIndexCacheService bqPriceIndexCacheService;

	/**
	 * 求盈亏比
	 * 
	 * @param indexPrice
	 *            最新价
	 * @param avePrice
	 *            持仓均价
	 * @param hode
	 *            持仓量
	 * @param deposit
	 *            保证金
	 * @param coefficient
	 * @return
	 */
	public boolean profit(BigDecimal indexPrice, BQMemberTransaction memberTran) {
		if (memberTran.getAverage_price().compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		
		BigDecimal bzj = memberTran.getDeposit().add(memberTran.getDeposit2());		// 总保证金 b=b1+b2
		if (bzj.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		
		BigDecimal amount = memberTran.getHold_count().add(memberTran.getLock_count());
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		

		BigDecimal rate = BigDecimal.ZERO;
		BigDecimal diff = indexPrice.subtract(memberTran.getAverage_price());
		if (memberTran.getTrade_type_id() == 1) {	// 买多
			if (diff.compareTo(BigDecimal.ZERO) >= 0) {
				return false;
			}
			
			// (indexPrice - avgPrice) * amount / deposit
			rate = diff.multiply(amount).divide(bzj, 8, RoundingMode.HALF_UP);
		} else {
			if (diff.compareTo(BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			// (avgPrice - indexPrice) * amount / deposit
			rate = diff.negate().multiply(amount).divide(bzj, 8, RoundingMode.HALF_UP);
		}
		
		return rate.add(new BigDecimal(0.50)).compareTo(BigDecimal.ZERO) <= 0; // c+0.5<=0
	}

	public void run() throws ServiceException {
		// 获取开启了追加保证金的用户
		List<BQMemberTransaction> list = bqMemberTransactionService.getBQMemberTrasactionByUsers(bqMemberDepositOnoffService.findOnDeposit());
		for (BQMemberTransaction bq : list) {
			String transactionCode = bq.getTransaction_code();
			
			BQTransaction trans = getTransaction(transactionCode);
			if (trans == null) continue;
			
			String typeCode = trans.getTransaction_type_code();
			BigDecimal indexPrice = getIndexPrice(typeCode);
			if (indexPrice == null || indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			if (profit(indexPrice, bq)) {	// 追加保证金
				long uid = bq.getF_user_id();
				BigDecimal appendDeposit = bq.getDeposit().add(bq.getDeposit2()).multiply(new BigDecimal(0.5));	// 自动追加保证金的50%
				if (bq.getFlag() < 0) continue;
				
				if (bqMemberTransactionService.lockToTrade(bq.getMt_id())) {
					try {
					serviceAddDesposit.AddDesposit(uid, appendDeposit, bq.getMt_id(), 0, transactionCode, bq.getTrade_type_id());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (bq != null) {
							bqMemberTransactionService.unlockToTrade(bq.getMt_id());
						}
					}
				}
			}
		}
	}
	
	private BQTransaction getTransaction(String code) {
		BQTransaction bqRet = null;
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			bqRet = transactionService.findTransByCode(code);
		} else {
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equalsIgnoreCase(code)) {
					bqRet = bqTransaction;
					break;
				}
			}
		}
		
		return bqRet;
	}
	
	private BigDecimal getIndexPrice(String typeCode) {
		BigDecimal btc_index_price = spyMemcachedClient.get(typeCode);
		if (btc_index_price == null) {
			BQTransactionType transType = bqTransTypeService.getTransTypeByCode(typeCode);
			if (transType == null) return btc_index_price;
			btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
		}
		
		return btc_index_price;
	}
}
