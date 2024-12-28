package com.btc100.btc_q.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class SMSConfig implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 5447744854826598007L;
	private static Properties prop = new Properties();

	static {
		InputStream in = Object.class.getResourceAsStream("/smsconfig.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 私有构造方法，不需要创建对象
	 */
	private SMSConfig() {
	}

	public static String getVal(String skey) {
		return prop.getProperty(skey);
	}

}
