package com.btc100.btc_q.util;

import java.util.Comparator;

public class MapKeyComparator implements Comparator<String> {

	@Override
	public int compare(String t1, String t2) {
		return t1.compareTo(t2);
	}

}
