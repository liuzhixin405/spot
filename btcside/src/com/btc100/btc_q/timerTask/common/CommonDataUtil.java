package com.btc100.btc_q.timerTask.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonDataUtil {

	/**
	 * 获取当前时间前一小时的毫秒数
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static String getOneHoursAgoTimeSecond() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
//			BigDecimal bd = new BigDecimal();
//			bd = bd.setScale(0, BigDecimal.ROUND_DOWN);
			return String.valueOf(df.parse(getOneHoursAgoTime()).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取当前时间前一小时
	 * 
	 * @return
	 */
	private static String getOneHoursAgoTime() {
		String oneHoursAgoTime = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 1); // 把时间设置为当前时间-1小时，同理，也可以设置其他时间
		oneHoursAgoTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());// 获取到完整的时间
		return oneHoursAgoTime;
	}

	/**
	 * 比较两个时间戳的大小
	 * 
	 * @param date1
	 *            后一个时间戳
	 * @param date2
	 *            前一个时间戳
	 * @return
	 * @throws ParseException
	 */
	public static boolean compare_date(long date1, long date2) throws ParseException {
		return new Date(date1 * 1000).after(new Date(date2 * 1000));
	}
	/**
	 * 时间param是不是比beCompared晚，晚就返回true,早就返回false
	 * @param param
	 * @param beCompared
	 * @return
	 * @throws ParseException
	 */
	public static boolean after(long param, long beCompared){
		return param>beCompared;
		
	}
	
	
	
	/**
	 * 获取当前日期格式yyMM
	 * @return
	 */
	public static String getYearMonth(){
		DateFormat sdf = new SimpleDateFormat("yyMM");
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		cl.add(Calendar.MONTH, 0);
		return sdf.format(cl.getTime());
	}
	/**
	 * 获取当前日期格式MMdd
	 * @return
	 */
	public static String getMonthDay(){
		DateFormat sdf = new SimpleDateFormat("MMdd");
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		cl.add(Calendar.MONTH, 0);
		return sdf.format(cl.getTime());
	}
	
	public static void main(String[] args) throws ParseException {
		long date = 1407295791;
		long dad = 1407291187;
		System.out.println(compare_date(date, dad));
		System.out.println(getYearMonth());
	}
	
}
