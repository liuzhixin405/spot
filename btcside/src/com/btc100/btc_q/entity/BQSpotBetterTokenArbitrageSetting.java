package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_spot_better_token_arbitrage_setting")
public class BQSpotBetterTokenArbitrageSetting implements Serializable {

	private static final long serialVersionUID = -3905030817323000550L;

	private Long id;
	private boolean running = false;
	private boolean scalping = false;
	private int scalping_frequency_min = 0;
	private int scalping_frequency_max = 0;
	private BigDecimal scalping_min_each = BigDecimal.ZERO;
	private BigDecimal scalping_max_each = BigDecimal.ZERO;
	private BigDecimal scalping_index_range = BigDecimal.ZERO;
	private BigDecimal scalping_depth_range = BigDecimal.ZERO;

	private BigDecimal depth_buy_price_gap = BigDecimal.ZERO; // 买盘递减价格
	private BigDecimal depth_sell_price_gap = BigDecimal.ZERO; // 卖盘递增价格

	private BigDecimal depth_buy_amount_min = BigDecimal.ZERO; // 买盘一单最小数量
	private BigDecimal depth_sell_amount_min = BigDecimal.ZERO; // 卖盘一单最小数量
	private BigDecimal depth_buy_amount_max = BigDecimal.ZERO; // 买盘一单最大数量
	private BigDecimal depth_sell_amount_max = BigDecimal.ZERO; // 卖盘一单最大数量

	private boolean depth_backhand = true; // 是否反手挂单
	private BigDecimal depth_buy_backhand_gap = BigDecimal.ZERO; // 买盘反手挂单差价
	private BigDecimal depth_sell_backhand_gap = BigDecimal.ZERO; // 卖盘反手挂单差价

	private BigDecimal open_price = BigDecimal.ZERO; // 开盘价格
	private boolean depth_reset_open_price = true; // 是否重置价格
	
	private BigDecimal depth_buy_limit_amount = BigDecimal.ZERO; // buy1达标成交量
	private BigDecimal depth_sell_limit_amount = BigDecimal.ZERO; // sell1达标成交量
	private long check_time = 0l;// 检查成交量是否达到触发的时间

	private int price_digits = 0;
	private int amount_digits = 0;
	private int depth_size = 0;
	
	private int cycle_time = 0; //循环挂单和撤单间隔
	private int cycle_buy_size = 0; //买单循环挂和撤条数
	private int cycle_sell_size = 0; //卖单循环挂和撤条数

	private BigDecimal volatility = BigDecimal.ZERO; // 达到该振幅邮箱提醒
	private boolean reminder_open = true; // 是否提醒
	private String reminder_email = ""; // 提醒邮箱

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean getScalping() {
		return scalping;
	}

	public void setScalping(boolean scalping) {
		this.scalping = scalping;
	}

	public int getScalping_frequency_min() {
		return scalping_frequency_min;
	}

	public void setScalping_frequency_min(int frequency) {
		this.scalping_frequency_min = frequency;
	}

	public int getScalping_frequency_max() {
		return scalping_frequency_max;
	}

	public void setScalping_frequency_max(int frequency) {
		this.scalping_frequency_max = frequency;
	}

	public BigDecimal getScalping_min_each() {
		return scalping_min_each;
	}

	public void setScalping_min_each(BigDecimal min) {
		scalping_min_each = min;
	}

	public BigDecimal getScalping_max_each() {
		return scalping_max_each;
	}

	public void setScalping_max_each(BigDecimal max) {
		scalping_max_each = max;
	}

	public BigDecimal getScalping_index_range() {
		return scalping_index_range;
	}

	public void setScalping_index_range(BigDecimal scalping_index_range) {
		this.scalping_index_range = scalping_index_range;
	}

	public BigDecimal getScalping_depth_range() {
		return scalping_depth_range;
	}

	public void setScalping_depth_range(BigDecimal scalping_depth_range) {
		this.scalping_depth_range = scalping_depth_range;
	}

	public BigDecimal getDepth_buy_price_gap() {
		return depth_buy_price_gap;
	}

	public void setDepth_buy_price_gap(BigDecimal depth_buy_price_gap) {
		this.depth_buy_price_gap = depth_buy_price_gap;
	}

	public BigDecimal getDepth_sell_price_gap() {
		return depth_sell_price_gap;
	}

	public void setDepth_sell_price_gap(BigDecimal depth_sell_price_gap) {
		this.depth_sell_price_gap = depth_sell_price_gap;
	}

