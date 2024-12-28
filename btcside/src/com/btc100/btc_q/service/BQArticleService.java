package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQArticle;
import com.btc100.btc_q.vo.PagerModel;

public interface BQArticleService {
	
	public int addList(BQArticle obj);
	
	public int modifyList(BQArticle obj);
	
	public int updateTop(int id,int top);
	
	public int update(BQArticle obj);
	
	public int delById(int id);
	
	
	/************************************ for read only **************************************/
	
	public List<BQArticle> readByCategoryAndTime(int categoryId, int from, String direct, long startTime, long endTime, int limit);
	
	public BQArticle readById(int id);
	
	public PagerModel readlistModel(String hql);
	
	public PagerModel readListModel(String title, int itype);
	
	public PagerModel readNewsByCategoryId(int i);
	
}
