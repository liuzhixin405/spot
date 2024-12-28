package com.btc100.btc_q.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Back15day {

	public static String day15() {
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(d.getTime() - 15 * 24 * 60 * 60 * 1000));
	}
	public static String day20() {
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(d.getTime() - 20 * 24 * 60 * 60 * 1000));
	}
	public static String daymax() {
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(d.getTime() - 20 * 24 * 60 * 60 * 1000));
	}
	
	public static String daymin() {
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(d.getTime() - 15 * 24 * 60 * 60 * 1000));

	}
	public static void main(String[] args) {
		day15();
	}
}
