package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberRechargeDao;

@Repository("bqMemberRechargeDao")
public class BQMemberRechargeDaoImpl<BQMemberRecharge> extends BaseDaoImpl<BQMemberRecharge> implements BQMemberRechargeDao<BQMemberRecharge> {

	public BigDecimal findSUMByUserID(Long uid, int itype) {
		if (uid <= 0) {
			return BigDecimal.ZERO;
		}

		String swhere = "";
		Map<String, Object> map = new HashMap<String, Object>();

		if (itype > 0) {
			swhere = " AND operation=:type";
			map.put("type", itype);
		}

		String sqlsum = "SELECT sum(amount) FROM BQMemberRecharge b WHERE b.f_user_id =:id" + swhere;
		map.put("id", uid);

		return super.sum(sqlsum, map);
	}

}
