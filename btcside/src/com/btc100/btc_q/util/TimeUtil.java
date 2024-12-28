package com.btc100.btc_q.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {

	/**
	 * 获取天数
	 * 
	 * @param startTime
	 *            起始日期
	 * @param endTime
	 *            截止日期
	 * @param format
	 *            时间格式
	 * @return 天数
	 * @throws ParseException
	 */
	private static long handleLoanedDayTime(String startTime, String endTime, String format) throws ParseException {
		// 按照传入的格式生成一个simpledateformate对象
		DateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数

		// 获得两个时间的毫秒时间差异
		long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff / nd;// 计算差多少天
		long hour = diff % nd / nh;// 计算差多少小时
		long min = diff % nd % nh / nm;// 计算差多少分钟
		long sec = diff % nd % nh % nm / ns;// 计算差多少秒
		// 输出结果
		// System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" +
		// sec + "秒。");

		if (hour != 0 || min != 0 || sec != 0) {
			day = day + 1;
		}

		return day;
	}

	/**
	 * 获取天数
	 * 
	 * @param startTime
	 *            起始日期
	 * @param endTime
	 *            截止日期
	 * @return 天数
	 * @throws ParseException
	 */
	public static int getRepayDays(Date fromtime, Date totime) throws ParseException {
		String fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fromtime); // 借款时间
		String toTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(totime); // 借款时间
		long timeDay = 0; // 借贷天数
		timeDay = handleLoanedDayTime(fromTime, toTime, "yyyy-MM-dd HH:mm:ss");
		return (int) timeDay;
	}

	/**
	 * 比较date是否在startTime和endTime之间，格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 *            目前时间(Date)
	 * @param startTime
	 *            起始时间(String)
	 * @param endTime
	 *            结束时间(String)
	 * @return
	 * @throws ParseException
	 */
	public static boolean compareDate(Date date, String startTime, String endTime) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fromTime = df.parse(startTime); // 开始时间
		Date toTime = df.parse(endTime); // 截止时间
		return (date.after(fromTime) && date.before(toTime));
	}

	/**
	 * 获取当天时间的前24小时时间（时间戳）
	 * 
	 * @return
	 */
	public static long getYesterdayTime() {
		BigDecimal bd = new BigDecimal(new Date().getTime() / 1000 - 86400);
		bd = bd.setScale(0, BigDecimal.ROUND_DOWN);
		return Long.parseLong(bd.toString());
	}

	/**
	 * 获取当天0点时间（时间戳）
	 * 
	 * @return
	 */
	public static int getTimesmorning() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 获取当天0点时间（时间戳）
	 * 
	 * @return
	 */
	public static int getTimesmorning(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date(time));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 当天的日期
	 * 
	 * @param pattern
	 *            格式
	 * @return 返回对应pattern的日期格式
	 */
	public static String getToday(String pattern) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date());
		Date last = cal.getTime();
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(last);
	}
	
	/**
	 * 当周的周几
	 * 
	 * @param week
	 *            周几
	 * @param pattern
	 *            格式
	 * @return 返回对应pattern的日期格式
	 */
	public static String getDayOfWeek(int week, String pattern) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + week - 1);
		Date last = cal.getTime();
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(last);
	}
	
	/**
	 * 当月最后一个周五
	 * 
	 * @param pattern
	 *            格式
	 * @return 返回对应pattern的日期格式
	 */
	public static String getLastFridayOfMonth(String pattern) {
		return getLastFridayOfYearAndMonth(null , null, pattern);
	}
	
	/**
	 * 当前日期开始的第number季度最后一个周五
	 * 
	 * @param number
	 *            季度，1代表当季度，2代表下个季度，以此类推
	 * @param pattern
	 *            格式
	 * @return 返回对应pattern的日期格式
	 */
	public static String getLastFridayOfQuarter(int number, String pattern) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		int month = 3 * number - 1;

		if (month > 12) {
			int year = month / 12;
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + year);
			month = month % 12;
		}
		return getLastFridayOfYearAndMonth(cal.get(Calendar.YEAR), month, pattern);
	}
	
	/**
	 * 指定年月的月最后一个周五
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param pattern
	 *            格式
	 * @return 返回对应pattern的日期格式
	 */
	public static String getLastFridayOfYearAndMonth(Integer year, Integer month, String pattern) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		if (year == null) {
			year = cal.get(Calendar.YEAR);
		}
		if (month == null) {
			month = cal.get(Calendar.MONTH);
		}
		cal.set(year, month, 1);
		cal.roll(Calendar.DATE, -1);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		switch (weekDay) {
		case 7:
			cal.add(Calendar.DATE, -1);
			break;
		case 1:
			cal.add(Calendar.DATE, -2);
			break;
		case 2:
			cal.add(Calendar.DATE, -3);
			break;
		case 3:
			cal.add(Calendar.DATE, -4);
			break;
		case 4:
			cal.add(Calendar.DATE, -5);
			break;
		case 5:
			cal.add(Calendar.DATE, -6);
			break;
		case 6:
			/*calendar.add(Calendar.DATE, -7);*/
			break;
		}
	
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(cal.getTime());
	}

	/**
	 * 获取当天0点时间（时间戳）
	 * 
	 * @return
	 */
	public static int getTimesmorning(int itime) {
		try {
			// Calendar ca = Calendar.getInstance();
			// int year = ca.get(Calendar.YEAR); // 获取年份
			// int month = ca.get(Calendar.MONTH) + 1; // 获取月份
			// int day = ca.get(Calendar.DATE); // 获取日
			//
			// String dateString = year + "-" + month + "-" + day + " 00:00:00";
			// return DateUtil.DateStrToUnix(dateString,"yyyy-MM-dd");

			Date datetime = DateUtil.UnixToDate(itime);
			datetime.setHours(0);
			datetime.setMinutes(0);
			datetime.setSeconds(0);

			return (int) DateUtil.DateToUnix(datetime);
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 获取当天0点时间（时间戳）
	 * 
	 * @return
	 */
	public static int getTimesmorning(Date datetime) {
		try {
			datetime.setHours(0);
			datetime.setMinutes(0);
			datetime.setSeconds(0);
			
			return (int) DateUtil.DateToUnix(datetime);
		} catch (Exception ex) {
			return 0;
		}
	}

	public static long FromMonth() {
		Calendar nowDate = new java.util.GregorianCalendar();
		nowDate.set(Calendar.HOUR_OF_DAY, 0);
		nowDate.set(Calendar.MINUTE, 0);
		nowDate.set(Calendar.SECOND, 0);
		nowDate.set(Calendar.MILLISECOND, 0);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		return nowDate.getTimeInMillis() / 1000;
	}

	public static long ToMonth() {
		Calendar nowDate = new java.util.GregorianCalendar();
		nowDate.set(Calendar.HOUR_OF_DAY, 0);
		nowDate.set(Calendar.MINUTE, 0);
		nowDate.set(Calendar.SECOND, 0);
		nowDate.set(Calendar.MILLISECOND, 0);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		nowDate.add(Calendar.MONTH, 1);
		return nowDate.getTimeInMillis() / 1000;
	}

	public static long FromWeek() {
		Calendar nowDate = new java.util.GregorianCalendar();
		nowDate.set(Calendar.HOUR_OF_DAY, 0);
		nowDate.set(Calendar.MINUTE, 0);
		nowDate.set(Calendar.SECOND, 0);
		nowDate.set(Calendar.MILLISECOND, 0);
		nowDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return nowDate.getTimeInMillis() / 1000;
	}

	public static long ToWeek() {
		Calendar nowDate = new java.util.GregorianCalendar();
		nowDate.set(Calendar.HOUR_OF_DAY, 0);
		nowDate.set(Calendar.MINUTE, 0);
		nowDate.set(Calendar.SECOND, 0);
		nowDate.set(Calendar.MILLISECOND, 0);
		nowDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		nowDate.add(Calendar.WEEK_OF_MONTH, 1);
		return nowDate.getTimeInMillis() / 1000;
	}
	
	/**
	 * 根据时间戳获取当周周一0点时刻
	 * @param time 时间戳，秒级
	 * @return
	 */
	public static long getMonday(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date(time * 1000l));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		
		return (long) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 根据时间戳获取当周周末末0点时刻
	 * @param time 时间戳，秒级
	 * @return
	 */
	public static long getWeekend(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date(time * 1000l));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, 6);
		
		return (long) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 根据时间戳获取该月月初当天0点
	 * @param time 时间戳，秒级
	 * @return
	 */
	public static long getMonthFirstDay(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date(time * 1000l));
		final int first = 1;
		cal.set(Calendar.DAY_OF_MONTH, first);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (long) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 根据时间戳获取该月月末当天0点
	 * @param time 时间戳，秒级
	 * @return
	 */
	public static long getMonthLastDay(long time) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.setTime(new Date(time * 1000l));
		final int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (long) (cal.getTimeInMillis() / 1000);
	}
	
	/**
	 * 本周的日期
	 * 
	 * @param n
	 *            周几
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static String getWeekDate(int n, String pattern) {
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(new Date());

		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + n - 1);
		Date last = cal.getTime();
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(last);
	}

	/**
	 * 本月最后一个周的周六
	 * 
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static String getMonthDate(String pattern) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		int lastDay = cal.get(Calendar.DAY_OF_WEEK);

		if (lastDay != Calendar.SATURDAY) {
			cal.add(Calendar.DAY_OF_MONTH, -lastDay);
		}
		Date last = cal.getTime();
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(last);
	}

	/**
	 * 获取当前的年月格式为yyMM
	 * 
	 * @return
	 */
	public static String getYearMonth() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2) + String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
	}

	/**
	 * 判断该日期是否是周六日
	 * 
	 * @param time
	 *            时间戳
	 * @return
	 * @throws ParseException
	 */
	public static boolean boleanSatudaySunday(long time) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.UnixToDate(time));
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断year年month月的最后一天的日期
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getlastdayforyearmonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 判断month月day日的日期
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getlastdayformonthday(int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	public static void main(String[] args) {

		// System.out.println(DateUtil.dateToStr(getlastdayformonthday(11, 28),
		// "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(getTimesmorning(1435544696));// DateUtil.dateToStr(getlastdayformonthday(11,
													// 28),
													// "yyyy-MM-dd HH:mm:ss"));
//		System.out.println("today" + getTimesmorning());
		System.out.println("星期五:" + getDayOfWeek(5, "yyyyMMdd"));
//		System.out.println("today:" + getToday("yyyyMMdd"));
//		System.out.println("本月最后个星期五:" + getLastFridayOfMonth("yyyyMMdd"));
//		System.out.println("2020年一季度:" + getLastFridayOfQuarter(1, "yyyyMMdd"));
//		System.out.println("2020年四季度:" + getLastFridayOfQuarter(4, "yyyyMMdd"));
		System.out.println("2021年一季度:" + getLastFridayOfQuarter(5, "yyyyMMdd"));
		System.out.println("2022年一季度:" + getLastFridayOfQuarter(9, "yyyyMMdd"));
	}
}
