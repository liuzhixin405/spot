package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BetterToken下单记录实体类（下单和成交记录字段相同）
 * @author win7
 *
 */
@Entity
@Table(name = "bq_spot_better_token_trade")
public class BQSpotBetterTokenTrade implements Serializable {

	private static final long serialVersionUID = 210314130192818021L;

	private long id;//
	private long order_id;// 订单id
	private String pair_code;// 是Base和quote之间的组合 BTC_USD
	private long user_id;// 用户id
	private long broker_id;// 券商id
	private String side;// 方向 买(buy)、卖(sell)
	private BigDecimal entrust_price;// 下单价格
	private BigDecimal amount;// 下单数量
	private BigDecimal deal_amount;// 成交数量
	private BigDecimal quote_amount;// 基准币数量 只有在市价买的情况下会用到
	private BigDecimal deal_quote_amount;// 基准币已成交数量
	private String system_order_type;// 10:限价 11:市价
	private int status;// 0:未成交 1:部分成交 2:完全成交 3:撤单中 -1:已撤单
	private String source_info;// 下单来源 web,api,Ios,android
	private long create_on;// 创建时间
	private long update_on;// 修改时间
	private String symbol;// 币种
	private BigDecimal trun_over; // 成交金额 dealQuoteAmount * dealAmount
	private BigDecimal not_strike; // 尚未成交的数量
	private BigDecimal average_price; // 平均成交价
	private BigDecimal open_amount; // 下单数量
	
	private boolean is_self = false; // 是否自成交

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public String getPair_code() {
		return pair_code;
	}

	public void setPair_code(String pair_code) {
		this.pair_code = pair_code;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getBroker_id() {
		return broker_id;
	}

	public void setBroker_id(long broker_id) {
		this.broker_id = broker_id;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public BigDecimal getEntrust_price() {
		return entrust_price;
	}

	public void setEntrust_price(BigDecimal entrust_price) {
		this.entrust_price = entrust_price;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getDeal_amount() {
		return deal_amount;
	}

	public void setDeal_amount(BigDecimal deal_amount) {
		this.deal_amount = deal_amount;
	}

	public BigDecimal getQuote_amount() {
		return quote_amount;
	}

	public void setQuote_amount(BigDecimal quote_amount) {
		this.quote_amount = quote_amount;
	}

	public BigDecimal getDeal_quote_amount() {
		return deal_quote_amount;
	}

	public void setDeal_quote_amount(BigDecimal deal_quote_amount) {
		this.deal_quote_amount = deal_quote_amount;
	}

	public String getSystem_order_type() {
		return system_order_type;
	}

	public void setSystem_order_type(String system_order_type) {
		this.system_order_type = system_order_type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSource_info() {
		return source_info;
	}

	public void setSource_info(String source_info) {
		this.source_info = source_info;
	}

	public long getCreate_on() {
		return create_on;
	}

	public void setCreate_on(long create_on) {
		this.create_on = create_on;
	}

	public long getUpdate_on() {
		return update_on;
	}

	public void setUpdate_on(long update_on) {
		this.update_on = update_on;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getTrun_over() {
		return trun_over;
	}

	public void setTrun_over(BigDecimal trun_over) {
		this.trun_over = trun_over;
	}

	public BigDecimal getNot_strike() {
		return not_strike;
	}

	public void setNot_strike(BigDecimal not_strike) {
		this.not_strike = not_strike;
	}

	public BigDecimal getAverage_price() {
		return average_price;
	}

	public void setAverage_price(BigDecimal average_price) {
		this.average_price = average_price;
	}

	public BigDecimal getOpen_amount() {
		return open_amount;
	}

	public void setOpen_amount(BigDecimal open_amount) {
		this.open_amount = open_amount;
	}

	public boolean isIs_self() {
		return is_self;
	}

	public void setIs_self(boolean is_self) {
		this.is_self = is_self;
	}
	
}
