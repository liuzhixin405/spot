package com.btc100.btc_q.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btc100.btc_q.dao.BQMemberApplyAgentAreaDao;
import com.btc100.btc_q.entity.BQMemberApplyAgentArea;
import com.btc100.btc_q.service.BQMemberApplyAgentAreaService;

@Service("bqMemberApplyAgentAreaService")
public class BQMemberApplyAgentAreaServiceImpl implements BQMemberApplyAgentAreaService {

	@Autowired
	private BQMemberApplyAgentAreaDao<BQMemberApplyAgentArea> bqMemberApplyAgentAreaDao;

	public void saveAgentArea(BQMemberApplyAgentArea agentArea) {
		bqMemberApplyAgentAreaDao.save(agentArea);
	}

}
