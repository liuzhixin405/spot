package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 现货交易区交易对（币币交易）
 * 
 */
@Entity
@Table(name = "bq_spot_market_coin_pair")
public class BQSpotMarketCoinPair implements Serializable {

	private static final long serialVersionUID = -2068956626379505628L;

	private long id; // 交易对id
	private String coin_pair_name; // 交易对名称（eg:BTCETH,BTC/LTC）
	private String coin_name; // 交易对币种名称（eg:ETH,ETC,EOS）
	private int coin_id; // 交易对币种id（与币种类型id关联）
	private int state; // 交易对状态(1:正常 2:关闭 3:其他)
	private BigDecimal min_trade_nums; // 最小交易数量
	private BigDecimal min_float_price; // 最小浮动价格
	private int decimals; // 交易数量小数位数
	private BigDecimal coin_buy_rate = BigDecimal.ZERO;// 交易对币种买入手续费比例
	private BigDecimal coin_sell_rate = BigDecimal.ZERO;// 交易对币种卖出手续费比例
	private BigDecimal latest_price;// 交易对最新价格
	private BigDecimal highest_price;// 交易对最高价格
	private BigDecimal lowest_price;// 交易对最低价格
	private int market_id; // 交易区id
	private int market_coin_id; // 交易区币种id
	private String market_coin_name;// 交易区币种名称
	private boolean suspend = false;

	public BQSpotMarketCoinPair() {

	}

	public BQSpotMarketCoinPair(String coinPairName, String coinName, int coinId, int state,
			BigDecimal minTradeNums, BigDecimal minFloatPrice, int decimals, BigDecimal coinBuyRate,
			BigDecimal coinSellRate, BigDecimal lastestPrice, BigDecimal higestPrice, BigDecimal lowestPrice,
			int marketId, int marketCoinId, String marketCoinName) {
		this.coin_name = coinName;
		this.coin_pair_name = coinPairName;
		this.coin_id = coinId;
		this.state = state;
		this.min_trade_nums = minTradeNums;
		this.min_float_price = minFloatPrice;
		this.decimals = decimals;
		this.coin_buy_rate = coinBuyRate;
		this.coin_sell_rate = coinSellRate;
		this.latest_price = lastestPrice;
		this.highest_price = higestPrice;
		this.lowest_price = lowestPrice;
		this.market_id = marketId;
		this.market_coin_id = marketCoinId;
		this.market_coin_name = marketCoinName;
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCoin_pair_name() {
		return coin_pair_name;
	}

	public void setCoin_pair_name(String coin_pair_name) {
		this.coin_pair_name = coin_pair_name;
	}

	public String getCoin_name() {
		return coin_name;
	}

	public void setCoin_name(String coin_name) {
		this.coin_name = coin_name;
	}

	public int getCoin_id() {
		return coin_id;
	}

	public void setCoin_id(int coin_id) {
		this.coin_id = coin_id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public BigDecimal getMin_trade_nums() {
		return min_trade_nums;
	}

	public void setMin_trade_nums(BigDecimal min_trade_nums) {
		this.min_trade_nums = min_trade_nums;
	}

	public BigDecimal getMin_float_price() {
		return min_float_price;
	}

	public void setMin_float_price(BigDecimal min_float_price) {
		this.min_float_price = min_float_price;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public BigDecimal getCoin_buy_rate() {
		return coin_buy_rate;
	}

	public void setCoin_buy_rate(BigDecimal coin_buy_rate) {
		this.coin_buy_rate = coin_buy_rate;
	}

	public BigDecimal getCoin_sell_rate() {
		return coin_sell_rate;
	}

	public void setCoin_sell_rate(BigDecimal coin_sell_rate) {
		this.coin_sell_rate = coin_sell_rate;
	}

	public BigDecimal getLatest_price() {
		return latest_price;
	}

	public void setLatest_price(BigDecimal latest_price) {
		this.latest_price = latest_price;
	}

	public BigDecimal getHighest_price() {
		return highest_price;
	}

	public void setHighest_price(BigDecimal highest_price) {
		this.highest_price = highest_price;
	}

	public BigDecimal getLowest_price() {
		return lowest_price;
	}

	public void setLowest_price(BigDecimal lowest_price) {
		this.lowest_price = lowest_price;
	}

	public int getMarket_id() {
		return market_id;
	}

	public void setMarket_id(int market_id) {
		this.market_id = market_id;
	}

	public int getMarket_coin_id() {
		return market_coin_id;
	}

	public void setMarket_coin_id(int market_coin_id) {
		this.market_coin_id = market_coin_id;
	}

	public String getMarket_coin_name() {
		return market_coin_name;
	}

	public void setMarket_coin_name(String market_coin_name) {
		this.market_coin_name = market_coin_name;
	}

	public boolean getSuspend() {
		return suspend;
	}
	
	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}
}
