package com.btc100.btc_q.common;


public class DBCache {
	
	private static BtcCache mbtcCache;
	
	public static BtcCache getInsance(){
		if(mbtcCache==null){
			mbtcCache=new BtcCache();
		}
		
		return mbtcCache;
	}
	
	public static void Put(){
		
	}
	
	
	
}
