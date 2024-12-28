package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VStopLoss;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VStopLossService;
import com.btc100.btc_q.service.VTransactionRateService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.Utils;

/**
 *  止盈止损服务
 *  //TODO:在交割的时候把合约相关的止盈止损表的记录删除掉
 * @author gyl
 *
 */
public class VStopLossWinTimerTask extends BaseMemberLevelTask {
	Logger logger = Logger.getLogger(VStopLossWinTimerTask.class);
	@Autowired
	private VStopLossService vStopLossService;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private VTransactionRateService vTransactionRateService;
	@Autowired
	private VTransactionTypeService vTransTypeService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	
	public void run() {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(transactionList == null){
			transactionList= vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		for(VTransaction bqt : transactionList){
			if (bqt.getSuspend()) {
				continue;
			}
			
			BigDecimal btc_index_price = spyMemcachedClient.get(bqt.getTransaction_type_code());
			if (btc_index_price == null) {
				BQPriceIndexCache cache = bqPriceIndexCacheService.getBtcIndexPrice(bqt.getTransaction_type_code());
				if (cache != null) {
					btc_index_price = cache.getPrice();
				} else {
					btc_index_price = BigDecimal.ZERO;
				}
			}
			
			// 获取标记价格
			BigDecimal markPrice = btc_index_price;
			BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
			if (setting != null && setting.isRunning()) {
				markPrice = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + bqt.getTransaction_code());
			}
			
			if (markPrice == null || markPrice.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			
			List<VStopLoss> stopWinManyList = vStopLossService.getStopWinByPrice(markPrice,Contents.TRADE_TYPE_BUY, bqt.getTransaction_code());
			List<VStopLoss> stopLossManyList = vStopLossService.getStopLossByPrice(markPrice,Contents.TRADE_TYPE_BUY, bqt.getTransaction_code());
			
			List<VStopLoss> stopWinEmptyList = vStopLossService.getStopWinByPrice(markPrice,Contents.TRADE_TYPE_SELL, bqt.getTransaction_code());
			List<VStopLoss> stopLossEmptyList = vStopLossService.getStopLossByPrice(markPrice,Contents.TRADE_TYPE_SELL, bqt.getTransaction_code());
			
			VTransactionRate index_price_rate = vTransactionRateService.getByCode(bqt.getTransaction_code());
			if(btc_index_price.signum()<=0){
				break;
			}
			if(index_price_rate == null){
				break;
			}
			
			VTransactionType transType = null;
			List<VTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
			if (types == null) {
				types = vTransTypeService.findAllUsing();
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
			}
			
			if (types == null) break;
			
			for (VTransactionType type : types) {
				if (type.getType_id() == bqt.getTransaction_type()) {
					transType = type;
					break;
				}
			}
			
			if (transType == null) break;
			int digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			
			for(VStopLoss sl : stopWinManyList){
				handUp(btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(VStopLoss sl : stopLossManyList){
				handUp(btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(VStopLoss sl : stopWinEmptyList){
				handUp(btc_index_price,index_price_rate,sl,bqt, digits);
			}
			for(VStopLoss sl : stopLossEmptyList){
				handUp(btc_index_price,index_price_rate,sl,bqt, digits);
			}
		}
	}
	
	private void handUp(BigDecimal btc_index_price,VTransactionRate index_price_rate,VStopLoss sl, VTransaction bqt, int digits) {
		VMemberTransaction  bqmt = vMemberTransactionService.get(sl.getTransaction_member_id());
		BigDecimal pendingAmount = sl.getPending_amount();
		if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (pendingAmount.compareTo(bqmt.getHold_count()) <= 0) {
				try {
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						tradePDC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						tradePKC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else { // 针对原有未设置挂单数量的止盈止损，全仓平
			if (bqmt.getHold_count().signum() > 0) {
				pendingAmount = bqmt.getHold_count();
				try {
					if(Contents.TRADE_TYPE_BUY == bqmt.getTrade_type_id()){	//多仓
						tradePDC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					} else {
						tradePKC(btc_index_price, index_price_rate, bqt, bqmt, sl, pendingAmount, digits);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//删除止盈止损记录
		vStopLossService.deleteByMid(sl.getTransaction_member_id());
	}
	
	private void tradePDC(BigDecimal btc_index_price, VTransactionRate index_price_rate, VTransaction bqTransaction,
			VMemberTransaction trans, VStopLoss sl, BigDecimal amount_client, int digits) {
		
		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("vcontract", bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("多单，止盈止损挂卖单失败，insertPCD调用失败！！无市价:" + bqTransaction.getTransaction_code());
				return;
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return;
		}
	
		BQMemberLevel userlevel = bqMemberLevelService.findByUserId(trans.getF_user_id());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (price_client.compareTo(minIndexPrice) < 0) {
			price_client = minIndexPrice;
		}
		
		// 爆仓价
		VAccountContract account = vAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_BUY, trans.getAverage_price(), deposit, userlevel.getEat_commission_rate(), 
				trans.getHold_count().add(trans.getLock_count()), BigDecimal.ONE);
		if (price_client.compareTo(bcj) < 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.UP);
				price_client = bcj;
			} else {
				return;
			}
		}

		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_SELL, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (vMemberTransactionService.lockToTrade(trans.getMt_id())) {
			try {
				vPendingDelegationCacheService.insertPCD(cahce, trans.getMt_id(), Contents.DEVICE_SERVER_SLW, "");
			} catch (Exception e) {
				logger.error("止盈止损挂卖单失败！！");
				e.printStackTrace();
			} finally {
				if (trans != null) {
					vMemberTransactionService.unlockToTrade(trans.getMt_id());
				}
			}
		}
	}

	private void tradePKC(BigDecimal btc_index_price, VTransactionRate index_price_rate, VTransaction bqTransaction,
			VMemberTransaction trans, VStopLoss sl, BigDecimal amount_client, int digits) {

		BigDecimal price_client = new BigDecimal(0);
		int orderType = sl.getOrder_type();
		if (orderType == 1) {
			price_client = getMarketOrderPrice("vcontract", bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
			if (price_client.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info("空单，止盈止损挂买单失败，insertPKC调用失败！！无市价:" + bqTransaction.getTransaction_code());
				return;
			}
		} else {
			price_client = sl.getPending_trans_price();
		}
		price_client = price_client.setScale(digits + 1, BigDecimal.ROUND_HALF_UP).setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (amount_client.signum() <= 0 || price_client.signum() <= 0) {
			return;
		}
	
		BQMemberLevel userlevel = bqMemberLevelService.findByUserId(trans.getF_user_id());
		BigDecimal minIndexPrice = btc_index_price.multiply((new BigDecimal(1)).subtract(index_price_rate.getDownrate()));
		minIndexPrice = minIndexPrice.setScale(digits, RoundingMode.HALF_UP);
		BigDecimal maxIndexPrice = btc_index_price.multiply(index_price_rate.getUprate().add(new BigDecimal(1)));
		maxIndexPrice = maxIndexPrice.setScale(digits, BigDecimal.ROUND_DOWN);
		
		if (price_client.compareTo(maxIndexPrice) > 0) {
			price_client = maxIndexPrice;
		}
		
		// 爆仓价
		VAccountContract account = vAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
		BigDecimal deposit = trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(trans.getF_frozenremain())
				.add(trans.getF_min_frozenremain()).add(account.getF_btc());
		BigDecimal bcj = BTCQMath.BCJ(Contents.DIRECTION_SELL, trans.getAverage_price(), deposit, userlevel.getEat_commission_rate(), 
				trans.getHold_count().add(trans.getLock_count()), BigDecimal.ONE);
		if (price_client.compareTo(bcj) > 0) {
			if (orderType == 1) {
				bcj = bcj.setScale(digits, RoundingMode.DOWN);
				price_client = bcj;
			} else {
				return;
			}
		}
		
		BigDecimal multiple = trans.getTrade_multiple();
		deposit = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP).multiply(trans.getDeposit());

		VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.DIRECTION_BUY, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), deposit, 
				userlevel.getEat_commission_rate(), Contents.PRIORITY_PC, trans.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);

		if (vMemberTransactionService.lockToTrade(trans.getMt_id())) {
			try {
				vPendingDelegationCacheService.insertPCK(cahce, trans.getMt_id(), Contents.DEVICE_SERVER_SLW, "");
			} catch (Exception e) {
				logger.error("止盈止损挂买单失败！！");
				e.printStackTrace();
			} finally {
				if (trans != null) {
					vMemberTransactionService.unlockToTrade(trans.getMt_id());
				}
			}
		}
	}
	
}
