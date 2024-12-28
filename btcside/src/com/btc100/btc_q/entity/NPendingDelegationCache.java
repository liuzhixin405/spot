package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.btc100.btc_q.util.RandomUtil;
/**
 * 委托挂单表（非全部成交）缓存表
 * @author Administrator
 *
 */
@Entity
@Table(name = "n_pending_delegation_cache") //TODO:怎么对应多个不同的表
@Immutable
public class NPendingDelegationCache implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5178299137410478963L;
	@Id
	@GeneratedValue
	private long delegation_id;//委托编号
	private long delegation_time;//委托时间,unix时间格式
	private long pending_no;//挂单流水号
	private long transaction_id;//合约号id
	private String transaction_code;//合约号
	private int direction;//操作方向
	private BigDecimal delegation_count;//委托数量
	private BigDecimal delegation_price;//委托价格
	private BigDecimal deal_count;//已成交数量
	private BigDecimal undeal_count;//未成交数量
	private int multiple;//杠杆
	private BigDecimal deposit;//保证金
	private BigDecimal commission_rate;//手续费率
	private int priority;//挂单成交优先级
	private long f_user_id;//用户ID
	private int flag=0;//0默认，1交易中，2撤单中（其他）
	private BigDecimal f_fromremain;//保证金来源于余额的量
	private BigDecimal f_frozenremain = BigDecimal.ZERO;//冻结备用金
	BigDecimal f_enforce_price=BigDecimal.ZERO;
	
	public NPendingDelegationCache() {
		super();
	}
	
	public NPendingDelegationCache(long transactionId, String transactionCode,
			int direction, BigDecimal delegationCount,
			BigDecimal delegationPrice, BigDecimal dealCount,
			BigDecimal undealCount, int multiple, BigDecimal deposit, BigDecimal commissionRate,
			int priority, long fUserId,BigDecimal fromremain,BigDecimal fenforceprice) {
		super();
		this.transaction_id = transactionId;
		this.transaction_code = transactionCode;
		this.direction = direction;
		this.delegation_count = delegationCount;
		this.delegation_price = delegationPrice;
		this.deal_count = dealCount;
		this.undeal_count = undealCount;
		this.multiple = multiple;
		this.deposit = deposit;
		this.commission_rate = commissionRate;
		this.priority = priority;
		this.f_user_id = fUserId;
		this.f_fromremain=fromremain;
		this.f_enforce_price=fenforceprice;
		this.pending_no = RandomUtil.getPendingNo();
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
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
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
	public BigDecimal getCommission_rate() {
		return commission_rate;
	}
	public void setCommission_rate(BigDecimal commissionRate) {
		this.commission_rate = commissionRate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getF_fromremain() {
		return f_fromremain;
	}

	public void setF_fromremain(BigDecimal fFromremain) {
		f_fromremain = fFromremain;
	}

	public BigDecimal getF_enforce_price() {
		return f_enforce_price;
	}

	public void setF_enforce_price(BigDecimal fEnforcePrice) {
		f_enforce_price = fEnforcePrice;
	}

	public BigDecimal getF_frozenremain() {
		return f_frozenremain;
	}
	
	public void setF_frozenremain(BigDecimal f_frozenremain) {
		this.f_frozenremain = f_frozenremain;
	}
	
}
