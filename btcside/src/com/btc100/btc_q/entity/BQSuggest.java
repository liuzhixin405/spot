package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_suggest")
public class BQSuggest implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private long f_user_id;// 建议用户ID
	private String f_user_email;// 建议用户邮箱
	private int f_sug_type;// 类型 1.页面错误 2. 逻辑错误 3 Bug 4其他
	private String f_sug_content;// 内容
	private long f_sug_time;// 提交时间
	private int f_sug_state;// 状态 0 未处理 1 采纳 2拒绝
	private String f_sug_reply;// 采纳回复
	private long f_sug_endtime;// 回复时间

	public BQSuggest() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String fUserEmail) {
		f_user_email = fUserEmail;
	}

	public int getF_sug_type() {
		return f_sug_type;
	}

	public void setF_sug_type(int fSugType) {
		f_sug_type = fSugType;
	}

	public String getF_sug_content() {
		return f_sug_content;
	}

	public void setF_sug_content(String fSugContent) {
		f_sug_content = fSugContent;
	}

	public long getF_sug_time() {
		return f_sug_time;
	}

	public void setF_sug_time(long fSugTime) {
		f_sug_time = fSugTime;
	}

	public int getF_sug_state() {
		return f_sug_state;
	}

	public void setF_sug_state(int fSugState) {
		f_sug_state = fSugState;
	}

	public String getF_sug_reply() {
		return f_sug_reply;
	}

	public void setF_sug_reply(String fSugReply) {
		f_sug_reply = fSugReply;
	}

	public long getF_sug_endtime() {
		return f_sug_endtime;
	}

	public void setF_sug_endtime(long fSugEndtime) {
		f_sug_endtime = fSugEndtime;
	}

}
