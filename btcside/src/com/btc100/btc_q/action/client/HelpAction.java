package com.btc100.btc_q.action.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;

@Controller
@RequestMapping("/help")
public class HelpAction extends BaseAction {

	@RequestMapping("/help")
	public String help(HttpServletRequest request, Model model) {
		return "jsp/user_help";
	}

	@RequestMapping("/rule")
	public String rule(HttpServletRequest request, Model model) {
		return "jsp/user_rule";
	}

	@RequestMapping("/safe")
	public String safe(HttpServletRequest request, Model model) {
		return "jsp/user_safe";
	}

	@RequestMapping("/sz")
	public String szcontract(HttpServletRequest request, Model model) {
		return "jsp/sz-contract";
	}
	
	@RequestMapping("/btc")
	public String btccontract(HttpServletRequest request, Model model) {
		return "jsp/btc-contract";
	}
	
	@RequestMapping("/eth")
	public String ethcontract(HttpServletRequest request, Model model) {
		return "jsp/eth-contract";
	}
	
	@RequestMapping("/ft")
	public String ftcontract(HttpServletRequest request, Model model) {
		return "jsp/ft-contract";
	}
	
	@RequestMapping("/a50")
	public String a50contract(HttpServletRequest request, Model model) {
		return "jsp/a50-contract";
	}
	
	@RequestMapping("/graphic")
	public String graphic(HttpServletRequest request, Model model) {
		return "jsp/graphic";
	}
}
