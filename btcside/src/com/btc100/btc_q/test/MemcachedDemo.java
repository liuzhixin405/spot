/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.btc100.btc_q.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.btc100.btc_q.common.SpyMemcachedClient;

@ContextConfiguration(locations = { " classpath*:/applicationContext-memcached.xml", "classpath*:/applicationContext.xml" })
public class MemcachedDemo extends SpringContextTestCase {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Test
	public void normal() {

		String key = "consumer:1";
		String value = "admin";

		spyMemcachedClient.set(key, 60 * 60 * 1, value);

		String ret = spyMemcachedClient.get(key);
		System.out.println(ret);
		
		spyMemcachedClient.set(key, 60 * 60 * 1, "test");

		ret = spyMemcachedClient.get(key);
		System.out.println(ret);

//		String result = spyMemcachedClient.get(key);
//		assertThat(result).isEqualTo(value);
//
//		spyMemcachedClient.delete(key);
//		result = spyMemcachedClient.get(key);
//		assertThat(result).isNull();
	}

	//@Test
	public void safeDelete() {
		String key = "consumer:1";
		spyMemcachedClient.set(key, 60, "admin");
		assertThat(spyMemcachedClient.safeDelete(key)).isTrue();
		assertThat(spyMemcachedClient.safeDelete("consumer:1")).isFalse();
	}

	//@Test
	public void incr() {
		String key = "incr_key";

		// 注意,incr返回的数值使用long表达
		long result = spyMemcachedClient.incr(key, 2, 1);
		assertThat(result).isEqualTo(1);
		// 注意,get返回的数值使用字符串表达
		String ret = spyMemcachedClient.get(key);
		assertThat(ret).isEqualTo("1");

		assertThat(spyMemcachedClient.incr(key, 2, 1)).isEqualTo(3);
		ret = spyMemcachedClient.get(key);
		assertThat(ret).isEqualTo("3");

		key = "set_and_incr_key";
		// 注意,set中的数值必须使用字符串,后面的incr操作结果才会正确.
		spyMemcachedClient.set(key, 60 * 60 * 1, "1");
		assertThat(spyMemcachedClient.incr(key, 2, 1)).isEqualTo(3);

	}

	public SpyMemcachedClient getSpyMemcachedClient() {
		return spyMemcachedClient;
	}

	public void setSpyMemcachedClient(SpyMemcachedClient spyMemcachedClient) {
		this.spyMemcachedClient = spyMemcachedClient;
	}
	
	
}
