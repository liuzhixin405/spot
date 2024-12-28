package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberLevelBuyRebateReconciliaDao;
import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;

@Repository("bqMemberLevelBuyRebateReconciliaDao")
public class BQMemberLevelBuyRebateReconciliaDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberLevelBuyRebateReconciliaDao<T> {

	public int updateRecord(T rd) {
		
		BQMemberLevelBuyRebateReconcilia record = (BQMemberLevelBuyRebateReconcilia) rd; 
		BQMemberLevelBuyRebateReconcilia tmp = this.getByUerId(record.getF_user_id());
		if (tmp == null) {
			this.save(rd);
			return 1;
		}
		
		
		String hql = "UPDATE BQMemberLevelBuyRebateReconcilia b SET b.total_buy_amount=b.total_buy_amount+:buyAmount," 
				+ "total_bfx_lock=total_bfx_lock+:bfxLock,total_bfx_unlock=total_bfx_unlock+:bfxUnlock,"
				+ "self_bfx_lock=self_bfx_lock+:selfLock WHERE id=:id";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("buyAmount", record.getTotal_buy_amount());
		map.put("bfxLock", record.getTotal_bfx_lock());
		map.put("bfxUnlock", record.getTotal_bfx_unlock());
		map.put("selfLock", record.getSelf_bfx_lock());
		map.put("id", tmp.getId());
		
		return this.updateCondition(hql, map);
	}

	@Override
	public BQMemberLevelBuyRebateReconcilia getByUerId(long uid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", uid);
		
		return (BQMemberLevelBuyRebateReconcilia) get("From BQMemberLevelBuyRebateReconcilia where f_user_id = :userId", params);
	}
	
}
