package com.btc100.btc_q.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.EncryptUtil;

public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);// 日志对象。

	/**
	 * 将对象转换为Int32类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的int类型结果
	 */
	public static int StrToInt(Object Expression, int defValue) {
		if (Expression != null && Expression.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToInt(Expression, defValue);
	}

	/**
	 * 将对象转换为Int32类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的long类型结果
	 */
	public static long StrToLong(Object Expression, long defValue) {
		if (Expression != null && Expression.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToLong(Expression, defValue);
	}

	public static float StrToFloat(Object strValue, float defValue) {
		if (strValue != null && strValue.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToFloat(strValue, defValue);
	}

	public static double StrToDouble(Object strValue, double defValue) {
		if (strValue != null && strValue.toString().toLowerCase() == "true")
			return 1;
		return TypeParse.StrToDouble(strValue, defValue);
	}

	public static Date StrToDate(String date) {
		if (date != null && !date.equals("")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
		return null;
	}

	// public static decimal StrToDecimal(Object strValue, decimal defValue)
	// {
	// if (strValue != null && strValue.toString().toLowerCase() == "true")
	// return 1;
	// return TypeParse.StrToDecimal(strValue, defValue);
	// }

	/**
	 * 根据毫秒获取最大天数
	 */
	public static int GetDays(double milliseconds, int BigDecimalType) {
		milliseconds = milliseconds / (1000 * 60 * 60 * 24);
		BigDecimal bd = new BigDecimal(Double.toString(milliseconds));
		bd = bd.setScale(0, BigDecimalType);
		return Integer.parseInt(bd.toString());
	}

	/**
	 * 获取两个时间相差几天（取整：向上取整 或 向下取整）
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param bigDecimalType
	 * @return
	 */
	public static int GetDiffDays(Date beginTime, Date endTime, int bigDecimalType) {
		double diff = endTime.getTime() - beginTime.getTime();
		diff = diff / (1000 * 60 * 60 * 24);
		BigDecimal bd = new BigDecimal(Double.toString(diff));
		bd = bd.setScale(0, bigDecimalType);
		return Integer.parseInt(bd.toString());
	}

	// 1 minute = 60 seconds
	// 1 hour = 60 x 60 = 3600
	// 1 day = 3600 x 24 = 86400
	/**
	 * 获取两个日期相差几天几小时几分几秒
	 */
	public static String DateDifference(Date startDate, Date endDate) {

		// milliseconds
		long different = endDate.getTime() - startDate.getTime();

		System.out.println("startDate : " + startDate);
		System.out.println("endDate : " + endDate);
		System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		// System.out.printf(
		// "%d days, %d hours, %d minutes, %d seconds%n",
		// elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

		return String.format("%d 天,%d 小时,%d 分钟,%d 秒", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
	}

	/**
	 * 计算利息
	 * 
	 * @param amount
	 *            本金
	 * @param rate
	 *            利率
	 * @param allDay
	 *            总借入天数（<=20）
	 * @param pastDay
	 *            已还款天数（<=19）
	 * @param sysSingleDay
	 *            系统定义的单倍计息天数
	 * @param sysDoubleDay
	 *            系统定义的双倍计息天数
	 * @return 利息
	 */
	public static double CalculatingInterest(double amount, double rate, int allDay, int pastDay, int sysSingleDay, int sysAllDay) {
		double interest = 0;

		int sysDoubleDay = sysAllDay - sysSingleDay;
		if (sysDoubleDay < 0) {
			sysDoubleDay = 0;
		}

		if (allDay > sysAllDay)
			allDay = sysAllDay;

		// 需计息天数
		int beDay = allDay - pastDay;
		// 单倍计息天数
		int singleDay = 0;
		// 双倍计息天数
		int doubleDay = 0;

		// 1 + 2 = 3 <= 15
		// 3 + 1 = 4 <= 15
		// 10 + 5 = 15 <= 15
		if (pastDay + beDay <= sysSingleDay) {
			// 2 = 2
			// 1 = 1
			// 5 = 5
			singleDay = beDay;
			// 0 = 0
			doubleDay = 0;
		} else {

			// 15 + 3 = 16 > 15
			// 10 + 6 = 16 > 15
			// 2 + 14 = 16 > 15

			// 15 >= 15
			// 10 < 15
			// 2 < 15
			if (pastDay >= sysSingleDay) {
				singleDay = 0;
				doubleDay = beDay;
			} else {
				// 1 = 6 - (15 - 10)
				// 1 = 14 - (15 - 2)
				doubleDay = beDay - (sysSingleDay - pastDay);
				// 5 = 6 - 1
				// 13 = 14 - 1
				singleDay = beDay - doubleDay;
			}
		}

		// // 如果已还款天数大于0，计算出需要计息的总天数 和 需要计息的单倍天数 和 双倍天数
		// if (pastDay > 0 && pastDay >= sysSingleDay) {
		// singleDay = 0;
		// doubleDay = beDay;
		// } else {
		// if(beDay > sysSingleDay)
		// singleDay = sysSingleDay - (beDay - pastDay);
		// doubleDay = (beDay - singleDay) < 0 ? 0 : (beDay - singleDay);
		// }

		// 利息 = (本金 * 利率 * 单倍计息天数) + (本金 * 利率 * 双倍计息天数 * 2)
		interest = (amount * rate * singleDay) + (amount * rate * doubleDay * 2);

		// 利息 只保留6位小数（第7位小数四舍五入）
		BigDecimal bDecimal = new BigDecimal(Double.toString(interest));
		bDecimal = bDecimal.setScale(6, BigDecimal.ROUND_HALF_UP);
		interest = bDecimal.doubleValue();

		return interest;
	}

	public static String PostUrl(String method, String postUrl, String httpVersion, String host, String accept, String contentType, String charset,
			String Authorization, String postData) {
		String result = "";
		try {
			// // 发送POST请求
			// URL url = new URL(postUrl);
			// HttpURLConnection conn = (HttpURLConnection)
			// url.openConnection();
			// if(!method.equals(""))
			// conn.setRequestMethod(method);
			// else
			// conn.setRequestMethod("POST");
			//
			// conn.setRequestProperty("Content-Length", "" +
			// postData.length());
			//			
			// if(!accept.equals(""))
			// conn.setRequestProperty("Accept", accept);
			// else
			// conn.setRequestProperty("Accept", "application/json;");
			//			
			// if(!contentType.equals(""))
			// conn.setRequestProperty("Content-Type", contentType);
			// else
			// conn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded;");
			// if(!charset.equals(""))
			// conn.setRequestProperty("charset", charset);
			// else
			// conn.setRequestProperty("charset", "utf-8;");
			//			
			// if(!Authorization.equals(""))
			// conn.setRequestProperty("Authorization", Authorization);
			//			
			// conn.setRequestProperty("Connection", "Keep-Alive");
			// conn.setUseCaches(false);
			// conn.setDoOutput(true);
			//
			// OutputStreamWriter out = new
			// OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// out.write(postData);
			// out.flush();
			// out.close();

			// 发送POST请求
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(postData);
			out.flush();
			out.close();

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// System.out.println("connect failed!");
				logger.error("POST请求错误：通信失败！");
				return "";
			}
			// 获取响应内容体
			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			in.close();
			return result;
		} catch (Exception ex) {
			logger.error("POST请求错误：" + ex.getMessage() + "\r\n" + ex.getStackTrace());
			result = "-1";
		}
		return result;
	}

	public static StringBuffer getStringBuffer(String host, String port, String version) {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(host).append(":").append(port);
		// sb.append("/2013-12-26");
		sb.append("/" + version);
		return sb;
	}

	private static void setHttpHeader(AbstractHttpMessage httpMessage) {
		// 构造请求头信息
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
	}

	public static String HttpsPost(String host, Integer port, String accountSid, String authToken, String postData, String version)
			throws KeyManagementException, NoSuchAlgorithmException {
		String reslut = "";
		// 创建HttpClient
		CcopHttpClient chc = new CcopHttpClient();

		DefaultHttpClient httpclient = chc.registerSSL(host, "TLS", port, "https");

		try {

			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);// ʱ���
			// md5(主账户Id + 主账户授权令牌 + 时间戳)
			String sig = accountSid + authToken + timestamp;
			// MD5加密
			EncryptUtil eu = new EncryptUtil();
			String signature = eu.md5Digest(sig);
			String url = getStringBuffer(host, port + "", version).append("/Accounts/").append(accountSid).append("/Calls/VoiceVerify?sig=").append(signature)
					.toString();
			// 创建HttpPost
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Host", host + ":" + port);
			setHttpHeader(httppost);
			String src = accountSid + ":" + timestamp;
			String auth = eu.base64Encoder(src);
			httppost.setHeader("Authorization", auth);// base64(主账户Id + 冒号 +
														// 时间戳)

			// SendSMSRequest sendSMS = new SendSMSRequest(to, body, msgType,
			// appId, subAccountSid);
			// Gson gson = new Gson();
			// String bodyData = gson.toJson(sendSMS);

			System.out.println("---------------------------- SendSMS for JSON begin----------------------------");
			System.out.println("Response content is: " + postData);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(postData.getBytes("UTF-8")));
			requestBody.setContentLength(postData.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			System.out.println("============================== Response setEntity ");

			// 执行客户端请求
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				reslut = EntityUtils.toString(entity, "UTF-8");
			}

			System.out.println("============================== Response " + reslut);
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {

			System.out.println("语音验证码发送 HttpsPost Error\r\n" + e.getMessage() + "\r\n" + e.getStackTrace());
			logger.error("语音验证码发送HttpsPost After\r\n" + e.getMessage() + "\r\n" + e.getStackTrace());

			e.printStackTrace();
		} finally {
			// 关闭连接
			httpclient.getConnectionManager().shutdown();
		}
		// 返回发送短信的响应体
		return reslut;
	}

	// /**
	// * 发送POST请求
	// * @param postUrl
	// * @param postData
	// * @return
	// */
	// public static String PostUrl(String postUrl,String postData){
	//    	
	// }

	public static String HttpsPostTest() throws KeyManagementException, NoSuchAlgorithmException {

		String reslut = "";
		String host = "btc100.org";
		Integer port = 443;

		// 创建HttpClient
		CcopHttpClient chc = new CcopHttpClient();

		DefaultHttpClient httpclient = chc.registerSSL(host, "TLS", port, "https");

		try {

			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);// ʱ���
			// md5(主账户Id + 主账户授权令牌 + 时间戳)
			// String sig = accountSid + authToken + timestamp;
			String sig = timestamp;
			// MD5加密
			EncryptUtil eu = new EncryptUtil();
			String signature = eu.md5Digest(sig);
			// String url = getStringBuffer(host,port + "",version)
			// .append("/Accounts/")
			// .append(accountSid).append("/Calls/VoiceVerify?sig=")
			// .append(signature).toString();

			// StringBuffer sb = new StringBuffer("https://");
			// sb.append(host).append(":").append(port);
			// //sb.append("/2013-12-26");
			// sb.append("/" + version);

			String url = "https://" + host + "/api/ticker?symbol=btc";

			// 创建HttpPost
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Host", host);
			setHttpHeader(httppost);
			// String src = accountSid + ":" + timestamp;
			String src = ":" + timestamp;
			String auth = eu.base64Encoder(src);
			httppost.setHeader("Authorization", auth);// base64(主账户Id + 冒号 +
														// 时间戳)

			// SendSMSRequest sendSMS = new SendSMSRequest(to, body, msgType,
			// appId, subAccountSid);
			// Gson gson = new Gson();
			// String bodyData = gson.toJson(sendSMS);

			System.out.println("---------------------------- SendSMS for JSON begin----------------------------");
			// System.out.println("Response content is: " + postData);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream("".getBytes("UTF-8")));
			requestBody.setContentLength("".getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			// 执行客户端请求
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				reslut = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {

			System.out.println("语音验证码发送 HttpsPost Error\r\n" + e.getMessage() + "\r\n" + e.getStackTrace());
			logger.error("语音验证码发送HttpsPost After\r\n" + e.getMessage() + "\r\n" + e.getStackTrace());

			e.printStackTrace();
		} finally {
			// 关闭连接
			httpclient.getConnectionManager().shutdown();
		}
		// 返回发送短信的响应体
		return reslut;

	}

}
