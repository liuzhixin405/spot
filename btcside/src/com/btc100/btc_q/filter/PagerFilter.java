package com.btc100.btc_q.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

public class PagerFilter implements Filter {

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String spager = request.getParameter("pager.offset");
		if (spager != null) {
			SystemContext.setOffset(getOffset(spager));
		}
		SystemContext.setPageSize(getPageSize());
		try {
			chain.doFilter(request, response);
		} finally {
			// 清空ThreadLocal中的值
			SystemContext.removeOffset();
			SystemContext.removePageSize();
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	public int getOffset(String spager) {
		int offset = 0;
		try {
			offset = Integer.parseInt(spager);
		} catch (NumberFormatException ignore) {
		}
		return offset;
	}

	// 设置每页显示多少条记录
	public int getPageSize() {
		return new PagerModel().getMaxitems();
	}

}
