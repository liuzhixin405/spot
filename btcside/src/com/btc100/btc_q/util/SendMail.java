package com.btc100.btc_q.util;

import org.springframework.context.ApplicationContext;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.init.SpringContext;

public class SendMail {
	public ApplicationContext ctx = null;
	
	private static final String REGION = "ap-southeast-1";
	
	private static final String ACCOUT = "system@mail.btcside.com";
	
	private static final String ALIAS = "BTCSIDE";
	
	private static final String ACCESS_KEY_ID = "LTAI5t8UGi5SW6sE5dtSrok1";
	
	private static final String ACCESS_KEY_SECRET = "HpdoPvsiulITZ6TQNwD1gCtpz3IUy5";

	
	private static final IAcsClient ACS_CLIENT = new DefaultAcsClient(DefaultProfile.getProfile(REGION, ACCESS_KEY_ID, ACCESS_KEY_SECRET));
	
	private static final String TAG_NAME = "BTCSIDE";

	public SendMail() {
		// ��ȡ������
		// ctx = new ClassPathXmlApplicationContext("applicationContext_mail.xml");

	}

	@SuppressWarnings("deprecation")
	public void send(String setTo, String content) {
		if (StringUtil.isEmpty(setTo)) {
			return;
		}
		
		try {
			DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        } catch (ClientException e) {
        	e.printStackTrace();
        }
		
		try {
			SingleSendMailRequest request = new SingleSendMailRequest();
			request.setAccountName(ACCOUT);
			request.setFromAlias(ALIAS);
			request.setAddressType(1);
			request.setTagName(TAG_NAME);
			request.setReplyToAddress(true);
			request.setToAddress(setTo);
			request.setSubject("BTCSIDE邮件通知");
			request.setHtmlBody(content);
			ACS_CLIENT.getAcsResponse(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		try {
			// ��ȡJavaMailSender bean
			JavaMailSender sender = (JavaMailSender) ctx.getBean("javaMailSender");

			JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

			MimeMessage mailMessage = senderImpl.createMimeMessage();

			// ����utf-8��GBK���룬�����ʼ���������
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");

			messageHelper.setTo(setTo);// ������

			// messageHelper.setFrom("btc100@btc100.org");// ������
			messageHelper.setFrom("service@xkd", "比特科技(XKD)");

			messageHelper.setSubject("XKD邮件通知");// ����

			// �ʼ����ݣ�ע��Ӳ���true����ʾ����html��ʽ
			messageHelper.setText(content, true);
			sender.send(mailMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	String registerNote1 = SpringContext.getText("msg.email.register.note1", Contents.Local.LOCALE.get());
	String registerNote2 = SpringContext.getText("msg.email.register.note2", Contents.Local.LOCALE.get());
	String registerNote3 = SpringContext.getText("msg.email.register.note3", Contents.Local.LOCALE.get());
	String registerNote4 = SpringContext.getText("msg.email.register.note4", Contents.Local.LOCALE.get());
	String registerNote5 = SpringContext.getText("msg.email.register.note5", Contents.Local.LOCALE.get());
	String registerNote6 = SpringContext.getText("msg.email.register.note6", Contents.Local.LOCALE.get());
	String registerNote7 = SpringContext.getText("msg.email.register.note7", Contents.Local.LOCALE.get());
	String registerNote8 = SpringContext.getText("msg.email.register.note8", Contents.Local.LOCALE.get());
	String registerNote9 = SpringContext.getText("msg.email.register.note9", Contents.Local.LOCALE.get());
	String registerNote10 = SpringContext.getText("msg.email.register.note10", Contents.Local.LOCALE.get());
	String registerNote11 = SpringContext.getText("msg.email.register.note11", Contents.Local.LOCALE.get());
	String registerStr = "  <div style=\"width:800px; margin:60px auto;overflow:hidden; font-size:12px; -webkit-border-radius:5px; -moz-border-radius:5px;  -ms-border-radius:5px; -o-border-radius:5px;  border-radius:5px;  -webkit-box-shadow:0 0 15px #eee; -moz-box-shadow:0 0 15px #eee; box-shadow:0 0 15px #eee;color:#6a6a6a;border:1px solid #dbdbdb;\">"
			+ "<h1 style=\"font-size:18px; font-weight:normal;height:50px;line-height:50px;background:#00376a;color:#7dc0ff;margin:0;\">"
			+ "  <a href=\"%s\" target=\"_blank\" style=\"display:flex; align-items:center; height:100%%; float:left; padding:0 20px 0 15px;\"><img src=\"%scommon/images/Btcside.png\" alt=\"Btcside.png\" style=\"width:119px;height:25px;\"></a>%s</h1>"
			+ "<div style=\"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:10px 35px;\">"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerNote1
			+ "    <br/>" + registerNote2 + "</p>"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\"><b>" + registerNote3 + "%s</b></p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;\">" + registerNote4 + "</p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;\"><a href=\"%s\" style=\"-webkit-border-radius:5px;-moz-border-radius:5px;-ms-border-radius:5px;-o-border-radius:5px;border-radius:5px;padding:10px 25px;color:#fff;font-size:14px;display:inline-block;text-align:center;background:#0f5f9d;text-decoration:none;\">" + registerNote5 + "</a></p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;\">" + registerNote6 + "</p>"
			+ "  <div style=\"height:auto;overflow:hidden;line-height:1.8em;padding:10px 15px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;margin:15px auto;-webkit-border-radius:5px;-moz-border-radius:5px;-ms-border-radius:5px;-o-border-radius:5px;border-radius:5px;border:1px solid #d7d7d7;word-break:break-all;\">"
			+ "    <span style=\"color:#333;text-decoration:none;\">%s</span></div>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;color:#0f5f9d;\">" + registerNote7 + "</p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;text-align:right;\">- " + registerNote8 + "</p>"
			+ "  </div></div>";
//			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerNote9 + "<b>"
//			+ "  <a href=\"%s\" style=\"color:#333;\"> " + registerNote10 + "</a></b></p></div></div>";

	public String getReister(String srootUrl, String semail, String surl) {
		String smail2 = String.format(registerStr, srootUrl, srootUrl, registerNote11, semail, surl, surl, srootUrl);
		return smail2;
	}
	
	/**
	 * 注册成功发送邮件
	 * 
	 */
	String registerSuccessNote1 = SpringContext.getText("msg.email.register.success.note1", Contents.Local.LOCALE.get());
	String registerSuccessNote4 = SpringContext.getText("msg.email.register.success.note4", Contents.Local.LOCALE.get());
	String registerSuccess = "  <div style=\"width:800px; margin:60px auto;overflow:hidden; font-size:14px; -webkit-border-radius:5px; -moz-border-radius:5px;  -ms-border-radius:5px; -o-border-radius:5px;  border-radius:5px;  -webkit-box-shadow:0 0 15px #eee; -moz-box-shadow:0 0 15px #eee; box-shadow:0 0 15px #eee;color:#6a6a6a;border:1px solid #dbdbdb;\">"
			+ "<h1 style=\"font-size:18px; font-weight:normal;height:50px;line-height:50px;background:#00376a;color:#7dc0ff;margin:0;\">"
			+ "  <a href=\"%s\" target=\"_blank\" style=\"float:left; padding:8px 25px 0 15px; color:#fff;\">%s</a></h1>"
			+ "<div style=\"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:10px 35px;\">"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerSuccessNote1 + "</p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;text-align:right;\">- " + registerNote8 + "</p>"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerNote9 + "<b>"
			+ "  <a href=\"%s\" style=\"color:#333;\"> " + registerNote10 + "</a></b></p></div></div>";

	public String getRegisterSuccessStr(String srootUrl) {
		return String.format(registerSuccess, srootUrl, srootUrl, srootUrl, srootUrl);
	}

	String pwdNote1 = SpringContext.getText("msg.email.pwd.note1", Contents.Local.LOCALE.get());
	String pwdNote2 = SpringContext.getText("msg.email.pwd.note2", Contents.Local.LOCALE.get());
	String pwdNote3 = SpringContext.getText("msg.email.pwd.note3", Contents.Local.LOCALE.get());
	String pwdNote4 = SpringContext.getText("msg.email.pwd.note4", Contents.Local.LOCALE.get());
	String pwdNote5 = SpringContext.getText("msg.email.pwd.note5", Contents.Local.LOCALE.get());
	String strpwd="  <div style=\"width:800px; margin:60px auto;overflow:hidden; font-size:12px; -webkit-border-radius:5px; -moz-border-radius:5px;  -ms-border-radius:5px; -o-border-radius:5px;  border-radius:5px;  -webkit-box-shadow:0 0 15px #eee; -moz-box-shadow:0 0 15px #eee; box-shadow:0 0 15px #eee;color:#6a6a6a;border:1px solid #dbdbdb;\">"
		+"<h1 style=\"position:relative;width:100%%;font-size:18px; font-weight:normal;height:50px;line-height:50px;background:#00376a;color:#7dc0ff;margin:0;\">"
		+"<a href=\"%s\" target=\"_blank\" style=\"display:flex; align-items:center; height:100%%; float:left; padding:0 20px 0 15px;\"><img src=\"%scommon/images/email-logo1.png\" alt=\"XKD\" style=\"width:119px;height:25px;\"></a><span style=\"\">%s</span></h1>"
		+"<div style=\"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:10px 35px;\">"
		+"<p style=\"line-height:1.8em;margin:15px 0;\">" + pwdNote1 + "%s" + pwdNote2 + "</p>"
		+"<div style=\"height:auto;overflow:hidden;line-height:1.8em;padding:10px 15px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;margin:15px auto;-webkit-border-radius:5px;-moz-border-radius:5px;-ms-border-radius:5px;-o-border-radius:5px;border-radius:5px;border:1px solid #d7d7d7;word-break:break-all;\">"
		+"  <a href=\"%s\" style=\"display:block;width:100%%;color:#333;text-decoration:none;\">%s</a></div>"
		+"<p style=\"line-height:1.8em;margin:15px 0;color:#0f5f9d; \">" + pwdNote3 + "</p>"
		+"<p style=\"line-height:1.8em;margin:15px 0;text-align:right; \">- " + registerNote8 + "</p>"
		+"<p style=\"line-height:1.8em; margin:15px 0; \">" + registerNote9
		+"<b><a href=\"%s\" style=\"color:#333; \"> " + registerNote10 + "</a></b></p></div></div>";

	public String getResetMoneyPWD(String srootUrl, String surl) {
		return String.format(strpwd, srootUrl, srootUrl, pwdNote5, pwdNote5, surl, surl, srootUrl);
	}

	public String getResetLoginPWD(String srootUrl, String surl) {
		String str = String.format(strpwd, srootUrl, srootUrl, pwdNote4, pwdNote4, surl, surl, srootUrl);
		return str;
	}
	
	/**
	 * API邮箱发送邮件
	 * 
	 */
	String apiNote1 = SpringContext.getText("msg.email.api.note1", Contents.Local.LOCALE.get());
	String apiNote2 = SpringContext.getText("msg.email.api.note2", Contents.Local.LOCALE.get());
	String apiStr = "  <div style=\"width:800px; margin:60px auto;overflow:hidden; font-size:14px; -webkit-border-radius:5px; -moz-border-radius:5px;  -ms-border-radius:5px; -o-border-radius:5px;  border-radius:5px;  -webkit-box-shadow:0 0 15px #eee; -moz-box-shadow:0 0 15px #eee; box-shadow:0 0 15px #eee;color:#6a6a6a;border:1px solid #dbdbdb;\">"
			+ "<h1 style=\"font-size:18px; font-weight:normal;height:50px;line-height:50px;background:#00376a;color:#7dc0ff;margin:0;\">"
			+ "  <a href=\"%s\" target=\"_blank\" style=\"float:left; padding:8px 25px 0 15px; color:#fff;\">%s</a></h1>"
			+ "<div style=\"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:10px 35px;\">"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerNote1
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + apiNote1 + "<b style=\"color: red; font-size: 30px;\">%s</b></p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;\">" + apiNote2 + "</p>"
			+ "  <p style=\"line-height:1.8em;margin:15px 0;text-align:right;\">- " + registerNote8 + "</p>"
			+ "  <p style=\"line-height:1.8em; margin:15px 0;\">" + registerNote9 + "<b>"
			+ "  <a href=\"%s\" style=\"color:#333;\"> " + registerNote10 + "</a></b></p></div></div>";

	public String getApiStr(String srootUrl, String code) {
		return String.format(apiStr, srootUrl, srootUrl, code, srootUrl);
	}
	/*
	 * public static void main(String[] args) { // SendMail themail = new
	 * SendMail(); // themail.send("z1058915843@163.com"); }
	 */
}
