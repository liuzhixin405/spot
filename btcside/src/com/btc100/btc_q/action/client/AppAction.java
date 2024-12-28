package com.btc100.btc_q.action.client;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQApp;
import com.btc100.btc_q.service.BQAppService;
import com.btc100.btc_q.util.Utils;

@Controller
@RequestMapping("/app")
public class AppAction extends BaseAction {
	
	@Autowired
	private BQAppService bqAppService;
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@RequestMapping("index")
	public String index(HttpServletRequest request,Model model) {
		String baserUrl = Utils.getWebRoot(request);
		baserUrl += "app/download.html?t=" + (new Date()).getTime() / 1000;
		return "redirect:" + baserUrl;
	}
	
	@RequestMapping("download")
	public String download(HttpServletRequest request,Model model) {
		String agent= request.getHeader("user-agent");
		
		String url = super.getRequestRoot(request);
		
		BQApp app = new BQApp();
		if(agent.contains("iPhone") || agent.contains("iPod") || agent.contains("iPad")) {
			app = spyMemcachedClient.get("BFX_APP_IOS");
			if (app == null) {
				 app = bqAppService.readByPlatform("IOS");
				 if (app != null) {
					 spyMemcachedClient.set("BFX_APP_IOS", BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, app);
				 }
			}
		} else {
			app = spyMemcachedClient.get("BFX_APP_Android");
			if (app == null) {
				 app = bqAppService.readByPlatform("Android");
				 if (app != null) {
					 spyMemcachedClient.set("BFX_APP_Android", BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, app);
				 }
			}
		}
		if (app != null) {
			url = app.getDownload_url();
		}
		
	    if(agent.contains("iPhone") || agent.contains("iPod") || agent.contains("iPad")) {
	    	url = "itms-services://?action=download-manifest&url=" + url;
	    	bqAppService.addIOSTimes();
	    } else {
	    	bqAppService.addAndroidTimes();
	    }
	    
	    return "redirect:" + url;
	}
}
