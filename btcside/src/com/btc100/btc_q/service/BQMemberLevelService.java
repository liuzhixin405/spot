package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberLevelService {
	
	/** 根据用户id查询用户等级 */
	BQMemberLevel findByUserId(long f_user_id);

	BQMemberLevel findById(long fUserLevelId);
	
	BQMemberLevel findNextLevelById(long fUserLevelId);

	BQMemberLevel findLevelId(int credits);
	
	BQMemberLevel findLevelByEatRate(BigDecimal rate);

	public List<BQMemberLevel> findAll(boolean isUsing);
	
	public List<BQMemberLevel> findAll();

	BQMemberLevel findByLevelId(long levelId);

	void save(BQMemberLevel level);

	void update(BQMemberLevel level);
	
	void delete(BQMemberLevel level);

	BQMemberLevel getMaxLevel();

	BQMemberLevel getMaxLevelExclusionState();

	BQMemberLevel findLevelBuyAmountById(long levelId);
	
	
	/************************************ for read only **************************************/
	
	BQMemberLevel readByUserId(long f_user_id);
	
	BQMemberLevel readById(long fUserLevelId);
	
	public PagerModel readLevels();

	BQMemberLevel readMaxLevel();
	
	BQMemberLevel readMaxLevelForLevelBuyAmount();
}
