package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.dao.BQInviteAwardCountDao;
import com.btc100.btc_q.dao.BQMemberApplyDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQInviteAwardCount;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberApply;
import com.btc100.btc_q.service.BQMemberApplyService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberApplyService")
public class BQMemberApplyServiceImpl implements BQMemberApplyService {

	@Autowired
	private BQMemberApplyDao<BQMemberApply> bqMemberApplyDao;
	@Autowired
	private BQInviteAwardCountDao<BQInviteAwardCount> bqInviteAwardCountDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;

	public void saveApply(BQMemberApply apply) {
		bqMemberApplyDao.save(apply);
	}

	public BQMemberApply searchApplyByUserid(long userId) {
		return bqMemberApplyDao.get(BQMemberApply.class, userId);
	}

	public void deleteApplyByUserid(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", userId);
		bqMemberApplyDao.deleteCondition("Delete from BQMemberApply where f_user_id = :userid", params);
	}

	public boolean findApplyByNo(String userNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userNo", userNo);
		List<BQMemberApply> lstApplys = bqMemberApplyDao.find("From BQMemberApply where idcard = :userNo", params);
		return lstApplys.size() > 0;
	}

	public List<BQMemberApply> searchApplyByParas(int type, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("state", state);
		List<BQMemberApply> lstApplys = bqMemberApplyDao.find("From BQMemberApply where user_type = :type and state = :state order by dateline DESC ", params);
		return lstApplys;
	}

	public void updateApplyByParas(long id, int state, String reason) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", id);
		params.put("state", state);
		params.put("reason", reason);
		params.put("dateTime", bqMemberApplyDao.getUnix_TIMESTAMP());
		bqMemberApplyDao.updateCondition("UPDATE BQMemberApply SET state = :state, reason = :reason, confirm_dateline = :dateTime WHERE f_user_id = :userid", params);
		if (state == 1) {
			BQInviteAwardCount o = new BQInviteAwardCount();
			o.setF_user_id(id);
			o.setDay_count(BigDecimal.ZERO);
			o.setWeek_count(BigDecimal.ZERO);
			o.setMonth_count(BigDecimal.ZERO);
			o.setTotal_count(BigDecimal.ZERO);
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			long time = bqMemberApplyDao.getUnix_TIMESTAMP();
			o.setDay(Convert.toLong(df.format(new Date(time * 1000l)), 0));
			
			if (bqInviteAwardCountDao.get(BQInviteAwardCount.class, id) != null)
				bqInviteAwardCountDao.update(o);
			else
				bqInviteAwardCountDao.save(o);
		}
	}

	public PagerModel searchApply(int userType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", userType);
		String sql = "from BQMemberApply WHERE user_type = :type ORDER BY ABS(state+0.1), dateline DESC";
		return bqMemberApplyDao.searchHql(sql, params);
	}

	public PagerModel findApplyByParams(int state, int userType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", userType);
		StringBuilder hql = new StringBuilder("from BQMemberApply WHERE user_type = :type");
		if (state == 0 || state == 1 || state == -1) {
			params.put("state", state);
			hql.append(" and state = :state");
		}
		hql.append(" ORDER BY ABS(state+0.1), dateline DESC ");
		return bqMemberApplyDao.searchHql(hql.toString(), params);
	}

	public void appendbroker(long userId, String userNo, String userTel, String userAddr) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", userId);
		List<BQMemberApply> list = bqMemberApplyDao.find("From BQMemberApply where f_user_id = :userid", params);

		if (list != null && list.size() > 0) {
			BQMemberApply apply = list.get(0);
			apply.setF_user_id(userId);
			apply.setState(Contents.BQMEMBER_APPLY_SUCC);
			apply.setUser_type(Contents.BQMEMBER_APPLY_ADVANCEDBROKER);
			apply.setReason("");
			bqMemberApplyDao.update(apply);
			
		} else {
			long unixTime = bqMemberApplyDao.getUnix_TIMESTAMP();
			BQMemberApply apply = new BQMemberApply();
			apply.setF_user_id(userId);
			apply.setIdcard(userNo);
			apply.setMobile(userTel);
			apply.setAddress(userAddr);
			apply.setDateline(unixTime);
			apply.setState(Contents.BQMEMBER_APPLY_SUCC);
			apply.setUser_type(Contents.BQMEMBER_APPLY_ADVANCEDBROKER);
			apply.setReason("");
			bqMemberApplyDao.save(apply);

			BQInviteAwardCount o = new BQInviteAwardCount();
			o.setF_user_id(userId);
			o.setDay_count(BigDecimal.ZERO);
			o.setWeek_count(BigDecimal.ZERO);
			o.setMonth_count(BigDecimal.ZERO);
			o.setTotal_count(BigDecimal.ZERO);
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			o.setDay(Convert.toLong(df.format(new Date(unixTime * 1000l)), 0));
			bqInviteAwardCountDao.save(o);
		}
		
		BQMember bqMember = bqMemberDao.get(BQMember.class, userId);
		long credits = bqMember.getF_user_credits();
		long cha= 50000000 - credits;
		if(cha>0){
			BQCommonCreditLog o = new BQCommonCreditLog();
			o.setCredit_type_id(Contents.CREDIT_TYPE_AWARD);
			o.setCredits(cha);
			o.setDateline(System.currentTimeMillis()/1000l);
			o.setF_user_id(userId);
			o.setTitle("高级经纪人奖励积分");
			bqCommonCreditLogDao.save(o);
			
			bqMember.setF_user_credits(50000000L);
			bqMember.setF_user_level_id(11);
		}
	}

}
