package com.btc100.btc_q.common;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class BtcCache {

	private static CacheManager cacheManager;
	private static Cache cache;

	public BtcCache() {
		if (cacheManager == null)
			cacheManager = new CacheManager();

		if (cache == null)
			cache = cacheManager.getCache("btcCache");

		if (cache == null)
			cache = cacheManager.getCache("defaultCache");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BtcCache btcCache = new BtcCache();

		for (int i = 0; i < 11; i++) {
			btcCache.put("key" + i, "value" + i);
		}
		List<String> list = btcCache.ListCache();
		for (int j = 0; j < list.size(); j++) {
			System.out.print(list.get(j));
			System.out.println("=" + btcCache.get(list.get(j)));
		}
		btcCache.remove("aa");
		list = btcCache.ListCache();
		for (int j = 0; j < list.size(); j++) {
			System.out.print(list.get(j));
			System.out.println("=" + btcCache.get(list.get(j)));
		}
	}

	/**
	 * 从缓存中移除
	 * 
	 * @param key
	 */
	public void remove(String key) {
		cache.remove(key);
	}

	/**
	 * 数据存入缓存
	 * 
	 * @param String
	 *            key
	 * @param Object
	 *            value
	 */
	public void put(String key, Object value) {
		cache.put(new Element(key, value));
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param String
	 *            key
	 * @return Object
	 */
	public Object get(String key) {
		if (cache.get(key) != null) {
			return cache.get(key).getObjectValue();
		} else {
			return null;
		}
	}

	/**
	 * 返回当前缓存中的所有key
	 * 
	 * @return string[]
	 */
	@SuppressWarnings("unchecked")
	public List<String> ListCache() {
		return cache.getKeys();
	}

	/**
	 * 刷新缓存
	 */
	public void refresh() {
		cache.flush();
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cm) {
		cacheManager = cm;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache c) {
		cache = c;
	}
}
