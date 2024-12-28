package com.btc100.btc_q.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQAreaCity;
import com.btc100.btc_q.entity.BQAreaCountry;
import com.btc100.btc_q.entity.BQAreaProvince;
import com.btc100.btc_q.service.BQAreaCityService;
import com.btc100.btc_q.service.BQAreaCountryService;
import com.btc100.btc_q.service.BQAreaProvinceService;
import com.btc100.btc_q.util.StringUtil;
import com.google.gson.Gson;

/**
 * 获取地区城市API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/cityApi")
public class CityApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(CityApiAction.class);
	
	@Autowired
	private BQAreaCityService bqAreaCityService;
	@Autowired
	private BQAreaProvinceService bqAreaProviceService;
	@Autowired
	private BQAreaCountryService bqAreaCountryService;
	
	/**
	 * 加载国家
	 * 
	 */
	@RequestMapping("/loadCountry")
	public String loadCountry(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		List<BQAreaCountry> countrys = bqAreaCountryService.readCountry();
		for (BQAreaCountry country : countrys) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("code", country.getCode()); // 国家编码
			map.put("zh_name", country.getZh_name()); // 中文名称
			map.put("en_name", country.getEn_name()); // 英文名称
			
			Gson gson = new Gson();
			array.put(new JSONObject(gson.toJson(map)));
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);	
		return apiData(response);
	}
	
	/**
	 * 加载省份
	 * 
	 */
	@RequestMapping("/loadProvice")
	public String loadProvice(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String countryCode = request.getParameter("countryCode");
		if (StringUtil.isEmpty(countryCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，countryCode错误");
			model.addAttribute("ticker", tradesStr);	
			return apiData(response);
		}
		
		Locale locale = getLocale(request);
		JSONArray array = new JSONArray();
		List<BQAreaProvince> provices = bqAreaProviceService.readProvice(countryCode);
		if (provices != null && provices.size() > 0) {
			for (BQAreaProvince province : provices) {
				Map<String,Object> map = new HashMap<String, Object>();
				String name = province.getName();
				if ("en_US".equals(locale.toString())) {
					name = StringUtil.getPinyinString(name).replaceAll("sheng", "").replaceAll("shi", "");
					if ("Xianggang".equals(name)) {
						name = "Hongkong";
					} else if ("Zhongqing".equals(name)) {
						name = "Chongqing";
					} else if ("Henan".equals(name)) {
						name = "He'nan";
					} else if ("Hunan".equals(name)) {
						name = "Hu'nan";
					} 
				}
				map.put("name", name);
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(map)));
			}
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);	
		return apiData(response);
	}
	
	/**
	 * 加载城市
	 * 
	 */
	@RequestMapping("/loadCity")
	public String loadCity(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String proviceCode = request.getParameter("proviceCode");
		if (StringUtil.isEmpty(proviceCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，proviceCode错误");
			model.addAttribute("ticker", tradesStr);	
			return apiData(response);
		}
		
//		Locale locale = getLocale(request);
		Locale locale = new Locale("en", "US");
		JSONArray array = new JSONArray();
		List<BQAreaCity> citys = bqAreaCityService.readCitys(proviceCode);
		if (citys != null && citys.size() > 0) {
			for (BQAreaCity city : citys) {
				Map<String,Object> map = new HashMap<String, Object>();
				String name = city.getName();
				if ("en_US".equals(locale.toString())) {
					name = StringUtil.getPinyinString(name).replaceAll("shi", "").replaceAll("diqu", "");
					if ("Xianggang".equals(name)) {
						name = "Hongkong";
					}
				}
				map.put("name", name);
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(map)));
			}
		}
		userJson.put("data", array);
		
		model.addAttribute("ticker", userJson);	
		return apiData(response);
	}
	
}
