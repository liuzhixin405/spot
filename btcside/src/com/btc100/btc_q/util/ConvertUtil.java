package com.btc100.btc_q.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 转码 MD5工具类
 */
public final class ConvertUtil {

	public final static char[] BToA = "0123456789abcdef".toCharArray();

	private ConvertUtil() {

	}

	/**
	 * 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static String toTrim(String str, String match) {
		if (str == null || str.length() <= 0)
			return "";
		if (str.endsWith(match)) {
			str = str.substring(0, str.length() - match.length());
		}

		return str;
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 把字节数组转换为对象
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	/**
	 * 把可序列化对象转换成字节数组
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final byte[] objectToBytes(Serializable s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream ot = new ObjectOutputStream(out);
		ot.writeObject(s);
		ot.flush();
		ot.close();
		return out.toByteArray();
	}

	public static final String objectToHexString(Serializable s) throws IOException {
		return bytesToHexString(objectToBytes(s));
	}

	public static final Object hexStringToObject(String hex) throws IOException, ClassNotFoundException {
		return bytesToObject(hexStringToByte(hex));
	}

	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
	}

	/**
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static String BCD2ASC(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			int h = ((bytes[i] & 0xf0) >>> 4);
			int l = (bytes[i] & 0x0f);
			temp.append(BToA[h]).append(BToA[l]);
		}
		return temp.toString();
	}

	/**
	 * 两字符数组异或
	 */
	public static byte[] byteArrXor(byte[] arr1, byte[] arr2, int len) {
		byte[] dest = new byte[len];

		if ((arr1.length < len) || (arr2.length < len)) {
			return null;
		}

		for (int i = 0; i < len; i++) {
			dest[i] = (byte) (arr1[i] ^ arr2[i]);
		}

		return dest;
	}

	/**
	 * MD5加密字符串，返回加密后的16进制字符串
	 * 
	 * @param origin
	 * @return
	 */
	public static String MD5EncodeToHex(String origin) {
		return bytesToHexString(MD5Encode(origin));
	}

	/**
	 * MD5加密字符串，返回加密后的字节数组
	 * 
	 * @param origin
	 * @return
	 */
	public static byte[] MD5Encode(String origin) {
		return MD5Encode(origin.getBytes());
	}

	/**
	 * MD5加密字节数组，返回加密后的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] MD5Encode(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}

	}

	/**
	 * 将"ISO-8859-1"转码成"GB2312"
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeStr(String str) {
		String s1 = "";
		try {
			s1 = new String(str.getBytes("ISO-8859-1"), "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s1;
	}

	public static double Format2Byte(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
		// return ((double)((int)(dvalue*100)))/100.0;
	}

	public static double Format3Byte(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(3, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
		// return ((double)((int)(dvalue*1000)))/1000.0;
	}

	public static double Format4Byte(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(4, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
		// return ((double)((int)(dvalue*10000)))/10000.0;
	}

	public static double Format5Byte(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(5, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
		// return ((double)((int)(dvalue*10000)))/10000.0;
	}

	public static double Format6Byte(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(6, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
		// return ((double)((int)(dvalue*1000000)))/1000000.0;
	}

	public static double Format6Half(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(6, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static double Format7Half(double dvalue) {
		BigDecimal bd = new BigDecimal(Double.toString(dvalue));
		bd = bd.setScale(7, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static Long toLong(Object o, Long defaultValue) {
		if (o == null)
			return defaultValue;

		Long iValue = 0l;
		try {
			iValue = Long.parseLong(o.toString());
		} catch (Exception e) {
			iValue = defaultValue;
		}

		return iValue;
	}

	public static int toInt(Object o, int defaultValue) {
		if (o == null)
			return defaultValue;

		int iValue = 0;
		try {
			iValue = Integer.parseInt(o.toString());
		} catch (Exception e) {
			iValue = defaultValue;
		}

		return iValue;
	}

	public static double toDouble(Object o, double defaultValue) {
		if (o == null)
			return defaultValue;

		double iValue = 0;
		try {
			iValue = Double.parseDouble(o.toString());
		} catch (Exception e) {
			iValue = defaultValue;
		}

		return iValue;
	}

	public String convert(String utfString) {
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;

		while ((i = utfString.indexOf("\\u", pos)) != -1) {
			sb.append(utfString.substring(pos, i));
			if (i + 5 < utfString.length()) {
				pos = i + 6;
				sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
			}
		}

		return sb.toString();
	}
	
	 private static final String NUMBER_PATTERN = "^[0-9]+(.[0-9]{0,2})?$";// 判断小数点后两位的数字的正则
	 /**
	  * 验证是不是数字(验证到小数点后一位)
	  *
	  * @param number
	  * @return
	  */
	 public static boolean isDecimalNumber(String number) {
	  return match(NUMBER_PATTERN, number);
	 }
	 /**
	  * 执行正则表达式
	  *
	  * @param pattern
	  *            表达式
	  * @param str
	  *            待验证字符串
	  * @return 返回 <b>true </b>,否则为 <b>false </b>
	  */
	 private static boolean match(String pattern, String str) {
	  Pattern p = Pattern.compile(pattern);
	  Matcher m = p.matcher(str);
	  return m.find();
	 }
	public static void main(String[] args) {
//		System.out.println(Format6Half(1.23412341234));
//		new BigDecimal("xxx");
		 BigDecimal decimal = new BigDecimal("1xx23.1191");  
	        decimal = decimal.setScale(2, RoundingMode.DOWN);
		System.out.println(decimal);
	}

}