package com.btc100.btc_q.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

public class UrlConfig implements Serializable {

	private static final long serialVersionUID = 6917368633310019443L;
	private Properties propertyCache = null;
	private BtcCache cache;
	
	public UrlConfig() {
		if (cache == null)
			cache = new BtcCache();
		if (cache.get("UrlConfig") == null) {
			load();
			cache.put("UrlConfig", propertyCache);
		} else {
			propertyCache = (Properties) cache.get("UrlConfig");
		}
	}

	/**
	 * 加载urlconfig.properties数据到缓存中
	 */
	private void load() {
		propertyCache = new Properties();	
		
		try {
			String fileName = "/urlconfig.properties";
			propertyCache.load(getClass().getResourceAsStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException(
					"IO exception  error in getResourceAsStream", e);
		}
	}

	public String get(String key) {
		String value = null;

		if (propertyCache.containsKey(key)) {
			value = propertyCache.getProperty(key);
		}

		return value;
	}
	
	/**
	 * 前台jsp页面调用
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		UrlConfig config = new UrlConfig();
		return config.get(key);
	}

}
