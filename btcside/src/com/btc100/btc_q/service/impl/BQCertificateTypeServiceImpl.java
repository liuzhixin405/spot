package com.btc100.btc_q.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQCertificateTypeDao;
import com.btc100.btc_q.entity.BQCertificateType;
import com.btc100.btc_q.service.BQCertificateTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqCertificateTypeService")
public class BQCertificateTypeServiceImpl implements BQCertificateTypeService {

	@Autowired
	private BQCertificateTypeDao<BQCertificateType> bqCertificateTypeDao;

	@Override
	public BQCertificateType readById(long id) {
		return bqCertificateTypeDao.get(BQCertificateType.class, id);
	}

	@Override
	public void save(BQCertificateType type) {
		bqCertificateTypeDao.save(type);
	}

	@Override
	public void update(BQCertificateType type) {
		bqCertificateTypeDao.update(type);
	}

	@Override
	public void delete(BQCertificateType type) {
		bqCertificateTypeDao.delete(type);
	}

	@Override
	public List<BQCertificateType> readAll() {
		return bqCertificateTypeDao.find("from BQCertificateType where 1=1");
	}

	@Override
	public PagerModel readAllTypes() {
		return bqCertificateTypeDao.searchHql("from BQCertificateType where 1=1");
	}

}
