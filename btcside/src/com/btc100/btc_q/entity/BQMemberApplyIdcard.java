package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_apply_idcard")
public class BQMemberApplyIdcard implements Serializable {

	private static final long serialVersionUID = 6455472706197309685L;
	
	private long f_user_id;
	private String photo_1;
	private String photo_2;
	private String photo_3;

	public BQMemberApplyIdcard() {
	}

	public BQMemberApplyIdcard(long fUserId, String photo_1, String photo_2,
			String photo_3) {
		super();
		f_user_id = fUserId;
		this.photo_1 = photo_1;
		this.photo_2 = photo_2;
		this.photo_3 = photo_3;
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getPhoto_1() {
		return photo_1;
	}

	public void setPhoto_1(String photo_1) {
		this.photo_1 = photo_1;
	}

	public String getPhoto_2() {
		return photo_2;
	}

	public void setPhoto_2(String photo_2) {
		this.photo_2 = photo_2;
	}

	public String getPhoto_3() {
		return photo_3;
	}

	public void setPhoto_3(String photo_3) {
		this.photo_3 = photo_3;
	}

}
