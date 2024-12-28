package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户认证信息
 * @author win7
 *
 */
@Entity
@Table(name = "bq_member_validate_info")
public class BQMemberValidateInfo implements Serializable {

private static final long serialVersionUID = -2912697022165838258L;
	
	private long id;
	private long user_id; // 用户id
	private String real_name; // 实名认证，用户姓名
	private String card_type; // 实名认证，证件类型
	private String card_no; // 实名认证，证件号码
	private String card_img; // 实名认证，证件图片
	private String card_front_img; // 实名认证，证件正面图片
	private String card_back_img; // 实名认证，证件反面图片
	private String card_due_time; // 实名认证，证件到期时间
	private String validate_source; // 实名认证来源
	private String validate_state; // 实名认证状态
	private String note; // 备注
	private long validate_time; // 初次认证时间
	private long last_validate_time; // 最新认证时间

	public BQMemberValidateInfo() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getCard_img() {
		return card_img;
	}

	public void setCard_img(String card_img) {
		this.card_img = card_img;
	}

	public String getCard_front_img() {
		return card_front_img;
	}

	public void setCard_front_img(String card_front_img) {
		this.card_front_img = card_front_img;
	}

	public String getCard_back_img() {
		return card_back_img;
	}

	public void setCard_back_img(String card_back_img) {
		this.card_back_img = card_back_img;
	}

	public String getCard_due_time() {
		return card_due_time;
	}

	public void setCard_due_time(String card_due_time) {
		this.card_due_time = card_due_time;
	}

	public String getValidate_source() {
		return validate_source;
	}

	public void setValidate_source(String validate_source) {
		this.validate_source = validate_source;
	}

	public String getValidate_state() {
		return validate_state;
	}

	public void setValidate_state(String validate_state) {
		this.validate_state = validate_state;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getValidate_time() {
		return validate_time;
	}

	public void setValidate_time(long validate_time) {
		this.validate_time = validate_time;
	}

	public long getLast_validate_time() {
		return last_validate_time;
	}

	public void setLast_validate_time(long last_validate_time) {
		this.last_validate_time = last_validate_time;
	}

}
