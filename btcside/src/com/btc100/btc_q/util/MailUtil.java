package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailUtil {

	/**
	 * 发送邮件
	 * @param toEmail
	 * @param htmlContent
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendMail(String toEmail, String htmlContent) throws MessagingException, UnsupportedEncodingException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext_mail.xml");
		JavaMailSender sender = (JavaMailSender) ctx.getBean("javaMailSender");
		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		// 建立邮件消息,发送简单邮件和html邮件的区别
		MimeMessage msg = senderImpl.createMimeMessage();
		// 为防止乱码，添加编码集设置
		MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");
		// 接收方邮箱
		helper.setTo(toEmail);
		helper.setFrom("btc100@btc100.org", "比特科技(BTC100)");
		helper.setSubject("BTC100邮件通知");
		// true 表示启动HTML格式的邮件
		helper.setText(htmlContent, true);
		sender.send(msg);
	}
	
	public static void main(String[] args) {
		try {
			sendMail("517491659@qq.com", "你真的错了么？");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
