package com.btc100.btc_q.util;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.btc100.btc_q.common.CcopHttpClient;

public class HttpUtil {

	private static final Logger logger = Logger.getLogger(HttpUtil.class);

	public static String HttpsPost(String host, Integer port, String accountSid, String authToken, String postData, String version) throws KeyManagementException, NoSuchAlgorithmException {

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
			String url = getStringBuffer(host, port + "", version).append("/Accounts/").append(accountSid).append("/Calls/VoiceVerify?sig=").append(signature).toString();
			// 创建HttpPost
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Host", host + ":" + port);
			setHttpHeader(httppost);
			String src = accountSid + ":" + timestamp;
			String auth = eu.base64Encoder(src);
			httppost.setHeader("Authorization", auth);// base64(主账户Id + 冒号 + 时间戳)

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(postData.getBytes("UTF-8")));
			requestBody.setContentLength(postData.getBytes("UTF-8").length);
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
			logger.error("语音验证码发送HttpsPost After\r\n" + e.getMessage() + "\r\n" + e.getStackTrace());
			e.printStackTrace();
		} finally {
			// 关闭连接
			httpclient.getConnectionManager().shutdown();
		}
		// 返回发送短信的响应体
		return reslut;
	}

	public static StringBuffer getStringBuffer(String host, String port, String version) {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(host).append(":").append(port);
		sb.append("/" + version);
		return sb;
	}

	private static void setHttpHeader(AbstractHttpMessage httpMessage) {
		// 构造请求头信息
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
	}

}
