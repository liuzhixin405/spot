package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 合约表（当前进行中的合约/已经清算完的合约/未开始的合约）
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "n_transaction")
public class NTransaction implements Serializable {

	private static final long serialVersionUID = -4868635401917763839L;

	private long transaction_id;
	private String transaction_code;
	private long transaction_begin_time;
	private long transaction_end_time;
	private int transaction_state;
	private BigDecimal latest_transaction_price;
	private BigDecimal transaction_highest;
	private BigDecimal transaction_lowest;
	private int sort;
	private String multiple;
	private String multiple_state;
	private BigDecimal trade_coefficient;
	private int transaction_type;
	private String transaction_type_code;
	private boolean suspend = false;
	private boolean transfer_in_enable = true;
	private boolean transfer_out_enable = true;
	private int currency_type_id;
	private Integer transaction_kind = 0;
	private String transaction_show_name;
	private String transaction_show_name_en = "";
	private String transaction_show_name_jp = "";
	private String transaction_show_name_kr = "";
	private String transaction_show_name_hk = "";
	
	public NTransaction() {

	}

	public NTransaction(String transactionCode, long transactionBeginTime, long transactionEndTime, int transactionState, BigDecimal latestTransactionPrice,
			BigDecimal transactionHighest, BigDecimal transactionLowest, int sort, String multiple, BigDecimal tradeCoefficient, String multiplestate,
			int transactiontype, String typeCode, int transactionKind, int crrencyId, String showName) {
		transaction_code = transactionCode;
		transaction_begin_time = transactionBeginTime;
		transaction_end_time = transactionEndTime;
		transaction_state = transactionState;
		latest_transaction_price = latestTransactionPrice;
		transaction_highest = transactionHighest;
		transaction_lowest = transactionLowest;
		this.sort = sort;
		this.multiple = multiple;
		this.trade_coefficient = tradeCoefficient;
		multiple_state = multiplestate;
		transaction_type = transactiontype;
		this.transaction_type_code = typeCode;
		transaction_kind = transactionKind;
		transaction_show_name = showName;
		currency_type_id = crrencyId;
	}

	@Id
	@GeneratedValue
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

	public long getTransaction_begin_time() {
		return transaction_begin_time;
	}

	public void setTransaction_begin_time(long transactionBeginTime) {
		transaction_begin_time = transactionBeginTime;
	}

	public int getTransaction_state() {
		return transaction_state;
	}

	public void setTransaction_state(int transactionState) {
		transaction_state = transactionState;
	}

	public BigDecimal getTransaction_highest() {
		return transaction_highest;
	}

	public void setTransaction_highest(BigDecimal transactionHighest) {
		transaction_highest = transactionHighest;
	}

	public BigDecimal getTransaction_lowest() {
		return transaction_lowest;
	}

	public void setTransaction_lowest(BigDecimal transactionLowest) {
		transaction_lowest = transactionLowest;
	}

	public BigDecimal getLatest_transaction_price() {
		return latest_transaction_price;
	}

	public void setLatest_transaction_price(BigDecimal latestTransactionPrice) {
		latest_transaction_price = latestTransactionPrice;
	}

	public long getTransaction_end_time() {
		return transaction_end_time;
	}

	public void setTransaction_end_time(long transactionEndTime) {
		transaction_end_time = transactionEndTime;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public BigDecimal getTrade_coefficient() {
		return trade_coefficient;
	}

	public void setTrade_coefficient(BigDecimal tradeCoefficient) {
		trade_coefficient = tradeCoefficient;
	}

	public String getMultiple_state() {
		return multiple_state;
	}

	public void setMultiple_state(String multipleState) {
		multiple_state = multipleState;
	}

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transactionType) {
		transaction_type = transactionType;
	}
	
	public String getTransaction_type_code() {
		return transaction_type_code;
	}

	public void setTransaction_type_code(String transactionTypeCode) {
		this.transaction_type_code = transactionTypeCode;
	}
	
	public boolean getSuspend() {
		return suspend;
	}
	
	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}
	
	public boolean getTransfer_in_enable() {
		return transfer_in_enable;
	}
	
	public void setTransfer_in_enable(boolean enable) {
		this.transfer_in_enable = enable;
	}
	
	public boolean getTransfer_out_enable() {
		return transfer_out_enable;
	}

	public void setTransfer_out_enable(boolean enable) {
		this.transfer_out_enable = enable;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int currencyId) {
		this.currency_type_id = currencyId;
	}

	public Integer getTransaction_kind() {
		return transaction_kind;
	}

	public void setTransaction_kind(Integer transaction_kind) {
		this.transaction_kind = transaction_kind;
	}

	public String getTransaction_show_name() {
		return transaction_show_name;
	}

	public void setTransaction_show_name(String transaction_show_name) {
		this.transaction_show_name = transaction_show_name;
	}

	public String getTransaction_show_name_en() {
		return transaction_show_name_en;
	}

	public void setTransaction_show_name_en(String transaction_show_name_en) {
		this.transaction_show_name_en = transaction_show_name_en;
	}

	public String getTransaction_show_name_jp() {
		return transaction_show_name_jp;
	}

	public void setTransaction_show_name_jp(String transaction_show_name_jp) {
		this.transaction_show_name_jp = transaction_show_name_jp;
	}

	public String getTransaction_show_name_kr() {
		return transaction_show_name_kr;
	}

	public void setTransaction_show_name_kr(String transaction_show_name_kr) {
		this.transaction_show_name_kr = transaction_show_name_kr;
	}

	public String getTransaction_show_name_hk() {
		return transaction_show_name_hk;
	}

	public void setTransaction_show_name_hk(String transaction_show_name_hk) {
		this.transaction_show_name_hk = transaction_show_name_hk;
	}
	
}
