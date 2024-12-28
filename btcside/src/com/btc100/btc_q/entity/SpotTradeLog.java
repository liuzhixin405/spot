package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * 交易记录日志表。 一个交易会产生两个用户对这个交易的操作记录，基本一份是更新，另一份是写入新记录
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "trade_log")
// TODO:怎么对应多个不同的表
@Immutable
public class SpotTradeLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5178299137410478962L;

	private long trade_id;						// 交易ID
	private long coin_pair_id;					// 交易对
	private String coin_pair_name;				// 交易对名称
	private int trade_trans_dirction;			// 这毛交易的成交方向,1买，2卖
	private BigDecimal trade_average_price;		// 成交均价
	private BigDecimal trade_count;				// 成交数量
	private BigDecimal trade_fee;				// 手续费
	private BigDecimal trade_bfx_fee = BigDecimal.ZERO;	// bfx抵扣手续费
	private long f_user_id;						// 用户ID
	private int coin_id;						// 交易货币Id
	private int market_coin_id;					// 交易资金币种ID
	private BigDecimal trade_amount;			// 交易额
	private long trade_time;					// 成交时间,unix时间格式
	private long pending_no = 0;				//对应挂单流水号

	public SpotTradeLog() {

	}
	
	public SpotTradeLog(long userId, BigDecimal tradeCount) {
		this.trade_count = tradeCount;
		this.f_user_id = userId;
	}
	
//	public SpotTradeLog(long time, BigDecimal lowPrice, BigDecimal highPrice, BigDecimal firstPrice, BigDecimal lastPrice, 
//			int coinId, int marketCoinId, BigDecimal tradeAmount) {
//		super();
//		trade_time = time;
//		trade_average_price = lowPrice;
//		trade_average_price = highPrice;
//		trade_average_price = firstPrice;
//		trade_average_price = lastPrice;
//		this.coin_id = coinId;
//		this.market_coin_id = marketCoinId;
//		this.trade_amount = tradeAmount;
//	}

	public SpotTradeLog(long pairId, String pairName, int tradeTransDirction,
			BigDecimal tradeAveragePrice, BigDecimal tradeCount,BigDecimal tradeFee, 
			long fUserId,int coinId, int marketCoinId, BigDecimal tradeAmount, long pendingNo) {
		super();
		coin_pair_id = pairId;
		coin_pair_name = pairName;
		trade_trans_dirction = tradeTransDirction;
		trade_average_price = tradeAveragePrice;
		trade_count = tradeCount;
		trade_fee = tradeFee;
		f_user_id = fUserId;
		this.market_coin_id = marketCoinId;
		this.coin_id = coinId;
		this.trade_amount = tradeAmount;
		this.pending_no = pendingNo;
	}

	@Id
	@GeneratedValue
	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long tradeId) {
		trade_id = tradeId;
	}

	public long getCoin_pair_id() {
		return coin_pair_id;
	}

	public void setCoin_pair_id(long pairId) {
		coin_pair_id = pairId;
	}

	public String getCoin_pair_name() {
		return coin_pair_name;
	}

	public void setCoin_pair_name(String pairName) {
		coin_pair_name = pairName;
	}
	
	public BigDecimal getTrade_average_price() {
		return trade_average_price;
	}

	public void setTrade_average_price(BigDecimal tradeAveragePrice) {
		trade_average_price = tradeAveragePrice;
	}

	public BigDecimal getTrade_count() {
		return trade_count;
	}

	public void setTrade_count(BigDecimal tradeCount) {
		trade_count = tradeCount;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal tradeFee) {
		trade_fee = tradeFee;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(long tradeTime) {
		trade_time = tradeTime;
	}

	public int getTrade_trans_dirction() {
		return trade_trans_dirction;
	}

	public void setTrade_trans_dirction(int tradeTransDirction) {
		trade_trans_dirction = tradeTransDirction;
	}
	
	public int getCoin_id() {
		return coin_id;
	}
	
	public void setCoin_id(int coinId) {
		this.coin_id = coinId;
	}
	
	public int getMarket_coin_id() {
		return market_coin_id;
	}
	
	public void setMarket_coin_id(int coinId) {
		this.market_coin_id = coinId;
	}
	
	public BigDecimal getTrade_amount() {
		return trade_amount;
	}
	
	public void setTrade_amount(BigDecimal amount) {
		this.trade_amount = amount;
	}

	public BigDecimal getTrade_bfx_fee() {
		return trade_bfx_fee;
	}

	public void setTrade_bfx_fee(BigDecimal trade_bfx_fee) {
		this.trade_bfx_fee = trade_bfx_fee;
	}

	public long getPending_no() {
		return pending_no;
	}

	public void setPending_no(long pending_no) {
		this.pending_no = pending_no;
	}

}
