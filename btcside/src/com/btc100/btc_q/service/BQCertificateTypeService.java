package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQCertificateType;
import com.btc100.btc_q.vo.PagerModel;

public interface BQCertificateTypeService {

	void save(BQCertificateType type); 
	
	void update(BQCertificateType type);
	
	void delete(BQCertificateType type);
	
	
	/************************************ for read only **************************************/
	
	BQCertificateType readById(long id);
	
	List<BQCertificateType> readAll();
	
	PagerModel readAllTypes();
}
