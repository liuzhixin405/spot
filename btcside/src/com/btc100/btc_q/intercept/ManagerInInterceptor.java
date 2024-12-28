package com.btc100.btc_q.intercept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.service.BQManagerMenuService;
import com.btc100.btc_q.util.Utils;

public class ManagerInInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private BQManagerMenuService bqManagerMenuService;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, null);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String surl = request.getRequestURL().toString();

		if (surl.contains("/managerin/owner.do"))
			return true;

		Object sName = request.getSession().getAttribute("owner");
		String googleCode = (String)request.getSession().getAttribute("googleCode");
		BQSurecome manager = (BQSurecome)sName;
		if (sName == null||manager==null) {
			request.setAttribute("type", 2); // 1对号, 2错号
			request.setAttribute("msg", "对不起，您需要先登录才能继续本操作！");
			request.setAttribute("ref", Utils.getWebRoot(request));
			request.getRequestDispatcher("/error/SystemInfo.jsp").forward(request, response);
			return false;
		}

		if (googleCode == null || googleCode.isEmpty()) {
			String secretKey = manager.getGoogle_no();
			request.getSession().setAttribute("owner", manager);
			request.getSession().setAttribute("secretKey", secretKey);
			request.getRequestDispatcher("/qtwner/up/down/togo/googleLogin.jsp").forward(request, response);
			return false;
		}
		
		String[] role = manager.getUser_role().split(",");
		List<String> lst_Roles=new ArrayList<String>();
		for(String s:role){
			lst_Roles.add(s);
		}
		
		String ssecName = "";
		String actionName = request.getRequestURI();
		int index = actionName.lastIndexOf("/");
		String sfirName = actionName.substring(0, index);
		index = sfirName.lastIndexOf("/");
		ssecName = sfirName.substring(index + 1);
		
		String[] paths = sfirName.split("/");
		if (paths != null && paths.length > 2) {
			if ("simulation".equalsIgnoreCase(paths[1])) {
				ssecName = "simulation";
			}
		}

//		System.out.println(ssecName);
		
		Map<String, String> mapRole=new HashMap<String, String>();
		List<BQManagerMenu> menus = bqManagerMenuService.findAllList(1);
		for (BQManagerMenu menu : menus) {
			mapRole.put(menu.getLink(), menu.getMenu_no());
		}
		
		if(mapRole.containsKey(ssecName)){
			if(lst_Roles.contains(mapRole.get(ssecName))){
				return true;
			}
		}
		
		request.setAttribute("type", 2); // 1对号, 2错号
		request.setAttribute("msg", "对不起，您没有该权限！");
		request.setAttribute("ref", Utils.getWebRoot(request)); 
		request.getRequestDispatcher("/error/SystemInfo.jsp").forward(request, response);
		return false;
	}
}
