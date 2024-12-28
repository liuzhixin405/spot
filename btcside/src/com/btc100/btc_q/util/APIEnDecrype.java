package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class APIEnDecrype {

	public static String des_mainkey = "d!7*h&^k";

	/**
	 * 加密数据
	 * 
	 * @param encryptString
	 *            注意：这里的数据长度只能为8的倍数
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString) throws Exception {
		SecretKeySpec key = new SecretKeySpec(getKey(des_mainkey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] clearByte = encryptString.getBytes("utf-8");
		byte[] encryptedData = cipher.doFinal(clearByte);
		return ConvertUtil.bytesToHexString(encryptedData);
	}

	/**
	 * 加密数据
	 * 
	 * @param encryptString
	 *            注意：这里的数据长度只能为8的倍数
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString, String desckey) throws Exception {
		SecretKeySpec key = new SecretKeySpec(getKey(desckey), "DES");
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
	public static String decrypt(String decryptString) {
		SecretKeySpec key;
		try {
			key = new SecretKeySpec(getKey(des_mainkey), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte decryptedData[] = cipher.doFinal(ConvertUtil.hexStringToByte(decryptString));
			return new String(decryptedData, "utf-8");
		} catch (UnsupportedEncodingException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		
		return "";
	}
	
	/***
	 * 解密数据
	 * 
	 * @param decryptString
	 * @param desckey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String decryptString, String desckey) {
		SecretKeySpec key;
		try {
			key = new SecretKeySpec(getKey(desckey), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte decryptedData[] = cipher.doFinal(ConvertUtil.hexStringToByte(decryptString));
			return new String(decryptedData, "utf-8");
		} catch (UnsupportedEncodingException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		
		return "";
	}

	 /** 
     * 公钥解密 
     *  
     * @param data 
     *            待解密数据 
     * @param key 
     *            公钥 
     * @return byte[] 解密数据 
     * @throws Exception 
     */
	 public static byte[] decryptRSAByPublicKey(byte[] data, String base64Key)  
	            throws Exception {  
  
		byte[] key = Base64.decodeBase64(base64Key);
		 
        // 取得公钥  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);  
  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
  
        // 生成公钥  
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);  
  
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
  
        cipher.init(Cipher.DECRYPT_MODE, publicKey);  
  
        return cipher.doFinal(data);  
		 
//		 byte[] keyBytes = Base64Utils.decode(publicKey);
//	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//	        Key publicK = keyFactory.generatePublic(x509KeySpec);
//	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//	        cipher.init(Cipher.DECRYPT_MODE, publicK);
//	        int inputLen = encryptedData.length;
//	        ByteArrayOutputStream out = new ByteArrayOutputStream();
//	        int offSet = 0;
//	        byte[] cache;
//	        int i = 0;
//	        // 对数据分段解密
//	        while (inputLen - offSet > 0) {
//	            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
//	                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
//	            } else {
//	                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
//	            }
//	            out.write(cache, 0, cache.length);
//	            i++;
//	            offSet = i * MAX_DECRYPT_BLOCK;
//	        }
//	        byte[] decryptedData = out.toByteArray();
//	        out.close();
//	        return decryptedData;
    }
}
