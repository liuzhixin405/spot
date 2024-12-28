package com.btc100.btc_q.service.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.dao.BQMemberSafeSettingsDao;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberService")
public class BQMemberServiceImpl implements BQMemberService {

	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	BQMemberSafeSettingsDao<BQMemberSafeSettings> bqMemberSafeSettingsDao;
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;
	@Autowired
	private BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;

	/**
	 * 判断用户名是否被占用
	 */

	public BQMember readCheckUserEmail(String email) {
		final String hql = "from BQMember u where u.f_user_email = :email";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", email);
		BQMember user = (BQMember) bqMemberDao.get(hql, paramMap);
		return user;
	}
	
	public BQMember readByRealEmail(String email) {
		final String hql = "from BQMember u where u.f_real_email = :email";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", email);
		BQMember user = (BQMember) bqMemberDao.get(hql, paramMap);
		return user;
	}

	public BQMember readSubUser(String username) {
		final String hql = "from BQMember u where u.f_user_email = :name and is_sub_user = 1 and sub_user_delete = 0";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", username);
		BQMember user = (BQMember) bqMemberDao.get(hql, paramMap);
		return user;
	}

	public void save(BQMember bqMember) {
		bqMemberDao.save(bqMember);
	}

	public void updatefreeze(long userid, int istate) {
		if (istate == 1)
			istate = -1;
		else if (istate == -1)
			istate = 1;
		else
			return;

		String hql = "UPDATE BQMember b SET b.f_user_state=:state WHERE b.f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", userid);
		map.put("state", istate);
		bqMemberDao.updateCondition(hql, map);// .update(infoUser);
	}

	public void updatepassword(long uid, String password) {
		String hql = "UPDATE BQMember b SET b.f_user_pwd=:password WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("password", password);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}

	@Override
	public void updateEmailByState(long uid, String email, boolean state) {
		String hql = "UPDATE BQMember b SET b.f_real_email=:email WHERE f_user_id=:uid";
		if (state){//true邮箱注册
			hql = "UPDATE BQMember b SET b.f_real_email=:email ,b.f_user_email=:email WHERE f_user_id=:uid";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}

	public void updateEmailStatus(long uid, int state) {
		String hql = "UPDATE BQMember b SET b.f_user_email_status=:state WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", state);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}
	
	public void updateFeeRateType(long fUserId, int type) {
		String hql = "UPDATE BQMember b SET b.f_fee_rate_type = :type WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		map.put("uid", fUserId);

		bqMemberDao.updateCondition(hql, map);
	}
	
	public void updateOms(long uid, int state) {
		String hql = "UPDATE BQMember b SET b.f_user_oms = :state WHERE f_user_id = :uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", state);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}

	public BQMember findUserById(long userId) {
		String hql = "from BQMember u where u.f_user_id = :userId and sub_user_delete = 0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		BQMember user = (BQMember) bqMemberDao.get(hql, params);
		return user;
	}
	
	public BQMember getUserAboveId(long userId) {
		String hql = "from BQMember u where u.f_user_id > :userId and sub_user_delete = 0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		BQMember user = (BQMember) bqMemberDao.get(hql, params);
		return user;
	}
	
	public BQMember readUserById(long userId) {
		String hql = "from BQMember u where u.f_user_id = :userId and sub_user_delete = 0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		BQMember user = (BQMember) bqMemberDao.get(hql, params);
		return user;
	}

	public void update(BQMember user) {
		bqMemberDao.update(user);
	}

	public BQMember findUserByEmail(String uemail) {
		return bqMemberDao.findByEmail(uemail);
	}
	
	public BQMember readUserByEmail(String uemail) {
		return bqMemberDao.findByEmail(uemail);
	}
	
	public BQMember readUserByNickName(String nickName) {
		String hql = "from BQMember u where u.f_user_nick_name = :nickName";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nickName", nickName);
		BQMember user = (BQMember) bqMemberDao.get(hql, params);
		return user;
	}

	public List<BQMember> findsUserByEmail(String uemail) {
		return bqMemberDao.findsByEmail(uemail);
	}

	public void updateUserCredits(long userId, long credit, String title, int type) {
		BQMember bqMember = bqMemberDao.get(BQMember.class, userId);
		if (bqMember != null) {
			// 增加日志
			BQCommonCreditLog bqcl = new BQCommonCreditLog();
			bqcl.setF_user_id(userId);
			bqcl.setCredit_type_id(type);
			bqcl.setCredits(credit);
			bqcl.setTitle(title);
			bqcl.setDateline(bqCommonCreditLogDao.getUnix_TIMESTAMP());
			bqCommonCreditLogDao.save(bqcl);
		}
	}

	@Override
	public long findCountByTime(long startTime, long endTime) {
		Session session = bqMemberDao.getSession();
		String sql = "select count(*) from bq_member u where u.f_user_time >= :startTime and u.f_user_time < :endTime";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		
		BigInteger total = (BigInteger)query.uniqueResult();
		return total.longValue();
	}

	@Override
	public long findCount() {
		Session session = bqMemberDao.getSession();
		String sql = "select count(*) from bq_member u where 1=1";
		SQLQuery query = session.createSQLQuery(sql.toString());
		BigInteger total = (BigInteger)query.uniqueResult();
		return total.longValue();
	}

	@Override
	public void updateHasOpen(long uid) {
		String hql = "UPDATE BQMember b SET b.has_open_position=:state WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", 1);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}
	
	@Override
	public void updateHeadImg(long uid, String path) {
		String hql = "UPDATE BQMember b SET b.f_user_head_img=:path WHERE f_user_id=:uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", path);
		map.put("uid", uid);

		bqMemberDao.updateCondition(hql, map);
	}

	@Override
	public PagerModel readByInviteManagement(int inviteManagement, long uid) {
		String hql = "from BQMember u where u.open_invite_management = :inviteManagement";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inviteManagement", inviteManagement);
		if (uid > 0) {
			hql += " and u.f_user_id = :uid";
			params.put("uid", uid);
		}
		return bqMemberDao.searchHql(hql, params);
	}
	
	@Override
	public PagerModel readByIdentity(int identity, long uid, String userEmail) {
		String hql = "from BQMember where 1 = 1";
		Map<String, Object> params = new HashMap<String, Object>();
		if (identity >= 0) {
			hql += " and f_user_identity = :identity";
			params.put("identity", identity);
		}
		if (uid > 0) {
			hql += " and f_user_id = :uid";
			params.put("uid", uid);
		}
		if (!StringUtil.isEmpty(userEmail)) {
			hql += " and f_user_email = :userEmail";
			params.put("userEmail", userEmail);
		}
		return bqMemberDao.searchHql(hql, params);
	}

	@Override
	public void checkVipLevel(BQMember user) {
		if (user != null) {
			long vipId = bqMemberLevelDao.getLevelByCredits(user.getF_user_credits(), true);
			if (vipId != user.getF_user_level_id()) {
				String hql = "UPDATE BQMember b SET b.f_user_level_id=:vipId WHERE f_user_id=:uid";
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("vipId", vipId);
				map.put("uid", user.getF_user_id());
				
				bqMemberLevelDao.updateCondition(hql, map);
			}
		}
		
	}
	
	@Override
	public int updatePhone(BQMemberSafeSettings bqms) {
		bqMemberSafeSettingsDao.update(bqms);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", bqms.getF_user_tel());
		params.put("uid", bqms.getF_user_id());
		return bqMemberDao.updateCondition("update BQMember b set b.f_user_email = :name where b.f_user_id = :uid", params);
	}

}
