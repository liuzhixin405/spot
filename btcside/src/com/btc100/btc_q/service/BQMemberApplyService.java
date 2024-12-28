package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberApply;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberApplyService {

	/**
	 * 保存申请人信息
	 * 
	 * @param apply
	 */
	public void saveApply(BQMemberApply apply);

	/**
	 * 通过用户id查询申请者
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public BQMemberApply searchApplyByUserid(long userId);

	/**
	 * 通过用户ID删除申请者
	 * 
	 * @param userId
	 *            用户ID
	 */
	public void deleteApplyByUserid(long userId);

	/**
	 * 通过用户的身份证查询申请者
	 * 
	 * @param userNo
	 * @return
	 */
	public boolean findApplyByNo(String userNo);

	/**
	 * 通过类型和状态查询申请者
	 * 
	 * @param type
	 *            类型（1:自由经纪人 2:区域代理商）
	 * @param state
	 *            状态（1:通过审核 0:待审 -1:拒绝）
	 * @return
	 */
	public List<BQMemberApply> searchApplyByParas(int type, int state);

	/**
	 * 对申请人进行管理
	 * 
	 * @param id
	 *            申请人id
	 * @param state
	 *            类型（1：通过，-1：拒绝）
	 * @param reason
	 *            理由
	 */
	public void updateApplyByParas(long id, int state, String reason);

	/**
	 * 经纪人
	 * 
	 * @param userType
	 * @return
	 */
	public PagerModel searchApply(int userType);

	/**
	 * 通过参数查找申请人
	 * 
	 * @param state
	 *            状态（1:通过审核 0:待审 -1:拒绝）
	 * @param userType
	 *            类型（1:自由经纪人 2:区域代理商）
	 * @return
	 */
	public PagerModel findApplyByParams(int state, int userType);

	/**
	 * 后台添加高级经纪人
	 * @param userId
	 * @param userNo
	 * @param userTel
	 * @param userAddr
	 */
	void appendbroker(long userId,String userNo,String userTel,String userAddr);
}