	public BigDecimal getDepth_buy_amount_min() {
		return depth_buy_amount_min;
	}

	public void setDepth_buy_amount_min(BigDecimal depth_buy_amount_min) {
		this.depth_buy_amount_min = depth_buy_amount_min;
	}

	public BigDecimal getDepth_sell_amount_min() {
		return depth_sell_amount_min;
	}

	public void setDepth_sell_amount_min(BigDecimal depth_sell_amount_min) {
		this.depth_sell_amount_min = depth_sell_amount_min;
	}

	public BigDecimal getDepth_buy_amount_max() {
		return depth_buy_amount_max;
	}

	public void setDepth_buy_amount_max(BigDecimal depth_buy_amount_max) {
		this.depth_buy_amount_max = depth_buy_amount_max;
	}

	public BigDecimal getDepth_sell_amount_max() {
		return depth_sell_amount_max;
	}

	public void setDepth_sell_amount_max(BigDecimal depth_sell_amount_max) {
		this.depth_sell_amount_max = depth_sell_amount_max;
	}

	public BigDecimal getDepth_buy_backhand_gap() {
		return depth_buy_backhand_gap;
	}

	public void setDepth_buy_backhand_gap(BigDecimal depth_buy_backhand_gap) {
		this.depth_buy_backhand_gap = depth_buy_backhand_gap;
	}

	public BigDecimal getDepth_sell_backhand_gap() {
		return depth_sell_backhand_gap;
	}

	public void setDepth_sell_backhand_gap(BigDecimal depth_sell_backhand_gap) {
		this.depth_sell_backhand_gap = depth_sell_backhand_gap;
	}

	public BigDecimal getOpen_price() {
		return open_price;
	}

	public void setOpen_price(BigDecimal open_price) {
		this.open_price = open_price;
	}

	public boolean isDepth_backhand() {
		return depth_backhand;
	}

	public void setDepth_backhand(boolean depth_backhand) {
		this.depth_backhand = depth_backhand;
	}

	public boolean isDepth_reset_open_price() {
		return depth_reset_open_price;
	}

	public void setDepth_reset_open_price(boolean depth_reset_open_price) {
		this.depth_reset_open_price = depth_reset_open_price;
	}

	public int getPrice_digits() {
		return price_digits;
	}

	public void setPrice_digits(int price_digits) {
		this.price_digits = price_digits;
	}

	public int getAmount_digits() {
		return amount_digits;
	}

	public void setAmount_digits(int amount_digits) {
		this.amount_digits = amount_digits;
	}

	public int getDepth_size() {
		return depth_size;
	}

	public void setDepth_size(int depth_size) {
		this.depth_size = depth_size;
	}

	public BigDecimal getVolatility() {
		return volatility;
	}

	public void setVolatility(BigDecimal volatility) {
		this.volatility = volatility;
	}

	public boolean isReminder_open() {
		return reminder_open;
	}

	public void setReminder_open(boolean reminder_open) {
		this.reminder_open = reminder_open;
	}

	public String getReminder_email() {
		return reminder_email;
	}

	public void setReminder_email(String reminder_email) {
		this.reminder_email = reminder_email;
	}

	public BigDecimal getDepth_buy_limit_amount() {
		return depth_buy_limit_amount;
	}

	public void setDepth_buy_limit_amount(BigDecimal depth_buy_limit_amount) {
		this.depth_buy_limit_amount = depth_buy_limit_amount;
	}

	public BigDecimal getDepth_sell_limit_amount() {
		return depth_sell_limit_amount;
	}

	public void setDepth_sell_limit_amount(BigDecimal depth_sell_limit_amount) {
		this.depth_sell_limit_amount = depth_sell_limit_amount;
	}

	public long getCheck_time() {
		return check_time;
	}

	public void setCheck_time(long check_time) {
		this.check_time = check_time;
	}

	public int getCycle_time() {
		return cycle_time;
	}

	public void setCycle_time(int cycle_time) {
		this.cycle_time = cycle_time;
	}

	public int getCycle_buy_size() {
		return cycle_buy_size;
	}

	public void setCycle_buy_size(int cycle_buy_size) {
		this.cycle_buy_size = cycle_buy_size;
	}

	public int getCycle_sell_size() {
		return cycle_sell_size;
	}

	public void setCycle_sell_size(int cycle_sell_size) {
		this.cycle_sell_size = cycle_sell_size;
	}

}
