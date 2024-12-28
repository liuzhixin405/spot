package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
/**
 * 委托挂单表日志表
 * @author Administrator
 *
 */
@Entity
@Table(name = "pending_log") //TODO:怎么对应多个不同的表
@Immutable
public class VPendingLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5178299137410478962L;
	@Id
	@GeneratedValue
	private long log_id;
	private long delegation_id;//委托编号
	private long pending_no;//挂单流水号
	private long delegation_time;//委托时间,unix时间格式
	private long transaction_id;//合约号id
	private String transaction_code;//合约号
	private int trade_type_id; //操作类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓)
	private BigDecimal delegation_count;//委托数量
	private BigDecimal delegation_price;//委托价格
	private int multiple;//杠杆
	private BigDecimal deal_count;//已成交数量
	private BigDecimal undeal_count;//未成交数量
	private BigDecimal deposit;//保证金
	private int action;//成交/结算/强平/撤单
	private long dateline;//操作时间,unix时间格式
	private long f_user_id;//用户ID
	private int flag=0;//0表示正常挂单日志，1标识交易异常时转过来的挂单
	private BigDecimal avg_price = BigDecimal.ZERO;//成交均价
	private BigDecimal income = BigDecimal.ZERO;//盈亏
	private BigDecimal fee = BigDecimal.ZERO;//手续费
	private BigDecimal bfx_fee = BigDecimal.ZERO;//bfx抵扣手续费
	private int device = 0;//下单设备（0-PC，1-App，2-Api，3-服务器[强撮单等]）
	private String ip_addr = "";//下单IP
	
	public VPendingLog() {
		super();
	}
	
	public VPendingLog(long delegationId, long delegationTime,
			long transactionId, String transactionCode, int tradeTypeId,
			BigDecimal delegationCount, BigDecimal delegationPrice,
			int multiple, BigDecimal dealCount, BigDecimal undealCount,
			BigDecimal deposit, int action, long fUserId,long pendingNo,
			int device, String ipAddr) {
		super();
		delegation_id = delegationId;
		delegation_time = delegationTime;
		transaction_id = transactionId;
		transaction_code = transactionCode;
		trade_type_id = tradeTypeId;
		delegation_count = delegationCount;
		delegation_price = delegationPrice;
		this.multiple = multiple;
		deal_count = dealCount;
		undeal_count = undealCount;
		this.deposit = deposit;
		this.action = action;
		f_user_id = fUserId;
		pending_no = pendingNo;
		this.setDevice(device);
		this.setIp_addr(ipAddr);
	}
	
	public long getPending_no() {
		return pending_no;
	}
	public void setPending_no(long pendingNo) {
		pending_no = pendingNo;
	}
	public long getDelegation_id() {
		return delegation_id;
	}
	public void setDelegation_id(long delegationId) {
		delegation_id = delegationId;
	}
	public long getDelegation_time() {
		return delegation_time;
	}
	public void setDelegation_time(long delegationTime) {
		delegation_time = delegationTime;
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
	public BigDecimal getDelegation_count() {
		return delegation_count;
	}
	public void setDelegation_count(BigDecimal delegationCount) {
		delegation_count = delegationCount;
	}
	public BigDecimal getDelegation_price() {
		return delegation_price;
	}
	public void setDelegation_price(BigDecimal delegationPrice) {
		delegation_price = delegationPrice;
	}
	public BigDecimal getDeal_count() {
		return deal_count;
	}
	public void setDeal_count(BigDecimal dealCount) {
		deal_count = dealCount;
	}
	public BigDecimal getUndeal_count() {
		return undeal_count;
	}
	public void setUndeal_count(BigDecimal undealCount) {
		undeal_count = undealCount;
	}
	public int getMultiple() {
		return multiple;
	}
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}
	public BigDecimal getDeposit() {
		return deposit;
	}
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
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
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getAvg_price() {
		return avg_price;
	}

	public void setAvg_price(BigDecimal avg_price) {
		this.avg_price = avg_price;
	}

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public String getIp_addr() {
		return ip_addr;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getBfx_fee() {
		return bfx_fee;
	}

	public void setBfx_fee(BigDecimal bfx_fee) {
		this.bfx_fee = bfx_fee;
	}
}
