package com.btc100.btc_q.common;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;

import com.btc100.btc_q.util.HttpUtil;
import com.btc100.btc_q.util.Utils;

public class VerificationCode {

	private static final Logger logger = Logger.getLogger(VerificationCode.class);
	
	/**
	 * 发送手机短信
	 * 
	 * @param telNum
	 *            手机号
	 * @param content
	 *            短信内容
	 * @return
	 */
	public boolean sendTelMessage1(String telNum, String content) {
		try {
			String PostData = "account=cf_donghuigis&password=Donghui126.com&mobile=" + telNum + "&content=" + java.net.URLEncoder.encode(content, "utf-8");
			String ret = MessageInterface.SMS(PostData, "http://121.199.16.178/webservice/sms.php?method=Submit");

			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("utf-8");
			Document document;
			document = saxReader.read(new ByteArrayInputStream(ret.getBytes("UTF-8")));
			Element root = document.getRootElement();
			Element element = root.element("code");
			int r = Integer.valueOf(element.getText()).intValue();
			if (r == 2) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			logger.info("发送手机验证码失败");
		}
		return false;
	}
	
	
	/**
	 * 发送手机验证码
	 * 
	 * @param telNum
	 *            手机号
	 * @param random
	 *            6位随机码
	 * @return
	 */
	public boolean sendTelCode1(String telNum, String random) {
		try {
			String PostData = "account=cf_donghuigis&password=Donghui126.com&mobile=" + telNum
					+ "&content=" + java.net.URLEncoder.encode("尊敬的用户您好，您的验证码为：" + random + "。请不要把验证码泄露给其他人。如非本人操作，可不用理会！",	"utf-8");
			String ret = MessageInterface.SMS(PostData, "http://121.199.16.178/webservice/sms.php?method=Submit");

			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("utf-8");
			Document document;
			document = saxReader.read(new ByteArrayInputStream(ret.getBytes("UTF-8")));
			Element root = document.getRootElement();
			Element element = root.element("code");
			int r = Integer.valueOf(element.getText()).intValue();
			if (r == 2) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			logger.info("发送手机验证码失败");
		}
		return false;
	}
	
	/**
	 * 发送手机语音验证码
	 * 
	 * @param telNum
	 *            手机号
	 * @param random
	 *            6位随机码
	 * @return
	 */
	public boolean sendTelCodeVoice(int auth_id, String telNum, int random) throws Exception {
		try {

			logger.error("语音验证码发送 sendTelCodeVoice\r\n");

			String host = VSMSConfig.getVal("host");
			String port = VSMSConfig.getVal("port");
			String version = VSMSConfig.getVal("version");
			String account_sid = VSMSConfig.getVal("account_sid");
			String authtoken = VSMSConfig.getVal("authtoken");
			String appid = VSMSConfig.getVal("appid");
			String playTimes = VSMSConfig.getVal("playTimes");
			String displayNum = VSMSConfig.getVal("displayNum");
			String respUrl = VSMSConfig.getVal("respUrl");

			StringBuffer sb = new StringBuffer();

			if (host.startsWith("http"))
				host = host.replace("http://", "");
			if (port.equals(""))
				port = "8883";

			sb = new StringBuffer();
			if (!playTimes.equals(""))
				playTimes = "2"; // 语音验证码默认读两遍

			if (displayNum.equals(""))
				displayNum = "037155031576"; // 默认显示公司固话号码

			if (telNum.length() == 11 && (telNum.startsWith("13") || telNum.startsWith("14")))
				telNum = "0086" + telNum;

			sb.append("{\"appId\":\"" + appid + "\",\"verifyCode\":\"" + random + "\",\"playTimes\":\"" + playTimes
					+ "\"," + "\"to\":\"" + telNum + "\",\"displayNum\":\"" + displayNum + "\",\"respUrl\":\""
					+ respUrl + "\"}");

			String postData = sb.toString();

			logger.error("语音验证码发送HttpsPost Before\r\n");

			String ret = HttpUtil.HttpsPost(host, Utils.StrToInt(port, 8883), account_sid, authtoken, postData, version);

			logger.error("语音验证码发送HttpsPost After\r\n");

			// 2.3 JSON响应示例
			// HTTP/1.1 200 OK
			// Content-Length: 641
			//
			// {"statusCode":"000000","VoiceVerify":{"dateCreated":"2013-02-01 15:53:06","callSid":" ff8080813c373cab013c94be9fe300c5"}}
			// {"statusCode":"000000","VoiceVerify":{"dateCreated":"2014-04-21 11:56:09","callSid":"1404211156097373000600020000010f"}}
			try {
				JSONObject obj = new JSONObject(ret);
				String code = obj.getString("statusCode");
				String verify = obj.getString("VoiceVerify");
				obj = new JSONObject(verify);
				String dateCreated = obj.getString("dateCreated");
				String callSid = obj.getString("callSid");

				if (code.equals("000000")) {

					// 更新数据库中验证码的 callSid
//					btcun_authcode infoAuthCode = new btcun_authcode();
//					infoAuthCode.setF_auth_id(auth_id);
//					infoAuthCode.setF_comment1(callSid + ":" + dateCreated);
//					maservice.updateAuthCodeComment1(infoAuthCode);

					return true;
				}

				logger.error("语音验证码发送失败：" + ret + "\n");

				return false;

			} catch (Exception ex) {

				logger.error("语音验证码发送失败：" + ret + "\r\n" + ex.getMessage() + "\r\n" + ex.getStackTrace());

				return false;
			}

		} catch (Exception ex) {

			logger.error("语音验证码网络通信错误：" + ex.getMessage() + "\r\n" + ex.getStackTrace());

			return false;
		}
	}

	
}
