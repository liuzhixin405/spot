package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * 交易详情表
 */
@Entity
@Table(name = "n_member_trade_detail") //TODO:怎么对应多个不同的表
@Immutable
public class NMemberTradeDetail implements Serializable{

	private static final long serialVersionUID = 8242799974825249410L;
	
	@Id
	@GeneratedValue
	private long id;
	
	private long f_buy_trade_id;//买方对应的交易ID
	private long f_buy_uid; //买家uid
	private String f_buy_user_email; //买家邮箱
	private long f_sell_uid; //卖家uid
	private String f_sell_user_email; // 卖家邮箱
	private long f_sell_trade_id; //卖方对应的交易id
	
	private Integer trade_buy_type_id;//成交类型ID(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓，
	private Integer trade_sell_type_id;//成交类型ID(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓，
	private Integer trade_flag;//交易类型标识(1:普通交易, 2:交割 3:强平)
	private Integer trade_trans_dirction;//这毛交易的成交方向,1买，2卖
	private BigDecimal trade_price;//成交价格
	private BigDecimal trade_count;//成交数量
	private long trade_time;//成交时间,unix时间格式
	
	private long transaction_id;//合约号id
	private String transaction_code;//合约号
	public NMemberTradeDetail() {
		super();
	}
	
	public NMemberTradeDetail(long fBuyTradeId, long fBuyUid,
			String fBuyUserEmail, long fSellUid, String fSellUserEmail,
			long fSellTradeId, Integer tradeBuyTypeId, Integer tradeSellTypeId,
			Integer tradeFlag, Integer tradeTransDirction,
			BigDecimal tradePrice, BigDecimal tradeCount, 
			long transactionId, String transactionCode) {
		super();
		f_buy_trade_id = fBuyTradeId;
		f_buy_uid = fBuyUid;
		f_buy_user_email = fBuyUserEmail;
		f_sell_uid = fSellUid;
		f_sell_user_email = fSellUserEmail;
		f_sell_trade_id = fSellTradeId;
		trade_buy_type_id = tradeBuyTypeId;
		trade_sell_type_id = tradeSellTypeId;
		trade_flag = tradeFlag;
		trade_trans_dirction = tradeTransDirction;
		trade_price = tradePrice;
		trade_count = tradeCount;
		transaction_id = transactionId;
		transaction_code = transactionCode;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getF_buy_trade_id() {
		return f_buy_trade_id;
	}
	public void setF_buy_trade_id(long fBuyTradeId) {
		f_buy_trade_id = fBuyTradeId;
	}
	public long getF_buy_uid() {
		return f_buy_uid;
	}
	public void setF_buy_uid(long fBuyUid) {
		f_buy_uid = fBuyUid;
	}
	public long getF_sell_uid() {
		return f_sell_uid;
	}
	public void setF_sell_uid(long fSellUid) {
		f_sell_uid = fSellUid;
	}

	public String getF_buy_user_email() {
		return f_buy_user_email;
	}
	public void setF_buy_user_email(String fBuyUserEmail) {
		f_buy_user_email = fBuyUserEmail;
	}
	public String getF_sell_user_email() {
		return f_sell_user_email;
	}
	public void setF_sell_user_email(String fSellUserEmail) {
		f_sell_user_email = fSellUserEmail;
	}
	public long getF_sell_trade_id() {
		return f_sell_trade_id;
	}
	public void setF_sell_trade_id(long fSellTradeId) {
		f_sell_trade_id = fSellTradeId;
	}
	public Integer getTrade_buy_type_id() {
		return trade_buy_type_id;
	}
	public void setTrade_buy_type_id(Integer tradeBuyTypeId) {
		trade_buy_type_id = tradeBuyTypeId;
	}
	public Integer getTrade_sell_type_id() {
		return trade_sell_type_id;
	}
	public void setTrade_sell_type_id(Integer tradeSellTypeId) {
		trade_sell_type_id = tradeSellTypeId;
	}
	public Integer getTrade_flag() {
		return trade_flag;
	}
	public void setTrade_flag(Integer tradeFlag) {
		trade_flag = tradeFlag;
	}
	public BigDecimal getTrade_price() {
		return trade_price;
	}
	public void setTrade_price(BigDecimal tradePrice) {
		trade_price = tradePrice;
	}
	public BigDecimal getTrade_count() {
		return trade_count;
	}
	public void setTrade_count(BigDecimal tradeCount) {
		trade_count = tradeCount;
	}
	public long getTrade_time() {
		return trade_time;
	}
	public void setTrade_time(long tradeTime) {
		trade_time = tradeTime;
	}
	public long getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(long transactionId) {
		transaction_id = transactionId;
	}
	public String getTransaction_code() {
		return transaction_code;
	}
	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}
	public Integer getTrade_trans_dirction() {
		return trade_trans_dirction;
	}
	public void setTrade_trans_dirction(Integer tradeTransDirction) {
		trade_trans_dirction = tradeTransDirction;
	}

}
