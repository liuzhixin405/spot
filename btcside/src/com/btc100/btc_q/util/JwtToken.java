package com.btc100.btc_q.util;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.btc100.btc_q.common.UrlConfig;

/**
 * Token的生成和解析
 * 
 */
public class JwtToken {

	private static final Logger logger = Logger.getLogger(JwtToken.class);

	/** jwt的token秘钥 */
	public static final String SECRET = UrlConfig.getValue("jwt.secret");
	/** token过期时间，单位分钟 */
	public static final String EXPIRES_MINUTES = UrlConfig.getValue("jwt.expiresMinutes");
	/** token创建的有效过期时间，单位分钟 */
	public static final String CREATE_EXPIRES_MINUTES = UrlConfig.getValue("jwt.create.expiresMinutes");
	/** token即将过期时间，单位分钟 */
	public static final String PRE_EXPIRES_MINUTES = UrlConfig.getValue("jwt.pre.expiresMinutes");

	private static String getKey() {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, "HmacSHA256");
		return signingKey.toString();
	}

	/**
	 * JWT生成Token
	 * 
	 * JWT构成: header, payload, signature
	 * 
	 * @param userId
	 *            登录成功后用户userId
	 * @param userName
	 *            登录成功后用户userName
	 */
	public static String createToken(Long userId, String userName) {
		Date iatDate = new Date();

		// header Map
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");

		// build token
		String token = null;
		try {
			token = JWT.create().withHeader(map) // header
					.withClaim("iss", "Service") // payload
					.withClaim("userId", null == userId ? null : userId.toString())
					.withClaim("userName", null == userName ? null : userName)
					.withClaim("iatDate", iatDate)
					.withIssuedAt(iatDate) // sign time
					.sign(Algorithm.HMAC256(getKey())); // signature
		} catch (Exception e) {
			logger.error("token 创建异常,异常在： " + e);
		}

		return token;
	}

	/**
	 * 解密Token
	 * 
	 * @param token
	 * @return Map<String, Claim>
	 */
	public static Map<String, Claim> decryptToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(getKey())).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			// token 校验失败, 抛出Token验证非法异常
			logger.error("token解密失败,异常在： " + e);
			return null;
		}
		return jwt.getClaims();
	}

	/**
	 * 验证token是否过期，没过期返回最新token
	 * 
	 * @param token
	 * @return token
	 */
	public static String verifyToken(String token, long expiresMinutes) {
		Map<String, Claim> claims = decryptToken(token);
		if (claims == null) {
			return null;
		}
		if (!claims.containsKey("iatDate") || !claims.containsKey("userId") || !claims.containsKey("userName")) {
			return null;
		}

		Date iatDate = claims.get("iatDate").asDate();
		Calendar nowTime = Calendar.getInstance();
		long interval = nowTime.getTimeInMillis() - iatDate.getTime();
		long expires = expiresMinutes * 60000; // Long.valueOf(EXPIRES_MINUTES) * 60000;
		if (interval > expires) {
			logger.info("token已经过期");
			return null;
		}
		
//		long preExpires = expires - Long.valueOf(PRE_EXPIRES_MINUTES) * 60000;
//		if (interval <= expires && interval >= preExpires) {
//			String userId = claims.get("userId").asString();
//			String userName = claims.get("userName").asString();
//			return JwtToken.createToken(Long.valueOf(userId), userName);
//		}

		return token;
	}
	
	public static String verifyToken(String token) {
		return verifyToken(token, Long.valueOf(EXPIRES_MINUTES));
	}
	
	/**
	 * 验证token是否在三天内
	 * 
	 * @param token
	 * @return boolean
	 */
	public static boolean checkTimeAndName(String token, String userName) {
		Map<String, Claim> claims = decryptToken(token);
		if (claims == null) {
			return false;
		}
		if (!claims.containsKey("iatDate") || !claims.containsKey("userId") || !claims.containsKey("userName")) {
			return false;
		}

		String tokenUserName = claims.get("userName").asString();
		if (!tokenUserName.equals(userName)) {
			return false;
		}
		
		Date iatDate = claims.get("iatDate").asDate();
		Calendar nowTime = Calendar.getInstance();
		long interval = nowTime.getTimeInMillis() - iatDate.getTime();
		if (interval > Long.valueOf(CREATE_EXPIRES_MINUTES) * 60000) {
			logger.info("token创建时间超过3天");
			return false;
		}
		
		return true;
	}

	public static boolean checkToken(String token) {
		Map<String, Claim> claims = decryptToken(token);
		if (claims == null) {
			return false;
		}
		if (!claims.containsKey("iatDate") || !claims.containsKey("userId") || !claims.containsKey("userName")) {
			return false;
		}

		Date iatDate = claims.get("iatDate").asDate();
		Calendar nowTime = Calendar.getInstance();
		long interval = nowTime.getTimeInMillis() - iatDate.getTime();
		long expires = Long.valueOf(EXPIRES_MINUTES) * 60000;
		if (interval > expires) {
			return false;
		}
		
		return true;
	}

	/**
	 * 根据Token获取userId
	 * 
	 * @param token
	 * @return userId
	 */
	public static Long getUserId(String token) {
		Map<String, Claim> claims = decryptToken(token);
		if (claims == null) {
			return 0l;
		}
		if (!claims.containsKey("iatDate") || !claims.containsKey("userId") || !claims.containsKey("userName")) {
			return 0l;
		}
		String userId = claims.get("userId").asString();

		return Long.valueOf(userId);
	}

}