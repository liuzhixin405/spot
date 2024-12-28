package com.btc100.btc_q.dao;

import java.util.List;

public interface BQMemberDao<BQMember> extends BaseDaoI<BQMember>{
	/**
	 * 模糊查询
	 * @param email
	 * @return
	 */
	List<BQMember> findsByEmail(String email);
	/**
	 * 精确查询
	 * @param email
	 * @return
	 */
	BQMember findByEmail(String email);
	/**
	 * 增加几分
	 * @param credits
	 * @param uid
	 * @return
	 */
	void addCredits(long credits, long vipId, long uid);
}
