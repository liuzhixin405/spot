package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberDepositOnoffDao;
import com.btc100.btc_q.entity.BQMemberDepositOnoff;
import com.btc100.btc_q.service.BQMemberDepositOnoffService;

@Service("bqMemberDepositOnoffService")
public class BQMemberDepositOnoffServiceImpl implements BQMemberDepositOnoffService {

	@Autowired
	private BQMemberDepositOnoffDao<BQMemberDepositOnoff> bqMemberDepositOnoffDao;

	public String findOnDeposit() {
		String hql = "FROM BQMemberDepositOnoff b WHERE b.value=1";
		List<BQMemberDepositOnoff> list = bqMemberDepositOnoffDao.find(hql);

		StringBuilder sbBuilder = new StringBuilder();
		String sresult = "";

		for (BQMemberDepositOnoff bq : list) {
			sbBuilder.append(bq.getF_user_id());
			sbBuilder.append(",");
		}

		if (sbBuilder.length() > 0 && sbBuilder.toString().endsWith(",")) {
			sresult = sbBuilder.substring(0, sbBuilder.length() - 1);
		}

		return sresult;
	}

	public BQMemberDepositOnoff readByUserId(long fUserId) {
		String hql = "FROM BQMemberDepositOnoff b WHERE b.f_user_id=:fUserId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fUserId", fUserId);
		List<BQMemberDepositOnoff> list = bqMemberDepositOnoffDao.find(hql,params);
		if(list.size()>0){
			return list.get(0);
		}else{
			BQMemberDepositOnoff onoff = new BQMemberDepositOnoff();
			onoff.setF_user_id(fUserId);
			onoff.setValue(0);
			bqMemberDepositOnoffDao.save(onoff);
			return onoff;
		}
//		return bqMemberDepositOnoffDao.get(BQMemberDepositOnoff.class, fUserId);
	}

	public void update(BQMemberDepositOnoff depositOnOff) {
		bqMemberDepositOnoffDao.update(depositOnOff);
		
	}
}
