package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQPriceIndexBtcLogDao;
import com.btc100.btc_q.entity.BQPriceIndexBtcLog;
import com.btc100.btc_q.service.BQPriceIndexBtcLogService;
import com.btc100.btc_q.vo.BpiVO;

@Service("bqPriceIndexBtcLogService")
public class BQPriceIndexBtcLogServiceImpl implements BQPriceIndexBtcLogService {

	@Autowired
	private BQPriceIndexBtcLogDao<BQPriceIndexBtcLog> bqPriceIndexBtcLogDao;

	public void saveBtcBpi(int typeId, BigDecimal price, long dateline) {
		BQPriceIndexBtcLog bpiLog = new BQPriceIndexBtcLog();
		bpiLog.setCurrency_type_id(typeId);
		bpiLog.setDateline(dateline);
		bpiLog.setPrice(price);
		bqPriceIndexBtcLogDao.save(bpiLog);
	}

	public BpiVO getBpiVO() {
		long now = bqPriceIndexBtcLogDao.getUnix_TIMESTAMP();
		long oneHourAgo = now - 3600;

		Session session = bqPriceIndexBtcLogDao.getSession();
		String sql = "select * from bq_price_index_btc_log where dateline >= :dateline order by dateline desc";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("dateline", oneHourAgo);
		query.addEntity(BQPriceIndexBtcLog.class);
		
		BpiVO resVO = null;
		List<BQPriceIndexBtcLog> logList = query.list();
		if(logList.size() >0){
			BQPriceIndexBtcLog nowLog = logList.get(0);
			BQPriceIndexBtcLog oneHourLog = logList.get(logList.size()-1);
			resVO = new BpiVO();
			resVO.setUpDownRate((nowLog.getPrice().subtract(oneHourLog.getPrice())).divide(oneHourLog.getPrice(), 4,RoundingMode.HALF_DOWN).doubleValue());
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			
			BQPriceIndexBtcLog log = null;
			for(int i=0;i<logList.size();i++){
				log = logList.get(i);
				if(high.signum() ==0 || high.compareTo(log.getPrice()) <= 0){
					high = log.getPrice();
				}
				if(low.signum() ==0 || low.compareTo(log.getPrice()) >= 0){
					low = log.getPrice();
				}
			}
			resVO.setHighestPrice(high.doubleValue());
			resVO.setLowestPrice(low.doubleValue());
		}
		
		
		
	
		return resVO;
	}

}
