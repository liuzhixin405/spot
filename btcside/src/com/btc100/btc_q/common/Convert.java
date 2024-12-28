package com.btc100.btc_q.common;

import java.util.Date;

public class Convert {
	public static double toDouble(String strValue, double defaultValue) {
		try {
			return new Double(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static long toLong(String strValue, long defaultValue) {
		try {
			return new Long(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static int toInt(String strValue, int defaultValue) {
		try {
			return new Integer(strValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long datePoorToDays(Date maxDate, Date minDate) {
		long diff = maxDate.getDay() - minDate.getDay();
		return diff;
	}

	public static int datePoorToYears(Date maxDate, Date minDate) {
		int iYear0 = maxDate.getYear() - minDate.getYear();
		int iMonth0 = maxDate.getMonth() - minDate.getMonth();
		if (iMonth0 < 0) {
			return iYear0 - 1;
		} else if (iMonth0 > 0) {
			return iYear0;
		}

		int iDay0 = maxDate.getDay() - minDate.getDay();
		if (iDay0 >= 0) {
			return iYear0;
		} else {
			return iYear0 - 1;
		}
	}

}
