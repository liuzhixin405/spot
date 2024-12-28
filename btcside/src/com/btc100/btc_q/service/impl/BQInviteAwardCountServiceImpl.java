package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQInviteAwardCountDao;
import com.btc100.btc_q.entity.BQInviteAwardCount;
import com.btc100.btc_q.service.BQInviteAwardCountSerivice;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;

@Service("bqInviteAwardCountSerivice")
public class BQInviteAwardCountServiceImpl implements BQInviteAwardCountSerivice {

	@Autowired
	private BQInviteAwardCountDao<BQInviteAwardCount> bqInviteAwardCountDao;

	public void saveAwardCount(long userId, BigDecimal fee) throws ParseException {
		BQInviteAwardCount awardCount = bqInviteAwardCountDao.get(BQInviteAwardCount.class, userId);
		if (awardCount == null) {
			BQInviteAwardCount o = new BQInviteAwardCount();
			o.setDay(bqInviteAwardCountDao.getUnix_TIMESTAMP());
			o.setDay_count(fee);
			o.setF_user_id(userId);
			o.setMonth_count(BigDecimal.ZERO);
			o.setWeek_count(BigDecimal.ZERO);
			o.setTotal_count(fee);
			bqInviteAwardCountDao.save(o);
		} else {
			Date date = new Date(bqInviteAwardCountDao.getUnix_TIMESTAMP() * 1000l);
			String today = DateUtil.dateToStr(date, "yyyyMMdd");
			awardCount.setDay(ConvertUtil.toLong(today, 0l));
			awardCount.setDay_count(fee);
			awardCount.setTotal_count(awardCount.getTotal_count().add(fee));
			awardCount.setF_user_id(userId);
			bqInviteAwardCountDao.update(awardCount);
		}
	}

}
