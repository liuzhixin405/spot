package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberCertificate;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberCertificateService {

	void save(BQMemberCertificate entity);
	
	void update(BQMemberCertificate entity);
	
	void delete(BQMemberCertificate entity);
	
	
	/************************************ for read only **************************************/
	
	BQMemberCertificate readById(long id);
	
	PagerModel readPagerModel();
	
	PagerModel readPageByUserId(long userId);
	
	List<BQMemberCertificate> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit);
	
}
