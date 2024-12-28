package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSurecomeDao;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqSurecomeService")
public class BQSurecomeServiceImpl implements BQSurecomeService {

	@Autowired
	private BQSurecomeDao<BQSurecome> bqSurecomeDao;

	public boolean login(String suname, String spwd) throws Exception {
		String hql = "FROM BQSurecome WHERE user_name=:name";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", suname);
		List<BQSurecome> list = bqSurecomeDao.find(hql, map);
		if (list == null || list.size() <= 0)
			return false;

		BQSurecome bqSurecome = list.get(0);
		if (MD5Service.encryptString(MD5Service.encryptString(spwd) + suname).equals(bqSurecome.getUser_pwd())) {
			return true;
		}

		return false;
	}

	public boolean surepwd2(String suname, String spwd) throws Exception {
		String hql = "FROM BQSurecome WHERE user_name=:name";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", suname);
		List<BQSurecome> list = bqSurecomeDao.find(hql, map);
		if (list == null || list.size() <= 0)
			return false;

		BQSurecome bqSurecome = list.get(0);
		String pwd = MD5Service.encryptString(bqSurecome.getUser_salt() + MD5Service.encryptString(spwd));
		if (pwd.equals(bqSurecome.getUser_thpwd())) {
			return true;	
		}

		return false;
	}

	public BQSurecome findUserByParams(String username, String password) throws Exception {
		String hql = "FROM BQSurecome WHERE user_name = :name AND user_pwd = :pwd";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", username);
		map.put("pwd", MD5Service.encryptString(MD5Service.encryptString(password) + username));
		List<BQSurecome> list = bqSurecomeDao.find(hql, map);
		if (list == null || list.size() <= 0)
			return null;

		return list.get(0);

	}

	@Override
	public int add(BQSurecome obj) {
		bqSurecomeDao.save(obj);
		return 1;
	}

	@Override
	public int update(BQSurecome obj) {
		bqSurecomeDao.update(obj);
		return 1;
	}

	@Override
	public int delById(int id) {
		String hql = "DELETE BQSurecome b where b.user_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqSurecomeDao.deleteCondition(hql, map);
	}

	@Override
	public void updateCoinsByName(String name, String coins) {
		final String hql="update BQSurecome u set u.user_coins=:coins where u.user_name=:name";
		Map<String, Object>  params = new HashMap<String, Object> ();
		params.put("name", name);
		params.put("coins",coins);
		bqSurecomeDao.updateCondition(hql, params);
	}

	@Override
	public BQSurecome findById(int id) {
		String hql = "FROM BQSurecome b where b.user_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqSurecomeDao.get(hql, map);
	}

	@Override
	public PagerModel findAllModel() {
		String hql = "FROM BQSurecome";
		return bqSurecomeDao.searchHql(hql);
	}
	
	@Override
	public BQSurecome getByName(String username) {
		final String hql = "from BQSurecome u where u.user_name = :username";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		BQSurecome manager = (BQSurecome) bqSurecomeDao.get(hql, params);
		return manager;
	}

	@Override
	public void clearLoginTime(String username) {
		final String hql="update BQSurecome u set u.pwd_count=0 where u.user_name=:name";
		Map<String, Object>  params = new HashMap<String, Object> ();
		params.put("name", username);
		bqSurecomeDao.updateCondition(hql, params);
	}

	@Override
	public void clearGoogleLoginTime(String username) {
		final String hql="update BQSurecome u set u.google_count=0 where u.user_name=:name";
		Map<String, Object>  params = new HashMap<String, Object> ();
		params.put("name", username);
		bqSurecomeDao.updateCondition(hql, params);
	}
}
