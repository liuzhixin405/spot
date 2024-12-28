package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQCommonDataDao;
import com.btc100.btc_q.entity.BQCommonData;
import com.btc100.btc_q.service.BQCommonDataService;

@Service("bqCommonDataService")
public class BQCommonDataServiceImpl implements BQCommonDataService {

	@Autowired
	private BQCommonDataDao<BQCommonData> bqCommonDataDao;

	public BigDecimal readShareLoss(String transactionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bqCode", transactionCode);
		List<BQCommonData> lstCommonData = bqCommonDataDao.find("FROM BQCommonData WHERE content = :bqCode", params);
		if (lstCommonData == null || lstCommonData.size() <= 0) {
			return BigDecimal.ZERO;
		}
		return lstCommonData.get(0).getData();
	}

	public void updateCommonData(String bqCode, BigDecimal data) {
		List<BQCommonData> lstCommonData = bqCommonDataDao.findByCode(bqCode);
		if (lstCommonData == null || lstCommonData.size() <= 0) {
			return;
		}
		BQCommonData commonData = lstCommonData.get(0);
		bqCommonDataDao.UpdateData(commonData.getId(), data);
	}

}
