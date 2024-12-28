package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQIeoAccountDao;
import com.btc100.btc_q.util.StringUtil;

@Repository("bqIeoAccountDao")
public class BQIeoAccountDaoImpl<T> extends BaseDaoImpl<T> implements BQIeoAccountDao<T> {

	@Override
	public int updateAccount(long uid, int currencyId, BigDecimal initial, BigDecimal locked, BigDecimal unlocked) {
		String hql = "UPDATE BQIeoAccount b";
		
		String whereStr = " where b.f_user_id = :uid and currency_type_id = :currencyId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("currencyId", currencyId);
		
		String setStr = "";
		if (initial.compareTo(BigDecimal.ZERO) != 0) {
			setStr += " set b.initial = b.initial + :initial";
			whereStr += " and b.initial + :initial >= 0";
			map.put("initial", initial);
		}
		
		if (locked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.locked = b.locked + :locked";
			whereStr += " and b.locked + :locked >= 0";
			map.put("locked", locked);
		}
		
		if (unlocked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.unlocked = b.unlocked + :unlocked";
			whereStr += " and b.unlocked + :unlocked >= 0";
			map.put("unlocked", unlocked);
		}
		
		if (StringUtil.isEmpty(setStr)) {
			return 0;
		}
		
		hql += (setStr + whereStr);
		return this.updateCondition(hql, map);
		
	}

}
