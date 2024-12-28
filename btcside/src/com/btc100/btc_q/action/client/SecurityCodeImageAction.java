package com.btc100.btc_q.action.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.MatrixToImageWriter;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.SecurityCode;
import com.btc100.btc_q.util.SecurityImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 提供图片验证码
 */
@Controller
@RequestMapping("/Captcha")
public class SecurityCodeImageAction extends BaseAction {

	@RequestMapping("/CaptchaAction")
	public void loadSecurityCodeImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取验证码.
		ServletOutputStream out = null;
		try {
			String securityCode = SecurityCode.getSecurityCode();
			// imageStream = SecurityImage.getImageAsInputStream(securityCode);
			// 获取验证码图片
			BufferedImage bfimg = SecurityImage.getImage(securityCode);
			System.out.println(securityCode);
			// 加密验证码
			String vertifiCode = securityCode.toLowerCase();
			securityCode = MD5Service.encryptString(vertifiCode);
			request.getSession().setAttribute("validCode", securityCode);
			// 获取干扰验证码
			// securityCode = SecurityCode.getSecurityCode();
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			response.setContentType("image/jpeg");
			out = response.getOutputStream();
			ImageIO.write(bfimg, "jpg", out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@RequestMapping("/google")
	public void google(HttpServletRequest request, HttpServletResponse response) {

		String email = request.getParameter("email");
		String secret = request.getParameter("secret");

		String ssString = GoogleAuthenticator.getQRBarcodeURL(email, secret);
		int width = 200, height = 200;

		String format = "jpg";
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 0);

		BitMatrix bitMatrix;
		try {
			bitMatrix = new MultiFormatWriter().encode(ssString, BarcodeFormat.QR_CODE, width, height, hints);
			// ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			response.setContentType("image/jpeg");
			ServletOutputStream out = response.getOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, format, out);
			try {
				out.flush();
			} finally {
				out.close();
			}
			// imageStream = new ByteArrayInputStream(out.toByteArray());
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}