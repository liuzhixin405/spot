package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;

public interface BQMemberService {

	// 保存
	void save(BQMember bqMember);

	// 激活邮箱
	void updateEmailStatus(long fUserId, int state);
	
	void updateFeeRateType(long fUserId, int type);

	// 更密码
	void updatepassword(long uid, String password);

	void updateEmailByState(long uid, String email, boolean state);

	// 更改用户冻结
	void updatefreeze(long userid, int istate);
	
	void updateOms(long uid, int state);

	public BQMember findUserById(long userId);
	
	public BQMember getUserAboveId(long userId);

	void update(BQMember user);

	BQMember findUserByEmail(String uemail);

	/**
	 * 给用户增加2000积分
	 * @param title 积分
	 * @param title 标题
	 * @param title 类型
	 */
	public void updateUserCredits(long userId, long credit, String title, int type);
	
	long findCountByTime(long startTime, long endTime);
	
	long findCount();
	
	public void updateHasOpen(long uid);
	
	public void updateHeadImg(long uid, String path);
	
	public void checkVipLevel(BQMember user);
	
	public int updatePhone(BQMemberSafeSettings bqms);


	/************************************ for read only **************************************/
	
	// 检查用户名
	BQMember readCheckUserEmail(String email);
	
	// 检查真实邮箱邮箱
	BQMember readByRealEmail(String email);

	// 获取子账户
	BQMember readSubUser(String username);
	
	BQMember readUserById(long userId);
	
	BQMember readUserByEmail(String uemail);
	
	BQMember readUserByNickName(String nickName);
	
	PagerModel readByInviteManagement(int inviteManagement, long uid);
	
	PagerModel readByIdentity(int identity, long uid, String userEmail);
}
