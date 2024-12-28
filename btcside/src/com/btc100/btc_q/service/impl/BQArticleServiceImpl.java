package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQArticleDao;
import com.btc100.btc_q.entity.BQArticle;
import com.btc100.btc_q.service.BQArticleService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqArticleService")
public class BQArticleServiceImpl implements BQArticleService {

	@Autowired
	private BQArticleDao<BQArticle> bqArticleDao;

	public List<BQArticle> readByCategoryAndTime(int categoryId, int from, String direct, long startTime, long endTime,
			int limit) {
		String hql = "from BQArticle where language = :language ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("language", Contents.Local.LOCALE.get().toString());
		
		if (categoryId > 0) {
			hql += " and category_id = :categoryId";
			map.put("categoryId", categoryId);
		}
		if (startTime > 0) {
			hql += " and dateline >= :startTime";
			map.put("startTime", startTime);
		}
		if (endTime > 0) {
			hql += " and dateline < :endTime";
			map.put("endTime", endTime);
		}
		if (from >= 0 && "prev".equals(direct)) {
			map.put("from", from);
			hql += " and article_id >= :from";
		} else if (from >= 0 && "next".equals(direct)) {
			map.put("from", from);
			hql += " and article_id < :from";
		}
		
		hql += " order by article_id desc";
		
		return bqArticleDao.find(hql, map, 1, limit);
	}

	public int updateTop(int id, int top) {
		String hql = "UPDATE BQArticle b SET b.istop=:top where b.article_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("top", top);

		return bqArticleDao.updateCondition(hql, map);
	}

	public int delById(int id) {
		String hql = "DELETE BQArticle b where b.article_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);

		return bqArticleDao.deleteCondition(hql, map);
	}

	public int addList(BQArticle obj) {
		bqArticleDao.save(obj);
		return obj.getArticle_id();
	}

	public int modifyList(BQArticle obj) {
		bqArticleDao.update(obj);

		return 0;
	}

	public BQArticle readById(int id) {
		String hql = "FROM BQArticle b where b.article_id=:id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		List<BQArticle> list = bqArticleDao.find(hql, map, 1, 1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public int update(BQArticle obj) {
		bqArticleDao.update(obj);
		return 1;
	}

	public PagerModel readlistModel(String hql) {
		hql = "from BQArticle b order by b.istop desc,b.article_id desc";
		return bqArticleDao.searchHql(hql, 0, new PagerModel().getMaxitems());
	}

	public PagerModel readListModel(String title, int itype) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQArticle b where ");
		if (title != null && !title.isEmpty()) {
			sbBuilder.append("b.title like :title AND   ");
			map.put("title", "%" + title + "%");
		}
		if (itype > 0) {
			sbBuilder.append("b.category_id=:category_id");
			map.put("category_id", itype);
		}

		String hql = " order by b.istop desc,b.article_id desc";

		if (sbBuilder.toString().endsWith("AND   ") || sbBuilder.toString().endsWith("where "))
			hql = sbBuilder.substring(0, sbBuilder.length() - 7).toString() + hql;
		else
			hql = sbBuilder.toString() + hql;

		return bqArticleDao.searchHql(hql, map);
	}

	public PagerModel readNewsByCategoryId(int i) {
		StringBuilder sbBuilder = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();

		sbBuilder.append("from BQArticle b where b.category_id=:category_id and language = :language");

		map.put("category_id", i);
		map.put("language", Contents.Local.LOCALE.get().toString());

		String hql = " order by b.istop desc,b.article_id desc";

		hql = sbBuilder.toString() + hql;

		return bqArticleDao.searchHql(hql, map);
	}
}
