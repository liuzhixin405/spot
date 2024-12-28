package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_spot_arbitrage_setting")
public class BQSpotArbitrageSetting implements Serializable {

	private static final long serialVersionUID = 3128160434069828725L;
	
	private Long id;
	private Long depth_uid;
	private String f_code;
	private String depth_url;
	private String spare_depth_url;
	private boolean running = false;
	private boolean scalping = false;
	private boolean hedging = false;
	private BigDecimal depth_buy_range = BigDecimal.ZERO;
	private BigDecimal depth_sell_range = BigDecimal.ZERO;
	private BigDecimal depth_amount_min = BigDecimal.ZERO;
	private BigDecimal depth_amount_max = BigDecimal.ZERO;
	private int scalping_frequency_min = 0;
	private int scalping_frequency_max = 0;
	private BigDecimal scalping_min_each = BigDecimal.ZERO;
	private BigDecimal scalping_max_each = BigDecimal.ZERO;
	private BigDecimal hedging_buy_range = BigDecimal.ZERO;
	private BigDecimal hedging_sell_range = BigDecimal.ZERO;
	private BigDecimal scalping_index_range = BigDecimal.ZERO;
	private BigDecimal scalping_depth_range = BigDecimal.ZERO;
	private int curr_use_state = 0;
	private boolean risk_manage = false;
	
	private BigDecimal depth_buy_price_gap = BigDecimal.ZERO;			// 买盘递减价格
	private BigDecimal depth_sell_price_gap = BigDecimal.ZERO;			// 卖盘递增价格
	
	private BigDecimal depth_buy_amount_min = BigDecimal.ZERO;			// 买盘一单最小数量
	private BigDecimal depth_sell_amount_min = BigDecimal.ZERO;			// 卖盘一单最小数量
	private BigDecimal depth_buy_amount_max = BigDecimal.ZERO;			// 买盘一单最大数量
	private BigDecimal depth_sell_amount_max = BigDecimal.ZERO;			// 卖盘一单最大数量
	private BigDecimal depth_buy_backhand_gap = BigDecimal.ZERO;		// 买盘反手挂单差价
	private BigDecimal depth_sell_backhand_gap = BigDecimal.ZERO;		// 买盘反手挂单差价
	
	private BigDecimal depth_buy_amount_other_min = BigDecimal.ZERO; 	// 买盘数量区间叠加最小值
	private BigDecimal depth_sell_amount_other_min = BigDecimal.ZERO; 	// 卖盘数量区间叠加最小值
	private BigDecimal depth_buy_amount_other_max = BigDecimal.ZERO; 	// 买盘数量区间叠加最大值
	private BigDecimal depth_sell_amount_other_max = BigDecimal.ZERO; 	// 卖盘数量区间叠加最大值
	
	private BigDecimal open_price = BigDecimal.ZERO;		// 开盘价格
	
	private boolean depth_self = false;						// 是否站内自己挂单(否：引入站外深度)
	private boolean depth_backhand = true;					// 是否反手挂单
	private boolean depth_increasing = false;				// 是否递增叠加
	private boolean depth_reset_open_price = true;			// 是否重置价格
	private boolean can_trade_self = false;					// 是否可自成交
	private int price_digits = 0;
	private int amount_digits = 0;
	private int depth_size = 0;
	
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getDepth_uid() {
		return depth_uid;
	}
	
	public void setDepth_uid(Long uid) {
		depth_uid = uid;
	}
	
	public String getF_code() {
		return f_code;
	}
	
	public void setF_code(String code) {
		f_code = code;
	}
	
	public String getDepth_url() {
		return depth_url;
	}
	
	public void setDepth_url(String url) {
		depth_url = url;
	}
	
	public String getSpare_depth_url() {
		return spare_depth_url;
	}
	
	public void setSpare_depth_url(String url) {
		this.spare_depth_url = url;
	}

	public BigDecimal getDepth_buy_range() {
		return depth_buy_range;
	}
	
	public void setDepth_buy_range(BigDecimal range) {
		depth_buy_range = range;
	}
	
	public BigDecimal getDepth_sell_range() {
		return depth_sell_range;
	}
	
	public void setDepth_sell_range(BigDecimal range) {
		depth_sell_range = range;
	}
	
	public BigDecimal getDepth_amount_min() {
		return depth_amount_min; 
	}
	
	public void setDepth_amount_min(BigDecimal amount) {
		depth_amount_min = amount;
	}
	
	public BigDecimal getDepth_amount_max() {
		return depth_amount_max; 
	}
	
	public void setDepth_amount_max(BigDecimal amount) {
		depth_amount_max = amount;
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
	
	public boolean getHedging() {
		return hedging;
	}
	
	public void setHedging(boolean hedging) {
		this.hedging = hedging;
	}
	
	public BigDecimal getHedging_buy_range() {
		return hedging_buy_range;
	}
	
	public void setHedging_buy_range(BigDecimal range) {
		hedging_buy_range = range;
	}
	
	public BigDecimal getHedging_sell_range() {
		return hedging_sell_range;
	}
	
	public void setHedging_sell_range(BigDecimal range) {
		hedging_sell_range = range;
	}
	
	public int getCurr_use_state() {
		return curr_use_state;
	}
	
	public void setCurr_use_state(int state) {
		this.curr_use_state = state;
	}
	
	public boolean getRisk_manage() {
		return risk_manage;
	}
	
	public void setRisk_manage(boolean state) {
		this.risk_manage = state;
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

	public BigDecimal getDepth_buy_amount_other_min() {
		return depth_buy_amount_other_min;
	}

	public void setDepth_buy_amount_other_min(BigDecimal depth_buy_amount_other_min) {
		this.depth_buy_amount_other_min = depth_buy_amount_other_min;
	}

	public BigDecimal getDepth_sell_amount_other_min() {
		return depth_sell_amount_other_min;
	}

	public void setDepth_sell_amount_other_min(BigDecimal depth_sell_amount_other_min) {
		this.depth_sell_amount_other_min = depth_sell_amount_other_min;
	}

	public BigDecimal getDepth_buy_amount_other_max() {
		return depth_buy_amount_other_max;
	}

	public void setDepth_buy_amount_other_max(BigDecimal depth_buy_amount_other_max) {
		this.depth_buy_amount_other_max = depth_buy_amount_other_max;
	}

	public BigDecimal getDepth_sell_amount_other_max() {
		return depth_sell_amount_other_max;
	}

	public void setDepth_sell_amount_other_max(BigDecimal depth_sell_amount_other_max) {
		this.depth_sell_amount_other_max = depth_sell_amount_other_max;
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

	public boolean isDepth_increasing() {
		return depth_increasing;
	}

	public void setDepth_increasing(boolean depth_increasing) {
		this.depth_increasing = depth_increasing;
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

	public boolean isCan_trade_self() {
		return can_trade_self;
	}

	public void setCan_trade_self(boolean can_trade_self) {
		this.can_trade_self = can_trade_self;
	}

	public boolean getDepth_self() {
		return depth_self;
	}

	public void setDepth_self(boolean depth_self) {
		this.depth_self = depth_self;
	}
}
