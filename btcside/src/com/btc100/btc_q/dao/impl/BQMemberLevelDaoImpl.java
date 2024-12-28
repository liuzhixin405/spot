package com.btc100.btc_q.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.DBCache;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQMemberLevel;

@Repository("bqMemberLevelDao")
public class BQMemberLevelDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberLevelDao<T>{

	public com.btc100.btc_q.entity.BQMemberLevel findByUserId(long fUserId) {
		StringBuffer sql = new StringBuffer("SELECT l.* from bq_member_level l LEFT JOIN bq_member member on l.level_id = member.f_user_level_id where  member.f_user_id = :fUserId ");
		
		SQLQuery query = getSession().createSQLQuery(sql.toString());
        
		query.setParameter("fUserId", fUserId);
        
        query.addEntity(com.btc100.btc_q.entity.BQMemberLevel.class);
		return (com.btc100.btc_q.entity.BQMemberLevel) query.list().get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List getAllByCache(){
		List<BQMemberLevel> bqMemberLevel=(List<BQMemberLevel>)DBCache.getInsance().get("bqLevelCache");
		if(bqMemberLevel==null){
			bqMemberLevel = (List<BQMemberLevel>) super.getAllList(BQMemberLevel.class);
			DBCache.getInsance().put("bqLevelCache", bqMemberLevel);
		}
		
		return bqMemberLevel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getLevelByCredits(long value, boolean withHiddenState) {
		List<BQMemberLevel> list = new ArrayList<BQMemberLevel>();
		String hql = "from BQMemberLevel where 1=1 ";
		if (!withHiddenState) {
			hql += " and state=0";
		}
		hql += " order by level_credits desc";
		list = (List<BQMemberLevel>) find(hql);
	
		long levelId = 0;
		for (BQMemberLevel level : list) {
			if (value >= level.getLevel_credits()) {
				levelId = level.getLevel_id();
				break;
			}
		}
		
		return levelId;
	}

}
