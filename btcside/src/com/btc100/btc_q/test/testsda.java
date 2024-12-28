package com.btc100.btc_q.test;

import java.util.ArrayList;
import java.util.List;

import com.btc100.btc_q.entity.BQWalletQueueBtc;
import com.google.gson.Gson;

public class testsda {

	public static void main(String[] args) {
		String sql = "INSERT INTO `bq_wallet_queue_btc` (`wallet_address`, `wallet_pic`) VALUES ";
		
		List<BQWalletQueueBtc> list = new ArrayList<BQWalletQueueBtc>();
		BQWalletQueueBtc e = new BQWalletQueueBtc();
		e.setWallet_address("11");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("12");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("13");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("14");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("15");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("16");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("17");
		e.setWallet_pic("543");
		list.add(e);
		e = new BQWalletQueueBtc();
		e.setWallet_address("18");
		e.setWallet_pic("543");
		list.add(e);
		Gson g = new Gson();
		
		System.out.println(g.toJson(list));
		
//		int icount = 0;
		StringBuilder sbBuilder = new StringBuilder();
		
//		for (com.btc100.btc_q.entity.BQWalletQueueBtc bq : list) {
//			sbBuilder.append("('" + bq.getWallet_address() + "','" + bq.getWallet_pic() + "'),");
//			icount++;
//	
//			String str = sbBuilder.toString();
//			if (icount % 3 == 1) {
//				if (str.length() > 0) {
//					if (str.endsWith(",")) {
//						str = str.substring(0, str.length() - 1);
//					}
//					sbBuilder = new StringBuilder();
//					System.out.println(sql+str);
//				}
//			}
//		}
		
		for (int i = 1; i < list.size() + 1; i++) {
			BQWalletQueueBtc bq = list.get(i - 1);
			sbBuilder.append("('" + bq.getWallet_address() + "','" + bq.getWallet_pic() + "'),");
	
			String str = sbBuilder.toString();
			if (i % 3 == 0) {
				if (str.length() > 0) {
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
					}
					sbBuilder = new StringBuilder();
					System.out.println(sql+str);
				}
			} else if (i > list.size() - 3) {
				if (str.length() > 0) {
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
					}
					sbBuilder = new StringBuilder();
					System.out.println(sql+str);
				}
			}
			// 最后的3条没出现
		}
		
	}
	
	
	
	
	
	
	
	

	
	
}
