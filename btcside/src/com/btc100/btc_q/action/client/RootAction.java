package com.btc100.btc_q.action.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;


@Controller
public class RootAction extends BaseAction {
	
	@RequestMapping("download")
	public String index(HttpServletRequest request,Model model) {
		return "jsp/download";
	}
}