package com.btc100.btc_q.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.btc100.btc_q.util.DateUtil;

public class DateUtilTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private long dateline = 0;
	private String pattern = "";

	public DateUtilTag() {

	}

	@Override
	public int doStartTag() throws JspException {
		return super.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			if (dateline <= 0) {
				writer.print("");
			} else {
				if (pattern != null && pattern != "")
					writer.print(DateUtil.dateToStr(DateUtil.UnixToDate(dateline), pattern));
				else
					writer.print(DateUtil.UnixToDateString(dateline));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
