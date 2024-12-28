package com.btc100.btc_q.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.btc100.btc_q.dao.UserDao;

@ContextConfiguration(locations = { "classpath*:/applicationContext-memcached.xml", "classpath*:/applicationContext.xml" })
public class RedisTemplateTest extends SpringContextTestCase{
	@Autowired  
	 private RedisTemplate redisTemplate;
	@Autowired
	private UserDao userDao;
	
	@Test
	public void normal() {

//		ValueOperations valueOps = redisTemplate.opsForValue();
//		HashOperations hashOps = redisTemplate.opsForHash();
//		ListOperations listOps2 = redisTemplate.opsForList();
//		SetOperations  setOps = redisTemplate.opsForSet();
//		ZSetOperations zsetOps = redisTemplate.opsForZSet();
//		
//		String key = "xxxxx";
//		valueOps.set(key, "aa", 60, TimeUnit.SECONDS);
//		
//		System.out.println(valueOps.get(key));
////		redisTemplate.expire(key, 15, TimeUnit.SECONDS);
//		
//		System.out.println(redisTemplate.hasKey(key));
		
//		userDao.get(clazz, id)
		
	}

}
