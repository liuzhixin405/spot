package com.btc100.btc_q.util;

public class SafeToken {
	public String getToken(){
		try {
			return MD5Service.encryptString16(System.currentTimeMillis()+"h"+RandomUtil.getRandomInt(0, 1000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0000000000000000";
		}
	}
}
