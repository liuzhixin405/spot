package com.btc100.btc_q.common;

import java.util.regex.*;

public class Regex {
	
	public static boolean IsMatch(String str,String regex){
		Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(str);
    	return matcher.matches();
	}
}
