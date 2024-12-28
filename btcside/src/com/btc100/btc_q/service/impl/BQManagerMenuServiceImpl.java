package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQManagerMenuDao;
import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.service.BQManagerMenuService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqManagerMenuService")
public class BQManagerMenuServiceImpl implements BQManagerMenuService {

	@Autowired
	private BQManagerMenuDao<BQManagerMenu> bqManagerMenuDao;

	@Override
	public int add(BQManagerMenu menu) {
		bqManagerMenuDao.save(menu);
		return 1;
	}

	@Override
	public int update(BQManagerMenu menu) {
		bqManagerMenuDao.update(menu);
		return 1;
	}

	@Override
	public int delById(int id) {
		String hql = "DELETE BQManagerMenu b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqManagerMenuDao.deleteCondition(hql, map);
	}

	@Override
	public BQManagerMenu readById(int id) {
		String hql = "FROM BQManagerMenu b where b.id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqManagerMenuDao.get(hql, map);
	}

	@Override
	public BQManagerMenu readByMenuNo(String menuNo) {
		String hql = "FROM BQManagerMenu b where b.menu_no=:menuNo";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("menuNo", menuNo);

		return bqManagerMenuDao.get(hql, map);
	}

	@Override
	public PagerModel readPagerModel() {
		String hql = "FROM BQManagerMenu";
		return bqManagerMenuDao.searchHql(hql);
	}

	@Override
	public List<BQManagerMenu> findAllList(int state) {
		String hql = "FROM BQManagerMenu b where b.state=:state";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", state);

		return bqManagerMenuDao.find(hql, map);
	}

}
