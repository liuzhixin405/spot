package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberWalletDao;
import com.btc100.btc_q.entity.BQMemberWallet;
import com.btc100.btc_q.service.BQMemberWalletService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqMemberWalletService")
public class BQMemberWalletServiceImpl implements BQMemberWalletService {

	@Autowired
	private BQMemberWalletDao<BQMemberWallet> bqMemberWalletDao;

	public List<BQMemberWallet> readById(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", fUserId);
		params.put("type", currencyId);

		String sql = "from BQMemberWallet where f_user_id=:uid and currency_type_id=:type ";
		return bqMemberWalletDao.find(sql, params);
	}
	
	@Override
	public List<BQMemberWallet> readErc20AddressByUser(long uid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("erc20", true);

		String sql = "from BQMemberWallet where f_user_id=:uid and erc20=:erc20 ";
		return bqMemberWalletDao.find(sql, params);
	}
	
	@Override
	public List<BQMemberWallet> readTrc20AddressByUser(long uid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("trc20", true);

		String sql = "from BQMemberWallet where f_user_id=:uid and trc20=:trc20 ";
		return bqMemberWalletDao.find(sql, params);
	}

	public PagerModel readPageById(long fUserId, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", currencyId);

		String sql = "from BQMemberWallet where currency_type_id=:type ";

		if (fUserId > 0) {
			params.put("uid", fUserId);
			sql = sql + " and f_user_id=:uid ";
		}

		return bqMemberWalletDao.searchHql(sql, params);
	}

	public PagerModel readByAddress(String address, int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", currencyId);

		String sql = "from BQMemberWallet where currency_type_id=:type";

		if (!StringUtil.isEmpty(address)) {
			params.put("address", address);
			sql = sql + " and wallet_address=:address ";
		}

		return bqMemberWalletDao.searchHql(sql, params);
	}

	public void save(BQMemberWallet bqw) {
		bqMemberWalletDao.save(bqw);
	}

	public PagerModel readAll(int currencyId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", currencyId);
		String hql = "FROM BQMemberWallet where currency_type_id=:type ORDER BY id DESC";
		return bqMemberWalletDao.searchHql(hql, params);
	}

	@Override
	public BQMemberWallet readByAddress(String address) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("address", address);
		String hql = "FROM BQMemberWallet where wallet_address=:address";
		
		return bqMemberWalletDao.get(hql, params);
	}

}
