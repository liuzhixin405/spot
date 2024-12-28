package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESEnDecrype {

	public static String des_mainkey = "b^xu87@y";

	/**
	 * 加密数据
	 * 
	 * @param encryptString
	 *            注意：这里的数据长度只能为8的倍数
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString, String encryptKey) throws Exception {
		// 解密key
		encryptKey = decrypt(encryptKey, des_mainkey);

		SecretKeySpec key = new SecretKeySpec(getKey(encryptKey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] clearByte = encryptString.getBytes("utf-8");

		byte[] encryptedData = cipher.doFinal(clearByte);
		return ConvertUtil.bytesToHexString(encryptedData);

	}

	/**
	 * 自定义一个key
	 * 
	 * @param string
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] getKey(String keyRule) throws UnsupportedEncodingException {
		Key key = null;
		byte[] keyByte = keyRule.getBytes("utf-8");
		// 创建一个空的八位数组,默认情况下为0
		byte[] byteTemp = new byte[8];
		// 将用户指定的规则转换成八位数组
		for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
			byteTemp[i] = keyByte[i];
		}
		key = new SecretKeySpec(byteTemp, "DES");
		return key.getEncoded();
	}

	/***
	 * 解密数据
	 * 
	 * @param decryptString
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String decryptString, String decryptKey) throws Exception {
		SecretKeySpec key = new SecretKeySpec(getKey(decryptKey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decryptedData[] = cipher.doFinal(ConvertUtil.hexStringToByte(decryptString));
		return new String(decryptedData, "utf-8");
	}

	/**
	 * 通过加密后的秘钥解密
	 * */
	public static String decryptDES(String decryptString, String decryptKey) throws Exception {
		// 用主秘钥解密Key
		decryptKey = decrypt(decryptKey, des_mainkey);
		// 再用解密后的key进行解密
		SecretKeySpec key = new SecretKeySpec(getKey(decryptKey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decryptedData[] = cipher.doFinal(ConvertUtil.hexStringToByte(decryptString));
		return new String(decryptedData, "utf-8");
	}

}
