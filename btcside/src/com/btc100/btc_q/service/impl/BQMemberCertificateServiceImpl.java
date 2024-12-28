package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberCertificateDao;
import com.btc100.btc_q.entity.BQMemberCertificate;
import com.btc100.btc_q.service.BQMemberCertificateService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberCertificateService")
public class BQMemberCertificateServiceImpl implements BQMemberCertificateService {

	@Autowired
	private BQMemberCertificateDao<BQMemberCertificate> bqMemberCertificateDao;

	@Override
	public BQMemberCertificate readById(long id) {
		return bqMemberCertificateDao.get(BQMemberCertificate.class, id);
	}

	@Override
	public void save(BQMemberCertificate entity) {
		bqMemberCertificateDao.save(entity);
	}

	@Override
	public void update(BQMemberCertificate entity) {
		bqMemberCertificateDao.update(entity);
	}

	@Override
	public void delete(BQMemberCertificate entity) {
		bqMemberCertificateDao.update(entity);
	}

	@Override
	public List<BQMemberCertificate> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime,
			int limit) {
		String hql = "from BQMemberCertificate where 1 = 1";
		Map<String, Object> params = new HashMap<String, Object>();
		if (uid > 0) {
			hql += " and user_id = :uid";
			params.put("uid", uid);
		}
		
		if (startTime > 0) {
			hql += " and issue_time >= :startTime";
			params.put("startTime", startTime);
		}
		
		if (endTime > 0) {
			hql += " and issue_time < :endTime";
			params.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			params.put("from", from);
			hql += " and id <= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			params.put("from", from);
			hql += " and id >= :from";
		}
		
		hql += " order by id asc";
		return bqMemberCertificateDao.find(hql, params, 1, limit);
	}
	
	@Override
	public PagerModel readPagerModel() {
		return bqMemberCertificateDao.searchHql("from BQMemberCertificate where 1=1");
	}

	@Override
	public PagerModel readPageByUserId(long userId) {
		String hql = "from BQMemberCertificate t where t.user_id = :userId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		return bqMemberCertificateDao.searchHql(hql, params);
	}

}
