package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQAreaCityDao;
import com.btc100.btc_q.dao.BQAreaCountryDao;
import com.btc100.btc_q.dao.BQAreaProvinceDao;
import com.btc100.btc_q.dao.BQCommonCreditLogDao;
import com.btc100.btc_q.dao.BQCurrencyTypeDao;
import com.btc100.btc_q.dao.BQMemberAreaDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberInviteAmountDao;
import com.btc100.btc_q.dao.BQMemberInviteLogDao;
import com.btc100.btc_q.dao.BQMemberLevelDao;
import com.btc100.btc_q.dao.BQMemberSafeSettingsDao;
import com.btc100.btc_q.dao.BQMemberStatusDao;
import com.btc100.btc_q.dao.BQSubMemberDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAreaCity;
import com.btc100.btc_q.entity.BQAreaCountry;
import com.btc100.btc_q.entity.BQAreaProvince;
import com.btc100.btc_q.entity.BQBcexMember;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberArea;
import com.btc100.btc_q.entity.BQMemberInviteAmount;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQMemberStatus;
import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;

@Service("registerService")
public class RegisterSerivceImpl implements RegisterSerivce {

	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQMemberInviteLogDao<BQMemberInviteLog> bqMemberInviteLogDao;
	@Autowired
	private BQCommonCreditLogDao<BQCommonCreditLog> bqCommonCreditLogDao;
	@Autowired
	BQMemberLevelDao<BQMemberLevel> bqMemberLevelDao;
	@Autowired
	private BQMemberStatusDao<BQMemberStatus> bqMemberStatusDao;
	@Autowired
	private BQMemberSafeSettingsDao<BQMemberSafeSettings> bqMemberSafeSettingsDao;
	@Autowired
	private BQAreaCityDao<BQAreaCity> bqAreaCityDao;
	@Autowired
	private BQAreaCountryDao<BQAreaCountry> bqAreaCountryDao;
	@Autowired
	private BQAreaProvinceDao<BQAreaProvince> bqAreaProvinceDao;
	@Autowired
	private BQMemberAreaDao<BQMemberArea> bqMemberAreaDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
	
	
	public int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city, String countryName) {
		return this.RegisterSuccess(user, invid, uip, country, province, city, countryName, "", false);
	}

	@Override
	public int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city, String countryName, boolean isPhone) {
		return this.RegisterSuccess(user, invid, uip, country, province, city, countryName, "", isPhone);
	}

	public int RegisterSuccess(BQMember user, String invid, String uip, String country, String province, String city, String countryName, String zone, boolean isPhone) {

		bqMemberDao.save(user);
		long uid = user.getF_user_id();
		long now = DateUtil.DateToUnix(new Date());

		if (!invid.equals("")) {
			// bq_member_invite_log经纪人用户邀请关系表
			long inviteID = ConvertUtil.toLong(invid, 0l);
			addSuccess(inviteID, user, user.getF_user_time());
		}

		// bq_common_credit_log记录每次的积分信息的
		BQCommonCreditLog bqCc = new BQCommonCreditLog();
		bqCc.setF_user_id(uid);
		bqCc.setCredit_type_id(Contents.CREDIT_TYPE_REGISTER);
		bqCc.setCredits(DATA_CreditNumber.CREDIT_ONE_REGISTER);
		bqCc.setTitle("注册");
		bqCc.setDateline(now);
		bqCommonCreditLogDao.save(bqCc);
		
		if (isPhone) {
			BQCommonCreditLog bqCc1 = new BQCommonCreditLog();
			bqCc1.setF_user_id(uid);
			bqCc1.setCredit_type_id(Contents.CREDIT_TYPE_BINDPHONE);
			bqCc1.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
			bqCc1.setTitle("绑定手机");
			bqCc1.setDateline(now);
			bqCommonCreditLogDao.save(bqCc1);
		} else {
			BQCommonCreditLog bqCc1 = new BQCommonCreditLog();
			bqCc1.setF_user_id(uid);
			bqCc1.setCredit_type_id(Contents.CREDIT_TYPE_SUREEMAIL);
			bqCc1.setCredits(DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
			bqCc1.setTitle("验证邮箱");
			bqCc1.setDateline(now);
			bqCommonCreditLogDao.save(bqCc1);
		}

		// bqMemberStatus
		BQMemberStatus bqSta = new BQMemberStatus();
		bqSta.setF_user_id(uid);// 用户ID',
		bqSta.setReg_ip(uip);
		bqSta.setLast_time((int) now);// 最后登录时间,Unix时间格式
		bqSta.setLast_ip(uip);// 基本后登录ip'
		bqSta.setLast_comein(BigDecimal.ZERO);
		bqMemberStatusDao.save(bqSta);

		// bqMemberSafeSettings
		BQMemberSafeSettings bqSs = new BQMemberSafeSettings();
		bqSs.setF_user_id(uid);
		if (isPhone) {
			bqSs.setF_user_safetype(1);
			bqSs.setF_user_tel(user.getF_user_email());
			bqSs.setF_user_zone(zone == null ? "" : zone);
		} else {
			bqSs.setF_user_safetype(0);
			bqSs.setF_user_tel("");
		}
		bqSs.setF_user_safegoogle("");
		bqSs.setF_user_safequertion(0);
		bqSs.setF_user_safeanswer("");
		bqMemberSafeSettingsDao.save(bqSs);

		BQMemberArea bqA = new BQMemberArea();
		bqA.setF_user_id(uid);
		bqA.setProvince_code(province);
		bqA.setCity_code(city);
		
		if (StringUtil.isEmpty(country)) {
			country = "CN";
		}
		bqA.setCountry_code(country);

		if (country != null && !country.isEmpty()) {
			String hql = "FROM BQAreaCountry b WHERE code=:code";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", country.toUpperCase());
			List<BQAreaCountry> list = bqAreaCountryDao.find(hql, map);
			if (list != null && list.size() > 0) {
				BQAreaCountry bq = list.get(0);
				bqA.setCountry_name(bq.getZh_name());
			} else {
				bqA.setCountry_name(countryName);
			}
		}

		if (country.equalsIgnoreCase("cn")) {
			if (province != null && !province.isEmpty()) {
				String hql = "FROM BQAreaProvince b WHERE b.code=:code";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("code", province);
				List<BQAreaProvince> list = bqAreaProvinceDao.find(hql, map);
				if (list != null && list.size() > 0) {
					BQAreaProvince bq = list.get(0);
					bqA.setProvince_name(bq.getName());
				}else{
					bqA.setProvince_name("");
				}
			} else {
				bqA.setProvince_name("");
			}

			if (city != null && !city.isEmpty()) {
				String hql = "FROM BQAreaCity b WHERE b.code=:code";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("code", city);
				List<BQAreaCity> list = bqAreaCityDao.find(hql, map);
				if (list != null && list.size() > 0) {
					BQAreaCity bq = list.get(0);
					bqA.setCity_name(bq.getName());
				}else{
					bqA.setCity_name("");
				}
			} else {
				bqA.setCity_name("");
			}
		}else{
			bqA.setProvince_name("");
			bqA.setCity_name("");
		}

		bqMemberAreaDao.save(bqA);

		List<BQCurrencyType> list = bqCurrencyTypeDao.findAll();
		for (BQCurrencyType type : list) {
			bqAccountDao.insertAccount(user, type.getType_id());
		}

		return 1;
	}

	@Autowired
	BQMemberInviteAmountDao<BQMemberInviteAmount> bqMemberInviteAmountDao;
	
	public void addSuccess(long inviteId, BQMember invitee, int inviteTime) {
		Date date = new Date(bqMemberInviteAmountDao.getUnix_TIMESTAMP() * 1000l);
		String today = DateUtil.dateToStr(date, "yyyyMMdd");
		
		BQMemberInviteLog inviteLog = readAgent(inviteId);
		if (inviteLog != null) {
			invitee.setF_user_oms(1);
			bqMemberDao.update(invitee);
		}
		
		BQMemberInviteLog bqil = new BQMemberInviteLog();
		bqil.setF_user_id(invitee.getF_user_id());
		bqil.setInvite_uid(inviteId);
		bqil.setInvite_time(inviteTime);
		bqMemberInviteLogDao.save(bqil);
		
		List<BQMemberInviteAmount> list = bqMemberInviteAmountDao.findByUidAndDay(inviteId, today);
		if(list==null||list.size()<=0){
			BQMemberInviteAmount pAmount=new BQMemberInviteAmount();
			pAmount.setF_date(today);
			pAmount.setF_day_click(1);
			pAmount.setF_day_success(1);
			pAmount.setF_user_id(inviteId);
			
			bqMemberInviteAmountDao.save(pAmount);
		}else{
			bqMemberInviteAmountDao.addSuccess(inviteId, today);
		}
	}
	
	public void click(long uid,String ip){
		Date date = new Date(bqMemberInviteAmountDao.getUnix_TIMESTAMP() * 1000l);
		String today = DateUtil.dateToStr(date, "yyyyMMdd");
		List<BQMemberInviteAmount> list = bqMemberInviteAmountDao.findByUidAndDay(uid, today);
		if(list==null||list.size()<=0){
			BQMemberInviteAmount pAmount=new BQMemberInviteAmount();
			pAmount.setF_date(today);
			pAmount.setF_day_click(1);
			pAmount.setF_day_success(0);
			pAmount.setF_user_id(uid);
			
			bqMemberInviteAmountDao.save(pAmount);
		}else{
			bqMemberInviteAmountDao.addClick(uid, today);
		}
	}

	@Override
	public int bcexRegister(BQMember user, String uip, BQBcexMember bcexMember) {

		bqMemberDao.save(user);
		long uid = user.getF_user_id();
		long now = DateUtil.DateToUnix(new Date());
		
		final long inviteID = 14967l;
		addSuccess(inviteID, user, user.getF_user_time());
		
		// bq_common_credit_log记录每次的积分信息的
		BQCommonCreditLog bqCc = new BQCommonCreditLog();
		bqCc.setF_user_id(uid);
		bqCc.setCredit_type_id(Contents.CREDIT_TYPE_REGISTER);
		bqCc.setCredits(user.getF_user_credits());
		bqCc.setTitle("bcex用户注册");
		bqCc.setDateline(now);
		bqCommonCreditLogDao.save(bqCc);
		
//		BQCommonCreditLog bqCc1 = new BQCommonCreditLog();
//		bqCc1.setF_user_id(uid);
//		bqCc1.setCredit_type_id(Contents.CREDIT_TYPE_SUREEMAIL);
//		bqCc1.setCredits(DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
//		bqCc1.setTitle("验证邮箱");
//		bqCc1.setDateline(now);
//		bqCommonCreditLogDao.save(bqCc1);

		// bqMemberStatus
		BQMemberStatus bqSta = new BQMemberStatus();
		bqSta.setF_user_id(uid);// 用户ID',
		bqSta.setReg_ip(uip);
		bqSta.setLast_time((int) now);// 最后登录时间,Unix时间格式
		bqSta.setLast_ip(uip);// 基本后登录ip'
		bqSta.setLast_comein(BigDecimal.ZERO);
		bqMemberStatusDao.save(bqSta);

		String tel = "";
		int safeType = 0;
		if (!StringUtil.isEmpty(bcexMember.getF_user_tel()) && bcexMember.getF_user_tel().length() > 8) {
			tel = bcexMember.getF_user_tel();
			safeType = 1;
//			BQCommonCreditLog bqCc2 = new BQCommonCreditLog();
//			bqCc2.setF_user_id(uid);
//			bqCc2.setCredit_type_id(Contents.CREDIT_TYPE_BINDPHONE);
//			bqCc2.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
//			bqCc2.setTitle("绑定手机");
//			bqCc2.setDateline(now);
//			bqCommonCreditLogDao.save(bqCc2);
		}
		// bqMemberSafeSettings
		BQMemberSafeSettings bqSs = new BQMemberSafeSettings();
		bqSs.setF_user_id(uid);
		bqSs.setF_user_safetype(safeType);
		bqSs.setF_user_tel(tel);
		bqSs.setF_user_safegoogle("");
		bqSs.setF_user_safequertion(0);
		bqSs.setF_user_safeanswer("");
		bqMemberSafeSettingsDao.save(bqSs);

		BQMemberArea bqA = new BQMemberArea();
		bqA.setF_user_id(uid);
		bqA.setCountry_code("CN");
		bqA.setProvince_code("");
		bqA.setCity_code("");
		bqA.setCountry_name("中国");
		bqA.setProvince_name("");
		bqA.setCity_name("");

		bqMemberAreaDao.save(bqA);

		List<BQCurrencyType> list = bqCurrencyTypeDao.findAll();
		for (BQCurrencyType type : list) {
			bqAccountDao.insertAccount(user, type.getType_id());
		}

		return 1;
	}

	@Override
	public int registerSubUser(BQMember motherUser, BQMember subUser, String uip, BQMemberArea area) {
		bqMemberDao.save(subUser);
		long uid = subUser.getF_user_id();
		long now = DateUtil.DateToUnix(new Date());

		// bq_member_invite_log经纪人用户邀请关系表
		addSuccess(motherUser.getF_user_id(), subUser, subUser.getF_user_time());
		// bq_sub_member 母子账户关系表
		addSubMemberSuccess(motherUser.getF_user_id(), uid, subUser.getF_user_time());

		// bq_common_credit_log记录每次的积分信息的
		BQCommonCreditLog bqCc = new BQCommonCreditLog();
		bqCc.setF_user_id(uid);
		bqCc.setCredit_type_id(Contents.CREDIT_TYPE_REGISTER);
		bqCc.setCredits(DATA_CreditNumber.CREDIT_ONE_REGISTER);
		bqCc.setTitle("子账号注册");
		bqCc.setDateline(now);
		bqCommonCreditLogDao.save(bqCc);
		
		BQCommonCreditLog bqCc1 = new BQCommonCreditLog();
		bqCc1.setF_user_id(uid);
		bqCc1.setCredit_type_id(Contents.CREDIT_TYPE_SUREEMAIL);
		bqCc1.setCredits(DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
		bqCc1.setTitle("验证邮箱");
		bqCc1.setDateline(now);
		bqCommonCreditLogDao.save(bqCc1);

		// bqMemberStatus
		BQMemberStatus bqSta = new BQMemberStatus();
		bqSta.setF_user_id(uid);// 用户ID',
		bqSta.setReg_ip(uip);
		bqSta.setLast_time((int) now);// 最后登录时间,Unix时间格式
		bqSta.setLast_ip(uip);// 基本后登录ip'
		bqSta.setLast_comein(BigDecimal.ZERO);
		bqMemberStatusDao.save(bqSta);

		// bqMemberSafeSettings
		BQMemberSafeSettings bqSs = new BQMemberSafeSettings();
		bqSs.setF_user_id(uid);
		bqSs.setF_user_safetype(0);
		bqSs.setF_user_tel("");
		bqSs.setF_user_safegoogle("");
		bqSs.setF_user_safequertion(0);
		bqSs.setF_user_safeanswer("");
		bqMemberSafeSettingsDao.save(bqSs);
		
		area.setF_user_id(uid);
		bqMemberAreaDao.save(area);

		List<BQCurrencyType> list = bqCurrencyTypeDao.findAll();
		for (BQCurrencyType type : list) {
			bqAccountDao.insertAccount(subUser, type.getType_id());
		}

		return 1;
	}
	
	@Autowired
	private BQSubMemberDao<BQSubMember> bqSubMemberDao;
	
	private void addSubMemberSuccess(long motherId, long subId, int dateline){
		BQSubMember subMember = new BQSubMember();
		subMember.setSub_user_id(subId);
		subMember.setMother_user_id(motherId);
		subMember.setDateline(dateline);
		bqSubMemberDao.save(subMember);
	}
	
	private BQMemberInviteLog readAgent(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		BQMemberInviteLog log = bqMemberInviteLogDao.get("From BQMemberInviteLog where f_user_id = :userId", params);
		if (log != null) {
			BQMember inviter = bqMemberDao.get(BQMember.class, log.getInvite_uid());
			if (inviter != null) {
				if (inviter.getF_user_identity() == 1) {
					return log;
				} else {
					BQMemberInviteLog inviteLog = readAgent(inviter.getF_user_id());
					if (inviteLog != null) {
						return inviteLog;
					}
				}
			}
		}
		return null;
	}
	
}
