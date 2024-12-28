package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *合约结算表公共表(一个合约一条记录)
 */
@Entity
@Table(name = "delivery_transaction_settle")
public class DeliveryTransactionSettle implements Serializable {

	private static final long serialVersionUID = 757375105439849067L;

	private long id;
	private long transaction_id;// '合约ID',
	private String transaction_code;// 合约号',
	private BigDecimal price;// '结算价格',
	private BigDecimal trans_num;// '成交量',
	private BigDecimal system_profit_loss;// '系统盈亏',
	private BigDecimal rate;// '净盈利部分分摊比例',
	private long dateline;// '结算时间',

	public DeliveryTransactionSettle() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTrans_num() {
		return trans_num;
	}

	public void setTrans_num(BigDecimal transNum) {
		trans_num = transNum;
	}

	public BigDecimal getSystem_profit_loss() {
		return system_profit_loss;
	}

	public void setSystem_profit_loss(BigDecimal systemProfitLoss) {
		system_profit_loss = systemProfitLoss;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
