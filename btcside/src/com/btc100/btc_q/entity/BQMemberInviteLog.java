package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_invite_log")
public class BQMemberInviteLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private long f_user_id;// '被邀请用户ID',
	private Long invite_uid;// '邀请人uid',
	private int invite_time;// 邀请时间,unix时间格式,

	public BQMemberInviteLog() {
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public Long getInvite_uid() {
		return invite_uid;
	}

	public void setInvite_uid(Long inviteUid) {
		invite_uid = inviteUid;
	}

	public int getInvite_time() {
		return invite_time;
	}

	public void setInvite_time(int inviteTime) {
		invite_time = inviteTime;
	}

}
