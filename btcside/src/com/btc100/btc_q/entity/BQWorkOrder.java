package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_work_order")
public class BQWorkOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private String f_order_code; //工单编号
	
	private long f_user_id;// 用户ID
	
	private String f_user_email;// 用户邮箱
	
	private String f_order_type; //工单类型：账户类(ACCOUNT),交易类(TRADE),投诉类(COMPLAIN),充提类(RW),合作类(COOP)
	
	private String f_from_order_state; //(用户)工单发起方显示状态: 未读(UNREAD),已读(READ),已发送(SEND),关闭(CLOSE)
	
	private String f_to_order_state; //(管理员)工单接收方显示状态: 未读(UNREAD),已读(READ),已发送(SEND),关闭(CLOSE)
	
	private String f_theme; //主题
	
	private String f_reply_content; //回复内容
	
	private String f_file; //上传附件
	
	private long f_create_time; //创建时间
	
	private long f_update_time; //更新时间

	public BQWorkOrder() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getF_order_code() {
		return f_order_code;
	}

	public void setF_order_code(String f_order_code) {
		this.f_order_code = f_order_code;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String f_user_email) {
		this.f_user_email = f_user_email;
	}

	public String getF_order_type() {
		return f_order_type;
	}

	public void setF_order_type(String f_order_type) {
		this.f_order_type = f_order_type;
	}

	public String getF_from_order_state() {
		return f_from_order_state;
	}

	public void setF_from_order_state(String f_from_order_state) {
		this.f_from_order_state = f_from_order_state;
	}

	public String getF_to_order_state() {
		return f_to_order_state;
	}

	public void setF_to_order_state(String f_to_order_state) {
		this.f_to_order_state = f_to_order_state;
	}

	public String getF_theme() {
		return f_theme;
	}

	public void setF_theme(String f_theme) {
		this.f_theme = f_theme;
	}

	public String getF_reply_content() {
		return f_reply_content;
	}

	public void setF_reply_content(String f_reply_content) {
		this.f_reply_content = f_reply_content;
	}

	public String getF_file() {
		return f_file;
	}

	public void setF_file(String f_file) {
		this.f_file = f_file;
	}

	public long getF_create_time() {
		return f_create_time;
	}

	public void setF_create_time(long f_create_time) {
		this.f_create_time = f_create_time;
	}

	public long getF_update_time() {
		return f_update_time;
	}

	public void setF_update_time(long f_update_time) {
		this.f_update_time = f_update_time;
	}

}
