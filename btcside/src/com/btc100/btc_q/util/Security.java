package com.btc100.btc_q.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Security {

	/**
	 * MD5 16位加密
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5x16(String str) {
		String result = null;

		if (str == null || str.equals("")) {
			return result;
		}

		try {
			result = MD5x32(str);
			System.out.println("mdt 16bit: " + result.toString().substring(8, 24));
			System.out.println("md5 32bit: " + result.toString());
			result = result.substring(8, 24);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * MD5 32位加密
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5x32(String str) {
		String result = null;
		if (str == null || str.equals("")) {
			return result;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// result = buf.toString(); //md5 32bit
			// result = buf.toString().substring(8, 24))); //md5 16bit
			result = buf.toString().toUpperCase();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private static String des_mainkey = "b@$!t#@$c!@#41!@#40$@#0";
	public final static String des_registerkey = "b.tPcGq!r@e#g*i%s^t(e)~r";
	// private static String des_mainkey2 = "0@$!0#@$1!@#4c!@#4t$@#b";

	private final static String DES = "DES";

	// public static void main(String[] args) throws Exception {
	// String data = "123 456";
	// String key = "wang!@#$%";
	// System.err.println(encrypt(data, key));
	// System.err.println(decrypt(encrypt(data, key), key));
	//
	// }

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		if (key == null || key.equals("")) {
			key = des_mainkey;
		}
		if (key.length() % 8 > 0) {
			int len = 8 - key.length() % 8;
			for (int i = 0; i < len; i++) {
				key += "x";
			}
		}

		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String strs = new BASE64Encoder().encode(bt);
		return strs;
	}

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return 
	 * @throws Exception
	 */
	public static String encryptToHex(String data, String key) throws Exception {
		if (key == null || key.equals("")) {
			key = des_mainkey;
		}
		if (key.length() % 8 > 0) {
			int len = 8 - key.length() % 8;
			for (int i = 0; i < len; i++) {
				key += "x";
			}
		}

		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		return ConvertUtil.bytesToHexString(bt);
	}
	
	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) throws IOException, Exception {
		if (data == null)
			return "";
		try {
			if (key == null || key.equals("")) {
				key = des_mainkey;
			}
			if (key.length() % 8 > 0) {
				int len = 8 - key.length() % 8;
				for (int i = 0; i < len; i++) {
					key += "x";
				}
			}

			BASE64Decoder decoder = new BASE64Decoder();
			byte[] buf = decoder.decodeBuffer(data);
			byte[] bt = decrypt(buf, key.getBytes());
			return new String(bt);
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decryptByHex(String data, String key) throws IOException, Exception {
		if (data == null)
			return "";
		try {
			if (key == null || key.equals("")) {
				key = des_mainkey;
			}
			if (key.length() % 8 > 0) {
				int len = 8 - key.length() % 8;
				for (int i = 0; i < len; i++) {
					key += "x";
				}
			}

//			BASE64Decoder decoder = new BASE64Decoder();
//			byte[] buf = decoder.decodeBuffer(data);

			byte[] buf=ConvertUtil.hexStringToByte(data);
			byte[] bt = decrypt(buf, key.getBytes());
			return new String(bt);
		} catch (Exception ex) {
			return "";
		}
	}
	
	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}
}
