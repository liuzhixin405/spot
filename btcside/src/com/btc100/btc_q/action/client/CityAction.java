package com.btc100.btc_q.action.client;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.entity.BQAreaArea;
import com.btc100.btc_q.entity.BQAreaCity;
import com.btc100.btc_q.entity.BQAreaCountry;
import com.btc100.btc_q.entity.BQAreaProvince;
import com.btc100.btc_q.service.BQAreaAreaService;
import com.btc100.btc_q.service.BQAreaCityService;
import com.btc100.btc_q.service.BQAreaCountryService;
import com.btc100.btc_q.service.BQAreaProvinceService;
import com.btc100.btc_q.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/city")
public class CityAction extends BaseAction {

	@Autowired
	private BQAreaCityService bqAreaCityService;
	@Autowired
	private BQAreaProvinceService bqAreaProviceService;
	@Autowired
	private BQAreaCountryService bqAreaCountryService;
	@Autowired
	private BQAreaAreaService bqAreaAreaService;

	/**
	 * 加载省份数据（直辖市）
	 * @throws JSONException 
	 * 
	 */
	@RequestMapping("loadProvice")
	public void loadProvice(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		Locale locale = getLocale(request);
		
		String countryCode = request.getParameter("countryCode");
		if (countryCode == null || countryCode.trim().equals("")) {
			toWeb(response, "noId");
			return;
		} else {
			List<BQAreaProvince> provices = bqAreaProviceService.readProvice(countryCode);
			if (provices == null || provices.size() < 1) {
				toWeb(response, "null");
				return;
			} else {
				JSONArray array = new JSONArray();
				StringBuilder builder = new StringBuilder("[");
				for (BQAreaProvince bc : provices) {
					JSONObject json = new JSONObject();
					json.put("code", bc.getCode());
					String name = bc.getName();
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
					json.put("name", name);
					array.put(json);
				}
				if (builder.length() > 1)
					builder.replace(builder.length() - 1, builder.length(), "]");
				toWeb(response, array);// builder.toString());
			}
		}
	}

	/**
	 * 加载城市数据
	 * 
	 */
	@RequestMapping("loadCity")
	public void loadCity(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = getLocale(request);
		
		String proviceCode = request.getParameter("proviceCode");
		if (proviceCode == null || proviceCode.trim().equals("")) {
			toWeb(response, "noId");
		} else {
			List<BQAreaCity> citys = bqAreaCityService.readCitys(proviceCode);
			if (citys == null || citys.size() < 1) {
				toWeb(response, "null");
			} else {
				StringBuilder builder = new StringBuilder("[");
				for (BQAreaCity baa : citys) {
					builder.append("{'code':'");
					builder.append(baa.getCode());
					builder.append("','name':'");
					String name = baa.getName();
					if ("en_US".equals(locale.toString())) {
						name = StringUtil.getPinyinString(name).replaceAll("shi", "").replaceAll("diqu", "");
						if ("Xianggang".equals(name)) {
							name = "Hongkong";
						}
					}
					builder.append(name);
					builder.append("'},");
				}
				if (builder.length() > 1)
					builder.replace(builder.length() - 1, builder.length(), "]");
				toWeb(response, builder.toString());
			}
		}
	}

	/**
	 * 加载区数据
	 *
	 */
	@RequestMapping("loadArea")
	public void loadArea(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = getLocale(request);

		String cityCode = request.getParameter("cityCode");
		if (cityCode == null || cityCode.trim().equals("")) {
			toWeb(response, "noId");
		} else {
			List<BQAreaArea> areas = bqAreaAreaService.readAreas(cityCode);
			if (areas == null || areas.size() < 1) {
				toWeb(response, "null");
			} else {
				StringBuilder builder = new StringBuilder("[");
				for (BQAreaArea area : areas) {
					builder.append("{'code':'");
					builder.append(area.getCode());
					builder.append("','name':'");
					String name = area.getName();
					if ("en_US".equals(locale.toString())) {
						name = StringUtil.getPinyinString(name).replaceAll("shi", "");
					}
					builder.append(name);
					builder.append("'},");
				}
				if (builder.length() > 1)
					builder.replace(builder.length() - 1, builder.length(), "]");
				toWeb(response, builder.toString());
			}
		}
	}

	/**
	 * 加载区数据
	 * 
	 */
	@RequestMapping("loadCityProvice")
	public void loadCityProvice(HttpServletRequest request, HttpServletResponse response) {
		String proviceCode = request.getParameter("proviceCode");
		if (proviceCode == null || proviceCode.trim().equals("")) {
			toWeb(response, "noId");
		} else {
			BQAreaProvince city = bqAreaProviceService.readProviceByCode(proviceCode);
			if (city == null ) {
				toWeb(response, "null");
			} else {
				StringBuilder builder = new StringBuilder("[");
				
					builder.append("{'code':'");
					builder.append(city.getCode());
					builder.append("','name':'");
					builder.append(city.getName());
					builder.append("'},");
				
				if (builder.length() > 1)
					builder.replace(builder.length() - 1, builder.length(), "]");
				toWeb(response, builder.toString());
			}
		}
	}
	
	/**
	 * 加载国家数据
	 * 
	 */
	@RequestMapping("loadCountry")
	public void loadCountry(HttpServletRequest request, HttpServletResponse response) {
		
		List<BQAreaCountry> citys = bqAreaCountryService.readCountry();
		if (citys == null || citys.size() < 1) {
			toWeb(response, "null");
		} else {
			StringBuilder builder = new StringBuilder("[");
			for (BQAreaCountry baa : citys) {
				builder.append("{'code':'");
				builder.append(baa.getCode());
				builder.append("','zh_name':'");
				builder.append(baa.getZh_name());
				builder.append("','en_name':'");
				builder.append(baa.getEn_name());
				builder.append("'},");
			}
			if (builder.length() > 1)
				builder.replace(builder.length() - 1, builder.length(), "]");
			toWeb(response, builder.toString());
		}
	}

}
