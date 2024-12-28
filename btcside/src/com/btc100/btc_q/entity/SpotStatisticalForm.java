package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "spot_statistical_form")
public class SpotStatisticalForm implements Serializable {

	private static final long serialVersionUID = -3807025643813997787L;

	private long id;
	private long statistical_time;
	private long total_trade_user; // 总交易人数
	private BigDecimal total_bfx_fee;// BFX抵扣手续费总额
	private BigDecimal total_market_fee; // 总市场币种手续费
	private BigDecimal total_coin_rebate;// 总币种手续费返利（买入）
	private BigDecimal total_market_rebate;// 总市场手续费返利（卖出）
	private BigDecimal total_bfx_rebate;// 总bfx抵扣手续费（卖出）

	private String code; // 交易对名称
	private String market; // 交易对市场名称
	private String coin; // 交易对币种名称
	private BigDecimal trade_amount; // 真实交易额
	private long trade_user; // 交易人数
	private BigDecimal buy_trade_fee;// 买入手续费
	private BigDecimal sell_trade_fee;// 卖出手续费
	private BigDecimal buy_trade_bfx_fee;// BFX抵扣买入手续费
	private BigDecimal sell_trade_bfx_fee;// BFX抵扣卖出手续费
	private BigDecimal coin_rebate;// 币种手续费返利（买入）
	private BigDecimal market_rebate;// 市场手续费返利（卖出）
	private BigDecimal bfx_rebate;// bfx抵扣手续费（卖出）

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStatistical_time() {
		return statistical_time;
	}

	public void setStatistical_time(long statistical_time) {
		this.statistical_time = statistical_time;
	}

	public long getTotal_trade_user() {
		return total_trade_user;
	}

	public void setTotal_trade_user(long total_trade_user) {
		this.total_trade_user = total_trade_user;
	}

	public BigDecimal getTotal_bfx_fee() {
		return total_bfx_fee;
	}

	public void setTotal_bfx_fee(BigDecimal total_bfx_fee) {
		this.total_bfx_fee = total_bfx_fee;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public BigDecimal getTrade_amount() {
		return trade_amount;
	}

	public void setTrade_amount(BigDecimal trade_amount) {
		this.trade_amount = trade_amount;
	}

	public long getTrade_user() {
		return trade_user;
	}

	public void setTrade_user(long trade_user) {
		this.trade_user = trade_user;
	}

	public BigDecimal getBuy_trade_fee() {
		return buy_trade_fee;
	}

	public void setBuy_trade_fee(BigDecimal buy_trade_fee) {
		this.buy_trade_fee = buy_trade_fee;
	}

	public BigDecimal getSell_trade_fee() {
		return sell_trade_fee;
	}

	public void setSell_trade_fee(BigDecimal sell_trade_fee) {
		this.sell_trade_fee = sell_trade_fee;
	}

	public BigDecimal getBuy_trade_bfx_fee() {
		return buy_trade_bfx_fee;
	}

	public void setBuy_trade_bfx_fee(BigDecimal buy_trade_bfx_fee) {
		this.buy_trade_bfx_fee = buy_trade_bfx_fee;
	}

	public BigDecimal getSell_trade_bfx_fee() {
		return sell_trade_bfx_fee;
	}

	public void setSell_trade_bfx_fee(BigDecimal sell_trade_bfx_fee) {
		this.sell_trade_bfx_fee = sell_trade_bfx_fee;
	}

	public BigDecimal getCoin_rebate() {
		return coin_rebate;
	}

	public void setCoin_rebate(BigDecimal coin_rebate) {
		this.coin_rebate = coin_rebate;
	}

	public BigDecimal getMarket_rebate() {
		return market_rebate;
	}

	public void setMarket_rebate(BigDecimal market_rebate) {
		this.market_rebate = market_rebate;
	}

	public BigDecimal getBfx_rebate() {
		return bfx_rebate;
	}

	public void setBfx_rebate(BigDecimal bfx_rebate) {
		this.bfx_rebate = bfx_rebate;
	}

	public BigDecimal getTotal_coin_rebate() {
		return total_coin_rebate;
	}

	public void setTotal_coin_rebate(BigDecimal total_coin_rebate) {
		this.total_coin_rebate = total_coin_rebate;
	}

	public BigDecimal getTotal_market_rebate() {
		return total_market_rebate;
	}

	public void setTotal_market_rebate(BigDecimal total_market_rebate) {
		this.total_market_rebate = total_market_rebate;
	}

	public BigDecimal getTotal_bfx_rebate() {
		return total_bfx_rebate;
	}

	public void setTotal_bfx_rebate(BigDecimal total_bfx_rebate) {
		this.total_bfx_rebate = total_bfx_rebate;
	}

	public BigDecimal getTotal_market_fee() {
		return total_market_fee;
	}

	public void setTotal_market_fee(BigDecimal total_market_fee) {
		this.total_market_fee = total_market_fee;
	}

}
