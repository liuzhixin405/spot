package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member")
public class BQMember implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;

	private long f_user_id;
	private String f_user_email;
	private String f_real_email = "";// '用户真实邮箱',
	private int f_user_email_status;// '邮箱认证(1:已认证 0:未认证)',
	private String f_user_pwd;
	private long f_user_credits;// '积分(活跃度)
	private long f_user_level_id;// '用户VIP级别(bq_member_level:level_id)',
	private int f_user_state;// '用户状态（0:无效,1:有效,-1:冻结）
	private String f_user_salt;// 密码随机码6位salt
	private int f_user_time;// 注册时间,unix时间格式
	private int has_open_position;// 是否开过仓(0,没开过，1开过)
	private String f_user_origin;// 用户来源
	private int f_first_login;// bcex用户是否第一次登陆(0:否,1:是)
	private int f_fee_rate_type = 0;// bfx抵扣手续费(0:否,1:是)
	private int is_sub_user = 0;// 是否是子账号(0:否,1:是)
	private String sub_user_note = "";// 子账号备注
	private int sub_user_delete = 0;// 子账号是否删除(0:否,1:是)
	private int open_invite_management = 0;// 是否开启会员管理(0:否,1:是)
	private int f_user_identity = 0;// '用户身份(0:普通用户,1:代理人)',
	private String f_user_nick_name;
	private String f_user_head_img = "";
	private int f_user_oms = 0;// '用户是否属于代理系统(0:不属于,1:属于)',

	public BQMember() {
	}

	@Id
	@GeneratedValue
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

	public int getF_user_email_status() {
		return f_user_email_status;
	}

	public void setF_user_email_status(int fUserEmailStatus) {
		f_user_email_status = fUserEmailStatus;
	}

	public String getF_user_pwd() {
		return f_user_pwd;
	}

	public void setF_user_pwd(String fUserPwd) {
		f_user_pwd = fUserPwd;
	}

	public long getF_user_credits() {
		return f_user_credits;
	}

	public void setF_user_credits(long fUserCredits) {
		f_user_credits = fUserCredits;
	}

	public long getF_user_level_id() {
		return f_user_level_id;
	}

	public void setF_user_level_id(long fUserLevelId) {
		f_user_level_id = fUserLevelId;
	}

	public int getF_user_state() {
		return f_user_state;
	}

	public void setF_user_state(int fUserState) {
		f_user_state = fUserState;
	}

	public String getF_user_salt() {
		return f_user_salt;
	}

	public void setF_user_salt(String fUserSalt) {
		f_user_salt = fUserSalt;
	}

	public int getF_user_time() {
		return f_user_time;
	}

	public void setF_user_time(int fUserTime) {
		f_user_time = fUserTime;
	}

	public int getHas_open_position() {
		return has_open_position;
	}

	public void setHas_open_position(int hasOpenPosition) {
		has_open_position = hasOpenPosition;
	}

	public String getF_user_origin() {
		return f_user_origin;
	}

	public void setF_user_origin(String f_user_origin) {
		this.f_user_origin = f_user_origin;
	}

	public int getF_first_login() {
		return f_first_login;
	}

	public void setF_first_login(int f_first_login) {
		this.f_first_login = f_first_login;
	}

	public int getF_fee_rate_type() {
		return f_fee_rate_type;
	}

	public void setF_fee_rate_type(int f_fee_rate_type) {
		this.f_fee_rate_type = f_fee_rate_type;
	}

	public int getIs_sub_user() {
		return is_sub_user;
	}

	public void setIs_sub_user(int is_sub_user) {
		this.is_sub_user = is_sub_user;
	}

	public String getSub_user_note() {
		return sub_user_note;
	}

	public void setSub_user_note(String sub_user_note) {
		this.sub_user_note = sub_user_note;
	}

	public int getSub_user_delete() {
		return sub_user_delete;
	}

	public void setSub_user_delete(int sub_user_delete) {
		this.sub_user_delete = sub_user_delete;
	}

	public int getOpen_invite_management() {
		return open_invite_management;
	}

	public void setOpen_invite_management(int open_invite_management) {
		this.open_invite_management = open_invite_management;
	}

	public int getF_user_identity() {
		return f_user_identity;
	}

	public void setF_user_identity(int f_user_identity) {
		this.f_user_identity = f_user_identity;
	}

	public String getF_user_nick_name() {
		return f_user_nick_name;
	}

	public void setF_user_nick_name(String f_user_nick_name) {
		this.f_user_nick_name = f_user_nick_name;
	}

	public String getF_user_head_img() {
		return f_user_head_img;
	}

	public void setF_user_head_img(String f_user_head_img) {
		this.f_user_head_img = f_user_head_img;
	}

	public int getF_user_oms() {
		return f_user_oms;
	}

	public void setF_user_oms(int f_user_oms) {
		this.f_user_oms = f_user_oms;
	}

	public String getF_real_email() {
		return f_real_email;
	}

	public void setF_real_email(String f_real_email) {
		this.f_real_email = f_real_email;
	}

}
