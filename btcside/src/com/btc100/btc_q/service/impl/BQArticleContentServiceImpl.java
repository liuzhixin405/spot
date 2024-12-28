package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQArticleContentDao;
import com.btc100.btc_q.entity.BQArticleContent;
import com.btc100.btc_q.service.BQArticleContentService;

@Service("bqArticleContentService")
public class BQArticleContentServiceImpl implements BQArticleContentService {

	@Autowired
	private BQArticleContentDao<BQArticleContent> bqArticleContentDao;

	public BQArticleContent readById(int id) {
		String hql = "FROM BQArticleContent b where b.article_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		List<BQArticleContent> list = bqArticleContentDao.find(hql, map, 1, 1);
		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public int add(BQArticleContent entify) {
		bqArticleContentDao.save(entify);
		return 1;
	}

	public int update(BQArticleContent entify) {
		bqArticleContentDao.update(entify);
		return 1;
	}
	
	public int del(int id){		
		String hql="DELETE BQArticleContent b where b.article_id=:id";
//		BQArticleContent o = (BQArticleContent)bqArticleContentDao.get(BQArticleContent.class,new Integer(id));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		bqArticleContentDao.deleteCondition(hql, map);
		return 1;
	}
	

}
