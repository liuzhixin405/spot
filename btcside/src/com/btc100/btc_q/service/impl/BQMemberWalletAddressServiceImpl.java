package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberWalletAddressDao;
import com.btc100.btc_q.entity.BQMemberWalletAddress;
import com.btc100.btc_q.service.BQMemberWalletAddressService;

@Service("bqMemberWalletAddressService")
public class BQMemberWalletAddressServiceImpl implements BQMemberWalletAddressService {

	@Autowired
	private BQMemberWalletAddressDao<BQMemberWalletAddress> bqMemberWalletAddressDao;

	public List<BQMemberWalletAddress> readByUserId(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.find(" from BQMemberWalletAddress t where t.f_user_id=:fUserId and currency_type_id = :currencyId and erc20 = false", params);

	}

	public BQMemberWalletAddress readDefaultAddress(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.get(" from BQMemberWalletAddress t where t.f_user_id=:fUserId and currency_type_id = :currencyId and t.is_default=1", params);
	}

	public void save(BQMemberWalletAddress bqwa) {
		bqMemberWalletAddressDao.save(bqwa);
	}

	public void deleteById(long id) {
		String hql = " delete BQMemberWalletAddress t where t.id=:id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		bqMemberWalletAddressDao.deleteCondition(hql, params);
	}

	public void updateById(long id, String address, String name) {
		String hql = "UPDATE BQMemberWalletAddress b SET b.wallet_address=:address and b.name=:name WHERE b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("address", address);
		map.put("name", name);
		map.put("id", id);
		bqMemberWalletAddressDao.updateCondition(hql, map);
	}

	public void setDefaultById(long id, int currencyId) {
		String hql = "UPDATE BQMemberWalletAddress b SET b.is_default=1 WHERE b.id=:id and currency_type_id = :currencyId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("currencyId", currencyId);
		bqMemberWalletAddressDao.updateCondition(hql, map);
	}

	public void updateDefaultById(long id, int currencyId) {
		String hql = "UPDATE BQMemberWalletAddress b SET b.is_default=1 WHERE b.id=:id and currency_type_id = :currencyId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("currencyId", currencyId);
		bqMemberWalletAddressDao.updateCondition(hql, map);
	}

	public BQMemberWalletAddress readById(long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return bqMemberWalletAddressDao.get(" from BQMemberWalletAddress t where t.id=:id", params);
	}
	
	public int readCountByUserAndAddress(long fUserId, String address, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("address", address);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.count("select count(*) from BQMemberWalletAddress t where t.f_user_id=:fUserId and t.wallet_address=:address and t.currency_type_id = :currencyId", params);
	}

	public int readCountByUserAndCurrency(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.count("select count(*) from BQMemberWalletAddress t where t.f_user_id=:fUserId and t.currency_type_id = :currencyId", params);
	}
	
	public void defaultAddr(long fUserId, long id, int currencyId) {
		BQMemberWalletAddress bwa = this.readById(id);
		
		if (bwa != null && bwa.getF_user_id() == fUserId) {
			this.updateDefaultById(id, currencyId);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("currencyId", currencyId);
			map.put("fUserId", fUserId);
			bqMemberWalletAddressDao.updateCondition("UPDATE BQMemberWalletAddress SET is_default=0 WHERE id <> :id and f_user_id = :fUserId and currency_type_id = :currencyId", map);
		}
	}

	@Override
	public List<BQMemberWalletAddress> readErc20ByUserId(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.find(" from BQMemberWalletAddress t where t.f_user_id=:fUserId and currency_type_id = :currencyId and erc20 = true", params);
	}
	
	@Override
	public List<BQMemberWalletAddress> readTrc20ByUserId(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fUserId", fUserId);
		params.put("currencyId", currencyId);
		return bqMemberWalletAddressDao.find(" from BQMemberWalletAddress t where t.f_user_id=:fUserId and currency_type_id = :currencyId and trc20 = true", params);
	}

}
