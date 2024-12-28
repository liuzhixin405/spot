package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQMemberStatusDao;
import com.btc100.btc_q.entity.BQMemberStatus;
import com.btc100.btc_q.service.BQMemberStatusService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberStatusService")
public class BQMemberStatusServiceImpl implements BQMemberStatusService {

	@Autowired
	private BQMemberStatusDao<BQMemberStatus> bqMemberStatusDao;

	public void save(BQMemberStatus bqMemberStatus) {
		bqMemberStatusDao.save(bqMemberStatus);
	}

	public PagerModel readUserIP(long userId) {
		if (userId <= 0) {
			return bqMemberStatusDao.searchHql("From BQMemberStatus ORDER BY last_time DESC");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userId);
		return bqMemberStatusDao.searchHql("From BQMemberStatus WHERE f_user_id = :userID ORDER BY last_time DESC", params);
	}

	public PagerModel readUserIP(String registIp, String lastIp) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("registIp", "%" + registIp + "%");
		params.put("lastIp", "%" + lastIp + "%");
		return bqMemberStatusDao.searchHql("From BQMemberStatus WHERE reg_ip like :registIp AND last_ip like :lastIp ORDER BY last_time DESC", params);
	}

	public void updateStatus(long userId, String IP) {
		BQMemberStatus userStatus = bqMemberStatusDao.get(BQMemberStatus.class, userId);
		long tim = bqMemberStatusDao.getUnix_TIMESTAMP();
		if (userStatus == null) {
			userStatus = new BQMemberStatus();
			userStatus.setF_user_id(userId);// 用户ID',
			userStatus.setReg_ip(IP);
			userStatus.setLast_time(Long.valueOf(tim).intValue());// 最后登录时间,Unix时间格式
			userStatus.setLast_ip(IP);// 基本后登录ip'
			bqMemberStatusDao.save(userStatus);
		} else {
			userStatus.setLast_ip(IP);
			userStatus.setLast_time(Long.valueOf(tim).intValue());// 最后登录时间,Unix时间格式
			bqMemberStatusDao.update(userStatus);
		}
	}

	public void updateComeIn(long userId, BigDecimal comin) {
		String sql = "UPDATE BQMemberStatus SET last_comein=:comin+last_comein WHERE f_user_id=:userid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("comin", comin);
		params.put("userid", userId);
		bqMemberStatusDao.updateCondition(sql, params);
	}

	public int addComeIn(String code) {
		String sql = "UPDATE bq_member_status a,(SELECT f_user_id,sum(trade_income-trade_fee) comein from "+code+"_trade_log GROUP BY f_user_id) b SET a.last_comein=a.last_comein+b.comein WHERE a.f_user_id=b.f_user_id";
		return bqMemberStatusDao.createSQLQuery(sql).executeUpdate();
	}

}
