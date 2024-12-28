package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "bq_sub_member_transfer_log")
@Immutable
public class BQSubMemberTransferLog implements Serializable {

	private static final long serialVersionUID = -420283900156793339L;

	private long id;
	private int currency_type_id;// 币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',
	private String currency_name;// '币种名称',
	private int operation;// 操作类型(1:母账户转入子账户/2:子账户转出到母账户)',
	private BigDecimal amount;// '金额',
	private long dateline;// '时间',
	private long mother_user_id;// '母账户id',
	private String mother_user_email;// '母账户用户名',
	private long sub_user_id;// '子账户id',
	private String sub_user_email;// '子账户用户名',
	private String remark;// '备注说明',

	public BQSubMemberTransferLog() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public String getCurrency_name() {
		return currency_name;
	}

	public void setCurrency_name(String currency_name) {
		this.currency_name = currency_name;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public long getMother_user_id() {
		return mother_user_id;
	}

	public void setMother_user_id(long mother_user_id) {
		this.mother_user_id = mother_user_id;
	}

	public String getMother_user_email() {
		return mother_user_email;
	}

	public void setMother_user_email(String mother_user_email) {
		this.mother_user_email = mother_user_email;
	}

	public long getSub_user_id() {
		return sub_user_id;
	}

	public void setSub_user_id(long sub_user_id) {
		this.sub_user_id = sub_user_id;
	}

	public String getSub_user_email() {
		return sub_user_email;
	}

	public void setSub_user_email(String sub_user_email) {
		this.sub_user_email = sub_user_email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
