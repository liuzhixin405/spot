package com.btc100.btc_q.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.TypeParse;

public class Utils {

	/**
	 * 将对象转换为Int32类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的int类型结果
	 */
	public static int StrToInt(Object Expression, int defValue) {
		if (Expression != null && Expression.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToInt(Expression, defValue);
	}

	/**
	 * 将对象转换为Int32类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的long类型结果
	 */
	public static long StrToLong(Object Expression, long defValue) {
		if (Expression != null && Expression.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToLong(Expression, defValue);
	}

	public static float StrToFloat(Object strValue, float defValue) {
		if (strValue != null && strValue.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToFloat(strValue, defValue);
	}

	public static double StrToDouble(Object strValue, double defValue) {
		if (strValue != null && strValue.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToDouble(strValue, defValue);
	}

	public static Date StrToDate(String date) {
		if (date != null && !date.equals("")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return null;
		}
		return null;
	}

	/**
	 * 根据毫秒获取最大天数
	 */
	public static int GetDays(double milliseconds,int BigDecimalType){
		milliseconds = milliseconds / (1000 * 60 * 60 * 24);
		BigDecimal bd = new BigDecimal(Double.toString(milliseconds));
		bd = bd.setScale(0, BigDecimalType);
		return Integer.parseInt(bd.toString());
	}
	
	/**
	 * 获取两个时间相差几天（取整：向上取整 或 向下取整）
	 * @param beginTime
	 * @param endTime
	 * @param bigDecimalType
	 * @return
	 */
	public static int GetDiffDays(Date beginTime,Date endTime,int bigDecimalType){
		double diff = endTime.getTime() - beginTime.getTime();
		diff = diff / (1000 * 60 * 60 * 24);
		BigDecimal bd = new BigDecimal(Double.toString(diff));
		bd = bd.setScale(0, bigDecimalType);
		return Integer.parseInt(bd.toString());
	}
	
	/**
	 * 获取两个日期相差几天几小时几分几秒
	 */
    public static String DateDifference(Date startDate, Date endDate){
 
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
 
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);
 
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
 
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
 
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
 
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
 
        long elapsedSeconds = different / secondsInMilli;
 
        return String.format("%d 天,%d 小时,%d 分钟,%d 秒", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }
	
    /**
	 * 计算利息
	 * 
	 * @param amount	本金
	 * @param rate		利率
	 * @param allDay	总借入天数（<=20）
	 * @param pastDay	已还款天数（<=19）
	 * @param sysSingleDay	系统定义的单倍计息天数
	 * @param sysDoubleDay	系统定义的双倍计息天数	
	 * @return 利息
	 */
    public static double CalculatingInterest(double amount, double rate, int allDay, int pastDay, int sysSingleDay,
			int sysAllDay) {
		double interest = 0;

		int sysDoubleDay = sysAllDay - sysSingleDay;
		if (sysDoubleDay < 0) {
			sysDoubleDay = 0;
		}

		if (allDay > sysAllDay)
			allDay = sysAllDay;

		// 需计息天数
		int beDay = allDay - pastDay;
		// 单倍计息天数
		int singleDay = 0;
		// 双倍计息天数
		int doubleDay = 0;
		
//			1	+	2 = 3 <= 15
//			3	+	1 = 4 <= 15
//			10	+	5 = 15 <= 15
		if(pastDay + beDay <= sysSingleDay){
//				2	=	2
//				1 	=	1
//				5	= 	5
			singleDay = beDay;
//				0	=	0
			doubleDay = 0;
		}
		else{
			
//			15	+	3 = 16 > 15
//			10	+	6 = 16 > 15
//			2	+	14 = 16 > 15
			
//				15		>=	15
//				10		<	15
//				2		<	15
			if(pastDay >= sysSingleDay){
				singleDay = 0;
				doubleDay = beDay;
			}
			else{
//					1	=	6	-	(15 - 10)
//					1	=	14	-	(15 - 2)
				doubleDay = beDay - (sysSingleDay - pastDay);
//					5	=	6	-	1
//					13	= 	14	-	1
				singleDay = beDay - doubleDay;
			}
		}
			
//		// 如果已还款天数大于0，计算出需要计息的总天数 和 需要计息的单倍天数 和 双倍天数
//		if (pastDay > 0 && pastDay >= sysSingleDay) {
//			singleDay = 0;
//			doubleDay = beDay;
//		} else {
//			if(beDay > sysSingleDay)
//				singleDay = sysSingleDay - (beDay - pastDay);
//			doubleDay = (beDay - singleDay) < 0 ? 0 : (beDay - singleDay);
//		}

		// 利息 = (本金 * 利率 * 单倍计息天数) + (本金 * 利率 * 双倍计息天数 * 2)
		interest = (amount * rate * singleDay) + (amount * rate * doubleDay * 2);

		// 利息 只保留6位小数（第7位小数四舍五入）
		BigDecimal bDecimal = new BigDecimal(Double.toString(interest));
		bDecimal = bDecimal.setScale(6, BigDecimal.ROUND_HALF_UP);
		interest = bDecimal.doubleValue();

		return interest;
	}
    
    public static int getAmountDigtis(String code) {
		return 2;
    }
    
    public static int getDigitsFromMinFloat(BigDecimal flot) {
    	if (flot == null) return 2;
    	
    	if (flot.compareTo(BigDecimal.ONE) >= 0) {
    		return 0;
    	} else if (flot.compareTo(new BigDecimal(0.1)) >= 0 && flot.compareTo(new BigDecimal(1)) < 0) {
    		return 1;
    	} else if (flot.compareTo(new BigDecimal(0.0099999999)) > 0 && flot.compareTo(new BigDecimal(0.1)) < 0) {
    		return 2;
    	} else if (flot.compareTo(new BigDecimal(0.00099999999)) > 0 && flot.compareTo(new BigDecimal(0.01)) < 0) {
    		return 3;
    	} else if (flot.compareTo(new BigDecimal(0.000099999999)) > 0 && flot.compareTo(new BigDecimal(0.001)) < 0) {
    		return 4;
    	} else if (flot.compareTo(new BigDecimal(0.0000099999999)) > 0 && flot.compareTo(new BigDecimal(0.0001)) < 0) {
    		return 5;
    	} else if (flot.compareTo(new BigDecimal(0.00000099999999)) > 0 && flot.compareTo(new BigDecimal(0.00001)) < 0) {
    		return 6;
    	} else if (flot.compareTo(new BigDecimal(0.000000099999999)) > 0 && flot.compareTo(new BigDecimal(0.000001)) < 0) {
    		return 7;
    	} else if (flot.compareTo(new BigDecimal(0.0000000099999999)) > 0 && flot.compareTo(new BigDecimal(0.0000001)) < 0) {
    		return 8;
    	} else if (flot.compareTo(BigDecimal.ZERO) > 0 && flot.compareTo(new BigDecimal(0.00000001)) < 0) {
    		return 9;
    	}   
    	
    	return 2;
    }
    
	/**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    public static String getCountryId(HttpServletRequest request) {
    	String countryID = "CN";
	    String sip = BaseAction.getIpAddr(request);
	    if (sip.startsWith("100.")) return countryID;
	    
		try {
			String sresult = HttpToServer.sendGetForHttp("http://ip.taobao.com/service/getIpInfo.php", "ip=" + sip, 2000, 5000);
			JSONObject jo = new JSONObject(sresult);
			int icode = ConvertUtil.toInt(jo.get("code"), 1);
			if (icode == 0) {
				JSONObject jodata = (JSONObject) jo.get("data");
				countryID = jodata.get("country_id").toString();
				String country = jodata.getString("country").toString();
				if (countryID.equals("xx") || (countryID.equals("IANA") && "未分配或者内网IP".equals(country))) {
					return "CN";
				}
			}
		} catch (Exception e) {
		}
		
		return countryID;
    }
    
    public static String getWebRoot(HttpServletRequest request) {
    	String scheme = request.getScheme();
		String scheme1 = request.getHeader("X-Forwarded-Proto");
		if (scheme1 != null && scheme1.equals("https")) {
			scheme = "https";
		}
		String port = "";
		if (scheme.equals("http")) {
			port = ":" + request.getServerPort();
		}
		String basePath = scheme + "://" + request.getServerName() + port + "/";
		return basePath;
    }
    
    public static Locale getLocale(String language) {
    	return new Locale(language);
    }
    
    public static Locale getLocale(HttpServletRequest request) {
    	Locale locale = (Locale) request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if (null == locale) {
			String lang = CookieHelper.getCookieValue(request, "language"); 
			if (!StringUtil.isEmpty(lang)) {
				String[] langs = lang.split("_");
				if (langs.length == 2) {
					locale = new Locale(langs[0], langs[1]);
				}
			}
			
			if (locale == null) {
				locale = new Locale("en", "US");
			}
			
			request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
		}
		
		return locale;
    	
//    	if ("CN".equals(getCountryId(request))) {
//    		return new Locale("zh", "CN");
//    	} else {
//    		return new Locale("en", "US");
//    	}
    }
}
