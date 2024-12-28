package com.btc100.btc_q.util;

import java.util.Date;

public class BackDate {

	
	public static String BDate(Date a){
		try {
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = new Date();
			//format.format(d);// 今天的日期
			java.util.Date beginDate = format.parse(format.format(a));//数据库日期
			java.util.Date endDate = format.parse(format.format(d));
			long day = (endDate.getTime() - beginDate.getTime())/(24 * 60 * 60 * 1000)+1;
			String Stringday = Long.toString(day); 
			return Stringday;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
