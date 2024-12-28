package com.btc100.btc_q.action.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.service.BQManagerMenuService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerMenu")
public class ManagerMenuAction extends BaseAction {

	@Autowired
	private BQManagerMenuService bqManagerMenuService;

	@RequestMapping(value = "/loadManagerMenu")
	public String loadManagerMenu(Model mo) {

		PagerModel pm = bqManagerMenuService.readPagerModel();
		mo.addAttribute("pm", pm);

		return super.url_JSP("managerMenuList");
	}
	
	@RequestMapping(value = "/loadUpdateMenu")
	public String loadUpdateMenu(String id, Model mo) {
		int menuId = ConvertUtil.toInt(id, 0);
		if (menuId < 0) {
			return super.errorPage(mo, "该菜单不存在");
		}
		BQManagerMenu menu = bqManagerMenuService.readById(menuId);
		mo.addAttribute("menu", menu);
		
		return super.url_JSP("managerMenuDetails");
	}

	@RequestMapping(value = "/addOrUpdateMenu", method = RequestMethod.POST)
	public String addOrUpdateMenu(HttpServletRequest request, Model mo) throws Exception {
		int id = ConvertUtil.toInt(request.getParameter("id"), 0);
		String menuNo = request.getParameter("menuNo");
		String menuName = request.getParameter("menuName");
		String link = request.getParameter("link");
		String password = request.getParameter("password");

		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (menuNo == null || menuNo.isEmpty()) {
			return super.errorPage(mo, "序号不能为空");
		}
		
		if (menuName == null || menuName.isEmpty()) {
			return super.errorPage(mo, "主菜单名不能为空");
		}
		
		if (link == null || link.isEmpty()) {
			return super.errorPage(mo, "链接不能为空");
		}
		
		BQManagerMenu menu = new BQManagerMenu();
		
		if (id > 0) {
			menu = bqManagerMenuService.readById(id);
			menu.setMenu_name(menuName);
			menu.setMenu_no(menuNo);
			menu.setLink(link);
			
			bqManagerMenuService.update(menu);
		} else {
			BQManagerMenu managerMenu = bqManagerMenuService.readByMenuNo(menuNo);
			if (null != managerMenu) {
				return super.errorPage(mo, "已存在该主菜单");
			}
			menu.setMenu_name(menuName);
			menu.setMenu_no(menuNo);
			menu.setLink(link);
			bqManagerMenuService.add(menu);
		}

		return "redirect:/managerMenu/loadManagerMenu.do";
	}

	@RequestMapping(value = "/delManagerMenu")
	public String delManagerMenu(String id) {
		int menuId = ConvertUtil.toInt(id, 0);
		if (menuId > 0) {
			bqManagerMenuService.delById(menuId);
		}
		return "redirect:/managerMenu/loadManagerMenu.do";
	}

}
