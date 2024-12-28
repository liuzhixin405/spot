package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.vo.PagerModel;
@Service("bqMemberLevelService")
public class BQMemberLevelServiceImpl implements BQMemberLevelService{
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	
	public BQMemberLevel findByUserId(long fUserId) {
		BQMemberLevel level = bqMemberLevelDao.findByUserId(fUserId);
		return level;
	}
	
	public BQMemberLevel readByUserId(long fUserId) {
		BQMemberLevel level = bqMemberLevelDao.findByUserId(fUserId);
		return level;
	}
	
	public List<BQMemberLevel> findAll() {
		String hql = "from BQMemberLevel where state = 0";
		List<BQMemberLevel> list = bqMemberLevelDao.find(hql);
		return list;
	}
	
	public List<BQMemberLevel> findAll(boolean isUsing){
		String hql = "from BQMemberLevel";
		if (isUsing) {
			hql += " where state = 0";
		}
		List<BQMemberLevel> list = bqMemberLevelDao.find(hql);
		return list;
	}

	public BQMemberLevel findById(long fUserLevelId) {
		return bqMemberLevelDao.get(BQMemberLevel.class, fUserLevelId);
	}
	
	public BQMemberLevel readById(long fUserLevelId) {
		return bqMemberLevelDao.get(BQMemberLevel.class, fUserLevelId);
	}

	public BQMemberLevel findLevelId(int credits) {
		Session session=bqMemberLevelDao.getSession();
		String sql = "select * from bq_member_level  where state = 0 and level_credits>? order by level_credits ASC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, credits);
		query.addEntity(BQMemberLevel.class);
		return (BQMemberLevel) query.uniqueResult();
	}

	@Override
	public BQMemberLevel findByLevelId(long levelId) {
		String hql = "from BQMemberLevel t where t.level_id = :levelId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("levelId", levelId);
		return bqMemberLevelDao.get(hql, params);
	}

	@Override
	public void save(BQMemberLevel level) {
		bqMemberLevelDao.save(level);
	}

	@Override
	public void update(BQMemberLevel level) {
		bqMemberLevelDao.update(level);
	}
	
	@Override
	public void delete(BQMemberLevel level) {
		bqMemberLevelDao.delete(level);
	}

	@Override
	public PagerModel readLevels() {
		StringBuilder hql = new StringBuilder("from BQMemberLevel");
		return bqMemberLevelDao.searchHql(hql.toString());
	}

	@Override
	public BQMemberLevel readMaxLevel() {
		String hql = "from BQMemberLevel t where state = 0 order by t.level_id desc";
		return bqMemberLevelDao.get(hql);
	}
	
	@Override
	public BQMemberLevel getMaxLevel() {
		String hql = "from BQMemberLevel t where state = 0 order by t.level_id desc";
		return bqMemberLevelDao.get(hql);
	}
	
	@Override
	public BQMemberLevel getMaxLevelExclusionState() {
		String hql = "from BQMemberLevel order by level_id desc";
		return bqMemberLevelDao.get(hql);
	}

	@Override
	public BQMemberLevel readMaxLevelForLevelBuyAmount() {
		String hql = "from BQMemberLevel t where state = 0 and level_buy_amount > 0 order by t.level_id desc";
		return bqMemberLevelDao.get(hql);
	}

	@Override
	public BQMemberLevel findNextLevelById(long fUserLevelId) {
		Session session = bqMemberLevelDao.getSession();
		String sql = "select * from bq_member_level t where state = 0 and t.level_id > :levelId order by t.level_id limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("levelId", fUserLevelId);
		query.addEntity(BQMemberLevel.class);
		return (BQMemberLevel) query.uniqueResult();
	}

	@Override
	public BQMemberLevel findLevelByEatRate(BigDecimal rate) {
		String hql = "from BQMemberLevel t where t.eat_commission_rate = :rate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rate", rate);
		return bqMemberLevelDao.get(hql, params);
	}
	
	@Override
	public BQMemberLevel findLevelBuyAmountById(long levelId) {
		String hql = "from BQMemberLevel t where state = 0 and t.level_id = :levelId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("levelId", levelId);
		return bqMemberLevelDao.get(hql, params);
	}
}
