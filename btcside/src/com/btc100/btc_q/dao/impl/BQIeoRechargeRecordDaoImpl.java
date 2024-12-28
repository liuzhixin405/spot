package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQIeoRechargeRecordDao;
import com.btc100.btc_q.util.StringUtil;

@Repository("bqIeoRechargeRecordDao")
public class BQIeoRechargeRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQIeoRechargeRecordDao<T> {
	
	@Override
	public int updateRecord(long id, BigDecimal locked, BigDecimal unlocked) {
		String hql = "UPDATE BQIeoRechargeRecord b";
		
		String whereStr = " where b.id = :id ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		
		String setStr = "";
		if (locked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.amount = b.amount + :locked";
			whereStr += " and b.amount + :locked >= 0";
			map.put("locked", locked);
		}
		
		if (unlocked.compareTo(BigDecimal.ZERO) != 0) {
			if (StringUtil.isEmpty(setStr)) {
				setStr += " set ";
			} else {
				setStr += ",";
			}
			
			setStr += "b.unlock_amount = b.unlock_amount + :unlocked";
			whereStr += " and b.unlock_amount + :unlocked >= 0";
			map.put("unlocked", unlocked);
		}
		
		if (StringUtil.isEmpty(setStr)) {
			return 0;
		}
		
		hql += (setStr + whereStr);
		return this.updateCondition(hql, map);
	}
}
