package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberSafeSettingsService {
	
	void save(BQMemberSafeSettings bqMem);

	void updateTel(BQMemberSafeSettings bqms);

	void updateSafe(BQMemberSafeSettings bqs);

	/**
	 * 检查该用户是否认证过
	 * @param uid
	 * @return
	 */
	public boolean checkUserSafe(long uid);

	/**
	 * 解除绑定的谷歌验证码
	 * @param userId 用户ID
	 */
	public void removeGoogle(long userId);
	
	BQMemberSafeSettings getById(long fUserId);
	
	
	/************************************ for read only **************************************/
	
	BQMemberSafeSettings readById(long fUserId);
	
	BQMemberSafeSettings readByTel(String tel);
	
	public PagerModel readUserSafe(long userId);

	boolean readHaveTel(String tel);
	
}
