package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_work_order_detail")
public class BQWorkOrderDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private long f_order_id; //工单ID
	
	private String f_order_code; //工单编号
	
	private String f_direction; //方向:用户(USER),系统(SYSTEM)
	
	private String f_user_name; //回复人员名称
	
	private String f_reply_content; //回复内容
	
	private String f_file; //上传附件
	
	private long f_create_time; //创建时间
	
	public BQWorkOrderDetail() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getF_order_id() {
		return f_order_id;
	}

	public void setF_order_id(long f_order_id) {
		this.f_order_id = f_order_id;
	}

	public String getF_order_code() {
		return f_order_code;
	}

	public void setF_order_code(String f_order_code) {
		this.f_order_code = f_order_code;
	}

	public String getF_direction() {
		return f_direction;
	}

	public void setF_direction(String f_direction) {
		this.f_direction = f_direction;
	}

	public String getF_user_name() {
		return f_user_name;
	}

	public void setF_user_name(String f_user_name) {
		this.f_user_name = f_user_name;
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



}
