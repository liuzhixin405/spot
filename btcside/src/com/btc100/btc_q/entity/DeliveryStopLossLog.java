package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
/**
 * 止盈止损日志表
 * @author Administrator
 *
 */
@Entity
@Table(name = "delivery_stop_loss_log") //TODO:怎么对应多个不同的表
@Immutable
public class DeliveryStopLossLog implements Serializable {

	private static final long serialVersionUID = 6846546526450302319L;

	@Id
	@GeneratedValue
	private long log_id;
	private long transaction_id;//合约号id
	private String transaction_code;//合约号
	private int trade_type_id; //1 买多, 2卖空 (成交类型1:多单开仓,买多/2:空头开仓,卖空)
	private long f_user_id;//用户ID
	private long dateline;//操作时间,unix时间格式
	private int action;//成交/结算/强平/撤单
	private String remark;//备注原因
	private int stop_loss_type_id;//1止盈 2止损
	private long transaction_member_id;//持仓ID
	private BigDecimal stop_loss_price;//止损价
	private BigDecimal pending_trans_price;//委托价
	
	public DeliveryStopLossLog(DeliveryStopLoss stopLoss, int action, String remark) {
		this.transaction_id = stopLoss.getTransaction_id();
		this.transaction_code = stopLoss.getTransaction_code();
		this.trade_type_id = stopLoss.getTrade_type_id();
		this.f_user_id = stopLoss.getF_user_id();
		this.action = action;
		this.remark = remark;
		this.stop_loss_type_id = stopLoss.getStop_loss_type_id();
		this.stop_loss_price = stopLoss.getPending_trans_price();
		this.transaction_member_id = stopLoss.getTransaction_member_id();
		this.pending_trans_price = stopLoss.getPending_trans_price();
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

	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public long getLog_id() {
		return log_id;
	}
	
	public void setLog_id(long logId) {
		log_id = logId;
	}
	
	public int getTrade_type_id() {
		return trade_type_id;
	}
	
	public void setTrade_type_id(int tradeTypeId) {
		trade_type_id = tradeTypeId;
	}
	
	public int getAction() {
		return action;
	}
	
	public void setAction(int action) {
		this.action = action;
	}
	
	public long getDateline() {
		return dateline;
	}
	
	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public int getStop_loss_type_id() {
		return stop_loss_type_id;
	}

	public void setStop_loss_type_id(int value) {
		stop_loss_type_id = value;
	}

	public long getTransaction_member_id() {
		return transaction_member_id;
	}

	public void setTransaction_member_id(long value) {
		transaction_member_id = value;
	}

	public BigDecimal getStop_loss_price() {
		return stop_loss_price;
	}

	public void setStop_loss_price(BigDecimal value) {
		stop_loss_price = value;
	}

	public BigDecimal getPending_trans_price() {
		return pending_trans_price;
	}

	public void setPending_trans_price(BigDecimal value) {
		pending_trans_price = value;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
