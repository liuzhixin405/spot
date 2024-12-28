package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_article_content")
public class BQArticleContent implements Serializable {

	private static final long serialVersionUID = -4588092280050653247L;
	
	private int article_id;
	private String content;
	private long credit_log_id;
	private int credit_type_id;
	private long dateline;
	private long f_user_id;
	private String title;

	public BQArticleContent() {

	}

	@Id
	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int value) {
		article_id = value;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String value) {
		content = value;
	}

	public long getCredit_log_id() {
		return credit_log_id;
	}

	public void setCredit_log_id(long value) {
		credit_log_id = value;
	}

	public int getCredit_type_id() {
		return credit_type_id;
	}

	public void setCredit_type_id(int value) {
		credit_type_id = value;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long value) {
		dateline = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		title = value;
	}

}
