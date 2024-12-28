package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.OTHERTradesdataDao;
import com.btc100.btc_q.entity.OTHERTradesdata;
import com.btc100.btc_q.service.OTHERTradesdataService;
import com.btc100.btc_q.vo.BPIDataVO;

@Service("otherTradesdataService")
public class OTHERTradesdataServiceImpl implements OTHERTradesdataService {

	@Autowired
	private OTHERTradesdataDao<OTHERTradesdata> otherTradesdataDao;

	public List<OTHERTradesdata> findLastOneByFlag(int flag) {
		return otherTradesdataDao.findOneByFlag(flag);
	}

	public OTHERTradesdata findSumByFlag(int flag) {
		return otherTradesdataDao.findSumByFlag(flag);
	}

	public BigDecimal findMaxByFlag(int flag) {
		return otherTradesdataDao.findMaxByFlag(flag);
	}

	public BigDecimal findMinByFlag(int flag) {
		return otherTradesdataDao.findMinByFlag(flag);
	}

	public OTHERTradesdata findLastByFlag(int flag) {
		return otherTradesdataDao.findLastByFlag(flag);
	}

	public int deleteByFlagAndTime(int flag, long time) {
		return otherTradesdataDao.deleteByFlagAndTime(flag, time);
	}

	public void insertList(List<OTHERTradesdata> data) {
		if (data != null) {
			for (OTHERTradesdata od : data) {
				otherTradesdataDao.save(od);
			}
		}
	}

	public void insertOne(OTHERTradesdata data) {
		if (data != null) {
			otherTradesdataDao.save(data);
		}
	}

	public int insertList2(List<BPIDataVO> data,int flag) {
		StringBuilder sql = new StringBuilder();

		// new
		// OTHERTradesdata(vo.getAmount(),vo.getPrice(),vo.getTid(),"1",vo.getTimestamp(),String.valueOf(Contents.OTHERTRADEDATAS_OKCOIN)
		if (data != null && data.size() > 0) {
			sql.append("INSERT INTO other_tradesdata (d_amount, d_price, d_oid, d_type, d_time, d_flag) VALUES");
			for (BPIDataVO vo : data) {
				sql.append("(");
				sql.append(vo.getAmount());
				sql.append(",");
				sql.append(vo.getPrice());
				sql.append(",");
				sql.append(vo.getTid());
				sql.append(",'1',");
				sql.append(vo.getDate());
				sql.append(",'");
				sql.append(String.valueOf(flag));
				sql.append("'),");
			}

			String sqlRun="";
			if (sql.toString().endsWith(",")) {
				sqlRun = sql.substring(0, sql.length()-1);
			}
			
			Session session=otherTradesdataDao.getSession();
			SQLQuery query = session.createSQLQuery(sqlRun);
			return query.executeUpdate();
		}
		
		return 0;
		// if (data != null) {
		// for (OTHERTradesdata od : data) {
		// otherTradesdataDao.save(od);
		// }
		// }
	}

}
