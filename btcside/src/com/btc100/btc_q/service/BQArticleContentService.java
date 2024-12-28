package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQArticleContent;

public interface BQArticleContentService {
	
	public int add(BQArticleContent entify);
	
	public int update(BQArticleContent entify);
	
	public int del(int id);
	
	
	/************************************ for read only **************************************/
	
	public BQArticleContent readById(int id);
}
