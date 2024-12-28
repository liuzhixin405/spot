package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_arbitrage_setting")
public class VArbitrageSetting implements Serializable {

	private static final long serialVersionUID = 8609801548465756924L;
	
	private Long id;
	private Long depth_uid;
	private String f_code;
	private String depth_url;
	private String spare_depth_url;
	private boolean running;
	private boolean scalping;
	private boolean auto_trade = true;
	private boolean hedging = false;
	private boolean can_trade_self;
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
	private BigDecimal amount_mutil = BigDecimal.ONE;
	private BigDecimal depth_price_gap = BigDecimal.ZERO;
	private BigDecimal depth_amount_gap_min = BigDecimal.ZERO;
	private BigDecimal depth_amount_gap_max = BigDecimal.ZERO;
	private BigDecimal depth_index_range = BigDecimal.ZERO;
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
	
	public boolean getAuto_trade() {
		return auto_trade;
	}
	
	public void setAuto_trade(boolean auto) {
		auto_trade = auto;
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
	
	public BigDecimal getAmount_mutil() {
		return amount_mutil;
	}
	
	public void setAmount_mutil(BigDecimal mutil) {
		this.amount_mutil = mutil;
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

	public boolean isCan_trade_self() {
		return can_trade_self;
	}

	public void setCan_trade_self(boolean can_trade_self) {
		this.can_trade_self = can_trade_self;
	}

	public BigDecimal getDepth_price_gap() {
		return depth_price_gap;
	}

	public void setDepth_price_gap(BigDecimal depth_price_gap) {
		this.depth_price_gap = depth_price_gap;
	}

	public BigDecimal getDepth_amount_gap_min() {
		return depth_amount_gap_min;
	}

	public void setDepth_amount_gap_min(BigDecimal depth_amount_gap_min) {
		this.depth_amount_gap_min = depth_amount_gap_min;
	}

	public BigDecimal getDepth_amount_gap_max() {
		return depth_amount_gap_max;
	}

	public void setDepth_amount_gap_max(BigDecimal depth_amount_gap_max) {
		this.depth_amount_gap_max = depth_amount_gap_max;
	}

	public BigDecimal getDepth_index_range() {
		return depth_index_range;
	}

	public void setDepth_index_range(BigDecimal depth_index_range) {
		this.depth_index_range = depth_index_range;
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
}
