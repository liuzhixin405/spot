package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 计划委托挂单表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "plan_pending_delegation")
public class DeliveryPlanPendingDelegation implements Serializable {

	private static final long serialVersionUID = 5653904921793791443L;

	private long id;
	private String f_code;// 合约号
	private long f_user_id;// 用户ID
	private int order_type = 0;
	private BigDecimal tri_price;// 触发价格
	private BigDecimal delegation_price;// 委托价格
	private BigDecimal delegation_count;// 委托数量
	private long delegation_time;// 委托时间,unix时间格式
	private int multiple;// 杠杆
	private int direction;// 操作方向
	private int type;// 类型（1-现价大于触发价时进行委托，2-现价低于触发价时进行委托）
	private int status;// 状态（0-未生效，1-已生效，2-失败，3-已撤销）
	private String note;// 备注

	public DeliveryPlanPendingDelegation() {

	}
	
	public DeliveryPlanPendingDelegation(String code, long userId, BigDecimal triPrice, BigDecimal delegationPrice,
			BigDecimal delegationCount, int multiple, int direction, int type, int status, String note, int orderType) {

		super();
		this.f_code = code;
		this.f_user_id = userId;
		this.tri_price = triPrice;
		this.delegation_price = delegationPrice;
		this.delegation_count = delegationCount;
		this.multiple = multiple;
		this.direction = direction;
		this.type = type;
		this.status = status;
		this.note = note;
		this.order_type = orderType;
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public BigDecimal getTri_price() {
		return tri_price;
	}

	public void setTri_price(BigDecimal tri_price) {
		this.tri_price = tri_price;
	}

	public BigDecimal getDelegation_price() {
		return delegation_price;
	}

	public void setDelegation_price(BigDecimal delegation_price) {
		this.delegation_price = delegation_price;
	}

	public BigDecimal getDelegation_count() {
		return delegation_count;
	}

	public void setDelegation_count(BigDecimal delegation_count) {
		this.delegation_count = delegation_count;
	}

	public long getDelegation_time() {
		return delegation_time;
	}

	public void setDelegation_time(long delegation_time) {
		this.delegation_time = delegation_time;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
