package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.VIEWUsermanagerDao;
import com.btc100.btc_q.entity.VIEWUsermanager;
import com.btc100.btc_q.service.VIEWUsermanagerService;
import com.btc100.btc_q.vo.PagerModel;

@Service("viewUsermanagerService")
public class VIEWUsermanagerServiceImpl implements VIEWUsermanagerService {

	@Autowired
	private VIEWUsermanagerDao<VIEWUsermanager> viewUsermanagerDao;

	public PagerModel findModel() {
		String hql = "FROM VIEWUsermanager v order by v.f_user_id desc";
		
		PagerModel pm = viewUsermanagerDao.searchHql(hql);
		return pm;
	}

	@Override
	public PagerModel findModel(long userid, String usertel, String useremail, String countryName, long inviteId, int userLevelId, int type) {
		StringBuilder sb = new StringBuilder("From VIEWUsermanager WHERE 1=1");
		Map<String, Object> map = new HashMap<String, Object>();
		if (userid > 0) {
			sb.append(" AND f_user_id = :userid");
			map.put("userid", userid);
		}
		if (usertel != null && !usertel.isEmpty()) {
			sb.append(" AND f_user_tel = :tel");
			map.put("tel", usertel);
		}
		if (useremail != null && !useremail.isEmpty()) {
			sb.append(" AND f_user_email like :email");
			map.put("email", "%" + useremail + "%");
		}
		if (countryName != null && !countryName.isEmpty()) {
			sb.append(" AND country_name like :countryName");
			map.put("countryName", "%" + countryName + "%");
		}
		if (inviteId > 0) {
			sb.append(" AND invite_uid = :inviteId");
			map.put("inviteId", inviteId);
		}
		if (userLevelId >= 0) {
			sb.append(" AND level_name = :userLevelId");
			map.put("userLevelId", "VIP" + userLevelId);
		}
		
		String orderHql = "";
		
		if (type == 1) {
			orderHql = " order by btc DESC, f_user_id DESC";
		} else if (type == 2) {
			orderHql = " order by btc_gain DESC, f_user_id DESC";
		} else if (type == 3) {
			orderHql = " order by level_name DESC, f_user_id DESC";
		} else {
			orderHql = " order by f_user_id DESC";
		}
		
		String hql = sb.toString() + orderHql;

		return viewUsermanagerDao.searchHql(hql, map);
	}
	
	public VIEWUsermanager findUser(long userid) {
		return viewUsermanagerDao.get("from VIEWUsermanager where f_user_id = " + userid);
	}

}
